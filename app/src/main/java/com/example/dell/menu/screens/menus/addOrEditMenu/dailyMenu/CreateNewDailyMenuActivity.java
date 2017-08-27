package com.example.dell.menu.screens.menus.addOrEditMenu.dailyMenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.ExtractedText;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;
import com.example.dell.menu.App;
import com.example.dell.menu.R;
import com.example.dell.menu.colors.ColorsBase;
import com.example.dell.menu.objects.DailyMenu;
import com.example.dell.menu.objects.Meal;
import com.example.dell.menu.screens.menus.addOrEditMenu.dailyMenu.chooseMeals.ChooseFromMealsActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Vector;
import java.util.regex.Pattern;

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
    @Bind(R.id.breakfastTags)
    TagView breakfastTags;
    @Bind(R.id.addMealForLunchImageButton)
    ImageButton addMealForLunchImageButton;
    @Bind(R.id.lunchTags)
    TagView lunchTags;
    @Bind(R.id.addMealForDinnerImageButton)
    ImageButton addMealForDinnerImageButton;
    @Bind(R.id.dinnerTags)
    TagView dinnerTags;
    @Bind(R.id.addMealForTeatimeImageButton)
    ImageButton addMealForTeatimeImageButton;
    @Bind(R.id.teatimeTags)
    TagView teatimeTags;
    @Bind(R.id.addMealForSupperImageButton)
    ImageButton addMealForSupperImageButton;
    @Bind(R.id.supperTags)
    TagView supperTags;
    @Bind(R.id.saveDailyMenuButton)
    Button saveDailyMenuButton;
    @Bind(R.id.cancel_action)
    Button cancelAction;
    @Bind(R.id.kcalLabelTextView)
    TextView kcalLabelTextView;
    @Bind(R.id.kcalTextView)
    TextView kcalTextView;


    private CreateNewDailyMenuManager manager;
    private boolean editMode;

    ArrayList<Tag> breakfastTagList = new ArrayList<>();
    ArrayList<Tag> lunchTagList = new ArrayList<>();
    ArrayList<Tag> dinnerTagList = new ArrayList<>();
    ArrayList<Tag> teatimeTagList = new ArrayList<>();
    ArrayList<Tag> supperTagList = new ArrayList<>();
    private DailyMenu currentDailyMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_daily_menu);
        ButterKnife.bind(this);
        manager = ((App) getApplication()).getCreateNewDailyMenuManager();

        Intent intent = getIntent();
        editMode = intent.getBooleanExtra(DailyMenuFragment.EDIT_MODE_KEY, false);        //check the mode in which the activity is being open
        if (editMode) {
            currentDailyMenu = (DailyMenu) intent.getSerializableExtra(DailyMenuFragment.DAILY_MENU_KEY);

            if (currentDailyMenu != null) {
                manager.setCurrentDailyMenu(currentDailyMenu);
                manager.setCumulativeNumberOfKcal(currentDailyMenu.getCumulativeNumberOfKcal());
                if (currentDailyMenu.getDailyMenuId() == -1) {
                    Log.e(getClass().getSimpleName(), "An error occurred while passing a daily menu");
                    finish();
                }
            }
        }

        setOnTagDeleteListeners();
    }

    private void setOnTagDeleteListeners() {
        setOnTagDeleteListener(breakfastTags, BREAKFAST_KEY);
        setOnTagDeleteListener(lunchTags, LUNCH_KEY);
        setOnTagDeleteListener(dinnerTags, DINNER_KEY);
        setOnTagDeleteListener(teatimeTags, TEATIME_KEY);
        setOnTagDeleteListener(supperTags, SUPPER_KEY);
    }

    private void setOnTagDeleteListener(final TagView tagView, final String mealType) {
        tagView.setOnTagDeleteListener(new TagView.OnTagDeleteListener() {

            @Override
            public void onTagDeleted(final TagView view, final Tag tag, final int position) {
                view.remove(position);
                manager.removeMeal(position, mealType);
            }
        });
    }

    private void makeAStatement(String statement, int duration) {
        Toast.makeText(this, statement, duration).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        manager.onAttach(this);

        if (manager.getCurrentDailyMenu() == null) manager.setCurrentDailyMenu(currentDailyMenu);

        getState();

        if (editMode) setEditMode();
        else setCreateMode();

        setChosenMeals();
    }

    private void setCreateMode() {
        dateEditText.setInputType(InputType.TYPE_NULL);
    }

    private void setEditMode() {
        dateEditText.setInputType(InputType.TYPE_CLASS_DATETIME);
        dateEditText.setText(currentDailyMenu.getDate());
        saveDailyMenuButton.setText("save changes");
        if (!manager.isEditMode()) {
            manager.setEditMode(true);
            manager.setDailyMenuMeals(currentDailyMenu);
        }

        setTitle("Edit daily menu");
    }

    public void setChosenMeals() {
        setTags(breakfastTags, manager.getBreakfastMeals(), breakfastTagList);
        setTags(lunchTags, manager.getLunchMeals(), lunchTagList);
        setTags(dinnerTags, manager.getDinnerMeals(), dinnerTagList);
        setTags(teatimeTags, manager.getTeatimeMeals(), teatimeTagList);
        setTags(supperTags, manager.getSupperMeals(), supperTagList);
    }


    private void setTags(TagView tagView, Vector<Meal> meals, ArrayList<Tag> tags) {
        tags.clear();

        for (int i = 0; i < meals.size(); i++) {
            tags.add(new Tag(meals.get(i).getName()));
            tags.get(i).layoutColor = ColorsBase.getRandomColor();
            tags.get(i).isDeletable = true;
        }
        tagView.addTags(tags);
    }

    @Override
    protected void onStop() {
        super.onStop();
        manager.onStop();
        setState();
    }

    private void setState() {
        String date = dateEditText.getText().toString();
        if (!date.equals("")){
            manager.setDailyMenuDate(dateEditText.getText().toString());
        }

    }

    private boolean isCorrectDate(String date) {
        Pattern pattern = Pattern.compile("\\d{4}-[0-1]\\d-[0-3]\\d");
        if(pattern.matcher(date).matches()){
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                dateFormat.setLenient(false);
                dateFormat.parse(date);
                return true;
            } catch (ParseException e) {
                return false;
            }
        }else return false;
    }

    @OnClick({R.id.saveDailyMenuButton, R.id.cancel_action})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.saveDailyMenuButton:
                if (editMode) updateDailyMenu();
                else save();
                break;
            case R.id.cancel_action:
                cancel();
                break;
        }
    }

    private void updateDailyMenu() {
        if(validateUsersInput()) {
            setState();
            manager.updateDailyMenu();
        }
    }

    private void cancel() {
       finishWorkOfActivity();
    }

    private boolean validateUsersInput() {
        boolean hasErrors = false;
        if (manager.getBreakfastMeals().size() == 0 && manager.getLunchMeals().size() == 0
                && manager.getDinnerMeals().size() == 0 && manager.getTeatimeMeals().size() == 0
                && manager.getSupperMeals().size() == 0) {
            Toast.makeText(this, "Your menu can't be empty! Choose at least one meal", Toast.LENGTH_LONG).show();
            hasErrors = true;
        } else if (dateEditText.length() == 0) {
            dateEditText.setError("You should type date");
            hasErrors = true;
        } else if (!isCorrectDate(dateEditText.getText().toString())) {
            dateEditText.setError("Date is not correct");
            hasErrors = true;
        }

        return !hasErrors;
    }

    private void save() {
        if(validateUsersInput()){
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


    private void addMeal(String mealType) {
        Intent intent = new Intent(this, ChooseFromMealsActivity.class);
        intent.putExtra(MEAL_TYPE_KEY, mealType);
        startActivityForResult(intent, REQUEST_CODE_ADD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ADD) {
            // TODO: 05.06.2017
        }
    }

    public void getState() {
        if (manager.getDailyMenuDate() != null) {
            dateEditText.setText(manager.getDailyMenuDate());
        }
        kcalTextView.setText(String.valueOf(manager.getCumulativeNumberOfKcal()) + "kcal");

        if (!editMode && manager.isEditMode()) editMode = true;
        if (currentDailyMenu == null) currentDailyMenu = manager.getCurrentDailyMenu();
    }

    public void updateCalories(){
        kcalTextView.setText(String.valueOf(manager.getCumulativeNumberOfKcal()) + "kcal");
    }

    public void saveFailed() {
        makeAStatement("Error while saving menu", Toast.LENGTH_SHORT);
        finishWorkOfActivity();
    }

    public void saveSuccess() {
        makeAStatement("Saving new daily menu in process", Toast.LENGTH_SHORT);
        finishWorkOfActivity();
    }

    public void updateSuccess() {
        makeAStatement("Updating new daily menu in process", Toast.LENGTH_SHORT);
        finishWorkOfActivity();
    }

    private void finishWorkOfActivity() {
        manager.clearVectorsOfMeals();
        dateEditText.setText("");
        manager.setDailyMenuDate(null);
        manager.setEditMode(false);
        manager.setCurrentDailyMenu(null);
        manager.setCumulativeNumberOfKcal(0);
        finish();
    }

    public void updateFailed() {
        makeAStatement("An error occurred while trying to update the daily menu", Toast.LENGTH_SHORT);
        finishWorkOfActivity();
    }
}
