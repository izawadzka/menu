package com.example.dell.menu.virtualfridge.screens;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.dell.menu.data.MenuDataBase;
import com.example.dell.menu.data.tables.DailyMenusTable;
import com.example.dell.menu.data.tables.ProductsOnShelvesTable;
import com.example.dell.menu.data.tables.ShelvesInVirtualFridgeTable;
import com.example.dell.menu.virtualfridge.events.AmountOfProductChangedEvent;
import com.example.dell.menu.virtualfridge.events.DeleteProductFromFridgeEvent;
import com.example.dell.menu.menuplanning.objects.Product;
import com.example.dell.menu.data.tables.ProductsTable;
import com.example.dell.menu.data.tables.VirtualFridgeTable;
import com.example.dell.menu.virtualfridge.flags.ProductFlags;
import com.example.dell.menu.virtualfridge.objects.ShelfInVirtualFridge;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TooManyListenersException;

import butterknife.Bind;

/**
 * Created by Dell on 28.10.2017.
 */

public class VirtualFridgeManager {
    public static final int RESULT_EMPTY = 0;
    public static final int RESULT_ERROR = -1;
    public static final int RESULT_OK = 1;

    private final Bus bus;
    private VirtualFridgeFragment virtualFridgeFragment;
    private List<ShelfInVirtualFridge> shelves = new ArrayList<>();

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

    public List<ShelfInVirtualFridge> getShelves() {
        return shelves;
    }

    public void clearShelves(){
        shelves.clear();
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
        updateAmountOfProduct(event.productId, event.quantity, event.oldQuantity, event.shelfId,
                event.productFlagId, event.extraShelf);
    }

