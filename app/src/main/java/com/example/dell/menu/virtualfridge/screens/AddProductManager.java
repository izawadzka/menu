package com.example.dell.menu.virtualfridge.screens;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.dell.menu.data.MenuDataBase;
import com.example.dell.menu.data.tables.DailyMenusTable;
import com.example.dell.menu.data.tables.ProductsOnShelvesTable;
import com.example.dell.menu.data.tables.ShelvesInVirtualFridgeTable;
import com.example.dell.menu.virtualfridge.events.AddProductClickedEvent;
import com.example.dell.menu.menuplanning.objects.Product;
import com.example.dell.menu.data.tables.ProductsTable;
import com.example.dell.menu.data.tables.ShoppingListsProductsTable;
import com.example.dell.menu.virtualfridge.flags.ProductFlags;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 29.10.2017.
 */

public class AddProductManager {
    public static final int RESULT_OK = 0;
    public static final int RESULT_ERROR = -1;
    public static final int RESULT_OTHER_NEED_IT = 2;
    private AddProductActivity addProductActivity;
    private final Bus bus;
    private List<Product> products = new ArrayList<>();
    private boolean shopping_list_mode = false;
    private boolean present_shelf_mode = false;
    private boolean extra_shelf_mode = false;
    private long shoppingListId;
    private int shelfId;

    public AddProductManager(Bus bus) {
        this.bus = bus;
        bus.register(this);
    }

    public void onAttach(AddProductActivity addProductActivity){
        this.addProductActivity = addProductActivity;
    }

    public void onStop(){
        this.addProductActivity = null;
    }

    public List<Product> getProducts() {
        return products;
    }

    void loadProducts(){
        if(addProductActivity != null){
            new AsyncTask<Void, Void, Boolean>(){

                @Override
                protected Boolean doInBackground(Void... params) {
                    boolean result = true;
                    products.clear();
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(addProductActivity);
                    String query;
                    if(present_shelf_mode)
                    query = String.format("SELECT post.%s, %s, %s, %s FROM %s post JOIN %s p " +
                            " ON post.%s = p.%s WHERE %s = %s AND %s = %s",
                            ProductsOnShelvesTable.getFirstColumnName(),
                            ProductsOnShelvesTable.getThirdColumnName(),
                            ProductsTable.getSecondColumnName(),
                            ProductsTable.getFifthColumnName(),
                            ProductsOnShelvesTable.getTableName(),
                            ProductsTable.getTableName(),
                            ProductsOnShelvesTable.getFirstColumnName(),
                            ProductsTable.getFirstColumnName(),
                            ProductsOnShelvesTable.getSecondColumnName(),
                            shelfId,
                            ProductsOnShelvesTable.getFourthColumnName(),
                            ProductFlags.NEED_TO_BE_BOUGHT_IND);
                    else if(extra_shelf_mode) query = String.format("SELECT %s, %s, %s FROM %s " +
                                    "ORDER BY %s",
                            ProductsTable.getFirstColumnName(),
                            ProductsTable.getSecondColumnName(),
                            ProductsTable.getFifthColumnName(),
                            ProductsTable.getTableName(),
                            ProductsTable.getSecondColumnName());
                    else if(shopping_list_mode) query = String.format("SELECT %s, %s, %s FROM %s " +
                            "WHERE NOT %s IN (SELECT %s FROM %s) ORDER BY %s",
                            ProductsTable.getFirstColumnName(),
                            ProductsTable.getSecondColumnName(),
                            ProductsTable.getFifthColumnName(),
                            ProductsTable.getTableName(),
                            ProductsTable.getFirstColumnName(),
                            ShoppingListsProductsTable.getSecondColumnName(),
                            ShoppingListsProductsTable.getTableName(),
                            ProductsTable.getSecondColumnName());
                    else query = "";

                    Cursor cursor = menuDataBase.downloadData(query);
                    if(cursor.getCount() > 0){
                        cursor.moveToPosition(-1);
                        while (cursor.moveToNext()){
                            if(present_shelf_mode){
                                products.add(new Product(cursor.getInt(0), cursor.getString(2),
                                        cursor.getString(3)));
                                if (cursor.getDouble(1) > 0)
                                    products.get(products.size()-1)
                                            .setMaxQuantity(cursor.getDouble(1));
                            }else
                            products.add(new Product(cursor.getInt(0), cursor.getString(1),
                                    cursor.getString(2)));
                        }
                        result = true;
                    }else result = false;

                    menuDataBase.close();
                    return result;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    if(addProductActivity != null){
                        if(result) addProductActivity.showProducts();
                        else addProductActivity.loadingFailed();
                    }
                }
            }.execute();
        }
    }

