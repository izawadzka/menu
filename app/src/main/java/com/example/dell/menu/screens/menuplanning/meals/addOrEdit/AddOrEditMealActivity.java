package com.example.dell.menu.screens.menuplanning.meals.addOrEdit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.menu.App;
import com.example.dell.menu.MealsType;
import com.example.dell.menu.R;
import com.example.dell.menu.objects.menuplanning.Meal;
import com.example.dell.menu.screens.menuplanning.meals.MealsFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddOrEditMealActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_ADD_PRODUCTS = 1;

    @Bind(R.id.addedProductsRecyclerView)
    RecyclerView addedProductsRecyclerView;
    @Bind(R.id.breakfastCheckBox)
    CheckBox breakfastCheckBox;
    @Bind(R.id.lunchCheckBox)
    CheckBox lunchCheckBox;
    @Bind(R.id.dinnerCheckBox)
    CheckBox dinnerCheckBox;
    @Bind(R.id.teatimeCheckBox)
    CheckBox teatimeCheckBox;
    @Bind(R.id.supperCheckBox)
    CheckBox supperCheckBox;
    @Bind(R.id.addedMealNameEditText)
    EditText addedMealNameEditText;
    @Bind(R.id.addedMealRecipeEditText)
    EditText addedMealRecipeEditText;
    @Bind(R.id.addProductsTextView)
    TextView addProductsTextView;
    @Bind(R.id.addProductsButton)
    ImageButton addProductsButton;
    @Bind(R.id.saveMealButton)
    Button saveMealButton;
    @Bind(R.id.cancel_action)
    Button cancelAction;
    @Bind(R.id.addedMealNumbOfKcal)
    TextView addedMealNumbOfKcal;
    @Bind(R.id.addedMealNumberOfProtein)
    TextView addedMealNumberOfProtein;
    @Bind(R.id.addedMealAmountOfCarbos)
    TextView addedMealAmountOfCarbos;
    @Bind(R.id.addedMealAmountOfFat)
    TextView addedMealAmountOfFat;
    @Bind(R.id.addedMealNameTextView)
    TextView addedMealNameTextView;
    @Bind(R.id.addeMealRecipeTextView)
    TextView addeMealRecipeTextView;

    private AddedProductsAdapter adapter;
    private AddOrEditMealManager addOrEditMealManager;
    private boolean edit_mode = false;
    private boolean show_mode = false;
    private boolean[] mealsTypesStates = new boolean[MealsType.AMOUNT_OF_TYPES];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_edit_meal);
        ButterKnife.bind(this);
        setTitle("Create meal");

        addOrEditMealManager = ((App) getApplication()).getAddOrEditMealManager();
        if (getIntent().getStringExtra(MealsFragment.EDIT_MODE_KEY) != null) edit_mode = true;
        else if (getIntent().getStringExtra(MealsFragment.SHOW_MODE_KEY) != null) show_mode = true;

        addedProductsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AddedProductsAdapter(((App) getApplication()).getBus(), show_mode);
        addedProductsRecyclerView.setAdapter(adapter);

        for (int i = 0; i < mealsTypesStates.length; i++) {
            mealsTypesStates[i] = false;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        addOrEditMealManager.onAttach(this);
        if (edit_mode) setEditMode();
        else if (show_mode) setShowMode();
        else {
            getState();
            setProducts();
        }
    }

    private void setShowMode() {
        setTitle(getString(R.string.show_meal_title));

        saveMealButton.setVisibility(View.GONE);
        addProductsButton.setVisibility(View.GONE);

        addProductsTextView.setText("Products:");

        addedMealNameEditText.setVisibility(View.GONE);
        addedMealNameTextView.setVisibility(View.VISIBLE);

        addedMealRecipeEditText.setVisibility(View.GONE);
        addeMealRecipeTextView.setVisibility(View.VISIBLE);

        breakfastCheckBox.setClickable(false);
        lunchCheckBox.setClickable(false);
        dinnerCheckBox.setClickable(false);
        teatimeCheckBox.setClickable(false);
        supperCheckBox.setClickable(false);

        addOrEditMealManager.setShowMode();
        addOrEditMealManager.loadMealToShow(getIntent().getIntExtra(MealsFragment.MEALS_ID_KEY, 0));
    }

    private void setEditMode() {
        setTitle("Edit meal");
        addOrEditMealManager.setEditMode();
        edit_mode = false;
        addOrEditMealManager.loadMealForEdit(getIntent().getIntExtra(MealsFragment.MEALS_ID_KEY, 0));
    }

    private void getState() {
        addedMealNameEditText.setText(addOrEditMealManager.getStateName());
        addedMealRecipeEditText.setText(addOrEditMealManager.getStateRecipe());


        mealsTypesStates = addOrEditMealManager.getMealsTypesStates();
        addedMealNumbOfKcal.setText(String.valueOf(addOrEditMealManager.getAmountOfKcal()));
        addedMealNumberOfProtein.setText(String.valueOf(addOrEditMealManager.getAmountOfProteins()));
        addedMealAmountOfCarbos.setText(String.valueOf(addOrEditMealManager.getAmountOfCarbons()));
        addedMealAmountOfFat.setText(String.valueOf(addOrEditMealManager.getAmountOfFat()));
        setCheckBoxes();
    }

    public void setProducts() {
        adapter.setProducts(addOrEditMealManager.getListOfProducts());
    }

    @Override
    protected void onStop() {
        super.onStop();
        setState();
        addOrEditMealManager.onStop();
    }

    private void setState() {
        addOrEditMealManager.setStateName(addedMealNameEditText.getText().toString());
        addOrEditMealManager.setStateRecipe(addedMealRecipeEditText.getText().toString());
        addOrEditMealManager.setMealsTypesStates(mealsTypesStates);
    }

    @OnClick({R.id.saveMealButton, R.id.cancel_action, R.id.addProductsButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.saveMealButton:
                saveMeal();
                break;
            case R.id.cancel_action:
                cancel();
                break;
            case R.id.addProductsButton:
                addProducts();
                break;
        }
    }

    private void saveMeal() {
        boolean hasErrors = false;
        boolean typeNotChosen = true;
        saveMealButton.setEnabled(false);

        if (addedMealNameEditText.length() < 5) {
            addedMealNameEditText.setError("Meal name has to have at least 5 characters");
            hasErrors = true;
        }

        if (addOrEditMealManager.getListOfProducts().size() == 0) {
            Toast.makeText(this, "You haven't choosen any products!", Toast.LENGTH_LONG).show();
            hasErrors = true;
        }


        for (boolean mealsTypesState : mealsTypesStates) {
            if (mealsTypesState) typeNotChosen = false;
        }
        if (typeNotChosen) {
            Toast.makeText(this, "You have to choose at least one type of meal!", Toast.LENGTH_LONG).show();
            hasErrors = true;
        }


        if (!hasErrors) {

            if (addOrEditMealManager.isEditMode()) {
                addOrEditMealManager.edit(addedMealNameEditText.getText().toString(),
                        addedMealRecipeEditText.getText().toString(), mealsTypesStates);
            } else {
                addOrEditMealManager.addMeal(addedMealNameEditText.getText().toString(),
                        ((App) getApplication()).getUserStorage().getUserId(), addedMealRecipeEditText.getText().toString(), mealsTypesStates);
            }
        }else saveMealButton.setEnabled(true);
    }

    private void addProducts() {
        addOrEditMealManager.saveState(addedMealNameEditText.getText().toString(), addedMealRecipeEditText.getText().toString());
        startActivityForResult(new Intent(this, ChooseFromProductsActivity.class), REQUEST_CODE_ADD_PRODUCTS);
    }

    private void cancel() {
        clearValues();
        setResult(MealsFragment.RESULT_CANCEL);
        finish();
    }


    public void saveSuccess() {
        clearValues();
        setResult(MealsFragment.RESULT_OK);
        finish();
    }

    public void saveFailed() {
        clearValues();
        setResult(MealsFragment.RESULT_ERROR);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearValues();
        addOrEditMealManager.resetValues();
    }

    public void productDeleteFailed(String name) {
        Toast.makeText(this, String.format("Error while trying to delete %s", name), Toast.LENGTH_SHORT).show();
    }

    public void loadingMealSuccess(Meal meal, boolean[] result) {
        if(show_mode){
            addedMealNameTextView.setText(meal.getName());
            addeMealRecipeTextView.setText(meal.getRecipe());
        }else{
            addedMealNameEditText.setText(meal.getName());
            addedMealRecipeEditText.setText(meal.getRecipe());
        }

        addedMealNumbOfKcal.setText(String.valueOf(meal.getCumulativeNumberOfKcal()));
        addedMealNumberOfProtein.setText(String.valueOf(meal.getAmountOfProteins()));
        addedMealAmountOfCarbos.setText(String.valueOf(meal.getAmountOfCarbos()));
        addedMealAmountOfFat.setText(String.valueOf(meal.getAmountOfFat()));



        mealsTypesStates = result;
        setCheckBoxes();


        addOrEditMealManager.downloadProductsInMeal(meal.getMealsId());
    }

    private void setCheckBoxes() {
        breakfastCheckBox.setChecked(mealsTypesStates[MealsType.BREAKFAST_INDX - 1]);
        lunchCheckBox.setChecked(mealsTypesStates[MealsType.LUNCH_INDX - 1]);
        dinnerCheckBox.setChecked(mealsTypesStates[MealsType.DINNER_INDX - 1]);
        teatimeCheckBox.setChecked(mealsTypesStates[MealsType.TEATIME_INDX - 1]);
        supperCheckBox.setChecked(mealsTypesStates[MealsType.SUPPER_INDX - 1]);
    }

    public void loadingMealFailed() {
        Toast.makeText(this, "Error while loading meal", Toast.LENGTH_SHORT).show();
        clearValues();
        setResult(MealsFragment.RESULT_ERROR);
        finish();
    }

    public void clearValues() {
        addedMealNameEditText.setText("");
        addedMealRecipeEditText.setText("");

        addOrEditMealManager.clearListOfProducts();
        addOrEditMealManager.resetMealsTypesStates();
        addOrEditMealManager.resetEditMode();
        addOrEditMealManager.resetShowMode();
    }

    public void updateSuccess() {
        clearValues();
        setResult(MealsFragment.RESULT_OK);
        finish();
    }

    public void updateFailed() {
        clearValues();
        setResult(MealsFragment.RESULT_ERROR);
        finish();
    }

    @OnClick({R.id.breakfastCheckBox, R.id.lunchCheckBox, R.id.dinnerCheckBox, R.id.teatimeCheckBox, R.id.supperCheckBox})
    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();

        switch (view.getId()) {
            case R.id.breakfastCheckBox:
                mealsTypesStates[MealsType.BREAKFAST_INDX - 1] = checked;
                break;
            case R.id.lunchCheckBox:
                mealsTypesStates[MealsType.LUNCH_INDX - 1] = checked;
                break;
            case R.id.dinnerCheckBox:
                mealsTypesStates[MealsType.DINNER_INDX - 1] = checked;
                break;
            case R.id.teatimeCheckBox:
                mealsTypesStates[MealsType.TEATIME_INDX - 1] = checked;
                break;
            case R.id.supperCheckBox:
                mealsTypesStates[MealsType.SUPPER_INDX - 1] = checked;
                break;
        }
    }
}
