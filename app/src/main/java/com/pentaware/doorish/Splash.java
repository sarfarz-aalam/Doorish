package com.pentaware.doorish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.pentaware.doorish.model.Address;
import com.pentaware.doorish.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Splash extends AppCompatActivity {

    Boolean splashLoaded = false;
    Handler handler;
    View mContentView;
    ImageView splashImg;
    FirebaseAuth auth;
    FirebaseFirestore db;
    CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        if (!splashLoaded) {
            setContentView(R.layout.activity_splash);
            splashImg = findViewById(R.id.fullscreen_content);
            mContentView = splashImg;
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startTimer();
                }
            }, 0);
            splashLoaded = true;
        }
    }

    private void startTimer() {
        timer = new CountDownTimer(3500, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {

                if (auth.getCurrentUser() != null) {
                    CommonVariables.m_sFirebaseUserId = auth.getUid();
                }

                getTags();

            }
        }.start();
    }

    public void LaunchActivity() {
        CommonVariables.m_sFirebaseUserId = auth.getUid();
        //check if this firebase user exists in users table. if not ask for some additional detials to enter
        DocumentReference docRef = db.collection("users").document(CommonVariables.m_sFirebaseUserId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        CommonVariables.loggedInUserDetails = document.toObject(User.class);
                        getAndUpdateFCMToken();
                        setAddress();
                        //  LoadAddresses();
                        LaunchLandingPage();
                    } else {
                        LaunchOtherDetailsActivity();
                    }
                } else {
                    auth.signOut();
                    LaunchLoginActivity();
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
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
    }

    private void getAndUpdateFCMToken() {
        //Get Firebase FCM token
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String fcmToken = instanceIdResult.getToken();
                if (!fcmToken.equals(CommonVariables.loggedInUserDetails.fcm)) {

                    DocumentReference washingtonRef = db.collection("users").document(CommonVariables.loggedInUserDetails.customer_id);
                    washingtonRef
                            .update("fcm", fcmToken)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Log.d(TAG, "DocumentSnapshot successfully updated!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Log.w(TAG, "Error updating document", e);
                                }
                            });
                }
            }
        });
    }

    private void LaunchLoginActivity() {
        Intent intent = new Intent(Splash.this, Login.class);
        //prevent the user to come again to this screen in he presses back btton
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
    }

    private void LaunchOtherDetailsActivity() {
        Intent intent = new Intent(Splash.this, UserOtherDetails.class);
        //prevent the user to come again to this screen in he presses back btton
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void LaunchLandingPage() {
        Intent intent = new Intent(Splash.this, LandingPage.class);
        //prevent the user to come again to this screen in he presses back btton
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
    }

    public void getTags() {

        db.collection("product_tags")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int iSize = task.getResult().size();
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                List<String> tags = (ArrayList) document.get("tags");
                                for (int i = 0; i < tags.size(); i++) {
                                    if (CommonVariables.tagList.indexOf(tags.get(i)) == -1) {
                                        CommonVariables.tagList.add(tags.get(i));
                                    }

                                }
                            }

                            if (auth.getCurrentUser() != null) {
                                CommonVariables.m_sFirebaseUserId = auth.getUid();
                                // LaunchInstaMojoActivity();
                                checkIfUserActive();
                            } else {
                                LaunchLandingPage();
                                // LaunchLoginActivity();
                            }

                        } else {
                            Log.d("HomeFragment: getTags", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    public void loadBrands() {

        db.collection("brands")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int iSize = task.getResult().size();
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                List<String> brands = (ArrayList) document.get("brands");
                                for (int i = 0; i < brands.size(); i++) {
                                    CommonVariables.brandList.add(brands.get(i));
                                }
                            }
                            getTags();

                        } else {
                            Log.d("HomeFragment: getTags", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void checkIfUserActive() {
        db.collection("users").document(CommonVariables.m_sFirebaseUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    Boolean isActive = (Boolean) snapshot.get("Active");
                    if(isActive != null){
                        if (isActive) {
                            LaunchActivity();
                        } else {
                            startActivity(new Intent(Splash.this, IfUserNotActive.class));
                            finish();
                        }
                    }
                    else {
                        startActivity(new Intent(Splash.this, UserOtherDetails.class));
                        finish();
                    }
                }
            }
        });
    }


}


