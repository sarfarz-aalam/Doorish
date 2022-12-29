package com.pentaware.doorish.model;



import java.util.Date;

public class WalletStatement {
    public double amount;
    public String details;
    public String transaction_type;
    public String order_id;

    public WalletStatement() {
    }

    public Date date;

    public WalletStatement(double amount, String details, String transaction_type, Date date, String order_id) {
        this.amount = amount;
        this.details = details;
        this.transaction_type = transaction_type;
        this.date = date;
        this.order_id = order_id;
    }
}
