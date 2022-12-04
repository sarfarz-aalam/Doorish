package com.pentaware.doorish;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pentaware.doorish.model.OfflineProduct;

import java.util.List;


public class AdapterOfflineProducts extends RecyclerView.Adapter<AdapterOfflineProducts.ViewHolder>{

    Context m_Context;
    List<OfflineProduct> productList;
    IOfflineProducts mListener;

    AdapterOfflineProducts(Context context, List<OfflineProduct> list, IOfflineProducts listener){
        m_Context = context;
        productList = list;
        mListener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.offline_products_listing, parent, false);
        return new AdapterOfflineProducts.ViewHolder(v, m_Context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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
        ImageView imgDeleteProduct;

        public ViewHolder(View itemView, Context ctx) {
            super(itemView);
            m_ctx = ctx;
            txtProductTitle = itemView.findViewById(R.id.txt_product_title);
            txtProductQuantity = itemView.findViewById(R.id.txt_product_quantity);
            imgDeleteProduct = itemView.findViewById(R.id.img_delete_product);

            imgDeleteProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        mListener.onClickDeleteProduct(pos);
                    }
                }
            });
        }
    }
    public interface IOfflineProducts {
         void onClickDeleteProduct(int position);
    }
}

