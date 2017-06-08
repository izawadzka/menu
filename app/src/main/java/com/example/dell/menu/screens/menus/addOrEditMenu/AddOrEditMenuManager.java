package com.example.dell.menu.screens.menus.addOrEditMenu;

import android.database.Cursor;
import android.os.AsyncTask;

import com.example.dell.menu.App;
import com.example.dell.menu.MenuDataBase;
import com.example.dell.menu.events.menus.AddNewDailyMenuEvent;
import com.example.dell.menu.events.menus.DeleteDailyMenuEvent;
import com.example.dell.menu.events.menus.ShowDailyMenuEvent;
import com.example.dell.menu.objects.DailyMenu;
import com.example.dell.menu.objects.Meal;
import com.example.dell.menu.objects.Menu;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * Created by Dell on 05.06.2017.
 */

public class AddOrEditMenuManager {
    private AddOrEditMenuActivity addOrEditMenuActivity;
    private List<DailyMenu> listOfDailyMenus = new ArrayList<>();
    private final Bus bus;
    private String menusName;
    private int menuId;
    private boolean show_mode = false;

    public AddOrEditMenuManager(Bus bus) {
        this.bus = bus;
        bus.register(this);
    }

    public boolean isShow_mode() {
        return show_mode;
    }

    public void setShow_mode(boolean show_mode) {
        this.show_mode = show_mode;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public void onAttach(AddOrEditMenuActivity addOrEditMenuActivity){
        this.addOrEditMenuActivity = addOrEditMenuActivity;
    }

    public void onStop(){
        this.addOrEditMenuActivity = null;
    }

    public List<DailyMenu> getListOfDailyMenus() {
        return listOfDailyMenus;
    }

    @Subscribe
    public void onAddNewDailyMenu(AddNewDailyMenuEvent event){
        listOfDailyMenus.add(event.dailyMenu);
    }

    @Subscribe
    public void onDeleteDailyMenu(DeleteDailyMenuEvent event){
        listOfDailyMenus.remove(event.position);
        if(addOrEditMenuActivity != null){
            addOrEditMenuActivity.dailyMenuDeleted(event.position);
        }
    }


    public String getMenusName() {
        return menusName;
    }

    public void setMenusName(String menusName) {
        this.menusName = menusName;
    }

    public void showDailyMenu(DailyMenu dailyMenu) {
        if(isShow_mode()){
            loadDailyMenusMeal(dailyMenu);
        }else {
            bus.post(new ShowDailyMenuEvent(dailyMenu, isShow_mode()));
        }
    }

    private void saveDailyMenus(Long result) {
        if(addOrEditMenuActivity != null) {
            new SaveDailyMenu().execute(result);
        }
    }

    public void saveMenu() {
        if(addOrEditMenuActivity != null){
            new SaveMenu().execute();
        }
    }


    public void clearListOfDailyMenu() {
        listOfDailyMenus.clear();
    }

    public void loadMenuName() {
        if(addOrEditMenuActivity != null){
            new LoadMenuName().execute();
        }
    }

    private void loadDailyMenus() {
        if(addOrEditMenuActivity != null){
            new LoadDailyMenus().execute();
        }
    }

    private void loadDailyMenusDates(Vector<Integer> dailyMenusId) {
        if(addOrEditMenuActivity != null){
            new LoadDailyMenusDates().execute(dailyMenusId);
        }
    }

    private void loadDailyMenusMeal(DailyMenu dailyMenu) {
        if(addOrEditMenuActivity != null) {
            new LoadDailyMenusMeals().execute(dailyMenu);
        }
    }


    class LoadDailyMenusMeals extends AsyncTask<DailyMenu, Integer, DailyMenu>{

        @Override
        protected DailyMenu doInBackground(DailyMenu... params) {
            MenuDataBase menuDataBase = MenuDataBase.getInstance(addOrEditMenuActivity);

            String breakfastQuery = String.format("SELECT %s, %s FROM %s bf JOIN  %s me ON bf.%s = me.%s WHERE bf.%s = '%s';",
                    MealsTable.getFirstColumnName(), MealsTable.getSecondColumnName(),
                    BreakfastTable.getTableName(), MealsTable.getTableName(),
                    BreakfastTable.getSecondColumnName(), MealsTable.getFirstColumnName(),
                    BreakfastTable.getFirstColumnName(), params[0].getDailyMenuId());
            Cursor breakfastCursor = menuDataBase.downloadDatas(breakfastQuery);
            if(breakfastCursor.getCount() > 0){
               breakfastCursor.moveToPosition(-1);
                while (breakfastCursor.moveToNext()){
                    params[0].addMeal(new Meal(breakfastCursor.getString(1)), DailyMenu.BREAKFAST_KEY);
                }
            }


            String lunchQuery = String.format("SELECT %s, %s FROM %s bf JOIN  %s me ON bf.%s = me.%s WHERE bf.%s = '%s';",
                    MealsTable.getFirstColumnName(), MealsTable.getSecondColumnName(),
                    LunchTable.getTableName(), MealsTable.getTableName(),
                    LunchTable.getSecondColumnName(), MealsTable.getFirstColumnName(),
                    LunchTable.getFirstColumnName(), params[0].getDailyMenuId());
            Cursor lunchCursor = menuDataBase.downloadDatas(lunchQuery);
            if(lunchCursor.getCount() > 0){
                lunchCursor.moveToPosition(-1);
                while (lunchCursor.moveToNext()){
                    params[0].addMeal(new Meal(lunchCursor.getString(1)), DailyMenu.LUNCH_KEY);
                }
            }

            String dinnerQuery = String.format("SELECT %s, %s FROM %s bf JOIN  %s me ON bf.%s = me.%s WHERE bf.%s = '%s';",
                    MealsTable.getFirstColumnName(), MealsTable.getSecondColumnName(),
                    DinnerTable.getTableName(), MealsTable.getTableName(),
                   DinnerTable.getSecondColumnName(), MealsTable.getFirstColumnName(),
                    DinnerTable.getFirstColumnName(), params[0].getDailyMenuId());
            Cursor dinnerCursor = menuDataBase.downloadDatas(dinnerQuery);
            if(dinnerCursor.getCount() > 0){
                dinnerCursor.moveToPosition(-1);
                while (dinnerCursor.moveToNext()){
                    params[0].addMeal(new Meal(dinnerCursor.getString(1)), DailyMenu.DINNER_KEY);
                }
            }

            String teatimeQuery = String.format("SELECT %s, %s FROM %s bf JOIN  %s me ON bf.%s = me.%s WHERE bf.%s = '%s';",
                    MealsTable.getFirstColumnName(), MealsTable.getSecondColumnName(),
                    TeatimeTable.getTableName(), MealsTable.getTableName(),
                    TeatimeTable.getSecondColumnName(), MealsTable.getFirstColumnName(),
                    TeatimeTable.getFirstColumnName(), params[0].getDailyMenuId());
            Cursor teatimeCursor = menuDataBase.downloadDatas(teatimeQuery);
            if(teatimeCursor.getCount() > 0){
                teatimeCursor.moveToPosition(-1);
                while (teatimeCursor.moveToNext()){
                    params[0].addMeal(new Meal(teatimeCursor.getString(1)), DailyMenu.TEATIME_KEY);
                }
            }

            String supperQuery = String.format("SELECT %s, %s FROM %s bf JOIN  %s me ON bf.%s = me.%s WHERE bf.%s = '%s';",
                    MealsTable.getFirstColumnName(), MealsTable.getSecondColumnName(),
                    SupperTable.getTableName(), MealsTable.getTableName(),
                    SupperTable.getSecondColumnName(), MealsTable.getFirstColumnName(),
                    SupperTable.getFirstColumnName(), params[0].getDailyMenuId());
            Cursor supperCursor = menuDataBase.downloadDatas(supperQuery);
            if(supperCursor.getCount() > 0){
                supperCursor.moveToPosition(-1);
                while (supperCursor.moveToNext()){
                    params[0].addMeal(new Meal(supperCursor.getString(1)), DailyMenu.SUPPER_KEY);
                }
            }

            menuDataBase.close();
            return params[0];
        }

        @Override
        protected void onPostExecute(DailyMenu dailyMenu) {
            bus.post(new ShowDailyMenuEvent(dailyMenu, isShow_mode()));
        }
    }

    class LoadDailyMenusDates extends AsyncTask<Vector<Integer>, Integer, Integer>{

        @Override
        protected Integer doInBackground(Vector<Integer>... params) {
            MenuDataBase menuDataBase = MenuDataBase.getInstance(addOrEditMenuActivity);
            for(Integer dailyMenuId : params[0]) {
                String dailyMenusDatesQuery = String.format("SELECT %s FROM %s WHERE %s = '%s'",
                        DailyMenusTable.getSecondColumnName(),
                        DailyMenusTable.getTableName(),
                        DailyMenusTable.getFirstColumnName(),
                        dailyMenuId);

                Cursor datesCursor = menuDataBase.downloadDatas(dailyMenusDatesQuery);
                if(datesCursor.getCount() > 0){
                    datesCursor.moveToPosition(-1);
                    while (datesCursor.moveToNext()){
                        listOfDailyMenus.add(new DailyMenu(Long.valueOf(dailyMenuId), new Date(datesCursor.getInt(0))));
                    }
                }else {
                    return -1;
                }
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if(result == 0){
                addOrEditMenuActivity.setDailyMenus();
            }else{
                addOrEditMenuActivity.showStatement("Error while loading dates");
            }
        }
    }

    class LoadDailyMenus extends AsyncTask<Void, Integer, Vector<Integer>>{

        @Override
        protected Vector<Integer> doInBackground(Void... params) {
            Vector<Integer> dailyMenusId = new Vector<>();
            MenuDataBase menuDataBase = MenuDataBase.getInstance(addOrEditMenuActivity);
            String dailyMenuIdQuery = String.format("SELECT %s FROM %s WHERE %s = '%s'",
                    MenusDailyMenusTable.getSecondColumnName(),
                    MenusDailyMenusTable.getTableName(),
                    MenusDailyMenusTable.getFirstColumnName(),
                    menuId);
            Cursor dailyMenusIdCursor = menuDataBase.downloadDatas(dailyMenuIdQuery);
            if(dailyMenusIdCursor.getCount() > 0){
                dailyMenusIdCursor.moveToPosition(-1);
                while (dailyMenusIdCursor.moveToNext()){
                    dailyMenusId.add(dailyMenusIdCursor.getInt(0));
                }
            }
            menuDataBase.close();
            return dailyMenusId;
        }

        @Override
        protected void onPostExecute(Vector<Integer> dailyMenusId) {
            if(dailyMenusId.size() > 0){
                loadDailyMenusDates(dailyMenusId);
            }else{
                addOrEditMenuActivity.showStatement("Error while trying to load daily menus");
            }
        }
    }

    class LoadMenuName extends AsyncTask<Void, Integer, String>{

        @Override
        protected String doInBackground(Void... params) {
            String menuName = "";
            MenuDataBase menuDataBase = MenuDataBase.getInstance(addOrEditMenuActivity);
            String query = String.format("SELECT %s FROM %s WHERE %s = '%s'",
                    MenusTable.getSecondColumnName(),
                    MenusTable.getTableName(),
                    MenusTable.getFirstColumnName(),
                    menuId);
            Cursor cursor = menuDataBase.downloadDatas(query);
            if(cursor.getCount() == 1){
                cursor.moveToPosition(-1);
                while (cursor.moveToNext()){
                    menuName = cursor.getString(0);
                }
            }
            menuDataBase.close();
            return menuName;
        }

        @Override
        protected void onPostExecute(String loadedMenuName) {
            if(loadedMenuName.equals("")){
                addOrEditMenuActivity.loadingMenuNameFailed();
            }else{
                setMenusName(loadedMenuName);
                addOrEditMenuActivity.getState();
                addOrEditMenuActivity.showStatement("Started loading daily menus");
                loadDailyMenus();
            }
        }
    }

    class SaveDailyMenu extends AsyncTask<Long, Integer, Integer>{

        private MenuDataBase menuDataBase;

        @Override
        protected Integer doInBackground(Long... params) {
            menuDataBase = MenuDataBase.getInstance(addOrEditMenuActivity);

            for (DailyMenu dailyMenu : listOfDailyMenus) {
                long dailyMenuId = menuDataBase.insert(DailyMenusTable.getTableName(),
                        DailyMenusTable.getContentValues(dailyMenu.getDate()));

                if(dailyMenuId != -1){
                    if(addDailyMenu(params[0], dailyMenuId) == -1){
                        menuDataBase.close();
                        return -1;
                    }
                    if(!addDailyMenuMeals(dailyMenuId, dailyMenu)){
                        return -1;
                    }
                }else{
                    menuDataBase.close();
                    return -1;
                }
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if(result == -1){
                addOrEditMenuActivity.saveMenuFailed();
            }else{
                addOrEditMenuActivity.saveSuccess();
            }
        }

        private boolean addDailyMenuMeals(long dailyMenuId, DailyMenu dailyMenu) {
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

        private Long addDailyMenu(Long menuId, long dailyMenuId) {
            return menuDataBase.insert(MenusDailyMenusTable.getTableName(),
                    MenusDailyMenusTable.getContentValues(menuId, dailyMenuId));
        }
    }

    class SaveMenu extends AsyncTask<Void, Integer, Long>{

        @Override
        protected Long doInBackground(Void... params) {
            int cumulativeNumberOfKcal = 0;
            for (DailyMenu listOfDailyMenu : listOfDailyMenus) {
                cumulativeNumberOfKcal += listOfDailyMenu.getCumulativeNumberOfKcal();
            }
            MenuDataBase menuDataBase = MenuDataBase.getInstance(addOrEditMenuActivity);
            long result = menuDataBase.insert(MenusTable.getTableName(),
                    MenusTable.getContentValues(new Menu(menusName, new Date(),
                            cumulativeNumberOfKcal,
                            ((App)addOrEditMenuActivity.getApplication()).getUserStorage().getUserId())));
            menuDataBase.close();
            return result;
        }

        @Override
        protected void onPostExecute(Long result) {
            if(result != -1){
                saveDailyMenus(result);
            }else{
                addOrEditMenuActivity.saveMenuFailed();
            }
        }
    }
}
