package com.example.dell.menu.virtualfridge.objects;

import com.example.dell.menu.menuplanning.objects.Product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 28.10.2017.
 */

public class ProductsShelf implements Serializable {
    private List<Product> products;
    private String typeName;

    public ProductsShelf(String typeName){
        this.products = new ArrayList<>();
        this.typeName = typeName;
    }

    public List<Product> getProducts() {
        return products;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public void addProduct(Product product){
        int indxOfProduct = findProduct(product.getProductId());
        if(indxOfProduct != -1) products.get(indxOfProduct).addQuantity(product.getQuantity());
        else products.add(product);
    }

    private int findProduct(int productId) {
        for (int i = 0; i < products.size(); i++) {
            if(products.get(i).getProductId() == productId) return i;
        }
        return -1;
    }
}
