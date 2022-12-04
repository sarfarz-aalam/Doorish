package com.pentaware.doorish;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.pentaware.doorish.model.Address;

public class AddressListing extends AppCompatActivity {

    private Address mAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_listing);
    }

    public AddressListing(Address address){
        this.mAddress = address;
    }

    public Address getAddress(){
        return this.mAddress;
    }
}
