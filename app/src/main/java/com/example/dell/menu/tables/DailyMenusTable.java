package com.example.dell.menu.tables;

import android.content.ContentValues;

import com.example.dell.menu.objects.DailyMenu;

import java.util.Date;

/**
 * Created by Dell on 05.06.2017.
 */

public class DailyMenusTable {
    private final static String tableName = "DailyMenus";
    private final static String firstColumnName = "dailyMenuId";
    private final static String secondColumnName = "date";
    private final static String thirdColumnName = "cumulativeNumberOfKcal";

    public static ContentValues getContentValues(String date, int cumulativeNumberOfKcal){
        ContentValues contentValues = new ContentValues();
        contentValues.put(secondColumnName, date);
        contentValues.put(thirdColumnName, cumulativeNumberOfKcal);
        return contentValues;
    }


    public static String create(){
        String createCommand = String.format("create table %s(", tableName)
                + String.format("%s INTEGER PRIMARY KEY, ", firstColumnName)
                + String.format("%s DATE, %s INT); ", secondColumnName, thirdColumnName);

        return createCommand;
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

    public static String getThirdColumnName() {
        return thirdColumnName;
    }
}