    private void updateAmountOfProduct(final int productId, final double quantity,
                                       final double oldQuantity, final long shelfId,
                                       final int productFlagId, final boolean extraShelf) {
        if(virtualFridgeFragment != null){
            new AsyncTask<Void, Void, Boolean>(){

                @Override
                protected Boolean doInBackground(Void... params) {
                    boolean result = true;
                    MenuDataBase menuDataBase = MenuDataBase
                            .getInstance(virtualFridgeFragment.getActivity());


                    //amount of eaten + not_eaten = const and amount of
                    // bought+to_buy+on_list = const

                    if(productFlagId == ProductFlags.BOUGHT_INDX){
                        //at first check if it's not the extra shelf
                        if(extraShelf){
                            ContentValues editContentValues = new ContentValues();
                            editContentValues.put(ProductsOnShelvesTable.getThirdColumnName(),
                                    quantity);

                            String whereClause = String.format("%s = ? AND %s = ?",
                                    ProductsOnShelvesTable.getFirstColumnName(),
                                    ProductsOnShelvesTable.getSecondColumnName());
                            String[] whereArgs = {String.valueOf(productId),
                                    String.valueOf(shelfId)};

                            result = menuDataBase.update(ProductsOnShelvesTable.getTableName(),
                                    editContentValues, whereClause, whereArgs) == 1;
                        }else {
                            double boughtQuantity = oldQuantity;
                            double toBuyQuantity = 0, onShoppingListQuantity = 0;
                            String query = String.format("SELECT %s, %s FROM %s " +
                                            "WHERE %s = %s AND %s = %s",
                                    ProductsOnShelvesTable.getThirdColumnName(),
                                    ProductsOnShelvesTable.getFourthColumnName(),
                                    ProductsOnShelvesTable.getTableName(),
                                    ProductsOnShelvesTable.getFirstColumnName(), productId,
                                    ProductsOnShelvesTable.getSecondColumnName(), shelfId);

                            Cursor productsCursor = menuDataBase.downloadData(query);
                            if (productsCursor.getCount() > 0) {
                                productsCursor.moveToPosition(-1);
                                while (productsCursor.moveToNext()) {
                                    if (productsCursor.getInt(1) == ProductFlags.NEED_TO_BE_BOUGHT_IND)
                                        toBuyQuantity = productsCursor.getDouble(0);
                                    else if (productsCursor.getInt(1) == ProductFlags.ADDED_TO_SHOPPING_LIST_INDX)
                                        onShoppingListQuantity = productsCursor.getDouble(0);
                                }

                                String boughtWhereClause = String.format("%s = ? AND %s = ? AND %s = ?",
                                        ProductsOnShelvesTable.getFirstColumnName(),
                                        ProductsOnShelvesTable.getSecondColumnName(),
                                        ProductsOnShelvesTable.getFourthColumnName());

                                String[] boughtWhereArgs = {String.valueOf(productId),
                                        String.valueOf(shelfId), String.valueOf(ProductFlags.BOUGHT_INDX)};


                                String toBuyWhereClause = String.format("%s = ? AND %s = ? " +
                                                "AND %s = ?",
                                        ProductsOnShelvesTable.getFirstColumnName(),
                                        ProductsOnShelvesTable.getSecondColumnName(),
                                        ProductsOnShelvesTable.getFourthColumnName());

                                String[] toBuyWhereArgs = {String.valueOf(productId),
                                        String.valueOf(shelfId),
                                        String.valueOf(ProductFlags.NEED_TO_BE_BOUGHT_IND)};


                                if (quantity > oldQuantity) {
                                    if (toBuyQuantity < quantity - oldQuantity) {
                                        //delete 'to_buy' and increase 'bought' with value of 'to_buy'
                                        menuDataBase.delete(ProductsOnShelvesTable.getTableName(),
                                                toBuyWhereClause, toBuyWhereArgs);
                                        ContentValues boughtContentValues = new ContentValues();
                                        boughtContentValues
                                                .put(ProductsOnShelvesTable.getThirdColumnName(),
                                                        oldQuantity + toBuyQuantity);

                                        menuDataBase.update(ProductsOnShelvesTable.getTableName(),
                                                boughtContentValues, boughtWhereClause, boughtWhereArgs);

                                    } else if (toBuyQuantity == quantity - oldQuantity) {
                                        //delete from 'to_buy" and everything put in "bought"

                                        menuDataBase.delete(ProductsOnShelvesTable.getTableName(),
                                                toBuyWhereClause, toBuyWhereArgs);


                                        ContentValues boughtContentValues = new ContentValues();
                                        boughtContentValues
                                                .put(ProductsOnShelvesTable.getThirdColumnName(), quantity);

                                        menuDataBase.update(ProductsOnShelvesTable.getTableName(),
                                                boughtContentValues, boughtWhereClause, boughtWhereArgs);
                                    } else {
                                        ContentValues toBuyContentValues = new ContentValues();
                                        toBuyContentValues
                                                .put(ProductsOnShelvesTable.getThirdColumnName(),
                                                        toBuyQuantity - (quantity - oldQuantity));
                                        menuDataBase.update(ProductsOnShelvesTable.getTableName(),
                                                toBuyContentValues, toBuyWhereClause, toBuyWhereArgs);

                                        ContentValues boughtContentValues = new ContentValues();
                                        boughtContentValues
                                                .put(ProductsOnShelvesTable.getThirdColumnName(), quantity);

                                        menuDataBase.update(ProductsOnShelvesTable.getTableName(),
                                                boughtContentValues, boughtWhereClause, boughtWhereArgs);
                                    }
                                } else if (quantity < oldQuantity) {
                                    if (toBuyQuantity > 0) {
                                        ContentValues toBuyContentValues = new ContentValues();
                                        toBuyContentValues
                                                .put(ProductsOnShelvesTable.getThirdColumnName(),
                                                        toBuyQuantity + (oldQuantity - quantity));
                                        menuDataBase.update(ProductsOnShelvesTable.getTableName(),
                                                toBuyContentValues, toBuyWhereClause, toBuyWhereArgs);
                                    } else {
                                        menuDataBase.insert(ProductsOnShelvesTable.getTableName(),
                                                ProductsOnShelvesTable
                                                        .getContentValues(new Product(productId,
                                                                        oldQuantity - quantity,
                                                                        ProductFlags.NEED_TO_BE_BOUGHT_IND),
                                                                shelfId));
                                    }
                                    ContentValues boughtContentValues = new ContentValues();
                                    boughtContentValues
                                            .put(ProductsOnShelvesTable.getThirdColumnName(), quantity);

                                    menuDataBase.update(ProductsOnShelvesTable.getTableName(),
                                            boughtContentValues, boughtWhereClause, boughtWhereArgs);
                                }
                            } else result = false;
                        }
                    }else if(productFlagId == ProductFlags.EATEN_INDX){
                        // TODO: 30.11.2017
                    }
                    menuDataBase.close();
                    return  result;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    if(virtualFridgeFragment != null){
                        if(result){
                            if(extraShelf) virtualFridgeFragment.updateExtraShelfSuccess();
                            else virtualFridgeFragment.updateSuccess();
                        }
                        else{
                            if(extraShelf) virtualFridgeFragment.updateExtraShelfFailed();
                            else virtualFridgeFragment.updateFailed();
                        }
                    }
                }

            }.execute();
        }
    }

