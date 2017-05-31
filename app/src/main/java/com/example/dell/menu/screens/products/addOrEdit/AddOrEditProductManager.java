package com.example.dell.menu.screens.products.addOrEdit;

import android.os.AsyncTask;

import com.example.dell.menu.MenuDataBase;
import com.example.dell.menu.objects.Product;
import com.example.dell.menu.tables.ProductsTable;

/**
 * Created by Dell on 29.05.2017.
 */

public class AddOrEditProductManager {
    protected AddOrEditProductActivity addOrEditProductActivity;

    public void onAttach(AddOrEditProductActivity addOrEditProductActivity){
        this.addOrEditProductActivity = addOrEditProductActivity;
    }

    public void onStop(){
        addOrEditProductActivity = null;
    }


    public void addNewProduct(String productName, int numberOfKcal, String addedProductType, String storageType) {
        if(addOrEditProductActivity != null){
            new AddNewProduct().execute(productName, String.valueOf(numberOfKcal), addedProductType, storageType);
        }
    }


    class AddNewProduct extends AsyncTask<String, Integer, Long>{

        @Override
        protected Long doInBackground(String... params) {
            MenuDataBase menuDataBase = MenuDataBase.getInstance(addOrEditProductActivity);

            Long result = menuDataBase.insert(ProductsTable.getTableName(),
                    ProductsTable.getContentValues(new Product(0,params[0], Integer.parseInt(params[1]), params[2], params[3])));
            menuDataBase.close();
            return result;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            if(addOrEditProductActivity != null) {
                if (aLong != -1) {
                    addOrEditProductActivity.addSuccesfull();
                }else{
                    addOrEditProductActivity.addFailed();
                }
            }
        }
    }
}
