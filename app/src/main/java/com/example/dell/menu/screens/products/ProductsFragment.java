package com.example.dell.menu.screens.products;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.dell.menu.App;
import com.example.dell.menu.R;
import com.example.dell.menu.objects.Product;
import com.example.dell.menu.screens.products.addOrEdit.AddOrEditProductActivity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Dell on 26.05.2017.
 */

public class ProductsFragment extends Fragment {
    public static final int REQUEST_CODE_ADD = 1;
    public static final int RESULT_OK = 0;
    public static final int RESULT_ERROR = -1;
    public static final int RESULT_CANCEL = 1;
    @Bind(R.id.productRecyclerView)
    RecyclerView productRecyclerView;

    private ProductFragmentManager productFragmentManager;
    private ProductsAdapter adapter;


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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        productRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ProductsAdapter(((App)getActivity().getApplication()).getBus());
        productRecyclerView.setAdapter(adapter);
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
        inflater.inflate(R.menu.products, menu);
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
            Toast.makeText(getActivity(), "New product added", Toast.LENGTH_SHORT).show();
        }else if(requestCode == REQUEST_CODE_ADD && resultCode == RESULT_ERROR){
            Toast.makeText(getActivity(), "Failed to add a new product", Toast.LENGTH_LONG).show();
        }else if(requestCode == REQUEST_CODE_ADD && resultCode == RESULT_CANCEL){
            Toast.makeText(getActivity(), "Cancel adding product", Toast.LENGTH_SHORT).show();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);
        ButterKnife.bind(this, view);
        return view;
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
    }
}
