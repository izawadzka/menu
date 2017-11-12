package com.example.dell.menu.menuplanning.screens.products.addOrEdit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.menu.App;
import com.example.dell.menu.R;
import com.example.dell.menu.menuplanning.types.StorageType;
import com.example.dell.menu.menuplanning.objects.Product;
import com.example.dell.menu.menuplanning.screens.products.ProductsFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddOrEditProductActivity extends AppCompatActivity {

    @Bind(R.id.addedProductName)
    EditText addedProductName;
    @Bind(R.id.addedProductNumbOfKcal)
    EditText addedProductNumbOfKcal;
    @Bind(R.id.addedProductTypes)
    Spinner addedProductTypes;
    @Bind(R.id.addedProductStorageTypes)
    Spinner addedProductStorageTypes;
    @Bind(R.id.saveProductButton)
    Button saveProductButton;
    @Bind(R.id.cancelButton)
    Button cancelButton;
    @Bind(R.id.addedProductNumberOfProtein)
    EditText addedProductNumberOfProtein;
    @Bind(R.id.addedProductAmountOfCarbos)
    EditText addedProductAmountOfCarbos;
    @Bind(R.id.addedProductAmountOfFat)
    EditText addedProductAmountOfFat;
    @Bind(R.id.activity_add_or_edit_product)
    LinearLayout activityAddOrEditProduct;
    @Bind(R.id.addedProductNumberOfKcalTextView)
    TextView addedProductNumberOfKcalTextView;
    @Bind(R.id.addedProductNumberOfProteinTextView)
    TextView addedProductNumberOfProteinTextView;
    @Bind(R.id.addedProductAmountOfCarbosTextView)
    TextView addedProductAmountOfCarbosTextView;
    @Bind(R.id.addedProductAmountOfFatTextView)
    TextView addedProductAmountOfFatTextView;
    @Bind(R.id.kcalRelativeLayout)
    RelativeLayout kcalRelativeLayout;
    @Bind(R.id.carbonsRelativeLayout)
    RelativeLayout carbonsRelativeLayout;
    @Bind(R.id.kcalUnitTextView)
    TextView kcalUnitTextView;
    @Bind(R.id.proteinUnitTextView)
    TextView proteinUnitTextView;
    @Bind(R.id.carbonsUnitTextView)
    TextView carbonsUnitTextView;
    @Bind(R.id.fatUnitTextView)
    TextView fatUnitTextView;


    private ArrayAdapter<CharSequence> productTypesaAdapter;
    private ArrayAdapter<CharSequence> storageTypesAdapter;
    private String addedProductType;
    private String storageType;
    private AddOrEditProductManager addOrEditProductManager;
    private boolean edit_mode;
    private boolean show_mode;
    private int productToEditId;
    private int productToShowId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_edit_product);
        ButterKnife.bind(this);

        setTitle("Product");

        addOrEditProductManager = ((App) getApplication()).getAddOrEditProductManager();

        productTypesaAdapter = ArrayAdapter.createFromResource(this, R.array.productTypes, android.R.layout.simple_spinner_item);
        productTypesaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addedProductTypes.setAdapter(productTypesaAdapter);
        addedProductTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                addedProductType = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        storageTypesAdapter = ArrayAdapter.createFromResource(this, R.array.storageTypes, android.R.layout.simple_spinner_item);
        storageTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addedProductStorageTypes.setAdapter(storageTypesAdapter);
        addedProductStorageTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                storageType = (String) parent.getItemAtPosition(position);

                int defaultAmount;
                if (storageType.equals(StorageType.ITEM)) defaultAmount = 1;
                else defaultAmount = 100;

                kcalUnitTextView.setText("kcal/" + defaultAmount + StorageType.getUnit(storageType));
                String unitValue = String.format("g/" + defaultAmount + StorageType.getUnit(storageType));
                proteinUnitTextView.setText(unitValue);
                carbonsUnitTextView.setText(unitValue);
                fatUnitTextView.setText(unitValue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        Intent intent = getIntent();
        if (intent.getBooleanExtra(ProductsFragment.EDIT_MODE_KEY, false)) {
            productToEditId = intent.getIntExtra(ProductsFragment.PRODUCT_ID_KEY, -1);
            if (productToEditId != -1) edit_mode = true;
            else {
                setResult(ProductsFragment.RESULT_ERROR);
                finish();
            }
        } else if (intent.getBooleanExtra(ProductsFragment.SHOW_MODE_KEY, false)) {
            productToShowId = intent.getIntExtra(ProductsFragment.PRODUCT_ID_KEY, -1);
            if (productToShowId != -1) show_mode = true;
            else {
                setResult(ProductsFragment.RESULT_ERROR);
                finish();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        addOrEditProductManager.onAttach(this);
        addOrEditProductManager.setEditMode(edit_mode, productToEditId);
        if (show_mode) prepareShowMode();
        addOrEditProductManager.setShowMode(show_mode, productToShowId);
    }

    private void prepareShowMode() {
        saveProductButton.setVisibility(View.INVISIBLE);

        addedProductName.setVisibility(View.GONE);

        addedProductNumbOfKcal.setVisibility(View.INVISIBLE);
        addedProductNumberOfKcalTextView.setVisibility(View.VISIBLE);

        addedProductNumberOfProtein.setVisibility(View.INVISIBLE);
        addedProductNumberOfProteinTextView.setVisibility(View.VISIBLE);

        addedProductAmountOfCarbos.setVisibility(View.INVISIBLE);
        addedProductAmountOfCarbosTextView.setVisibility(View.VISIBLE);

        addedProductAmountOfFat.setVisibility(View.INVISIBLE);
        addedProductAmountOfFatTextView.setVisibility(View.VISIBLE);

        addedProductTypes.setEnabled(false);
        addedProductStorageTypes.setEnabled(false);

        kcalRelativeLayout.setBackgroundResource(R.color.lighterAdapterColor);
        carbonsRelativeLayout.setBackgroundResource(R.color.lighterAdapterColor);
    }

    @Override
    protected void onStop() {
        super.onStop();
        addOrEditProductManager.onStop();
    }

    @OnClick({R.id.saveProductButton, R.id.cancelButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.saveProductButton:
                saveProduct();
                break;
            case R.id.cancelButton:
                cancel();
                break;
        }
    }

    private void cancel() {
        setResult(ProductsFragment.RESULT_CANCEL);
        finish();
    }

    private void saveProduct() {
        saveProductButton.setEnabled(false);
        if (addedProductName.length() < 5) {
            Toast.makeText(this, "Name hast to have at least 5 characters", Toast.LENGTH_SHORT).show();
            saveProductButton.setEnabled(true);
        } else {
            String productName = addedProductName.getText().toString();

            int amountOfKcal, amountOfProteins, amountOfCarbos, amountOfFat;
            if (addedProductNumbOfKcal.length() == 0) amountOfKcal = 0;
            else amountOfKcal = Integer.parseInt(addedProductNumbOfKcal.getText().toString());

            if (addedProductNumberOfProtein.length() == 0) amountOfProteins = 0;
            else
                amountOfProteins = Integer.parseInt(addedProductNumberOfProtein.getText().toString());

            if (addedProductAmountOfCarbos.length() == 0) amountOfCarbos = 0;
            else amountOfCarbos = Integer.parseInt(addedProductAmountOfCarbos.getText().toString());

            if (addedProductAmountOfFat.length() == 0) amountOfFat = 0;
            else amountOfFat = Integer.parseInt(addedProductAmountOfFat.getText().toString());


            if (edit_mode)
                addOrEditProductManager.editProduct(productName, amountOfKcal, addedProductType,
                        storageType, amountOfProteins, amountOfCarbos, amountOfFat);

            else
                addOrEditProductManager.addNewProduct(productName, amountOfKcal, addedProductType, storageType, amountOfProteins, amountOfCarbos, amountOfFat);
        }
    }

    public void addSuccessful() {
        setResult(ProductsFragment.RESULT_OK);
        finish();
    }

    public void addFailed() {
        setResult(ProductsFragment.RESULT_ERROR);
        finish();
    }

    public void loadingProductFailed() {
        Toast.makeText(this, "An error occurred while an attempt to load product to edit", Toast.LENGTH_LONG).show();
        setResult(ProductsFragment.RESULT_ERROR);
        finish();
    }

    public void loadingProductSuccess(Product product) {
        if (show_mode) {
            setTitle(product.getName());
            addedProductNumberOfKcalTextView.setText(String.valueOf(product.getKcalPer100g_mlOr1Unit()));
            addedProductNumberOfProteinTextView.setText(String.
                    valueOf(product.getProteinsPer100g_mlOr1Unit()));
            addedProductAmountOfCarbosTextView.setText(String
                    .valueOf(product.getCarbohydratesPer100g_mlOr1Unit()));
            addedProductAmountOfFatTextView.setText(String.valueOf(product.getFatPer100g_mlOr1Unit()));
        } else {
            addedProductName.setText(product.getName());
            addedProductNumbOfKcal.setText(String.valueOf(product.getKcalPer100g_mlOr1Unit()));
            addedProductNumberOfProtein.setText(String.valueOf(product.getProteinsPer100g_mlOr1Unit()));
            addedProductAmountOfCarbos.setText(String.valueOf(product.getCarbohydratesPer100g_mlOr1Unit()));
            addedProductAmountOfFat.setText(String.valueOf(product.getFatPer100g_mlOr1Unit()));
        }

        addedProductTypes.setSelection(productTypesaAdapter.getPosition(product.getType()));
        addedProductStorageTypes.setSelection(storageTypesAdapter.getPosition(product.getStorageType()));

        int defaultAmount;
        if (product.getStorageType().equals(StorageType.ITEM)) defaultAmount = 1;
        else defaultAmount = 100;

        kcalUnitTextView.setText("kcal/" + defaultAmount + StorageType.getUnit(product.getStorageType()));
        String unitValue = String.format("g/" + defaultAmount + StorageType.getUnit(product.getStorageType()));
        proteinUnitTextView.setText(unitValue);
        carbonsUnitTextView.setText(unitValue);
        fatUnitTextView.setText(unitValue);
    }

    public void editingSuccess() {
        setResult(ProductsFragment.RESULT_OK);
        finish();
    }

    public void editingFailed() {
        setResult(ProductsFragment.RESULT_ERROR);
        finish();
    }

}
