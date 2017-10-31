package com.example.dell.menu.screens.virtualfridge;

import android.database.Cursor;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.dell.menu.MenuDataBase;
import com.example.dell.menu.events.AddProductClickedEvent;
import com.example.dell.menu.objects.menuplanning.Product;
import com.example.dell.menu.tables.ProductsTable;
import com.example.dell.menu.tables.ShoppingListsProductsTable;
import com.example.dell.menu.tables.VirtualFridgeTable;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 29.10.2017.
 */

public class AddProductManager {
    private AddProductActivity addProductActivity;
    private final Bus bus;
    private List<Product> products = new ArrayList<>();
    private boolean shopping_list_mode = false;
    private boolean fridge_mode = false;
    private int shoppingListId;

    public AddProductManager(Bus bus) {
        this.bus = bus;
        bus.register(this);
    }

    public void onAttach(AddProductActivity addProductActivity){
        this.addProductActivity = addProductActivity;
    }

    public void onStop(){
        this.addProductActivity = null;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void loadProducts(){
        if(addProductActivity != null){
            new AsyncTask<Void, Void, Boolean>(){

                @Override
                protected Boolean doInBackground(Void... params) {
                    boolean result;
                    products.clear();
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(addProductActivity);
                    String query;
                    if(fridge_mode)
                    query = String.format("SELECT %s, %s, %s FROM %s WHERE NOT %s IN " +
                            "(SELECT %s FROM %s) ORDER BY %s",
                            ProductsTable.getFirstColumnName(),
                            ProductsTable.getSecondColumnName(),
                            ProductsTable.getFifthColumnName(),
                            ProductsTable.getTableName(),
                            ProductsTable.getFirstColumnName(),
                            VirtualFridgeTable.getFirstColumnName(),
                            VirtualFridgeTable.getTableName(),
                            ProductsTable.getSecondColumnName());
                    else if(shopping_list_mode) query = String.format("SELECT %s, %s, %s FROM %s ORDER BY %s",
                            ProductsTable.getFirstColumnName(),
                            ProductsTable.getSecondColumnName(),
                            ProductsTable.getFifthColumnName(),
                            ProductsTable.getTableName(),
                            ProductsTable.getSecondColumnName());
                    else query = "";

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
                    if(addProductActivity != null){
                        if(result) addProductActivity.showProducts();
                        else addProductActivity.loadingFailed();
                    }
                }
            }.execute();
        }
    }

    @Subscribe
    public void onAddProductClickedEvent(final AddProductClickedEvent event){
        if(addProductActivity != null){
            new AsyncTask<Void, Void, Boolean>(){

                @Override
                protected Boolean doInBackground(Void... params) {
                    boolean result = true;
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(addProductActivity);

                    if(fridge_mode)
                    result = menuDataBase.insert(VirtualFridgeTable.getTableName(),
                            VirtualFridgeTable.getContentValues(event.productId, event.quantity)) != -1;

                    else if(shopping_list_mode)
                        result = menuDataBase.insert(ShoppingListsProductsTable.getTableName(),
                                ShoppingListsProductsTable.getContentValues(shoppingListId,
                                        event.productId, event.quantity)) != -1;
                    else return false;
                    menuDataBase.close();
                    return result;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    if(addProductActivity != null){
                        if (result){
                            addProductActivity
                                .makeAStatement(String.format("%s successfully added", event.name),
                                        Toast.LENGTH_SHORT);
                            loadProducts();
                        }
                        else addProductActivity
                                .makeAStatement(String.format("Adding %s failed", event.name),
                                        Toast.LENGTH_LONG);
                    }
                }
            }.execute();
        }
    }


    public void setShoppingListMode(boolean shopping_list_mode, int shoppingListId) {
        this.shopping_list_mode = shopping_list_mode;
        this.shoppingListId = shoppingListId;
    }

    public void setFridgeMode(boolean fridge_mode) {
        this.fridge_mode = fridge_mode;
    }
}
