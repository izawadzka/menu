package com.example.dell.menu.data;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.dell.menu.App;
import com.example.dell.menu.data.backup.BackupTimer;

/**
 * Created by Dell on 24.05.2017.
 */

public class MenuDataBase{
    private static final String DEBUG_TAG = "SqLiteTodoManager";
    public static final String DATABASE_NAME = "menu.db";
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static MenuDataBase instance;


    private MenuDataBase(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    public static MenuDataBase getInstance(Context context){
        if(instance == null) instance = new MenuDataBase(context);
        return instance;
    }

    public static MenuDataBase getInstance(Activity activity){
        if(instance == null){
            instance = new MenuDataBase(activity);
        }

        return instance;
    }

    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    public long insert(String tableName, ContentValues contentValues){
        if(BackupTimer.isTimerCounting()) BackupTimer.stopCounting();
        open();
        long callback = database.insert(tableName, null, contentValues);
        return callback;
    }

    public Cursor downloadData(String query) {
        open();
        return database.rawQuery(query, null);
    }

    public int delete(String tableName, String whereClause, String[] whereArgs){
        if(BackupTimer.isTimerCounting()) BackupTimer.stopCounting();
        open();
        return database.delete(tableName, whereClause, whereArgs);
    }

    public int update(String tableName, ContentValues newContentValues, String whereClause,
                      String[] whereArgs){
        if(BackupTimer.isTimerCounting()) BackupTimer.stopCounting();
        open();
        return database.update(tableName, newContentValues, whereClause, whereArgs);
    }
}
