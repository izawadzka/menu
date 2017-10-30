package com.example.dell.menu.screens.menuplanning.products;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.dell.menu.R;
import com.example.dell.menu.events.products.DeleteProductEvent;
import com.example.dell.menu.events.products.UpdateProductEvent;
import com.example.dell.menu.objects.menuplanning.Product;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Dell on 26.05.2017.
 */

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductViewHolder> {
    private final Bus bus;
    private ProductClickedListener productClickedListener;
    private List<Product> products = new ArrayList<>();

    public ProductsAdapter(Bus bus) {
        this.bus = bus;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ProductViewHolder(inflater.inflate(R.layout.item_product, parent, false), bus);
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

    public void setProductClickedListener(ProductClickedListener productClickedListener) {
        this.productClickedListener = productClickedListener;
    }

    void itemClicked(Product product) {
        if (productClickedListener != null) {
            productClickedListener.productClicked(product);
        }
    }


    class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.productNameTextView)
        TextView productNameTextView;
        @Bind(R.id.caloriesTextView)
        TextView caloriesTextView;
        @Bind(R.id.updateOrAddProductImageButton)
        ImageButton editProductImageButton;
        @Bind(R.id.deleteProductImageButton)
        ImageButton deleteProductImageButton;
        @Bind(R.id.proteinsTextView)
        TextView proteinsTextView;
        @Bind(R.id.carbonsTextView)
        TextView carbonsTextView;
        @Bind(R.id.fatTextView)
        TextView fatTextView;

        private final Bus bus;
        private Product product;


        public ProductViewHolder(View itemView, Bus bus) {
            super(itemView);
            this.bus = bus;
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void setProduct(Product product) {
            this.product = product;
            productNameTextView.setText(product.getName());
            caloriesTextView.setText(String.format("%s kcal", product.getNumberOfKcalPer100g()));
            proteinsTextView.setText(String.format("P: %s g", product.getAmountOfProteinsPer100g()));
            carbonsTextView.setText(String.format("C: %s g", product.getAmountOfCarbosPer100g()));
            fatTextView.setText(String.format("F: %s g", product.getAmountOfFatPer100g()));
        }

        @OnClick(R.id.deleteProductImageButton)
        public void onDeleteProductButtonClicked() {
            bus.post(new DeleteProductEvent(product.getProductId()));
        }

        @OnClick(R.id.updateOrAddProductImageButton)
        public void onUpdateProductImageButtonClicked() {
            bus.post(new UpdateProductEvent(product.getProductId()));
        }

        @Override
        public void onClick(View v) {
            itemClicked(product);
        }
    }

    public interface ProductClickedListener {
        void productClicked(Product product);
    }
}


