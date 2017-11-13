package com.example.dell.menu.data.backup.screens.backupondemand;

import android.util.Log;

import com.example.dell.menu.App;
import com.example.dell.menu.data.backup.Backup;
import com.example.dell.menu.data.backup.events.AvailableBackupsGotEvent;
import com.example.dell.menu.data.backup.events.RestoringBackupResultEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * Created by Dell on 13.11.2017.
 */

public class BackupOnDemandManager {
    private final Bus bus;
    private BackupOnDemandActivity backupOnDemandActivity;
    private final static String TAG = "BackupOnDemandManager";

    public BackupOnDemandManager(Bus bus){
        this.bus = bus;
    }

    public void onAttach(BackupOnDemandActivity backupOnDemandActivity){
        this.backupOnDemandActivity =backupOnDemandActivity;
        bus.register(this);
    }

    public void onStop(){
        this.backupOnDemandActivity = null;
        bus.unregister(this);
    }

    public void getAvailableBackups() {
        if (backupOnDemandActivity != null) {
            Backup backup = new Backup((App) backupOnDemandActivity.getApplication());
            backup.checkAvailableBackups();
        }
    }

    @Subscribe
    public void onRestoringBackupResultEvent(RestoringBackupResultEvent event){
        if(backupOnDemandActivity != null){
            backupOnDemandActivity.showResult(event.success);
        }
    }

    @Subscribe
    public void onAvailableBackupsChecked(AvailableBackupsGotEvent event){
        if(backupOnDemandActivity != null) {
            if (event.backupInfos != null) {
                backupOnDemandActivity.setBackupsInfo(event.backupInfos);
            } else backupOnDemandActivity.checkingBackupsFailed();
        }
    }
}
