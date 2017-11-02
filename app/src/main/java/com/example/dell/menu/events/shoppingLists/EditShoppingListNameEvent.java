package com.example.dell.menu.events.shoppinglists;

/**
 * Created by Dell on 05.09.2017.
 */

public class EditShoppingListNameEvent {
    public long shoppingListId;
    public String shoppingListName;

    public EditShoppingListNameEvent(long shoppingListId, String shoppingListName){
        this.shoppingListId = shoppingListId;
        this.shoppingListName = shoppingListName;
    }
}
