package com.pentaware.doorish;

import android.content.Context;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.internal.service.Common;
import com.pentaware.doorish.model.Product;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterProductListing extends RecyclerView.Adapter<AdapterProductListing.ViewHolder> {

    private List<ProductListing> m_lstProducts;
    private Context m_Context;
    private StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();

    ProductAdapterClickEvent mProductAdapterClickEvent;

    boolean m_bSearchLocal = false;
    boolean m_bShowFromShop = false;
    int m_iPageIndex = 0;
    public boolean isLastPage = false;
    boolean m_bProductFromLocalShop = false;

    public AdapterProductListing(List<ProductListing> lstProducts, Context ctx, ProductAdapterClickEvent ev, boolean bSearchLocal, boolean showFromShop, int iPageIndex) {
        this.m_lstProducts = lstProducts;
        m_Context = ctx;
        mProductAdapterClickEvent = ev;
        m_bSearchLocal = bSearchLocal;
        m_bShowFromShop = showFromShop;
        m_iPageIndex = iPageIndex;

    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_product_listing, parent, false);
//        return new ViewHolder(v, m_Context);
//
        View itemView;
        boolean lastItem = false;

        if(viewType == R.layout.activity_product_listing){
            lastItem = false;
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_product_listing, parent, false);
        }

        else {
            lastItem = true;
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_prev_next, parent, false);
        }

        return new ViewHolder(itemView, m_Context, lastItem);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {


        if((m_lstProducts.size() == CommonVariables.PAGE_SIZE || m_iPageIndex > 0) && position == m_lstProducts.size()){
            return;
        }

        ProductListing listItem = m_lstProducts.get(position);
        final Product product = listItem.getProduct();

        if(product.wishlist_customers.indexOf(CommonVariables.m_sFirebaseUserId) >= 0){
            product.is_favourite = true;
        }else{
            product.is_favourite = false;
        }
        String productTitle = product.Title;
        int productMRP = product.MRP;
        float offerPrice = product.Offer_Price;

        String sOfferPrice = CommonMethods.formatCurrency(offerPrice);
        holder.txtOfferPrice.setText("₹" + sOfferPrice);



        if(product.variant_price_map != null) {
            List<Float> priceList = new ArrayList<>();
            if (product.variant_pricing) {

                for (Map.Entry<String, Float> entry : product.variant_price_map.entrySet()) {
                    float price = entry.getValue();
                    priceList.add(price);
                }
                Collections.sort(priceList);

                String sMinPrice = CommonVariables.rupeeSymbol + CommonMethods.formatCurrency(priceList.get(0));
                String sMaxPrice = CommonVariables.rupeeSymbol + CommonMethods.formatCurrency(priceList.get(priceList.size() - 1));

                sOfferPrice = sMinPrice + " - " + sMaxPrice;
              //  sOfferPrice += " per " + product.UOM;
                holder.txtOfferPrice.setText(sOfferPrice);

            }
        }

        float saving = productMRP - offerPrice;
        boolean isCOD = product.COD;
        float rating = product.Avg_Rating;
        String coverImageUrl = product.ImageUrlCover;


        holder.txtProductTitle.setText(productTitle);
        String sMRP = CommonMethods.formatCurrency(productMRP);
        holder.txtMRP.setText( "₹" +  sMRP);

        CommonMethods.markTextStrikeThrough(holder.txtMRP);

        //  holder.txtMRP.setPaintFlags(holder.txtMRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


        holder.ratingBar.setRating(rating);

//        if(saving <= 0)
//        {
//            holder.txtSavings.setVisibility(View.GONE);
//
//        }

        String sSaving = CommonMethods.formatCurrency(saving);
        holder.txtSavings.setText("Total Savings: ₹" +  sSaving);

        //
        double pointsPrice = (offerPrice * CommonVariables.percentOfAmountCreditedIntoPoints) / 100;
        double points =  Math.ceil(pointsPrice * CommonVariables.NumberOfPointsInOneRupee);

        String sMsg = "Buy and earn " + Double.toString(points)  +" points";
        holder.txtEarnPoints.setText(sMsg);
        String sCOD = "Yes";
        if (isCOD == false)
            sCOD = "No";
        holder.txtCOD.setText("COD Available: " +  sCOD);

        Glide.with(m_Context)
                .load(product.ImageUrlCover) // the uri you got from Firebase
                .into(holder.imgView); //Your imageView variable

        if(m_bSearchLocal){
            holder.txtSellerName.setText(product.seller.company_name);
            holder.txttSellerAddressLine1.setText(product.seller.address_line1);
            holder.txttSellerAddressLine2.setText(product.seller.address_line2);
            holder.txttSellerAddressLine3.setText(product.seller.address_line3);

            String citystatepin = "City: " +  product.seller.city + " (" + product.seller.state + ") Pin: " + product.seller.pincode;
            holder.txttCityStatePin.setText(citystatepin);
            holder.txtSellerPhone.setText("Contact No: " + product.seller.mobile);
            holder.txtSellerPhone.setVisibility(View.GONE);
            holder.txtShopPrice.setText("Price At Shop: "  + product.shop_price);

            String sTimings = "Shop Timings: " + product.seller.shop_opening_time + " - " + product.seller.shop_closing_time;
            holder.txtTimings.setText(sTimings);




            if(!product.seller.shop_offers.equals("")){
                holder.txtOffers.setText("Offers: " + product.seller.shop_offers);
            }
        }

        if(m_bShowFromShop){
            holder.txtShopPrice.setText("Price At Shop: "  + product.shop_price);
        }

        if(m_bShowFromShop || m_bSearchLocal){
            m_bProductFromLocalShop = true;
        }

        if(product.is_favourite == false)
        {
            holder.btnHeart.setBackgroundResource(R.drawable.ic_favorite_border_24px);
        }
        else{
            holder.btnHeart.setBackgroundResource(R.drawable.ic_favorite_24px);
        }


    }

    @Override
    public int getItemCount() {
        if(m_lstProducts.size() == CommonVariables.PAGE_SIZE || m_iPageIndex > 0){
            return m_lstProducts.size() + 1;
        }
        else{
            return m_lstProducts.size();
        }

    }

    @Override
    public int getItemViewType(int position) {
        if(m_lstProducts.size() == CommonVariables.PAGE_SIZE || m_iPageIndex > 0){
            return (position == m_lstProducts.size()) ? R.layout.activity_prev_next : R.layout.activity_product_listing;

        }
        else{

            return R.layout.activity_product_listing;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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

        public Button btnNext;

        public Button btnPrevious;

        private Context m_ctx;
        private Button btnHeart;



        public ViewHolder(View itemView, final Context ctx, boolean isLast) {

            super(itemView);

            if(!isLast) {
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
                btnHeart = (Button)itemView.findViewById(R.id.btnHeart);

                btnHeart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION){
                            Vibrator vibe = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
                            vibe.vibrate(100);

                            ProductListing productListing = m_lstProducts.get(pos);
                            Product product = productListing.getProduct();
                                if(product.is_favourite){
                                product.is_favourite = false;
                                product.wishlist_customers.remove(CommonVariables.m_sFirebaseUserId);
                                btnHeart.setBackgroundResource(R.drawable.ic_favorite_border_24px);

                                Toast.makeText(ctx, "Product Removed From Wishlist", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                product.is_favourite = true;
                                product.wishlist_customers.add(CommonVariables.m_sFirebaseUserId);
                                btnHeart.setBackgroundResource(R.drawable.ic_favorite_24px);
                                Toast.makeText(ctx, "Product Added to Wishlist", Toast.LENGTH_SHORT).show();
                            }

                            mProductAdapterClickEvent.onHeartClick(product);
                        }

                    }
                });

                if (m_bSearchLocal) {
                    layoutShop.setVisibility(View.VISIBLE);
                    txtShopPrice.setVisibility(View.VISIBLE);

                } else if (m_bShowFromShop) {
                    txtShopPrice.setVisibility(View.VISIBLE);
                    layoutShop.setVisibility(View.GONE);
                } else {
                    layoutShop.setVisibility(View.GONE);
                    txtShopPrice.setVisibility(View.GONE);

                }

                itemView.setOnClickListener(this);
            }
            else{

                btnNext = (Button)itemView.findViewById(R.id.btnNext);
                btnPrevious = (Button)itemView.findViewById(R.id.btnPrev);
                Log.w("Page_index",  Integer.toString(m_iPageIndex));
                if(m_iPageIndex == 0){
                    btnPrevious.setVisibility(View.GONE);
                }
                else{
                    btnPrevious.setVisibility(View.VISIBLE);
                }
                if(m_lstProducts.size() < CommonVariables.PAGE_SIZE){
                    btnNext.setVisibility(View.GONE);
                }
                else{
                    btnNext.setVisibility(View.VISIBLE);
                }


                btnNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        mProductAdapterClickEvent.onNextClick();
                    }
                });

                btnPrevious.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mProductAdapterClickEvent.onPreviousClick();
                    }
                });

            }


        }



        @Override
        public void onClick(View v) {

            int pos = getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                ProductListing productListing = m_lstProducts.get(pos);
                Product product = productListing.getProduct();
                product.is_city_product = m_bProductFromLocalShop;
                mProductAdapterClickEvent.onProductClick(product);
            }
        }

    }
}

