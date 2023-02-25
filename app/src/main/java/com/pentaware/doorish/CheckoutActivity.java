package com.pentaware.doorish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.jhonnyx2012.horizontalpicker.DatePickerListener;
import com.github.jhonnyx2012.horizontalpicker.Day;
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker;
import com.pentaware.doorish.model.OfflineProduct;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.pentaware.doorish.model.CapturePayment;
import com.pentaware.doorish.model.Cart;
import com.pentaware.doorish.model.CheckoutCart;
import com.pentaware.doorish.model.Orders;
import com.pentaware.doorish.model.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pentaware.doorish.model.User;
import com.pentaware.doorish.ui.address.ChangeDeliveryAddressActivity;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckoutActivity extends AppCompatActivity implements PaymentResultListener, DatePickerListener, IDeliverySlotOperations {

    //  Order razorpayOrder;

    private boolean TEST_MODE = true;

    String sOrderId = "";


    private String razorPayOrderId = null;
    private double mNetPayable = 0;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<String> orderIdList = new ArrayList<>();
    private Context mContext;
    private double mAvailableWalletPoints;
    private double mAvailableWalletMoney;
    private double mWalletMoneyUsed = 0;
    private double mRemainingWalletPoints = 0;
    List<CheckoutCart> lstCheckoutCart = new ArrayList<>();
    ArrayList<OfflineProduct> offlineProducts;
    private String MyPREFERENCES = "MyPrefs";
    private String orderId;
    SharedPreferences sharedPreferences;

    private double order_net_amount = 0;

    private double mOriginalAvailableWalletMoney = 0;
    private double mOriginalNetPayable = 0;
    private double mOriginalRemainingWalletPoints = 0;
    private boolean m_bCartDeleted = false;
    private boolean m_bOrderCreated = false;
    private boolean m_bCOD = false;
    private int mWalletPointsConsumed;
    private double mDeliveryCharges = 0;

    Button btnDatePicker, btnTimePicker;
    EditText txtDate, txtTime;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private String pickupTimeStamp;

    TextView txtNetPayable, txtWalletMoney;

    private Button btnCOD;
    private Button btnPrepaid;
    private Button btnOfflineProducts;

    private RadioButton rbSlot0;
    private RadioButton rbSlot1;
    private RadioButton rbSlot2;

    private static int RESULT_CODE = 0;

    private boolean anytimeSlotBonus = false;

    private HorizontalPicker datePicker;
    private RecyclerView recyclerViewDeliverySlots;
    private AdapterDeliverySlots adapterDeliverySlots;
    private String deliveryDate, deliveryTimeSlot;
    private boolean preSelected = true;

    private Button btnChangeDeliveryAddress;
    CheckBox chkPickupFromStore;
    CheckBox chkUseWallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rbSlot0 = findViewById(R.id.slot0);
        rbSlot1 = findViewById(R.id.slot1);
        rbSlot2 = findViewById(R.id.slot2);

        //  createRazorpayOrder();
        setContentView(R.layout.activity_checkout);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));
        }

        mContext = this;
        btnCOD = findViewById(R.id.btnCOD);
        btnPrepaid = findViewById(R.id.btnPayNow);

        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String id_start = CommonVariables.loggedInUserDetails.Name.substring(0, 2).toUpperCase();
        long id_end = System.currentTimeMillis()/1000;
        sOrderId = id_start.trim() + id_end;


        if (getIntent() != null) {
            if (getIntent().getParcelableArrayListExtra("offline_products") != null) {
                offlineProducts = getIntent().getParcelableArrayListExtra("offline_products");
            }
        }


        Intent iin = getIntent();
        Bundle b = iin.getExtras();

        if (b != null) {
            if (b.get("Net_Payable") != null) {
                mNetPayable = (double) b.get("Net_Payable");
                float netPayable = (float) mNetPayable;
                editor.putFloat("netPayable", netPayable);
                editor.apply();
            } else {
                mNetPayable = (double) sharedPreferences.getFloat("netPayable", 0);
            }
            if (b.get("Delivery_Charges") != null) {
                mDeliveryCharges = (double) b.get("Delivery_Charges");
                editor.putFloat("deliveryCharges", (float) mDeliveryCharges);
                editor.apply();
            } else {
                mDeliveryCharges = (double) sharedPreferences.getFloat("deliveryCharges", 0);
            }
            if (b.get("wallet_money_used") != null) {
                mWalletMoneyUsed = (double) b.get("wallet_money_used");
                editor.putFloat("wallet_money_used", (float) mWalletMoneyUsed);
                editor.apply();
            } else {
//                mWalletMoneyUsed = (double) sharedPreferences.getFloat("wallet_money_used", 0);
            }
            mOriginalNetPayable = mNetPayable;
        }







        chkPickupFromStore = findViewById(R.id.chkPickupFromStore);

        btnChangeDeliveryAddress = findViewById(R.id.btn_change_address);
        datePicker = findViewById(R.id.date_picker);
        recyclerViewDeliverySlots = findViewById(R.id.recycler_view_delivery_slots);

        datePicker.setListener(CheckoutActivity.this).setDays(7)
                .setOffset(0)
                .showTodayButton(false)
                .setDateSelectedColor(getResources().getColor(R.color.colorPrimary))
                .init();


        datePicker.onDateSelected(new Day(DateTime.now()));

        datePicker.showTodayButton(false);

        setDeliveryAddress();

        txtNetPayable = findViewById(R.id.txtNetPayable_Checkout);

        String sNetPayable = CommonVariables.rupeeSymbol + CommonMethods.formatCurrency(mNetPayable);
        txtNetPayable.setText(sNetPayable);

        txtWalletMoney = findViewById(R.id.txtWalletMoney);
        showAvailableWalletMoney();

        //btnOfflineProducts = findViewById(R.id.btn_offline_products);

