package com.example.dell.menu.tables;

import android.content.ContentValues;

import com.example.dell.menu.objects.Meal;

/**
 * Created by Dell on 27.05.2017.
 */

public class MealsTable {
    private final static String tableName = "Meals";
    private final static String firstColumnName = "mealsId";
    private final static String secondColumnName = "name";
    private final static String thirdColumnName = "cumulativeNumberOfKcalPer100g";
    private final static String fourthColumnName = "authorsId";
    private final static String fifthColumnName = "recipe";

    public static String create(){
        String createCommand = String.format("create table %s(", tableName)
                + String.format("%s INTEGER PRIMARY KEY, ", firstColumnName)
                + String.format("%s TEXT, ", secondColumnName)
                + String.format("%s INTEGER, ", thirdColumnName)
                + String.format("%s INTEGER, ", fourthColumnName)
                + String.format("%s TEXT);", fifthColumnName);

        return createCommand;
    }

    public static ContentValues getContentValues(Meal meal){
        ContentValues contentValues = new ContentValues();
        contentValues.put(secondColumnName, meal.getName());
        contentValues.put(thirdColumnName, meal.getCumulativeNumberOfKcalPer100g());
        contentValues.put(fourthColumnName, meal.getAuthorsId());
        contentValues.put(fifthColumnName, meal.getRecipe());
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
