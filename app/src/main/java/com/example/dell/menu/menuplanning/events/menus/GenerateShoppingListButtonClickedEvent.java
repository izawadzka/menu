package com.example.dell.menu.menuplanning.events.menus;

import com.example.dell.menu.menuplanning.objects.Menu;

/**
 * Created by Dell on 07.06.2017.
 */

public class GenerateShoppingListButtonClickedEvent {
    public Menu menu;
    public GenerateShoppingListButtonClickedEvent(Menu menu){
        this.menu= menu;
    }
}
