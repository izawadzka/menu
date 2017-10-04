package com.example.dell.menu.events.shoppingLists;

import com.example.dell.menu.objects.ShoppingList;

/**
 * Created by Dell on 05.09.2017.
 */

public class EditShoppingListNameEvent {
    public int shoppingListId;
    public String shoppingListName;

    public EditShoppingListNameEvent(int shoppingListId, String shoppingListName){
        this.shoppingListId = shoppingListId;
        this.shoppingListName = shoppingListName;
    }
}
