package com.example.dell.menu.menuplanning.screens.menus.addOrEditMenu;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.dell.menu.App;
import com.example.dell.menu.R;
import com.example.dell.menu.data.backup.BackupTimer;
import com.example.dell.menu.menuplanning.objects.DailyMenu;
import com.example.dell.menu.menuplanning.screens.menus.addOrEditMenu.dailyMenu.CreateNewDailyMenuActivity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DailyMenusActivity extends AppCompatActivity {

    public static final String MENU_ID_KEY = "menuId";
    public static final int REQUEST_CODE_ADD = 1;
    private boolean blockOtherActions;
    @Bind(R.id.dailyMenusViewPager)
    ViewPager dailyMenusViewPager;
    @Bind(R.id.dailyMenusTabLayout)
    TabLayout dailyMenusTabLayout;

    private DailyMenusManager dailyMenusManager;
    private long menuId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_menus);
        ButterKnife.bind(this);

        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDefaultDisplayHomeAsUpEnabled(true);

        blockOtherActions = false;

        dailyMenusManager = ((App) getApplication()).getDailyMenusManager();
        //download id of current menu
        Intent intent = getIntent();
        menuId = intent.getLongExtra(MENU_ID_KEY, (long)-1);
        if(menuId == -1){
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        dailyMenusManager.onAttach(this);
        dailyMenusManager.setMenuId(menuId);

        if(dailyMenusManager.isWaitingToAddNewDailyMenu()) {
            dailyMenusManager.addNewDailyMenu();
            blockOtherActions = true;
        }
        else if(dailyMenusManager.isWaitingToEditDailyMenu()){
            dailyMenusManager.updateDailyMenu();
            blockOtherActions = true;
        }

        else dailyMenusManager.loadDailyMenus();
    }


    public void setAdapter() {
        List<DailyMenu> dailyMenuList = dailyMenusManager.getDailyMenus();
        DailyMenusPagerAdapter adapter = new DailyMenusPagerAdapter(getSupportFragmentManager(), dailyMenuList);
        dailyMenusViewPager.setAdapter(adapter);
        dailyMenusTabLayout.setupWithViewPager(dailyMenusViewPager);
    }


    @Override
    protected void onStop() {
        super.onStop();
        dailyMenusManager.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.daily_menus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(!blockOtherActions) {
            if (item.getItemId() == android.R.id.home) {
                finish();
                return true;
            } else if (item.getItemId() == R.id.action_add_menu) {
                Intent intent = new Intent(this, CreateNewDailyMenuActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD);
            }else if(item.getItemId() == R.id.action_mark_daily_menu_as_used){
                dailyMenusManager.getDailyMenus()
                        .get(dailyMenusViewPager.getCurrentItem()).setAlreadyUsed(true);
                dailyMenusManager.setDailyMenuWasUsed(dailyMenusViewPager.getCurrentItem(), true);
            }else if(item.getItemId() == R.id.action_unmark_daily_menu){
                dailyMenusManager.getDailyMenus().get(dailyMenusViewPager.getCurrentItem())
                        .setAlreadyUsed(false);
                dailyMenusManager.setDailyMenuWasUsed(dailyMenusViewPager.getCurrentItem(), false);
            }else if(item.getItemId() == R.id.action_synchronize_marked_daily_menus_with_virtual_fridge){
                synchronizeMarkedDailyMenusWithVirtualFridge();
            }else if(item.getItemId() == R.id.action_mark_all_daily_menus_as_used){
                for (int i = 0; i < dailyMenusManager.getDailyMenus().size(); i++) {
                    dailyMenusManager.getDailyMenus().get(i).setAlreadyUsed(true);
                    dailyMenusManager.setDailyMenuWasUsed(i, true);
                }

            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void synchronizeMarkedDailyMenusWithVirtualFridge() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Synchronize with virtual fridge");

        alertDialogBuilder.setMessage("If you synchronize marked daily menus with the fridge, the " +
                "amount of products from the fridge that are included in those daily menus will be " +
                "reduced. \n Are you sure you want to do it?")
                .setCancelable(true)
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dailyMenusManager.synchronizeMarkedDailyMenusWithVirtualFridge();
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    public void makeAStatement(String statement, int duration) {
        Toast.makeText(this, statement, duration).show();
    }

    public void downloadingDailyMenusFailed() {
        makeAStatement("An error occurred while an attempt to download daily menus", Toast.LENGTH_LONG);
        blockOtherActions = false;
        NavUtils.navigateUpFromSameTask(this);
    }

    public void dailyMenuDeleteSuccess() {
        makeAStatement("Daily menu was deleted successfully", Toast.LENGTH_SHORT);
        blockOtherActions = false;
        dailyMenusManager.loadDailyMenus();
        BackupTimer backupTimer = new BackupTimer((App)getApplication());
        backupTimer.start();
    }

    public void dailyMenuDeleteFailed() {
        makeAStatement("An error occurred while trying to delete daily menu", Toast.LENGTH_LONG);
    }

    public void deleteDailyMenu(DailyMenu dailyMenu) {
        blockOtherActions = true;
        dailyMenusManager.deleteDailyMenu(dailyMenu);
    }

    public void updateCumulativeAmountOfKcalSuccess() {
        blockOtherActions = false;
        makeAStatement("Successfully updated cumulative amount of kcal", Toast.LENGTH_LONG);
        BackupTimer backupTimer = new BackupTimer((App)getApplication());
        backupTimer.start();
    }

    public void updateCumulativeAmountOfKcalFailed() {
        makeAStatement("An error occurred while an attempt to update " +
                "menus cumulative number of kcal", Toast.LENGTH_LONG);
        blockOtherActions = false;
    }

    public void addingDailyMenuFailed(String message, int duration) {
        makeAStatement(message, duration);
        blockOtherActions = false;
    }
}
