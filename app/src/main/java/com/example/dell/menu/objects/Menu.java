package com.example.dell.menu.objects;

import java.util.Date;

/**
 * Created by Dell on 04.06.2017.
 */

public class Menu {
    private int menuId;
    private String name;
    private String creationDateString;
    private java.sql.Date creationDate;
    private int cumulativeNumberOfKcal;
    private long authorsId;
    private String authorsName;

    public Menu(String name, java.sql.Date creationDate, int cumulativeNumberOfKcal, long authorsId){
        this.name = name;
        this.creationDate = creationDate;
        this.cumulativeNumberOfKcal = cumulativeNumberOfKcal;
        this.authorsId = authorsId;
        creationDateString = creationDate.toString();
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMenuId() {
        return menuId;
    }

    public String getName() {
        return name;
    }

    public String getCreationDate() {
        return creationDateString;
    }

    public int getCumulativeNumberOfKcal() {
        return cumulativeNumberOfKcal;
    }

    public long getAuthorsId() {
        return authorsId;
    }

    public void setCumulativeNumberOfKcal(int cumulativeNumberOfKcal) {
        this.cumulativeNumberOfKcal = cumulativeNumberOfKcal;
    }
}
