package com.example.dell.menu.internetconnection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;



/**
 * Created by Dell on 24.10.2017.
 */

public class    ConnectivityReceiver extends BroadcastReceiver {
    public static ConnectivityReceiverListener connectivityReceiverListener;
    private InternetConnection connection=new InternetConnection();

    @Override
    public void onReceive(Context context, Intent intent) {

        if (connectivityReceiverListener != null) {
            connectivityReceiverListener
                    .onNetworkConnectionChanged(connection.checkConnection(context));
        }
    }

    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }
}
