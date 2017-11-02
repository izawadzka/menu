package com.example.dell.menu.screens.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dell.menu.App;
import com.example.dell.menu.MainActivity;
import com.example.dell.menu.R;
import com.example.dell.menu.objects.User;
import com.example.dell.menu.UserStorage;
import com.example.dell.menu.screens.register.RegisterActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {
    @Bind(R.id.loginEditText)
    EditText loginEditText;
    @Bind(R.id.passwordEditText)
    EditText passwordEditText;
    @Bind(R.id.loginButton)
    Button loginButton;
    @Bind(R.id.registerButton)
    Button registerButton;

    private LoginManager loginManager;
    private UserStorage userStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        userStorage = ((App) getApplication()).getUserStorage();
        loginManager = ((App) getApplication()).getLoginManager();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loginManager.onAttach(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        loginManager.onStop();
    }

    @OnClick(R.id.loginButton)
    public void onLoginClicked() {
        boolean hasErrors = false;
        loginButton.setEnabled(false);
        if(loginEditText.getText().length() < 3){
            hasErrors = true;
            loginEditText.setError("Login has at least 3 characters!");
        }

        if(passwordEditText.getText().length() < 6){
            hasErrors = true;
            passwordEditText.setError("Password is at least 6 character long");
        }
        if(!hasErrors)
            loginManager.login(loginEditText.getText().toString(),
                    passwordEditText.getText().toString());
        else loginButton.setEnabled(true);
    }

    @OnClick(R.id.registerButton)
    public void onRegisterClicked() {
        startActivity(new Intent(this, RegisterActivity.class));
        finish();
    }

    public void loginSuccess() {
        userStorage.login(new User(loginEditText.getText().toString(),passwordEditText.getText().toString()));
        Toast.makeText(this, "Login success", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public void loginFailed() {
        Toast.makeText(this, "Wrong password or username", Toast.LENGTH_SHORT).show();
        loginButton.setEnabled(true);
    }
}
