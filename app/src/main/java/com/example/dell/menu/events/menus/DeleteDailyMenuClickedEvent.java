package com.example.dell.menu.events.menus;

import com.example.dell.menu.objects.DailyMenu;

/**
 * Created by Dell on 27.10.2017.
 */

public class DeleteDailyMenuClickedEvent {
    public DailyMenu dailyMenu;

    public DeleteDailyMenuClickedEvent(DailyMenu dailyMenu){
        this.dailyMenu = dailyMenu;
    }
}
