package com.example.dell.menu;

import android.app.Application;
import android.preference.PreferenceManager;

import com.example.dell.menu.screens.login.LoginManager;
import com.example.dell.menu.screens.meals.addOrEdit.AddOrEditMealManager;
import com.example.dell.menu.screens.meals.addOrEdit.ChooseFromProductsManager;
import com.example.dell.menu.screens.meals.extendedMealInformation.FullMealInformationActivityManager;
import com.example.dell.menu.screens.meals.MealsFragmentManager;
import com.example.dell.menu.screens.products.ProductFragmentManager;
import com.example.dell.menu.screens.products.addOrEdit.AddOrEditProductManager;
import com.example.dell.menu.screens.register.RegisterManager;
import com.squareup.otto.Bus;

/**
 * Created by Dell on 25.05.2017.
 */

public class App extends Application {
    //private MenuDataBase menuDataBase;
    private LoginManager loginManager;
    private RegisterManager registerManager;
    private ProductFragmentManager productFragmentManager;
    private MealsFragmentManager mealsFragmentManager;
    private FullMealInformationActivityManager fullMealInformationActivityManager;
    private UserStorage userStorage;
    private Bus bus;
    private AddOrEditProductManager addOrEditProductManager;
    private ChooseFromProductsManager chooseFromProductsManager;
    private AddOrEditMealManager addOrEditMealManager;

    @Override
    public void onCreate() {
        super.onCreate();
        //menuDataBase = new MenuDataBase(this);
        /*loginManager = new LoginManager(menuDataBase);
        registerManager = new RegisterManager(menuDataBase);
        productFragmentManager = new ProductFragmentManager(menuDataBase);
        mealsFragmentManager = new MealsFragmentManager(menuDataBase);
        fullMealInformationActivityManager = new FullMealInformationActivityManager(menuDataBase);*/
        bus = new Bus();
        loginManager = new LoginManager();
        registerManager = new RegisterManager();
        productFragmentManager = new ProductFragmentManager(bus);
        mealsFragmentManager = new MealsFragmentManager(bus);
        fullMealInformationActivityManager = new FullMealInformationActivityManager();
        addOrEditProductManager = new AddOrEditProductManager();
        chooseFromProductsManager = new ChooseFromProductsManager();
        addOrEditMealManager = new AddOrEditMealManager(bus);
        userStorage =  new UserStorage(PreferenceManager.getDefaultSharedPreferences(this));
    }

    public AddOrEditMealManager getAddOrEditMealManager() {
        return addOrEditMealManager;
    }

    public ChooseFromProductsManager getChooseFromProductsManager() {
        return chooseFromProductsManager;
    }

    public FullMealInformationActivityManager getFullMealInformationActivityManager() {
        return fullMealInformationActivityManager;
    }

    public MealsFragmentManager getMealsFragmentManager() {
        return mealsFragmentManager;
    }

    public AddOrEditProductManager getAddOrEditProductManager() {
        return addOrEditProductManager;
    }

    public Bus getBus() {
        return bus;
    }

    //public MenuDataBase getMenuDataBase() {
     //   return menuDataBase;
    //}

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
