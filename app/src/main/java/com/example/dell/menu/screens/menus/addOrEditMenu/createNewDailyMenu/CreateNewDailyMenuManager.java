package com.example.dell.menu.screens.menus.addOrEditMenu.createNewDailyMenu;

import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.example.dell.menu.MenuDataBase;
import com.example.dell.menu.events.menus.AddMealToDailyMenuEvent;
import com.example.dell.menu.events.menus.AddNewDailyMenuEvent;
import com.example.dell.menu.events.menus.MealAddedEvent;
import com.example.dell.menu.events.menus.ShowDailyMenuEvent;
import com.example.dell.menu.objects.DailyMenu;
import com.example.dell.menu.objects.Meal;
import com.example.dell.menu.objects.Menu;
import com.example.dell.menu.tables.DailyMenusTable;
import com.example.dell.menu.tables.MealsTable;
import com.example.dell.menu.tables.mealTypes.BreakfastTable;
import com.example.dell.menu.tables.mealTypes.DinnerTable;
import com.example.dell.menu.tables.mealTypes.LunchTable;
import com.example.dell.menu.tables.mealTypes.SupperTable;
import com.example.dell.menu.tables.mealTypes.TeatimeTable;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.Date;
import java.util.Vector;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by Dell on 05.06.2017.
 */

public class CreateNewDailyMenuManager {
    private Vector<Meal> breakfastMeals = new Vector<>();
    private Vector<Meal> lunchMeals = new Vector<>();
    private Vector<Meal> dinnerMeals = new Vector<>();
    private Vector<Meal> teatimeMeals = new Vector<>();
    private Vector<Meal> supperMeals = new Vector<>();

    private CreateNewDailyMenuActivity createNewDailyMenuActivity;
    private final Bus bus;
    private String dailyMenuDate;
    protected Long dailyMenuId;


    public String getDailyMenuDate() {
        return dailyMenuDate;
    }

    public void setDailyMenuDate(String dailyMenuDate) {
        this.dailyMenuDate = dailyMenuDate;
    }

    public CreateNewDailyMenuManager(Bus bus) {
        this.bus = bus;
        bus.register(this);
    }

    public void onAttach(CreateNewDailyMenuActivity createNewDailyMenuActivity) {
        this.createNewDailyMenuActivity = createNewDailyMenuActivity;
    }

    public void onStop() {
        this.createNewDailyMenuActivity = null;
    }

    public void clearVectorsOfMeals() {
        breakfastMeals.clear();
        lunchMeals.clear();
        dinnerMeals.clear();
        teatimeMeals.clear();
        supperMeals.clear();
    }

    public Vector<Meal> getBreakfastMeals() {
        return breakfastMeals;
    }

    public Vector<Meal> getLunchMeals() {
        return lunchMeals;
    }

    public Vector<Meal> getDinnerMeals() {
        return dinnerMeals;
    }

    public Vector<Meal> getTeatimeMeals() {
        return teatimeMeals;
    }

    public Vector<Meal> getSupperMeals() {
        return supperMeals;
    }

    @Subscribe
    public void onAddClicked(AddMealToDailyMenuEvent event) {
        switch (event.mealType) {
            case CreateNewDailyMenuActivity.BREAKFAST_KEY:
                addMeal(breakfastMeals, event.meal);
                break;
            case CreateNewDailyMenuActivity.LUNCH_KEY:
                addMeal(lunchMeals, event.meal);
                break;
            case CreateNewDailyMenuActivity.DINNER_KEY:
                addMeal(dinnerMeals, event.meal);
                break;
            case CreateNewDailyMenuActivity.TEATIME_KEY:
                addMeal(teatimeMeals, event.meal);
                break;
            case CreateNewDailyMenuActivity.SUPPER_KEY:
                addMeal(supperMeals, event.meal);
                break;
        }

        bus.post(new MealAddedEvent(event.meal.getName()));
    }

    private void addMeal(Vector<Meal> mealType, Meal mealToAdd) {
        if (!wasAddedPreviously(mealType, mealToAdd)) {
            mealType.add(mealToAdd);
        }
    }

    private boolean wasAddedPreviously(Vector<Meal> vector, Meal mealToAdd) {
        for (Meal meal : vector) {
            if (meal.getMealsId() == mealToAdd.getMealsId()) {
                return true;
            }
        }
        return false;
    }

    public void saveDailyMenu() {
        try {
            DailyMenu dailyMenu = new DailyMenu(dailyMenuDate);

            for (Meal breakfastMeal : breakfastMeals) {
                dailyMenu.addMeal(breakfastMeal, DailyMenu.BREAKFAST_KEY);
            }

            for (Meal lunchMeal : lunchMeals) {
                dailyMenu.addMeal(lunchMeal, DailyMenu.LUNCH_KEY);
            }

            for (Meal dinnerMeal : dinnerMeals) {
                dailyMenu.addMeal(dinnerMeal, DailyMenu.DINNER_KEY);
            }

            for (Meal teatimeMeal : teatimeMeals) {
                dailyMenu.addMeal(teatimeMeal, DailyMenu.TEATIME_KEY);
            }

            for(Meal supperMeal : supperMeals){
                dailyMenu.addMeal(supperMeal, DailyMenu.SUPPER_KEY);
            }


            createNewDailyMenuActivity.saveSuccess();
            bus.post(new AddNewDailyMenuEvent(dailyMenu));
        }catch (Exception e){
            Log.d(createNewDailyMenuActivity.getPackageName(), e.getLocalizedMessage());
            createNewDailyMenuActivity.saveFailed();
        }
    }

