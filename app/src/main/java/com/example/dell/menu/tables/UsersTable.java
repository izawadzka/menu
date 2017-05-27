package com.example.dell.menu.tables;


import android.content.ContentValues;

import com.example.dell.menu.objects.User;

/**
 * Created by Dell on 24.05.2017.
 */

public class UsersTable {
    private final static String tableName = "Users";
    private final static String firstColumnName = "usersId";
    private final static String secondColumnName = "login";
    private final static String thirdColumnName = "password";
    private final static String fourthColumnName = "lastActivity";

    public static String create(){
        String createCommand = String.format("create table %s(", tableName)
                + String.format("%s INTEGER PRIMARY KEY, ", firstColumnName)
                + String.format("%s TEXT, ", secondColumnName)
                + String.format("%s TEXT, ", thirdColumnName)
                + String.format("%s DATE);", fourthColumnName);

        return createCommand;
    }

    public static ContentValues getContentValues(User user){
        ContentValues contentValues = new ContentValues();
        contentValues.put(secondColumnName, user.getLogin());
        contentValues.put(thirdColumnName, user.getPassword());
        return contentValues;
    }

    public static String getSecondColumnName() {
        return secondColumnName;
    }

    public static String getThirdColumnName() {
        return thirdColumnName;
    }

    public static String getTableName() {
        return tableName;
    }
}
