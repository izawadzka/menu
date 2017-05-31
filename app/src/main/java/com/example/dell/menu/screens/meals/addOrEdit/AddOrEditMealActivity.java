package com.example.dell.menu.screens.meals.addOrEdit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.dell.menu.R;
import com.example.dell.menu.screens.meals.MealsFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddOrEditMealActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_edit_meal);
        ButterKnife.bind(this);

        addedProductsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AddedProductsAdapter();
        addedProductsRecyclerView.setAdapter(adapter);
    }


    @OnClick({R.id.saveMealButton, R.id.cancel_action})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.saveMealButton:
                break;
            case R.id.cancel_action:
                cancel();
                break;
        }
    }

    private void cancel() {
        setResult(MealsFragment.RESULT_CANCEL);
        finish();
    }
}
