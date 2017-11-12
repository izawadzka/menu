package com.example.dell.menu.data.tables;

import android.content.ContentValues;

/**
 * Created by Dell on 05.06.2017.
 */

public class DailyMenusTable {
    private final static String tableName = "DailyMenus";
    private final static String firstColumnName = "dailyMenuId";
    private final static String secondColumnName = "date";
    private final static String thirdColumnName = "cumulativeNumberOfKcal";
    private final static String fourthColumnName = "cumulativeNumberOfProteins";
    private final static String fifthColumnName = "cumulativeNumberOfCarbohydrates";
    private final static String sixthColumnName = "cumulativeNumberOfFat";


    public static ContentValues getContentValues(String date, int cumulativeNumberOfKcal){
        ContentValues contentValues = new ContentValues();
        contentValues.put(secondColumnName, date);
        contentValues.put(thirdColumnName, cumulativeNumberOfKcal);
        return contentValues;
    }

    public static ContentValues getContentValues(String date, int cumulativeNumberOfKcal,
                                                 int cumulativeNumberOfProteins,
                                                 int cumulativeNumberOfCarbohydrates,
                                                 int cumulativeNumberOfFat){
        ContentValues contentValues = new ContentValues();
        contentValues.put(secondColumnName, date);
        contentValues.put(thirdColumnName, cumulativeNumberOfKcal);
        contentValues.put(fourthColumnName, cumulativeNumberOfProteins);
        contentValues.put(fifthColumnName, cumulativeNumberOfCarbohydrates);
        contentValues.put(sixthColumnName, cumulativeNumberOfFat);
        return contentValues;
    }


    public static String getFourthColumnName() {
        return fourthColumnName;
    }

    public static String getFifthColumnName() {
        return fifthColumnName;
    }

    public static String getSixthColumnName() {
        return sixthColumnName;
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
