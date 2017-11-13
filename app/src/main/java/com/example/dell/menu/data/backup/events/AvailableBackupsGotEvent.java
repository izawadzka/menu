package com.example.dell.menu.data.backup.events;

import com.example.dell.menu.data.backup.objects.BackupInfo;

import java.util.Vector;

/**
 * Created by Dell on 12.11.2017.
 */

public class AvailableBackupsGotEvent {
    public Vector<BackupInfo> backupInfos;

    public AvailableBackupsGotEvent(Vector<BackupInfo> backupInfos){
        this.backupInfos = backupInfos;
    }
}
