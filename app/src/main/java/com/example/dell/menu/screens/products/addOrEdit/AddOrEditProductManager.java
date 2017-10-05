package com.example.dell.menu.screens.products.addOrEdit;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;

import com.example.dell.menu.MenuDataBase;
import com.example.dell.menu.objects.Product;
import com.example.dell.menu.tables.ProductsTable;

/**
 * Created by Dell on 29.05.2017.
 */

public class AddOrEditProductManager {
    private AddOrEditProductActivity addOrEditProductActivity;
    private boolean editMode;
    boolean showMode;
    private Product product;

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

    public void setEditMode(boolean editMode, int productId) {
        this.editMode = editMode;
        if(editMode){
            loadProduct(productId);
        }
    }

    private void loadProduct(int productId) {
        if(addOrEditProductActivity != null){
            new LoadProduct().execute(productId);
        }
    }

    public void editProduct(String productName, int numberOfKcal, String addedProductType, String storageType) {
        if(addOrEditProductActivity != null){
            new EditProduct().execute(productName, String.valueOf(numberOfKcal), addedProductType, storageType);
        }
    }

    public void setShowMode(boolean showMode, int productToShowId) {
        this.showMode = showMode;
        if(showMode) loadProduct(productToShowId);
    }


    private class EditProduct extends AsyncTask<String, Void, Boolean>{

        @Override
        protected Boolean doInBackground(String... params) {
            boolean result = false;
            MenuDataBase menuDataBase = MenuDataBase.getInstance(addOrEditProductActivity);

            ContentValues editContentValue = new ContentValues();
            if(!product.getName().equals(params[0])) editContentValue.put(ProductsTable.getSecondColumnName(),params[0]);

            try {
                int numberOfKcal = Integer.parseInt(params[1]);
                if(numberOfKcal != product.getNumberOfKcalPer100g()) editContentValue.put(ProductsTable.getThirdColumnName(), numberOfKcal);
            }catch (NumberFormatException e){
                menuDataBase.close();
                return false;
            }

            if(!product.getType().equals(params[2])) editContentValue.put(ProductsTable.getFourthColumnName(), params[2]);

            if(!product.getStorageType().equals(params[3])) editContentValue.put(ProductsTable.getFifthColumnName(), params[3]);

            String[] productsId = {String.valueOf(product.getProductId())};
            String whereClause = String.format("%s = ?", ProductsTable.getFirstColumnName());
            if(menuDataBase.update(ProductsTable.getTableName(), editContentValue, whereClause, productsId) == 1) result = true;
            else result = false;

            menuDataBase.close();
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(addOrEditProductActivity != null){
                if(result) addOrEditProductActivity.editingSuccess();
                else addOrEditProductActivity.editingFailed();
            }
        }
    }

    private class LoadProduct extends AsyncTask<Integer, Void, Product>{

        @Override
        protected Product doInBackground(Integer... params) {
            Product productToEdit = null;
            MenuDataBase menuDataBase = MenuDataBase.getInstance(addOrEditProductActivity);

            String query = String.format("SELECT * FROM %s WHERE %s = '%s'",
                    ProductsTable.getTableName(), ProductsTable.getFirstColumnName(), params[0]);
            Cursor cursor = menuDataBase.downloadData(query);
            cursor.moveToPosition(-1);
            while (cursor.moveToNext()){
                productToEdit = new Product(params[0], cursor.getString(1), cursor.getInt(2),
                        cursor.getString(3), cursor.getString(4));
            }

            menuDataBase.close();
            product = productToEdit;

            return productToEdit;
        }


        @Override
        protected void onPostExecute(Product product) {
            if(addOrEditProductActivity != null) {
                if (product != null) addOrEditProductActivity.loadingProductSuccess(product);
                else addOrEditProductActivity.loadingProductFailed();
            }
        }
    }


    private class AddNewProduct extends AsyncTask<String, Integer, Long>{

        @Override
        protected Long doInBackground(String... params) {
            MenuDataBase menuDataBase = MenuDataBase.getInstance(addOrEditProductActivity);

            try {
                Long result = menuDataBase.insert(ProductsTable.getTableName(),
                        ProductsTable.getContentValues(new Product(0,params[0], Integer.parseInt(params[1]), params[2], params[3])));
                menuDataBase.close();
                return result;
            }catch (NumberFormatException e){
                menuDataBase.close();
                return (long)-1;
            }
        }

        @Override
        protected void onPostExecute(Long aLong) {
            if(addOrEditProductActivity != null) {
                if (aLong != -1) {
                    addOrEditProductActivity.addSuccessfull();
                }else{
                    addOrEditProductActivity.addFailed();
                }
            }
        }
    }
}
