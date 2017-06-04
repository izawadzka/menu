package com.example.dell.menu.screens.menus;

import com.example.dell.menu.objects.Menu;
import com.squareup.otto.Bus;

/**
 * Created by Dell on 04.06.2017.
 */

public class MenusManager {
    private final Bus bus;
    private MenusFragment menusFragment;

    public MenusManager(Bus bus) {
        this.bus = bus;
    }

    public void onAttach(MenusFragment menusFragment){
        this.menusFragment = menusFragment;
    }

    public void onStop(){
        this.menusFragment = null;
    }
}
