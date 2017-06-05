package com.example.dell.menu.tables.mealTypes;

import android.content.ContentValues;

/**
 * Created by Dell on 05.06.2017.
 */

public class MealTypeBaseTable {
    protected static String firstColumnName = "dailyMenuId";
    protected static String secondColumnName = "mealId";

    public static String getFirstColumnName() {
        return firstColumnName;
    }

    public static String getSecondColumnName() {
        return secondColumnName;
    }
}
