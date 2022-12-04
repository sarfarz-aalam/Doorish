package com.pentaware.doorish;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pentaware.doorish.model.Offline_Orders;
import com.pentaware.doorish.model.OrderAndProduct;
import com.pentaware.doorish.model.Orders;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterCityOrdersListing extends RecyclerView.Adapter<AdapterCityOrdersListing.ViewHolder> {

    private List<CityOrdersListing> m_lstOrders;
    private Context m_Context;

    public AdapterCityOrdersListing(List<CityOrdersListing>lstOrders, Context ctx){
        m_lstOrders = lstOrders;
        m_Context = ctx;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_city_orders_listing, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CityOrdersListing listItem = m_lstOrders.get(position);
        Offline_Orders offline_order = listItem.getmOfflineOrders();

        Date dtOrder = offline_order.timestamp;
        SimpleDateFormat sfd = new SimpleDateFormat("dd MMM yyyy");
        String dt1 = sfd.format(dtOrder);
        holder.txtOrderDate.setText( dt1);

        holder.txtInvoiceId.setText("Invoice Id: " + offline_order.invoice_id);

        holder.txtSellerName.setText((offline_order.company_name));

        holder.txtSellerCity.setText(offline_order.sellerCity);

        String productList = "";
        float totalAmount = 0;
        for(int i = 0; i < offline_order.product_names.size(); i++){
            String productName = offline_order.product_names.get(i);
            float productPrice = offline_order.price_list.get(i);
            int qty = offline_order.product_qty.get(i);
            String productStatus = offline_order.status_list.get(i);
            if(!productStatus.toUpperCase().trim().equals("RETURNED")){
                float amount = productPrice * qty;
                totalAmount += amount;
            }
            productList += Integer.toString(i+1) + ": " + productName + "<br/> Qty: " + Integer.toString(qty) + "<br/>Price: " + Float.toString(productPrice) + "<br/>Status: <b>" + productStatus + "</b><br/><br/>  " ;
        }

        double pointsPrice = (totalAmount * CommonVariables.percentOfAmountCreditedIntoPoints) / 100;
        double points =  pointsPrice * CommonVariables.NumberOfPointsInOneRupee;

        holder.txtPointsEarned.setText("Total Points: " + Double.toString(points));
        holder.txtTotalAmount.setText("Total Amount: " + CommonVariables.rupeeSymbol +  Float.toString(totalAmount));
        holder.txtProducts.setText((Html.fromHtml(productList)));

    }

    @Override
    public int getItemCount() {
        return m_lstOrders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView txtOrderDate;
        public TextView txtInvoiceId;
        public TextView txtSellerName;
        public TextView txtSellerCity;
        public TextView txtProducts;
        public TextView txtTotalAmount;
        public TextView txtPointsEarned;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtOrderDate = (TextView) itemView.findViewById(R.id.txtOrderDate);
            txtInvoiceId = (TextView) itemView.findViewById(R.id.txtInvoiceId);
            txtSellerName = (TextView) itemView.findViewById(R.id.txtSellerName);
            txtSellerCity = (TextView) itemView.findViewById(R.id.txtSellerCity);
            txtProducts = (TextView) itemView.findViewById(R.id.txtProducts);
            txtTotalAmount = (TextView) itemView.findViewById(R.id.txtTotalAmount);
            txtPointsEarned = (TextView) itemView.findViewById(R.id.txtPointsEarned);
        }
    }

}
