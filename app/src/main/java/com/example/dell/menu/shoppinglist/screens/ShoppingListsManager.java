package com.example.dell.menu.shoppinglist.screens;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.example.dell.menu.data.MenuDataBase;
import com.example.dell.menu.data.tables.ProductsTable;
import com.example.dell.menu.menuplanning.types.StorageType;
import com.example.dell.menu.shoppinglist.events.DeleteShoppingListEvent;
import com.example.dell.menu.shoppinglist.events.EditShoppingListNameEvent;
import com.example.dell.menu.shoppinglist.events.GenerateShoppingListEvent;
import com.example.dell.menu.shoppinglist.events.SendShoppingListInSmsButtonClickedEvent;
import com.example.dell.menu.shoppinglist.events.SynchronizeShoppingListWithFridgeButtonClickedEvent;
import com.example.dell.menu.menuplanning.objects.Menu;
import com.example.dell.menu.menuplanning.objects.Product;
import com.example.dell.menu.shoppinglist.objects.ShoppingList;
import com.example.dell.menu.data.tables.ShoppingListsProductsTable;
import com.example.dell.menu.data.tables.ShoppingListsTable;
import com.example.dell.menu.data.tables.UsersTable;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 07.06.2017.
 */

public class ShoppingListsManager {
    public static final int RESULT_ERROR = -1;
    public static final int RESULT_EMPTY = 1;
    public static final int RESULT_OK = 0;
    private final Bus bus;
    private ShoppingListsFragment shoppingListsFragment;
    private Menu currentMenu;
    private String shoppingListName;
    private long shoppingListAuthorsId;
    private boolean waitingToGenerateNewShoppingList;
    private boolean createShoppingList_mode = false;
    private List<ShoppingList> shoppingLists = new ArrayList<>();
    private long idOfShoppingListToEdit;

    public ShoppingListsManager(Bus bus) {
        this.bus = bus;
        bus.register(this);
        waitingToGenerateNewShoppingList = false;
    }

    void setCreateShoppingList_mode(boolean createShoppingList_mode) {
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
    public void onSendShoppingListInSmsButtonClicked(final SendShoppingListInSmsButtonClickedEvent event){
        if(shoppingListsFragment != null){
            new AsyncTask<Void, Void, String>(){

                @Override
                protected String doInBackground(Void... params) {
                    String message = "";
                    MenuDataBase menuDataBase = MenuDataBase
                            .getInstance(shoppingListsFragment.getActivity());

                    String query = String.format("SELECT productsId, name, storageType, totalQuantity, " +
                                    "wasBought FROM %s p JOIN %s slp ON p.productsId = slp.productId WHERE " +
                                    "%s = '%s' ORDER BY %s ",
                            ProductsTable.getTableName(), ShoppingListsProductsTable.getTableName(),
                            ShoppingListsProductsTable.getFirstColumnName(),event.shoppingListId,
                            ProductsTable.getSecondColumnName());

                    Cursor cursor = menuDataBase.downloadData(query);
                    if (cursor.getCount() > 0){
                        cursor.moveToPosition(-1);
                        while (cursor.moveToNext()){
                            message += cursor.getString(1) + " " + cursor.getDouble(3) +
                                    StorageType.getUnit(cursor.getString(2)) + ",\n";
                        }
                    }
                    menuDataBase.close();
                    return message;
                }

                @Override
                protected void onPostExecute(String s) {
                    if(s.length() != 0){
                        Log.i("slm", "message: " + s);
                    }else Log.i("slm", "niby puste ale: " + s);
                }
            }.execute();
        }
    }



    private void setShoppingListWasSynchronized(final long shoppingListId) {
        if(shoppingListsFragment != null){
            new AsyncTask<Void, Void, Boolean>(){

                @Override
                protected Boolean doInBackground(Void... params) {
                    boolean result;
                    MenuDataBase menuDataBase = MenuDataBase
                            .getInstance(shoppingListsFragment.getActivity());
                    ContentValues editContentValues = new ContentValues();
                    editContentValues.put(ShoppingListsTable.getFifthColumnName(),1);

                    String whereClause = String.format("%s = ?",
                            ShoppingListsTable.getFirstColumnName());

                    String[] args = {String.valueOf(shoppingListId)};

                    result = menuDataBase.update(ShoppingListsTable.getTableName(),
                            editContentValues, whereClause, args) == 1;
                    menuDataBase.close();
                    return result;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    if(shoppingListsFragment != null){
                        if (result){
                            shoppingListsFragment
                                .makeAStatement("Synchronizing completed!", Toast.LENGTH_SHORT);
                            loadShoppingLists();
                        }
                        else shoppingListsFragment
                                .makeAStatement("An error occured while an attempt to mark the list" +
                                        " as synchronized", Toast.LENGTH_LONG);
                    }
                }
            }.execute();
        }
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
    public void onDeleteShoppingListEvent(final DeleteShoppingListEvent event){
        if(shoppingListsFragment != null){
            //new DeleteShoppingList().execute(event.shoppingList);
            new AsyncTask<Void, Void, Boolean>(){
                @Override
                protected Boolean doInBackground(Void... params) {
                    boolean result = true;
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(shoppingListsFragment.getActivity());

                    String[] shoppingListsId = {String.valueOf(event.shoppingList.getShoppingListId())};

                    result = menuDataBase.delete(ShoppingListsTable.getTableName(), String.format("%s = ?",
                            ShoppingListsTable.getFirstColumnName()), shoppingListsId) > 0;


                    if(result){
                        menuDataBase.delete(ShoppingListsProductsTable.getTableName(),
                                String.format("%s = ?",
                                        ShoppingListsProductsTable.getFirstColumnName()),
                                shoppingListsId);


                        int indx = -1;
                        for (int i = 0; i < shoppingLists.size(); i++) {
                            if (shoppingLists.get(i).getShoppingListId() == event.shoppingList.getShoppingListId()) {
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
                            shoppingListsFragment.showShoppingLists(shoppingLists);
                        }
                        else shoppingListsFragment.shoppingListDeleteFailed();
                    }
                }
            }.execute();
        }
    }


    void updateShoppingListName(String shoppingListName) {
        if(shoppingListsFragment != null){
            new UpdateShoppingListName().execute(shoppingListName);
        }
    }

    void findLists(final String newText) {
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

    void addNewShoppingList(String shoppingListName, Long userId) {
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


                    String query = String.format("SELECT %s, %s, %s, %s, %s, %s FROM %s  sl JOIN " +
                                    " %s u ON sl.%s = u.%s ORDER BY %s",
                            ShoppingListsTable.getFirstColumnName(),
                            ShoppingListsTable.getSecondColumnName(),
                            ShoppingListsTable.getThirdColumnName(),
                            ShoppingListsTable.getFourthColumnName(),
                            ShoppingListsTable.getFifthColumnName(),
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
                            shoppingLists.get(shoppingLists.size()-1).setAuthorsName(cursor.getString(5));
                            shoppingLists.get(shoppingLists.size()-1).setAlreadySynchronized(cursor.getInt(4) != 0);
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
