package shopon.com.shopon.utils;

import io.realm.Realm;
import shopon.com.shopon.datamodel.customer.CustomersRealm;
import shopon.com.shopon.datamodel.merchant.MerchantsRealm;
import shopon.com.shopon.datamodel.offer.OfferRealm;


public class DatabaseUtils {

    public void createCustomer(String emailId, int user_id, String subscribed_categories, int merchant_id, String name, String mobile_number) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        CustomersRealm user = realm.createObject(CustomersRealm.class); // Create a new object
        user.setEmail(emailId);
        user.setId(user_id);
        user.setIntrestedIn(subscribed_categories);
        user.setMerchentId(merchant_id);
        user.setName(name);
        user.setMobile(mobile_number);
        realm.commitTransaction();
    }

    public void updateCustomer() {

    }


    public void createMerchant(String emailId, int user_id, String subscribed_categories, int merchant_id, String name, String mobile_number) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        MerchantsRealm user = realm.createObject(MerchantsRealm.class); // Create a new object
        user.setEmail(emailId);
        user.setMerchentCategory(subscribed_categories);
        user.setUserId(user_id);
        user.setName(name);
        user.setMobile(mobile_number);
        realm.commitTransaction();
    }
}
