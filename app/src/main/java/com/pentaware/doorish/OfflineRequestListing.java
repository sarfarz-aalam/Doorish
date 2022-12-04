package com.pentaware.doorish;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.pentaware.doorish.model.Cart;
import com.pentaware.doorish.model.Offline_Requests;

public class OfflineRequestListing extends AppCompatActivity {

    private Offline_Requests mOfflineRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_request_listing);
    }

    public OfflineRequestListing(Offline_Requests request){
        this.mOfflineRequest = request;
    }

    public Offline_Requests getmOfflineRequest(){
        return this.mOfflineRequest;
    }
}