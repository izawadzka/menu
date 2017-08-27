package com.example.dell.menu.screens.menus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dell.menu.App;
import com.example.dell.menu.R;
import com.example.dell.menu.objects.Menu;
import com.example.dell.menu.screens.menus.addOrEditMenu.DailyMenusActivity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Dell on 04.06.2017.
 */

public class MenusFragment extends Fragment implements MenusAdapter.MenuClickedListener {
    public static final String MENU_ID_KEY = "menuId";
    @Bind(R.id.menusRecyclerView)
    RecyclerView menusRecyclerView;

    private MenusAdapter adapter;
    private MenusManager menusManager;
    private LayoutInflater layoutInflater;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menusManager = ((App)getActivity().getApplication()).getMenusManager();
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.layoutInflater = inflater;
        View view = this.layoutInflater.inflate(R.layout.fragment_menus, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        menusManager.onAttach(this);
        menusManager.loadMenus();
    }

    @Override
    public void onStop() {
        super.onStop();
        menusManager.onStop();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        menusRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MenusAdapter(((App)getActivity().getApplication()).getBus());
        adapter.setMenuClickedListener(this);
        menusRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(android.view.Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menus, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_add_menu){
            addNewMenu();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addNewMenu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = layoutInflater.inflate(R.layout.create_new_menu_dialog_layout, null);
        final EditText menuNameEditText = (EditText) view.findViewById(R.id.addMenuNameEditText);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();
        Button addNewMenu = (Button) view.findViewById(R.id.addMenuButton);
        addNewMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String menuName = menuNameEditText.getText().toString();
                if(menuName.length() > 0){
                    dialog.dismiss();
                    menusManager.addNewMenu(menuName);
                }else menuNameEditText.setError("Menu must have a name!");
            }
        });

        Button cancelButton = (Button) view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void showMenus(List<Menu> result){
        adapter.setMenus(result);
    }

    @Override
    public void menuClicked(Menu menu) {
        Intent intent = new Intent(getContext(), DailyMenusActivity.class);
        intent.putExtra(MENU_ID_KEY, (long)menu.getMenuId());
        startActivity(intent);
    }

    public void addNewMenuSuccess(Long menuId) {
        Toast.makeText(getContext(), "Successfully added new menu!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getContext(), DailyMenusActivity.class);
        intent.putExtra(MENU_ID_KEY, menuId);
        startActivity(intent);
    }

    public void addNewMenuFailed() {
        Toast.makeText(getContext(), "An error occurred while trying to create new menu!", Toast.LENGTH_SHORT).show();
    }
}
