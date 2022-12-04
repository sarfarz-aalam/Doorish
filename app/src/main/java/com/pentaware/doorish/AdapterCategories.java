package com.pentaware.doorish;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import java.util.List;


public class AdapterCategories extends RecyclerView.Adapter<AdapterCategories.ViewHolder> {

    Context ctx;
    ICategory iCategoryOperations;
    List<String> categories;
    List<Integer> categoryImg;


    public AdapterCategories(Context context, List<String> lstCategories, List<Integer> lstUrl, ICategory iCategoryOperations) {
        this.iCategoryOperations = iCategoryOperations;
        categories = lstCategories;
        categoryImg = lstUrl;
        ctx = context;
    }


    @NonNull
    @Override
    public AdapterCategories.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_list_item, parent, false);
        return new AdapterCategories.ViewHolder(view, ctx);
    }


    @Override
    public void onBindViewHolder(@NonNull AdapterCategories.ViewHolder holder, int position) {
        holder.categoryTxt.setText(categories.get(position));
        holder.categoryImg.setImageResource(categoryImg.get(position));
//        Glide.with(ctx)
//                .load(categoryImg.get(position)) //the uri we got from Firebase
//                .into(holder.categoryImg); //imageView variable
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView categoryImg;
        TextView categoryTxt;

        public ViewHolder(View itemView, Context ctx) {
            super(itemView);
            categoryImg = itemView.findViewById(R.id.img_category);
            categoryTxt = itemView.findViewById(R.id.txt_category);

            itemView.setOnClickListener(view -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    iCategoryOperations.onCategoryClick(categories.get(getAdapterPosition()));
                }
            });
        }

        @Override
        public void onClick(View view) { }
    }

}



