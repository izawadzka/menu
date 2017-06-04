package com.example.dell.menu.objects;

import java.util.Date;

/**
 * Created by Dell on 04.06.2017.
 */

public class Menu {
    private int menuId;
    private String name;
    private Date creationDate;
    private int cumulativeNumberOfKcal;
    private int authorsId;

    public Menu(String name, Date creationDate, int cumulativeNumberOfKcal, int authorsId){
        this.name = name;
        this.creationDate = creationDate;
        this.cumulativeNumberOfKcal = cumulativeNumberOfKcal;
        this.authorsId = authorsId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public int getMenuId() {
        return menuId;
    }

    public String getName() {
        return name;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public int getCumulativeNumberOfKcal() {
        return cumulativeNumberOfKcal;
    }

    public int getAuthorsId() {
        return authorsId;
    }
}
