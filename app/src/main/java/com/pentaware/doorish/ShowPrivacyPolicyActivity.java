package com.pentaware.doorish;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.pentaware.doorish.policies.PrivacyPolicy;
import com.pentaware.doorish.policies.TermsAndConditions;

public class ShowPrivacyPolicyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_privacy_policy);

        String showFragment = getIntent().getStringExtra("showFragment");

        if (showFragment != null) {
            if (showFragment.equals("term and conditions")) {
                showTermAndConditions();
            } else if (showFragment.equals("privacy policy")) {
                showPrivacyPolicy();
            }
        }
    }

    private void showPrivacyPolicy() {
        Fragment newFragment = new PrivacyPolicy();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.privacy_policy_fragment, newFragment, "privacy policy")
                .commit();
    }

    private void showTermAndConditions() {
        Fragment newFragment = new TermsAndConditions();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.privacy_policy_fragment, newFragment, "terms and conditions")
                .commit();
    }
}