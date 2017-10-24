package com.example.dell.menu.backup;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.dell.menu.App;
import com.example.dell.menu.MainActivity;
import com.example.dell.menu.internetconnection.ConnectivityReceiver;

/**
 * Created by Dell on 23.10.2017.
 */

public class BackupService extends Service implements ConnectivityReceiver.ConnectivityReceiverListener {
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
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener){
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i(TAG, "service");
        Backup backup = new Backup(this, (App)getApplication());
        backup.doBackup();
        //stopSelf();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        Log.i(TAG, "network connection changed");
        if(isConnected){
            if(((App)getApplication()).getBackupFlagStorage().checkFlag()) {
                Backup backup = new Backup(this, (App) getApplication());
                backup.doBackup();
            }
        }
    }
}
