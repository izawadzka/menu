package com.example.dell.menu.screens.meals.addOrEdit;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.example.dell.menu.MealsType;
import com.example.dell.menu.MenuDataBase;
import com.example.dell.menu.events.meals.AddProductToIngredientsEvent;
import com.example.dell.menu.events.meals.DeleteProductFromMealEvent;
import com.example.dell.menu.objects.Meal;
import com.example.dell.menu.objects.Product;
import com.example.dell.menu.tables.MealsProductsTable;
import com.example.dell.menu.tables.MealsTable;
import com.example.dell.menu.tables.MealsTypesMealsTable;
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
    private String stateName;
    private String stateKcal;
    private String stateRecipe;

    private boolean editMode;
    private boolean showMode;

    private boolean[] mealsTypesStates = new boolean[MealsType.AMOUNT_OF_TYPES];
    private Meal loadedMeal;

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

    public void setStateRecipe(String stateRecipe) {
        this.stateRecipe = stateRecipe;
    }

    public void setStateKcal(String stateKcal) {
        this.stateKcal = stateKcal;
    }

    public List<Product> getListOfProducts() {
        return listOfProducts;
    }

    public void clearListOfProducts(){
        listOfProducts.clear();
    }

    public boolean[] getMealsTypesStates() {
        return mealsTypesStates;
    }

    public void setMealsTypesStates(boolean[] mealsTypesStates) {
        this.mealsTypesStates = mealsTypesStates;
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

    public void addMeal(String mealsName, String kcal, Long userId, String recipe, boolean[] mealsTypesStates) {
        if(addOrEditMealActivity != null){
            this.mealsTypesStates = mealsTypesStates;
            new AddMeal().execute(mealsName, kcal, String.valueOf(userId), recipe);
        }
    }

    public void loadMealForEdit(long mealId) {
        if(addOrEditMealActivity != null){
            this.mealId = mealId;
            new LoadMeal().execute(mealId);
        }
    }

    public void loadMealToShow(long mealId){
        if(addOrEditMealActivity != null){
            this.mealId = mealId;
            new LoadMeal().execute(mealId);
        }
    }

    public void downloadProductsInMeal(int mealsId) {
        if(addOrEditMealActivity != null){
            new DownloadProductsInMeal().execute(mealsId);
        }
    }

    public void saveState(String name, String kcal, String recipe) {
        this.stateName = name;
        this.stateKcal = kcal;
        this.stateRecipe = recipe;
    }

    public void resetState(){
        saveState("", "", "");
    }

    public String getStateName() {
        return stateName;
    }

    public String getStateKcal() {
        return stateKcal;
    }

    public String getStateRecipe() {
        return stateRecipe;
    }

    public void setEditMode() {
        editMode = true;
    }

    public void resetEditMode(){
        editMode = false;
    }

    public boolean isEditMode(){
        return editMode;
    }

    public void edit(String name, String kcal, String recipe, boolean[] mealsTypesStates) {
        if(addOrEditMealActivity != null){
            this.mealsTypesStates = mealsTypesStates;
            new EditMeal().execute(name, kcal, recipe);
        }
    }

    private void editProductsInMeal() {
        new DeleteProducts().execute(mealId);
    }

    public void resetMealsTypesStates() {
        for (int i = 0; i < mealsTypesStates.length; i++) {
            mealsTypesStates[i] = false;
        }
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public void setShowMode() {
        showMode = true;
    }

    public void resetShowMode() {
        showMode = false;
    }

    private class DeleteProducts extends AsyncTask<Long, Integer, Integer>{

        @Override
        protected Integer doInBackground(Long... params) {
            int numberOfDeletedRows;
            MenuDataBase menuDataBase = MenuDataBase.getInstance(addOrEditMealActivity);
            String[] mealId = {String.valueOf(params[0])};
            numberOfDeletedRows = menuDataBase.delete(MealsProductsTable.getTableName(),
                    String.format("%s = ?", MealsProductsTable.getSecondColumnName()), mealId);
            menuDataBase.close();
            return numberOfDeletedRows;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if(result > 0) addProducts(mealId);
            else if(addOrEditMealActivity != null) addOrEditMealActivity.updateFailed();
        }
    }

    private class EditMeal extends AsyncTask<String, Integer, Integer>{

        @Override
        protected Integer doInBackground(String... params) {
            MenuDataBase menuDataBase = MenuDataBase.getInstance(addOrEditMealActivity);
            ContentValues editContentValues = new ContentValues();
            editContentValues.put(MealsTable.getSecondColumnName(), params[0]);
            editContentValues.put(MealsTable.getThirdColumnName(), params[1]);
            editContentValues.put(MealsTable.getFifthColumnName(), params[2]);
            String[] mealsId = {String.valueOf(mealId)};
            String whereClause = String.format("%s = ?", MealsTable.getFirstColumnName());
            int result = menuDataBase.update(MealsTable.getTableName(), editContentValues, whereClause, mealsId);
            menuDataBase.close();
            return result;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if(integer == 1){
                editProductsInMeal();
            }else if(integer == 0){
                addOrEditMealActivity.updateFailed();
            }
        }
    }


    private class DownloadProductsInMeal extends AsyncTask<Integer, Integer, List<Product>>{

        @Override
        protected List<Product> doInBackground(Integer... params) {
            MenuDataBase menuDataBase = MenuDataBase.getInstance(addOrEditMealActivity);
            List<Product> result = new ArrayList<>();
            String query = String.format("SELECT %s, %s, %s, %s FROM %s p JOIN %s mp\n" +
                            "ON mp.%s = p.%s WHERE mp.%s = '%s';",
                    ProductsTable.getFirstColumnName(),
                    ProductsTable.getSecondColumnName(),
                    ProductsTable.getFifthColumnName(),
                    MealsProductsTable.getThirdColumnName(),
                    ProductsTable.getTableName(),
                    MealsProductsTable.getTableName(),
                    MealsProductsTable.getFirstColumnName(),
                    ProductsTable.getFirstColumnName(),
                    MealsProductsTable.getSecondColumnName(),
                    params[0]);
            Cursor cursor = menuDataBase.downloadData(query);
            if(cursor.getCount() > 0){
                cursor.moveToPosition(-1);
                while (cursor.moveToNext()){
                    result.add(new Product(cursor.getString(1), cursor.getInt(3), cursor.getString(2)));
                    result.get(result.size()-1).setProductId(cursor.getInt(0));
                }
            }
            menuDataBase.close();
            return result;
        }

        @Override
        protected void onPostExecute(List<Product> products) {
            listOfProducts.clear();
            listOfProducts.addAll(products);
            Log.d(addOrEditMealActivity.getPackageName(), String.format(" size %s", listOfProducts.size()));
            addOrEditMealActivity.setProducts();
        }
    }

    private class LoadMeal extends AsyncTask<Long, Integer, Meal>{

        @Override
        protected Meal doInBackground(Long... params) {
            MenuDataBase menuDataBase = MenuDataBase.getInstance(addOrEditMealActivity);
            String query = String.format("SELECT * FROM %s WHERE %s = '%s'",
                    MealsTable.getTableName(), MealsTable.getFirstColumnName(), params[0]);
            Cursor cursor = menuDataBase.downloadData(query);
            if(cursor.getCount() == 1){
                cursor.moveToPosition(0);
                Meal meal = new Meal(cursor.getInt(0), cursor.getString(1), cursor.getInt(2),
                        cursor.getInt(3),cursor.getString(4));
                menuDataBase.close();
                //odkomentowalam zamkniecie bazy
                return meal;
            }else{
                menuDataBase.close();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Meal meal) {
            if(meal != null){
                loadedMeal = meal;
                checkMealTypes(meal.getMealsId());
            }else{
                loadedMeal = null;
                addOrEditMealActivity.loadingMealFailed();
            }
        }
    }

    private void checkMealTypes(int mealId) {
        if(addOrEditMealActivity != null){
            new CheckMealTypes().execute(mealId);
        }
    }

    private class CheckMealTypes extends AsyncTask<Integer, Void, Boolean[]>{

        @Override
        protected Boolean[] doInBackground(Integer... params) {
            Boolean[] result = new Boolean[MealsType.AMOUNT_OF_TYPES];

            for (int i = 0; i < result.length; i++) {
                result[i] = false;
            }

            MenuDataBase menuDataBase = MenuDataBase.getInstance(addOrEditMealActivity);

            String query = String.format("SELECT %s FROM %s WHERE %s = '%s'",
                    MealsTypesMealsTable.getFirstColumnName(), MealsTypesMealsTable.getTableName(),
                    MealsTypesMealsTable.getSecondColumnName(), params[0]);
            Cursor cursor = menuDataBase.downloadData(query);
            if(cursor.getCount() > 0){
                cursor.moveToPosition(-1);
                while (cursor.moveToNext()){
                    result[cursor.getInt(0)-1] = true;
                }
            }else result = null;

            menuDataBase.close();


            return result;
        }

        @Override
        protected void onPostExecute(Boolean[] result) {
            if(addOrEditMealActivity != null) {
                if (result != null) {
                    boolean[] convertedResult = new boolean[MealsType.AMOUNT_OF_TYPES];
                    for (int i = 0; i < convertedResult.length; i++){
                        convertedResult[i] = result[i];
                    }
                    addOrEditMealActivity.loadingMealSuccess(loadedMeal, convertedResult);
                } else addOrEditMealActivity.loadingMealFailed();
            }
        }
    }

    private class AddProducts extends AsyncTask<List<Product>, Integer, Long>{

        @Override
        protected Long doInBackground(List<Product>... params) {
            MenuDataBase menuDataBase = MenuDataBase.getInstance(addOrEditMealActivity);
            try {
                for (Product product : params[0]) {
                    if(menuDataBase.insert(MealsProductsTable.getTableName(), MealsProductsTable.getContentValues(product.getProductId(), mealId, product.getQuantity())) == -1){
                        menuDataBase.close();
                        return (long)-1;
                    }
                }
            }catch (Exception e){
                menuDataBase.close();
                return (long)-1;
            }
            menuDataBase.close();
            return (long)0;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            if(aLong == 0){
                if(editMode){
                    //addOrEditMealActivity.updateSuccess();
                    resetMealTypes();
                }else{
                    setMealTypes();
                }
            }else if(aLong == -1){
                if(editMode){
                    addOrEditMealActivity.updateFailed();
                }else{
                    addOrEditMealActivity.saveFailed();
                }
            }
        }
    }

    private void resetMealTypes() {
        if(addOrEditMealActivity != null){
            new ResetMealTypes().execute();
        }
    }
    private class ResetMealTypes extends AsyncTask<Void, Void, Integer>{

        @Override
        protected Integer doInBackground(Void... params) {
            int numberOfDeletedRows;
            MenuDataBase menuDataBase = MenuDataBase.getInstance(addOrEditMealActivity);
            String[] mealsId = {String.valueOf(mealId)};
            numberOfDeletedRows =  menuDataBase.delete(MealsTypesMealsTable.getTableName(),
                    String.format("%s = ?", MealsTypesMealsTable.getSecondColumnName()), mealsId);
            menuDataBase.close();
            return numberOfDeletedRows;
        }

        @Override
        protected void onPostExecute(Integer numberOfDeletedRows) {
            if(numberOfDeletedRows > 0) setMealTypes();
            else if(addOrEditMealActivity != null) addOrEditMealActivity.updateFailed();
        }
    }

    private void setMealTypes() {
        if(addOrEditMealActivity != null) new SetMealTypes().execute();
    }

    private class SetMealTypes extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean result = true;

            MenuDataBase menuDataBase = MenuDataBase.getInstance(addOrEditMealActivity);
            for (int i = 0; i < mealsTypesStates.length;i++) {
                if(mealsTypesStates[i]){
                    if(menuDataBase.insert(MealsTypesMealsTable.getTableName(), MealsTypesMealsTable.getContentValues(i+1,mealId)) == -1){
                        result = false;
                        break;
                    }
                }
            }

            menuDataBase.close();
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (addOrEditMealActivity != null){
                if(result) {
                    if(editMode) addOrEditMealActivity.updateSuccess();
                    else addOrEditMealActivity.saveSuccess();
                }else {
                    if(editMode) addOrEditMealActivity.updateFailed();
                    else addOrEditMealActivity.saveFailed();
                }
            }
        }
    }

    private class AddMeal extends AsyncTask<String, Integer, Long>{

        @Override
        protected Long doInBackground(String... params) {
            try {
                MenuDataBase menuDataBase = MenuDataBase.getInstance(addOrEditMealActivity);
                String name = params[0];
                int kcal = Integer.parseInt(params[1]);
                int usersId = Integer.parseInt(params[2]);
                String recipe = params[3];

                ContentValues contentValues = MealsTable.getContentValues(new Meal(name, kcal, usersId, recipe));
                long index = menuDataBase.insert(MealsTable.getTableName(), contentValues);
                menuDataBase.close();
                return index;
            }catch (Exception e){
                return (long)-1;
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
