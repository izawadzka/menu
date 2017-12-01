package com.example.dell.menu.virtualfridge.screens;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.dell.menu.App;
import com.example.dell.menu.R;
import com.example.dell.menu.shoppinglist.screens.ShowProductsInListActivity;
import com.example.dell.menu.virtualfridge.screens.shelf.ProductsOnTheShelfMainAdapter;


import butterknife.Bind;
import butterknife.ButterKnife;

public class AddProductActivity extends AppCompatActivity {

    ProductsOnTheShelfMainAdapter adapter;
    @Bind(R.id.productsOnTheShelfRecyclerView)
    RecyclerView productsOnTheShelfRecyclerView;
    AddProductManager manager;
    private boolean shopping_list_mode = false;
    private boolean fridge_mode = false;
    private long shoppingListId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_products_shelf);
        ButterKnife.bind(this);

        manager = ((App)getApplication()).getAddProductManager();

        productsOnTheShelfRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ProductsOnTheShelfMainAdapter(((App) getApplication()).getBus(),
                ProductsOnTheShelfMainAdapter.ADD_MODE);
        productsOnTheShelfRecyclerView.setAdapter(adapter);

        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        shopping_list_mode = getIntent()
                .getBooleanExtra(ShowProductsInListActivity.ADD_DO_LIST_KEY, false);
        if(shopping_list_mode) shoppingListId = getIntent()
                .getLongExtra(ShowProductsInListActivity.SHOPPING_LIST_ID_KEY, -1);
        fridge_mode = getIntent()
                .getBooleanExtra(VirtualFridgeFragment.ADD_TO_FRIDGE_KEY, fridge_mode);
    }

    @Override
    protected void onStart() {
        super.onStart();
        manager.onAttach(this);
        manager.setShoppingListMode(shopping_list_mode, shoppingListId);
        manager.setFridgeMode(fridge_mode);
        if(shopping_list_mode) setTitle("Add products to the list");
        else if(fridge_mode) setTitle("Add products to the fridge");
        manager.loadProducts();
    }

    @Override
    protected void onStop() {
        super.onStop();
        manager.onStop();
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
}
