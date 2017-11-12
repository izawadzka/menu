package com.example.dell.menu.menuplanning.objects;

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
    private int amountOfProteins;
    private int amountOfCarbos;
    private int amountOfFat;

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
        this.amountOfProteins = amountOfProteins;
        this.amountOfCarbos = amountOfCarbons;
        this.amountOfFat = amountOfFat;
    }

    public Meal(String name){
        this.name = name;
    }

    public Meal(int mealsId, String name, int amountOfKCal, int amountOfProteins, int amountOfCarbons, int amountOfFat) {
        this.mealsId = mealsId;
        this.name = name;
        this.cumulativeNumberOfKcal = amountOfKCal;
        this.amountOfProteins = amountOfProteins;
        this.amountOfCarbos = amountOfCarbons;
        this.amountOfFat = amountOfFat;
    }

    public Meal(String mealsName, int amountOfKcal, int authorsId, String mealsRecipe, int amountOfProteins, int amountOfCarbons, int amountOfFat) {
        this.name = mealsName;
        this.cumulativeNumberOfKcal = amountOfKcal;
        this.authorsId = authorsId;
        this.recipe = mealsRecipe;
        this.amountOfProteins = amountOfProteins;
        this.amountOfCarbos = amountOfCarbons;
        this.amountOfFat = amountOfFat;
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

    public int getAmountOfProteins() {
        return amountOfProteins;
    }

    public int getAmountOfCarbos() {
        return amountOfCarbos;
    }

    public int getAmountOfFat() {
        return amountOfFat;
    }
}
