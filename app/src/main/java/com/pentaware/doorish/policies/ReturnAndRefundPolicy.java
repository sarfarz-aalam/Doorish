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

public class ReturnAndRefundPolicy extends BaseFragment {

    private View mView;
    private ReturnAndRefundPolicyViewModel mViewModel;

    public static ReturnAndRefundPolicy newInstance() {
        return new ReturnAndRefundPolicy();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.return_and_refund_policy_fragment, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ReturnAndRefundPolicyViewModel.class);
        // TODO: Use the ViewModel

        // TextView txtPolicy = mView.findViewById(R.id.txtRefundPolicy);
        // txtPolicy.setText(Html.fromHtml(getRefundText()));

//        Button btnShowPolicy = (Button)mView.findViewById(R.id.btnShowPolicy);
//        btnShowPolicy.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://firebasestorage.googleapis.com/v0/b/my-rupeaze.appspot.com/o/policies%2FReturns%20Policy%20My%20Rupeaze.pdf?alt=media&token=e891ef3e-9540-4e34-b9a7-35fa320d0b81"));
//                startActivity(browserIntent);
//
//            }
//        });




        //   holder.txtReviewTitle.setText(Html.fromHtml(reviewTitle));
    }



}
