package com.pentaware.doorish.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User implements Serializable {

    public String Name;

    public String Email;

    public String Phone;

    public String Gender;

    public String AddressLine1;

    public String AddressLine2;

    public String AddressLine3;

    public String Landmark;

    public String Pincode;

    public String buyer_area_pin;

    public String City;

    public String State;

    public String AddressId;

    public List<Address> AddressList = new ArrayList<>();

    public List<String> coupon_used = new ArrayList<>();

    public String fcm;

    public double points;

    public String customer_id;

    public boolean Active;

    public String status;

    public String img_url;

    public String dob;

    public String anniversary;

    public String child_birthday;

    public Date last_order_date = null;

    public String last_payment_status = null;

    public String referral_code = null;

    public String referral_customer_id;

    public String first_order_id;

    public String referee_fcm;

    public boolean referral_claimed = false;

  //  public double wallet_money = 0;

}
