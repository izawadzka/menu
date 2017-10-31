package com.example.dell.menu.events.shoppinglists;

/**
 * Created by Dell on 31.10.2017.
 */

public class SynchronizeShoppingListWithFridgeButtonClickedEvent {
    public int shoppingListId;

    public SynchronizeShoppingListWithFridgeButtonClickedEvent(int shoppingListId){
        this.shoppingListId = shoppingListId;
    }
}
