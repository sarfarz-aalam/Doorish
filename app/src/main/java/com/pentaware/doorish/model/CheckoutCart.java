package com.pentaware.doorish.model;;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CheckoutCart implements Serializable {

    public List<Product> productList = new ArrayList<>();

    public String orderId;

    public String sellerId;

}
