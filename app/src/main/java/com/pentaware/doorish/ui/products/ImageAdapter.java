package com.pentaware.doorish.ui.products;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class ImageAdapter extends PagerAdapter {
    Context mContext;
    private LayoutInflater layoutInflater;
    private List<Uri> mImageUriList = new ArrayList<>();


//    private Uri[] sliderImageId = new Uri[]{
//            Uri.parse("https://firebasestorage.googleapis.com/v0/b/my-rupeaze.appspot.com/o/products%2F25771781-6147-476b-8666-9b15df344805%2Fcover.jpg?alt=media&token=156176d9-5294-48a2-9bec-0dd0e4500b1e"),
//            Uri.parse("https://firebasestorage.googleapis.com/v0/b/my-rupeaze.appspot.com/o/products%2F25771781-6147-476b-8666-9b15df344805%2F1.jpg?alt=media&token=e69f4a7c-4da2-4afe-a184-b0eee8c43f7a"),
//            Uri.parse("https://firebasestorage.googleapis.com/v0/b/my-rupeaze.appspot.com/o/products%2F25771781-6147-476b-8666-9b15df344805%2F2.jpg?alt=media&token=23a168bd-805c-4eef-bf41-7017bab64671"),
//            Uri.parse("https://firebasestorage.googleapis.com/v0/b/my-rupeaze.appspot.com/o/products%2F25771781-6147-476b-8666-9b15df344805%2F3.jpg?alt=media&token=1d2f5ab1-787d-4c4e-ac82-c535967c4108"),
//            Uri.parse("https://firebasestorage.googleapis.com/v0/b/my-rupeaze.appspot.com/o/products%2F25771781-6147-476b-8666-9b15df344805%2F5.jpg?alt=media&token=811f502d-a580-4b2e-bd4d-ee2ffe6d4df3")
//            //  R.drawable.image1, R.drawable.image2, R.drawable.image3,R.drawable.image4, R.drawable.image5,
//    };

    ImageAdapter(Context context, List<Uri> lstImages) {
        this.mContext = context;
        this.mImageUriList = lstImages;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ImageView) object);
    }



    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(mContext);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        //imageView.setImageURI(sliderImageId[position]);

        //imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        Glide.with(mContext)
                .load(mImageUriList.get(position)) // the uri you got from Firebase
                .into(imageView);
        // imageView.setImageResource(sliderImageId[position]);


        //imageView.setAdjustViewBounds(true);


        ((ViewPager) container).addView(imageView, 0);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((ImageView) object);
    }

    @Override
    public int getCount() {
        return mImageUriList.size();
    }
}