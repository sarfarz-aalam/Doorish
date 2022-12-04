package com.pentaware.doorish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsApi;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest;
import com.google.android.play.core.install.model.ActivityResult;
import com.pentaware.doorish.model.Address;
import com.pentaware.doorish.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.w3c.dom.Text;

import java.util.concurrent.TimeUnit;

import pl.droidsonroids.gif.GifImageView;

@TargetApi(Build.VERSION_CODES.DONUT)
public class Login extends AppCompatActivity {

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack;
    private String m_sVerificationId;
    private String m_sPhone;
    private FirebaseAuth mAuth;
    private EditText txt1, txt2, txt3, txt4, txt5, txt6;

    private EditText txtPhone, txtEmail;
    private Button otpButton, loginButton;
    private RelativeLayout layoutPhone, layoutEmail;
    private Enums.LoginMethod loginMethod = Enums.LoginMethod.EMAIL;
    private ProgressBar gifView;
    private TextView txtPrivacyPolicy;
    private static final int CREDENTIAL_PICKER_REQUEST = 1;
    final Handler handler = new Handler();
    final Handler handler_carousel = new Handler();
    private TextView txtHeadCarousel, txtBodyCarousel;
    private boolean shownHint = false;

    int[] carouselImages = {R.drawable.illust_1, R.drawable.illust_2, R.drawable.illust_3, R.drawable.illust_4};
    String[] carouselTextHead = {"Find products you love", "Place order now", "Fast deliveries", "Quality service"};
    String[] carouselTextBody = {"Explore a wide range of products available here", "Buy your favourite products now on discount"
            , "Don't wait, order now and get fast deliveries", "Always providing quality service to customers"};

    private ImageView img_carousel;

    private String m_sPhoneLoginText = "Login Using Phone";
    private String m_sEmailLoginText = "Login Using Email";

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    LinearLayout layoutOtp;
    private boolean m_bIsPhoneLogin = true;

    private CarouselView carouselView;
    //for google login
    private static final int RC_SIGN_IN = 123;
    private GoogleSignInClient mGoogleSignInClient;

