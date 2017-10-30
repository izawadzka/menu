package com.example.dell.menu.screens.virtualfridge;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.dell.menu.MenuDataBase;
import com.example.dell.menu.events.virtualfridge.AddProductToFridgeClickedEvent;
import com.example.dell.menu.objects.menuplanning.Product;
import com.example.dell.menu.tables.ProductsTable;
import com.example.dell.menu.tables.VirtualFridgeTable;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 29.10.2017.
 */

public class AddProductToFridgeManager {
    private AddProductToFridgeActivity addProductToFridgeActivity;
    private final Bus bus;
    private List<Product> products = new ArrayList<>();

    public AddProductToFridgeManager(Bus bus) {
        this.bus = bus;
        bus.register(this);
    }

    public void onAttach(AddProductToFridgeActivity addProductToFridgeActivity){
        this.addProductToFridgeActivity = addProductToFridgeActivity;
    }

    public void onStop(){
        this.addProductToFridgeActivity = null;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void loadProducts(){
        if(addProductToFridgeActivity != null){
            new AsyncTask<Void, Void, Boolean>(){

                @Override
                protected Boolean doInBackground(Void... params) {
                    boolean result;
                    products.clear();
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(addProductToFridgeActivity);
                    String query = String.format("SELECT %s, %s, %s FROM %s WHERE NOT %s IN " +
                            "(SELECT %s FROM %s) ORDER BY %s",
                            ProductsTable.getFirstColumnName(),
                            ProductsTable.getSecondColumnName(),
                            ProductsTable.getFifthColumnName(),
                            ProductsTable.getTableName(),
                            ProductsTable.getFirstColumnName(),
                            VirtualFridgeTable.getFirstColumnName(),
                            VirtualFridgeTable.getTableName(),
                            ProductsTable.getSecondColumnName());

                    Cursor cursor = menuDataBase.downloadData(query);
                    if(cursor.getCount() > 0){
                        cursor.moveToPosition(-1);
                        while (cursor.moveToNext()){
                            products.add(new Product(cursor.getInt(0), cursor.getString(1),
                                    cursor.getString(2)));
                        }
                        result = true;
                    }else result = false;

                    menuDataBase.close();
                    return result;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    if(addProductToFridgeActivity != null){
                        if(result) addProductToFridgeActivity.showProducts();
                        else addProductToFridgeActivity.loadingFailed();
                    }
                }
            }.execute();
        }
    }

    @Subscribe
    public void onAddProductToFridgeClickedEvent(final AddProductToFridgeClickedEvent event){
        if(addProductToFridgeActivity != null){
            new AsyncTask<Void, Void, Boolean>(){

                @Override
                protected Boolean doInBackground(Void... params) {
                    boolean result = true;
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(addProductToFridgeActivity);

                    result = menuDataBase.insert(VirtualFridgeTable.getTableName(),
                            VirtualFridgeTable.getContentValues(event.productId, event.quantity)) != -1;
                    menuDataBase.close();
                    return result;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    if(addProductToFridgeActivity != null){
                        if (result) addProductToFridgeActivity
                                .makeAStatement(String.format("%s successfully added", event.name),
                                        Toast.LENGTH_SHORT);
                        else addProductToFridgeActivity
                                .makeAStatement(String.format("Adding %s failed", event.name),
                                        Toast.LENGTH_LONG);
                    }
                }
            }.execute();
        }
    }
}
