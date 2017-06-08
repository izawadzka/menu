package com.example.dell.menu.screens.shoppingLists;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.dell.menu.R;
import com.example.dell.menu.StorageType;
import com.example.dell.menu.objects.Product;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Dell on 08.06.2017.
 */

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductsInListViewHolder> {
    List<Product> products = new ArrayList<>();

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

    class ProductsInListViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.productName)
        TextView productName;
        @Bind(R.id.productInMealQuantity)
        TextView productInMealQuantity;
        @Bind(R.id.deleteProductsButton)
        ImageButton deleteProductsButton;

        public ProductsInListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setProduct(Product product) {
            productName.setText(product.getName());
            productInMealQuantity.setText(product.getQuantity() + StorageType.getUnit(product.getStorageType()));
        }
    }
}
