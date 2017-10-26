package com.example.dell.menu.screens.menus.addOrEditMenu.dailyMenu.chooseMeals;

import android.content.Intent;
import android.os.Bundle;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.dell.menu.App;
import com.example.dell.menu.R;
import com.example.dell.menu.objects.Meal;
import com.example.dell.menu.screens.meals.MealsFragment;
import com.example.dell.menu.screens.meals.extendedMealInformation.FullMealInformationActivity;
import com.example.dell.menu.screens.menus.addOrEditMenu.dailyMenu.DailyMenuFragment;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChooseFromMealsActivity extends AppCompatActivity implements MealsToChooseAdapter.MealToChooseClickedListener {

    @Bind(R.id.mealsToChooseRecyclerView)
    RecyclerView mealsToChooseRecyclerView;
    private ActionBar actionBar;
    private ChooseFromMealsManager chooseFromMealsManager;
    private MealsToChooseAdapter adapter;
    private String mealType;
    private long currentDailyMenuId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_from_meals);
        ButterKnife.bind(this);

        mealType = getIntent().getStringExtra(DailyMenuFragment.MEAL_TYPE_KEY);
        currentDailyMenuId = getIntent().getLongExtra(DailyMenuFragment.DAILY_MENU_ID_KEY, -1);

        actionBar = getSupportActionBar();
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);

        chooseFromMealsManager = ((App)getApplication()).getChooseFromMealsManager();

        mealsToChooseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MealsToChooseAdapter(((App) getApplication()).getBus(), mealType, currentDailyMenuId);
        adapter.setMealClickedListener(this);
        mealsToChooseRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        chooseFromMealsManager.onAttach(this);
        chooseFromMealsManager.loadMeals();
    }

    @Override
    protected void onStop() {
        super.onStop();
        chooseFromMealsManager.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        menu.findItem(R.id.action_add).setVisible(false);
        MenuItem item = menu.findItem(R.id.search_menu);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                chooseFromMealsManager.searchMeals(newText);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            setResult(DailyMenuFragment.RESULT_OK);
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showMeals(List<Meal> meals) {
        adapter.setMealsToChoose(meals);
    }

    @Override
    public void mealToChooseClicked(Meal meal) {
        String authorsName;
        if(meal.getAuthorsId() == 0){
            authorsName = "authomaticly_generated";
        }else{
            //authorsName = ((App)getApplication()).getMealsFragmentManager().getAuthorsName(meal);
        }

        Intent intent = new Intent(this, FullMealInformationActivity.class);
        intent.putExtra(MealsFragment.MEAL_NAME_KEY, meal.getName());
        intent.putExtra(MealsFragment.MEAL_NUMBER_OF_KCAL_KEY, String.format("%s",meal.getCumulativeNumberOfKcal()));
        //intent.putExtra(MealsFragment.MEALS_AUTHOR_NAME_KEY, authorsName);
        intent.putExtra(MealsFragment.MEALS_RECIPE_KEY, meal.getRecipe());
        intent.putExtra(MealsFragment.MEALS_ID_KEY, String.format("%s",meal.getMealsId()));
        startActivityForResult(intent, MealsFragment.REQUEST_CODE_SHOW);
    }

    public void showMealWasAdded(String mealName) {
        Toast.makeText(this, String.format("Meal %s was added", mealName), Toast.LENGTH_LONG).show();
    }
}