    @Subscribe
    public void onAddProductClickedEvent(final AddProductClickedEvent event){
        if(addProductActivity != null){
            new AsyncTask<Void, Void, Integer>(){

                @Override
                protected Integer doInBackground(Void... params) {
                    int result = RESULT_OK;
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(addProductActivity);

                    if(present_shelf_mode) {
                        //check if exact product already exists in database
                        String findProductQuery = String.format("SELECT %s FROM %s WHERE " +
                                "%s = %s AND %s = %s AND %s = %s",
                                ProductsOnShelvesTable.getThirdColumnName(),
                                ProductsOnShelvesTable.getTableName(),
                                ProductsOnShelvesTable.getSecondColumnName(),
                                shelfId,
                                ProductsOnShelvesTable.getFourthColumnName(),
                                ProductFlags.BOUGHT_INDX,
                                ProductsOnShelvesTable.getFirstColumnName(),
                                event.productId);
                        Cursor foundProductCursor = menuDataBase.downloadData(findProductQuery);

                        if(foundProductCursor.getCount() == 1){
                            foundProductCursor.moveToPosition(0);

                            ContentValues editContentValues = new ContentValues();
                            editContentValues.put(ProductsOnShelvesTable.getThirdColumnName(),
                                    foundProductCursor.getDouble(0) + event.quantity);
                            String editWhereClause = String.format("%s = ? AND %s = ? AND %s = ?",
                                    ProductsOnShelvesTable.getFirstColumnName(),
                                    ProductsOnShelvesTable.getSecondColumnName(),
                                    ProductsOnShelvesTable.getFourthColumnName());
                            String[] editWhereArgs = {String.valueOf(event.productId),
                            String.valueOf(shelfId), String.valueOf(ProductFlags.BOUGHT_INDX)};
                            if(menuDataBase.update(ProductsOnShelvesTable.getTableName(),
                                    editContentValues, editWhereClause, editWhereArgs) != 1)
                                result = RESULT_ERROR;
                        }
                        else if(foundProductCursor.getCount() == 0){
                            if(menuDataBase.insert(ProductsOnShelvesTable.getTableName(),
                                    ProductsOnShelvesTable
                                            .getContentValues(new Product(event.productId,
                                                            event.quantity, ProductFlags.BOUGHT_INDX),
                                                    shelfId)) == -1) result = RESULT_ERROR;
                        }else result = RESULT_ERROR;
                        if(result == RESULT_OK) {
                            String whereClause = String.format("%s = ? AND %s = ? " +
                                            "AND %s = ?",
                                    ProductsOnShelvesTable.getFirstColumnName(),
                                    ProductsOnShelvesTable.getSecondColumnName(),
                                    ProductsOnShelvesTable.getFourthColumnName());
                            String[] whereArgs = {String.valueOf(event.productId),
                                    String.valueOf(shelfId),
                                    String.valueOf(ProductFlags.NEED_TO_BE_BOUGHT_IND)};
                            if (event.maxQuantity - event.quantity == 0) {

                                if(menuDataBase.delete(ProductsOnShelvesTable.getTableName(),
                                        whereClause, whereArgs) != 1) result = RESULT_ERROR;
                            } else {
                                ContentValues editContentValues = new ContentValues();
                                editContentValues.put(ProductsOnShelvesTable.getThirdColumnName(),
                                        event.maxQuantity - event.quantity);
                                if(menuDataBase.update(ProductsOnShelvesTable.getTableName(),
                                        editContentValues, whereClause, whereArgs) != 1)
                                    result = RESULT_ERROR;

                            }

                        }

                    }
                    else if(extra_shelf_mode){
                        String checkIfOtherShelvesNeedItQuery = String.format("SELECT %s, %s FROM %s" +
                                " WHERE %s = %s AND %s = %s AND NOT %s = %s",
                                ProductsOnShelvesTable.getThirdColumnName(),
                                ProductsOnShelvesTable.getSecondColumnName(),
                                ProductsOnShelvesTable.getTableName(),
                                ProductsOnShelvesTable.getFirstColumnName(),
                                event.productId,
                                ProductsOnShelvesTable.getFourthColumnName(),
                                ProductFlags.NEED_TO_BE_BOUGHT_IND,
                                ProductsOnShelvesTable.getSecondColumnName(),
                                shelfId);
                        Cursor otherShelvesCursor = menuDataBase
                                .downloadData(checkIfOtherShelvesNeedItQuery);
                        if(otherShelvesCursor.getCount() > 0){
                            result = RESULT_OTHER_NEED_IT;
                        }
                    }

                    else if(shopping_list_mode)
                        if(menuDataBase.insert(ShoppingListsProductsTable.getTableName(),
                                ShoppingListsProductsTable.getContentValues(shoppingListId,
                                        event.productId, event.quantity)) == -1)
                            result = RESULT_ERROR;
                    else return RESULT_ERROR;
                    menuDataBase.close();
                    return result;
                }

                @Override
                protected void onPostExecute(Integer result) {
                    if(addProductActivity != null){
                        if (result == RESULT_OK){
                            addProductActivity
                                .makeAStatement(String.format("%s successfully added", event.name),
                                        Toast.LENGTH_SHORT);
                            loadProducts();
                        }
                        else if(result == RESULT_ERROR)
                            addProductActivity.makeAStatement(String
                                    .format("Adding %s failed", event.name), Toast.LENGTH_LONG);
                        else if(result == RESULT_OTHER_NEED_IT){
                            addProductActivity.askUserIfWantToFillOtherShelvesFirst(event.productId,
                                    event.quantity);
                        }
                    }
                }
            }.execute();
        }
    }


