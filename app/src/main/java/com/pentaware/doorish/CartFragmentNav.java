package com.pentaware.doorish;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pentaware.doorish.model.Cart;
import com.pentaware.doorish.model.Product;
import com.pentaware.doorish.model.User;
import com.pentaware.doorish.ui.orders.MyOrdersFragment;
import com.pentaware.doorish.ui.products.ProductFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

public class CartFragmentNav extends BaseFragment implements ICartOperatoins {

    private List<Cart> mCartList;
    private TextView txtEmptyCart;
    private RelativeLayout layoutEmptyCart;
    private ImageView imgEmptyCart;
    private double mNetPayable = 0;
    TextView txtNetPayable;
    double originalTotalAmount = 0;
    private LinearLayout cartLayout;
    private List<CartListing> listItems;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ICartOperatoins mCartOperations;
    private GifImageView gifImageView;
    double iTotalAmont = 0;
    TextView txtWalletMoney;
    private Button btnShopNow;
    double iProductCharges = 0;
    double iMrpCharges = 0;
    double iDeliveryCharges = 0;
    private double mAvailableWalletPoints;
    private double mAvailableWalletMoney;
    private double mWalletMoneyUsed = 0;
    private double mRemainingWalletPoints = 0;
    private double mOriginalAvailableWalletMoney = 0;
    private double mOriginalNetPayable = 0;
    private double mOriginalRemainingWalletPoints = 0;
    private ProgressBar progressBarCart;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    int idx = 0;
    int cartItems = 0;
    View mView;
    CheckBox chkUseWallet;

    private TextView txtWalletMoneyUsed, txtDiscount, txtTotalSavings;

