package com.example.dell.menu.screens.shoppingLists;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.dell.menu.App;
import com.example.dell.menu.R;
import com.example.dell.menu.objects.menuplanning.Product;
import com.example.dell.menu.screens.virtualfridge.AddProductActivity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ShowProductsInListActivity extends AppCompatActivity {


    public static final String ADD_DO_LIST_KEY = "add do list";
    public static final String SHOPPING_LIST_ID_KEY = "shoppingListId";
    @Bind(R.id.productsInShoppingListRecyclerView)
    RecyclerView productsInShoppingListRecyclerView;

    private ProductsAdapter adapter;
    private ShowProductsInListManager showProductsInListManager;
    private long shoppingListId;


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
        shoppingListId = intent.getLongExtra(ShoppingListsFragment.SHOPPING_LIST_ID_KEY, (long) 0);
        showProductsInListManager.setShoppingListId(shoppingListId);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        setTitle("Show list");
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
        showProductsInListManager.loadProducts();
    }

    public void shoppingListIsEmpty() {
        Toast.makeText(this, "Shopping list is empty. Add products", Toast.LENGTH_SHORT).show();
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

    public void productFromShoppingListDeletedSuccessfully() {
        makeAStatement("Successfully deleted product from shopping list", Toast.LENGTH_SHORT);
        adapter.setProducts(showProductsInListManager.getProductsInShoppingList());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.search_menu);

        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                showProductsInListManager.findProducts(newText);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_add){
            Intent intent = new Intent(this, AddProductActivity.class);
            intent.putExtra(ADD_DO_LIST_KEY, true);
            intent.putExtra(SHOPPING_LIST_ID_KEY, shoppingListId);
            startActivity(intent);
            return true;
        }else if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
