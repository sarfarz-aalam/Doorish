package com.pentaware.doorish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.resources.TextAppearance;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pentaware.doorish.model.MyRequirements;
import com.pentaware.doorish.model.Seller;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PostYOurRequirement extends AppCompatActivity {

    private List<MyRequirements> lstProductId = new ArrayList<>();
    private Seller m_Seller;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Context ctx;


    Button btnDatePicker, btnTimePicker;
    EditText txtDate, txtTime;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private String pickupTimeStamp;
    private boolean pickupAtStore = false;
    private boolean bPayByCash = false;
    private CheckBox chkRedeemPonints;



    ArrayList<LinearLayout> rows = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_y_our_requirement);

        Intent iin= getIntent();
        Bundle b = iin.getExtras();

        if(b!=null)
        {
            m_Seller =(Seller) b.get("seller");
        }

        TextView txtCompanyName = findViewById(R.id.txtCompanyName);
        if(m_Seller != null){
            txtCompanyName.setText(m_Seller.company_name);
        }

        chkRedeemPonints = findViewById(R.id.chkRedeemPoints);
        final TextView txtProductName = findViewById(R.id.txtProductName);
        final TextView txtQty = findViewById(R.id.txtQty);
        Button btnAdd = findViewById(R.id.btnAdd);

        ctx = this;

        final Button btnSubmitToSeller = findViewById(R.id.btnSubmitToSeller);
       final LinearLayout submitLayout = findViewById(R.id.submitLayout);

        final LinearLayout layoutRequirements = findViewById(R.id.layoutRequirements);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(txtProductName.getText().toString().trim().equals("")){
                    txtProductName.setError("Please Enter Product Name");
                    return;
                }

                if(txtQty.getText().toString().trim().equals("") || txtQty.getText().toString().trim().equals("0") ){
                    txtQty.setError("Please Enter a valid Product Qty");
                    return;
                }



                submitLayout.setVisibility(View.VISIBLE);

                String productId = UUID.randomUUID().toString();
                MyRequirements myTableRow = new MyRequirements();
                myTableRow.productId = productId;
                myTableRow.productName = txtProductName.getText().toString();
                myTableRow.Qty = txtQty.getText().toString();
                lstProductId.add(myTableRow);

                LinearLayout layout = new LinearLayout(ctx);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setLayoutParams(layoutParams);
                layout.setTag(productId);

                TextView txtProductDetail = new TextView(ctx);
                String htmlContent = "Product Name: " + myTableRow.productName + "<br />Qty: " + myTableRow.Qty;
                txtProductDetail.setText((Html.fromHtml(htmlContent)));
                txtProductDetail.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                txtProductDetail.setTextColor(getResources().getColor(R.color.textBoxStroke));

                final Button btnDelete =  new Button(ctx);
                //LinearLayout.LayoutParams lpButton = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                btnDelete.setText("Delete");

                btnDelete.setTag(productId);
                btnDelete.setBackgroundResource(R.drawable.gradient);

                layout.addView(txtProductDetail);
                layout.addView(btnDelete);

                txtProductName.setText("");
                txtQty.setText("");
                txtProductName.requestFocus();

                rows.add(layout);

                layoutRequirements.addView(layout);

                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(ctx, "Button clicked", Toast.LENGTH_SHORT).show();
                        String productId = (String) btnDelete.getTag();
                        int index = 0;
                        for(int i = 0; i < rows.size(); i++){
                            String sPid = (String) rows.get(i).getTag();
                            if(sPid.equals(productId)){
                               index = i;
                               break;
                            }
                        }

                        layoutRequirements.removeView(rows.get(index));

                        index = 0;
                        for(int i = 0; i < lstProductId.size(); i++){
                            MyRequirements mr = lstProductId.get(i);
                            String sPid = mr.productId;
                            if(sPid.equals(productId)){
                                index = i;
                                break;
                            }
                        }
                        lstProductId.remove(index);

                    }
                });




//                btnDatePicker.setOnClickListener(new View.OnClickListener() {
//                    @Override

            }
        });

        btnDatePicker=(Button)findViewById(R.id.btn_date);
        btnTimePicker=(Button)findViewById(R.id.btn_time);
        txtDate=(EditText)findViewById(R.id.in_date);
        txtTime=(EditText)findViewById(R.id.in_time);

        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(ctx,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                String sDayOfMonth = Integer.toString(dayOfMonth);
                                if(dayOfMonth < 10){
                                    sDayOfMonth = "0" + sDayOfMonth;
                                }

                                String sMonth = CommonMethods.getMonthName(monthOfYear + 1);
                                String sYear = Integer.toString(year);

                                txtDate.setText(sDayOfMonth + "-" + sMonth + "-" + sYear);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        btnTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(ctx,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                String sHourOfDay = Integer.toString(hourOfDay);
                                if(hourOfDay < 10){
                                    sHourOfDay = "0" + Integer.toString(hourOfDay);
                                }

                                String sMinute = Integer.toString(minute);
                                if(minute < 10){
                                    sMinute = "0" + Integer.toString(minute);
                                }
                                txtTime.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });








                btnSubmitToSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pickupTimeStamp = txtDate.getText().toString() + " " + txtTime.getText().toString();
                RadioButton rbPickupFromStore = (RadioButton)findViewById(R.id.rbPickupAtStore);
                if(rbPickupFromStore.isChecked()){
                    pickupAtStore = true;
                }
                else{
                    pickupAtStore = false;
                }

               // pickupAtStore = chkPickupAtStore.isChecked();

