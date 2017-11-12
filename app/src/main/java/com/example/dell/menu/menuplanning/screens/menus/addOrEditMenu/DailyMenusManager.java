package com.example.dell.menu.menuplanning.screens.menus.addOrEditMenu;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.dell.menu.menuplanning.types.MealsType;
import com.example.dell.menu.data.MenuDataBase;
import com.example.dell.menu.menuplanning.events.menus.AddNewDailyMenuEvent;
import com.example.dell.menu.menuplanning.events.menus.DailyMenuHasChangedEvent;
import com.example.dell.menu.menuplanning.objects.DailyMenu;
import com.example.dell.menu.menuplanning.objects.Meal;
import com.example.dell.menu.data.tables.DailyMenusTable;
import com.example.dell.menu.data.tables.MealsTable;
import com.example.dell.menu.data.tables.MealsTypesDailyMenusAmountOfPeopleTable;
import com.example.dell.menu.data.tables.MealsTypesMealsDailyMenusTable;
import com.example.dell.menu.data.tables.MenusDailyMenusTable;
import com.example.dell.menu.data.tables.MenusTable;
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
    private static final String TAG = "DailyMenusManager";
    private final Bus bus;
    private DailyMenusActivity dailyMenusActivity;

    private List<DailyMenu> listOfDailyMenus = new ArrayList<>();
    private long menuId;

    private static final int RESULT_OK = 1;
    private static final int RESULT_NO_DATA_DOWNLOADED = 0;
    private DailyMenu currentDailyMenu;
    private boolean waitingToAddNewDailyMenu = false;
    private boolean waitingToEditDailyMenu = false;
    private boolean edit_mode = false;
    private boolean delete_mode = false;

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

    void loadDailyMenus() {
        if(dailyMenusActivity != null) {
            listOfDailyMenus.clear();

            new AsyncTask<Void, Void, Integer>() {
                @Override
                protected Integer doInBackground(Void... params) {
                    int result;
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(dailyMenusActivity);
                    String query = String.format("SELECT dailyMenuId FROM MenusDailyMenus WHERE menuId = '%s';", menuId);
                    Cursor cursor = menuDataBase.downloadData(query);
                    if (cursor.getCount() > 0) {
                        cursor.moveToPosition(-1);
                        while (cursor.moveToNext()) {
                            listOfDailyMenus.add(new DailyMenu(cursor.getLong(0)));
                        }
                        result = RESULT_OK;
                    } else {
                        result = RESULT_NO_DATA_DOWNLOADED;
                    }
                    menuDataBase.close();

                    return result;
                }

                @Override
                protected void onPostExecute(Integer result) {
                    if (result == RESULT_OK) {
                        loadDailyMenusDetails();
                    } else {
                        if (dailyMenusActivity != null) {
                            dailyMenusActivity.makeAStatement("Seems like current menu doesn't " +
                                    "contain any daily menu as yet", Toast.LENGTH_LONG);
                            dailyMenusActivity.setAdapter();
                        }
                    }
                }
            }.execute();
        }
    }

    @Subscribe
    public void onAddNewDailyMenu(AddNewDailyMenuEvent event) {
            currentDailyMenu = event.dailyMenu;
            waitingToAddNewDailyMenu = true;        //waiting for DailyMenuActivity to start,
                                                    // need context to open database
    }


    @Subscribe
    public void onDailyMenuHasChanged(DailyMenuHasChangedEvent event){
        currentDailyMenu = event.dailyMenu;
        waitingToEditDailyMenu = true;
        edit_mode = true;
    }

    boolean isWaitingToAddNewDailyMenu() {
        return waitingToAddNewDailyMenu;
    }

    boolean isWaitingToEditDailyMenu() {
        return waitingToEditDailyMenu;
    }

    public void deleteDailyMenu(DailyMenu dailyMenu) {
        delete_mode = true;
        currentDailyMenu = dailyMenu;

        if(dailyMenusActivity != null){
            new AsyncTask<Void, Void, Boolean>(){
                @Override
                protected Boolean doInBackground(Void... params) {
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(dailyMenusActivity);
                    String[] dailyMenusId = {String.valueOf(currentDailyMenu.getDailyMenuId())};
                    boolean result;

                    result = menuDataBase.delete(MenusDailyMenusTable.getTableName(),
                            String.format("%s = ?", MenusDailyMenusTable.getSecondColumnName()),
                            dailyMenusId) != 0;


                    if(result) result = menuDataBase.delete(DailyMenusTable.getTableName(),
                            String.format("%s = ?", DailyMenusTable.getFirstColumnName()),
                            dailyMenusId) != 0;

                    menuDataBase.close();
                    return result;
                }

                @Override
                protected void onPostExecute(Boolean result){
                    if(dailyMenusActivity != null){
                        if(result){
                           deleteAmountOfServings();
                        }else dailyMenusActivity.dailyMenuDeleteFailed();
                    }
                }
            }.execute();
        }
    }


    void addNewDailyMenu() {
        if(dailyMenusActivity != null) {
            waitingToAddNewDailyMenu = false;

            new AsyncTask<Void, Void, Boolean>(){

                @Override
                protected Boolean doInBackground(Void... params) {
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(dailyMenusActivity);
                    boolean result;

                    long dailyMenuId = menuDataBase.insert(DailyMenusTable.getTableName(),
                            DailyMenusTable.getContentValues(currentDailyMenu.getDate(),
                                    currentDailyMenu.getCumulativeNumberOfKcal(),
                                    currentDailyMenu.getCumulativeAmountOfProteins(),
                                    currentDailyMenu.getCumulativeAmountOfCarbons(),
                                    currentDailyMenu.getCumulativeAmountOfFat())); //add new daily menu into DailyMenusTable

                    if(dailyMenuId != -1){
                        currentDailyMenu.setDailyMenuId(dailyMenuId);
                        //add new daily menu and current menu into MenusDailyMenusTable
                        result = menuDataBase.insert(MenusDailyMenusTable.getTableName(),
                                MenusDailyMenusTable.getContentValues(menuId, dailyMenuId)) != -1;
                    } else result = false;

                    menuDataBase.close();
                    return result;
                }


                @Override
                protected void onPostExecute(Boolean result) {
                    if(result){
                        addDailyMenuMeals();
                    }
                }
            }.execute();
        }
    }

    private void addDailyMenuMeals(){
        if(dailyMenusActivity != null){
            new AsyncTask<Void, Void, Boolean>(){

                @Override
                protected Boolean doInBackground(Void... params) {
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(dailyMenusActivity);

                    for (Meal meal : currentDailyMenu.getBreakfast()) {
                        if(menuDataBase.insert(MealsTypesMealsDailyMenusTable.getTableName(),
                                MealsTypesMealsDailyMenusTable.getContentValues(MealsType.BREAKFAST_INDX,
                                        meal.getMealsId(), currentDailyMenu.getDailyMenuId())) == -1){
                            menuDataBase.close();
                            return false;
                        }
                    }

                    for (Meal meal : currentDailyMenu.getLunch()) {
                        if(menuDataBase.insert(MealsTypesMealsDailyMenusTable.getTableName(),
                                MealsTypesMealsDailyMenusTable.getContentValues(MealsType.LUNCH_INDX,
                                        meal.getMealsId(), currentDailyMenu.getDailyMenuId())) == -1){
                            menuDataBase.close();
                            return false;
                        }
                    }


                    for (Meal meal : currentDailyMenu.getDinner()) {
                        if(menuDataBase.insert(MealsTypesMealsDailyMenusTable.getTableName(),
                                MealsTypesMealsDailyMenusTable.getContentValues(MealsType.DINNER_INDX,
                                        meal.getMealsId(), currentDailyMenu.getDailyMenuId())) == -1){
                            menuDataBase.close();
                            return false;
                        }
                    }

                    for (Meal meal : currentDailyMenu.getTeatime()) {
                        if(menuDataBase.insert(MealsTypesMealsDailyMenusTable.getTableName(),
                                MealsTypesMealsDailyMenusTable.getContentValues(MealsType.TEATIME_INDX,
                                        meal.getMealsId(), currentDailyMenu.getDailyMenuId())) == -1){
                            menuDataBase.close();
                            return false;
                        }
                    }

                    for (Meal meal : currentDailyMenu.getSupper()) {
                        if(menuDataBase.insert(MealsTypesMealsDailyMenusTable.getTableName(),
                                MealsTypesMealsDailyMenusTable.getContentValues(MealsType.SUPPER_INDX,
                                        meal.getMealsId(), currentDailyMenu.getDailyMenuId())) == -1){
                            menuDataBase.close();
                            return false;
                        }
                    }
                    menuDataBase.close();
                    return true;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    if(result) {
                        if(edit_mode){
                            loadDailyMenus();
                            updateMenuCumulativeNumberOfKcalProteinsCarbonsAndFat();
                        }
                        else addAmountOfServings();
                    }else{
                        if(dailyMenusActivity != null && edit_mode)
                            dailyMenusActivity.makeAStatement("An error occurred while trying to update meals", Toast.LENGTH_LONG);
                    }
                    edit_mode = false;
                }
            }.execute();
        }
    }

    void updateDailyMenu() {
        if(dailyMenusActivity != null){
            new AsyncTask<Void, Void, Boolean>(){
                @Override
                protected Boolean doInBackground(Void... params) {
                    boolean result;

                    MenuDataBase menuDataBase = MenuDataBase.getInstance(dailyMenusActivity);

                    ContentValues editContentValues = new ContentValues();
                    editContentValues.put(DailyMenusTable.getSecondColumnName(), currentDailyMenu.getDate());
                    editContentValues.put(DailyMenusTable.getThirdColumnName(), currentDailyMenu.getCumulativeNumberOfKcal());
                    editContentValues.put(DailyMenusTable.getFourthColumnName(), currentDailyMenu.getCumulativeAmountOfProteins());
                    editContentValues.put(DailyMenusTable.getFifthColumnName(), currentDailyMenu.getCumulativeAmountOfCarbons());
                    editContentValues.put(DailyMenusTable.getSixthColumnName(), currentDailyMenu.getCumulativeAmountOfFat());

                    String[] dailyMenusId = {String.valueOf(currentDailyMenu.getDailyMenuId())};
                    String whereClause = String.format("%s = ?", DailyMenusTable.getFirstColumnName());
                    result = menuDataBase.update(DailyMenusTable.getTableName(), editContentValues, whereClause, dailyMenusId) != -1;
                    menuDataBase.close();
                    return result;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    waitingToEditDailyMenu = false;
                    if(result){
                        if(dailyMenusActivity != null) dailyMenusActivity.makeAStatement("Daily menu details successfully changed", Toast.LENGTH_SHORT);
                        updateAmountsOfServings();
                    }else{
                        if(dailyMenusActivity != null) dailyMenusActivity.makeAStatement("An error occurred while trying to update daily menu", Toast.LENGTH_SHORT);
                    }
                }
            }.execute();
        }
    }

    private void updateAmountsOfServings() {
        if(dailyMenusActivity != null){
            new AsyncTask<Void, Void, Boolean>(){

                @Override
                protected Boolean doInBackground(Void... params) {
                    boolean result = true;
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(dailyMenusActivity);
                    String[] dailyMenusId = {String.valueOf(currentDailyMenu.getDailyMenuId())};
                    result = menuDataBase.delete(MealsTypesDailyMenusAmountOfPeopleTable.getTableName(),
                            String.format("%s = ?", DailyMenusTable.getFirstColumnName()),
                            dailyMenusId) > 0;

                    if(result){
                        int[] amountsOfService = {currentDailyMenu.getAmountOfServingsInBreakfast(),
                                currentDailyMenu.getAmountOfServingsInLunch(),
                                currentDailyMenu.getAmountOfServingsInDinner(),
                                currentDailyMenu.getAmountOfServingsInTeatime(),
                                currentDailyMenu.getAmountOfServingsInSupper()};

                        int[] sizeOfMeals = {currentDailyMenu.getBreakfast().size(),
                                currentDailyMenu.getLunch().size(),
                                currentDailyMenu.getDinner().size(),
                                currentDailyMenu.getTeatime().size(),
                                currentDailyMenu.getSupper().size()};

                        int[] index = MealsType.arrayOfTypes;

                        for (int i = 0; i < MealsType.AMOUNT_OF_TYPES; i++) {
                            if(amountsOfService[i] != 0 && sizeOfMeals[i] > 0){
                                if(menuDataBase.insert(MealsTypesDailyMenusAmountOfPeopleTable.getTableName(),
                                        MealsTypesDailyMenusAmountOfPeopleTable
                                                .getContentValues(index[i],
                                                        currentDailyMenu.getDailyMenuId(),
                                                        amountsOfService[i])) == -1){
                                    menuDataBase.close();
                                    return false;
                                }
                            }
                        }
                    }

                    menuDataBase.close();
                    return result;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    if(result) deleteDailyMenuMeals();
                    else if(dailyMenusActivity != null){
                        dailyMenusActivity.makeAStatement("An error occurred while an attempt to" +
                                " update some details about daily menu", Toast.LENGTH_LONG);
                    }
                }
            }.execute();
        }
    }

    private void deleteAmountOfServings(){

        if(dailyMenusActivity != null) {
            new AsyncTask<Void, Void, Boolean>() {

                @Override
                protected Boolean doInBackground(Void... params) {
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(dailyMenusActivity);
                    String[] dailyMenusId = {String.valueOf(currentDailyMenu.getDailyMenuId())};
                    boolean result;
                    result = menuDataBase
                            .delete(MealsTypesDailyMenusAmountOfPeopleTable.getTableName(),
                            String.format("%s = ?", MealsTypesDailyMenusAmountOfPeopleTable.getSecondColumnName()),
                            dailyMenusId) != 0;
                    menuDataBase.close();
                    return result;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    if (result) deleteDailyMenuMeals();
                    else if(dailyMenusActivity != null) {
                        if(delete_mode) dailyMenusActivity.makeAStatement("An error occurred " +
                                "while an attempt to delete amount of servings", Toast.LENGTH_LONG);
                    }
                }
            }.execute();
        }
    }

    private void deleteDailyMenuMeals() {
        if(dailyMenusActivity != null){
            new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... params) {
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(dailyMenusActivity);
                    String[] dailyMenusId = {String.valueOf(currentDailyMenu.getDailyMenuId())};
                    boolean result;
                    result = menuDataBase.delete(MealsTypesMealsDailyMenusTable.getTableName(),
                            String.format("%s = ?", MealsTypesMealsDailyMenusTable.getThirdColumnName()),
                            dailyMenusId) != 0;

                    menuDataBase.close();
                    return result;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    //onPost in case of updating dailyMenu
                    if(edit_mode){
                        if(result) addDailyMenuMeals();
                        else if(dailyMenusActivity != null){
                            dailyMenusActivity.makeAStatement("An error occurred while an attempt " +
                                    "to update some details about daily menu", Toast.LENGTH_LONG);
                        }
                    }else if(delete_mode){
                        if(result){
                            updateMenuCumulativeNumberOfKcalProteinsCarbonsAndFat();
                            delete_mode = false;
                            if(dailyMenusActivity != null) dailyMenusActivity.dailyMenuDeleteSuccess();
                        }
                        else if(dailyMenusActivity != null)
                            dailyMenusActivity.makeAStatement("An error occurred while trying to " +
                                "delete a daily menu", Toast.LENGTH_LONG);

                    }
                }
            }.execute();
        }
    }

    private void addAmountOfServings() {
        if(dailyMenusActivity != null){
            new AsyncTask<Void, Void, Boolean>(){

                @Override
                protected Boolean doInBackground(Void... params) {
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(dailyMenusActivity);

                    int[] amountsOfService = {currentDailyMenu.getAmountOfServingsInBreakfast(),
                    currentDailyMenu.getAmountOfServingsInLunch(),
                    currentDailyMenu.getAmountOfServingsInDinner(),
                    currentDailyMenu.getAmountOfServingsInTeatime(),
                    currentDailyMenu.getAmountOfServingsInSupper()};

                    int[] sizeOfMeals = {currentDailyMenu.getBreakfast().size(),
                    currentDailyMenu.getLunch().size(),
                    currentDailyMenu.getDinner().size(),
                    currentDailyMenu.getTeatime().size(),
                    currentDailyMenu.getSupper().size()};

                    int[] index = {MealsType.BREAKFAST_INDX, MealsType.LUNCH_INDX,
                            MealsType.DINNER_INDX,MealsType.TEATIME_INDX, MealsType.SUPPER_INDX};


                    for (int i = 0; i < MealsType.AMOUNT_OF_TYPES; i++) {
                        if(amountsOfService[i] != 0 && sizeOfMeals[i] > 0){
                            if(menuDataBase.insert(MealsTypesDailyMenusAmountOfPeopleTable.getTableName(),
                                    MealsTypesDailyMenusAmountOfPeopleTable
                                            .getContentValues(index[i],
                                                    currentDailyMenu.getDailyMenuId(),
                                                    amountsOfService[i])) == -1){
                                menuDataBase.close();
                                return false;
                            }
                        }
                    }

                    menuDataBase.close();
                    return true;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    if(result){
                        updateMenuCumulativeNumberOfKcalProteinsCarbonsAndFat();
                        loadDailyMenus();
                    }else{
                        if(dailyMenusActivity != null)
                            dailyMenusActivity.makeAStatement("An error occurred while an attempt to " +
                                    "insert amount of serving", Toast.LENGTH_LONG);
                    }
                }
            }.execute();
        }
    }

    private void updateMenuCumulativeNumberOfKcalProteinsCarbonsAndFat() {
        if(dailyMenusActivity != null){

            new AsyncTask<Void, Void, Boolean>(){

                @Override
                protected Boolean doInBackground(Void... params) {
                    boolean result;
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(dailyMenusActivity);


                    String query = String.format("SELECT SUM(%s), SUM(%s), SUM(%s), SUM(%s) FROM %s WHERE %s IN " +
                            "(SELECT %s FROM %s WHERE %s = '%s')", DailyMenusTable.getThirdColumnName(),
                            DailyMenusTable.getFourthColumnName(), DailyMenusTable.getFifthColumnName(),
                            DailyMenusTable.getSixthColumnName(),
                            DailyMenusTable.getTableName(), DailyMenusTable.getFirstColumnName(),
                            MenusDailyMenusTable.getSecondColumnName(), MenusDailyMenusTable.getTableName(),
                            MenusDailyMenusTable.getFirstColumnName(), menuId);

                    Cursor cursor = menuDataBase.downloadData(query);
                    if(cursor.getCount() > 0){
                        cursor.moveToPosition(0);
                        ContentValues editContentValues = new ContentValues();
                        editContentValues.put(MenusTable.getFourthColumnName(), cursor.getInt(0));
                        editContentValues.put(MenusTable.getSixthColumnName(), cursor.getInt(1));
                        editContentValues.put(MenusTable.getSeventhColumnName(), cursor.getInt(2));
                        editContentValues.put(MenusTable.getEighthColumnName(), cursor.getInt(3));
                        String[] menusId = {String.valueOf(menuId)};
                        String whereClause = String.format("%s = ?", MenusTable.getFirstColumnName());
                        result = menuDataBase.update(MenusTable.getTableName(), editContentValues, whereClause, menusId) != -1;
                        menuDataBase.close();
                    }else result = false;



                    return result;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    if(dailyMenusActivity != null){
                        if(result){
                            dailyMenusActivity.updateCumulativeAmountOfKcalSuccess();
                        }
                        else dailyMenusActivity
                                .makeAStatement("An error occurred while an attempt to update " +
                                        "menus cumulative number of kcal", Toast.LENGTH_LONG);
                    }
                }
            }.execute();
        }
    }



    private void loadDailyMenusDetails() {

        if(dailyMenusActivity != null){
            new AsyncTask<Void, Void, Boolean>(){
                @Override
                protected Boolean doInBackground(Void... params) {

                    MenuDataBase menuDataBase = MenuDataBase.getInstance(dailyMenusActivity);

                    for (int i = 0; i < listOfDailyMenus.size(); i++) {
                        String query =  String.format("SELECT %s, %s, %s, %s, %s FROM %s " +
                                "WHERE %s = '%s';",
                                DailyMenusTable.getSecondColumnName(),
                                DailyMenusTable.getThirdColumnName(),
                                DailyMenusTable.getFourthColumnName(),
                                DailyMenusTable.getFifthColumnName(),
                                DailyMenusTable.getSixthColumnName(),
                                DailyMenusTable.getTableName(),
                                DailyMenusTable.getFirstColumnName(),
                                listOfDailyMenus.get(i).getDailyMenuId());

                        Cursor cursor = menuDataBase.downloadData(query);
                        if(cursor.getCount() > 0){
                            cursor.moveToPosition(-1);
                            while(cursor.moveToNext()){
                                listOfDailyMenus.get(i).setDailyMenuDate(cursor.getString(0));
                                listOfDailyMenus.get(i).setCumulativeNumberOfKcal(cursor.getInt(1));
                                listOfDailyMenus.get(i).setCumulativeAmountOfProteins(cursor.getInt(2));
                                listOfDailyMenus.get(i).setCumulativeAmountOfCarbons(cursor.getInt(3));
                                listOfDailyMenus.get(i).setCumulativeAmountOfFat(cursor.getInt(4));
                            }
                        }else{
                            menuDataBase.close();
                            return false;
                        }

                        String amountQuery = String.format("SELECT %s, %s FROM %s WHERE %s = '%s'",
                                MealsTypesDailyMenusAmountOfPeopleTable.getFirstColumnName(),
                                MealsTypesDailyMenusAmountOfPeopleTable.getThirdColumnName(),
                                MealsTypesDailyMenusAmountOfPeopleTable.getTableName(),
                                MealsTypesDailyMenusAmountOfPeopleTable.getSecondColumnName(),
                                listOfDailyMenus.get(i).getDailyMenuId());

                        Cursor amountCursor = menuDataBase.downloadData(amountQuery);
                        if(amountCursor.getCount() > 0){
                            amountCursor.moveToPosition(-1);
                            while (amountCursor.moveToNext()){
                                listOfDailyMenus.get(i).setAmountOfServings(amountCursor.getInt(1),
                                        amountCursor.getInt(0));
                            }
                        }else{
                            menuDataBase.close();
                            return false;
                        }
                    }
                    menuDataBase.close();
                    return true;
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
            }.execute();
        }
    }

    public void setMenuId(long menuId) {
        this.menuId = menuId;
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

        if(dailyMenusActivity != null){
            new AsyncTask<Void, Void, Boolean>(){

                @Override
                protected Boolean doInBackground(Void... params) {
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(dailyMenusActivity);
                    for (DailyMenu dailyMenu : listOfDailyMenus) {
                        dailyMenu.clearVectors();

                        //load types of meals and id of those meals (that belongs to current daily
                        //menu
                        String query = String.format("SELECT %s, %s FROM %s WHERE %s = '%s'",
                                MealsTypesMealsDailyMenusTable.getFirstColumnName(),
                                MealsTypesMealsDailyMenusTable.getSecondColumnName(),
                                MealsTypesMealsDailyMenusTable.getTableName(),
                                MealsTypesMealsDailyMenusTable.getThirdColumnName(),
                                dailyMenu.getDailyMenuId());

                        Cursor cursor = menuDataBase.downloadData(query);
                        if(cursor.getCount() > 0){
                            cursor.moveToPosition(-1);
                            while (cursor.moveToNext()){
                                //load name of current meal
                                String mealQuery = String.format("SELECT %s FROM %s WHERE %s = '%s'",
                                        MealsTable.getSecondColumnName(), MealsTable.getTableName(),
                                        MealsTable.getFirstColumnName(), cursor.getInt(1));

                                Cursor mealCursor = menuDataBase.downloadData(mealQuery);
                                if(mealCursor.getCount() == 1){
                                    mealCursor.moveToPosition(0);
                                    dailyMenu.addMeal(new Meal(cursor.getInt(1),
                                            mealCursor.getString(0)), cursor.getInt(0));
                                }else{
                                    Log.e(TAG, "Loading meals failed");
                                    menuDataBase.close();
                                    return false;
                                }
                            }
                        }else {
                            menuDataBase.close();
                            Log.e(TAG, "Something went wrong");
                            return false;
                        }
                    }

                    menuDataBase.close();
                    return true;
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
            }.execute();
        }
    }




    List<DailyMenu> getDailyMenus() {
        return listOfDailyMenus;
    }


}
