package com.example.dell.menu.events.shoppingLists;

import com.example.dell.menu.objects.Menu;

/**
 * Created by Dell on 04.10.2017.
 */

public class GenerateShoppingListEvent {
    public Menu menu;
    public GenerateShoppingListEvent(Menu menu){
        this.menu = menu;
    }
}
