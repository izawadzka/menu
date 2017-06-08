package com.example.dell.menu.objects;

/**
 * Created by Dell on 27.05.2017.
 */

public class Meal {
    private int mealsId;
    private String name;
    private int cumulativeNumberOfKcal;
    private int authorsId;
    private String recipe;
    private String authorsName;

    public Meal(int mealsId){
        this.mealsId = mealsId;
    }

    public Meal(int mealsId, String name, int cumulativeNumberOfKcal,int authorsId, String recipe){
        this.mealsId = mealsId;
        this.name = name;
        this.cumulativeNumberOfKcal = cumulativeNumberOfKcal;
        this.authorsId = authorsId;
        this.recipe = recipe;
    }

    public Meal(String name){
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Meal(String name, int cumulativeNumberOfKcal, int authorsId, String recipe){
        this.name = name;
        this.cumulativeNumberOfKcal = cumulativeNumberOfKcal;
        this.authorsId = authorsId;
        this.recipe = recipe;
    }

    public int getMealsId() {
        return mealsId;
    }

    public String getName() {
        return name;
    }

    public int getCumulativeNumberOfKcal() {
        return cumulativeNumberOfKcal;
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
