package com.example.dell.menu.tables;

import android.content.ContentValues;

import com.example.dell.menu.objects.shoppinglist.ShoppingList;

/**
 * Created by Dell on 07.06.2017.
 */

public class ShoppingListsTable {
    private final static String tableName = "ShoppingLists";
    private final static String firstColumnName = "shoppingListId";
    private final static String secondColumnName = "name";
    private final static String thirdColumnName = "creationDate";
    private final static String fourthColumnName = "authorsId";
    private final static String fifthColumnName = "synchronized";


    public static ContentValues getContentValues(ShoppingList shoppingList){
        ContentValues contentValues = new ContentValues();
        contentValues.put(secondColumnName, shoppingList.getName());
        contentValues.put(thirdColumnName, shoppingList.getCreationDate());
        contentValues.put(fourthColumnName, shoppingList.getAuthorsId());
        contentValues.put(fifthColumnName, shoppingList.isAlreadySynchronized());
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

    public static String getFifthColumnName() {
        return fifthColumnName;
    }
}
