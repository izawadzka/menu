package com.example.dell.menu.screens.menus.addOrEditMenu.createNewDailyMenu;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.dell.menu.R;
import com.example.dell.menu.events.menus.AddMealToDailyMenuEvent;
import com.example.dell.menu.objects.Meal;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Dell on 05.06.2017.
 */

public class MealsToChooseAdapter extends RecyclerView.Adapter<MealsToChooseAdapter.MealsToChooseViewHolder> {
    private final Bus bus;
    private final String mealType;
    List<Meal> meals = new ArrayList<>();
    private MealToChooseClickedListener mealToChooseClickedListener;

    public MealsToChooseAdapter(Bus bus, String mealType) {
        this.bus = bus;
        this.mealType = mealType;
    }

    @Override
    public MealsToChooseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new MealsToChooseViewHolder(inflater.inflate(R.layout.item_meal_in_adding_recyclerview, parent, false), bus, mealType);
    }

    @Override
    public void onBindViewHolder(MealsToChooseViewHolder holder, int position) {
        holder.setMeal(meals.get(position));
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    public void setMealClickedListener(MealToChooseClickedListener mealToChooseClickedListener) {
        this.mealToChooseClickedListener = mealToChooseClickedListener;
    }


    private void itemClicked(Meal meal) {
        if(mealToChooseClickedListener != null){
            mealToChooseClickedListener.mealToChooseClicked(meal);
        }
    }

    public void setMealsToChoose(List<Meal> meals) {
        this.meals.clear();
        this.meals.addAll(meals);
        notifyDataSetChanged();
    }

    class MealsToChooseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @Bind(R.id.mealToAddNameTextView)
        TextView mealToAddNameTextView;
        @Bind(R.id.mealToAddCaloriesTextView)
        TextView mealToAddCaloriesTextView;
        @Bind(R.id.addMealToDailyMenuImageButton)
        ImageButton addMealToDailyMenuImageButton;

        private final Bus bus;
        private final String mealType;
        private Meal meal;

        public MealsToChooseViewHolder(View itemView, Bus bus, String mealType) {
            super(itemView);
            this.bus = bus;
            this.mealType = mealType;
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void setMeal(Meal meal) {
            this.meal = meal;
            mealToAddNameTextView.setText(meal.getName());
            mealToAddCaloriesTextView.setText(String.valueOf(meal.getCumulativeNumberOfKcal()) + "kcal");
        }

        @OnClick(R.id.addMealToDailyMenuImageButton)
        public void onAddClicked() {
            bus.post(new AddMealToDailyMenuEvent(meal, mealType));
        }


        @Override
        public void onClick(View v) {
            Log.d("click", "click");
            itemClicked(meal);
        }
    }

    public interface MealToChooseClickedListener {
        void mealToChooseClicked(Meal meal);
    }
}
