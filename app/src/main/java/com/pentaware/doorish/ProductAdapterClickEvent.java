package com.pentaware.doorish;

import android.view.View;

import com.pentaware.doorish.model.Product;

public interface ProductAdapterClickEvent {

    void onProductClick(Product product);

    void onNextClick();

    void onPreviousClick();

    void onHeartClick(Product product);

    void onAddToCartClick(Product product, View view);

    void onBuyWeightClick(Product product);
}