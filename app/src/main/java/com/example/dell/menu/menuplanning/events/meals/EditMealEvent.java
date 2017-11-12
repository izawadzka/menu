package com.example.dell.menu.menuplanning.events.meals;

import com.example.dell.menu.menuplanning.objects.Meal;

/**
 * Created by Dell on 03.06.2017.
 */

public class EditMealEvent {
    public final Meal meal;

    public EditMealEvent(Meal meal) {
        this.meal = meal;
    }
}
