package com.pentaware.doorish.policies;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pentaware.doorish.BaseFragment;
import com.pentaware.doorish.R;

public class ContactUs extends BaseFragment {

    private ContactUsViewModel mViewModel;
    private View mView;

    public static ContactUs newInstance() {
        return new ContactUs();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.contact_us_fragment, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ContactUsViewModel.class);
        // TODO: Use the ViewModel

        TextView txtHelp = mView.findViewById(R.id.txtHelp);
        TextView txtGrievence = mView.findViewById(R.id.txtGrievence);
        TextView txtFeedback = mView.findViewById(R.id.txtFeedbacks);
        TextView txtTollFree = mView.findViewById(R.id.txtTollFreeNumber);

        String sHelp = "For any kind of help regarding the app, please write an email to -\n" +
                "<b>support@doorish.com</b>";

        txtHelp.setText(Html.fromHtml(sHelp));

        String sGrievence = "For any issues or grievances please send an email to-\n" +
                "<b>support@doorish.com</b>";
        txtGrievence.setText(Html.fromHtml(sGrievence));

        String sFeedback = "Have a feedback for us. Good or bad, please let us know by sending an email to-\n" +
                "<b>support@doorish.com</b>";
        txtFeedback.setText(Html.fromHtml(sFeedback));


        String sTollFree = "Want to talk to us? Please call on below number :-\n" +
                "<b>18003091112</b>";
        txtTollFree.setText(Html.fromHtml(sTollFree));

        String sAddress = "A-203 transport nagar sector-69 Noida. Pin-201301";
        TextView txtAddress = mView.findViewById(R.id.txtAddress);
        txtAddress.setText(sAddress);




//        holder.txtReviewTitle.setText(Html.fromHtml(reviewTitle));
    }

}
