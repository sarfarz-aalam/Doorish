package com.pentaware.doorish.model;


import java.io.Serializable;
import java.util.HashMap;

public class Cart implements Serializable {

    public Product product;

    public String CartId;

    public int Qty;

    public HashMap<String, String>Variants = new HashMap<>();
}
