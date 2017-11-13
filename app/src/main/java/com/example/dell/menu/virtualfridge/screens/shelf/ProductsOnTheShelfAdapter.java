package com.example.dell.menu.virtualfridge.screens.shelf;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.dell.menu.R;
import com.example.dell.menu.menuplanning.types.StorageType;
import com.example.dell.menu.virtualfridge.events.AddProductClickedEvent;
import com.example.dell.menu.virtualfridge.events.AmountOfProductChangedEvent;
import com.example.dell.menu.virtualfridge.events.DeleteProductFromFridgeEvent;
import com.example.dell.menu.menuplanning.objects.Product;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Dell on 29.10.2017.
 */

public class ProductsOnTheShelfAdapter extends RecyclerView.Adapter<ProductsOnTheShelfAdapter.ProductOnTheShelfViewHolder> {
    public final static int SHOW_MODE = 1;
    public final static int ADD_MODE = 2;
    public static final int EDIT_MODE = 3;


    private List<Product> products = new ArrayList<>();
    private final Bus bus;
    private int currentMode;

    public ProductsOnTheShelfAdapter(Bus bus, int currentMode) {
        this.bus = bus;
        this.currentMode = currentMode;
    }

    @Override
    public ProductOnTheShelfViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ProductOnTheShelfViewHolder(inflater.inflate(R.layout.item_product_on_the_shelf, parent, false));
    }

    @Override
    public void onBindViewHolder(ProductOnTheShelfViewHolder holder, int position) {
        holder.setProduct(products.get(position), currentMode);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void setProducts(List<Product> products) {
        this.products.clear();
        this.products.addAll(products);
        notifyDataSetChanged();
    }


    class ProductOnTheShelfViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.productNameTextView)
        TextView productNameTextView;
        @Bind(R.id.quantityTextView)
        TextView quantityTextView;
        @Bind(R.id.quantityEditText)
        EditText quantityEditText;
        @Bind(R.id.quantityUnitTextView)
        TextView quantityUnitTextView;
        @Bind(R.id.updateOrAddProductImageButton)
        ImageButton updateProductImageButton;
        @Bind(R.id.deleteProductImageButton)
        ImageButton deleteProductImageButton;
        @Bind(R.id.blockedAmountLabel)
        TextView blockedAmountLabel;

        private Product product;

        public ProductOnTheShelfViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setProduct(Product product, int currentMode) {
            this.product = product;
            productNameTextView.setText(product.getName());
            quantityUnitTextView.setText(StorageType.getUnit(product.getStorageType()));

            if (currentMode == SHOW_MODE) {
                quantityTextView.setText(String.format("%s", product.getQuantity()));
                blockedAmountLabel.setText(String.format("(including %s %s blocked)",
                        product.getBlockedAmount(), StorageType.getUnit(product.getStorageType())));
            } else if (currentMode == ADD_MODE) {
                quantityTextView.setVisibility(View.GONE);
                quantityEditText.setVisibility(View.VISIBLE);
                deleteProductImageButton.setVisibility(View.INVISIBLE);
                blockedAmountLabel.setVisibility(View.GONE);
                updateProductImageButton.setImageResource(R.drawable.ic_add);
            }
        }


        @OnClick(R.id.deleteProductImageButton)
        public void onDeleteProductClicked() {
            bus.post(new DeleteProductFromFridgeEvent(product.getProductId()));
        }

        @OnClick(R.id.updateOrAddProductImageButton)
        public void onUpdateOrAddProductClicked() {
            if (currentMode == ADD_MODE) {
                if (quantityEditText.getText().length() > 0) {
                    bus.post(new AddProductClickedEvent(product.getProductId(),
                            Double.parseDouble(quantityEditText.getText().toString()),
                            product.getName()));
                    quantityEditText.setText("");
                } else quantityEditText.setError("You must type quantity");
            } else if (currentMode == SHOW_MODE) {
                currentMode = EDIT_MODE;
                quantityTextView.setVisibility(View.GONE);

                quantityEditText.setVisibility(View.VISIBLE);
                quantityEditText.setText(String.valueOf(product.getQuantity()));

                updateProductImageButton.setImageResource(R.drawable.ic_save);
            } else if (currentMode == EDIT_MODE) {
                if (quantityEditText.getText().length() > 0) {
                    currentMode = SHOW_MODE;

                    if (Double.parseDouble(quantityEditText.getText().toString()) != product.getQuantity())
                        bus.post(new AmountOfProductChangedEvent(product.getProductId(),
                                Double.parseDouble(quantityEditText.getText().toString())));

                    updateProductImageButton.setImageResource(R.drawable.ic_edit);
                    quantityTextView.setText(quantityEditText.getText().toString());
                    quantityTextView.setVisibility(View.VISIBLE);
                    quantityEditText.setVisibility(View.GONE);
                } else quantityEditText.setError("You must type quantity");
            }
        }
    }

}