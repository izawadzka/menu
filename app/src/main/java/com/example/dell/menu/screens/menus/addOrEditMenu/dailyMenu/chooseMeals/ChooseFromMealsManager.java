package com.example.dell.menu.screens.menus.addOrEditMenu.dailyMenu.chooseMeals;

import android.database.Cursor;
import android.os.AsyncTask;

import com.example.dell.menu.MenuDataBase;
import com.example.dell.menu.events.menus.MealAddedEvent;
import com.example.dell.menu.objects.Meal;
import com.example.dell.menu.screens.menus.addOrEditMenu.dailyMenu.chooseMeals.ChooseFromMealsActivity;
import com.example.dell.menu.tables.MealsTable;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 05.06.2017.
 */

public class ChooseFromMealsManager {
    private ChooseFromMealsActivity chooseFromMealsActivity;
    private final Bus bus;

    public ChooseFromMealsManager(Bus bus) {
        this.bus = bus;
        bus.register(this);
    }

    public void onAttach(ChooseFromMealsActivity chooseFromMealsActivity){
        this.chooseFromMealsActivity = chooseFromMealsActivity;
    }

    public void onStop(){
        this.chooseFromMealsActivity = null;
    }

    public void searchMeals(String newText) {
        if(chooseFromMealsActivity != null){
            new SearchMeals().execute(newText);
        }
    }

    @Subscribe
    public void mealAdded(MealAddedEvent event){
        if(chooseFromMealsActivity != null){
            chooseFromMealsActivity.showMealWasAdded(event.mealName);
        }
    }

    public void loadMeals() {
        new LoadMeals().execute();
    }

    class LoadMeals extends AsyncTask<Void, Integer, List<Meal>>{

        @Override
        protected List<Meal> doInBackground(Void... params) {
            List<Meal> result = new ArrayList<>();
            MenuDataBase menuDataBase = MenuDataBase.getInstance(chooseFromMealsActivity);
            String query = String.format("SELECT * FROM %s", MealsTable.getTableName());
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
                chooseFromMealsActivity.showMeals(meals);
            }
        }
    }

    class SearchMeals extends AsyncTask<String, Integer, List<Meal>> {

        @Override
        protected List<Meal> doInBackground(String... params) {
            List<Meal> result = new ArrayList<>();
            MenuDataBase menuDataBase = MenuDataBase.getInstance(chooseFromMealsActivity);
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
                chooseFromMealsActivity.showMeals(meals);
            }
        }
    }
}
