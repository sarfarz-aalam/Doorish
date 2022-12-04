package com.pentaware.doorish;

import android.os.AsyncTask;

import com.pentaware.doorish.model.CapturePayment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import org.json.JSONException;
import org.json.JSONObject;

public class CapturePaymentTask extends AsyncTask<CapturePayment, String, Void> {

    @Override
    protected Void doInBackground(CapturePayment... capturePayments) {
        CapturePayment objCapturePayment = capturePayments[0];
        capturePayment(objCapturePayment.paymentId, objCapturePayment.Amount);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    private String createRazorpayOrder(Integer amount){
        RazorpayClient client = null;
        try {
            client = new RazorpayClient(CommonVariables.razorPayKeyId, CommonVariables.razorPayKeySecret);
        } catch (RazorpayException e) {
            e.printStackTrace();
        }
        try {

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount); // amount in the smallest currency unit
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "order_rcptid_11");
            orderRequest.put("payment_capture", true);


            com.razorpay.Order  razorpayOrder = client.Orders.create(orderRequest);
            JSONObject jsonObject = new JSONObject(String.valueOf(razorpayOrder));
            String id = jsonObject.getString("id");
            return id;

        } catch (RazorpayException e) {
            // Handle Exception
            System.out.println(e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void capturePayment(String sPaymentId, int Amount){
        JSONObject options = new JSONObject();
        try {
            options.put("amount", Amount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RazorpayClient razorpayClient = null;
        try {
            razorpayClient = new RazorpayClient(CommonVariables.razorPayKeyId, CommonVariables.razorPayKeySecret);
        } catch (RazorpayException e) {
            e.printStackTrace();
        }
        try {
            razorpayClient.Payments.capture(sPaymentId, options);

        } catch (RazorpayException e) {
            e.printStackTrace();
        }
    }
}



