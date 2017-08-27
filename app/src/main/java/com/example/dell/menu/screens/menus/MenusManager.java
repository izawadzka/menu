package com.example.dell.menu.screens.menus;

import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.example.dell.menu.MenuDataBase;
import com.example.dell.menu.UserStorage;
import com.example.dell.menu.objects.Menu;
import com.example.dell.menu.tables.MenusTable;
import com.squareup.otto.Bus;

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

    public MenusManager(Bus bus, UserStorage userStorage) {
        this.bus = bus;
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
                    creationDate = new Date(cursor.getLong(2));
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
