package com.example.dell.menu.objects;

/**
 * Created by Dell on 25.05.2017.
 */

public class User {
    private String login;
    private String password;
    private int userId;

    public User(String login, String password){
        this.login = login;
        this.password = password;

        userId = -1;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int usersId) {
        this.userId = usersId;
    }
}
