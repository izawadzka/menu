package com.example.dell.menu.screens.meals;

import android.database.Cursor;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.dell.menu.MenuDataBase;
import com.example.dell.menu.events.meals.DeleteMealEvent;
import com.example.dell.menu.objects.Meal;
import com.example.dell.menu.tables.MealsProductsTable;
import com.example.dell.menu.tables.MealsTable;
import com.example.dell.menu.tables.UsersTable;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 27.05.2017.
 */

public class MealsFragmentManager {

    protected MealsFragment mealsFragment;
    private Bus bus;
    private int currentMealId;
    protected Meal currentMeal;

    public MealsFragmentManager(Bus bus){
        this.bus = bus;
        bus.register(this);
    }

    public void onAttach(MealsFragment mealsFragment){
        this.mealsFragment = mealsFragment;
    }

    public void onStop(){
        this.mealsFragment = null;
    }


    public void loadMeals() {
        if(mealsFragment != null) {
            new DownloadMeals().execute();
        }
    }

    private void deleteConnectionWithProducts() {
        if(mealsFragment != null){
            new DeleteAllConnectionsWithProducts().execute(currentMealId);
        }
    }

    @Subscribe
    public void onDeleteMeal(DeleteMealEvent event){
        currentMealId = event.meal.getMealsId();
        currentMeal = event.meal;
        if(mealsFragment != null) {
            new DeleteMeal().execute(event.meal.getMealsId());
        }
    }

    public String getAuthorsName(Meal meal) {
        if(mealsFragment != null){
            MenuDataBase menuDataBase = MenuDataBase.getInstance(mealsFragment.getActivity());
        String query = String.format("SELECT %s FROM %s WHERE %s = '%s';", UsersTable.getSecondColumnName(), UsersTable.getTableName(), UsersTable.getFirstColumnName(), meal.getAuthorsId());
        Cursor cursor = menuDataBase.downloadData(query);
        if(cursor.getCount() != 0){
            cursor.moveToPosition(-1);
            cursor.moveToNext();
            return cursor.getString(0);
        }
            menuDataBase.close();
        }
        return "";
    }

    public void findMeals(String textToFind) {
        if(mealsFragment != null){
            new FindMeals().execute(textToFind);
        }
    }

    class FindMeals extends AsyncTask<String, Void, List<Meal>>{

        @Override
        protected List<Meal> doInBackground(String... params) {
            List<Meal> result = new ArrayList<>();
            MenuDataBase menuDataBase = MenuDataBase.getInstance(mealsFragment.getActivity());
            String query = String.format("SELECT * FROM %s WHERE %s LIKE '%%%s%%'", MealsTable.getTableName(), MealsTable.getSecondColumnName(), params[0]);
            Cursor cursor = menuDataBase.downloadData(query);
            if(cursor.getCount() > 0){
                cursor.moveToPosition(-1);
                while (cursor.moveToNext()){
                    result.add(new Meal(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3), cursor.getString(4)));
                }
            }
            menuDataBase.close();
            return result;
        }

        @Override
        protected void onPostExecute(List<Meal> meals) {
            if(meals.size() > 0){
                mealsFragment.showMeals(meals);
            }
        }
    }

    class DeleteMeal extends AsyncTask<Integer, Integer, Integer>{

        @Override
        protected Integer doInBackground(Integer... params) {
            String[] mealId = {String.valueOf(params[0])};
            MenuDataBase menuDataBase = MenuDataBase.getInstance(mealsFragment.getActivity());
            return menuDataBase.delete(MealsTable.getTableName(), String.format("%s = ?", MealsTable.getFirstColumnName()), mealId);
        }

        @Override
        protected void onPostExecute(Integer result) {
            if(result > 0){
                mealsFragment.deleteSuccess(currentMeal);
                deleteConnectionWithProducts();
            }else{
                mealsFragment.deleteFailed();
            }
        }
    }

    class DeleteAllConnectionsWithProducts extends AsyncTask<Integer, Integer, Integer>{

        @Override
        protected Integer doInBackground(Integer... params) {
            String[] mealId = {String.valueOf(params[0])};
            MenuDataBase menuDataBase = MenuDataBase.getInstance(mealsFragment.getActivity());
            return menuDataBase.delete(MealsProductsTable.getTableName(), String.format("%s = ?", MealsProductsTable.getSecondColumnName()), mealId);
        }
    }

    class DownloadMeals extends AsyncTask<Void, Integer, List<Meal>>{

        @Override
        protected List<Meal> doInBackground(Void... params) {
            MenuDataBase menuDataBase = MenuDataBase.getInstance(mealsFragment.getActivity());

            List<Meal> results = new ArrayList<>();
            String query = String.format("SELECT * FROM %s ", MealsTable.getTableName());
            Cursor cursor = menuDataBase.downloadData(query);

            if (cursor.getCount() > 0) {
                int mealsId, authorsId;
                int cumulativeNumberOfKcalPer100g;
                String name, recipe;
                cursor.moveToPosition(-1);

                while (cursor.moveToNext()) {
                    mealsId = cursor.getInt(0);
                    name = cursor.getString(1);
                    cumulativeNumberOfKcalPer100g = cursor.getInt(2);
                    authorsId = cursor.getInt(3);
                    recipe = cursor.getString(4);
                    results.add(new Meal(mealsId, name, cumulativeNumberOfKcalPer100g, authorsId, recipe));
                }
            }

            menuDataBase.close();

            return results;
        }

        @Override
        protected void onPostExecute(List<Meal> meals) {
            mealsFragment.showMeals(meals);
        }
    }
}
