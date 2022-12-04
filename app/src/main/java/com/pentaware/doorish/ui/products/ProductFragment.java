package com.pentaware.doorish.ui.products;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.core.ArrayContainsFilter;
import com.pentaware.ISortFilterOperations;
import com.pentaware.doorish.AdapterFilterProducts;
import com.pentaware.doorish.AdapterSortProducts;
import com.pentaware.doorish.Enums;
import com.pentaware.doorish.LandingPage;
import com.pentaware.doorish.Popup;
import com.pentaware.doorish.BaseFragment;
import com.pentaware.doorish.CommonVariables;
import com.pentaware.doorish.ProductAdapterClickEvent;
import com.pentaware.doorish.ProductListing;
import com.pentaware.doorish.ProductViewHolder;
import com.pentaware.doorish.R;
import com.pentaware.doorish.model.Product;
import com.pentaware.doorish.model.Seller;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import pl.droidsonroids.gif.GifImageView;

public class ProductFragment extends BaseFragment implements ProductAdapterClickEvent, AdapterView.OnItemSelectedListener, ISortFilterOperations {


    DocumentSnapshot mLastVisible = null;
    String sCategory = "";
    String mSubCategory = "";
    boolean m_bSearchByTag = false;
    boolean m_bSearchByBrand = false;
    String TAG_NAME = "";
    String BRAND_NAME = "";

    boolean m_bSearchLocal = false;
    boolean m_bshowFromShop = false;
    boolean m_bSearchBySubCategory = false;
    boolean m_bWishlist = false;
    String mSellerId;
    private ProductViewModel mViewModel;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    TextView txtNoItemToDisplay;

    private List<ProductListing> listItems;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private Spinner spinner;
    private static final String[] paths = {"Sort By:", "Price (Low -> High)", "Price (High -> Low)", "Brand"};

    //private Button btnNext, btnPrevious;
    private int numberOfProducts;

    private int currentSellerIndex = 0;

    GifImageView gifView;

    private View mView;

    int position = 0;

    private ProgressBar progressBarRecycler;
    public ProgressBar progressBarCart;
    private TextView txtErrorRecycler;
    private Button btnRetryRecycler;

    private RecyclerView recyclerViewSortProducts;
    private RecyclerView recyclerViewFilterProducts;

    private AdapterSortProducts adapterSortProducts;
    private AdapterFilterProducts adapterFilterProducts;

    public FirestorePagingAdapter pagingAdapter;

    List<String> sortByList = new ArrayList<>();
    List<String> filterByList = new ArrayList<>();

    private String filterSelected = null;
    private String sortSelected = null;

    ProductAdapterClickEvent event;

    private long mLastClickTime = System.currentTimeMillis();
    private static final long CLICK_TIME_INTERVAL = 1000;

    List<Query> queryList = new ArrayList<>();
    int pageIndex = 0;
    Enums.ProductSorting activeProductSorting = Enums.ProductSorting.None;

