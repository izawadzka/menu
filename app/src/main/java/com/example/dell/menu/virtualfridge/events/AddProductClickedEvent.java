package com.example.dell.menu.virtualfridge.events;

/**
 * Created by Dell on 30.10.2017.
 */

public class AddProductClickedEvent {
    public int productId;
    public double quantity, maxQuantity;
    public String name;

    public AddProductClickedEvent(int productId, double quantity, String name, double maxQuantity){
        this.productId = productId;
        this.quantity = quantity;
        this.name = name;
        this.maxQuantity = maxQuantity;
    }
}
