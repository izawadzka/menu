package com.example.dell.menu.screens.login;

import android.database.Cursor;
import android.os.AsyncTask;

import com.example.dell.menu.MenuDataBase;
import com.example.dell.menu.tables.UsersTable;

/**
 * Created by Dell on 25.05.2017.
 */

public class LoginManager {
    //private final MenuDataBase menuDataBase;
    protected LoginActivity loginActivity;

    public void onAttach(LoginActivity loginActivity){
        this.loginActivity = loginActivity;
    }

    //public LoginManager(MenuDataBase menuDataBase){
        //this.menuDataBase = menuDataBase;
    //}

    public void onStop(){
        loginActivity = null;
    }

    public void login(String login, String password){
        new CheckIfDatasCorrect().execute(login, password);
    }


    class CheckIfDatasCorrect extends AsyncTask<String, Integer, Integer>{

        @Override
        protected Integer doInBackground(String... params) {
            if(loginActivity != null) {
                MenuDataBase menuDataBase = MenuDataBase.getInstance(loginActivity);
                String query = String.format("SELECT * FROM %s WHERE %s = '%s' AND %s = '%s';", UsersTable.getTableName(), UsersTable.getSecondColumnName(), params[0], UsersTable.getThirdColumnName(), params[1]);
                Cursor cursor = menuDataBase.downloadData(query);
                int count = cursor.getCount();
                cursor.close();
                menuDataBase.close();
                return count;
            } return 0;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if(loginActivity != null) {
                if (integer > 0) {
                    loginActivity.loginSuccess();
                }else{
                    loginActivity.loginFailed();
                }
            }
        }
    }
}
