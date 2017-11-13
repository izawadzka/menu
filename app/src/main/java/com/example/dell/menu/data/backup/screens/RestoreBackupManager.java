package com.example.dell.menu.data.backup.screens;

import android.util.Log;

import com.example.dell.menu.App;
import com.example.dell.menu.data.backup.Backup;
import com.example.dell.menu.data.backup.events.AvailableBackupsGotEvent;
import com.example.dell.menu.data.backup.events.RestoringBackupResultEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * Created by Dell on 12.11.2017.
 */

public class RestoreBackupManager {
    private final Bus bus;
    private RestoreBackupActivity restoreBackupActivity;
    public RestoreBackupManager(Bus bus){
        this.bus = bus;
        this.bus.register(this);
    }

    @Subscribe
    public void onAvailableBackupsChecked(AvailableBackupsGotEvent event){
        if(restoreBackupActivity != null) {
            if (event.backupInfos != null) {
                restoreBackupActivity.setBackupsInfo(event.backupInfos);
            } else restoreBackupActivity.checkingBackupsFailed();
        }
    }

    @Subscribe
    public void onRestoringBackupResultEvent(RestoringBackupResultEvent event){
        if(restoreBackupActivity != null){
            restoreBackupActivity.showResult(event.success);
        }
    }

    public void onAttach(RestoreBackupActivity restoreBackupActivity){
        this.restoreBackupActivity = restoreBackupActivity;
    }

    public void onStop(){
        this.restoreBackupActivity = null;
    }

    public void getAvailableBackups() {
        if (restoreBackupActivity != null) {
            Backup backup = new Backup((App) restoreBackupActivity.getApplication());
            backup.checkAvailableBackups();
        }
    }
}
