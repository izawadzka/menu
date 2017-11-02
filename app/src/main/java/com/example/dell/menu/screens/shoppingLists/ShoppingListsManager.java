package com.example.dell.menu.screens.shoppingLists;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.example.dell.menu.MenuDataBase;
import com.example.dell.menu.events.shoppinglists.DeleteShoppingListEvent;
import com.example.dell.menu.events.shoppinglists.EditShoppingListNameEvent;
import com.example.dell.menu.events.shoppinglists.GenerateShoppingListEvent;
import com.example.dell.menu.events.shoppinglists.SynchronizeShoppingListWithFridgeButtonClickedEvent;
import com.example.dell.menu.objects.menuplanning.Menu;
import com.example.dell.menu.objects.menuplanning.Product;
import com.example.dell.menu.objects.shoppinglist.ShoppingList;
import com.example.dell.menu.tables.MealsProductsTable;
import com.example.dell.menu.tables.MealsTypesDailyMenusAmountOfPeopleTable;
import com.example.dell.menu.tables.MealsTypesMealsDailyMenusTable;
import com.example.dell.menu.tables.MenusDailyMenusTable;
import com.example.dell.menu.tables.ProductsTable;
import com.example.dell.menu.tables.ShoppingListsBlockedProductsTable;
import com.example.dell.menu.tables.ShoppingListsProductsTable;
import com.example.dell.menu.tables.ShoppingListsTable;
import com.example.dell.menu.tables.UsersTable;
import com.example.dell.menu.tables.VirtualFridgeTable;
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
    public void onSynchronizeShoppingListWithFridgeButtonClicked
            (final SynchronizeShoppingListWithFridgeButtonClickedEvent event){
        if(shoppingListsFragment != null) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog
                    .Builder(shoppingListsFragment.getContext());

            alertDialogBuilder.setTitle("Synchronize with the fridge");

            alertDialogBuilder.setMessage("Are you sure you want to synchronize the " +
                    "shopping list with the fridge?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int d) {
                            synchronizeWithFridge(event.shoppingListId);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int d) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();

            alertDialog.show();
        }
    }

    private void synchronizeWithFridge(final long shoppingListId) {
        if(shoppingListsFragment != null){
            new AsyncTask<Void, Void, Integer>(){

                @Override
                protected Integer doInBackground(Void... params) {
                    int result = RESULT_OK;
                    MenuDataBase menuDataBase = MenuDataBase
                            .getInstance(shoppingListsFragment.getActivity());
                    String query = String.format("SELECT sp.%s, %s, %s, %s FROM %s sp " +
                            "JOIN %s vf ON sp.%s = vf.%s WHERE sp.%s = '%s'",
                            ShoppingListsProductsTable.getSecondColumnName(),
                            ShoppingListsProductsTable.getThirdColumnName(),
                            VirtualFridgeTable.getSecondColumnName(),
                            VirtualFridgeTable.getThirdColumnName(),
                            ShoppingListsProductsTable.getTableName(),
                            VirtualFridgeTable.getTableName(),
                            ShoppingListsProductsTable.getSecondColumnName(),
                            VirtualFridgeTable.getFirstColumnName(),
                            ShoppingListsProductsTable.getFirstColumnName(),
                            shoppingListId);

                    Cursor cursor = menuDataBase.downloadData(query);
                    if(cursor.getCount() > 0){
                        cursor.moveToPosition(-1);
                        String shoppingListWhereClause = String.format("%s = ? AND %s = ?",
                                ShoppingListsProductsTable.getFirstColumnName(),
                                ShoppingListsProductsTable.getSecondColumnName());
                        String fridgeWhereClause = String.format("%s = ?",
                                VirtualFridgeTable.getFirstColumnName());
                        while (cursor.moveToNext()){
                            ContentValues shoppingListContentValues = new ContentValues();
                            ContentValues fridgeContentValues = new ContentValues();

                            String[] shoppingListArgs = {String.valueOf(shoppingListId),
                                    String.valueOf(cursor.getInt(0))};
                            String[] fridgeArgs = {String.valueOf(cursor.getInt(0))};

                            double amount;

                            if(cursor.getDouble(2) - cursor.getDouble(3) > 0) {
                                if (cursor.getDouble(1) > cursor.getDouble(2) - cursor.getDouble(3)) {
                                    shoppingListContentValues
                                            .put(ShoppingListsProductsTable.getThirdColumnName(),
                                                    cursor.getDouble(1) - cursor.getDouble(2)
                                            + cursor.getDouble(3));


                                    fridgeContentValues.put(VirtualFridgeTable.getThirdColumnName(),
                                            cursor.getDouble(2));


                                    amount = cursor.getDouble(2) - cursor.getDouble(3);

                                    if (menuDataBase.update(ShoppingListsProductsTable.getTableName(),
                                            shoppingListContentValues, shoppingListWhereClause, shoppingListArgs) == 0
                                            || menuDataBase.update(VirtualFridgeTable.getTableName(),
                                            fridgeContentValues, fridgeWhereClause, fridgeArgs) == 0) {
                                        result = RESULT_ERROR;
                                        break;
                                    }
                                } else {
                                    amount = cursor.getDouble(1);
                                    fridgeContentValues.put(VirtualFridgeTable.getThirdColumnName(),
                                            cursor.getDouble(1) + cursor.getDouble(3));
                                    if (menuDataBase.delete(ShoppingListsProductsTable.getTableName(),
                                            shoppingListWhereClause, shoppingListArgs) == 0
                                            || menuDataBase.update(VirtualFridgeTable.getTableName(),
                                            fridgeContentValues, fridgeWhereClause, fridgeArgs) == 0) {
                                        result = RESULT_ERROR;
                                        break;
                                    }
                                }
                                menuDataBase.insert(ShoppingListsBlockedProductsTable.getTableName(),
                                        ShoppingListsBlockedProductsTable
                                                .getContentValues(shoppingListId, cursor.getInt(0),
                                                        amount));

                            }
                        }
                    }else result = RESULT_EMPTY;
                    menuDataBase.close();
                    return result;
                }

                @Override
                protected void onPostExecute(Integer result) {
                    if(result == RESULT_OK) setShoppingListWasSynchronized(shoppingListId);
                    if(shoppingListsFragment != null){
                        if(result == RESULT_EMPTY) shoppingListsFragment
                                .makeAStatement("No product from the shopping list found in the " +
                                        "fridge", Toast.LENGTH_LONG);
                        else if(result == RESULT_ERROR) shoppingListsFragment
                                .makeAStatement("An error occurred while an attempt to synchronize" +
                                        " the shopping list with the fridge", Toast.LENGTH_LONG);
                    }
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
                            putTheProductsBackToTheFridge(event.shoppingList.getShoppingListId());
                        }
                        else shoppingListsFragment.shoppingListDeleteFailed();
                    }
                }
            }.execute();
        }
    }

    private void putTheProductsBackToTheFridge(final long shoppingListId) {
        if(shoppingListsFragment != null){
            new AsyncTask<Void, Void, Boolean>(){

                @Override
                protected Boolean doInBackground(Void... params) {
                    boolean result = true;
                    MenuDataBase menuDataBase = MenuDataBase
                            .getInstance(shoppingListsFragment.getActivity());
                    String query = String.format("SELECT bp.%s, bp.%s, vf.%s FROM %s bp" +
                            " JOIN %s vf ON bp.%s = vf.%s WHERE bp.%s = '%s' ",
                            ShoppingListsBlockedProductsTable.getSecondColumnName(),
                            ShoppingListsBlockedProductsTable.getThirdColumnName(),
                            VirtualFridgeTable.getThirdColumnName(),
                            ShoppingListsBlockedProductsTable.getTableName(),
                            VirtualFridgeTable.getTableName(),
                            ShoppingListsBlockedProductsTable.getSecondColumnName(),
                            VirtualFridgeTable.getFirstColumnName(),
                            ShoppingListsBlockedProductsTable.getFirstColumnName(),
                            shoppingListId);

                    Cursor cursor = menuDataBase.downloadData(query);

                    if(cursor.getCount() > 0){
                        cursor.moveToPosition(-1);
                        String whereClause = String.format("%s = ?",
                                VirtualFridgeTable.getFirstColumnName());
                        while (cursor.moveToNext()){
                            String[] args = {String.valueOf(cursor.getInt(0))};
                            ContentValues editContentValues = new ContentValues();
                            editContentValues.put(VirtualFridgeTable.getThirdColumnName(),
                                    cursor.getDouble(1) - cursor.getDouble(2));
                            result = menuDataBase.update(VirtualFridgeTable.getTableName(),
                                    editContentValues, whereClause, args) == 1;
                            if(!result) break;
                        }

                        if (result){
                            String clause = String.format("%s = ?",
                                    ShoppingListsBlockedProductsTable.getFirstColumnName());
                            String[] whereArgs = {String.valueOf(shoppingListId)};
                            menuDataBase.delete(ShoppingListsBlockedProductsTable.getTableName(),
                                    clause, whereArgs);
                        }
                    }
                    menuDataBase.close();
                    return result;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    if(result){
                        shoppingListsFragment.shoppingListDeleteSuccess();
                        shoppingListsFragment.showShoppingLists(shoppingLists);
                    }else shoppingListsFragment.shoppingListDeleteFailed();
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
