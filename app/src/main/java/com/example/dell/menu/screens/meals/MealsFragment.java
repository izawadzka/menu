package com.example.dell.menu.screens.meals;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.dell.menu.App;
import com.example.dell.menu.R;
import com.example.dell.menu.events.meals.EditMealEvent;
import com.example.dell.menu.objects.Meal;
import com.example.dell.menu.screens.meals.addOrEdit.AddOrEditMealActivity;
import com.example.dell.menu.screens.meals.extendedMealInformation.FullMealInformationActivity;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;


import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Dell on 27.05.2017.
 */

public class MealsFragment extends Fragment implements MealsAdapter.MealClickedListener {

    public static final int REQUEST_CODE_SHOW = 1;
    public static final int REQUEST_CODE_ADD = 10;
    public static final int RESULT_ERROR = -1;
    public static final int RESULT_OK = 0;
    public static final int RESULT_CANCEL = 11;
    public static final String MEAL_NAME_KEY = "mealName";
    public static final String MEAL_NUMBER_OF_KCAL_KEY = "mealNumberOfKcal";
    public static final String MEALS_AUTHOR_NAME_KEY = "mealsAuthorName";
    public static final String MEALS_RECIPE_KEY = "mealsRecipe";
    public static final String MEALS_ID_KEY = "mealsId";
    public static final int REQUEST_CODE_EDIT = 15;
    public static final String EDIT_MODE_KEY = "edit mode";

    @Bind(R.id.mealsRecyclerView)
    RecyclerView mealsRecyclerView;
    private MealsFragmentManager mealsFragmentManager;
    private MealsAdapter adapter;
    private Bus bus;

    @Override
    public void onStart() {
        super.onStart();
        bus.register(this);
        mealsFragmentManager.onAttach(this);
        mealsFragmentManager.loadMeals();
    }

    @Override
    public void onStop() {
        super.onStop();
        bus.unregister(this);
        mealsFragmentManager.onStop();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mealsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MealsAdapter(bus);
        adapter.setMealClickedListener(this);
        mealsRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mealsFragmentManager = ((App) getActivity().getApplication()).getMealsFragmentManager();
        setHasOptionsMenu(true);
        bus = ((App) getActivity().getApplication()).getBus();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.meals, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_add_meal){
            addNewMeal();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addNewMeal() {
        startActivityForResult(new Intent(getActivity(), AddOrEditMealActivity.class), REQUEST_CODE_ADD);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meals, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void showMeals(List<Meal> result){
        adapter.setMeals(result);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void mealClicked(Meal meal) {
        String authorsName;
        if(meal.getAuthorsId() == 0){
            authorsName = "authomaticly_generated";
        }else{
            authorsName = mealsFragmentManager.getAuthorsName(meal);
        }

        Intent intent = new Intent(getActivity(), FullMealInformationActivity.class);
        intent.putExtra(MEAL_NAME_KEY, meal.getName());
        intent.putExtra(MEAL_NUMBER_OF_KCAL_KEY, String.format("%s",meal.getCumulativeNumberOfKcal()));
        intent.putExtra(MEALS_AUTHOR_NAME_KEY, authorsName);
        intent.putExtra(MEALS_RECIPE_KEY, meal.getRecipe());
        intent.putExtra(MEALS_ID_KEY, String.format("%s",meal.getMealsId()));
        startActivityForResult(intent, REQUEST_CODE_SHOW);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode ==  REQUEST_CODE_SHOW && resultCode== RESULT_ERROR){
            Toast.makeText(getActivity(), "Error while trying to show meal information", Toast.LENGTH_SHORT).show();
        }else if(requestCode == REQUEST_CODE_ADD && resultCode == RESULT_CANCEL){
            Toast.makeText(getActivity(), "Cancel adding meal", Toast.LENGTH_SHORT).show();
        }else if(requestCode == REQUEST_CODE_ADD && resultCode == RESULT_OK){
            Toast.makeText(getActivity(), "New meal added", Toast.LENGTH_SHORT).show();
        }else if(requestCode == REQUEST_CODE_ADD && requestCode == RESULT_ERROR){
            Toast.makeText(getActivity(), "Failed to add a new meal", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteSuccess(Meal currentMeal) {
        adapter.deleteMeal(currentMeal);
    }

    public void deleteFailed() {
        Toast.makeText(getActivity(), "Failed while trying to delete a meal", Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void onEditMeal(EditMealEvent event){
        Intent intent = new Intent(getActivity(), AddOrEditMealActivity.class);
        intent.putExtra(EDIT_MODE_KEY, "true");
        intent.putExtra(MEALS_ID_KEY, event.meal.getMealsId());
        startActivityForResult(intent, REQUEST_CODE_EDIT);
    }
}
