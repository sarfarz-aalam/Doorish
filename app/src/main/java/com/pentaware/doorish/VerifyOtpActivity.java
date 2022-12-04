package com.pentaware.doorish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pentaware.doorish.model.Address;
import com.pentaware.doorish.model.User;

import java.util.concurrent.TimeUnit;

public class VerifyOtpActivity extends AppCompatActivity {

    private EditText txt1, txt2, txt3, txt4, txt5, txt6;
    private String phone;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack;
    private String m_sVerificationId;
    final Handler handler = new Handler();
    private FirebaseFirestore db;
    CountDownTimer aTimer;
    private TextView txtPhone, txtPrivacyPolicy, txtChangePhone;
    private TextView txtResend, txtWaitingOtp;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        phone = getIntent().getStringExtra("phone");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));

            /*aligning action bar in center*/
            AppCompatTextView mTitleTextView = new AppCompatTextView(getApplicationContext());
            mTitleTextView.setSingleLine();
            ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;


            mTitleTextView.setText("Sign in with OTP");
            mTitleTextView.setTextColor(getResources().getColor(R.color.white));
            mTitleTextView.setTextAppearance(getApplicationContext(), R.style.action_bar_text);

            actionBar.setCustomView(mTitleTextView, layoutParams);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM );
        }

        db = FirebaseFirestore.getInstance();
        txtPhone = findViewById(R.id.txt_phone);
        String strPhone = phone.replace("+91", "");
        txtPhone.setText(strPhone);

        txtChangePhone = findViewById(R.id.txt_phone_change);

        txtPrivacyPolicy = findViewById(R.id.txt_privacy_policy);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        Button btnVerify = findViewById(R.id.btn_verify);
        txtResend = findViewById(R.id.txt_resend_otp);
        txtWaitingOtp = findViewById(R.id.txt_waiting_otp);

        txt1 = findViewById(R.id.txtOTP1);
        txt2 = findViewById(R.id.txtOTP2);
        txt3 = findViewById(R.id.txtOTP3);
        txt4 = findViewById(R.id.txtOTP4);
        txt5 = findViewById(R.id.txtOTP5);
        txt6 = findViewById(R.id.txtOTP6);

        btnVerify.setOnClickListener(view -> {
            String s1 = txt1.getText().toString();
            String s2 = txt2.getText().toString();
            String s3 = txt3.getText().toString();
            String s4 = txt4.getText().toString();
            String s5 = txt5.getText().toString();
            String s6 = txt6.getText().toString();
            String sCode = s1 + s2 + s3 + s4 + s5 + s6;
            if(sCode.length() == 6)
                verifyCode(sCode);
        });

        txtResend.setOnClickListener(view -> {
            startTimer();
            txtResend.setVisibility(View.VISIBLE);
            txtResend.setTextColor(getResources().getColor(R.color.colorLight));
            sendVerificationCode(phone);
        });

        txt2.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                //                txt2.setText("");
                Log.d("otp_text", "2 is clicked");
                if(txt2.getText().toString().equals("")){
                    txt1.requestFocus();
                    txt1.setText("");
                }
                else{
                    txt1.requestFocus();
                }

            }
            return false;
        });

        txt3.setOnKeyListener((v, keyCode, event) -> {
            Log.d("otp_text", "3 is cooolicked");
            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                //                txt3.setText("");
                Log.d("otp_text", "3 is clicked");
                if(txt3.getText().toString().equals("")){
                    txt2.requestFocus();
                    txt2.setText("");
                }
                else{
                    txt2.requestFocus();
                }


            }
            return false;
        });

        txt4.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
//                txt4.setText("");
                Log.d("otp_text", "4 is clicked");
                if(txt4.getText().toString().equals("")){
                    txt3.requestFocus();
                    txt3.setText("");
                }
                else{
                    txt3.requestFocus();
                }


            }
            return false;
        });

        txt5.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                //                txt5.setText("");
                Log.d("otp_text", "5 is clicked");

                if(txt5.getText().toString().equals("")){
                    txt4.requestFocus();
                    txt4.setText("");
                }
                else{
                    txt4.requestFocus();
                }

            }
            return false;
        });

        txt6.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                //                txt6.setText("");
                Log.d("otp_text", "6 is clicked");
                if(txt6.getText().toString().equals("")){
                    txt5.requestFocus();
                    txt5.setText("");

                }
                else{
                    txt5.requestFocus();
                }

            }
            return false;
        });


        txt1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String sText = s.toString();
                if (sText.length() == 1) {
                    txt2.requestFocus();
                }
            }
        });

        txt2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String sText = s.toString();

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String sText = s.toString();
                if (sText.length() == 1) {
                    txt3.requestFocus();
                }

//                if (sText.length() == 0) {
//                    txt1.requestFocus();
//                }
            }
        });

        txt3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String sText = s.toString();

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String sText = s.toString();
                if (sText.length() == 1) {
                    txt4.requestFocus();
                }
//                if (sText.length() == 0) {
//                    txt2.requestFocus();
//                }
            }
        });

        txt4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String sText = s.toString();

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String sText = s.toString();
                if (sText.length() == 1) {
                    txt5.requestFocus();
                }
//                if (sText.length() == 0) {
//                    txt3.requestFocus();
//                }
            }
        });

        txt5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String sText = s.toString();

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String sText = s.toString();
                if (sText.length() == 1) {
                    txt6.requestFocus();
                }
