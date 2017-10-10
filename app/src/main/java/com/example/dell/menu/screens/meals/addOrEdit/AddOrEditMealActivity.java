package com.example.dell.menu.screens.meals.addOrEdit;

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
import com.example.dell.menu.objects.Meal;
import com.example.dell.menu.objects.Product;
import com.example.dell.menu.screens.meals.MealsFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddOrEditMealActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_ADD_PRODUCTS = 1;
    private static final int RESULT_ADDED = 0;

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
    @Bind(R.id.cumulativeNumberOfKcalEditText)
    EditText cumulativeNumberOfKcalEditText;
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

    private AddedProductsAdapter adapter;
    private AddOrEditMealManager addOrEditMealManager;
    private boolean edit_mode;
    private boolean[] mealsTypesStates = new boolean[MealsType.AMOUNT_OF_TYPES];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_edit_meal);
        ButterKnife.bind(this);

        addedProductsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AddedProductsAdapter(((App) getApplication()).getBus());
        addedProductsRecyclerView.setAdapter(adapter);

        addOrEditMealManager = ((App) getApplication()).getAddOrEditMealManager();
        if (getIntent().getStringExtra(MealsFragment.EDIT_MODE_KEY) != null) {
            edit_mode = true;
        }

        for (int i = 0; i < mealsTypesStates.length; i++) {
            mealsTypesStates[i] = false;
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        addOrEditMealManager.onAttach(this);

        if (edit_mode) setEditMode();
        else {
            getState();
            setProducts();
        }
    }

    private void setEditMode() {
        setTitle("Edit meal");
        addOrEditMealManager.setEditMode();
        addOrEditMealManager.downloadMealForEdit(getIntent().getIntExtra(MealsFragment.MEALS_ID_KEY, 0));
    }

    private void getState() {
        addedMealNameEditText.setText(addOrEditMealManager.getStateName());
        cumulativeNumberOfKcalEditText.setText(addOrEditMealManager.getStateKcal());
        addedMealRecipeEditText.setText(addOrEditMealManager.getStateRecipe());
        mealsTypesStates = addOrEditMealManager.getMealsTypesStates();
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
        addOrEditMealManager.setStateKcal(cumulativeNumberOfKcalEditText.getText().toString());
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

        if (addedMealNameEditText.length() < 5) {
            addedMealNameEditText.setError("Meal name has to have at least 5 characters");
            hasErrors = true;
        }

        if (addOrEditMealManager.getListOfProducts().size() == 0) {
            Toast.makeText(this, "You haven't choosen any products!", Toast.LENGTH_LONG).show();
            hasErrors = true;
        }

        try {
            Integer.parseInt(cumulativeNumberOfKcalEditText.getText().toString());
        } catch (NumberFormatException e) {
            cumulativeNumberOfKcalEditText.setError("kcal must be a number!");
            hasErrors = true;
        }

        for (boolean mealsTypesState : mealsTypesStates) {
            if(mealsTypesState) typeNotChosen = false;
        }
        if(typeNotChosen){
            Toast.makeText(this, "You have to choose at least one type of meal!", Toast.LENGTH_LONG).show();
            hasErrors = true;
        }


        if (!hasErrors) {
            addOrEditMealManager.resetState();
            if (addOrEditMealManager.isEditMode()) {
                addOrEditMealManager.edit(addedMealNameEditText.getText().toString(),
                        cumulativeNumberOfKcalEditText.getText().toString(),
                        addedMealRecipeEditText.getText().toString(), mealsTypesStates);
            } else {
                addOrEditMealManager.addMeal(addedMealNameEditText.getText().toString(),
                        cumulativeNumberOfKcalEditText.getText().toString(),
                        ((App) getApplication()).getUserStorage().getUserId(), addedMealRecipeEditText.getText().toString(), mealsTypesStates);
            }
        }
    }

    private void addProducts() {
        addOrEditMealManager.saveState(addedMealNameEditText.getText().toString(),
                cumulativeNumberOfKcalEditText.getText().toString(), addedMealRecipeEditText.getText().toString());
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

    public void productDeleteSuccess(Product productToDelete) {
        adapter.deleteFromAddedProducts(productToDelete);
    }

    public void productDeleteFailed(String name) {
        Toast.makeText(this, String.format("Error while trying to delete %s", name), Toast.LENGTH_SHORT);
    }

    public void downloadingMealSuccess(Meal meal, boolean[] result) {
        addedMealNameEditText.setText(meal.getName());
        cumulativeNumberOfKcalEditText.setText(String.valueOf(meal.getCumulativeNumberOfKcal()));
        addedMealRecipeEditText.setText(meal.getRecipe());


        mealsTypesStates = result;
        setCheckBoxes();


        addOrEditMealManager.downloadProductsInMeal(meal.getMealsId());
    }

    private void setCheckBoxes() {
        breakfastCheckBox.setChecked(mealsTypesStates[MealsType.BREAKFAST_INDX-1]);
        lunchCheckBox.setChecked(mealsTypesStates[MealsType.LUNCH_INDX-1]);
        dinnerCheckBox.setChecked(mealsTypesStates[MealsType.DINNER_INDX-1]);
        teatimeCheckBox.setChecked(mealsTypesStates[MealsType.TEATIME_INDX-1]);
        supperCheckBox.setChecked(mealsTypesStates[MealsType.SUPPER_INDX-1]);
    }

    public void downloadingMealFailed() {
        Toast.makeText(this, "Error while downloading meal to edit", Toast.LENGTH_SHORT).show();
        clearValues();
        setResult(MealsFragment.RESULT_ERROR);
        finish();
    }

    public void clearValues(){
        addedMealNameEditText.setText("");
        cumulativeNumberOfKcalEditText.setText("");
        addedMealRecipeEditText.setText("");

        addOrEditMealManager.clearListOfProducts();
        addOrEditMealManager.resetMealsTypesStates();
        addOrEditMealManager.resetEditMode();
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
                if (checked) mealsTypesStates[MealsType.BREAKFAST_INDX-1] = true;
                else mealsTypesStates[MealsType.BREAKFAST_INDX-1] = false;
                break;
            case R.id.lunchCheckBox:
                if (checked) mealsTypesStates[MealsType.LUNCH_INDX-1] = true;
                else mealsTypesStates[MealsType.LUNCH_INDX-1] = false;
                break;
            case R.id.dinnerCheckBox:
                if (checked) mealsTypesStates[MealsType.DINNER_INDX-1] = true;
                else mealsTypesStates[MealsType.DINNER_INDX-1] = false;
                break;
            case R.id.teatimeCheckBox:
                if (checked) mealsTypesStates[MealsType.TEATIME_INDX-1] = true;
                else mealsTypesStates[MealsType.TEATIME_INDX-1] = false;
                break;
            case R.id.supperCheckBox:
                if (checked) mealsTypesStates[MealsType.SUPPER_INDX-1] = true;
                else mealsTypesStates[MealsType.SUPPER_INDX-1] = false;
                break;
        }
    }
}
