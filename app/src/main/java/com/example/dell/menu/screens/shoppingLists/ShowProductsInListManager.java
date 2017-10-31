package com.example.dell.menu.screens.shoppingLists;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.dell.menu.MenuDataBase;
import com.example.dell.menu.events.shoppinglists.DeleteProductFromShoppingListEvent;
import com.example.dell.menu.events.shoppinglists.QuantityOfProductChangedEvent;
import com.example.dell.menu.objects.menuplanning.Product;
import com.example.dell.menu.tables.ProductsTable;
import com.example.dell.menu.tables.ShoppingListsProductsTable;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 08.06.2017.
 */

public class ShowProductsInListManager {
    private Bus bus;
    private ShowProductsInListActivity showProductsInListActivity;
    private int shoppingListId;
    private int productToChangeId;
    private ProductsAdapter.ProductsInListViewHolder holder;
    private int idOfProductToDelete;
    private List<Product> productsInShoppingList;

    public ShowProductsInListManager(Bus bus){
        this.bus = bus;
        bus.register(this);
        shoppingListId = 0;
    }

    public void onAttach(ShowProductsInListActivity showProductsInListActivity){
        this.showProductsInListActivity = showProductsInListActivity;
    }

    public void onStop(){
        this.showProductsInListActivity = null;
    }

    void loadProducts() {
        if(showProductsInListActivity != null){
            new DownloadProductsFromDatabase().execute();
        }
    }

    public int getShoppingListId() {
        return shoppingListId;
    }

    List<Product> getProductsInShoppingList() {
        return productsInShoppingList;
    }

    @Subscribe
    public void onDeleteProductFromShoppingList(DeleteProductFromShoppingListEvent event){
        if(showProductsInListActivity != null){
            idOfProductToDelete = event.productId;

            new AsyncTask<Void, Void, Boolean>(){
                @Override
                protected Boolean doInBackground(Void... params) {
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(showProductsInListActivity);
                    String[] ids = {String.valueOf(idOfProductToDelete), String.valueOf(shoppingListId)};
                    boolean result;
                    String whereClause = String.format("%s = ? AND %s = ?", ShoppingListsProductsTable.getSecondColumnName(),
                            ShoppingListsProductsTable.getFirstColumnName());
                    try {
                        menuDataBase.delete(ShoppingListsProductsTable.getTableName(), whereClause,
                                ids);
                        result = true;
                    }catch (Exception e){
                        Log.e(getClass().getName(), e.getLocalizedMessage());
                        result = false;
                    }

                    menuDataBase.close();
                    return result;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    if(showProductsInListActivity != null){
                        if(result) {
                            if(deleteProductFromArrayList(idOfProductToDelete)){
                                showProductsInListActivity.productFromShoppingListDeletedSuccessfully();
                            }else loadProducts();

                        }
                        else showProductsInListActivity.makeAStatement("An error occurred while an attempt to delete product from shopping list",
                                Toast.LENGTH_LONG);
                    }
                }
            }.execute();
        }
    }

    void findProducts(final String newText) {
        if(showProductsInListActivity != null) {
            new AsyncTask<Void, Void, List<Product>>() {
                @Override
                protected List<Product> doInBackground(Void... params) {
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(showProductsInListActivity);
                    productsInShoppingList = new ArrayList<>();

                    String query = String.format("SELECT productsId, name, storageType, " +
                            "totalQuantity FROM %s p JOIN %s slp ON p.productsId = slp.productId " +
                            "WHERE %s = '%s' AND %s LIKE '%%%s%%' ORDER BY %s ",
                            ProductsTable.getTableName(), ShoppingListsProductsTable.getTableName(),
                            ShoppingListsProductsTable.getFirstColumnName(), shoppingListId,
                            ProductsTable.getSecondColumnName(),
                            newText,
                            ProductsTable.getSecondColumnName());

                    Cursor cursor = menuDataBase.downloadData(query);
                    if (cursor.getCount() > 0) {
                        cursor.moveToPosition(-1);
                        while (cursor.moveToNext()) {
                            productsInShoppingList.add(new Product(cursor.getInt(0), cursor.getString(1), cursor.getDouble(3), cursor.getString(2)));
                        }
                    }

                    menuDataBase.close();
                    return productsInShoppingList;
                }

                @Override
                protected void onPostExecute(List<Product> products) {
                    if (products.size() > 0) {
                        showProductsInListActivity.setProducts(products);
                        showProductsInListActivity.makeAStatement("You can change quantity by clicking on it", Toast.LENGTH_LONG);
                    } else {
                        showProductsInListActivity.shoppingListIsEmpty();
                    }
                }
            }.execute();
        }
    }


