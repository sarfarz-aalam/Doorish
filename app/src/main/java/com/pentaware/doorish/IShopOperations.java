package com.pentaware.doorish;


import com.pentaware.doorish.model.Seller;
import com.pentaware.doorish.model.Shops;

public interface IShopOperations {

    void DoShopOperations(Seller seller);

    void onHeartClick(Seller seller);

    void onPostYourRequirementClick(Seller seller);
}
