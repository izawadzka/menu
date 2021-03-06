package com.example.dell.menu.user.screens.register;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dell.menu.App;
import com.example.dell.menu.MainActivity;
import com.example.dell.menu.R;
import com.example.dell.menu.data.backup.Backup;
import com.example.dell.menu.internetconnection.InternetConnection;
import com.example.dell.menu.user.objects.User;
import com.example.dell.menu.user.UserStorage;
import com.example.dell.menu.user.screens.login.LoginActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {

    @Bind(R.id.registerUsernameEditText)
    EditText registerUsernameEditText;
    @Bind(R.id.registerPasswordEditText)
    EditText registerPasswordEditText;
    @Bind(R.id.registerButton)
    Button registerButton;

    private UserStorage userStorage;
    private RegisterManager registerManager;
    private InternetConnection internetConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        userStorage = ((App) getApplication()).getUserStorage();
        registerManager = ((App) getApplication()).getRegisterManager();

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);

        internetConnection = new InternetConnection();
        if(!internetConnection.checkConnection(this))
        showInternetConnectionAlertDialog();
    }

    private void showInternetConnectionAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Internet connection needed");

        alertDialogBuilder.setMessage("In order to register the Internet connection is needed. " +
                "Please, turn it on. \n\n" +
                "*After being registered, you can turn it off")
                .setCancelable(true)
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerManager.onAttach(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        registerManager.onStop();
    }

    @OnClick(R.id.registerButton)
    public void onRegisterClicked() {
        boolean hasErrors  = false;
        registerButton.setEnabled(false);
        if(registerUsernameEditText.length() < 3){
            hasErrors = true;
            registerUsernameEditText.setError("Username must have at least 3 characters");
        }

        if(registerPasswordEditText.length() < 6){
            hasErrors = true;
            registerPasswordEditText.setError("Password must have at least 6 characters");
        }

        if(!internetConnection.checkConnection(this)){
            hasErrors = true;
            Toast.makeText(this, "Internet connection needed!", Toast.LENGTH_LONG).show();
        }

        if(!hasErrors)
            registerManager.register(registerUsernameEditText.getText().toString(),
                    registerPasswordEditText.getText().toString());

        else registerButton.setEnabled(true);
    }

    public void registerSuccess() {
        Toast.makeText(this, "Register succesful", Toast.LENGTH_SHORT).show();
        userStorage.login(new User(registerUsernameEditText.getText().toString(), registerPasswordEditText.getText().toString()));
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public void registerFailed(String message) {
        Toast.makeText(this, "Register failed " + message, Toast.LENGTH_SHORT).show();
        registerButton.setEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void userAlreadyExists() {
        registerButton.setEnabled(true);
        registerUsernameEditText.setError("User already exists");
    }

    public void createDirectoryFailed() {
        registerButton.setEnabled(true);
        Toast.makeText(this, "Something went wrong. Try again", Toast.LENGTH_LONG).show();
    }
}
