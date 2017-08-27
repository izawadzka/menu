package com.example.dell.menu.screens.shoppingLists;

import android.database.Cursor;
import android.os.AsyncTask;

import com.example.dell.menu.MenuDataBase;
import com.example.dell.menu.events.shoppingLists.GenerateShoppingListButtonClickedEvent;
import com.example.dell.menu.objects.Menu;
import com.example.dell.menu.objects.ShoppingList;
import com.example.dell.menu.tables.ShoppingListsMenusTable;
import com.example.dell.menu.tables.ShoppingListsTable;
import com.example.dell.menu.tables.UsersTable;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Dell on 07.06.2017.
 */

public class ShoppingListsManager {
    private final Bus bus;
    private ShoppingListsFragment shoppingListsFragment;
    private Menu currentMenu;
    private String shoppingListName;
    private long shoppingListAuthorsId;
    private boolean generateNewShoppingListEvent;

    public ShoppingListsManager(Bus bus) {
        this.bus = bus;
        bus.register(this);
        generateNewShoppingListEvent = false;
    }


    public boolean isGenerateNewShoppingListEvent() {
        return generateNewShoppingListEvent;
    }

    public void resetGenerateNewShoppingListEvent(){
        generateNewShoppingListEvent = false;
    }

    public void onAttach(ShoppingListsFragment shoppingListsFragment){
        this.shoppingListsFragment = shoppingListsFragment;
    }

    public void onStop(){
        this.shoppingListsFragment = null;
    }

    @Subscribe
    public void onGenerateNewShoppingList(GenerateShoppingListButtonClickedEvent event){
        currentMenu = event.menu;
        shoppingListAuthorsId = currentMenu.getAuthorsId();
        shoppingListName = currentMenu.getName() + "list";
        generateNewShoppingListEvent = true;
    }

    public void createShoppingList() {
        if(shoppingListsFragment != null){
            new CreateShoppingList().execute();
        }
    }

    private void addConnectionBetweenShoppingListAndMenu(Long shoppingListId) {
        if(shoppingListsFragment != null) {
            new ConnectShoppingListAndMenu().execute(shoppingListId);
        }
    }

    public void loadShoppingLists() {
        if(shoppingListsFragment != null){
            new LoadShoppingLists().execute();
        }
    }

    class LoadShoppingLists  extends AsyncTask<Void, Integer, List<ShoppingList>>{

        @Override
        protected List<ShoppingList> doInBackground(Void... params) {
            List<ShoppingList> shoppingLists = new ArrayList<>();
            MenuDataBase menuDataBase = MenuDataBase.getInstance(shoppingListsFragment.getActivity());
            String query = String.format("SELECT * FROM %s", ShoppingListsTable.getTableName());
            Cursor cursor = menuDataBase.downloadData(query);
            if(cursor.getCount() > 0){
                cursor.moveToPosition(-1);
                while (cursor.moveToNext()){
                    shoppingLists.add(new ShoppingList(cursor.getString(1),
                            new Date(cursor.getInt(2)),
                            cursor.getInt(3)));
                    shoppingLists.get(shoppingLists.size()-1).setShoppingListId(cursor.getInt(0));
                }
            }

            for (ShoppingList shoppingList : shoppingLists) {
                String authorsNameQuery = String.format("SELECT %s FROM %s WHERE %s = '%s'",
                        UsersTable.getSecondColumnName(), UsersTable.getTableName(),
                        UsersTable.getFirstColumnName(), shoppingList.getAuthorsId());
                Cursor authorsNameCursor = menuDataBase.downloadData(authorsNameQuery);
                if(authorsNameCursor.getCount() > 0){
                    authorsNameCursor.moveToPosition(-1);
                    while (authorsNameCursor.moveToNext()){
                        shoppingList.setAuthorsName(authorsNameCursor.getString(0));
                    }
                }
            }


            return  shoppingLists;
        }

        @Override
        protected void onPostExecute(List<ShoppingList> shoppingLists) {
            if(shoppingLists.size() > 0){
                if (shoppingListsFragment != null){
                    shoppingListsFragment.showShoppingLists(shoppingLists);
                } // TODO: 08.06.2017 else
            }
        }
    }

    class ConnectShoppingListAndMenu extends AsyncTask<Long, Integer, Long>{

        @Override
        protected Long doInBackground(Long... params) {
            MenuDataBase menuDataBase = MenuDataBase.getInstance(shoppingListsFragment.getActivity());
            long result = menuDataBase.insert(ShoppingListsMenusTable.getTableName(),
                    ShoppingListsMenusTable.getContentValues(params[0], currentMenu.getMenuId()));
            return result;
        }

        @Override
        protected void onPostExecute(Long result) {
            if(result > 0){
                resetGenerateNewShoppingListEvent();
                shoppingListsFragment.generateShoppingListSuccess();
            }else{
                shoppingListsFragment.showStatment("Error while trying to connect shopping list and menu");
            }
        }
    }

    class CreateShoppingList extends AsyncTask<Void, Integer, Long>{

        @Override
        protected Long doInBackground(Void... params) {
            MenuDataBase menuDataBase = MenuDataBase.getInstance(shoppingListsFragment.getContext());
            long result = menuDataBase.insert(ShoppingListsTable.getTableName(),
                    ShoppingListsTable.getContentValues(new ShoppingList(shoppingListName, new Date(), shoppingListAuthorsId)));
            menuDataBase.close();
            return result;
        }

        @Override
        protected void onPostExecute(Long result) {
            if(result != -1){
                addConnectionBetweenShoppingListAndMenu(result);
            }else{
             shoppingListsFragment.createShoppingListFailed();
            }
        }
    }
}
