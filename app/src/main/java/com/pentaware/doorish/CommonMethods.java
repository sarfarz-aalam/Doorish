package com.pentaware.doorish;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pentaware.doorish.model.Product;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CommonMethods {

    /**
     * Get a diff between two dates
     * @param date1 the oldest date
     * @param date2 the newest date
     * @param timeUnit the unit in which you want the diff
     * @return the diff value, in the provided unit
     */
    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }

    public static String formatCurrency(int iNumber){
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        String yourFormattedString = formatter.format(iNumber);
        return yourFormattedString;
    }

    public static String formatCurrency(double iNumber){
        DecimalFormat formatter = new DecimalFormat("#,###,###.#");
        String yourFormattedString = formatter.format(iNumber);
        return yourFormattedString;
    }
    public static void markTextStrikeThrough(TextView txtView){
        txtView.setPaintFlags(txtView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

    public static Boolean copyFile(String p_sourceFile, String p_destFile)
            throws IOException {

        File sourceFile = new File(p_sourceFile);
        File destFile = new File(p_destFile);
        if(destFile.exists()){
            destFile.delete();
        }
        if (!destFile.exists()) {
            destFile.createNewFile();


            FileChannel source = null;
            FileChannel destination = null;
            try {
                source = new FileInputStream(sourceFile).getChannel();
                destination = new FileOutputStream(destFile).getChannel();
                destination.transferFrom(source, 0, source.size());
            } finally {
                if (source != null)
                    source.close();
                if (destination != null)
                    destination.close();
            }
            return true;
        }
        return false;
    }

    public static String getApplicatoinPath(Context context){
        return context.getFilesDir().getPath().toString();
    }

    public static String getImageFileName(Context ctx){
        return  getApplicatoinPath(ctx) + "/" +  CommonVariables.m_sFirebaseUserId  + ".jpg";
    }

    public static double getOfferPrice(int qty, Product mProduct){

        if(mProduct.qty_discount_in_percent == null){
            if(mProduct.variant_price_map != null){
                return getVariantPrice(mProduct);
            }
            else{
                return mProduct.Offer_Price;
            }

        }
        List<Integer> lstQty = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : mProduct.qty_discount_in_percent.entrySet()) {
            Integer offer_qty = Integer.parseInt(entry.getKey());
            lstQty.add(offer_qty);
            // Integer offer_price = entry.getValue();
        }

        Collections.sort(lstQty);
        int level = -1;

        for (Integer i: lstQty) {
            if (qty >= i) {
                level = i;

            }

        }

        if(level == -1){
            float price = getVariantPrice(mProduct);
            return price;
        }
        else{
            float price = getVariantPrice(mProduct);
            //int price = mProduct.qty_discounts.get(Integer.toString(level));
            int percent_off =  mProduct.qty_discount_in_percent.get(Integer.toString(level));
            int discount = (int) (price * percent_off) / 100;
            int finalPrice = (int) price - discount;
            return finalPrice;
            //return  Float.parseFloat(Integer.toString(price));
        }


    }

    public static double getMrpPrice(int qty, Product mProduct){
        if(mProduct.qty_discount_in_percent == null){
            if(mProduct.variant_mrp_map != null){
                return getVariantMrp(mProduct);
            }
            else{
                return mProduct.MRP;
            }

        }
        List<Integer> lstQty = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : mProduct.qty_discount_in_percent.entrySet()) {
            Integer offer_qty = Integer.parseInt(entry.getKey());
            lstQty.add(offer_qty);
            // Integer offer_price = entry.getValue();
        }

        Collections.sort(lstQty);
        int level = -1;

        for (Integer i: lstQty) {
            if (qty >= i) {
                level = i;

            }

        }

        if(level == -1){
            double price = getVariantMrp(mProduct);
            return price;
        }
        else{
            double price = getVariantMrp(mProduct);
            int percent_off =  mProduct.qty_discount_in_percent.get(Integer.toString(level));
            int discount = (int) (price * percent_off) / 100;
            int finalPrice = (int) price - discount;
            return finalPrice;
            //return  Float.parseFloat(Integer.toString(price));
        }


    }

    public static double getVariantMrp(Product product){
        Float variantMrp = 0f;
        Float mrp = (float) product.MRP;

        if(product.selectedVariants == null){
            return mrp;
        }


        for (Map.Entry<String, String> entry : product.selectedVariants.entrySet()) {
            String key = entry.getKey().trim();
            String value = entry.getValue().trim();



            if(product.variant_pricing){
                if(product.variant_pricing_attribute.equals(key)){
                    HashMap<String, Float> hashMap = product.variant_mrp_map;
                    variantMrp =  hashMap.get(value);
                }
            }

        }
        if(variantMrp != null){
            if(variantMrp > 0f){
                mrp = variantMrp;
            }
        }
        return mrp;
    }

    public static Float getVariantPrice(Product product){
        Float variantPrice = 0f;
        Float offerPrice = product.Offer_Price;

        if(product.selectedVariants == null){
            return  offerPrice;
        }

        for (Map.Entry<String, String> entry : product.selectedVariants.entrySet()) {
            String key = entry.getKey().trim();
            String value = entry.getValue().trim();


            if(product.variant_pricing){
                if(product.variant_pricing_attribute.equals(key)){
                    HashMap<String, Float> hashMap = product.variant_price_map;
                    variantPrice =  hashMap.get(value);

                }
            }

        }
        if(variantPrice != null){
            if(variantPrice > 0f){
                offerPrice = variantPrice;
            }
        }
        return offerPrice;
    }

    public static void creditPoints(double mNetPayable, Context ctx) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference washingtonRef = db.collection("users").document(CommonVariables.m_sFirebaseUserId);
        double pointsToIncrement = mNetPayable * CommonVariables.points_to_credit_per_rupee;

       // Toast.makeText(ctx, "Added points : " + pointsToIncrement, Toast.LENGTH_SHORT).show();
        washingtonRef.update("points", FieldValue.increment(pointsToIncrement));
    }

    public static long dateToTimestamp(Date date) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTime(date);
        return cal.getTimeInMillis() / 1000L;
    }


    public static void debitPoints(double mNetPayable, Context ctx) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference washingtonRef = db.collection("users").document(CommonVariables.m_sFirebaseUserId);
        double dp = (mNetPayable * CommonVariables.percentOfAmountCreditedIntoPoints) / 100;
        double pointsToDeduct = Math.ceil(dp * CommonVariables.NumberOfPointsInOneRupee);
        pointsToDeduct = -pointsToDeduct;

        washingtonRef.update("points", FieldValue.increment(pointsToDeduct));
    }

    public static void debitPoints(double Points) {
        Log.d("debit_cod", "here: " + Points);
        double pointstoDeduct = -Points;
        Log.d("debit_cod", "here :: " + Points);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference washingtonRef = db.collection("users").document(CommonVariables.m_sFirebaseUserId);
        washingtonRef.update("points", FieldValue.increment(pointstoDeduct));
    }

    public static double calculatePointsAgainstPurchase(double amount){
        //first calculate the 2.5 percent of the amount
        double points = amount * CommonVariables.points_to_credit_per_rupee;
        return points;
    }

    public static void creditPointsDirect(double points, Context ctx) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference washingtonRef = db.collection("users").document(CommonVariables.m_sFirebaseUserId);
       // Toast.makeText(ctx, "Added points : " + Double.toString(points), Toast.LENGTH_SHORT).show();
        washingtonRef.update("points", FieldValue.increment(points));
    }

    public static void debitPointsDirect(double points, Context ctx) {

        points = -points;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference washingtonRef = db.collection("users").document(CommonVariables.m_sFirebaseUserId);
        washingtonRef.update("points", FieldValue.increment(points));
    }

    public static long getEpochTime(){
        //long currentTimeMillis ()-Returns the current time in milliseconds.
        long millis = System.currentTimeMillis();

        //Divide millis by 1000 to get the number of seconds.
        long seconds = millis / 1000;

        return seconds;
    }

    public static String getMonthName(int month){
        String sMonth = "";
        switch (month){
            case 1:
                sMonth = "Jan";
                break;

            case 2:
                sMonth = "Feb";
                break;

            case 3:
                sMonth = "Mar";
                break;

            case 4:
                sMonth = "Apr";
                break;

            case 5:
                sMonth = "May";
                break;

            case 6:
                sMonth = "Jun";
                break;

            case 7:
                sMonth = "Jul";
                break;

            case 8:
                sMonth = "Aug";
                break;

            case 9:
                sMonth = "Sep";
                break;

            case 10:
                sMonth = "Oct";
                break;

            case 11:
                sMonth = "Nov";
                break;

            case 12:
                sMonth = "Dec";
                break;


        }
        return sMonth;
    }



}
