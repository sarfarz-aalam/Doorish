package com.pentaware.doorish;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import pl.droidsonroids.gif.GifImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pentaware.doorish.BaseFragment;
import com.pentaware.doorish.IShopOperations;
import com.pentaware.doorish.ShopListing;
import com.pentaware.doorish.ShopViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.Query;
import com.pentaware.doorish.model.Product;
import com.pentaware.doorish.model.Seller;
import com.pentaware.doorish.model.Shops;
import com.pentaware.doorish.ui.products.ProductFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pentaware.doorish.R;
import com.pentaware.doorish.ui.products.*;


import java.util.ArrayList;
import java.util.List;

public class ShopFragment extends BaseFragment implements IShopOperations {

    private ShopViewModel mViewModel;


    private List<ShopListing> listItems;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private IShopOperations shopOperations;
    TextView txtnoItemTodisplay;
    GifImageView gifImageView;

    private String mCategory;
    private String mSubCategory;
    private boolean m_bFavorite;
    private String mAreaPin = "";


    FirebaseFirestore db = FirebaseFirestore.getInstance();



    private View mView;
    public static ShopFragment newInstance() {
        return new ShopFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.shop_fragment, container, false);



        txtnoItemTodisplay = mView.findViewById(R.id.noItemTodisplay);
        gifImageView = mView.findViewById(R.id.gifView);

        gifImageView.setVisibility(View.VISIBLE);

        listItems = new ArrayList<>();
        recyclerView = (RecyclerView) mView.findViewById(R.id.shopRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mView.getContext()));

        if(getArguments() != null)
        {
            //String sCategory = getArguments().getCategory();
            String sCategory = ProductFragmentArgs.fromBundle(getArguments()).getCategory();

            String []arrData = sCategory.split("\\|");
            if(arrData.length >= 2) {
                mCategory = arrData[0];
                mSubCategory = arrData[1];
                mAreaPin = arrData[2];
            }

            if(sCategory.equals("favorite")){
                m_bFavorite = true;
            }
        }

        CollectionReference shopRef = db.collection("seller");



        Query query;

       if(m_bFavorite){

           query = db.collection("seller")
                   .whereArrayContains("wishlist_customers", CommonVariables.m_sFirebaseUserId);
       }
       else{
           if(mSubCategory.toUpperCase().trim().equals("ALL")){
               query = db.collection("seller")
                       .whereEqualTo("seller_area_pin", mAreaPin)
                       .whereEqualTo("status", "approved")
                       .whereEqualTo("city_seller", true)
                       .whereEqualTo("seller_category", mCategory);
           }
           else {
               query = db.collection("seller")
                       .whereEqualTo("seller_area_pin", mAreaPin)
                       .whereEqualTo("status", "approved")
                       .whereEqualTo("city_seller", true)
                       .whereEqualTo("seller_category", mCategory)
                       .whereArrayContains("seller_subcategories", mSubCategory);
           }

       }

        showShopsForCity(query);

        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ShopViewModel.class);
        shopOperations = this;
        // TODO: Use the ViewModel

        if(CommonVariables.loggedInUserDetails == null){
            gifImageView.setVisibility(View.GONE);
            showPopup();
            return;
        }



    }

    private void ShowShopsForProduct(final Product product){

//        CollectionReference shopRef = db.collection("shops");
//
//
//        shopRef.whereArrayContains("Products", product.Product_Id)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                if(task.getResult().size() == 0){
//                                    txtnoItemTodisplay.setVisibility(View.VISIBLE);
//                                }
//                                //  Log.d(TAG, document.getId() + " => " + document.getData());
//                                Shops shop = (Shops) document.toObject(Shops.class);
//
//                                if(shop.city.toUpperCase().equals(CommonVariables.deliveryAddress.City.toUpperCase()))
//                                {
//                                    Object objPrice = shop.product_price_map.get(product.Product_Id);
//                                    shop.ProductPrice = Float.parseFloat(objPrice.toString());
//                                    shop.ProductName = product.Title;
//
//                                    ShopListing shopListing = new ShopListing(shop);
//                                    listItems.add(shopListing);
//                                }
//                            }
//
//                            adapter = new AdapterShopListing(listItems, mView.getContext());
//                            recyclerView.setAdapter(adapter);
//
//                            gifImageView.setVisibility(View.GONE);
//
//                        } else {
//                            // Log.d(TAG, "Error getting documents: ", task.getException());
//                        }
//                    }
//                });

    }

    private void showShopsForCity(Query query){

                query
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult().size() == 0){
                                txtnoItemTodisplay.setVisibility(View.VISIBLE);
                                if(m_bFavorite){
                                    txtnoItemTodisplay.setText("You have not added any shop to your favourite list");
                                }
                            }
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Seller seller = (Seller) document.toObject(Seller.class);
                                ShopListing shopListing = new ShopListing(seller);
                                listItems.add(shopListing);
                            }

                            adapter = new AdapterShopListing(listItems, mView.getContext(), shopOperations);
                            recyclerView.setAdapter(adapter);

                            gifImageView.setVisibility(View.GONE);

                        } else {
                            gifImageView.setVisibility(View.GONE);
                            txtnoItemTodisplay.setVisibility(View.VISIBLE);
                            // Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void showPopup(){
        Intent intent = new Intent(getActivity(), Popup.class);
        startActivity(intent);
    }



    @Override
    public void DoShopOperations(Seller seller) {

        Fragment newFragment = ProductFragment.newInstance();


        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        Bundle bund1 = new Bundle();
        bund1.putString("category", "seller|" + seller.seller_id);

        newFragment.setArguments(bund1);
        transaction.hide(this);
        transaction.add(R.id.nav_host_fragment, newFragment);

        // transaction.replace(R.id.nav_host_fragment, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    @Override
    public void onHeartClick(Seller seller) {

        if(seller.is_favourite){
            AddToWishList(seller, true);
        }
        else {
            AddToWishList(seller, false);
        }

    }

    private void AddToWishList(Seller seller, boolean bAdd){

        if(bAdd){
            if(!seller.wishlist_customers.contains(CommonVariables.m_sFirebaseUserId)){
                seller.wishlist_customers.add(CommonVariables.m_sFirebaseUserId);
            }
        }
        else{
            if(seller.wishlist_customers.contains(CommonVariables.m_sFirebaseUserId)){
                seller.wishlist_customers.remove(CommonVariables.m_sFirebaseUserId);
            }
        }
        db.collection("seller").document(seller.seller_id)
                .update("wishlist_customers", seller.wishlist_customers)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    @Override
    public void onPostYourRequirementClick(Seller seller) {

        Intent intent = new Intent(getActivity(), PostYOurRequirement.class);
        intent.putExtra("seller", seller);
        startActivity(intent);

    }
}
