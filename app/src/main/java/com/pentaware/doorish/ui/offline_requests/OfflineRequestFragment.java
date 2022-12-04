package com.pentaware.doorish.ui.offline_requests;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pentaware.doorish.AdapterCityOrdersListing;
import com.pentaware.doorish.AdapterOfflineRequest;
import com.pentaware.doorish.CityOrdersListing;
import com.pentaware.doorish.CommonVariables;
import com.pentaware.doorish.IOfflineRequests;
import com.pentaware.doorish.LandingPage;
import com.pentaware.doorish.OfflineRequestListing;
import com.pentaware.doorish.Popup;
import com.pentaware.doorish.R;
import com.pentaware.doorish.UserOtherDetails;
import com.pentaware.doorish.model.Offline_Orders;
import com.pentaware.doorish.model.Offline_Requests;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class OfflineRequestFragment extends Fragment implements IOfflineRequests {

    private OfflineRequestViewModel mViewModel;

    private View mView;
    private List<OfflineRequestListing> listItems;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    GifImageView gifImage;
    private IOfflineRequests offlineRequests;

    public static OfflineRequestFragment newInstance() {
        return new OfflineRequestFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.offline_request_fragment, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(OfflineRequestViewModel.class);
        if(CommonVariables.loggedInUserDetails == null){
            FrameLayout frameLayout = mView.findViewById(R.id.frameFragment);
            frameLayout.setVisibility(View.GONE);
            showPopup();
            return;
        }
        offlineRequests = this;
        // TODO: Use the ViewModel

        gifImage = (GifImageView)mView.findViewById(R.id.gifView);

        listItems = new ArrayList<>();
        recyclerView = (RecyclerView) mView.findViewById(R.id.offlineRequestRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mView.getContext()));

        Query query =  db.collection("offline_requests")
                .whereEqualTo("customer_id", CommonVariables.loggedInUserDetails.customer_id)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        LoadOfflineOrders(query);

    }

    public void LoadOfflineOrders(Query query){

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    QuerySnapshot documentSnapshots = task.getResult();
                    if (documentSnapshots.size() == 0) {
                        gifImage.setVisibility(View.GONE);
                        Toast.makeText(mView.getContext(), "No orders found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int orderCount = task.getResult().size();
                    if (orderCount == 0) {

                        gifImage.setVisibility(View.GONE);
                        Toast.makeText(mView.getContext(), "No order found", Toast.LENGTH_SHORT).show();
                        return;

                    }

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Offline_Requests order = document.toObject(Offline_Requests.class);
                        OfflineRequestListing listing = new OfflineRequestListing(order);
                        listItems.add(listing);
                    }

                    adapter = new AdapterOfflineRequest(listItems, mView.getContext(), offlineRequests);
                    recyclerView.setAdapter(adapter);
                    gifImage.setVisibility(View.GONE);

                }
                else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }

            }
        });

    }

    @Override
    public void acceptEnquiry(Offline_Requests requests) {
        updateStaus(requests, 3 );
    }

    @Override

    public void rejectEnquery(Offline_Requests requests) {

        rejectEnquiry(requests, 4);

    }

    private void updateStaus(Offline_Requests req,  int statusCode){


        DocumentReference washingtonRef =  db.collection("offline_requests").document(req.doc_id);
        // Set the "isCapital" field of the city 'DC'
        washingtonRef
                .update("status_code", statusCode)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                      Toast.makeText( mView.getContext(), "Status Updated. Taking to Home Page", Toast.LENGTH_SHORT).show();
                      LaunchLandingPage();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText( mView.getContext(), "Acceptance sent to seller", Toast.LENGTH_SHORT).show();
//                        LaunchLandingPage();
                    }
                });


    }

    private void rejectEnquiry(Offline_Requests req, int statusCode){
        DocumentReference washingtonRef =  db.collection("offline_requests").document(req.doc_id);
        // Set the "isCapital" field of the city 'DC'
        washingtonRef
                .update(
                        "status_code", statusCode,
                        "buyer_rejection_reason", req.buyer_rejection_reason
                )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText( mView.getContext(), "Status Updated. Taking to Home Page", Toast.LENGTH_SHORT).show();
                        LaunchLandingPage();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText( mView.getContext(), "Acceptance sent to seller", Toast.LENGTH_SHORT).show();
//                        LaunchLandingPage();
                    }
                });
    }

    private void LaunchLandingPage(){
        Intent intent = new Intent(mView.getContext(), LandingPage.class);
        //prevent the user to come again to this screen in he presses back btton
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
    }

    private void showPopup(){
        Intent intent = new Intent(getActivity(), Popup.class);
        startActivity(intent);
    }

}

