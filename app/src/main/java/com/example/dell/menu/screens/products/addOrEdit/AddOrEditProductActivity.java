package com.example.dell.menu.screens.products.addOrEdit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.dell.menu.App;
import com.example.dell.menu.R;
import com.example.dell.menu.objects.Product;
import com.example.dell.menu.screens.products.ProductsFragment;

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
        addedProductName.setInputType(InputType.TYPE_NULL);
        addedProductNumbOfKcal.setInputType(InputType.TYPE_NULL);
        addedProductNumberOfProtein.setInputType(InputType.TYPE_NULL);
        addedProductAmountOfCarbos.setInputType(InputType.TYPE_NULL);
        addedProductAmountOfFat.setInputType(InputType.TYPE_NULL);
        addedProductTypes.setEnabled(false);
        addedProductStorageTypes.setEnabled(false);
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
        if (addedProductName.length() < 5) {
            Toast.makeText(this, "Name hast to have at least 5 characters", Toast.LENGTH_SHORT).show();
            return;
        } else {
            String productName = addedProductName.getText().toString();

            int amountOfKcal, amountOfProteins, amountOfCarbos, amountOfFat;
            if (addedProductNumbOfKcal.length() == 0) amountOfKcal = 0;
            else amountOfKcal = Integer.parseInt(addedProductNumbOfKcal.getText().toString());

            if(addedProductNumberOfProtein.length() == 0) amountOfProteins = 0;
            else amountOfProteins = Integer.parseInt(addedProductNumberOfProtein.getText().toString());

            if(addedProductAmountOfCarbos.length() == 0) amountOfCarbos = 0;
            else amountOfCarbos = Integer.parseInt(addedProductAmountOfCarbos.getText().toString());

            if(addedProductAmountOfFat.length() == 0) amountOfFat = 0;
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
        addedProductName.setText(product.getName());
        addedProductNumbOfKcal.setText(String.valueOf(product.getNumberOfKcalPer100g()));
        addedProductNumberOfProtein.setText(String.valueOf(product.getAmountOfProteinsPer100g()));
        addedProductAmountOfCarbos.setText(String.valueOf(product.getAmountOfCarbosPer100g()));
        addedProductAmountOfFat.setText(String.valueOf(product.getAmountOfFatPer100g()));
        addedProductTypes.setSelection(productTypesaAdapter.getPosition(product.getType()));
        addedProductStorageTypes.setSelection(storageTypesAdapter.getPosition(product.getStorageType()));
        if(show_mode) setTitle(product.getName());
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
