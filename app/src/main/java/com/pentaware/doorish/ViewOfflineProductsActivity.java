package com.pentaware.doorish;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import com.pentaware.doorish.model.OfflineProduct;
import java.util.ArrayList;
public class ViewOfflineProductsActivity extends AppCompatActivity {

    RecyclerView rvDisplayOfflineProducts;
    ArrayList<OfflineProduct> offlineProducts;
    RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_offline_products);

        offlineProducts = new ArrayList<>();
        rvDisplayOfflineProducts = findViewById(R.id.rv_display_offline_products);

        if(getIntent() != null){
            offlineProducts = getIntent().getParcelableArrayListExtra("offline_product_list");
            initRecyclerView();
        }



    }

    private void initRecyclerView() {
        rvDisplayOfflineProducts.setHasFixedSize(true);
        rvDisplayOfflineProducts.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdapterViewProductOrder(this, offlineProducts);
        rvDisplayOfflineProducts.setAdapter(adapter);
    }
}