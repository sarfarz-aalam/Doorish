package com.pentaware.doorish;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pentaware.doorish.model.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterBestSelling extends RecyclerView.Adapter<AdapterBestSelling.ViewHolder>{


    Context ctx;
    ProductAdapterClickEvent mProductAdapterClickEvent;
    List<Product> productList;

    HashMap<String, String> mSelectedChoices = new HashMap<>();
    HashMap<String, List<Button>> mButtonAndVariants = new HashMap<>();

    float mVariantPrice = 0;

    float mVariantMrp = 0;



    public AdapterBestSelling(Context context, List<Product> productList, ProductAdapterClickEvent productAdapterClickEvent) {
       mProductAdapterClickEvent = productAdapterClickEvent;
        this.productList = productList;
        ctx = context;
    }


    @NonNull
    @Override
    public AdapterBestSelling.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_category_item, parent, false);
        return new AdapterBestSelling.ViewHolder(view, ctx);
    }


    @Override
    public void onBindViewHolder(@NonNull AdapterBestSelling.ViewHolder holder, int position) {

        Product product = productList.get(position);
        String productTitle = product.Title;
        int productMRP = product.MRP;
        float offerPrice = product.Offer_Price;

        holder.txtProductTitle.setText(productTitle);
        String sOfferPrice = CommonMethods.formatCurrency(offerPrice);
        holder.txtOfferPrice.setText(CommonVariables.rupeeSymbol + CommonMethods.formatCurrency(product.Offer_Price));

        String sMRP = CommonMethods.formatCurrency(productMRP);
        holder.txtMRP.setText("₹" + sMRP);

        CommonMethods.markTextStrikeThrough(holder.txtMRP);

        Glide.with(ctx)
                .load(product.ImageUrlCover) // the uri you got from Firebase
                .into(holder.imgView); //Your imageView variable

        if(product.VariantsAvailable){
            holder.layoutVariants.setVisibility(View.VISIBLE);
        }
        else {
            holder.layoutVariants.setVisibility(View.GONE);
        }


        if(product.VariantsAvailable){

                holder.layoutVariants.removeAllViews();
                for (Map.Entry<String, String> entry : product.Variants.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    String[] values = value.split(",");

                    NestedScrollView nestedScrollView = new NestedScrollView(holder.itemView.getContext());
                    holder.layoutVariants.addView(nestedScrollView);

                    Spinner spinner = new Spinner(holder.itemView.getContext());


                    HorizontalScrollView scrollView = new HorizontalScrollView(holder.itemView.getContext());
                    LinearLayout.LayoutParams scrollLayoutParms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    nestedScrollView.addView(scrollView);

                    LinearLayout layout = new LinearLayout(holder.itemView.getContext());
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layout.setOrientation(LinearLayout.HORIZONTAL);
                    scrollView.addView(layout, layoutParams);
                    TextView textView = new TextView(holder.itemView.getContext());
                    textView.setTextColor(ctx.getResources().getColor(R.color.black));
                    textView.setGravity(Gravity.CENTER_VERTICAL);
                    //  LinearLayout.LayoutParams lp0 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    GridView.LayoutParams lp0 = new GridView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    textView.setText(key + ": ");
                    //textView.setLayoutParams(new GridView.LayoutParams(65, 65));
                    layout.addView(textView, lp0);

                    if(product.Category.equals("Vegetables and Fruites")) {
                        Button btnBuyWeight = new Button(holder.itemView.getContext());
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
                            //btn buy weight click here
                            Toast.makeText(ctx, "click", Toast.LENGTH_SHORT).show();
                            mProductAdapterClickEvent.onBuyWeightClick(product);
                        });
                    }

                    List<Button> lstButtons = new ArrayList<>();
                    int index = 0;
                    for (String s : values) {
                        Button btn = new Button(holder.itemView.getContext());
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
                            Log.d("variant_here", "first variant" + product.selectedVariants.toString());

                        } else {
                            //  btn.setBackgroundColor(btn.getContext().getResources().getColor(R.color.light_grey));
                            btn.setBackground(btn.getContext().getResources().getDrawable(R.drawable.white_btn_bg));
                            btn.setTextColor(ctx.getResources().getColor(R.color.black));
                        }
                        index++;

                        btn.setTag(key + "|" + s);
                        btn.setText(s);
                        if (product.variant_pricing) {
                            if (product.variant_pricing_attribute.equals(key)) {
                                Log.d("variant_error", product.Product_Id + product.variant_price_map + product.variant_mrp_map + s.trim());
                                float varinatPrice = (float) product.variant_price_map.get(s.trim());
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
                                product.selectedVariants = mSelectedChoices;
                                CommonVariables.selectedProductId = product.Product_Id;
                                CommonVariables.selectedKey = key;
                                CommonVariables.selectedValue = s;
                                CommonVariables.selected_variant_index = finalIndex1;
                                //txtQty.setText("1");

                                Log.d("variant_here", product.selectedVariants.toString());


                                if (product.variant_pricing) {
                                    if (product.variant_pricing_attribute.equals(sKey)) {
                                        mVariantPrice = (float) product.variant_price_map.get(sVal.trim());
                                        if(product.variant_mrp_map != null){
                                            if(!product.variant_mrp_map.isEmpty()){
                                                mVariantMrp = (float) product.variant_mrp_map.get(sVal.trim());
                                            }
                                            else {
                                                mVariantMrp = product.MRP;
                                            }
                                        }
                                        else {
                                            mVariantMrp = product.MRP;
                                        }

                                        String sOfferPrice;
                                        if (mVariantPrice > 0) {
                                            sOfferPrice = CommonVariables.rupeeSymbol + CommonMethods.formatCurrency(mVariantPrice);
                                        } else {
                                            sOfferPrice = CommonVariables.rupeeSymbol + CommonMethods.formatCurrency(product.Offer_Price);
                                        }


                                        holder.txtOfferPrice.setText(sOfferPrice);
                                        holder.txtMRP.setText("₹" + CommonMethods.formatCurrency(mVariantMrp));
                                        CommonMethods.markTextStrikeThrough(holder.txtMRP);
                                        // txtOfferPrice.setText((sOfferPrice));

                                        if(product.variant_mrp_map != null){
                                            if(!product.variant_mrp_map.isEmpty()){
                                                float iTotalSavings = mVariantMrp - mVariantPrice;
                                                double savingInPercent = (Double.parseDouble(String.valueOf(iTotalSavings)) / mVariantMrp) * 100;
                                                int iSavingInPercent = (int) Math.round(savingInPercent);
                                                String sDiscount = Integer.toString(iSavingInPercent) + "% OFF";
                                                holder.btnDiscount.setText(sDiscount);
                                               // txtSavings.setText("Total Savings: ₹" + String.format("%.2f", iTotalSavings));
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
//                CommonVariables.selectedProduct.selectedVariants = mSelectedChoices;
//                product.selectedVariants = mSelectedChoices;
               // Log.d("variant_here", mProduct.selectedVariants.toString());
            }





    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView txtProductTitle;
        public TextView txtOfferPrice;
        public TextView txtMRP;
        public ImageView imgView;
        public LinearLayout layoutVariants;
        public Button btnAddToCart;
        public Button  btnDiscount;


        public ViewHolder(View itemView, Context ctx) {
            super(itemView);

            txtProductTitle = itemView.findViewById(R.id.txtProductTitle);
            txtOfferPrice = itemView.findViewById(R.id.txtOfferPrice);
            txtMRP = itemView.findViewById(R.id.txtMRP);
            imgView = itemView.findViewById(R.id.imgProduct);
            layoutVariants = itemView.findViewById(R.id.layoutVariants);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
            btnDiscount = itemView.findViewById(R.id.btn_discount);

            itemView.setOnClickListener(view -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                   // productList.get(pos).is_city_product = m_bProductFromLocalShop;
                    mProductAdapterClickEvent.onProductClick(productList.get(pos));
                }
            });
        }

        @Override
        public void onClick(View view) { }
    }

}





