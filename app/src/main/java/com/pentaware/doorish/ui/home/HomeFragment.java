package com.pentaware.doorish.ui.home;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.pentaware.doorish.BaseFragment;
import com.pentaware.doorish.CommonVariables;
import com.pentaware.doorish.R;
import com.pentaware.doorish.model.Address;
import com.pentaware.doorish.model.AppInfo;
import com.pentaware.doorish.model.Dashboard;
import com.pentaware.doorish.model.Product;
import com.pentaware.doorish.ui.products.ProductFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pentaware.doorish.ui.subcategories.SubCategories;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

public class HomeFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private HomeViewModel homeViewModel;
    private View mView;

    List<Uri> lstImages = new ArrayList<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();



    CheckBox chkLocal;

    int count = 0;

    private StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();

    AutoCompleteTextView searchBox;

    Dashboard mDashboard;

    CheckBox chkSearchByBrand;
    boolean m_bSearchByBrand = false;


    String restaurants[] = {
            "KFC",
            "Dominos",
            "Pizza Hut",
            "Burger King",
            "Subway",
            "Dunkin' Donuts",
            "Starbucks",
            "Cafe Coffee Day"
    };

    List<String> tagList = new ArrayList<>();
    List<String> brandList = new ArrayList<>();


    private int[] mImagesElectronics = new int[5];


    private Uri[] mCarouselImages = new Uri[5];
//    private Uri[] mCarouselImages = new Uri[]{
//          Uri.parse("https://firebasestorage.googleapis.com/v0/b/my-rupeaze.appspot.com/o/dashboard%2Fdashboard_pic1.jpg?alt=media&token=5075b0b2-2f71-4019-bcc3-8a0b74842cef"),
//            Uri.parse("https://firebasestorage.googleapis.com/v0/b/my-rupeaze.appspot.com/o/dashboard%2Fdashboard_pic1.jpg?alt=media&token=5075b0b2-2f71-4019-bcc3-8a0b74842cef"),
//            Uri.parse("https://firebasestorage.googleapis.com/v0/b/my-rupeaze.appspot.com/o/dashboard%2Fdashboard_pic1.jpg?alt=media&token=5075b0b2-2f71-4019-bcc3-8a0b74842cef"),
//            Uri.parse("https://firebasestorage.googleapis.com/v0/b/my-rupeaze.appspot.com/o/dashboard%2Fdashboard_pic1.jpg?alt=media&token=5075b0b2-2f71-4019-bcc3-8a0b74842cef"),
//            Uri.parse("https://firebasestorage.googleapis.com/v0/b/my-rupeaze.appspot.com/o/dashboard%2Fdashboard_pic1.jpg?alt=media&token=5075b0b2-2f71-4019-bcc3-8a0b74842cef")
//
//    };

    private String[] carouselTags = new String[5];

    CarouselView carouselView;

    //ImageListener imgListenerElectronics;
