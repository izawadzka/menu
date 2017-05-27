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

public class MealsAdapter extends RecyclerView.Adapter<MealsViewHolder> {
    List<Meal> meals = new ArrayList<>();


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

    @Override
    public int getItemCount() {
        return meals.size();
    }

}

class MealsViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.mealNameTextView)
    TextView mealNameTextView;
    @Bind(R.id.caloriesTextView)
    TextView caloriesTextView;
    @Bind(R.id.editProductImageButton)
    ImageButton editProductImageButton;
    @Bind(R.id.deleteProductImageButton)
    ImageButton deleteProductImageButton;


    public MealsViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setMeal(Meal meal) {
        mealNameTextView.setText(meal.getName());
        caloriesTextView.setText(String.format("%s kcal/100g", meal.getCumulativeNumberOfKcalPer100g()));
    }
}
