package com.pentaware.doorish.model;

import com.google.firebase.firestore.FieldValue;
import com.pentaware.doorish.CommonVariables;

import java.util.Date;

public class Feedback {
    public String feedback;
    public String feedback_response;
    public String customer_id;
    public String doc_id;
    public Boolean active;
    public String customer_name;
    public String customer_phone;
    public String customer_email;
    public Date timestamp;

    public Feedback() {
    }


    public Feedback(Feedback feedback) {
        this.feedback = feedback.feedback;
        this.customer_id = feedback.customer_id;
        this.doc_id = feedback.doc_id;
        this.active = feedback.active;
        this.customer_name = feedback.customer_name;
        this.timestamp = feedback.timestamp;
        this.customer_phone = feedback.customer_phone;
        this.customer_email = feedback.customer_email;
        this.feedback_response = feedback.feedback_response;
    }
}
