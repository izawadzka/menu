package com.example.dell.menu.menuplanning.screens.products;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.dell.menu.R;
import com.example.dell.menu.menuplanning.types.StorageType;
import com.example.dell.menu.menuplanning.events.products.DeleteProductEvent;
import com.example.dell.menu.menuplanning.events.products.UpdateProductEvent;
import com.example.dell.menu.menuplanning.objects.Product;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Dell on 26.05.2017.
 */

class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductViewHolder> {
    private final Bus bus;
    private ProductClickedListener productClickedListener;
    private List<Product> products = new ArrayList<>();

    ProductsAdapter(Bus bus) {
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

    void setProductClickedListener(ProductClickedListener productClickedListener) {
        this.productClickedListener = productClickedListener;
    }

    private void itemClicked(Product product) {
        if (productClickedListener != null) {
            productClickedListener.productClicked(product);
        }
    }

    public void remove(int adapterPosition) {
        bus.post(new DeleteProductEvent(products.get(adapterPosition).getProductId()));
    }


    class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.productNameTextView)
        TextView productNameTextView;
        @Bind(R.id.caloriesTextView)
        TextView caloriesTextView;
        @Bind(R.id.proteinsTextView)
        TextView proteinsTextView;
        @Bind(R.id.carbonsTextView)
        TextView carbonsTextView;
        @Bind(R.id.fatTextView)
        TextView fatTextView;
        @Bind(R.id.unitLabel)
        TextView unitLabel;

        private final Bus bus;
        private Product product;


        ProductViewHolder(View itemView, Bus bus) {
            super(itemView);
            this.bus = bus;
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void setProduct(Product product) {
            this.product = product;
            productNameTextView.setText(product.getName());
            caloriesTextView.setText(String.format("%s kcal", product.getKcalPer100g_mlOr1Unit()));
            proteinsTextView.setText(String.format("P: %s g", product.getProteinsPer100g_mlOr1Unit()));
            carbonsTextView.setText(String.format("C: %s g", product.getCarbohydratesPer100g_mlOr1Unit()));
            fatTextView.setText(String.format("F: %s g", product.getFatPer100g_mlOr1Unit()));

            if(product.getStorageType().equals(StorageType.ITEM))
                unitLabel.setText("/1"+StorageType.getUnit(product.getStorageType()));
            else unitLabel.setText("/100"+StorageType.getUnit(product.getStorageType()));
        }


        @Override
        public void onClick(View v) {
            itemClicked(product);
        }
    }

    interface ProductClickedListener {
        void productClicked(Product product);
    }
}


