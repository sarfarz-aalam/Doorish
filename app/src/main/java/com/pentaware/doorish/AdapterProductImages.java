package com.pentaware.doorish;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pentaware.doorish.model.Orders;

public class AdapterProductImages extends RecyclerView.Adapter<AdapterProductImages.ViewHolder> {

    private Orders mOrder;
    private Context ctx;

    public AdapterProductImages(Orders mOrder, Context ctx) {
        this.mOrder = mOrder;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_image_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imgUrl = mOrder.img_url_list.get(position);

        String offerPrice = "Price: ₹" + mOrder.product_offer_prices.get(position);

        if(mOrder.product_status_list.get(position).equals("cancelled")){
            holder.txtProductPrice.setText("");
        }

        Glide.with(ctx).load(imgUrl).into(holder.imgProduct);

        holder.txtProductPrice.setText(offerPrice);
    }

    @Override
    public int getItemCount() {
        return mOrder.product_ids.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgProduct;
        public TextView txtProductPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProduct = itemView.findViewById(R.id.img_product);
            txtProductPrice = itemView.findViewById(R.id.txt_product_price);
        }
    }
}
