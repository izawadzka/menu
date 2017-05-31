package com.example.dell.menu.screens.meals.extendedMealInformation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.dell.menu.App;
import com.example.dell.menu.R;
import com.example.dell.menu.objects.Product;
import com.example.dell.menu.screens.meals.MealsFragment;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FullMealInformationActivity extends AppCompatActivity {

    @Bind(R.id.mealNameTextViewExt)
    TextView mealNameTextViewExt;
    @Bind(R.id.recipeTextViewExt)
    TextView recipeTextViewExt;
    @Bind(R.id.kcalTextViewExt)
    TextView kcalTextViewExt;
    @Bind(R.id.mealAuthorsTextViewExt)
    TextView mealAuthorsTextViewExt;
    @Bind(R.id.productInMealsRecyclerView)
    RecyclerView productInMealsRecyclerView;


    private FullMealInformationActivityManager fullMealInformationActivityManager;
    private int mealsId;
    private FullMealInformationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_meal_information);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            Intent intent = getIntent();
            mealNameTextViewExt.setText(intent.getStringExtra(MealsFragment.MEAL_NAME_KEY));
            kcalTextViewExt.setText(intent.getStringExtra(MealsFragment.MEAL_NUMBER_OF_KCAL_KEY) + "kcal");
            mealAuthorsTextViewExt.setText("Author: " + intent.getStringExtra(MealsFragment.MEALS_AUTHOR_NAME_KEY));
            recipeTextViewExt.setText(intent.getStringExtra(MealsFragment.MEALS_RECIPE_KEY));
            mealsId = Integer.parseInt(intent.getStringExtra(MealsFragment.MEALS_ID_KEY));

            fullMealInformationActivityManager = ((App) getApplication()).getFullMealInformationActivityManager();

            productInMealsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new FullMealInformationAdapter();
            productInMealsRecyclerView.setAdapter(adapter);
        }catch (Exception e) {
            Log.d("full", e.getMessage());
            setResult(MealsFragment.RESULT_ERROR);
            finish();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        fullMealInformationActivityManager.onAttach(this);
        fullMealInformationActivityManager.loadUsedProducts(mealsId);
    }

    @Override
    protected void onStop() {
        super.onStop();
        fullMealInformationActivityManager.onStop();
    }

    public void showProducts(List<Product> result) {
        adapter.setProducts(result);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            setResult(MealsFragment.RESULT_OK);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
