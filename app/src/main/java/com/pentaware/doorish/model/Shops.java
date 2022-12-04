package com.pentaware.doorish.model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Shops implements Serializable {

    public String Name;

    public String AddressLine1;

    public String AddressLine2;

    public String AddressLine3;

    public String city;

    public String Pincode;

    public String Landmark;

    public String Phone;

    public float Avg_Rating;

    public List<String>Products = new ArrayList<>();

    public Map<String, Object> product_price_map = new HashMap<>();

    public String ProductName;

    public float ProductPrice;

    public String seller_id;
}
