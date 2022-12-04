package com.pentaware.doorish;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.pentaware.doorish.model.Reviews;

public class ReviewListing extends AppCompatActivity {

    private Reviews mReview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_listing);
    }

    public ReviewListing(Reviews review){
        this.mReview = review;
    }

    public Reviews getReview(){
        return this.mReview;
    }
}
