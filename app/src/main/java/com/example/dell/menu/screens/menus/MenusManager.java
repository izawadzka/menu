package com.example.dell.menu.screens.menus;

import android.database.Cursor;
import android.os.AsyncTask;

import com.example.dell.menu.MenuDataBase;
import com.example.dell.menu.objects.Menu;
import com.example.dell.menu.tables.MenusTable;
import com.squareup.otto.Bus;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Dell on 04.06.2017.
 */

public class MenusManager {
    private final Bus bus;
    private MenusFragment menusFragment;

    public MenusManager(Bus bus) {
        this.bus = bus;
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

    class LoadMenus extends AsyncTask<Void, Void, List<Menu>>{

        @Override
        protected List<Menu> doInBackground(Void... params) {
            List<Menu> result = new ArrayList<>();
            MenuDataBase menuDataBase = MenuDataBase.getInstance(menusFragment.getActivity());
            String query = String.format("SELECT * FROM %s", MenusTable.getTableName());
            Cursor cursor = menuDataBase.downloadDatas(query);
            if(cursor.getCount() > 0){
                cursor.moveToPosition(-1);
                int menuId, cumulativeNumberOfKcal, authorsId;
                String creationDate;
                String name;
                while(cursor.moveToNext()){
                    menuId = cursor.getInt(0);
                    name = cursor.getString(1);
                    creationDate = cursor.getString(2);
                    cumulativeNumberOfKcal = cursor.getInt(3);
                    authorsId = cursor.getInt(4);
                    result.add(new Menu(name, creationDate, cumulativeNumberOfKcal, authorsId));
                    result.get(result.size()-1).setMenuId(menuId);
                }
            }
            menuDataBase.close();
            return result;
        }

        @Override
        protected void onPostExecute(List<Menu> result) {
            if(menusFragment != null){
                menusFragment.showMenus(result);
            }
        }
    }
}
