package com.example.dell.menu.menuplanning.objects;

import android.util.Log;

import com.example.dell.menu.menuplanning.types.StorageType;

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
    private double quantity, maxQuantity = -1;
    private int proteinsPer100g_mlOr1Unit;
    private int carbohydratesPer100g_mlOr1Unit;
    private int fatPer100g_mlOr1Unit;
    private boolean bought = false;
    private int productFlagId;

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

    public Product(int productsId, String name, double quantity, String storageType, int flagId) {
        this.productId = productsId;
        this.name = name;
        this.quantity = quantity;
        this.storageType = storageType;
        this.productFlagId = flagId;
    }

    public Product(int productId,  double quantity, int productsFlagId) {
        this.productId = productId;
        this.quantity = quantity;
        this.productFlagId = productsFlagId;
    }

    public Product(int productId, String name, String storageType, double quantity) {
        this.productId = productId;
        this.name = name;
        this.storageType = storageType;
        this.quantity = quantity;
    }

    public double getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(double maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    public boolean isBought() {
        return bought;
    }

    public void setBought(boolean bought) {
        this.bought = bought;
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

    public Product(int productId, String name, String type, String storageType, double quantity) {
        this.productId = productId;
        this.name = name;
        this.type = type;
        this.quantity = quantity;
        this.storageType = storageType;
    }

    public Product(int productId, String name, String storageType) {
        this.productId = productId;
        this.name = name;
        this.storageType = storageType;
    }

    public Product(int productId, String name, int kcalPer100g_mlOr1Unit, String storageType,
                   int proteinsPer100g_mlOr1Unit, int carbonsPer100g_mlOr1Unit,
                   int fatPer100g_mlOr1Unit, double quantity) {
        this.productId = productId;
        this.name = name;
        this.kcalPer100g_mlOr1Unit = kcalPer100g_mlOr1Unit;
        this.storageType = storageType;
        this.proteinsPer100g_mlOr1Unit = proteinsPer100g_mlOr1Unit;
        this.carbohydratesPer100g_mlOr1Unit = carbonsPer100g_mlOr1Unit;
        this.fatPer100g_mlOr1Unit = fatPer100g_mlOr1Unit;
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

    public int countCalories(double amount){
        if(storageType.equals(StorageType.ITEM)){
            Log.i("countKCal", String.format("count %s",amount * kcalPer100g_mlOr1Unit));
            return (int)(amount * kcalPer100g_mlOr1Unit);
        }
        else return (int)(amount*kcalPer100g_mlOr1Unit/ DEFAULT_AMOUNT_FOR_VOLUME_OR_WEIGHT);
    }

    public int countProteins(double amount){
        if(storageType.equals(StorageType.ITEM)) return (int)(amount*proteinsPer100g_mlOr1Unit);
        else return (int)(amount * proteinsPer100g_mlOr1Unit / DEFAULT_AMOUNT_FOR_VOLUME_OR_WEIGHT);
    }

    public int countCarbons(double amount){
        if(storageType.equals(StorageType.ITEM)) return (int)(amount*carbohydratesPer100g_mlOr1Unit);
        else return (int)(amount*carbohydratesPer100g_mlOr1Unit/DEFAULT_AMOUNT_FOR_VOLUME_OR_WEIGHT);
    }

    public int countFat(double amount){
        if(storageType.equals(StorageType.ITEM)) return (int)(amount*fatPer100g_mlOr1Unit);
        else return (int)(amount*fatPer100g_mlOr1Unit/DEFAULT_AMOUNT_FOR_VOLUME_OR_WEIGHT);
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    public int getProductFlagId() {
        return productFlagId;
    }

    public void setProductFlagId(int productFlagId) {
        this.productFlagId = productFlagId;
    }
}
