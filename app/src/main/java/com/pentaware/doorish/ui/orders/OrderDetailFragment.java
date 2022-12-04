package com.pentaware.doorish.ui.orders;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.pentaware.doorish.AdapterProductImages;
import com.pentaware.doorish.AdapterViewOrderedProducts;
import com.pentaware.doorish.BaseFragment;
import com.pentaware.doorish.CommonVariables;
import com.pentaware.doorish.LandingPage;
import com.pentaware.doorish.Login;
import com.pentaware.doorish.ShowPrivacyPolicyActivity;
import com.pentaware.doorish.TrackOrderActivity;
import com.pentaware.doorish.model.Address;
import com.pentaware.doorish.model.Orders;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pentaware.doorish.R;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OrderDetailFragment extends BaseFragment implements AdapterViewOrderedProducts.IViewProductOperations {

    private Orders mOrder;
    private View mView;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();
    private Button btnTrackOrder, btnViewProducts;
    private EditText txtCancelReason;
    private RecyclerView recyclerViewProductImages;
    private LinearLayout layoutOrderButtons;

    private LinearLayout layoutRefundText;
    private TextView txtRefundTip;
    private View viewRefundText;
    private NestedScrollView orderScrollView;

    private AdapterViewOrderedProducts adapterViewOrderedProducts;

    TextView txtOrderStatus;
    private ImageButton btnExpandOrderDetails;
    private LinearLayout layoutOrderDetails;
    private boolean hidden = false;
    private TextView txtOrderDetails;

    public OrderDetailFragment(Orders order) {
        mOrder = order;
    }

    public static OrderDetailFragment newInstance(Orders order) {
        return new OrderDetailFragment(order);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.order_detail_fragment, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        boolean bReturnable = false;


        recyclerViewProductImages = mView.findViewById(R.id.recycler_view_product_images);
        recyclerViewProductImages.setHasFixedSize(true);
        recyclerViewProductImages.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        layoutOrderButtons = mView.findViewById(R.id.layout_order_buttons);

        AdapterProductImages adapter = new AdapterProductImages(mOrder, getContext());
        recyclerViewProductImages.setAdapter(adapter);

        layoutOrderDetails = mView.findViewById(R.id.layout_order_details);
        btnExpandOrderDetails = mView.findViewById(R.id.btn_expand_order_details);
        txtOrderDetails = mView.findViewById(R.id.txt_order_details);

        orderScrollView = mView.findViewById(R.id.order_scroll_view);

        layoutRefundText = mView.findViewById(R.id.layout_refund_text);
        txtRefundTip = mView.findViewById(R.id.txt_refund_tip);
        viewRefundText = mView.findViewById(R.id.view_refund_text);

        btnTrackOrder = mView.findViewById(R.id.btn_track_order);
        btnViewProducts = mView.findViewById(R.id.btn_view_products);

        btnViewProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewProducts();
            }
        });

        if (mOrder.Status.equals("Order Confirmed. Preparing for Dispatch") || mOrder.Status.equals("Order received. Seller Confirmation pending.")) {
            btnTrackOrder.setVisibility(View.VISIBLE);
        } else {
            btnTrackOrder.setVisibility(View.GONE);
        }




        btnTrackOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TrackOrder();
            }
        });


        String title;
        String productTitle = mOrder.product_titles.get(0);

        if (mOrder.product_titles.size() > 1) {
            title = productTitle + " and " + (mOrder.product_titles.size() - 1) + " others";
        } else {
            title = productTitle;
        }


        final Button btnReplaceOrder = (Button) mView.findViewById(R.id.btnReplaceOrder_OrderDetail);
        Button btnStopCancellation = mView.findViewById(R.id.btn_stop_cancellation);


        final LinearLayout layoutCancel = (LinearLayout) mView.findViewById(R.id.cancelLayout);
        layoutCancel.setVisibility(View.GONE);


        //Order Details

        setOrderDetails();


        //Ordered Items
        TextView txtOrderItems = mView.findViewById(R.id.txt_order_items);
        String orderItems = "Order Items (" + mOrder.product_ids.size() + ")";
        txtOrderItems.setText(orderItems);

        RecyclerView recyclerViewOrderedProducts = mView.findViewById(R.id.recycler_view_ordered_products);
        recyclerViewOrderedProducts.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapterViewOrderedProducts = new AdapterViewOrderedProducts(mOrder, getContext(), this);
        recyclerViewOrderedProducts.setAdapter(adapterViewOrderedProducts);


        //Delivery Address
        TextView txtLineAddress = mView.findViewById(R.id.txt_line_address);
        TextView txtCityStateAddress = mView.findViewById(R.id.txt_city_state_address);

        Address address = CommonVariables.deliveryAddress;
        String lineAddress = address.Landmark + ", " + address.AddressLine1 + ", " + address.AddressLine2 + ", " + address.AddressLine3;
        String cityStateAddress = address.City + ", " + address.State + ", " + address.Pincode;

        txtLineAddress.setText(lineAddress);
        txtCityStateAddress.setText(cityStateAddress);


        final Button btnCancel = (Button) mView.findViewById(R.id.btnCancelOrder_OrderDetail);

        if (mOrder.Status.equals("Order received. Seller Confirmation pending.")) {
            btnCancel.setVisibility(View.VISIBLE);
        } else {
            btnCancel.setVisibility(View.GONE);
        }

        final Button btnSubmitCancel = (Button) mView.findViewById(R.id.btnCancellationReason);

        final boolean finalBReturnable1 = bReturnable;
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtCancelReason.setVisibility(View.VISIBLE);
                btnSubmitCancel.setVisibility(View.VISIBLE);
                layoutCancel.setVisibility(View.VISIBLE);

                //scrolling to bottom
                orderScrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        orderScrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });

                if (btnCancel.getText().toString().equals("Stop Cancel")) {
                    txtCancelReason.setText("");
                    layoutCancel.setVisibility(View.GONE);
                    btnCancel.setText("Cancel Order");
                } else {
                    btnCancel.setText("Stop Cancel");

                }
            }
        });

        btnStopCancellation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtCancelReason.setText("");
                layoutCancel.setVisibility(View.GONE);
                btnCancel.setVisibility(View.VISIBLE);
                btnStopCancellation.setVisibility(View.GONE);
            }
        });


        txtCancelReason = (EditText) mView.findViewById(R.id.txtCancellationReason);
        if (bReturnable) {
            txtCancelReason.setHint("Enter Return/Replacement Reason");
        }
        final boolean finalBReturnable = bReturnable;
        btnSubmitCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (btnSubmitCancel.getText().toString().equals("Raise Replacement Request")) {
                    Toast.makeText(mView.getContext(), "Replacement Request Raised", Toast.LENGTH_SHORT).show();

                } else {

                    if (txtCancelReason.getText().toString().equals("")) {
                        txtCancelReason.setError("Please enter cancellation reason");
                        return;
                    }

                    SubmitCancelReason(txtCancelReason.getText().toString());
                    layoutCancel.setVisibility(View.GONE);

                }
            }
        });


        btnExpandOrderDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandOrderDetails();
            }
        });

        txtOrderDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandOrderDetails();
            }
        });

    }

    private void setOrderDetails() {

        //Customer Name
        TextView txtCustomerName = mView.findViewById(R.id.txt_customer_name);
        String customerName = CommonVariables.loggedInUserDetails.Name;
        txtCustomerName.setText(customerName);

        //Order Id
        TextView txtOrderId = mView.findViewById(R.id.txt_order_id);
        String orderId = mOrder.order_id;
        txtOrderId.setText(orderId);

        //Order Date
        TextView txtOrderDate = mView.findViewById(R.id.txt_order_date);
        Date dtOrder = mOrder.order_date;
        SimpleDateFormat date = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat time = new SimpleDateFormat("hh:mm:ss aa");
        String strDate = date.format(dtOrder);
        String strTime = time.format(dtOrder);
        String dateAndTime = strDate + " at " + strTime;
        txtOrderDate.setText(dateAndTime);

        //Product Total
        TextView txtProductTotal = mView.findViewById(R.id.txt_product_total);
        double productTotal = 0;
        for (int i = 0; i < mOrder.product_ids.size(); i++) {
            if (!mOrder.product_status_list.get(i).equals("cancelled")) {
                productTotal += mOrder.product_mrp_list.get(i) * mOrder.product_qty_list.get(i);

            }
        }
        String strProductTotal = CommonVariables.rupeeSymbol + String.format(Locale.getDefault(), "%.2f", productTotal);
        txtProductTotal.setText(strProductTotal);

        //Order Amount
        TextView txtOfferPrice = mView.findViewById(R.id.txt_order_amount);
        double totalAmount = 0;
        for (int i = 0; i < mOrder.product_ids.size(); i++) {
            if (!mOrder.product_status_list.get(i).equals("cancelled")) {
                totalAmount = totalAmount + (mOrder.product_offer_prices.get(i) * mOrder.product_qty_list.get(i));
            }
        }
        totalAmount = totalAmount + mOrder.delivery_charges;
        String strOrderAmount = CommonVariables.rupeeSymbol + String.format(Locale.getDefault(), "%.2f", totalAmount);
        txtOfferPrice.setText(strOrderAmount);

        TextView txtOfferPrices = mView.findViewById(R.id.txt_offer_price);
        double offerPrice = totalAmount - mOrder.delivery_charges;
        String strOfferPrice = CommonVariables.rupeeSymbol + String.format(Locale.getDefault(), "%.2f", offerPrice);
        txtOfferPrices.setText(strOfferPrice);

        TextView txtDiscount = mView.findViewById(R.id.txt_order_discount);
        double discount = productTotal - offerPrice;
        String strDiscount = CommonVariables.rupeeSymbol + String.format(Locale.getDefault(), "%.2f", discount);
        txtDiscount.setText(strDiscount);

        TextView txtDeliveryCharges = mView.findViewById(R.id.txt_delivery_fee);
        String strDeliveryCharges = CommonVariables.rupeeSymbol + String.format(Locale.getDefault(), "%.2f", mOrder.delivery_charges);
        txtDeliveryCharges.setText(strDeliveryCharges);

        TextView txtWalletMoneyUsed = mView.findViewById(R.id.txt_wallet_money_used);
        String strWalletUsed = CommonVariables.rupeeSymbol + String.format(Locale.getDefault(), "%.2f", mOrder.wallet_money_used);
        txtWalletMoneyUsed.setText(strWalletUsed);

        TextView txtNetAmount = mView.findViewById(R.id.txt_net_amount);
        double netAmount = totalAmount - mOrder.wallet_money_used;

        if(netAmount < 0){
            netAmount = 0;
        }

        String strNetAmount = CommonVariables.rupeeSymbol + String.format(Locale.getDefault(), "%.2f", netAmount);
        txtNetAmount.setText(strNetAmount);


        //Payment Mode
        TextView txtPaymentMode = mView.findViewById(R.id.txt_payment_mode);
        String paymentMode;
        if (mOrder.COD) {
            paymentMode = "Cash On Delivery";
        } else {
            paymentMode = "Prepaid Order";
        }
        txtPaymentMode.setText(paymentMode);

        //Order Status
        txtOrderStatus = (TextView) mView.findViewById(R.id.txt_order_status);
        String sStatus = mOrder.Status;
        String statusSpan = sStatus;
        if (sStatus.equals("Order received. Seller Confirmation pending.") || sStatus.equals("Order Confirmed. Preparing for Dispatch") || sStatus.equals("Delivered")) {
            statusSpan = getColoredSpanned(statusSpan, "#27AE60");
        } else if (sStatus.equals("Cancelled") || sStatus.equals("Order Cancelled by Seller") || sStatus.equals("Order Cancelled by Customer")) {
            statusSpan = getColoredSpanned(statusSpan, "#ff0000");
        }
        txtOrderStatus.setText(Html.fromHtml(statusSpan));

        for (int i = 0; i < mOrder.product_ids.size(); i++) {
            if (mOrder.product_status_list.get(i).equals("cancelled") || mOrder.cancelled) {
                setRefundText();
                layoutRefundText.setVisibility(View.VISIBLE);
                viewRefundText.setVisibility(View.VISIBLE);

            }
        }

        if (mOrder.Status.equals("Cancelled") || mOrder.Status.equals("Order Cancelled by Seller") || mOrder.Status.equals("Order Cancelled by Customer")) {
            setRefundText();
            layoutRefundText.setVisibility(View.VISIBLE);
            viewRefundText.setVisibility(View.VISIBLE);
        }

    }

    private void setRefundText() {
        SpannableString SpanString = new SpannableString(
                "For partial or complete cancellation of order, Refund will be initiated to the original payment method, it will reflect in your account within 7 working days. Please refer Terms & Conditions.");

        ClickableSpan termsAndCondition = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent mIntent = new Intent(getContext(), ShowPrivacyPolicyActivity.class);
                mIntent.putExtra("showFragment", "term and conditions");
                startActivity(mIntent);
            }
        };

