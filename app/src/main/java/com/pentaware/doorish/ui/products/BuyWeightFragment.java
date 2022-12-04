package com.pentaware.doorish.ui.products;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.pentaware.doorish.CommonVariables;
import com.pentaware.doorish.LandingPage;
import com.pentaware.doorish.R;
import com.pentaware.doorish.model.Product;

import java.util.HashMap;


public class BuyWeightFragment extends Fragment {

    private Product mProduct;
    private TextView txtNetAmount;
    private TextView txtMaxWeight;

    public static BuyWeightFragment newInstance(Product product) {

        return new BuyWeightFragment(product);
    }

    private BuyWeightFragment(Product product) {
        mProduct = product;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_buy_weight, container, false);
        txtNetAmount = view.findViewById(R.id.txtProductCharges);
        loadUI(view);

        return view;
    }

    private void loadUI(View view) {

        TextView txtProductTitle = view.findViewById(R.id.txtProductTitle);
        ImageView imgProduct = view.findViewById(R.id.imgProduct);
        TextView txtPricePerGram = view.findViewById(R.id.txt_price_per_gram);
        EditText editTextWeight = view.findViewById(R.id.edit_text_weight);
        txtMaxWeight = view.findViewById(R.id.txt_max_weight);

        Button btnAddToCart = view.findViewById(R.id.btnAddToCart);

        Glide.with(this)
                .load(mProduct.ImageUrlCover) // the uri you got from Firebase
                .into(imgProduct); //Your imageView variable

        txtProductTitle.setText(mProduct.Title);

        int pricePerKg = (int) (mProduct.price_per_gram * 1000);

        String strMaxWeight = "Maximum weight allowed is " + CommonVariables.maxWeight + "gm OR " + CommonVariables.maxWeight/1000 + "kg";
        txtMaxWeight.setText(strMaxWeight);

        String pricePerGram = CommonVariables.rupeeSymbol + mProduct.price_per_gram + "/gm  OR  " + CommonVariables.rupeeSymbol + pricePerKg + "/kg" ;
        txtPricePerGram.setText(pricePerGram);

        editTextWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String sText = s.toString();
                if(!sText.equals("")){
                    if(Integer.parseInt(sText) > CommonVariables.maxWeight){
                        String max = "Maximum weight allowed is " + CommonVariables.maxWeight + "gm OR "  + CommonVariables.maxWeight/1000 + "kg";
                        Toast.makeText(getContext(), max, Toast.LENGTH_SHORT).show();
                        editTextWeight.setText(sText.substring(0, sText.length() - 1));
                        txtNetAmount.setText("");
                    }

                    else
                    setPrice(sText);
                }

            }
        });



        btnAddToCart.setOnClickListener(view1 -> {
            if(editTextWeight.getText().toString().equals(""))
                return;

            HashMap<String, String> mSelectedChoices = new HashMap<>();
//            mSelectedChoices.put("weight", "1");
            mProduct.selectedVariants = mSelectedChoices;
            mProduct.VariantsAvailable = false;

            CommonVariables.selectedProduct = mProduct;
            CommonVariables.selectedCart.product = mProduct;
            mProduct.buy_by_weight = true;


            mProduct.Offer_Price = Float.parseFloat(editTextWeight.getText().toString());
            if (getActivity() != null) {
                ((LandingPage) getActivity()).onAddToCart_Click(view, null, false);
                editTextWeight.setText("");
                //getActivity().finish();
            }
        });

    }

    private void setPrice(String strPrice) {
        double netAmount = Double.parseDouble(strPrice) * mProduct.price_per_gram;
        mProduct.weight_in_gram = Double.parseDouble(strPrice);
        txtNetAmount.setText(CommonVariables.rupeeSymbol + String.format("%.2f", netAmount));
    }
}