    private Context m_Ctx;
    CountDownTimer aTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_Ctx = getApplicationContext();

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));
            actionBar.hide();
        }


        //  List<String> lstLastMonths = CommonMethods.getInstance().getLastMonths(12);

        loginButton = findViewById(R.id.btnLogin);
        layoutPhone = findViewById(R.id.layoutPhoneLogin);
        layoutEmail = findViewById(R.id.layoutEmailLogin);
        Button btnEmail = findViewById(R.id.btnEmail);
        carouselView = findViewById(R.id.carousel);

        txtHeadCarousel = findViewById(R.id.txt_head_illustration);
        txtBodyCarousel = findViewById(R.id.txt_body_illustration);

        TextView txtSkip = findViewById(R.id.txt_skip);

        layoutOtp = findViewById(R.id.layout_otp);

        img_carousel = findViewById(R.id.img_carousel);
        gifView = findViewById(R.id.progress_bar);
        txtPrivacyPolicy = findViewById(R.id.txt_privacy_policy);


        // loginButton.setVisibility(View.GONE);

        txtEmail = findViewById(R.id.txtEmail);


        initiatePhoneLoginSettings();

        changeLayout();

        setPrivacyPolicyText();

        setCarouselView();

        txtPhone.setOnClickListener(view -> {
            if(!shownHint){
                shownHint = true;

            }

        });

        txtSkip.setOnClickListener(view -> {
            CommonVariables.deliveryAddress = null;
            CommonVariables.loggedInUserDetails = null;
            CommonVariables.m_sFirebaseUserId = null;

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();
            LaunchLandingPage();
        });



        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!loginMethod.equals(Enums.LoginMethod.EMAIL)) {
                    loginMethod = Enums.LoginMethod.EMAIL;
//            loginButton.setText(m_sPhoneLoginText);
                    layoutEmail.setVisibility(View.VISIBLE);
                    layoutPhone.setVisibility(View.GONE);
                }

            }
        });


        showPhoneHint();
    }



    private void showPhoneHint(){

       // GetPhoneNumberHintIntentRequest request = GetPhoneNumberHintIntentRequest.builder().build();


        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();



        PendingIntent intent = Credentials.getClient(getApplicationContext()).getHintPickerIntent(hintRequest);

        try
        {
            startIntentSenderForResult(intent.getIntentSender(), CREDENTIAL_PICKER_REQUEST, null, 0, PendingIntent.FLAG_MUTABLE, 0,new Bundle());
        }
        catch (IntentSender.SendIntentException e)
        {
            e.printStackTrace();
        }
    }


    private void setCarouselView() {

        ImageView slider1 = findViewById(R.id.slider_1);
        ImageView slider2 = findViewById(R.id.slider_2);
        ImageView slider3 = findViewById(R.id.slider_3);
        ImageView slider4 = findViewById(R.id.slider_4);



        aTimer = new CountDownTimer(Integer.MAX_VALUE, 5000) {

            public void onTick(long millisUntilFinished) {
                Log.d("tickk", String.valueOf(CommonVariables.idx_carousel));
                if(CommonVariables.idx_carousel > 3)
                    CommonVariables.idx_carousel = 0;
                img_carousel.setImageResource(carouselImages[CommonVariables.idx_carousel]);
                txtHeadCarousel.setText(carouselTextHead[CommonVariables.idx_carousel]);
                txtBodyCarousel.setText(carouselTextBody[CommonVariables.idx_carousel]);

                slider1.setImageResource(R.drawable.disabled);
                slider2.setImageResource(R.drawable.disabled);
                slider3.setImageResource(R.drawable.disabled);
                slider4.setImageResource(R.drawable.disabled);


                switch (CommonVariables.idx_carousel){
                    case 0:
                        slider1.setImageResource(R.drawable.gradient);
                        break;
                    case 1:
                        slider2.setImageResource(R.drawable.gradient);
                        break;
                    case 2:
                        slider3.setImageResource(R.drawable.gradient);
                        break;
                    case 3:
                        slider4.setImageResource(R.drawable.gradient);
                        break;
                }
                CommonVariables.idx_carousel++;
            }

            public void onFinish() {

            }
        }.start();




        carouselView.setPageCount(carouselImages.length);



        ImageListener imageListener = new ImageListener() {



            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                Log.d("Position_t", String.valueOf(position));
                imageView.setImageResource(carouselImages[position]);
                //txtHeadCarousel.setText(carouselTextHead[position]);
                //txtBodyCarousel.setText(carouselTextBody[position]);
            }
        };

        carouselView.setImageListener(imageListener);
    }

    private void changeLayout() {
        if (m_bIsPhoneLogin) {
//            loginButton.setText(m_sEmailLoginText);
            layoutEmail.setVisibility(View.GONE);
            layoutPhone.setVisibility(View.VISIBLE);
            loginMethod = Enums.LoginMethod.PHONE;
        } else {
            loginMethod = Enums.LoginMethod.EMAIL;
//            loginButton.setText(m_sPhoneLoginText);
            layoutEmail.setVisibility(View.VISIBLE);
            layoutPhone.setVisibility(View.GONE);
        }

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.sign_in_button:
                        signInWithGoogle();
                        break;
                    // ...
                }
            }
        });
    }


    private void initiatePhoneLoginSettings() {
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCodeValue = tm.getNetworkCountryIso();

        txt1 = findViewById(R.id.txtOTP1);
        txt2 = findViewById(R.id.txtOTP2);
        txt3 = findViewById(R.id.txtOTP3);
        txt4 = findViewById(R.id.txtOTP4);
        txt5 = findViewById(R.id.txtOTP5);
        txt6 = findViewById(R.id.txtOTP6);

        otpButton = findViewById(R.id.btnOTP);

        txtPhone = findViewById(R.id.txtPhone);

        txtPhone.requestFocus();



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


//
//        txt1.addTextChangedListener(new OTPWatcher(txt1, txt2));
//        txt2.addTextChangedListener(new OTPWatcher(txt2, txt3));
//        txt3.addTextChangedListener(new OTPWatcher(txt3, txt4));
//        txt4.addTextChangedListener(new OTPWatcher(txt4, txt5));
//        txt5.addTextChangedListener(new OTPWatcher(txt5, txt6));

        mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                m_sVerificationId = s;
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
                Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                gifView.setVisibility(View.GONE);
            }
        };
    }

    private boolean okForOTPValidation() {
        if (txt1.getText().toString().matches("")
                || txt6.getText().toString().matches("") || txt2.getText().toString().matches("") || txt3.getText().toString().matches("")
                || txt4.getText().toString().matches("") || txt5.getText().toString().matches("")) {
            return false;
        }
        return true;
    }

    private void verifyCode(String code) {
        gifView.setVisibility(View.VISIBLE);
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
                            Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            gifView.setVisibility(View.GONE);

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

    public void onSendOTP_Click(View view) {


        if (!txtPhone.getText().toString().equals("")) {
            if(txtPhone.getText().toString().trim().length() == 10){

                m_sPhone = txtPhone.getText().toString();
                CommonVariables.Phone = m_sPhone;
                String sPhone = "+91" + m_sPhone;


                Intent intent = new Intent(this, VerifyOtpActivity.class);
                intent.putExtra("phone",sPhone);
                startActivity(intent);


                //layoutOtp.setVisibility(View.VISIBLE);

                //otpButton.setEnabled(false);
//          otpButton.setBackgroundResource(R.drawable.blue_button_disabled);
                //startTimer();
//                m_sPhone = txtPhone.getText().toString();
//                CommonVariables.Phone = m_sPhone;
//                String sPhone = "+91" + m_sPhone;
//                sendVerificationCode(sPhone);
//                txt1.requestFocus();
//                Toast.makeText(this, "OTP Sent", Toast.LENGTH_SHORT).show();
            }
            else {
                txtPhone.setError("Phone number has to be 10 digits number");
            }

        }


    }

    public void onLoginbtn_Click(View view) {
//      m_bIsPhoneLogin = !m_bIsPhoneLogin;

        //loginButton.setText(m_sEmailLoginText);
        if (!loginMethod.equals(Enums.LoginMethod.PHONE)) {
            layoutEmail.setVisibility(View.GONE);
            layoutPhone.setVisibility(View.VISIBLE);
            loginMethod = Enums.LoginMethod.PHONE;
        }


//      changeLayout();

    }

    private void startTimer() {
        aTimer = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
//              otpButton.setText("Waiting for OTP: " + millisUntilFinished / 1000);
                otpButton.setText("Resend OTP in: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                otpButton.setEnabled(true);
                otpButton.setBackgroundResource(R.drawable.blue_button);
                otpButton.setText("RESEND OTP");
            }
        }.start();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void onRegister_Click(View view) {
        Intent intent = new Intent(Login.this, RegisterUser.class);
        startActivity(intent);

    }

    public void onLoginEmail_Click(View view) {
        EditText txtEmail = findViewById(R.id.txtEmail);
        EditText txtPwd = findViewById(R.id.txtPassword);

        if (txtEmail.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
        } else if (txtPwd.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
        } else {
            gifView.setVisibility(View.VISIBLE);
            signInWithEmail(txtEmail.getText().toString(), txtPwd.getText().toString());
        }
    }


    private void signInWithEmail(String email, String password) {

        CommonVariables.Email = email;

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            // Log.d(TAG, "signInWithEmail:success");

                            LaunchActivity();

                        } else {
                            gifView.setVisibility(View.GONE);
                            // If sign in fails, display a message to the user.
                            //   Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed. Reason: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            // ...
                        }

                        // ...
                    }
                });
    }

    public void onGoogleSignin_Click(View view) {
        gifView.setVisibility(View.VISIBLE);
        loginMethod = Enums.LoginMethod.GOOGLE;
//        if(mGoogleSignInClient == null) {
//            // Configure sign-in to request the user's ID, email address, and basic
//            // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
//            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                    .requestIdToken(getString(R.string.default_web_client_id))
//                    .requestEmail()
//                    .build();
//
//            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
//        }

        signInWithGoogle();

    }

    private void signInWithGoogle() {

        if (mGoogleSignInClient == null) {
            // Configure sign-in to request the user's ID, email address, and basic
            // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        }


        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);


    }

    //@Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
