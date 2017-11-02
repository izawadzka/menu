package com.example.dell.menu.screens.menuplanning.products.addOrEdit;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;

import com.example.dell.menu.MenuDataBase;
import com.example.dell.menu.objects.menuplanning.Product;
import com.example.dell.menu.tables.ProductsTable;

/**
 * Created by Dell on 29.05.2017.
 */

public class AddOrEditProductManager {
    private AddOrEditProductActivity addOrEditProductActivity;
    private boolean editMode;
    boolean showMode;
    private Product productToEdit;

    public void onAttach(AddOrEditProductActivity addOrEditProductActivity){
        this.addOrEditProductActivity = addOrEditProductActivity;
    }

    public void onStop(){
        addOrEditProductActivity = null;
    }


    public void addNewProduct(String productName, int numberOfKcal, String addedProductType, String storageType, int amountOfProteins, int amountOfCarbos, int amountOfFat) {
        if(addOrEditProductActivity != null){
            new AsyncTask<String, Void, Long>(){
                @Override
                protected Long doInBackground(String... params) {
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(addOrEditProductActivity);

                    Long result = menuDataBase.insert(ProductsTable.getTableName(),
                            ProductsTable.getContentValues(new Product(0,params[0],
                                    Integer.parseInt(params[1]), params[2], params[3],
                                    Integer.parseInt(params[4]), Integer.parseInt(params[5]),
                                    Integer.parseInt(params[6]))));
                    menuDataBase.close();
                    return result;
                }

                @Override
                protected void onPostExecute(Long result) {
                    if(addOrEditProductActivity != null) {
                        if (result != -1) {
                            addOrEditProductActivity.addSuccessful();
                        }else{
                            addOrEditProductActivity.addFailed();
                        }
                    }
                }
            }.execute(productName, String.valueOf(numberOfKcal), addedProductType, storageType,
                    String.valueOf(amountOfProteins), String.valueOf(amountOfCarbos),
                    String.valueOf(amountOfFat));
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
            new AsyncTask<Integer, Void, Product>(){
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
                                cursor.getString(3), cursor.getString(4), cursor.getInt(5),
                                cursor.getInt(6), cursor.getInt(7));
                    }

                    menuDataBase.close();
                    AddOrEditProductManager.this.productToEdit = productToEdit;

                    return productToEdit;
                }


                @Override
                protected void onPostExecute(Product product) {
                    if(addOrEditProductActivity != null) {
                        if (product != null) addOrEditProductActivity.loadingProductSuccess(product);
                        else addOrEditProductActivity.loadingProductFailed();
                    }
                }
            }.execute(productId);
        }
    }

    public void editProduct(String productName, int numberOfKcal, String addedProductType, String storageType, int amountOfProteins, int amountOfCarbos, int amountOfFat) {
        if(addOrEditProductActivity != null){
            new AsyncTask<String, Void, Boolean>(){

                @Override
                protected Boolean doInBackground(String... params) {
                    boolean result = false;
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(addOrEditProductActivity);

                    ContentValues editContentValue = new ContentValues();
                    if(!productToEdit.getName().equals(params[0])) editContentValue.put(ProductsTable.getSecondColumnName(),params[0]);


                    int amountOfKcal = Integer.parseInt(params[1]);
                    if(amountOfKcal != productToEdit.getKcalPer100g_mlOr1Unit()) editContentValue.put(ProductsTable.getThirdColumnName(), amountOfKcal);

                    int amountOfProteins = Integer.parseInt(params[4]);
                    if(amountOfProteins != productToEdit.getProteinsPer100g_mlOr1Unit()) editContentValue.put(ProductsTable.getSixthColumnName(), amountOfProteins);

                    int amountOfCarbos = Integer.parseInt(params[5]);
                    if(amountOfCarbos != productToEdit.getCarbohydratesPer100g_mlOr1Unit()) editContentValue.put(ProductsTable.getSeventhColumnName(), amountOfCarbos);

                    int amountOfFat = Integer.parseInt(params[6]);
                    if(amountOfFat != productToEdit.getFatPer100g_mlOr1Unit()) editContentValue.put(ProductsTable.getEighthColumnName(), amountOfFat);

                    if(!productToEdit.getType().equals(params[2])) editContentValue.put(ProductsTable.getFourthColumnName(), params[2]);

                    if(!productToEdit.getStorageType().equals(params[3])) editContentValue.put(ProductsTable.getFifthColumnName(), params[3]);

                    String[] productsId = {String.valueOf(productToEdit.getProductId())};
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
            }.execute(productName, String.valueOf(numberOfKcal), addedProductType, storageType,
                    String.valueOf(amountOfProteins), String.valueOf(amountOfCarbos),
                    String.valueOf(amountOfFat));
        }
    }

    public void setShowMode(boolean showMode, int productToShowId) {
        this.showMode = showMode;
        if(showMode) loadProduct(productToShowId);
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
                        cursor.getString(3), cursor.getString(4), cursor.getInt(5),
                        cursor.getInt(6), cursor.getInt(7));
            }

            menuDataBase.close();
            AddOrEditProductManager.this.productToEdit = productToEdit;

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

}
