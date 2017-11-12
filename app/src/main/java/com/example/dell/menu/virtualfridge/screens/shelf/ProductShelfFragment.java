package com.example.dell.menu.virtualfridge.screens.shelf;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dell.menu.App;
import com.example.dell.menu.R;
import com.example.dell.menu.virtualfridge.objects.ProductsShelf;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Dell on 28.10.2017.
 */

public class ProductShelfFragment extends Fragment {

    public static final String SHELF_KEY = "shelf";
    @Bind(R.id.productsOnTheShelfRecyclerView)
    RecyclerView productsOnTheShelfRecyclerView;

    private ProductsShelf productsShelf;
    private ProductsOnTheShelfAdapter adapter;

    public static ProductShelfFragment newInstance(ProductsShelf productsShelf) {
        Bundle args = new Bundle();
        args.putSerializable(SHELF_KEY, productsShelf);
        ProductShelfFragment fragment = new ProductShelfFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products_shelf, container, false);
        ButterKnife.bind(this, view);

        productsShelf = (ProductsShelf) getArguments().getSerializable(SHELF_KEY);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        showProducts();
    }

    private void showProducts() {
        adapter.setProducts(productsShelf.getProducts());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        productsOnTheShelfRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ProductsOnTheShelfAdapter(((App)getActivity().getApplication()).getBus(),
                ProductsOnTheShelfAdapter.SHOW_MODE);
        productsOnTheShelfRecyclerView.setAdapter(adapter);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
