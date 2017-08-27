package com.example.dell.menu.events.menus;

import com.example.dell.menu.objects.DailyMenu;

/**
 * Created by Dell on 16.08.2017.
 */

public class DailyMenuHasChangedEvent {
    public DailyMenu dailyMenu;
    public DailyMenuHasChangedEvent(DailyMenu dailyMenu){
        this.dailyMenu = dailyMenu;
    }
}
