package com.example.dell.menu.screens.shoppingLists;

import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.example.dell.menu.MenuDataBase;
import com.example.dell.menu.objects.Product;
import com.example.dell.menu.tables.MealsProductsTable;
import com.example.dell.menu.tables.MenusDailyMenusTable;
import com.example.dell.menu.tables.ProductsTable;
import com.example.dell.menu.tables.ShoppingListsMenusTable;
import com.example.dell.menu.tables.mealTypes.BreakfastTable;
import com.example.dell.menu.tables.mealTypes.DinnerTable;
import com.example.dell.menu.tables.mealTypes.LunchTable;
import com.example.dell.menu.tables.mealTypes.MealTypeBaseTable;
import com.example.dell.menu.tables.mealTypes.SupperTable;
import com.example.dell.menu.tables.mealTypes.TeatimeTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 08.06.2017.
 */

public class ShowProductsInListManager {
    private ShowProductsInListActivity showProductsInListActivity;
    private int shoppingListId;
    private int menuId;

    public ShowProductsInListManager(){
        menuId = 0;
        shoppingListId = 0;
    }

    public void onAttach(ShowProductsInListActivity showProductsInListActivity){
        this.showProductsInListActivity = showProductsInListActivity;
    }

    public void onStop(){
        this.showProductsInListActivity = null;
    }

    public void loadProducts() {
        if(showProductsInListActivity != null){
            new LoadMenuId().execute();
        }
    }

    public int getShoppingListId() {
        return shoppingListId;
    }

    public void setShoppingListId(int shoppingListId) {
        this.shoppingListId = shoppingListId;
    }

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    private void downloadProductsFromDatabase() {
        if(showProductsInListActivity != null){
            new DownloadProductsFromDatabase().execute();
        }
    }


    class DownloadProductsFromDatabase extends AsyncTask<Void, Integer, List<Product>>{

        private List<Product> productsInShoppingList;

