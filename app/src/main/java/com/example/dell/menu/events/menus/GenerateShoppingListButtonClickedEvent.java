package com.example.dell.menu.events.menus;

import com.example.dell.menu.objects.Menu;

import java.security.PublicKey;

/**
 * Created by Dell on 07.06.2017.
 */

public class GenerateShoppingListButtonClickedEvent {
    public Menu menu;
    public GenerateShoppingListButtonClickedEvent(Menu menu){
        this.menu= menu;
    }
}