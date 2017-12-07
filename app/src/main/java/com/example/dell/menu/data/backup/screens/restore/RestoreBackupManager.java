package com.example.dell.menu.data.backup.screens.restore;

import com.example.dell.menu.App;
import com.example.dell.menu.data.backup.Backup;
import com.example.dell.menu.data.backup.events.AvailableBackupsGotEvent;
import com.example.dell.menu.data.backup.events.RestoringBackupResultEvent;
import com.example.dell.menu.virtualfridge.ShelvesArchive;
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
            if(event.success){
                ShelvesArchive shelvesArchive = new ShelvesArchive(restoreBackupActivity);
                shelvesArchive.manageArchive();
            }
        }
    }

    public void onAttach(RestoreBackupActivity restoreBackupActivity){
        this.restoreBackupActivity = restoreBackupActivity;
        this.bus.register(this);
    }

    public void onStop(){
        this.restoreBackupActivity = null;
        this.bus.unregister(this);
    }

    public void getAvailableBackups() {
        if (restoreBackupActivity != null) {
            Backup backup = new Backup((App) restoreBackupActivity.getApplication());
            backup.checkAvailableBackups();
        }
    }
}
