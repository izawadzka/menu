package com.example.dell.menu.menuplanning.screens.menus;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.dell.menu.R;
import com.example.dell.menu.menuplanning.events.menus.DeleteMenuEvent;
import com.example.dell.menu.menuplanning.events.menus.EditMenuNameEvent;
import com.example.dell.menu.menuplanning.events.menus.GenerateShoppingListButtonClickedEvent;
import com.example.dell.menu.menuplanning.objects.Menu;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


class MenusAdapter extends RecyclerView.Adapter<MenusAdapter.MenuViewHolder> {
    private final Bus bus;
    private List<Menu> menus = new ArrayList<>();
    private MenuClickedListener menuClickedListener;

    MenusAdapter(Bus bus) {
        this.bus = bus;
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new MenuViewHolder(inflater.inflate(R.layout.item_menu, parent, false), bus);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        holder.setMenu(menus.get(position));
    }

    @Override
    public int getItemCount() {
        return menus.size();
    }

    public void setMenus(List<Menu> result) {
        menus.clear();
        menus.addAll(result);
        notifyDataSetChanged();
    }

    void setMenuClickedListener(MenuClickedListener menuClickedListener) {
        this.menuClickedListener = menuClickedListener;
    }

    private void itemClicked(Menu menu) {
        if (menuClickedListener != null) {
            menuClickedListener.menuClicked(menu);
        }
    }

    class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.menuNameTextView)
        TextView menuNameTextView;
        @Bind(R.id.creationDateTextView)
        TextView creationDateTextView;
        @Bind(R.id.editMenuNameImageButton)
        ImageButton editMenuImageButton;
        @Bind(R.id.deleteMenuImageButton)
        ImageButton deleteMenuImageButton;
        @Bind(R.id.generateShoppingList)
        ImageButton generateShoppingList;
        @Bind(R.id.caloriesTextView)
        TextView caloriesTextView;
        @Bind(R.id.proteinsTextView)
        TextView proteinsTextView;
        @Bind(R.id.carbonsTextView)
        TextView carbonsTextView;
        @Bind(R.id.fatTextView)
        TextView fatTextView;

        private final Bus bus;
        private Menu menu;

        MenuViewHolder(View itemView, Bus bus) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.bus = bus;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClicked(menu);
        }

        public void setMenu(Menu menu) {
            this.menu = menu;
            menuNameTextView.setText(menu.getName());
            caloriesTextView.setText(String.format("%s kcal", menu.getCumulativeNumberOfKcal()));
            proteinsTextView.setText(String.format("P: %s g", menu.getAmountOfProteins()));
            carbonsTextView.setText(String.format("C: %s g", menu.getAmountOfCarbos()));
            fatTextView.setText(String.format("F: %s g", menu.getAmountOfFat()));
            creationDateTextView.setText("creation date: " + menu.getCreationDate());
        }

        @OnClick(R.id.generateShoppingList)
        void onGenerateShoppingListButtonClicked() {
            bus.post(new GenerateShoppingListButtonClickedEvent(menu));
        }

        @OnClick(R.id.editMenuNameImageButton)
        void onEditMenuImageButtonClicked() {
            bus.post(new EditMenuNameEvent(menu));
        }

        @OnClick(R.id.deleteMenuImageButton)
        void onDeleteMenuImageButtonClicked() {
            bus.post(new DeleteMenuEvent(menu));
        }
    }

    interface MenuClickedListener {
        void menuClicked(Menu menu);
    }
}
