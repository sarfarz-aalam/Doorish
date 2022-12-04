package com.pentaware.doorish.ui.faq;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pentaware.doorish.AdapterAddressListing;
import com.pentaware.doorish.AddressListing;
import com.pentaware.doorish.IAddressOperation;
import com.pentaware.doorish.R;
import com.pentaware.doorish.model.Address;
import com.pentaware.doorish.model.FAQ;

import java.util.List;

public class AdapterFAQListing extends RecyclerView.Adapter<AdapterFAQListing.ViewHolder>  {

    private List<faqListing> m_lstFAQ;
    private Context m_Context;

    public AdapterFAQListing(List<faqListing> lstFAQ, Context ctx) {
        m_lstFAQ = lstFAQ;
        m_Context = ctx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_faq_listing, parent, false);
        return new AdapterFAQListing.ViewHolder(v, m_Context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        faqListing listItem = m_lstFAQ.get(position);
        FAQ faq = listItem.getFAQ();

        holder.txtQuestion.setText(faq.question);
        holder.txtAnswer.setText(faq.answer);

    }

    @Override
    public int getItemCount() {
        return m_lstFAQ.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView txtQuestion;
        public TextView txtAnswer;
        public Button btnExpand;
        private boolean hidden = true;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            txtQuestion = itemView.findViewById(R.id.txtQuestion);
            txtAnswer = itemView.findViewById(R.id.txtAnswer);
            btnExpand = itemView.findViewById(R.id.btnExpand);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    expandFaq();
                }
            });

            btnExpand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    expandFaq();
                }
            });


        }

        public void expandFaq(){
            int pos = getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                if(hidden){
                    txtAnswer.setVisibility(View.VISIBLE);
                    btnExpand.setBackgroundResource(R.drawable.ic_drop_up);
                    hidden = false;
                }
                else {
                    txtAnswer.setVisibility(View.GONE);
                    btnExpand.setBackgroundResource(R.drawable.ic_drop_down);
                    hidden = true;
                }
            }
        }

        @Override
        public void onClick(View view) {

        }
    }
}
