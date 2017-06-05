package com.example.dell.menu.events.menus;

import com.example.dell.menu.objects.Meal;

/**
 * Created by Dell on 05.06.2017.
 */

public class AddMealToDailyMenuEvent {
    public Meal meal;
    public String mealType;
    public AddMealToDailyMenuEvent(Meal meal, String mealType){
        this.meal = meal;
        this.mealType = mealType;
    }
}
