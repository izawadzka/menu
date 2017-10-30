package com.example.dell.menu;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.dell.menu.backup.Backup;
import com.example.dell.menu.backup.BackupService;
import com.example.dell.menu.events.shoppinglists.ShowShoppingListEvent;
import com.example.dell.menu.screens.login.LoginActivity;
import com.example.dell.menu.screens.menuplanning.meals.MealsFragment;
import com.example.dell.menu.screens.menuplanning.menus.MenusFragment;
import com.example.dell.menu.screens.menuplanning.products.ProductsFragment;
import com.example.dell.menu.screens.reports.ReportsFragment;
import com.example.dell.menu.screens.shoppingLists.ShoppingListsFragment;
import com.example.dell.menu.screens.virtualfridge.VirtualFridgeFragment;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private UserStorage userStorage;
    private DrawerLayout drawer;
    private Bus bus;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        userStorage = ((App)getApplication()).getUserStorage();

        startService(new Intent(this, BackupService.class));

        if(userStorage.hasToLogin()){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_menu);
        onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_menu));

        bus = ((App)getApplication()).getBus();

        ImageView backgroundImageView = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.backgroundImageView);
        String backgroundImageName = "food.jpg";
        Glide.with(backgroundImageView.getContext())
                .load("file:///android_asset/" + backgroundImageName)
                .into(backgroundImageView);

    }

    @Override
    protected void onStart() {
        super.onStart();
        bus.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        bus.unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("MainActivity", "destroy");

        //Backup backup = new Backup(this, (App)getApplication());
        //backup.doBackup();
    }






    @Subscribe
    public void onShowShoppingList(ShowShoppingListEvent event){
        showFragment(new ShoppingListsFragment());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_logout) {
            logout();
        }else if(id == R.id.action_restore_backup){
            restoreBackup();
        }

        return super.onOptionsItemSelected(item);
    }

    private void restoreBackup() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Restore backup");

        alertDialogBuilder.setMessage("The last backup version will be restored.\n" +
                "The last backup was done either when the app was closed the last time or, " +
                "if there was no Internet connection, just right when the connection was available " +
                "after app was opened again.\n\n" +
                "Are you sure you want to restore backup?\n\n" +
                "*Internet connection is needed")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int d){
                        Backup backup = new Backup(MainActivity.this, (App)getApplication());
                        backup.restoreBackup();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int d){
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    private void logout() {
        userStorage.logout();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        drawer.closeDrawer(GravityCompat.START);
        if (id == R.id.nav_products) {
            showFragment(new ProductsFragment());
        } else if (id == R.id.nav_meal) {
            showFragment(new MealsFragment());
        } else if (id == R.id.nav_menu) {
            showFragment(new MenusFragment());
        } else if (id == R.id.nav_shoppingList) {
            showFragment(new ShoppingListsFragment());
        } else if (id == R.id.nav_reports) {
            showFragment(new ReportsFragment());
        } else if(id == R.id.nav_manage_fridge){
            showFragment(new VirtualFridgeFragment());
        }


        return true;
    }

    public void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment)
                .commit();
    }

}
