package com.example.dell.menu.data.backup.events;

/**
 * Created by Dell on 13.11.2017.
 */

public class SearchingForUserResultEvent {
    public boolean result;
    public String login, password;
    public SearchingForUserResultEvent(boolean result, String login, String password){
        this.result = result;
        this.login = login;
        this.password = password;
    }
}
