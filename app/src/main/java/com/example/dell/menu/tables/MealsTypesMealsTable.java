package com.example.dell.menu.tables;

import android.content.ContentValues;

/**
 * Created by Dell on 09.10.2017.
 */

public class MealsTypesMealsTable {
    private final static String tableName = "MealsTypesMeals";
    private final static String firstColumnName = "mealsTypeId";
    private final static String secondColumnName = "mealsId";

    public static String getTableName() {
        return tableName;
    }

    public static String getFirstColumnName() {
        return firstColumnName;
    }

    public static String getSecondColumnName() {
        return secondColumnName;
    }

    public static ContentValues getContentValues(int mealsTypeId, long mealId){
        ContentValues contentValues = new ContentValues();
        contentValues.put(firstColumnName, mealsTypeId);
        contentValues.put(secondColumnName, mealId);
        return contentValues;
    }
}
