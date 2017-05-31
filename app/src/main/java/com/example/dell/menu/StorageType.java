package com.example.dell.menu;

import android.icu.text.AlphabeticIndex;
import android.support.test.espresso.core.deps.guava.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dell on 27.05.2017.
 */

public class StorageType {
    public final static String WEIGHT = "weight";
    public final static String ITEM = "item";
    public final static String VOLUME = "volume";

    static final Map<String, String> units = ImmutableMap.of(
            WEIGHT, "g",
            ITEM, "pc.",
            VOLUME, "ml"
    );

    public static String getUnit(String key){
        return units.get(key);
    }
}
