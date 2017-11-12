package com.example.dell.menu.user;

import android.content.SharedPreferences;

import com.example.dell.menu.user.objects.User;

/**
 * Created by Dell on 25.05.2017.
 */

public class UserStorage {
    public static final String LOGIN_KEY = "login";
    public static final String PASSWORD_KEY = "password";
    public static final String USER_ID_KEY = "userId";
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

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LOGIN_KEY, login);
        editor.putString(PASSWORD_KEY, password);
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
        return preferences.getString(LOGIN_KEY, "");
    }

    public String getPassword() {
        return preferences.getString(PASSWORD_KEY, "");
    }

    public Long getUserId() {
        return preferences.getLong(USER_ID_KEY, 0);
    }

    public void setUserId(Long userId) {
        this.userId = userId;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(USER_ID_KEY, userId);
        editor.apply();
    }
}
