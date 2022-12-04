package com.pentaware.doorish;

import android.content.Context;
import android.graphics.Paint;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pentaware.doorish.model.Cart;
import com.pentaware.doorish.model.Product;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterCartListing extends RecyclerView.Adapter<AdapterCartListing.ViewHolder> {

    private List<CartListing> m_lstCart;
    private Context m_Context;
    private StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();

    ICartOperatoins mCartOperatoins;
    public AdapterCartListing(List<CartListing> lstCart, Context ctx, ICartOperatoins cartOperatoins) {
        this.m_lstCart = lstCart;
        m_Context = ctx;
        mCartOperatoins = cartOperatoins;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_cart_listing, parent, false);
        return new ViewHolder(v, m_Context);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        CartListing listItem = m_lstCart.get(position);
        Cart cart = listItem.getmCart();
        Product product = cart.product;

        float variantPrice = 0;

        double offerPrice = 0;
        if(product.buy_by_weight){
            offerPrice = product.weight_in_gram * product.price_per_gram;
            String weight = String.format("%.0f", product.weight_in_gram);
            holder.txtVariants.setText("Weight: " + weight + " gram");
            holder.txtVariants.setVisibility(View.VISIBLE);
        }
        else offerPrice = holder.getOfferPrice(cart, product);

        Log.d("offer_price_weight", "cart price: " + offerPrice + product.buy_by_weight);


        String productTitle = product.Title;
        holder.txtProductTitle.setText(productTitle);


        holder.txtQty.setText(Integer.toString(cart.Qty));
        if(cart.Qty > 1){
            holder.btnDecreaseQty.setVisibility(View.VISIBLE);
        }
        else
            holder.btnDecreaseQty.setVisibility(View.GONE);

//        if(CommonVariables.buyNowOption){
//            holder.btnRemoveFromCart.setVisibility(View.GONE);
//        }else{
//            holder.btnRemoveFromCart.setVisibility(View.VISIBLE);
//        }


        String  sOfferPrice =  CommonMethods.formatCurrency(offerPrice);
        double  iTotalPrice = cart.Qty * offerPrice;

        Log.d("product_price", "cart" + String.valueOf(offerPrice));



        holder.txtOfferPrice.setText(CommonVariables.rupeeSymbol + sOfferPrice); // + " per " + cart.product.UOM);

        String sTotalPrice = CommonMethods.formatCurrency(iTotalPrice);
        holder.txtTotalPrice.setText(CommonVariables.rupeeSymbol + sTotalPrice);

        //Toast.makeText(ctx, "grid image load successful", Toast.LENGTH_SHORT).show();
        Glide.with(m_Context)
                .load(cart.product.ImageUrlCover) // the uri you got from Firebase
                .into(holder.imgView); //Your imageView variable

//        String sDir = "products/" + product.Product_Id + "/";
//        String sChildNode = sDir + "cover.jpg";
//        StorageReference pathReferenceImg1 = mStorageReference.child(sChildNode);
//        final Context ctx = m_Context;
//        pathReferenceImg1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                                                    @Override
//                                                                    public void onSuccess(Uri uri) {
//                                                                        //Toast.makeText(ctx, "grid image load successful", Toast.LENGTH_SHORT).show();
//                                                                        Glide.with(ctx)
//                                                                                .load(uri) // the uri you got from Firebase
//                                                                                .into(holder.imgView); //Your imageView variable
//                                                                    }
//                                                                }
//        ).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle any errors
//                // Toast.makeText(ctx, "Error showing image " + exception.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return m_lstCart.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtProductTitle;
        public EditText txtQty;
        public TextView txtOfferPrice;
        public TextView txtTotalPrice;
        public ImageView imgView;
        public ImageView imgRemoveFromCart;
        public Button btnAddQty;
        public Button btnDecreaseQty;
        public TextView txtVariants;

        private Context m_ctx;

        public ViewHolder(View itemView, Context ctx) {
            super(itemView);
            m_ctx = ctx;
            txtProductTitle = (TextView) itemView.findViewById(R.id.txtProductTitleCart);
//            txtProductTitle.setPaintFlags(txtProductTitle.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

            txtQty = (EditText) itemView.findViewById(R.id.txtQty_Cart);
            txtOfferPrice = (TextView) itemView.findViewById(R.id.txtOfferPrice_Cart);
            txtTotalPrice = (TextView) itemView.findViewById(R.id.txtTotalPriceCart);
            imgView = (ImageView) itemView.findViewById(R.id.imgProductCart);
            imgRemoveFromCart = (ImageView) itemView.findViewById(R.id.imgRemoveFromCart);
            txtVariants = (TextView) itemView.findViewById(R.id.txtVariants);
            imgRemoveFromCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        CartListing cartListing = m_lstCart.get(pos);
                        Cart cart = cartListing.getmCart();
                        mCartOperatoins.DoCartOperations(Enums.CartOperations.RemoveFromCart, cart, pos);
                        m_lstCart.remove(cart);
                    }


                }
            });

            btnAddQty = (Button) itemView.findViewById(R.id.btnIncrease_Cart);
            btnAddQty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        CartListing cartListing = m_lstCart.get(pos);
                        Cart cart = cartListing.getmCart();
                        mCartOperatoins.DoCartOperations(Enums.CartOperations.AddQuantity, cart, pos);

                        double offerPrice =  CommonMethods.getOfferPrice(cart.Qty, cart.product); // product.Offer_Price;
                        txtOfferPrice.setText(CommonVariables.rupeeSymbol + (int) offerPrice);
                    }
                }
            });

            btnDecreaseQty = (Button) itemView.findViewById(R.id.btnDecrease_Cart);
            btnDecreaseQty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        CartListing cartListing = m_lstCart.get(pos);
                        Cart cart = cartListing.getmCart();
                        mCartOperatoins.DoCartOperations(Enums.CartOperations.RemoveQuantity, cart, pos);
                    }
                }
            });

//            txtProductTitle.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    int pos = getAdapterPosition();
//                    if (pos != RecyclerView.NO_POSITION) {
//                        CartListing cartListing = m_lstCart.get(pos);
//                        Cart cart = cartListing.getmCart();
//
//                        mCartOperatoins.DoCartOperations(Enums.CartOperations.SelectProduct, cart);
//                    }
//
//                }
//            });

            itemView.setOnClickListener(this);


        }


        @Override
        public void onClick(View v) {


        }

        public double getOfferPrice(Cart cart, Product product) {
            double offerPrice =  CommonMethods.getOfferPrice(cart.Qty, product); // product.Offer_Price;

            String sVariants = "";
            if(cart.Variants != null){

                if(cart.Variants.size() > 0){

                    for (Map.Entry<String, String> entry : cart.Variants.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        sVariants += "<b>" + key + ":  " + "</b>" + value ;

                        if(product.variant_pricing){
                            if(product.variant_pricing_attribute.trim().equals(key.trim())){
                                offerPrice =  product.variant_price_map.get(value.trim());
                            }
                        }
                    }
                    txtVariants.setVisibility(View.VISIBLE);
                    txtVariants.setText((Html.fromHtml(sVariants)));
                }

            }
            return offerPrice;
        }
    }

}

