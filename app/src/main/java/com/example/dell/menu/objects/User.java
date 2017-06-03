package com.example.dell.menu.objects;

/**
 * Created by Dell on 25.05.2017.
 */

public class User {
    private String login;
    private String password;
    private Long userId;

    public User(String login, String password){
        this.login = login;
        this.password = password;

        userId = Long.valueOf(-1);
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long usersId) {
        this.userId = usersId;
    }
}
