package com.example.dell.menu.objects;

/**
 * Created by Dell on 26.05.2017.
 */

public class Product {
    private int productId;
    private String name;
    private int numberOfKcalPer100g;
    private String type;
    private String storageType;
    private double quantity;

    public Product(int productId, String name, int numberOfKcalPer100g, String type, String storageType){
        this.productId = productId;
        this.name = name;
        this.numberOfKcalPer100g = numberOfKcalPer100g;
        this.type = type;
        this.storageType =storageType;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getProductId() {
        return productId;
    }

    public Product(String name, int quantity, String storageType){  //constructor for products that are to be showed in meals full activity
        this.name = name;
        this.quantity = quantity;
        this.storageType = storageType;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getName() {
        return name;
    }

    public int getNumberOfKcalPer100g() {
        return numberOfKcalPer100g;
    }

    public String getType() {
        return type;
    }

    public String getStorageType() {
        return storageType;
    }
}
