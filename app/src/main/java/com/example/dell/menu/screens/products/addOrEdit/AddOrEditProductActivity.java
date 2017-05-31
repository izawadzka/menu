package com.example.dell.menu.screens.products.addOrEdit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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


    private ArrayAdapter<CharSequence> productTypesaAdapter;
    private ArrayAdapter<CharSequence> storageTypesAdapter;
    private String addedProductType;
    private String storageType;
    private AddOrEditProductManager addOrEditProductManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_edit_product);
        ButterKnife.bind(this);

        addOrEditProductManager = ((App)getApplication()).getAddOrEditProductManager();

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
    }

    @Override
    protected void onStart() {
        super.onStart();
        addOrEditProductManager.onAttach(this);
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
        if(addedProductName.length() < 5){
            Toast.makeText(this, "Name hast to have at least 5 characters", Toast.LENGTH_SHORT).show();
            return;
        }else{
            String productName = addedProductName.getText().toString();
            int numberOfKcal;
            if(addedProductNumbOfKcal.length() == 0){
                 numberOfKcal = 0;
            }else numberOfKcal = Integer.parseInt(addedProductNumbOfKcal.getText().toString());
                addOrEditProductManager.addNewProduct(productName, numberOfKcal, addedProductType, storageType);
        }
    }

    public void addSuccesfull() {
        setResult(ProductsFragment.RESULT_OK);
        finish();
    }

    public void addFailed() {
        setResult(ProductsFragment.RESULT_ERROR);
        finish();
    }
}
