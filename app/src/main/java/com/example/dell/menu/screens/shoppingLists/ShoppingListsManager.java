package com.example.dell.menu.screens.shoppingLists;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.example.dell.menu.MenuDataBase;
import com.example.dell.menu.events.shoppinglists.DeleteShoppingListEvent;
import com.example.dell.menu.events.shoppinglists.EditShoppingListNameEvent;
import com.example.dell.menu.events.shoppinglists.GenerateShoppingListEvent;
import com.example.dell.menu.objects.menuplanning.Menu;
import com.example.dell.menu.objects.menuplanning.Product;
import com.example.dell.menu.objects.shoppinglist.ShoppingList;
import com.example.dell.menu.tables.MealsProductsTable;
import com.example.dell.menu.tables.MealsTypesDailyMenusAmountOfPeopleTable;
import com.example.dell.menu.tables.MealsTypesMealsDailyMenusTable;
import com.example.dell.menu.tables.MenusDailyMenusTable;
import com.example.dell.menu.tables.ProductsTable;
import com.example.dell.menu.tables.ShoppingListsProductsTable;
import com.example.dell.menu.tables.ShoppingListsTable;
import com.example.dell.menu.tables.UsersTable;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 07.06.2017.
 */

public class ShoppingListsManager {
    private final Bus bus;
    private ShoppingListsFragment shoppingListsFragment;
    private Menu currentMenu;
    private String shoppingListName;
    private long shoppingListAuthorsId;
    private boolean waitingToGenerateNewShoppingList;
    private boolean createShoppingList_mode = false;
    private List<ShoppingList> shoppingLists = new ArrayList<>();
    private int idOfShoppingListToEdit;

    public ShoppingListsManager(Bus bus) {
        this.bus = bus;
        bus.register(this);
        waitingToGenerateNewShoppingList = false;
    }

    public void setCreateShoppingList_mode(boolean createShoppingList_mode) {
        this.createShoppingList_mode = createShoppingList_mode;
    }

    public Bus getBus() {
        return bus;
    }

    public List<ShoppingList> getShoppingLists() {
        return shoppingLists;
    }

    boolean isWaitingToGenerateNewShoppingList() {
        return waitingToGenerateNewShoppingList;
    }

    private void resetGenerateNewShoppingListEvent(){
        waitingToGenerateNewShoppingList = false;
    }

    public void onAttach(ShoppingListsFragment shoppingListsFragment){
        this.shoppingListsFragment = shoppingListsFragment;
    }

    public void onStop(){
        this.shoppingListsFragment = null;
    }

    @Subscribe
    public void onEditShoppingListNameImageButtonClicked(EditShoppingListNameEvent event){
        if(shoppingListsFragment != null){
            idOfShoppingListToEdit = event.shoppingListId;
            shoppingListsFragment.showEditShoppingListNameDialog(event.shoppingListName);
        }
    }

    @Subscribe
    public void onGenerateNewShoppingListEvent(GenerateShoppingListEvent event){
        currentMenu = event.menu;
        shoppingListAuthorsId = currentMenu.getAuthorsId();
        shoppingListName = currentMenu.getName() + "list";
        waitingToGenerateNewShoppingList = true;
    }

    @Subscribe
    public void onDeleteShoppingListEvent(DeleteShoppingListEvent event){
        if(shoppingListsFragment != null){
            new DeleteShoppingList().execute(event.shoppingList);
        }
    }

   void updateShoppingListName(String shoppingListName) {
        if(shoppingListsFragment != null){
            new UpdateShoppingListName().execute(shoppingListName);
        }
    }

