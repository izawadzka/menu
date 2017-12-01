package com.example.dell.menu.menuplanning.screens.menus;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.dell.menu.data.MenuDataBase;
import com.example.dell.menu.data.tables.ProductsOnShelvesTable;
import com.example.dell.menu.data.tables.ShelvesInVirtualFridgeTable;
import com.example.dell.menu.menuplanning.objects.DailyMenu;
import com.example.dell.menu.menuplanning.objects.Product;
import com.example.dell.menu.user.UserStorage;
import com.example.dell.menu.menuplanning.events.menus.DeleteMenuEvent;
import com.example.dell.menu.menuplanning.events.menus.EditMenuNameEvent;
import com.example.dell.menu.menuplanning.events.menus.GenerateShoppingListButtonClickedEvent;
import com.example.dell.menu.shoppinglist.events.GenerateShoppingListEvent;
import com.example.dell.menu.shoppinglist.events.ShowShoppingListEvent;
import com.example.dell.menu.menuplanning.objects.Menu;
import com.example.dell.menu.data.tables.DailyMenusTable;
import com.example.dell.menu.data.tables.MealsTypesDailyMenusAmountOfPeopleTable;
import com.example.dell.menu.data.tables.MealsTypesMealsDailyMenusTable;
import com.example.dell.menu.data.tables.MenusDailyMenusTable;
import com.example.dell.menu.data.tables.MenusTable;
import com.example.dell.menu.virtualfridge.flags.ProductFlags;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 04.06.2017.
 */

public class MenusManager {
    private final Bus bus;
    private final UserStorage userStorage;
    private MenusFragment menusFragment;
    private List<Menu> menusArrayList;
    private Menu currentMenu;
    private boolean delete_mode;

    public MenusManager(Bus bus, UserStorage userStorage) {
        this.bus = bus;
        bus.register(this);
        this.userStorage = userStorage;
    }

    public void onAttach(MenusFragment menusFragment){
        this.menusFragment = menusFragment;
    }

    public void onStop(){
        this.menusFragment = null;
    }

    public void loadMenus() {
        if(menusFragment != null){
            new LoadMenus().execute();
        }
    }

    public void addNewMenu(String menuName) {
        if(menusFragment != null){
            new AddNewMenu().execute(menuName);
        }
    }

    public List<Menu> getMenusArrayList() {
        return menusArrayList;
    }

    @Subscribe
    public void onEditMenuNameImageButtonClicked(EditMenuNameEvent event){
        if(menusFragment != null){
            menusFragment.editMenuName(event.menu);
        }
    }


    @Subscribe
    public void onDeleteMenuImageButtonClicked(DeleteMenuEvent event){
        if(menusFragment != null){
            currentMenu = event.menu;
            delete_mode = true;
            new CheckIfMenuIsEmpty().execute(currentMenu);
        }
    }

    @Subscribe
    public void onGenerateNewShoppingListButtonClicked(GenerateShoppingListButtonClickedEvent event){
        if(menusFragment != null){
            new CheckIfMenuIsEmpty().execute(event.menu);
        }
    }

    class CheckIfMenuIsEmpty extends AsyncTask<Menu, Void, Menu>{

        @Override
        protected Menu doInBackground(Menu... params) {
            Menu result;
            MenuDataBase menuDataBase = MenuDataBase.getInstance(menusFragment.getActivity());
            String checkIfMenuIsEmptyQuery = String.format("SELECT COUNT(%s) FROM %s WHERE %s = '%s'",
                    MenusDailyMenusTable.getFirstColumnName(),
                    MenusDailyMenusTable.getTableName(), MenusDailyMenusTable.getFirstColumnName(),
                    params[0].getMenuId());

            Cursor cursor = menuDataBase.downloadData(checkIfMenuIsEmptyQuery);
            cursor.moveToPosition(0);
            if(cursor.getInt(0) == 0) result = null;
            else result = params[0];

            menuDataBase.close();
            return result;
        }

        @Override
        protected void onPostExecute(Menu menu) {
            if(menu == null){
                if(delete_mode) deleteMenu();
                else menusFragment.makeAStatement("It's impossible to create a shopping list. Menu is empty", Toast.LENGTH_LONG);
            }else{
                if(delete_mode){
                    deleteShelves(menu.getMenuId());
                    //deleteDailyMenus();
                }
                else {
                    bus.post(new ShowShoppingListEvent(menu));
                    bus.post(new GenerateShoppingListEvent(menu));
                }
            }
        }
    }

