package com.example.dell.menu.menuplanning.screens.products;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.dell.menu.App;
import com.example.dell.menu.R;
import com.example.dell.menu.data.backup.BackupTimer;
import com.example.dell.menu.menuplanning.objects.Product;
import com.example.dell.menu.menuplanning.screens.products.addOrEdit.AddOrEditProductActivity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Dell on 26.05.2017.
 */

public class ProductsFragment extends Fragment implements ProductsAdapter.ProductClickedListener{
    public static final int RESULT_OK = 0;
    public static final int RESULT_ERROR = -1;
    public static final int RESULT_CANCEL = 1;

    public static final int REQUEST_CODE_ADD = 1;
    public static final int REQUEST_CODE_EDIT = 2;
    public static final int REQUEST_CODE_SHOW = 3;

    public static final String PRODUCT_ID_KEY = "productId";
    public static final String SHOW_MODE_KEY = "show_mode";

    private ProductFragmentManager productFragmentManager;
    private ProductsAdapter adapter;

    @Bind(R.id.productRecyclerView)
    RecyclerView productRecyclerView;

    @Override
    public void onStart() {
        super.onStart();
        productFragmentManager.onAttach(this);
        productFragmentManager.loadProducts();
    }

    @Override
    public void onStop() {
        super.onStop();
        productFragmentManager.onStop();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        productRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ProductsAdapter(((App)getActivity().getApplication()).getBus());
        adapter.setProductClickedListener(this);
        productRecyclerView.setAdapter(adapter);
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper
                .SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT){

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Toast.makeText(getContext(), "Swipe to delete product", Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.remove(viewHolder.getAdapterPosition());
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(productRecyclerView);

        getActivity().setTitle("Products");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        productFragmentManager = ((App)getActivity().getApplication()).getProductFragmentManager();
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
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
                productFragmentManager.findProducts(newText);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_add){
            addNewProduct();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addNewProduct() {
        startActivityForResult(new Intent(getActivity(), AddOrEditProductActivity.class), REQUEST_CODE_ADD);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_ADD && resultCode == RESULT_OK){
            Toast.makeText(getContext(), "New product added", Toast.LENGTH_SHORT).show();
        }else if(requestCode == REQUEST_CODE_ADD && resultCode == RESULT_ERROR){
            Toast.makeText(getContext(), "Failed to add a new product", Toast.LENGTH_LONG).show();
        }else if(requestCode == REQUEST_CODE_ADD && resultCode == RESULT_CANCEL){
            Toast.makeText(getContext(), "Cancel adding product", Toast.LENGTH_SHORT).show();
        }else if(requestCode == REQUEST_CODE_EDIT && resultCode == RESULT_OK){
            Toast.makeText(getContext(), "The product was updated successfully", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void showProducts(List<Product> result) {
        adapter.setProducts(result);
        adapter.notifyDataSetChanged();
    }

    public void deleteSuccess() {
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        BackupTimer backupTimer = new BackupTimer((App)getActivity().getApplication());
        backupTimer.start();
    }

    @Override
    public void productClicked(Product product) {
        Intent intent = new Intent(this.getContext(), AddOrEditProductActivity.class);
        intent.putExtra(SHOW_MODE_KEY, true);
        intent.putExtra(PRODUCT_ID_KEY, product.getProductId());
        startActivityForResult(intent, REQUEST_CODE_SHOW);
    }

    public void makeAStatement(String statement, int duration) {
        Toast.makeText(getContext(), statement, duration).show();
    }
}