//                CheckBox chkPayByCash = (CheckBox)  findViewById(R.id.chkPayByCash);
//                bPayByCash = chkPayByCash.isChecked();
                RadioButton rbPayByCash = (RadioButton) findViewById(R.id.rbPayByCash);
                bPayByCash = rbPayByCash.isChecked();
                saveToDb();
            }
        });
    }

    private void saveToDb(){

        List<String> productIds = new ArrayList<>();
        List<String> productNames = new ArrayList<>();
        List<String> productQty = new ArrayList<>();
        List<Float> productPrices = new ArrayList<>();
        List<Float> totalPrices = new ArrayList<>();
        List<String> availability = new ArrayList<>();
        String noteToSeller = null;
        EditText txtNoteToSeller = (EditText)findViewById(R.id.txtNoteToSeller);
        if(!txtNoteToSeller.getText().toString().trim().equals("")){
            noteToSeller = txtNoteToSeller.getText().toString();
        }


        for(int i = 0; i < lstProductId.size(); i++){
            MyRequirements requirements = lstProductId.get(i);
            productIds.add(requirements.productId);
            productNames.add(requirements.productName);
            productQty.add(requirements.Qty);
            productPrices.add(0f);
            totalPrices.add(0f);
            availability.add("Pending");

        }

        String docId = UUID.randomUUID().toString();
        Map<String, Object> orderRequest = new HashMap<>();
        orderRequest.put("customer_id", CommonVariables.loggedInUserDetails.customer_id);
        orderRequest.put("fcm", CommonVariables.loggedInUserDetails.fcm);
        orderRequest.put("customer_name", CommonVariables.loggedInUserDetails.Name);
        orderRequest.put("customer_phone", CommonVariables.deliveryAddress.Phone);
        orderRequest.put("customer_address_line1", CommonVariables.deliveryAddress.AddressLine1);
        orderRequest.put("customer_address_line2", CommonVariables.deliveryAddress.AddressLine2);
        orderRequest.put("customer_address_line3", CommonVariables.deliveryAddress.AddressLine3);
        orderRequest.put("customer_pin", CommonVariables.deliveryAddress.Pincode);
        orderRequest.put("customer_city", CommonVariables.deliveryAddress.City);
        orderRequest.put("customer_state", CommonVariables.deliveryAddress.State);




        orderRequest.put("doc_id", docId);
        orderRequest.put("seller_id", m_Seller.seller_id);
        orderRequest.put("company_name", m_Seller.company_name);
        orderRequest.put("seller_phone", m_Seller.mobile);
        orderRequest.put("seller_address_line1", m_Seller.address_line1);
        orderRequest.put("seller_address_line2", m_Seller.address_line2);
        orderRequest.put("seller_address_line3", m_Seller.address_line3);
        orderRequest.put("seller_city", m_Seller.city);
        orderRequest.put("seller_state", m_Seller.state);
        orderRequest.put("seller_pin", m_Seller.pincode);

        orderRequest.put("product_ids", productIds);
        orderRequest.put("product_names", productNames);
        orderRequest.put("product_qty", productQty);
        orderRequest.put("product_prices", productPrices);
        orderRequest.put("product_prices_total", totalPrices);
        orderRequest.put("available_status", availability);
        orderRequest.put("buyer_rejection_reason", null);
        orderRequest.put("seller_rejection_reason", null);
        orderRequest.put("pickup_timestamp", pickupTimeStamp);
        orderRequest.put("pickup_from_store", pickupAtStore);
        orderRequest.put("pay_by_cash", bPayByCash);
        orderRequest.put("note_to_seller", noteToSeller);
        orderRequest.put("redeem_points", chkRedeemPonints.isChecked());
        orderRequest.put("timestamp", FieldValue.serverTimestamp());

        //Status Code 0: Requested by customer
        //Status Code 1: Accepted by Seller
        //Status Code 2: Rejected by seller
        //Status Code 3: Accepted by Buyer
        //Status Code 4: Rejected by Buyer
        orderRequest.put("status_code", 0);


        db.collection("offline_requests").document(docId)
                .set(orderRequest)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                       Toast.makeText(ctx, "Request Sent to Seller", Toast.LENGTH_SHORT).show();
                       LaunchLandingPage();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                       // Log.w(TAG, "Error writing document", e);
                    }
                });

    }

    private void LaunchLandingPage(){
    Intent intent = new Intent(PostYOurRequirement.this, LandingPage.class);
        //prevent the user to come again to this screen in he presses back btton
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
    }
}