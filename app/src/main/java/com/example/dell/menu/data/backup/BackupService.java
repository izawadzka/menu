package com.example.dell.menu.data.backup;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.dell.menu.App;
import com.example.dell.menu.MainActivity;
import com.example.dell.menu.R;
import com.example.dell.menu.internetconnection.ConnectivityReceiver;

import java.util.Random;

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
    public void onNetworkConnectionChanged(boolean isConnected) {
        Log.i(TAG, "network connection changed");
        if(isConnected){
            if(((App)getApplication()).getBackupFlagStorage().checkFlag() && !BackupTimer.isTimerCounting()) {
                Backup backup = new Backup(this, (App) getApplication());
                backup.doBackup();
            }
        }
    }
}
