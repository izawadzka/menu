package com.example.dell.menu.screens.meals.addOrEdit;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.dell.menu.R;
import com.example.dell.menu.events.meals.DeleteProductFromMealEvent;
import com.example.dell.menu.objects.Product;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Dell on 29.05.2017.
 */

public class AddedProductsAdapter extends RecyclerView.Adapter<AddedProductsAdapter.AddedProductsViewHolder> {
    private final Bus bus;
    List<Product> products = new ArrayList<>();
    private AddedProductsViewHolder holder;


    public AddedProductsAdapter(Bus bus){
        this.bus = bus;
    }

    @Override
    public AddedProductsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new AddedProductsViewHolder(inflater.inflate(R.layout.item_added_product, parent, false), bus);
    }

    @Override
    public void onBindViewHolder(AddedProductsViewHolder holder, int position) {
        this.holder = holder;
        holder.setProduct(products.get(position));
    }

    public void deleteFromAddedProducts(Product product){
        holder.deleteProduct(product);
    }

    public void setProducts(List<Product> products) {
        this.products.clear();
        this.products.addAll(products);
        notifyDataSetChanged();
    }


    public void addProduct(Product product){
        this.products.add(product);
    }


    @Override
    public int getItemCount() {
        return products.size();
    }

    class AddedProductsViewHolder extends RecyclerView.ViewHolder {
        private final Bus bus;
        @Bind(R.id.productInMealName)
        TextView productInMealName;
        @Bind(R.id.productInMealQuantity)
        TextView productInMealQuantity;
        @Bind(R.id.deleteFromMealButton)
        ImageButton deleteFromMealButton;

        private Product product;

        public AddedProductsViewHolder(View itemView, Bus bus) {
            super(itemView);
            this.bus = bus;
            ButterKnife.bind(this, itemView);
        }

        public void setProduct(Product product) {
            this.product = product;
            productInMealName.setText(product.getName());
            productInMealQuantity.setText(String.valueOf(product.getQuantity()));
        }


        @OnClick(R.id.deleteFromMealButton)
        public void onDeleteProductButtonClicked() {
            products.remove(product);
            notifyDataSetChanged();
            bus.post(new DeleteProductFromMealEvent(product));
        }

        public void deleteProduct(Product product) {

        }
    }

}
