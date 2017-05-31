package com.example.dell.menu.screens.meals;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.dell.menu.R;
import com.example.dell.menu.objects.Meal;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Dell on 27.05.2017.
 */

public class MealsAdapter extends RecyclerView.Adapter<MealsAdapter.MealsViewHolder> {
    List<Meal> meals = new ArrayList<>();

    public MealClickedListener mealClickedListener;

    @Override
    public MealsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new MealsViewHolder(inflater.inflate(R.layout.item_meal, parent, false));
    }

    @Override
    public void onBindViewHolder(MealsViewHolder holder, int position) {
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

    @Override
    public int getItemCount() {
        return meals.size();
    }

    private void itemClicked(Meal meal) {
        if(mealClickedListener != null){
            mealClickedListener.mealClicked(meal);
        }
    }

    class MealsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.mealNameTextView)
        TextView mealNameTextView;
        @Bind(R.id.caloriesTextView)
        TextView caloriesTextView;
        @Bind(R.id.editProductImageButton)
        ImageButton editProductImageButton;
        @Bind(R.id.deleteProductImageButton)
        ImageButton deleteProductImageButton;
        private Meal meal;


        public MealsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void setMeal(Meal meal) {
            this.meal = meal;
            mealNameTextView.setText(meal.getName());
            caloriesTextView.setText(String.format("%s kcal", meal.getCumulativeNumberOfKcalPer100g()));
        }

        @Override
        public void onClick(View v) {
            itemClicked(meal);
        }
    }

    public interface MealClickedListener{
        void mealClicked(Meal meal);
    }
}
