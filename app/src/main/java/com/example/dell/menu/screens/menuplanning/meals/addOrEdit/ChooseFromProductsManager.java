package com.example.dell.menu.screens.menuplanning.meals.addOrEdit;

import android.database.Cursor;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.dell.menu.MenuDataBase;
import com.example.dell.menu.events.meals.ProductAddedSuccessfullyToIngredientsEvent;
import com.example.dell.menu.events.meals.QuantityWasntTypedEvent;
import com.example.dell.menu.objects.menuplanning.Product;
import com.example.dell.menu.tables.ProductsTable;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 01.06.2017.
 */

public class ChooseFromProductsManager {
    protected ChooseFromProductsActivity chooseFromProductsActivity;
    private final Bus bus;
    private List<Product> products;

    public ChooseFromProductsManager(Bus bus){
        this.bus = bus;
        bus.register(this);
    }

    @Subscribe
    public void onQuantityWasntTyped(QuantityWasntTypedEvent quantityWasntTypedEvent){
        if(chooseFromProductsActivity != null) chooseFromProductsActivity.makeAStatement("You must type quantity of product", Toast.LENGTH_LONG);
    }

    @Subscribe
    public void onProductAddedSuccessfully(ProductAddedSuccessfullyToIngredientsEvent event){
        if(chooseFromProductsActivity != null){
            chooseFromProductsActivity.makeAStatement(String.format("%s added successfully",
                    event.productName), Toast.LENGTH_SHORT);
            chooseFromProductsActivity.showProducts(products);
        }
    }

    public void onAttach(ChooseFromProductsActivity chooseFromProductsActivity){
        this.chooseFromProductsActivity = chooseFromProductsActivity;
    }

    public void onStop(){
        this.chooseFromProductsActivity = null;
    }

    public void searchProducts(String query){
        if(chooseFromProductsActivity != null){
            new SearchProducts().execute(query);
        }
    }

    public void loadProducts() {
        if(chooseFromProductsActivity != null){
            new LoadProducts().execute();
        }
    }

    class LoadProducts extends AsyncTask<Void, Integer, List<Product>>{

        @Override
        protected List<Product> doInBackground(Void... params) {
            List<Product> result = new ArrayList<>();
            MenuDataBase menuDataBase = MenuDataBase.getInstance(chooseFromProductsActivity);
            String query = String.format("SELECT * FROM %s ORDER BY %s", ProductsTable.getTableName(),
                    ProductsTable.getSecondColumnName());
            Cursor cursor = menuDataBase.downloadData(query);
            if(cursor.getCount() > 0){
                cursor.moveToPosition(-1);
                while (cursor.moveToNext()){
                    result.add(new Product(cursor.getInt(0), cursor.getString(1), cursor.getInt(2),
                            cursor.getString(3), cursor.getString(4), cursor.getInt(5),
                            cursor.getInt(6), cursor.getInt(7)));
                }
                products = result;
            }
            return result;
        }

        @Override
        protected void onPostExecute(List<Product> products) {
            if(products.size() > 0){
                chooseFromProductsActivity.showProducts(products);
            }
        }
    }

    class SearchProducts extends AsyncTask<String, Integer, List<Product>>{

        @Override
        protected List<Product> doInBackground(String... params) {
            List<Product> result = new ArrayList<>();
            MenuDataBase menuDataBase = MenuDataBase.getInstance(chooseFromProductsActivity);
            String query = String.format("SELECT * FROM %s WHERE %s LIKE '%%%s%%'" +
                    "ORDER BY %s",
                    ProductsTable.getTableName(), ProductsTable.getSecondColumnName(), params[0],
                    ProductsTable.getSecondColumnName());
            Cursor cursor = menuDataBase.downloadData(query);
            if(cursor.getCount() > 0){
                cursor.moveToPosition(-1);
                while (cursor.moveToNext()) {
                    result.add(new Product(cursor.getInt(0), cursor.getString(1), cursor.getInt(2),
                            cursor.getString(3), cursor.getString(4), cursor.getInt(5),
                            cursor.getInt(6), cursor.getInt(7)));
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(List<Product> products) {
            if(products.size() > 0){
                chooseFromProductsActivity.showProducts(products);
            }
        }
    }
}
