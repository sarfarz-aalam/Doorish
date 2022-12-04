package com.pentaware.doorish;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.pentaware.doorish.model.Cart;

public class CartListing extends AppCompatActivity {

    Cart mCart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_listing);
    }

    public CartListing(Cart cart){
        this.mCart = cart;
    }

    public Cart getmCart(){
        return this.mCart;
    }
}

