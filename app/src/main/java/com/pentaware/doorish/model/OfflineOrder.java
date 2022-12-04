package com.pentaware.doorish.model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class OfflineOrder implements Serializable {

    ArrayList<OfflineProduct> offlineProducts;
    String customer_id;
    String order_id;
    Boolean offline_order;
    Date order_date;

    public Date getOrder_date() {
        return order_date;
    }

    public OfflineOrder() {}

    public String getCustomer_id() {
        return customer_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public Boolean getOffline_order() {
        return offline_order;
    }
}
