package com.pentaware.doorish.ui.orders;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import pl.droidsonroids.gif.GifImageView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.pentaware.OrdersViewHolder;
import com.pentaware.doorish.AdapterOrderListing;
import com.pentaware.doorish.BaseFragment;
import com.pentaware.doorish.CommonVariables;
import com.pentaware.doorish.Enums;
import com.pentaware.doorish.IOrderOperations;
import com.pentaware.doorish.OrderListing;
import com.pentaware.doorish.ViewOfflineProductsActivity;
import com.pentaware.doorish.model.Cart;
import com.pentaware.doorish.model.OfflineProduct;
import com.pentaware.doorish.model.OrderAndProduct;
import com.pentaware.doorish.model.Orders;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.pentaware.doorish.R;
import com.pentaware.doorish.ui.products.ProductFragment;

import java.util.ArrayList;
import java.util.List;

public class  MyOrdersFragment extends BaseFragment implements IOrderOperations, AdapterOrderListing.IOfflineProducts {

    private int PAGE_SIZE = 15;
    DocumentSnapshot mLastVisible = null;
    private MyOrdersViewModel mViewModel;
    private List<Cart> mCartList;
    private List<OrderListing> listItems;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private IOrderOperations mOrderOperations;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    GifImageView gifView;
    private int orderCount = 0;
    private int productsMapped = 0;
    private Button btnNext, btnPrevious;
    List<Orders> ordersList;

    private TextView txtNoOrders;
    private Button btnShopNow;
    private ImageView imgNoOrders;

    private View mView;
    private FirestorePagingAdapter<Orders, OrdersViewHolder> pagingAdapter;

    private ProgressBar progressBarRecycler;
    private TextView txtErrorRecycler;
    private Button btnRetryRecycler;

    List<Query> queryList = new ArrayList<>();
    int pageIndex = 0;

    public static MyOrdersFragment newInstance() {
        return new MyOrdersFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.my_orders_fragment, container, false);
        mOrderOperations = this;
        recyclerView = (RecyclerView) mView.findViewById(R.id.orderRecycler);
//        recyclerView.setHasFixedSize(true);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(MyOrdersViewModel.class);


        gifView = (GifImageView) mView.findViewById(R.id.gifView);

        // TODO: Use the ViewModel

        btnNext = mView.findViewById(R.id.btnNext);
        btnPrevious = mView.findViewById(R.id.btnPrevious);

        progressBarRecycler = mView.findViewById(R.id.progress_bar_recycler);
        txtErrorRecycler = mView.findViewById(R.id.txt_error_recycler);
        btnRetryRecycler = mView.findViewById(R.id.btn_retry);

        txtNoOrders = mView.findViewById(R.id.txt_no_orders);
        btnShopNow = mView.findViewById(R.id.btn_shop_now);
        imgNoOrders = mView.findViewById(R.id.img_no_orders);

        btnShopNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment newFragment = new ProductFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                Bundle bund1 = new Bundle();
                bund1.putString("category", "All");
                newFragment.setArguments(bund1);

                transaction.hide(MyOrdersFragment.this);
                transaction.replace(R.id.nav_host_fragment, newFragment);

                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


//        txtNoOrders.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });


        ordersList = new ArrayList<>();

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gifView.setVisibility(View.VISIBLE);
                btnPrevious.setVisibility(View.VISIBLE);

                Query query = db.collection("online_orders")
                        .whereEqualTo("customer_id", CommonVariables.m_sFirebaseUserId)
                        .orderBy("order_date", Query.Direction.DESCENDING)
                        .startAfter(mLastVisible)
                        .limit(PAGE_SIZE);

                pageIndex++;
                queryList.add(query);
                LoadAllOrders(query);

            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gifView.setVisibility(View.VISIBLE);

                if (pageIndex == 0) {
                    btnPrevious.setVisibility(View.GONE);
                    return;
                }

                pageIndex = pageIndex - 1;
                btnNext.setVisibility(View.VISIBLE);


