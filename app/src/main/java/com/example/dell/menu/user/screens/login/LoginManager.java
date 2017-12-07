package com.example.dell.menu.user.screens.login;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.dell.menu.App;
import com.example.dell.menu.data.MenuDataBase;
import com.example.dell.menu.data.backup.Backup;
import com.example.dell.menu.data.backup.events.RestoringBackupResultEvent;
import com.example.dell.menu.data.backup.events.SearchingForUserResultEvent;
import com.example.dell.menu.data.tables.UsersTable;
import com.example.dell.menu.internetconnection.InternetConnection;
import com.example.dell.menu.user.objects.User;
import com.example.dell.menu.virtualfridge.ShelvesArchive;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.Vector;

/**
 * Created by Dell on 25.05.2017.
 */

public class LoginManager {
    private LoginActivity loginActivity;
    private final Bus bus;
    private String login, password;

    public LoginManager(Bus bus) {
        this.bus = bus;
    }

    @Subscribe
    public void onSearchingForUserResultEvent(SearchingForUserResultEvent event){
        if(loginActivity != null){
            if(event.result){
                loginActivity.makeAStatement("Restoring the last backup in progress", Toast.LENGTH_LONG);
                this.login = event.login;
                this.password = event.password;
                Backup backup = new Backup((App)loginActivity.getApplication());
                backup.restoreBackup(MenuDataBase.DATABASE_NAME, event.login, event.password);
            }
            else loginActivity.loginFailed();
        }
    }

    @Subscribe
    public void onRestoringBackupResultEvent(RestoringBackupResultEvent event){
        if(event.success) login(login, password);
        else if(loginActivity != null) loginActivity.loginFailed();
    }



    public void onAttach(LoginActivity loginActivity){
        this.loginActivity = loginActivity;
        bus.register(this);
    }

    public void onStop(){
        loginActivity = null;
        bus.unregister(this);
    }

    public void login(final String login, final String password){
        if(loginActivity != null){
            new AsyncTask<Void, Void, Long>(){

                @Override
                protected Long doInBackground(Void... params) {
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(loginActivity);
                    long result;
                    String query = String.format("SELECT * FROM %s WHERE %s = '%s' AND %s = '%s';",
                            UsersTable.getTableName(), UsersTable.getSecondColumnName(), login,
                            UsersTable.getThirdColumnName(), password);
                    Cursor cursor = menuDataBase.downloadData(query);
                    if(cursor.getCount()  > 0){
                        cursor.moveToPosition(0);
                        result = cursor.getLong(0);
                    }else result = -1;
                    cursor.close();
                    menuDataBase.close();
                    return result;
                }

                @Override
                protected void onPostExecute(Long result) {
                    if (result > 0){
                        if(loginActivity != null){
                            loginActivity.loginSuccess(result);
                            ShelvesArchive shelvesArchive = new ShelvesArchive(loginActivity);
                            shelvesArchive.manageArchive();
                        }
                    }else checkIfAccountExists(login, password);
                }
            }.execute();
        }
    }

    private void checkIfAccountExists(String login, String password) {
        if(loginActivity != null){
            Backup backup = new Backup((App)loginActivity.getApplication());
            backup.findUser(login, password);
        }
    }
}
