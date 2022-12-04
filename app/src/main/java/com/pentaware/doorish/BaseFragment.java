package com.pentaware.doorish;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.KeyEvent;

import com.pentaware.doorish.model.Cart;
import com.pentaware.doorish.model.Product;
import com.pentaware.doorish.ui.address.AddressFragment;
import com.pentaware.doorish.ui.feedback.FeedbackFragment;
import com.pentaware.doorish.ui.orders.MyOrdersFragment;
import com.pentaware.doorish.ui.products.ProductDetailFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class BaseFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void ShowCart(List<Cart> cartList) {

//        Fragment fragmentCart = ShowCartFragment.newInstance(cartList);
//        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//        transaction.replace(R.id.nav_host_fragment, fragmentCart);
//        transaction.addToBackStack(null);
//        transaction.commit();
        Log.d("show_cart", "inside method 1");
        LoadProductsToCartFromDb();
    }

    public void ShowProduct(Product product) {

        Fragment fragmentProduct = ProductDetailFragment.newInstance(product);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment, fragmentProduct);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    protected boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Enclose everything in a try block so we can just
        // use the default view if anything goes wrong.
        try {
            // Get the font size value from SharedPreferences.
            SharedPreferences settings =
                    getActivity().getSharedPreferences("com.example.YourAppPackage", Context.MODE_PRIVATE);

            // Get the font size option.  We use "FONT_SIZE" as the key.
            // Make sure to use this key when you set the value in SharedPreferences.
            // We specify "Medium" as the default value, if it does not exist.
            String fontSizePref = settings.getString("FONT_SIZE", "Medium");

            // Select the proper theme ID.
            // These will correspond to your theme names as defined in themes.xml.
            int themeID = R.style.FontSizeMedium;
            if (fontSizePref == "Small") {
                themeID = R.style.FontSizeSmall;
            } else if (fontSizePref == "Large") {
                themeID = R.style.FontSizeLarge;
            }

            // Set the theme for the activity.
            getActivity().setTheme(themeID);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    int idx = 0;
    int cartItems = 0;

    private void LoadProductsToCartFromDb() {
        if (CommonVariables.m_sFirebaseUserId == null) {
            return;
        }
        Log.d("show_cart", "inside method 2");
        CommonVariables.buyNowOption = false;
        CommonVariables.cartlist.clear();
        db.collection("users").document(CommonVariables.m_sFirebaseUserId).collection("cart")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            Log.d("show_cart", "inside method 3");
                            cartItems = task.getResult().size();

                            if (cartItems == 0) {
                                showCartItems();
                            } else {
                                for (final QueryDocumentSnapshot document : task.getResult()) {
                                    String sProductId = document.get("Product_id").toString();
                                    final String sCartId = document.get("CartId").toString();
                                    final int qty = Integer.parseInt(document.get("Qty").toString());
                                    final HashMap<String, String> mapVariants = (HashMap<String, String>) document.get("Variants");
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
                                                                // Thread.sleep(200);
                                                                Fragment fragmentCart = new CartFragmentNav();
                                                                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                                                transaction.replace(R.id.nav_host_fragment, fragmentCart);
                                                                transaction.addToBackStack(null);
                                                                transaction.commit();

                                                            }


                                                        }
                                                    }


                                                }
                                            });

                                }
                            }


                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    public void showCartItems() {
        Fragment fragmentCart = new CartFragmentNav();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment, fragmentCart);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void changeDeliveryAddress() {
        Fragment fragmentCart = new AddressFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment, fragmentCart);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void showMyOrders(){
        Fragment fragmentCart = new MyOrdersFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment, fragmentCart);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}

