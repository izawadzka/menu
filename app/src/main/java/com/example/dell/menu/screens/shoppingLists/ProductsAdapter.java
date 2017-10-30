package com.example.dell.menu.screens.shoppingLists;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.example.dell.menu.R;
import com.example.dell.menu.StorageType;
import com.example.dell.menu.events.shoppinglists.DeleteProductFromShoppingListEvent;
import com.example.dell.menu.events.shoppinglists.QuantityOfProductChangedEvent;
import com.example.dell.menu.objects.menuplanning.Product;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Dell on 08.06.2017.
 */

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductsInListViewHolder> {
    private final Bus bus;
    List<Product> products = new ArrayList<>();

    public ProductsAdapter(Bus bus) {
        this.bus = bus;
        this.bus.register(this);
    }

    public void quantityUpdatedSuccessfully(ProductsInListViewHolder holder){
        holder.updateSuccess();
    }

    @Override
    public ProductsInListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ProductsInListViewHolder(inflater.inflate(R.layout.item_product_in_shopping_list, parent, false));
    }

    @Override
    public void onBindViewHolder(ProductsInListViewHolder holder, int position) {
        holder.setProduct(products.get(position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void setProducts(List<Product> result) {
        products.clear();
        products.addAll(result);
        notifyDataSetChanged();
    }

    public class ProductsInListViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.productNameTextView)
        TextView productNameTextView;
        @Bind(R.id.clickableQuantityTextView)
        TextView clickableQuantityTextView;
        @Bind(R.id.quantityEditText)
        EditText quantityEditText;
        @Bind(R.id.textView_editText_switcher)
        ViewSwitcher textViewEditTextSwitcher;
        @Bind(R.id.unitTextView)
        TextView unitTextView;
        @Bind(R.id.updateOrAddProductImageButton)
        ImageButton updateQuantityImageButton;
        @Bind(R.id.deleteProductImageButton)
        ImageButton deleteProductImageButton;
        private Product product;

        public ProductsInListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setProduct(Product product) {
            this.product = product;
            productNameTextView.setText(product.getName());

            clickableQuantityTextView.setText(String.valueOf(product.getQuantity()));
            quantityEditText.setText(String.valueOf(product.getQuantity()));

            unitTextView.setText(StorageType.getUnit(product.getStorageType()));

            updateQuantityImageButton.setVisibility(View.INVISIBLE);
        }

        @OnClick(R.id.clickableQuantityTextView)
        public void onQuantityTextViewClicked() {
            textViewEditTextSwitcher.showNext();
            updateQuantityImageButton.setVisibility(View.VISIBLE);
        }

        @OnClick(R.id.updateOrAddProductImageButton)
        public void onUpdateQuantityImageButtonClicked(){
            try {
                double newQuantity = Double.valueOf(quantityEditText.getText().toString());
                if(newQuantity >= 0) {

                    clickableQuantityTextView.setText(String.valueOf(newQuantity));

                    bus.post(new QuantityOfProductChangedEvent(newQuantity, product.getProductId(), this));
                }else quantityEditText.setError("Quantity can't be lower than 0");
            }catch (Exception e){
                quantityEditText.setError("Invalid format");
            }
        }

        @OnClick(R.id.deleteProductImageButton)
        public void onDeleteProductImageButtonClicked() {
            bus.post(new DeleteProductFromShoppingListEvent(product.getProductId()));
        }

        public void updateSuccess() {
            textViewEditTextSwitcher.showPrevious();
            updateQuantityImageButton.setVisibility(View.INVISIBLE);
        }
    }
}
