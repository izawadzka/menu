package com.example.dell.menu.screens.menuplanning.meals.extendedMealInformation;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dell.menu.R;
import com.example.dell.menu.StorageType;
import com.example.dell.menu.objects.menuplanning.Product;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Dell on 28.05.2017.
 */

public class FullMealInformationAdapter extends RecyclerView.Adapter<FullMealInfViewHolder> {
    List<Product> products = new ArrayList<>();

    @Override
    public FullMealInfViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new FullMealInfViewHolder(inflater.inflate(R.layout.item_full_product, parent, false));
    }

    @Override
    public void onBindViewHolder(FullMealInfViewHolder holder, int position) {
        holder.setProducts(products.get(position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }


    public void setProducts(List<Product> result){
        this.products.clear();
        this.products.addAll(result);
        notifyDataSetChanged();
    }
}

class FullMealInfViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.productName)
    TextView productName;
    @Bind(R.id.productQuantity)
    TextView productQuantity;
    public FullMealInfViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    public void setProducts(Product products) {
        productName.setText(products.getName());
        productQuantity.setText(String.valueOf(products.getQuantity() + StorageType.getUnit(products.getStorageType())));
    }
}
