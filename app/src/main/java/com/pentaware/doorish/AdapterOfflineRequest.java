package com.pentaware.doorish;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pentaware.doorish.model.MyRequirements;
import com.pentaware.doorish.model.Offline_Requests;
import com.pentaware.doorish.model.OrderAndProduct;
import com.pentaware.doorish.model.Orders;
import com.pentaware.doorish.model.Product;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AdapterOfflineRequest extends RecyclerView.Adapter<AdapterOfflineRequest.ViewHolder> {

    private List<OfflineRequestListing> m_lstOfflineRequests;
    private Context m_Context;
    private IOfflineRequests m_IOfflineRequest;

    public AdapterOfflineRequest(List<OfflineRequestListing> lstOfflineRequests, Context ctx, IOfflineRequests offlineRequests){
        m_lstOfflineRequests = lstOfflineRequests;
        m_Context = ctx;
        m_IOfflineRequest = offlineRequests;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_offline_request_listing, parent, false);
        return new AdapterOfflineRequest.ViewHolder(v, m_Context);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        OfflineRequestListing listItem = m_lstOfflineRequests.get(position);

        Offline_Requests offline_requests = listItem.getmOfflineRequest();

        Date dtOrder = offline_requests.timestamp;
        SimpleDateFormat sfd = new SimpleDateFormat("dd MMM yyyy");
        String dt1 = sfd.format(dtOrder);
        holder.txtOrderDate.setText(dt1);

        holder.txtCompanyName.setText(offline_requests.company_name);
        holder.txtAddresLine1.setText(offline_requests.seller_address_line1);
        holder.txtAddresLine2.setText(offline_requests.seller_address_line2);
        holder.txtAddresLine3.setText(offline_requests.seller_address_line3);
        String cityStatePin = offline_requests.seller_city + " (" + offline_requests.seller_state + ") Pin: " + offline_requests.seller_pin;
        holder.txttCityStatePin.setText(cityStatePin);
        String sPayByCash = "No";
        if(offline_requests.pay_by_cash){
            sPayByCash = "Yes";
        }
        holder.txtPayByCash.setText("Pay By Cash: " + sPayByCash);
//        holder.txtPhone.setText("Phone No: " + offline_requests.seller_phone);

        if (offline_requests.status_code == 0) {
            holder.txtOrderStatus.setText("Status: Pending For Seller Confirmation");
            holder.txtOrderStatus.setTextColor(Color.RED);
            holder.layoutAcceptReject.setVisibility(View.GONE);
        }

        if (offline_requests.status_code == 1) {
            holder.txtOrderStatus.setText("Status: Accepted by Seller and Pending for your Confirmation");
        }

        if (offline_requests.status_code == 2) {
            String status = "Status: Rejected by seller";
            if(offline_requests.seller_rejection_reason != null){
                status += " (Reason: " + offline_requests.seller_rejection_reason + ")";
            }
            holder.txtOrderStatus.setText(status);
            holder.txtOrderStatus.setTextColor(Color.RED);
            holder.layoutAcceptReject.setVisibility(View.GONE);

        }

        if (offline_requests.status_code == 3) {
            holder.txtOrderStatus.setText("Status: Accepted by you");
            holder.layoutAcceptReject.setVisibility(View.GONE);

        }

        if (offline_requests.status_code == 4) {
            String status = "Status: Rejected by you";
            if(offline_requests.buyer_rejection_reason != null){
                status += " (Reason: " + offline_requests.buyer_rejection_reason + ")";
            }
            holder.txtOrderStatus.setText(status);
            holder.txtOrderStatus.setTextColor(Color.RED);
            holder.layoutAcceptReject.setVisibility(View.GONE);

        }

        if (offline_requests.status_code == 5) {
            holder.txtOrderStatus.setText("Status: Delivered to you");
            holder.layoutAcceptReject.setVisibility(View.GONE);

        }

        if (offline_requests.status_code == 6) {
            holder.txtOrderStatus.setText("Status: Order Ready for Pickup From Store");
            holder.layoutAcceptReject.setVisibility(View.GONE);

        }

        String htmlContent = "";
        for (int i = 0; i < offline_requests.product_ids.size(); i++) {

            String productName = offline_requests.product_names.get(i);
            String qty = offline_requests.product_qty.get(i);
            float unitprice = offline_requests.product_prices.get(i);
            float totalPrice = offline_requests.product_prices_total.get(i);
            String status = offline_requests.available_status.get(i);


            htmlContent += "Product Name: <b>" + productName + "</b><br />"
                    + "Quantity: " + qty + "<br />"
                    + "Unit Price: " + unitprice + "<br />"
                    + "Total Price: " + totalPrice + "<br />"
                    + "<span> Status : <b>" + status + "</b></span><br /> <br /> <br />";

        }

        holder.txtProducts.setText((Html.fromHtml(htmlContent)));
    }

    @Override
    public int getItemCount() {
        return m_lstOfflineRequests.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtOrderDate;
        public TextView txtCompanyName;
        public TextView txtAddresLine1;
        public TextView txtAddresLine2;
        public TextView txtAddresLine3;
       // public TextView txtPhone;
        public TextView txttCityStatePin;
        public TableLayout tableLayout;
        public LinearLayout layoutAcceptReject;
        public TextView txtOrderStatus;
        public TextView  txtProducts;
        public Button btnAccept;
        public Button btnReject;
        public TextView txtPayByCash;
        public EditText txtRejectionReason;

        private Context m_ctx;

        public ViewHolder(final View itemView, final Context ctx) {
            super(itemView);
            m_ctx = ctx;
            txtOrderDate = (TextView) itemView.findViewById(R.id.txtOrderDate);
            txtCompanyName = (TextView) itemView.findViewById(R.id.txtCompanyName);
            txtAddresLine1 = (TextView) itemView.findViewById(R.id.txtAddresLine1);
            txtAddresLine2 = (TextView) itemView.findViewById(R.id.txtAddresLine2);
            txtAddresLine3 = (TextView) itemView.findViewById(R.id.txtAddresLine3);
            //txtPhone = (TextView) itemView.findViewById(R.id.txtPhone);
            txttCityStatePin = (TextView) itemView.findViewById(R.id.txttCityStatePin);
            layoutAcceptReject = (LinearLayout) itemView.findViewById(R.id.layoutAcceptReject);
            txtOrderStatus = (TextView) itemView.findViewById(R.id.txtOrderStatus);
            txtProducts = (TextView) itemView.findViewById(R.id.txtProducts);

            btnAccept = (Button) itemView.findViewById(R.id.btnAccept);
            btnReject = (Button)itemView.findViewById(R.id.btnReject);
            txtPayByCash = (TextView)itemView.findViewById(R.id.txtPayByCash);
            txtRejectionReason = (EditText) itemView.findViewById(R.id.txtRejectionReason);

            itemView.setOnClickListener(this);

            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        OfflineRequestListing offlineRequestListing = m_lstOfflineRequests.get(pos);
                        Offline_Requests offline_requests = offlineRequestListing.getmOfflineRequest();
                        m_IOfflineRequest.acceptEnquiry(offline_requests);

                    }
                }
            });

            btnReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        OfflineRequestListing offlineRequestListing = m_lstOfflineRequests.get(pos);
                        Offline_Requests offline_requests = offlineRequestListing.getmOfflineRequest();
                        if(txtRejectionReason.getText().toString().equals("")){
                            txtRejectionReason.setError("Please enter rejection reason");
                            return;
                        }
                        offline_requests.buyer_rejection_reason = txtRejectionReason.getText().toString();
                        m_IOfflineRequest.rejectEnquery(offline_requests);

                    }
                }
            });


        }


        // @Override
        public void onClick(View v) {
//            int pos = getAdapterPosition();
//            if (pos != RecyclerView.NO_POSITION) {
//                OrderListing orderListing = m_lstOrders.get(pos);
//                OrderAndProduct orderAndProduct = orderListing.getmOrderAndProduct();
//                mOrderOperations.DoOrderOperations(orderAndProduct, Enums.OrderOperations.NoOperation);
//
        }
//
    }
}

