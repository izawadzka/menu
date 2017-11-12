package com.example.dell.menu.data.tables;

import android.content.ContentValues;

/**
 * Created by Dell on 27.10.2017.
 */

public class MealsTypesMealsDailyMenusTable {
    private final static String tableName = "MealsTypesMealsDailyMenus";
    private final static String firstColumnName = "mealsTypeId";
    private final static String secondColumnName = "mealsId";
    private final static String thirdColumnName = "dailyMenuId";


    public static ContentValues getContentValues(int mealsTypeId, int mealsId, long dailyMenuId){
        ContentValues contentValues = new ContentValues();
        contentValues.put(firstColumnName, mealsTypeId);
        contentValues.put(secondColumnName, mealsId);
        contentValues.put(thirdColumnName, dailyMenuId);
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

    public static String getThirdColumnName() {
        return thirdColumnName;
    }
}
