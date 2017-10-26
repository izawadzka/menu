package com.example.dell.menu.screens.meals;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.dell.menu.R;
import com.example.dell.menu.events.meals.DeleteMealEvent;
import com.example.dell.menu.events.meals.EditMealEvent;
import com.example.dell.menu.objects.Meal;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Dell on 27.05.2017.
 */

public class MealsAdapter extends RecyclerView.Adapter<MealsAdapter.MealsViewHolder> {
    private final Bus bus;
    List<Meal> meals = new ArrayList<>();
    public MealClickedListener mealClickedListener;
    private MealsViewHolder holder;

    public MealsAdapter(Bus bus) {
        this.bus = bus;
    }

    @Override
    public MealsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new MealsViewHolder(inflater.inflate(R.layout.item_meal, parent, false), bus);
    }

    @Override
    public void onBindViewHolder(MealsViewHolder holder, int position) {
        this.holder = holder;
        holder.setMeal(meals.get(position));
    }

    public void setMeals(List<Meal> meals) {
        this.meals.clear();
        this.meals.addAll(meals);
        notifyDataSetChanged();
    }

    public void setMealClickedListener(MealClickedListener mealClickedListener) {
        this.mealClickedListener = mealClickedListener;
    }

    public void deleteMeal(Meal meal) {
        holder.deleteMeal(meal);
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    private void itemClicked(Meal meal) {
        if (mealClickedListener != null) {
            mealClickedListener.mealClicked(meal);
        }
    }

    class MealsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final Bus bus;
        @Bind(R.id.mealNameTextView)
        TextView mealNameTextView;
        @Bind(R.id.caloriesTextView)
        TextView caloriesTextView;
        @Bind(R.id.updateProductImageButton)
        ImageButton editMealImageButton;
        @Bind(R.id.deleteMealImageButton)
        ImageButton deleteMealImageButton;
        @Bind(R.id.proteinsTextView)
        TextView proteinsTextView;
        @Bind(R.id.carbonsTextView)
        TextView carbonsTextView;
        @Bind(R.id.fatTextView)
        TextView fatTextView;
        private Meal meal;


        public MealsViewHolder(View itemView, Bus bus) {
            super(itemView);
            this.bus = bus;
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void deleteMeal(Meal meal) {
            meals.remove(meal);
            notifyDataSetChanged();
        }

        public void setMeal(Meal meal) {
            this.meal = meal;
            mealNameTextView.setText(meal.getName());
            caloriesTextView.setText(String.format("%s kcal", meal.getCumulativeNumberOfKcal()));
            proteinsTextView.setText(String.format("P: %s g", meal.getAmountOfProteinsPer100g()));
            carbonsTextView.setText(String.format("C: %s g", meal.getAmountOfCarbosPer100g()));
            fatTextView.setText(String.format("F: %s g", meal.getAmountOfFatPer100g()));
        }

        @OnClick(R.id.deleteMealImageButton)
        public void onDeleteMealImageButtonClicked() {
            bus.post(new DeleteMealEvent(meal));
        }

        @OnClick(R.id.updateProductImageButton)
        public void onEditMealImageButtonclicked() {
            bus.post(new EditMealEvent(meal));
        }

        @Override
        public void onClick(View v) {
            itemClicked(meal);
        }
    }

    public interface MealClickedListener {
        void mealClicked(Meal meal);
    }
}
