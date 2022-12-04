package com.pentaware.doorish.ui.feedback;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.pentaware.doorish.AdapterFeedbackList;
import com.pentaware.doorish.BaseFragment;
import com.pentaware.doorish.R;
import com.pentaware.doorish.model.Feedback;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class ViewFeedbackFragment extends BaseFragment {

    private RecyclerView recyclerViewFeedback;
    private TextView txtNoFeedback;
    private GifImageView progressBar;
    private FirebaseFirestore db;
    private List<Feedback> feedbackList;
    private AdapterFeedbackList adapter;
    private View root;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_view_feedback, container, false);

        db = FirebaseFirestore.getInstance();
        recyclerViewFeedback = root.findViewById(R.id.recycler_view_feedback);
        txtNoFeedback = root.findViewById(R.id.txt_no_feedback);
        progressBar = root.findViewById(R.id.progress_bar);

        feedbackList = new ArrayList<>();
        recyclerViewFeedback.setHasFixedSize(true);
        recyclerViewFeedback.setLayoutManager(new LinearLayoutManager(root.getContext()));

        Query query = db.collection("feedbacks").whereEqualTo("active", true).orderBy("timestamp", Query.Direction.DESCENDING);
        getFeedbackList(query);

        return root;
    }

    private void getFeedbackList(Query query) {
        query.get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                if (task.getResult() != null) {
                    if (task.getResult().size() == 0) {
                        Log.d("get_feedback_list", "empty ");
                        progressBar.setVisibility(View.GONE);
                        txtNoFeedback.setVisibility(View.VISIBLE);
                    }

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Feedback feedback = document.toObject(Feedback.class);
                        feedbackList.add(new Feedback(feedback));
                    }

                    adapter = new AdapterFeedbackList(feedbackList, root.getContext());
                    recyclerViewFeedback.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);
                }

            } else {
                progressBar.setVisibility(View.GONE);
                txtNoFeedback.setVisibility(View.VISIBLE);
                Log.d("get_feedback_list", "error ");

            }
        });
    }
}