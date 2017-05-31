package com.example.dell.menu.screens.meals;

import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.example.dell.menu.MenuDataBase;
import com.example.dell.menu.objects.Meal;
import com.example.dell.menu.tables.MealsTable;
import com.example.dell.menu.tables.UsersTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 27.05.2017.
 */

public class MealsFragmentManager {

    protected MealsFragment mealsFragment;

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

    public String getAuthorsName(Meal meal) {
        if(mealsFragment != null){
            MenuDataBase menuDataBase = MenuDataBase.getInstance(mealsFragment.getActivity());
        String query = String.format("SELECT %s FROM %s WHERE %s = '%s';", UsersTable.getSecondColumnName(), UsersTable.getTableName(), UsersTable.getFirstColumnName(), meal.getAuthorsId());
        Cursor cursor = menuDataBase.downloadDatas(query);
        if(cursor.getCount() != 0){
            cursor.moveToPosition(-1);
            cursor.moveToNext();
            return cursor.getString(0);
        }
            menuDataBase.close();
        }
        return "";
    }

    class DownloadMeals extends AsyncTask<Void, Integer, List<Meal>>{

        @Override
        protected List<Meal> doInBackground(Void... params) {
            MenuDataBase menuDataBase = MenuDataBase.getInstance(mealsFragment.getActivity());

            List<Meal> results = new ArrayList<>();
            String query = String.format("SELECT * FROM %s ", MealsTable.getTableName());
            Cursor cursor = menuDataBase.downloadDatas(query);

            if (cursor.getCount() > 0) {
                int mealsId, cumulativeNumberOfKcalPer100g, authorsId;
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
