package com.pentaware.doorish.model;


import com.google.firebase.Timestamp;
import java.io.Serializable;

public class Reviews implements Serializable {

    public String doc_id;

    public boolean active;

    public String Title;

    public String customer_id;

    public String posted_by;

    public String customer_phone;

    public String customer_email;

    public String product_img;

    public String product_title;

    public String Comment;

    public float Ratings;

    public String product_id;

    public Timestamp timestamp;
}