//                if (sText.length() == 0) {
//                    txt4.requestFocus();
//                }
            }
        });

        txt6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String sText = s.toString();

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String sText = s.toString();
                if (sText.length() == 1) {
                    String s1 = txt1.getText().toString();
                    String s2 = txt2.getText().toString();
                    String s3 = txt3.getText().toString();
                    String s4 = txt4.getText().toString();
                    String s5 = txt5.getText().toString();
                    String s6 = sText;
                    String sCode = s1 + s2 + s3 + s4 + s5 + s6;
                    // Toast.makeText(m_Ctx, "Going to verify code" + sCode, Toast.LENGTH_SHORT).show();
                    verifyCode(sCode);

                }

//                if (sText.length() == 0) {
//                    txt5.requestFocus();
//                }
            }
        });

        mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                m_sVerificationId = s;
                progressBar.setVisibility(View.GONE);
                startTimer();
                Toast.makeText(VerifyOtpActivity.this, "OTP sent successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                String code = phoneAuthCredential.getSmsCode();
                if(code != null){
                    char[] codeDigits = code.toCharArray();
                    txt1.setText(String.valueOf(codeDigits[0]));
                    txt2.setText(String.valueOf(codeDigits[1]));
                    txt3.setText(String.valueOf(codeDigits[2]));
                    txt4.setText(String.valueOf(codeDigits[3]));
                    txt5.setText(String.valueOf(codeDigits[4]));
                    txt6.setText(String.valueOf(codeDigits[5]));
                }

                //verifyCode(code);





            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        };

        txtChangePhone.setOnClickListener(view -> {
            finish();
        });

        setPrivacyPolicyText();
        txtResend.setEnabled(false);
//        startTimer();
        sendVerificationCode(phone);

    }
    private void verifyCode(String code) {
        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(m_sVerificationId, code);
        signInWithCredintial(credential);
    }

    private void signInWithCredintial(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    LaunchActivity();
                                }
                            }, 500);
                        } else {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);

                        }
                    }
                });

    }

    public void sendVerificationCode(String sPhone) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                sPhone,
                60,
                TimeUnit.SECONDS,
                this,
                mCallBack
        );
    }

    private void setPrivacyPolicyText() {
        SpannableString SpanString = new SpannableString(
                "By continuing, you agree to our Terms and Conditions and Privacy Policy");

        ClickableSpan termsAndCondition = new ClickableSpan() {

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }

            @Override
            public void onClick(View textView) {
                Intent mIntent = new Intent(VerifyOtpActivity.this, ShowPrivacyPolicyActivity.class);
                mIntent.putExtra("showFragment", "term and conditions");
                startActivity(mIntent);
            }
        };

        ClickableSpan privacyPolicy = new ClickableSpan() {
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }

            @Override
            public void onClick(View textView) {
                Intent mIntent = new Intent(VerifyOtpActivity.this, ShowPrivacyPolicyActivity.class);
                mIntent.putExtra("showFragment", "privacy policy");
                startActivity(mIntent);
            }
        };

        SpanString.setSpan(termsAndCondition, 32, 52, 0);
        SpanString.setSpan(privacyPolicy, 57, 71, 0);
        SpanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorLightBlue)), 32, 52, 0);
        SpanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorLightBlue)), 57, 71, 0);
        SpanString.setSpan(new BackgroundColorSpan(Color.parseColor("#fafafa")), 0, 71, 0);
//        SpanString.setSpan(new UnderlineSpan(), 32, 52, 0);
//        SpanString.setSpan(new UnderlineSpan(), 57, 71, 0);

        txtPrivacyPolicy.setMovementMethod(LinkMovementMethod.getInstance());
        txtPrivacyPolicy.setText(SpanString, TextView.BufferType.SPANNABLE);
        txtPrivacyPolicy.setSelected(true);

    }

    public void LaunchActivity() {
        CommonVariables.m_sFirebaseUserId = mAuth.getUid();
        //check if this firebase user exists in users table. if not ask for some additional detials to enter
        DocumentReference docRef = db.collection("users").document(CommonVariables.m_sFirebaseUserId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        CommonVariables.loggedInUserDetails = document.toObject(User.class);
                        setAddress();
                        //LaunchLandingPage();

                    } else {
                        LaunchOtherDetailsActivity();
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
    }

    public void setAddress() {
        DocumentReference docRef = db.collection("users").document(CommonVariables.m_sFirebaseUserId).collection("Addresses")
                .document(CommonVariables.loggedInUserDetails.AddressId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        CommonVariables.deliveryAddress = document.toObject(Address.class);
                        LoadAddresses();
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
    }

    public void LoadAddresses() {
        db.collection("users").document(CommonVariables.m_sFirebaseUserId).collection("Addresses")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (final QueryDocumentSnapshot document : task.getResult()) {
                                Address objAddress = document.toObject(Address.class);
                                CommonVariables.loggedInUserDetails.AddressList.add(objAddress);
                            }
                            progressBar.setVisibility(View.INVISIBLE);
                            LaunchLandingPage();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void startTimer() {
        aTimer = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
               //txtResend.setEnabled(true);
                txtWaitingOtp.setText("Waiting for OTP: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                txtResend.setEnabled(true);
                txtResend.setTextColor(getResources().getColor(R.color.colorLightBlue));
                txtWaitingOtp.setVisibility(View.GONE);
//                otpButton.setText("RESEND OTP");
            }
        }.start();
    }

    private void LaunchLandingPage() {
        Intent intent = new Intent(getApplicationContext(), LandingPage.class);
        //prevent the user to come again to this screen in he presses back btton
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
    }

    private void LaunchOtherDetailsActivity() {
        Intent intent = new Intent(getApplicationContext(), UserOtherDetails.class);
        //prevent the user to come again to this screen in he presses back btton
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}