package com.pentaware.doorish;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.pentaware.doorish.model.Cart;
import com.pentaware.doorish.model.Product;
import com.pentaware.doorish.ui.products.BuyWeightFragment;
import com.pentaware.doorish.ui.products.ProductDetailFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public Context ctx;
    ProductAdapterClickEvent mProductAdapterClickEvent;
    boolean m_bProductFromLocalShop = false;
    Product mProduct;

    public TextView txtProductTitle;
    public RatingBar ratingBar;
    public TextView txtOfferPrice;
    public TextView txtMRP;
    public ImageView imgView;
    public TextView txtSavings;
    public TextView txtCOD;
    public LinearLayout layoutShop;

    public TextView txtSellerName;
    public TextView txttSellerAddressLine1;
    public TextView txttSellerAddressLine2;
    public TextView txttSellerAddressLine3;
    public TextView txttCityStatePin;
    public TextView txtSellerPhone;
    public TextView txtShopPrice;
    public TextView txtTimings;
    public TextView txtOffers;
    public TextView txtEarnPoints;

    public EditText txtQty;
    public Button btnIncrease, btnDecrease;
    public Button btnAddToCart;
    public LinearLayout layoutVariants;

    public Context m_ctx;
    public Button btnHeart, btnDiscount;

    float mVariantPrice = 0;

    float mVariantMrp = 0;


    HashMap<String, String> mSelectedChoices = new HashMap<>();
    HashMap<String, List<Button>> mButtonAndVariants = new HashMap<>();

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        m_ctx = ctx;
        txtProductTitle = (TextView) itemView.findViewById(R.id.txtProductTitle);
        ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
        txtOfferPrice = (TextView) itemView.findViewById(R.id.txtOfferPrice);
        imgView = (ImageView) itemView.findViewById(R.id.imgProduct);
        txtMRP = (TextView) itemView.findViewById(R.id.txtMRP);
        txtSavings = (TextView) itemView.findViewById(R.id.txtSavings);
        txtCOD = (TextView) itemView.findViewById(R.id.txtCOD);
        layoutShop = (LinearLayout) itemView.findViewById(R.id.layoutShop);
        txtSellerName = (TextView) itemView.findViewById(R.id.txtSellerName);
        txttSellerAddressLine1 = (TextView) itemView.findViewById(R.id.txttSellerAddressLine1);
        txttSellerAddressLine2 = (TextView) itemView.findViewById(R.id.txttSellerAddressLine2);
        txttSellerAddressLine3 = (TextView) itemView.findViewById(R.id.txttSellerAddressLine3);
        txttCityStatePin = (TextView) itemView.findViewById(R.id.txttCityStatePin);
        txtSellerPhone = (TextView) itemView.findViewById(R.id.txtSellerPhone);
        txtShopPrice = (TextView) itemView.findViewById(R.id.txtShopPrice);
        txtTimings = (TextView) itemView.findViewById(R.id.txtTimings);
        txtOffers = (TextView) itemView.findViewById(R.id.txtOfers);
        txtEarnPoints = (TextView) itemView.findViewById(R.id.txtEarnPoints);
        btnHeart = (Button) itemView.findViewById(R.id.btnHeart);
        btnDiscount = (Button) itemView.findViewById(R.id.btn_discount);

        txtQty = itemView.findViewById(R.id.txtQty_ProductDetail);
        btnIncrease = itemView.findViewById(R.id.btnIncrease);
        btnDecrease = itemView.findViewById(R.id.btnDecrease);
        layoutVariants = itemView.findViewById(R.id.layoutVariants);
        btnAddToCart = itemView.findViewById(R.id.btnAddToCart);


        btnHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    Vibrator vibe = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
                    vibe.vibrate(100);
                    Product product = mProduct;
                    if (product.is_favourite) {
                        product.is_favourite = false;
                        product.wishlist_customers.remove(CommonVariables.m_sFirebaseUserId);
                        btnHeart.setBackgroundResource(R.drawable.ic_favorite_border_24px);

                        Toast.makeText(ctx, "Product Removed From Wishlist", Toast.LENGTH_SHORT).show();
                    } else {
                        product.is_favourite = true;
                        product.wishlist_customers.add(CommonVariables.m_sFirebaseUserId);
                        btnHeart.setBackgroundResource(R.drawable.ic_favorite_24px);
                        Toast.makeText(ctx, "Product Added to Wishlist", Toast.LENGTH_SHORT).show();
                    }

                    mProductAdapterClickEvent.onHeartClick(product);
                }

            }
        });

        btnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String sQty = txtQty.getText().toString();
                int iQty = Integer.parseInt(sQty);

                if(iQty == 5){
                    Toast.makeText(ctx, "You cannot add more Quantities for this item", Toast.LENGTH_LONG).show();
                    return;
                }

                iQty++;

                Log.d("value_here", String.valueOf(iQty));

                double price = CommonMethods.getOfferPrice(iQty, mProduct);
                String sOfferPrice = CommonVariables.rupeeSymbol + CommonMethods.formatCurrency(price);
                txtOfferPrice.setText(sOfferPrice);
                CommonVariables.selectedCart.Qty = iQty;
                txtQty.setText(Integer.toString(iQty));
                btnDecrease.setVisibility(View.VISIBLE);
            }
        });

        btnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtQty.getText().toString().equals("1")){
                    Toast.makeText(ctx, "Quantity cannot be less than 1", Toast.LENGTH_SHORT).show();
                }
                else {
                    String sQty = txtQty.getText().toString();
                    int iQty = Integer.parseInt(sQty);
                    iQty--;
                    CommonVariables.selectedCart.Qty = Integer.parseInt(sQty);
                    double price = CommonMethods.getOfferPrice(iQty, mProduct);
                    String sOfferPrice = CommonVariables.rupeeSymbol + CommonMethods.formatCurrency(price);
                    txtOfferPrice.setText(sOfferPrice);
                    txtQty.setText(Integer.toString(iQty));
                }

            }
        });

        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    CommonVariables.selectedCart.Qty = Integer.parseInt(txtQty.getText().toString());
                    Log.d("variant_here", "variant: " + mProduct.selectedVariants.toString());
                    CommonVariables.selectedCart.product = mProduct;
                    mProductAdapterClickEvent.onAddToCartClick(mProduct, btnAddToCart);
                }
            }
        });


        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int pos = getAdapterPosition();
        if (pos != RecyclerView.NO_POSITION) {
            mProduct.is_city_product = m_bProductFromLocalShop;
            mProductAdapterClickEvent.onProductClick(mProduct);
        }
    }

    public void bind(Product product, Context ctx, ProductAdapterClickEvent mProductAdapterClickEvent, boolean m_bSearchLocal, boolean m_bShowFromShop) {

        this.mProductAdapterClickEvent = mProductAdapterClickEvent;
        this.ctx = ctx;
        mProduct = product;

        CommonVariables.selectedProduct = mProduct;

        txtQty.setText("1");

        CommonVariables.selectedCart = new Cart();
        String sQty = txtQty.getText().toString();
        CommonVariables.selectedCart.Qty = Integer.parseInt(sQty);


        if (product.wishlist_customers.indexOf(CommonVariables.m_sFirebaseUserId) >= 0) {
            product.is_favourite = true;
        } else {
            product.is_favourite = false;
        }

        float iTotalSavings = mProduct.MRP - mProduct.Offer_Price;
        Double dTotalSavings = new Double(iTotalSavings);
        Double dMRP = new Double(mProduct.MRP);
        double savingInPercent = ( dTotalSavings / dMRP) * 100;
        int iSavingInPercent = (int) Math.round(savingInPercent);

        if(iSavingInPercent > 0) {
            String sDiscount = Integer.toString(iSavingInPercent) + "% OFF";
            btnDiscount.setText(sDiscount);
        }
        else{
            btnDiscount.setVisibility(View.GONE);
        }

        String productTitle = product.Title;
        int productMRP = product.MRP;
        float offerPrice = product.Offer_Price;

        String sOfferPrice = CommonMethods.formatCurrency(offerPrice);
        txtOfferPrice.setText("₹" + sOfferPrice);


//        if (product.variant_price_map != null) {
//            List<Float> priceList = new ArrayList<>();
//            if (product.variant_pricing) {
//
//                for (Map.Entry<String, Float> entry : product.variant_price_map.entrySet()) {
//                    float price = entry.getValue();
//                    priceList.add(price);
//                }
//                Collections.sort(priceList);
//
//                String sMinPrice = CommonVariables.rupeeSymbol + CommonMethods.formatCurrency(priceList.get(0));
//                String sMaxPrice = CommonVariables.rupeeSymbol + CommonMethods.formatCurrency(priceList.get(priceList.size() - 1));
//
//                sOfferPrice = sMinPrice + " - " + sMaxPrice;
//                //  sOfferPrice += " per " + product.UOM;
//                txtOfferPrice.setText(sOfferPrice);
//
//            }
//        }

        txtOfferPrice.setText(CommonVariables.rupeeSymbol + CommonMethods.formatCurrency(product.Offer_Price));

        float saving = productMRP - offerPrice;
        boolean isCOD = product.COD;
        float rating = product.Avg_Rating;
        String coverImageUrl = product.ImageUrlCover;


        txtProductTitle.setText(productTitle);
        String sMRP = CommonMethods.formatCurrency(productMRP);
        txtMRP.setText("₹" + sMRP);

        CommonMethods.markTextStrikeThrough(txtMRP);

        //  holder.txtMRP.setPaintFlags(holder.txtMRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


        ratingBar.setRating(rating);

//        if(saving <= 0)
//        {
//            holder.txtSavings.setVisibility(View.GONE);
//
//        }

        String sSaving = CommonMethods.formatCurrency(saving);
        txtSavings.setText("Total Savings: ₹" + sSaving);

        //
        double pointsPrice = (offerPrice * CommonVariables.percentOfAmountCreditedIntoPoints) / 100;
        double points = Math.ceil(pointsPrice * CommonVariables.NumberOfPointsInOneRupee);

        String sMsg = "Buy and earn " + Double.toString(points) + " points";
        txtEarnPoints.setText(sMsg);
        String sCOD = "Yes";
        if (isCOD == false)
            sCOD = "No";
        txtCOD.setText("COD Available: " + sCOD);

        Glide.with(ctx)
                .load(product.ImageUrlCover) // the uri you got from Firebase
                .into(imgView); //Your imageView variable

        if (product.VariantsAvailable) {
            setVariantList();
        }

        if (m_bSearchLocal) {
            txtSellerName.setText(product.seller.company_name);
            txttSellerAddressLine1.setText(product.seller.address_line1);
            txttSellerAddressLine2.setText(product.seller.address_line2);
            txttSellerAddressLine3.setText(product.seller.address_line3);

            String citystatepin = "City: " + product.seller.city + " (" + product.seller.state + ") Pin: " + product.seller.pincode;
            txttCityStatePin.setText(citystatepin);
            txtSellerPhone.setText("Contact No: " + product.seller.mobile);
            txtSellerPhone.setVisibility(View.GONE);
            txtShopPrice.setText("Price At Shop: " + product.shop_price);

            String sTimings = "Shop Timings: " + product.seller.shop_opening_time + " - " + product.seller.shop_closing_time;
            txtTimings.setText(sTimings);


            if (!product.seller.shop_offers.equals("")) {
                txtOffers.setText("Offers: " + product.seller.shop_offers);
            }
        }

        if(product.VariantsAvailable){
            layoutVariants.setVisibility(View.VISIBLE);
        }
        else {
            layoutVariants.setVisibility(View.GONE);
        }

        if (m_bShowFromShop) {
            txtShopPrice.setText("Price At Shop: " + product.shop_price);
        }

        if (m_bShowFromShop || m_bSearchLocal) {
            m_bProductFromLocalShop = true;
        }

        if (product.is_favourite == false) {
            btnHeart.setBackgroundResource(R.drawable.ic_favorite_border_24px);
        } else {
            btnHeart.setBackgroundResource(R.drawable.ic_favorite_24px);
        }
    }

    private void setVariantList() {
        layoutVariants.removeAllViews();
        for (Map.Entry<String, String> entry : mProduct.Variants.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            String[] values = value.split(",");

            HorizontalScrollView scrollView = new HorizontalScrollView(itemView.getContext());
            LinearLayout.LayoutParams scrollLayoutParms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutVariants.addView(scrollView);

            LinearLayout layout = new LinearLayout(itemView.getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            scrollView.addView(layout, layoutParams);
            TextView textView = new TextView(itemView.getContext());
            textView.setTextColor(ctx.getResources().getColor(R.color.black));
            textView.setGravity(Gravity.CENTER_VERTICAL);
            //  LinearLayout.LayoutParams lp0 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            GridView.LayoutParams lp0 = new GridView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textView.setText(key + ": ");
            //textView.setLayoutParams(new GridView.LayoutParams(65, 65));
            layout.addView(textView, lp0);

            if(mProduct.Category.equals("Vegetables and Fruites")) {
                Button btnBuyWeight = new Button(itemView.getContext());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    btnBuyWeight.setTextAppearance(android.R.style.TextAppearance_DeviceDefault_Medium);
                }
                btnBuyWeight.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                btnBuyWeight.setTextSize(15);
                btnBuyWeight.setBackground(btnBuyWeight.getContext().getResources().getDrawable(R.drawable.white_btn_bg));
                btnBuyWeight.setTextColor(ctx.getResources().getColor(R.color.black));

                btnBuyWeight.setText("Buy per \n gram");
                LinearLayout.LayoutParams lpp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lpp.setMargins(5, 5, 5, 5);
                layout.addView(btnBuyWeight, lpp);

                btnBuyWeight.setOnClickListener(view -> {
                    mProductAdapterClickEvent.onBuyWeightClick(mProduct);
                });
            }

            List<Button> lstButtons = new ArrayList<>();
            int index = 0;
            for (String s : values) {
                Button btn = new Button(itemView.getContext());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    btn.setTextAppearance(android.R.style.TextAppearance_DeviceDefault_Medium);
                }
                btn.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                btn.setTextSize(15);

                if (index == 0) {
                    // btn.setBackgroundColor(btn.getContext().getResources().getColor(R.color.variant_color));
                    btn.setBackground(btn.getContext().getResources().getDrawable(R.drawable.oragne_btn_bg));
                    btn.setTextColor(ctx.getResources().getColor(R.color.white));
                    mSelectedChoices.put(key, s);
                    CommonVariables.selected_variant_index = index;
                    Log.d("variant_here", "first variant" + mProduct.selectedVariants.toString());

                } else {
                    //  btn.setBackgroundColor(btn.getContext().getResources().getColor(R.color.light_grey));
                    btn.setBackground(btn.getContext().getResources().getDrawable(R.drawable.white_btn_bg));
                    btn.setTextColor(ctx.getResources().getColor(R.color.black));
                }
                index++;

                btn.setTag(key + "|" + s);
                btn.setText(s);
                if (mProduct.variant_pricing) {
                    if (mProduct.variant_pricing_attribute.equals(key)) {
                        Log.d("variant_error", mProduct.Product_Id + mProduct.variant_price_map + mProduct.variant_mrp_map + s.trim());
                        float varinatPrice = (float) mProduct.variant_price_map.get(s.trim());
                        String sFinalData = s + "\n " + CommonVariables.rupeeSymbol + Float.toString(varinatPrice);
                        btn.setText(sFinalData);
                    }
                }

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(5, 5, 5, 5);
                layout.addView(btn, lp);
                lstButtons.add(btn);


                int finalIndex1 = index;
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
                            btn.setBackground(btn.getContext().getResources().getDrawable(R.drawable.white_btn_bg));
                            btn.setTextColor(ctx.getResources().getColor(R.color.black));
                        }

                        //   btn.setBackground(btn.getContext().getResources().getDrawable(R.drawable.orange_button));
                        v.setBackground(v.getContext().getResources().getDrawable(R.drawable.oragne_btn_bg));
                        btn.setTextColor(ctx.getResources().getColor(R.color.white));
                        //v.setBackgroundColor(v.getContext().getResources().getColor(R.color.variant_color));
                        mSelectedChoices.put(sKey, sVal);
                        CommonVariables.selectedProduct.selectedVariants = mSelectedChoices;
                        mProduct.selectedVariants = mSelectedChoices;
                        CommonVariables.selectedProductId = mProduct.Product_Id;
                        CommonVariables.selectedKey = key;
                        CommonVariables.selectedValue = s;
                        CommonVariables.selected_variant_index = finalIndex1;
                        txtQty.setText("1");

                        Log.d("variant_here", mProduct.selectedVariants.toString());


                        if (mProduct.variant_pricing) {
                            if (mProduct.variant_pricing_attribute.equals(sKey)) {
                                mVariantPrice = (float) mProduct.variant_price_map.get(sVal.trim());
                                if(mProduct.variant_mrp_map != null){
                                    if(!mProduct.variant_mrp_map.isEmpty()){
                                        mVariantMrp = (float) mProduct.variant_mrp_map.get(sVal.trim());
                                    }
                                    else {
                                        mVariantMrp = mProduct.MRP;
                                    }
                                }
                                else {
                                    mVariantMrp = mProduct.MRP;
                                }

                                String sOfferPrice;
                                if (mVariantPrice > 0) {
                                    sOfferPrice = CommonVariables.rupeeSymbol + CommonMethods.formatCurrency(mVariantPrice);
                                } else {
                                    sOfferPrice = CommonVariables.rupeeSymbol + CommonMethods.formatCurrency(mProduct.Offer_Price);
                                }


                                txtOfferPrice.setText(sOfferPrice);
                                txtMRP.setText("₹" + CommonMethods.formatCurrency(mVariantMrp));
                                CommonMethods.markTextStrikeThrough(txtMRP);
                                // txtOfferPrice.setText((sOfferPrice));

                                if(mProduct.variant_mrp_map != null){
                                    if(!mProduct.variant_mrp_map.isEmpty()){
                                        float iTotalSavings = mVariantMrp - mVariantPrice;
                                        double savingInPercent = (Double.parseDouble(String.valueOf(iTotalSavings)) / mVariantMrp) * 100;
                                        int iSavingInPercent = (int) Math.round(savingInPercent);
                                        String sDiscount = Integer.toString(iSavingInPercent) + "% OFF";
                                        btnDiscount.setText(sDiscount);
                                        txtSavings.setText("Total Savings: ₹" + String.format("%.2f", iTotalSavings));
                                    }
                                }

                            }
                        }


                        // v.setBackground(Drawable.);
                    }
                });
            }

            mButtonAndVariants.put(key, lstButtons);

            //sTechicalDetails += "<b>" + key + ":  " + "</b>" + value + "<br/><brV/>";

        }
        CommonVariables.selectedProduct.selectedVariants = mSelectedChoices;
        mProduct.selectedVariants = mSelectedChoices;
        Log.d("variant_here", mProduct.selectedVariants.toString());
    }
}
