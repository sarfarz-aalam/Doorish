package com.pentaware;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pentaware.doorish.Enums;
import com.pentaware.doorish.IOrderOperations;
import com.pentaware.doorish.R;
import com.pentaware.doorish.model.Orders;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class OrdersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public Orders mOrder;
    IOrderOperations mOrderOperations;

    public TextView txtProductTitle;
    public TextView txtOrderDate;
    public TextView txtOrderStatus;
    public ImageView imgView;
    public TextView txtPointsAdded;
    public TextView txtVariants;
    public LinearLayout layoutPickupFromStore;
    public TextView txtPickupAtStore;
    public TextView txtPickupTime;
    //   public Button btnCancelOrder;
    public Orders order;
    public TextView txtTotalPrice;
    public Button btnRemoveFromCart;
    public Button btnAddQty;
    public Button btnDecreaseQty;
    public Button btnNext, btnPrevious;

//  Button btnViewOfflineProducts;

    private Context m_ctx;

    public OrdersViewHolder(@NonNull View itemView) {
        super(itemView);
        txtPointsAdded = (TextView) itemView.findViewById(R.id.txtPointsEarned);
        txtProductTitle = (TextView) itemView.findViewById(R.id.txtProductTitle_Order);
        txtOrderDate = (TextView) itemView.findViewById(R.id.txtOrderDate);
        txtOrderStatus = (TextView) itemView.findViewById(R.id.txtOrderStatus);
        imgView = (ImageView) itemView.findViewById(R.id.imgProduct_order);
        txtVariants = (TextView) itemView.findViewById(R.id.txtVariants);
        layoutPickupFromStore = (LinearLayout) itemView.findViewById(R.id.layoutPickupFromStore);
        txtPickupAtStore = (TextView) itemView.findViewById(R.id.txtPickupAtStore);
        txtPickupTime = (TextView) itemView.findViewById(R.id.txtPickupTime);
//            btnViewOfflineProducts = itemView.findViewById(R.id.btnViewOfflineProducts);
//
//            btnViewOfflineProducts.setOnClickListener(view -> {
//                mListener.onClickViewProducts(getAdapterPosition());
//            });
//            btnCancelOrder = (Button) itemView.findViewById(R.id.btnCancelOrder);
//
//            btnCancelOrder.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mOrderOperations.DoOrderOperations(order, Enums.OrderOperations.CancelOrder);
//                }
//            });
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int pos = getAdapterPosition();
        if (pos != RecyclerView.NO_POSITION) {
            mOrderOperations.DoOrderOperations(mOrder, Enums.OrderOperations.NoOperation);

        }
    }

    public void bind(Orders order, Context ctx, IOrderOperations mOrderOperations) {

        mOrder = order;
        this.mOrderOperations = mOrderOperations;

        String productTitle = order.product_titles.get(0);
        String imgUrlCover = order.img_url_list.get(0);

        String title;

        if(order.product_titles.size() > 1){
            title = productTitle + " <u><font color = #0000FF>and " + (order.product_titles.size() - 1) + " others</font></u>";
        }
        else {
            title = productTitle;
        }



//        StringBuilder sVariants = new StringBuilder();
        String sVariants = "";

//        if (order.product_variants.size() > 0) {
//
//            for (Map.Entry<String, String> entry : order.product_variants.entrySet()) {
//                String key = entry.getKey();
//                String value = entry.getValue();
//                sVariants.append("<b>").append(key).append(":  ").append("</b>").append(value).append("<br/>");
//            }
//            txtVariants.setText((Html.fromHtml(sVariants.toString())));
//        }

//        if (order.product_variants.size() > 0) {
//            if(order.product_variants.get(getAdapterPosition()) != null){
//                sVariants = order.product_variants.get(getAdapterPosition());
//                txtVariants.setText(sVariants);
//            }
//
//        }

        txtProductTitle.setText(Html.fromHtml(title));

        // holder.txtOrderId.setText("Order Id: " + order.order_id);

        Date dtOrder = order.order_date;

        SimpleDateFormat sfd = new SimpleDateFormat("dd MMM yyyy");
        String dt1 = sfd.format(dtOrder);
        txtOrderDate.setText(dt1);

        String sStatus = order.Status;

//        if (order.product_return_request.get(getAdapterPosition())) {
//            sStatus += " - Return Requested";
//        }
//        if (order.product_return_processed.get(getAdapterPosition())) {
//            sStatus += " and Return Processed";
//        }
//
//        if (order.replacement_requested.get(getAdapterPosition())) {
//            sStatus += " - Replacement Requested";
//        }

        if (order.pickup_from_store) {
            layoutPickupFromStore.setVisibility(View.VISIBLE);
            txtPickupAtStore.setText("This is a Pick up from store order");
            if (order.pickup_timestamp != null) {
                txtPickupTime.setText("Pickup Time: " + order.pickup_timestamp);
            }
        }
        txtOrderStatus.setText(sStatus);

        //txtPointsAdded.setText("Points Earned: " + Double.toString(order.points_added));

//        Glide.with(ctx)
//                .load(imgUrlCover) // the uri you got from Firebase
//                .into(imgView);

        imgView.setImageResource(R.drawable.img_order_basket);

    }

}
