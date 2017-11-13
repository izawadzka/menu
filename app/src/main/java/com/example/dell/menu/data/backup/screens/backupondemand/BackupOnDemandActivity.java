package com.example.dell.menu.data.backup.screens.backupondemand;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.menu.App;
import com.example.dell.menu.MainActivity;
import com.example.dell.menu.R;
import com.example.dell.menu.data.backup.Backup;
import com.example.dell.menu.data.backup.objects.BackupInfo;
import com.example.dell.menu.internetconnection.InternetConnection;

import java.util.Vector;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BackupOnDemandActivity extends AppCompatActivity {

    @Bind(R.id.infoTextView)
    TextView infoTextView;
    @Bind(R.id.firstUsersBackupToOverrideCheckBox)
    CheckBox firstUsersBackupToOverrideCheckBox;
    @Bind(R.id.secondUsersBackupToOverrideCheckBox)
    CheckBox secondUsersBackupToOverrideCheckBox;
    @Bind(R.id.filesToOverrideRelativeLayout)
    RelativeLayout filesToOverrideRelativeLayout;
    @Bind(R.id.firstUsersBackupToCreateCheckBox)
    CheckBox firstUsersBackupToCreateCheckBox;
    @Bind(R.id.secondUsersBackupToCreateCheckBox)
    CheckBox secondUsersBackupToCreateCheckBox;
    @Bind(R.id.filesToCreateRelativeLayout)
    RelativeLayout filesToCreateRelativeLayout;
    @Bind(R.id.doBackupButton)
    Button doBackupButton;
    @Bind(R.id.cancelButton)
    Button cancelButton;
    @Bind(R.id.availableBackupsRelativeLayout)
    RelativeLayout availableBackupsRelativeLayout;
    @Bind(R.id.findRestoreBackupProgressBar)
    ProgressBar findRestoreBackupProgressBar;

    private BackupOnDemandManager backupOnDemandManager;
    private boolean firstBackupToOverride = false, secondBackupToOverride = false;
    private ProgressBar restoreBackupProgressbar;
    private TextView restoreResultTextView;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_on_demand);
        ButterKnife.bind(this);

        backupOnDemandManager = ((App) getApplication()).getBackupOnDemandManager();

        infoTextView.setText("In order to do backup, choose one of those listed below.\n\n" +
                "By default you can store two files with database on our server.\n" +
                "If you have done backup before, you can override a file or you can create a new file" +
                " (but remember about total equals 2). \n\n\nChose one of the options below");

        final CheckBox[] checkBoxes = {firstUsersBackupToOverrideCheckBox,
                secondUsersBackupToOverrideCheckBox,
                firstUsersBackupToCreateCheckBox,
                secondUsersBackupToCreateCheckBox};

        for (final CheckBox checkBox : checkBoxes) {
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (CheckBox box : checkBoxes) {
                        if(box != checkBox) box.setChecked(false);
                    }

                }
            });
        }


        firstUsersBackupToOverrideCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                secondUsersBackupToOverrideCheckBox.setChecked(false);
                firstUsersBackupToCreateCheckBox.setChecked(false);
                secondUsersBackupToCreateCheckBox.setChecked(false);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        backupOnDemandManager.onAttach(this);
        backupOnDemandManager.getAvailableBackups();
    }

    @Override
    protected void onStop() {
        super.onStop();
        backupOnDemandManager.onStop();
    }

    public void setBackupsInfo(Vector<BackupInfo> backupInfo) {
        findRestoreBackupProgressBar.setVisibility(View.GONE);
        availableBackupsRelativeLayout.setVisibility(View.VISIBLE);

        for (BackupInfo info : backupInfo) {
            if(info.isFirstUsersBackup()) {
                firstUsersBackupToOverrideCheckBox.setVisibility(View.VISIBLE);
                firstUsersBackupToOverrideCheckBox.setText(info.getName() + "\n" + info.getDate()
                        + " " + info.getTime());
                firstBackupToOverride = true;
            }
            else if(info.isSecondUsersBackup()) {
                secondUsersBackupToOverrideCheckBox.setVisibility(View.VISIBLE);
                secondUsersBackupToOverrideCheckBox.setText(info.getName() + "\n" + info.getDate()
                        + " " + info.getTime());
                secondBackupToOverride = true;
            }
        }

        if(!firstBackupToOverride){
            firstUsersBackupToCreateCheckBox.setVisibility(View.VISIBLE);
            firstUsersBackupToCreateCheckBox.setText(BackupInfo.FIRST_USERS_BACKUP_KEY);
        }
        if(!secondBackupToOverride) {
            secondUsersBackupToCreateCheckBox.setVisibility(View.VISIBLE);
            secondUsersBackupToCreateCheckBox.setText(BackupInfo.SECOND_USERS_BACKUP_KEY);
        }

        if(firstBackupToOverride && secondBackupToOverride)
            filesToCreateRelativeLayout.setVisibility(View.GONE);

        if(!firstBackupToOverride && !secondBackupToOverride)
            filesToOverrideRelativeLayout.setVisibility(View.GONE);
    }

    public void checkingBackupsFailed() {
        Toast.makeText(this, "An error occurred while an attempt to check available backups",
                Toast.LENGTH_LONG).show();
    }

    @OnClick (R.id.cancelButton)
    public void onCancelButtonClicked(){
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @OnClick (R.id.doBackupButton)
    public void onDoBackupButtonClicked(){
        InternetConnection internetConnection = new InternetConnection();
        if(internetConnection.checkConnection(this)) {
            cancelButton.setEnabled(false);
            doBackupButton.setEnabled(false);

            alertDialog = createRestoreBackupProgressDialog();
            alertDialog.setMessage("Doing backup in progress. Please, wait");
            alertDialog.setCancelable(false);
            alertDialog.show();

            Backup backup = new Backup((App) getApplication());
            if (firstUsersBackupToCreateCheckBox.isChecked() ||
                    firstUsersBackupToOverrideCheckBox.isChecked())
                backup.doBackup(BackupInfo.FIRST_USERS_BACKUP_KEY);
            else if (secondUsersBackupToCreateCheckBox.isChecked() ||
                    secondUsersBackupToOverrideCheckBox.isChecked())
                backup.doBackup(BackupInfo.SECOND_USERS_BACKUP_KEY);
        }else{
            Toast.makeText(this, "Internet connection needed!",Toast.LENGTH_LONG).show();
        }
    }

    public AlertDialog createRestoreBackupProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater()
                .inflate(R.layout.backup_progress_dialog_layout, null);
        builder.setView(view);
        restoreBackupProgressbar = (ProgressBar) view.findViewById(R.id.restoreBackupProgressbar);
        restoreResultTextView = (TextView) view.findViewById(R.id.restoreResultTextView);
        return builder.create();
    }

    public void showResult(boolean success) {
        restoreBackupProgressbar.setVisibility(View.GONE);
        restoreResultTextView.setVisibility(View.VISIBLE);
        String restoreSuccess = "Backup created successfully!";
        String restoreFailed = "An error occurred while an attempt to create backup!";
        if (success) restoreResultTextView.setText(restoreSuccess);
        else restoreResultTextView.setText(restoreFailed);

        alertDialog.cancel();

        if(success) Toast.makeText(this, restoreSuccess, Toast.LENGTH_LONG).show();
        else Toast.makeText(this, restoreFailed, Toast.LENGTH_LONG).show();

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