    private void deleteShelves(final int menuId) {
        if(menusFragment != null){
            new AsyncTask<Void, Void, Boolean>(){

                @Override
                protected Boolean doInBackground(Void... params) {
                    boolean result = true;
                    int extraShelfId = -1;
                    List<DailyMenu> dailyMenus = new ArrayList<DailyMenu>();
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(menusFragment.getActivity());
                    String extraShelfQuery = String.format("SELECT %s FROM %s WHERE %s = %s",
                            ShelvesInVirtualFridgeTable.getFirstColumnName(),
                            ShelvesInVirtualFridgeTable.getTableName(),
                            ShelvesInVirtualFridgeTable.getSecondColumnName(), 0);
                    Cursor extraShelfCursor = menuDataBase.downloadData(extraShelfQuery);

                    if(extraShelfCursor.getCount() == 1) {
                        //find extra shelf id
                        extraShelfCursor.moveToPosition(0);
                        extraShelfId = extraShelfCursor.getInt(0);

                        //load daily menus
                        String loadDailyMenusQuery = String.format("SELECT %s FROM %s WHERE %s = %s",
                                MenusDailyMenusTable.getSecondColumnName(),
                                MenusDailyMenusTable.getTableName(),
                                MenusDailyMenusTable.getFirstColumnName(),
                                menuId);

                        Cursor dailyMenusCursor = menuDataBase.downloadData(loadDailyMenusQuery);
                        if (dailyMenusCursor.getCount() > 0) {
                            dailyMenusCursor.moveToPosition(-1);
                            while (dailyMenusCursor.moveToNext()) {
                                dailyMenus.add(new DailyMenu(dailyMenusCursor.getInt(0)));
                            }


                            for (DailyMenu dailyMenu : dailyMenus) {
                                //move bought products to extra shelf
                                ContentValues editContentValues = new ContentValues();
                                editContentValues.put(ProductsOnShelvesTable.getSecondColumnName(),
                                        extraShelfId);
                                String moveProductsWhereClause = String.format("%s IN (SELECT %s FROM %s " +
                                                "WHERE %s = ?) AND %s = ?",
                                        ProductsOnShelvesTable.getSecondColumnName(),
                                        ShelvesInVirtualFridgeTable.getFirstColumnName(),
                                        ShelvesInVirtualFridgeTable.getTableName(),
                                        ShelvesInVirtualFridgeTable.getSecondColumnName(),
                                        ProductsOnShelvesTable.getFourthColumnName());

                                String[] moveProductsWhereArgs = {String.valueOf(dailyMenu.getDailyMenuId()),
                                        String.valueOf(ProductFlags.BOUGHT_INDX)};
                                menuDataBase.update(ProductsOnShelvesTable.getTableName(),editContentValues,
                                        moveProductsWhereClause, moveProductsWhereArgs);

                                List<Product> productsFromExtraShelf = new ArrayList<Product>();
                                String productsFromExtraShelfQuery = String.format("SELECT %s, %s, %s " +
                                                "FROM %s WHERE %s = %s",
                                        ProductsOnShelvesTable.getFirstColumnName(),
                                        ProductsOnShelvesTable.getThirdColumnName(),
                                        ProductsOnShelvesTable.getFourthColumnName(),
                                        ProductsOnShelvesTable.getTableName(),
                                        ProductsOnShelvesTable.getSecondColumnName(),
                                        extraShelfId);
                                Cursor cursorForExtraShelf = menuDataBase
                                        .downloadData(productsFromExtraShelfQuery);
                                if(cursorForExtraShelf.getCount() > 0){
                                    cursorForExtraShelf.moveToPosition(-1);
                                    while (cursorForExtraShelf.moveToNext()){
                                        int indx = wasAddedPreviously(cursorForExtraShelf.getInt(0),
                                                productsFromExtraShelf);
                                        if(indx != -1){
                                            productsFromExtraShelf.get(indx)
                                                    .addQuantity(cursorForExtraShelf.getDouble(1));
                                        }
                                        else
                                        {
                                            productsFromExtraShelf.add(new Product(cursorForExtraShelf.getInt(0),
                                                    cursorForExtraShelf.getDouble(1),
                                                    cursorForExtraShelf.getInt(2)));
                                        }
                                    }
                                    String[] deleteFromExtraShelfArgs = {String.valueOf(extraShelfId)};
                                    menuDataBase.delete(ProductsOnShelvesTable.getTableName(),
                                            String.format("%s = ?", ProductsOnShelvesTable.getSecondColumnName()),
                                            deleteFromExtraShelfArgs);

                                    for (Product product : productsFromExtraShelf) {
                                        menuDataBase.insert(ProductsOnShelvesTable.getTableName(),
                                                ProductsOnShelvesTable
                                                        .getContentValues(new Product(product.getProductId(),
                                                                        product.getQuantity(),
                                                                        product.getProductFlagId()),
                                                                extraShelfId));
                                    }

                                }

                                //delete all connected products from virtual fridge
                                String deleteProductsWhereClause = String.format("%s IN (SELECT %s FROM %s " +
                                                "WHERE %s = ?)", ProductsOnShelvesTable.getSecondColumnName(),
                                        ShelvesInVirtualFridgeTable.getFirstColumnName(),
                                        ShelvesInVirtualFridgeTable.getTableName(),
                                        ShelvesInVirtualFridgeTable.getSecondColumnName());

                                String[] deleteProductsWhereArgs = {String.valueOf(dailyMenu.getDailyMenuId())};
                                menuDataBase.delete(ProductsOnShelvesTable.getTableName(),
                                        deleteProductsWhereClause, deleteProductsWhereArgs);


                                String deleteShelfWhereClause = String.format("%s = ?",
                                        ShelvesInVirtualFridgeTable.getSecondColumnName());
                                String[] deleteShelfWhereArgs = {String.valueOf(dailyMenu.getDailyMenuId())};
                                result = menuDataBase.delete(ShelvesInVirtualFridgeTable.getTableName(),
                                        deleteShelfWhereClause, deleteShelfWhereArgs) != 0;
                            }

                        } else result = false;
                    }else result = false;

                    menuDataBase.close();
                    return  result;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    if(result){
                        if(menusFragment != null)
                            menusFragment.makeAStatement("Successfully deleted shelves",
                                    Toast.LENGTH_SHORT);
                        deleteDailyMenus();
                    }else if(menusFragment != null)
                        menusFragment.makeAStatement("An error occurred while an attempt to delete " +
                                "shelves!", Toast.LENGTH_LONG);
                }
            }.execute();
        }
    }

