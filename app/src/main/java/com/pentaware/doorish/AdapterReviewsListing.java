package com.pentaware.doorish;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.pentaware.doorish.model.Reviews;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterReviewsListing extends RecyclerView.Adapter<AdapterReviewsListing.ViewHolder>  {

    private List<ReviewListing> m_lstReviews;
    private Context m_Context;
    private StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();

    public AdapterReviewsListing(List<ReviewListing> lstReviews, Context ctx) {
        this.m_lstReviews = lstReviews;
        m_Context = ctx;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_review_listing, parent, false);
        return new ViewHolder(v, m_Context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ReviewListing listItem = m_lstReviews.get(position);
        Reviews review = listItem.getReview();
        String reviewTitle =  "<b>" + review.Title + "</b>";
        float rating = review.Ratings;
        String comment =  review.Comment;
        String postedBy = review.posted_by;
        Date postDate = review.timestamp.toDate();


        SimpleDateFormat sfd = new SimpleDateFormat("dd MMM yyyy");
        String dt1 = sfd.format(postDate);

        holder.txtReviewTitle.setText(Html.fromHtml(reviewTitle));
        holder.ratingBar.setRating(rating);
        holder.txtComment.setText(comment);
        String sPostedBy = "Posted By: " + postedBy;
        holder.txtPostedBy.setText((sPostedBy));
        String sPostedOn = "Posted On: " + dt1;
        holder.txtPostDate.setText(sPostedOn);

    }

    @Override
    public int getItemCount() {
        return m_lstReviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {



        public TextView txtReviewTitle;
        public RatingBar ratingBar;
        public TextView txtComment;
        public TextView txtPostedBy;
        public TextView txtPostDate;
        private Context m_ctx;



        public ViewHolder(View itemView, Context ctx) {
            super(itemView);
            m_ctx = ctx;
            txtReviewTitle = (TextView) itemView.findViewById(R.id.txtCommentTitle);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBarComments);
            txtComment = (TextView) itemView.findViewById(R.id.txtComment);
            txtPostedBy = (TextView) itemView.findViewById(R.id.txtPostedBy);
            txtPostDate = (TextView) itemView.findViewById(R.id.txtCommentDate);
            //  itemView.setOnClickListener(this);


        }



        //  @Override
        public void onClick(View v) {
//
//            int pos = getAdapterPosition();
//            if (pos != RecyclerView.NO_POSITION) {
//                ProductListing productListing = m_lstProducts.get(pos);
//                Product product = productListing.getProduct();
//                mProductAdapterClickEvent.onProductClick(product);
//
////                Fragment newFragment = new SendFragment();
////                FragmentManager manager = ((AppCompatActivity)v.getContext()).getSupportFragmentManager();
////                Fragment fragment = manager.findFragmentById(R.id.nav_host_fragment);
////                FragmentTransaction transaction =  manager.beginTransaction();
////                transaction.replace(R.id.nav_host_fragment, newFragment);
////                transaction.addToBackStack(null);
////                transaction.commit();
//
////                Intent intent = new Intent(m_ctx, EmployeeTabs.class);
////                intent.putExtra("employee", employee);
////                m_ctx.startActivity(intent);
//            }
        }

    }
}
