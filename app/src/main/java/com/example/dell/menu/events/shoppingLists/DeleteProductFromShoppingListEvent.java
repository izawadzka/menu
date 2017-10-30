package com.example.dell.menu.events.shoppinglists;

/**
 * Created by Dell on 04.09.2017.
 */

public class DeleteProductFromShoppingListEvent {
    public int productId;
    public DeleteProductFromShoppingListEvent(int productId) {
        this.productId = productId;
    }
}
