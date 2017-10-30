package com.example.dell.menu.events.virtualfridge;

/**
 * Created by Dell on 30.10.2017.
 */

public class AddProductToFridgeClickedEvent {
    public int productId;
    public double quantity;
    public String name;

    public AddProductToFridgeClickedEvent(int productId, double quantity, String name){
        this.productId = productId;
        this.quantity = quantity;
        this.name = name;
    }
}
