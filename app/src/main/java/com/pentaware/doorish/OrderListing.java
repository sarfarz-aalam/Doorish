package com.pentaware.doorish;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.pentaware.doorish.model.OfflineProduct;
import com.pentaware.doorish.model.OrderAndProduct;

import java.util.ArrayList;

public class OrderListing extends AppCompatActivity {

    private OrderAndProduct mOrderAndProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_listing);
    }

    public OrderListing(OrderAndProduct order){
        this.mOrderAndProduct = order;
    }

    public OrderAndProduct getmOrderAndProduct(){
        return this.mOrderAndProduct;
    }

}

