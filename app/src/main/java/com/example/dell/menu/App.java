package com.example.dell.menu;

import android.app.Application;
import android.preference.PreferenceManager;

import com.example.dell.menu.screens.login.LoginManager;
import com.example.dell.menu.screens.meals.addOrEdit.AddOrEditMealManager;
import com.example.dell.menu.screens.meals.addOrEdit.ChooseFromProductsManager;
import com.example.dell.menu.screens.meals.extendedMealInformation.FullMealInformationActivityManager;
import com.example.dell.menu.screens.meals.MealsFragmentManager;
import com.example.dell.menu.screens.menus.MenusManager;
import com.example.dell.menu.screens.menus.addOrEditMenu.AddOrEditMenuManager;
import com.example.dell.menu.screens.menus.addOrEditMenu.createNewDailyMenu.ChooseFromMealsManager;
import com.example.dell.menu.screens.menus.addOrEditMenu.createNewDailyMenu.CreateNewDailyMenuManager;
import com.example.dell.menu.screens.products.ProductFragmentManager;
import com.example.dell.menu.screens.products.addOrEdit.AddOrEditProductManager;
import com.example.dell.menu.screens.register.RegisterManager;
import com.example.dell.menu.screens.reports.ReportsManager;
import com.example.dell.menu.screens.shoppingLists.ShoppingListsManager;
import com.example.dell.menu.screens.shoppingLists.ShowProductsInListManager;
import com.squareup.otto.Bus;

/**
 * Created by Dell on 25.05.2017.
 */

public class App extends Application {
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
    private MenusManager menusManager;
    private ChooseFromMealsManager chooseFromMealsManager;
    private CreateNewDailyMenuManager createNewDailyMenuManager;
    private AddOrEditMenuManager addOrEditMenuManager;
    private ShoppingListsManager shoppingListsManager;
    private ShowProductsInListManager showProductsInListManager;
    private ReportsManager reportsManager;

    @Override
    public void onCreate() {
        super.onCreate();
        bus = new Bus();
        loginManager = new LoginManager();
        registerManager = new RegisterManager();
        productFragmentManager = new ProductFragmentManager(bus);
        mealsFragmentManager = new MealsFragmentManager(bus);
        menusManager = new MenusManager(bus);
        chooseFromMealsManager = new ChooseFromMealsManager(bus);
        createNewDailyMenuManager = new CreateNewDailyMenuManager(bus);
        fullMealInformationActivityManager = new FullMealInformationActivityManager();
        addOrEditProductManager = new AddOrEditProductManager();
        addOrEditMenuManager = new AddOrEditMenuManager(bus);
        shoppingListsManager = new ShoppingListsManager(bus);
        chooseFromProductsManager = new ChooseFromProductsManager();
        showProductsInListManager = new ShowProductsInListManager();
        addOrEditMealManager = new AddOrEditMealManager(bus);
        reportsManager = new ReportsManager();
        userStorage =  new UserStorage(PreferenceManager.getDefaultSharedPreferences(this));
    }

    public ReportsManager getReportsManager() {
        return reportsManager;
    }

    public ShowProductsInListManager getShowProductsInListManager() {
        return showProductsInListManager;
    }

    public CreateNewDailyMenuManager getCreateNewDailyMenuManager() {
        return createNewDailyMenuManager;
    }

    public ShoppingListsManager getShoppingListsManager() {
        return shoppingListsManager;
    }

    public AddOrEditMenuManager getAddOrEditMenuManager() {
        return addOrEditMenuManager;
    }

    public MenusManager getMenusManager() {
        return menusManager;
    }

    public ChooseFromMealsManager getChooseFromMealsManager() {
        return chooseFromMealsManager;
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
