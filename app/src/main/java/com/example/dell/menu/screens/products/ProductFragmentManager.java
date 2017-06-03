package com.example.dell.menu.screens.products;

import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.example.dell.menu.MenuDataBase;
import com.example.dell.menu.events.products.DeleteProductAnywayEvent;
import com.example.dell.menu.events.products.DeleteProductEvent;
import com.example.dell.menu.objects.Product;
import com.example.dell.menu.screens.products.dialog.Dialog;
import com.example.dell.menu.tables.MealsProductsTable;
import com.example.dell.menu.tables.MealsTable;
import com.example.dell.menu.tables.ProductsTable;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 26.05.2017.
 */

public class ProductFragmentManager {
    //private final MenuDataBase menuDataBase;
    protected ProductsFragment productsFragment;
    private final Bus bus;
    private int idOfProductToDelete;

    public ProductFragmentManager(Bus bus) {
        this.bus = bus;
        bus.register(this);
    }

    //public ProductFragmentManager(MenuDataBase menuDataBase){
        //this.menuDataBase = menuDataBase;
    //}

    public void onAttach(ProductsFragment productsFragment){
        this.productsFragment = productsFragment;
    }

    public void onStop(){
        productsFragment = null;
    }

    public void loadProducts(){

        if(productsFragment != null) {
            new LoadProducts().execute();
        }
        //// TODO: 27.05.2017  else
    }

    @Subscribe
    public void onDeleteProduct(DeleteProductEvent deleteProductEvent){
        idOfProductToDelete = deleteProductEvent.getProductId();
        if(productsFragment != null) {
            Dialog allerDialog = new Dialog();
            allerDialog.show(productsFragment.getActivity().getSupportFragmentManager(), "alert_dialog_tag");
        }
    }

    @Subscribe
    public void deleteProduct(DeleteProductAnywayEvent deleteProductAnywayEvent){
        if(productsFragment != null){
            new DeleteProduct().execute();
        }
    }


    class LoadProducts extends AsyncTask<Void, Void, List<Product>>{

        @Override
        protected List<Product> doInBackground(Void... params) {
            MenuDataBase menuDataBase = MenuDataBase.getInstance(productsFragment.getActivity());

            String query = String.format("SELECT * FROM %s ", ProductsTable.getTableName());
            Cursor cursor = menuDataBase.downloadDatas(query);
            List<Product> results =  new ArrayList<>();

            if (cursor.getCount() > 0) {
                int productsId, numberOfKcalPer100g;
                String name, type, storageType;
                cursor.moveToPosition(-1);
                while (cursor.moveToNext()) {
                    productsId = cursor.getInt(0);
                    name = cursor.getString(1);
                    numberOfKcalPer100g = cursor.getInt(2);
                    type = cursor.getString(3);
                    storageType = cursor.getString(4);
                    results.add(new Product(productsId, name, numberOfKcalPer100g, type, storageType));
                }
            }
            menuDataBase.close();
            return results;
        }

        @Override
        protected void onPostExecute(List<Product> results) {
            if(productsFragment != null) {
                productsFragment.showProducts(results);
            }
        }
    }

    class DeleteProduct extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            String[] productId = {String.valueOf(idOfProductToDelete)};
            MenuDataBase menuDataBase = MenuDataBase.getInstance(productsFragment.getActivity());

            menuDataBase.delete(ProductsTable.getTableName(),
                    String.format("%s = ?", ProductsTable.getFirstColumnName()), productId);

            String query = String.format("SELECT %s FROM %s WHERE %s = '%s'",
                    MealsProductsTable.getSecondColumnName(), MealsProductsTable.getTableName(),
                    MealsProductsTable.getFirstColumnName(), idOfProductToDelete);
            Cursor cursor = menuDataBase.downloadDatas(query);

            if(cursor.getCount()>0){
                cursor.moveToPosition(-1);
                while (cursor.moveToNext()){
                    String[] mealsId = {String.valueOf(cursor.getInt(0))};
                    menuDataBase.delete(MealsProductsTable.getTableName(), String.format("%s = ?", MealsProductsTable.getSecondColumnName()), mealsId);
                    menuDataBase.delete(MealsTable.getTableName(), String.format("%s = ?", MealsTable.getFirstColumnName()),  mealsId);
                }

                menuDataBase.close();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            productsFragment.deleteSuccess();
        }
    }
}
