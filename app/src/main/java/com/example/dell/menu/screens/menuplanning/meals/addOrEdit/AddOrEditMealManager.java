package com.example.dell.menu.screens.menuplanning.meals.addOrEdit;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;


import com.example.dell.menu.MealsType;
import com.example.dell.menu.MenuDataBase;
import com.example.dell.menu.events.meals.AddProductToIngredientsEvent;
import com.example.dell.menu.events.meals.DeleteProductFromMealEvent;
import com.example.dell.menu.events.meals.ProductAddedSuccessfullyToIngredientsEvent;
import com.example.dell.menu.objects.menuplanning.Meal;
import com.example.dell.menu.objects.menuplanning.Product;
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
    private static final int HUNDRED_GRAMS = 100;
    private AddOrEditMealActivity addOrEditMealActivity;
    private List<Product> listOfProducts;
    private Bus bus;
    protected Long mealId;
    private Product productToDelete;
    private String stateName;
    private String stateRecipe;
    private double amountOfKcal;
    private double amountOfProteins;
    private double amountOfCarbons;
    private double amountOfFat;

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

    void onStop(){
        this.addOrEditMealActivity = null;
    }

    void setStateRecipe(String stateRecipe) {
        this.stateRecipe = stateRecipe;
    }


    List<Product> getListOfProducts() {
        return listOfProducts;
    }

    void clearListOfProducts(){
        listOfProducts.clear();
    }

    boolean[] getMealsTypesStates() {
        return mealsTypesStates;
    }

    void setMealsTypesStates(boolean[] mealsTypesStates) {
        this.mealsTypesStates = mealsTypesStates;
    }

    double getAmountOfKcal() {
        return amountOfKcal;
    }

    double getAmountOfProteins() {
        return amountOfProteins;
    }


    double getAmountOfCarbons() {
        return amountOfCarbons;
    }


    double getAmountOfFat() {
        return amountOfFat;
    }

    @Subscribe
    public void onAddToListOfProducts(AddProductToIngredientsEvent event){
        boolean found = false;
        double quantity = event.product.getQuantity();
        for (Product product : listOfProducts) {
            if(product.getProductId() == event.product.getProductId()){
                found = true;
                product.setQuantity(product.getQuantity()+ quantity);
                break;
            }
        }

        if(!found) listOfProducts.add(event.product);

        amountOfKcal += event.product.countCalories(quantity);
        amountOfProteins += event.product.countProteins(quantity);
        amountOfCarbons += event.product.countCarbons(quantity);
        amountOfFat += event.product.countFat(quantity);

        bus.post(new ProductAddedSuccessfullyToIngredientsEvent(event.product.getName()));
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
        /*
        if(indx != -1){
            listOfProducts.remove(indx);
            if(addOrEditMealActivity != null){
                addOrEditMealActivity.productDeleteSuccess(productToDelete);
            }
        }else{
            if(addOrEditMealActivity != null){
                addOrEditMealActivity.productDeleteFailed(productToDelete.getName());
            }
        }*/

        if(indx != -1){
            listOfProducts.remove(indx);
            amountOfKcal -= productToDelete.getKcalPer100g_mlOr1Unit();
            amountOfProteins -= productToDelete.getProteinsPer100g_mlOr1Unit();
            amountOfCarbons -= productToDelete.getCarbohydratesPer100g_mlOr1Unit();
            amountOfFat -= productToDelete.getFatPer100g_mlOr1Unit();
        }
        else{
            if(addOrEditMealActivity != null) addOrEditMealActivity.productDeleteFailed(productToDelete.getName());
        }

    }

    private void addProducts(Long mealsId){
        this.mealId = mealsId;

        if(addOrEditMealActivity != null){
            new AsyncTask<List<Product>, Void, Long>(){
                @Override
                protected Long doInBackground(List<Product>... params) {
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(addOrEditMealActivity);
                    try {
                        for (Product product : params[0]) {
                            if(menuDataBase.insert(MealsProductsTable.getTableName(),
                                    MealsProductsTable.getContentValues(product.getProductId(),
                                            mealId, product.getQuantity())) == -1){
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
            }.execute(listOfProducts);
        }
    }

    void addMeal(final String mealsName, final long mealAuthorId, final String mealsRecipe, boolean[] mealsTypesStates) {
        if(addOrEditMealActivity != null){
            this.mealsTypesStates = mealsTypesStates;

            new AsyncTask<Void, Void, Long>(){
                @Override
                protected Long doInBackground(Void... params) {
                    try {
                        MenuDataBase menuDataBase = MenuDataBase.getInstance(addOrEditMealActivity);
                        ContentValues contentValues = MealsTable.getContentValues(new Meal(mealsName,
                                (int)amountOfKcal, Integer.parseInt(String.valueOf(mealAuthorId)), mealsRecipe, (int)amountOfProteins,
                                (int)amountOfCarbons, (int)amountOfFat));
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
            }.execute();
        }
    }

    void loadMealForEdit(long mealId) {
        if(addOrEditMealActivity != null){
            this.mealId = mealId;
            new LoadMeal().execute(mealId);
        }
    }

    void loadMealToShow(long mealId){
        if(addOrEditMealActivity != null){
            this.mealId = mealId;
            new LoadMeal().execute(mealId);
        }
    }

    void downloadProductsInMeal(int mealsId) {
        if(addOrEditMealActivity != null){
            new DownloadProductsInMeal().execute(mealsId);
        }
    }

    void saveState(String name, String recipe) {
        this.stateName = name;
        this.stateRecipe = recipe;
    }

    void resetState(){
        stateName = "";
        stateRecipe = "";
        amountOfKcal = 0;
        amountOfProteins = 0;
        amountOfCarbons = 0;
        amountOfFat = 0;
        listOfProducts.clear();
    }

    String getStateName() {
        return stateName;
    }


    String getStateRecipe() {
        return stateRecipe;
    }

    void setEditMode() {
        editMode = true;
    }

    void resetEditMode(){
        editMode = false;
    }

    boolean isEditMode(){
        return editMode;
    }

    void edit(String name, String recipe, boolean[] mealsTypesStates) {
        if(addOrEditMealActivity != null){
            this.mealsTypesStates = mealsTypesStates;
            new AsyncTask<String, Void, Integer>(){
                @Override
                protected Integer doInBackground(String... params) {
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(addOrEditMealActivity);
                    ContentValues editContentValues = new ContentValues();
                    editContentValues.put(MealsTable.getSecondColumnName(), params[0]);
                    editContentValues.put(MealsTable.getThirdColumnName(), amountOfKcal);
                    editContentValues.put(MealsTable.getFifthColumnName(), params[1]);
                    editContentValues.put(MealsTable.getSixthColumnName(), amountOfProteins);
                    editContentValues.put(MealsTable.getSeventhColumnName(), amountOfCarbons);
                    editContentValues.put(MealsTable.getEighthColumnName(), amountOfFat);
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
            }.execute(name, recipe);
        }
    }

    private void editProductsInMeal() {
        new DeleteProducts().execute(mealId);
    }

    void resetMealsTypesStates() {
        for (int i = 0; i < mealsTypesStates.length; i++) {
            mealsTypesStates[i] = false;
        }
    }

    void setStateName(String stateName) {
        this.stateName = stateName;
    }

    void setShowMode() {
        showMode = true;
    }

    void resetShowMode() {
        showMode = false;
    }

    void resetValues() {
        stateName = "";
        amountOfKcal = 0;

        stateRecipe = "";
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
                    result.add(new Product(cursor.getString(1), cursor.getDouble(3), cursor.getString(2)));
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
                        cursor.getInt(3),cursor.getString(4), cursor.getInt(5), cursor.getInt(6),
                        cursor.getInt(7));
                amountOfKcal = cursor.getInt(2);
                amountOfProteins = cursor.getInt(5);
                amountOfCarbons = cursor.getInt(6);
                amountOfFat = cursor.getInt(7);
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
}
