package com.example.dell.menu.data.backup;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.dell.menu.App;
import com.example.dell.menu.datechangedetection.DateChangeReceiver;
import com.example.dell.menu.internetconnection.ConnectivityReceiver;
import com.example.dell.menu.virtualfridge.ShelvesArchive;

/**
 * Created by Dell on 23.10.2017.
 */

public class InterceptCommunicationService extends Service
        implements ConnectivityReceiver.ConnectivityReceiverListener, DateChangeReceiver.DateChangeReceiverListener{
    private static final String TAG = "service";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        setConnectivityListener(this);
        setDateChangeListener(this);
    }


    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener){
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    public void setDateChangeListener(DateChangeReceiver.DateChangeReceiverListener listener){
        DateChangeReceiver.dateChangeReceiverListener = listener;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        Log.i(TAG, "network connection changed");
        if(isConnected){
            if(((App)getApplication()).getBackupFlagStorage().checkFlag() && !BackupTimer.isTimerCounting()) {
                Backup backup = new Backup(this, (App) getApplication());
                backup.doBackup();
            }
        }
    }

    @Override
    public void onDateChanged(Context context) {
        Log.i(TAG, "date changed");
        ShelvesArchive shelvesArchive = new ShelvesArchive(context);
        shelvesArchive.manageArchive();
    }
}
