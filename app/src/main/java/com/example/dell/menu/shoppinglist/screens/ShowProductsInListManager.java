package com.example.dell.menu.shoppinglist.screens;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.dell.menu.data.MenuDataBase;
import com.example.dell.menu.data.backup.Backup;
import com.example.dell.menu.data.tables.VirtualFridgeTable;
import com.example.dell.menu.menuplanning.objects.Menu;
import com.example.dell.menu.shoppinglist.events.DeleteProductFromShoppingListEvent;
import com.example.dell.menu.shoppinglist.events.QuantityOfProductChangedEvent;
import com.example.dell.menu.menuplanning.objects.Product;
import com.example.dell.menu.data.tables.ProductsTable;
import com.example.dell.menu.data.tables.ShoppingListsProductsTable;
import com.example.dell.menu.shoppinglist.screens.ProductsAdapter;
import com.example.dell.menu.shoppinglist.screens.ShowProductsInListActivity;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 08.06.2017.
 */

public class ShowProductsInListManager {
    public static final int RESULT_OK = 1;
    public static final int RESULT_ERROR = -1;
    public static final int RESULT_EMPTY = 0;
    private Bus bus;
    private ShowProductsInListActivity showProductsInListActivity;
    private long shoppingListId;
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

    public long getShoppingListId() {
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

    public void setShoppingListId(long shoppingListId) {
        this.shoppingListId = shoppingListId;
    }

    public void setProductWasBought(final Product product, final long shoppingListId) {
        if(showProductsInListActivity != null){
            new AsyncTask<Void, Void, Boolean>(){

                @Override
                protected Boolean doInBackground(Void... params) {
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(showProductsInListActivity);

                    ContentValues editContentValues = new ContentValues();
                    editContentValues.put(ShoppingListsProductsTable.getFourthColumnName(),
                            product.isBought());

                    String whereClause = String.format("%s = ? AND %s = ?",
                            ShoppingListsProductsTable.getFirstColumnName(),
                            ShoppingListsProductsTable.getSecondColumnName());

                    String[] whereArgs = {String.valueOf(shoppingListId),
                            String.valueOf(product.getProductId())};

                    boolean result = menuDataBase.update(ShoppingListsProductsTable.getTableName(),
                            editContentValues, whereClause, whereArgs) == 1;

                    menuDataBase.close();
                    return result;
                }
            }.execute();
        }
    }

    public void moveCrossedProductsToVirtualFridge() {
        if(showProductsInListActivity != null){
            new AsyncTask<Void, Void, Integer>(){

                @Override
                protected Integer doInBackground(Void... params) {
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(showProductsInListActivity);

                    int result = RESULT_OK;

                    String whereClause = String.format("%s = ?",
                            VirtualFridgeTable.getFirstColumnName());

                    int counter = 0;
                    String deleteQuery = String.format("%s = ? AND %s = ?",
                            ShoppingListsProductsTable.getFirstColumnName(),
                            ShoppingListsProductsTable.getSecondColumnName());

                    for (Product product : productsInShoppingList) {
                        if(product.isBought()){
                            counter++;
                            String[] deleteArgs = {String.valueOf(shoppingListId),
                                    String.valueOf(product.getProductId())};
                            String[] whereArgs = {String.valueOf(product.getProductId())};
                            String query = String.format("SELECT * FROM %s WHERE %s = '%s';",
                                    VirtualFridgeTable.getTableName(),
                                    VirtualFridgeTable.getFirstColumnName(),
                                    product.getProductId());
                            Cursor cursor = menuDataBase.downloadData(query);
                            if(cursor.getCount() == 1){
                                cursor.moveToPosition(0);
                                ContentValues editContentValues = new ContentValues();
                                editContentValues.put(VirtualFridgeTable.getSecondColumnName(),
                                        cursor.getDouble(1) + product.getQuantity());
                                if(menuDataBase.update(VirtualFridgeTable.getTableName(),
                                        editContentValues, whereClause, whereArgs) == 1) {
                                    result = RESULT_OK;
                                    menuDataBase.delete(ShoppingListsProductsTable.getTableName(),
                                            deleteQuery, deleteArgs);
                                }
                                else result = RESULT_ERROR;
                            }else if(cursor.getCount() == 0){
                                if(menuDataBase.insert(VirtualFridgeTable.getTableName(),
                                        VirtualFridgeTable.getContentValues(product.getProductId(),
                                                product.getQuantity())) > 0){
                                    result = RESULT_OK;
                                    menuDataBase.delete(ShoppingListsProductsTable.getTableName(),
                                            deleteQuery, deleteArgs);
                                }
                                else result = RESULT_ERROR;
                            }else result = RESULT_ERROR;
                        }
                    }

                    if(counter == 0) result = RESULT_EMPTY;

                    menuDataBase.close();
                    return result;
                }

                @Override
                protected void onPostExecute(Integer result) {
                    if(showProductsInListActivity != null){
                        if(result == RESULT_OK){
                            showProductsInListActivity.movingProductsSuccess();
                            loadProducts();
                        }
                        else if(result == RESULT_ERROR)
                            showProductsInListActivity.movingProductsFailed();
                        else if(result == RESULT_EMPTY)
                            showProductsInListActivity.noProductsWereCrossed();
                    }
                }
            }.execute();
        }
    }

    public void crossAllProducts() {
        for (Product product : productsInShoppingList) {
            if(!product.isBought()) {
                product.setBought(true);
                setProductWasBought(product, shoppingListId);
                if (showProductsInListActivity != null)
                    showProductsInListActivity.setProducts(productsInShoppingList);
            }
        }

    }


    class DownloadProductsFromDatabase extends AsyncTask<Void, Integer, List<Product>>{

        @Override
        protected List<Product> doInBackground(Void... params) {
            MenuDataBase menuDataBase = MenuDataBase.getInstance(showProductsInListActivity);
            productsInShoppingList = new ArrayList<>();

            String query = String.format("SELECT productsId, name, storageType, totalQuantity, " +
                    "wasBought FROM %s p JOIN %s slp ON p.productsId = slp.productId WHERE " +
                    "%s = '%s' ORDER BY %s ",
                    ProductsTable.getTableName(), ShoppingListsProductsTable.getTableName(),
                    ShoppingListsProductsTable.getFirstColumnName(),shoppingListId,
                    ProductsTable.getSecondColumnName());

            Cursor cursor = menuDataBase.downloadData(query);
            if (cursor.getCount() > 0){
                cursor.moveToPosition(-1);
                while (cursor.moveToNext()){
                    productsInShoppingList.add(new Product(cursor.getInt(0), cursor.getString(1),
                            cursor.getDouble(3), cursor.getString(2)));
                    productsInShoppingList.get(productsInShoppingList.size()-1)
                            .setBought(cursor.getInt(4) == 1);
                }
            }

            menuDataBase.close();
            return productsInShoppingList;
        }

        @Override
        protected void onPostExecute(List<Product> products) {
            if(products.size() > 0){
                showProductsInListActivity.setProducts(products);
                showProductsInListActivity
                        .makeAStatement("You can change quantity by clicking on it",
                                Toast.LENGTH_LONG);
            }else{
                showProductsInListActivity.shoppingListIsEmpty();
                showProductsInListActivity.setProducts(products);
            }
        }
    }
}
