package com.example.dell.menu.screens.products;

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
 * Created by Dell on 26.05.2017.
 */

public class ProductsAdapter extends RecyclerView.Adapter<ProductViewHolder> {
    private List<Product> products = new ArrayList<>();

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ProductViewHolder(inflater.inflate(R.layout.item_product, parent, false));
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        holder.setProduct(products.get(position));
    }

    public void setProducts(List<Product> products) {
        this.products.clear();
        this.products.addAll(products);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

}

class ProductViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.productNameTextView)
    TextView productNameTextView;
    @Bind(R.id.caloriesTextView)
    TextView caloriesTextView;
    @Bind(R.id.editProductImageButton)
    ImageButton editProductImageButton;
    @Bind(R.id.deleteProductImageButton)
    ImageButton deleteProductImageButton;


    public ProductViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setProduct(Product product) {
        productNameTextView.setText(product.getName());
        caloriesTextView.setText(String.format("%s kcal/100g", product.getNumberOfKcalPer100g()));
    }
}