    void setShoppingListMode(boolean shopping_list_mode, long shoppingListId) {
        this.shopping_list_mode = shopping_list_mode;
        this.shoppingListId = shoppingListId;
    }

    void setPresentShelfMode(boolean present_shelf_mode, int shelfId){
        this.present_shelf_mode = present_shelf_mode;
        this.shelfId = shelfId;
    }

    void setExtraShelfMode(boolean extra_shelf_mode, int shelfId){
        this.extra_shelf_mode = extra_shelf_mode;
        this.shelfId = shelfId;
    }

    public void distributeProductBetweenShelves(final int productId, final double quantity) {
        if(addProductActivity != null){
            new AsyncTask<Void, Void, Double>(){

                @Override
                protected Double doInBackground(Void... params) {
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(addProductActivity);
                    double quantityToDistribute = quantity;
                    String checkIfOtherShelvesNeedItQuery = String.format("SELECT %s,post.%s, %s " +
                            "FROM %s post JOIN %s shelves ON post.%s = shelves.%s " +
                            "JOIN %s dm ON shelves.%s = dm.%s WHERE %s = %s AND %s = %s AND " +
                            "NOT post.%s = %s GROUP BY dm.%s",
                            ProductsOnShelvesTable.getThirdColumnName(),
                            ProductsOnShelvesTable.getSecondColumnName(),
                            DailyMenusTable.getSecondColumnName(),
                            ProductsOnShelvesTable.getTableName(),
                            ShelvesInVirtualFridgeTable.getTableName(),
                            ProductsOnShelvesTable.getSecondColumnName(),
                            ShelvesInVirtualFridgeTable.getFirstColumnName(),
                            DailyMenusTable.getTableName(),
                            ShelvesInVirtualFridgeTable.getSecondColumnName(),
                            DailyMenusTable.getFirstColumnName(),
                            ProductsOnShelvesTable.getFirstColumnName(),
                            productId,
                            ProductsOnShelvesTable.getFourthColumnName(),
                            ProductFlags.NEED_TO_BE_BOUGHT_IND,
                            ProductsOnShelvesTable.getSecondColumnName(),
                            shelfId,
                            DailyMenusTable.getSecondColumnName());

                    Cursor otherShelvesCursor = menuDataBase
                            .downloadData(checkIfOtherShelvesNeedItQuery);
                    if(otherShelvesCursor.getCount() > 0 && quantityToDistribute > 0){
                        otherShelvesCursor.moveToPosition(-1);
                        while (otherShelvesCursor.moveToNext()){
                            if(otherShelvesCursor.getDouble(0) <= quantityToDistribute){
                                ContentValues editContentValues = new ContentValues();
                                editContentValues.put(ProductsOnShelvesTable.getFourthColumnName(),
                                        ProductFlags.BOUGHT_INDX);
                                String whereClause = String.format("%s = ? AND %s = ? AND %s = ?",
                                        ProductsOnShelvesTable.getTableName(),
                                        ProductsOnShelvesTable.getSecondColumnName(),
                                        ProductsOnShelvesTable.getFourthColumnName());
                                String[] whereArgs = {String.valueOf(productId),
                                        String.valueOf(otherShelvesCursor.getInt(1)),
                                        String.valueOf(ProductFlags.NEED_TO_BE_BOUGHT_IND)};
                                menuDataBase.update(ProductsOnShelvesTable.getTableName(),
                                        editContentValues, whereClause, whereArgs);
                                quantityToDistribute -= otherShelvesCursor.getDouble(0);
                            }else{
                                menuDataBase.insert(ProductsOnShelvesTable.getTableName(),
                                        ProductsOnShelvesTable
                                                .getContentValues(new Product(productId,
                                                        quantityToDistribute,ProductFlags.BOUGHT_INDX),
                                                        otherShelvesCursor.getInt(1)));
                                ContentValues editContentValues = new ContentValues();
                                editContentValues.put(ProductsOnShelvesTable.getThirdColumnName(),
                                        otherShelvesCursor.getDouble(0) - quantityToDistribute);

                                String whereClause = String.format("%s = ? AND %s = ? AND %s = ?",
                                        ProductsOnShelvesTable.getFirstColumnName(),
                                        ProductsOnShelvesTable.getSecondColumnName(),
                                        ProductsOnShelvesTable.getFourthColumnName());
                                String[] whereArgs = {String.valueOf(productId),
                                        String.valueOf(otherShelvesCursor.getInt(1)),
                                        String.valueOf(ProductFlags.NEED_TO_BE_BOUGHT_IND)};
                                menuDataBase.update(ProductsOnShelvesTable.getTableName(),
                                        editContentValues, whereClause, whereArgs);
                                quantityToDistribute = 0;
                            }
                        }


                    }

                    menuDataBase.close();
                    return quantityToDistribute;
                }

                @Override
                protected void onPostExecute(Double result) {
                    if(addProductActivity != null){
                        if(result > 0) addProductToExtraShelf(productId, result);
                        else addProductActivity.productSuccessfullyDistributed();
                    }
                }
            }.execute();
        }
    }

