package com.example.dell.menu.menuplanning.events.products;

/**
 * Created by Dell on 05.10.2017.
 */

public class UpdateProductEvent {
    public int productId;
    public UpdateProductEvent(int productId){
        this.productId = productId;
    }
}
