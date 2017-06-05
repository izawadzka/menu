package com.example.dell.menu.tables.mealTypes;

import android.content.ContentValues;

/**
 * Created by Dell on 05.06.2017.
 */

public class TeatimeTable extends MealTypeBaseTable{
    private final static String tableName = "Teatime";

    public static String create(){
        String createCommand = String.format("create table %s(", tableName)
                + String.format("%s INTEGER, ", firstColumnName)
                + String.format("%s INTEGER); ", secondColumnName);
        return createCommand;
    }

    public static ContentValues getContentValues(Long dailyMenuId, int mealId){
        ContentValues contentValues = new ContentValues();
        contentValues.put(firstColumnName, dailyMenuId);
        contentValues.put(secondColumnName, mealId);
        return contentValues;
    }

    public static String getTableName() {
        return tableName;
    }
}
