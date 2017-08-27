package com.example.dell.menu.screens.menus;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.dell.menu.MenuDataBase;
import com.example.dell.menu.UserStorage;
import com.example.dell.menu.events.menus.DeleteMenuEvent;
import com.example.dell.menu.events.menus.EditMenuNameEvent;
import com.example.dell.menu.objects.Menu;
import com.example.dell.menu.tables.DailyMenusTable;
import com.example.dell.menu.tables.MenusDailyMenusTable;
import com.example.dell.menu.tables.MenusTable;
import com.example.dell.menu.tables.mealTypes.BreakfastTable;
import com.example.dell.menu.tables.mealTypes.DinnerTable;
import com.example.dell.menu.tables.mealTypes.LunchTable;
import com.example.dell.menu.tables.mealTypes.MealTypeBaseTable;
import com.example.dell.menu.tables.mealTypes.SupperTable;
import com.example.dell.menu.tables.mealTypes.TeatimeTable;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 04.06.2017.
 */

public class MenusManager {
    private final Bus bus;
    private final UserStorage userStorage;
    private MenusFragment menusFragment;
    private List<Menu> menusArrayList;
    private Menu menuToDelete;

    public MenusManager(Bus bus, UserStorage userStorage) {
        this.bus = bus;
        bus.register(this);
        this.userStorage = userStorage;
    }

    public void onAttach(MenusFragment menusFragment){
        this.menusFragment = menusFragment;
    }

    public void onStop(){
        this.menusFragment = null;
    }

    public void loadMenus() {
        if(menusFragment != null){
            new LoadMenus().execute();
        }
    }

    public void addNewMenu(String menuName) {
        if(menusFragment != null){
            new AddNewMenu().execute(menuName);
        }
    }


    @Subscribe
    public void onEditMenuNameImageButtonClicked(EditMenuNameEvent event){
        if(menusFragment != null){
            menusFragment.editMenuName(event.menu);
        }
    }


    @Subscribe
    public void onDeleteMenuImageButtonClicked(DeleteMenuEvent event){
        if(menusFragment != null){
            menuToDelete = event.menu;
            new DeleteDailyMenus().execute();
        }
    }

    class DeleteDailyMenus extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {
            MenuDataBase menuDataBase = MenuDataBase.getInstance(menusFragment.getContext());
            String[] menusId = {String.valueOf(menuToDelete.getMenuId())};



            //meals
            String[] tableNames = {BreakfastTable.getTableName(), LunchTable.getTableName(), DinnerTable.getTableName(),
                    TeatimeTable.getTableName(), SupperTable.getTableName()};

            for (String tableName : tableNames) {
                try {
                    menuDataBase.delete(tableName, String.format("%s IN (SELECT %s FROM %s WHERE %s = ?)",
                            MealTypeBaseTable.getFirstColumnName(), MenusDailyMenusTable.getSecondColumnName(), MenusDailyMenusTable.getTableName(),
                            MenusDailyMenusTable.getFirstColumnName()), menusId);
                }catch (Exception e){
                    menuDataBase.close();
                    return false;
                }
            }

            //daily menus from dailyMenusTable
            try {
                menuDataBase.delete(DailyMenusTable.getTableName(), String.format("%s IN (SELECT %s FROM %s WHERE %s = ?)",
                        DailyMenusTable.getFirstColumnName(), MenusDailyMenusTable.getSecondColumnName(),
                        MenusDailyMenusTable.getTableName(), MenusDailyMenusTable.getFirstColumnName()), menusId);
            }catch (Exception e){
                menuDataBase.close();
                return false;
            }

            //daily menus from MenusDailyMenusTable
            try {
                menuDataBase.delete(MenusDailyMenusTable.getTableName(), String.format("%s = ?", MenusDailyMenusTable.getFirstColumnName()),menusId);
            }catch (Exception e){
                menuDataBase.close();
                return false;
            }

            //menu from menusTable
            try {
                menuDataBase.delete(MenusTable.getTableName(), String.format("%s = ?", MenusTable.getFirstColumnName()), menusId);
            }catch (Exception e){
                return false;
            }

            menuDataBase.close();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(menusFragment != null){
                if(result){
                    menusFragment.makeAStatement("Successfully deleted menu", Toast.LENGTH_SHORT);
                    loadMenus();
                }else{
                    menusFragment.makeAStatement("An error occurred while an attempt to delete menu", Toast.LENGTH_LONG);
                }
            }
        }
    }

    public void editMenuName(Menu menu) {
        if(menusFragment != null){
            new UpdateMenuName().execute(menu);
        }
    }

    class UpdateMenuName extends AsyncTask<Menu, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Menu... params) {
            boolean result;
            MenuDataBase menuDataBase = MenuDataBase.getInstance(menusFragment.getContext());
            ContentValues editContentValues = new ContentValues();
            editContentValues.put(MenusTable.getSecondColumnName(), params[0].getName());
            String[] menusId = {String.valueOf(params[0].getMenuId())};
            String whereClause = String.format("%s = ?", MenusTable.getFirstColumnName());
            if(menuDataBase.update(MenusTable.getTableName(), editContentValues, whereClause, menusId) != -1) result = true;
            else  result = false;

            menuDataBase.close();

            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(menusFragment != null) {
                if (result) {
                    menusFragment.editMenusNameSuccess();
                }else{
                    menusFragment.editMenusNameFailed();
                }
            }
        }
    }

    class AddNewMenu extends AsyncTask<String, Void, Long>{

        @Override
        protected Long doInBackground(String... params) {
            long menuId;
            MenuDataBase menuDataBase = MenuDataBase.getInstance(menusFragment.getContext());
            java.sql.Date sqlDate = new java.sql.Date(System.currentTimeMillis());
            menuId = menuDataBase.insert(MenusTable.getTableName(), MenusTable.getContentValues(new Menu(params[0], sqlDate,0, userStorage.getUserId())));
            menuDataBase.close();
            return menuId;
        }

        @Override
        protected void onPostExecute(Long menuId) {
            if (menusFragment != null){
                if(menuId != -1){
                    menusFragment.addNewMenuSuccess(menuId);
                }
                else menusFragment.addNewMenuFailed();
            }
        }
    }

    class LoadMenus extends AsyncTask<Void, Void, List<Menu>>{

        @Override
        protected List<Menu> doInBackground(Void... params) {
            menusArrayList = new ArrayList<>();
            MenuDataBase menuDataBase = MenuDataBase.getInstance(menusFragment.getActivity());
            String query = String.format("SELECT * FROM %s", MenusTable.getTableName());
            Cursor cursor = menuDataBase.downloadData(query);
            if(cursor.getCount() > 0){
                cursor.moveToPosition(-1);
                int menuId, cumulativeNumberOfKcal, authorsId;
                Date creationDate;
                String name;
                while(cursor.moveToNext()){
                    menuId = cursor.getInt(0);
                    name = cursor.getString(1);
                    creationDate = Date.valueOf(cursor.getString(2));
                    cumulativeNumberOfKcal = cursor.getInt(3);
                    authorsId = cursor.getInt(4);
                    menusArrayList.add(new Menu(name, creationDate, cumulativeNumberOfKcal, authorsId));
                    menusArrayList.get(menusArrayList.size()-1).setMenuId(menuId);
                }
            }
            menuDataBase.close();
            return menusArrayList;
        }

        @Override
        protected void onPostExecute(List<Menu> result) {
            if(menusFragment != null){
                menusFragment.showMenus(result);
            }
        }
    }
}
