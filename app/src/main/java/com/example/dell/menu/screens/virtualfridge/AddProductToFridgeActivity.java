package com.example.dell.menu.screens.virtualfridge;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.dell.menu.App;
import com.example.dell.menu.R;
import com.example.dell.menu.objects.menuplanning.Product;
import com.example.dell.menu.screens.virtualfridge.shelf.ProductsOnTheShelfAdapter;


import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddProductToFridgeActivity extends AppCompatActivity {

    ProductsOnTheShelfAdapter adapter;
    @Bind(R.id.productsOnTheShelfRecyclerView)
    RecyclerView productsOnTheShelfRecyclerView;
    AddProductToFridgeManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_products_shelf);
        ButterKnife.bind(this);

        manager = ((App)getApplication()).getAddProductToFridgeManager();

        productsOnTheShelfRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ProductsOnTheShelfAdapter(((App) getApplication()).getBus(),
                ProductsOnTheShelfAdapter.ADD_MODE);
        productsOnTheShelfRecyclerView.setAdapter(adapter);

        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onStart() {
        super.onStart();
        manager.onAttach(this);
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
