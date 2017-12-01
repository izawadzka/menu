package com.example.dell.menu.virtualfridge.screens.shelf;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.dell.menu.R;
import com.example.dell.menu.menuplanning.objects.Product;
import com.example.dell.menu.menuplanning.types.StorageType;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Dell on 30.11.2017.
 */

public class ProductsOnTheShelfAdditionalAdapter extends RecyclerView.Adapter<ProductsOnTheShelfAdditionalAdapter.ProductOnTheShelfViewHolder> {
    private List<Product> products = new ArrayList<>();
    private final Bus bus;

    public ProductsOnTheShelfAdditionalAdapter(Bus bus) {
        this.bus = bus;
    }

    @Override
    public ProductOnTheShelfViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ProductOnTheShelfViewHolder(inflater.inflate(R.layout.item_product_on_the_shelf, parent, false));
    }

    @Override
    public void onBindViewHolder(ProductOnTheShelfViewHolder holder, int position) {
        holder.setProduct(products.get(position));
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

    public class ProductOnTheShelfViewHolder extends RecyclerView.ViewHolder {
        private Product product;
        @Bind(R.id.productNameTextView)
        TextView productNameTextView;
        @Bind(R.id.quantityTextView)
        TextView quantityTextView;
        @Bind(R.id.quantityUnitTextView)
        TextView quantityUnitTextView;
        @Bind(R.id.updateOrAddProductImageButton)
        ImageButton updateOrAddProductImageButton;

        public ProductOnTheShelfViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
        }

        public void setProduct(Product product) {
            this.product = product;
            updateOrAddProductImageButton.setVisibility(View.GONE);
            productNameTextView.setText(product.getName());
            quantityUnitTextView.setText(StorageType.getUnit(product.getStorageType()));
            quantityTextView.setText(String.format("%s", product.getQuantity()));
        }
    }
}
