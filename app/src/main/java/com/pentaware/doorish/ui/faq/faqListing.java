package com.pentaware.doorish.ui.faq;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.pentaware.doorish.R;
import com.pentaware.doorish.model.Address;
import com.pentaware.doorish.model.FAQ;

public class faqListing extends AppCompatActivity {

    private FAQ mFAQ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq_listing);
    }

    public faqListing(FAQ faq){
        this.mFAQ = faq;
    }

    public FAQ getFAQ(){
        return this.mFAQ;
    }
}