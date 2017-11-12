package com.example.dell.menu.virtualfridge.screens;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;

import com.example.dell.menu.data.MenuDataBase;
import com.example.dell.menu.virtualfridge.events.AmountOfProductChangedEvent;
import com.example.dell.menu.virtualfridge.events.DeleteProductFromFridgeEvent;
import com.example.dell.menu.menuplanning.objects.Product;
import com.example.dell.menu.virtualfridge.objects.ProductsShelf;
import com.example.dell.menu.data.tables.ProductsTable;
import com.example.dell.menu.data.tables.VirtualFridgeTable;
import com.example.dell.menu.virtualfridge.screens.VirtualFridgeFragment;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 28.10.2017.
 */

public class VirtualFridgeManager {
    public static final int RESULT_EMPTY = 0;
    public static final int RESULT_ERROR = -1;
    public static final int RESULT_OK = 1;

    private final Bus bus;
    private VirtualFridgeFragment virtualFridgeFragment;
    private List<ProductsShelf> productShelves = new ArrayList<>();

    public VirtualFridgeManager(Bus bus) {
        this.bus = bus;
        bus.register(this);
    }

    public void onAttach(VirtualFridgeFragment virtualFridgeFragment){
        this.virtualFridgeFragment = virtualFridgeFragment;
    }

    public void onStop(){
        virtualFridgeFragment = null;
    }

    List<ProductsShelf> getProductShelves() {
        return productShelves;
    }


    @Subscribe
    public void onDeleteProductFromFridgeEvent(DeleteProductFromFridgeEvent event){
        deleteProduct(event.productId);
    }

    private void deleteProduct(final int productId) {
        if(virtualFridgeFragment != null){
            new AsyncTask<Void, Void, Boolean>() {

                @Override
                protected Boolean doInBackground(Void... params) {
                    boolean result;
                    String[] args = {String.valueOf(productId)};
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(virtualFridgeFragment.getActivity());
                    result = menuDataBase.delete(VirtualFridgeTable.getTableName(),
                            String.format("%s = ?", VirtualFridgeTable.getFirstColumnName()), args) > 0;
                    menuDataBase.close();
                    return result;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    if(virtualFridgeFragment != null){
                        if (result) virtualFridgeFragment.deleteSuccess();
                        else virtualFridgeFragment.deleteFailed();
                    }
                }
            }.execute();
        }
    }

    @Subscribe
    public void onAmountOfProductChangedEvent(AmountOfProductChangedEvent event){
        updateAmountOfProduct(event.productId, event.quantity);
    }

    private void updateAmountOfProduct(final int productId, final double quantity) {
        if(virtualFridgeFragment != null){
            new AsyncTask<Void, Void, Boolean>(){

                @Override
                protected Boolean doInBackground(Void... params) {
                    boolean result;
                    MenuDataBase menuDataBase = MenuDataBase.getInstance(virtualFridgeFragment.getActivity());
                    ContentValues editContentValues = new ContentValues();
                    String[] args = {String.valueOf(productId)};
                    editContentValues.put(VirtualFridgeTable.getSecondColumnName(), quantity);
                    result = menuDataBase.update(VirtualFridgeTable.getTableName(),
                            editContentValues,
                            String.format("%s = ?", VirtualFridgeTable.getFirstColumnName()),
                            args) > 0;
                    menuDataBase.close();
                    return result;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    if(virtualFridgeFragment != null){
                        if(result) virtualFridgeFragment.updateSuccess();
                        else virtualFridgeFragment.updateFailed();
                    }
                }
            }.execute();
        }
    }

    public void loadContent(){
        if(virtualFridgeFragment != null){
            productShelves.clear();
            new AsyncTask<Void, Void, Integer>(){

                @Override
                protected Integer doInBackground(Void... params) {
                    int result = RESULT_OK;
                    MenuDataBase menuDataBase = MenuDataBase
                            .getInstance(virtualFridgeFragment.getActivity());

                    String query = String.format("SELECT %s, %s, %s, %s,%s, %s FROM %s vf JOIN %s pt ON " +
                            "vf.%s = pt.%s ORDER BY %s",
                            VirtualFridgeTable.getFirstColumnName(),
                            VirtualFridgeTable.getSecondColumnName(),
                            VirtualFridgeTable.getThirdColumnName(),
                            ProductsTable.getSecondColumnName(),
                            ProductsTable.getFourthColumnName(),
                            ProductsTable.getFifthColumnName(),
                            VirtualFridgeTable.getTableName(),
                            ProductsTable.getTableName(),
                            VirtualFridgeTable.getFirstColumnName(),
                            ProductsTable.getFirstColumnName(),
                            ProductsTable.getSecondColumnName());

                    Cursor cursor = menuDataBase.downloadData(query);
                    if(cursor.getCount() > 0){
                        cursor.moveToPosition(-1);
                        while (cursor.moveToNext()){
                            int shelfIndx = shelfExists(cursor.getString(4));
                            if(shelfIndx != -1) {
                                productShelves.get(shelfIndx).addProduct(new Product(cursor.getInt(0),
                                        cursor.getString(3), cursor.getString(4),
                                        cursor.getString(5), cursor.getDouble(1), cursor.getDouble(2)));
                            }
                            else{
                                productShelves.add(new ProductsShelf(cursor.getString(4)));
                                productShelves.get(productShelves.size()-1)
                                        .addProduct(new Product(cursor.getInt(0), cursor.getString(3),
                                        cursor.getString(4), cursor.getString(5), cursor.getDouble(1),
                                                cursor.getDouble(2)));
                            }
                        }
                    }else result = RESULT_EMPTY;

                    menuDataBase.close();
                    return result;
                }

                @Override
                protected void onPostExecute(Integer result) {
                    if(virtualFridgeFragment != null){
                        if(result == RESULT_OK) virtualFridgeFragment.setAdapter();
                        else if(result == RESULT_EMPTY) virtualFridgeFragment.fridgeEmpty();
                        else if(result == RESULT_ERROR) virtualFridgeFragment.loadingContentFailed();
                    }
                }
            }.execute();
        }
    }

    private int shelfExists(String shelfName) {
        for (int i = 0; i < productShelves.size(); i++) {
            if(productShelves.get(i).getTypeName().equals(shelfName)) return i;
        }
        return -1;
    }
}
