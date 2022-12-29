package com.pentaware.doorish;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.pentaware.doorish.model.AppInfo;
import com.pentaware.doorish.model.Cart;
import com.pentaware.doorish.model.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import android.os.Vibrator;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pentaware.doorish.model.User;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LandingPage extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private AppBarConfiguration mAppBarConfiguration;
    public int count = 0;
    int cartItems = 0;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String mVersion;
    private double totalPriceCart;
    private Uri selectedImage;
    private ImageView mImg;
    private View hView;
    private BottomNavigationView bottomNavigationView;
    private ProgressBar progressBar;

    NavController navController;

    private boolean mShowProduct = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
        checkVersion();

        getAndUpdateFCMTokenForGuests();

        if(getSupportActionBar() != null)
           getSupportActionBar().setElevation(0);



        List<String> toList = new ArrayList<>();
//        toList.add("manoj1985del@gmail.com");

//        new SendMailTask(SendMailActivity.this).execute(fromEmail,
//                fromPassword, toEmailList, emailSubject, emailBody);

        //new SendMailTask(LandingPage.this).execute("texpediscia@gmail.com", "shivyan@123", toList, "Hello", "Hello from android");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //WhatsApp Chat Action
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openWhatsappContact("Hi! Can you help me with my query?");
            }
        });

        progressBar = findViewById(R.id.progress_bar_activity);

        bottomNavigationView = findViewById(R.id.nav_bottom);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_wishlist, R.id.nav_slideshow, R.id.nav_city_orders,
                R.id.nav_tools, R.id.nav_city, R.id.nav_offline_requests, R.id.nav_video, R.id.nav_refer_and_earn, R.id.cartFragmentNav, R.id.nav_myprofile, R.id.nav_wallet_statement, R.id.nav_coupon_and_reward)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // Logout from navigation drawer
        MenuItem menuItemLogout = navigationView.getMenu().findItem(R.id.nav_sign_out);
        if (CommonVariables.loggedInUserDetails == null) {
            menuItemLogout.setTitle("Login");
        } else {
            menuItemLogout.setTitle("Logout");
        }
        menuItemLogout.setOnMenuItemClickListener(menuItem -> {
            logout();
            return true;
        });

        hView = navigationView.getHeaderView(0);


        // make sure to import android.support.v7.app.ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));
        }


        Intent iin = getIntent();
        Bundle b = iin.getExtras();

        if (b != null) {
            String sPage = (String) b.get("page");
            if (sPage.equals("order_created")) {
                CommonVariables.cartlist.clear();
                CommonVariables.priceTotalCart=0;
                totalPriceCart=0;
                count = 0;
                invalidateOptionsMenu();
                deleteCart();
            } else if (sPage.equals("show_product")) {
                //   LoadProductsToCartFromDb();
                invalidateOptionsMenu();
                mShowProduct = true;
            }


//            } else if(sPage.equals("orderDetail")){
//                NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
//                BaseFragment baseFragment = (BaseFragment) navHostFragment.getChildFragmentManager().getFragments().get(0);
//                baseFragment.showMyOrders();
//            }
           else {
                LoadProductsToCartFromDb();
            }


        } else {

            LoadProductsToCartFromDb();

        }



        CommonVariables.startedByActivity = false;

    }

    @Override
    protected void onStart() {
        super.onStart();

        TextView nav_user = (TextView) hView.findViewById(R.id.txtProfileName);
        TextView nav_email = (TextView) hView.findViewById(R.id.txtProfileEmail);
        TextView nav_wallet = (TextView) hView.findViewById(R.id.txtWalletMoney);
        ImageView imgUser = (ImageView) hView.findViewById(R.id.imgUser);
        TextView txtLogin = hView.findViewById(R.id.txt_login);
        LinearLayout layoutLogin = hView.findViewById(R.id.layout_log_in);

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        if (CommonVariables.loggedInUserDetails != null) {
            setPoints();
            nav_user.setText(CommonVariables.loggedInUserDetails.Name);
            nav_email.setText(CommonVariables.loggedInUserDetails.Email);
            nav_email.setVisibility(View.VISIBLE);
            layoutLogin.setVisibility(View.GONE);
            if (CommonVariables.loggedInUserDetails.img_url != null) {
                Glide.with(this)
                        .load(CommonVariables.loggedInUserDetails.img_url) // the uri you got from Firebase
                        .into(imgUser); //Your imageView variable
            }

        } else {
            nav_user.setText("Hello Guest!");
            nav_email.setVisibility(View.GONE);
            layoutLogin.setVisibility(View.VISIBLE);
            String sWalletMoney = "Wallet Balance: " + CommonVariables.rupeeSymbol + "0"; // + CommonVariables.loggedInUserDetails.points;
            nav_wallet.setText(sWalletMoney);
        }

        if (mShowProduct) {
            LoadProductsToCartFromDb();
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            BaseFragment baseFragment = (BaseFragment) navHostFragment.getChildFragmentManager().getFragments().get(0);
            baseFragment.ShowProduct(CommonVariables.selectedProduct);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        count = CommonVariables.cartlist.size();
        totalPriceCart = CommonVariables.priceTotalCart;
        MenuItem menuItem = menu.findItem(R.id.testAction);
        menuItem.setIcon(buildCounterDrawable(count, R.drawable.ic_shopping_cart_24px));


        MenuItem cartTotalMenu = menu.findItem(R.id.cart_total_price);
        String cartTotal = "Total\n" + CommonVariables.rupeeSymbol + String.format("%.2f", totalPriceCart);
        cartTotalMenu.setTitle(cartTotal);




        cartTotalMenu.setVisible(count != 0);
        if (count == 0)
        CommonVariables.priceTotalCart = 0;

        MenuItem menuItemLogout = menu.findItem(R.id.btnLogout);
        if (CommonVariables.loggedInUserDetails == null) {
            menuItemLogout.setTitle("Login");
        }

        for(int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if(item.getItemId() == R.id.cart_total_price){
                SpannableString spanString = new SpannableString(menu.getItem(i).getTitle().toString());
                int end = spanString.length();
                spanString.setSpan(new RelativeSizeSpan(0.9f), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                item.setTitle(spanString);
            }

        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (CommonVariables.loggedInUserDetails != null) {
            setPoints();
        }
        int id = item.getItemId();

        if (id == R.id.testAction) {
//            if (CommonVariables.cartlist.size() == 0) {
//                Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
//            } else {
                NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                BaseFragment baseFragment = (BaseFragment) navHostFragment.getChildFragmentManager().getFragments().get(0);
                baseFragment.ShowCart(CommonVariables.cartlist);
//
//                count = 0;
//                invalidateOptionsMenu();

                //  navController.navigate(R.id.cartFragmentNav);
//            }

            return true;
        }

        if (id == R.id.btnLogout) {
            logout();
        }

        if (id == R.id.btnReferAndEarn) {
//           openWhatsApp();
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            navHostFragment.getNavController().navigate(R.id.nav_refer_and_earn);
        }

//        if(id == R.id.btnPostYourRequirement){
//
//            String url = "https://myrupeaze.in/post-requirements/";
//            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//            startActivity(browserIntent);
//
//        }
//
//        if(id == R.id.btnSeller){
//            String url = "https://myrupeaze.com/seller_login.html";
//            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//            startActivity(browserIntent);
//        }
//
//        if(id == R.id.btnReferAndEarn){
//            String url = "https://myrupeaze.in/refer-and-earn";
//            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//            startActivity(browserIntent);
//        }

//        if(id == R.id.btnJackpot){
//            String url = "https://myrupeaze.in/jackpot/";
//            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//            startActivity(browserIntent);
//        }

        return super.onOptionsItemSelected(item);
    }

    public void changeDeliveryAddress(){
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        BaseFragment baseFragment = (BaseFragment) navHostFragment.getChildFragmentManager().getFragments().get(0);
        baseFragment.changeDeliveryAddress();
    }


    private Drawable buildCounterDrawable(int count, int backgroundImageId) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.feed_update_count, null);
        view.setBackgroundResource(backgroundImageId);

        if (count == 0) {
            View counterTextPanel = view.findViewById(R.id.counterValuePanel);
            counterTextPanel.setVisibility(View.GONE);
        } else {
            TextView textView = (TextView) view.findViewById(R.id.count);
            textView.setText("" + count);
        }

        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return new BitmapDrawable(getResources(), bitmap);
    }

    private void logout() {
        CommonVariables.deliveryAddress = null;
        CommonVariables.loggedInUserDetails = null;
        CommonVariables.m_sFirebaseUserId = null;

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();


        Intent intent = new Intent(LandingPage.this, Login.class);
        //prevent the user to come again to this screen in he presses back btton
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void doIncrease() {
        count++;
        invalidateOptionsMenu();
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


    }


    int idx = 0;

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
                                                        //totalPriceCart  +=  product.Offer_Price;

                                                        Cart obCart = new Cart();
                                                        obCart.product = product;
                                                        obCart.CartId = sCartId;
                                                        obCart.Qty = qty;
                                                        obCart.Variants = mapVariants;
                                                        obCart.product.is_city_product = finalIs_city_product;
                                                        obCart.product.selectedVariants = mapVariants;

                                                        product.buy_by_weight = buy_by_weight;
                                                        product.weight_in_gram = weight_in_gram;
                                                        if (buy_by_weight)
                                                            CommonVariables.priceTotalCart += (weight_in_gram * product.price_per_gram) * obCart.Qty;
                                                        else
                                                            // trying fix
                                                            CommonVariables.priceTotalCart += getOfferPrice(obCart, obCart.product) * obCart.Qty;
                                                        Log.d("price_total_cart", "price " + CommonVariables.priceTotalCart);

                                                        if (!CommonVariables.cartlist.contains(obCart))
                                                            CommonVariables.cartlist.add(obCart);
                                                        if (CommonVariables.cartlist.size() == cartItems) {
                                                            try {
                                                                Thread.sleep(200);
                                                            } catch (InterruptedException e) {
                                                                e.printStackTrace();
                                                            }
                                                            initiateCart();
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

    private void AddToCart(Product product) {

        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(100);

        count++;
        invalidateOptionsMenu();
    }

    public void initiateCart() {
        count = CommonVariables.cartlist.size();
        totalPriceCart = CommonVariables.priceTotalCart;
        invalidateOptionsMenu();
    }


    public void onAddToCart_Click(View view, FirestorePagingAdapter pagingAdapter, boolean loadCart) {

        if (CommonVariables.loggedInUserDetails == null) {
            showPopup();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        initiateCart();
        CommonVariables.buyNowOption = false;
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(100);

        CollectionReference cartCollection = db.collection("users").document(CommonVariables.loggedInUserDetails.customer_id).collection("cart");

        Query cartQuery = cartCollection.whereEqualTo("Product_id", CommonVariables.selectedCart.product.Product_Id).whereEqualTo("Variants", CommonVariables.selectedCart.product.selectedVariants);

        if (CommonVariables.selectedCart.product.selectedVariants.isEmpty()) {
            Log.d("value_here", "no variants");
            cartQuery = cartCollection.whereEqualTo("Product_id", CommonVariables.selectedCart.product.Product_Id);
        }

        cartQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot snapshots = task.getResult();
                    if (snapshots != null) {
                        if (snapshots.size() > 0) {
                            for (DocumentSnapshot documentSnapshot : snapshots) {
                                Cart cartItem = documentSnapshot.toObject(Cart.class);
                                if (cartItem != null) {
                                    if (cartItem.Variants.equals(CommonVariables.selectedCart.product.selectedVariants)) {
                                        cartItem.Qty++;
                                        double price = CommonMethods.getOfferPrice(cartItem.Qty, CommonVariables.selectedCart.product);
                                        CommonVariables.priceTotalCart += price;
                                        cartCollection.document(cartItem.CartId).update("Qty", cartItem.Qty).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    count = CommonVariables.cartlist.size();

//                                                    double price = CommonMethods.getOfferPrice(cartItem.Qty, cartItem.product);
//                                                    Log.d("product_price", "price: " + price);
//                                                    CommonVariables.priceTotalCart += price;
                                                    invalidateOptionsMenu();
                                                    if(loadCart){
                                                        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                                                        BaseFragment baseFragment = (BaseFragment) navHostFragment.getChildFragmentManager().getFragments().get(0);
                                                        baseFragment.showCartItems();
                                                    }
                                                    else {
                                                        Toast.makeText(LandingPage.this, "Product added to cart", Toast.LENGTH_SHORT).show();
                                                    }
                                                    progressBar.setVisibility(View.GONE);
                                                }
                                                if(pagingAdapter != null){
                                                    pagingAdapter.notifyDataSetChanged();
                                                }

                                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


                                            }
                                        });
                                    } else {
                                        addNewProductToCart(pagingAdapter, loadCart);
                                    }
                                } else {
                                    addNewProductToCart(pagingAdapter, loadCart);
                                }
                            }
                        } else {
                            addNewProductToCart(pagingAdapter, loadCart);
                        }
                    } else {
                        addNewProductToCart(pagingAdapter, loadCart);
                    }
                }
            }
        });
    }


    private void addNewProductToCart(FirestorePagingAdapter pagingAdapter, boolean loadCart) {
        Log.d("load_cart", "tru");
        Cart obCart = new Cart();
        obCart.product = CommonVariables.selectedCart.product;
        obCart.Qty = CommonVariables.selectedCart.Qty;
        obCart.Variants = CommonVariables.selectedCart.Variants;
        UUID cartId = UUID.randomUUID();
        obCart.CartId = cartId.toString();
        if(obCart.product != null){
            if (obCart.product.VariantsAvailable) {
                if (obCart.product.selectedVariants == null || obCart.product.selectedVariants.isEmpty()) {
                    Toast.makeText(this, "Product not added, Please try again", Toast.LENGTH_SHORT).show();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    progressBar.setVisibility(View.GONE);
                    if(pagingAdapter != null){
                        pagingAdapter.notifyDataSetChanged();
                    }
                    return;
                } else {
                    String key = obCart.product.selectedVariants.get(obCart.product.variant_pricing_attribute);
                    if (!obCart.product.variant_price_map.containsKey(key.trim())) {
                        Toast.makeText(this, "Product not added, Please try again", Toast.LENGTH_SHORT).show();
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        progressBar.setVisibility(View.GONE);
                        if(pagingAdapter != null){
                            pagingAdapter.notifyDataSetChanged();
                        }
                        return;
                    }

                }
            }

        }


        CommonVariables.cartlist.add((obCart));



        if(obCart.product.buy_by_weight)
            CommonVariables.priceTotalCart += obCart.product.weight_in_gram * obCart.product.price_per_gram;
        else
        CommonVariables.priceTotalCart += CommonMethods.getOfferPrice(obCart.Qty, obCart.product) * obCart.Qty;

        CollectionReference cartCollection = db.collection("users").document(CommonVariables.m_sFirebaseUserId).collection("cart");
        Map<String, Object> data1 = new HashMap<>();
        data1.put("Product_id", obCart.product.Product_Id);
        data1.put("CartId", obCart.CartId);
        data1.put("Qty", obCart.Qty);
        data1.put("is_city_product", obCart.product.is_city_product);
        data1.put("buy_by_weight", obCart.product.buy_by_weight);
        data1.put("weight_in_gram", obCart.product.weight_in_gram);
        Log.d("offer_price_weight", "buy" +obCart.product.buy_by_weight);

        if (obCart.product.selectedVariants.size() > 0) {
            data1.put("Variants", obCart.product.selectedVariants);
            obCart.Variants = obCart.product.selectedVariants;
        }
        cartCollection.document(obCart.CartId).set(data1).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    if(loadCart){
                        Log.d("load_cart", "loading");
                        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                        BaseFragment baseFragment = (BaseFragment) navHostFragment.getChildFragmentManager().getFragments().get(0);
                        baseFragment.showCartItems();
                    }
                    else {
                        Toast.makeText(LandingPage.this, "Product added to cart", Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                }
                if(pagingAdapter != null){
                    pagingAdapter.notifyDataSetChanged();
                }
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
        count = CommonVariables.cartlist.size();
        totalPriceCart = CommonVariables.priceTotalCart;
        invalidateOptionsMenu();
    }

    private void showPopup() {
        Intent intent = new Intent(LandingPage.this, Popup.class);
        startActivity(intent);
    }

    private void showVersionPopup() {
        Intent intent = new Intent(LandingPage.this, PopupVersion.class);
        startActivity(intent);
    }


    private void checkVersion() {
        getAppVersion();

        DocumentReference docRef = db.collection("AppInfo").document("AppInfo");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        AppInfo appInfo = document.toObject(AppInfo.class);
                        CommonVariables.points_to_credit_per_rupee = appInfo.points_to_credit_per_rupee;
                        CommonVariables.slot_timings = appInfo.slot_timings;
                        CommonVariables.availability = appInfo.availability;
                        CommonVariables.deliveryCharges = appInfo.delivery_charges;
                        CommonVariables.maxWeight = appInfo.max_weight;

                        if (appInfo.block_apk) {
                            if (appInfo.apk_version.equals(mVersion)) {
                                startActivity(new Intent(getApplicationContext(), BlockApkActivity.class));
                                finish();
                            }
                        }

                        if (appInfo.force_update) {
                            String version = appInfo.version;
                            if (!version.equals(mVersion)) {
                                showVersionPopup();
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

    public void getAppVersion() {
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            mVersion = pInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        initiateCart();
    }

    private void deleteCart() {

        db.collection("users").document(CommonVariables.m_sFirebaseUserId).collection("cart")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String id = document.getId();

                                db.collection("users").document(CommonVariables.m_sFirebaseUserId).collection("cart").document(id)
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("TAG", "DocumentSnapshot successfully deleted!");
                                               // startActivity(new Intent(LandingPage.this, OrderPlacedActivity.class));

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("TAG", "Error deleting document", e);
                                               // startActivity(new Intent(LandingPage.this, OrderPlacedActivity.class));
                                            }
                                        });

                                Log.d("cartid", id);
                                //  Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void setPoints() {

        DocumentReference docRef = db.collection("users").document(CommonVariables.loggedInUserDetails.customer_id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        CommonVariables.loggedInUserDetails = document.toObject(User.class);

                        TextView nav_wallet = (TextView) hView.findViewById(R.id.txtWalletMoney);

                        if (CommonVariables.loggedInUserDetails != null) {
                            String sTemp = String.format("%.0f", CommonVariables.loggedInUserDetails.points);
                            String sWalletMoney = "Wallet Balance: " + CommonVariables.rupeeSymbol + sTemp;
                            nav_wallet.setText(sWalletMoney);
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

    void openWhatsappContact(String txtToSend) {
        PackageManager packageManager = hView.getContext().getPackageManager();
        Intent i = new Intent(Intent.ACTION_VIEW);

        try {
            String phone = "919953783009";
            String message = txtToSend;
            String url = "https://api.whatsapp.com/send?phone=" + phone + "&text=" + URLEncoder.encode(message, "UTF-8");
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
            if (i.resolveActivity(packageManager) != null) {
                hView.getContext().startActivity(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getAndUpdateFCMTokenForGuests() {
        if (CommonVariables.loggedInUserDetails != null) {
            return;
        }
        //Get Firebase FCM token
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String fcmToken = instanceIdResult.getToken();

                DocumentReference washingtonRef = db.collection("guests").document(fcmToken);

// Set the "isCapital" field of the city 'DC'
                washingtonRef
                        .update("fcm", fcmToken)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Log.d(TAG, "DocumentSnapshot successfully updated!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Log.w(TAG, "Error updating document", e);
                            }
                        });

            }
        });
    }

    public void openWhatsApp() {
        Log.d("referal_code", CommonVariables.loggedInUserDetails.referral_code);
        PackageManager pm = getPackageManager();
        try {
            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");
            String text = "Hi! Please Install the Doorish App from the URL - https://bit.ly/3CqL1HO and get INR 20 in your wallet.\nPlease use referral code :" + CommonVariables.loggedInUserDetails.referral_code;  // Replace with your own message.

            PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            //Check if package exists or not. If not then code
            //in catch block will be called
            waIntent.setPackage("com.whatsapp");
            waIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(waIntent, "Share with"));

        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                    .show();
        } catch (Exception e) {
            // e.printStacktrace();
        }

    }


}

