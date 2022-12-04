package com.pentaware.doorish;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.pentaware.doorish.model.Seller;
import com.pentaware.doorish.model.Shops;

public class ShopListing extends AppCompatActivity {

    private Seller mSeller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_listing);
    }

    public ShopListing(Seller seller){
        this.mSeller = seller;
    }

    public Seller getSeller(){
        return this.mSeller;
    }
}
