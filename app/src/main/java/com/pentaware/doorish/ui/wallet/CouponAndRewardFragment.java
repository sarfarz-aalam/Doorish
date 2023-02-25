package com.pentaware.doorish.ui.wallet;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.pentaware.doorish.AdapterCouponAndRewards;
import com.pentaware.doorish.AdapterWalletStatement;
import com.pentaware.doorish.BaseFragment;
import com.pentaware.doorish.CartFragmentNav;
import com.pentaware.doorish.CommonVariables;
import com.pentaware.doorish.ICoupon;
import com.pentaware.doorish.R;
import com.pentaware.doorish.model.Coupon;
import com.pentaware.doorish.model.WalletStatement;
import com.pentaware.doorish.ui.products.ProductFragment;

import java.util.ArrayList;
import java.util.List;

public class CouponAndRewardFragment extends BaseFragment implements ICoupon {

    private RecyclerView recyclerViewCoupons;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Coupon> couponList = new ArrayList<>();
    private AdapterCouponAndRewards adapterCouponAndRewards;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coupon_and_reward, container, false);
        recyclerViewCoupons = view.findViewById(R.id.recycler_view_coupons);

        getCoupons();

        return view;
    }

    private void getCoupons() {
        db.collection("coupons").whereEqualTo("active", true).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                if(task.getResult() != null){
                    QuerySnapshot snapshot = task.getResult();

                    for(DocumentSnapshot st: snapshot){
                        Coupon coupon = st.toObject(Coupon.class);
                        couponList.add(coupon);
                    }
                }
                setUpRecyclerView();
            }
        });
    }

    private void setUpRecyclerView() {
        recyclerViewCoupons.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapterCouponAndRewards = new AdapterCouponAndRewards(getContext(), couponList, CouponAndRewardFragment.this);
        recyclerViewCoupons.setAdapter(adapterCouponAndRewards);
    }

    @Override
    public void applyCoupon(Coupon coupon) {
        //Coupon coupon = couponList.get(position);
        CommonVariables.selected_coupon = coupon;

//        Fragment newFragment = new CartFragmentNav();
//        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//
//        transaction.hide(CouponAndRewardFragment.this);
//        transaction.replace(R.id.nav_host_fragment, newFragment);
//
//        transaction.addToBackStack(null);
//        transaction.commit();
        //getActivity().getFragmentManager().popBackStack();

        getActivity().onBackPressed();

    }
}