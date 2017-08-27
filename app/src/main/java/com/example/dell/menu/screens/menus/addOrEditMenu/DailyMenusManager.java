package com.example.dell.menu.screens.menus.addOrEditMenu;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.dell.menu.MenuDataBase;
import com.example.dell.menu.events.menus.AddNewDailyMenuEvent;
import com.example.dell.menu.events.menus.DailyMenuHasChangedEvent;
import com.example.dell.menu.objects.DailyMenu;
import com.example.dell.menu.objects.Meal;
import com.example.dell.menu.tables.DailyMenusTable;
import com.example.dell.menu.tables.MealsTable;
import com.example.dell.menu.tables.MenusDailyMenusTable;
import com.example.dell.menu.tables.MenusTable;
import com.example.dell.menu.tables.mealTypes.BreakfastTable;
import com.example.dell.menu.tables.mealTypes.DinnerTable;
import com.example.dell.menu.tables.mealTypes.LunchTable;
import com.example.dell.menu.tables.mealTypes.SupperTable;
import com.example.dell.menu.tables.mealTypes.TeatimeTable;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Dell on 03.08.2017.
 */

public class DailyMenusManager {
    private final Bus bus;
    private DailyMenusActivity dailyMenusActivity;

    private List<DailyMenu> listOfDailyMenus = new ArrayList<>();
    private long menuId;

    private static final int RESULT_OK = 1;
    private static final int RESULT_NO_DATA_DOWNLOADED = 0;
    private static final int RESULT_ERROR = -1;
    private DailyMenu dailyMenuToAddOrEdit;
    private boolean waitingToAddNewDailyMenu = false;
    private boolean waitingToEditDailyMenu = false;
    private boolean waitingToDeleteDailyMenu = false;
    private long dailyMenuToDeleteId = -1;

    public DailyMenusManager(Bus bus) {
        this.bus = bus;
        bus.register(this);
    }

    public void onAttach(DailyMenusActivity dailyMenusActivity){
        this.dailyMenusActivity = dailyMenusActivity;
    }

    public void onStop(){
        this.dailyMenusActivity = null;
    }

    public void loadDailyMenus() {
            listOfDailyMenus.clear();
            new LoadListOfDailyMenus().execute();
    }

    @Subscribe
    public void onAddNewDailyMenu(AddNewDailyMenuEvent event) {
            dailyMenuToAddOrEdit = event.dailyMenu;
            waitingToAddNewDailyMenu = true;
    }

    @Subscribe
    public void onDailyMenuHasChanged(DailyMenuHasChangedEvent event){
        dailyMenuToAddOrEdit = event.dailyMenu;
        waitingToEditDailyMenu = true;
    }

    public boolean isWaitingToAddNewDailyMenu() {
        return waitingToAddNewDailyMenu;
    }

    public boolean isWaitingToEditDailyMenu() {
        return waitingToEditDailyMenu;
    }

    @Nullable
    private DailyMenu findDailyMenu(long dailyMenuId) {
        for (DailyMenu dailyMenu : listOfDailyMenus) {
            if(dailyMenu.getDailyMenuId() == dailyMenuId) return dailyMenu;
        }
        return null;
    }

    public void addNewDailyMenu() {
        if(dailyMenusActivity != null) {
            waitingToAddNewDailyMenu = false;
            new CreateNewDailyMenuInDatabase().execute(dailyMenuToAddOrEdit);
        }
    }

    public void updateDailyMenu() {
        if(dailyMenusActivity != null){
            new UpdateDailyMenu().execute(dailyMenuToAddOrEdit);
        }
    }

    public void deleteDailyMenu(long dailyMenuId) {
        if(dailyMenusActivity != null) {
            dailyMenuToDeleteId = dailyMenuId;
            waitingToDeleteDailyMenu = true;
            new DeleteDailyMenuMeals().execute(dailyMenuId);
        }
    }