    public void addProductToExtraShelf(final int productId, final double quantity) {
        if(addProductActivity != null){
            new AsyncTask<Void, Void, Boolean>(){

                @Override
                protected Boolean doInBackground(Void... params) {
                    boolean result = true;
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(addProductActivity);
                    String checkIfAlreadyExistsQuery = String.format("SELECT %s FROM %s " +
                            "WHERE %s = %s AND %s = %s AND %s = %s",
                            ProductsOnShelvesTable.getThirdColumnName(),
                            ProductsOnShelvesTable.getTableName(),
                            ProductsOnShelvesTable.getFirstColumnName(),
                            productId,
                            ProductsOnShelvesTable.getSecondColumnName(),
                            shelfId,
                            ProductsOnShelvesTable.getFourthColumnName(),
                            ProductFlags.BOUGHT_INDX);
                    Cursor cursor = menuDataBase.downloadData(checkIfAlreadyExistsQuery);
                    if(cursor.getCount() == 1){
                        ContentValues editContentValues = new ContentValues();
                        editContentValues.put(ProductsOnShelvesTable.getThirdColumnName(),
                                cursor.getDouble(0) + quantity);
                        String whereClause = String.format("%s = ? AND %s = ?",
                                ProductsOnShelvesTable.getFirstColumnName(),
                                ProductsOnShelvesTable.getSecondColumnName());
                        String[] whereArgs = {String.valueOf(productId),
                                String.valueOf(shelfId)};
                        result = menuDataBase.update(ProductsOnShelvesTable.getTableName(),
                                editContentValues, whereClause, whereArgs) == 1;
                    }else{
                        result = menuDataBase.insert(ProductsOnShelvesTable.getTableName(),
                                ProductsOnShelvesTable.getContentValues(new Product(productId,
                                        quantity, ProductFlags.BOUGHT_INDX), shelfId)) != -1;
                    }
                    menuDataBase.close();
                    return result;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    if(addProductActivity != null){
                        if(result) addProductActivity.addingProductSuccess();
                        else addProductActivity.addingProductFailed();
                    }
                }
            }.execute();
        }
    }
}
