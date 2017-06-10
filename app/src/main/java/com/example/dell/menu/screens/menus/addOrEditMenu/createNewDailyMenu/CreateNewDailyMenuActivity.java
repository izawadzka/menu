package com.example.dell.menu.screens.menus.addOrEditMenu.createNewDailyMenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.menu.App;
import com.example.dell.menu.R;
import com.example.dell.menu.objects.Meal;
import com.example.dell.menu.screens.menus.addOrEditMenu.AddOrEditMenuActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateNewDailyMenuActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_ADD = 1;
    public static final String MEAL_TYPE_KEY = "mealType";
    public static final String BREAKFAST_KEY = "breakfast";
    public static final String LUNCH_KEY = "lunch";
    public static final String DINNER_KEY = "dinner";
    public static final String TEATIME_KEY = "teatime";
    public static final String SUPPER_KEY = "supper";
    @Bind(R.id.dateEditText)
    EditText dateEditText;
    @Bind(R.id.addMealForBreakfastImageButton)
    ImageButton addMealForBreakfastImageButton;
    @Bind(R.id.breakfastElementsTextView)
    TextView breakfastElementsTextView;
    @Bind(R.id.addMealForLunchImageButton)
    ImageButton addMealForLunchImageButton;
    @Bind(R.id.lunchElementsTextView)
    TextView lunchElementsTextView;
    @Bind(R.id.addMealForDinnerImageButton)
    ImageButton addMealForDinnerImageButton;
    @Bind(R.id.dinnerElementsTextView)
    TextView dinnerElementsTextView;
    @Bind(R.id.addMealForTeatimeImageButton)
    ImageButton addMealForTeatimeImageButton;
    @Bind(R.id.teatimeElementsTextView)
    TextView teatimeElementsTextView;
    @Bind(R.id.addMealForSupperImageButton)
    ImageButton addMealForSupperImageButton;
    @Bind(R.id.supperElementsTextView)
    TextView supperElementsTextView;
    @Bind(R.id.saveDailyMenuButton)
    Button saveDailyMenuButton;
    @Bind(R.id.cancel_action)
    Button cancelAction;
    private CreateNewDailyMenuManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_daily_menu);
        ButterKnife.bind(this);
        manager = ((App)getApplication()).getCreateNewDailyMenuManager();
    }

    @Override
    protected void onStart() {
        super.onStart();
        manager.onAttach(this);
        getState();
        setChosenMeals();
    }

    public void setChosenMeals() {
        breakfastElementsTextView.setText(getMealsNames(manager.getBreakfastMeals()));
        lunchElementsTextView.setText(getMealsNames(manager.getLunchMeals()));
        dinnerElementsTextView.setText(getMealsNames(manager.getDinnerMeals()));
        teatimeElementsTextView.setText(getMealsNames(manager.getTeatimeMeals()));
        supperElementsTextView.setText(getMealsNames(manager.getSupperMeals()));
    }


    @NonNull
    private String getMealsNames(Vector<Meal> vector) {
        String mealsNames="";
        for(Meal meal : vector){
            mealsNames += meal.getName() + ", ";
        }
        return mealsNames;
    }

    @Override
    protected void onStop() {
        super.onStop();
        manager.onStop();
        setState();
    }

    private void setState() {
        if(!dateEditText.getText().toString().equals("")) {
            //DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
            //try {
                manager.setDailyMenuDate(dateEditText.getText().toString());
            //} catch (ParseException e) {
              //  Log.e(getPackageName(), e.getLocalizedMessage());
            //}
        }
    }

    @OnClick({R.id.saveDailyMenuButton, R.id.cancel_action})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.saveDailyMenuButton:
                save();
                break;
            case R.id.cancel_action:
                cancel();
                break;
        }
    }

    public void hideButtons(){
        saveDailyMenuButton.setVisibility(View.INVISIBLE);
        addMealForBreakfastImageButton.setVisibility(View.INVISIBLE);
        addMealForLunchImageButton.setVisibility(View.INVISIBLE);
        addMealForDinnerImageButton.setVisibility(View.INVISIBLE);
        addMealForTeatimeImageButton.setVisibility(View.INVISIBLE);
        addMealForSupperImageButton.setVisibility(View.INVISIBLE);
    }

    private void cancel() {
        setResult(AddOrEditMenuActivity.RESULT_CODE_CANCEL);
        manager.clearVectorsOfMeals();
        dateEditText.setText("");
        manager.setDailyMenuDate(null);
        finish();
    }

    private void save() {
        if(manager.getBreakfastMeals().size()==0 && manager.getLunchMeals().size()==0
                && manager.getDinnerMeals().size()==0 && manager.getTeatimeMeals().size()==0
                && manager.getSupperMeals().size()==0){
            Toast.makeText(this, "Your menu can't be empty! Choose at least one meal", Toast.LENGTH_LONG).show();
        }else if(dateEditText.length() == 0){
            Toast.makeText(this, "You should type date", Toast.LENGTH_SHORT).show();
        }
        else{
            setState();
            manager.saveDailyMenu();
        }
    }

    @OnClick({R.id.addMealForBreakfastImageButton, R.id.addMealForLunchImageButton, R.id.addMealForDinnerImageButton, R.id.addMealForTeatimeImageButton, R.id.addMealForSupperImageButton})
    public void onAddMealClicked(View view) {
        switch (view.getId()) {
            case R.id.addMealForBreakfastImageButton:
                addMeal(BREAKFAST_KEY);
                break;
            case R.id.addMealForLunchImageButton:
                addMeal(LUNCH_KEY);
                break;
            case R.id.addMealForDinnerImageButton:
                addMeal(DINNER_KEY);
                break;
            case R.id.addMealForTeatimeImageButton:
                addMeal(TEATIME_KEY);
                break;
            case R.id.addMealForSupperImageButton:
                addMeal(SUPPER_KEY);
                break;
        }
    }



    private void addMeal(String mealType){
        Intent intent = new Intent(this, ChooseFromMealsActivity.class);
        intent.putExtra(MEAL_TYPE_KEY, mealType);
        startActivityForResult(intent, REQUEST_CODE_ADD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_ADD){
            // TODO: 05.06.2017
        }
    }

    public void getState() {
        if(manager.getDailyMenuDate() != null) {
            dateEditText.setText(manager.getDailyMenuDate());
        }
    }

    public void saveFailed() {
        Toast.makeText(this, "Error while saving menu", Toast.LENGTH_SHORT).show();
        setResult(AddOrEditMenuActivity.RESULT_CODE_ERROR);
        manager.clearVectorsOfMeals();
        dateEditText.setText("");
        manager.setDailyMenuDate(null);
        finish();
    }

    public void saveSuccess() {
        setResult(AddOrEditMenuActivity.RESULT_CODE_ADDED);
        manager.clearVectorsOfMeals();
        dateEditText.setText("");
        manager.setDailyMenuDate(null);
        finish();
    }
}
