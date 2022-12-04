package com.pentaware.doorish;

import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import pl.droidsonroids.gif.GifImageView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.pentaware.doorish.model.Cart;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pentaware.doorish.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShowCartFragment extends BaseFragment implements ICartOperatoins {

    private ShowCartViewModel mViewModel;


    private List<Cart> mCartList;

    private List<CartListing> listItems;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ICartOperatoins mCartOperations;
    private GifImageView gifImageView;
    double iTotalAmont = 0;
    double iProductCharges = 0;
    double   iDeliveryCharges = 0;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private View mView;

    private ShowCartFragment(List<Cart>cartList){
        this.mCartList = cartList;
    }
    public static ShowCartFragment newInstance(List<Cart>cartList) {
        return new ShowCartFragment(cartList);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.show_cart_fragment, container, false);
        mCartOperations = this;
        setHasOptionsMenu(true);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ShowCartViewModel.class);
        // TODO: Use the ViewModel

        setHasOptionsMenu(true);
        gifImageView = mView.findViewById(R.id.gifView);


        recyclerView = (RecyclerView) mView.findViewById(R.id.cartRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mView.getContext()));

        LoadCart();
        loadRecyclerView();

        Button btnProceedToCheckout = (Button)mView.findViewById(R.id.btnProceedtoCheckout);
        btnProceedToCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //to be uncommented later on..
