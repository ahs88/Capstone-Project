package shopon.com.shopon.db;

import android.content.Context;
import android.text.TextUtils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import shopon.com.shopon.datamodel.customer.CustomerData;
import shopon.com.shopon.datamodel.customer.Customers;
import shopon.com.shopon.datamodel.customer.CustomersRealm;
import shopon.com.shopon.preferences.UserSharedPreferences;
import shopon.com.shopon.view.constants.Constants;


/**
 * Created by Akshath on 14-11-2016.
 */

public class CustomerRealmUtil {
    public static Customers convertRealmCustomerToCustomers(CustomersRealm customersRealm){
        Customers customers = new Customers();
        customers.setEmail(customersRealm.getEmail());
        customers.setId(customersRealm.getId());
        customers.setMobile(customersRealm.getMobile());
        customers.setName(customersRealm.getName());
        customers.setIntrestedIn(customersRealm.getIntrestedIn());
        return customers;
    }

    public static CustomersRealm convertRemoteCustomerToRealmCustomer(CustomersRealm customer_realm,Customers customers){
        customer_realm.setEmail(customers.getEmail());
        if(customer_realm.getId() == null || TextUtils.isEmpty(String.valueOf(customer_realm.getId()))) {
            customer_realm.setId(customers.getId());
        }
        customer_realm.setMobile(customers.getMobile());
        customer_realm.setName(customers.getName());
        customer_realm.setIntrestedIn(customers.getIntrestedIn());
        return customer_realm;
    }



    public static List<Customers> convertRealmCustomerListToCustomerList(List<CustomersRealm> customersRealms) {
        List<Customers> customersList = new ArrayList<>();
        for(int i =0;i<customersRealms.size();i++){
            customersList.add(convertRealmCustomerToCustomers(customersRealms.get(i)));
        }
        return customersList;
    }

    public static void updateCustomer(Context mContext,CustomersRealm customer_realm) {
        CustomerData customer_data = new CustomerData();
        customer_data.setRealmCustomer(customer_realm);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        UserSharedPreferences userSharedPreferences = new UserSharedPreferences(mContext);
        mDatabase.child(Constants.FIREBASE_CUSTOMER_PREFIX + Constants.FIREBASE_MERCHANT_PREFIX + (String) userSharedPreferences.getPref(Constants.MERCHANT_MSISDN_PREF)).child(String.valueOf(customer_data.getCustomers().getId())).setValue(customer_data.getCustomers());//+userSharedPreferences.getPref(Constants.MERCHANT_ID_PREF)
    }


}
