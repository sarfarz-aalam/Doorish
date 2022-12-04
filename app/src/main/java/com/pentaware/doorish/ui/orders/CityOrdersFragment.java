package com.pentaware.doorish.ui.orders;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import pl.droidsonroids.gif.GifImageView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pentaware.doorish.AdapterCityOrdersListing;
import com.pentaware.doorish.AdapterOrderListing;
import com.pentaware.doorish.CityOrdersListing;
import com.pentaware.doorish.CommonVariables;
import com.pentaware.doorish.OrderListing;
import com.pentaware.doorish.R;
import com.pentaware.doorish.model.Offline_Orders;
import com.pentaware.doorish.model.Orders;

import java.util.ArrayList;
import java.util.List;

public class CityOrdersFragment extends Fragment {

    private CityOrdersViewModel mViewModel;

    private View mView;
    private List<CityOrdersListing> listItems;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    GifImageView gifImage;

    public static CityOrdersFragment newInstance() {
        return new CityOrdersFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.city_orders_fragment, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(CityOrdersViewModel.class);
        // TODO: Use the ViewModel

        gifImage = (GifImageView)mView.findViewById(R.id.gifView);

        listItems = new ArrayList<>();
        recyclerView = (RecyclerView) mView.findViewById(R.id.orderRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mView.getContext()));

        Query query =  db.collection("offline_invoices")
                .whereEqualTo("customer_id", CommonVariables.m_sFirebaseUserId)
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
                        Offline_Orders order = document.toObject(Offline_Orders.class);
                        CityOrdersListing listing = new CityOrdersListing(order);
                        listItems.add(listing);
                    }

                    adapter = new AdapterCityOrdersListing(listItems, mView.getContext());
                    recyclerView.setAdapter(adapter);
                    gifImage.setVisibility(View.GONE);

                }
                else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }

            }
        });

    }

}