//                if(iTotalAmont < 100){
//                    Toast.makeText(mView.getContext(), "Minimum Order Amount should be INR 100", Toast.LENGTH_LONG).show();
//                    return;
//                }

                Intent intent = new Intent(getActivity(), CheckoutActivity.class);
                intent.putExtra("Net_Payable", iTotalAmont);
                intent.putExtra("Delivery_Charges", iDeliveryCharges);
                startActivity(intent);
            }
        });

    }

    private void LoadCart(){
        iProductCharges = 0;
        iTotalAmont = 0;

        TextView txtCartEmpty = (TextView)mView.findViewById(R.id.txtCartEmpty);
        LinearLayout layout = (LinearLayout)mView.findViewById(R.id.cartHeaders);
        TextView txtProductCharges = (TextView) mView.findViewById(R.id.txtProductCharges);
        TextView txtDeliveryCharges = (TextView)mView.findViewById(R.id.txtDeliveryChargres);
        TextView txtNetPayable = (TextView) mView.findViewById(R.id.txtNetPayable);
        TextView txtTip = (TextView) mView.findViewById(R.id.txtTip);

        if(mCartList.size() == 0){
            recyclerView.setVisibility(View.GONE);
            layout.setVisibility(View.GONE);
            txtCartEmpty.setVisibility(View.VISIBLE);
            return;
        }

        recyclerView.setVisibility(View.VISIBLE);
        layout.setVisibility(View.VISIBLE);
        txtCartEmpty.setVisibility(View.GONE);

        listItems = new ArrayList<>();
        for (Cart cart: mCartList) {
            double offerPrice = CommonMethods.getOfferPrice(cart.Qty, cart.product);
            cart.product.Offer_Price = (float) offerPrice;
            iProductCharges += cart.Qty * offerPrice;
            CartListing cartListing = new CartListing(cart);
            listItems.add(cartListing);
        }


        txtProductCharges.setText( CommonVariables.rupeeSymbol +  CommonMethods.formatCurrency(iProductCharges));
        if(iProductCharges < 99){
            iDeliveryCharges = CommonVariables.deliveryCharges;
            txtTip.setVisibility(View.VISIBLE);
            txtTip.setText("**Shop more than 99 rupees and get your delivery free!!");
        }else{
            iDeliveryCharges = 0;
            txtTip.setVisibility(View.GONE);
        }

//        iDeliveryCharges = 0;
        txtDeliveryCharges.setText( CommonVariables.rupeeSymbol +  CommonMethods.formatCurrency(iDeliveryCharges));
        iTotalAmont = iProductCharges + iDeliveryCharges;
        txtNetPayable.setText( CommonVariables.rupeeSymbol +  CommonMethods.formatCurrency(iTotalAmont));

        for(int i = 0; i < listItems.size(); i++){
            CartListing listItem = listItems.get(i);
            Cart cart = listItem.getmCart();
            Product product = cart.product;
            float variantPrice = 0;
            double offerPrice = getOfferPrice(cart, product);
            if(offerPrice == 0){
                int finalI = i;
                db.collection("users").document(CommonVariables.loggedInUserDetails.customer_id)
                        .collection("cart").document(listItem.getmCart().CartId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("remove_cart", "removed: " + listItem.getmCart().CartId);
                        listItems.remove(finalI);
                    }
                });
            }
        }
    }

    public void loadRecyclerView(){
        adapter = new AdapterCartListing(listItems, mView.getContext(), mCartOperations);
        recyclerView.setAdapter(adapter);
        gifImageView.setVisibility(View.GONE);
    }

    public double getOfferPrice(Cart cart, Product product) {
        double offerPrice =  CommonMethods.getOfferPrice(cart.Qty, product); // product.Offer_Price;

        String sVariants = "";
        if(cart.Variants != null){

            if(cart.Variants.size() > 0){

                for (Map.Entry<String, String> entry : cart.Variants.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    sVariants += "<b>" + key + ":  " + "</b>" + value + "<br/>";

                    if(product.variant_pricing){
                        if(product.variant_pricing_attribute.trim().equals(key.trim())){
                            offerPrice =  product.variant_price_map.get(value.trim());
                        }
                    }
                }
            }

        }
        return offerPrice;
    }

    @Override
    public void DoCartOperations(Enums.CartOperations operation, Cart cart, int position) {
        if(operation == Enums.CartOperations.RemoveFromCart){
            mCartList.remove(cart);

            if(CommonVariables.buyNowOption == false){
                db.collection("users").document(CommonVariables.m_sFirebaseUserId).collection("cart").document(cart.CartId)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                CommonVariables.cartlist.remove(cart);
                                getActivity().invalidateOptionsMenu();
                                Toast.makeText(mView.getContext(), "Item removed from cart", Toast.LENGTH_SHORT).show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

            }



            LoadCart();
            loadRecyclerView();
        }

        if(operation == Enums.CartOperations.AddQuantity){

            int iQty = cart.Qty;

            if(iQty == 5){
                Toast.makeText(getContext(), "You cannot add more Quantities for this item", Toast.LENGTH_LONG).show();
                return;
            }

            iQty++;
            cart.Qty = iQty;
            LoadCart();


            if(CommonVariables.buyNowOption == false) {
                DocumentReference docRef = db.collection("users").document(CommonVariables.m_sFirebaseUserId).collection("cart")
                        .document(cart.CartId);


                docRef
                        .update("Qty", Integer.toString(iQty))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                adapter.notifyDataSetChanged();

                            }

                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Log.w(TAG, "Error updating document", e);
                            }
                        });
            }


        }

        if(operation == Enums.CartOperations.RemoveQuantity) {

            int iQty = cart.Qty;
            iQty--;
            cart.Qty = iQty;
            LoadCart();

            if (CommonVariables.buyNowOption == false) {
                DocumentReference docRef = db.collection("users").document(CommonVariables.m_sFirebaseUserId).collection("cart")
                        .document(cart.CartId);


                docRef
                        .update("Qty", Integer.toString(iQty))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Log.w(TAG, "Error updating document", e);
                            }
                        });

            }



        }

        if(operation == Enums.CartOperations.SelectProduct){

            CommonVariables.selectedProduct = cart.product;
            LaunchLandingPageToShowProduct();

//            Fragment newFragment = ProductDetailFragment.newInstance(cart.product);
//            FragmentTransaction transaction = getFragmentManager().beginTransaction();
//
//
//            transaction.hide(this);
//            transaction.add(R.id.nav_host_fragment, newFragment);
//
//            //  transaction.replace(R.id.nav_host_fragment, newFragment);
//            transaction.addToBackStack(null);
//            transaction.commit();
        }



    }

    private void LaunchLandingPageToShowProduct(){
        CommonVariables.buyNowOption = false;
        Intent intent = new Intent(mView.getContext(), LandingPage.class);
        //prevent the user to come again to this screen in he presses back btton
      //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);

        intent.putExtra("page", "show_product");

        startActivity(intent);
    }
}
