package com.pentaware.doorish.model;

import java.util.ArrayList;
import java.util.List;

public class Coupon {
    public String coupon_name;
    public String coupon_type;
    public boolean active;
    public int coupon_disount;
    public String doc_id;
    public List<String> terms_and_conditions = new ArrayList<>();

    public Coupon() {
    }

    public Coupon(String coupon_name, String coupon_type, boolean active, int coupon_disount, String doc_id, List<String> terms_and_conditions) {
        this.coupon_name = coupon_name;
        this.coupon_type = coupon_type;
        this.active = active;
        this.coupon_disount = coupon_disount;
        this.doc_id = doc_id;
        this.terms_and_conditions = terms_and_conditions;
    }
}
