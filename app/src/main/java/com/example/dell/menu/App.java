package com.example.dell.menu;

import android.app.Application;
import android.preference.PreferenceManager;

import com.example.dell.menu.screens.login.LoginManager;
import com.example.dell.menu.screens.meals.MealsFragmentManager;
import com.example.dell.menu.screens.products.ProductFragmentManager;
import com.example.dell.menu.screens.register.RegisterManager;
import com.squareup.otto.Bus;

/**
 * Created by Dell on 25.05.2017.
 */

public class App extends Application {
    private MenuDataBase menuDataBase;
    private LoginManager loginManager;
    private RegisterManager registerManager;
    private ProductFragmentManager productFragmentManager;
    private MealsFragmentManager mealsFragmentManager;
    private UserStorage userStorage;
    private Bus bus;

    @Override
    public void onCreate() {
        super.onCreate();
        menuDataBase = new MenuDataBase(this);
        loginManager = new LoginManager(menuDataBase);
        registerManager = new RegisterManager(menuDataBase);
        productFragmentManager = new ProductFragmentManager(menuDataBase);
        mealsFragmentManager = new MealsFragmentManager(menuDataBase);
        userStorage =  new UserStorage(PreferenceManager.getDefaultSharedPreferences(this));
        bus = new Bus();
    }

    public MealsFragmentManager getMealsFragmentManager() {
        return mealsFragmentManager;
    }

    public Bus getBus() {
        return bus;
    }

    public MenuDataBase getMenuDataBase() {
        return menuDataBase;
    }

    public UserStorage getUserStorage() {
        return userStorage;
    }

    public LoginManager getLoginManager() {
        return loginManager;
    }

    public RegisterManager getRegisterManager() {
        return registerManager;
    }

    public ProductFragmentManager getProductFragmentManager() {
        return productFragmentManager;
    }
}