    @Subscribe
    public void onShowDailyMenuEvent(ShowDailyMenuEvent event){
            clearVectorsOfMeals();

            dailyMenuDate = event.dailyMenu.getDate();
            breakfastMeals.addAll(event.dailyMenu.getBreakfast());
            lunchMeals.addAll(event.dailyMenu.getLunch());
            dinnerMeals.addAll(event.dailyMenu.getDinner());
            teatimeMeals.addAll(event.dailyMenu.getTeatime());
            supperMeals.addAll(event.dailyMenu.getSupper());

            if(event.is_showe_mode){
                if(createNewDailyMenuActivity != null){
                    createNewDailyMenuActivity.hideButtons();
                }
            }

            if(createNewDailyMenuActivity != null){
                createNewDailyMenuActivity.setChosenMeals();
            }
    }

    class LoadFullDetails extends  AsyncTask<DailyMenu, Integer, DailyMenu>{

        @Override
        protected DailyMenu doInBackground(DailyMenu... params) {
            MenuDataBase menuDataBase = MenuDataBase.getInstance(createNewDailyMenuActivity);
            for(Meal mealToShow : params[0].getBreakfast()) {
                String mealNames = String.format("SELECT %s FROM %s WHERE %s = '%s'",
                        MealsTable.getSecondColumnName(), MealsTable.getTableName(),
                        MealsTable.getFirstColumnName(), mealToShow.getMealsId());
                Cursor breakfastCursor = menuDataBase.downloadDatas(mealNames);
                if(breakfastCursor.getCount() == 1){
                    breakfastCursor.moveToPosition(-1);
                    while(breakfastCursor.moveToNext()) {
                        mealToShow.setName(breakfastCursor.getString(0));
                    }
                }
            }

            for(Meal mealToShow : params[0].getLunch()) {
                String mealNames = String.format("SELECT %s FROM %s WHERE %s = '%s'",
                        MealsTable.getSecondColumnName(), MealsTable.getTableName(),
                        MealsTable.getFirstColumnName(), mealToShow.getMealsId());
                Cursor lunchCursor = menuDataBase.downloadDatas(mealNames);
                if(lunchCursor.getCount() == 1){
                    lunchCursor.moveToPosition(-1);
                    while(lunchCursor.moveToNext()) {
                        mealToShow.setName(lunchCursor.getString(0));
                    }
                }
            }

            for(Meal mealToShow : params[0].getDinner()) {
                String mealNames = String.format("SELECT %s FROM %s WHERE %s = '%s'",
                        MealsTable.getSecondColumnName(), MealsTable.getTableName(),
                        MealsTable.getFirstColumnName(), mealToShow.getMealsId());
                Cursor dinnerCursor = menuDataBase.downloadDatas(mealNames);
                if(dinnerCursor.getCount() == 1){
                    dinnerCursor.moveToPosition(-1);
                    while(dinnerCursor.moveToNext()) {
                        mealToShow.setName(dinnerCursor.getString(0));
                    }
                }
            }

            for(Meal mealToShow : params[0].getTeatime()) {
                String mealNames = String.format("SELECT %s FROM %s WHERE %s = '%s'",
                        MealsTable.getSecondColumnName(), MealsTable.getTableName(),
                        MealsTable.getFirstColumnName(), mealToShow.getMealsId());
                Cursor teatimeCursor = menuDataBase.downloadDatas(mealNames);
                if(teatimeCursor.getCount() == 1){
                    teatimeCursor.moveToPosition(-1);
                    while(teatimeCursor.moveToNext()) {
                        mealToShow.setName(teatimeCursor.getString(0));
                    }
                }
            }

            for(Meal mealToShow : params[0].getSupper()) {
                String mealNames = String.format("SELECT %s FROM %s WHERE %s = '%s'",
                        MealsTable.getSecondColumnName(), MealsTable.getTableName(),
                        MealsTable.getFirstColumnName(), mealToShow.getMealsId());
                Cursor supperCursor = menuDataBase.downloadDatas(mealNames);
                if(supperCursor.getCount() == 1){
                   supperCursor.moveToPosition(-1);
                    while(supperCursor.moveToNext()) {
                        mealToShow.setName(supperCursor.getString(0));
                    }
                }
            }
            return params[0];
        }

        @Override
        protected void onPostExecute(DailyMenu dailyMenu) {
            if(dailyMenu != null){
                if(createNewDailyMenuActivity != null){
                    createNewDailyMenuActivity.setChosenMeals();
                }
            }
        }
    }
}

