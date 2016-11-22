package shopon.com.shopon.remote;

import android.content.Context;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.HashMap;

import shopon.com.shopon.datamodel.customer.Customers;
import shopon.com.shopon.datamodel.offer.Offer;
import shopon.com.shopon.view.constants.Constants;
import shopon.com.shopon.viewmodel.login.preferences.UserSharedPreferences;



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
}