    public void loadContent(Date startOfPeriod, Date endOfPeriod){
        if(virtualFridgeFragment != null){
            shelves.clear();

            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            final String start = sdf.format(startOfPeriod);
            final String end = sdf.format(endOfPeriod);

            new AsyncTask<Void, Void, Integer>(){

                @Override
                protected Integer doInBackground(Void... params) {
                    int result = RESULT_OK;
                    MenuDataBase menuDataBase = MenuDataBase
                            .getInstance(virtualFridgeFragment.getActivity());


                    String query = String.format("SELECT %s, sh.%s,%s FROM %s sh JOIN %s dm ON" +
                            " sh.%s = dm.%s WHERE dm.%s IN " +
                            "( SELECT dm.%s FROM %s WHERE dm.%s > 0 AND dm.%s <= '%s' AND dm.%s >= '%s' )",
                            ShelvesInVirtualFridgeTable.getFirstColumnName(),
                            ShelvesInVirtualFridgeTable.getSecondColumnName(),
                            DailyMenusTable.getSecondColumnName(),
                            ShelvesInVirtualFridgeTable.getTableName(),
                            DailyMenusTable.getTableName(),
                            ShelvesInVirtualFridgeTable.getSecondColumnName(),
                            DailyMenusTable.getFirstColumnName(),
                            DailyMenusTable.getFirstColumnName(),
                            DailyMenusTable.getFirstColumnName(),
                            DailyMenusTable.getTableName(),
                            DailyMenusTable.getFirstColumnName(),
                            DailyMenusTable.getSecondColumnName(),
                            end,
                            DailyMenusTable.getSecondColumnName(),
                            start);


                    Cursor cursor = menuDataBase.downloadData(query);
                    if(cursor.getCount() > 0){
                        cursor.moveToPosition(-1);
                        while (cursor.moveToNext()){
                            try {
                                shelves.add(new ShelfInVirtualFridge(cursor.getInt(0), cursor.getInt(1),
                                        isArchived(cursor.getString(2)),
                                        cursor.getString(2)));
                            } catch (ParseException e) {
                                Log.e("VirtualFridgeManager", e.getLocalizedMessage());
                                result = RESULT_ERROR;
                                break;
                            }
                        }


                        if(result == RESULT_OK){
                            Collections.sort(shelves, new Comparator<ShelfInVirtualFridge>() {
                                @Override
                                public int compare(ShelfInVirtualFridge o1, ShelfInVirtualFridge o2) {
                                    return (java.sql.Date.valueOf(o1.getDate())
                                            .compareTo(java.sql.Date.valueOf(o2.getDate())));
                                }
                            });
                            for (ShelfInVirtualFridge shelf : shelves) {
                                String productsQuery = String.format("SELECT pon.%s, %s, %s, %s, %s FROM %s" +
                                        " pon JOIN %s p ON pon.%s = p.%s WHERE pon.%s = %s",
                                        ProductsOnShelvesTable.getFirstColumnName(),
                                        ProductsOnShelvesTable.getThirdColumnName(),
                                        ProductsOnShelvesTable.getFourthColumnName(),
                                        ProductsTable.getSecondColumnName(),
                                        ProductsTable.getFifthColumnName(),
                                        ProductsOnShelvesTable.getTableName(),
                                        ProductsTable.getTableName(),
                                        ProductsOnShelvesTable.getFirstColumnName(),
                                        ProductsTable.getFirstColumnName(),
                                        ProductsOnShelvesTable.getSecondColumnName(),
                                        shelf.getShelfId());

                                Cursor productsCursor = menuDataBase.downloadData(productsQuery);
                                if(productsCursor.getCount() > 0){
                                    productsCursor.moveToPosition(-1);
                                    while (productsCursor.moveToNext()){
                                        shelf.addProduct(new Product(productsCursor.getInt(0),
                                                productsCursor.getString(3),
                                                productsCursor.getDouble(1),
                                                productsCursor.getString(4),
                                                productsCursor.getInt(2)));
                                    }
                                }else{
                                    result = RESULT_ERROR;
                                    break;
                                }
                            }

                        }
                    }else result = RESULT_EMPTY;
                    menuDataBase.close();
                    return result;
                }

                private boolean isArchived(String dailyMenuDateString) throws ParseException {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(sdf.parse(dailyMenuDateString));
                    Calendar today = Calendar.getInstance();

                    int calendarYear = calendar.get(Calendar.YEAR);
                    int calendarMonth = calendar.get(Calendar.MONTH)+1;
                    int calendarDay = calendar.get(Calendar.DAY_OF_MONTH);

                    int todayYear = today.get(Calendar.YEAR);
                    int todayMonth = today.get(Calendar.MONTH)+1;
                    int todayDay = today.get(Calendar.DAY_OF_MONTH);

                    if(calendarYear < todayYear) return true;
                    else if(calendarYear == todayYear){
                        if(calendarMonth < todayMonth) return true;
                        else if ( calendarMonth == todayMonth && calendarDay < todayDay) return true;
                    }
                    return false;
                }

                @Override
                protected void onPostExecute(Integer result) {
                    if(virtualFridgeFragment != null){
                        if(result == RESULT_OK) virtualFridgeFragment.setAdapter();
                        else if(result == RESULT_EMPTY) {
                            virtualFridgeFragment.setAdapter();

                            virtualFridgeFragment.fridgeEmpty();
                        }
                        else if(result == RESULT_ERROR) virtualFridgeFragment.loadingContentFailed();
                    }
                }
            }.execute();
        }
    }