    public static ProductFragment newInstance() {
        return new ProductFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.product_fragment, container, false);
        event = this;
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ProductViewModel.class);
        // TODO: Use the ViewModel

        spinner = (Spinner) mView.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mView.getContext(),
                android.R.layout.simple_spinner_item, paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        Button btnSortProducts = mView.findViewById(R.id.btn_sort_products);
        Button btnFilterProducts = mView.findViewById(R.id.btn_filter_products);

        progressBarCart = mView.findViewById(R.id.progress_bar_add_to_cart);

        recyclerViewSortProducts = mView.findViewById(R.id.recycler_view_sort_products);
        recyclerViewFilterProducts = mView.findViewById(R.id.recycler_view_filter_products);

        txtNoItemToDisplay = (TextView)mView.findViewById(R.id.noItemTodisplay);

        gifView = (GifImageView) mView.findViewById(R.id.gifView);
        progressBarRecycler = mView.findViewById(R.id.progress_bar_recycler);
        txtErrorRecycler = mView.findViewById(R.id.txt_error_recycler);
        btnRetryRecycler = mView.findViewById(R.id.btn_retry_recycler);
        initSortAndFilter();

        btnSortProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnFilterProducts.setBackgroundResource(R.drawable.white_btn_bg);
                btnFilterProducts.setTextColor(getResources().getColor(R.color.black));
                btnFilterProducts.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_filter, 0, 0, 0);

                btnSortProducts.setBackgroundResource(R.drawable.blue_btn_bg);
                btnSortProducts.setTextColor(getResources().getColor(R.color.white));
                btnSortProducts.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sort_white, 0, 0, 0);
                recyclerViewSortProducts.setVisibility(View.VISIBLE);
                recyclerViewFilterProducts.setVisibility(View.GONE);
            }
        });


        btnFilterProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSortProducts.setBackgroundResource(R.drawable.white_btn_bg);
                btnSortProducts.setTextColor(getResources().getColor(R.color.black));
                btnSortProducts.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sort, 0, 0, 0);

                btnFilterProducts.setBackgroundResource(R.drawable.blue_btn_bg);
                btnFilterProducts.setTextColor(getResources().getColor(R.color.white));
                btnFilterProducts.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_filter_white, 0, 0, 0);
                recyclerViewFilterProducts.setVisibility(View.VISIBLE);
                recyclerViewSortProducts.setVisibility(View.GONE);
            }
        });

        btnRetryRecycler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reloadRecyclerOnError();
            }
        });

        LoadProducts();
    }

    private void initSortAndFilter() {
        sortByList.clear();
        filterByList.clear();
        recyclerViewSortProducts.setHasFixedSize(true);
        recyclerViewFilterProducts.setHasFixedSize(true);

        recyclerViewSortProducts.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        recyclerViewFilterProducts.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

        Collections.addAll(sortByList, "Sort by Name (A-Z)", "Price: Low to High", "Price: High to Low");
        Collections.addAll(filterByList, "All", "Vegetables", "Fruits", "Groceries", "Sweets and Savouries", "Let's Party");

        adapterSortProducts = new AdapterSortProducts(sortByList, getContext(), ProductFragment.this);
        recyclerViewSortProducts.setAdapter(adapterSortProducts);

        adapterFilterProducts = new AdapterFilterProducts(filterByList, getContext(), ProductFragment.this);
        recyclerViewFilterProducts.setAdapter(adapterFilterProducts);
    }

    private void LoadProducts() {

        if (getArguments() != null) {

            // The getPrivacyPolicyLink() method will be created automatically.
            sCategory = ProductFragmentArgs.fromBundle(getArguments()).getCategory();

            String searchLocal = ProductFragmentArgs.fromBundle(getArguments()).getSearchlocal();
            if (searchLocal == "true") {
                m_bSearchLocal = true;
            } else {
                m_bSearchLocal = false;
            }

            String[] arrTag = sCategory.split("\\|");
            if (arrTag.length == 2) {
                if (arrTag[0].toUpperCase().equals("TAG")) {
                    m_bSearchByTag = true;
                    TAG_NAME = arrTag[1];
                }

                if (arrTag[0].toUpperCase().equals("BRAND")) {
                    m_bSearchByBrand = true;
                    BRAND_NAME = arrTag[1];
                }


                if (arrTag[0].toUpperCase().equals("SELLER")) {
                    m_bshowFromShop = true;
                    mSellerId = arrTag[1];
                }


            }

            if (arrTag[0].equals("subcategory")) {
                sCategory = arrTag[1];
                mSubCategory = arrTag[2];

                Log.d("selected_value", sCategory + " " + mSubCategory);

                if(!mSubCategory.isEmpty()){
                    if(mSubCategory.equals("All"))
                        filterSelected = sCategory;
                    else filterSelected = mSubCategory;
                    if(mSubCategory.equals("Fruites")){
                        filterSelected = "Fruits";
                    }
                }
                else {
                    filterSelected = sCategory;
                }

//                if(sCategory.equals("Vegetables and Fruites")){
//                    if(mSubCategory.equals("Vegetables")){
//                        filterSelected = "Vegetables";
//                    }
//                    else if(mSubCategory.equals("Fruites")) {
//                        filterSelected = "Fruites";
//                    }
//                }
//
//                if(sCategory.equals("Groceries")){
//                    filterSelected = "Groceries";
//                }
//
//                if(sCategory.equals("Sweets and Savouries")){
//                    filterSelected = "Sweets and Savouries";
//                }
//
//                if(sCategory.equals("Let's Party")){
//                    filterSelected = "Let's Party";
//                }

                    if (arrTag[3].toUpperCase().trim().equals("TRUE")) {
                    m_bSearchLocal = true;
                }

                if (mSubCategory.toUpperCase().equals("ALL")) {
                    m_bSearchBySubCategory = false;
                } else {
                    m_bSearchBySubCategory = true;
                }
            }

            if (arrTag[0].equals("wishlist")) {
                m_bWishlist = true;
                RelativeLayout layoutSpinner = (RelativeLayout) mView.findViewById(R.id.layoutSpinner);
                layoutSpinner.setVisibility(View.GONE);

            }
        }


        listItems = new ArrayList<>();
        recyclerView = (RecyclerView) mView.findViewById(R.id.productRecycler);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(mView.getContext()));
        // runLayoutAnimation(recyclerView);

        Query query;

        if (m_bWishlist) {

            if (CommonVariables.m_sFirebaseUserId == null) {
                showPopup();
                return;
            }

            query = db.collection("products")
                    .whereEqualTo("Active", true)
                    .whereEqualTo("status", "approved")
                    .whereArrayContains("wishlist_customers", CommonVariables.m_sFirebaseUserId);
            queryList.add(query);
            LoadAllProducts(query);
        } else if (m_bSearchLocal && m_bSearchBySubCategory == false) {

            if (activeProductSorting == Enums.ProductSorting.PriceLowToHigh) {

                if (m_bSearchByBrand) {
                    query = db.collection("products")
                            .whereEqualTo("Active", true)
                            .whereEqualTo("status", "approved")
                            .whereEqualTo("selling_offline", true)
                            .whereEqualTo("seller_area_pin", CommonVariables.deliveryAddress.buyer_area_pin)
                            .whereEqualTo("Brand", BRAND_NAME)
                            .orderBy("Offer_Price", Query.Direction.ASCENDING);
                } else {
                    query = db.collection("products")
                            .whereEqualTo("Active", true)
                            .whereEqualTo("status", "approved")
                            .whereEqualTo("selling_offline", true)
                            .whereEqualTo("seller_area_pin", CommonVariables.deliveryAddress.buyer_area_pin)
                            .whereArrayContains("Tags", TAG_NAME)
                            .orderBy("Offer_Price", Query.Direction.ASCENDING);
                }

            } else if (activeProductSorting == Enums.ProductSorting.PriceHighToLow) {

                if (m_bSearchByBrand) {
                    query = db.collection("products")
                            .whereEqualTo("Active", true)
                            .whereEqualTo("status", "approved")
                            .whereEqualTo("selling_offline", true)
                            .whereEqualTo("seller_area_pin", CommonVariables.deliveryAddress.buyer_area_pin)
                            .whereEqualTo("Brand", BRAND_NAME)
                            .orderBy("Offer_Price", Query.Direction.DESCENDING);
                } else {
                    query = db.collection("products")
                            .whereEqualTo("Active", true)
                            .whereEqualTo("status", "approved")
                            .whereEqualTo("selling_offline", true)
                            .whereEqualTo("seller_area_pin", CommonVariables.deliveryAddress.buyer_area_pin)
                            .whereArrayContains("Tags", TAG_NAME)
                            .orderBy("Offer_Price", Query.Direction.DESCENDING);
                }

            } else if (activeProductSorting == Enums.ProductSorting.Brand) {

                if (m_bSearchByBrand) {
                    query = db.collection("products")
                            .whereEqualTo("Active", true)
                            .whereEqualTo("status", "approved")
                            .whereEqualTo("selling_offline", true)
                            .whereEqualTo("seller_area_pin", CommonVariables.deliveryAddress.buyer_area_pin)
                            .whereEqualTo("Brand", BRAND_NAME)
                            .orderBy("Brand");
                } else {
                    query = db.collection("products")
                            .whereEqualTo("Active", true)
                            .whereEqualTo("status", "approved")
                            .whereEqualTo("selling_offline", true)
                            .whereEqualTo("seller_area_pin", CommonVariables.deliveryAddress.buyer_area_pin)
                            .whereArrayContains("Tags", TAG_NAME)
                            .orderBy("Brand");
                }
            } else {

                if (m_bSearchByBrand) {
                    query = db.collection("products")
                            .whereEqualTo("Active", true)
                            .whereEqualTo("status", "approved")
                            .whereEqualTo("selling_offline", true)
                            .whereEqualTo("seller_area_pin", CommonVariables.deliveryAddress.buyer_area_pin)
                            .whereEqualTo("Brand", BRAND_NAME)
                            .orderBy("timestamp", Query.Direction.DESCENDING);
                } else {
                    query = db.collection("products")
                            .whereEqualTo("Active", true)
                            .whereEqualTo("status", "approved")
                            .whereEqualTo("selling_offline", true)
                            .whereEqualTo("seller_area_pin", CommonVariables.deliveryAddress.buyer_area_pin)
                            .whereArrayContains("Tags", TAG_NAME)
                            .orderBy("timestamp", Query.Direction.DESCENDING);
                }

            }


            queryList.add(query);

            LoadAllProducts(query);

        } else if (m_bSearchLocal && m_bSearchBySubCategory == true) {
            if (activeProductSorting == Enums.ProductSorting.PriceLowToHigh) {
                query = db.collection("products")
                        .whereEqualTo("Active", true)
                        .whereEqualTo("status", "approved")
                        .whereEqualTo("selling_offline", true)
                        .whereEqualTo("Category", sCategory)
                        .whereEqualTo("SubCategory", mSubCategory)
                        .whereEqualTo("seller_area_pin", CommonVariables.deliveryAddress.buyer_area_pin)
                        .orderBy("Offer_Price");


            } else if (activeProductSorting == Enums.ProductSorting.PriceHighToLow) {
                query = db.collection("products")
                        .whereEqualTo("Active", true)
                        .whereEqualTo("status", "approved")
                        .whereEqualTo("selling_offline", true)
                        .whereEqualTo("Category", sCategory)
                        .whereEqualTo("SubCategory", mSubCategory)
                        .whereEqualTo("seller_area_pin", CommonVariables.deliveryAddress.buyer_area_pin)
                        .orderBy("Offer_Price", Query.Direction.DESCENDING);


            } else if (activeProductSorting == Enums.ProductSorting.Brand) {
                query = db.collection("products")
                        .whereEqualTo("Active", true)
                        .whereEqualTo("status", "approved")
                        .whereEqualTo("selling_offline", true)
                        .whereEqualTo("Category", sCategory)
                        .whereEqualTo("SubCategory", mSubCategory)
                        .whereEqualTo("seller_area_pin", CommonVariables.deliveryAddress.buyer_area_pin)
                        .orderBy("Brand");


            } else {
                query = db.collection("products")
                        .whereEqualTo("Active", true)
                        .whereEqualTo("status", "approved")
                        .whereEqualTo("selling_offline", true)
                        .whereEqualTo("Category", sCategory)
                        .whereEqualTo("SubCategory", mSubCategory)
                        .whereEqualTo("seller_area_pin", CommonVariables.deliveryAddress.buyer_area_pin)
                        .orderBy("timestamp", Query.Direction.DESCENDING);
            }

            queryList.add(query);
            LoadAllProducts(query);


        } else if (m_bshowFromShop) {

            if (activeProductSorting == Enums.ProductSorting.PriceLowToHigh) {
                query = db.collection("products")
                        .whereEqualTo("Active", true)
                        .whereEqualTo("status", "approved")
                        .whereEqualTo("seller_id", mSellerId)
                        .whereEqualTo("selling_offline", true)
                        .orderBy("Offer_Price");
            } else if (activeProductSorting == Enums.ProductSorting.PriceHighToLow) {
                query = db.collection("products")
                        .whereEqualTo("Active", true)
                        .whereEqualTo("status", "approved")
                        .whereEqualTo("seller_id", mSellerId)
                        .whereEqualTo("selling_offline", true)
                        .orderBy("Offer_Price", Query.Direction.DESCENDING);
            } else if (activeProductSorting == Enums.ProductSorting.Brand) {
                query = db.collection("products")
                        .whereEqualTo("Active", true)
                        .whereEqualTo("status", "approved")
                        .whereEqualTo("seller_id", mSellerId)
                        .whereEqualTo("selling_offline", true)
                        .orderBy("Brand");
            } else {
                query = db.collection("products")
                        .whereEqualTo("Active", true)
                        .whereEqualTo("status", "approved")
                        .whereEqualTo("seller_id", mSellerId)
                        .whereEqualTo("selling_offline", true)
                        .orderBy("timestamp", Query.Direction.DESCENDING);
            }

            queryList.add(query);
            LoadAllProducts(query);

        } else if (m_bSearchByTag) {

            if (activeProductSorting == Enums.ProductSorting.PriceLowToHigh) {
                query = db.collection("products")
                        .whereEqualTo("Active", true)
                        .whereEqualTo("status", "approved")
                        .whereArrayContains("Tags", TAG_NAME)
                        .orderBy("Offer_Price");
            } else if (activeProductSorting == Enums.ProductSorting.PriceHighToLow) {
                query = db.collection("products")
                        .whereEqualTo("Active", true)
                        .whereEqualTo("status", "approved")
                        .whereArrayContains("Tags", TAG_NAME)
                        .orderBy("Offer_Price", Query.Direction.DESCENDING);
            } else if (activeProductSorting == Enums.ProductSorting.Brand) {
                query = db.collection("products")
                        .whereEqualTo("Active", true)
                        .whereEqualTo("status", "approved")
                        .whereArrayContains("Tags", TAG_NAME)
                        .orderBy("Brand");
            } else {
                query = db.collection("products")
                        .whereEqualTo("Active", true)
                        .whereEqualTo("status", "approved")
                        .whereArrayContains("Tags", TAG_NAME)
                        .orderBy("timestamp", Query.Direction.DESCENDING);
            }

            queryList.add(query);
            LoadAllProducts(query);

        } else if (m_bSearchByBrand) {

            if (activeProductSorting == Enums.ProductSorting.PriceLowToHigh) {
                query = db.collection("products")
                        .whereEqualTo("Active", true)
                        .whereEqualTo("status", "approved")
                        .whereEqualTo("Brand", BRAND_NAME)
                        .orderBy("Offer_Price");
            } else if (activeProductSorting == Enums.ProductSorting.PriceHighToLow) {
                query = db.collection("products")
                        .whereEqualTo("Active", true)
                        .whereEqualTo("status", "approved")
                        .whereEqualTo("Brand", BRAND_NAME)
                        .orderBy("Offer_Price", Query.Direction.DESCENDING);
            } else if (activeProductSorting == Enums.ProductSorting.Brand) {
                query = db.collection("products")
                        .whereEqualTo("Active", true)
                        .whereEqualTo("status", "approved")
                        .whereEqualTo("Brand", BRAND_NAME)
                        .orderBy("Brand");
            } else {
                query = db.collection("products")
                        .whereEqualTo("Active", true)
                        .whereEqualTo("status", "approved")
                        .whereEqualTo("Brand", BRAND_NAME)
                        .orderBy("timestamp", Query.Direction.DESCENDING);
            }

            queryList.add(query);
            LoadAllProducts(query);

        } else if (m_bSearchBySubCategory) {

            if (activeProductSorting == Enums.ProductSorting.PriceLowToHigh) {
                query = db.collection("products")
                        .whereEqualTo("Active", true)
                        .whereEqualTo("status", "approved")
                        .whereEqualTo("Category", sCategory)
                        .whereEqualTo("SubCategory", mSubCategory)
                        .orderBy("Offer_Price");
            } else if (activeProductSorting == Enums.ProductSorting.PriceHighToLow) {
                query = db.collection("products")
                        .whereEqualTo("Active", true)
                        .whereEqualTo("status", "approved")
                        .whereEqualTo("Category", sCategory)
                        .whereEqualTo("SubCategory", mSubCategory)
                        .orderBy("Offer_Price", Query.Direction.DESCENDING);
            } else if (activeProductSorting == Enums.ProductSorting.Brand) {
                query = db.collection("products")
                        .whereEqualTo("Active", true)
                        .whereEqualTo("status", "approved")
                        .whereEqualTo("Category", sCategory)
                        .whereEqualTo("SubCategory", mSubCategory)
                        .orderBy("Brand");
            } else {
                query = db.collection("products")
                        .whereEqualTo("Active", true)
                        .whereEqualTo("status", "approved")
                        .whereEqualTo("Category", sCategory)
                        .whereEqualTo("SubCategory", mSubCategory)
                        .orderBy("timestamp", Query.Direction.DESCENDING);
            }


            queryList.add(query);

            LoadAllProducts(query);


        } else {
            if (sCategory.toUpperCase().equals("ALL")) {
                if (activeProductSorting == Enums.ProductSorting.PriceLowToHigh) {
                    query = db.collection("products")
                            .whereEqualTo("Active", true)
                            .whereEqualTo("status", "approved")
                            .orderBy("Offer_Price");
                } else if (activeProductSorting == Enums.ProductSorting.PriceHighToLow) {
                    query = db.collection("products")
                            .whereEqualTo("Active", true)
                            .whereEqualTo("status", "approved")
                            .orderBy("Offer_Price", Query.Direction.DESCENDING);
                } else if (activeProductSorting == Enums.ProductSorting.Brand) {
                    query = db.collection("products")
                            .whereEqualTo("Active", true)
                            .whereEqualTo("status", "approved")
                            .orderBy("Brand");
                } else {
                    query = db.collection("products")
                            .whereEqualTo("Active", true)
                            .whereEqualTo("status", "approved")
                            .orderBy("timestamp", Query.Direction.DESCENDING);
                }


                queryList.add(query);

                LoadAllProducts(query);
            } else {
                if (activeProductSorting == Enums.ProductSorting.PriceLowToHigh) {
                    query = db.collection("products")
                            .whereEqualTo("Active", true)
                            .whereEqualTo("status", "approved")
                            .whereEqualTo("Category", sCategory)
                            .orderBy("Offer_Price");
                } else if (activeProductSorting == Enums.ProductSorting.PriceHighToLow) {
                    query = db.collection("products")
                            .whereEqualTo("Active", true)
                            .whereEqualTo("status", "approved")
                            .whereEqualTo("Category", sCategory)
                            .orderBy("Offer_Price", Query.Direction.DESCENDING);
                } else if (activeProductSorting == Enums.ProductSorting.Brand) {
                    query = db.collection("products")
                            .whereEqualTo("Active", true)
                            .whereEqualTo("status", "approved")
                            .whereEqualTo("Category", sCategory)
                            .orderBy("Brand");
                } else {
                    query = db.collection("products")
                            .whereEqualTo("Active", true)
                            .whereEqualTo("status", "approved")
                            .whereEqualTo("Category", sCategory)
                            .orderBy("timestamp", Query.Direction.DESCENDING);
                }


                queryList.add(query);
                LoadAllProducts(query);
            }

        }
    }

    private void LoadAllProducts(Query query) {

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(2)
                .setPageSize(7)
                .build();

        FirestorePagingOptions options = new FirestorePagingOptions.Builder<Product>()
                .setLifecycleOwner(this)
                .setQuery(query, config, Product.class)
                .build();


        pagingAdapter = new FirestorePagingAdapter<Product, ProductViewHolder>(options) {

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_product_listing, parent, false);
                return new ProductViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int i, @NonNull Product product) {
                holder.bind(product, getContext(), ProductFragment.this, m_bSearchLocal, m_bshowFromShop);

                position = i;

                if (m_bSearchLocal) {
                    holder.layoutShop.setVisibility(View.VISIBLE);
                    holder.txtShopPrice.setVisibility(View.VISIBLE);

                } else if (m_bshowFromShop) {
                    holder.txtShopPrice.setVisibility(View.VISIBLE);
                    holder.layoutShop.setVisibility(View.GONE);
                } else {
                    holder.layoutShop.setVisibility(View.GONE);
                    holder.txtShopPrice.setVisibility(View.GONE);

                }
            }

            @Override
            protected void onLoadingStateChanged(@NonNull LoadingState state) {
                switch (state) {
                    case LOADING_INITIAL:
                        Log.d("value_here", "initial loading");
                        gifView.setVisibility(View.VISIBLE);
                        txtNoItemToDisplay.setVisibility(View.GONE);
                        break;

                    case LOADING_MORE:
                        Log.d("value_here", "loading more");
                        progressBarRecycler.setVisibility(View.VISIBLE);
                        txtNoItemToDisplay.setVisibility(View.GONE);
                        break;

                    case LOADED:
                        Log.d("value_here", "loaded");
                        gifView.setVisibility(View.GONE);
                        progressBarRecycler.setVisibility(View.GONE);
                        txtErrorRecycler.setVisibility(View.GONE);
                        btnRetryRecycler.setVisibility(View.GONE);
                        txtNoItemToDisplay.setVisibility(View.GONE);
                        break;

                    case ERROR:
                        Log.d("value_here", "error occurred ");
                        txtErrorRecycler.setVisibility(View.VISIBLE);
                        btnRetryRecycler.setVisibility(View.VISIBLE);
                        break;

                    case FINISHED:
                        Log.d("value_here", "finished loading ");
                        if(getItemCount() == 0){
                            txtNoItemToDisplay.setVisibility(View.VISIBLE);
                            gifView.setVisibility(View.GONE);
                        }
                        else {
                            txtNoItemToDisplay.setVisibility(View.GONE);
                        }
                        progressBarRecycler.setVisibility(View.GONE);
                        recyclerView.setPadding(0, 0, 0, 0);
                        recyclerView.scrollToPosition(3);
                        break;
                }
            }


            @Override
            protected void onError(@NonNull Exception e) {
                super.onError(e);
                Log.d("value_here", "onError: " + e.toString());
            }
        };

        recyclerView.setAdapter(pagingAdapter);
        recyclerView.setVisibility(View.VISIBLE);
        gifView.setVisibility(View.GONE);


