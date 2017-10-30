package com.example.dell.menu.tables;

import android.content.ContentValues;

import com.example.dell.menu.objects.menuplanning.Product;

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
    private final static String sixthColumnName = "amountOfProtein";
    private final static String seventhColumnName = "amountOfCarbohydrates";
    private final static String eighthColumnName = "amountOfFat";

    public static ContentValues getContentValues(Product product){
        ContentValues contentValues = new ContentValues();
        contentValues.put(secondColumnName, product.getName());
        contentValues.put(thirdColumnName, product.getNumberOfKcalPer100g());
        contentValues.put(fourthColumnName, product.getType());
        contentValues.put(fifthColumnName, product.getStorageType());
        contentValues.put(sixthColumnName, product.getAmountOfProteinsPer100g());
        contentValues.put(seventhColumnName, product.getAmountOfCarbosPer100g());
        contentValues.put(eighthColumnName, product.getAmountOfFatPer100g());
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