        @Override
        protected List<Product> doInBackground(Void... params) {
            MenuDataBase menuDataBase = MenuDataBase.getInstance(showProductsInListActivity);
            productsInShoppingList = new ArrayList<>();

            String breakfastQuery = String.format(String.format("SELECT name, storageType, quantity FROM Products p JOIN Meals_Products mp\n" +
                    "ON p.productsId = mp.productId\n" +
                    "where \n" +
                    " mp.mealId IN\n" +
                    "(SELECT mealId FROM Breakfast WHERE dailyMenuId IN\n" +
                    "(SELECT dailyMenuId FROM MenusDailyMenus WHERE menuId = '%s'))", menuId));
            Cursor breakfastCursor = menuDataBase.downloadDatas(breakfastQuery);
            if(breakfastCursor.getCount() > 0){
                breakfastCursor.moveToPosition(-1);
                while (breakfastCursor.moveToNext()) {
                    int result = findProduct(breakfastCursor.getString(0), breakfastCursor.getString(1));
                    if(result == -1) {
                        productsInShoppingList.add(new Product(breakfastCursor.getString(0),
                                breakfastCursor.getInt(2),
                                breakfastCursor.getString(1)));
                    }else{
                        productsInShoppingList.get(result).addQuantity(breakfastCursor.getInt(2));
                    }
                }
            }

            String lunchQuery = String.format(String.format("SELECT name, storageType, quantity FROM Products p JOIN Meals_Products mp\n" +
                    "ON p.productsId = mp.productId\n" +
                    "where \n" +
                    " mp.mealId IN\n" +
                    "(SELECT mealId FROM Lunch WHERE dailyMenuId IN\n" +
                    "(SELECT dailyMenuId FROM MenusDailyMenus WHERE menuId = '%s'))", menuId));
            Cursor lunchCursor = menuDataBase.downloadDatas(lunchQuery);
            if(lunchCursor.getCount() > 0){
               lunchCursor.moveToPosition(-1);
                while (lunchCursor.moveToNext()) {
                    int result = findProduct(lunchCursor.getString(0), lunchCursor.getString(1));
                    if(result == -1) {
                        productsInShoppingList.add(new Product(lunchCursor.getString(0),
                                lunchCursor.getInt(2),
                                lunchCursor.getString(1)));
                    }else{
                       productsInShoppingList.get(result).addQuantity(lunchCursor.getInt(2));
                    }
                }
            }

            String dinnerQuery = String.format(String.format("SELECT name, storageType, quantity FROM Products p JOIN Meals_Products mp\n" +
                    "ON p.productsId = mp.productId\n" +
                    "where \n" +
                    " mp.mealId IN\n" +
                    "(SELECT mealId FROM Dinner WHERE dailyMenuId IN\n" +
                    "(SELECT dailyMenuId FROM MenusDailyMenus WHERE menuId = '%s'))", menuId));
            Cursor dinnerCursor = menuDataBase.downloadDatas(dinnerQuery);
            if(dinnerCursor.getCount() > 0){
                dinnerCursor.moveToPosition(-1);
                while (dinnerCursor.moveToNext()) {

                    int result = findProduct(dinnerCursor.getString(0), dinnerCursor.getString(1));
                    if(result == -1) {
                        productsInShoppingList.add(new Product(dinnerCursor.getString(0),
                                dinnerCursor.getInt(2),
                                dinnerCursor.getString(1)));
                    }else{
                        productsInShoppingList.get(result).addQuantity(dinnerCursor.getInt(2));
                    }
                }
            }


            String teatimeQuery = String.format(String.format("SELECT name, storageType, quantity FROM Products p JOIN Meals_Products mp\n" +
                    "ON p.productsId = mp.productId\n" +
                    "where \n" +
                    " mp.mealId IN\n" +
                    "(SELECT mealId FROM Teatime WHERE dailyMenuId IN\n" +
                    "(SELECT dailyMenuId FROM MenusDailyMenus WHERE menuId = '%s'))", menuId));
            Cursor teatimCursor = menuDataBase.downloadDatas(teatimeQuery);
            if(teatimCursor.getCount() > 0){
                teatimCursor.moveToPosition(-1);
                while (teatimCursor.moveToNext()) {

                    int result = findProduct(teatimCursor.getString(0), teatimCursor.getString(1));
                    if(result == -1) {
                        productsInShoppingList.add(new Product(teatimCursor.getString(0),
                                teatimCursor.getInt(2),
                                teatimCursor.getString(1)));
                    }else{
                        productsInShoppingList.get(result).addQuantity(teatimCursor.getInt(2));
                    }
                }
            }


            String supperQuery = String.format(String.format("SELECT name, storageType, quantity FROM Products p JOIN Meals_Products mp\n" +
                    "ON p.productsId = mp.productId\n" +
                    "where \n" +
                    " mp.mealId IN\n" +
                    "(SELECT mealId FROM Supper WHERE dailyMenuId IN\n" +
                    "(SELECT dailyMenuId FROM MenusDailyMenus WHERE menuId = '%s'))", menuId));
            Cursor supperCursor = menuDataBase.downloadDatas(supperQuery);
            if(supperCursor.getCount() > 0){
                supperCursor.moveToPosition(-1);
                while (supperCursor.moveToNext()) {

                    int result = findProduct(supperCursor.getString(0), supperCursor.getString(1));
                    if(result == -1) {
                        productsInShoppingList.add(new Product(supperCursor.getString(0),
                                supperCursor.getInt(2),
                                supperCursor.getString(1)));
                    }else{
                        productsInShoppingList.get(result).addQuantity(supperCursor.getInt(2));
                    }
                }
            }

            menuDataBase.close();
            return productsInShoppingList;
        }

        private int findProduct(String name, String storageType) {
            for (int i = 0; i < productsInShoppingList.size(); i++){
                if(productsInShoppingList.get(i).getName().equals(name) &&
                        productsInShoppingList.get(i).getStorageType().equals(storageType)){
                    return i;
                }
            }
            return -1;
        }

        @Override
        protected void onPostExecute(List<Product> products) {
            if(products.size() > 0){
                showProductsInListActivity.setProducts(products);
            }else{
                showProductsInListActivity.loadingProductsFailed();
            }
        }
    }

    class LoadMenuId extends AsyncTask<Void, Integer, Integer>{

        @Override
        protected Integer doInBackground(Void... params) {
            int menuId = 0;
            MenuDataBase menuDataBase = MenuDataBase.getInstance(showProductsInListActivity);
            String query = String.format("SELECT %s FROM %s WHERE %s = '%s'",
                    ShoppingListsMenusTable.getSecondColumnName(),
                    ShoppingListsMenusTable.getTableName(),
                    ShoppingListsMenusTable.getFirstColumnName(),
                    shoppingListId);

            Cursor cursor = menuDataBase.downloadDatas(query);
            if(cursor.getCount() == 1){
                cursor.moveToPosition(-1);
                while (cursor.moveToNext()){
                    menuId = cursor.getInt(0);
                }
            }
            menuDataBase.close();
            return menuId;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if(result != 0){
                menuId = result;
                downloadProductsFromDatabase();
            }else{
                // TODO: 08.06.2017
            }
        }
    }
}
