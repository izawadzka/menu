package com.example.dell.menu.virtualfridge.screens;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.dell.menu.App;
import com.example.dell.menu.R;
import com.example.dell.menu.shoppinglist.screens.ShowProductsInListActivity;
import com.example.dell.menu.virtualfridge.events.ReloadContentEvent;
import com.example.dell.menu.virtualfridge.screens.shelf.ProductsOnTheShelfMainAdapter;
import com.example.dell.menu.virtualfridge.screens.shelf.ShelfFragment;


import butterknife.Bind;
import butterknife.ButterKnife;

public class AddProductActivity extends AppCompatActivity {

    public static final String ADD_TO_LIST_KEY = "add to list";
    public static final String ADD_TO_PRESENT_SHELF = "add to present  shelf";
    public static final String ADD_TO_EXTRA_SHELF = "add to extra shelf";
    public static final String SHELF_ID = "shelfId";
    public static final String SHOPPING_LIST_ID_KEY = "shoppingListId";


    ProductsOnTheShelfMainAdapter adapter;
    @Bind(R.id.productsOnTheShelfRecyclerView)
    RecyclerView productsOnTheShelfRecyclerView;
    AddProductManager manager;
    private boolean shopping_list_mode = false;
    private boolean present_shelf_mode = false;
    private boolean extra_shelf_mode = false;
    private long shoppingListId;
    private int shelfId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_products_shelf);
        ButterKnife.bind(this);

        manager = ((App)getApplication()).getAddProductManager();


        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        shopping_list_mode = getIntent()
                .getBooleanExtra(ADD_TO_LIST_KEY, false);
        if(shopping_list_mode) shoppingListId = getIntent()
                .getLongExtra(SHOPPING_LIST_ID_KEY, -1);

        if(getIntent().getBooleanExtra(ADD_TO_PRESENT_SHELF, present_shelf_mode)){
            present_shelf_mode = true;
            shelfId = getIntent().getIntExtra(SHELF_ID, -1);
        }

        if(getIntent().getBooleanExtra(ADD_TO_EXTRA_SHELF, extra_shelf_mode)){
            extra_shelf_mode = true;
            shelfId = getIntent().getIntExtra(SHELF_ID, -1);
        }

        productsOnTheShelfRecyclerView.setLayoutManager(new LinearLayoutManager(this));
       if(shopping_list_mode || extra_shelf_mode) adapter = new ProductsOnTheShelfMainAdapter(((App) getApplication())
               .getBus(), ProductsOnTheShelfMainAdapter.ADD_MODE);
        else if(present_shelf_mode) adapter = new ProductsOnTheShelfMainAdapter(((App) getApplication()).getBus(),
               ProductsOnTheShelfMainAdapter.ADD_TO_PRESENT_SHELF_MODE);
        productsOnTheShelfRecyclerView.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        manager.onAttach(this);
        manager.setShoppingListMode(shopping_list_mode, shoppingListId);
        manager.setPresentShelfMode(present_shelf_mode, shelfId);
        manager.setExtraShelfMode(extra_shelf_mode, shelfId);
        if(shopping_list_mode) setTitle("Add products to the list");
        else if(present_shelf_mode) setTitle("Add products to the shelf");
        else if(extra_shelf_mode) setTitle("Add products to extra shelf");
        manager.loadProducts();
    }

    @Override
    protected void onStop() {
        super.onStop();
        manager.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(extra_shelf_mode || present_shelf_mode ){
            ((App)getApplication()).getBus().post(new ReloadContentEvent());
        }
    }

    public void showProducts() {
        adapter.setProducts(manager.getProducts());
    }

    public void loadingFailed() {
        Toast.makeText(this, "There's no product to display", Toast.LENGTH_LONG).show();
        finish();
    }

    public void makeAStatement(String statement, int duration) {
        Toast.makeText(this, statement, duration).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void askUserIfWantToFillOtherShelvesFirst(final int productId, final double quantity) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Other shelves need that product");

        alertDialogBuilder.setMessage("Other shelves need that product. Click 'YES' to distribute it " +
                "first between those shelves and the rest add here, to the extra shelf. \n" +
                "Click 'NO' to add it directly to the extra shelf")
                .setCancelable(true)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        manager.distributeProductBetweenShelves(productId, quantity);
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        manager.addProductToExtraShelf(productId, quantity);
                    }
                })
                .setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    public void productSuccessfullyDistributed() {
        Toast.makeText(this, "Product was successfully distributed", Toast.LENGTH_SHORT).show();
        manager.loadProducts();
    }

    public void addingProductSuccess() {
        Toast.makeText(this, "Successfully added product", Toast.LENGTH_SHORT).show();
        manager.loadProducts();
    }

    public void addingProductFailed() {
        Toast.makeText(this, "Failed to add product", Toast.LENGTH_SHORT).show();
        manager.loadProducts();
    }
}
