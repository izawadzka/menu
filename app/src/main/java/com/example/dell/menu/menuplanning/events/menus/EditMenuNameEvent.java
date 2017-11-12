package com.example.dell.menu.menuplanning.events.menus;

import com.example.dell.menu.menuplanning.objects.Menu;

/**
 * Created by Dell on 27.08.2017.
 */

public class EditMenuNameEvent {
    public Menu menu;
    public EditMenuNameEvent(Menu menu){
        this.menu = menu;
    }
}
