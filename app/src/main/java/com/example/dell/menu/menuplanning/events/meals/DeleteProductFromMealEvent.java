package com.example.dell.menu.menuplanning.events.meals;

import com.example.dell.menu.menuplanning.objects.Product;

/**
 * Created by Dell on 03.06.2017.
 */

public class DeleteProductFromMealEvent {
    public final Product product;

    public DeleteProductFromMealEvent(Product product){
        this.product = product;
    }
}
