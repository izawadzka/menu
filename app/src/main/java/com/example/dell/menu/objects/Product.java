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
    private int amountOfProteinsPer100g;
    private int amountOfCarbosPer100g;
    private int amountOfFatPer100g;

    public Product(int productId, String name, int numberOfKcalPer100g, String type,
                   String storageType, int amountOfProteinsPer100g, int amountOfCarbosPer100g,
                   int amountOfFatPer100g){
        this.productId = productId;
        this.name = name;
        this.numberOfKcalPer100g = numberOfKcalPer100g;
        this.type = type;
        this.storageType =storageType;
        this.amountOfProteinsPer100g = amountOfProteinsPer100g;
        this.amountOfCarbosPer100g = amountOfCarbosPer100g;
        this.amountOfFatPer100g = amountOfFatPer100g;
    }

    public Product(int productsId, String name, int numberOfKcalPer100g,
                   int amountOfProteins, int amountOfCarbos, int amountOfFat){
        this.productId = productsId;
        this.name = name;
        this.numberOfKcalPer100g = numberOfKcalPer100g;
        this.amountOfProteinsPer100g = amountOfProteins;
        this.amountOfCarbosPer100g = amountOfCarbos;
        this.amountOfFatPer100g = amountOfFat;
    }


    public Product(int productId, String name, double quantity, String storageType){
        this.productId = productId;
        this.name = name;
        this.quantity = quantity;
        this.storageType = storageType;
    }

    public Product(int productId, String name, String type, String storageType){
        this.productId = productId;
        this.name = name;
        this.type = type;
        this.storageType = storageType;
    }

    public Product(int productId, double quantity){
        this.productId = productId;
        this.quantity = quantity;
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

    public Product(String name, double quantity, String storageType){  //constructor for products that are to be showed in meals full activity
        this.name = name;
        this.quantity = quantity;
        this.storageType = storageType;
    }

    public void addQuantity(double quantity){
        this.quantity += quantity;
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

    public int getAmountOfProteinsPer100g() {
        return amountOfProteinsPer100g;
    }

    public int getAmountOfCarbosPer100g() {
        return amountOfCarbosPer100g;
    }

    public int getAmountOfFatPer100g() {
        return amountOfFatPer100g;
    }
}
