package com.pentaware.doorish;

import android.os.Bundle;

import com.pentaware.doorish.model.Product;

import androidx.appcompat.app.AppCompatActivity;

public class ProductListing extends AppCompatActivity {

    private Product mProduct;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_listing);

    }

    public ProductListing(Product prodcut){
        this.mProduct = prodcut;
    }

    public Product getProduct(){
        return this.mProduct;
    }
}
