package com.example.dell.menu.screens.virtualfridge;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.dell.menu.App;
import com.example.dell.menu.R;
import com.example.dell.menu.screens.menuplanning.meals.addOrEdit.AddedProductsAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Dell on 28.10.2017.
 */

public class VirtualFridgeFragment extends Fragment {
    public static final String ADD_TO_FRIDGE_KEY = "Add to fridge";
    @Bind(R.id.shelvesTabLayout)
    TabLayout shelvesTabLayout;
    @Bind(R.id.shelvesViewPager)
    ViewPager shelvesViewPager;
    private VirtualFridgeManager manager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        manager = ((App) getActivity().getApplication()).getVirtualFridgeManager();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_fridge, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle("Virtual Fridge");

        return view;
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
                // TODO: 29.10.2017
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
        Intent intent = new Intent(getActivity(), AddProductActivity.class);
        intent.putExtra(ADD_TO_FRIDGE_KEY, true);
        startActivity(intent);
    }


    @Override
    public void onStart() {
        super.onStart();
        manager.onAttach(this);
        manager.loadContent();
    }

    @Override
    public void onStop() {
        super.onStop();
        manager.onStop();
    }

    public void setAdapter() {
        ProductShelvesPagerAdapter adapter =
                new ProductShelvesPagerAdapter(getActivity().getSupportFragmentManager(),
                        manager.getProductShelves());
        shelvesViewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        shelvesTabLayout.setupWithViewPager(shelvesViewPager);
    }

    public void fridgeEmpty() {
        Toast.makeText(getContext(), "Your fridge is empty", Toast.LENGTH_LONG).show();
    }

    public void loadingContentFailed() {
        Toast.makeText(getContext(), "An error occurred while an attempt to load the content",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void updateSuccess() {
        Toast.makeText(getContext(), "Successfully updated quantity of product", Toast.LENGTH_SHORT).show();
        manager.loadContent();
    }

    public void updateFailed() {
        Toast.makeText(getContext(), "An error occurred while an attempt to update quantity of " +
                "product", Toast.LENGTH_LONG).show();
    }

    public void deleteSuccess() {
        Toast.makeText(getContext(), "Successfully deleted product", Toast.LENGTH_SHORT).show();
        manager.loadContent();
    }

    public void deleteFailed() {
        Toast.makeText(getContext(), "An error occurred while an attempt to delete product",
                Toast.LENGTH_LONG).show();
    }
}
