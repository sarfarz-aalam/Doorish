package com.pentaware.doorish.ui.mrcity;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pentaware.doorish.ICategory;
import com.pentaware.doorish.R;

public class MrCityImageAdapter extends BaseAdapter {

    // Keep all Images in array
    public Integer[] mThumbIds = {
            R.drawable.groceries,
            R.drawable.veg_and_fruites,
            R.drawable.ic_sweets_nav,
            R.drawable.ic_cake_nav
//            R.drawable.electronics_appliances,
//            R.drawable.fashion_apparel,
//
//            R.drawable.footwears,
//            R.drawable.pandemic,
//            R.drawable.books,
//            R.drawable.home_decor,
//            R.drawable.mobile_computer,
//            R.drawable.sports_fitness,
//            R.drawable.cakes
    };

    public String[] mLabels = {
            "Groceries",
            "Vegetables and Fruites",
            "Sweets and Savouries",
            "Let's Party"
//            "Electronics and Appliances",
//            "Fashion and apparels",
//            "Groceries",
//            "Footwears",
//            "Pandemic Needs",
//            "Books, Toys, Games, Baby products",
//            "Home, Kitchen, pets, Furniture",
//            "Mobiles, Computers",
//            "Sports, Fitness, Bags & Luggage",
//            "Cakes, Sweets and Confectionaries"

    };

    private ICategory interfaceCategory;
    private Context mContext;
    public MrCityImageAdapter(Context ctx, ICategory icat){
        mContext = ctx;
        interfaceCategory = icat;
    }

    @Override
    public int getCount() {
        return mThumbIds.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
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
            TextView textViewAndroid = (TextView) gridViewAndroid.findViewById(R.id.android_gridview_text);
            ImageView imageViewAndroid = (ImageView) gridViewAndroid.findViewById(R.id.android_gridview_image);
            textViewAndroid.setText(mLabels[position]);
            imageViewAndroid.setImageResource(mThumbIds[position]);

            imageViewAndroid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String sCategory = mLabels[position];
                    interfaceCategory.onCategoryClick(sCategory);
                }
            });

            textViewAndroid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String sCategory = mLabels[position];
                    interfaceCategory.onCategoryClick(sCategory);
                }
            });
        } else {
            gridViewAndroid = (View) convertView;
        }

        return gridViewAndroid;
    }


}