//            ClickableSpan privacyPolicy = new ClickableSpan() {
//                @Override
//                public void onClick(View textView) {
//                    Intent mIntent = new Intent(Login.this, ShowPrivacyPolicyActivity.class);
//                    mIntent.putExtra("showFragment", "privacy policy");
//                    startActivity(mIntent);
//                }
//            };

        SpanString.setSpan(termsAndCondition, 172, 191, 0);
//            SpanString.setSpan(privacyPolicy, 57, 71, 0);
        SpanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorLightBlue)), 172, 191, 0);
//            SpanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorLightBlue)), 57, 71, 0);
        SpanString.setSpan(new BackgroundColorSpan(Color.parseColor("#ffffff")), 0, 191, 0);
//        SpanString.setSpan(new UnderlineSpan(), 32, 52, 0);
//        SpanString.setSpan(new UnderlineSpan(), 57, 71, 0);

        txtRefundTip.setMovementMethod(LinkMovementMethod.getInstance());
        txtRefundTip.setText(SpanString, TextView.BufferType.SPANNABLE);
        txtRefundTip.setSelected(true);

    }

    public void expandOrderDetails() {
        if (hidden) {
            layoutOrderDetails.setVisibility(View.VISIBLE);
            btnExpandOrderDetails.setBackgroundResource(R.drawable.ic_expand_less_24px);
            hidden = false;
        } else {
            layoutOrderDetails.setVisibility(View.GONE);
            btnExpandOrderDetails.setBackgroundResource(R.drawable.ic_expand_more_24px);
            hidden = true;
        }
    }

    private void viewProducts() {
        Fragment newFragment = ViewOrderedProductsFragment.newInstance(mOrder);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.hide(this);
        transaction.add(R.id.nav_host_fragment, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void TrackOrder() {
        Orders order = mOrder;
        Intent intent = new Intent(getContext(), TrackOrderActivity.class);
        intent.putExtra("order_id", order.order_id);
        startActivity(intent);
    }


    private String getColoredSpanned(String text, String color) {
        return "<font color=" + color + ">" + text + "</font>";
    }

    private void SubmitCancelReason(String sReason) {

        for (int i = 0; i < mOrder.product_ids.size(); i++) {
            mOrder.product_offer_prices.set(i, 0d);
            mOrder.product_status_list.set(i, "cancelled");
        }

        DocumentReference docOrder = db.collection("online_orders").document(mOrder.order_id);

        docOrder.update(
                "cancellation_reason", sReason,
                "Status", "Cancelled",
                "cancelled", true,
                "product_offer_prices", mOrder.product_offer_prices,
                "product_status_list", mOrder.product_status_list
        )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(mView.getContext(), "Order Cancelled successfully", Toast.LENGTH_SHORT).show();
                        String statusSpan = getColoredSpanned("Cancelled", "#ff0000");
                        txtOrderStatus.setText(statusSpan);
                        LaunchLandingPage();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w(TAG, "Error updating document", e);
                    }
                });

    }


    @Override
    public void cancelProduct(Orders order, int index, String reason) {
        order.product_status_list.set(index, "cancelled");
        order.cancelled_by.set(index, "customer");
//        order.product_offer_prices.set(index, (double) 0);
        order.cancellation_reasons.set(index, reason);
        db.collection("online_orders").document(order.order_id).set(order).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getContext(), "Product cancelled", Toast.LENGTH_SHORT).show();
                adapterViewOrderedProducts.notifyDataSetChanged();
                int cancelledProducts = 0;
                double netPayable = 0;
                for (int i = 0; i < mOrder.product_ids.size(); i++) {
                    if (mOrder.product_status_list.get(i).equals("cancelled")) {
                        cancelledProducts++;
                    } else {
                        netPayable = netPayable + mOrder.product_offer_prices.get(i);
                    }
                }
                if (cancelledProducts == mOrder.product_ids.size()) {
                    cancelCompleteOrder(mOrder);
                } else {
                    if (netPayable < 100 && mOrder.delivery_charges == 0) {
                        debitDeliveryCharges();

                    }
                }
                setOrderDetails();


            }
        });
    }

    private void debitDeliveryCharges() {
        double wallet_money = CommonVariables.loggedInUserDetails.points - CommonVariables.deliveryCharges;
        db.collection("users").document(CommonVariables.loggedInUserDetails.customer_id).update("points", wallet_money);
    }

    @Override
    public void reviewProduct(Orders order, int index) {
        Fragment newFragment = ReviewProductFragment.newInstance(mOrder, index);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.hide(this);
        transaction.add(R.id.nav_host_fragment, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void returnProduct(Orders order, int index, String reason) {
        mOrder.Status = "Return requested";
        mOrder.product_status_list.set(index, "Return requested");
        mOrder.return_reasons.set(index, reason);
        db.collection("online_orders").document(mOrder.order_id).set(mOrder).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(), "Return request raised successfully", Toast.LENGTH_SHORT).show();
                getFragmentManager().popBackStack();
            }
        });
    }

    @Override
    public void replaceProduct(Orders order, int index, String reason) {
        mOrder.Status = "Replacement requested";
        mOrder.product_status_list.set(index, "Replacement requested");
        mOrder.return_reasons.set(index, reason);
        db.collection("online_orders").document(mOrder.order_id).set(mOrder).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(), "Replacement request raised successfully", Toast.LENGTH_SHORT).show();
                getFragmentManager().popBackStack();
            }
        });
    }

    private void cancelCompleteOrder(Orders mOrder) {
        mOrder.Status = "Order Cancelled by Customer";
        mOrder.cancelled = true;
        db.collection("online_orders").document(mOrder.order_id).set(mOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                adapterViewOrderedProducts.notifyDataSetChanged();
                Toast.makeText(getContext(), "Order Cancelled", Toast.LENGTH_SHORT).show();
                layoutOrderButtons.setVisibility(View.GONE);
                txtOrderStatus.setText(Html.fromHtml(getColoredSpanned("Cancelled", "#ff0000")));
                double totalAmountPayable = 0;
                for (int i = 0; i < mOrder.product_ids.size(); i++) {
                    double offerPrice = mOrder.product_offer_prices.get(i);
                    Long qty = mOrder.product_qty_list.get(i);
                    totalAmountPayable += offerPrice * qty;
                }
                setOrderDetails();

                double balance = totalAmountPayable - mOrder.wallet_money_used;
                if (balance < 0) {
//                    creditPoints(balance);
                }
            }
        });
    }

    private void creditPoints(double balance) {
        balance = Math.ceil(balance);
        balance = -balance;
        db.collection("users").document(mOrder.customer_id).update("points", balance).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("value_here", "updated successfully");
            }
        });
    }

    private void LaunchLandingPage() {
        CommonVariables.buyNowOption = false;
        Intent intent = new Intent(mView.getContext(), LandingPage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("page", "order_created");
        startActivity(intent);
    }
}



