package com.example.dell.menu.menuplanning.events.menus;

import com.example.dell.menu.menuplanning.objects.DailyMenu;

/**
 * Created by Dell on 16.08.2017.
 */

public class DailyMenuHasChangedEvent {
    public DailyMenu dailyMenu;
    public DailyMenuHasChangedEvent(DailyMenu dailyMenu){
        this.dailyMenu = dailyMenu;
    }
}
