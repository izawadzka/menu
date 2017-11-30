package com.example.dell.menu.menuplanning.screens.products;

import android.database.Cursor;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.dell.menu.data.MenuDataBase;
import com.example.dell.menu.menuplanning.events.products.DeleteProductAnywayEvent;
import com.example.dell.menu.menuplanning.events.products.DeleteProductEvent;
import com.example.dell.menu.menuplanning.events.products.SetProductsAdapterEvent;
import com.example.dell.menu.menuplanning.events.products.UpdateProductEvent;
import com.example.dell.menu.menuplanning.objects.Product;
import com.example.dell.menu.menuplanning.screens.products.dialog.Dialog;
import com.example.dell.menu.data.tables.MealsProductsTable;
import com.example.dell.menu.data.tables.MealsTable;
import com.example.dell.menu.data.tables.ProductsTable;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 26.05.2017.
 */

public class ProductFragmentManager {
    private ProductsFragment productsFragment;
    private int idOfProductToDelete;
    private List<Product> productList = new ArrayList<>();

    public ProductFragmentManager(Bus bus) {
        bus.register(this);
    }

    public void onAttach(ProductsFragment productsFragment){
        this.productsFragment = productsFragment;
    }

    public void onStop(){
        productsFragment = null;
    }

    void loadProducts(){

        if(productsFragment != null) {
            new AsyncTask<Void, Void, Integer>(){

                @Override
                protected Integer doInBackground(Void... params) {
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(productsFragment.getActivity());
                    int result = 1;
                    productList.clear();
                    String query = String.format("SELECT %s, %s, %s, %s, %s, %s, %s FROM %s ORDER BY %s",
                            ProductsTable.getFirstColumnName(),
                            ProductsTable.getSecondColumnName(), ProductsTable.getThirdColumnName(),
                            ProductsTable.getSixthColumnName(), ProductsTable.getSeventhColumnName(),
                            ProductsTable.getEighthColumnName(), ProductsTable.getFifthColumnName(),
                            ProductsTable.getTableName(), ProductsTable.getSecondColumnName());
                    Cursor cursor = menuDataBase.downloadData(query);

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
                            productList.add(new Product(productsId, name, numberOfKcalPer100g,
                                    amountOfProteins, amountOfCarbos, amountOfFat));
                            productList.get(productList.size()-1).setStorageType(cursor.getString(6));
                        }
                    }else result = 0;
                    menuDataBase.close();
                    return result;
                }

                @Override
                protected void onPostExecute(Integer results) {
                    if(productsFragment != null) {
                        productsFragment.showProducts(productList);
                        if(results == 0)
                            productsFragment
                                    .makeAStatement("There's no products in database",
                                            Toast.LENGTH_SHORT);
                    }
                }
            }.execute();
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
    public void deleteProductAnyway(DeleteProductAnywayEvent deleteProductAnywayEvent){
        if(productsFragment != null){
            new DeleteProduct().execute();
        }
    }

    @Subscribe
    public void onSetProductsAdapterEvent(SetProductsAdapterEvent event){
        if(productsFragment != null){
            productsFragment.showProducts(productList);
        }
    }

    void findProducts(final String textToFind) {
        if(productsFragment != null){
            new AsyncTask<Void, Void, Void>(){

                @Override
                protected Void doInBackground(Void... params) {
                    productList.clear();
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(productsFragment.getActivity());
                    String query = String.format("SELECT * FROM %s WHERE %s LIKE '%%%s%%' ORDER BY %s",
                            ProductsTable.getTableName(), ProductsTable.getSecondColumnName(), textToFind,
                            ProductsTable.getSecondColumnName());
                    Cursor cursor = menuDataBase.downloadData(query);
                    if(cursor.getCount() > 0){
                        cursor.moveToPosition(-1);
                        while (cursor.moveToNext()){
                            productList.add(new Product(cursor.getInt(0), cursor.getString(1), cursor.getInt(2),
                                    cursor.getString(3), cursor.getString(4), cursor.getInt(5),
                                    cursor.getInt(6), cursor.getInt(7)));
                        }
                    }
                    menuDataBase.close();
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    if(productsFragment != null){
                        productsFragment.showProducts(productList);
                    }
                }
            }.execute();
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
