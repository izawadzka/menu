package com.example.dell.menu.shoppinglist.events;

import com.example.dell.menu.menuplanning.objects.Menu;

/**
 * Created by Dell on 04.10.2017.
 */

public class GenerateShoppingListEvent {
    public Menu menu;
    public GenerateShoppingListEvent(Menu menu){
        this.menu = menu;
    }
}
