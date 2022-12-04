package com.pentaware.doorish.model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Offline_Orders implements Serializable {

    public String company_name;

    public String customer_id;

    public List<Float> gst_list = new ArrayList<>();

    public String invoice_id;

    public String merchant_id;

    public List<Float> price_list = new ArrayList<>();

    public List<String>product_names = new ArrayList<>();

    public List<Integer> product_qty = new ArrayList<>();

    public List<String>status_list = new ArrayList<>();

    public String sellerAddressLine1;

    public String sellerAddressLine2;

    public String sellerAddressLine3;

    public String sellerCity;

    public String selerPin;

    public Date timestamp;



}
