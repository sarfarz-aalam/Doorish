package com.pentaware.doorish.ui.orders;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pentaware.doorish.BaseFragment;
import com.pentaware.doorish.CommonVariables;
import com.pentaware.doorish.LandingPage;
import com.pentaware.doorish.R;
import com.pentaware.doorish.model.Orders;
import com.pentaware.doorish.model.Reviews;
import java.util.Date;
import java.util.UUID;

public class ReviewProductFragment extends BaseFragment {

    private FirebaseFirestore db;
    private final Orders mOrder;
    private final int index;
    private View mView;
    private EditText editTextReviewTitle, editTextReview;
    private ImageView imgProduct;
    private TextView txtProductTitle;
    private RatingBar productRatingBar;
    private Boolean inputError = false;


    private ReviewProductFragment(Orders order, int index) {
        mOrder = order;
        this.index = index;
    }


    public static ReviewProductFragment newInstance(Orders order, int index) {
        return new ReviewProductFragment(order, index);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_review_product, container, false);

        db = FirebaseFirestore.getInstance();

        editTextReviewTitle = mView.findViewById(R.id.edit_text_review_title);
        editTextReview = mView.findViewById(R.id.edit_text_review);
        imgProduct = mView.findViewById(R.id.img_product);
        txtProductTitle = mView.findViewById(R.id.txt_product_title);
        productRatingBar = mView.findViewById(R.id.product_rating_bar);

        showProductDetails();

        Button btnSubmitReview = mView.findViewById(R.id.btn_submit_review);
        btnSubmitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitReview();
            }
        });


        return mView;
    }

    private void showProductDetails() {
        Glide.with(this).load(mOrder.img_url_list.get(index)).into(imgProduct);
        txtProductTitle.setText(mOrder.product_titles.get(index));
    }

    private void submitReview() {
        validateInput();

        if (!inputError) {
            Reviews review = new Reviews();
            review.active = false;
            review.Ratings = productRatingBar.getRating();
            review.Title = editTextReviewTitle.getText().toString();
            review.Comment = editTextReview.getText().toString();
            review.posted_by = CommonVariables.loggedInUserDetails.Name;
            review.product_id = mOrder.product_ids.get(index);
            review.doc_id = UUID.randomUUID().toString();
            review.customer_id = CommonVariables.loggedInUserDetails.customer_id;
            review.customer_email = CommonVariables.loggedInUserDetails.Email;
            review.customer_phone = CommonVariables.loggedInUserDetails.Phone;
            review.product_img = mOrder.img_url_list.get(index);
            review.product_title = mOrder.product_titles.get(index);
            Date dt = new Date();
            review.timestamp = new Timestamp(dt);

            db.collection("reviews").document(review.doc_id).set(review).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    editTextReview.setText("");
                    editTextReviewTitle.setText("");
                    productRatingBar.setRating(0);
                    Toast.makeText(mView.getContext(), "Review submitted successfully", Toast.LENGTH_SHORT).show();
                    getFragmentManager().popBackStack();
                }
            });
        }
    }

    private void validateInput() {

        if (productRatingBar.getRating() == 0) {
            Toast.makeText(mView.getContext(), "Please give a rating", Toast.LENGTH_SHORT).show();
            inputError = true;
        } else if (editTextReviewTitle.getText().toString().equals("")) {
            editTextReviewTitle.setError("Please enter a review Title");
            inputError = true;
        } else if (editTextReview.getText().toString().equals("")) {
            editTextReview.setError("Please enter some comments");
            inputError = true;
        } else {
            inputError = false;
        }

    }

}