package com.example.dell.menu.screens.meals;

import android.database.Cursor;
import android.util.Log;

import com.example.dell.menu.MenuDataBase;
import com.example.dell.menu.objects.Meal;
import com.example.dell.menu.tables.MealsTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 27.05.2017.
 */

public class MealsFragmentManager {

    private final MenuDataBase menuDataBase;
    private MealsFragment mealsFragment;

    public MealsFragmentManager(MenuDataBase menuDataBase){
        this.menuDataBase = menuDataBase;
    }

    public void onAttach(MealsFragment mealsFragment){
        this.mealsFragment = mealsFragment;
    }

    public void onStop(){
        this.mealsFragment = null;
    }


    public void loadMeals() {
        List<Meal> results = new ArrayList<>();
        String query = String.format("SELECT * FROM %s ", MealsTable.getTableName());
        Cursor cursor = menuDataBase.downloadDatas(query);

        if(cursor.getCount() > 0){
            int mealsId, cumulativeNumberOfKcalPer100g, authorsId;
            String name, recipe;
            cursor.moveToPosition(-1);

            while (cursor.moveToNext()){
                mealsId = cursor.getInt(0);
                name = cursor.getString(1);
                cumulativeNumberOfKcalPer100g = cursor.getInt(2);
                authorsId = cursor.getInt(3);
                recipe = cursor.getString(4);
                results.add(new Meal(mealsId, name, cumulativeNumberOfKcalPer100g, authorsId, recipe));
            }
            // TODO: 27.05.2017 else

            if(mealsFragment != null){
                mealsFragment.showMeals(results);
            }
            // TODO: 27.05.2017 else
        }
    }
}
