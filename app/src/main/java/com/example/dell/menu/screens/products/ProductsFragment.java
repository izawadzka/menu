package com.example.dell.menu.screens.products;

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
import com.example.dell.menu.objects.Product;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Dell on 26.05.2017.
 */

public class ProductsFragment extends Fragment {
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
        adapter = new ProductsAdapter();
        productRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        productFragmentManager = ((App)getActivity().getApplication()).getProductFragmentManager();
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
}
