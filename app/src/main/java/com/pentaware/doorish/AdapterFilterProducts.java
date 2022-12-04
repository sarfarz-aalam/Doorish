package com.pentaware.doorish;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.pentaware.ISortFilterOperations;
import java.util.List;

public class AdapterFilterProducts extends RecyclerView.Adapter<AdapterFilterProducts.ViewHolder> {

    private List<String> sortProductList;
    private Context ctx;
    private int selected = -1;
    private ISortFilterOperations iSortFilterOperations;

    public AdapterFilterProducts(List<String> sortProductList, Context context, ISortFilterOperations iSortFilterOperations) {
        this.sortProductList = sortProductList;
        this.ctx = context;
        this.iSortFilterOperations = iSortFilterOperations;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filter_product_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.cardViewSort.setElevation(0);
        }
        holder.cardViewSort.setRadius(7);

        String listItem = sortProductList.get(position);
        holder.txtSortFilter.setText(listItem);

        if (selected == position) {
            holder.cardViewSort.setCardBackgroundColor(Color.parseColor("#018CC9"));
            holder.txtSortFilter.setTextColor(Color.parseColor("#ffffff"));
            holder.cardViewSort.setStrokeWidth(0);
        }
        else {
            holder.cardViewSort.setStrokeColor(Color.parseColor("#d9d9d9"));
            holder.cardViewSort.setStrokeWidth(2);
            holder.cardViewSort.setCardBackgroundColor(Color.parseColor("#e5e5e5"));
            holder.txtSortFilter.setTextColor(Color.parseColor("#000000"));
        }
    }

    @Override
    public int getItemCount() {
        return sortProductList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView txtSortFilter;
        public MaterialCardView cardViewSort;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtSortFilter = itemView.findViewById(R.id.txt_sort_filter);
            cardViewSort = itemView.findViewById(R.id.card_view_sort);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    selected = getAdapterPosition();
                    iSortFilterOperations.onFilterSelected(sortProductList.get(pos));
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onClick(View view) {

        }
    }

}