    class DeleteDailyMenu extends AsyncTask<Long, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Long... params) {
            MenuDataBase menuDataBase = MenuDataBase.getInstance(dailyMenusActivity);
            String[] dailyMenusId = {String.valueOf(params[0])};
            boolean result;
            try {
                menuDataBase.delete(MenusDailyMenusTable.getTableName(), String.format("%s = ?", MenusDailyMenusTable.getSecondColumnName()), dailyMenusId);
                result = true;
            }catch (Exception e){
                dailyMenusActivity.makeAStatement(e.getLocalizedMessage(), Toast.LENGTH_LONG);
                result = false;
            }

            if(result){
                try {
                    menuDataBase.delete(DailyMenusTable.getTableName(), String.format("%s = ?", DailyMenusTable.getFirstColumnName()), dailyMenusId);
                    result = true;
                }catch (Exception e){
                    dailyMenusActivity.makeAStatement(e.getLocalizedMessage(), Toast.LENGTH_LONG);
                    result = false;
                }
            }
            menuDataBase.close();
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result){
            if(dailyMenusActivity != null){
                if(result){
                    dailyMenusActivity.dailyMenuDeleteSuccess();
                }else dailyMenusActivity.dailyMenuDeleteFailed();
            }
        }
    }

    class UpdateDailyMenu extends AsyncTask<DailyMenu, Void, Boolean>{

        @Override
        protected Boolean doInBackground(DailyMenu... params) {
            boolean result;
            if(dailyMenusActivity != null){
                MenuDataBase menuDataBase = MenuDataBase.getInstance(dailyMenusActivity);

                ContentValues editContentValues = new ContentValues();
                editContentValues.put(DailyMenusTable.getSecondColumnName(), params[0].getDate());
                editContentValues.put(DailyMenusTable.getThirdColumnName(), params[0].getCumulativeNumberOfKcal());
                String[] dailyMenusId = {String.valueOf(params[0].getDailyMenuId())};
                String whereClause = String.format("%s = ?", DailyMenusTable.getFirstColumnName());
                if(menuDataBase.update(DailyMenusTable.getTableName(), editContentValues, whereClause, dailyMenusId) != -1) result = true;
                else  result = false;
                menuDataBase.close();
                return result;
            }else return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result){
                if(dailyMenusActivity != null) dailyMenusActivity.makeAStatement("Daily menu date successfully changed", Toast.LENGTH_SHORT);
                    deleteDailyMenuMeals(dailyMenuToAddOrEdit.getDailyMenuId());
            }else{
                waitingToEditDailyMenu = false;
                if(dailyMenusActivity != null) dailyMenusActivity.makeAStatement("An error occurred while trying to update daily menu", Toast.LENGTH_SHORT);
            }
        }
    }

    private void deleteDailyMenuMeals(long dailyMenuId) {
        if(dailyMenusActivity != null) new DeleteDailyMenuMeals().execute(dailyMenuId);
    }

    class DeleteDailyMenuMeals extends AsyncTask<Long, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Long... params) {
            MenuDataBase menuDataBase = MenuDataBase.getInstance(dailyMenusActivity);
            String[] dailyMenusId = {String.valueOf(params[0])};

            //breakfast
            try {
                menuDataBase.delete(BreakfastTable.getTableName(),
                        String.format("%s = ?", BreakfastTable.getFirstColumnName()), dailyMenusId);
            }catch (Exception e){
                dailyMenusActivity.makeAStatement(e.getLocalizedMessage(), Toast.LENGTH_LONG);
                menuDataBase.close();
                return false;
            }


            //lunch
            try {
                menuDataBase.delete(LunchTable.getTableName(),
                        String.format("%s = ?", LunchTable.getFirstColumnName()), dailyMenusId);
            }catch (Exception e){
                dailyMenusActivity.makeAStatement(e.getLocalizedMessage(), Toast.LENGTH_LONG);
                menuDataBase.close();
                return false;
            }

            //dinner
            try {
                menuDataBase.delete(DinnerTable.getTableName(),
                        String.format("%s = ?", DinnerTable.getFirstColumnName()), dailyMenusId);
            }catch (Exception e){
                dailyMenusActivity.makeAStatement(e.getLocalizedMessage(), Toast.LENGTH_LONG);
                menuDataBase.close();
                return false;
            }

            //teatime
            try {
                menuDataBase.delete(TeatimeTable.getTableName(),
                        String.format("%s = ?", TeatimeTable.getFirstColumnName()), dailyMenusId);
            }catch (Exception e){
                dailyMenusActivity.makeAStatement(e.getLocalizedMessage(), Toast.LENGTH_LONG);
                menuDataBase.close();
                return false;
            }

            //supper
            try {
                menuDataBase.delete(SupperTable.getTableName(),
                        String.format("%s = ?", SupperTable.getFirstColumnName()), dailyMenusId);
            }catch (Exception e){
                dailyMenusActivity.makeAStatement(e.getLocalizedMessage(), Toast.LENGTH_LONG);
                menuDataBase.close();
                return false;
            }

            menuDataBase.close();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            //onPost in case of updating dailyMenu
            if (waitingToEditDailyMenu) {
                if(result){
                    addDailyMenuMeals(dailyMenuToAddOrEdit);
                }else waitingToEditDailyMenu = false;
            }else if(waitingToDeleteDailyMenu){
                waitingToDeleteDailyMenu = false;
                if(result) {
                    new DeleteDailyMenu().execute(dailyMenuToDeleteId);
                }else{
                    dailyMenusActivity.makeAStatement("An error occurred while trying to delete a daily menu", Toast.LENGTH_LONG);
                }
            }
        }
    }

    private void addDailyMenuMeals(DailyMenu dailyMenuToAddOrEdit) {
        if(dailyMenusActivity != null) new AddDailyMenuMeals().execute(dailyMenuToAddOrEdit);
    }

    class AddDailyMenuMeals extends AsyncTask<DailyMenu, Void, Boolean>{

        @Override
        protected Boolean doInBackground(DailyMenu... params) {
            MenuDataBase menuDataBase = MenuDataBase.getInstance(dailyMenusActivity);


            for (Meal meal : params[0].getBreakfast()) {
                if(menuDataBase.insert(BreakfastTable.getTableName(), BreakfastTable.getContentValues(params[0].getDailyMenuId(), meal.getMealsId())) == -1){
                    menuDataBase.close();
                    Log.d("br", "tu sie wywala");
                    return false;
                }
            }

            for (Meal meal : params[0].getLunch()) {
                if(menuDataBase.insert(LunchTable.getTableName(), LunchTable.getContentValues(params[0].getDailyMenuId(), meal.getMealsId())) == -1){
                    menuDataBase.close();
                    Log.d("l", "tu sie wywala");
                    return false;
                }
            }

            for (Meal meal : params[0].getDinner()) {
                if(menuDataBase.insert(DinnerTable.getTableName(), DinnerTable.getContentValues(params[0].getDailyMenuId(), meal.getMealsId())) == -1){
                    menuDataBase.close();
                    Log.d("d", "tu sie wywala");
                    return false;
                }
            }

            for (Meal meal : params[0].getTeatime()) {
                if(menuDataBase.insert(TeatimeTable.getTableName(), TeatimeTable.getContentValues(params[0].getDailyMenuId(), meal.getMealsId())) == -1){
                    menuDataBase.close();
                    Log.d("t", "tu sie wywala");
                    return false;
                }
            }

            for (Meal meal : params[0].getSupper()) {
                if(menuDataBase.insert(SupperTable.getTableName(), SupperTable.getContentValues(params[0].getDailyMenuId(), meal.getMealsId())) == -1){
                    menuDataBase.close();
                    Log.d("s", "tu sie wywala");
                    return false;
                }
            }

            menuDataBase.close();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(waitingToEditDailyMenu){
                waitingToEditDailyMenu = false;
                if(result){
                    updateMenuCumulativeNumberOfKcal();
                    loadDailyMenus();
                }else{
                    if(dailyMenusActivity != null){
                        dailyMenusActivity.makeAStatement("An error occurred while trying to update meals", Toast.LENGTH_LONG);
                    }
                }
            }
        }
    }

    private void updateMenuCumulativeNumberOfKcal() {
        if(dailyMenusActivity != null){
            new UpdateMenuCumulativeNumberOfKcal().execute();
        }
    }

    class UpdateMenuCumulativeNumberOfKcal extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {
            int sum = 0;
            boolean result;
            MenuDataBase menuDataBase = MenuDataBase.getInstance(dailyMenusActivity);
            String[] mealsTypes = {"Breakfast", "Lunch", "Dinner", "Teatime", "Supper"};
            for (String mealsType : mealsTypes) {
                String query = String.format("SELECT SUM(cumulativeNumberOfKcal) FROM Meals WHERE mealsId IN (SELECT mealId FROM %s WHERE dailyMenuId IN (SELECT dailyMenuId FROM MenusDailyMenus WHERE\n" +
                        "menuId = '%s'));", mealsType, menuId);
                Cursor cursor = menuDataBase.downloadData(query);
                if(cursor.getCount() > 0){
                    cursor.moveToPosition(-1);
                    while (cursor.moveToNext()){
                        sum += cursor.getInt(0);
                    }
                }
            }

            ContentValues editContentValues = new ContentValues();
            editContentValues.put(MenusTable.getFourthColumnName(), sum);
            String[] menusId = {String.valueOf(menuId)};
            String whereClause = String.format("%s = ?", MenusTable.getFirstColumnName());
            if(menuDataBase.update(MenusTable.getTableName(), editContentValues, whereClause, menusId) != -1) result = true;
            else  result = false;
            menuDataBase.close();

            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(dailyMenusActivity != null){
                if(result) dailyMenusActivity.makeAStatement("Successfully updated menus cumulative number of kcal", Toast.LENGTH_LONG);
                else dailyMenusActivity.makeAStatement("An error occurred while an attempt to update menus cumulative number of kcal", Toast.LENGTH_LONG);
            }
        }
    }


    class CreateNewDailyMenuInDatabase extends AsyncTask<DailyMenu, Void, Boolean>{

        private MenuDataBase menuDataBase;

        @Override
        protected Boolean doInBackground(DailyMenu... params) {
            menuDataBase = MenuDataBase.getInstance(dailyMenusActivity);

            long dailyMenuId = menuDataBase.insert(DailyMenusTable.getTableName(),
                    DailyMenusTable.getContentValues(params[0].getDate(), params[0].getCumulativeNumberOfKcal())); //add new daily menu into DailyMenusTable

            if(dailyMenuId != -1){
                //add new daily menu and current menu into MenusDailyMenusTable
                if( menuDataBase.insert(MenusDailyMenusTable.getTableName(),
                        MenusDailyMenusTable.getContentValues((long)menuId, dailyMenuId)) == -1){
                    menuDataBase.close();
                    return false;
                }
                if(!addDailyMenuMeals(dailyMenuId, params[0])){
                    return false;
                }
            }else{
                menuDataBase.close();
                return false;
            }

            menuDataBase.close();
            dailyMenuToAddOrEdit.setDailyMenuId(dailyMenuId);
            return true;
        }

        public boolean addDailyMenuMeals(long dailyMenuId, DailyMenu dailyMenu) {
            for (Meal meal : dailyMenu.getBreakfast()) {
                if(menuDataBase.insert(BreakfastTable.getTableName(), BreakfastTable.getContentValues(dailyMenuId, meal.getMealsId())) == -1){
                    menuDataBase.close();
                    return false;
                }
            }

            for (Meal meal : dailyMenu.getLunch()) {
                if(menuDataBase.insert(LunchTable.getTableName(), LunchTable.getContentValues(dailyMenuId, meal.getMealsId())) == -1){
                    menuDataBase.close();
                    return false;
                }
            }

            for (Meal meal : dailyMenu.getDinner()) {
                if(menuDataBase.insert(DinnerTable.getTableName(), DinnerTable.getContentValues(dailyMenuId, meal.getMealsId())) == -1){
                    menuDataBase.close();
                    return false;
                }
            }

            for (Meal meal : dailyMenu.getTeatime()) {
                if(menuDataBase.insert(TeatimeTable.getTableName(), TeatimeTable.getContentValues(dailyMenuId, meal.getMealsId())) == -1){
                    menuDataBase.close();
                    return false;
                }
            }

            for (Meal meal : dailyMenu.getSupper()) {
                if(menuDataBase.insert(SupperTable.getTableName(), SupperTable.getContentValues(dailyMenuId, meal.getMealsId())) == -1){
                    menuDataBase.close();
                    return false;
                }
            }
            menuDataBase.close();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result){
                listOfDailyMenus.add(dailyMenuToAddOrEdit);
                updateMenuCumulativeNumberOfKcal();
                if(dailyMenusActivity != null){
                    dailyMenusActivity.setAdapter();
                }
            }
        }
    }




    class LoadListOfDailyMenus extends AsyncTask<Void, Void, Integer>{

        @Override
        protected Integer doInBackground(Void... params) {
            int result;
            if(dailyMenusActivity != null){
                MenuDataBase menuDataBase = MenuDataBase.getInstance(dailyMenusActivity);
                String query = String.format("SELECT dailyMenuId FROM MenusDailyMenus WHERE menuId = '%s';",menuId);
                Cursor cursor = menuDataBase.downloadData(query);
                if(cursor.getCount() > 0){
                    cursor.moveToPosition(-1);
                    while (cursor.moveToNext()){
                        listOfDailyMenus.add(new DailyMenu(cursor.getLong(0)));
                    }
                    result = RESULT_OK;
                }else{
                    result = RESULT_NO_DATA_DOWNLOADED;
                }
                menuDataBase.close();
            }else result = RESULT_ERROR;

            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if(result == RESULT_OK){
                loadDailyMenusDatesAndCalories();
            }else {
                if (dailyMenusActivity != null) {
                    if (result == RESULT_ERROR) {
                        dailyMenusActivity.makeAStatement("An error occurred while trying to download dailyMenus", Toast.LENGTH_SHORT);
                    } else if (result == RESULT_NO_DATA_DOWNLOADED) {
                        dailyMenusActivity.makeAStatement("Seems like current menu doesn't contain any daily menu as yet", Toast.LENGTH_LONG);
                    }
                }
            }
        }
    }


    private void loadDailyMenusDatesAndCalories() {
        new LoadDailyMenusDatesAndCalories().execute();
    }

    public void setMenuId(long menuId) {
        this.menuId = menuId;
    }

    class LoadDailyMenusDatesAndCalories extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean result;
            if(dailyMenusActivity != null) {
                MenuDataBase menuDataBase = MenuDataBase.getInstance(dailyMenusActivity);

                for (int i = 0; i < listOfDailyMenus.size(); i++) {
                    String query =  String.format("SELECT date, cumulativeNumberOfKcal FROM DailyMenus WHERE dailyMenuId = '%s';",
                            listOfDailyMenus.get(i).getDailyMenuId());
                    Cursor cursor = menuDataBase.downloadData(query);
                    if(cursor.getCount() > 0){
                        cursor.moveToPosition(-1);
                        while(cursor.moveToNext()){
                            listOfDailyMenus.get(i).setDailyMenuDate(cursor.getString(0));
                            listOfDailyMenus.get(i).setCumulativeNumberOfKcal(cursor.getInt(1));
                        }
                    }else{
                        menuDataBase.close();
                        return false;
                    }
                }
                result = true;
                menuDataBase.close();
            }else result = false;

            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(dailyMenusActivity != null){
                if(result){
                    sortDailyMenusByDate();
                    loadDailyMenusMeals();
                }else{
                    dailyMenusActivity.makeAStatement("An error occurred while an attempt to download daily menus",
                            Toast.LENGTH_SHORT);
                    dailyMenusActivity.makeAStatement("Impossible to display daily menus", Toast.LENGTH_LONG);
                }
            }
        }
    }

    private void sortDailyMenusByDate() {
        Collections.sort(listOfDailyMenus, new Comparator<DailyMenu>() {
            @Override
            public int compare(DailyMenu o1, DailyMenu o2) {
                return (Date.valueOf(o1.getDate()).compareTo(Date.valueOf(o2.getDate())));
            }
        });
    }

    private void loadDailyMenusMeals() {
        new LoadDailyMenusMeals().execute();
    }

    class LoadDailyMenusMeals extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {
            if(dailyMenusActivity != null){
                MenuDataBase menuDataBase = MenuDataBase.getInstance(dailyMenusActivity);

                for (DailyMenu dailyMenu : listOfDailyMenus) {
                    dailyMenu.clearVectors();   //remove previously loaded meals


                    //breakfast
                    String breakfastQuery = String.format("SELECT mealId, name, cumulativeNumberOfKcal, authorsId, recipe FROM %s bf JOIN  %s me ON bf.%s = me.%s WHERE bf.%s = '%s';",
                            BreakfastTable.getTableName(), MealsTable.getTableName(),
                            BreakfastTable.getSecondColumnName(), MealsTable.getFirstColumnName(),
                            BreakfastTable.getFirstColumnName(), dailyMenu.getDailyMenuId());
                    Cursor breakfastCursor = menuDataBase.downloadData(breakfastQuery);
                    if(breakfastCursor.getCount() > 0){
                        breakfastCursor.moveToPosition(-1);
                        while (breakfastCursor.moveToNext()){
                            dailyMenu.addMeal(new Meal(breakfastCursor.getInt(0),breakfastCursor.getString(1),
                                    breakfastCursor.getInt(2), breakfastCursor.getInt(3),
                                    breakfastCursor.getString(4)), DailyMenu.BREAKFAST_KEY);
                        }
                    }

                    //lunch
                    String lunchQuery = String.format("SELECT mealId, name, cumulativeNumberOfKcal, authorsId, recipe FROM %s bf JOIN  %s me ON bf.%s = me.%s WHERE bf.%s = '%s';",
                            LunchTable.getTableName(), MealsTable.getTableName(),
                            LunchTable.getSecondColumnName(), MealsTable.getFirstColumnName(),
                            LunchTable.getFirstColumnName(), dailyMenu.getDailyMenuId());
                    Cursor lunchCursor = menuDataBase.downloadData(lunchQuery);
                    if(lunchCursor.getCount() > 0){
                        lunchCursor.moveToPosition(-1);
                        while (lunchCursor.moveToNext()){
                            dailyMenu.addMeal(new Meal(lunchCursor.getInt(0),lunchCursor.getString(1),
                                    lunchCursor.getInt(2), lunchCursor.getInt(3),
                                    lunchCursor.getString(4)), DailyMenu.LUNCH_KEY);
                        }
                    }

                    //lunch
                    String dinnerQuery = String.format("SELECT mealId, name, cumulativeNumberOfKcal, authorsId, recipe FROM %s bf JOIN  %s me ON bf.%s = me.%s WHERE bf.%s = '%s';",
                            DinnerTable.getTableName(), MealsTable.getTableName(),
                            DinnerTable.getSecondColumnName(), MealsTable.getFirstColumnName(),
                            DinnerTable.getFirstColumnName(), dailyMenu.getDailyMenuId());
                    Cursor dinnerCursor = menuDataBase.downloadData(dinnerQuery);
                    if(dinnerCursor.getCount() > 0){
                        dinnerCursor.moveToPosition(-1);
                        while (dinnerCursor.moveToNext()){
                            dailyMenu.addMeal(new Meal(dinnerCursor.getInt(0),dinnerCursor.getString(1),
                                    dinnerCursor.getInt(2), dinnerCursor.getInt(3),
                                    dinnerCursor.getString(4)), DailyMenu.DINNER_KEY);
                        }
                    }


                    //teatime
                    String teatimeQuery = String.format("SELECT mealId, name, cumulativeNumberOfKcal, authorsId, recipe FROM %s bf JOIN  %s me ON bf.%s = me.%s WHERE bf.%s = '%s';",
                            TeatimeTable.getTableName(), MealsTable.getTableName(),
                            TeatimeTable.getSecondColumnName(), MealsTable.getFirstColumnName(),
                            TeatimeTable.getFirstColumnName(), dailyMenu.getDailyMenuId());
                    Cursor teatimeCursor = menuDataBase.downloadData(teatimeQuery);
                    if(teatimeCursor.getCount() > 0){
                        teatimeCursor.moveToPosition(-1);
                        while (teatimeCursor.moveToNext()){
                            dailyMenu.addMeal(new Meal(teatimeCursor.getInt(0),teatimeCursor.getString(1),
                                    teatimeCursor.getInt(2), teatimeCursor.getInt(3),
                                    teatimeCursor.getString(4)), DailyMenu.TEATIME_KEY);
                        }
                    }

                    //supper
                    String supperQuery = String.format("SELECT mealId, name, cumulativeNumberOfKcal, authorsId, recipe FROM %s bf JOIN  %s me ON bf.%s = me.%s WHERE bf.%s = '%s';",
                            SupperTable.getTableName(), MealsTable.getTableName(),
                            SupperTable.getSecondColumnName(), MealsTable.getFirstColumnName(),
                            SupperTable.getFirstColumnName(), dailyMenu.getDailyMenuId());
                    Cursor supperCursor = menuDataBase.downloadData(supperQuery);
                    if(supperCursor.getCount() > 0){
                        supperCursor.moveToPosition(-1);
                        while (supperCursor.moveToNext()){
                            dailyMenu.addMeal(new Meal(supperCursor.getInt(0),supperCursor.getString(1),
                                    supperCursor.getInt(2), supperCursor.getInt(3),
                                    supperCursor.getString(4)), DailyMenu.SUPPER_KEY);
                        }
                    }
/*
                    //breakfast
                    String breakfastQuery = String.format("SELECT %s, %s FROM %s bf JOIN  %s me ON bf.%s = me.%s WHERE bf.%s = '%s';",
                            MealsTable.getFirstColumnName(), MealsTable.getSecondColumnName(),
                            BreakfastTable.getTableName(), MealsTable.getTableName(),
                            BreakfastTable.getSecondColumnName(), MealsTable.getFirstColumnName(),
                            BreakfastTable.getFirstColumnName(), dailyMenu.getDailyMenuId());
                    Cursor breakfastCursor = menuDataBase.downloadData(breakfastQuery);
                    if(breakfastCursor.getCount() > 0){
                        breakfastCursor.moveToPosition(-1);
                        while (breakfastCursor.moveToNext()){
                            dailyMenu.addMeal(new Meal(breakfastCursor.getInt(0),breakfastCursor.getString(1)), DailyMenu.BREAKFAST_KEY);
                        }
                    }


                    //lunch
                    String lunchQuery = String.format("SELECT %s, %s FROM %s bf JOIN  %s me ON bf.%s = me.%s WHERE bf.%s = '%s';",
                            MealsTable.getFirstColumnName(), MealsTable.getSecondColumnName(),
                            LunchTable.getTableName(), MealsTable.getTableName(),
                            LunchTable.getSecondColumnName(), MealsTable.getFirstColumnName(),
                            LunchTable.getFirstColumnName(), dailyMenu.getDailyMenuId());
                    Cursor lunchCursor = menuDataBase.downloadData(lunchQuery);
                    if(lunchCursor.getCount() > 0){
                        lunchCursor.moveToPosition(-1);
                        while (lunchCursor.moveToNext()){
                            dailyMenu.addMeal(new Meal(lunchCursor.getInt(0),lunchCursor.getString(1)), DailyMenu.LUNCH_KEY);
                        }
                    }


                    //dinner
                    String dinnerQuery = String.format("SELECT %s, %s FROM %s bf JOIN  %s me ON bf.%s = me.%s WHERE bf.%s = '%s';",
                            MealsTable.getFirstColumnName(), MealsTable.getSecondColumnName(),
                            DinnerTable.getTableName(), MealsTable.getTableName(),
                            DinnerTable.getSecondColumnName(), MealsTable.getFirstColumnName(),
                            DinnerTable.getFirstColumnName(), dailyMenu.getDailyMenuId());
                    Cursor dinnerCursor = menuDataBase.downloadData(dinnerQuery);
                    if(dinnerCursor.getCount() > 0){
                        dinnerCursor.moveToPosition(-1);
                        while (dinnerCursor.moveToNext()){
                            dailyMenu.addMeal(new Meal(dinnerCursor.getInt(0),dinnerCursor.getString(1)), DailyMenu.DINNER_KEY);
                        }
                    }

                    //teatime
                    String teatimeQuery = String.format("SELECT %s, %s FROM %s bf JOIN  %s me ON bf.%s = me.%s WHERE bf.%s = '%s';",
                            MealsTable.getFirstColumnName(), MealsTable.getSecondColumnName(),
                            TeatimeTable.getTableName(), MealsTable.getTableName(),
                            TeatimeTable.getSecondColumnName(), MealsTable.getFirstColumnName(),
                            TeatimeTable.getFirstColumnName(), dailyMenu.getDailyMenuId());
                    Cursor teatimeCursor = menuDataBase.downloadData(teatimeQuery);
                    if(teatimeCursor.getCount() > 0){
                        teatimeCursor.moveToPosition(-1);
                        while (teatimeCursor.moveToNext()){
                            dailyMenu.addMeal(new Meal(teatimeCursor.getInt(0),teatimeCursor.getString(1)), DailyMenu.TEATIME_KEY);
                        }
                    }

                    //supper
                    String supperQuery = String.format("SELECT %s, %s FROM %s bf JOIN  %s me ON bf.%s = me.%s WHERE bf.%s = '%s';",
                            MealsTable.getFirstColumnName(), MealsTable.getSecondColumnName(),
                            SupperTable.getTableName(), MealsTable.getTableName(),
                            SupperTable.getSecondColumnName(), MealsTable.getFirstColumnName(),
                            SupperTable.getFirstColumnName(), dailyMenu.getDailyMenuId());
                    Cursor supperCursor = menuDataBase.downloadData(supperQuery);
                    if(supperCursor.getCount() > 0){
                        supperCursor.moveToPosition(-1);
                        while (supperCursor.moveToNext()){
                            dailyMenu.addMeal(new Meal(supperCursor.getInt(0),supperCursor.getString(1)), DailyMenu.SUPPER_KEY);
                        }
                    }*/
                }
                menuDataBase.close();
                return true;
            }else return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(dailyMenusActivity != null){
                if(result){
                    dailyMenusActivity.setAdapter();
                }else{
                    dailyMenusActivity.downloadingDailyMenusFailed();
                }
            }
        }
    }


    public List<DailyMenu> getDailyMenus() {
        return listOfDailyMenus;
    }
}
