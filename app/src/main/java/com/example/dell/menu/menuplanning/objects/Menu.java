package com.example.dell.menu.menuplanning.objects;

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
    private int amountOfProteins;
    private int amountOfCarbos;
    private int amountOfFat;

    public Menu(String name, java.sql.Date creationDate, int cumulativeNumberOfKcal, long authorsId){
        this.name = name;
        this.creationDate = creationDate;
        this.cumulativeNumberOfKcal = cumulativeNumberOfKcal;
        this.authorsId = authorsId;
        creationDateString = creationDate.toString();
    }

    public Menu(String name, java.sql.Date creationDate, int cumulativeNumberOfKcal, int authorsId,
                int amountOfProteins, int amountOfCarbons, int amountOfFat) {
        this.name = name;
        this.creationDate = creationDate;
        creationDateString = creationDate.toString();
        this.cumulativeNumberOfKcal = cumulativeNumberOfKcal;
        this.authorsId = authorsId;
        this.amountOfProteins = amountOfProteins;
        this.amountOfCarbos = amountOfCarbons;
        this.amountOfFat = amountOfFat;
    }

    public void setAmountOfProteins(int amountOfProteins) {
        this.amountOfProteins = amountOfProteins;
    }

    public void setAmountOfCarbos(int amountOfCarbos) {
        this.amountOfCarbos = amountOfCarbos;
    }

    public void setAmountOfFat(int amountOfFat) {
        this.amountOfFat = amountOfFat;
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
