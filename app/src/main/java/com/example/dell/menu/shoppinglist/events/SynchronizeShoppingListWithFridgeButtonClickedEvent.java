package com.example.dell.menu.shoppinglist.events;

/**
 * Created by Dell on 31.10.2017.
 */

public class SynchronizeShoppingListWithFridgeButtonClickedEvent {
    public long shoppingListId;

    public SynchronizeShoppingListWithFridgeButtonClickedEvent(long shoppingListId){
        this.shoppingListId = shoppingListId;
    }
}
