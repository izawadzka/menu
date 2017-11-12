package com.example.dell.menu.data.tables;

import android.content.ContentValues;

/**
 * Created by Dell on 27.10.2017.
 */

public class MealsTypesDailyMenusAmountOfPeopleTable {
    private final static String tableName = "MealsTypesDailyMenusAmountOfPeople";
    private final static String firstColumnName = "mealsTypeId";
    private final static String secondColumnName = "dailyMenuId";
    private final static String thirdColumnName = "amountOfPeople";

    public static ContentValues getContentValues(int mealsTypeId, Long dailyMenuId, int amountOfPeople){
        ContentValues contentValues = new ContentValues();
        contentValues.put(firstColumnName, mealsTypeId);
        contentValues.put(secondColumnName, dailyMenuId);
        contentValues.put(thirdColumnName, amountOfPeople);
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
