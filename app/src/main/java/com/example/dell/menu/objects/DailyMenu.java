package com.example.dell.menu.objects;

import java.util.Date;
import java.util.Vector;

/**
 * Created by Dell on 04.06.2017.
 */

public class DailyMenu {
    public static final String BREAKFAST_KEY = "breakfast";
    public static final String LUNCH_KEY = "lunch";
    public static final String DINNER_KEY = "dinner";
    public static final String TEATIME_KEY = "teatime";
    public static final String SUPPER_KEY = "supper";
    private Long dailyMenuId;
    private String date;
    private int cumulativeNumberOfKcal = 0;
    private Vector<Meal> breakfast = new Vector<>();
    private Vector<Meal> lunch = new Vector<>();
    private Vector<Meal> dinner = new Vector<>();
    private Vector<Meal> teatime = new Vector<>();
    private Vector<Meal> supper = new Vector<>();

    public DailyMenu(Long dailyMenuId, String date){
        this.dailyMenuId = dailyMenuId;
        this.date = date;
    }

    public DailyMenu(String date){
        this.date = date;
    }

    public DailyMenu(String date, Vector<Meal> breakfast,Vector<Meal> lunch,
                     Vector<Meal> dinner,Vector<Meal> teatime, Vector<Meal> supper){
        this.date = date;
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.dinner = dinner;
        this.teatime = teatime;
        this.supper = supper;
    }

    public void setDailyMenuId(Long dailyMenuId) {
        this.dailyMenuId = dailyMenuId;
    }

    public Long getDailyMenuId() {
        return dailyMenuId;
    }

    public String getDate() {
        return date;
    }


    public void addMeal(Meal meal, String mealType){
        switch (mealType){
            case BREAKFAST_KEY: breakfast.add(meal);
                                break;
            case LUNCH_KEY: lunch.add(meal);
                                break;
            case DINNER_KEY: dinner.add(meal);
                                break;
            case TEATIME_KEY: teatime.add(meal);
                                break;
            case SUPPER_KEY: supper.add(meal);
                                break;
        }
        cumulativeNumberOfKcal+= meal.getCumulativeNumberOfKcal();
    }

    public int getCumulativeNumberOfKcal() {
        return cumulativeNumberOfKcal;
    }

    public Vector<Meal> getBreakfast() {
        return breakfast;
    }

    public Vector<Meal> getLunch() {
        return lunch;
    }

    public Vector<Meal> getDinner() {
        return dinner;
    }

    public Vector<Meal> getTeatime() {
        return teatime;
    }

    public Vector<Meal> getSupper() {
        return supper;
    }

    public void clearVectors(){
        breakfast.clear();
        lunch.clear();
        dinner.clear();
        teatime.clear();
        supper.clear();
    }
}