//        if (requestCode == RC_SIGN_IN) {
//            // The Task returned from this call is always completed, no need to attach
//            // a listener.
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            handleSignInResult(task);
//        }
//    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                gifView.setVisibility(View.GONE);
                Toast.makeText(Login.this, "Google sign in failed: Reason - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("google_sign_in_failed", resultCode + " ",e);
                // Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }

        if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == RESULT_OK)
        {
            // Obtain the phone number from the result
            Credential credentials = data.getParcelableExtra(Credential.EXTRA_KEY);
            String phone = credentials.getId().substring(3); //get the selected phone number
            txtPhone.setText(phone);

        }
        else if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == CredentialsApi.ACTIVITY_RESULT_NO_HINTS_AVAILABLE)
        {
            // *** No phone numbers available ***
          //  Toast.makeText(getApplicationContext(), "No phone numbers found", Toast.LENGTH_LONG).show();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());
        CommonVariables.Email = acct.getEmail();
        CommonVariables.UserName = acct.getDisplayName();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            // Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            LaunchActivity();
                            //  updateUI(user);
                        } else {
                            Exception ex = task.getException();
                            gifView.setVisibility(View.GONE);

                            // If sign in fails, display a message to the user.
                            Log.w("google exception", "signInWithCredential:failure", task.getException());
                            // Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //  updateUI(null);
                        }

                        // ...
                    }
                });
    }

