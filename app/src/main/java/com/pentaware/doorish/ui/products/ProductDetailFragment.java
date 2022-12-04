package com.pentaware.doorish.ui.products;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pentaware.doorish.BaseFragment;
import com.pentaware.doorish.CartFragmentNav;
import com.pentaware.doorish.CommonMethods;
import com.pentaware.doorish.CommonVariables;
import com.pentaware.doorish.LandingPage;
import com.pentaware.doorish.Popup;
import com.pentaware.doorish.ShowCartFragment;
import com.pentaware.doorish.ShowReviewsFragment;
import com.pentaware.doorish.model.Cart;
import com.pentaware.doorish.model.Product;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pentaware.doorish.R;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDetailFragment extends BaseFragment {

    private ProductDetailViewModel mViewModel;

    ViewPager mViewPager;
    DotsIndicator dot1;

    List<Bitmap> lstImages = new ArrayList<>();
    HashMap<String, String> mSelectedChoices = new HashMap<>();
    HashMap<String, List<Button>> mButtonAndVariants = new HashMap<>();

    HashMap<String, String> mSelectedQtyDiscount = new HashMap<>();
    HashMap<String, List<Button>> mButtonAndDiscounts = new HashMap<>();

    public Product mProduct;
    private View mView;

    private TextView txtMRP;

    private RelativeLayout relativeLayoutWeightPolicy, relativeLayoutDisclaimer;

    private float mVariantPrice = 0;
    private float mVariantMrp = 0;

    private List<Uri> mImageUriList = new ArrayList<>();
    TextView txtOfferPrice;

    PopupWindow popUp;
    TextView txtSavings;

    Button btnDiscount;

    private TextView txtAboutProductHeader, txtProductHighlightHeader, txtProductFeaturesHeader, txtCOOHeader, txtReturnWindowHeader, txtVariableWeightHeader, txtDisclaimerHeader, txtProductIdHeader;
    private ImageButton btnExpandAboutProduct, btnExpandHighlights, btnExpandFeatures, btnExpandCOO, btnReturnWindow, btnExpandPolicy, btnExpandDisclaimer, btnExpandProductId;
    private LinearLayout layoutAboutProduct, layoutProductHighlights, layoutProductFeatures, layoutCOO, layoutReturnWindow, layoutPolicy, layoutDisclaimer, layoutProductId;

    private TextView txtVariableWeight, txtDisclaimer;

    private boolean hiddenAbout = true, hiddenHighlight = true, hiddenFeatures = true, hiddenCOO = true, hiddenReturn = true, hiddenWeightPolicy = true, hiddenDisclaimer = true, hiddenId = true;

    private StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();

    private ProductDetailFragment(Product product) {
        mProduct = product;
    }

    public static ProductDetailFragment newInstance(Product product) {

        return new ProductDetailFragment(product);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mProduct = ProductDetailFragmentArgs.fromBundle(getArguments()).getProduct();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.product_detail_fragment, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ProductDetailViewModel.class);
        // TODO: Use the ViewModel

        Button btnAddToCart = mView.findViewById(R.id.btnAddToCart);
        mViewPager = (ViewPager) mView.findViewById(R.id.viewPage);
        dot1 = mView.findViewById(R.id.dot1);

        txtAboutProductHeader = mView.findViewById(R.id.txt_about_the_product);
        txtProductHighlightHeader = mView.findViewById(R.id.txt_product_highlights);
        txtProductFeaturesHeader = mView.findViewById(R.id.txt_product_features);
        txtCOOHeader = mView.findViewById(R.id.txt_country_of_origin);
        txtReturnWindowHeader = mView.findViewById(R.id.txt_returning_window);
        txtVariableWeightHeader = mView.findViewById(R.id.txt_variable_weight_policy_header);
        txtDisclaimerHeader = mView.findViewById(R.id.txt_disclaimer_header);
        txtProductIdHeader = mView.findViewById(R.id.txt_product_id_header);

        btnExpandAboutProduct = mView.findViewById(R.id.btn_expand_about_product);
        btnExpandHighlights = mView.findViewById(R.id.btn_expand_product_highlights);
        btnExpandFeatures = mView.findViewById(R.id.btn_expand_features);
        btnExpandCOO = mView.findViewById(R.id.btn_expand_country_of_origin);
        btnReturnWindow = mView.findViewById(R.id.btn_expand_returning_window);
        btnExpandPolicy = mView.findViewById(R.id.btn_expand_variable_weight_policy);
        btnExpandDisclaimer = mView.findViewById(R.id.btn_expand_disclaimer);
        btnExpandProductId = mView.findViewById(R.id.btn_expand_product_id);

        layoutAboutProduct = mView.findViewById(R.id.layout_about_the_product);
        layoutProductHighlights = mView.findViewById(R.id.layout_product_highlights);
        layoutProductFeatures = mView.findViewById(R.id.layout_product_features);
        layoutCOO = mView.findViewById(R.id.layout_country_of_origin);
        layoutReturnWindow = mView.findViewById(R.id.layout_return_window);
        layoutPolicy = mView.findViewById(R.id.layout_var_weight_policy);
        layoutDisclaimer = mView.findViewById(R.id.layout_disclaimer);
        layoutProductId = mView.findViewById(R.id.layout_product_id);

        txtVariableWeight = mView.findViewById(R.id.txt_variable_weight_policy);
        txtDisclaimer = mView.findViewById(R.id.txt_disclaimer);

        relativeLayoutDisclaimer = mView.findViewById(R.id.relative_layout_disclaimer);
        relativeLayoutWeightPolicy = mView.findViewById(R.id.relative_layout_variable_weight);

        layoutAboutProduct.setVisibility(View.GONE);
        layoutProductHighlights.setVisibility(View.GONE);
        layoutProductFeatures.setVisibility(View.GONE);
        layoutCOO.setVisibility(View.GONE);
        layoutReturnWindow.setVisibility(View.GONE);
        layoutPolicy.setVisibility(View.GONE);
        layoutDisclaimer.setVisibility(View.GONE);
        layoutProductId.setVisibility(View.GONE);

        btnExpandAboutProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandAboutProduct();
            }
        });

        btnExpandHighlights.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandHighlights();
            }
        });

        btnExpandFeatures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandFeatures();
            }
        });

        btnExpandCOO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandCOO();
            }
        });

        btnReturnWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandReturnWindow();
            }
        });

        btnExpandPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandPolicy();
            }
        });

        btnExpandDisclaimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandDisclaimer();
            }
        });

        btnExpandProductId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandProductId();
            }
        });

        txtAboutProductHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandAboutProduct();
            }
        });

        txtProductHighlightHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandHighlights();
            }
        });

        txtProductFeaturesHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandFeatures();
            }
        });

        txtCOOHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandCOO();
            }
        });

        txtReturnWindowHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandReturnWindow();
            }
        });

        txtVariableWeightHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandPolicy();
            }
        });

        txtDisclaimerHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandDisclaimer();
            }
        });

        txtProductIdHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandProductId();
            }
        });

        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    ((LandingPage) getActivity()).onAddToCart_Click(view, null, false);
                }

            }
        });

        LoadUI();


    }

    public void expandAboutProduct() {
        if (hiddenAbout) {
            layoutAboutProduct.setVisibility(View.VISIBLE);
            btnExpandAboutProduct.setBackgroundResource(R.drawable.ic_expand_less_24px);
            hiddenAbout = false;
        } else {
            layoutAboutProduct.setVisibility(View.GONE);
            btnExpandAboutProduct.setBackgroundResource(R.drawable.ic_expand_more_24px);
            hiddenAbout = true;
        }
    }

    public void expandHighlights() {
        if (hiddenHighlight) {
            layoutProductHighlights.setVisibility(View.VISIBLE);
            btnExpandHighlights.setBackgroundResource(R.drawable.ic_expand_less_24px);
            hiddenHighlight = false;
        } else {
            layoutProductHighlights.setVisibility(View.GONE);
            btnExpandHighlights.setBackgroundResource(R.drawable.ic_expand_more_24px);
            hiddenHighlight = true;
        }
    }

    public void expandFeatures() {
        if (hiddenFeatures) {
            layoutProductFeatures.setVisibility(View.VISIBLE);
            btnExpandFeatures.setBackgroundResource(R.drawable.ic_expand_less_24px);
            hiddenFeatures = false;
        } else {
            layoutProductFeatures.setVisibility(View.GONE);
            btnExpandFeatures.setBackgroundResource(R.drawable.ic_expand_more_24px);
            hiddenFeatures = true;
        }
    }

    public void expandCOO() {
        if (hiddenCOO) {
            layoutCOO.setVisibility(View.VISIBLE);
            btnExpandCOO.setBackgroundResource(R.drawable.ic_expand_less_24px);
            hiddenCOO = false;
        } else {
            layoutCOO.setVisibility(View.GONE);
            btnExpandCOO.setBackgroundResource(R.drawable.ic_expand_more_24px);
            hiddenCOO = true;
        }
    }

    public void expandReturnWindow() {
        if (hiddenReturn) {
            layoutReturnWindow.setVisibility(View.VISIBLE);
            btnReturnWindow.setBackgroundResource(R.drawable.ic_expand_less_24px);
            hiddenReturn = false;
        } else {
            layoutReturnWindow.setVisibility(View.GONE);
            btnReturnWindow.setBackgroundResource(R.drawable.ic_expand_more_24px);
            hiddenReturn = true;
        }
    }

    public void expandPolicy() {
        if (hiddenWeightPolicy) {
            layoutPolicy.setVisibility(View.VISIBLE);
            btnExpandPolicy.setBackgroundResource(R.drawable.ic_expand_less_24px);
            hiddenWeightPolicy = false;
        } else {
            layoutPolicy.setVisibility(View.GONE);
            btnExpandPolicy.setBackgroundResource(R.drawable.ic_expand_more_24px);
            hiddenWeightPolicy = true;
        }
    }

    public void expandDisclaimer() {
        if (hiddenDisclaimer) {
            layoutDisclaimer.setVisibility(View.VISIBLE);
            btnExpandDisclaimer.setBackgroundResource(R.drawable.ic_expand_less_24px);
            hiddenDisclaimer = false;
        } else {
            layoutDisclaimer.setVisibility(View.GONE);
            btnExpandDisclaimer.setBackgroundResource(R.drawable.ic_expand_more_24px);
            hiddenDisclaimer = true;
        }
    }

    public void expandProductId() {
        if (hiddenId) {
            layoutProductId.setVisibility(View.VISIBLE);
            btnExpandProductId.setBackgroundResource(R.drawable.ic_expand_less_24px);
            hiddenId = false;
        } else {
            layoutProductId.setVisibility(View.GONE);
            btnExpandProductId.setBackgroundResource(R.drawable.ic_expand_more_24px);
            hiddenId = true;
        }
    }


    private void LoadUI() {
        CommonVariables.selectedProduct = mProduct;
        txtMRP = (TextView) mView.findViewById(R.id.txtMRP_ProductDetail);
        btnDiscount = mView.findViewById(R.id.btnDiscount);
        txtSavings = (TextView) mView.findViewById(R.id.txtSavings_ProductDetail);

        txtOfferPrice = (TextView) mView.findViewById(R.id.txtOfferPrice_ProductDetail);

        final EditText txtQty = (EditText) mView.findViewById(R.id.txtQty_ProductDetail);
        Button btnIncrease = mView.findViewById(R.id.btnIncrease);
        final Button btnDecrease = mView.findViewById(R.id.btnDecrease);

        if (mProduct.VariantsAvailable) {
            LinearLayout layoutVariants = (LinearLayout) mView.findViewById(R.id.layoutVariants);
            for (Map.Entry<String, String> entry : mProduct.Variants.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                String[] values = value.split(",");

                HorizontalScrollView scrollView = new HorizontalScrollView(mView.getContext());
                LinearLayout.LayoutParams scrollLayoutParms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 80.0f);
                layoutVariants.addView(scrollView);

                LinearLayout layout = new LinearLayout(mView.getContext());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layout.setOrientation(LinearLayout.HORIZONTAL);
                scrollView.addView(layout, layoutParams);
                TextView textView = new TextView(mView.getContext());
                //  LinearLayout.LayoutParams lp0 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                GridView.LayoutParams lp0 = new GridView.LayoutParams(150, 65);
                textView.setText(key + ": ");
                //textView.setLayoutParams(new GridView.LayoutParams(65, 65));
                layout.addView(textView, lp0);

                if(mProduct.Category.equals("Vegetables and Fruites")) {
                    Button btnBuyWeight = new Button(getContext());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        btnBuyWeight.setTextAppearance(android.R.style.TextAppearance_DeviceDefault_Medium);
                    }
                    btnBuyWeight.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                    btnBuyWeight.setTextSize(15);
                    btnBuyWeight.setBackground(btnBuyWeight.getContext().getResources().getDrawable(R.drawable.white_btn_bg));
                    btnBuyWeight.setTextColor(this.getResources().getColor(R.color.black));

                    btnBuyWeight.setText("Buy per \n gram");
                    LinearLayout.LayoutParams lpp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lpp.setMargins(5, 5, 5, 5);
                    layout.addView(btnBuyWeight, lpp);

                    btnBuyWeight.setOnClickListener(view -> {
                        Fragment newFragment = BuyWeightFragment.newInstance(mProduct);
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();


                        transaction.hide(this);
                        transaction.add(R.id.nav_host_fragment, newFragment);

                        //transaction.replace(R.id.nav_host_fragment, newFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    });
                }

                boolean selected = false;

                List<Button> lstButtons = new ArrayList<>();
                int index = 0;
                for (String s : values) {
                    Button btn = new Button(mView.getContext());


                 if (index == 0) {
                     if(CommonVariables.selectedProductId.equals(mProduct.Product_Id) && CommonVariables.selected_variant_index > -1){
                         btn.setBackground(btn.getContext().getResources().getDrawable(R.drawable.orange_button));
                         Log.d("variant_selected ", "not selected");
                     }
                     else {
                         btn.setBackground(btn.getContext().getResources().getDrawable(R.drawable.blue_button_variant));
                         mSelectedChoices.put(key, s);
                     }
                     // btn.setBackgroundColor(btn.getContext().getResources().getColor(R.color.variant_color));


                    } else {
                        //  btn.setBackgroundColor(btn.getContext().getResources().getColor(R.color.light_grey));
                        btn.setBackground(btn.getContext().getResources().getDrawable(R.drawable.orange_button));
                    }
                    index++;

                    if (CommonVariables.selectedProductId.equals(mProduct.Product_Id) && CommonVariables.selected_variant_index == index) {
                        if (CommonVariables.selectedKey != null & CommonVariables.selectedValue != null) {
                            if(!selected) {
                                btn.setBackground(btn.getContext().getResources().getDrawable(R.drawable.blue_button));
                                mSelectedChoices.put(CommonVariables.selectedKey, CommonVariables.selectedValue);
                                setVariantPrice(CommonVariables.selectedKey, CommonVariables.selectedValue);
                                selected = true;
                            }
                        }
                    }

                    btn.setTag(key + "|" + s);
                    btn.setText(s);
                    if (mProduct.variant_pricing) {
                        if (mProduct.variant_pricing_attribute.equals(key)) {
                            float varinatPrice = (float) mProduct.variant_price_map.get(s.trim());

                            String sFinalData = s + "\n\n " + CommonVariables.rupeeSymbol + " " + Float.toString(varinatPrice);
                            btn.setText(sFinalData);
                        }
                    }

                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(3, 3, 3, 3);
                    layout.addView(btn, lp);
                    lstButtons.add(btn);

                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String btnTag = v.getTag().toString();
                            String[] arrTag = btnTag.split("\\|");
                            String sKey = arrTag[0];
                            String sVal = arrTag[1];
                            List<Button> btnList = mButtonAndVariants.get(sKey);
                            for (Button btn : btnList) {
                                //   btn.setBackgroundColor(btn.getContext().getResources().getColor(R.color.light_grey));
                                btn.setBackground(btn.getContext().getResources().getDrawable(R.drawable.orange_button));

                            }

                            //   btn.setBackground(btn.getContext().getResources().getDrawable(R.drawable.orange_button));
                            v.setBackground(v.getContext().getResources().getDrawable(R.drawable.blue_button));
                            //v.setBackgroundColor(v.getContext().getResources().getColor(R.color.variant_color));
                            mSelectedChoices.put(sKey, sVal);
                            CommonVariables.selectedProduct.selectedVariants = mSelectedChoices;

                            txtQty.setText("1");

                            setVariantPrice(sKey, sVal);


//                            if (mProduct.variant_pricing) {
//                                if (mProduct.variant_pricing_attribute.equals(sKey)) {
//                                    mVariantPrice = (float) mProduct.variant_price_map.get(sVal.trim());
//
//                                    if (mProduct.variant_mrp_map != null) {
//                                        if (!mProduct.variant_mrp_map.isEmpty()) {
//                                            mVariantMrp = (float) mProduct.variant_mrp_map.get(sVal.trim());
//                                        } else {
//                                            mVariantMrp = mProduct.MRP;
//                                        }
//                                    } else {
//                                        mVariantMrp = mProduct.MRP;
//                                    }
//
//                                    String sOfferPrice;
//                                    if (mVariantPrice > 0) {
//                                        sOfferPrice = CommonVariables.rupeeSymbol + CommonMethods.formatCurrency(mVariantPrice);
//                                    } else {
//                                        sOfferPrice = CommonVariables.rupeeSymbol + CommonMethods.formatCurrency(mProduct.Offer_Price);
//                                    }
//                                    setOfferPrice((sOfferPrice));
//                                    txtMRP.setText("₹" + CommonMethods.formatCurrency(mVariantMrp));
//                                    // txtOfferPrice.setText((sOfferPrice));
//
//
//                                    if (mProduct.variant_mrp_map != null) {
//                                        if (!mProduct.variant_mrp_map.isEmpty()) {
//                                            float iTotalSavings = mVariantMrp - mVariantPrice;
//                                            double savingInPercent = (Double.parseDouble(String.valueOf(iTotalSavings)) / mVariantMrp) * 100;
//                                            int iSavingInPercent = (int) Math.round(savingInPercent);
//                                            String sDiscount = Integer.toString(iSavingInPercent) + "% OFF";
//                                            btnDiscount.setText(sDiscount);
//                                            txtSavings.setText("₹" + String.format("%.2f", iTotalSavings) + " (" + iSavingInPercent + "%)");
//                                        }
//                                    }
//
//
//                                }
//                            }


                            // v.setBackground(Drawable.);
                        }
                    });
                }

                mButtonAndVariants.put(key, lstButtons);

                //sTechicalDetails += "<b>" + key + ":  " + "</b>" + value + "<br/><brV/>";
            }
            CommonVariables.selectedProduct.selectedVariants = mSelectedChoices;
        }

        if (mProduct.qty_discounts != null) {

            TextView txtQtyDiscounts = (TextView) mView.findViewById(R.id.txtQtyDiscounts);
            txtQtyDiscounts.setVisibility(View.GONE);
            LinearLayout layoutQtyDiscount = (LinearLayout) mView.findViewById(R.id.layoutQtyDiscount);
            HorizontalScrollView scrollView = new HorizontalScrollView(mView.getContext());
            LinearLayout.LayoutParams scrollLayoutParms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 80.0f);
            layoutQtyDiscount.addView(scrollView);

            LinearLayout layout = new LinearLayout(mView.getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            scrollView.addView(layout, layoutParams);
//            TextView textView = new TextView(mView.getContext());
//            //  LinearLayout.LayoutParams lp0 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            GridView.LayoutParams lp0 = new GridView.LayoutParams(150, 65);
//            textView.setText("Discounts: ");
//            //textView.setLayoutParams(new GridView.LayoutParams(65, 65));
//            layout.addView(textView, lp0);

            int index = 0;
            if (mProduct.qty_discount_in_percent != null) {
                for (Map.Entry<String, Integer> entry : mProduct.qty_discount_in_percent.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue().toString() + " % Off";

                    List<Button> lstButtons = new ArrayList<>();
                    Button btn = new Button(mView.getContext());
                    btn.setBackground(btn.getContext().getResources().getDrawable(R.drawable.orange_button));
                    btn.setText(key + "+" + "\n\n" + value);

                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(3, 3, 3, 3);
                    layout.addView(btn, lp);
                    lstButtons.add(btn);


                    mButtonAndDiscounts.put(key, lstButtons);
                }
            }
        }

        LinearLayout layoutHideForShop = (LinearLayout) mView.findViewById(R.id.layouthideForShop);
