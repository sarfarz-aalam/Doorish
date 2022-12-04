package com.pentaware.doorish;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.pentaware.doorish.ui.products.ProductFragment;

public class ReferAndEarnFragment extends BaseFragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Button btnRefer;
    private TextView txtReferCode;
    private ProgressBar referProgressBar;

    public static ReferAndEarnFragment newInstance() {
        return new ReferAndEarnFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_refer_and_earn, container, false);

        txtReferCode = view.findViewById(R.id.txt_refer_code);
        btnRefer = view.findViewById(R.id.btn_refer);
        referProgressBar = view.findViewById(R.id.refer_progress_bar);
        checkIfLoggedIn();


        btnRefer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                referProgressBar.setVisibility(View.VISIBLE);
                openWhatsApp();
            }
        });
        return view;
    }

    private void checkIfLoggedIn() {
        if(CommonVariables.m_sFirebaseUserId == null){
            btnRefer.setEnabled(false);
            String txtReferral = "Login to Earn Rewards";
            txtReferCode.setText(txtReferral);
        }
        else {
            String txtReferral = "Referral Code: " + CommonVariables.loggedInUserDetails.referral_code;
            txtReferCode.setText(txtReferral);
        }
    }

    public void openWhatsApp(){
        Log.d("referal_code", CommonVariables.loggedInUserDetails.referral_code);
        PackageManager pm = getActivity().getPackageManager();
        try {
            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");
            String text = "Hi! Please Install the Doorish App from the URL - https://bit.ly/3CqL1HO and get INR 20 in your wallet.\nPlease use referral code :" + CommonVariables.loggedInUserDetails.referral_code;  // Replace with your own message.

            PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            //Check if package exists or not. If not then code
            //in catch block will be called
            waIntent.setPackage("com.whatsapp");
            waIntent.putExtra(Intent.EXTRA_TEXT, text);
            referProgressBar.setVisibility(View.INVISIBLE);
            startActivity(Intent.createChooser(waIntent, "Share with"));

        } catch (PackageManager.NameNotFoundException e) {
            referProgressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(getContext(), "WhatsApp not Installed", Toast.LENGTH_SHORT)
                    .show();
        }catch(Exception e){
            referProgressBar.setVisibility(View.INVISIBLE);
            // e.printStacktrace();
        }

    }

}