//        currentSellerIndex = 0;
//        listItems.clear();
//        final TextView txtNoItemToDisplay = (TextView)mView.findViewById(R.id.noItemTodisplay);
//                query
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
//                {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task)
//                    {
//                        if (task.isSuccessful())
//                        {
//
//                            QuerySnapshot documentSnapshots = task.getResult();
//                            numberOfProducts = documentSnapshots.size();
//                            if(numberOfProducts == 0){
//                                txtNoItemToDisplay.setVisibility(View.VISIBLE);
//                                gifView.setVisibility(View.GONE);
//                                //Toast.makeText(mView.getContext(), "No more products to show", Toast.LENGTH_SHORT).show();
//                              //  btnNext.setVisibility(View.GONE);
//                                adapter = new AdapterProductListing(listItems, mView.getContext(),  event, m_bSearchLocal, m_bshowFromShop, pageIndex);
//                                recyclerView.setAdapter(adapter);
//                                gifView.setVisibility(View.GONE);
//                                return;
//                            }
//                            mLastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size()-1);
//                            if(documentSnapshots.size() < CommonVariables.PAGE_SIZE){
//                              //  btnNext.setVisibility(View.GONE);
//                            }
//
//
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//
//                                Product product = document.toObject(Product.class);
//
//                                ProductListing productListing = new ProductListing(product);
//                                listItems.add(productListing);
//                                getSellerDetails(product);
//
//
////                                if(m_bSearchLocal){
////                                    getSellerDetails(product);
////                                }
//
//
//                                // Log.d("TAG", "getting documents: ", task.getException());
//                            }
//                            if(listItems.size() ==0){
//                                txtNoItemToDisplay.setVisibility(View.VISIBLE);
//                            }else{
////
////                                Collections.sort(listItems, new Comparator<ProductListing>() {
////
////                                    @Override
////                                    public int compare(ProductListing o1, ProductListing o2) {
////                                        return Double.compare(o1.getProduct().Offer_Price, o2.getProduct().Offer_Price);
////                                    }
////
////                                });
//
//                                txtNoItemToDisplay.setVisibility(View.GONE);
//                            }
//
//                            if(!m_bSearchLocal) {
//
//                                adapter = new AdapterProductListing(listItems, mView.getContext(), event, m_bSearchLocal, m_bshowFromShop, pageIndex);
//                                recyclerView.setAdapter(adapter);
//                                gifView.setVisibility(View.GONE);
//                            }
//
//                        }
//                        else {
//                            Log.d("TAG", "Error getting documents: ", task.getException());
//                        }
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(mView.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
    }

    private void reloadRecyclerOnError() {
        pagingAdapter.retry();
    }

    private void getSellerDetails(final Product product) {
        final DocumentReference docRef = db.collection("seller").document(product.seller_id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        Seller seller = document.toObject(Seller.class);
                        currentSellerIndex++;
                        product.seller = seller;

                        startProductFragment(product);
//                        if(currentSellerIndex == numberOfProducts && m_bSearchLocal == true){
//                            adapter = new AdapterProductListing(listItems, mView.getContext(), event, m_bSearchLocal, m_bshowFromShop, pageIndex);
//                            recyclerView.setAdapter(adapter);
//                            gifView.setVisibility(View.GONE);
//                        }

                        //  Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        //   Log.d(TAG, "No such document");
                    }
                } else {
                    //  Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }


    @Override
    public void onProductClick(Product product) {
        long now = System.currentTimeMillis();
        if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
            return;
        }
        mLastClickTime = now;
        getSellerDetails(product);
    }

    private void startProductFragment(Product product) {
        Fragment newFragment = ProductDetailFragment.newInstance(product);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();


        transaction.hide(this);
        transaction.add(R.id.nav_host_fragment, newFragment);

        //transaction.replace(R.id.nav_host_fragment, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        // ProductFragmentDirections.ActionNavGalleryToProductDetailFragment actionNavGalleryToProductDetailFragment = ProductFragmentDirections.actionNavGalleryToProductDetailFragment(product);
        // Navigation.findNavController(mView).navigate(actionNavGalleryToProductDetailFragment);
    }

    @Override
    public void onNextClick() {
        // LoadNextProducts();
    }

    @Override
    public void onPreviousClick() {
//        LoadPreviousProducts();
    }

    @Override
    public void onHeartClick(Product product) {
        if (product.is_favourite) {
            AddToWishList(product, true);
        } else {
            AddToWishList(product, false);
        }
    }

    @Override
    public void onAddToCartClick(Product product, View view) {
        if(getActivity() != null){
            ((LandingPage) getActivity()).onAddToCart_Click(view, pagingAdapter, false);
        }

    }

    @Override
    public void onBuyWeightClick(Product product) {

        Fragment newFragment = BuyWeightFragment.newInstance(product);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();


        transaction.hide(this);
        transaction.add(R.id.nav_host_fragment, newFragment);

        //transaction.replace(R.id.nav_host_fragment, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        switch (position) {
            case 0:
                if (activeProductSorting != Enums.ProductSorting.None) {
                    activeProductSorting = Enums.ProductSorting.None;
                    LoadProducts();
                } else {
                    activeProductSorting = Enums.ProductSorting.None;
                }
                //Toast.makeText(mView.getContext(), Integer.toString(position), Toast.LENGTH_SHORT).show();
                break;
            case 1:
                activeProductSorting = Enums.ProductSorting.PriceLowToHigh;
                LoadProducts();
                // Whatever you want to happen when the second item gets selected
                break;

            case 2:
                // Whatever you want to happen when the thrid item gets selected
                activeProductSorting = Enums.ProductSorting.PriceHighToLow;
                LoadProducts();
                break;

            case 3:
                // Whatever you want to happen when the thrid item gets selected
                activeProductSorting = Enums.ProductSorting.Brand;
                LoadProducts();
                break;
        }
        //     listItems = new ArrayList<>();
        //    LoadProducts();
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void AddToWishList(Product product, boolean bAdd) {

        if (bAdd) {
            if (!product.wishlist_customers.contains(CommonVariables.m_sFirebaseUserId)) {
                product.wishlist_customers.add(CommonVariables.m_sFirebaseUserId);
            }
        } else {
            if (product.wishlist_customers.contains(CommonVariables.m_sFirebaseUserId)) {
                product.wishlist_customers.remove(CommonVariables.m_sFirebaseUserId);
            }
        }
        db.collection("products").document(product.Product_Id)
                .update("wishlist_customers", product.wishlist_customers)
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

    private void showPopup() {
        Intent intent = new Intent(getActivity(), Popup.class);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (CommonVariables.m_sFirebaseUserId != null) {
            pagingAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (CommonVariables.m_sFirebaseUserId != null) {
            pagingAdapter.stopListening();
        }
    }

    @Override
    public void onSortSelected(String sortType) {

        sortSelected = sortType;
        Query sortQuery;

        switch (sortType) {
            case "Sort by Name (A-Z)":
                if (filterSelected != null && !filterSelected.equals("All")) {
                    switch (filterSelected) {
                        case "Fruits":
                            sortQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .whereEqualTo("Category", "Vegetables and Fruites")
                                    .whereEqualTo("SubCategory", "Fruites")
                                    .orderBy("Title", Query.Direction.ASCENDING);
                            break;
                        case "Vegetables":
                            sortQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .whereEqualTo("Category", "Vegetables and Fruites")
                                    .whereEqualTo("SubCategory", "Vegetables")
                                    .orderBy("Title", Query.Direction.ASCENDING);
                            break;
                        case "Groceries":
                            sortQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .whereEqualTo("Category", "Groceries")
                                    .orderBy("Title", Query.Direction.ASCENDING);
                            break;
                        case "Sweets and Savouries":
                            sortQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .whereEqualTo("Category", "Sweets and Savouries")
                                    .orderBy("Title", Query.Direction.ASCENDING);
                            break;
                        case "Let's Party":
                            sortQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .whereEqualTo("Category", "Let's Party")
                                    .orderBy("Title", Query.Direction.ASCENDING);
                            break;
                        case "Vegetables and Fruites":
                            sortQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .whereEqualTo("Category", "Vegetables and Fruites")
                                    .orderBy("Title", Query.Direction.ASCENDING);
                            break;
                        default:
                            sortQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .orderBy("timestamp", Query.Direction.DESCENDING);
                            break;
                    }

                } else {
                    sortQuery = db.collection("products")
                            .whereEqualTo("Active", true)
                            .whereEqualTo("status", "approved")
                            .orderBy("Title", Query.Direction.ASCENDING);
                }

                break;
            case "Price: Low to High":
                if (filterSelected != null && !filterSelected.equals("All")) {
                    switch (filterSelected) {
                        case "Fruits":
                            sortQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .whereEqualTo("Category", "Vegetables and Fruites")
                                    .whereEqualTo("SubCategory", "Fruites")
                                    .orderBy("Offer_Price", Query.Direction.ASCENDING);
                            break;
                        case "Vegetables":
                            sortQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .whereEqualTo("Category", "Vegetables and Fruites")
                                    .whereEqualTo("SubCategory", "Vegetables")
                                    .orderBy("Offer_Price", Query.Direction.ASCENDING);
                            break;
                        case "Groceries":
                            sortQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .whereEqualTo("Category", "Groceries")
                                    .orderBy("Offer_Price", Query.Direction.ASCENDING);
                            break;
                        case "Sweets and Savouries":
                            sortQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .whereEqualTo("Category", "Sweets and Savouries")
                                    .orderBy("Offer_Price", Query.Direction.ASCENDING);
                            break;
                        case "Let's Party":
                            sortQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .whereEqualTo("Category", "Let's Party")
                                    .orderBy("Offer_Price", Query.Direction.ASCENDING);
                            break;
                        case "Vegetables and Fruites":
                            sortQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .whereEqualTo("Category", "Vegetables and Fruites")
                                    .orderBy("Offer_Price", Query.Direction.ASCENDING);
                            break;
                        default:
                            sortQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .orderBy("timestamp", Query.Direction.DESCENDING);
                            break;
                    }

                } else {
                    sortQuery = db.collection("products")
                            .whereEqualTo("Active", true)
                            .whereEqualTo("status", "approved")
                            .orderBy("Offer_Price", Query.Direction.ASCENDING);
                }

                break;
            case "Price: High to Low":
                if (filterSelected != null && !filterSelected.equals("All")) {
                    switch (filterSelected) {
                        case "Fruits":
                            sortQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .whereEqualTo("Category", "Vegetables and Fruites")
                                    .whereEqualTo("SubCategory", "Fruites")
                                    .orderBy("Offer_Price", Query.Direction.DESCENDING);
                            break;
                        case "Vegetables":
                            sortQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .whereEqualTo("Category", "Vegetables and Fruites")
                                    .whereEqualTo("SubCategory", "Vegetables")
                                    .orderBy("Offer_Price", Query.Direction.DESCENDING);
                            break;
                        case "Groceries":
                            sortQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .whereEqualTo("Category", "Groceries")
                                    .orderBy("Offer_Price", Query.Direction.DESCENDING);
                            break;
                        case "Sweets and Savouries":
                            sortQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .whereEqualTo("Category", "Sweets and Savouries")
                                    .orderBy("Offer_Price", Query.Direction.DESCENDING);
                            break;
                        case "Let's Party":
                            sortQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .whereEqualTo("Category", "Let's Party")
                                    .orderBy("Offer_Price", Query.Direction.DESCENDING);
                            break;
                        case "Vegetables and Fruites":
                            sortQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .whereEqualTo("Category", "Vegetables and Fruites")
                                    .orderBy("Offer_Price", Query.Direction.DESCENDING);
                            break;
                        default:
                            sortQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .orderBy("timestamp", Query.Direction.DESCENDING);
                            break;
                    }

                } else {
                    sortQuery = db.collection("products")
                            .whereEqualTo("Active", true)
                            .whereEqualTo("status", "approved")
                            .orderBy("Offer_Price", Query.Direction.DESCENDING);
                }

                break;
            default:
                sortQuery = db.collection("products")
                        .whereEqualTo("Active", true)
                        .whereEqualTo("status", "approved")
                        .orderBy("timestamp", Query.Direction.DESCENDING);
        }
        LoadAllProducts(sortQuery);
    }

    @Override
    public void onFilterSelected(String filterType) {

        filterSelected = filterType;

        Query filterQuery;

        switch (filterType) {
            case "All":
                if (sortSelected != null) {
                    switch (sortSelected) {
                        case "Sort by Name (A-Z)":
                            filterQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .orderBy("Title", Query.Direction.ASCENDING);
                            break;
                        case "Price: Low to High":
                            filterQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .orderBy("Offer_Price", Query.Direction.ASCENDING);
                            break;
                        case "Price: High to Low":
                            filterQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .orderBy("Offer_Price", Query.Direction.DESCENDING);
                            break;
                        default:
                            filterQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .orderBy("timestamp", Query.Direction.DESCENDING);
                            break;
                    }

                } else {
                    filterQuery = db.collection("products")
                            .whereEqualTo("Active", true)
                            .whereEqualTo("status", "approved")
                            .orderBy("timestamp", Query.Direction.DESCENDING);
                }
                break;

            case "Vegetables":
                if (sortSelected != null) {
                    switch (sortSelected) {
                        case "Sort by Name (A-Z)":
                            filterQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .whereEqualTo("Category", "Vegetables and Fruites")
                                    .whereEqualTo("SubCategory", "Vegetables")
                                    .orderBy("Title", Query.Direction.ASCENDING);
                            break;
                        case "Price: Low to High":
                            filterQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .whereEqualTo("Category", "Vegetables and Fruites")
                                    .whereEqualTo("SubCategory", "Vegetables")
                                    .orderBy("Offer_Price", Query.Direction.ASCENDING);
                            break;
                        case "Price: High to Low":
                            filterQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .whereEqualTo("Category", "Vegetables and Fruites")
                                    .whereEqualTo("SubCategory", "Vegetables")
                                    .orderBy("Offer_Price", Query.Direction.DESCENDING);
                            break;
                        default:
                            filterQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .orderBy("timestamp", Query.Direction.DESCENDING);
                            break;
                    }

                } else {
                    filterQuery = db.collection("products")
                            .whereEqualTo("Active", true)
                            .whereEqualTo("status", "approved")
                            .whereEqualTo("Category", "Vegetables and Fruites")
                            .whereEqualTo("SubCategory", "Vegetables");
                }
                break;
            case "Fruits":
                if (sortSelected != null) {
                    switch (sortSelected) {
                        case "Sort by Name (A-Z)":
                            filterQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .whereEqualTo("Category", "Vegetables and Fruites")
                                    .whereEqualTo("SubCategory", "Fruites")
                                    .orderBy("Title", Query.Direction.ASCENDING);
                            break;
                        case "Price: Low to High":
                            filterQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .whereEqualTo("Category", "Vegetables and Fruites")
                                    .whereEqualTo("SubCategory", "Fruites")
                                    .orderBy("Offer_Price", Query.Direction.ASCENDING);
                            break;
                        case "Price: High to Low":
                            filterQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .whereEqualTo("Category", "Vegetables and Fruites")
                                    .whereEqualTo("SubCategory", "Fruites")
                                    .orderBy("Offer_Price", Query.Direction.DESCENDING);
                            break;
                        default:
                            filterQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .orderBy("timestamp", Query.Direction.DESCENDING);
                            break;
                    }

                } else {
                    filterQuery = db.collection("products")
                            .whereEqualTo("Active", true)
                            .whereEqualTo("status", "approved")
                            .whereEqualTo("Category", "Vegetables and Fruites")
                            .whereEqualTo("SubCategory", "Fruites");
                }

                break;
            case "Groceries":
                if (sortSelected != null) {
                    switch (sortSelected) {
                        case "Sort by Name (A-Z)":
                            filterQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .whereEqualTo("Category", "Groceries")
                                    .orderBy("Title", Query.Direction.ASCENDING);
                            break;
                        case "Price: Low to High":
                            filterQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .whereEqualTo("Category", "Groceries")
                                    .orderBy("Offer_Price", Query.Direction.ASCENDING);
                            break;
                        case "Price: High to Low":
                            filterQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .whereEqualTo("Category", "Groceries")
                                    .orderBy("Offer_Price", Query.Direction.DESCENDING);
                            break;
                        default:
                            filterQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .orderBy("timestamp", Query.Direction.DESCENDING);
                            break;
                    }

                } else {
                    filterQuery = db.collection("products")
                            .whereEqualTo("Active", true)
                            .whereEqualTo("status", "approved")
                            .whereEqualTo("Category", "Groceries");
                }

                break;
            case "Sweets and Savouries":
                if (sortSelected != null) {
                    switch (sortSelected) {
                        case "Sort by Name (A-Z)":
                            filterQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .whereEqualTo("Category", "Sweets and Savouries")
                                    .orderBy("Title", Query.Direction.ASCENDING);
                            break;
                        case "Price: Low to High":
                            filterQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .whereEqualTo("Category", "Sweets and Savouries")
                                    .orderBy("Offer_Price", Query.Direction.ASCENDING);
                            break;
                        case "Price: High to Low":
                            filterQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .whereEqualTo("Category", "Sweets and Savouries")
                                    .orderBy("Offer_Price", Query.Direction.DESCENDING);
                            break;
                        default:
                            filterQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .orderBy("timestamp", Query.Direction.DESCENDING);
                            break;
                    }

                } else {
                    filterQuery = db.collection("products")
                            .whereEqualTo("Active", true)
                            .whereEqualTo("status", "approved")
                            .whereEqualTo("Category", "Sweets and Savouries");
                }

                break;
            case "Let's Party":
                if (sortSelected != null) {
                    switch (sortSelected) {
                        case "Sort by Name (A-Z)":
                            filterQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .whereEqualTo("Category", "Let's Party")
                                    .orderBy("Title", Query.Direction.ASCENDING);
                            break;
                        case "Price: Low to High":
                            filterQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .whereEqualTo("Category", "Let's Party")
                                    .orderBy("Offer_Price", Query.Direction.ASCENDING);
                            break;
                        case "Price: High to Low":
                            filterQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .whereEqualTo("Category", "Let's Party")
                                    .orderBy("Offer_Price", Query.Direction.DESCENDING);
                            break;
                        default:
                            filterQuery = db.collection("products")
                                    .whereEqualTo("Active", true)
                                    .whereEqualTo("status", "approved")
                                    .orderBy("timestamp", Query.Direction.DESCENDING);
                            break;
                    }

                } else {
                    filterQuery = db.collection("products")
                            .whereEqualTo("Active", true)
                            .whereEqualTo("status", "approved")
                            .whereEqualTo("Category", "Let's Party");
                }

                break;
            default:
                filterQuery = db.collection("products")
                        .whereEqualTo("Active", true)
                        .whereEqualTo("status", "approved")
                        .orderBy("timestamp", Query.Direction.DESCENDING);
        }

        LoadAllProducts(filterQuery);
    }

}

