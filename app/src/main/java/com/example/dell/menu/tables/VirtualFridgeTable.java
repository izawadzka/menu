package com.example.dell.menu.tables;

import android.content.ContentValues;

/**
 * Created by Dell on 29.10.2017.
 */

public class VirtualFridgeTable {
    private final static String tableName = "VirtualFridge";
    private final static String firstColumnName = "productId";
    private final static String secondColumnName = "quantity";

    public static ContentValues getContentValues(int productId, double quantity){
        ContentValues contentValues = new ContentValues();
        contentValues.put(firstColumnName, productId);
        contentValues.put(secondColumnName, quantity);
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
}
