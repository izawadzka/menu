package com.example.dell.menu.events.meals;

/**
 * Created by Dell on 26.10.2017.
 */

public class ProductAddedSuccessfullyToIngredientsEvent {
    public String productName;

    public ProductAddedSuccessfullyToIngredientsEvent(String productName){
        this.productName = productName;
    }
}
