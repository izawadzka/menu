package com.example.dell.menu.data.backup;

import android.os.CountDownTimer;
import android.util.Log;

import com.example.dell.menu.App;

/**
 * Created by Dell on 09.11.2017.
 */

public class BackupTimer {

    private static final int TIMER_DURATION = 120000;
    private static final int TICK_DURATION = 1000;
    public static App app;
    private static  CountDownTimer timer;

    public BackupTimer(App application){
        app = application;
    }

    public void start(){
        if (timer != null){
            timer.cancel();
            timer = null;
        }
Log.i("timer", "start");
        app.getBackupFlagStorage().setFlag(true);
        timer = new CountDownTimer(TIMER_DURATION, TICK_DURATION) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                if (app != null) {
                    Backup backup = new Backup(app);
                    backup.doBackup();
                    app = null;
                }

            }
        }.start();
    }

    public static boolean isTimerCounting(){
        return timer != null;
    }
    public static void stopCounting(){
        timer.cancel();
        timer = null;
        Log.i("timer", "stoped");
    }
}
