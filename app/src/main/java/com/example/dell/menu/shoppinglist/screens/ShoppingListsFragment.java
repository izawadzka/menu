package com.example.dell.menu.shoppinglist.screens;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dell.menu.App;
import com.example.dell.menu.R;
import com.example.dell.menu.shoppinglist.objects.ShoppingList;

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
    EditText menuNameEditText;
    Button addMenuButton;
    Button cancelButton;

    private ShoppingListsManager shoppingListsManager;
    private ShoppingListAdapter adapter;
    private LayoutInflater inflater;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shoppingListsManager = ((App) getActivity().getApplication()).getShoppingListsManager();

        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        shoppingListsManager.onAttach(this);
        if (shoppingListsManager.isWaitingToGenerateNewShoppingList()) {
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.search_menu);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                shoppingListsManager.findLists(newText);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_add){
            addNewShoppingList();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addNewShoppingList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = inflater.inflate(R.layout.name_dialog_layout, null);
        menuNameEditText = (EditText) view.findViewById(R.id.addNameEditText);
        menuNameEditText.setHint("new menu name");
        addMenuButton = (Button) view.findViewById(R.id.addButton);
        addMenuButton.setText("Add new shopping list");
        cancelButton = (Button) view.findViewById(R.id.cancelButton);
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        dialog.show();
        addMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shoppingListName = menuNameEditText.getText().toString();
                if (shoppingListName.length() > 0) {
                    dialog.dismiss();
                    shoppingListsManager.setCreateShoppingList_mode(true);
                    shoppingListsManager.addNewShoppingList(shoppingListName,
                            ((App)getActivity().getApplication()).getUserStorage().getUserId());
                } else menuNameEditText.setError("Shopping list must have a name!");
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        this.inflater = inflater;

        final View view = inflater.inflate(R.layout.fragment_shopping_lists, container, false);
        ButterKnife.bind(this, view);

        shoppingListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ShoppingListAdapter(shoppingListsManager.getBus());
        adapter.setShoppingListClickedListener(this);
        shoppingListRecyclerView.setAdapter(adapter);
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper
                .SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                Toast.makeText(getContext(), "Swipe to delete!", Toast.LENGTH_LONG).show();
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.remove(viewHolder.getAdapterPosition());
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(shoppingListRecyclerView);

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
        Toast.makeText(getActivity(), "Shopping list created successfully", Toast.LENGTH_SHORT).show();
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

    public void goToShoppingList(Long shoppingListId) {
        Intent intent = new Intent(getActivity(), ShowProductsInListActivity.class);
        intent.putExtra(SHOPPING_LIST_ID_KEY, shoppingListId);
        startActivity(intent);
    }

    public void makeAStatement(String statement, int duration) {
        Toast.makeText(getContext(), statement, duration).show();
    }
}
