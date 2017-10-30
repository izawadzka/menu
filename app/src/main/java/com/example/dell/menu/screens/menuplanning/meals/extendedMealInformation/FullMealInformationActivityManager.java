package com.example.dell.menu.screens.menuplanning.meals.extendedMealInformation;

import android.database.Cursor;
import android.os.AsyncTask;

import com.example.dell.menu.MenuDataBase;
import com.example.dell.menu.objects.menuplanning.Product;
import com.example.dell.menu.tables.MealsProductsTable;
import com.example.dell.menu.tables.ProductsTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 28.05.2017.
 */

public class FullMealInformationActivityManager {
    protected FullMealInformationActivity fullMealInf;
    private int currentMealsId;


    public void onAttach(FullMealInformationActivity fullMealInf){
        this.fullMealInf = fullMealInf;
    }


    public int getCurrentMealsId() {
        return currentMealsId;
    }

    public void onStop(){
        this.fullMealInf = null;
    }

    public void loadUsedProducts(int mealsId) {
        currentMealsId = mealsId;


        if (fullMealInf != null) {
            new DownloadUsedProducts().execute();
        }
    }

    class DownloadUsedProducts extends AsyncTask<Void, Integer, List<Product>>{


        @Override
        protected List<Product> doInBackground(Void... params) {
            MenuDataBase menuDataBase = MenuDataBase.getInstance(fullMealInf);

            List<Product> result = new ArrayList<>();
            String query = String.format("SELECT %s, %s, %s FROM %s p JOIN %s mp\n" +
                            "ON mp.%s = p.%s WHERE mp.%s = '%s';",
                    ProductsTable.getSecondColumnName(),
                    ProductsTable.getFifthColumnName(),
                    MealsProductsTable.getThirdColumnName(),
                    ProductsTable.getTableName(),
                    MealsProductsTable.getTableName(),
                    MealsProductsTable.getFirstColumnName(),
                    ProductsTable.getFirstColumnName(),
                    MealsProductsTable.getSecondColumnName(),
                    getCurrentMealsId());
            Cursor cursor = menuDataBase.downloadData(query);

            if(cursor.getCount() > 0){
                cursor.moveToPosition(-1);
                while (cursor.moveToNext()){
                    result.add(new Product(cursor.getString(0), cursor.getDouble(2), cursor.getString(1)));
                }
            }
            menuDataBase.close();
            return result;
        }

        @Override
        protected void onPostExecute(List<Product> products) {
            fullMealInf.showProducts(products);
        }
    }
}
