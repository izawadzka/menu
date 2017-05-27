package com.example.dell.menu.screens.login;

import android.database.Cursor;

import com.example.dell.menu.MenuDataBase;
import com.example.dell.menu.tables.UsersTable;

/**
 * Created by Dell on 25.05.2017.
 */

public class LoginManager {
    private final MenuDataBase menuDataBase;
    private LoginActivity loginActivity;

    public void onAttach(LoginActivity loginActivity){
        this.loginActivity = loginActivity;
    }

    public LoginManager(MenuDataBase menuDataBase){
        this.menuDataBase = menuDataBase;
    }

    public void onStop(){
        loginActivity = null;
    }

    public void login(String login, String password){
        if(checkIfDatasCorrect(login, password)) {
            if(loginActivity!=null){
                loginActivity.loginSuccess();
            }
        }else{
            if(loginActivity != null) {
                loginActivity.loginFailed();
            }
        }
    }

    private boolean checkIfDatasCorrect(String login, String password) {
        String query = String.format("SELECT * FROM %s WHERE %s = '%s' AND %s = '%s';", UsersTable.getTableName(), UsersTable.getSecondColumnName(), login, UsersTable.getThirdColumnName(), password);
        Cursor cursor = menuDataBase.downloadDatas(query);
        return cursor.getCount() > 0;
    }
}
