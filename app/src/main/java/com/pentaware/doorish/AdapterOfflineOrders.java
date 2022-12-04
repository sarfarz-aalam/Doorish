package com.pentaware.doorish;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.pentaware.doorish.model.OfflineOrder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AdapterOfflineOrders extends RecyclerView.Adapter<AdapterOfflineOrders.ViewHolder>{

    Context m_Context;
    List<OfflineOrder> orderList;
    IOfflineOrders mListener;

    AdapterOfflineOrders(Context context, List<OfflineOrder> list, IOfflineOrders listener){
        m_Context = context;
        orderList = list;
        mListener = listener;
    }

    @NonNull
    @Override
    public AdapterOfflineOrders.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.offline_order_listing, parent, false);
        return new AdapterOfflineOrders.ViewHolder(v, m_Context);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterOfflineOrders.ViewHolder holder, int position) {
        OfflineOrder order = orderList.get(position);
        holder.txtOrderTitle.setText("Offline Order " + (position + 1));
        Date dtOrder = order.getOrder_date();
        SimpleDateFormat sfd = new SimpleDateFormat("dd MMM yyyy");
        String date = "Ordered on " + sfd.format(dtOrder);
        holder.txtDateOrdered.setText(date);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private Context m_ctx;
        TextView txtOrderTitle;
        TextView txtDateOrdered;
        Button btnViewProducts;

        public ViewHolder(View itemView, Context ctx) {
            super(itemView);
            m_ctx = ctx;
            txtOrderTitle = itemView.findViewById(R.id.txt_offline_order);
            txtDateOrdered = itemView.findViewById(R.id.txt_date_ordered);
            btnViewProducts = itemView.findViewById(R.id.btn_view_products_offline);
            btnViewProducts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onClickViewProducts(getAdapterPosition());
                }
            });
        }
    }
    interface IOfflineOrders{
        void onClickViewProducts(int position);
    }
}