    public void loadExtraShelf() {
        if(virtualFridgeFragment != null){
            shelves.clear();
            new AsyncTask<Void, Void,Integer>(){

                @Override
                protected Integer doInBackground(Void... params) {
                    int result = RESULT_OK;
                    MenuDataBase menuDataBase = MenuDataBase
                            .getInstance(virtualFridgeFragment.getActivity());
                    String query = String.format("SELECT %s FROM %s WHERE %s = 0",
                            ShelvesInVirtualFridgeTable.getFirstColumnName(),
                            ShelvesInVirtualFridgeTable.getTableName(),
                            ShelvesInVirtualFridgeTable.getSecondColumnName());

                    Cursor cursor = menuDataBase.downloadData(query);
                    if(cursor.getCount() == 1){
                        cursor.moveToPosition(0);
                        shelves.add(new ShelfInVirtualFridge(cursor.getInt(0), 0,
                                false,""));


                        String productsQuery = String.format("SELECT pon.%s, %s, %s, %s, %s FROM %s" +
                                        " pon JOIN %s p ON pon.%s = p.%s WHERE pon.%s = %s",
                                ProductsOnShelvesTable.getFirstColumnName(),
                                ProductsOnShelvesTable.getThirdColumnName(),
                                ProductsOnShelvesTable.getFourthColumnName(),
                                ProductsTable.getSecondColumnName(),
                                ProductsTable.getFifthColumnName(),
                                ProductsOnShelvesTable.getTableName(),
                                ProductsTable.getTableName(),
                                ProductsOnShelvesTable.getFirstColumnName(),
                                ProductsTable.getFirstColumnName(),
                                ProductsOnShelvesTable.getSecondColumnName(),
                                shelves.get(0).getShelfId());

                        Cursor productsCursor = menuDataBase.downloadData(productsQuery);
                        if(productsCursor.getCount() > 0){
                            productsCursor.moveToPosition(-1);
                            while (productsCursor.moveToNext()){
                                shelves.get(0).addProduct(new Product(productsCursor.getInt(0),
                                        productsCursor.getString(3),
                                        productsCursor.getDouble(1),
                                        productsCursor.getString(4),
                                        productsCursor.getInt(2)));
                            }
                        }else{
                            result = RESULT_EMPTY;
                        }
                    }
                    else result = RESULT_ERROR;
                    menuDataBase.close();
                    return result;
                }

                @Override
                protected void onPostExecute(Integer result) {
                    if(virtualFridgeFragment != null){
                        if(result == RESULT_OK) virtualFridgeFragment.setAdapter();
                        else if(result == RESULT_EMPTY){
                            virtualFridgeFragment.setAdapter();
                            virtualFridgeFragment.emptyExtraShelf();
                        }
                        else if(result == RESULT_ERROR) virtualFridgeFragment.loadingContentFailed();
                    }
                }
            }.execute();
        }
    }
}
