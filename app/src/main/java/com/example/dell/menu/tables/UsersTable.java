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
    private final static String fourthColumnName = "limitOfKcalPerDay";
    private final static String sixthColumnName = "limitOfProteinsPerDay";
    private final static String seventhColumnName = "limitOfCarbohydratesPerDay";
    private final static String eigththColumnName = "limitOfFatPerDay";

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

    public static String getFirstColumnName() {
        return firstColumnName;
    }
}
