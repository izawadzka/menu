package com.example.dell.menu.menuplanning.events.menus;

import com.example.dell.menu.menuplanning.objects.DailyMenu;

/**
 * Created by Dell on 06.06.2017.
 */

public class ShowDailyMenuEvent {
    public final DailyMenu dailyMenu;
    public final boolean is_showe_mode;
    public ShowDailyMenuEvent(DailyMenu dailyMenu, boolean is_showe_mode){
        this.dailyMenu = dailyMenu;
        this.is_showe_mode = is_showe_mode;
    }
}
