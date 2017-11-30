package com.example.dell.menu.menuplanning.objects;

import com.example.dell.menu.menuplanning.types.MealsType;

import java.io.Serializable;
import java.util.Vector;

/**
 * Created by Dell on 04.06.2017.
 */

public class DailyMenu implements Serializable{
    private Long dailyMenuId;
    private String date;

    private int cumulativeNumberOfKcal = 0;
    private int cumulativeAmountOfProteins;
    private int cumulativeAmountOfCarbons;
    private int cumulativeAmountOfFat;

    private int amountOfServingsInBreakfast = 1;
    private int amountOfServingsInLunch = 1;
    private int amountOfServingsInDinner = 1;
    private int amountOfServingsInTeatime = 1;
    private int amountOfServingsInSupper = 1;

    private boolean alreadyUsed = false;
    private boolean alreadySynchronizedWithVirtualFridge = false;

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

    public DailyMenu(long dailyMenuId){
        this.dailyMenuId = dailyMenuId;
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

    public boolean isAlreadyUsed() {
        return alreadyUsed;
    }

    public void setAlreadyUsed(boolean alreadyUsed) {
        this.alreadyUsed = alreadyUsed;
    }

    public boolean isAlreadySynchronizedWithVirtualFridge() {
        return alreadySynchronizedWithVirtualFridge;
    }

    public void setAlreadySynchronizedWithVirtualFridge(boolean alreadySynchronizedWithVirtualFridge) {
        this.alreadySynchronizedWithVirtualFridge = alreadySynchronizedWithVirtualFridge;
    }

    public void setAmountOfServings(int amount, int indxOfMeal){

        switch (indxOfMeal){
            case MealsType.BREAKFAST_INDX: amountOfServingsInBreakfast = amount;
                break;
            case MealsType.LUNCH_INDX: amountOfServingsInLunch = amount;
                break;
            case MealsType.DINNER_INDX: amountOfServingsInDinner = amount;
                break;
            case MealsType.TEATIME_INDX: amountOfServingsInTeatime = amount;
                break;
            case MealsType.SUPPER_INDX: amountOfServingsInSupper = amount;
                break;
        }
    }

    public int getAmountOfServings(int indxOfMeal){
        switch (indxOfMeal){
            case MealsType.BREAKFAST_INDX: return amountOfServingsInBreakfast;
            case MealsType.LUNCH_INDX: return amountOfServingsInLunch;
            case MealsType.DINNER_INDX: return amountOfServingsInDinner;
            case MealsType.TEATIME_INDX: return amountOfServingsInTeatime;
            case MealsType.SUPPER_INDX: return amountOfServingsInSupper;
            default: return -1;
        }
    }

    public int getCumulativeAmountOfProteins() {
        return cumulativeAmountOfProteins;
    }

    public void setCumulativeAmountOfProteins(int cumulativeAmountOfProteins) {
        this.cumulativeAmountOfProteins = cumulativeAmountOfProteins;
    }

    public int getCumulativeAmountOfCarbons() {
        return cumulativeAmountOfCarbons;
    }

    public void setCumulativeAmountOfCarbons(int cumulativeAmountOfCarbons) {
        this.cumulativeAmountOfCarbons = cumulativeAmountOfCarbons;
    }

    public int getCumulativeAmountOfFat() {
        return cumulativeAmountOfFat;
    }

    public void setCumulativeAmountOfFat(int cumulativeAmountOfFat) {
        this.cumulativeAmountOfFat = cumulativeAmountOfFat;
    }

    public int getAmountOfServingsInBreakfast() {
        return amountOfServingsInBreakfast;
    }

    public void setAmountOfServingsInBreakfast(int amountOfServingsInBreakfast) {
        this.amountOfServingsInBreakfast = amountOfServingsInBreakfast;
    }

    public int getAmountOfServingsInLunch() {
        return amountOfServingsInLunch;
    }

    public void setAmountOfServingsInLunch(int amountOfServingsInLunch) {
        this.amountOfServingsInLunch = amountOfServingsInLunch;
    }

    public int getAmountOfServingsInDinner() {
        return amountOfServingsInDinner;
    }

    public void setAmountOfServingsInDinner(int amountOfServingsInDinner) {
        this.amountOfServingsInDinner = amountOfServingsInDinner;
    }

    public int getAmountOfServingsInTeatime() {
        return amountOfServingsInTeatime;
    }

    public void setAmountOfServingsInTeatime(int amountOfServingsInTeatime) {
        this.amountOfServingsInTeatime = amountOfServingsInTeatime;
    }

    public int getAmountOfServingsInSupper() {
        return amountOfServingsInSupper;
    }

    public void setAmountOfServingsInSupper(int amountOfServingsInSupper) {
        this.amountOfServingsInSupper = amountOfServingsInSupper;
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


    public void addMeal(Meal meal, int mealTypeIndx){
        switch (mealTypeIndx){
            case MealsType.BREAKFAST_INDX: breakfast.add(meal);
                break;
            case MealsType.LUNCH_INDX: lunch.add(meal);
                break;
            case MealsType.DINNER_INDX: dinner.add(meal);
                break;
            case MealsType.TEATIME_INDX: teatime.add(meal);
                break;
            case MealsType.SUPPER_INDX: supper.add(meal);
                break;
        }
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

    public void setDailyMenuDate(String dailyMenuDate) {
        this.date = dailyMenuDate;
    }

    public void setCumulativeNumberOfKcal(int cumulativeNumberOfKcal) {
        this.cumulativeNumberOfKcal = cumulativeNumberOfKcal;
    }
}
