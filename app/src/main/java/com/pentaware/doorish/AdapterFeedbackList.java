package com.pentaware.doorish;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.pentaware.doorish.model.Feedback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AdapterFeedbackList extends RecyclerView.Adapter<AdapterFeedbackList.ViewHolder> {

    private final List<Feedback> feedbackList;
    private final Context m_Context;

    public AdapterFeedbackList(List<Feedback> feedbackList, Context ctx) {
        this.feedbackList = feedbackList;
        m_Context = ctx;
    }

    @NonNull
    @Override
    public AdapterFeedbackList.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feedback_list, parent, false);
        return new AdapterFeedbackList.ViewHolder(v, m_Context);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterFeedbackList.ViewHolder holder, int position) {

        Feedback feedback = feedbackList.get(position);

        holder.txtFeedback.setText(feedback.feedback);

        Date timestamp = feedback.timestamp;
        SimpleDateFormat sfd = new SimpleDateFormat("dd MMM yyyy");
        String date = sfd.format(timestamp);

        String authorWithDate = feedback.customer_name + " at " + date;
        holder.txtReviewAuthor.setText(authorWithDate);
        
        if (feedback.feedback_response != null) {
//            holder.txtFeedbackResponse.setText(feedback.feedback_response);
//            holder.btnExpand.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtFeedback, txtFeedbackResponse, txtReviewAuthor;
        public ImageButton btnExpand;
        private Boolean hidden = true;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            txtFeedback = itemView.findViewById(R.id.txt_feedback);
            txtFeedbackResponse = itemView.findViewById(R.id.txt_feedback_response);
            txtReviewAuthor = itemView.findViewById(R.id.txt_feedback_author);
            btnExpand = itemView.findViewById(R.id.btn_expand);

            btnExpand.setVisibility(View.GONE);

            btnExpand.setOnClickListener(view -> {

                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    if (hidden) {
                        txtFeedbackResponse.setVisibility(View.VISIBLE);
                        btnExpand.setBackgroundResource(R.drawable.ic_arrow_up);
                        hidden = false;
                        return;
                    }
                    txtFeedbackResponse.setVisibility(View.GONE);
                    btnExpand.setBackgroundResource(R.drawable.ic_arrow_down);
                    hidden = true;
                }
            });

        }
    }
}
