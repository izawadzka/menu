package com.example.dell.menu.objects.shoppinglist;


import java.sql.Date;

/**
 * Created by Dell on 07.06.2017.
 */

public class ShoppingList {
    private int shoppingListId;
    private String name;
    private Date creationDate;
    private long authorsId;
    private String authorsName;

    public ShoppingList(String name, Date creationDate, long authorsId){
        this.name = name;
        this.creationDate = creationDate;
        this.authorsId = authorsId;
    }

    public String getAuthorsName() {
        return authorsName;
    }

    public void setAuthorsName(String authorsName) {
        this.authorsName = authorsName;
    }

    public int getShoppingListId() {
        return shoppingListId;
    }

    public void setShoppingListId(int shoppingListId) {
        this.shoppingListId = shoppingListId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreationDate() {
        return creationDate.toString();
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public long getAuthorsId() {
        return authorsId;
    }

    public void setAuthorsId(int authorsId) {
        this.authorsId = authorsId;
    }
}
