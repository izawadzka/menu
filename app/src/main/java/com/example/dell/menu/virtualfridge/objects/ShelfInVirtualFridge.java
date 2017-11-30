package com.example.dell.menu.virtualfridge.objects;

import android.util.Log;

import com.example.dell.menu.menuplanning.objects.Product;
import com.example.dell.menu.virtualfridge.flags.ProductFlags;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 29.11.2017.
 */

public class ShelfInVirtualFridge implements Serializable{
    private int shelfId;
    private long dailyMenuId;
    private boolean archived = false;
    private boolean extraShelf = false;
    private String date;

    private List<Product> boughtProducts = new ArrayList<>();
    private List<Product> productsToBuy = new ArrayList<>();
    private List<Product> productsOnShoppingLists = new ArrayList<>();
    private List<Product> eatenProducts = new ArrayList<>();
    private List<Product> notEatenProducts = new ArrayList<>();

    public ShelfInVirtualFridge(int shelfId, int dailyMenuId, boolean archived,
                                String date){
        this.shelfId = shelfId;
        this.dailyMenuId = dailyMenuId;
        this.archived = archived;
        this.date = date;

        extraShelf = dailyMenuId == 0;
    }

    public ShelfInVirtualFridge(long dailyMenuId) {
        this.dailyMenuId = dailyMenuId;
    }

    public boolean isExtraShelf() {
        return extraShelf;
    }

    public void setExtraShelf(boolean extraShelf) {
        this.extraShelf = extraShelf;
    }

    public String getDate() {
        return date;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public int getShelfId() {
        return shelfId;
    }

    public void setShelfId(int shelfId) {
        this.shelfId = shelfId;
    }

    public long getDailyMenuId() {
        return dailyMenuId;
    }

    public void setDailyMenuId(int dailyMenuId) {
        this.dailyMenuId = dailyMenuId;
    }


    public List<Product> getBoughtProducts() {
        return boughtProducts;
    }

    public List<Product> getProductsToBuy() {
        return productsToBuy;
    }

    public List<Product> getProductsOnShoppingLists() {
        return productsOnShoppingLists;
    }

    public List<Product> getEatenProducts() {
        return eatenProducts;
    }

    public List<Product> getNotEatenProducts() {
        return notEatenProducts;
    }

    public void addProduct(Product product) {
        switch (product.getProductFlagId()){
            case ProductFlags.BOUGHT_INDX: boughtProducts.add(product);
                break;
            case ProductFlags.NEED_TO_BE_BOUGHT_IND: productsToBuy.add(product);
                break;
            case ProductFlags.ADDED_TO_SHOPPING_LIST_INDX: productsOnShoppingLists.add(product);
                break;
            case ProductFlags.EATEN_INDX: eatenProducts.add(product);
                break;
            case ProductFlags.WASNT_USED_INDX: notEatenProducts.add(product);
                break;
        }
    }

    public String getLabel(){
        if(extraShelf) return "EXTRA SHELF";
        else return date;
    }
}
