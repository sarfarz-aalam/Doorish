package com.pentaware.doorish.policies;

import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.pentaware.doorish.BaseFragment;
import com.pentaware.doorish.R;
import com.pentaware.doorish.ui.products.ProductFragment;

public class TermsAndConditions extends BaseFragment {

    private TermsAndConditionsViewModel mViewModel;
    private View mView;

    public static TermsAndConditions newInstance() {
        return new TermsAndConditions();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.terms_and_conditions_fragment, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(TermsAndConditionsViewModel.class);
        // TODO: Use the ViewModel

        /*Button btnShowPolicy = (Button)mView.findViewById(R.id.btnShowPolicy);
        btnShowPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = "https://essentials-933fb.web.app/Terms.html";
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);

            }
        });*/

    }

}
