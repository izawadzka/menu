package com.example.dell.menu.screens.menus.addOrEditMenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dell.menu.App;
import com.example.dell.menu.R;
import com.example.dell.menu.objects.DailyMenu;
import com.example.dell.menu.screens.menus.MenusFragment;
import com.example.dell.menu.screens.menus.addOrEditMenu.createNewDailyMenu.CreateNewDailyMenuActivity;
import com.squareup.otto.Bus;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddOrEditMenuActivity extends AppCompatActivity implements DailyMenusAdapter.DailyMenuClickedListener {

    public static final int REQUEST_CODE_ADD = 1;
    public static final int RESULT_CODE_ADDED = 0;
    public static final int RESULT_CODE_CANCEL = -1;
    public static final int RESULT_CODE_ERROR = 5;
    public static final int REQUEST_CODE_SHOW = 2;

    @Bind(R.id.dailyMenusRecyclerView)
    RecyclerView dailyMenusRecyclerView;
    @Bind(R.id.saveMenuButton)
    Button saveMenuButton;
    @Bind(R.id.cancelAddingMenuButton)
    Button cancelAddingMenuButto;
    @Bind(R.id.newMenuNameTextView)
    EditText newMenuNameEditTextView;

    private Bus bus;
    private DailyMenusAdapter adapter;
    private AddOrEditMenuManager addOrEditMenuManager;
    private int menuId;
    private boolean show_mode;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        show_mode = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_edit_menu);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewDailyMenu();
            }
        });

        bus = ((App) getApplication()).getBus();

        addOrEditMenuManager = ((App) getApplication()).getAddOrEditMenuManager();

        adapter = new DailyMenusAdapter(bus);
        dailyMenusRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setDailyMenuClickedListener(this);
        dailyMenusRecyclerView.setAdapter(adapter);


        Intent intent = getIntent();
        if(intent.getBooleanExtra(MenusFragment.SHOW_MODE_KEY, false)){
            menuId = intent.getIntExtra(MenusFragment.MENU_ID_KEY, 0);
            show_mode = true;
            setShowMode();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        addOrEditMenuManager.onAttach(this);
        if(show_mode && !addOrEditMenuManager.isShow_mode()){
            addOrEditMenuManager.setShow_mode(true);
            addOrEditMenuManager.setMenuId(menuId);
            addOrEditMenuManager.loadMenuName();
        }else if(show_mode){
        }
        else {
            getState();
            setDailyMenus();
        }
    }

    private void setShowMode() {
        saveMenuButton.setVisibility(View.INVISIBLE);
        fab.setVisibility(View.GONE);
        newMenuNameEditTextView.setEnabled(true);
        setTitle("Show menu");
    }

    public void setDailyMenus() {
        adapter.setDailyMenus(addOrEditMenuManager.getListOfDailyMenus());
    }

    @Override
    protected void onStop() {
        super.onStop();
        setState();
        addOrEditMenuManager.onStop();
    }

    private void setState() {
        if(!newMenuNameEditTextView.getText().toString().equals("")){
            addOrEditMenuManager.setMenusName(newMenuNameEditTextView.getText().toString());
        }
    }

    private void addNewDailyMenu() {
        startActivityForResult(new Intent(this, CreateNewDailyMenuActivity.class), REQUEST_CODE_ADD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ADD && resultCode == RESULT_CODE_ADDED) {
            Toast.makeText(this, "New daily menu created!", Toast.LENGTH_SHORT).show();
        } else if (requestCode == REQUEST_CODE_ADD && resultCode == RESULT_CODE_CANCEL) {
            Toast.makeText(this, "Cancel adding new daily menu", Toast.LENGTH_SHORT).show();
        } else if (requestCode == REQUEST_CODE_ADD && requestCode == RESULT_CODE_ERROR) {
            Toast.makeText(this, "Error while trying to add new daily meny", Toast.LENGTH_SHORT).show();
        }
    }

    public void dailyMenuDeleted(int position) {
        adapter.deleteDailyMenu(position);
    }

    @Override
    public void dailyMenuClicked(DailyMenu dailyMenu) {
        startActivity(new Intent(this, CreateNewDailyMenuActivity.class));
        addOrEditMenuManager.showDailyMenu(dailyMenu);
    }

    @OnClick({R.id.saveMenuButton, R.id.cancelAddingMenuButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.saveMenuButton:
                saveMenu();
                break;
            case R.id.cancelAddingMenuButton:
                cancel();
                break;
        }
    }

    private void cancel() {
        addOrEditMenuManager.clearListOfDailyMenu();
        newMenuNameEditTextView.setText("");
        addOrEditMenuManager.setMenusName("");
        addOrEditMenuManager.setShow_mode(false);
        finish();
    }

    private void saveMenu() {
        boolean hasErrors = false;
        if (adapter.getItemCount() == 0) {
            Toast.makeText(this, "Menu should contains at least one daily menu!", Toast.LENGTH_SHORT).show();
            hasErrors = true;
        }
        if (newMenuNameEditTextView.length() == 0){
            Toast.makeText(this, "Menu should have name!", Toast.LENGTH_SHORT).show();
            hasErrors = true;
        }

        if(!hasErrors){
            setState();
            addOrEditMenuManager.saveMenu();
        }
    }

    public void getState() {
        if(addOrEditMenuManager.getMenusName() != null){
            newMenuNameEditTextView.setText(addOrEditMenuManager.getMenusName());
            show_mode = addOrEditMenuManager.isShow_mode();
        }
    }

    public void saveMenuFailed() {
        Toast.makeText(this, "Error while trying to save menu", Toast.LENGTH_SHORT).show();
        newMenuNameEditTextView.setText("");
        addOrEditMenuManager.setMenusName("");
        addOrEditMenuManager.clearListOfDailyMenu();
        addOrEditMenuManager.setShow_mode(false);
        finish();
    }

    public void saveSuccess() {
        Toast.makeText(this, "New menu added", Toast.LENGTH_SHORT).show();
        newMenuNameEditTextView.setText("");
        addOrEditMenuManager.setMenusName("");
        addOrEditMenuManager.clearListOfDailyMenu();
        addOrEditMenuManager.setShow_mode(false);
        finish();
    }


    public void loadingMenuNameFailed() {
        Toast.makeText(this, "Error while trying to load menu name", Toast.LENGTH_SHORT).show();
    }

    public void showStatement(String statement) {
        Toast.makeText(this, statement, Toast.LENGTH_SHORT).show();
    }
}
