package com.pentaware.doorish.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Orders implements Serializable {

//    public List<Product> productList = new ArrayList<>();

    public boolean COD;

    public boolean offline_order;

    public String Status;

    public boolean cancelled;

    public String customer_id;

    public String fcm;

    public Date delivery_date;

    public String requested_delivery_time;

    public String requested_delivery_date;

    public boolean anytime_slot_bonus;

    public Date order_date;

    public Date confirmation_date;

    public Date dispatch_date;

    public String order_id;

    public String payment_id;

    public String cancellation_reason;

    public boolean IsDelivered = false;

    public String seller_id;

    public double wallet_money_used;

    public boolean prepaid_cancellation_processed = false;

    public String original_order_id;

    public boolean replacement_order;

    public double points_added = 0;

    public boolean pickup_from_store;

    public String replacement_order_id;

    public String pickup_timestamp;

    public boolean returned;

    public String invoice_id;

    public boolean settlement_done;

    public Date settlement_date;

    public String delivery_agent_id;

    public String pickup_rejection_reason;

    public String pickup_status;

    public double delivery_charges;

    public String Name;

    public String Email;

    public String Phone;

    public String AddressLine1;

    public String AddressLine2;

    public String AddressLine3;

    public String Landmark;

    public String Pincode;

    public String City;

    public String State;

    public double total_return_amount;

    public List<String> product_ids = new ArrayList<>();

    public List<String> product_titles = new ArrayList<>();

    //public List<String> sold_by_list = new ArrayList<>();

    public List<String> seller_ids = new ArrayList<>();

    public List<Double> product_offer_prices = new ArrayList<Double>();

    public List<Double> product_mrp_list = new ArrayList<Double>();

    public List<Double> product_gst_list = new ArrayList<Double>();

    public List<Long> product_qty_list = new ArrayList<>();

    public List<String> img_url_list = new ArrayList<>();

    public List<Boolean> replacement_requested = new ArrayList<>();

    public List<String> product_category_list = new ArrayList<>();

    public List<Integer> product_returning_window_list = new ArrayList<Integer>();

    public List<Boolean> product_return_request = new ArrayList<Boolean>();

    public List<Boolean> product_return_processed = new ArrayList<Boolean>();

    public List<Boolean> product_return_rejected = new ArrayList<Boolean>();

    public List<String> seller_return_remarks = new ArrayList<>();

    public List<String> return_reasons = new ArrayList<>();

    public List<Long> return_amount = new ArrayList<>();

    public List<String> product_status_list = new ArrayList<>();

    public List<String> cancelled_by = new ArrayList<String>();

    public List<String> cancellation_reasons = new ArrayList<String>();

    public List<String> product_variants = new ArrayList<>();

    public String referred_user_fcm = null;

    public int order_no = 0;
    //public HashMap<String, String> product_variants = new HashMap<>();
}
