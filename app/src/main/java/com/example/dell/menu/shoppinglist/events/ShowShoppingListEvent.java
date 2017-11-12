package com.example.dell.menu.shoppinglist.events;

import com.example.dell.menu.menuplanning.objects.Menu;

/**
 * Created by Dell on 07.06.2017.
 */

public class ShowShoppingListEvent {
    public Menu menu;
    public ShowShoppingListEvent(Menu menu){
        this.menu = menu;
    }
}
