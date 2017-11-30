package com.example.dell.menu.menuplanning.screens.menus.addOrEditMenu;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.dell.menu.data.backup.Backup;
import com.example.dell.menu.data.tables.ProductsOnShelvesTable;
import com.example.dell.menu.data.tables.ShelvesInVirtualFridgeTable;
import com.example.dell.menu.data.tables.VirtualFridgeTable;
import com.example.dell.menu.menuplanning.objects.Product;
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
import com.example.dell.menu.virtualfridge.flags.ProductFlags;
import com.example.dell.menu.virtualfridge.objects.ShelfInVirtualFridge;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.StringReader;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Dell on 03.08.2017.
 */

public class DailyMenusManager {
    private static final String TAG = "DailyMenusManager";
    public static final int RESULT_ERROR = -1;
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
                    String query = String.format("SELECT dailyMenuId FROM MenusDailyMenus WHERE " +
                            "menuId = '%s';", menuId);
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
                        createNewShelf(currentDailyMenu.getDailyMenuId(),
                                currentDailyMenu.getDate());
                        loadDailyMenus();
                    }else{
                        if(dailyMenusActivity != null)
                            dailyMenusActivity.addingDailyMenuFailed("An error occurred while an attempt to " +
                                "insert amount of serving", Toast.LENGTH_LONG);
                    }
                }
            }.execute();
        }
    }

    private void createNewShelf(final Long dailyMenuId, final String date) {
        if(dailyMenusActivity != null){
            new AsyncTask<Void, Void, Long>(){

                @Override
                protected Long doInBackground(Void... params) {
                    long result;
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(dailyMenusActivity);
                    result = menuDataBase.insert(ShelvesInVirtualFridgeTable.getTableName(),
                            ShelvesInVirtualFridgeTable
                                    .getContentValues(new ShelfInVirtualFridge(dailyMenuId)));
                    menuDataBase.close();
                    return result;
                }

                @Override
                protected void onPostExecute(Long shelfId) {
                    if(shelfId != -1){
                        addProductsToShelf(shelfId, dailyMenuId, date);
                    }else{
                        if(dailyMenusActivity != null)
                            dailyMenusActivity.makeAStatement("An error occurred while an attempt to" +
                                    " create a shelf for that daily menu", Toast.LENGTH_LONG);
                    }
                }
            }.execute();
        }
    }

    private void addProductsToShelf(final Long shelfId, final Long dailyMenuId, final String date) {
        if(dailyMenusActivity != null){
            new AsyncTask<Void, Void, Boolean>(){

                @Override
                protected Boolean doInBackground(Void... params) {
                    boolean result = true;
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(dailyMenusActivity);
                    List<Product> productsInDailyMenu = new ArrayList<Product>();
                    List<Product> productsInVirtualFridge = new ArrayList<Product>();

                    boolean createForPast = false;
                    try {
                        createForPast = dailyMenuForPast(date);
                    } catch (ParseException e) {
                        Log.e(TAG, e.getLocalizedMessage());
                    }

                    //load every product, that the daily menu is consisted of
                    String query = String.format("SELECT productId, quantity, mt.mealsTypeId, amountOfPeople \n" +
                            "FROM Meals_Products mp \n" +
                            "JOIN Products p ON mp.productId = p.productsId \n" +
                            "JOIN MealsTypesMealsDailyMenus mt ON mt.mealsId = mp.mealId \n" +
                            "JOIN MealsTypesDailyMenusAmountOfPeople am on am.dailyMenuId = mt.dailyMenuId \n" +
                            "and am.mealsTypeId = mt.mealsTypeId \n" +
                            "WHERE mp.mealId IN \n" +
                            "(SELECT mealsId FROM MealsTypesMealsDailyMenus \n" +
                            "WHERE dailyMenuId IN \n" +
                            "(select MenusDailyMenus.dailyMenuId \n" +
                            "FROM MenusDailyMenus \n" +
                            "WHERE MenusDailyMenus.menuId = '1')) \n" +
                            "AND mt.dailyMenuId = '%s';", dailyMenuId);

                    Cursor cursor = menuDataBase.downloadData(query);
                    if(cursor.getCount() > 0){
                        cursor.moveToPosition(-1);
                        while (cursor.moveToNext()){
                            int indx = wasAddedPreviously(cursor.getInt(0), productsInDailyMenu);
                            if(indx != -1){
                                productsInDailyMenu.get(indx).addQuantity(cursor.getDouble(1)
                                        *cursor.getInt(3));
                            }
                            else
                            {
                                if(!createForPast)
                                productsInDailyMenu.add(new Product(cursor.getInt(0),
                                        cursor.getDouble(1) * cursor.getInt(3),
                                        ProductFlags.NEED_TO_BE_BOUGHT_IND));
                                else productsInDailyMenu.add(new Product(cursor.getInt(0),
                                        cursor.getDouble(1) * cursor.getInt(3),
                                        ProductFlags.EATEN_INDX));
                            }
                        }



                        if(!createForPast) {
                            //load the content of the extra shelf
                            boolean wasSynchronizedWithTheFridge;
                            String fridgeQuery = String.format("SELECT %s, %s FROM %s WHERE %s IN " +
                                            "(SELECT %s FROM %s WHERE %s = '0')",
                                    ProductsOnShelvesTable.getFirstColumnName(),
                                    ProductsOnShelvesTable.getThirdColumnName(),
                                    ProductsOnShelvesTable.getTableName(),
                                    ProductsOnShelvesTable.getSecondColumnName(),
                                    ShelvesInVirtualFridgeTable.getFirstColumnName(),
                                    ShelvesInVirtualFridgeTable.getTableName(),
                                    ShelvesInVirtualFridgeTable.getSecondColumnName());

                            Log.i("man", fridgeQuery);

                            Cursor fridgeCursor = menuDataBase.downloadData(fridgeQuery);

                            if (fridgeCursor.getCount() > 0) {
                                fridgeCursor.moveToPosition(-1);
                                while (fridgeCursor.moveToNext()) {
                                    productsInVirtualFridge.add(new Product(fridgeCursor.getInt(0),
                                            fridgeCursor.getDouble(1), ProductFlags.BOUGHT_INDX));
                                }

                                //synchronize with fridge
                                wasSynchronizedWithTheFridge = compareContents(productsInDailyMenu,
                                        productsInVirtualFridge);


                            } else wasSynchronizedWithTheFridge = false;

                            if(wasSynchronizedWithTheFridge){
                                int extraShelfId;
                                String findExtraShelfIdQuery = String.format("SELECT %s FROM %s " +
                                                "WHERE %s  = %s",
                                        ShelvesInVirtualFridgeTable.getFirstColumnName(),
                                        ShelvesInVirtualFridgeTable.getTableName(),
                                        ShelvesInVirtualFridgeTable.getSecondColumnName(),0);

                                Cursor extraShelfIdCursor = menuDataBase
                                        .downloadData(findExtraShelfIdQuery);
                                if(extraShelfIdCursor.getCount() == 1){
                                    extraShelfIdCursor.moveToPosition(0);
                                    extraShelfId = extraShelfIdCursor.getInt(0);
                                }else extraShelfId = -1;

                                if(extraShelfId > 0) {
                                    String whereClause = String.format("%s = ?",
                                            ProductsOnShelvesTable.getSecondColumnName(),
                                            ShelvesInVirtualFridgeTable.getFirstColumnName(),
                                            ShelvesInVirtualFridgeTable.getTableName(),
                                            ShelvesInVirtualFridgeTable.getSecondColumnName());
                                    String[] whereArgs = {String.valueOf(extraShelfId)};
                                    menuDataBase.delete(ProductsOnShelvesTable.getTableName(), whereClause,
                                            whereArgs);

                                    for (Product product : productsInVirtualFridge) {
                                        if (product.getQuantity() > 0) {
                                            menuDataBase.insert(ProductsOnShelvesTable.getTableName(),
                                                    ProductsOnShelvesTable
                                                            .getContentValues(new Product(product.getProductId(),
                                                                            product.getQuantity(),
                                                                            product.getProductFlagId()),
                                                                    extraShelfId));
                                        }
                                    }
                                }else result = false;

                            }
                        }

                        //add products to shelf
                        for (Product product : productsInDailyMenu) {
                            if(menuDataBase.insert(ProductsOnShelvesTable.getTableName(),
                                    ProductsOnShelvesTable
                                            .getContentValues(new Product(product.getProductId(),
                                                    product.getQuantity(),
                                                    product.getProductFlagId()), shelfId)) == -1){
                                result = false;
                                break;
                            }
                        }

                        //update the extra shelf


                    }else result = false;




                    menuDataBase.close();
                    return result;
                }

                private int wasAddedPreviously(long productId, List<Product> products) {
                    for (int i = 0; i < products.size(); i++) {
                        if(products.get(i).getProductId() == productId) return i;
                    }

                    return -1;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    if(dailyMenusActivity != null){
                        if (result)
                            dailyMenusActivity
                                    .makeAStatement("Successfully created shelf for that daily meneu",
                                            Toast.LENGTH_SHORT);
                        else dailyMenusActivity.makeAStatement("An error occurred while an attempt to" +
                                " create a shelf for that daily menu", Toast.LENGTH_LONG);
                    }
                }
            }.execute();
        }
    }

    private boolean dailyMenuForPast(String dailyMenuDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sdf.parse(dailyMenuDate));
        Calendar today = Calendar.getInstance();

        int calendarYear = calendar.get(Calendar.YEAR);
        int calendarMonth = calendar.get(Calendar.MONTH)+1;
        int calendarDay = calendar.get(Calendar.DAY_OF_MONTH);

        int todayYear = today.get(Calendar.YEAR);
        int todayMonth = today.get(Calendar.MONTH)+1;
        int todayDay = today.get(Calendar.DAY_OF_MONTH);

        if(calendarYear < todayYear) return true;
        else if(calendarYear == todayYear){
            if(calendarMonth < todayMonth) return true;
            else if ( calendarMonth == todayMonth && calendarDay < todayDay) return true;
        }
        return false;
    }

    private boolean compareContents(List<Product> productsInDailyMenu,
                                 List<Product> productsInVirtualFridge) {
        List<Product> comparedProducts = new ArrayList<>();
        for (Product productInDailyMenu : productsInDailyMenu) {
            for (Product productInFridge : productsInVirtualFridge) {
                if(productInDailyMenu.getProductId() == productInFridge.getProductId()){
                    if(productInFridge.getQuantity() > 0) {
                        if (productInDailyMenu.getQuantity() <= productInFridge.getQuantity()) {
                            productInDailyMenu.setProductFlagId(ProductFlags.BOUGHT_INDX);
                            productInFridge
                                    .setQuantity(productInFridge.getQuantity()
                                            - productInDailyMenu.getQuantity());
                        } else {
                            productInDailyMenu
                                    .setQuantity(productInDailyMenu.getQuantity() -
                                            productInFridge.getQuantity());
                            comparedProducts.add(new Product(productInDailyMenu.getProductId(),
                                    productInFridge.getQuantity(), ProductFlags.BOUGHT_INDX));
                            productInFridge.setQuantity(0);
                        }
                    }
                }
            }

        }

        productsInDailyMenu.addAll(comparedProducts);


        return comparedProducts.size() > 0;
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
                        else dailyMenusActivity.updateCumulativeAmountOfKcalFailed();
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
                        String query =  String.format("SELECT %s, %s, %s, %s, %s, %s, %s FROM %s " +
                                "WHERE %s = '%s';",
                                DailyMenusTable.getSecondColumnName(),
                                DailyMenusTable.getThirdColumnName(),
                                DailyMenusTable.getFourthColumnName(),
                                DailyMenusTable.getFifthColumnName(),
                                DailyMenusTable.getSixthColumnName(),
                                DailyMenusTable.getSeventhColumnName(),
                                DailyMenusTable.getEighthColumnName(),
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
                                listOfDailyMenus.get(i).setAlreadyUsed(cursor.getInt(5) == 1);
                                listOfDailyMenus.get(i)
                                        .setAlreadySynchronizedWithVirtualFridge(cursor.getInt(6) == 1);
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


    public void setDailyMenuWasUsed(final int indexOfDailyMenu, boolean wasUsed) {
        if(dailyMenusActivity != null){
            new AsyncTask<Void, Void, Boolean>(){

                @Override
                protected Boolean doInBackground(Void... params) {
                    boolean result;
                    MenuDataBase menuDatabase = MenuDataBase.getInstance(dailyMenusActivity);
                    ContentValues editContentValues = new ContentValues();
                    editContentValues.put(DailyMenusTable.getSeventhColumnName(),
                            listOfDailyMenus.get(indexOfDailyMenu).isAlreadyUsed() ? 1 : 0);

                    String whereClause = String.format("%s = ?",
                            DailyMenusTable.getFirstColumnName());

                    String[] whereArgs = {String.valueOf(listOfDailyMenus.get(indexOfDailyMenu)
                            .getDailyMenuId())};

                    result = menuDatabase.update(DailyMenusTable.getTableName(),editContentValues,
                            whereClause, whereArgs) == 1;
                    menuDatabase.close();
                    return result;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    if(dailyMenusActivity != null){
                        dailyMenusActivity.setAdapter();
                        if(!result) dailyMenusActivity
                                .makeAStatement("Failed to mark daily menu as used",
                                        Toast.LENGTH_LONG);
                    }
                }
            }.execute();
        }
    }

    public void synchronizeMarkedDailyMenusWithVirtualFridge() {
        if(dailyMenusActivity != null){
            new AsyncTask<Void, Void, Integer>(){

                @Override
                protected Integer doInBackground(Void... params) {
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(dailyMenusActivity);
                    ArrayList<Product> products = new ArrayList<Product>();
                    int queryResult = RESULT_OK;
                    for (DailyMenu dailyMenu : listOfDailyMenus) {
                        if(dailyMenu.isAlreadyUsed() &&
                                !dailyMenu.isAlreadySynchronizedWithVirtualFridge()) {

                            String query = String.format("SELECT productId, quantity, " +
                                    "mt.mealsTypeId, amountOfPeople FROM Meals_Products mp JOIN " +
                                    "Products p ON mp.productId = p.productsId JOIN " +
                                    "MealsTypesMealsDailyMenus mt ON mt.mealsId = mp.mealId JOIN " +
                                    "MealsTypesDailyMenusAmountOfPeople am on " +
                                    "am.dailyMenuId = mt.dailyMenuId and am.mealsTypeId = mt.mealsTypeId " +
                                    "WHERE mp.mealId IN (SELECT mealsId FROM MealsTypesMealsDailyMenus " +
                                    "WHERE dailyMenuId = '%s');", dailyMenu.getDailyMenuId());

                            Cursor queryCursor = menuDataBase.downloadData(query);
                            if (queryCursor.getCount() > 0) {
                                queryCursor.moveToPosition(-1);
                                while (queryCursor.moveToNext()) {
                                    int indx = wasAddedPreviously(queryCursor.getInt(0), products);

                                    if (indx != -1)
                                        products.get(indx).addQuantity(queryCursor.getDouble(1)
                                                * queryCursor.getInt(3));
                                    else {
                                        products.add(new Product(queryCursor.getInt(0),
                                                queryCursor.getDouble(1) * queryCursor.getInt(3)));
                                    }
                                }
                            } else {
                                queryResult = RESULT_ERROR;
                                break;
                            }
                        }
                    }

                    if(queryResult == RESULT_OK) {
                        String whereClause = String.format("%s = ?",
                                VirtualFridgeTable.getFirstColumnName());
                        if (products.size() == 0) queryResult = RESULT_NO_DATA_DOWNLOADED;
                        else {
                            for (Product product : products) {

                                String[] whereArgs = {String.valueOf(product.getProductId())};
                                Cursor productCursor = menuDataBase
                                        .downloadData(String.format("SELECT %s FROM %s WHERE %s = " +
                                                        "'%s'", VirtualFridgeTable.getSecondColumnName(),
                                                VirtualFridgeTable.getTableName(),
                                                VirtualFridgeTable.getFirstColumnName(),
                                                product.getProductId()));

                                if (productCursor.getCount() == 1) {
                                    productCursor.moveToPosition(0);

                                    /*
                                    ContentValues editContentValues = new ContentValues();

                                    editContentValues.put(VirtualFridgeTable.getSecondColumnName(),
                                            productCursor.getDouble(0) - product.getQuantity() > 0 ?
                                                    productCursor.getDouble(0) - product.getQuantity() :
                                                    0);

                                    menuDataBase.update(VirtualFridgeTable.getTableName(),
                                            editContentValues, whereClause, whereArgs);*/

                                    if(productCursor.getDouble(0) > product.getQuantity()){
                                        ContentValues editContentValues = new ContentValues();

                                        editContentValues.put(VirtualFridgeTable.getSecondColumnName(),
                                                        productCursor.getDouble(0)
                                                                - product.getQuantity());

                                        menuDataBase.update(VirtualFridgeTable.getTableName(),
                                                editContentValues, whereClause, whereArgs);
                                    }else {
                                        menuDataBase.delete(VirtualFridgeTable.getTableName(),
                                                whereClause, whereArgs);
                                    }
                                }
                            }
                        }
                    }

                    menuDataBase.close();
                    return queryResult;
                }

                private int wasAddedPreviously(long productId, ArrayList<Product> products) {
                    for (int i = 0; i < products.size(); i++) {
                        if(products.get(i).getProductId() == productId) return i;
                    }

                    return -1;
                }

                @Override
                protected void onPostExecute(Integer result) {
                    if(result == RESULT_OK) setDailyMenuWasSynchronized();
                    else if(dailyMenusActivity != null){
                        if(result == RESULT_NO_DATA_DOWNLOADED)
                            dailyMenusActivity.makeAStatement("No products in common found in the " +
                                    "fridge", Toast.LENGTH_SHORT);
                        else if(result == RESULT_ERROR)
                        dailyMenusActivity.makeAStatement("An error occurred while an attempt to " +
                                "synchronize daily menus with fridge", Toast.LENGTH_LONG);
                    }
                }
            }.execute();
        }
    }

    private void setDailyMenuWasSynchronized() {
        if(dailyMenusActivity != null){
            new AsyncTask<Void, Void, Boolean>(){

                @Override
                protected Boolean doInBackground(Void... params) {
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(dailyMenusActivity);
                    boolean result = true;
                    String whereClause = String.format("%s = ?", DailyMenusTable.getFirstColumnName());
                    for (DailyMenu dailyMenu : listOfDailyMenus) {
                        if(dailyMenu.isAlreadyUsed()){
                            String[] whereArgs = {String.valueOf(dailyMenu.getDailyMenuId())};
                            dailyMenu.setAlreadySynchronizedWithVirtualFridge(true);

                            ContentValues editContentValues = new ContentValues();
                            editContentValues.put(DailyMenusTable.getEighthColumnName(),1);

                            if(menuDataBase.update(DailyMenusTable.getTableName(),editContentValues,
                                    whereClause, whereArgs) != 1){
                                result = false;
                                break;
                            }
                        }
                    }

                    menuDataBase.close();
                    return result;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    if(dailyMenusActivity != null){
                        if(result) dailyMenusActivity.setAdapter();
                        else dailyMenusActivity.makeAStatement("An error occurred while an attempt " +
                                "to synchronize daily menus with fridge", Toast.LENGTH_LONG);
                    }
                }
            }.execute();
        }
    }
}
