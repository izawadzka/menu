package com.example.dell.menu.screens.menus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.dell.menu.App;
import com.example.dell.menu.R;
import com.example.dell.menu.objects.Menu;
import com.example.dell.menu.screens.meals.addOrEdit.AddOrEditMealActivity;
import com.example.dell.menu.screens.menus.addOrEditMenu.AddOrEditMenuActivity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Dell on 04.06.2017.
 */

public class MenusFragment extends Fragment implements MenusAdapter.MenuClickedListener {
    public static final int REQUEST_CODE_ADD = 1;
    public static final String SHOW_MODE_KEY = "show_mode";
    public static final String MENU_ID_KEY = "menuId";
    public static final int REQUEST_CODE_SHOW = 2;
    @Bind(R.id.menusRecyclerView)
    RecyclerView menusRecyclerView;

    private MenusAdapter adapter;
    private MenusManager menusManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menusManager = ((App)getActivity().getApplication()).getMenusManager();
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menus, container, false);
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
        startActivityForResult(new Intent(getActivity(), AddOrEditMenuActivity.class), REQUEST_CODE_ADD);
    }

    public void showMenus(List<Menu> result){
        adapter.setMenus(result);
    }

    @Override
    public void menuClicked(Menu menu) {
        Intent intent = new Intent(getActivity(), AddOrEditMenuActivity.class);
        intent.putExtra(SHOW_MODE_KEY, true);
        intent.putExtra(MENU_ID_KEY, menu.getMenuId());
        startActivityForResult(intent, REQUEST_CODE_SHOW);
    }
}
