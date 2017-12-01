package com.example.dell.menu.virtualfridge.events;

/**
 * Created by Dell on 30.10.2017.
 */

public class AmountOfProductChangedEvent {
    public int productId, productFlagId;
    public double quantity, oldQuantity;
    public long shelfId;
    public boolean extraShelf;

    public AmountOfProductChangedEvent(int productId, double quantity, double oldQuantity,
                                       long shelfId, int productFlagId, boolean extraShelf){
        this.productId = productId;
        this.quantity = quantity;
        this.productFlagId = productFlagId;
        this.shelfId = shelfId;
        this.oldQuantity = oldQuantity;
        this.extraShelf = extraShelf;
    }
}
