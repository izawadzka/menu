package com.example.dell.menu.virtualfridge;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SearchRecentSuggestionsProvider;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.example.dell.menu.data.MenuDataBase;
import com.example.dell.menu.data.tables.DailyMenusTable;
import com.example.dell.menu.data.tables.ProductsOnShelvesTable;
import com.example.dell.menu.data.tables.ShelvesInVirtualFridgeTable;
import com.example.dell.menu.menuplanning.objects.Product;
import com.example.dell.menu.virtualfridge.flags.ProductFlags;
import com.example.dell.menu.virtualfridge.objects.ShelfInVirtualFridge;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by Dell on 01.12.2017.
 */

public class ShelvesArchive {
    private Context context = null;
    private final static String TAG = "ShelvesArchive";

    public ShelvesArchive(Context context){
        this.context = context;
    }

    public void manageArchive(){
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                boolean result = true;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String today = simpleDateFormat.format(new Date());
                List<Integer> shelvesId = new ArrayList<>();
                List<Product> products = new ArrayList<>();
                List<Product> productsInVirtualFridge = new ArrayList<Product>();

                MenuDataBase menuDataBase = MenuDataBase.getInstance(context);

                //at first situation when date was incremented (change present shelves into past)
                String findPastShelvesQuery = String.format("SELECT %s FROM %s WHERE %s IN " +
                        "(SELECT %s FROM %s WHERE %s < '%s') AND %s > 0",
                        ShelvesInVirtualFridgeTable.getFirstColumnName(),
                        ShelvesInVirtualFridgeTable.getTableName(),
                        ShelvesInVirtualFridgeTable.getSecondColumnName(),
                        DailyMenusTable.getFirstColumnName(),
                        DailyMenusTable.getTableName(),
                        DailyMenusTable.getSecondColumnName(), today,
                        ShelvesInVirtualFridgeTable.getSecondColumnName());


                Cursor pastShelvesCursor = menuDataBase.downloadData(findPastShelvesQuery);

                if(pastShelvesCursor.getCount()>0) {
                    Log.i(TAG, "refactoring past shelves");
                    pastShelvesCursor.moveToPosition(-1);
                    while (pastShelvesCursor.moveToNext()) {
                        shelvesId.add(pastShelvesCursor.getInt(0));
                    }

                    for (int shelfId : shelvesId) {
                        ContentValues changeBoughtToEatenContentValues = new ContentValues();
                        changeBoughtToEatenContentValues.put(ProductsOnShelvesTable.getFourthColumnName(),
                                ProductFlags.EATEN_INDX);
                        String changeBoughtToEatenWhereClause = String.format("%s = ? AND %s = ?",
                                ProductsOnShelvesTable.getSecondColumnName(),
                                ProductsOnShelvesTable.getFourthColumnName());
                        String[] changeBoughtToEatenWhereArgs = {String.valueOf(shelfId),
                                String.valueOf(ProductFlags.BOUGHT_INDX)};

                        menuDataBase.update(ProductsOnShelvesTable.getTableName(),
                                changeBoughtToEatenContentValues, changeBoughtToEatenWhereClause,
                                changeBoughtToEatenWhereArgs);


                        ContentValues changeToBuyAndOnShoppingListToNotEatenContentValues = new ContentValues();
                        changeToBuyAndOnShoppingListToNotEatenContentValues
                                .put(ProductsOnShelvesTable.getFourthColumnName(),
                                        ProductFlags.WASNT_USED_INDX);
                        String changeToNotEatenWhereClause = String.format("%s = ? AND  (%s = ? OR " +
                                "%s = ?)",
                                ProductsOnShelvesTable.getSecondColumnName(),
                                ProductsOnShelvesTable.getFourthColumnName(),
                                ProductsOnShelvesTable.getFourthColumnName());
                        String[] changeToNotEatenWhereArgs = {String.valueOf(shelfId),
                                String.valueOf(ProductFlags.NEED_TO_BE_BOUGHT_IND),
                                String.valueOf(ProductFlags.ADDED_TO_SHOPPING_LIST_INDX)};
                        menuDataBase.update(ProductsOnShelvesTable.getTableName(),
                                changeToBuyAndOnShoppingListToNotEatenContentValues,
                                changeToNotEatenWhereClause, changeToNotEatenWhereArgs);

                        String loadNotEatenQuery = String.format("SELECT %s, %s FROM %s " +
                                        "WHERE %s = %s AND %s = %s",
                                ProductsOnShelvesTable.getFirstColumnName(),
                                ProductsOnShelvesTable.getThirdColumnName(),
                                ProductsOnShelvesTable.getTableName(),
                                ProductsOnShelvesTable.getSecondColumnName(),
                                shelfId,
                                ProductsOnShelvesTable.getFourthColumnName(),
                                ProductFlags.WASNT_USED_INDX);
                        Cursor notEatenCursor = menuDataBase.downloadData(loadNotEatenQuery);
                        if (notEatenCursor.getCount() > 0) {
                            notEatenCursor.moveToPosition(-1);
                            while (notEatenCursor.moveToNext()) {
                                int indx = wasAddedPreviously(notEatenCursor.getInt(0), products);
                                if(indx != -1) products.get(indx)
                                        .addQuantity(notEatenCursor.getDouble(1));
                                else products.add(new Product(notEatenCursor.getInt(0),
                                        notEatenCursor.getDouble(1)));
                            }

                            String[] whereArgs = {String.valueOf(shelfId),
                            String.valueOf(ProductFlags.WASNT_USED_INDX)};

                            result = menuDataBase.delete(ProductsOnShelvesTable.getTableName(),
                                    String.format("%s = ? AND %s = ?",
                                            ProductsOnShelvesTable.getSecondColumnName(),
                                            ProductsOnShelvesTable.getFourthColumnName()),
                                    whereArgs) > 0;

                            for (Product product : products) {
                                if(menuDataBase.insert(ProductsOnShelvesTable.getTableName(),
                                        ProductsOnShelvesTable
                                                .getContentValues(new Product(product.getProductId(),
                                                        product.getQuantity(),
                                                        ProductFlags.WASNT_USED_INDX),
                                                        shelfId)) == -1){
                                    result = false;
                                    break;
                                }
                            }

                        }
                    }
                }


                //then situation when time was turned back
                String findFutureShelves = String.format("SELECT %s FROM %s WHERE %s IN " +
                                "(SELECT %s FROM %s WHERE %s >= '%s') AND %s > 0",
                        ShelvesInVirtualFridgeTable.getFirstColumnName(),
                        ShelvesInVirtualFridgeTable.getTableName(),
                        ShelvesInVirtualFridgeTable.getSecondColumnName(),
                        DailyMenusTable.getFirstColumnName(),
                        DailyMenusTable.getTableName(),
                        DailyMenusTable.getSecondColumnName(), today,
                        ShelvesInVirtualFridgeTable.getSecondColumnName());

                Cursor futureShelvesCursor = menuDataBase.downloadData(findFutureShelves);
                if (futureShelvesCursor.getCount() > 0){
                    Log.i(TAG, "refactoring future shelves");
                    shelvesId.clear();
                    futureShelvesCursor.moveToPosition(-1);
                    while (futureShelvesCursor.moveToNext()){
                        shelvesId.add(futureShelvesCursor.getInt(0));
                    }

                    for (int shelfId : shelvesId) {
                        String findProducts = String.format("SELECT %s,%s FROM %s WHERE %s = %s" +
                                " AND (%s = %s OR %s = %s)",
                                ProductsOnShelvesTable.getFirstColumnName(),
                                ProductsOnShelvesTable.getThirdColumnName(),
                                ProductsOnShelvesTable.getTableName(),
                                ProductsOnShelvesTable.getSecondColumnName(),
                                shelfId,
                                ProductsOnShelvesTable.getFourthColumnName(),
                                ProductFlags.EATEN_INDX,
                                ProductsOnShelvesTable.getFourthColumnName(),
                                ProductFlags.WASNT_USED_INDX);

                        Cursor foundProductsCursor = menuDataBase.downloadData(findProducts);
                        if(foundProductsCursor.getCount() > 0){
                            products.clear();
                            foundProductsCursor.moveToPosition(-1);
                            while (foundProductsCursor.moveToNext()){
                                int indx = wasAddedPreviously(foundProductsCursor.getInt(0), products);
                                if(indx != -1) products.get(indx)
                                        .addQuantity(foundProductsCursor.getDouble(1));
                                else products.add(new Product(foundProductsCursor.getInt(0),
                                        foundProductsCursor.getDouble(1),
                                        ProductFlags.NEED_TO_BE_BOUGHT_IND));
                            }

                            String[] deleteArgs = {String.valueOf(shelfId)};
                            result = menuDataBase.delete(ProductsOnShelvesTable.getTableName(),
                                    String.format("%s = ?", ProductsOnShelvesTable.getSecondColumnName()),
                                    deleteArgs) > 0;
                            if(!result) break;

                            boolean wasSynchronizedWithTheFridge;
                            String fridgeQuery = String.format("SELECT %s, %s FROM %s WHERE %s IN " +
                                            "(SELECT %s FROM %s WHERE %s = '0')",
                                    ProductsOnShelvesTable.getFirstColumnName(),
                                    ProductsOnShelvesTable.getThirdColumnName(),
                                    ProductsOnShelvesTable.getTableName(),
                                    ProductsOnShelvesTable.getSecondColumnName(),
                                    ShelvesInVirtualFridgeTable.getFirstColumnName(),
                                    ShelvesInVirtualFridgeTable.getTableName(),
                                    ShelvesInVirtualFridgeTable.getSecondColumnName());


                            Cursor fridgeCursor = menuDataBase.downloadData(fridgeQuery);

                            if (fridgeCursor.getCount() > 0) {
                                fridgeCursor.moveToPosition(-1);
                                while (fridgeCursor.moveToNext()) {
                                    productsInVirtualFridge.add(new Product(fridgeCursor.getInt(0),
                                            fridgeCursor.getDouble(1), ProductFlags.BOUGHT_INDX));
                                }

                                //synchronize with fridge
                                wasSynchronizedWithTheFridge = compareContents(products,
                                        productsInVirtualFridge);


                            } else wasSynchronizedWithTheFridge = false;

                            if(wasSynchronizedWithTheFridge){


                                int extraShelfId;
                                String findExtraShelfIdQuery = String.format("SELECT %s FROM %s " +
                                                "WHERE %s  = %s",
                                        ShelvesInVirtualFridgeTable.getFirstColumnName(),
                                        ShelvesInVirtualFridgeTable.getTableName(),
                                        ShelvesInVirtualFridgeTable.getSecondColumnName(),0);

                                Cursor extraShelfIdCursor = menuDataBase
                                        .downloadData(findExtraShelfIdQuery);
                                if(extraShelfIdCursor.getCount() == 1){
                                    extraShelfIdCursor.moveToPosition(0);
                                    extraShelfId = extraShelfIdCursor.getInt(0);
                                }else extraShelfId = -1;

                                if(extraShelfId > 0) {
                                    String whereClause = String.format("%s = ?",
                                            ProductsOnShelvesTable.getSecondColumnName());
                                    String[] whereArgs = {String.valueOf(extraShelfId)};
                                    menuDataBase.delete(ProductsOnShelvesTable.getTableName(), whereClause,
                                            whereArgs);

                                    for (Product product : productsInVirtualFridge) {
                                        if (product.getQuantity() > 0) {
                                            menuDataBase.insert(ProductsOnShelvesTable.getTableName(),
                                                    ProductsOnShelvesTable
                                                            .getContentValues(new Product(product.getProductId(),
                                                                            product.getQuantity(),
                                                                            product.getProductFlagId()),
                                                                    extraShelfId));
                                        }
                                    }
                                }else result = false;

                            }

                        //add products to shelf
                        for (Product product : products) {
                            if(menuDataBase.insert(ProductsOnShelvesTable.getTableName(),
                                    ProductsOnShelvesTable
                                            .getContentValues(new Product(product.getProductId(),
                                                    product.getQuantity(),
                                                    product.getProductFlagId()), shelfId)) == -1){
                                result = false;
                                break;
                            }
                        }
                        }
                    }


                }

                menuDataBase.close();
                return result;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if(result) Log.i("ShelvesArchive", "Archive success");
                else Log.i("ShelvesArchive", "Archive failed");

            }
        }.execute();
    }

    private int wasAddedPreviously(long productId, List<Product> products) {
        for (int i = 0; i < products.size(); i++) {
            if(products.get(i).getProductId() == productId) return i;
        }

        return -1;
    }

    private boolean compareContents(List<Product> productsInDailyMenu,
                                    List<Product> productsInVirtualFridge) {
        boolean wasSynchronized = false;
        List<Product> comparedProducts = new ArrayList<>();
        for (Product productInDailyMenu : productsInDailyMenu) {
            for (Product productInFridge : productsInVirtualFridge) {
                if(productInDailyMenu.getProductId() == productInFridge.getProductId()){
                    wasSynchronized = true;
                    if(productInFridge.getQuantity() > 0) {
                        if (productInDailyMenu.getQuantity() <= productInFridge.getQuantity()) {
                            productInDailyMenu.setProductFlagId(ProductFlags.BOUGHT_INDX);
                            productInFridge
                                    .setQuantity(productInFridge.getQuantity()
                                            - productInDailyMenu.getQuantity());
                        } else {
                            productInDailyMenu
                                    .setQuantity(productInDailyMenu.getQuantity() -
                                            productInFridge.getQuantity());
                            comparedProducts.add(new Product(productInDailyMenu.getProductId(),
                                    productInFridge.getQuantity(), ProductFlags.BOUGHT_INDX));
                            productInFridge.setQuantity(0);
                        }
                    }
                }
            }

        }

        productsInDailyMenu.addAll(comparedProducts);


        return wasSynchronized;
    }
}
