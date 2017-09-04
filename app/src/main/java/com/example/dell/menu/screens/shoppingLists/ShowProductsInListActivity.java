package com.example.dell.menu.screens.shoppingLists;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.dell.menu.App;
import com.example.dell.menu.R;
import com.example.dell.menu.objects.Product;
import com.example.dell.menu.objects.ShoppingList;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ShowProductsInListActivity extends AppCompatActivity {


    @Bind(R.id.productsInShoppingListRecyclerView)
    RecyclerView productsInShoppingListRecyclerView;

    private ProductsAdapter adapter;
    private ShowProductsInListManager showProductsInListManager;
    private int shoppingListId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_products_in_list);
        ButterKnife.bind(this);

        showProductsInListManager = ((App)getApplication()).getShowProductsInListManager();
        productsInShoppingListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductsAdapter(((App)getApplication()).getBus());
        productsInShoppingListRecyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        shoppingListId = intent.getIntExtra(ShoppingListsFragment.SHOPPING_LIST_ID_KEY, 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        showProductsInListManager.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        showProductsInListManager.onAttach(this);
        showProductsInListManager.setShoppingListId(shoppingListId);
        showProductsInListManager.loadProducts();
    }

    public void loadingProductsFailed() {
        Toast.makeText(this, "Error while trying to load products into shopping list", Toast.LENGTH_SHORT).show();
    }

    public void setProducts(List<Product> result){
        adapter.setProducts(result);
    }

    public void makeAStatement(String statement, int duration){
        Toast.makeText(this, statement, duration).show();
    }

    public void updateQuantitySuccess(ProductsAdapter.ProductsInListViewHolder holder) {
        makeAStatement("Successfully updated Quantity", Toast.LENGTH_SHORT);
        adapter.quantityUpdatedSuccessfully(holder);
    }

    public void updateQuantityFailed() {
        makeAStatement("An error occured while an attempt to update quantity", Toast.LENGTH_LONG);
    }
}