    public void findLists(final String newText) {
        if(shoppingListsFragment != null){
            new AsyncTask<Void, Void, List<ShoppingList>>(){

                @Override
                protected List<ShoppingList> doInBackground(Void... params) {
                    shoppingLists.clear();

                    MenuDataBase menuDataBase = MenuDataBase
                            .getInstance(shoppingListsFragment.getActivity());


                    String query = String.format("SELECT %s, %s, %s, %s, %s FROM %s  sl JOIN " +
                            " %s u ON sl.%s = u.%s WHERE %s LIKE '%%%s%%' ORDER BY %s",
                            ShoppingListsTable.getFirstColumnName(),
                            ShoppingListsTable.getSecondColumnName(),
                            ShoppingListsTable.getThirdColumnName(),
                            ShoppingListsTable.getFourthColumnName(),
                            UsersTable.getSecondColumnName(),
                            ShoppingListsTable.getTableName(),
                            UsersTable.getTableName(),
                            ShoppingListsTable.getFourthColumnName(),
                            UsersTable.getFirstColumnName(),
                            ShoppingListsTable.getSecondColumnName(),
                            newText, ShoppingListsTable.getSecondColumnName());
                    Cursor cursor = menuDataBase.downloadData(query);
                    if(cursor.getCount() > 0){
                        cursor.moveToPosition(-1);
                        while (cursor.moveToNext()){
                            Date creationDate = Date.valueOf(cursor.getString(2));
                            shoppingLists.add(new ShoppingList(cursor.getString(1), creationDate, cursor.getInt(3)));
                            shoppingLists.get(shoppingLists.size()-1).setShoppingListId(cursor.getInt(0));
                            shoppingLists.get(shoppingLists.size()-1).setAuthorsName(cursor.getString(4));
                        }
                    }


                    menuDataBase.close();
                    return shoppingLists;
                }

                @Override
                protected void onPostExecute(List<ShoppingList> shoppingLists) {
                    if(shoppingLists.size() > 0){
                        if (shoppingListsFragment != null){
                            shoppingListsFragment.showShoppingLists(shoppingLists);
                        }
                    }
                }
            }.execute();
        }
    }

    public void addNewShoppingList(String shoppingListName, Long userId) {
        shoppingListAuthorsId = userId;
        this.shoppingListName = shoppingListName;
        createShoppingList();
    }

