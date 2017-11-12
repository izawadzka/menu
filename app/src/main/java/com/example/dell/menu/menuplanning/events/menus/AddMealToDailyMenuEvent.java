package com.example.dell.menu.menuplanning.events.menus;

import com.example.dell.menu.menuplanning.objects.Meal;

/**
 * Created by Dell on 05.06.2017.
 */

public class AddMealToDailyMenuEvent {
    public Meal meal;
    public String mealType;
    public final long dailyMenuId;

    public AddMealToDailyMenuEvent(Meal meal, String mealType, long dailyMenuId){
        this.meal = meal;
        this.mealType = mealType;
        this.dailyMenuId = dailyMenuId;
    }
}
