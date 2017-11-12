package com.example.dell.menu.virtualfridge.screens;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.dell.menu.virtualfridge.objects.ProductsShelf;
import com.example.dell.menu.virtualfridge.screens.shelf.ProductShelfFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 29.10.2017.
 */

public class ProductShelvesPagerAdapter extends FragmentStatePagerAdapter {
    List<ProductsShelf> productsShelves = new ArrayList<>();

    public ProductShelvesPagerAdapter(FragmentManager fm, List<ProductsShelf> productsShelves) {
        super(fm);
        this.productsShelves.clear();
        this.productsShelves.addAll(productsShelves);
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return ProductShelfFragment.newInstance(productsShelves.get(position));
    }

    @Override
    public int getCount() {
        return productsShelves.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return productsShelves.get(position).getTypeName();
    }
}
