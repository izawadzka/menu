package com.example.dell.menu.screens.menuplanning.menus.addOrEditMenu;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.dell.menu.objects.menuplanning.DailyMenu;
import com.example.dell.menu.screens.menuplanning.menus.addOrEditMenu.dailyMenu.DailyMenuFragment;

import java.util.List;

/**
 * Created by Dell on 03.08.2017.
 */

public class DailyMenusPagerAdapter extends FragmentStatePagerAdapter{
    private final List<DailyMenu> dailyMenus;

    public DailyMenusPagerAdapter(FragmentManager fm, List<DailyMenu> dailyMenus) {
        super(fm);
        this.dailyMenus = dailyMenus;
    }


    @Override
    public Fragment getItem(int position) {
        return DailyMenuFragment.newInstance(dailyMenus.get(position));
    }

    @Override
    public int getCount() {
        return dailyMenus.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return new String("Day " + (position+1));
    }
}
