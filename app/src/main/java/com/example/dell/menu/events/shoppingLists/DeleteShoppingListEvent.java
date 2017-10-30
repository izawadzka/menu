package com.example.dell.menu.events.shoppinglists;

import com.example.dell.menu.objects.shoppinglist.ShoppingList;

/**
 * Created by Dell on 27.08.2017.
 */

public class DeleteShoppingListEvent {
    public ShoppingList shoppingList;
    public DeleteShoppingListEvent(ShoppingList shoppingList){
        this.shoppingList = shoppingList;
    }
}
