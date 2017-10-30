package com.example.dell.menu.events.menus;

import com.example.dell.menu.objects.menuplanning.Menu;

/**
 * Created by Dell on 27.08.2017.
 */

public class DeleteMenuEvent {
    public Menu menu;
    public DeleteMenuEvent(Menu menu){
        this.menu = menu;
    }
}
