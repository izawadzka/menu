package com.example.dell.menu.screens.products;

import android.database.Cursor;
import android.os.AsyncTask;

import com.example.dell.menu.MenuDataBase;
import com.example.dell.menu.events.products.DeleteProductAnywayEvent;
import com.example.dell.menu.events.products.DeleteProductEvent;
import com.example.dell.menu.events.products.UpdateProductEvent;
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
    protected ProductsFragment productsFragment;
    private final Bus bus;
    private int idOfProductToDelete;

    public ProductFragmentManager(Bus bus) {
        this.bus = bus;
        bus.register(this);
    }

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
    }

    @Subscribe
    public void onDeleteProduct(DeleteProductEvent deleteProductEvent){
        idOfProductToDelete = deleteProductEvent.getProductId();
        if(productsFragment != null) {
            Dialog allertDialog = new Dialog();
            allertDialog.show(productsFragment.getActivity().getSupportFragmentManager(), "alert_dialog_tag");
        }
    }



    @Subscribe
    public void onUpdateProductEvent(UpdateProductEvent event){
        if(productsFragment != null){
            productsFragment.editProduct(event.productId);
        }
    }

    @Subscribe
    public void deleteProductAnyway(DeleteProductAnywayEvent deleteProductAnywayEvent){
        if(productsFragment != null){
            new DeleteProduct().execute();
        }
    }

    public void findProducts(String textToFind) {
        if(productsFragment != null){
            new FindProducts().execute(textToFind);
        }
    }

    class FindProducts extends AsyncTask<String, Void, List<Product>>{

        @Override
        protected List<Product> doInBackground(String... params) {
            List<Product> result = new ArrayList<>();
            MenuDataBase menuDataBase = MenuDataBase.getInstance(productsFragment.getActivity());
            String query = String.format("SELECT * FROM %s WHERE %s LIKE '%%%s%%' ORDER BY %s",
                    ProductsTable.getTableName(), ProductsTable.getSecondColumnName(), params[0],
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
            menuDataBase.close();
            return result;
        }

        @Override
        protected void onPostExecute(List<Product> products) {
            if(products.size() > 0){
                productsFragment.showProducts(products);
            }
        }
    }


    class LoadProducts extends AsyncTask<Void, Void, List<Product>>{

        @Override
        protected List<Product> doInBackground(Void... params) {
            MenuDataBase menuDataBase = MenuDataBase.getInstance(productsFragment.getActivity());

            String query = String.format("SELECT %s, %s, %s, %s, %s, %s FROM %s ORDER BY %s",
                    ProductsTable.getFirstColumnName(),
                    ProductsTable.getSecondColumnName(), ProductsTable.getThirdColumnName(),
                    ProductsTable.getSixthColumnName(), ProductsTable.getSeventhColumnName(),
                    ProductsTable.getEighthColumnName(), ProductsTable.getTableName(),
                    ProductsTable.getSecondColumnName());
            Cursor cursor = menuDataBase.downloadData(query);
            List<Product> results =  new ArrayList<>();

            if (cursor.getCount() > 0) {
                int productsId, numberOfKcalPer100g, amountOfProteins, amountOfCarbos, amountOfFat;
                String name;
                cursor.moveToPosition(-1);
                while (cursor.moveToNext()) {
                    productsId = cursor.getInt(0);
                    name = cursor.getString(1);
                    numberOfKcalPer100g = cursor.getInt(2);
                    amountOfProteins = cursor.getInt(3);
                    amountOfCarbos = cursor.getInt(4);
                    amountOfFat = cursor.getInt(5);
                    results.add(new Product(productsId, name, numberOfKcalPer100g,
                            amountOfProteins, amountOfCarbos, amountOfFat));
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
            Cursor cursor = menuDataBase.downloadData(query);

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
