package com.example.dell.menu.virtualfridge.screens.shelf;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dell.menu.App;
import com.example.dell.menu.R;
import com.example.dell.menu.virtualfridge.objects.ShelfInVirtualFridge;

import javax.annotation.Nullable;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Dell on 29.11.2017.
 */

public class ShelfFragment extends Fragment {
    public static final String SHELF_KEY = "shelf";
    private final static String TAG = "ShelfFragment";
    @Bind(R.id.boughtProductsRecyclerView)
    RecyclerView boughtProductsRecyclerView;
    @Bind(R.id.boughtProductsLayout)
    LinearLayout boughtProductsLayout;
    @Bind(R.id.productsToBuyRecyclerView)
    RecyclerView productsToBuyRecyclerView;
    @Bind(R.id.productsToBuyLayout)
    LinearLayout productsToBuyLayout;
    @Bind(R.id.productsOnShoppingListRecyclerView)
    RecyclerView productsOnShoppingListRecyclerView;
    @Bind(R.id.productsOnShoppingListLayout)
    LinearLayout productsOnShoppingListLayout;
    @Bind(R.id.eatenProductsRecyclerView)
    RecyclerView eatenProductsRecyclerView;
    @Bind(R.id.eatenProductsLayout)
    LinearLayout eatenProductsLayout;
    @Bind(R.id.notEatenProductsRecyclerView)
    RecyclerView notEatenProductsRecyclerView;
    @Bind(R.id.notEatenProductsLayout)
    LinearLayout notEatenProductsLayout;
    @Bind(R.id.boughtProductsLabel)
    TextView boughtProductsLabel;
    private ShelfInVirtualFridge shelf;
    private ProductsOnTheShelfMainAdapter boughtProductsAdapter;
    private ProductsOnTheShelfAdditionalAdapter productsToBuyAdapter;
    private ProductsOnTheShelfAdditionalAdapter productsOnShoppingListAdapter;
    private ProductsOnTheShelfAdditionalAdapter notEatenProductsAdapter;
    private ProductsOnTheShelfMainAdapter eatenProductsAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shelf_in_virtual_fridge, container, false);
        ButterKnife.bind(this, view);
        shelf = (ShelfInVirtualFridge) getArguments().getSerializable(SHELF_KEY);
        return view;
    }

    @Override
    public void onViewCreated(View view, @android.support.annotation.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        boughtProductsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        boughtProductsAdapter = new ProductsOnTheShelfMainAdapter(((App) getActivity().getApplication())
                .getBus(), ProductsOnTheShelfMainAdapter.SHOW_MODE_BOUGHT, shelf.getShelfId());
        boughtProductsRecyclerView.setAdapter(boughtProductsAdapter);

        productsToBuyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        productsToBuyAdapter = new ProductsOnTheShelfAdditionalAdapter(((App) getActivity().getApplication())
                .getBus());
        productsToBuyRecyclerView.setAdapter(productsToBuyAdapter);

        productsOnShoppingListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        productsOnShoppingListAdapter = new ProductsOnTheShelfAdditionalAdapter(((App) getActivity()
                .getApplication()).getBus());
        productsOnShoppingListRecyclerView.setAdapter(productsOnShoppingListAdapter);

        eatenProductsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eatenProductsAdapter = new ProductsOnTheShelfMainAdapter(((App)
                getActivity().getApplication()).getBus(),
                ProductsOnTheShelfMainAdapter.SHOW_MODE_EATEN, shelf.getShelfId());
        eatenProductsRecyclerView.setAdapter(eatenProductsAdapter);

        notEatenProductsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        notEatenProductsAdapter = new ProductsOnTheShelfAdditionalAdapter(((App)
                getActivity().getApplication()).getBus());
        notEatenProductsRecyclerView.setAdapter(notEatenProductsAdapter);
    }

    public static ShelfFragment newInstance(ShelfInVirtualFridge shelf) {
        Bundle args = new Bundle();
        args.putSerializable(SHELF_KEY, shelf);
        ShelfFragment fragment = new ShelfFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        showProducts();
    }

    private void showProducts() {
        if (!shelf.isArchived()) {
            if (shelf.getBoughtProducts().size() > 0) {
                if (shelf.isExtraShelf()){
                    boughtProductsAdapter.setExtraShelf(true);
                    boughtProductsLabel.setText("Content of the extra shelf:");
                }else boughtProductsAdapter.setExtraShelf(false);
                    boughtProductsAdapter.setProducts(shelf.getBoughtProducts());
            } else boughtProductsLayout.setVisibility(View.GONE);

            if (shelf.getProductsToBuy().size() > 0)
                productsToBuyAdapter.setProducts(shelf.getProductsToBuy());
            else productsToBuyLayout.setVisibility(View.GONE);

            if (shelf.getProductsOnShoppingLists().size() > 0)
                productsOnShoppingListAdapter.setProducts(shelf.getProductsOnShoppingLists());
            else productsOnShoppingListLayout.setVisibility(View.GONE);

            eatenProductsLayout.setVisibility(View.GONE);
            notEatenProductsLayout.setVisibility(View.GONE);
        } else {
            if (shelf.getEatenProducts().size() > 0) {
                eatenProductsAdapter.setProducts(shelf.getEatenProducts());
                eatenProductsLayout.setVisibility(View.VISIBLE);
            }

            if (shelf.getNotEatenProducts().size() > 0) {
                notEatenProductsAdapter.setProducts(shelf.getNotEatenProducts());
                notEatenProductsLayout.setVisibility(View.VISIBLE);

            }
            boughtProductsLayout.setVisibility(View.GONE);
            productsToBuyLayout.setVisibility(View.GONE);
            productsOnShoppingListLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