    private int wasAddedPreviously(long productId, List<Product> products) {
        for (int i = 0; i < products.size(); i++) {
            if(products.get(i).getProductId() == productId) return i;
        }

        return -1;
    }

    private void deleteDailyMenus() {
        if(menusFragment != null){
            new AsyncTask<Void, Void, Boolean>(){

                @Override
                protected Boolean doInBackground(Void... params) {
                    boolean result;
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(menusFragment.getActivity());

                    //delete meals of dailyMenus
                    String[] menusId = {String.valueOf(currentMenu.getMenuId())};
                    String whereClauseForMeals = String.format("%s IN (SELECT %s FROM %s WHERE %s = ?)",
                            MealsTypesMealsDailyMenusTable.getThirdColumnName(),
                            MenusDailyMenusTable.getSecondColumnName(),
                            MenusDailyMenusTable.getTableName(),
                            MenusDailyMenusTable.getFirstColumnName());

                    result = menuDataBase.delete(MealsTypesMealsDailyMenusTable.getTableName(),
                            whereClauseForMeals, menusId) > 0;

                    if(result){
                        String whereClauseForAmountOfServings = String.format("%s IN (SELECT %s" +
                                " FROM %s WHERE %s = ?)",
                                MealsTypesDailyMenusAmountOfPeopleTable.getSecondColumnName(),
                                MenusDailyMenusTable.getSecondColumnName(),
                                MenusDailyMenusTable.getTableName(),
                                MenusDailyMenusTable.getFirstColumnName());

                        result = menuDataBase.delete(MealsTypesDailyMenusAmountOfPeopleTable.getTableName(),
                                whereClauseForAmountOfServings, menusId) > 0;


                        if(result){ //delete daily menu from daily menu table
                            String whereClauseForDailyMenus = String.format("%s IN(SELECT %s FROM" +
                                    " %s WHERE %s = ?)",
                                    DailyMenusTable.getFirstColumnName(),
                                    MenusDailyMenusTable.getSecondColumnName(),
                                    MenusDailyMenusTable.getTableName(),
                                    MenusDailyMenusTable.getFirstColumnName());

                            result = menuDataBase.delete(DailyMenusTable.getTableName(),
                                    whereClauseForDailyMenus, menusId) > 0;

                            if(result){
                                result = menuDataBase.delete(MenusDailyMenusTable.getTableName(),
                                        String.format("%s = ?",
                                                MenusDailyMenusTable.getFirstColumnName()),
                                                menusId) > 0;
                            }
                        }
                    }

                    menuDataBase.close();
                    return result;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    if(result){
                        deleteMenu();
                    }else{
                        delete_mode = false;
                        if(menusFragment != null)
                            menusFragment.makeAStatement("Deleting dialy menus failed", Toast.LENGTH_LONG);
                    }
                }
            }.execute();
        }
    }

