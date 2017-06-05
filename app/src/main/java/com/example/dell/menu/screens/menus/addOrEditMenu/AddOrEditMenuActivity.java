package com.example.dell.menu.screens.menus.addOrEditMenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.example.dell.menu.App;
import com.example.dell.menu.R;
import com.example.dell.menu.screens.menus.addOrEditMenu.createNewDailyMenu.CreateNewDailyMenuActivity;
import com.squareup.otto.Bus;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddOrEditMenuActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_ADD = 1;
    public static final int RESULT_CODE_ADDED = 0;
    public static final int RESULT_CODE_CANCEL = -1;
    public static final int RESULT_CODE_ERROR = 5;
    @Bind(R.id.dailyMenusRecyclerView)
    RecyclerView dailyMenusRecyclerView;
    private Bus bus;
    private DailyMenusAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_edit_menu);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewDayMenu();
               /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

        bus = ((App) getApplication()).getBus();

        adapter = new DailyMenusAdapter(bus);
        dailyMenusRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dailyMenusRecyclerView.setAdapter(adapter);
    }


    private void addNewDayMenu() {
        startActivityForResult(new Intent(this, CreateNewDailyMenuActivity.class), REQUEST_CODE_ADD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_ADD && resultCode == RESULT_CODE_ADDED){
            Toast.makeText(this, "New daily menu created!", Toast.LENGTH_SHORT).show();
        }else  if(requestCode == REQUEST_CODE_ADD && resultCode == RESULT_CODE_CANCEL){
            Toast.makeText(this, "Cancel adding new daily menu", Toast.LENGTH_SHORT).show();
        }
    }
}
