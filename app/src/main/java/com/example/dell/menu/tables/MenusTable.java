package com.example.dell.menu.tables;

import android.content.ContentValues;

import com.example.dell.menu.objects.Menu;

import java.sql.Date;

/**
 * Created by Dell on 04.06.2017.
 */

public class MenusTable {
    private final static String tableName = "Menus";
    private final static String firstColumnName = "menuId";
    private final static String secondColumnName = "name";
    private final static String thirdColumnName = "creationDate";
    private final static String fourthColumnName = "cumulativeNumberOfKcal";
    private final static String fifthColumnName = "authorsId";

    public static ContentValues getContentValues(Menu menu){
        ContentValues contentValues = new ContentValues();
        contentValues.put(secondColumnName, menu.getName());
        contentValues.put(thirdColumnName, menu.getCreationDate());
        contentValues.put(fourthColumnName, menu.getCumulativeNumberOfKcal());
        contentValues.put(fifthColumnName, menu.getAuthorsId());
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
