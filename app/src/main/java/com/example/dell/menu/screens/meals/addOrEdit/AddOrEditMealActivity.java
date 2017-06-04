package com.example.dell.menu.screens.meals.addOrEdit;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.menu.App;
import com.example.dell.menu.R;
import com.example.dell.menu.StorageType;
import com.example.dell.menu.events.meals.QuantityWasntTypedEvent;
import com.example.dell.menu.objects.Meal;
import com.example.dell.menu.objects.Product;
import com.example.dell.menu.screens.meals.MealsFragment;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddOrEditMealActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_ADD_PRODUCTS = 1;
    private static final int RESULT_ADDED = 0;
    @Bind(R.id.addedMealNameEditText)
    EditText addedMealNameEditText;
    @Bind(R.id.cumulativeNumberOfKcalEditText)
    EditText cumulativeNumberOfKcalEditText;
    @Bind(R.id.addedMealRecipeEditText)
    EditText addedMealRecipeEditText;
    @Bind(R.id.saveMealButton)
    Button saveMealButton;
    @Bind(R.id.cancel_action)
    Button cancelAction;
    @Bind(R.id.addProductsTextView)
    TextView addProductsTextView;
    @Bind(R.id.addProductsButton)
    ImageButton addProductsButton;
    @Bind(R.id.addedProductsRecyclerView)
    RecyclerView addedProductsRecyclerView;

    private AddedProductsAdapter adapter;
    private AddOrEditMealManager addOrEditMealManager;
    private boolean edit_mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_edit_meal);
        ButterKnife.bind(this);

        addedProductsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AddedProductsAdapter(((App)getApplication()).getBus());
        addedProductsRecyclerView.setAdapter(adapter);

        addOrEditMealManager = ((App)getApplication()).getAddOrEditMealManager();
        if(getIntent().getStringExtra(MealsFragment.EDIT_MODE_KEY) != null){
            edit_mode = true;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        addOrEditMealManager.onAttach(this);

        if(edit_mode){
            addOrEditMealManager.setEditMode();
            addOrEditMealManager.downloadMealForEdit(getIntent().getLongExtra(MealsFragment.MEALS_ID_KEY, 0));
        }else{
            getState();
            setProducts();
        }
    }

    private void getState() {
        addedMealNameEditText.setText(addOrEditMealManager.getStateName());
        cumulativeNumberOfKcalEditText.setText(addOrEditMealManager.getStateKcal());
        addedMealRecipeEditText.setText(addOrEditMealManager.getStateRecipe());
    }

    public void setProducts() {
        adapter.setProducts(addOrEditMealManager.getListOfProducts());
    }

    @Override
    protected void onStop() {
        super.onStop();
        addOrEditMealManager.onStop();
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
                // TODO: 03.06.2017 saving typed datas before going to next activity (like name or recipe)
                addProducts();
                break;
        }
    }

    private void saveMeal() {
        boolean hasErrors = false;

        if(addedMealNameEditText.length() < 5){
            Toast.makeText(this, "Meal name has to have at least 5 characters", Toast.LENGTH_SHORT).show();
            hasErrors = true;
        }

        if(addOrEditMealManager.getListOfProducts().size() == 0){
            Toast.makeText(this, "You haven't choosen any products!", Toast.LENGTH_LONG).show();
            hasErrors = true;
        }

        try{
           Integer.parseInt(cumulativeNumberOfKcalEditText.getText().toString());
        }catch (NumberFormatException e){
            Toast.makeText(this, "kcal has to be a number!", Toast.LENGTH_SHORT).show();
            hasErrors = true;
        }

        if(!hasErrors) {
            addOrEditMealManager.saveState("", "", "");
            if(addOrEditMealManager.isEditMode()){
                addOrEditMealManager.edit(addedMealNameEditText.getText().toString(),
                        cumulativeNumberOfKcalEditText.getText().toString(),
                        addedMealRecipeEditText.getText().toString());
            }else{
            addOrEditMealManager.addMeal(addedMealNameEditText.getText().toString(),
                    cumulativeNumberOfKcalEditText.getText().toString(),
                    ((App) getApplication()).getUserStorage().getUserId(), addedMealRecipeEditText.getText().toString());
            }
        }
    }

    private void addProducts() {
        addOrEditMealManager.saveState(addedMealNameEditText.getText().toString(),
                cumulativeNumberOfKcalEditText.getText().toString(), addedMealRecipeEditText.getText().toString());
        startActivityForResult(new Intent(this, ChooseFromProductsActivity.class), REQUEST_CODE_ADD_PRODUCTS);
    }

    private void cancel() {
        addOrEditMealManager.clearListOfProducts();
        setResult(MealsFragment.RESULT_CANCEL);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_ADD_PRODUCTS && resultCode == RESULT_ADDED){
        }
    }


    public void saveSuccess() {
        addOrEditMealManager.clearListOfProducts();
        setResult(MealsFragment.RESULT_OK);
        finish();
    }

    public void saveFailed() {
        addOrEditMealManager.clearListOfProducts();
        setResult(MealsFragment.RESULT_ERROR);
        finish();
    }

    public void productDeleteSuccess(Product productToDelete) {
        adapter.deleteFromAddedProducts(productToDelete);
    }

    public void productDeleteFailed(String name) {
        Toast.makeText(this, String.format("Error while trying to delete %s", name), Toast.LENGTH_SHORT);
    }

    public void downloadingMealSuccess(Meal meal) {
        addedMealNameEditText.setText(meal.getName());
        cumulativeNumberOfKcalEditText.setText(String.valueOf(meal.getCumulativeNumberOfKcal()));
        addedMealRecipeEditText.setText(meal.getRecipe());

        addOrEditMealManager.downloadProductsInMeal(meal.getMealsId());
    }

    public void downloadingMealFailed() {
        Toast.makeText(this, "Error while downloading meal to edit", Toast.LENGTH_SHORT).show();
        addOrEditMealManager.resetEditMode();
        setResult(MealsFragment.RESULT_ERROR);
        finish();
    }

    public void updateSuccess() {
        Log.d(getPackageName(), String.valueOf(addOrEditMealManager.getListOfProducts().size()));
        addOrEditMealManager.clearListOfProducts();
        addOrEditMealManager.resetEditMode();
        setResult(MealsFragment.RESULT_OK);
        finish();
    }

    public void updateFailed() {
        addOrEditMealManager.clearListOfProducts();
        addOrEditMealManager.resetEditMode();
        setResult(MealsFragment.RESULT_ERROR);
        finish();
    }
}
