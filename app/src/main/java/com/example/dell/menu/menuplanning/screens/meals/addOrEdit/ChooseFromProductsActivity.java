package com.example.dell.menu.menuplanning.screens.meals.addOrEdit;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.dell.menu.App;
import com.example.dell.menu.R;
import com.example.dell.menu.menuplanning.objects.Product;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChooseFromProductsActivity extends AppCompatActivity {

    @Bind(R.id.productsToChooseRecyclerView)
    RecyclerView productsToChooseRecyclerView;
    private ChooseFromProductsManager chooseFromProductsManager;
    private ProductToChooseAdapter productToChooseAdapter;
    private ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_from_products);
        ButterKnife.bind(this);
        actionBar = getSupportActionBar();
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);

        chooseFromProductsManager = ((App) getApplication()).getChooseFromProductsManager();
        productsToChooseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        productToChooseAdapter = new ProductToChooseAdapter(((App)getApplication()).getBus());
        productsToChooseRecyclerView.setAdapter(productToChooseAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        chooseFromProductsManager.onAttach(this);
        chooseFromProductsManager.loadProducts();
    }

    @Override
    protected void onStop() {
        super.onStop();
        chooseFromProductsManager.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.search_menu);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                chooseFromProductsManager.searchProducts(newText);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void showProducts(List<Product> products) {
        List<Product> listOfAlreadyAddedProducts = ((App)getApplication())
                .getAddOrEditMealManager().getListOfProducts();
        productToChooseAdapter.setProducts(products, listOfAlreadyAddedProducts);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            setResult(AddOrEditMealActivity.RESULT_OK);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void makeAStatement(String statement, int duration) {
        Toast.makeText(this, statement, duration).show();
    }
}
