package com.pentaware.doorish.model;
import com.google.firebase.firestore.FieldValue;
import com.pentaware.doorish.CommonVariables;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Offline_Requests {

    public String customer_id;
    public String seller_id;
    public String company_name;
    public String seller_phone;
    public String seller_address_line1;
    public String seller_address_line2;
    public String seller_address_line3;
    public String seller_city;
    public String seller_state;
    public String seller_pin;

    public List<String> product_ids = new ArrayList<>();
    public List<String> product_names = new ArrayList<>();
    public List<String> product_qty = new ArrayList<>();
    public List<Float> product_prices = new ArrayList<>();
    public List<Float> product_prices_total = new ArrayList<>();
    public List<String>  available_status = new ArrayList<>();

    public String buyer_rejection_reason;
    public String seller_rejection_reason;
    public Date timestamp;
    public int status_code;
    public boolean pickup_from_store;

    public String doc_id;

    public boolean pay_by_cash;


}


