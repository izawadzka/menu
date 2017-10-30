package com.example.dell.menu.screens.shoppingLists;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dell.menu.App;
import com.example.dell.menu.R;
import com.example.dell.menu.objects.shoppinglist.ShoppingList;

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
    private LayoutInflater inflater;


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

        this.inflater = inflater;

        View view = inflater.inflate(R.layout.fragment_shopping_lists, container, false);
        ButterKnife.bind(this, view);

        shoppingListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ShoppingListAdapter(shoppingListsManager.getBus());
        adapter.setShoppingListClickedListener(this);
        shoppingListRecyclerView.setAdapter(adapter);

        getActivity().setTitle("Shopping list");

        return view;
    }

    public void showStatment(String statement) {
        Toast.makeText(getActivity(), statement, Toast.LENGTH_SHORT).show();
    }

    public void createShoppingListFailed() {
        Toast.makeText(getActivity(), "An error occurred while trying to create shopping list", Toast.LENGTH_SHORT).show();
    }

    public void generateShoppingListSuccess() {
        //Toast.makeText(getActivity(), "Shopping list created successfully", Toast.LENGTH_SHORT).show();
        //shoppingListsManager.loadShoppingLists();
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

    public void showEditShoppingListNameDialog(String shoppingListName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = inflater.inflate(R.layout.name_dialog_layout, null);
        final EditText nameEditText = (EditText) view.findViewById(R.id.addNameEditText);
        nameEditText.setHint("New shopping list name");
        Button addButton = (Button) view.findViewById(R.id.addButton);
        addButton.setText(R.string.save_button_text);
        Button cancelButton = (Button) view.findViewById(R.id.cancelButton);
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        dialog.show();
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shoppingListName = nameEditText.getText().toString();
                if (shoppingListName.length() > 0) {
                    dialog.dismiss();
                    shoppingListsManager.updateShoppingListName(shoppingListName);
                } else nameEditText.setError("You must type new name!");
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void editShoppingListNameSuccess() {
        Toast.makeText(getContext(), "Shopping list name changed successfully", Toast.LENGTH_SHORT).show();
        adapter.setShoppingLists(shoppingListsManager.getShoppingLists());
    }

    public void editShoppingListNameFailed() {
        Toast.makeText(getContext(), "An error occurred while an attempt to change shopping list's name", Toast.LENGTH_LONG).show();
    }
}
