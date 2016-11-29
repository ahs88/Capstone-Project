package shopon.com.shopon.view.constants;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Observable;

/**
 * Created by akshath on 7/5/2016.
 */
public class Constants {
    public static final String CURRENT_LOGIN_STATE = "LOGIN_STATE";
    public static final String EXTRAS_OTP = "EXTRAS_OTP";
    public static final String MERCHANT_OTP = "MERCHANT_OTP";
    public static final String SHOP_CATEGORY_SUBSCRIPTION_LIST = "SHOP_CATEGORY_SUBSCRIPTION_LIST";
    public static final int RETRIEVE_CATEGORY = 1002;
    public static final java.lang.String UPDATE_MERCHANT_DETAIL = "UPDATE_MERCHANT_DETAIL";
    public static final int ADD_CUSTOMER = 2000;
    public static final String SELECTED_NUMBERS = "SELECTED_NUMBERS";
    public static final String FIREBASE_MERCHANT_PREFIX = "merchant_";
    public static final String FIREBASE_CUSTOMER_PREFIX = "customer_";
    public static final String OFFER_PREFIX = "offer_";
    public static final int ADD_OFFER = 103;
    public static final int PICK_CONTACT = 105;
    public static final int ADD_CONTACTS = 107;
    public static final int PERMISSIONS_REQUEST_READ_CONTACTS = 109;
    public static final String IS_RETURNING_CUSTOMER = "IS_RETURNING_CUSTOMER";
    public static final String EXTRAS_OFFER_ID = "EXTRAS_OFFER_ID";
    public static final String SYNC_DB = "SYNC_DB";
    public static final java.lang.String EXTRAS_CUSTOMER_ID = "EXTRAS_CUSTOMER_ID";
    private static final String ARG_IS_TWO_PANE = "isTwoPane";
    public static final String EXTRAS_IS_TWO_PANE = "isTwoPane";
    public static final String EXTRAS_SCHEDULED_DATE = "schedued_date";
    public static final String EXTRAS_OFFER_TEXT = "offer text";
    public static final String EXTRAS_CUSTOMER_LIST = "customer_number_list";
    public static final String EXTRAS_CUSTOMER_NAME = "customer_name";
    public static final String EXTRAS_MOBILE = "mobile";
    public static final String EXTRAS_EMAIL = "email";
    public static final String EXTRAS_CATEGORY_LIST = "category_list";
    public static final String STATE_CHANGE = "state_change";
    public static final String CURRENT_FRAGMENT = "current_fragment";
    public static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 200;
    public static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 201;
    public static final int INTERNET_NOT_CONNECTED = -1;
    public static final int MOBILE_DATA_ENABLED = 0;
    public static final int WIFI_ENABLED = 1;

    public static final String FILTER_EQUAL_TO = "equalTo";

    public static final int  ALL_OFFERS = 500;
    public static final int SCHEDULED_OFFER = 501;
    public static final int SENT_OFFER = 502;


    public static final int ALL_CUSTOMERS = 600;


    public static int SPLASH_TIME_OUT = 1000;

    public static final int REQUEST_SUB_CATEGORY = 101;
    public static final int RETRIEVE_MSISDN = 102;

    public static final String LIST_OF_SHOPIDS = "LIST_OF_SHOPIDS";
    public static final String SUB_CATEGORY_LIST = "SUB_CATEGORY_LIST";
    public static final String SELECTED_CATEGORY_LIST = "SELECTED_CATEGORY_LIST";
    public static final String MERCHANT_MSISDN_PREF = "MERCHANT_MSISDN_PREF";
    public static final String MERCHANT_ID_PREF = "userId";

    public static final int MSISDN_STATE=0;
    public static final int OTP_STATE=1;
    public static final int PROFILE_STATE=2;
    public static final int CATEGORY_STATE=3;
    public static final int LOGIN_COMPLETE = 4;


}
