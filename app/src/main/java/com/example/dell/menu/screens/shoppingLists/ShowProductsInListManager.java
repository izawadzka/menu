package com.example.dell.menu.screens.shoppingLists;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.dell.menu.MenuDataBase;
import com.example.dell.menu.events.shoppingLists.QuantityOfProductChangedEvent;
import com.example.dell.menu.objects.Product;
import com.example.dell.menu.tables.ProductsTable;
import com.example.dell.menu.tables.ShoppingListsMenusTable;
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

    public void loadProducts() {
        if(showProductsInListActivity != null){
            new DownloadProductsFromDatabase().execute();
        }
    }

    public int getShoppingListId() {
        return shoppingListId;
    }

    @Subscribe
    public void onQuantityOfProductChangedEvent(QuantityOfProductChangedEvent event){
        if(showProductsInListActivity != null){
            productToChangeId = event.productId;
            holder = event.holder;
            new UpdateQuantityOfProduct().execute(event.quantity);
        }
    }

    class UpdateQuantityOfProduct extends AsyncTask<Double, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Double... params) {
            MenuDataBase menuDataBase = MenuDataBase.getInstance(showProductsInListActivity);
            boolean result;
            ContentValues editContentValues = new ContentValues();
            editContentValues.put(ShoppingListsProductsTable.getThirdColumnName(), params[0]);

            String[] whereClauseArgs = {String.valueOf(shoppingListId),String.valueOf(productToChangeId)};

            String whereClause = String.format("%s = ? AND %s = ?",
                    ShoppingListsProductsTable.getFirstColumnName(),ShoppingListsProductsTable.getSecondColumnName());
            if(menuDataBase.update(ShoppingListsProductsTable.getTableName(), editContentValues, whereClause,
                    whereClauseArgs) != -1) result = true;
            else  result = false;
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
    }

    public void setShoppingListId(int shoppingListId) {
        this.shoppingListId = shoppingListId;
    }


    class DownloadProductsFromDatabase extends AsyncTask<Void, Integer, List<Product>>{

        private List<Product> productsInShoppingList;

        @Override
        protected List<Product> doInBackground(Void... params) {
            MenuDataBase menuDataBase = MenuDataBase.getInstance(showProductsInListActivity);
            productsInShoppingList = new ArrayList<>();

            String query = String.format("SELECT productsId, name, storageType, totalQuantity FROM %s p JOIN %s slp" +
                    " ON p.productsId = slp.productId WHERE %s = '%s'", ProductsTable.getTableName(),
                    ShoppingListsProductsTable.getTableName(), ShoppingListsProductsTable.getFirstColumnName(),shoppingListId);

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
                showProductsInListActivity.loadingProductsFailed();
            }
        }
    }
}
