package com.pentaware.doorish;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.pentaware.doorish.model.OfflineProduct;
import java.util.ArrayList;


public class OfflineProductsActivity extends AppCompatActivity implements AdapterOfflineProducts.IOfflineProducts {

    private RecyclerView rvOfflineProducts;
    private Button btnAddProduct;
    private Button btnSubmit;
    private ArrayList<OfflineProduct> productList;
    private RecyclerView.Adapter adapter;
    private EditText etProductTitle;
    private EditText etProductQuantity;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_products);

        mContext = this;
        productList = new ArrayList<>();
        rvOfflineProducts = findViewById(R.id.rv_offline_products);
        btnAddProduct = findViewById(R.id.btn_add_product);
        btnSubmit = findViewById(R.id.btn_submit);
        etProductTitle = findViewById(R.id.et_product_title);
        etProductQuantity = findViewById(R.id.et_product_quantity);

        btnAddProduct.setOnClickListener(view -> {
            if(etProductTitle.getText().toString().matches("") || (etProductQuantity.getText().toString().matches(""))){
                Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();;
            }
            else{
                String strTitle = etProductTitle.getText().toString();
                String strQuantity = etProductQuantity.getText().toString();
                productList.add(new OfflineProduct(strTitle, strQuantity, false, ""));
                initRecyclerView();
                etProductTitle.setText("");
                etProductQuantity.setText("");
            }
        });

        btnSubmit.setOnClickListener(view -> {
            Intent intent = new Intent(this, CheckoutActivity.class);
            intent.putParcelableArrayListExtra("offline_products", productList);
            startActivity(intent);
            if(productList.isEmpty()) Toast.makeText(this, "No Products added", Toast.LENGTH_SHORT).show();
            else Toast.makeText(this, "Products added successfully", Toast.LENGTH_SHORT).show();
        });
    }

    private void initRecyclerView() {
        rvOfflineProducts.setHasFixedSize(true);
        rvOfflineProducts.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdapterOfflineProducts(mContext, productList, this);
        rvOfflineProducts.setAdapter(adapter);
    }

    @Override
    public void onClickDeleteProduct(int position) {
        productList.remove(position);
        adapter.notifyDataSetChanged();
    }
}