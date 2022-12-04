package com.pentaware.doorish;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.pentaware.doorish.model.OfflineProduct;
import java.util.List;

public class AdapterViewProductOrder extends RecyclerView.Adapter<AdapterViewProductOrder.ViewHolder>{

    Context m_Context;
    List<OfflineProduct> productList;

    AdapterViewProductOrder(Context context, List<OfflineProduct> list){
        m_Context = context;
        productList = list;
    }


    @NonNull
    @Override
    public AdapterViewProductOrder.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_offline_order_listing, parent, false);
        return new AdapterViewProductOrder.ViewHolder(v, m_Context);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewProductOrder.ViewHolder holder, int position) {
        OfflineProduct product = productList.get(position);
        holder.txtProductTitle.setText(product.getProductTitle());
        String strQuantity = "Quantity: " + product.getProductQuantity();
        holder.txtProductQuantity.setText(strQuantity);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private Context m_ctx;
        TextView txtProductTitle;
        TextView txtProductQuantity;

        public ViewHolder(View itemView, Context ctx) {
            super(itemView);
            m_ctx = ctx;
            txtProductTitle = itemView.findViewById(R.id.txt_product_title);
            txtProductQuantity = itemView.findViewById(R.id.txt_product_quantity);
        }
    }
}