//        if(CommonVariables.showProductsFromShop) {
//            layoutHideForShop.setVisibility(View.GONE);
//        }

        if (mProduct.variable_weight_policy != null) {
            Log.d("weight_policy", "weight_policy");
            txtVariableWeight.setText(mProduct.variable_weight_policy);
        } else {
            relativeLayoutWeightPolicy.setVisibility(View.GONE);
        }

        if (mProduct.disclaimer != null) {
            txtDisclaimer.setText(mProduct.disclaimer);
        } else {
            relativeLayoutDisclaimer.setVisibility(View.GONE);
        }

        TextView txtDeliverTo = (TextView) mView.findViewById(R.id.txtCustomer);
        if (CommonVariables.loggedInUserDetails != null) {
            String sText = "Deliver to " + CommonVariables.loggedInUserDetails.Name + " - " + CommonVariables.loggedInUserDetails.City + "(" + CommonVariables.loggedInUserDetails.Pincode + ")";
            txtDeliverTo.setText(sText);
        } else {
            txtDeliverTo.setText("Hello Guest!");
        }


        TextView txtBrand = (TextView) mView.findViewById(R.id.txtBrand);
        txtBrand.setText(mProduct.Brand);

        TextView txtProductTitle = (TextView) mView.findViewById(R.id.txtTitle_ProductDetail);
        txtProductTitle.setText((mProduct.Title));

        //loadCarouselImages();
        mImageUriList.clear();
        loadImageList();
        ImageAdapter adapterView = new ImageAdapter(mView.getContext(), mImageUriList);
        mViewPager.setAdapter(adapterView);
        dot1.setViewPager(mViewPager);
        TextView txtLoading = (TextView) mView.findViewById(R.id.txtLoadingImages);
        txtLoading.setVisibility(View.GONE);

        RatingBar ratingBar = (RatingBar) mView.findViewById(R.id.ratingBar_ProductDetail);
        ratingBar.setRating(mProduct.Avg_Rating);


        String sOfferPrice;
        if (mVariantPrice > 0) {
            sOfferPrice = CommonVariables.rupeeSymbol + CommonMethods.formatCurrency(mVariantPrice);
        } else {
            sOfferPrice = CommonVariables.rupeeSymbol + CommonMethods.formatCurrency(mProduct.Offer_Price);
        }

        setOfferPrice((sOfferPrice));
        // txtOfferPrice.setText(sOfferPrice);

        String sMRP = CommonVariables.rupeeSymbol + CommonMethods.formatCurrency(mProduct.MRP);
        txtMRP.setText(sMRP);
        txtMRP.setPaintFlags(txtMRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        TextView txtCountryOfOrigin = (TextView) mView.findViewById(R.id.txtcoo);
        txtCountryOfOrigin.setText(mProduct.CountryOfOrigin);

        TextView txtReturnWindow = (TextView) mView.findViewById(R.id.txtReturnWindow);
        String sReturnWindow = Integer.toString(mProduct.returning_window) + " days";
        if (mProduct.returning_window == 0) {
            sReturnWindow = "Non Returnable";
        }
        txtReturnWindow.setText(sReturnWindow);

//        if (TextUtils.isEmpty(mProduct.ExpiryDate)) {
//            LinearLayout layoutExp = (LinearLayout) mView.findViewById(R.id.layoutExpDate);
//            layoutExp.setVisibility(View.GONE);
//        } else {
//            TextView txtExpDate = (TextView) mView.findViewById(R.id.txtExpDate);
//            txtExpDate.setText(mProduct.ExpiryDate);
//        }




        float iTotalSavings = mProduct.MRP - mProduct.Offer_Price;
        Double dTotalSavings = new Double(iTotalSavings);
        Double dMRP = new Double(mProduct.MRP);
        double savingInPercent = (dTotalSavings / dMRP) * 100;
        int iSavingInPercent = (int) Math.round(savingInPercent);
        String sTotalSavings = "₹" + String.format("%.2f", iTotalSavings) + " (" + Integer.toString(iSavingInPercent) + "%)";
        txtSavings.setText(sTotalSavings);


        if (iSavingInPercent > 0) {

            String sDiscount = Integer.toString(iSavingInPercent) + "%\nOFF";
            btnDiscount.setText(sDiscount);
        } else {
            btnDiscount.setVisibility(View.GONE);
        }

        TextView txtCODAvailable = (TextView) mView.findViewById(R.id.txtCODAvailable_ProductDetail);
        String sCOD = mProduct.COD ? "Yes" : "No";
        txtCODAvailable.setText(sCOD);

        TextView txtSoldBy = (TextView) mView.findViewById(R.id.txtSoldBy);
        txtSoldBy.setText(mProduct.SoldBy);

        TextView txtProductId = (TextView) mView.findViewById(R.id.txtProductId);
        txtProductId.setText(mProduct.Product_Id);


        btnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CommonVariables.loggedInUserDetails == null) {
                    showPopup();
                    return;
                }

                String sQty = txtQty.getText().toString();
                int iQty = Integer.parseInt(sQty);

                if (iQty == 5) {
                    Toast.makeText(getContext(), "You cannot add more Quantities for this item", Toast.LENGTH_LONG).show();
                    return;
                }

                iQty++;
                double price = CommonMethods.getOfferPrice(iQty, mProduct);
                String sOfferPrice = CommonVariables.rupeeSymbol + CommonMethods.formatCurrency(price);
                setOfferPrice(sOfferPrice);
                //txtOfferPrice.setText(sOfferPrice);
                CommonVariables.selectedCart.Qty = iQty;

                txtQty.setText(Integer.toString(iQty));

            }
        });

        btnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CommonVariables.loggedInUserDetails == null) {
                    showPopup();
                    return;
                }

                if (txtQty.getText().toString().equals("1")) {
                    Toast.makeText(getContext(), "Quantity cannot be less than 1", Toast.LENGTH_SHORT).show();
                } else {
                    String sQty = txtQty.getText().toString();
                    int iQty = Integer.parseInt(sQty);
                    iQty--;
                    CommonVariables.selectedCart.Qty = Integer.parseInt(sQty);
                    double price = CommonMethods.getOfferPrice(iQty, mProduct);
                    String sOfferPrice = CommonVariables.rupeeSymbol + CommonMethods.formatCurrency(price);
                    setOfferPrice(sOfferPrice);
                    // txtOfferPrice.setText(sOfferPrice);


                    txtQty.setText(Integer.toString(iQty));
                }

            }
        });

        CommonVariables.selectedCart = new

                Cart();

        CommonVariables.selectedCart.product = mProduct;
        String sQty = txtQty.getText().toString();
        CommonVariables.selectedCart.Qty = Integer.parseInt(sQty);
        CommonVariables.selectedCart.Variants = mSelectedChoices;

        TextView txtBulletPoints = (TextView) mView.findViewById(R.id.txtBulletPoints);
        String sBulletPoints = "";
        for (
                String s : mProduct.bullet_points) {

            sBulletPoints += "\u2022 " + s + "\n\n";
        }

        txtBulletPoints.setText(sBulletPoints.trim());

        TextView txtProductDetails = (TextView) mView.findViewById(R.id.txtproductDetails);
        txtProductDetails.setText(mProduct.Description);

        TextView txtTechnicalDetails = (TextView) mView.findViewById(R.id.txtFeatures);
        String sTechicalDetails = "";
        for (
                Map.Entry<String, String> entry : mProduct.Features.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sTechicalDetails += "\n" + key + ":  " + "</b>" + value + "\n\n";
        }
        txtTechnicalDetails.setText((Html.fromHtml(sTechicalDetails.trim())));

        Button btnReviews = (Button) mView.findViewById(R.id.btnReviewComments);
        btnReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CommonVariables.loggedInUserDetails == null) {
                    showPopup();
                    return;
                }

                Fragment newFragment = ShowReviewsFragment.newInstance(mProduct);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                transaction.replace(R.id.nav_host_fragment, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        Button btnBuyNow = (Button) mView.findViewById(R.id.btnBuyNow);
        btnBuyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonVariables.loggedInUserDetails == null) {
                    showPopup();
                    return;
                }
                ((LandingPage) getActivity()).onAddToCart_Click(null, null, true);//
                //      CommonVariables.cartlist.clear();
