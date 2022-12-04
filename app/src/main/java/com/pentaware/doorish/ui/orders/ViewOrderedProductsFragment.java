package com.pentaware.doorish.ui.orders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pentaware.doorish.AdapterViewOrderedProducts;
import com.pentaware.doorish.R;
import com.pentaware.doorish.model.Orders;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ViewOrderedProductsFragment extends Fragment implements AdapterViewOrderedProducts.IViewProductOperations {

    private View mView;
    private RecyclerView recyclerViewViewProducts;
    private AdapterViewOrderedProducts adapter;
    private Orders mOrder;
    private FirebaseFirestore db;

    private ViewOrderedProductsFragment(Orders order) {
        mOrder = order;
    }

    public static ViewOrderedProductsFragment newInstance(Orders order) {
        return new ViewOrderedProductsFragment(order);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_view_ordered_products, container, false);

        db = FirebaseFirestore.getInstance();

        recyclerViewViewProducts = mView.findViewById(R.id.recycler_view_view_products);
        initRecyclerView();
        return mView;
    }

    private void initRecyclerView() {
        recyclerViewViewProducts.setHasFixedSize(true);
        recyclerViewViewProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdapterViewOrderedProducts(mOrder, getContext(), ViewOrderedProductsFragment.this);
        recyclerViewViewProducts.setAdapter(adapter);
    }

    @Override
    public void cancelProduct(Orders order, int index, String reason) {

        order.product_status_list.set(index, "cancelled");
        order.cancelled_by.set(index, "customer");
        order.product_offer_prices.set(index, (double) 0);
        order.cancellation_reason = reason;
        db.collection("online_orders").document(order.order_id).set(order).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getContext(), "Product cancelled", Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
                int cancelledProducts = 0;
                for (int i = 0; i < mOrder.product_ids.size(); i++) {
                    if (mOrder.product_status_list.get(i).equals("cancelled")) {
                        cancelledProducts++;
                    }
                }
                if (cancelledProducts == mOrder.product_ids.size()) {
                    cancelCompleteOrder(mOrder);
                }
            }
        });
    }

    @Override
    public void reviewProduct(Orders order, int index) {
        Fragment newFragment = ReviewProductFragment.newInstance(mOrder, index);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.hide(this);
        transaction.add(R.id.nav_host_fragment, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void returnProduct(Orders order, int index, String reason) {

        mOrder.Status = "Return requested";
        mOrder.product_status_list.set(index, "Return requested");
        mOrder.return_reasons.set(index, reason);
        db.collection("online_orders").document(mOrder.order_id).set(mOrder).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(), "Return request raised successfully", Toast.LENGTH_SHORT).show();
                getFragmentManager().popBackStack();
            }
        });
    }


    @Override
    public void replaceProduct(Orders order, int index, String reason) {
        mOrder.Status = "Replacement requested";
        mOrder.product_status_list.set(index, "Replacement requested");
        mOrder.return_reasons.set(index, reason);
        db.collection("online_orders").document(mOrder.order_id).set(mOrder).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(), "Replacement request raised successfully", Toast.LENGTH_SHORT).show();
                getFragmentManager().popBackStack();
            }
        });
    }

    private void cancelCompleteOrder(Orders mOrder) {
        mOrder.Status = "Order Cancelled by Customer";
        mOrder.cancelled = true;
        db.collection("online_orders").document(mOrder.order_id).set(mOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                adapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "Order Cancelled", Toast.LENGTH_SHORT).show();
                double totalAmountPayable = 0;
                for (int i = 0; i < mOrder.product_ids.size(); i++) {
                    double offerPrice = mOrder.product_offer_prices.get(i);
                    Long qty = mOrder.product_qty_list.get(i);
                    totalAmountPayable += offerPrice * qty;
                }

                double balance = totalAmountPayable - mOrder.wallet_money_used;
                if (balance < 0) {
                    creditPoints(balance);
                }
            }
        });
    }

    private void creditPoints(double balance) {

        balance = -balance;

        db.collection("users").document(mOrder.customer_id).update("points", balance).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("value_here", "updated successfully");
            }
        });
    }
}