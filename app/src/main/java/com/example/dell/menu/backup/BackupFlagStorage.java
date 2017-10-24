package com.example.dell.menu.backup;

import android.content.SharedPreferences;

/**
 * Created by Dell on 23.10.2017.
 */

public class BackupFlagStorage {
    public final static String FLAG_KEY = "flag";

    private final SharedPreferences preferences;

    public BackupFlagStorage(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public void setFlag(boolean backupFlag){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(FLAG_KEY, backupFlag);
        editor.apply();
    }



    public boolean checkFlag(){
        return preferences.getBoolean(FLAG_KEY, false);
    }
}