//                Cart obCart = new Cart();
//                obCart.product = mProduct;
//                String sQty = txtQty.getText().toString();
//                int iQty = Integer.parseInt(sQty);
//                obCart.Qty = iQty;
//                obCart.Variants = mProduct.selectedVariants;
//                CommonVariables.cartlist.add(obCart);
//
//                CommonVariables.buyNowOption = true;
//                Fragment fragmentCart = new CartFragmentNav();
//                FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                transaction.replace(R.id.nav_host_fragment, fragmentCart);
//                transaction.addToBackStack(null);
//                transaction.commit();
            }
        });


//        Button btnNearbyShops = (Button)mView.findViewById(R.id.btnNearbyShops);
//        btnNearbyShops.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Fragment newFragment = ShopFragment.newInstance();
//                FragmentTransaction transaction = getFragmentManager().beginTransaction();
//
////                transaction.hide(this);
////                transaction.add(R.id.nav_host_fragment, newFragment);
//
//                transaction.replace(R.id.nav_host_fragment, newFragment);
//                transaction.addToBackStack(null);
//                transaction.commit();
//            }
//        });


    }


    private void showPopup() {
        Intent intent = new Intent(getActivity(), Popup.class);
        startActivity(intent);
    }


    private void loadImageList() {

        if (!TextUtils.isEmpty((mProduct.ImageUrlCover))) {
            mImageUriList.add(Uri.parse(mProduct.ImageUrlCover));
        }

        if (!TextUtils.isEmpty((mProduct.ImageUrlImage1))) {
            mImageUriList.add(Uri.parse(mProduct.ImageUrlImage1));
        }

        if (!TextUtils.isEmpty((mProduct.ImageUrlImage2))) {
            mImageUriList.add(Uri.parse(mProduct.ImageUrlImage2));
        }

        if (!TextUtils.isEmpty((mProduct.ImageUrlImage3))) {
            mImageUriList.add(Uri.parse(mProduct.ImageUrlImage3));
        }

        if (!TextUtils.isEmpty((mProduct.ImageUrlImage4))) {
            mImageUriList.add(Uri.parse(mProduct.ImageUrlImage4));
        }

        if (!TextUtils.isEmpty((mProduct.ImageUrlImage5))) {
            mImageUriList.add(Uri.parse(mProduct.ImageUrlImage5));
        }

    }

    private void setOfferPrice(String sOfferPrice) {
        txtOfferPrice.setText(sOfferPrice); // + " per " + mProduct.UOM);
    }

    public void setVariantPrice(String sKey, String sVal){
        if (mProduct.variant_pricing) {
            if (mProduct.variant_pricing_attribute.equals(sKey)) {
                mVariantPrice = (float) mProduct.variant_price_map.get(sVal.trim());

                if (mProduct.variant_mrp_map != null) {
                    if (!mProduct.variant_mrp_map.isEmpty()) {
                        mVariantMrp = (float) mProduct.variant_mrp_map.get(sVal.trim());
                    } else {
                        mVariantMrp = mProduct.MRP;
                    }
                } else {
                    mVariantMrp = mProduct.MRP;
                }

                String sOfferPrice;
                if (mVariantPrice > 0) {
                    sOfferPrice = CommonVariables.rupeeSymbol + CommonMethods.formatCurrency(mVariantPrice);
                } else {
                    sOfferPrice = CommonVariables.rupeeSymbol + CommonMethods.formatCurrency(mProduct.Offer_Price);
                }
                setOfferPrice((sOfferPrice));
                txtMRP.setText("₹" + CommonMethods.formatCurrency(mVariantMrp));
                // txtOfferPrice.setText((sOfferPrice));


                if (mProduct.variant_mrp_map != null) {
                    if (!mProduct.variant_mrp_map.isEmpty()) {
                        float iTotalSavings = mVariantMrp - mVariantPrice;
                        double savingInPercent = (Double.parseDouble(String.valueOf(iTotalSavings)) / mVariantMrp) * 100;
                        int iSavingInPercent = (int) Math.round(savingInPercent);
                        String sDiscount = Integer.toString(iSavingInPercent) + "% OFF";
                        btnDiscount.setText(sDiscount);
                        txtSavings.setText("₹" + String.format("%.2f", iTotalSavings) + " (" + iSavingInPercent + "%)");
                    }
                }


            }
        }
    }


}
