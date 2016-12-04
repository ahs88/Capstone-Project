package shopon.com.shopon.remote;

import android.content.ContentValues;
import android.content.Context;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.logging.Handler;

import shopon.com.shopon.datamodel.customer.Customers;
import shopon.com.shopon.datamodel.offer.Offer;
import shopon.com.shopon.preferences.UserSharedPreferences;
import shopon.com.shopon.view.constants.Constants;




/**
 * Created by Akshath on 14-11-2016.
 */

public class FireBaseUtils {
    private static final String TAG = FireBaseUtils.class.getName();
    public static void updateOfferDataBase(Context mContext, Offer fOffer) {
        Log.d(TAG,"remote update offer_id:"+fOffer.getOfferId());
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        UserSharedPreferences userSharedPreferences = new UserSharedPreferences(mContext);
        mDatabase.child(Constants.OFFER_PREFIX+Constants.FIREBASE_MERCHANT_PREFIX+(String)userSharedPreferences.getPref(Constants.MERCHANT_MSISDN_PREF)).child(String.valueOf(fOffer.getOfferId())).setValue(fOffer);
    }

    public static void updateCustomer(Context mContext, Customers customer) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        UserSharedPreferences userSharedPreferences = new UserSharedPreferences(mContext);
        mDatabase.child(Constants.FIREBASE_CUSTOMER_PREFIX + Constants.FIREBASE_MERCHANT_PREFIX + (String) userSharedPreferences.getPref(Constants.MERCHANT_MSISDN_PREF)).child(String.valueOf(customer.getId())).setValue(customer);//+userSharedPreferences.getPref(Constants.MERCHANT_ID_PREF)
    }

    public static Offer getOfferById(Context mContext, int offerId, DataSnapshot snapshot) {
        UserSharedPreferences userSharedPreferences = new UserSharedPreferences(mContext);
        return snapshot.child(Constants.OFFER_PREFIX+Constants.FIREBASE_MERCHANT_PREFIX+(String)userSharedPreferences.getPref(Constants.MERCHANT_MSISDN_PREF)).child(String.valueOf(offerId)).getValue(Offer.class);
    }

    public static Customers getCustomerById(Context mContext, int userId, DataSnapshot snapshot) {
        UserSharedPreferences userSharedPreference = new UserSharedPreferences(mContext);
        return snapshot.child(Constants.FIREBASE_CUSTOMER_PREFIX+Constants.FIREBASE_MERCHANT_PREFIX+(String)userSharedPreference.getPref(Constants.MERCHANT_MSISDN_PREF)).child(String.valueOf(userId)).getValue(Customers.class);
    }

    public static HashMap<String,Customers> getAllCustomers(Context mContext, DataSnapshot snapshots){
        UserSharedPreferences userSharedPreferences = new UserSharedPreferences(mContext);
        GenericTypeIndicator<HashMap<String,Customers>> customer_list = new GenericTypeIndicator<HashMap<String, Customers>>() {};
        return snapshots.child(Constants.FIREBASE_CUSTOMER_PREFIX+Constants.FIREBASE_MERCHANT_PREFIX+(String)userSharedPreferences.getPref(Constants.MERCHANT_MSISDN_PREF)).getValue(customer_list);
    }

    public static HashMap<String,Offer> getAllOffer(Context mContext, DataSnapshot snapshot){
        UserSharedPreferences userSharedPreferences = new UserSharedPreferences(mContext);
        GenericTypeIndicator<HashMap<String,Offer>> offer_list = new GenericTypeIndicator<HashMap<String, Offer>>() {};
        return snapshot.child(Constants.OFFER_PREFIX+Constants.FIREBASE_MERCHANT_PREFIX+(String)userSharedPreferences.getPref(Constants.MERCHANT_MSISDN_PREF)).getValue(offer_list);
    }

}
