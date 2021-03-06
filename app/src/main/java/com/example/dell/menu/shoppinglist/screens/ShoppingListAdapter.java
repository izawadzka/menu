package com.example.dell.menu.shoppinglist.screens;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.dell.menu.R;
import com.example.dell.menu.shoppinglist.events.DeleteShoppingListEvent;
import com.example.dell.menu.shoppinglist.events.EditShoppingListNameEvent;
import com.example.dell.menu.shoppinglist.events.SendShoppingListInSmsButtonClickedEvent;
import com.example.dell.menu.shoppinglist.events.SynchronizeShoppingListWithFridgeButtonClickedEvent;
import com.example.dell.menu.shoppinglist.objects.ShoppingList;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Dell on 08.06.2017.
 */

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder> {

    private final Bus bus;
    List<ShoppingList> shoppingLists = new ArrayList<>();
    private ShoppingListClickedListener shoppingListClickedListener;

    public ShoppingListAdapter(Bus bus) {
        this.bus = bus;
    }

    @Override
    public ShoppingListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ShoppingListViewHolder(inflater.inflate(R.layout.item_shopping_list, parent, false));
    }

    @Override
    public void onBindViewHolder(ShoppingListViewHolder holder, int position) {
        holder.setShoppingList(shoppingLists.get(position));
    }

    @Override
    public int getItemCount() {
        return shoppingLists.size();
    }

    public void setShoppingLists(List<ShoppingList> result) {
        shoppingLists.clear();
        shoppingLists.addAll(result);
        notifyDataSetChanged();
    }

    public void setShoppingListClickedListener(ShoppingListClickedListener shoppingListClickedListener) {
        this.shoppingListClickedListener = shoppingListClickedListener;
    }

    private void itemClicked(ShoppingList shoppingList) {
        if (shoppingListClickedListener != null) {
            shoppingListClickedListener.shoppingListClicked(shoppingList);
        }
    }

    public void remove(int adapterPosition) {
        bus.post(new DeleteShoppingListEvent(shoppingLists.get(adapterPosition)));
    }

    class ShoppingListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.shoppingListNameTextView)
        TextView shoppingListNameTextView;
        @Bind(R.id.authorsNameTextView)
        TextView authorsNameTextView;
        @Bind(R.id.editShoppingListNameImageButton)
        ImageButton editShoppingListNameImageButton;
        @Bind(R.id.sendShoppingListInSmsImageButton)
        ImageButton sendShoppingListInSmsImageButton;
        @Bind(R.id.synchronizeWithFridgeImageButton)
        ImageButton synchronizeWithFridgeImageButton;
        @Bind(R.id.synchronizedLabel)
        TextView synchronizedLabel;

        private ShoppingList shoppingList;


        public ShoppingListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        void setShoppingList(ShoppingList shoppingList) {
            this.shoppingList = shoppingList;
            shoppingListNameTextView.setText(shoppingList.getName());
            authorsNameTextView.setText("Author: " + shoppingList.getAuthorsName());
            if(shoppingList.isAlreadySynchronized()){
                synchronizedLabel.setVisibility(View.VISIBLE);
                synchronizeWithFridgeImageButton.setVisibility(View.GONE);
            }
        }

        @OnClick(R.id.editShoppingListNameImageButton)
        void onEditShoppingListNameImageButtonClicked() {
            bus.post(new EditShoppingListNameEvent(shoppingList.getShoppingListId(), shoppingList.getName()));
        }

        @OnClick(R.id.synchronizeWithFridgeImageButton)
        void onSynchronizeListWithFridgeImageButtonClicked() {
            bus.post(new SynchronizeShoppingListWithFridgeButtonClickedEvent(shoppingList
                    .getShoppingListId()));
        }

        @OnClick(R.id.sendShoppingListInSmsImageButton)
        void onSendShoppingListInSmsImageButtonClicked() {
            bus.post(new SendShoppingListInSmsButtonClickedEvent(shoppingList.getShoppingListId()));
        }

        @Override
        public void onClick(View v) {
            itemClicked(shoppingList);
        }
    }

    public interface ShoppingListClickedListener {
        void shoppingListClicked(ShoppingList shoppingList);
    }
}
