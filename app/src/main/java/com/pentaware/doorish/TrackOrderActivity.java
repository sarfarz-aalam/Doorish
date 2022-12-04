package com.pentaware.doorish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.pentaware.doorish.model.OfflineProduct;
import com.pentaware.doorish.model.Orders;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TrackOrderActivity extends AppCompatActivity {

    private CardView cardViewOrdered, cardViewConfirmed, cardViewShipped, cardViewDelivered;
    private ImageView imgOrdered, imgConfirmed, imgShipped, imgDelivered;
    private TextView txtOrderedDate, txtConfirmedDate, txtShippedDate, txtDeliveredDate;
    private FirebaseFirestore db;
    private String orderId;
    private TextView txtExpectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_order);

        db = FirebaseFirestore.getInstance();

        orderId = getIntent().getStringExtra("order_id");

        cardViewOrdered = findViewById(R.id.card_ordered);
        cardViewConfirmed = findViewById(R.id.card_confirmed);
        cardViewShipped = findViewById(R.id.card_shipped);
        cardViewDelivered = findViewById(R.id.card_delivered);

        imgOrdered = findViewById(R.id.img_ordered);
        imgConfirmed = findViewById(R.id.img_confirmed);
        imgShipped = findViewById(R.id.img_shipped);
        imgDelivered = findViewById(R.id.img_delivered);

        txtExpectedDate = findViewById(R.id.txt_expected_delivery_date);

        txtOrderedDate = findViewById(R.id.txt_ordered_date);
        txtConfirmedDate = findViewById(R.id.txt_confirmed_date);
        txtShippedDate = findViewById(R.id.txt_shipped_date);
        txtDeliveredDate = findViewById(R.id.txt_delivered_date);

        getOrderStatus();


    }

    private void getOrderStatus() {
        db.collection("online_orders").document(orderId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){

                    DocumentSnapshot documentSnapshot = task.getResult();
                    Orders order = documentSnapshot.toObject(Orders.class);
                    String expectedDeliveryDate;

                    if(order.requested_delivery_time.contains("Anytime")){
                        expectedDeliveryDate =  order.requested_delivery_date;;
                    }
                    else {
                        expectedDeliveryDate =  order.requested_delivery_date + " at " + order.requested_delivery_time;
                    }

                    String status = order.Status;
                    SimpleDateFormat sfd = new SimpleDateFormat("dd MMM yyyy hh:mm:ss aa");
                    Date dateOrdered = order.order_date;


                    String sDateOrdered = sfd.format(dateOrdered);
                    String sConfirmedDate = null;
                    String sDispatchDate = null;
                    String sDeliveryDate = null;

                    Date dtConfirmed = order.confirmation_date;

                    if(dtConfirmed != null){
                        sConfirmedDate = sfd.format(dtConfirmed);
                    }

                    if(order.dispatch_date != null){
                        sDispatchDate = sfd.format(order.dispatch_date);
                    }

                    if(order.delivery_date != null){
                        sDeliveryDate = sfd.format(order.delivery_date);
                    }



                    txtExpectedDate.setText("Arriving on " + expectedDeliveryDate);

                 //   Date dtOrdered = (Date) documentSnapshot.get("order_date");

                    switch (status){
                        case "Order received. Seller Confirmation pending.":
                            cardViewOrdered.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            imgOrdered.setImageResource(R.drawable.ic_done_white);
                            txtOrderedDate.setText(sDateOrdered);

                            cardViewConfirmed.setCardBackgroundColor(getResources().getColor(R.color.color_grey));

                            cardViewShipped.setCardBackgroundColor(getResources().getColor(R.color.color_grey));

                            cardViewDelivered.setCardBackgroundColor(getResources().getColor(R.color.color_grey));


                            break;
                        case "Order Confirmed. Preparing for Dispatch":
                            cardViewOrdered.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            imgOrdered.setImageResource(R.drawable.ic_done_white);
                            txtOrderedDate.setText(sDateOrdered);

                            txtConfirmedDate.setText(sConfirmedDate);
                            cardViewConfirmed.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            imgConfirmed.setImageResource(R.drawable.ic_done_white);

                            cardViewShipped.setCardBackgroundColor(getResources().getColor(R.color.color_grey));

                            cardViewDelivered.setCardBackgroundColor(getResources().getColor(R.color.color_grey));

                            break;
                        case "Dispatched":
                            cardViewOrdered.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            imgOrdered.setImageResource(R.drawable.ic_done_white);
                            txtOrderedDate.setText(sDateOrdered);

                            cardViewConfirmed.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            txtConfirmedDate.setText(sConfirmedDate);
                            imgConfirmed.setImageResource(R.drawable.ic_done_white);

                            cardViewShipped.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            txtShippedDate.setText(sDispatchDate);
                            imgShipped.setImageResource(R.drawable.ic_done_white);

                            cardViewDelivered.setCardBackgroundColor(getResources().getColor(R.color.color_grey));
                            break;
                        case "Delivered":
                            cardViewOrdered.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            imgOrdered.setImageResource(R.drawable.ic_done_white);
                            txtOrderedDate.setText(sDateOrdered);
                            cardViewConfirmed.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            txtConfirmedDate.setText(sConfirmedDate);

                            imgConfirmed.setImageResource(R.drawable.ic_done_white);
                            cardViewShipped.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            txtShippedDate.setText(sDispatchDate);
                            imgShipped.setImageResource(R.drawable.ic_done_white);

                            cardViewDelivered.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            imgDelivered.setImageResource(R.drawable.ic_done_white);
                            txtDeliveredDate.setText(sDeliveryDate);
                            break;
                    }


                }
            }
        });
    }
}