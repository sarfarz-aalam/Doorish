package com.pentaware.doorish.ui.subcategories;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pentaware.doorish.ISubCategories;
import com.pentaware.doorish.R;
import com.pentaware.doorish.model.subcategories;
import com.pentaware.doorish.ui.products.ProductFragment;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import static com.firebase.ui.auth.AuthUI.TAG;

public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public List<Uri> m_lstImages = new ArrayList<>();
    public String[] mLabels;

    ISubCategories mSubCategory;
    private String mCategory;

    // Constructor
    public ImageAdapter(Context c, List<String> lstCategories, List<String>urlList, ISubCategories subCategory, String sCategory) {
        mContext = c;
        mSubCategory = subCategory;
        mCategory = sCategory;


        mLabels = new String[lstCategories.size()];
        for(int i = 0; i < lstCategories.size(); i++) {
            String category = lstCategories.get(i);
            mLabels[i] = category;
        }

        for(int i =0; i < urlList.size(); i++){
            m_lstImages.add(Uri.parse(urlList.get(i)));
        }

    }

    public int getCount() {
        return m_lstImages.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View gridViewAndroid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            gridViewAndroid = new View(mContext);
            gridViewAndroid = inflater.inflate(R.layout.gridview_layout_mrcity, null);
            final TextView textViewAndroid = (TextView) gridViewAndroid.findViewById(R.id.android_gridview_text);
            ImageView imageViewAndroid = (ImageView) gridViewAndroid.findViewById(R.id.android_gridview_image);
            textViewAndroid.setText(mLabels[position]);
            Glide.with(mContext)
                    .load(m_lstImages.get(position)) // the uri you got from Firebase
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            textViewAndroid.setText(mLabels[position]);
                            return false;
                        }
                    })
                    .into(imageViewAndroid); //Your imageView variable


            imageViewAndroid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSubCategory.onSubCategoryClick(mCategory, mLabels[position]);
                }
            });

            textViewAndroid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSubCategory.onSubCategoryClick(mCategory, mLabels[position]);
                }
            });

        } else {
            gridViewAndroid = (View) convertView;
        }

        return gridViewAndroid;
    }

//    // create a new ImageView for each item referenced by the Adapter
//    public View getView(final int position, View convertView, ViewGroup parent) {
//        ImageView imageView;
//        LinearLayout linearLayout;
//        final TextView txtView;
//
//        if (convertView == null) {
//
//            linearLayout = new LinearLayout(mContext);
//            linearLayout.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.WRAP_CONTENT, GridView.LayoutParams.WRAP_CONTENT));
//            linearLayout.setOrientation(LinearLayout.VERTICAL);
//
//            imageView = new ImageView(mContext);
//            imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
//            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            imageView.setPadding(8, 8, 8, 8);
//
//            txtView = new TextView(mContext);
//            txtView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, GridView.LayoutParams.WRAP_CONTENT));
//
//            txtView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//            txtView.setPadding(8, 8, 8, 8);
//
//            //imageView.setImageResource(mThumbIds[position]);
//            //imageView.setImageURI(m_lstImages.get(position));
//
//          //  Toast.makeText(ctx, "grid image load successful", Toast.LENGTH_SHORT).show();
//            Glide.with(mContext)
//                    .load(m_lstImages.get(position)) // the uri you got from Firebase
//                    .listener(new RequestListener<Drawable>() {
//                        @Override
//                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                            return false;
//                        }
//
//                        @Override
//                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                            txtView.setText(mLabels[position]);
//                            return false;
//                        }
//                    })
//                    .into(imageView); //Your imageView variable
//
//            imageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mSubCategory.onSubCategoryClick(mCategory, mLabels[position]);
//                }
//            });
//
//
//
//            linearLayout.addView(imageView);
//            linearLayout.addView(txtView);
//
//
//        }
//        else
//        {
//            //imageView = (ImageView) convertView;
//            linearLayout = (LinearLayout)convertView;
//        }
//
//
//        return linearLayout;
//    }

    // Keep all Images in array



}
