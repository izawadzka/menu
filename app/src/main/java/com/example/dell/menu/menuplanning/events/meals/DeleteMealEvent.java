package com.example.dell.menu.menuplanning.events.meals;

import com.example.dell.menu.menuplanning.objects.Meal;

/**
 * Created by Dell on 03.06.2017.
 */

public class DeleteMealEvent {
    public Meal meal;
    public DeleteMealEvent(Meal meal){
        this.meal = meal;
    }
}