//        btnOfflineProducts.setOnClickListener(view -> {
//            startActivity(new Intent(this, OfflineProductsActivity.class));
//        });

        btnChangeDeliveryAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                FrameLayout frameLayout = findViewById(R.id.layout_change_address);
//                frameLayout.setVisibility(View.VISIBLE);
                CommonVariables.startedByActivity = true;

                startActivity(new Intent(CheckoutActivity.this, ChangeDeliveryAddressActivity.class));

            }
        });

        final Context ctx = this;

        //final LinearLayout layoutPickupFromStroe = (LinearLayout)findViewById(R.id.layoutPickupFromStore);
        // layoutPickupFromStroe.setVisibility(View.GONE);


        mOriginalRemainingWalletPoints = (int) CommonVariables.loggedInUserDetails.points;

        chkUseWallet = findViewById(R.id.chkUseWalletMoney);
        chkUseWallet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UseWallet(isChecked);
            }
        });

        double walletMoney = CommonVariables.loggedInUserDetails.points;
        if (walletMoney == 0 || walletMoney < 1) {
            chkUseWallet.setVisibility(View.GONE);
        }


        if (hidePickupFromStoreButton()) {
            //  chkPickupFromStore.setVisibility(View.GONE);

        }

//        chkPickupFromStore.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(chkPickupFromStore.isChecked()){
//                    layoutPickupFromStroe.setVisibility(View.VISIBLE);
//                }
//                else{
//                    layoutPickupFromStroe.setVisibility(View.GONE);
//                }
//            }
//        });


        btnDatePicker = (Button) findViewById(R.id.btn_date);
        btnTimePicker = (Button) findViewById(R.id.btn_time);
        txtDate = (EditText) findViewById(R.id.in_date);
        txtTime = (EditText) findViewById(R.id.in_time);

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
                                if (dayOfMonth < 10) {
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
                                if (hourOfDay < 10) {
                                    sHourOfDay = "0" + Integer.toString(hourOfDay);
                                }

                                String sMinute = Integer.toString(minute);
                                if (minute < 10) {
                                    sMinute = "0" + Integer.toString(minute);
                                }
                                txtTime.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });


        if(mNetPayable == 0){
            btnCOD.setVisibility(View.GONE);
            btnPrepaid.setText("Place Order");
        }

    }

    private void setDeliveryAddress() {
        TextView txtName = findViewById(R.id.txtName_Checkout);
        txtName.setText(CommonVariables.deliveryAddress.Name);

        TextView txtAddressLine1 = findViewById(R.id.txtAddresLine1_Checkout);
        txtAddressLine1.setText(CommonVariables.deliveryAddress.AddressLine1);

        TextView txtAddressLine2 = findViewById(R.id.txtAddresLine2_Checkout);
        txtAddressLine2.setText(CommonVariables.deliveryAddress.AddressLine2);

        TextView txtAddressLine3 = findViewById(R.id.txtAddresLine3_Checkout);
        txtAddressLine3.setText(CommonVariables.deliveryAddress.AddressLine3);

        TextView txtLandmark = findViewById(R.id.txtLandmark_Checkout);
        if(CommonVariables.deliveryAddress.Landmark != null){
            if (CommonVariables.deliveryAddress.Landmark.equals(""))
                txtLandmark.setVisibility(View.GONE);
            else
                txtLandmark.setText("Landmark: " + CommonVariables.deliveryAddress.Landmark);
        }
        else {
            txtLandmark.setVisibility(View.GONE);
        }



        TextView pinCode = findViewById(R.id.txtPincode_Checkout);
        pinCode.setText("Pincode: " + CommonVariables.deliveryAddress.Pincode);

        TextView txtCity = findViewById(R.id.txtCity_Checkout);
        txtCity.setText(CommonVariables.deliveryAddress.City);

        TextView txtState = findViewById(R.id.txtState_Checkout);
        txtState.setText(CommonVariables.deliveryAddress.State);

        TextView txtMobile = findViewById(R.id.txtContact_Checkout);
        txtMobile.setText("Contact No. " + CommonVariables.deliveryAddress.Phone);

        if(CommonVariables.deliveryAddress.AddressLine2.equals("")){
            txtAddressLine2.setVisibility(View.GONE);
        }

        if(CommonVariables.deliveryAddress.AddressLine3.equals("")){
            txtAddressLine3.setVisibility(View.GONE);
        }
    }


    public void onCOD_Click(View view) {

//        Intent i = new Intent(this,codPopup.class);
//        startActivityForResult(i, RESULT_CODE);

        processCOD();
    }

    private void processCOD() {

        if (chkPickupFromStore.isChecked()) {
            if (txtDate.getText().toString().trim().equals("")) {
                Toast.makeText(this, "Please select pickup date", Toast.LENGTH_SHORT).show();
                return;
            }

            if (txtTime.getText().toString().trim().equals("")) {
                Toast.makeText(this, "Please select pickup time", Toast.LENGTH_SHORT).show();
                return;
            }

            pickupTimeStamp = txtDate.getText().toString() + " " + txtTime.getText().toString();
        } else {
            pickupTimeStamp = null;
        }

        if (!allOrdersCODEligible()) {
            Toast.makeText(this, "One or more products in the cart are not eligible for COD.\nHence COD is not possible for this order", Toast.LENGTH_LONG).show();
            return;
        }
        m_bCOD = true;
        //        //if wallet money was used update the balance points first
        if (mWalletMoneyUsed > 0) {
            double points = mWalletMoneyUsed * CommonVariables.NumberOfPointsInOneRupee;
            Log.d("wallet_money_test", "wallet_money used process cod: " + points);
            CommonMethods.debitPoints(points, sOrderId);
        }


        //if wallet money was used and entire wallet money was sufficient for the order
        if (mWalletMoneyUsed > 0 && mNetPayable == 0) {

            if (!createOrder(false, "fully_paid_by_wallet")) {
                updatePaymentStatusOfUser("Success");
                return;
            }
            //Toast.makeText(this, "Order Created. Taking to Home Page.", Toast.LENGTH_SHORT).show();
            CommonVariables.cartlist.clear();
            launchOrderPlaced();
            //LaunchLandingPage();


        }

        if (!createOrder(true, null)) {
            return;
        }

        deleteCart();


       // Toast.makeText(this, "Order Created. Taking to Home Page.", Toast.LENGTH_SHORT).show();
        CommonVariables.cartlist.clear();

        updatePaymentStatusOfUser("NA");


        //LaunchLandingPage();
        launchOrderPlaced();

    }

    public void onPayNow_Click(View view) {
        Log.d("value_here", "text: " + btnPrepaid.getText().toString());
        processPrepaid();
//        if (btnPrepaid.getText().toString().equals("Place Order")) {
//            processPrepaid();
//        } else {
//            Toast.makeText(this, "Prepaid payments coming soon!!", Toast.LENGTH_LONG).show();
//        }
    }

    private void processPrepaid() {
        if (chkPickupFromStore.isChecked()) {
            if (txtDate.getText().toString().trim().equals("")) {
                Toast.makeText(this, "Please select pickup date", Toast.LENGTH_SHORT).show();
                return;
            }

            if (txtTime.getText().toString().trim().equals("")) {
                Toast.makeText(this, "Please select pickup time", Toast.LENGTH_SHORT).show();
                return;
            }

            pickupTimeStamp = txtDate.getText().toString() + " " + txtTime.getText().toString();
        } else {
            pickupTimeStamp = null;
        }


//        //if wallet money was used update the balance points first
        if (mWalletMoneyUsed > 0) {
            double points = mWalletMoneyUsed * CommonVariables.NumberOfPointsInOneRupee;
            CommonMethods.debitPoints(points, sOrderId);
            Log.d("wallet_money_test", "wallet_money used process predpaid: " + points);
        }


        //if wallet money was used and entire wallet money was sufficient for the order
        if (mWalletMoneyUsed > 0 && mNetPayable == 0) {

            if (!createOrder(false, "fully_paid_by_wallet")) {
                updatePaymentStatusOfUser("Success");
                return;
            }
           // Toast.makeText(this, "Order Created. Taking to Home Page.", Toast.LENGTH_SHORT).show();
            CommonVariables.cartlist.clear();
            //LaunchLandingPage();
            launchOrderPlaced();

        } else {
            if (!createOrder(false, null)) {
                return;
            }
            deleteCart();
            Button btnPay = findViewById(R.id.btnPayNow);
            btnPay.setVisibility(View.GONE);
            //Toast.makeText(this, "Order Created. Taking to Payment Page.", Toast.LENGTH_SHORT).show();

            startPayment();
        }
    }

    private boolean allOrdersCODEligible() {
        boolean bRet = true;
        for (Cart cart : CommonVariables.cartlist) {
            if (cart.product.COD == false) {
                bRet = false;
                break;
            }
        }
        return bRet;
    }

    //if any of the products added are not added from store hide pickup from store button
    private boolean hidePickupFromStoreButton() {
        boolean bRet = false;
        for (Cart cart : CommonVariables.cartlist) {
            if (cart.product.is_city_product == false) {
                bRet = true;
                break;
            }
        }
        return bRet;
    }

    private boolean createOrder(boolean cod, String sPaymentId) {

        Log.d("create_order", "delivery date" + deliveryDate);

        if (deliveryDate == null || deliveryTimeSlot == null) {
            Toast.makeText(mContext, "Same date delivery is not available. Please select any other date", Toast.LENGTH_SHORT).show();
            return false;
        }

        double pointsToIncrement = CommonMethods.calculatePointsAgainstPurchase(mNetPayable);
        //no wallet money to be credited if wallet moeny being used..
        if (mWalletMoneyUsed == 0) {
            //creditPoints();
            pointsToIncrement = 0;
        }

        m_bOrderCreated = false;

        for (Cart cart : CommonVariables.cartlist) {

            CheckoutCart chCart = null;
            for (CheckoutCart checkoutCart : lstCheckoutCart) {
                if (checkoutCart.sellerId.equals(cart.product.seller_id)) {
                    chCart = checkoutCart;
                    break;
                }
            }

            if (chCart == null) {
                chCart = new CheckoutCart();
                chCart.sellerId = cart.product.seller_id;
                cart.product.CartQty = cart.Qty;
                cart.product.selectedVariants = cart.Variants;
                chCart.productList.add(cart.product);
                lstCheckoutCart.add(chCart);
            } else {
                cart.product.CartQty = cart.Qty;
                cart.product.selectedVariants = cart.Variants;
                chCart.productList.add(cart.product);
            }
        }

        Orders order = new Orders();


        for (CheckoutCart checkoutCart : lstCheckoutCart) {

//            String id_start = CommonVariables.loggedInUserDetails.Name.substring(0, 2).toUpperCase();
//            long id_end = System.currentTimeMillis()/1000;
//
//
//            //String sOrderId = UUID.randomUUID().toString();
//            String sOrderId = id_start.trim() + id_end;

            orderIdList.add(sOrderId);

            checkoutCart.orderId = sOrderId;
            DocumentReference docOrder = db.collection("online_orders").document(sOrderId);

            CommonVariables.paymentStatus = cod;

            order.COD = cod;
            order.offline_order = false;
            order.Status = "Order received. Seller Confirmation pending.";
            order.cancelled = false;
            order.returned = false;
            order.replacement_order = false;
            order.customer_id = CommonVariables.m_sFirebaseUserId;
            order.fcm = CommonVariables.loggedInUserDetails.fcm;
            order.delivery_date = null;

            Date dt = new Date();
            Timestamp timestamp = new Timestamp(dt);

            orderId = sOrderId;
            order.order_date = dt;
            order.cancellation_reason = null;
            order.order_id = sOrderId;
            order.seller_id = checkoutCart.sellerId;
            order.payment_id = sPaymentId;
            order.points_added = pointsToIncrement;
            order.invoice_id = null;
            order.wallet_money_used = mWalletMoneyUsed;
            order.prepaid_cancellation_processed = false;
            order.settlement_done = false;
            order.settlement_date = null;
            order.delivery_partner_id = null;
            order.pickup_rejection_reason = null;
            order.pickup_status = null;
            order.delivery_charges = mDeliveryCharges;
            order.requested_delivery_date = deliveryDate;
            order.requested_delivery_time = deliveryTimeSlot;
            order.pickup_from_store = chkPickupFromStore.isChecked();
            order.anytime_slot_bonus = anytimeSlotBonus;


            order.Name = CommonVariables.deliveryAddress.Name;
            order.Email = CommonVariables.loggedInUserDetails.Email;
            order.Phone = CommonVariables.deliveryAddress.Phone;
            order.AddressLine1 = CommonVariables.deliveryAddress.AddressLine1;
            order.AddressLine2 = CommonVariables.deliveryAddress.AddressLine2;
            order.AddressLine3 = CommonVariables.deliveryAddress.AddressLine3;
            order.Landmark = CommonVariables.deliveryAddress.Landmark;
            order.Pincode = CommonVariables.deliveryAddress.Pincode;
            order.City = CommonVariables.deliveryAddress.City;
            order.State = CommonVariables.deliveryAddress.State;
            order.user_loc_lat = CommonVariables.deliveryAddress.user_loc_lat;
            order.user_loc_long = CommonVariables.deliveryAddress.user_loc_long;

            order.total_return_amount = 0;

            order.order_no = CommonVariables.user_orders + 1;

            Log.d("value_here", String.valueOf(anytimeSlotBonus));


//            Map<String, Object> data1 = new HashMap<>();
//
//            docOrder.set(data1);

            for (Product product : checkoutCart.productList) {

                double dprice = (double) product.Offer_Price * product.CartQty;
                double ptsToIncrement = dprice * CommonVariables.points_to_credit_per_rupee;
                if (mWalletMoneyUsed > 0) {
                    ptsToIncrement = 0;
                }


                //DocumentReference docProduct = db.collection("orders").document(sOrderId).collection("products").document(product.Product_Id);


                order.product_ids.add(product.Product_Id);
                order.product_titles.add(product.Title);
                order.seller_ids.add(product.seller_id);

                double price = CommonMethods.getOfferPrice(product.CartQty, product);
                double mrp = CommonMethods.getMrpPrice(product.CartQty, product);

//              price = Math.round(price);
                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                float roundPrice = Float.valueOf(decimalFormat.format(price));

                String str_price = decimalFormat.format(price);
                String str_mrp = decimalFormat.format(mrp);

                Log.d("product_price", "checkout" + String.valueOf(price));




                Log.d("roundPrice", String.valueOf(roundPrice));

                order.product_offer_prices.add(Double.parseDouble(str_price));

                // net order amount
                order_net_amount += Double.parseDouble(str_price);
                order.product_mrp_list.add(Double.parseDouble(str_mrp));


                order.product_gst_list.add(product.GST);
                order.product_qty_list.add((long) product.CartQty);
                order.img_url_list.add(product.ImageUrlCover);
                order.product_category_list.add(product.Category);
                order.product_returning_window_list.add(product.returning_window);
                order.product_return_request.add(false);
                order.product_return_processed.add(false);
                order.product_return_rejected.add(false);
                order.seller_return_remarks.add(null);
                order.return_reasons.add(null);
                order.return_amount.add((long) 0);
                order.product_status_list.add("OK");
                order.replacement_requested.add(false);
                order.cancelled_by.add(null);
                order.referred_user_fcm = null;
                order.cancellation_reasons.add(null);

                StringBuilder sVariants = new StringBuilder();

                if (product.selectedVariants != null) {
                    if (product.selectedVariants.size() > 0) {
                        for (Map.Entry<String, String> entry : product.selectedVariants.entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue();
                            sVariants.append(key).append(": ").append(value).append(", ");
                        }
                        sVariants = new StringBuilder(sVariants.substring(0, sVariants.length() - 2));
                        order.product_variants.add(sVariants.toString());

                    }
                } else {
                    order.product_variants.add(null);
                }

                if(CommonVariables.loggedInUserDetails.first_order_id == null){
                    User customer = CommonVariables.loggedInUserDetails;
                    customer.first_order_id = null;
                    customer.referral_claimed = false;
                    order.referred_user_fcm = CommonVariables.loggedInUserDetails.referee_fcm;
                    db.collection("users").document(customer.customer_id).update("first_order_id", order.order_id).
                            addOnCompleteListener(task -> {
                                docOrder.set(order);
                                updateOrderTimestamp();
                            });
                }
                else {
                    docOrder.set(order);
                    updateOrderTimestamp();
                }

            }
            if (offlineProducts != null) {
                for (OfflineProduct product : offlineProducts) {
                    DocumentReference docOfflineProducts = db.collection("orders").document(sOrderId).collection("offline_products").document();
                    Map<String, Object> dataProduct = new HashMap<>();
                    dataProduct.put("productTitle", product.getProductTitle());
                    dataProduct.put("productQuantity", product.getProductQuantity());
                    dataProduct.put("pureOfflineOrder", product.isPureOfflineOrder());
                    dataProduct.put("orderId", sOrderId);
                    docOfflineProducts.set(dataProduct);
                }
            }
        }


        m_bOrderCreated = true;
        return true;

        //  sendEmail();

    }

    private void updateOrderTimestamp() {

        DocumentReference washingtonRef = db.collection("users").document(CommonVariables.loggedInUserDetails.customer_id);

// Set the "isCapital" field of the city 'DC'
        washingtonRef
                .update("last_order_date", FieldValue.serverTimestamp())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w(TAG, "Error updating document", e);
                    }
                });

    }

    private void updatePaymentStatusOfUser(String status) {

        DocumentReference washingtonRef = db.collection("users").document(CommonVariables.loggedInUserDetails.customer_id);

// Set the "isCapital" field of the city 'DC'
        washingtonRef
                .update("last_payment_status", status)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w(TAG, "Error updating document", e);
                    }
                });

    }


    public void startPayment() {
        Checkout checkout = new Checkout();
        checkout.setKeyID(CommonVariables.razorPayKeyId);
        /**
         * Instantiate Checkout
         */

        /**
         * Set your logo here
         */
        // checkout.setImage(R.drawable.logo);

        /**
         * Reference to current activity
         */
        final Activity activity = this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            /**
             * Merchant Name
             * eg: ACME Corp || HasGeek etc.
             */
            options.put("name", "Doorish Pvt. Ltd.");

            /**
             * Description can be anything
             * eg: Reference No. #123123 - This order number is passed by you for your internal reference. This is not the `razorpay_order_id`.
             *     Invoice Payment
             *     etc.
             */
            options.put("description", "Reference No. 123456");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            //options.put("order_id", "order_9A33XWu170gUtm");
            options.put("currency", "INR");

            /**
             * Amount is always passed in currency subunits
             * Eg: "500" = INR 5.00
             */

            int iNetPayable = (int) (mNetPayable * 100);
//            options.put("amount", mNetPayable * 100);
            options.put("amount", iNetPayable);

            checkout.open(this, options);
        } catch (Exception e) {
            Log.e("TAG", "Error in starting Razorpay Checkout", e);
        }
    }

    private void capturePayment(String sPaymentId) {
        JSONObject options = new JSONObject();
        try {
            options.put("amount", 100);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RazorpayClient razorpayClient = null;
        try {
            razorpayClient = new RazorpayClient(CommonVariables.razorPayKeyId, CommonVariables.razorPayKeySecret);
        } catch (RazorpayException e) {
            e.printStackTrace();
        }
        try {
            razorpayClient.Payments.capture(sPaymentId, options);
        } catch (RazorpayException e) {
            e.printStackTrace();
        }
    }

//    public void updatePoints( int points) {
//        DocumentReference washingtonRef = db.collection("users").document(CommonVariables.m_sFirebaseUserId);
//
//// Set the "isCapital" field of the city 'DC'
//        washingtonRef
//                .update("points", points)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        //  Log.d(TAG, "DocumentSnapshot successfully updated!");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        //  Log.w(TAG, "Error updating document", e);
//                    }
//                });
//
//    }


    @Override
    public void onPaymentSuccess(final String sPaymentId) {

        for (String sOrderId : orderIdList) {

            DocumentReference docOrder = db.collection("online_orders").document(sOrderId);

            docOrder
                    .update("payment_id", sPaymentId)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //Log.d(TAG, "DocumentSnapshot successfully updated!");
                            // capturePayment(sPaymentId);
                            updatePaymentStatusOfUser("Success");
                            CapturePayment objCapturePayment = new CapturePayment();
                            objCapturePayment.paymentId = sPaymentId;
                            objCapturePayment.Amount = (int) mNetPayable * 100;
                            new CapturePaymentTask().execute(objCapturePayment);
                            Toast.makeText(mContext, "Payment Confirmation Done", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Log.w(TAG, "Error updating document", e);
                            Toast.makeText(mContext, "Payment Confirmation Failed", Toast.LENGTH_SHORT).show();
                        }
                    });


        }


        CommonVariables.cartlist.clear();
        //LaunchLandingPage();
       launchOrderPlaced();

    }

    private void deleteCart() {

//        if(CommonVariables.buyNowOption){
//            m_bCartDeleted = true;
//            return;
//        }
//
//        db.collection("users").document(CommonVariables.m_sFirebaseUserId).collection("cart")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                String id = document.getId();
//
//                                db.collection("users").document(CommonVariables.m_sFirebaseUserId).collection("cart").document(id)
//                                        .delete()
//                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                            @Override
//                                            public void onSuccess(Void aVoid) {
//                                                Log.d("TAG", "DocumentSnapshot successfully deleted!");
//                                                m_bCartDeleted = true;
//
//                                            }
//                                        })
//                                        .addOnFailureListener(new OnFailureListener() {
//                                            @Override
//                                            public void onFailure(@NonNull Exception e) {
//                                                Log.w("TAG", "Error deleting document", e);
//                                            }
//                                        });
//
//                                Log.d("cartid", id);
//                                //  Log.d(TAG, document.getId() + " => " + document.getData());
//                            }
//                        } else {
//                            Log.d("TAG", "Error getting documents: ", task.getException());
//                        }
//                    }
//                });
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
        updatePaymentStatusOfUser("Fail");
        double amount = mNetPayable - mDeliveryCharges;

        String orderId = orderIdList.get(0);

        //debit the points earned (2.5% of the purchase amount)
        double pointsEarned = CommonMethods.calculatePointsAgainstPurchase(amount);
//        CommonMethods.debitPointsDirect(pointsEarned, this);

        //deleting order if payment fails
        db.collection("online_orders").document(orderId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("order_deleted", "order deleted for failed payment");
            }
        });

        //if any wallet money used during the purchase credit the points straightaway..
        if (mWalletMoneyUsed > 0) {
            double pointsUsedAgainstWalletMoney = mWalletMoneyUsed * CommonVariables.NumberOfPointsInOneRupee;
            //CommonMethods.creditPointsDirect(pointsUsedAgainstWalletMoney, this);
        }
    }

    private void LaunchLandingPage() {
        CommonVariables.buyNowOption = false;
        Intent intent = new Intent(CheckoutActivity.this, LandingPage.class);
        //prevent the user to come again to this screen in he presses back btton
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);

        intent.putExtra("page", "order_created");

        startActivity(intent);
    }

    private void launchOrderPlaced() {

        CommonVariables.user_orders++;

        Log.d("user_orders", "cashback credited for " + order_net_amount);
        CommonVariables.buyNowOption = false;
        Intent intent = new Intent(CheckoutActivity.this, OrderPlacedActivity.class);
        intent.putExtra("order_id", orderId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("page", "order_created");

        startActivity(intent);

        // credit the cashback for first 3 orders
//        int cashback = 0;
//        if(CommonVariables.user_orders < 3){
//            if(order_net_amount > 200)
//                cashback = 200;
//            else cashback = Integer.parseInt(String.valueOf(order_net_amount));
//
//            db.collection("users").document(CommonVariables.loggedInUserDetails.customer_id).update("points", FieldValue.increment(cashback))
//                    .addOnCompleteListener(task -> {
//
//
//                    });
//        }


    }

    void UseWallet(boolean use) {
        if (use) {
            //COD option not available while using wallet
            //btnCOD.setVisibility(View.GONE);

            //when wallet money available is more than net payable
            if (mAvailableWalletMoney >= mNetPayable) {
                mWalletMoneyUsed = mNetPayable;
                //1 rupee = 8 points
                mRemainingWalletPoints = (int) CommonVariables.loggedInUserDetails.points - (mNetPayable * CommonVariables.NumberOfPointsInOneRupee);
                btnCOD.setVisibility(View.GONE);
                btnPrepaid.setText("Place Order");
                mNetPayable = 0;


            } else {
                mWalletMoneyUsed = mAvailableWalletMoney;
                //all points are consumed
                double dPointsConsumed = CommonVariables.loggedInUserDetails.points;
                mRemainingWalletPoints = 0;
                mNetPayable = mNetPayable - mWalletMoneyUsed;

            }

            String sNetPayable = CommonVariables.rupeeSymbol + CommonMethods.formatCurrency(mNetPayable);
            txtNetPayable.setText(sNetPayable);
            double iReaminingInWallet = mAvailableWalletMoney - mWalletMoneyUsed;
            String sAVailable = "Wallet Money: " + CommonVariables.rupeeSymbol + CommonMethods.formatCurrency(iReaminingInWallet);
            txtWalletMoney.setText(sAVailable);

            Log.d("value_here", String.valueOf(iReaminingInWallet));

            CommonVariables.loggedInUserDetails.points = mRemainingWalletPoints;
        } else {

            mWalletMoneyUsed = 0;
            mNetPayable = mOriginalNetPayable;
            mAvailableWalletMoney = mOriginalAvailableWalletMoney;
            mRemainingWalletPoints = mOriginalRemainingWalletPoints;

            String sAVailable = "Wallet Money: " + CommonVariables.rupeeSymbol + CommonMethods.formatCurrency(mAvailableWalletMoney);
            txtWalletMoney.setText(sAVailable);

            String sNetPayable = CommonVariables.rupeeSymbol + CommonMethods.formatCurrency(mNetPayable);
            txtNetPayable.setText(sNetPayable);

            CommonVariables.loggedInUserDetails.points = mRemainingWalletPoints;
            btnCOD.setVisibility(View.VISIBLE);
        }

    }

    private void creditPoints() {

        //if wallet money is not used then only credit points...
        if (mNetPayable > 0) {
            //credit will not be given on delivery charges..
            double amountAgainstCredit = mNetPayable - mDeliveryCharges;
            // CommonMethods.creditPoints(amountAgainstCredit, this);
        }

    }

    private void showAvailableWalletMoney() {

        DocumentReference docRef = db.collection("users").document(CommonVariables.m_sFirebaseUserId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        CommonVariables.loggedInUserDetails = document.toObject(User.class);
                        if (CommonVariables.loggedInUserDetails != null) {

                            mAvailableWalletPoints = CommonVariables.loggedInUserDetails.points;
                            //8 points make one rupee
                            mAvailableWalletMoney = mAvailableWalletPoints / CommonVariables.NumberOfPointsInOneRupee;
                            mOriginalAvailableWalletMoney = mAvailableWalletMoney;
                            String sWalletMoney = "Wallet Money: " + CommonVariables.rupeeSymbol + CommonMethods.formatCurrency(mAvailableWalletMoney);
                            // CommonVariables.rupeeSymbol + CommonMethods.formatCurrency(mAvailableWalletMoney);
                            txtWalletMoney.setText(sWalletMoney);
                        }

                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });

    }

    private void showPopup() {
        Intent intent = new Intent(this, codPopup.class);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (0): {
                if (resultCode == Activity.RESULT_OK) {
                    String newText = data.getStringExtra("value");
                    if (newText.trim().equals("1")) {
                        processCOD();
                    }
                    if (newText.trim().equals("0")) {
                        processPrepaid();
                    }

                }
                break;
            }
        }
    }


    @Override
    public void onDateSelected(DateTime dateSelected) {
        String day = String.valueOf(dateSelected.getDayOfMonth());
        String month = CommonMethods.getMonthName(dateSelected.getMonthOfYear());
        String year = String.valueOf(dateSelected.getYear()).substring(Math.max(2, 0));

        deliveryDate = day + " " + month + " " + year;

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        String todayDate = String.valueOf(Integer.parseInt(String.valueOf(DateFormat.format("dd", today))));


        if (day.equals(todayDate)) {
            if (!preSelected) {
                deliveryDate = null;
                Toast.makeText(this, "Same date delivery is not available. Please select any other date", Toast.LENGTH_LONG).show();
                return;
            }
            deliveryDate = null;
        }

        recyclerViewDeliverySlots.setHasFixedSize(true);
        recyclerViewDeliverySlots.setLayoutManager(new LinearLayoutManager(this));
        adapterDeliverySlots = new AdapterDeliverySlots(this, CommonVariables.slot_timings, CommonVariables.availability, CheckoutActivity.this);
        recyclerViewDeliverySlots.setAdapter(adapterDeliverySlots);
        preSelected = false;
    }

    @Override
    public void selectedTimeSlot(String timeSlot) {
        deliveryTimeSlot = timeSlot;
        Log.d("bonus_slot_selected", timeSlot+ " selected");
        if (timeSlot.contains("Anytime")) {
            anytimeSlotBonus = true;
            Log.d("bonus_slot_selected", "selectedTimeSlot: " + true);
        }
        else {
            anytimeSlotBonus = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDeliveryAddress();
        CommonVariables.startedByActivity = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}

