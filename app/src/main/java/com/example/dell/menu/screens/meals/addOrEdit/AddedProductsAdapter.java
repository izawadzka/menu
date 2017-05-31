package com.example.dell.menu.screens.meals.addOrEdit;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.dell.menu.R;
import com.example.dell.menu.objects.Product;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Dell on 29.05.2017.
 */

public class AddedProductsAdapter extends RecyclerView.Adapter<AddedProductsAdapter.AddedProductsViewHolder> {
    List<Product> products = new ArrayList<>();


    @Override
    public AddedProductsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new AddedProductsViewHolder(inflater.inflate(R.layout.item_added_product, parent, false));
    }

    @Override
    public void onBindViewHolder(AddedProductsViewHolder holder, int position) {
        holder.setProduct(products.get(position));
    }

    public void setProducts(List<Product> products) {
        this.products.clear();
        this.products.addAll(products);
        notifyDataSetChanged();
    }

    public void addProduct(Product product) {
        products.add(product);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class AddedProductsViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.productInMealName)
        TextView productInMealName;
        @Bind(R.id.productInMealQuantity)
        TextView productInMealQuantity;
        @Bind(R.id.deleteFromMealButton)
        ImageButton deleteFromMealButton;

        private Product product;

        public AddedProductsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setProduct(Product product) {
            productInMealName.setText(product.getName());
            productInMealQuantity.setText(String.valueOf(product.getQuantity()));
        }
    }

}
