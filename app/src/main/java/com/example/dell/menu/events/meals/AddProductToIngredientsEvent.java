package com.example.dell.menu.events.meals;

import com.example.dell.menu.objects.Product;

/**
 * Created by Dell on 01.06.2017.
 */

public class AddProductToIngredientsEvent {
    public Product product;

    public AddProductToIngredientsEvent(Product product){
        this.product = product;
    }
}
