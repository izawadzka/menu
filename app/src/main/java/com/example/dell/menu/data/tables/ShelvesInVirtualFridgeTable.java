package com.example.dell.menu.data.tables;

import android.content.ContentValues;

import com.example.dell.menu.virtualfridge.objects.ShelfInVirtualFridge;

/**
 * Created by Dell on 29.11.2017.
 */

public class ShelvesInVirtualFridgeTable {
    private final static String tableName = "ShelvesInVirtualFridge";
    private final static String firstColumnName = "shelfId";
    private final static String secondColumnName = "dailyMenuId";

    public static ContentValues getContentValues(ShelfInVirtualFridge shelf){
        ContentValues contentValues = new ContentValues();
        contentValues.put(secondColumnName, shelf.getDailyMenuId());
        return contentValues;
    }

    public static String getSecondColumnName() {
        return secondColumnName;
    }

    public static String getTableName() {
        return tableName;
    }

    public static String getFirstColumnName() {
        return firstColumnName;
    }
}
