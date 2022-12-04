package com.pentaware.doorish;

import android.content.Context;
import android.os.Vibrator;
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
import com.pentaware.doorish.model.Seller;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class AdapterShopListing extends RecyclerView.Adapter<AdapterShopListing.ViewHolder> {


    private List<ShopListing> m_lstShops;
    private Context m_Context;
    private StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();
    private IShopOperations mShopOperations;

    public  AdapterShopListing(List<ShopListing> lstShops, Context ctx) {
        m_lstShops = lstShops;
        m_Context = ctx;
    }

    public AdapterShopListing(List<ShopListing> lstShops, Context ctx, IShopOperations operation) {
        m_lstShops = lstShops;
        m_Context = ctx;
        mShopOperations = operation;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_shop_listing, parent, false);
        return new ViewHolder(v, m_Context);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ShopListing listItem = m_lstShops.get(position);
        Seller seller = listItem.getSeller();

        if(seller.wishlist_customers.indexOf(CommonVariables.m_sFirebaseUserId) >= 0){
            seller.is_favourite = true;
        }else{
            seller.is_favourite = false;
        }


        holder.txtShopName.setText(seller.company_name);
        holder.txtAddressLine1.setText(seller.address_line1);
        holder.txtAddressLine2.setText(seller.address_line2);
        holder.txtAddressLine3.setText(seller.address_line3);
        holder.txtPincode.setText(seller.pincode);
     //   holder.txtContactNumber.setText(seller.mobile);
        holder.txtLandmark.setVisibility(View.GONE);

        if(seller.about_shop != null) {
            if (!seller.about_shop.trim().equals("")) {
                holder.txtAboutShop.setText("About Shop: " + seller.about_shop);
            }
        }

      //  holder.txtContactNumber.setVisibility(View.GONE);
     //   holder.imgPhone.setVisibility(View.GONE);

        if(seller.shop_opening_time != null && seller.shop_closing_time != null){
            String sTimings = "Shop Timings: " + seller.shop_opening_time + " - " + seller.shop_closing_time;
            holder.txtTimings.setText(sTimings);
        }



        if(seller.shop_offers != null) {
            if (!seller.shop_offers.equals("")) {
                holder.txtOffers.setText("Offers: " + seller.shop_offers);
            }
        }

        holder.txtCity.setText(seller.city);

        if(seller.is_favourite == false)
        {
            holder.btnHeartSeller.setBackgroundResource(R.drawable.ic_favorite_border_24px);
        }
        else{
            holder.btnHeartSeller.setBackgroundResource(R.drawable.ic_favorite_24px);
        }

        if(seller.img_url != null) {
            Glide.with(m_Context)
                    .load(seller.img_url) // the uri you got from Firebase
                    .into(holder.imgShop); //Your imageView variable
        }

    }

    @Override
    public int getItemCount() {
        return m_lstShops.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView imgShop;
        public TextView txtShopName;
        public TextView txtAddressLine1;
        public TextView txtAddressLine2;
        public TextView txtAddressLine3;
        public TextView txtLandmark;
        public TextView txtPincode;
        public TextView txtProductName;
        public TextView txtCity;
        public TextView txtContactNumber;
        public TextView txtProductPrice;
        public RatingBar ratingBar;
        public LinearLayout layoutPrice;
        ImageView imgPhone;
        public TextView txtTimings;
        public TextView txtOffers;
        public Button btnRequirement;
        public TextView txtAboutShop;

       public Button btnHeartSeller;


        private Context m_ctx;

        public ViewHolder(View itemView, Context ctx) {
            super(itemView);
            m_ctx = ctx;
            imgShop = (ImageView) itemView.findViewById(R.id.imgShop);
            txtShopName = (TextView) itemView.findViewById(R.id.txtShopName);
            txtAddressLine1 = (TextView) itemView.findViewById(R.id.txtAddresLine1_Shop);
            txtAddressLine2 = (TextView) itemView.findViewById(R.id.txtAddresLine2_Shop);
            txtAddressLine3 = (TextView) itemView.findViewById(R.id.txtAddresLine3_Shop);
            txtLandmark = (TextView) itemView.findViewById(R.id.txtLandmark_Shop);
            txtPincode = (TextView) itemView.findViewById(R.id.txtPincode_Shop);

            txtCity = (TextView) itemView.findViewById(R.id.txtCity_Shop);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar_Shop);
            txtTimings = (TextView) itemView.findViewById(R.id.txtTimings);
            txtOffers = (TextView) itemView.findViewById(R.id.txtOfers);
            btnHeartSeller = (Button) itemView.findViewById(R.id.btnHeartSeller);
            btnRequirement = (Button) itemView.findViewById(R.id.btnPostYourRequirement);
            txtAboutShop = (TextView) itemView.findViewById(R.id.txtAboutShop);
            itemView.setOnClickListener(this);



            btnHeartSeller.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION){
                        Vibrator vibe = (Vibrator) m_ctx.getSystemService(Context.VIBRATOR_SERVICE);
                        vibe.vibrate(100);

                        ShopListing shopListing = m_lstShops.get(pos);
                        Seller seller = shopListing.getSeller();
                        if(seller.is_favourite){
                            seller.is_favourite = false;
                            seller.wishlist_customers.remove(CommonVariables.m_sFirebaseUserId);
                            btnHeartSeller.setBackgroundResource(R.drawable.ic_favorite_border_24px);

                            Toast.makeText(m_ctx, "Seller Removed From Favorites", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            seller.is_favourite = true;
                            seller.wishlist_customers.add(CommonVariables.m_sFirebaseUserId);
                            btnHeartSeller.setBackgroundResource(R.drawable.ic_favorite_24px);
                            Toast.makeText(m_ctx, "Seller Added to Favorites", Toast.LENGTH_SHORT).show();
                        }

                        mShopOperations.onHeartClick(seller);
                    }
                }
            });

            btnRequirement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        ShopListing shopListing = m_lstShops.get(pos);
                        Seller seller = shopListing.getSeller();
                        mShopOperations.onPostYourRequirementClick(seller);

                    }

                }
            });


        }


        @Override
        public void onClick(View v) {

            int pos = getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                ShopListing shopListing = m_lstShops.get(pos);
                Seller seller = shopListing.getSeller();
                mShopOperations.DoShopOperations(seller);

            }
        }
    }


}

