package com.example.dell.menu.data.backup.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.example.dell.menu.data.MenuDataBase;
import com.example.dell.menu.data.backup.Backup;
import com.example.dell.menu.data.backup.objects.BackupInfo;
import com.example.dell.menu.internetconnection.InternetConnection;

import java.util.Vector;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RestoreBackupActivity extends AppCompatActivity {

    @Bind(R.id.restoreBackupInfo)
    TextView restoreBackupInfo;
    @Bind(R.id.automaticallyGeneratedBackupCheckBox)
    CheckBox automaticallyGeneratedBackupCheckBox;
    @Bind(R.id.firstUsersBackupCheckBox)
    CheckBox firstUsersBackupCheckBox;
    @Bind(R.id.secondUsersBackupCheckBox)
    CheckBox secondUsersBackupCheckBox;
    @Bind(R.id.restoreButton)
    Button restoreButton;
    @Bind(R.id.cancelButton)
    Button cancelButton;
    @Bind(R.id.availableBackupsRelativeLayout)
    RelativeLayout availableBackupsRelativeLayout;
    @Bind(R.id.findRestoreBackupProgressBar)
    ProgressBar findRestoreBackupProgressBar;

    private RestoreBackupManager manager;
    private AlertDialog alertDialog;
    ProgressBar restoreBackupProgressbar;
    TextView restoreResultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore_backup);
        ButterKnife.bind(this);


        restoreBackupInfo.setText(getString(R.string.sure_to_restore) + "\n" +
                "Choose one of those listed below." + "\n\n" +
                "*Internet connection is needed");

        manager = ((App) getApplication()).getRestoreBackupManager();

        automaticallyGeneratedBackupCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstUsersBackupCheckBox.setChecked(false);
                secondUsersBackupCheckBox.setChecked(false);
            }
        });

        firstUsersBackupCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                automaticallyGeneratedBackupCheckBox.setChecked(false);
                secondUsersBackupCheckBox.setChecked(false);
            }
        });

        secondUsersBackupCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                automaticallyGeneratedBackupCheckBox.setChecked(false);
                firstUsersBackupCheckBox.setChecked(false);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        manager.onAttach(this);
        manager.getAvailableBackups();
    }

    @Override
    protected void onStop() {
        super.onStop();
        manager.onStop();
    }

    @OnClick(R.id.restoreButton)
    public void onRestoreButtonClicked() {
        InternetConnection internetConnection = new InternetConnection();
        if(internetConnection.checkConnection(this)) {
            cancelButton.setEnabled(false);
            restoreButton.setEnabled(false);

            alertDialog = createRestoreBackupProgressDialog();
            alertDialog.setMessage("Restoring backup in progress. Please, wait");
            alertDialog.setCancelable(false);
            alertDialog.show();

            Backup backup = new Backup((App) getApplication());
            if (automaticallyGeneratedBackupCheckBox.isChecked())
                backup.restoreBackup(MenuDataBase.DATABASE_NAME);
            else if (firstUsersBackupCheckBox.isChecked())
                backup.restoreBackup(BackupInfo.FIRST_USERS_BACKUP_KEY);
            else if (secondUsersBackupCheckBox.isChecked())
                backup.restoreBackup(BackupInfo.SECOND_USERS_BACKUP_KEY);
        }else{
            Toast.makeText(this, "Internet connection needed!",Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.cancelButton)
    public void onCancelButtonClicked() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }


    public AlertDialog createRestoreBackupProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater()
                .inflate(R.layout.restore_backup_prograss_dialog_layout, null);
        builder.setView(view);
        restoreBackupProgressbar = (ProgressBar) view.findViewById(R.id.restoreBackupProgressbar);
        restoreResultTextView = (TextView) view.findViewById(R.id.restoreResultTextView);
        return builder.create();
    }

    public void checkingBackupsFailed() {
        Toast.makeText(this, "An error occurred while an attempt to check available backups",
                Toast.LENGTH_LONG).show();
    }

    public void setBackupsInfo(Vector<BackupInfo> backupInfos) {
        findRestoreBackupProgressBar.setVisibility(View.GONE);
        availableBackupsRelativeLayout.setVisibility(View.VISIBLE);
        if (backupInfos.size() == 0)
            Toast.makeText(this, "You don't have any backups!", Toast.LENGTH_LONG).show();
        else {
            for (BackupInfo backupInfo : backupInfos) {
                if (backupInfo.isAutomaticallyGenerated()) {
                    automaticallyGeneratedBackupCheckBox.setVisibility(View.VISIBLE);
                    automaticallyGeneratedBackupCheckBox
                            .setText(backupInfo.getName() + "\n" + backupInfo.getDate()
                                    + " " + backupInfo.getTime());
                } else if (backupInfo.isFirstUsersBackup()) {
                    firstUsersBackupCheckBox.setVisibility(View.VISIBLE);
                    firstUsersBackupCheckBox.setText(backupInfo.getName() + "\n" + backupInfo.getDate()
                            + " " + backupInfo.getTime());
                } else if (backupInfo.isSecondUsersBackup()) {
                    secondUsersBackupCheckBox.setVisibility(View.VISIBLE);
                    secondUsersBackupCheckBox.setText(backupInfo.getName() + "\n" + backupInfo.getDate()
                            + " " + backupInfo.getTime());
                }
            }

        }

    }

    public void showResult(boolean success) {
        restoreBackupProgressbar.setVisibility(View.GONE);
        restoreResultTextView.setVisibility(View.VISIBLE);
        String restoreSuccess = "Successfully restored backup!";
        String restoreFailed = "An error occurred while an attempt to restore backup!";
        if (success) restoreResultTextView.setText(restoreSuccess);
        else restoreResultTextView.setText(restoreFailed);

        alertDialog.cancel();

        if(success) Toast.makeText(this, restoreSuccess, Toast.LENGTH_LONG).show();
        else Toast.makeText(this, restoreFailed, Toast.LENGTH_LONG).show();

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
