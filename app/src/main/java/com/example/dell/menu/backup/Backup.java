package com.example.dell.menu.backup;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.example.dell.menu.App;
import com.example.dell.menu.MenuDataBase;
import com.example.dell.menu.internetconnection.InternetConnection;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by Dell on 23.10.2017.
 */

public class Backup {
    String TAG = "Backup";
    //private final Activity activity;
    private final App app;
    private final Context context;
    private final BackupService backupService;


    String SFTPHOST = "serwer1721060.home.pl";
    int SFTPPORT = 22;
    String SFTPUSER = "kazanowskidawid@kazanowskidawid.pl";
    String SFTPPASS = "Turystyka12345";
    String SFTPWORKINGDIR = "public_html/test";
    String storagePath;
    String fileToTransfer;
/*
    public Backup(Activity activity) {
        this.activity = activity;
        this.app = (App)activity.getApplication();
    }*/
/*
    public Backup(Context context, App app){
        this.context = context;
        this.app = app;
    }*/
    public Backup(BackupService backupService, App app){
        this.context = backupService;
        this.app = app;
        this.backupService = backupService;
        storagePath = "data/data/" + context.getPackageName()+"/databases/";
        fileToTransfer = storagePath + MenuDataBase.DATABASE_NAME;
        //Log.i(TAG, "path: " + fileToTransfer);
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }


    private void putFileToServer() throws JSchException, SftpException, FileNotFoundException {
        Session session = null;
        Channel channel = null;
        ChannelSftp channelSftp = null;

        JSch jsch = new JSch();
        session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
        session.setPassword(SFTPPASS);
        Log.i(TAG, "password set");
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        Log.i(TAG, "config put");
        session.setConfig(config);
        Log.i(TAG, "config set");
        session.connect();
        Log.i(TAG, " session connected");
        channel = session.openChannel("sftp");
        channel.connect();
        Log.i(TAG, "channel connected");
        channelSftp = (ChannelSftp) channel;
        channelSftp.cd(SFTPWORKINGDIR);
        File f = new File(fileToTransfer);
        channelSftp.put(new FileInputStream(f), f.getName());
        channel.disconnect();
        session.disconnect();
    }

    public void doBackup() {
        Log.i(TAG, "backup needed: " + app.isBackupFlag());
        if (app.isBackupFlag()) {
            app.getBackupFlagStorage().setFlag(true);
            InternetConnection connection = new InternetConnection();
            if (connection.checkConnection(context)) {
                //Log.i(TAG, "zaczynam");
                new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Void... params) {
                        try {
                            putFileToServer();
                        } catch (JSchException e) {
                            Log.e(TAG, e.getLocalizedMessage());
                            return false;
                        } catch (SftpException e) {
                            Log.e(TAG, e.getLocalizedMessage());
                            return false;
                        } catch (FileNotFoundException e) {
                            Log.e(TAG, e.getLocalizedMessage());
                            return false;
                        }
                        return true;
                    }

                    @Override
                    protected void onPostExecute(Boolean result) {
                        if (result) {
                            app.getBackupFlagStorage().setFlag(false);
                            app.setBackupFlag(false);
                        }

                        if(result) Log.i(TAG, "Success");
                        else Log.e(TAG, "Error");
                        backupService.stopSelf();
                    }
                }.execute();
            } else {
                app.getBackupFlagStorage().setFlag(true);
                app.setBackupFlag(true);
                backupService.stopSelf();
            }

        }
    }
}
