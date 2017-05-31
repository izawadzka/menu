package com.example.dell.menu.objects;

/**
 * Created by Dell on 27.05.2017.
 */

public class Meal {
    private int mealsId;
    private String name;
    private int cumulativeNumberOfKcalPer100g;
    private int authorsId;
    private String recipe;
    private String authorsName;

    public Meal(int mealsId, String name, int cumulativeNumberOfKcalPer100g, int authorsId, String recipe){
        this.mealsId = mealsId;
        this.name = name;
        this.cumulativeNumberOfKcalPer100g = cumulativeNumberOfKcalPer100g;
        this.authorsId = authorsId;
        this.recipe = recipe;
    }

    public int getMealsId() {
        return mealsId;
    }

    public String getName() {
        return name;
    }

    public int getCumulativeNumberOfKcalPer100g() {
        return cumulativeNumberOfKcalPer100g;
    }

    public int getAuthorsId() {
        return authorsId;
    }

    public String getRecipe() {
        return recipe;
    }

    public String getAuthorsName() {
        return authorsName;
    }

    public void setAuthorsName(String authorsName) {
        this.authorsName = authorsName;
    }
}