    MenuItem menuItem;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.cart_layout_nav, container, false);
        mCartOperations = this;
        setHasOptionsMenu(true);

        cartLayout = mView.findViewById(R.id.cart_layout);
        layoutEmptyCart = mView.findViewById(R.id.layout_cart_empty);
        txtEmptyCart = mView.findViewById(R.id.txt_cart_empty);
        imgEmptyCart = mView.findViewById(R.id.img_cart_empty);
        btnShopNow = mView.findViewById(R.id.btn_shop_now);
        txtWalletMoney = mView.findViewById(R.id.txtWalletMoney);
        txtNetPayable = (TextView) mView.findViewById(R.id.txtNetPayable);
        txtWalletMoneyUsed = mView.findViewById(R.id.txt_wallet_money_used);
        txtDiscount = mView.findViewById(R.id.txt_discount);
        txtTotalSavings = mView.findViewById(R.id.txt_savings);
        txtTotalSavings = mView.findViewById(R.id.txt_savings);

        progressBarCart = mView.findViewById(R.id.progress_bar_cart);



        if (CommonVariables.cartlist.size() == 0) {
            hideUI();
//            Toast.makeText(getContext(), "Cart is empty", Toast.LENGTH_SHORT).show();
        } else {
            LoadProductsToCartFromDb();
        }

        showAvailableWalletMoney();


        chkUseWallet = mView.findViewById(R.id.chkUseWalletMoney);

        chkUseWallet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UseWallet(isChecked);
            }
        });

        btnShopNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment newFragment = new ProductFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                Bundle bund1 = new Bundle();
                bund1.putString("category", "All");
                newFragment.setArguments(bund1);

                transaction.hide(CartFragmentNav.this);
                transaction.replace(R.id.nav_host_fragment, newFragment);

                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        return mView;
    }

    private void hideUI() {
        layoutEmptyCart.setVisibility(View.VISIBLE);
        cartLayout.setVisibility(View.INVISIBLE);
    }

    public void showCartItems() {
        gifImageView = mView.findViewById(R.id.gifView);
        recyclerView = (RecyclerView) mView.findViewById(R.id.cartRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mView.getContext()));

        LoadCart();
        loadRecyclerView();

        mOriginalNetPayable = iTotalAmont;


        Button btnProceedToCheckout = (Button) mView.findViewById(R.id.btnProceedtoCheckout);
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
                intent.putExtra("wallet_money_used", mWalletMoneyUsed);
                startActivity(intent);
            }
        });

    }

    private void LoadProductsToCartFromDb() {
        if (CommonVariables.m_sFirebaseUserId == null) {
            return;
        }
        CommonVariables.buyNowOption = false;
        CommonVariables.cartlist.clear();
        CommonVariables.priceTotalCart = 0;
        db.collection("users").document(CommonVariables.m_sFirebaseUserId).collection("cart")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            cartItems = task.getResult().size();
                            for (final QueryDocumentSnapshot document : task.getResult()) {
                                String sProductId = document.get("Product_id").toString();
                                final String sCartId = document.get("CartId").toString();
                                final int qty = Integer.parseInt(document.get("Qty").toString());
                                final HashMap<String, String> mapVariants = (HashMap<String, String>) document.get("Variants");
                                final boolean buy_by_weight = (boolean) document.get("buy_by_weight");
                                final double weight_in_gram = (double) document.get("weight_in_gram");
                                boolean is_city_product = false;
                                try {
                                    is_city_product = (boolean) document.get("is_city_product");
                                } catch (Exception ex) {
                                    is_city_product = false;
                                }


                                final boolean finalIs_city_product = is_city_product;
                                db.collection("products").document(sProductId).get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot docProduct = task.getResult();
                                                    if (docProduct.exists()) {

                                                        Product product = docProduct.toObject(Product.class);
                                                        product.buy_by_weight = buy_by_weight;
                                                        product.weight_in_gram = weight_in_gram;
                                                        if (buy_by_weight)
                                                            CommonVariables.priceTotalCart += weight_in_gram * product.price_per_gram;
                                                        else
                                                        CommonVariables.priceTotalCart += CommonMethods.getOfferPrice(qty, product);
                                                        Cart obCart = new Cart();
                                                        obCart.product = product;
                                                        obCart.CartId = sCartId;
                                                        obCart.Qty = qty;
                                                        obCart.Variants = mapVariants;
                                                        obCart.product.is_city_product = finalIs_city_product;
                                                        obCart.product.selectedVariants = mapVariants;
                                                        if (!CommonVariables.cartlist.contains(obCart))
                                                            CommonVariables.cartlist.add(obCart);
                                                        if (CommonVariables.cartlist.size() == cartItems) {
                                                            mCartList = CommonVariables.cartlist;
                                                            showCartItems();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }

                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    void UseWallet(boolean use) {
        mNetPayable = iTotalAmont;
        Log.d("net_payable", "payable = " + mNetPayable);
        if (use) {
            //COD option not available while using wallet
            //btnCOD.setVisibility(View.GONE);

            //when wallet money available is more than net payable
            if (mAvailableWalletMoney >= mNetPayable) {
                mWalletMoneyUsed = mNetPayable;
                //1 rupee = 8 points
                mRemainingWalletPoints = (int) CommonVariables.loggedInUserDetails.points - (mNetPayable * CommonVariables.NumberOfPointsInOneRupee);
//                btnCOD.setVisibility(View.GONE);
//                btnPrepaid.setText("Place Order");
                mNetPayable = 0;
            } else {
                mWalletMoneyUsed = mAvailableWalletMoney;
                //all points are consumed
                double dPointsConsumed = CommonVariables.loggedInUserDetails.points;
                mRemainingWalletPoints = 0;
                mNetPayable = mNetPayable - mWalletMoneyUsed;

            }

            String sNetPayable = CommonVariables.rupeeSymbol + CommonMethods.formatCurrency(mNetPayable);
            txtNetPayable.setText(sNetPayable);
            double iReaminingInWallet = mAvailableWalletMoney - mWalletMoneyUsed;
            String sAVailable = "Wallet Balance: " + CommonVariables.rupeeSymbol + CommonMethods.formatCurrency(iReaminingInWallet);
            txtWalletMoney.setText(sAVailable);

            Log.d("value_here", String.valueOf(iReaminingInWallet));

            CommonVariables.loggedInUserDetails.points = mRemainingWalletPoints;
            iTotalAmont = mNetPayable;

            txtWalletMoneyUsed.setText("-"  +CommonVariables.rupeeSymbol + CommonMethods.formatCurrency(mWalletMoneyUsed));
        } else {

            Log.d("net_payable", "payable og = " + mOriginalAvailableWalletMoney);

            mWalletMoneyUsed = 0;
            mNetPayable = mOriginalNetPayable;
            iTotalAmont = originalTotalAmount;
            mAvailableWalletMoney = mOriginalAvailableWalletMoney;
            mRemainingWalletPoints = mOriginalRemainingWalletPoints;

            String sAVailable = "Wallet Balance: " + CommonVariables.rupeeSymbol + CommonMethods.formatCurrency(mAvailableWalletMoney);
            txtWalletMoney.setText(sAVailable);

            String sNetPayable = CommonVariables.rupeeSymbol + CommonMethods.formatCurrency(iTotalAmont);
            txtNetPayable.setText(sNetPayable);

            CommonVariables.loggedInUserDetails.points = mRemainingWalletPoints;
            txtWalletMoneyUsed.setText(CommonVariables.rupeeSymbol + CommonMethods.formatCurrency(mWalletMoneyUsed));
//            btnCOD.setVisibility(View.VISIBLE);
        }

    }

    private void showAvailableWalletMoney() {

        DocumentReference docRef = db.collection("users").document(CommonVariables.m_sFirebaseUserId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        CommonVariables.loggedInUserDetails = document.toObject(User.class);
                        if (CommonVariables.loggedInUserDetails != null) {

                            mAvailableWalletPoints = CommonVariables.loggedInUserDetails.points;
                            //8 points make one rupee
                            mAvailableWalletMoney = mAvailableWalletPoints / CommonVariables.NumberOfPointsInOneRupee;
                            mOriginalAvailableWalletMoney = mAvailableWalletMoney;
                            String sWalletMoney = "Wallet Balance: " + CommonVariables.rupeeSymbol + CommonMethods.formatCurrency(mAvailableWalletMoney);
                            // CommonVariables.rupeeSymbol + CommonMethods.formatCurrency(mAvailableWalletMoney);
                            txtWalletMoney.setText(sWalletMoney);
                            if(mAvailableWalletMoney == 0 || mAvailableWalletMoney < 0){
                                chkUseWallet.setVisibility(View.GONE);
                            }
                        }

                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });

    }


    private void LoadCart() {
        iProductCharges = 0;
        iTotalAmont = 0;
        iMrpCharges = 0;

        TextView txtCartEmpty = (TextView) mView.findViewById(R.id.txtCartEmpty);
        LinearLayout layout = (LinearLayout) mView.findViewById(R.id.cartHeaders);
        TextView txtProductCharges = (TextView) mView.findViewById(R.id.txtProductCharges);
        TextView txtDeliveryCharges = (TextView) mView.findViewById(R.id.txtDeliveryChargres);
        TextView txtTip = (TextView) mView.findViewById(R.id.txtTip);

        if (mCartList != null) {
            if (mCartList.size() == 0) {
                recyclerView.setVisibility(View.GONE);
                layout.setVisibility(View.GONE);
//                txtCartEmpty.setVisibility(View.VISIBLE);
                hideUI();


                return;
            }
        }


        recyclerView.setVisibility(View.VISIBLE);
        layout.setVisibility(View.VISIBLE);
        txtCartEmpty.setVisibility(View.GONE);



        listItems = new ArrayList<>();
        for (Cart cart : mCartList) {
            double offerPrice = 0;
            if(cart.product.buy_by_weight)
                offerPrice = cart.product.weight_in_gram * cart.product.price_per_gram;
            else
                offerPrice = CommonMethods.getOfferPrice(cart.Qty, cart.product);

            Log.d("offer_price_weight", "price: " + offerPrice + cart.product.buy_by_weight);
            cart.product.Offer_Price = (float) offerPrice;
            double productMrp = CommonMethods.getMrpPrice(cart.Qty, cart.product);
            iProductCharges += cart.Qty * offerPrice;
            iMrpCharges += cart.Qty * productMrp;
            CartListing cartListing = new CartListing(cart);
            listItems.add(cartListing);
        }


        txtProductCharges.setText(CommonVariables.rupeeSymbol + CommonMethods.formatCurrency(iMrpCharges));
        CommonVariables.itemTotalPrice = iMrpCharges;
        Log.d("cart_fragment_load", "load cart");

        CommonVariables.priceTotalCart = iProductCharges;

        if(iProductCharges <= 99){
            Log.d("cart_fragment_load", "load cart if");
            iDeliveryCharges = CommonVariables.deliveryCharges;
            txtTip.setVisibility(View.VISIBLE);
            txtTip.setText("Shop for ₹99 or more & get free delivery");
            txtTip.setTextColor(getResources().getColor(R.color.colorPrimary));

        }else{
            iDeliveryCharges = 0;
            Log.d("cart_fragment_load", "load cart else");
            txtTip.setVisibility(View.VISIBLE);
            txtTip.setText("Yay! You saved ₹20 on delivery charge");
            txtTip.setTextColor(getResources().getColor(R.color.price_green));
        }

//        iDeliveryCharges = 0;


        txtWalletMoneyUsed.setText(CommonVariables.rupeeSymbol + 0);

        double discount = iMrpCharges - iProductCharges;
        txtDiscount.setText("-"  + CommonVariables.rupeeSymbol + CommonMethods.formatCurrency(discount));

        double savings = discount;


        if(iDeliveryCharges == 0){
            txtDeliveryCharges.setText("FREE");
            txtDeliveryCharges.setTextColor(getResources().getColor(R.color.price_green));
            savings += iDeliveryCharges;
        }

        else{
            txtDeliveryCharges.setText(CommonVariables.rupeeSymbol + CommonMethods.formatCurrency(iDeliveryCharges));
            txtDeliveryCharges.setTextColor(getResources().getColor(R.color.flat_black));
        }


        iTotalAmont = iProductCharges + iDeliveryCharges;
        originalTotalAmount = iTotalAmont;
        txtNetPayable.setText(CommonVariables.rupeeSymbol + CommonMethods.formatCurrency(iTotalAmont));
        CommonVariables.netPayable = iTotalAmont;


        //savings calculate
        double cashback = CommonVariables.points_to_credit_per_rupee * iProductCharges;
        savings += cashback;

        txtTotalSavings.setText(CommonVariables.rupeeSymbol + CommonMethods.formatCurrency(savings));
        CommonVariables.totalSavings = savings;



        for(int i = 0; i < listItems.size(); i++){
            CartListing listItem = listItems.get(i);
            Cart cart = listItem.getmCart();
            Product product = cart.product;
            float variantPrice = 0;
            double offerPrice = getOfferPrice(cart, product);
            if(offerPrice == 0){
                int finalI = i;
                String cartId = listItem.getmCart().CartId;
                CartListing cartItem = listItems.get(finalI);
                listItems.remove(finalI);
                CommonVariables.cartlist.remove(cartItem.getmCart());
                if (product.buy_by_weight)
                    CommonVariables.priceTotalCart -= product.weight_in_gram * product.price_per_gram;
                else
                CommonVariables.priceTotalCart -= CommonMethods.getOfferPrice(cart.Qty, product);
                Log.d("remove_cart", "removed: " + listItem.getmCart().CartId);
//                db.collection("users").document(CommonVariables.loggedInUserDetails.customer_id)
//                        .collection("cart").document(cartId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        Log.d("remove_cart", "removed: " + listItem.getmCart().CartId);
//                    }
//                });
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
        if (operation == Enums.CartOperations.RemoveFromCart) {
            mCartList.remove(cart);

            if (CommonVariables.buyNowOption == false) {
                db.collection("users").document(CommonVariables.m_sFirebaseUserId).collection("cart").document(cart.CartId)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
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

        if (operation == Enums.CartOperations.AddQuantity) {

            int iQty = cart.Qty;

            if(iQty == 5){
                Toast.makeText(getContext(), "You cannot add more Quantities for this item", Toast.LENGTH_LONG).show();
                return;
            }

            progressBarCart.setVisibility(View.VISIBLE);
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            iQty++;
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
                                progressBarCart.setVisibility(View.GONE);
                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressBarCart.setVisibility(View.GONE);
                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                // Log.w(TAG, "Error updating document", e);
                            }
                        });
            }


        }

        if (operation == Enums.CartOperations.RemoveQuantity) {

            progressBarCart.setVisibility(View.VISIBLE);
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

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
                                progressBarCart.setVisibility(View.GONE);
                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Log.w(TAG, "Error updating document", e);
                                progressBarCart.setVisibility(View.GONE);
                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            }
                        });

            }


        }

        if (operation == Enums.CartOperations.SelectProduct) {

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

    private void LaunchLandingPageToShowProduct() {
        CommonVariables.buyNowOption = false;
        Intent intent = new Intent(mView.getContext(), LandingPage.class);
        intent.putExtra("page", "show_product");
        startActivity(intent);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.testAction).setEnabled(false);
    }
}
