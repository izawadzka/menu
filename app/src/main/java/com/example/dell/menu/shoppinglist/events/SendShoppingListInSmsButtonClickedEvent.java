package com.example.dell.menu.shoppinglist.events;

/**
 * Created by Dell on 22.11.2017.
 */

public class SendShoppingListInSmsButtonClickedEvent {
    public long shoppingListId;
    public SendShoppingListInSmsButtonClickedEvent(long shoppingListId){
        this.shoppingListId = shoppingListId;
    }
}
