package com.example.dell.menu.screens.menus.addOrEditMenu.dailyMenu;

import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.dell.menu.MenuDataBase;
import com.example.dell.menu.events.menus.AddMealToDailyMenuEvent;
import com.example.dell.menu.events.menus.AddNewDailyMenuEvent;
import com.example.dell.menu.events.menus.DailyMenuHasChangedEvent;
import com.example.dell.menu.events.menus.MealAddedEvent;
import com.example.dell.menu.events.menus.ShowDailyMenuEvent;
import com.example.dell.menu.objects.DailyMenu;
import com.example.dell.menu.objects.Meal;
import com.example.dell.menu.tables.MealsTable;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.Vector;

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
    private boolean editMode;
    private int cumulativeNumberOfKcal;
    private DailyMenu currentDailyMenu;


    public void setCumulativeNumberOfKcal(int cumulativeNumberOfKcal) {
        this.cumulativeNumberOfKcal = cumulativeNumberOfKcal;
    }


    public DailyMenu getCurrentDailyMenu() {
        return currentDailyMenu;
    }

    public void setCurrentDailyMenu(DailyMenu currentDailyMenu) {
        this.currentDailyMenu = currentDailyMenu;
    }

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
        cumulativeNumberOfKcal += event.meal.getCumulativeNumberOfKcal();
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
            DailyMenu dailyMenu = addMealsAndDateToNewDailyMenu();
            Log.d("save", String.valueOf(dailyMenu.getCumulativeNumberOfKcal()));
            createNewDailyMenuActivity.saveSuccess();

            bus.post(new AddNewDailyMenuEvent(dailyMenu));
        }catch (Exception e){
            Log.d(createNewDailyMenuActivity.getPackageName(), e.getLocalizedMessage());
            createNewDailyMenuActivity.saveFailed();
        }
    }

    public void updateDailyMenu() {
        try {
            DailyMenu dailyMenu = addMealsAndDateToNewDailyMenu();
            dailyMenu.setDailyMenuId(currentDailyMenu.getDailyMenuId());
            createNewDailyMenuActivity.updateSuccess();
            bus.post(new DailyMenuHasChangedEvent(dailyMenu));
        }catch (Exception e){
            Log.d(createNewDailyMenuActivity.getPackageName(), e.getLocalizedMessage());
            createNewDailyMenuActivity.updateFailed();
        }
    }

    @NonNull
    private DailyMenu addMealsAndDateToNewDailyMenu() {
        DailyMenu dailyMenu = new DailyMenu(dailyMenuDate);
        dailyMenu.setCumulativeNumberOfKcal(cumulativeNumberOfKcal);

        for (Meal breakfastMeal : breakfastMeals) dailyMenu.addMeal(breakfastMeal, DailyMenu.BREAKFAST_KEY);


        for (Meal lunchMeal : lunchMeals) dailyMenu.addMeal(lunchMeal, DailyMenu.LUNCH_KEY);

        for (Meal dinnerMeal : dinnerMeals) dailyMenu.addMeal(dinnerMeal, DailyMenu.DINNER_KEY);

        for (Meal teatimeMeal : teatimeMeals) dailyMenu.addMeal(teatimeMeal, DailyMenu.TEATIME_KEY);

        for(Meal supperMeal : supperMeals) dailyMenu.addMeal(supperMeal, DailyMenu.SUPPER_KEY);

        return dailyMenu;
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

            if(createNewDailyMenuActivity != null){
                createNewDailyMenuActivity.setChosenMeals();
            }
    }

    public void removeMeal(int position, String mealType) {
        Log.d("man", "przed usunieciem " + String.valueOf(cumulativeNumberOfKcal));
        switch (mealType){
            case CreateNewDailyMenuActivity.BREAKFAST_KEY: cumulativeNumberOfKcal -= breakfastMeals.get(position).getCumulativeNumberOfKcal();
                Log.d("w man", String.valueOf(breakfastMeals.get(position).getCumulativeNumberOfKcal()));
                breakfastMeals.remove(position);
                                                            break;
            case CreateNewDailyMenuActivity.LUNCH_KEY:    cumulativeNumberOfKcal -= lunchMeals.get(position).getCumulativeNumberOfKcal();
                Log.d("w man", String.valueOf(lunchMeals.get(position).getCumulativeNumberOfKcal()));

                lunchMeals.remove(position);
                                                            break;
            case CreateNewDailyMenuActivity.DINNER_KEY: cumulativeNumberOfKcal -= dinnerMeals.get(position).getCumulativeNumberOfKcal();
                Log.d("w man", String.valueOf(dinnerMeals.get(position).getCumulativeNumberOfKcal()));

                dinnerMeals.remove(position);
                                                        break;
            case CreateNewDailyMenuActivity.TEATIME_KEY: cumulativeNumberOfKcal -= teatimeMeals.get(position).getCumulativeNumberOfKcal();
                Log.d("w man", String.valueOf(teatimeMeals.get(position).getCumulativeNumberOfKcal()));

                teatimeMeals.remove(position);
                                                        break;
            case CreateNewDailyMenuActivity.SUPPER_KEY: cumulativeNumberOfKcal -= supperMeals.get(position).getCumulativeNumberOfKcal();
                Log.d("w man", String.valueOf(supperMeals.get(position).getCumulativeNumberOfKcal()));

                supperMeals.remove(position);
                                                        break;
        }

        Log.d("man", "po " + String.valueOf(cumulativeNumberOfKcal));
        if(createNewDailyMenuActivity != null) createNewDailyMenuActivity.updateCalories();
    }

    public void setDailyMenuMeals(DailyMenu dailyMenu) {
        clearVectorsOfMeals();

        breakfastMeals.addAll(currentDailyMenu.getBreakfast());

        lunchMeals.addAll(currentDailyMenu.getLunch());

        dinnerMeals.addAll(currentDailyMenu.getDinner());

        teatimeMeals.addAll(currentDailyMenu.getTeatime());

        supperMeals.addAll(currentDailyMenu.getSupper());
    }


    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public int getCumulativeNumberOfKcal() {
        return cumulativeNumberOfKcal;
    }


    class LoadFullDetails extends  AsyncTask<DailyMenu, Integer, DailyMenu>{

        @Override
        protected DailyMenu doInBackground(DailyMenu... params) {
            MenuDataBase menuDataBase = MenuDataBase.getInstance(createNewDailyMenuActivity);
            for(Meal mealToShow : params[0].getBreakfast()) {
                String mealNames = String.format("SELECT %s FROM %s WHERE %s = '%s'",
                        MealsTable.getSecondColumnName(), MealsTable.getTableName(),
                        MealsTable.getFirstColumnName(), mealToShow.getMealsId());
                Cursor breakfastCursor = menuDataBase.downloadData(mealNames);
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
                Cursor lunchCursor = menuDataBase.downloadData(mealNames);
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
                Cursor dinnerCursor = menuDataBase.downloadData(mealNames);
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
                Cursor teatimeCursor = menuDataBase.downloadData(mealNames);
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
                Cursor supperCursor = menuDataBase.downloadData(mealNames);
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

