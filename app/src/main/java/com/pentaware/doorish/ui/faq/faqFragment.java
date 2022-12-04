package com.pentaware.doorish.ui.faq;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pentaware.doorish.AdapterShopListing;

import com.pentaware.doorish.BaseFragment;
import com.pentaware.doorish.R;

import com.pentaware.doorish.model.FAQ;


import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class faqFragment extends BaseFragment {

    private View mView;
    private TextView txtNoItemToDisplay;
    private GifImageView gifImageView;
    private RecyclerView recyclerView;

    private List<faqListing> listItems;
    private RecyclerView.Adapter adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    public static faqFragment newInstance() {
        return new faqFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.faq_fragment, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel

        txtNoItemToDisplay = mView.findViewById(R.id.noItemTodisplay);
        gifImageView = mView.findViewById(R.id.gifView);
        recyclerView = mView.findViewById(R.id.faqRecycler);

        listItems =  new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mView.getContext()));


        Query query =    db.collection("faq");
        showFAQ(query);
    }

    private void showFAQ(Query query){

        query
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult().size() == 0){
                                txtNoItemToDisplay.setVisibility(View.VISIBLE);

                            }
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                FAQ faq = (FAQ) document.toObject(FAQ.class);
                                faqListing faqListing = new faqListing(faq);
                                listItems.add(faqListing);
                            }

                            adapter = new AdapterFAQListing(listItems, mView.getContext());
                            recyclerView.setAdapter(adapter);

                            gifImageView.setVisibility(View.GONE);

                        } else {
                            gifImageView.setVisibility(View.GONE);
                            txtNoItemToDisplay.setVisibility(View.VISIBLE);
                            // Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

}