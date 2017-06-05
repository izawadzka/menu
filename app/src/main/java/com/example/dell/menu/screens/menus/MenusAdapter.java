package com.example.dell.menu.screens.menus;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.dell.menu.R;
import com.example.dell.menu.objects.Menu;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Dell on 04.06.2017.
 */

public class MenusAdapter extends RecyclerView.Adapter<MenusAdapter.MenuViewHolder> {
    private final Bus bus;
    List<Menu> menus = new ArrayList<>();
    private MenuClickedListener menuClickedListener;

    public MenusAdapter(Bus bus) {
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

    public void setMenuClickedListener(MenuClickedListener menuClickedListener) {
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
        @Bind(R.id.editMenuImageButton)
        ImageButton editMenuImageButton;
        @Bind(R.id.deleteMenuImageButton)
        ImageButton deleteMenuImageButton;

        private final Bus bus;
        private Menu menu;

        public MenuViewHolder(View itemView, Bus bus) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.bus = bus;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
// TODO: 04.06.2017
        }

        public void setMenu(Menu menu) {
            this.menu = menu;
            menuNameTextView.setText(menu.getName());
            creationDateTextView.setText(String.valueOf(menu.getCreationDate()));
        }
    }

    public interface MenuClickedListener {
        void menuClicked(Menu menu);
    }
}
