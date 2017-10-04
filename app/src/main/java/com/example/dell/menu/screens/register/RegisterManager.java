package com.example.dell.menu.screens.register;

import android.database.Cursor;
import android.util.Log;

import com.example.dell.menu.App;
import com.example.dell.menu.MenuDataBase;
import com.example.dell.menu.objects.User;
import com.example.dell.menu.tables.UsersTable;

/**
 * Created by Dell on 25.05.2017.
 */

public class RegisterManager {
    //private final MenuDataBase menuDataBase;
    private RegisterActivity registerActivity;

    public void onAttach(RegisterActivity registerActivity){
        this.registerActivity = registerActivity;
    }


    public void onStop(){
        registerActivity = null;
    }

    public void register(String login, String password){
        if(registerActivity != null) {
            MenuDataBase menuDataBase = MenuDataBase.getInstance(registerActivity);

            String checkIfUserExistsQuery = String.format("SELECT %s FROM %s WHERE %s = '%s'",
                    UsersTable.getFirstColumnName(), UsersTable.getTableName(),
                    UsersTable.getSecondColumnName(), login);
            Cursor cursor = menuDataBase.downloadData(checkIfUserExistsQuery);
            if (cursor.getCount() > 0) registerActivity.registerFailed("User already exists");
            else {

                Long id = menuDataBase.insert(UsersTable.getTableName(), UsersTable.getContentValues(new User(login, password)));
                if (id != -1) {
                    if (registerActivity != null) {
                        ((App) registerActivity.getApplication()).getUserStorage().setUserId(id);
                        registerActivity.registerSuccess();
                    }
                } else {
                    if (registerActivity != null) {
                        registerActivity.registerFailed("");
                    }
                }
            }
            menuDataBase.close();
        }
    }
}
