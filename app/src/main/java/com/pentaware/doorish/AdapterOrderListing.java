package com.pentaware.doorish;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.pentaware.doorish.model.OrderAndProduct;
import com.pentaware.doorish.model.Orders;
import com.pentaware.doorish.model.Product;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterOrderListing extends RecyclerView.Adapter<AdapterOrderListing.ViewHolder> {

    private List<OrderListing> m_lstOrders;
    private Context m_Context;
    private StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();

    IOrderOperations mOrderOperations;
    IOfflineProducts mListener;

    int m_iPageIndex = 0;
    public boolean isLastPage = false;

    public AdapterOrderListing(List<OrderListing> lstOrders, Context ctx, IOrderOperations orderOperations, IOfflineProducts listener, int pageIndex) {
        this.m_lstOrders = lstOrders;
        m_Context = ctx;
        mOrderOperations = orderOperations;
        mListener = listener;
        m_iPageIndex = pageIndex;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView;
        boolean lastItem = false;

        if(viewType == R.layout.activity_order_listing){
            lastItem = false;
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_order_listing, parent, false);
        }

        else {
            lastItem = true;
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_prev_next, parent, false);
        }

        return new ViewHolder(itemView, m_Context, lastItem);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        if((m_lstOrders.size() == CommonVariables.PAGE_SIZE || m_iPageIndex > 0) && position == m_lstOrders.size()){
            return;
        }

        OrderListing listItem = m_lstOrders.get(position);
        OrderAndProduct orderAndProduct = listItem.getmOrderAndProduct();
        Orders order = orderAndProduct.order;
        Product product = orderAndProduct.product;

        holder.order = order;
        String sVariants = "";
        if (product.Variants.size() > 0) {

            for (Map.Entry<String, String> entry : product.Variants.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                sVariants += "<b>" + key + ":  " + "</b>" + value + "<br/>";
            }
            holder.txtVariants.setText((Html.fromHtml(sVariants)));
        }


        String productTitle = product.Title;
        holder.txtProductTitle.setText(productTitle);

        // holder.txtOrderId.setText("Order Id: " + order.order_id);

        Date dtOrder = order.order_date;

        SimpleDateFormat sfd = new SimpleDateFormat("dd MMM yyyy");
        String dt1 = sfd.format(dtOrder);
        holder.txtOrderDate.setText(dt1);

        String sStatus = order.Status;
        if (product.return_requested) {
            sStatus += " - Return Requested";
        }
        if (product.return_processed) {
            sStatus += " and Return Processed";
        }

        if (product.replacement_requested) {
            sStatus += " - Replacement Requested";
        }

        if (order.pickup_from_store) {
            holder.layoutPickupFromStore.setVisibility(View.VISIBLE);
            holder.txtPickupAtStore.setText("This is a Pick up from store order");
            if (order.pickup_timestamp != null) {
                holder.txtPickupTime.setText("Pickup Time: " + order.pickup_timestamp);
            }
        }
        holder.txtOrderStatus.setText(sStatus);

        holder.txtPointsAdded.setText("Points Earned: " + Double.toString(product.points_added));

        Glide.with(m_Context)
                .load(product.ImageUrlCover) // the uri you got from Firebase
                .into(holder.imgView);


    }

    @Override
    public int getItemCount() {
        if(m_lstOrders.size() == CommonVariables.PAGE_SIZE || m_iPageIndex > 0){
            return m_lstOrders.size() + 1;
        }
        else{
            return m_lstOrders.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(m_lstOrders.size() == CommonVariables.PAGE_SIZE || m_iPageIndex > 0){
            return (position == m_lstOrders.size()) ? R.layout.activity_prev_next : R.layout.activity_order_listing;

        }
        else{

            return R.layout.activity_order_listing;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
        public Button btnNext,btnPrevious;

//        Button btnViewOfflineProducts;

        private Context m_ctx;

        public ViewHolder(View itemView, Context ctx, boolean isLast) {
            super(itemView);

            if(!isLast){
                m_ctx = ctx;
                txtPointsAdded = (TextView) itemView.findViewById(R.id.txtPointsEarned);
                txtProductTitle = (TextView) itemView.findViewById(R.id.txtProductTitle_Order);
                txtOrderDate = (TextView) itemView.findViewById(R.id.txtOrderDate);
                txtOrderStatus = (TextView) itemView.findViewById(R.id.txtOrderStatus);
                imgView = (ImageView) itemView.findViewById(R.id.imgProduct_order);
                txtVariants = (TextView)itemView.findViewById(R.id.txtVariants);
                layoutPickupFromStore = (LinearLayout)itemView.findViewById(R.id.layoutPickupFromStore);
                txtPickupAtStore = (TextView)itemView.findViewById(R.id.txtPickupAtStore);
                txtPickupTime = (TextView)itemView.findViewById(R.id.txtPickupTime);
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
            else {

                btnNext = (Button)itemView.findViewById(R.id.btnNext);
                btnPrevious = (Button)itemView.findViewById(R.id.btnPrev);
                Log.w("Page_index",  Integer.toString(m_iPageIndex));

                if(m_iPageIndex == 0){
                    btnPrevious.setVisibility(View.GONE);
                }
                else{
                    btnPrevious.setVisibility(View.VISIBLE);
                }

                if(m_lstOrders.size() < CommonVariables.PAGE_SIZE){
                    btnNext.setVisibility(View.GONE);
                }
                else{
                    btnNext.setVisibility(View.VISIBLE);
                }

                btnNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        mOrderOperations.onNextClick();
                    }
                });

                btnPrevious.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOrderOperations.onPreviousClick();
                    }
                });

            }


        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                OrderListing orderListing = m_lstOrders.get(pos);
                OrderAndProduct orderAndProduct = orderListing.getmOrderAndProduct();
              //  mOrderOperations.DoOrderOperations(orderAndProduct, Enums.OrderOperations.NoOperation);

            }

        }
    }
    public interface IOfflineProducts {
        void onClickViewProducts(int position);
    }
}
