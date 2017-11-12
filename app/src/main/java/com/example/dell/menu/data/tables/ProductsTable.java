package com.example.dell.menu.data.tables;

import android.content.ContentValues;

import com.example.dell.menu.menuplanning.objects.Product;

/**
 * Created by Dell on 27.05.2017.
 */

public class ProductsTable {
    private final static String tableName = "Products";
    private final static String firstColumnName = "productsId";
    private final static String secondColumnName = "name";
    private final static String thirdColumnName = "kcalPer100g_mlOr1Unit";
    private final static String fourthColumnName = "type";
    private final static String fifthColumnName = "storageType";
    private final static String sixthColumnName = "proteinsPer100g_mlOr1Unit";
    private final static String seventhColumnName = "carbohydratesPer100g_mlOr1Unit";
    private final static String eighthColumnName = "fatPer100g_mlOr1Unit";

    public static ContentValues getContentValues(Product product){
        ContentValues contentValues = new ContentValues();
        contentValues.put(secondColumnName, product.getName());
        contentValues.put(thirdColumnName, product.getKcalPer100g_mlOr1Unit());
        contentValues.put(fourthColumnName, product.getType());
        contentValues.put(fifthColumnName, product.getStorageType());
        contentValues.put(sixthColumnName, product.getProteinsPer100g_mlOr1Unit());
        contentValues.put(seventhColumnName, product.getCarbohydratesPer100g_mlOr1Unit());
        contentValues.put(eighthColumnName, product.getFatPer100g_mlOr1Unit());
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
