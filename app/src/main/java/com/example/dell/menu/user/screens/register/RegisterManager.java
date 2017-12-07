package com.example.dell.menu.user.screens.register;

import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.example.dell.menu.App;
import com.example.dell.menu.data.MenuDataBase;
import com.example.dell.menu.data.backup.Backup;
import com.example.dell.menu.user.events.RegisterResultEvent;
import com.example.dell.menu.user.objects.User;
import com.example.dell.menu.data.tables.UsersTable;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * Created by Dell on 25.05.2017.
 */

public class RegisterManager {
    public static final int RESULT_USER_ALREADY_EXISTS = 1;
    public static final int RESULT_REGISTER_SUCCESS = 0;
    private final static String TAG = "RegisterManager";
    private final Bus bus;
    private RegisterActivity registerActivity;
    private Long id;

    public RegisterManager(Bus bus) {
        this.bus = bus;
        bus.register(this);
    }

    public void onAttach(RegisterActivity registerActivity){
        this.registerActivity = registerActivity;
    }


    public void onStop(){
        registerActivity = null;
    }

    @Subscribe
    public void onRegisterResultEvent(RegisterResultEvent event){
        if(registerActivity != null) {
            if (event.result == Backup.RESULT_ALREADY_EXISTS) {
                Log.i(TAG, "User already exists");
                registerActivity.userAlreadyExists();
            }
            else if (event.result == Backup.RESULT_FAILED_TO_CREATE_DIRECTORY) {
                Log.i(TAG, "Failed to create a directory");
                registerActivity.createDirectoryFailed();
            } else if (event.result == Backup.RESULT_USER_REGISTER) {
                Log.i(TAG, "User registered on server");
                addUserToDatabase(event.login, event.password);
            }
        }
    }

    private void addUserToDatabase(final String login, final String password) {
        if(registerActivity != null){
            new AsyncTask<Void, Void, Integer>(){

                private Long id;

                @Override
                protected Integer doInBackground(Void... params) {
                    int result;
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(registerActivity);

                    String checkIfUserExistsQuery = String.format("SELECT %s FROM %s WHERE %s = '%s'",
                            UsersTable.getFirstColumnName(), UsersTable.getTableName(),
                            UsersTable.getSecondColumnName(), login);
                    Cursor cursor = menuDataBase.downloadData(checkIfUserExistsQuery);

                    if (cursor.getCount() > 0) result = RESULT_USER_ALREADY_EXISTS;
                    else {

                        id = menuDataBase.insert(UsersTable.getTableName(), UsersTable.getContentValues(new User(login, password)));
                        if (id != -1) {
                            result = RESULT_REGISTER_SUCCESS;
                        } else result = -1;
                    }
                    menuDataBase.close();
                    return result;
                }

                @Override
                protected void onPostExecute(Integer result) {
                    if(registerActivity != null) {
                        if (result == RESULT_USER_ALREADY_EXISTS) registerActivity.userAlreadyExists();
                        else if(result == RESULT_REGISTER_SUCCESS){
                            ((App) registerActivity.getApplication()).getUserStorage().setUserId(id);
                            registerActivity.registerSuccess();
                            Backup backup = new Backup((App)registerActivity.getApplication());
                            backup.doBackup();
                        }else registerActivity.registerFailed("");
                    }
                }
            }.execute();
        }
    }

    public void register(String login, String password){
        if(registerActivity != null) {
            Backup backup = new Backup((App)registerActivity.getApplication());
            backup.registerNewUser(login, password);
        }
    }
}
