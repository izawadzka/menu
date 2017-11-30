package com.example.dell.menu.virtualfridge.screens;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.dell.menu.virtualfridge.objects.ShelfInVirtualFridge;
import com.example.dell.menu.virtualfridge.screens.shelf.ShelfFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 29.10.2017.
 */

public class ShelvesPagerAdapter extends FragmentStatePagerAdapter {
    List<ShelfInVirtualFridge> shelves = new ArrayList<>();

    public ShelvesPagerAdapter(FragmentManager fm, List<ShelfInVirtualFridge> shelves) {
        super(fm);
        this.shelves.clear();
        this.shelves.addAll(shelves);
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return ShelfFragment.newInstance(shelves.get(position));
    }

    @Override
    public int getCount() {
        return shelves.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return shelves.get(position).getLabel();
    }
}
