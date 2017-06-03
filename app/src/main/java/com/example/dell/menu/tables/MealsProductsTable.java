package com.example.dell.menu.tables;

import android.content.ContentValues;

import com.example.dell.menu.objects.Meal;
import com.example.dell.menu.objects.Product;

/**
 * Created by Dell on 28.05.2017.
 */

public class MealsProductsTable {
    private final static String tableName = "Meals_Products";
    private final static String firstColumnName = "productId";
    private final static String secondColumnName = "mealId";
    private final static String thirdColumnName = "quantity";

    public static String create(){
        String createCommand = String.format("create table %s(", tableName)
                + String.format("%s INTEGER, ", firstColumnName)
                + String.format("%s INTEGER, ", secondColumnName)
                + String.format("%s DOUBLE);", thirdColumnName);
        return createCommand;
    }

    public static ContentValues getContentValues(int productId, Long mealId, double quantity){
        ContentValues contentValues = new ContentValues();
        contentValues.put(firstColumnName, productId);
        contentValues.put(secondColumnName, mealId);
        contentValues.put(thirdColumnName, quantity);
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
