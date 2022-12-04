package com.pentaware.doorish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.pentaware.doorish.model.User;

public class ReferAndEarn extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer_and_earn);

        EditText txtReferralCode = findViewById(R.id.txtReferralCode);

        Button btnClaim = findViewById(R.id.btnClaim);
        Button btnSkip = findViewById(R.id.btnSkip);

        btnClaim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sReferralCode = txtReferralCode.getText().toString();
                claimReferralCode(sReferralCode);
            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LaunchLandingPage();
            }
        });
    }

    private void claimReferralCode(String sReferralCode) {

        db.collection("users")
                .whereEqualTo("referral_code", sReferralCode)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int docCount = task.getResult().size();
                            if (docCount == 0) {
                                Toast.makeText(getApplicationContext(), "Invalid Referral Code", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                updateReferralId(user);
                                // updateWalletMoney(user);

                            }
                        } else {
                            Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                            // Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void updateReferralId(User user) {
        User customer = CommonVariables.loggedInUserDetails;
        customer.referral_customer_id = user.customer_id;
        customer.first_order_id = null;
        customer.referral_claimed = false;
        customer.referee_fcm = user.fcm;
        CommonVariables.loggedInUserDetails = customer;
        db.collection("users").document(customer.customer_id).set(customer).
                addOnCompleteListener(task -> LaunchLandingPage());
    }

    private void updateWalletMoney(User user) {

        // Get a new write batch
        WriteBatch batch = db.batch();

// Set the value of 'NYC'
        DocumentReference refUser = db.collection("users").document(user.customer_id);
        batch.update(refUser, "points", 50);

// Update the population of 'SF'
        DocumentReference refLoggedInUser = db.collection("users").document(CommonVariables.loggedInUserDetails.customer_id);
        batch.update(refLoggedInUser, "points", 50);


// Commit the batch
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // ...
                Toast.makeText(getApplicationContext(), "Referral Code applied successfully. Added INR 50 in your wallet", Toast.LENGTH_SHORT).show();
                LaunchLandingPage();

            }
        });

    }

    private void LaunchLandingPage() {
        Intent intent = new Intent(ReferAndEarn.this, LandingPage.class);
        //prevent the user to come again to this screen in he presses back btton
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
    }
}