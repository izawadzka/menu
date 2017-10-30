package com.example.dell.menu.screens.menuplanning.meals.addOrEdit;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.dell.menu.R;
import com.example.dell.menu.StorageType;
import com.example.dell.menu.events.meals.AddProductToIngredientsEvent;
import com.example.dell.menu.events.meals.QuantityWasntTypedEvent;
import com.example.dell.menu.objects.menuplanning.Product;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Dell on 01.06.2017.
 */

public class ProductToChooseAdapter extends RecyclerView.Adapter<ProductsToChooseViewHolder> {

    private final Bus bus;
    List<Product> products = new ArrayList<>();
    List<Product> alreadyAddedProducts = new ArrayList<>();


    public ProductToChooseAdapter(Bus bus) {
        this.bus = bus;
    }

    @Override
    public ProductsToChooseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ProductsToChooseViewHolder(inflater.inflate(R.layout.item_product_in_adding_recyclerview, parent, false), bus);
    }

    @Override
    public void onBindViewHolder(ProductsToChooseViewHolder holder, int position) {
        holder.setProduct(products.get(position), alreadyAddedProducts);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void setProducts(List<Product> result, List<Product> listOfAlreadyAddedProducts) {
        products.clear();
        products.addAll(result);
        alreadyAddedProducts.clear();
        alreadyAddedProducts.addAll(listOfAlreadyAddedProducts);
        notifyDataSetChanged();
    }
}

class ProductsToChooseViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.productName)
    TextView productName;
    @Bind(R.id.addProductToIngredientsButton)
    ImageButton addProductButton;
    @Bind(R.id.quantityEditText)
    EditText quantityEditText;
    @Bind(R.id.quantityUnitTextView)
    TextView quantityUnitTextView;
    @Bind(R.id.caloriesTextView)
    TextView caloriesTextView;
    @Bind(R.id.proteinsTextView)
    TextView proteinsTextView;
    @Bind(R.id.carbonsTextView)
    TextView carbonsTextView;
    @Bind(R.id.fatTextView)
    TextView fatTextView;
    @Bind(R.id.alreadyAddedAmount)
    TextView alreadyAddedAmount;

    private final Bus bus;
    private Product product;

    public ProductsToChooseViewHolder(View itemView, Bus bus) {
        super(itemView);
        this.bus = bus;
        ButterKnife.bind(this, itemView);
    }

    public void setProduct(Product product, List<Product> alreadyAddedProducts) {
        this.product = product;
        productName.setText(product.getName());
        quantityUnitTextView.setText(StorageType.getUnit(product.getStorageType()));
        caloriesTextView.setText(String.format("%s kcal", product.getNumberOfKcalPer100g()));
        proteinsTextView.setText(String.format("P: %s g", product.getAmountOfProteinsPer100g()));
        carbonsTextView.setText(String.format("C: %s g", product.getAmountOfCarbosPer100g()));
        fatTextView.setText(String.format("F: %s g", product.getAmountOfFatPer100g()));

        alreadyAddedAmount.setText("("+String.valueOf(alreadyAdded(alreadyAddedProducts))+
                StorageType.getUnit(product.getStorageType())+ ")");
    }

    private double alreadyAdded(List<Product> addedProducts) {
        for (Product addedProduct : addedProducts) {
            if(addedProduct.getProductId() == product.getProductId()) return addedProduct.getQuantity();
        }
        return 0;
    }

    @OnClick(R.id.addProductToIngredientsButton)
    public void onAddProductButtonClicked() {
        if (quantityEditText.length() > 0) {
            product.setQuantity(Double.parseDouble(quantityEditText.getText().toString()));
            quantityEditText.getText().clear();
            bus.post(new AddProductToIngredientsEvent(product));
        } else {
            bus.post(new QuantityWasntTypedEvent());
        }
    }
}
