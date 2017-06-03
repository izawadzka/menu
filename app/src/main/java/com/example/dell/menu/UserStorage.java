package com.example.dell.menu;

import android.content.SharedPreferences;

import com.example.dell.menu.objects.User;

/**
 * Created by Dell on 25.05.2017.
 */

public class UserStorage {
    public static final String LOGIN_KEY = "login";
    public static final String PASSWORD_KEY = "password";
    private final SharedPreferences preferences;
    private String login;
    private String password;
    private Long userId;
    public UserStorage(SharedPreferences sharedPreferences){
        preferences = sharedPreferences;
    }

    public void login(User user){
        login = user.getLogin();
        password = user.getPassword();
        userId = user.getUserId();


        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LOGIN_KEY, login);
        editor.putString(PASSWORD_KEY, password);
        editor.putLong("userId", userId);
        editor.apply();
    }

    public boolean hasToLogin() {
        return preferences.getString(LOGIN_KEY, "").isEmpty();
    }

    public void logout() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    public String getLogin() {
        return login;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
