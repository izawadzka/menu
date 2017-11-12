package com.example.dell.menu.virtualfridge.events;

/**
 * Created by Dell on 30.10.2017.
 */

public class AmountOfProductChangedEvent {
    public int productId;
    public double quantity;

    public AmountOfProductChangedEvent(int productId, double quantity){
        this.productId = productId;
        this.quantity = quantity;
    }
}
