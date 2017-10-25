package com.example.dell.menu.screens.meals.addOrEdit;

import android.database.Cursor;
import android.os.AsyncTask;

import com.example.dell.menu.MenuDataBase;
import com.example.dell.menu.objects.Product;
import com.example.dell.menu.tables.ProductsTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 01.06.2017.
 */

public class ChooseFromProductsManager {
    protected ChooseFromProductsActivity chooseFromProductsActivity;

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
            String query = String.format("SELECT %s, %s, %s, %s FROM %s WHERE %s LIKE '%%%s%%'" +
                    "ORDER BY %s",
                    ProductsTable.getFirstColumnName(), ProductsTable.getSecondColumnName(),
                    ProductsTable.getFourthColumnName(), ProductsTable.getFifthColumnName(),
                    ProductsTable.getTableName(), ProductsTable.getSecondColumnName(), params[0],
                    ProductsTable.getSecondColumnName());
            Cursor cursor = menuDataBase.downloadData(query);
            if(cursor.getCount() > 0){
                cursor.moveToPosition(-1);
                while (cursor.moveToNext()){
                   result.add(new Product(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3)));
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
