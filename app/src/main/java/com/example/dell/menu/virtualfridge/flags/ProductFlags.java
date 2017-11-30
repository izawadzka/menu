package com.example.dell.menu.virtualfridge.flags;

import android.support.test.espresso.core.deps.guava.collect.ImmutableMap;

import java.util.Map;

/**
 * Created by Dell on 29.11.2017.
 */

public class ProductFlags {
    public final static int BOUGHT_INDX = 1;
    public final static int NEED_TO_BE_BOUGHT_IND = 2;
    public final static int ADDED_TO_SHOPPING_LIST_INDX = 3;
    public final static int EATEN_INDX = 4;
    public final static int WASNT_USED_INDX = 5;

    private final static String[] flags = {"bought", "need_to_be_bought",
    "added_to_shopping_list", "eaten", "wasn't used"};

    public static String getFlag(int indx){
        return flags[indx-1];
    }
}
