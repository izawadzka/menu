package com.example.dell.menu.menuplanning.types;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 27.05.2017.
 */

public class ProductsType {
    public final static String VEGETABLES = "vegetables";
    public final static String FRUITS = "fruits";
    public final static String LIQUID = "liquid";
    public final static String MEAT = "meat";
    public final static String FAT = "fat";
    public final static String DRY_GOODS = "dry_goods";
    public final static String SPICE = "spice";
    public final static String DAIRY_PRODUCTS = "dairy_products";
    public final static String FISH = "fish";
    public final static String BAKED_GOODS = "baked_goods";


    public static List<String> getTypes(){
        List<String> types = new ArrayList<>();
        types.add(VEGETABLES);
        types.add(FRUITS);
        types.add(LIQUID);
        types.add(MEAT);
        types.add(FAT);
        types.add(DRY_GOODS);
        types.add(SPICE);
        types.add(DRY_GOODS);
        types.add(DAIRY_PRODUCTS);
        types.add(FISH);
        types.add(BAKED_GOODS);
        return types;
    }
}
