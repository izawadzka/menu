package com.example.dell.menu.user.events;

/**
 * Created by Dell on 12.11.2017.
 */

public class RegisterResultEvent {
    public int result;
    public String login, password;

    public RegisterResultEvent(int result, String  login, String password){
        this.result = result;
        this.login = login;
        this.password = password;
    }
}
