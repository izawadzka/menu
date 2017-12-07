package com.example.dell.menu.virtualfridge.events;

import com.example.dell.menu.menuplanning.objects.Product;

/**
 * Created by Dell on 05.12.2017.
 */

public class DeleteProductFromBoughtEvent {
    public long shelfId;
    public Product product;
    public DeleteProductFromBoughtEvent(Product product, long shelfId){
        this.product = product;
        this.shelfId = shelfId;
    }
}
