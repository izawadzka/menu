package com.example.dell.menu.shoppinglist.objects;


import java.sql.Date;

/**
 * Created by Dell on 07.06.2017.
 */

public class ShoppingList {
    private long shoppingListId;
    private String name;
    private Date creationDate;
    private long authorsId;
    private String authorsName;
    private boolean alreadySynchronized;

    public ShoppingList(String name, Date creationDate, long authorsId){
        this.name = name;
        this.creationDate = creationDate;
        this.authorsId = authorsId;
        alreadySynchronized = false;
    }

    public void setAlreadySynchronized(boolean alreadySynchronized){
        this.alreadySynchronized = alreadySynchronized;
    }

    public boolean isAlreadySynchronized(){
        return alreadySynchronized;
    }

    public String getAuthorsName() {
        return authorsName;
    }

    public void setAuthorsName(String authorsName) {
        this.authorsName = authorsName;
    }

    public long getShoppingListId() {
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
