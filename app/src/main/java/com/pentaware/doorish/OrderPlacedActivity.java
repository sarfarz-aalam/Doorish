package com.pentaware.doorish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pentaware.HostFragmentActivity;
import com.pentaware.doorish.model.Address;
import com.pentaware.doorish.model.Orders;
import com.pentaware.doorish.ui.address.AddressFragment;
import com.pentaware.doorish.ui.feedback.FeedbackFragment;

public class OrderPlacedActivity extends AppCompatActivity {

    private TextView txtPaymentStatus, txtItemTotal, txtOrderAmount, txtSavings, txtLineAddress, txtCityStateAddress;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String orderId;
    Orders order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_placed);

        txtPaymentStatus = findViewById(R.id.txt_payment_status);
        txtItemTotal = findViewById(R.id.txt_item_total);
        txtOrderAmount = findViewById(R.id.txt_net_payable);
        txtSavings = findViewById(R.id.txt_total_savings);
        txtLineAddress = findViewById(R.id.txt_line_address);
        txtCityStateAddress = findViewById(R.id.txt_city_state_address);
        TextView txtGiveFeedback = findViewById(R.id.txt_give_feedback);

        Button btnGoToHomepage = findViewById(R.id.btn_goto_homepage);
        Button btnOrderDetail = findViewById(R.id.btn_order_detail);
        Button btnGiveFeedback = findViewById(R.id.btn_give_feedback);
        ImageView imgClose = findViewById(R.id.img_close);

        orderId = getIntent().getStringExtra("order_id");
        getOrderDetails();



        setOrderDetails();
        setDeliveryAddress();

        CommonVariables.cartlist.clear();
        deleteCart();

        imgClose.setOnClickListener(view -> {
            Intent intent = new Intent(OrderPlacedActivity.this, LandingPage.class);
            //prevent the user to come again to this screen in he presses back btton
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);

            intent.putExtra("page", "order_created");

            startActivity(intent);
        });

        btnGoToHomepage.setOnClickListener(view -> {

        });

        btnOrderDetail.setOnClickListener(view -> {

            Intent intent = new Intent(this, HostFragmentActivity.class);
            intent.putExtra("fragment", "orderDetail");
            startActivity(intent);
        });

        txtGiveFeedback.setOnClickListener(view -> {
           Intent intent = new Intent(this, HostFragmentActivity.class);
           intent.putExtra("fragment", "feedback");
           startActivity(intent);
        });

        btnGiveFeedback.setOnClickListener(view -> {
            Intent intent = new Intent(this, HostFragmentActivity.class);
            intent.putExtra("fragment", "feedback");
            startActivity(intent);
        });

        askRatings();

    }

    private void getOrderDetails() {
        db.collection("online_orders").document(orderId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult() != null){
                    order = task.getResult().toObject(Orders.class);
                    CommonVariables.order_detail = order;
                }

            }
        });
    }

    private void setOrderDetails() {
        //Delivery Address
        Address address = CommonVariables.deliveryAddress;
        String lineAddress = address.Landmark + ", " + address.AddressLine1 + ", " + address.AddressLine2 + ", " + address.AddressLine3;
        String cityStateAddress = address.City + ", " + address.State + ", " + address.Pincode;

        txtLineAddress.setText(lineAddress);
        txtCityStateAddress.setText(cityStateAddress);
    }

    private void setDeliveryAddress() {

        if(CommonVariables.paymentStatus) txtPaymentStatus.setText("Cash on Delivery");
        else txtPaymentStatus.setText("Prepaid");

        txtItemTotal.setText(CommonVariables.rupeeSymbol + CommonMethods.formatCurrency(CommonVariables.itemTotalPrice));
        txtOrderAmount.setText(CommonVariables.rupeeSymbol + CommonMethods.formatCurrency(CommonVariables.netPayable));
        txtSavings.setText(CommonVariables.rupeeSymbol + CommonMethods.formatCurrency(CommonVariables.totalSavings));



    }

    private void deleteCart() {

        db.collection("users").document(CommonVariables.m_sFirebaseUserId).collection("cart")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String id = document.getId();

                                db.collection("users").document(CommonVariables.m_sFirebaseUserId).collection("cart").document(id)
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("TAG", "DocumentSnapshot successfully deleted!");

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("TAG", "Error deleting document", e);
                                            }
                                        });

                                Log.d("cartid", id);
                                //  Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    void askRatings() {
        ReviewManager manager = ReviewManagerFactory.create(this);
        com.google.android.play.core.tasks.Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                ReviewInfo reviewInfo = task.getResult();
                com.google.android.play.core.tasks.Task<Void> flow = manager.launchReviewFlow(this, reviewInfo);
                flow.addOnCompleteListener(task2 -> {
                    Log.d("ask_rating", "complete");
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                });
            } else {
                Log.d("ask_rating", task.getException().toString());
                // There was some problem, continue regardless of the result.
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(OrderPlacedActivity.this, LandingPage.class));
        finish();
    }
}