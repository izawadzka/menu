package com.example.dell.menu.datechangedetection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Dell on 01.12.2017.
 */

public class DateChangeReceiver extends BroadcastReceiver{
    public static DateChangeReceiverListener dateChangeReceiverListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(dateChangeReceiverListener != null)
            dateChangeReceiverListener.onDateChanged(context);
    }

    public interface DateChangeReceiverListener{
        void onDateChanged(Context context);
    }
}
