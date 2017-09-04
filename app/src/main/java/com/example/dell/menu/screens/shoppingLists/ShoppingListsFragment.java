package com.example.dell.menu.screens.shoppingLists;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.dell.menu.App;
import com.example.dell.menu.R;
import com.example.dell.menu.objects.ShoppingList;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShoppingListsFragment extends Fragment implements ShoppingListAdapter.ShoppingListClickedListener {


    public static final String SHOPPING_LIST_ID_KEY = "shoppingListId";
    @Bind(R.id.shoppingListRecyclerView)
    RecyclerView shoppingListRecyclerView;
    private ShoppingListsManager shoppingListsManager;
    private ShoppingListAdapter adapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shoppingListsManager = ((App) getActivity().getApplication()).getShoppingListsManager();
    }

    @Override
    public void onStart() {
        super.onStart();
        shoppingListsManager.onAttach(this);
        if (shoppingListsManager.isGenerateNewShoppingListEvent()) {
            shoppingListsManager.createShoppingList();
        }
        shoppingListsManager.loadShoppingLists();
    }

    @Override
    public void onStop() {
        super.onStop();
        shoppingListsManager.onStop();
    }

    public ShoppingListsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shopping_lists, container, false);
        ButterKnife.bind(this, view);

        shoppingListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ShoppingListAdapter(shoppingListsManager.getBus());
        adapter.setShoppingListClickedListener(this);
        shoppingListRecyclerView.setAdapter(adapter);

        return view;
    }

    public void showStatment(String statement) {
        Toast.makeText(getActivity(), statement, Toast.LENGTH_SHORT).show();
    }

    public void createShoppingListFailed() {
        Toast.makeText(getActivity(), "An error occurred while trying to create shopping list", Toast.LENGTH_SHORT).show();
    }

    public void generateShoppingListSuccess() {
        Toast.makeText(getActivity(), "Shopping list created successfully", Toast.LENGTH_SHORT).show();
        shoppingListsManager.loadShoppingLists();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void showShoppingLists(List<ShoppingList> result) {
        adapter.setShoppingLists(result);
    }

    @Override
    public void shoppingListClicked(ShoppingList shoppingList) {
        Intent intent = new Intent(getActivity(), ShowProductsInListActivity.class);
        intent.putExtra(SHOPPING_LIST_ID_KEY, shoppingList.getShoppingListId());
        startActivity(intent);
    }

    public void shoppingListDeleteSuccess() {
        Toast.makeText(getContext(), "Successfully deleted shopping list", Toast.LENGTH_SHORT).show();
    }

    public void shoppingListDeleteFailed() {
        Toast.makeText(getContext(), "An error occurred while an attempt to delete shopping list", Toast.LENGTH_LONG).show();
    }
}
