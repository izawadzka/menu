package com.example.dell.menu.data.backup.objects;

import android.util.Log;
import android.util.TimeFormatException;

import com.example.dell.menu.data.MenuDataBase;

/**
 * Created by Dell on 12.11.2017.
 */

public class BackupInfo {
    public static final String FIRST_USERS_BACKUP_KEY = "first_users_backup.db";
    public static final String SECOND_USERS_BACKUP_KEY = "second_users_backup.db";
    private String name, date, time;

    public BackupInfo(String name,String date){
        String firstPart = date.substring(4, 10);
        String secondPart = date.substring(date.length()-4, date.length());
        this.date = secondPart + " " + firstPart;
        this.time = date.substring(11, 19);
        this.name = name;
    }

    public boolean isAutomaticallyGenerated() {
        return name.equals(MenuDataBase.DATABASE_NAME);
    }
    public boolean isFirstUsersBackup() {return name.equals(FIRST_USERS_BACKUP_KEY);}
    public boolean isSecondUsersBackup(){ return name.equals(SECOND_USERS_BACKUP_KEY);}

    public void setDate(String date) {
        this.date = date;
    }


    public String getTime() {
        return time;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }
}