    private boolean deleteProductFromArrayList(int idOfProductToDelete) {
        int indx = -1;
        for (int i = 0; i < productsInShoppingList.size(); i++) {
            if(productsInShoppingList.get(i).getProductId() == idOfProductToDelete){
                indx = i;
                break;
            }
        }
        if(indx != -1) productsInShoppingList.remove(indx);

        return indx != -1;
    }

    @Subscribe
    public void onQuantityOfProductChangedEvent(final QuantityOfProductChangedEvent event){
        if(showProductsInListActivity != null){
            productToChangeId = event.productId;
            holder = event.holder;

            new AsyncTask<Void, Void, Boolean>(){
                @Override
                protected Boolean doInBackground(Void... params) {
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(showProductsInListActivity);
                    boolean result;
                    ContentValues editContentValues = new ContentValues();
                    editContentValues.put(ShoppingListsProductsTable.getThirdColumnName(), event.quantity);

                    String[] whereClauseArgs = {String.valueOf(shoppingListId),String.valueOf(productToChangeId)};

                    String whereClause = String.format("%s = ? AND %s = ?",
                            ShoppingListsProductsTable.getFirstColumnName(),ShoppingListsProductsTable.getSecondColumnName());

                    result = menuDataBase.update(ShoppingListsProductsTable.getTableName(), editContentValues, whereClause,
                            whereClauseArgs) != -1;
                    menuDataBase.close();
                    return result;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    if(showProductsInListActivity != null){
                        if(result){
                            showProductsInListActivity.updateQuantitySuccess(holder);
                        }else showProductsInListActivity.updateQuantityFailed();
                    }
                }
            }.execute();
        }
    }

    public void setShoppingListId(int shoppingListId) {
        this.shoppingListId = shoppingListId;
    }


    class DownloadProductsFromDatabase extends AsyncTask<Void, Integer, List<Product>>{

        @Override
        protected List<Product> doInBackground(Void... params) {
            MenuDataBase menuDataBase = MenuDataBase.getInstance(showProductsInListActivity);
            productsInShoppingList = new ArrayList<>();

            String query = String.format("SELECT productsId, name, storageType, totalQuantity FROM " +
                    "%s p JOIN %s slp ON p.productsId = slp.productId WHERE %s = '%s' ORDER BY %s ",
                    ProductsTable.getTableName(), ShoppingListsProductsTable.getTableName(),
                    ShoppingListsProductsTable.getFirstColumnName(),shoppingListId,
                    ProductsTable.getSecondColumnName());

            Cursor cursor = menuDataBase.downloadData(query);
            if (cursor.getCount() > 0){
                cursor.moveToPosition(-1);
                while (cursor.moveToNext()){
                    productsInShoppingList.add(new Product(cursor.getInt(0), cursor.getString(1), cursor.getDouble(3), cursor.getString(2)));
                }
            }

            menuDataBase.close();
            return productsInShoppingList;
        }

        @Override
        protected void onPostExecute(List<Product> products) {
            if(products.size() > 0){
                showProductsInListActivity.setProducts(products);
                showProductsInListActivity.makeAStatement("You can change quantity by clicking on it", Toast.LENGTH_LONG);
            }else{
                showProductsInListActivity.shoppingListIsEmpty();
            }
        }
    }
}
