package com.pentaware.doorish;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import pl.droidsonroids.gif.GifImageView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pentaware.doorish.model.Product;
import com.pentaware.doorish.model.Reviews;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pentaware.doorish.R;

import java.util.ArrayList;
import java.util.List;

public class ShowReviewsFragment extends BaseFragment {

    private ShowReviewsViewModel mViewModel;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    GifImageView gifImageView;

    private Product mProduct;

    private List<ReviewListing> listItems;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private View mView;

    private ShowReviewsFragment(Product product){
        mProduct = product;
    }
    public static ShowReviewsFragment newInstance(Product product) {
        return new ShowReviewsFragment(product);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.show_reviews_fragment, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ShowReviewsViewModel.class);
        // TODO: Use the ViewModel
        gifImageView = (GifImageView)mView.findViewById(R.id.gifView);


        listItems = new ArrayList<>();
        recyclerView = (RecyclerView) mView.findViewById(R.id.ReviewsRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mView.getContext()));

        LoadAllReviews();
    }

    private void LoadAllReviews(){
        db.collection("reviews").whereEqualTo("product_id", mProduct.Product_Id).whereEqualTo("active", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Reviews reviews = document.toObject(Reviews.class);
                                ReviewListing reviewListing = new ReviewListing(reviews);
                                listItems.add(reviewListing);
                                // Log.d("TAG", "getting documents: ", task.getException());
                            }
                            adapter = new AdapterReviewsListing(listItems, mView.getContext());
                            recyclerView.setAdapter(adapter);
                            gifImageView.setVisibility(View.GONE);
                        }
                        else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}

