package com.example.dell.menu.events.menus;

import com.example.dell.menu.objects.DailyMenu;

/**
 * Created by Dell on 06.06.2017.
 */

public class AddNewDailyMenuEvent {
    public DailyMenu dailyMenu;
    public AddNewDailyMenuEvent(DailyMenu dailyMenu){
        this.dailyMenu = dailyMenu;
    }
}
