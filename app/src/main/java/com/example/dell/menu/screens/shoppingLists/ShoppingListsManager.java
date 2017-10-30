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
    private boolean generateNewShoppingListEvent;
    private List<ShoppingList> shoppingLists;
    private int idOfShoppingListToEdit;

    public ShoppingListsManager(Bus bus) {
        this.bus = bus;
        bus.register(this);
        generateNewShoppingListEvent = false;
    }


    public Bus getBus() {
        return bus;
    }

    public List<ShoppingList> getShoppingLists() {
        return shoppingLists;
    }

    public boolean isGenerateNewShoppingListEvent() {
        return generateNewShoppingListEvent;
    }

    public void resetGenerateNewShoppingListEvent(){
        generateNewShoppingListEvent = false;
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
        generateNewShoppingListEvent = true;
    }

    @Subscribe
    public void onDeleteShoppingListEvent(DeleteShoppingListEvent event){
        if(shoppingListsFragment != null){
            new DeleteShoppingList().execute(event.shoppingList);
        }
    }

    public void updateShoppingListName(String shoppingListName) {
        if(shoppingListsFragment != null){
            new UpdateShoppingListName().execute(shoppingListName);
        }
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
            new CreateShoppingList().execute();
        }
    }

    private void addConnectionBetweenShoppingListAndMenu(Long shoppingListId) {
        if(shoppingListsFragment != null) {
            new ConnectShoppingListAndMenu().execute(shoppingListId);
        }
    }

    public void loadShoppingLists() {
        if(shoppingListsFragment != null){
            new LoadShoppingLists().execute();
        }
    }

    class LoadShoppingLists  extends AsyncTask<Void, Integer, List<ShoppingList>>{

        @Override
        protected List<ShoppingList> doInBackground(Void... params) {
            shoppingLists = new ArrayList<>();
            MenuDataBase menuDataBase = MenuDataBase.getInstance(shoppingListsFragment.getActivity());
            String query = String.format("SELECT * FROM %s", ShoppingListsTable.getTableName());
            Cursor cursor = menuDataBase.downloadData(query);
            if(cursor.getCount() > 0){
                cursor.moveToPosition(-1);
                while (cursor.moveToNext()){
                    Date creationDate = Date.valueOf(cursor.getString(2));
                    shoppingLists.add(new ShoppingList(cursor.getString(1), creationDate, cursor.getInt(3)));
                    shoppingLists.get(shoppingLists.size()-1).setShoppingListId(cursor.getInt(0));
                }
            }

            for (ShoppingList shoppingList : shoppingLists) {
                String authorsNameQuery = String.format("SELECT %s FROM %s WHERE %s = '%s'",
                        UsersTable.getSecondColumnName(), UsersTable.getTableName(),
                        UsersTable.getFirstColumnName(), shoppingList.getAuthorsId());
                Cursor authorsNameCursor = menuDataBase.downloadData(authorsNameQuery);
                if(authorsNameCursor.getCount() > 0){
                    authorsNameCursor.moveToPosition(-1);
                    while (authorsNameCursor.moveToNext()){
                        shoppingList.setAuthorsName(authorsNameCursor.getString(0));
                    }
                }
            }


            return shoppingLists;
        }

        @Override
        protected void onPostExecute(List<ShoppingList> shoppingLists) {
            if(shoppingLists.size() > 0){
                if (shoppingListsFragment != null){
                    shoppingListsFragment.showShoppingLists(shoppingLists);
                } // TODO: 08.06.2017 else
            }
        }
    }

    class ConnectShoppingListAndMenu extends AsyncTask<Long, Integer, Long>{

        @Override
        protected Long doInBackground(Long... params) {
            MenuDataBase menuDataBase = MenuDataBase.getInstance(shoppingListsFragment.getActivity());
            /*long result = menuDataBase.insert(ShoppingListsMenusTable.getTableName(),
                    ShoppingListsMenusTable.getContentValues(params[0], currentMenu.getMenuId()));
            return result;*/
            return null;
        }

        @Override
        protected void onPostExecute(Long result) {
            if(result > 0){
                resetGenerateNewShoppingListEvent();
                shoppingListsFragment.generateShoppingListSuccess();
            }else{
                shoppingListsFragment.showStatment("Error while trying to connect shopping list and menu");
            }
        }
    }

    class CreateShoppingList extends AsyncTask<Void, Integer, Long>{

        @Override
        protected Long doInBackground(Void... params) {
            try{
            MenuDataBase menuDataBase = MenuDataBase.getInstance(shoppingListsFragment.getActivity());
            java.sql.Date sqlDate = new java.sql.Date(System.currentTimeMillis());
            long result = menuDataBase.insert(ShoppingListsTable.getTableName(),
                    ShoppingListsTable.getContentValues(new ShoppingList(shoppingListName, sqlDate, shoppingListAuthorsId)));
            menuDataBase.close();
            return result;}
            catch (Exception e){
                Log.d("create", e.getLocalizedMessage());
                return (long)-1;
            }
        }

        @Override
        protected void onPostExecute(Long result) {
            if(result != -1){
                //addConnectionBetweenShoppingListAndMenu(result);
                addProductsToShoppingList(result);
            }else{
             shoppingListsFragment.createShoppingListFailed();
            }
        }
    }

    private void addProductsToShoppingList(Long result) {
        if(shoppingListsFragment != null){
            new AddProductsToShoppingList().execute(result);
        }
    }

    class AddProductsToShoppingList extends AsyncTask<Long, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Long... params) {
            MenuDataBase menuDataBase = MenuDataBase.getInstance(shoppingListsFragment.getActivity());
            ArrayList<Product> productList = new ArrayList<>();
/*
            String[] mealTypesTableNames = {BreakfastTable.getTableName(), LunchTable.getTableName(),
                    DinnerTable.getTableName(), TeatimeTable.getTableName(), SupperTable.getTableName()};

            try {
                for (String tableName : mealTypesTableNames) {
                    String query = String.format("SELECT productId,quantity FROM Meals_Products WHERE " +
                            "mealId IN (SELECT mealId FROM %s WHERE dailyMenuId IN " +
                            "(SELECT dailyMenuId FROM MenusDailyMenus WHERE menuId = '%s'))", tableName, currentMenu.getMenuId());

                    Cursor cursor = menuDataBase.downloadData(query);
                    if (cursor.getCount() > 0) {
                        cursor.moveToPosition(-1);
                        while (cursor.moveToNext()) {
                            //if(menuDataBase.insert(ShoppingListsProductsTable.getTableName(),
                            //ShoppingListsProductsTable.getContentValues(params[0], cursor.getLong(0), cursor.getDouble(1)))== -1){
                            //menuDataBase.close();
                            //return false;
                            int indx = wasAddedPreviously(cursor.getLong(0), productList);
                            if (indx != -1) {
                                productList.get(indx).addQuantity(cursor.getDouble(1));
                            } else {
                                productList.add(new Product(cursor.getInt(0), cursor.getDouble(1)));
                            }
                        }

                    }
                }
                for (Product product : productList) {
                    if(menuDataBase.insert(ShoppingListsProductsTable.getTableName(),
                            ShoppingListsProductsTable.getContentValues(params[0], product.getProductId(), product.getQuantity())) == -1){
                        menuDataBase.close();
                        return false;
                    }
                }
        }catch (Exception e){
                Log.d(getClass().getName(), e.getLocalizedMessage());
                menuDataBase.close();
                return false;
            }

            menuDataBase.close();
            return true;
        }

        private int wasAddedPreviously(long productId, ArrayList<Product> products) {
            for (int i = 0; i < products.size(); i++) {
                if(products.get(i).getProductId() == productId) return i;
            }
            */
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(shoppingListsFragment != null){
                if(result){
                    resetGenerateNewShoppingListEvent();
                    shoppingListsFragment.generateShoppingListSuccess();
                }
                else shoppingListsFragment.createShoppingListFailed();
            }
        }
    }
}
