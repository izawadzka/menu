package com.example.dell.menu.screens.products;

import android.database.Cursor;
import android.util.Log;

import com.example.dell.menu.MenuDataBase;
import com.example.dell.menu.ProductsType;
import com.example.dell.menu.StorageType;
import com.example.dell.menu.objects.Product;
import com.example.dell.menu.tables.ProductsTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 26.05.2017.
 */

public class ProductFragmentManager {
    private final MenuDataBase menuDataBase;
    private ProductsFragment productsFragment;


    public ProductFragmentManager(MenuDataBase menuDataBase){
        this.menuDataBase = menuDataBase;
    }

    public void onAttach(ProductsFragment productsFragment){
        this.productsFragment = productsFragment;
    }

    public void onStop(){
        productsFragment = null;
    }

    public void loadProducts(){
        List<Product> results =  new ArrayList<>();
        String query = String.format("SELECT * FROM %s ", ProductsTable.getTableName());
        Cursor cursor = menuDataBase.downloadDatas(query);

        if(cursor.getCount() > 0){
            int productsId, numberOfKcalPer100g;
            String name, type, storageType;
            cursor.moveToPosition(-1);
            while (cursor.moveToNext()){
                productsId = cursor.getInt(0);
                name = cursor.getString(1);
                numberOfKcalPer100g = cursor.getInt(2);
                type = cursor.getString(3);
                storageType = cursor.getString(4);
                results.add(new Product(productsId, name, numberOfKcalPer100g, type, storageType));
            }
        }
        //// TODO: 27.05.2017  else

        if(productsFragment != null){
            productsFragment.showProducts(results);
        }
        // TODO: 27.05.2017 else
    }
}