                Query query = queryList.get(pageIndex);
                LoadAllOrders(query);

            }
        });

        listItems = new ArrayList<>();


        btnRetryRecycler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reloadRecyclerOnError();
            }
        });


        Query query = db.collection("online_orders")
                .whereEqualTo("customer_id", CommonVariables.m_sFirebaseUserId)
                .orderBy("order_date", Query.Direction.DESCENDING);

        queryList.add(query);
        LoadAllOrders(query);
    }

    @Override
    public void onStart() {
        super.onStart();
        pagingAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        pagingAdapter.stopListening();
    }

    private void LoadAllOrders(Query query) {

        recyclerView.setLayoutManager(new LinearLayoutManager(mView.getContext()));

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(2)
                .setPageSize(7)
                .build();

        FirestorePagingOptions options = new FirestorePagingOptions.Builder<Orders>()
                .setLifecycleOwner(MyOrdersFragment.this)
                .setQuery(query, config, Orders.class)
                .build();

        pagingAdapter = new FirestorePagingAdapter<Orders, OrdersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrdersViewHolder ordersViewHolder, int i, @NonNull Orders orders) {
                ordersViewHolder.bind(orders, getContext(), MyOrdersFragment.this);
            }

            @NonNull
            @Override
            public OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_order_listing, parent, false);
                return new OrdersViewHolder(view);
            }

            @Override
            protected void onLoadingStateChanged(@NonNull LoadingState state) {
                super.onLoadingStateChanged(state);
                switch (state) {
                    case LOADING_INITIAL:
                        Log.d("value_here", "initial loading");
                        gifView.setVisibility(View.VISIBLE);
                        break;

                    case LOADING_MORE:
                        Log.d("value_here", "loading more");
                        progressBarRecycler.setVisibility(View.VISIBLE);
                        break;

                    case LOADED:
                        Log.d("value_here", "loaded");
                        Log.d("value_here", String.valueOf(pagingAdapter.getItemCount()));
                        gifView.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        progressBarRecycler.setVisibility(View.GONE);
                        txtErrorRecycler.setVisibility(View.GONE);
                        btnRetryRecycler.setVisibility(View.GONE);
                        break;

                    case ERROR:
                        Log.d("value_here", "error occurred ");
                        txtErrorRecycler.setVisibility(View.VISIBLE);
                        btnRetryRecycler.setVisibility(View.VISIBLE);
                        break;

                    case FINISHED:
                        Log.d("value_here", "finished loading ");
                        if(getItemCount() == 0){
                            txtNoOrders.setVisibility(View.VISIBLE);
                            btnShopNow.setVisibility(View.VISIBLE);
                            imgNoOrders.setVisibility(View.VISIBLE);
                            gifView.setVisibility(View.GONE);
                        }
                        else {
                            txtNoOrders.setVisibility(View.GONE);
                            btnShopNow.setVisibility(View.GONE);
                            imgNoOrders.setVisibility(View.GONE);
                        }
                        progressBarRecycler.setVisibility(View.GONE);
                        recyclerView.setPadding(0,0,0,0);
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

//        query.get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            QuerySnapshot documentSnapshots = task.getResult();
//                            if (documentSnapshots.size() == 0) {
//                                gifImage.setVisibility(View.GONE);
//                                txtNoOrders.setVisibility(View.VISIBLE);
//                                imgNoOrders.setVisibility(View.VISIBLE);
//                                adapter = new AdapterOrderListing(listItems, mView.getContext(), mOrderOperations, MyOrdersFragment.this, pageIndex);
//                                recyclerView.setAdapter(adapter);
//                                Toast.makeText(mView.getContext(), "No orders found", Toast.LENGTH_SHORT).show();
//                                return;
//                            }
//                            mLastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
//                            if (documentSnapshots.size() < CommonVariables.PAGE_SIZE) {
////                                btnNext.setVisibility(View.GONE);
//                            }
//
//
//                            orderCount = task.getResult().size();
//                            if (orderCount == 0) {
//
//                                btnNext.setVisibility(View.GONE);
//                                gifImage.setVisibility(View.GONE);
//                                txtNoOrders.setVisibility(View.VISIBLE);
//                                imgNoOrders.setVisibility(View.VISIBLE);
//                                Toast.makeText(mView.getContext(), "No order found", Toast.LENGTH_SHORT).show();
//                                return;
//
//                            }
//
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Orders order = document.toObject(Orders.class);
//                                ordersList.add(order);
//                                //if it was a prepaid order but payment could not be processed don't show it..
//                                if (TextUtils.isEmpty(order.payment_id) && order.COD == false) {
//                                    orderCount--;
//                                    continue;
//                                }
//
//                                if(ordersList.size() > 0){
//                                    txtNoOrders.setVisibility(View.GONE);
//                                    imgNoOrders.setVisibility(View.GONE);
//                                }
//
//                                Log.w("Order_Status - Order_id", order.Status + " - " + order.order_id);
//                                // MapOfflineProductAgainstOrder(order);
//                                MapProductAgainstOrder(order);
//                            }
//
//
//                            if (orderCount == 0) {
//                                btnNext.setVisibility(View.GONE);
//                                gifImage.setVisibility(View.GONE);
//                                txtNoOrders.setVisibility(View.VISIBLE);
//                                imgNoOrders.setVisibility(View.VISIBLE);
//                                Toast.makeText(mView.getContext(), "No order found", Toast.LENGTH_SHORT).show();
//                                return;
//
//                            }
//                        } else {
//                            Log.d("TAG", "Error getting documents: ", task.getException());
//                        }
//                    }
//                });

    }

//    private void MapProductAgainstOrder(final Orders order) {
//
//
//        db.collection("orders").document(order.order_id).collection("products")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            productsMapped++;
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Product product = document.toObject(Product.class);
//                                order.productList.add(product);
//                                OrderAndProduct orderAndProduct = new OrderAndProduct();
//                                orderAndProduct.order = order;
//
//                                Log.w("Order_id", order.order_id);
//                                //Toast.makeText(mView.getContext(), order.Status, Toast.LENGTH_SHORT).show();
//                                orderAndProduct.product = product;
//                                OrderListing orderListing = new OrderListing(orderAndProduct);
//                                listItems.add(orderListing);
//                                // Log.d("TAG", "getting documents: ", task.getException());
//                            }
//                            if (productsMapped == orderCount) {
//                                productsMapped = 0;
//                                orderCount = 0;
//                                adapter = new AdapterOrderListing(listItems, mView.getContext(), mOrderOperations, MyOrdersFragment.this, pageIndex);
//                                recyclerView.setAdapter(adapter);
//                                gifView.setVisibility(View.GONE);
//                            }
//                        } else {
//                            Log.d("TAG", "Error getting documents: ", task.getException());
//                        }
//                    }
//                });
//
//
//    }


//    @Override
//    public void DoOrderOperations(OrderAndProduct orderAndProduct, Enums.OrderOperations orderOperations) {
//        Orders order = orderAndProduct.order;
//        if (orderOperations == Enums.OrderOperations.CancelOrder) {
//
//            DocumentReference orderRef = db.collection("orders").document(order.order_id);
//            orderRef.update("Status", "Cancelled")
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            Toast.makeText(mView.getContext(), "Order Cancelled", Toast.LENGTH_SHORT).show();
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(mView.getContext(), "Order could not be cancelled", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//
//
//        }
//
//        if (orderOperations == Enums.OrderOperations.NoOperation) {
//            BaseFragment newFragment = OrderDetailFragment.newInstance(orderAndProduct);
////            Fragment fragment = getFragmentManager().findFragmentById(R.id.nav_host_fragment);
//            FragmentTransaction transaction = getFragmentManager().beginTransaction();
//            transaction.hide(this);
//            transaction.add(R.id.nav_host_fragment, newFragment);
//            transaction.addToBackStack(null);
//            transaction.commit();
//        }
//
//
//        // mProductAdapterClickEvent.onProductClick(product);
//
//
////
////        Intent intent = new Intent(m_ctx, EmployeeTabs.class);
////        intent.putExtra("employee", employee);
////        m_ctx.startActivity(intent);
//
//    }

    @Override
    public void DoOrderOperations(Orders order, Enums.OrderOperations orderOperations) {
        if (orderOperations == Enums.OrderOperations.CancelOrder) {

            DocumentReference orderRef = db.collection("online_orders").document(order.order_id);
            orderRef.update("Status", "Cancelled")
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(mView.getContext(), "Order Cancelled", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(mView.getContext(), "Order could not be cancelled", Toast.LENGTH_SHORT).show();
                        }
                    });


        }

        if (orderOperations == Enums.OrderOperations.NoOperation) {
            BaseFragment newFragment = OrderDetailFragment.newInstance(order);
//            Fragment fragment = getFragmentManager().findFragmentById(R.id.nav_host_fragment);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.hide(this);
            transaction.add(R.id.nav_host_fragment, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }


        // mProductAdapterClickEvent.onProductClick(product);


//
//        Intent intent = new Intent(m_ctx, EmployeeTabs.class);
//        intent.putExtra("employee", employee);
//        m_ctx.startActivity(intent);

    }

    @Override
    public void onNextClick() {
        LoadNextProducts();
    }

    @Override
    public void onPreviousClick() {
        LoadPreviousProducts();
    }

    private void LoadNextProducts() {

        gifView.setVisibility(View.VISIBLE);
        Query query = db.collection("orders")
                .whereEqualTo("customer_id", CommonVariables.m_sFirebaseUserId)
                .orderBy("order_date", Query.Direction.DESCENDING)
                .startAfter(mLastVisible)
                .limit(CommonVariables.PAGE_SIZE);

        pageIndex++;
        queryList.add(query);
        LoadAllOrders(query);
    }

    private void LoadPreviousProducts() {
        gifView.setVisibility(View.VISIBLE);

        if (pageIndex == 0) {
            return;
        }

        pageIndex = pageIndex - 1;


        Query query = queryList.get(pageIndex);
        LoadAllOrders(query);
    }

    private void reloadRecyclerOnError(){
        pagingAdapter.retry();
    }


    @Override
    public void onClickViewProducts(int position) {
        ArrayList<OfflineProduct> offlineProductArrayList = new ArrayList<>();
        OrderListing listing = listItems.get(position);
        OrderAndProduct orderAndProduct = listing.getmOrderAndProduct();
        String orderId = orderAndProduct.order.order_id;
        db.collection("orders").document(orderId).collection("offline_products").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        offlineProductArrayList.add(documentSnapshot.toObject(OfflineProduct.class));
                    }
                    if (offlineProductArrayList.isEmpty()) {
                        Toast.makeText(getContext(), "No offline products", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(getContext(), ViewOfflineProductsActivity.class);
                        intent.putParcelableArrayListExtra("offline_product_list", offlineProductArrayList);
                        startActivity(intent);
                    }
                }

            }
        });
    }
}

