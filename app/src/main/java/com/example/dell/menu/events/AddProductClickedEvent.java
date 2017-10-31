package com.example.dell.menu.events;

/**
 * Created by Dell on 30.10.2017.
 */

public class AddProductClickedEvent {
    public int productId;
    public double quantity;
    public String name;

    public AddProductClickedEvent(int productId, double quantity, String name){
        this.productId = productId;
        this.quantity = quantity;
        this.name = name;
    }
}
