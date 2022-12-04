package com.pentaware.doorish;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pentaware.doorish.model.OfflineOrder;
import com.pentaware.doorish.model.OfflineProduct;
import com.pentaware.doorish.model.Orders;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewOfflineOrdersFragment extends Fragment implements AdapterOfflineOrders.IOfflineOrders {

    private RecyclerView rvViewOfflineOrders;
    List<OfflineOrder> offlineOrders;
    private Context mContext;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView.Adapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_offline_orders, container, false);

        offlineOrders = new ArrayList<>();
        mContext = getContext();
        rvViewOfflineOrders = view.findViewById(R.id.rv_view_offline_orders);
        getOfflineOrders();


        return view;
    }

    private void getOfflineOrders() {
        db.collection("orders").whereEqualTo("offline_order", true)
        .orderBy("order_date", Query.Direction.DESCENDING)
        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (DocumentSnapshot order : task.getResult())
                    {
                        OfflineOrder offlineOrder = order.toObject(OfflineOrder.class);
                        offlineOrders.add(offlineOrder);
                    }
                    initRecyclerView();
                }
            }
        });
    }

    private void initRecyclerView() {
        rvViewOfflineOrders.setHasFixedSize(true);
        rvViewOfflineOrders.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new AdapterOfflineOrders(mContext, offlineOrders, ViewOfflineOrdersFragment.this);
        rvViewOfflineOrders.setAdapter(adapter);
    }


    @Override
    public void onClickViewProducts(int position) {
        ArrayList<OfflineProduct> offlineProductArrayList = new ArrayList<>();
        String orderId = offlineOrders.get(position).getOrder_id();
        db.collection("orders").document(orderId).collection("offline_products")
            .orderBy("order_date", Query.Direction.DESCENDING)
               .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){

                    for (DocumentSnapshot documentSnapshot : task.getResult()){
                        offlineProductArrayList.add(documentSnapshot.toObject(OfflineProduct.class));
                    }
                    if(offlineProductArrayList.isEmpty()){
                        Toast.makeText(getContext(), "No offline products", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Intent intent = new Intent(getContext(), ViewOfflineProductsActivity.class);
                        intent.putParcelableArrayListExtra("offline_product_list", offlineProductArrayList);
                        startActivity(intent);
                    }
                }

            }
        });
    }
}
