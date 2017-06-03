package com.example.dell.menu.objects;

/**
 * Created by Dell on 27.05.2017.
 */

public class Meal {
    private Long mealsId;
    private String name;
    private int cumulativeNumberOfKcal;
    private Long authorsId;
    private String recipe;
    private String authorsName;

    public Meal(Long mealsId, String name, int cumulativeNumberOfKcal, Long authorsId, String recipe){
        this.mealsId = mealsId;
        this.name = name;
        this.cumulativeNumberOfKcal = cumulativeNumberOfKcal;
        this.authorsId = authorsId;
        this.recipe = recipe;
    }

    public Meal(String name, int cumulativeNumberOfKcal, Long authorsId, String recipe){
        this.name = name;
        this.cumulativeNumberOfKcal = cumulativeNumberOfKcal;
        this.authorsId = authorsId;
        this.recipe = recipe;
    }

    public Long getMealsId() {
        return mealsId;
    }

    public String getName() {
        return name;
    }

    public int getCumulativeNumberOfKcal() {
        return cumulativeNumberOfKcal;
    }

    public Long getAuthorsId() {
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
