package com.example.dell.menu.screens.menus.addOrEditMenu;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.dell.menu.R;
import com.example.dell.menu.objects.DailyMenu;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Dell on 04.06.2017.
 */

public class DailyMenusAdapter extends RecyclerView.Adapter<DailyMenusAdapter.DailyMenuViewHolder> {
    List<DailyMenu> dailyMenus = new ArrayList<>();
    private final Bus bus;
    private DailyMenuClickedListener dailyMenuClickedListener;

    public DailyMenusAdapter(Bus bus) {
        this.bus = bus;
    }

    @Override
    public DailyMenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new DailyMenuViewHolder(inflater.inflate(R.layout.item_daily_menu, parent, false), bus);
    }

    @Override
    public void onBindViewHolder(DailyMenuViewHolder holder, int position) {
        holder.setDailyMenu(dailyMenus.get(position), position);
    }

    @Override
    public int getItemCount() {
        return dailyMenus.size();
    }

    public void setDailyMenus(List<DailyMenu> result){
        dailyMenus.clear();
        dailyMenus.addAll(result);
        notifyDataSetChanged();
    }

    public void setDailyMenuClickedListener(DailyMenuClickedListener dailyMenuClickedListener){
        this.dailyMenuClickedListener = dailyMenuClickedListener;
    }

    private void itemClicked(DailyMenu dailyMenu) {
        if (dailyMenuClickedListener != null) {
            dailyMenuClickedListener.dailyMenuClicked(dailyMenu);
        }
    }

    class DailyMenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.day_record_number)
        TextView dayRecordNumber;
        @Bind(R.id.deleteDailyMenuImageButton)
        ImageButton deleteDailyMenuImageButton;
        @Bind(R.id.editDailyMenuImageButton)
        ImageButton editDailyMenuImageButton;

        private final Bus bus;
        private DailyMenu dailyMenu;

        public DailyMenuViewHolder(View itemView, Bus bus) {
            super(itemView);
            this.bus = bus;
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void setDailyMenu(DailyMenu dailyMenu, int position) {
            this.dailyMenu  = dailyMenu;
            dayRecordNumber.setText(String.format("Day %s", position));
        }

        @Override
        public void onClick(View v) {
            // TODO: 04.06.2017
        }
    }

    public interface DailyMenuClickedListener {
        void dailyMenuClicked(DailyMenu dailyMenu);
    }
}
