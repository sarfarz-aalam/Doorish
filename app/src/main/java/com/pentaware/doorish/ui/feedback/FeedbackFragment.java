package com.pentaware.doorish.ui.feedback;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.internal.service.Common;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pentaware.doorish.BaseFragment;
import com.pentaware.doorish.CommonVariables;
import com.pentaware.doorish.LandingPage;
import com.pentaware.doorish.Popup;
import com.pentaware.doorish.R;
import com.pentaware.doorish.Splash;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FeedbackFragment extends BaseFragment {

    private FeedbackViewModel mViewModel;
    private View mView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static FeedbackFragment newInstance() {
        return new FeedbackFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.feedback_fragment, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FeedbackViewModel.class);
        // TODO: Use the ViewModel

        if(CommonVariables.loggedInUserDetails == null){
            LinearLayout frameLayout = mView.findViewById(R.id.frameFragment);
            frameLayout.setVisibility(View.GONE);
            showPopup();
            return;
        }
        final EditText txtFeedback = mView.findViewById(R.id.txtFeedback);
        Button btnPost = mView.findViewById(R.id.btnPost);

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtFeedback.getText().toString().equals("")) {
                    txtFeedback.setError("Please Enter your feedback or suggestion");
                    return;
                }

                // hide keyboard
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(txtFeedback.getWindowToken(), 0);
                saveToDb(txtFeedback.getText().toString());
            }
        });
    }

    private void saveToDb(String sFeedback) {

        String docId = UUID.randomUUID().toString();

        DocumentReference docOrder = db.collection("feedbacks").document(docId);
        Map<String, Object> data1 = new HashMap<>();
        data1.put("doc_id", docId);
        data1.put("active", false);
        data1.put("customer_id", CommonVariables.m_sFirebaseUserId);
        data1.put("customer_name", CommonVariables.loggedInUserDetails.Name);
        data1.put("customer_phone", CommonVariables.loggedInUserDetails.Phone);
        data1.put("customer_email", CommonVariables.loggedInUserDetails.Email);
        data1.put("feedback", sFeedback);
        data1.put("timestamp", FieldValue.serverTimestamp());

        docOrder.set(data1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(mView.getContext(), "Feedback posted successfully", Toast.LENGTH_SHORT).show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                LaunchLandingPage();

                            }
                        }, 1000);
                    }
                });

    }

    private void LaunchLandingPage(){
        Intent intent = new Intent(getContext(), LandingPage.class);
        //prevent the user to come again to this screen in he presses back btton
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
    }

    private void showPopup(){
        Intent intent = new Intent(getActivity(), Popup.class);
        startActivity(intent);
    }

}