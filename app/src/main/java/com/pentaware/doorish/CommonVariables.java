package com.pentaware.doorish;


import com.pentaware.doorish.model.Address;
import com.pentaware.doorish.model.AppInfo;
import com.pentaware.doorish.model.Cart;
import com.pentaware.doorish.model.Orders;
import com.pentaware.doorish.model.Product;
import com.pentaware.doorish.model.Shops;
import com.pentaware.doorish.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommonVariables {

    /*
    Doorish razorpay
    key_id : rzp_live_z9bB9tHcFJmosu
    key secret : QnWtp2amlijQbhOxLRY6sOku
     */

//////    test mode
//    public static String razorPayKeyId = "rzp_test_2ZpqgSyZMdmdzJ";
//    public static String razorPayKeySecret = "tytTbP4Zkwx9biAwRwWoziEC";

    //live mode
    public static String razorPayKeyId = "rzp_live_z9bB9tHcFJmosu";
    public static String razorPayKeySecret = "QnWtp2amlijQbhOxLRY6sOku";

    public static String YOUTUBE_API_KEY = "AIzaSyBZG2asnd6X4rSweZMzs-D9x3eCA3XQI0Q";

    public static String MAIL_SENDER_FROM = "texpediscia@gmail.com";

    public static String MAIL_PWD = "shivyan@123";

    public static boolean buyNowOption = false;

    public static String tagToSearch = "";

    public static int PAGE_SIZE = 3;


    public static String m_sFirebaseUserId;

    public static User loggedInUserDetails;

    public static double wageHours = 8;

    public static String UserName = "";

    public static String Phone = "";

    public static String Email = "";

    public static Cart selectedCart = null;


    public static List<Cart> cartlist = new ArrayList<>();
//    public static Map<String, String> mapEarnings = new HashMap<>();
//
//    public static Map<String, String> mapAdvance = new HashMap<>();


    public static String rupeeSymbol = "₹";

    public static Address deliveryAddress = new Address();

    public static Product selectedProduct;

    //  public static boolean showProductsFromShop = false;

    public static Shops selectedShop;

    public static String selectedProductCategory = "";

    public static List<String> tagList = new ArrayList<>();

    public static List<String> brandList = new ArrayList<>();

    public static int NumberOfPointsInOneRupee = 1;

    public static double percentOfAmountCreditedIntoPoints = 0;

    public static int deliveryCharges = 39;

    public static double points_to_credit_per_rupee;

    public static List<String> slot_timings = new ArrayList<>();

    public static List<String> availability = new ArrayList<>();

    public static String referred_user_fcm;

    public static int selected_variant_index = -1;

    public static String selectedKey;

    public static String selectedValue;

    public static String selectedProductId = "";

    public static boolean startedByActivity = false;

    public static int selectedAddressByDefault = -1;

    public static int idx_carousel = 0;

    // order detail for order placed screen

    public static double itemTotalPrice = 0;
    public static double netPayable = 0;
    public static double totalSavings = 0;
    public static boolean paymentStatus = false;

    public static Orders order_detail = null;

    public static double priceTotalCart = 0;

    public static int maxWeight;

    public static int user_orders = 0;

}

