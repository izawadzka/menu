package com.example.dell.menu.data.backup;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.dell.menu.App;
import com.example.dell.menu.data.MD5Hash;
import com.example.dell.menu.data.MenuDataBase;
import com.example.dell.menu.data.backup.events.AvailableBackupsGotEvent;
import com.example.dell.menu.data.backup.events.RestoringBackupResultEvent;
import com.example.dell.menu.data.backup.objects.BackupInfo;
import com.example.dell.menu.internetconnection.InternetConnection;
import com.example.dell.menu.user.events.RegisterResultEvent;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.squareup.otto.Bus;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Vector;


/**
 * Created by Dell on 23.10.2017.
 */

public class Backup {
    public static final int RESULT_ALREADY_EXISTS = 1;
    public static final int RESULT_USER_REGISTER = 0;
    public static final int RESULT_FAILED_TO_CREATE_DIRECTORY = -1;
    private String TAG = "Backup";
    private final App app;
    private final Context context;
    private BackupService backupService = null;


    private final static String SFTPHOST = "serwer1721060.home.pl";
    private final static int SFTPPORT = 22;
    private final static String SFTPUSER = "izabela@kazanowskidawid.pl";
    private final static String SFTPPASS = "d7KtueiNV3^I";
    private final static String SFTPBASEWORKINGDIR = "public_html/menu_backup";
    private String USER_SPECIFICATION;
    private String SFTPWORKINGDIR;
    private String fileToTransfer;
    private Session session;
    private Channel channel;
    private ChannelSftp channelSftp;
    private InternetConnection internetConnection;