    class UpdateShoppingListName extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            MenuDataBase menuDataBase = MenuDataBase.getInstance(shoppingListsFragment.getActivity());
            String newName;
            ContentValues editContentValues = new ContentValues();
            editContentValues.put(ShoppingListsTable.getSecondColumnName(), params[0]);
            String[] listId = {String.valueOf(idOfShoppingListToEdit)};
            String whereClause = String.format("%s = ?", ShoppingListsTable.getFirstColumnName());
            if(menuDataBase.update(ShoppingListsTable.getTableName(), editContentValues, whereClause, listId) != -1) newName=params[0];
            else  newName = null;
            menuDataBase.close();
            return newName;
        }

        @Override
        protected void onPostExecute(String shoppingListName) {
            if(shoppingListsFragment != null){
                if(shoppingListName != null) {
                    changeShoppingListNameInArrayList(shoppingListName);
                    shoppingListsFragment.editShoppingListNameSuccess();
                }else shoppingListsFragment.editShoppingListNameFailed();
            }
        }
    }

    private void changeShoppingListNameInArrayList(String shoppingListName) {
        for (ShoppingList shoppingList : shoppingLists) {
            if(shoppingList.getShoppingListId() == idOfShoppingListToEdit){
                shoppingList.setName(shoppingListName);
                break;
            }
        }

    }

    class DeleteShoppingList extends AsyncTask<ShoppingList, Void, Boolean>{

        @Override
        protected Boolean doInBackground(ShoppingList... params) {
            boolean result = true;
            MenuDataBase menuDataBase = MenuDataBase.getInstance(shoppingListsFragment.getActivity());

            String[] shoppingListsId = {String.valueOf(params[0].getShoppingListId())};
            try {
                menuDataBase.delete(ShoppingListsTable.getTableName(), String.format("%s = ?",
                        ShoppingListsTable.getFirstColumnName()), shoppingListsId);
            }catch (Exception e){
                menuDataBase.close();
                return false;
            }

            try {
                menuDataBase.delete(ShoppingListsProductsTable.getTableName(),
                        String.format("%s = ?",ShoppingListsProductsTable.getFirstColumnName()), shoppingListsId);
            }catch (Exception e){
                result = false;
            }

            if(result) {
                int indx = -1;
                for (int i = 0; i < shoppingLists.size(); i++) {
                    if (shoppingLists.get(i).getShoppingListId() == params[0].getShoppingListId()) {
                        indx = i;
                        break;
                    }
                }

                if (indx != -1) {
                    shoppingLists.remove(indx);
                    result = true;
                } else result = false;
            }
            menuDataBase.close();
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(shoppingListsFragment != null){
                if(result) {

                    shoppingListsFragment.shoppingListDeleteSuccess();
                    shoppingListsFragment.showShoppingLists(shoppingLists);}
                else shoppingListsFragment.shoppingListDeleteFailed();
            }
        }
    }

    public void createShoppingList() {
        if(shoppingListsFragment != null){

            new AsyncTask<Void, Void, Long>(){

                @Override
                protected Long doInBackground(Void... params) {
                    long result;
                    MenuDataBase menuDataBase = MenuDataBase
                            .getInstance(shoppingListsFragment.getActivity());

                    java.sql.Date sqlDate = new java.sql.Date(System.currentTimeMillis());
                    result = menuDataBase.insert(ShoppingListsTable.getTableName(),
                            ShoppingListsTable.getContentValues(new ShoppingList(shoppingListName,
                                    sqlDate, shoppingListAuthorsId)));

                    menuDataBase.close();
                    return result;
                }

                @Override
                protected void onPostExecute(Long result) {
                    if(result > 0){
                        if(createShoppingList_mode){
                            if(shoppingListsFragment != null){
                                shoppingListsFragment.goToShoppingList(result);
                            }
                        }
                        else addProductsToShoppingList(result);
                    }else if(shoppingListsFragment != null)
                        shoppingListsFragment.createShoppingListFailed();
                    createShoppingList_mode = false;
                }
            }.execute();
        }
    }

    public void loadShoppingLists() {
        if(shoppingListsFragment != null){
            new AsyncTask<Void, Void, List<ShoppingList>>(){
                @Override
                protected List<ShoppingList> doInBackground(Void... params) {
                    shoppingLists.clear();
                    MenuDataBase menuDataBase = MenuDataBase
                            .getInstance(shoppingListsFragment.getActivity());


                    String query = String.format("SELECT %s, %s, %s, %s, %s FROM %s  sl JOIN " +
                                    " %s u ON sl.%s = u.%s ORDER BY %s",
                            ShoppingListsTable.getFirstColumnName(),
                            ShoppingListsTable.getSecondColumnName(),
                            ShoppingListsTable.getThirdColumnName(),
                            ShoppingListsTable.getFourthColumnName(),
                            UsersTable.getSecondColumnName(),
                            ShoppingListsTable.getTableName(),
                            UsersTable.getTableName(),
                            ShoppingListsTable.getFourthColumnName(),
                            UsersTable.getFirstColumnName(),
                            ShoppingListsTable.getSecondColumnName());
                    Cursor cursor = menuDataBase.downloadData(query);
                    if(cursor.getCount() > 0){
                        cursor.moveToPosition(-1);
                        while (cursor.moveToNext()){
                            Date creationDate = Date.valueOf(cursor.getString(2));
                            shoppingLists.add(new ShoppingList(cursor.getString(1), creationDate, cursor.getInt(3)));
                            shoppingLists.get(shoppingLists.size()-1).setShoppingListId(cursor.getInt(0));
                            shoppingLists.get(shoppingLists.size()-1).setAuthorsName(cursor.getString(4));
                        }
                    }


                    menuDataBase.close();
                    return shoppingLists;
                }

                @Override
                protected void onPostExecute(List<ShoppingList> shoppingLists) {
                    if(shoppingLists.size() > 0){
                        if (shoppingListsFragment != null){
                            shoppingListsFragment.showShoppingLists(shoppingLists);
                        }
                    }
                }
            }.execute();
        }
    }


    private void addProductsToShoppingList(final Long result) {
        if(shoppingListsFragment != null){
            new AsyncTask<Void, Void, Boolean>(){

                @Override
                protected Boolean doInBackground(Void... params) {
                    boolean queryResult = true;
                    ArrayList<Product> products = new ArrayList<Product>();
                    MenuDataBase menuDataBase = MenuDataBase
                            .getInstance(shoppingListsFragment.getActivity());
                    /*
                    String query = String.format("SELECT productId, quantity, name, storageType, " +
                            "mt.mealsTypeId, amountOfPeople FROM Meals_Products mp JOIN Products p " +
                            "ON mp.productId = p.productsId JOIN MealsTypesMealsDailyMenus mt ON " +
                            "mt.mealsId = mp.mealId JOIN " +
                            "MealsTypesDailyMenusAmountOfPeople am on am.dailyMenuId = mt.dailyMenuId " +
                            "and am.mealsTypeId = mt.mealsTypeId WHERE mp.mealId IN " +
                            "(SELECT mealsId FROM MealsTypesMealsDailyMenus WHERE dailyMenuId IN " +
                            "(select MenusDailyMenus.dailyMenuId FROM MenusDailyMenus WHERE " +
                            "MenusDailyMenus.menuId = '%s')) AND mt.dailyMenuId IN " +
                            "(select MenusDailyMenus.dailyMenuId FROM MenusDailyMenus WHERE " +
                            "MenusDailyMenus.menuId = '%s');",
                            currentMenu.getMenuId(), currentMenu.getMenuId());*/
                    String query = String.format("SELECT productId, quantity, " +
                                    "mt.mealsTypeId, amountOfPeople FROM Meals_Products mp JOIN Products p " +
                                    "ON mp.productId = p.productsId JOIN MealsTypesMealsDailyMenus mt ON " +
                                    "mt.mealsId = mp.mealId JOIN " +
                                    "MealsTypesDailyMenusAmountOfPeople am on am.dailyMenuId = mt.dailyMenuId " +
                                    "and am.mealsTypeId = mt.mealsTypeId WHERE mp.mealId IN " +
                                    "(SELECT mealsId FROM MealsTypesMealsDailyMenus WHERE dailyMenuId IN " +
                                    "(select MenusDailyMenus.dailyMenuId FROM MenusDailyMenus WHERE " +
                                    "MenusDailyMenus.menuId = '%s')) AND mt.dailyMenuId IN " +
                                    "(select MenusDailyMenus.dailyMenuId FROM MenusDailyMenus WHERE " +
                                    "MenusDailyMenus.menuId = '%s');",
                            currentMenu.getMenuId(), currentMenu.getMenuId());
                    // TODO: 30.10.2017  
                    Cursor queryCursor = menuDataBase.downloadData(query);
                    if(queryCursor.getCount() > 0){
                        queryCursor.moveToPosition(-1);
                        while (queryCursor.moveToNext()){

                            int indx = wasAddedPreviously(queryCursor.getInt(0), products);
                            if(indx != -1)
                                products.get(indx).addQuantity(queryCursor.getDouble(1)
                                        *queryCursor.getInt(3));
                            else{
                                products.add(new Product(queryCursor.getInt(0),
                                        queryCursor.getDouble(1) * queryCursor.getInt(3)));
                            }
                        }
                    }else queryResult = false;

                    if(queryResult){
                        for (Product product : products) {
                            if(menuDataBase.insert(ShoppingListsProductsTable.getTableName(),
                                    ShoppingListsProductsTable.getContentValues(result,
                                            product.getProductId(), product.getQuantity())) == -1){
                                queryResult = false;
                                break;
                            }
                        }
                    }

                    menuDataBase.close();
                    return queryResult;
                }

                private int wasAddedPreviously(long productId, ArrayList<Product> products) {
                    for (int i = 0; i < products.size(); i++) {
                        if(products.get(i).getProductId() == productId) return i;
                    }

                    return -1;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    if(result) waitingToGenerateNewShoppingList = false;

                    if(shoppingListsFragment != null){
                        if(result) shoppingListsFragment.generateShoppingListSuccess();
                        else shoppingListsFragment.createShoppingListFailed();
                    }
                }
            }.execute();
        }
    }
}
