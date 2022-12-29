package com.pentaware.doorish;


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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pentaware.doorish.model.WalletStatement;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AdapterWalletStatement extends RecyclerView.Adapter<AdapterWalletStatement.ViewHolder> {

    private List<WalletStatement> walletList;
    private Context m_Context;
    private boolean hidden = false;


    public AdapterWalletStatement(Context ctx, List<WalletStatement> walletList) {
        this.walletList = walletList;
        m_Context = ctx;
    }

    @NonNull
    @Override
    public AdapterWalletStatement.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wallet_statement, parent, false);
        return new AdapterWalletStatement.ViewHolder(v, m_Context);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterWalletStatement.ViewHolder holder, int position) {
        WalletStatement statement = walletList.get(position);
        holder.txtTransactionDetails.setText(statement.details);

        String strAmount = "";

        if(statement.transaction_type.equals("credit")){
            strAmount = CommonVariables.rupeeSymbol + String.format("%.2f", statement.amount);
            holder.txtWalletAmount.setTextColor(m_Context.getResources().getColor(R.color.price_green));
        }
        else {
            strAmount = "-" + CommonVariables.rupeeSymbol + String.format("%.2f", statement.amount);
            holder.txtWalletAmount.setTextColor(m_Context.getResources().getColor(R.color.colorRed));
        }
        holder.txtWalletAmount.setText(strAmount);


        holder.txtWalletAmount.setText(strAmount);

        //transaction Date
        Date dtTransaction = statement.date;
        SimpleDateFormat date = new SimpleDateFormat("dd MMM");
        SimpleDateFormat time = new SimpleDateFormat("hh:mm aa");
        String strDate = date.format(dtTransaction);
        String strTime = time.format(dtTransaction);
        String dateAndTime = strDate + ", " + strTime.toUpperCase();
        holder.txtTransactionDate.setText(dateAndTime);

        String strOrderId = "Order Id: " + statement.order_id;
        holder.txtOrderId.setText(strOrderId);

        if(statement.order_id == null || statement.order_id.equals("na") )
            holder.btnExpand.setVisibility(View.GONE);
        else holder.btnExpand.setVisibility(View.VISIBLE);

        holder.btnExpand.setOnClickListener(view -> {
            if (hidden) {
                holder.txtOrderId.setVisibility(View.VISIBLE);
                holder.btnExpand.setBackgroundResource(R.drawable.ic_expand_less_24px);
                hidden = false;
            } else {
                holder.txtOrderId.setVisibility(View.GONE);
                holder.btnExpand.setBackgroundResource(R.drawable.ic_expand_more_24px);
                hidden = true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return walletList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txtTransactionDetails, txtWalletAmount, txtTransactionDate, txtOrderId;
        public ImageView btnExpand;

        public ViewHolder(View itemView, Context ctx) {
            super(itemView);

            txtTransactionDetails = itemView.findViewById(R.id.txt_transaction_details);
            txtWalletAmount = itemView.findViewById(R.id.txt_wallet_amount);
            txtTransactionDate = itemView.findViewById(R.id.txt_transaction_date);
            txtOrderId = itemView.findViewById(R.id.txt_order_id);
            btnExpand = itemView.findViewById(R.id.btn_expand);
        }
        @Override
        public void onClick(View view) { }
    }
}
