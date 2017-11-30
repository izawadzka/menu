package com.example.dell.menu.data.tables;

import android.content.ContentValues;

import com.example.dell.menu.menuplanning.objects.Product;

/**
 * Created by Dell on 29.11.2017.
 */

public class ProductsOnShelvesTable {
    private final static String tableName = "ProductsOnShelves";
    private final static String firstColumnName = "productsId";
    private final static String secondColumnName = "shelfId";
    private final static String thirdColumnName = "amount";
    private final static String fourthColumnName = "productFlagId";


    public static ContentValues getContentValues(Product product, int shelfId){
        ContentValues contentValues = new ContentValues();
        contentValues.put(firstColumnName, product.getProductId());
        contentValues.put(secondColumnName, shelfId);
        contentValues.put(thirdColumnName, product.getQuantity());
        contentValues.put(fourthColumnName, product.getProductFlagId());
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

    public static String getFourthColumnName() {
        return fourthColumnName;
    }
}