//    ImageListener imgListenerElectronics = new ImageListener() {
//        @Override
//        public void setImageForPosition(int position, ImageView imageView) {
//            //  imageView.setImageResource(mImagesElectronics[position]);
//            // imageView.setImageBitmap(lstImages.get(position));
//            imageView.setImageURI(mCarouselImages[position]);
//
//            Glide.with(mView.getContext())
//                    .load(mCarouselImages[position])
//                    .into(imageView);
//
//
//
//        }
//    };


    private void setGridImages() {

        setCarouselImages();

        setDashboardImages();

        setGrid1Images();

        setGrid2Images();

        setGrid3Images();

        setGrid4Images();

    }

    private void loadDashboardSettings() {
        DocumentReference docRef = db.collection("dashboard").document("dashboard");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        mDashboard = document.toObject(Dashboard.class);
                        setGridImages();

                        //  Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("HomeFragment", "No such document");
                    }
                } else {
                    Log.d("HomeFragment", "get failed with ", task.getException());
                }
            }
        });
    }


    private void setCarouselImages() {

        if (mView.getContext() == null) {

            return;
        }

        ImageView imgGrid1 = (ImageView) mView.findViewById(R.id.dashboard_pic1);
        Uri urlImg1 = Uri.parse(mDashboard.carousel_img1_url);
        mCarouselImages[0] = urlImg1;
        carouselTags[0] = mDashboard.carousel_img1_tag;

        //  lstImages.add((urlImg1));


//        imgGrid1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showItems(mDashboard.featured_img1_tag);
//            }
//        });


        ImageView imgGrid2 = (ImageView) mView.findViewById(R.id.dashboard_pic2);
        Uri urlImg2 = Uri.parse(mDashboard.carousel_img2_url);

        mCarouselImages[1] = urlImg2;
        carouselTags[1] = mDashboard.carousel_img2_tag;
        //lstImages.add(urlImg2);
//        Glide.with(mView.getContext())
//                .load(urlImg2)
//                .into(imgGrid2);
//
//        imgGrid2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showItems(mDashboard.featured_img2_tag);
//            }
//        });

        ImageView imgGrid3 = (ImageView) mView.findViewById(R.id.dashboard_pic3);
        Uri urlImg3 = Uri.parse(mDashboard.carousel_img3_url);
        mCarouselImages[2] = urlImg3;
        carouselTags[2] = mDashboard.carousel_img3_tag;
        // lstImages.add(urlImg3);
//        Glide.with(mView.getContext())
//                .load(urlImg3)
//                .into(imgGrid3);
//
//        imgGrid3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showItems(mDashboard.featured_img3_tag);
//            }
//        });

        ImageView imgGrid4 = (ImageView) mView.findViewById(R.id.dashboard_pic4);
        Uri urlImg4 = Uri.parse(mDashboard.carousel_img4_url);
        mCarouselImages[3] = urlImg4;
        carouselTags[3] = mDashboard.carousel_img4_tag;
        // lstImages.add((urlImg4));
//        Glide.with(mView.getContext())
//                .load(urlImg4)
//                .into(imgGrid4);
//
//        imgGrid4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showItems(mDashboard.featured_img4_tag);
//            }
//        });

        ImageView imgGrid5 = (ImageView) mView.findViewById(R.id.dashboard_pic5);
        Uri urlImg5 = Uri.parse(mDashboard.carousel_img5_url);
        mCarouselImages[4] = urlImg5;
        carouselTags[4] = mDashboard.carousel_img5_tag;
        //  lstImages.add((urlImg5));
//        Glide.with(mView.getContext())
//                .load(urlImg5)
//                .into(imgGrid5);
//
//        imgGrid5.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showItems(mDashboard.featured_img5_tag);
//            }
//        });

//        imgListenerElectronics = new ImageListener() {
//            @Override
//            public void setImageForPosition(int position, ImageView imageView) {
//                //imageView.setImageResource(mImagesElectronics[position]);
//                // imageView.setImageBitmap(lstImages.get(position));
//                imageView.setImageURI(lstImages.get(position));
//
//
//            }
//        };

        ImageListener imgListenerElectronics = new ImageListener() {
            @Override
            public void setImageForPosition(final int position, ImageView imageView) {
                //  imageView.setImageResource(mImagesElectronics[position]);
                // imageView.setImageBitmap(lstImages.get(position));
                imageView.setImageURI(mCarouselImages[position]);
                //  imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(mView.getContext(), "Pic index - " + Integer.toString(position), Toast.LENGTH_SHORT).show();
                        showItems(carouselTags[position]);
                    }
                });

                Glide.with(mView.getContext())
                        .load(mCarouselImages[position])
                        .into(imageView);


            }
        };


        carouselView = (CarouselView) mView.findViewById(R.id.carousel);


        carouselView.setImageListener(imgListenerElectronics);
        carouselView.setPageCount(5);

    }

    private void setDashboardImages() {

        if (mView.getContext() == null) {

            return;
        }

        ImageView imgGrid1 = (ImageView) mView.findViewById(R.id.dashboard_pic1);
        Uri urlImg1 = Uri.parse(mDashboard.featured_img1_url);
        Glide.with(mView.getContext())
                .load(urlImg1)
                .into(imgGrid1);

        imgGrid1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItems(mDashboard.featured_img1_tag);
            }
        });


        ImageView imgGrid2 = (ImageView) mView.findViewById(R.id.dashboard_pic2);
        Uri urlImg2 = Uri.parse(mDashboard.featured_img2_url);
        Glide.with(mView.getContext())
                .load(urlImg2)
                .into(imgGrid2);

        imgGrid2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItems(mDashboard.featured_img2_tag);
            }
        });

        ImageView imgGrid3 = (ImageView) mView.findViewById(R.id.dashboard_pic3);
        Uri urlImg3 = Uri.parse(mDashboard.featured_img3_url);
        Glide.with(mView.getContext())
                .load(urlImg3)
                .into(imgGrid3);

        imgGrid3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItems(mDashboard.featured_img3_tag);
            }
        });

        ImageView imgGrid4 = (ImageView) mView.findViewById(R.id.dashboard_pic4);
        Uri urlImg4 = Uri.parse(mDashboard.featured_img4_url);
        Glide.with(mView.getContext())
                .load(urlImg4)
                .into(imgGrid4);

        imgGrid4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItems(mDashboard.featured_img4_tag);
            }
        });

        ImageView imgGrid5 = (ImageView) mView.findViewById(R.id.dashboard_pic5);
        Uri urlImg5 = Uri.parse(mDashboard.featured_img5_url);
        Glide.with(mView.getContext())
                .load(urlImg5)
                .into(imgGrid5);

        imgGrid5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItems(mDashboard.featured_img5_tag);
            }
        });


    }

    private void setGrid1Images() {

        if (mView.getContext() == null) {
            return;
        }

        TextView txtHeading = mView.findViewById(R.id.header_grp1);
        TextView txtItem1 = mView.findViewById(R.id.item1_grp1);
        TextView txtItem2 = mView.findViewById(R.id.item2_grp1);
        TextView txtItem3 = mView.findViewById(R.id.item3_grp1);
        TextView txtItem4 = mView.findViewById(R.id.item4_grp1);

        txtHeading.setText(mDashboard.grp1_Title);
        txtItem1.setText(mDashboard.grp1_img1_name);
        txtItem2.setText(mDashboard.grp1_img2_name);
        txtItem3.setText(mDashboard.grp1_img3_name);
        txtItem4.setText(mDashboard.grp1_img4_name);

        Button btnDiscountGrp1Img1 = mView.findViewById(R.id.grid1_img1_discount);
        Button btnDiscountGrp1Img2 = mView.findViewById(R.id.grid1_img2_discount);
        Button btnDiscountGrp1Img3 = mView.findViewById(R.id.grid1_img3_discount);
        Button btnDiscountGrp1Img4 = mView.findViewById(R.id.grid1_img4_discount);

        if (mDashboard.discount_grp1_img1 > 0) {
            String discount = mDashboard.discount_grp1_img1 + "% OFF";
            btnDiscountGrp1Img1.setText(discount);
        } else {
            btnDiscountGrp1Img1.setVisibility(View.GONE);
        }

        if (mDashboard.discount_grp1_img2 > 0) {
            String discount = mDashboard.discount_grp1_img2 + "% OFF";
            btnDiscountGrp1Img2.setText(discount);
        } else {
            btnDiscountGrp1Img2.setVisibility(View.GONE);
        }

        if (mDashboard.discount_grp1_img3 > 0) {
            String discount = mDashboard.discount_grp1_img3 + "% OFF";
            btnDiscountGrp1Img3.setText(discount);
        } else {
            btnDiscountGrp1Img3.setVisibility(View.GONE);
        }

        if (mDashboard.discount_grp1_img4 > 0) {
            String discount = mDashboard.discount_grp1_img4 + "% OFF";
            btnDiscountGrp1Img4.setText(discount);
        } else {
            btnDiscountGrp1Img4.setVisibility(View.GONE);
        }

        ImageView imgGrid1 = (ImageView) mView.findViewById(R.id.grd1Img1);
        imgGrid1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItems(mDashboard.grp1_img1_tag);
            }
        });
        Uri urlImg1 = Uri.parse(mDashboard.grp1_img1_url);
        Glide.with(mView.getContext())
                .load(urlImg1)
                .into(imgGrid1);


        ImageView imgGrid2 = (ImageView) mView.findViewById(R.id.grd1Img2);
        imgGrid2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItems(mDashboard.grp1_img2_tag);
            }
        });
        Uri urlImg2 = Uri.parse(mDashboard.grp1_img2_url);
        Glide.with(mView.getContext())
                .load(urlImg2)
                .into(imgGrid2);

        ImageView imgGrid3 = (ImageView) mView.findViewById(R.id.grd1Img3);
        imgGrid3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItems(mDashboard.grp1_img3_tag);
            }
        });
        Uri urlImg3 = Uri.parse(mDashboard.grp1_img3_url);
        Glide.with(mView.getContext())
                .load(urlImg3)
                .into(imgGrid3);


        ImageView imgGrid4 = (ImageView) mView.findViewById(R.id.grd1Img4);
        imgGrid4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItems(mDashboard.grp1_img4_tag);
            }
        });
        Uri urlImg4 = Uri.parse(mDashboard.grp1_img4_url);
        Glide.with(mView.getContext())
                .load(urlImg4)
                .into(imgGrid4);
    }

    private void showItems(String sTag) {

        Fragment newFragment = new ProductFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        Bundle bund1 = new Bundle();
        bund1.putString("category", "Tag|" + sTag);
        newFragment.setArguments(bund1);

        transaction.hide(this);
        transaction.add(R.id.nav_host_fragment, newFragment);

        // transaction.replace(R.id.nav_host_fragment, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void setGrid2Images() {

        TextView txtHeading = mView.findViewById(R.id.header_grp2);
        TextView txtItem1 = mView.findViewById(R.id.item1_grp2);
        TextView txtItem2 = mView.findViewById(R.id.item2_grp2);
        TextView txtItem3 = mView.findViewById(R.id.item3_grp2);
        TextView txtItem4 = mView.findViewById(R.id.item4_grp2);

        txtHeading.setText(mDashboard.grp2_Title);
        txtItem1.setText(mDashboard.grp2_img1_name);
        txtItem2.setText(mDashboard.grp2_img2_name);
        txtItem3.setText(mDashboard.grp2_img3_name);
        txtItem4.setText(mDashboard.grp2_img4_name);

        Button btnDiscountGrp2Img1 = mView.findViewById(R.id.grid2_img1_discount);
        Button btnDiscountGrp2Img2 = mView.findViewById(R.id.grid2_img2_discount);
        Button btnDiscountGrp2Img3 = mView.findViewById(R.id.grid2_img3_discount);
        Button btnDiscountGrp2Img4 = mView.findViewById(R.id.grid2_img4_discount);

        if (mDashboard.discount_grp2_img1 > 0) {
            String discount = mDashboard.discount_grp1_img1 + "% OFF";
            btnDiscountGrp2Img1.setText(discount);
        } else {
            btnDiscountGrp2Img1.setVisibility(View.GONE);
        }

        if (mDashboard.discount_grp2_img2 > 0) {
            String discount = mDashboard.discount_grp2_img2 + "% OFF";
            btnDiscountGrp2Img2.setText(discount);
        } else {
            btnDiscountGrp2Img2.setVisibility(View.GONE);
        }

        if (mDashboard.discount_grp2_img3 > 0) {
            String discount = mDashboard.discount_grp2_img3 + "% OFF";
            btnDiscountGrp2Img3.setText(discount);
        } else {
            btnDiscountGrp2Img3.setVisibility(View.GONE);
        }

        if (mDashboard.discount_grp2_img4 > 0) {
            String discount = mDashboard.discount_grp2_img4 + "% OFF";
            btnDiscountGrp2Img4.setText(discount);
        } else {
            btnDiscountGrp2Img4.setVisibility(View.GONE);
        }


        ImageView imgGrid1 = (ImageView) mView.findViewById(R.id.grd2Img1);
        Uri urlImg1 = Uri.parse(mDashboard.grp2_img1_url);
        Glide.with(mView.getContext())
                .load(urlImg1)
                .into(imgGrid1);

        imgGrid1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItems(mDashboard.grp2_img1_tag);
            }
        });


        ImageView imgGrid2 = (ImageView) mView.findViewById(R.id.grd2Img2);
        Uri urlImg2 = Uri.parse(mDashboard.grp2_img2_url);
        Glide.with(mView.getContext())
                .load(urlImg2)
                .into(imgGrid2);

        imgGrid2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItems(mDashboard.grp2_img2_tag);
            }
        });

        ImageView imgGrid3 = (ImageView) mView.findViewById(R.id.grd2Img3);
        Uri urlImg3 = Uri.parse(mDashboard.grp2_img3_url);
        Glide.with(mView.getContext())
                .load(urlImg3)
                .into(imgGrid3);

        imgGrid3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItems(mDashboard.grp2_img3_tag);
            }
        });


        ImageView imgGrid4 = (ImageView) mView.findViewById(R.id.grd2Img4);
        Uri urlImg4 = Uri.parse(mDashboard.grp2_img4_url);
        Glide.with(mView.getContext())
                .load(urlImg4)
                .into(imgGrid4);

        imgGrid4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItems(mDashboard.grp2_img4_tag);
            }
        });

    }

    private void setGrid3Images() {

        TextView txtHeading = mView.findViewById(R.id.header_grp3);
        TextView txtItem1 = mView.findViewById(R.id.item1_grp3);
        TextView txtItem2 = mView.findViewById(R.id.item2_grp3);
        TextView txtItem3 = mView.findViewById(R.id.item3_grp3);
        TextView txtItem4 = mView.findViewById(R.id.item4_grp3);

        txtHeading.setText(mDashboard.grp3_Title);
        txtItem1.setText(mDashboard.grp3_img1_name);
        txtItem2.setText(mDashboard.grp3_img2_name);
        txtItem3.setText(mDashboard.grp3_img3_name);
        txtItem4.setText(mDashboard.grp3_img4_name);

        Button btnDiscountGrp3Img1 = mView.findViewById(R.id.grid3_img1_discount);
        Button btnDiscountGrp3Img2 = mView.findViewById(R.id.grid3_img2_discount);
        Button btnDiscountGrp3Img3 = mView.findViewById(R.id.grid3_img3_discount);
        Button btnDiscountGrp3Img4 = mView.findViewById(R.id.grid3_img4_discount);

        if (mDashboard.discount_grp3_img1 > 0) {
            String discount = mDashboard.discount_grp3_img1 + "% OFF";
            btnDiscountGrp3Img1.setText(discount);
        } else {
            btnDiscountGrp3Img1.setVisibility(View.GONE);
        }

        if (mDashboard.discount_grp3_img2 > 0) {
            String discount = mDashboard.discount_grp3_img2 + "% OFF";
            btnDiscountGrp3Img2.setText(discount);
        } else {
            btnDiscountGrp3Img2.setVisibility(View.GONE);
        }

        if (mDashboard.discount_grp3_img3 > 0) {
            String discount = mDashboard.discount_grp3_img3 + "% OFF";
            btnDiscountGrp3Img3.setText(discount);
        } else {
            btnDiscountGrp3Img3.setVisibility(View.GONE);
        }

        if (mDashboard.discount_grp2_img4 > 0) {
            String discount = mDashboard.discount_grp2_img4 + "% OFF";
            btnDiscountGrp3Img4.setText(discount);
        } else {
            btnDiscountGrp3Img4.setVisibility(View.GONE);
        }


        ImageView imgGrid1 = (ImageView) mView.findViewById(R.id.grd3Img1);
        Uri urlImg1 = Uri.parse(mDashboard.grp3_img1_url);
        Glide.with(mView.getContext())
                .load(urlImg1)
                .into(imgGrid1);

        imgGrid1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItems(mDashboard.grp3_img1_tag);
            }
        });


        ImageView imgGrid2 = (ImageView) mView.findViewById(R.id.grd3Img2);
        Uri urlImg2 = Uri.parse(mDashboard.grp3_img2_url);
        Glide.with(mView.getContext())
                .load(urlImg2)
                .into(imgGrid2);

        imgGrid2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItems(mDashboard.grp3_img2_tag);
            }
        });

        ImageView imgGrid3 = (ImageView) mView.findViewById(R.id.grd3Img3);
        Uri urlImg3 = Uri.parse(mDashboard.grp3_img3_url);
        Glide.with(mView.getContext())
                .load(urlImg3)
                .into(imgGrid3);

        imgGrid3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItems(mDashboard.grp3_img3_tag);
            }
        });


        ImageView imgGrid4 = (ImageView) mView.findViewById(R.id.grd3Img4);
        Uri urlImg4 = Uri.parse(mDashboard.grp3_img4_url);
        Glide.with(mView.getContext())
                .load(urlImg4)
                .into(imgGrid4);

        imgGrid4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItems(mDashboard.grp3_img4_tag);
            }
        });


    }

    private void setGrid4Images() {

        TextView txtHeading = mView.findViewById(R.id.header_grp4);
        TextView txtItem1 = mView.findViewById(R.id.item1_grp4);
        TextView txtItem2 = mView.findViewById(R.id.item2_grp4);
        TextView txtItem3 = mView.findViewById(R.id.item3_grp4);
        TextView txtItem4 = mView.findViewById(R.id.item4_grp4);

        txtHeading.setText(mDashboard.grp4_Title);
        txtItem1.setText(mDashboard.grp4_img1_name);
        txtItem2.setText(mDashboard.grp4_img2_name);
        txtItem3.setText(mDashboard.grp4_img3_name);
        txtItem4.setText(mDashboard.grp4_img4_name);

        Button btnDiscountGrp4Img1 = mView.findViewById(R.id.grid4_img1_discount);
        Button btnDiscountGrp4Img2 = mView.findViewById(R.id.grid4_img2_discount);
        Button btnDiscountGrp4Img3 = mView.findViewById(R.id.grid4_img3_discount);
        Button btnDiscountGrp4Img4 = mView.findViewById(R.id.grid4_img4_discount);

        if (mDashboard.discount_grp4_img1 > 0) {
            String discount = mDashboard.discount_grp4_img1 + "% OFF";
            btnDiscountGrp4Img1.setText(discount);
        } else {
            btnDiscountGrp4Img1.setVisibility(View.GONE);
        }

        if (mDashboard.discount_grp4_img2 > 0) {
            String discount = mDashboard.discount_grp4_img2 + "% OFF";
            btnDiscountGrp4Img2.setText(discount);
        } else {
            btnDiscountGrp4Img2.setVisibility(View.GONE);
        }

        if (mDashboard.discount_grp4_img3 > 0) {
            String discount = mDashboard.discount_grp4_img3 + "% OFF";
            btnDiscountGrp4Img3.setText(discount);
        } else {
            btnDiscountGrp4Img3.setVisibility(View.GONE);
        }

        if (mDashboard.discount_grp4_img4 > 0) {
            String discount = mDashboard.discount_grp4_img4 + "% OFF";
            btnDiscountGrp4Img4.setText(discount);
        } else {
            btnDiscountGrp4Img4.setVisibility(View.GONE);
        }

        ImageView imgGrid1 = (ImageView) mView.findViewById(R.id.grd4Img1);
        Uri urlImg1 = Uri.parse(mDashboard.grp4_img1_url);
        Glide.with(mView.getContext())
                .load(urlImg1)
                .into(imgGrid1);

        imgGrid1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItems(mDashboard.grp4_img1_tag);
            }
        });


        ImageView imgGrid2 = (ImageView) mView.findViewById(R.id.grd4Img2);
        Uri urlImg2 = Uri.parse(mDashboard.grp4_img2_url);
        Glide.with(mView.getContext())
                .load(urlImg2)
                .into(imgGrid2);

        imgGrid2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItems(mDashboard.grp4_img2_tag);
            }
        });


        ImageView imgGrid3 = (ImageView) mView.findViewById(R.id.grd4Img3);
        Uri urlImg3 = Uri.parse(mDashboard.grp4_img3_url);
        Glide.with(mView.getContext())
                .load(urlImg3)
                .into(imgGrid3);

        imgGrid3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItems(mDashboard.grp4_img3_tag);
            }
        });


        ImageView imgGrid4 = (ImageView) mView.findViewById(R.id.grd4Img4);
        Uri urlImg4 = Uri.parse(mDashboard.grp4_img4_url);
        Glide.with(mView.getContext())
                .load(urlImg4)
                .into(imgGrid4);

        imgGrid4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItems(mDashboard.grp4_img4_tag);
            }
        });


    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        mView = inflater.inflate(R.layout.fragment_home, container, false);

        chkSearchByBrand = mView.findViewById(R.id.chkSearchByBrands);
        chkSearchByBrand.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                m_bSearchByBrand = b;
                setAdapterForSearchBox(b);
            }
        });

        Log.d("user_orders", "count = " + CommonVariables.user_orders);


        if((getActivity()) != null )
        //((AppCompatActivity)getActivity()).getActionBar().setElevation(0);



        init();
       // askRatings();


        return mView;
    }


    private void init() {
        searchBox = (AutoCompleteTextView) mView.findViewById(R.id.txtSearchGlobal);
        ImageView imgfruites = (ImageView) mView.findViewById(R.id.grd0Imgfruits);
        ImageView imgGroceries = (ImageView) mView.findViewById(R.id.grd0ImgGroceries);
        ImageView imgSweets = mView.findViewById(R.id.grd0ImgSweets);
        ImageView imgParty = mView.findViewById(R.id.grd0ImgletsParty);

        imgfruites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProducts("Vegetables and Fruites");
            }
        });

        imgGroceries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProducts("Groceries");
            }
        });

        imgSweets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProducts("Sweets and Savouries");
            }
        });

        imgParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProducts("Let's Party");
            }
        });

        setAdapterForSearchBox(false);
        loadDashboardSettings();

        chkLocal = (CheckBox) mView.findViewById(R.id.chkSearchLocal);

        //setGridImages();

        if (CommonVariables.loggedInUserDetails == null) {
            TextView txtDeliverTo = (TextView) mView.findViewById(R.id.txtDeliverTo);
            txtDeliverTo.setText("Hello Guest!");
        } else {
            setAddress();
        }

        getAppInfo();

    }


    void setAdapterForSearchBox(boolean bSearchByBrand) {
        ArrayAdapter<String> adapter = null;
        if (!bSearchByBrand) {
            adapter = new ArrayAdapter<String>(mView.getContext(), android.R.layout.simple_list_item_1, CommonVariables.tagList);
        } else {
            adapter = new ArrayAdapter<String>(mView.getContext(), android.R.layout.simple_list_item_1, CommonVariables.brandList);
        }


        searchBox.setAdapter(adapter);
        searchBox.setOnItemClickListener(this);

        searchBox.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
                //If the keyevent is a key-down event on the "enter" button
                if ((keyevent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    hideKeyboard();
                    SearchItem(searchBox.getText().toString(), m_bSearchByBrand);
                    //...
                    // Perform your action on key press here
                    // ...
                    return true;
                }
                return false;
            }
        });

    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        hideKeyboard();

        String item = parent.getItemAtPosition(position).toString();
        SearchItem(item, m_bSearchByBrand);
        //   CommonVariables.SEARCH_LOCAL = chkLocal.isChecked();

        // create Toast with user selected value
        //Toast.makeText(mView.getContext(), "Selected Item is: \t" + item, Toast.LENGTH_LONG).show();

        // set user selected value to the TextView


    }

    private void SearchItem(String item, boolean searchByBrand) {

        String searchLocal = "false";
        if (chkLocal.isChecked()) {
            searchLocal = "true";
        }

        searchBox.setText(item);


        Fragment newFragment = new ProductFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        Bundle bund1 = new Bundle();
        if (!searchByBrand) {
            bund1.putString("category", "Tag|" + item);
        } else {
            bund1.putString("category", "Brand|" + item);
        }

        bund1.putString("searchlocal", searchLocal);
        newFragment.setArguments(bund1);

        transaction.hide(this);
        transaction.add(R.id.nav_host_fragment, newFragment);

        // transaction.replace(R.id.nav_host_fragment, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        //clearing searched query
        searchBox.setText("");
    }

    public void setAddress() {
        final TextView txtDeliverTo = (TextView) mView.findViewById(R.id.txtDeliverTo);
        DocumentReference docRef = db.collection("users").document(CommonVariables.loggedInUserDetails.customer_id).collection("Addresses")
                .document(CommonVariables.loggedInUserDetails.AddressId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        CommonVariables.deliveryAddress = document.toObject(Address.class);


                        if (CommonVariables.loggedInUserDetails != null) {

                            String sText;
                            if (CommonVariables.deliveryAddress.Name != null) {
                                sText = "Deliver to " + CommonVariables.deliveryAddress.Name + " - " + CommonVariables.deliveryAddress.City + "(" + CommonVariables.deliveryAddress.Pincode + ")";
                            } else
                                sText = "Deliver to " + CommonVariables.loggedInUserDetails.Name + " - " + CommonVariables.loggedInUserDetails.City + "(" + CommonVariables.loggedInUserDetails.Pincode + ")";
                            txtDeliverTo.setText(sText);
                        } else {
                            txtDeliverTo.setText("Hello Guest!");
                        }
                    }
                } else {
                    // Log.d("TAG", "get failed with ", task.getException());
                    txtDeliverTo.setText("Hello Guest!");
                }
            }
        });
    }

    public void showProducts(String sCategory) {
        Fragment newFragment = SubCategories.newInstance();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Bundle bund1 = new Bundle();
        bund1.putString("category", sCategory);
        newFragment.setArguments(bund1);
        transaction.hide(this);
        transaction.add(R.id.nav_host_fragment, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void getAppInfo() {
        db.collection("AppInfo").document("AppInfo").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() != null) {
                        AppInfo appInfo = task.getResult().toObject(AppInfo.class);
                        if (appInfo != null) {
                            Button btnDiscount1 = mView.findViewById(R.id.grid0_img1_discount);
                            Button btnDiscount2 = mView.findViewById(R.id.grid0_img2_discount);
                            Button btnDiscount3 = mView.findViewById(R.id.grid0_img3_discount);
                            Button btnDiscount4 = mView.findViewById(R.id.grid0_img4_discount);

                            btnDiscount1.setText(appInfo.category_1_discount);
                            btnDiscount2.setText(appInfo.category_2_discount);
                            btnDiscount3.setText(appInfo.category_3_discount);
                            btnDiscount4.setText(appInfo.category_4_discount);
                        }
                    }
                }
            }
        });
    }
    void askRatings() {
        ReviewManager manager = ReviewManagerFactory.create(getContext());
        com.google.android.play.core.tasks.Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                ReviewInfo reviewInfo = task.getResult();
                com.google.android.play.core.tasks.Task<Void> flow = manager.launchReviewFlow(getActivity(), reviewInfo);
                flow.addOnCompleteListener(task2 -> {
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                });
            } else {
                Log.d("ask_rating", "msg:"+task.getException().toString());
                // There was some problem, continue regardless of the result.
            }
        });
    }
}