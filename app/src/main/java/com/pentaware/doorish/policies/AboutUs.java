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
import com.pentaware.doorish.policies.AboutUsViewModel;

public class AboutUs extends BaseFragment {

    private AboutUsViewModel mViewModel;
    private View mView;

    public static AboutUs newInstance() {
        return new AboutUs();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.about_us_fragment, container, false);
        return  mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(AboutUsViewModel.class);
        // TODO: Use the ViewModel


        /*Button btnShowPolicy = (Button)mView.findViewById(R.id.btnShowPolicy);
        btnShowPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://essentials-933fb.web.app/Aboutus.html";
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);

            }
        });*/

    }

}
