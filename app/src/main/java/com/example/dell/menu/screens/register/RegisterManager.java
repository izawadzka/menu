package com.example.dell.menu.screens.register;

import com.example.dell.menu.MenuDataBase;
import com.example.dell.menu.objects.User;
import com.example.dell.menu.tables.UsersTable;

/**
 * Created by Dell on 25.05.2017.
 */

public class RegisterManager {
    private final MenuDataBase menuDataBase;
    private RegisterActivity registerActivity;

    public void onAttach(RegisterActivity registerActivity){
        this.registerActivity = registerActivity;
    }

    public RegisterManager(MenuDataBase menuDataBase){
        this.menuDataBase = menuDataBase;
    }

    public void onStop(){
        registerActivity = null;
    }

    public void register(String login, String password){
        if(menuDataBase.insert(UsersTable.getTableName(), UsersTable.getContentValues(new User(login, password))) != -1){
        //if(menuDataBase.createUser(new User(login, password)) != -1){
            if(registerActivity != null){
                registerActivity.registerSuccess();
            }
        }else{
            if(registerActivity != null){
                registerActivity.registerFailed();
            }
        }
    }
}
