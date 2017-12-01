package com.example.dell.menu.virtualfridge.screens.shelf;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.example.dell.menu.R;
import com.example.dell.menu.menuplanning.objects.Product;
import com.example.dell.menu.menuplanning.types.StorageType;
import com.example.dell.menu.virtualfridge.events.AddProductClickedEvent;
import com.example.dell.menu.virtualfridge.events.AmountOfProductChangedEvent;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Dell on 29.10.2017.
 */

public class ProductsOnTheShelfMainAdapter extends RecyclerView.Adapter<ProductsOnTheShelfMainAdapter.ProductOnTheShelfViewHolder> {
    public final static int ADD_MODE = 2;
    public final static int SHOW_MODE_EATEN = 3;
    public final static int SHOW_MODE_BOUGHT= 4;

    private List<Product> products = new ArrayList<>();
    private final Bus bus;
    private int currentMode;
    private long shelfId;
    private boolean extraShelf = false;

    public ProductsOnTheShelfMainAdapter(Bus bus, int currentMode) {
        this.bus = bus;
        this.currentMode = currentMode;
        shelfId = -1;
    }

    public ProductsOnTheShelfMainAdapter(Bus bus, int currentMode, long shelfId){
        this.bus = bus;
        this.currentMode = currentMode;
        this.shelfId = shelfId;
    }

    public void setExtraShelf(boolean extraShelf){
        this.extraShelf = extraShelf;
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
        @Bind(R.id.textView_editText_switcher)
        ViewSwitcher textViewEditTextSwitcher;


        private Product product;

        public ProductOnTheShelfViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setProduct(Product product, int currentMode) {
            this.product = product;
            productNameTextView.setText(product.getName());
            quantityUnitTextView.setText(StorageType.getUnit(product.getStorageType()));

            if (currentMode == SHOW_MODE_BOUGHT
                    || currentMode == SHOW_MODE_EATEN) {
                quantityTextView.setText(String.format("%s", product.getQuantity()));
                quantityEditText.setText(String.format("%s", product.getQuantity()));
                updateProductImageButton.setVisibility(View.INVISIBLE);
            } else if (currentMode == ADD_MODE) {
                quantityTextView.setVisibility(View.GONE);
                quantityEditText.setVisibility(View.VISIBLE);
                updateProductImageButton.setImageResource(R.drawable.ic_add_black);
            }
        }

        @OnClick(R.id.quantityTextView)
        public void onQuantityTextViewClicked() {
            textViewEditTextSwitcher.showNext();
            updateProductImageButton.setVisibility(View.VISIBLE);
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
            } else{
                try {
                    double newQuantity = Double.valueOf(quantityEditText.getText().toString());
                    if (newQuantity >= 0) {

                        quantityTextView.setText(String.valueOf(newQuantity));

                        bus.post(new AmountOfProductChangedEvent(product.getProductId(),
                                Double.parseDouble(quantityEditText.getText().toString()),
                                product.getQuantity(), shelfId, product.getProductFlagId(),
                                extraShelf));
                    } else quantityEditText.setError("Quantity can't be lower than 0");
                } catch (Exception e) {
                    quantityEditText.setError("Invalid format");
                }
            }
        }
    }

}