    private void deleteMenu() {
        if(menusFragment != null){
            new AsyncTask<Void, Void, Boolean>(){

                @Override
                protected Boolean doInBackground(Void... params) {
                    boolean result;
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(menusFragment.getActivity());

                    String[] menusId = {String.valueOf(currentMenu.getMenuId())};
                    result = menuDataBase.delete(MenusTable.getTableName(),
                            String.format("%s = ?", MenusTable.getFirstColumnName()), menusId) > 0;
                    menuDataBase.close();
                    return result;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    delete_mode = false;
                    if(menusFragment != null){
                        if (result){
                            menusFragment.deleteSuccess();
                        }else  menusFragment.makeAStatement("Deleting menu failed", Toast.LENGTH_LONG);
                    }
                }
            }.execute();
        }
    }


    public void editMenuName(final Menu menu) {
        if(menusFragment != null){

            new AsyncTask<Void, Void, Menu>(){
                @Override
                protected Menu doInBackground(Void... params) {
                    Menu result;
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(menusFragment.getActivity());
                    ContentValues editContentValues = new ContentValues();
                    editContentValues.put(MenusTable.getSecondColumnName(), menu.getName());
                    String[] menusId = {String.valueOf(menu.getMenuId())};
                    String whereClause = String.format("%s = ?", MenusTable.getFirstColumnName());
                    if(menuDataBase.update(MenusTable.getTableName(), editContentValues, whereClause, menusId) != -1)
                        result = menu;
                    else  result = null;

                    menuDataBase.close();

                    return result;
                }

                @Override
                protected void onPostExecute(Menu result) {
                    if(menusFragment != null) {
                        if (result != null) {
                            changeMenusNameInArrayList(result.getMenuId(), result.getName());
                            menusFragment.editMenusNameSuccess();
                        }else{
                            menusFragment.editMenusNameFailed();
                        }
                    }
                }
            }.execute();
        }
    }

    private void changeMenusNameInArrayList(int menuId, String name) {
        for (Menu menu : menusArrayList) {
            if(menu.getMenuId() == menuId){
                menu.setName(name);
                break;
            }
        }

    }

    class AddNewMenu extends AsyncTask<String, Void, Long>{

        @Override
        protected Long doInBackground(String... params) {
            long menuId;
            MenuDataBase menuDataBase = MenuDataBase.getInstance(menusFragment.getActivity());
            java.sql.Date sqlDate = new java.sql.Date(System.currentTimeMillis());
            menuId = menuDataBase.insert(MenusTable.getTableName(), MenusTable.getContentValues(new Menu(params[0], sqlDate,0, userStorage.getUserId())));
            menuDataBase.close();
            return menuId;
        }

        @Override
        protected void onPostExecute(Long menuId) {
            if (menusFragment != null){
                if(menuId != -1){
                    menusFragment.addNewMenuSuccess(menuId);
                }
                else menusFragment.addNewMenuFailed();
            }
        }
    }

    class LoadMenus extends AsyncTask<Void, Void, List<Menu>>{

        @Override
        protected List<Menu> doInBackground(Void... params) {
            menusArrayList = new ArrayList<>();
            MenuDataBase menuDataBase = MenuDataBase.getInstance(menusFragment.getActivity());
            String query = String.format("SELECT * FROM %s", MenusTable.getTableName());
            Cursor cursor = menuDataBase.downloadData(query);
            if(cursor.getCount() > 0){
                cursor.moveToPosition(-1);
                int menuId, cumulativeNumberOfKcal, authorsId;
                Date creationDate;
                String name;
                while(cursor.moveToNext()){
                    menuId = cursor.getInt(0);
                    name = cursor.getString(1);
                    creationDate = Date.valueOf(cursor.getString(2));
                    cumulativeNumberOfKcal = cursor.getInt(3);
                    authorsId = cursor.getInt(4);
                    menusArrayList.add(new Menu(name, creationDate, cumulativeNumberOfKcal, authorsId,
                            cursor.getInt(5), cursor.getInt(6), cursor.getInt(7)));
                    menusArrayList.get(menusArrayList.size()-1).setMenuId(menuId);
                }
            }
            menuDataBase.close();
            return menusArrayList;
        }

        @Override
        protected void onPostExecute(List<Menu> result) {
            if(menusFragment != null){
                menusFragment.showMenus(result);
            }
        }
    }
}
