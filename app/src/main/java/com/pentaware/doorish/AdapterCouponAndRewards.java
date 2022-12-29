package com.pentaware.doorish;


import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pentaware.doorish.model.Coupon;
import com.pentaware.doorish.model.WalletStatement;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AdapterCouponAndRewards extends RecyclerView.Adapter<AdapterCouponAndRewards.ViewHolder> {

    private List<Coupon> couponList;
    private Context m_Context;


    public AdapterCouponAndRewards(Context ctx, List<Coupon> couponList) {
        this.couponList = couponList;
        m_Context = ctx;
    }

    @NonNull
    @Override
    public AdapterCouponAndRewards.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coupon_and_rewards, parent, false);
        return new AdapterCouponAndRewards.ViewHolder(v, m_Context);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCouponAndRewards.ViewHolder holder, int position) {
        Coupon coupon = couponList.get(position);

        String strCouponName = "Coupon Name: " + coupon.coupon_name;
        holder.txtCouponName.setText(strCouponName);

        String strCouponDetails = "";
        if(coupon.coupon_type.equals("Percent"))
            strCouponDetails = "Get upto " + coupon.coupon_disount + "% OFF on your order";
        else strCouponDetails = "Get flat " + CommonVariables.rupeeSymbol + coupon.coupon_disount + " OFF on your order";

        holder.txtCouponDetails.setText(strCouponDetails);


    }

    @Override
    public int getItemCount() {
        return couponList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txtCouponName, txtCouponDetails;
        public Button btnClaimCoupon;

        public ViewHolder(View itemView, Context ctx) {
            super(itemView);

            txtCouponName = itemView.findViewById(R.id.txt_coupon_name);
            txtCouponDetails = itemView.findViewById(R.id.txt_coupon_details);
            btnClaimCoupon = itemView.findViewById(R.id.btn_claim_coupon);

            btnClaimCoupon.setOnClickListener(view -> {
                ClipboardManager _clipboard = (ClipboardManager)ctx.getSystemService(Context.CLIPBOARD_SERVICE);
                _clipboard.setText(couponList.get(getAdapterPosition()).coupon_name);
                Toast.makeText(ctx, "Coupon code copied successfully", Toast.LENGTH_SHORT).show();
            });
        }
        @Override
        public void onClick(View view) { }
    }
}
