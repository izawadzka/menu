package com.example.dell.menu;

import android.app.Application;

import com.example.dell.menu.data.backup.BackupFlagStorage;
import com.example.dell.menu.data.backup.screens.backupondemand.BackupOnDemandManager;
import com.example.dell.menu.data.backup.screens.restore.RestoreBackupManager;
import com.example.dell.menu.user.screens.login.LoginManager;
import com.example.dell.menu.menuplanning.screens.meals.addOrEdit.AddOrEditMealManager;
import com.example.dell.menu.menuplanning.screens.meals.addOrEdit.ChooseFromProductsManager;
import com.example.dell.menu.menuplanning.screens.meals.MealsFragmentManager;
import com.example.dell.menu.menuplanning.screens.menus.MenusManager;
import com.example.dell.menu.menuplanning.screens.menus.addOrEditMenu.DailyMenusManager;
import com.example.dell.menu.menuplanning.screens.menus.addOrEditMenu.dailyMenu.chooseMeals.ChooseFromMealsManager;
import com.example.dell.menu.menuplanning.screens.menus.addOrEditMenu.dailyMenu.CreateNewDailyMenuManager;
import com.example.dell.menu.menuplanning.screens.products.ProductFragmentManager;
import com.example.dell.menu.menuplanning.screens.products.addOrEdit.AddOrEditProductManager;
import com.example.dell.menu.user.screens.register.RegisterManager;
import com.example.dell.menu.reports.screens.ReportsManager;
import com.example.dell.menu.shoppinglist.screens.ShoppingListsManager;
import com.example.dell.menu.shoppinglist.screens.ShowProductsInListManager;
import com.example.dell.menu.virtualfridge.screens.AddProductManager;
import com.example.dell.menu.virtualfridge.screens.VirtualFridgeManager;
import com.example.dell.menu.user.UserStorage;
import com.squareup.otto.Bus;

/**
 * Created by Dell on 25.05.2017.
 */

public class App extends Application {
    private LoginManager loginManager;
    private RegisterManager registerManager;
    private ProductFragmentManager productFragmentManager;
    private MealsFragmentManager mealsFragmentManager;
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
    private AddProductManager addProductManager;
    private RestoreBackupManager restoreBackupManager;
    private BackupOnDemandManager backupOnDemandManager;


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
        loginManager = new LoginManager(bus);
        registerManager = new RegisterManager(bus);
        userStorage =  new UserStorage(getSharedPreferences("userStorage", MODE_PRIVATE));
        backupFlagStorage = new BackupFlagStorage(getSharedPreferences("backupFlagStorage", MODE_PRIVATE));
        productFragmentManager = new ProductFragmentManager(bus);
        mealsFragmentManager = new MealsFragmentManager(bus);
        menusManager = new MenusManager(bus, userStorage);
        chooseFromMealsManager = new ChooseFromMealsManager(bus);
        createNewDailyMenuManager = new CreateNewDailyMenuManager(bus);
        addOrEditProductManager = new AddOrEditProductManager();
        shoppingListsManager = new ShoppingListsManager(bus);
        chooseFromProductsManager = new ChooseFromProductsManager(bus);
        showProductsInListManager = new ShowProductsInListManager(bus);
        addOrEditMealManager = new AddOrEditMealManager(bus);
        dailyMenusManager = new DailyMenusManager(bus);
        reportsManager = new ReportsManager();
        virtualFridgeManager = new VirtualFridgeManager(bus);
        addProductManager = new AddProductManager(bus);
        restoreBackupManager = new RestoreBackupManager(bus);
        backupOnDemandManager = new BackupOnDemandManager(bus);
    }

    public BackupOnDemandManager getBackupOnDemandManager() {
        return backupOnDemandManager;
    }

    public RestoreBackupManager getRestoreBackupManager() {
        return restoreBackupManager;
    }

    public AddProductManager getAddProductManager() {
        return addProductManager;
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
