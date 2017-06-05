package com.example.dell.menu.objects;

import java.util.Date;
import java.util.Vector;

/**
 * Created by Dell on 04.06.2017.
 */

public class DailyMenu {
    private int dailyMenuId;
    private Date date;
    private Vector<Integer> breakfast;
    private Vector<Integer> lunch;
    private Vector<Integer> dinner;
    private Vector<Integer> teatime;
    private Vector<Integer> supper;


    public DailyMenu(Date date, Vector<Integer> breakfast, Vector<Integer> lunch, Vector<Integer> dinner, Vector<Integer> teatime, Vector<Integer> supper){
        this.date = date;
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.dinner = dinner;
        this.teatime = teatime;
        this.supper = supper;
    }

    public void setDailyMenuId(int dailyMenuId) {
        this.dailyMenuId = dailyMenuId;
    }

    public int getDailyMenuId() {
        return dailyMenuId;
    }

    public Date getDate() {
        return date;
    }

    public Vector<Integer> getBreakfast() {
        return breakfast;
    }

    public Vector<Integer> getLunch() {
        return lunch;
    }

    public Vector<Integer> getDinner() {
        return dinner;
    }

    public Vector<Integer> getTeatime() {
        return teatime;
    }

    public Vector<Integer> getSupper() {
        return supper;
    }
}
