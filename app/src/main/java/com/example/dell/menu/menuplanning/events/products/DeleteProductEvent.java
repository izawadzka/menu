package com.example.dell.menu.menuplanning.events.products;

/**
 * Created by Dell on 28.05.2017.
 */

public class DeleteProductEvent {
    private final int productId;

    public DeleteProductEvent(int productId) {
        this.productId = productId;
    }

    public int getProductId() {
        return productId;
    }
}
