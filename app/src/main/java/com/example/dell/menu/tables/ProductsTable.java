package com.example.dell.menu.tables;

import android.content.ContentValues;

import com.example.dell.menu.objects.Product;

/**
 * Created by Dell on 27.05.2017.
 */

public class ProductsTable {
    private final static String tableName = "Products";
    private final static String firstColumnName = "productsId";
    private final static String secondColumnName = "name";
    private final static String thirdColumnName = "numberOfKcalPer100g";
    private final static String fourthColumnName = "type";
    private final static String fifthColumnName = "storageType";

    public static String create(){
        String createCommand = String.format("create table %s(", tableName)
                + String.format("%s INTEGER PRIMARY KEY, ", firstColumnName)
                + String.format("%s TEXT, ", secondColumnName)
                + String.format("%s INTEGER, ", thirdColumnName)
                + String.format("%s TEXT, ", fourthColumnName)
                + String.format("%s TEXT);", fifthColumnName);

        return createCommand;
    }

    public static ContentValues getContentValues(Product product){
        ContentValues contentValues = new ContentValues();
        contentValues.put(secondColumnName, product.getName());
        contentValues.put(thirdColumnName, product.getNumberOfKcalPer100g());
        contentValues.put(fourthColumnName, product.getType());
        contentValues.put(fifthColumnName, product.getStorageType());
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

    public static String getFifthColumnName() {
        return fifthColumnName;
    }
}
