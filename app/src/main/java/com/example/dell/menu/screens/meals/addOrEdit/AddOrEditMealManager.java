package com.example.dell.menu.screens.meals.addOrEdit;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LongSparseArray;
import android.widget.EditText;

import com.example.dell.menu.MenuDataBase;
import com.example.dell.menu.events.meals.AddProductToIngredientsEvent;
import com.example.dell.menu.events.meals.DeleteProductFromMealEvent;
import com.example.dell.menu.objects.Meal;
import com.example.dell.menu.objects.Product;
import com.example.dell.menu.tables.MealsProductsTable;
import com.example.dell.menu.tables.MealsTable;
import com.example.dell.menu.tables.ProductsTable;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 01.06.2017.
 */

public class AddOrEditMealManager {
    protected AddOrEditMealActivity addOrEditMealActivity;
    private List<Product> listOfProducts;
    private Bus bus;
    protected Long mealId;
    protected Product productToDelete;

    public AddOrEditMealManager(Bus bus){
        this.bus = bus;
        bus.register(this);
        listOfProducts = new ArrayList<>();
    }

    public void onAttach(AddOrEditMealActivity addOrEditMealActivity){
        this.addOrEditMealActivity = addOrEditMealActivity;
    }

    public void onStop(){
        this.addOrEditMealActivity = null;
    }

    public List<Product> getListOfProducts() {
        return listOfProducts;
    }

    public void clearListOfProducts(){
        listOfProducts.clear();
    }

    @Subscribe
    public void onAddToListOfProducts(AddProductToIngredientsEvent event){
        for (Product product : listOfProducts) {
            if(product.getProductId() == event.product.getProductId()){
                product.setQuantity(product.getQuantity()+event.product.getQuantity());
                return;
            }
        }

        listOfProducts.add(event.product);
    }

    @Subscribe
    public void onDeleteFromListOfProducts(DeleteProductFromMealEvent event){
        int indx = -1;
        productToDelete = event.product;
        for (int i = 0; i < listOfProducts.size(); i++) {
            if(listOfProducts.get(i).getProductId() == event.product.getProductId()){
                indx = i;
            }
        }

        if(indx != -1){
            listOfProducts.remove(indx);
            if(addOrEditMealActivity != null){
                addOrEditMealActivity.productDeleteSuccess(productToDelete);
            }
        }else{
            if(addOrEditMealActivity != null){
                addOrEditMealActivity.productDeleteFailed(productToDelete.getName());
            }
        }

    }

    public void addProducts(Long mealId){
        this.mealId = mealId;
        if(addOrEditMealActivity != null){
            new AddProducts().execute(listOfProducts);
        }
    }

    public void addMeal(String mealsName, String kcal, Long userId, String recipe) {
        if(addOrEditMealActivity != null){
            new AddMeal().execute(mealsName, kcal, String.valueOf(userId), recipe);
        }
    }

    class AddProducts extends AsyncTask<List<Product>, Integer, Long>{

        @Override
        protected Long doInBackground(List<Product>... params) {
            MenuDataBase menuDataBase = MenuDataBase.getInstance(addOrEditMealActivity);
            try {
                for (Product product : params[0]) {
                    if(menuDataBase.insert(MealsProductsTable.getTableName(), MealsProductsTable.getContentValues(product.getProductId(), mealId, product.getQuantity())) == -1)
                        return Long.valueOf(-1);
                }
            }catch (Exception e){
                return Long.valueOf(-1);
            }
            return Long.valueOf(0);
        }

        @Override
        protected void onPostExecute(Long aLong) {
            if(aLong == 0){
                addOrEditMealActivity.saveSuccess();
            }else if(aLong == -1){
                addOrEditMealActivity.saveFailed();
            }
        }
    }

    class AddMeal extends AsyncTask<String, Integer, Long>{

        @Override
        protected Long doInBackground(String... params) {
            try {
                MenuDataBase menuDataBase = MenuDataBase.getInstance(addOrEditMealActivity);
                String name = params[0];
                int kcal = Integer.parseInt(params[1]);
                Long usersId = Long.parseLong(params[2]);
                String recipe = params[3];

                ContentValues contentValues = MealsTable.getContentValues(new Meal(name, kcal, usersId, recipe));
                Long indeks = menuDataBase.insert(MealsTable.getTableName(), contentValues);
                return indeks;
            }catch (Exception e){
                return Long.valueOf(-1);
            }

        }

        @Override
        protected void onPostExecute(Long aLong) {
            if(aLong != -1){
                addProducts(aLong);
            }
        }
    }
}
