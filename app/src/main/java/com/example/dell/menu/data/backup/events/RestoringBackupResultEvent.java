package com.example.dell.menu.data.backup.events;

/**
 * Created by Dell on 13.11.2017.
 */

public class RestoringBackupResultEvent {
    public boolean success;
    public RestoringBackupResultEvent(boolean success){
        this.success =success;
    }
}
