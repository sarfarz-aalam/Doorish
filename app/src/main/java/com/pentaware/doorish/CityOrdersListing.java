package com.pentaware.doorish;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.pentaware.doorish.model.Offline_Orders;
import com.pentaware.doorish.model.OrderAndProduct;

public class CityOrdersListing extends AppCompatActivity {

    private Offline_Orders mOfflineOrder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_orders_listing);
    }

    public CityOrdersListing(Offline_Orders order){
        this.mOfflineOrder = order;
    }

    public Offline_Orders getmOfflineOrders(){
        return this.mOfflineOrder;
    }
}
