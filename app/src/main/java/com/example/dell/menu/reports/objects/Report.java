package com.example.dell.menu.reports.objects;

import com.example.dell.menu.menuplanning.types.ProductsType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dell on 09.06.2017.
 */

public class Report {
    public final static String BREAKFAST_KEY = "breakfast";
    public final static String LUNCH_KEY = "lunch";
    public final static String TEATIME_KEY = "teatime";
    public final static String SUPPER_KEY = "supper";
    public final static String DINNER_KEY = "dinner";

    public final static int VEGETABLES = 0;
    public final static int FRUITS = 1;
    public final static int LIQUID = 2;
    public final static int MEAT = 3;
    public final static int  FAT = 4;
    public final static int DRY_GOODS = 5;
    public final static int SPICE = 6;
    public final static int DAIRY_PRODUCTS = 7;
    public final static int FISH = 8;
    public final static int BAKED_GOODS = 9;




    private Map<String, Integer> breakfasts;
    private Map<String, Integer> lunches;
    private Map<String, Integer> dinners;
    private Map<String, Integer> teatimes;
    private Map<String, Integer> suppers;

    private int[] types;


    public Report(){
        breakfasts = new HashMap<>();
        lunches = new HashMap<>();
        dinners = new HashMap<>();
        teatimes = new HashMap<>();
        suppers = new HashMap<>();
        types = new int[10];
    }

    public void addValues(String mealType, String key, int value){
        switch (mealType){
            case BREAKFAST_KEY: breakfasts.put(key, value);
                                break;
            case LUNCH_KEY: lunches.put(key, value);
                break;
            case DINNER_KEY: dinners.put(key, value);
                break;
            case TEATIME_KEY: teatimes.put(key, value);
                break;
            case SUPPER_KEY: suppers.put(key, value);
                break;
        }
    }

    public void sumUp(){
        int i = 0;
        for (String type : ProductsType.getTypes()) {
            if(breakfasts.get(type) != null)
            types[i] += breakfasts.get(type);

            if(lunches.get(type) != null)
            types[i] += lunches.get(type);

            if(dinners.get(type) != null)
            types[i] += dinners.get(type);

            if(teatimes.get(type) != null)
            types[i] += teatimes.get(type);

            if(suppers.get(type) != null)
            types[i] += suppers.get(type);
            i++;
        }
    }

    public int getValues(int type){
        sumUp();
        return types[type];
    }

    public Map<String, Integer> getBreakfasts() {
        return breakfasts;
    }

    public Map<String, Integer> getLunches() {
        return lunches;
    }

    public Map<String, Integer> getDinners() {
        return dinners;
    }

    public Map<String, Integer> getTeatimes() {
        return teatimes;
    }

    public Map<String, Integer> getSuppers() {
        return suppers;
    }
}
