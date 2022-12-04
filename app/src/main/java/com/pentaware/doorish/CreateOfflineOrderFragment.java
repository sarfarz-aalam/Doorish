package com.pentaware.doorish;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.pentaware.doorish.model.OfflineProduct;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateOfflineOrderFragment extends Fragment implements AdapterOfflineProducts.IOfflineProducts {

    private RecyclerView rvOfflineProducts;
    private Button btnAddProduct;
    private Button btnCreateOfflineOrder;
    private ArrayList<OfflineProduct> productList;
    private RecyclerView.Adapter adapter;
    private EditText etProductTitle;
    private EditText etProductQuantity;
    private Context mContext;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmnet_create_offline_order, container, false);


        mContext = getContext();
        productList = new ArrayList<>();
        rvOfflineProducts = view.findViewById(R.id.rv_offline_products);
        btnAddProduct = view.findViewById(R.id.btn_add_product);
        btnCreateOfflineOrder = view.findViewById(R.id.btnCreateOfflineOrder);
        etProductTitle = view.findViewById(R.id.et_product_title);
        etProductQuantity = view.findViewById(R.id.et_product_quantity);

        btnAddProduct.setOnClickListener(v -> {
            if(etProductTitle.getText().toString().matches("") || (etProductQuantity.getText().toString().matches(""))){
                Toast.makeText(mContext, "Invalid input", Toast.LENGTH_SHORT).show();;
            }
            else{
                String strTitle = etProductTitle.getText().toString();
                String strQuantity = etProductQuantity.getText().toString();
                productList.add(new OfflineProduct(strTitle, strQuantity, true, ""));
                initRecyclerView();
                etProductTitle.setText("");
                etProductQuantity.setText("");
            }
        });

        btnCreateOfflineOrder.setOnClickListener(v -> {
            if(productList.isEmpty()) Toast.makeText(mContext, "No Products added", Toast.LENGTH_SHORT).show();
            else{
                String sOrderId = UUID.randomUUID().toString();

                DocumentReference docOrder = db.collection("orders").document(sOrderId);
                Map<String, Object> data1 = new HashMap<>();

                data1.put("customer_id", CommonVariables.m_sFirebaseUserId);
                data1.put("order_id", sOrderId);
                Date dt = new Date();
                Timestamp timestamp = new Timestamp(dt);
                data1.put("order_date", timestamp);
                data1.put("offline_order", true);
                data1.put("COD", true);
                //  data1.put("GST", cart.product.GST);
                //  data1.put("Qty", cart.Qty);
                data1.put("Status", "Order received. Seller Confirmation pending.");
                //  data1.put("Unit_Price", cart.product.Offer_Price);
                data1.put("cancelled", false);
                data1.put("returned", false);
                data1.put("replacement_order", false);
                data1.put("fcm", CommonVariables.loggedInUserDetails.fcm);
                data1.put("delivery_date", null);
                //  float net_payable = cart.product.Offer_Price * cart.Qty;
                //  data1.put("net_payable", net_payable);
                data1.put("cancellation_reason", null);
                data1.put("seller_id", "exuF8Qh03rQlKtVww8cZHRMpHoE3");
                data1.put("payment_id", null);
                data1.put("points_added", 0);
                data1.put("invoice_id", null);
                data1.put("wallet_money_used", 0);
                data1.put("prepaid_cancellation_processed", false);
                data1.put("settlement_done", false);
                data1.put("settlement_date", null);
                data1.put("delivery_agent_id", null);
                data1.put("pickup_rejection_reason", null);
                data1.put("pickup_status", null);
                data1.put("delivery_charges", null);
                data1.put("pickup_timestamp", null);
                data1.put("pickup_from_store", false);
                docOrder.set(data1);



                    for (OfflineProduct product : productList){
                        DocumentReference docOfflineProducts = db.collection("orders").document(sOrderId).collection("offline_products").document();
                        Map<String, Object> offlineProduct = new HashMap<>();
                        offlineProduct.put("productTitle", product.getProductTitle());
                        offlineProduct.put("productQuantity", product.getProductQuantity());
                        offlineProduct.put("pureOfflineOrder", product.isPureOfflineOrder());
                        offlineProduct.put("orderId", sOrderId);
                        docOfflineProducts.set(offlineProduct);
                    }
                Toast.makeText(mContext, "Order placed successfully", Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
            }
        });


        return view;
    }

    private void initRecyclerView() {
        rvOfflineProducts.setHasFixedSize(true);
        rvOfflineProducts.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new AdapterOfflineProducts(mContext, productList, this);
        rvOfflineProducts.setAdapter(adapter);
    }

    @Override
    public void onClickDeleteProduct(int position) {
        productList.remove(position);
        adapter.notifyDataSetChanged();
    }
}