//    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
//        try {
//            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
//            FirebaseGoogleAuth(account);
//            CommonVariables.Email = account.getEmail();
//            CommonVariables.UserName = account.getDisplayName();
//
//            LaunchActivity();
//            // Signed in successfully, show authenticated UI.
//            //updateUI(account);
//        } catch (ApiException e) {
//            // The ApiException status code indicates the detailed failure reason.
//            // Please refer to the GoogleSignInStatusCodes class reference for more information.
//            Log.w("TAG", "signInResult:failed code=" + e.getStatusCode());
//            // updateUI(null);
//        }
//    }

//    private void FirebaseGoogleAuth(GoogleSignInAccount account){
//        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
//        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                Log.w("TAG", "signInResult:failed code=");
//            }
//        });
//    }


    public void onForgotPwd_Click(View view) {
        EditText txtEmail = findViewById(R.id.txtEmail);
        if (!txtEmail.getText().toString().equals("")) {
            String sEmail = txtEmail.getText().toString();
            FirebaseAuth.getInstance().sendPasswordResetEmail(sEmail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Login.this, "Password recovery mail sent", Toast.LENGTH_SHORT).show();
                                //  Log.d(TAG, "Email sent.");
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "Enter a valid Email address", Toast.LENGTH_SHORT).show();
        }

    }

    private void LaunchLandingPage() {
        Intent intent = new Intent(Login.this, LandingPage.class);
        //prevent the user to come again to this screen in he presses back btton
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
    }

    private void LaunchUserRegistratoinActivity() {
        Intent intent = new Intent(Login.this, RegisterUser.class);
        //prevent the user to come again to this screen in he presses back btton
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
    }

    private void LaunchOtherDetailsActivity() {
        Intent intent = new Intent(Login.this, UserOtherDetails.class);
        //prevent the user to come again to this screen in he presses back btton
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
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
                Intent mIntent = new Intent(Login.this, ShowPrivacyPolicyActivity.class);
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
                Intent mIntent = new Intent(Login.this, ShowPrivacyPolicyActivity.class);
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
                            gifView.setVisibility(View.INVISIBLE);
                            LaunchLandingPage();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

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


}
