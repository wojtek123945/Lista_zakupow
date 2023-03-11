package com.example.lista_zakupow.API;

import java.io.Serializable;
import java.util.ArrayList;

public class Shopping implements Serializable {
    private String category;
    private ArrayList<Product> products = new ArrayList<Product>();

    public Shopping(String categoryName){
        this.category = categoryName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }
}
