package com.pentaware.doorish.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Seller implements Serializable {

    public String account_type;

    public String account_holder_name;

    public String account_no;

    public String address_line1;

    public String address_line2;

    public String address_line3;

    public String bank_name;

    public String cheque_url;

    public String city;

    public boolean city_seller;

    public String company_name;

    public String email;

    public String ifsc;

    public String merchant_id;

    public String mobile;

    public String pan_no;

    public String pincode;

    public String seller_category;

    public String seller_id;

    public String seller_name;

    public String state;

    public String status;

    public String seller_area_pin;

    public String shop_opening_time;

    public String shop_closing_time;

    public String shop_offers;

    public boolean is_favourite;

    public List<String> wishlist_customers = new ArrayList<>();

    public String img_url;

    public String about_shop;


}
