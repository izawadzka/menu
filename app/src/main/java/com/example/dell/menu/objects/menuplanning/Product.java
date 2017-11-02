package com.example.dell.menu.objects.menuplanning;

import com.example.dell.menu.StorageType;

import java.io.Serializable;

/**
 * Created by Dell on 26.05.2017.
 */

public class Product implements Serializable{
    public static final int DEFAULT_AMOUNT_FOR_VOLUME_OR_WEIGHT = 100;
    private int productId;
    private String name;
    private int kcalPer100g_mlOr1Unit;
    private String type;
    private String storageType;
    private double quantity;
    private int proteinsPer100g_mlOr1Unit;
    private int carbohydratesPer100g_mlOr1Unit;
    private int fatPer100g_mlOr1Unit;
    private double blockedAmount = 0; //used in virtual fridge, amount blocked by shopping lists

    public Product(int productId, String name, int kcalPer100g_mlOr1Unit, String type,
                   String storageType, int proteinsPer100g_mlOr1Unit, int carbohydratesPer100g_mlOr1Unit,
                   int fatPer100g_mlOr1Unit){
        this.productId = productId;
        this.name = name;
        this.kcalPer100g_mlOr1Unit = kcalPer100g_mlOr1Unit;
        this.type = type;
        this.storageType =storageType;
        this.proteinsPer100g_mlOr1Unit = proteinsPer100g_mlOr1Unit;
        this.carbohydratesPer100g_mlOr1Unit = carbohydratesPer100g_mlOr1Unit;
        this.fatPer100g_mlOr1Unit = fatPer100g_mlOr1Unit;
    }

    public Product(int productsId, String name, int kcalPer100g_mlOr1Unit,
                   int amountOfProteins, int amountOfCarbos, int amountOfFat){
        this.productId = productsId;
        this.name = name;
        this.kcalPer100g_mlOr1Unit = kcalPer100g_mlOr1Unit;
        this.proteinsPer100g_mlOr1Unit = amountOfProteins;
        this.carbohydratesPer100g_mlOr1Unit = amountOfCarbos;
        this.fatPer100g_mlOr1Unit = amountOfFat;
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

    public Product(int productId, String name, String type, String storageType, double quantity,
                   double blockedAmount) {
        this.productId = productId;
        this.name = name;
        this.type = type;
        this.quantity = quantity;
        this.storageType = storageType;
        this.blockedAmount = blockedAmount;
    }

    public Product(int productId, String name, String storageType) {
        this.productId = productId;
        this.name = name;
        this.storageType = storageType;
    }

    public double getBlockedAmount() {
        return blockedAmount;
    }

    public void setBlockedAmount(double blockedAmount) {
        this.blockedAmount = blockedAmount;
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

    public int getKcalPer100g_mlOr1Unit() {
        return kcalPer100g_mlOr1Unit;
    }

    public String getType() {
        return type;
    }

    public String getStorageType() {
        return storageType;
    }

    public int getProteinsPer100g_mlOr1Unit() {
        return proteinsPer100g_mlOr1Unit;
    }

    public int getCarbohydratesPer100g_mlOr1Unit() {
        return carbohydratesPer100g_mlOr1Unit;
    }

    public int getFatPer100g_mlOr1Unit() {
        return fatPer100g_mlOr1Unit;
    }

    public double countCalories(double amount){
        if(storageType.equals(StorageType.ITEM)) return amount * kcalPer100g_mlOr1Unit;
        else return amount*kcalPer100g_mlOr1Unit/ DEFAULT_AMOUNT_FOR_VOLUME_OR_WEIGHT;
    }

    public double countProteins(double amount){
        if(storageType.equals(StorageType.ITEM)) return amount*proteinsPer100g_mlOr1Unit;
        else return amount * proteinsPer100g_mlOr1Unit / DEFAULT_AMOUNT_FOR_VOLUME_OR_WEIGHT;
    }

    public double countCarbons(double amount){
        if(storageType.equals(StorageType.ITEM)) return amount*carbohydratesPer100g_mlOr1Unit;
        else return amount*carbohydratesPer100g_mlOr1Unit/DEFAULT_AMOUNT_FOR_VOLUME_OR_WEIGHT;
    }

    public double countFat(double amount){
        if(storageType.equals(StorageType.ITEM)) return amount*fatPer100g_mlOr1Unit;
        else return amount*fatPer100g_mlOr1Unit/DEFAULT_AMOUNT_FOR_VOLUME_OR_WEIGHT;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }
}
