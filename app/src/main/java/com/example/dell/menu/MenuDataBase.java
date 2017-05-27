package com.example.dell.menu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.dell.menu.tables.MealsTable;
import com.example.dell.menu.tables.ProductsTable;
import com.example.dell.menu.tables.UsersTable;

/**
 * Created by Dell on 24.05.2017.
 */

public class MenuDataBase extends SQLiteOpenHelper{
    private static final String DEBUG_TAG = "SqLiteTodoManager";
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "menu.db";
    private final SQLiteDatabase db;


    public MenuDataBase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        db = this.getWritableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UsersTable.create());
        db.execSQL(ProductsTable.create());
        db.execSQL(MealsTable.create());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long insert(String tableName, ContentValues contentValues){
        //SQLiteDatabase db = this.getWritableDatabase();
        long callback = db.insert(tableName, null, contentValues);
        //db.close();
        return callback;
    }

    public Cursor downloadDatas(String query) {
        SQLiteDatabase db = this.getWritableDatabase();

        return db.rawQuery(query, null);
    }
}
