package com.example.dell.menu.menuplanning.screens.menus.addOrEditMenu.dailyMenu;

import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.dell.menu.menuplanning.types.MealsType;
import com.example.dell.menu.data.MenuDataBase;
import com.example.dell.menu.menuplanning.events.menus.AddMealToDailyMenuEvent;
import com.example.dell.menu.menuplanning.events.menus.AddNewDailyMenuEvent;
import com.example.dell.menu.menuplanning.events.menus.DailyMenuHasChangedEvent;
import com.example.dell.menu.menuplanning.events.menus.MealAddedEvent;
import com.example.dell.menu.menuplanning.events.menus.ShowDailyMenuEvent;
import com.example.dell.menu.menuplanning.objects.DailyMenu;
import com.example.dell.menu.menuplanning.objects.Meal;
import com.example.dell.menu.data.tables.MealsTable;
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
    private int amountOfServingsInBreakfast = 1;
    private int amountOfServingsInLunch = 1;
    private int amountOfServingsInDinner = 1;
    private int amountOfServingsInTeatime = 1;
    private int amountOfServingsInSupper = 1;
    private int amountOfProteins;
    private int amountOfCarbons;
    private int amountOfFat;


    public void setCumulativeNumberOfKcal(int cumulativeNumberOfKcal) {
        this.cumulativeNumberOfKcal = cumulativeNumberOfKcal;
    }


    DailyMenu getCurrentDailyMenu() {
        return currentDailyMenu;
    }

    void setCurrentDailyMenuAndDetails(DailyMenu currentDailyMenu) {
        this.currentDailyMenu = currentDailyMenu;
        if(currentDailyMenu != null){
            dailyMenuDate = currentDailyMenu.getDate();

            cumulativeNumberOfKcal = currentDailyMenu.getCumulativeNumberOfKcal();
            amountOfProteins = currentDailyMenu.getCumulativeAmountOfProteins();
            amountOfCarbons = currentDailyMenu.getCumulativeAmountOfCarbons();
            amountOfFat = currentDailyMenu.getCumulativeAmountOfFat();

            amountOfServingsInBreakfast = currentDailyMenu.getAmountOfServingsInBreakfast();
            amountOfServingsInLunch = currentDailyMenu.getAmountOfServingsInLunch();
            amountOfServingsInDinner = currentDailyMenu.getAmountOfServingsInDinner();
            amountOfServingsInTeatime = currentDailyMenu.getAmountOfServingsInTeatime();
            amountOfServingsInSupper = currentDailyMenu.getAmountOfServingsInSupper();

            setDailyMenuMeals();
        }
    }

    String getDailyMenuDate() {
        return dailyMenuDate;
    }

    void setDailyMenuDate(String dailyMenuDate) {
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

    void clearVectorsOfMeals() {
        breakfastMeals.clear();
        lunchMeals.clear();
        dinnerMeals.clear();
        teatimeMeals.clear();
        supperMeals.clear();
    }

    Vector<Meal> getBreakfastMeals() {
        return breakfastMeals;
    }

    Vector<Meal> getLunchMeals() {
        return lunchMeals;
    }

    Vector<Meal> getDinnerMeals() {
        return dinnerMeals;
    }

    Vector<Meal> getTeatimeMeals() {
        return teatimeMeals;
    }

    Vector<Meal> getSupperMeals() {
        return supperMeals;
    }


    @Subscribe
    public void onAddClicked(AddMealToDailyMenuEvent event) {
        cumulativeNumberOfKcal += event.meal.getCumulativeNumberOfKcal();
        amountOfProteins += event.meal.getAmountOfProteins();
        amountOfCarbons += event.meal.getAmountOfCarbos();
        amountOfFat += event.meal.getAmountOfFat();

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

    void saveDailyMenu() {
        try {
            DailyMenu dailyMenu = addMealsAndDateToNewDailyMenu();

            bus.post(new AddNewDailyMenuEvent(dailyMenu));
            if(createNewDailyMenuActivity != null) createNewDailyMenuActivity.saveSuccess();
        }catch (Exception e){
            Log.d(createNewDailyMenuActivity.getPackageName(), e.getLocalizedMessage());
            createNewDailyMenuActivity.saveFailed();
        }
    }

    void updateDailyMenu() {
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
        dailyMenu.setCumulativeAmountOfProteins(amountOfProteins);
        dailyMenu.setCumulativeAmountOfCarbons(amountOfCarbons);
        dailyMenu.setCumulativeAmountOfFat(amountOfFat);

        dailyMenu.setAmountOfServingsInBreakfast(amountOfServingsInBreakfast);
        dailyMenu.setAmountOfServingsInLunch(amountOfServingsInLunch);
        dailyMenu.setAmountOfServingsInDinner(amountOfServingsInDinner);
        dailyMenu.setAmountOfServingsInTeatime(amountOfServingsInTeatime);
        dailyMenu.setAmountOfServingsInSupper(amountOfServingsInSupper);

        for (Meal breakfastMeal : breakfastMeals) dailyMenu.addMeal(breakfastMeal, MealsType.BREAKFAST_INDX);


        for (Meal lunchMeal : lunchMeals) dailyMenu.addMeal(lunchMeal, MealsType.LUNCH_INDX);

        for (Meal dinnerMeal : dinnerMeals) dailyMenu.addMeal(dinnerMeal, MealsType.DINNER_INDX);

        for (Meal teatimeMeal : teatimeMeals) dailyMenu.addMeal(teatimeMeal, MealsType.TEATIME_INDX);

        for(Meal supperMeal : supperMeals) dailyMenu.addMeal(supperMeal, MealsType.SUPPER_INDX);

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

    void removeMeal(int position, int mealTypeIndx) {
        Vector<Meal> mealVector;
        switch (mealTypeIndx){
            case MealsType.BREAKFAST_INDX: mealVector = breakfastMeals;
                break;
            case MealsType.LUNCH_INDX:    mealVector = lunchMeals;
                break;
            case MealsType.DINNER_INDX: mealVector = dinnerMeals;
                break;
            case MealsType.TEATIME_INDX: mealVector = teatimeMeals;
                break;
            case MealsType.SUPPER_INDX: mealVector = supperMeals;
                break;
            default: mealVector = null;
        }

        if(mealVector != null) {
            cumulativeNumberOfKcal -= mealVector.get(position).getCumulativeNumberOfKcal();
            amountOfProteins -= mealVector.get(position).getAmountOfProteins();
            amountOfCarbons -= mealVector.get(position).getAmountOfCarbos();
            amountOfFat -= mealVector.get(position).getAmountOfFat();
            mealVector.remove(position);

            if (createNewDailyMenuActivity != null) createNewDailyMenuActivity.setDetails();
        }
    }

    void setDailyMenuMeals() {
        clearVectorsOfMeals();

        breakfastMeals.addAll(currentDailyMenu.getBreakfast());

        lunchMeals.addAll(currentDailyMenu.getLunch());

        dinnerMeals.addAll(currentDailyMenu.getDinner());

        teatimeMeals.addAll(currentDailyMenu.getTeatime());

        supperMeals.addAll(currentDailyMenu.getSupper());
    }


    boolean isEditMode() {
        return editMode;
    }

    void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public int getCumulativeNumberOfKcal() {
        return cumulativeNumberOfKcal;
    }

    void setAmountOfServingsInBreakfast(int amountOfServingsInBreakfast) {
        this.amountOfServingsInBreakfast = amountOfServingsInBreakfast;
    }

    void setAmountOfServingsInLunch(int amountOfServingsInLunch) {
        this.amountOfServingsInLunch = amountOfServingsInLunch;
    }

    void setAmountOfServingsInDinner(int amountOfServingsInDinner) {
        this.amountOfServingsInDinner = amountOfServingsInDinner;
    }

    void setAmountOfServingsInTeatime(int amountOfServingsInTeatime) {
        this.amountOfServingsInTeatime = amountOfServingsInTeatime;
    }

    void setAmountOfServingsInSupper(int amountOfServingsInSupper) {
        this.amountOfServingsInSupper = amountOfServingsInSupper;
    }

    int getAmountOfServingsInBreakfast() {
        return amountOfServingsInBreakfast;
    }

    int getAmountOfServingsInLunch() {
        return amountOfServingsInLunch;
    }

    int getAmountOfServingsInDinner() {
        return amountOfServingsInDinner;
    }

    int getAmountOfServingsInTeatime() {
        return amountOfServingsInTeatime;
    }

    int getAmountOfServingsInSupper() {
        return amountOfServingsInSupper;
    }

    int getAmountOfProteins() {
        return amountOfProteins;
    }

    int getAmountOfCarbons() {
        return amountOfCarbons;
    }

    public int getAmountOfFat() {
        return amountOfFat;
    }

    public void resetValues() {
        amountOfProteins = 0;
        amountOfCarbons = 0;
        amountOfFat = 0;
        cumulativeNumberOfKcal = 0;
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