    public Backup(App app){
        this.context = app.getBaseContext();
        this.app = app;
        internetConnection = new InternetConnection();

        String storagePath = "data/data/" + context.getPackageName() + "/databases/";
        fileToTransfer = storagePath + MenuDataBase.DATABASE_NAME;

        try {
            USER_SPECIFICATION = app.getUserStorage().getLogin()+"_"
                    + MD5Hash.getHash(app.getUserStorage().getPassword());
            SFTPWORKINGDIR = SFTPBASEWORKINGDIR + "/" + USER_SPECIFICATION;
            Log.i(TAG, "path: " + SFTPWORKINGDIR);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }


    Backup(BackupService backupService, App app){
        this(app);
        this.backupService = backupService;
    }

    public void checkAvailableBackups(){
        session = null;
        channel = null;
        channelSftp = null;

            if (internetConnection.checkConnection(app.getBaseContext())) {
                new AsyncTask<Void, Void, Vector<BackupInfo>>() {

                    @Override
                    protected Vector<BackupInfo> doInBackground(Void... params) {
                        try {
                            return performLsCommand();
                        } catch (JSchException e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Vector<BackupInfo> result) {
                        Bus bus = app.getBus();
                        bus.post(new AvailableBackupsGotEvent(result));
                        bus.unregister(this);
                    }
                }.execute();
            }
    }

    private Vector<BackupInfo> performLsCommand() throws JSchException {
        Vector<ChannelSftp.LsEntry> result;
        Vector<BackupInfo> availableBackupsVector = new Vector<>();

        session = null;
        channel = null;
        channelSftp = null;

        connectToServer();
        try {
            result = channelSftp.ls(SFTPWORKINGDIR);

            for ( ChannelSftp.LsEntry lsEntry : result) {
                if(!lsEntry.getFilename().startsWith(String.valueOf("."))) {
                    availableBackupsVector
                            .add(new BackupInfo(lsEntry.getFilename(),
                                    lsEntry.getAttrs().getMtimeString()));
                }
            }

        } catch (SftpException e) {
            Log.e(TAG, e.getLocalizedMessage());
            availableBackupsVector = null;
        }

        channel.disconnect();
        session.disconnect();

        return availableBackupsVector;
    }


    private void putFileToServer() throws JSchException, SftpException, FileNotFoundException {
        session = null;
        channel = null;
        channelSftp = null;

        connectToServer();
        try {
            channelSftp.cd(SFTPWORKINGDIR);
            File f = new File(fileToTransfer);
            channelSftp.put(new FileInputStream(f), f.getName());
        }catch (Exception e){
            Log.e(TAG, e.getLocalizedMessage());
        }
        channel.disconnect();
        session.disconnect();
    }

    private void connectToServer() throws JSchException {
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
    }

    public void doBackup() {
        app.getBackupFlagStorage().setFlag(true);
            if (internetConnection.checkConnection(context)) {
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
                            Log.i(TAG, "Success");
                            app.getBackupFlagStorage().setFlag(false);
                        }

                        else Log.e(TAG, "Error");
                        if(backupService != null) backupService.stopSelf();
                    }
                }.execute();
            } else {
                app.getBackupFlagStorage().setFlag(true);
                if(backupService != null) backupService.stopSelf();
            }

    }

    public void restoreBackup(final String backupVersion) {
        if(internetConnection.checkConnection(context)){
            new AsyncTask<Void, Void, Boolean>(){

                @Override
                protected Boolean doInBackground(Void... params) {
                    if(BackupTimer.isTimerCounting()) BackupTimer.stopCounting();

                    try {
                        getFileFromServer(backupVersion);
                    } catch (JSchException e) {
                        Log.e(TAG,e.getLocalizedMessage());
                        return false;
                    } catch (SftpException e) {
                        Log.e(TAG,e.getLocalizedMessage());
                        return false;
                    } catch (IOException e) {
                        Log.e(TAG,e.getLocalizedMessage());
                        return false;
                    }

                    return true;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    if (result) {
                        Log.i(TAG, "Success");
                    }

                    else Log.e(TAG, "Error");

                    Bus bus = app.getBus();
                    bus.register(this);
                    bus.post(new RestoringBackupResultEvent(result));
                    bus.unregister(this);
                }
            }.execute();
        }else Log.e(TAG, "there's no internet connection");
    }

    private void getFileFromServer(String fileName) throws SftpException, IOException, JSchException {
        String FILETOWRITEIN = fileToTransfer;

        session = null;
        channel = null;
        channelSftp = null;


        connectToServer();

        channelSftp.cd(SFTPWORKINGDIR);

        Log.i(TAG, "moved to exact directory");
        byte[] buffer = new byte[1024];
        BufferedInputStream bis = new BufferedInputStream(channelSftp.get(String.format("%s", fileName)));
        File newFile = new File(FILETOWRITEIN);
        Log.i(TAG, "new database file created");
        OutputStream os = new FileOutputStream(newFile);
        BufferedOutputStream bos = new BufferedOutputStream(os);
        int readCount;
        while ((readCount = bis.read(buffer)) > 0) {
            System.out.println("Writing: ");
            bos.write(buffer, 0, readCount);
        }
        bis.close();
        bos.close();

        channel.disconnect();
        session.disconnect();

    }

    public void registerNewUser(final String login, final String password) {
        try {
            USER_SPECIFICATION = login+"_"
                    + MD5Hash.getHash(password);
            SFTPWORKINGDIR = SFTPBASEWORKINGDIR + "/" + USER_SPECIFICATION;
            Log.i(TAG, "path: " + SFTPWORKINGDIR);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, e.getLocalizedMessage());
            return;
        }

        if(internetConnection.checkConnection(context)) {
            new AsyncTask<Void, Void, Integer>() {

                @Override
                protected Integer doInBackground(Void... params) {
                    try {
                        if (checkIfUserAlreadyExists(login, password)) return RESULT_ALREADY_EXISTS;
                        else return RESULT_USER_REGISTER;
                    } catch (JSchException e) {
                        e.printStackTrace();
                        return RESULT_FAILED_TO_CREATE_DIRECTORY;
                    } catch (SftpException e) {
                        e.printStackTrace();
                        return RESULT_FAILED_TO_CREATE_DIRECTORY;
                    }
                }

                @Override
                protected void onPostExecute(Integer result) {
                    app.getBus().register(this);
                    app.getBus().post(new RegisterResultEvent(result, login, password));
                    app.getBus().unregister(this);
                }
            }.execute();
        }
    }

    private boolean checkIfUserAlreadyExists(String login, String password) throws JSchException, SftpException {
        session = null;
        channel = null;
        channelSftp = null;

        connectToServer();
        try {
            channelSftp.cd(SFTPWORKINGDIR); //if it works, then the dictionary already exists -
            // login has to be changed
            return true;
        } catch (SftpException e) {
            channelSftp.cd(SFTPBASEWORKINGDIR);
            channelSftp.mkdir(USER_SPECIFICATION);
            channelSftp.cd(USER_SPECIFICATION);
            return false;
        }
    }
}
