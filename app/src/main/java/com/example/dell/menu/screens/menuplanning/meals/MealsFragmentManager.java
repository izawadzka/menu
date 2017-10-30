package com.example.dell.menu.screens.menuplanning.meals;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;

import com.example.dell.menu.MenuDataBase;
import com.example.dell.menu.events.meals.DeleteMealEvent;
import com.example.dell.menu.objects.menuplanning.Meal;
import com.example.dell.menu.tables.MealsProductsTable;
import com.example.dell.menu.tables.MealsTable;
import com.example.dell.menu.tables.MealsTypesMealsTable;
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
        if(mealsFragment != null){
            new AsyncTask<Void, Void, List<Meal>>(){
                @Override
                protected List<Meal> doInBackground(Void... params) {
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(mealsFragment.getActivity());

                    List<Meal> results = new ArrayList<>();
                    String query = String.format("SELECT %s, %s, %s, %s, %s, %s FROM %s ",
                            MealsTable.getFirstColumnName(), MealsTable.getSecondColumnName(),
                            MealsTable.getThirdColumnName(), MealsTable.getSixthColumnName(),
                            MealsTable.getSeventhColumnName(), MealsTable.getEighthColumnName(),
                            MealsTable.getTableName());
                    Cursor cursor = menuDataBase.downloadData(query);

                    if (cursor.getCount() > 0) {
                        int mealsId, amountOfKCal, amountOfProteins, amountOfCarbons, amountOfFat;
                        String name;
                        cursor.moveToPosition(-1);

                        while (cursor.moveToNext()) {
                            mealsId = cursor.getInt(0);
                            name = cursor.getString(1);
                            amountOfKCal = cursor.getInt(2);
                            amountOfProteins = cursor.getInt(3);
                            amountOfCarbons = cursor.getInt(4);
                            amountOfFat = cursor.getInt(5);
                            results.add(new Meal(mealsId, name, amountOfKCal, amountOfProteins,
                                    amountOfCarbons, amountOfFat));
                        }
                    }

                    menuDataBase.close();

                    return results;
                }

                @Override
                protected void onPostExecute(List<Meal> meals) {
                    mealsFragment.showMeals(meals);
                }
            }.execute();
        }
    }

    private void deleteConnectionWithProducts() {
        if(mealsFragment != null){
            new AsyncTask<Void, Void, Integer>(){
                @Override
                protected Integer doInBackground(Void... params) {
                    String[] mealId = {String.valueOf(currentMealId)};
                    int result;
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(mealsFragment.getActivity());
                    result = menuDataBase.delete(MealsProductsTable.getTableName(), String.format("%s = ?", MealsProductsTable.getSecondColumnName()), mealId);
                    menuDataBase.close();
                    return result;
                }

                @Override
                protected void onPostExecute(Integer result) {
                    if(result > 0){
                        deleteMealTypes();
                    }else{
                        if(mealsFragment != null){
                            mealsFragment.deleteFailed();
                        }
                    }
                }
            }.execute();
        }
    }

    private void deleteMealTypes() {
        if(mealsFragment != null){
            new AsyncTask<Void, Void, Integer>(){

                @Override
                protected Integer doInBackground(Void... params) {
                    int result;
                    String[] mealId = {String.valueOf(currentMealId)};
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(mealsFragment.getActivity());
                    result = menuDataBase.delete(MealsTypesMealsTable.getTableName(),
                            String.format("%s = ?", MealsTypesMealsTable.getSecondColumnName()), mealId);
                    menuDataBase.close();
                    return result;
                }

                @Override
                protected void onPostExecute(Integer result) {
                    if(mealsFragment != null){
                        if(result > 0) mealsFragment.deleteSuccess();
                        else mealsFragment.deleteFailed();
                    }
                }
            }.execute();
        }
    }

    public void deleteMeal(Meal meal){
        currentMealId = meal.getMealsId();
        currentMeal = meal;


        if(mealsFragment != null) {

            new AsyncTask<Void, Void, Integer>() {
                @Override
                protected Integer doInBackground(Void... params) {
                    String[] mealId = {String.valueOf(currentMealId)};
                    int result;
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(mealsFragment.getActivity());
                    result = menuDataBase.delete(MealsTable.getTableName(),
                            String.format("%s = ?", MealsTable.getFirstColumnName()), mealId);
                    menuDataBase.close();
                    return result;
                }

                @Override
                protected void onPostExecute(Integer result) {
                    if (result > 0) {
                        deleteConnectionWithProducts();
                    } else {
                        mealsFragment.deleteFailed();
                    }
                }
            }.execute();
        }
    }

    @Subscribe
    public void onDeleteMeal(final DeleteMealEvent event){
        if(mealsFragment != null){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mealsFragment.getContext());

            alertDialogBuilder.setTitle("Delete meal");

            alertDialogBuilder.setMessage(String.format("Are you sure you want to delete %s?",
                    event.meal.getName()))
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int d){
                            deleteMeal(event.meal);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int d){
                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();

            alertDialog.show();
        }
    }


    public void findMeals(String textToFind) {
        if(mealsFragment != null){
            new AsyncTask<String, Void, List<Meal>>(){
                @Override
                protected List<Meal> doInBackground(String... params) {
                    List<Meal> result = new ArrayList<>();
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(mealsFragment.getActivity());
                    String query = String.format("SELECT %s, %s, %s, %s, %s, %s FROM %s  WHERE %s " +
                                    "LIKE '%%%s%%' ORDER BY %s",
                            MealsTable.getFirstColumnName(), MealsTable.getSecondColumnName(),
                            MealsTable.getThirdColumnName(), MealsTable.getSixthColumnName(),
                            MealsTable.getSeventhColumnName(), MealsTable.getEighthColumnName(),
                            MealsTable.getTableName(), MealsTable.getSecondColumnName(), params[0],
                            MealsTable.getSecondColumnName());
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
            }.execute(textToFind);
        }
    }

}
