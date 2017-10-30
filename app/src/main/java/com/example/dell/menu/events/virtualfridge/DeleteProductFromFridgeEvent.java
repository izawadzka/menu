package com.example.dell.menu.events.virtualfridge;

/**
 * Created by Dell on 30.10.2017.
 */

public class DeleteProductFromFridgeEvent {
    public int productId;

    public DeleteProductFromFridgeEvent(int productId){
        this.productId = productId;
    }
}
