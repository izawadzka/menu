package com.example.dell.menu.tables;

import android.content.ContentValues;

/**
 * Created by Dell on 05.06.2017.
 */

public class MenusDailyMenusTable {
    private final static String tableName = "MenusDailyMenus";
    private final static String firstColumnName = "menuId";
    private final static String secondColumnName = "dailyMenuId";

    public static ContentValues getContentValues(Long menuId, Long dailyMenuID){
        ContentValues contentValues = new ContentValues();
        contentValues.put(firstColumnName, menuId);
        contentValues.put(secondColumnName, dailyMenuID);
        return contentValues;
    }

    public static String getTableName() {
        return tableName;
    }

    public static String getFirstColumnName() {
        return firstColumnName;
    }

    public static String getSecondColumnName() {
        return secondColumnName;
    }
}
