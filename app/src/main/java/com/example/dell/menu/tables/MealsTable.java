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
    private final static String thirdColumnName = "cumulativeNumberOfKcal";
    private final static String fourthColumnName = "authorsId";
    private final static String fifthColumnName = "recipe";
    private final static String sixthColumnName = "cumulativeAmountOfProtein";
    private final static String seventhColumnName = "cumulativeAmountOfCarbohydrates";
    private final static String eighthColumnName = "cumulativeAmountOfFat";


    public static ContentValues getContentValues(Meal meal){
        ContentValues contentValues = new ContentValues();
        contentValues.put(secondColumnName, meal.getName());
        contentValues.put(thirdColumnName, meal.getCumulativeNumberOfKcal());
        contentValues.put(fourthColumnName, meal.getAuthorsId());
        contentValues.put(fifthColumnName, meal.getRecipe());
        contentValues.put(sixthColumnName, meal.getAmountOfProteinsPer100g());
        contentValues.put(seventhColumnName, meal.getAmountOfCarbosPer100g());
        contentValues.put(eighthColumnName, meal.getAmountOfFatPer100g());
        return contentValues;
    }

    public static String getSecondColumnName() {
        return secondColumnName;
    }

    public static String getThirdColumnName() {
        return thirdColumnName;
    }

    public static String getFifthColumnName() {
        return fifthColumnName;
    }

    public static String getTableName() {
        return tableName;
    }

    public static String getFirstColumnName() {
        return firstColumnName;
    }

    public static String getFourthColumnName() {
        return fourthColumnName;
    }

    public static String getSixthColumnName() {
        return sixthColumnName;
    }

    public static String getSeventhColumnName() {
        return seventhColumnName;
    }

    public static String getEighthColumnName() {
        return eighthColumnName;
    }
}
