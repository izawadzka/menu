package com.example.dell.menu.screens.menus.addOrEditMenu.createNewDailyMenu;

import android.os.AsyncTask;
import android.util.Log;

import com.example.dell.menu.MenuDataBase;
import com.example.dell.menu.events.menus.AddMealToDailyMenuEvent;
import com.example.dell.menu.events.menus.MealAddedEvent;
import com.example.dell.menu.objects.DailyMenu;
import com.example.dell.menu.objects.Meal;
import com.example.dell.menu.objects.Menu;
import com.example.dell.menu.tables.DailyMenusTable;
import com.example.dell.menu.tables.mealTypes.BreakfastTable;
import com.example.dell.menu.tables.mealTypes.DinnerTable;
import com.example.dell.menu.tables.mealTypes.LunchTable;
import com.example.dell.menu.tables.mealTypes.SupperTable;
import com.example.dell.menu.tables.mealTypes.TeatimeTable;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.Date;
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
    private Date dailyMenuDate;


    public Date getDailyMenuDate() {
        return dailyMenuDate;
    }

    public void setDailyMenuDate(Date dailyMenuDate) {
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
        if(createNewDailyMenuActivity != null) {
            new SaveDailyMenu().execute();
        }
    }

    private void saveMeals(Long result) {
        if(createNewDailyMenuActivity != null){
            new SaveMeals().execute(result);
        }
    }


    class SaveMeals extends AsyncTask<Long, Integer, Long>{

        @Override
        protected Long doInBackground(Long... params) {
            MenuDataBase menuDataBase = MenuDataBase.getInstance(createNewDailyMenuActivity);

            for (Meal breakfastMeal : breakfastMeals) {
                Long result = menuDataBase.insert(BreakfastTable.getTableName(),
                        BreakfastTable.getContentValues(params[0], breakfastMeal.getMealsId()));
                if( result == -1){
                    menuDataBase.close();
                    return result;
                }
            }

            for (Meal lunchMeal : lunchMeals) {
                Long result = menuDataBase.insert(LunchTable.getTableName(),
                        LunchTable.getContentValues(params[0], lunchMeal.getMealsId()));
                if(result == -1){
                    menuDataBase.close();
                    return result;
                }
            }

            for (Meal dinnerMeal : dinnerMeals) {
                Long result = menuDataBase.insert(DinnerTable.getTableName(),
                        DinnerTable.getContentValues(params[0], dinnerMeal.getMealsId()));
                if(result == -1){
                    menuDataBase.close();
                    return result;
                }
            }

            for (Meal teatimeMeal : teatimeMeals) {
                Long result = menuDataBase.insert(TeatimeTable.getTableName(),
                        TeatimeTable.getContentValues(params[0], teatimeMeal.getMealsId()));
                if(result == -1){
                    menuDataBase.close();
                    return result;
                }
            }

            for (Meal supperMeal : supperMeals) {
                Long result = menuDataBase.insert(SupperTable.getTableName(),
                        SupperTable.getContentValues(params[0], supperMeal.getMealsId()));
                if(result == -1){
                    menuDataBase.close();
                    return result;
                }
            }
            menuDataBase.close();
            return Long.valueOf(0);

        }

        @Override
        protected void onPostExecute(Long result) {
            if(result == Long.valueOf(0)){
                Log.d("MEALS", "OK");
                createNewDailyMenuActivity.saveSuccess();
            }else{
                createNewDailyMenuActivity.saveFailed();
            }
        }
    }

    class SaveDailyMenu extends AsyncTask<Void, Integer, Long> {

        @Override
        protected Long doInBackground(Void... params) {
            MenuDataBase menuDataBase = MenuDataBase.getInstance(createNewDailyMenuActivity);
            Long result =  menuDataBase.insert(DailyMenusTable.getTableName(), DailyMenusTable.getContentValues(dailyMenuDate));
            menuDataBase.close();
            return result;
        }

        @Override
        protected void onPostExecute(Long result) {
            if (result != -1) {
                saveMeals(result);
            }else{
                createNewDailyMenuActivity.saveFailed();
            }
        }
    }
}

