package com.pentaware;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

import com.pentaware.doorish.CommonVariables;
import com.pentaware.doorish.R;
import com.pentaware.doorish.ui.address.AddressFragment;
import com.pentaware.doorish.ui.feedback.FeedbackFragment;
import com.pentaware.doorish.ui.orders.OrderDetailFragment;

public class HostFragmentActivity extends AppCompatActivity {

    private String fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_fragment);

        fragment = getIntent().getStringExtra("fragment");
        startFragment(fragment);



    }

    private void startFragment(String fragment) {
        switch (fragment){
            case "feedback":
                getSupportActionBar().setTitle("Feedback and Suggestions");
                Fragment addressFragment = new FeedbackFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.layout_host_fragment, addressFragment);
                transaction.addToBackStack(null);
                transaction.commit();break;
            case "orderDetail":
                getSupportActionBar().setTitle("Order Details");
                Fragment orderFragment = new OrderDetailFragment(CommonVariables.order_detail);
                FragmentTransaction transactions = getSupportFragmentManager().beginTransaction();
                transactions.add(R.id.layout_host_fragment, orderFragment);
                transactions.addToBackStack(null);
                transactions.commit();break;
            default:
                Log.d("startFragment", "startFragment: invalid");
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}