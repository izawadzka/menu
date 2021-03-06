package com.example.dell.menu.data.tables;

import android.content.ContentValues;

/**
 * Created by Dell on 28.08.2017.
 */

public class ShoppingListsProductsTable {
    private final static String tableName = "ShoppingListsProducts";
    private final static String firstColumnName = "shoppingListId";
    private final static String secondColumnName = "productId";
    private final static String thirdColumnName = "totalQuantity";
    private final static String fourthColumnName = "wasBought";


    public static ContentValues getContentValues(long shoppingListId, long productId, double quantity){
        ContentValues contentValues = new ContentValues();
        contentValues.put(firstColumnName, shoppingListId);
        contentValues.put(secondColumnName, productId);
        contentValues.put(thirdColumnName, quantity);
        contentValues.put(fourthColumnName, 0);
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

    public static String getFourthColumnName() {
        return fourthColumnName;
    }
}
