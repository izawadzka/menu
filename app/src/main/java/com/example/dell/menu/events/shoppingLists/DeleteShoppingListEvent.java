package com.example.dell.menu.events.shoppingLists;

import com.example.dell.menu.objects.ShoppingList;

/**
 * Created by Dell on 27.08.2017.
 */

public class DeleteShoppingListEvent {
    public ShoppingList shoppingList;
    public DeleteShoppingListEvent(ShoppingList shoppingList){
        this.shoppingList = shoppingList;
    }
}
