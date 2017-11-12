package com.example.dell.menu.user.screens.login;

import android.database.Cursor;
import android.os.AsyncTask;

import com.example.dell.menu.data.MenuDataBase;
import com.example.dell.menu.data.tables.UsersTable;

/**
 * Created by Dell on 25.05.2017.
 */

public class LoginManager {
    protected LoginActivity loginActivity;

    public void onAttach(LoginActivity loginActivity){
        this.loginActivity = loginActivity;
    }

    public void onStop(){
        loginActivity = null;
    }

    public void login(String login, String password){
        new CheckIfDatasCorrect().execute(login, password);
    }


    class CheckIfDatasCorrect extends AsyncTask<String, Integer, Long>{

        @Override
        protected Long doInBackground(String... params) {
            if(loginActivity != null) {
                MenuDataBase menuDataBase = MenuDataBase.getInstance(loginActivity);
                long result;
                String query = String.format("SELECT * FROM %s WHERE %s = '%s' AND %s = '%s';", UsersTable.getTableName(), UsersTable.getSecondColumnName(), params[0], UsersTable.getThirdColumnName(), params[1]);
                Cursor cursor = menuDataBase.downloadData(query);
                if(cursor.getCount()  > 0){
                    cursor.moveToPosition(0);
                    result = cursor.getLong(0);
                }else result = -1;
                cursor.close();
                menuDataBase.close();
                return result;
            } return (long)-1;
        }

        @Override
        protected void onPostExecute(Long result) {
            if(loginActivity != null) {
                if (result > 0) {
                    loginActivity.loginSuccess(result);
                }else{
                    loginActivity.loginFailed();
                }
            }
        }
    }
}
