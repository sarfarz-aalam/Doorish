package com.pentaware.doorish;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.pentaware.doorish.model.Coupon;
import java.util.List;

public class AdapterCouponAndRewards extends RecyclerView.Adapter<AdapterCouponAndRewards.ViewHolder> {

    private List<Coupon> couponList;
    private Context m_Context;
    private ICoupon iCoupon;


    public AdapterCouponAndRewards(Context ctx, List<Coupon> couponList, ICoupon iCoupon) {
        this.couponList = couponList;
        m_Context = ctx;
        this.iCoupon = iCoupon;
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

        String strCouponName = coupon.coupon_name;
        holder.txtCouponName.setText(strCouponName);

        String strCouponDetails = "";
        if(coupon.coupon_type.equals("Percent"))
            strCouponDetails = "Get upto " + coupon.coupon_disount + "% OFF on your order";
        else strCouponDetails = "Get flat " + CommonVariables.rupeeSymbol + coupon.coupon_disount + " OFF on your order";

        String strCouponDesc = "";
        if(coupon.coupon_type.equals("Percent"))
            strCouponDesc = "Use code " + coupon.coupon_name + " and get upto " + coupon.coupon_disount +  "% off on orders above " +
                    CommonVariables.rupeeSymbol + "100.";
        else strCouponDesc = "Use code " + coupon.coupon_name + " and get upto " + CommonVariables.rupeeSymbol + coupon.coupon_disount +  " off on orders above " +
                CommonVariables.rupeeSymbol + "100.";


        holder.txtCouponDesc.setText(strCouponDesc);
        holder.txtCouponDetails.setText(strCouponDetails);

        String strTermsAndConditions = "Terms and Conditions \n\n";
        for(int i = 0; i < coupon.terms_and_conditions.size(); i++){
            strTermsAndConditions += "•" + coupon.terms_and_conditions.get(i) + "\n";
        }

        holder.txtTermsAndConditions.setText(strTermsAndConditions);

    }

    @Override
    public int getItemCount() {
        return couponList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txtCouponName, txtCouponDetails, txtTermsAndConditions, txtCouponDesc;
        public TextView btnClaimCoupon;
        public RelativeLayout layoutMore;

        public ViewHolder(View itemView, Context ctx) {
            super(itemView);

            txtCouponName = itemView.findViewById(R.id.txt_coupon_name);
            txtCouponDetails = itemView.findViewById(R.id.txt_coupon_details);
            btnClaimCoupon = itemView.findViewById(R.id.btn_claim_coupon);
            txtTermsAndConditions = itemView.findViewById(R.id.txt_terms_and_conditions);
            txtCouponDesc = itemView.findViewById(R.id.txt_coupon_desc);
            layoutMore = itemView.findViewById(R.id.layout_more);

            btnClaimCoupon.setOnClickListener(view -> {
//                ClipboardManager _clipboard = (ClipboardManager)ctx.getSystemService(Context.CLIPBOARD_SERVICE);
//                _clipboard.setText(couponList.get(getAdapterPosition()).coupon_name);
                Toast.makeText(ctx, "Coupon code applied successfully", Toast.LENGTH_SHORT).show();
                iCoupon.applyCoupon(couponList.get(getAdapterPosition()));
            });

            layoutMore.setOnClickListener(view -> {
                layoutMore.setVisibility(View.GONE);
                txtTermsAndConditions.setVisibility(View.VISIBLE);
            });
        }
        @Override
        public void onClick(View view) { }
    }
}
