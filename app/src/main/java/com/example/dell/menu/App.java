package com.example.dell.menu;

import android.app.Application;

import com.example.dell.menu.backup.BackupFlagStorage;
import com.example.dell.menu.screens.login.LoginManager;
import com.example.dell.menu.screens.menuplanning.meals.addOrEdit.AddOrEditMealManager;
import com.example.dell.menu.screens.menuplanning.meals.addOrEdit.ChooseFromProductsManager;
import com.example.dell.menu.screens.menuplanning.meals.extendedMealInformation.FullMealInformationActivityManager;
import com.example.dell.menu.screens.menuplanning.meals.MealsFragmentManager;
import com.example.dell.menu.screens.menuplanning.menus.MenusManager;
import com.example.dell.menu.screens.menuplanning.menus.addOrEditMenu.DailyMenusManager;
import com.example.dell.menu.screens.menuplanning.menus.addOrEditMenu.dailyMenu.chooseMeals.ChooseFromMealsManager;
import com.example.dell.menu.screens.menuplanning.menus.addOrEditMenu.dailyMenu.CreateNewDailyMenuManager;
import com.example.dell.menu.screens.menuplanning.products.ProductFragmentManager;
import com.example.dell.menu.screens.menuplanning.products.addOrEdit.AddOrEditProductManager;
import com.example.dell.menu.screens.register.RegisterManager;
import com.example.dell.menu.screens.reports.ReportsManager;
import com.example.dell.menu.screens.shoppingLists.ShoppingListsManager;
import com.example.dell.menu.screens.shoppingLists.ShowProductsInListManager;
import com.example.dell.menu.screens.virtualfridge.AddProductToFridgeActivity;
import com.example.dell.menu.screens.virtualfridge.AddProductToFridgeManager;
import com.example.dell.menu.screens.virtualfridge.VirtualFridgeManager;
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
    private BackupFlagStorage backupFlagStorage;
    private Bus bus;
    private AddOrEditProductManager addOrEditProductManager;
    private ChooseFromProductsManager chooseFromProductsManager;
    private AddOrEditMealManager addOrEditMealManager;
    private MenusManager menusManager;
    private ChooseFromMealsManager chooseFromMealsManager;
    private CreateNewDailyMenuManager createNewDailyMenuManager;
    private ShoppingListsManager shoppingListsManager;
    private ShowProductsInListManager showProductsInListManager;
    private ReportsManager reportsManager;
    private DailyMenusManager dailyMenusManager;
    private VirtualFridgeManager virtualFridgeManager;
    private boolean backupFlag = false;
    private AddProductToFridgeManager addProductToFridgeManager;

    public boolean isBackupFlag() {
        return backupFlag;
    }

    public void setBackupFlag(boolean backupFlag) {
        this.backupFlag = backupFlag;
    }

    public DailyMenusManager getDailyMenusManager() {
        return dailyMenusManager;
    }

    public VirtualFridgeManager getVirtualFridgeManager() {
        return virtualFridgeManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        bus = new Bus();
        loginManager = new LoginManager();
        registerManager = new RegisterManager();
        userStorage =  new UserStorage(getSharedPreferences("userStorage", MODE_PRIVATE));
        backupFlagStorage = new BackupFlagStorage(getSharedPreferences("backupFlagStorage", MODE_PRIVATE));
        backupFlag = backupFlagStorage.checkFlag();
        productFragmentManager = new ProductFragmentManager(bus);
        mealsFragmentManager = new MealsFragmentManager(bus);
        menusManager = new MenusManager(bus, userStorage);
        chooseFromMealsManager = new ChooseFromMealsManager(bus);
        createNewDailyMenuManager = new CreateNewDailyMenuManager(bus);
        fullMealInformationActivityManager = new FullMealInformationActivityManager();
        addOrEditProductManager = new AddOrEditProductManager();
        shoppingListsManager = new ShoppingListsManager(bus);
        chooseFromProductsManager = new ChooseFromProductsManager(bus);
        showProductsInListManager = new ShowProductsInListManager(bus);
        addOrEditMealManager = new AddOrEditMealManager(bus);
        dailyMenusManager = new DailyMenusManager(bus);
        reportsManager = new ReportsManager();
        virtualFridgeManager = new VirtualFridgeManager(bus);
        addProductToFridgeManager = new AddProductToFridgeManager(bus);
    }

    public AddProductToFridgeManager getAddProductToFridgeManager() {
        return addProductToFridgeManager;
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

    public UserStorage getUserStorage() {
        return userStorage;
    }

    public BackupFlagStorage getBackupFlagStorage() {return backupFlagStorage;}

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
