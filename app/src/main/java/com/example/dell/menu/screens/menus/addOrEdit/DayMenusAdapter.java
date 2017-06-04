package com.example.dell.menu.screens.menus.addOrEdit;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Dell on 04.06.2017.
 */

public class DayMenusAdapter extends RecyclerView.Adapter<DayMenusAdapter.DayMenuViewHolder>{
    @Override
    public DayMenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(DayMenuViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class DayMenuViewHolder extends RecyclerView.ViewHolder{

        public DayMenuViewHolder(View itemView) {
            super(itemView);
        }
    }
}
