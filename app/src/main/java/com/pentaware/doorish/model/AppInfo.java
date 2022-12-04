package com.pentaware.doorish.model;

;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AppInfo implements Serializable {

    public String version;

    public String apk_version;

    public boolean block_apk;

    public boolean force_update;

    public double points_to_credit_per_rupee;

    public List<String> slot_timings = new ArrayList<>();

    public List<String> availability = new ArrayList<>();

    public int delivery_charges;

    public String category_1_discount;

    public String category_2_discount;

    public String category_3_discount;

    public String category_4_discount;

    public int max_weight;

}
