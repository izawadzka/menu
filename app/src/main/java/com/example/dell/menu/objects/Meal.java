package com.example.dell.menu.objects;

import java.io.Serializable;

/**
 * Created by Dell on 27.05.2017.
 */

public class Meal implements Serializable{
    private int mealsId;
    private String name;
    private int cumulativeNumberOfKcal;
    private int authorsId;
    private String recipe;
    private String authorsName;
    private int amountOfProteinsPer100g;
    private int amountOfCarbosPer100g;
    private int amountOfFatPer100g;

    public Meal(int mealsId){
        this.mealsId = mealsId;
    }
    public Meal(int mealsId, String name){
        this.mealsId = mealsId;
        this.name = name;
    }

    public Meal(int mealsId, String name, int cumulativeNumberOfKcal,int authorsId, String recipe){
        this.mealsId = mealsId;
        this.name = name;
        this.cumulativeNumberOfKcal = cumulativeNumberOfKcal;
        this.authorsId = authorsId;
        this.recipe = recipe;
    }

    public Meal(int mealsId, String name, int cumulativeNumberOfKcal,int authorsId, String recipe,
                int amountOfProteins, int amountOfCarbons, int amountOfFat){
        this.mealsId = mealsId;
        this.name = name;
        this.cumulativeNumberOfKcal = cumulativeNumberOfKcal;
        this.authorsId = authorsId;
        this.recipe = recipe;
        this.amountOfProteinsPer100g = amountOfProteins;
        this.amountOfCarbosPer100g = amountOfCarbons;
        this.amountOfFatPer100g = amountOfFat;
    }

    public Meal(String name){
        this.name = name;
    }

    public Meal(int mealsId, String name, int amountOfKCal, int amountOfProteins, int amountOfCarbons, int amountOfFat) {
        this.mealsId = mealsId;
        this.name = name;
        this.cumulativeNumberOfKcal = amountOfKCal;
        this.amountOfProteinsPer100g = amountOfProteins;
        this.amountOfCarbosPer100g = amountOfCarbons;
        this.amountOfFatPer100g = amountOfFat;
    }

    public Meal(String mealsName, int amountOfKcal, int authorsId, String mealsRecipe, int amountOfProteins, int amountOfCarbons, int amountOfFat) {
        this.name = mealsName;
        this.cumulativeNumberOfKcal = amountOfKcal;
        this.authorsId = authorsId;
        this.recipe = mealsRecipe;
        this.amountOfProteinsPer100g = amountOfProteins;
        this.amountOfCarbosPer100g = amountOfCarbons;
        this.amountOfFatPer100g = amountOfFat;
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

    @Override
    public String toString() {
        return name;
    }

    public int getAmountOfProteinsPer100g() {
        return amountOfProteinsPer100g;
    }

    public int getAmountOfCarbosPer100g() {
        return amountOfCarbosPer100g;
    }

    public int getAmountOfFatPer100g() {
        return amountOfFatPer100g;
    }
}
