package com.example.dell.menu.tables;

import android.content.ContentValues;

/**
 * Created by Dell on 07.06.2017.
 */

public class ShoppingListsMenusTable {
    private final static String tableName = "ShoppingListsMenus";
    private final static String firstColumnName = "shoppingListId";
    private final static String secondColumnName = "menuId";


    public static ContentValues getContentValues(long shoppingListId, int menuId){
        ContentValues contentValues = new ContentValues();
        contentValues.put(firstColumnName, shoppingListId);
        contentValues.put(secondColumnName, menuId);
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
