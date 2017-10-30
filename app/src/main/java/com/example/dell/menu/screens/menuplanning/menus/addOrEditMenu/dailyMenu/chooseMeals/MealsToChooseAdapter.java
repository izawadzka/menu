package com.example.dell.menu.screens.menuplanning.menus.addOrEditMenu.dailyMenu.chooseMeals;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.dell.menu.R;
import com.example.dell.menu.events.menus.AddMealToDailyMenuEvent;
import com.example.dell.menu.objects.menuplanning.Meal;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Dell on 05.06.2017.
 */

class MealsToChooseAdapter extends RecyclerView.Adapter<MealsToChooseAdapter.MealsToChooseViewHolder> {
    private final Bus bus;
    private final String mealType;
    private final long currentDailyMenuId;
    private List<Meal> meals = new ArrayList<>();
    private MealToChooseClickedListener mealToChooseClickedListener;

    MealsToChooseAdapter(Bus bus, String mealType, long currentDailyMenuId) {
        this.bus = bus;
        this.mealType = mealType;
        this.currentDailyMenuId = currentDailyMenuId;
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

    void setMealClickedListener(MealToChooseClickedListener mealToChooseClickedListener) {
        this.mealToChooseClickedListener = mealToChooseClickedListener;
    }


    private void itemClicked(Meal meal) {
        if (mealToChooseClickedListener != null) {
            mealToChooseClickedListener.mealToChooseClicked(meal);
        }
    }

    void setMealsToChoose(List<Meal> meals) {
        this.meals.clear();
        this.meals.addAll(meals);
        notifyDataSetChanged();
    }

    class MealsToChooseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.mealToAddNameTextView)
        TextView mealToAddNameTextView;
        @Bind(R.id.mealToAddCaloriesTextView)
        TextView mealToAddCaloriesTextView;
        @Bind(R.id.addMealToDailyMenuImageButton)
        ImageButton addMealToDailyMenuImageButton;
        @Bind(R.id.proteinsTextView)
        TextView proteinsTextView;
        @Bind(R.id.carbonsTextView)
        TextView carbonsTextView;
        @Bind(R.id.fatTextView)
        TextView fatTextView;

        private final Bus bus;
        private final String mealType;
        private Meal meal;

        MealsToChooseViewHolder(View itemView, Bus bus, String mealType) {
            super(itemView);
            this.bus = bus;
            this.mealType = mealType;
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void setMeal(Meal meal) {
            this.meal = meal;
            mealToAddNameTextView.setText(meal.getName());
            mealToAddCaloriesTextView.setText(String.format("%s kcal", meal.getCumulativeNumberOfKcal()));
            proteinsTextView.setText(String.format("P: %s g", meal.getAmountOfProteins()));
            carbonsTextView.setText(String.format("C: %s g", meal.getAmountOfCarbos()));
            fatTextView.setText(String.format("F: %s g", meal.getAmountOfFat()));
        }

        @OnClick(R.id.addMealToDailyMenuImageButton)
        void onAddClicked() {
            bus.post(new AddMealToDailyMenuEvent(meal, mealType, currentDailyMenuId));
        }


        @Override
        public void onClick(View v) {
            itemClicked(meal);
        }
    }

    interface MealToChooseClickedListener {
        void mealToChooseClicked(Meal meal);
    }
}
