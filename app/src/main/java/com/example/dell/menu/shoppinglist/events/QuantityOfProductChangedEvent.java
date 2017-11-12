package com.example.dell.menu.shoppinglist.events;

import com.example.dell.menu.shoppinglist.screens.ProductsAdapter;

/**
 * Created by Dell on 02.09.2017.
 */

public class QuantityOfProductChangedEvent {
    public final double quantity;
    public final int productId;
    public final ProductsAdapter.ProductsInListViewHolder holder;

    public QuantityOfProductChangedEvent(double quantity, int productId, ProductsAdapter.ProductsInListViewHolder holder) {
        this.quantity = quantity;
        this.productId = productId;
        this.holder = holder;
    }
}
