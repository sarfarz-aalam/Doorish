package com.pentaware.doorish.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Product implements Serializable {
  public String Product_Id;

  public String Title;

  public int MRP;

  public float Offer_Price;

  public float price_per_gram;
  public float price_per_gram;

  public double weight_in_gram = 0;

  public boolean buy_by_weight = false;

  public float Avg_Rating;

  public String Brand;

  public int Qty;

  public String Description;

  public HashMap<String, String>Features = new HashMap<>();

  public HashMap<String, Integer>qty_discounts = new HashMap<>();

  public HashMap<String, Integer>qty_discount_in_percent = new HashMap<>();

  public int Total_Ratings;

  public ArrayList<String>bullet_points = new ArrayList<>();

  public boolean COD = true;

  public String SoldBy;

  public double GST;

  public int stock_qty;

  public String seller_id;

  public Date timestamp;

  public ArrayList<String>Tags = new ArrayList<>();

  public String variable_weight_policy;

  public String disclaimer;

  public String ImageUrlCover;

  public String ImageUrlImage1;

  public String ImageUrlImage2;

  public String ImageUrlImage3;

  public String ImageUrlImage4;

  public String ImageUrlImage5;

  public int CartQty;

  public String CountryOfOrigin;

  public String Category;

  public String ExpiryDate;

  public boolean VariantsAvailable;

  public HashMap<String, String> Variants = new HashMap<>();

  public HashMap<String, String>selectedVariants = new HashMap<>();

  public int returning_window;

  public boolean return_requested;

  public boolean return_processed;

  public String return_reason;

  public boolean return_rejected;

  public String seller_return_remarks;

  public float return_amount;

  public String seller_name;

  public String seller_address_line1;

  public String seller_address_line2;

  public String seller_address_line3;

  public String seller_phone;

  public String seller_city;

  public String seller_state;

  public boolean selling_offline;

  public float shop_price;

  public String seller_pin;

  public Seller seller;

  public boolean replacement_requested;

  public Date replacement_request_date;

  public String replacement_status;

  public int replacement_qty;

  public boolean variant_pricing;

  public String variant_pricing_attribute;

  public HashMap<String, Float> variant_price_map = new HashMap<>();

  public HashMap<String, Float> variant_mrp_map = new HashMap<>();

  public boolean is_city_product;

  public boolean is_favourite;

  public List<String> wishlist_customers = new ArrayList<>();

  public double points_added = 0;

  public String UOM;



}
