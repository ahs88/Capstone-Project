package shopon.com.shopon.remote;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observer;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;
import shopon.com.shopon.datamodel.customer.Customers;
import shopon.com.shopon.datamodel.customer.CustomersRealm;
import shopon.com.shopon.datamodel.offer.Offer;
import shopon.com.shopon.datamodel.offer.OfferRealm;
import shopon.com.shopon.db.CustomerRealmUtil;
import shopon.com.shopon.db.OfferRealmUtil;
import shopon.com.shopon.preferences.UserSharedPreferences;
import shopon.com.shopon.utils.Utils;
import shopon.com.shopon.view.constants.Constants;


/**
 * Created by Akshath on 06-11-2016.
 */
public class SyncLocalDB extends AsyncTask<Void,Void,Void>{

    private final Context mContext;
    private DatabaseReference mDatabase;
    public static final String TAG = SyncLocalDB.class.getName();
    private UserSharedPreferences userSharedPreferences;
    private final Object MUTEX = new Object();
    private List<SyncInterface> observers = new ArrayList<>();

    public SyncLocalDB(Context ctx){
        mContext = ctx;
        userSharedPreferences = new UserSharedPreferences(mContext);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

    }

    @Override
    protected Void doInBackground(Void... voids) {
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d(TAG,"onDataChange snapshot:"+snapshot.getValue());
//                +mobile.getText().toString()

                //Customers customers = snapshot.child(Constants.FIREBASE_CUSTOMER_PREFIX+Constants.FIREBASE_MERCHANT_PREFIX+(String)userSharedPreferences.getPref(Constants.MERCHANT_MSISDN_PREF)).child(String.valueOf(userId)).getValue(Customers.class);


                syncCustomerDB(snapshot);

                syncOfferDB(snapshot);

                updateObservers();
                Log.d(TAG,"onDataChange snapshot:"+snapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }

        });
        return null;
    }

    private void updateObservers() {
        for (final SyncInterface obj : observers) {
            Handler mHandler = new Handler(Looper.getMainLooper());
            mHandler.post(new Runnable() {
                public void run() {
                    obj.update(Constants.SYNC_DB, null);
                }
            });
        }
    }

    private void syncOfferDB(DataSnapshot snapshot) {
        GenericTypeIndicator<HashMap<String,Offer>> offer_list = new GenericTypeIndicator<HashMap<String, Offer>>() {};
        HashMap<String,Offer> offerList = snapshot.child(Constants.OFFER_PREFIX+Constants.FIREBASE_MERCHANT_PREFIX+(String)userSharedPreferences.getPref(Constants.MERCHANT_MSISDN_PREF)).getValue(offer_list);
        Realm realm = Realm.getDefaultInstance();

        RealmResults<OfferRealm> offers = realm.where(OfferRealm.class).findAll();
        //no data in remote db hence clear local db
        if (offerList == null){
            realm.beginTransaction();
            offers.deleteAllFromRealm();
            realm.commitTransaction();
            return;
        }

        int position = 0;
        for (OfferRealm offer : offers){
            realm.beginTransaction();
            Offer offer_remote_data = offerList.get(String.valueOf(offer.getOfferId()));
            //update exsiting data(remove in remote data list after updating)
            if (offer_remote_data!=null){
                Log.d(TAG,"update exsiting data id:"+offer.getOfferId());
                offer = OfferRealmUtil.convertRemoteOfferToRealmCustomer(offer,offer_remote_data);
                realm.insertOrUpdate(offer);
                    offerList.remove(String.valueOf(offer.getOfferId()));
            }else{
                Log.d(TAG,"remove exsiting data id:"+offer.getOfferId());
                // remove from realm that is not found in remote list
                offers.deleteFromRealm(position);
            }
            realm.commitTransaction();
            position++;
        }

        //add the remaining data in remotelist to local realm db)
        for (Map.Entry<String, Offer> entry : offerList.entrySet())
        {
            try {
                realm.beginTransaction();
                Log.d("TAG", " inserting Key :" + entry.getKey() + " Value:" + entry.getValue());
                int offerId = Integer.parseInt(entry.getKey());
                Offer offer = entry.getValue();
                OfferRealm offer_local = realm.createObject(OfferRealm.class, offerId);
                OfferRealmUtil.convertRemoteOfferToRealmCustomer(offer_local, offer);
                realm.commitTransaction();
            }catch (RealmPrimaryKeyConstraintException re){
                Log.e(TAG, "failed to write key already exists:" + entry.getKey());
            }
        }
    }

    private void syncCustomerDB(DataSnapshot snapshot){//Realm realm,RealmResults<CustomersRealm> customers,HashMap<String,Customers> customerList) {

        HashMap<String,Customers> customerList = FireBaseUtils.getAllCustomers(mContext,snapshot);
        Realm realm = Realm.getDefaultInstance();

        RealmResults<CustomersRealm> customers = realm.where(CustomersRealm.class).findAll();
        //no data in remote db hence clear local db
        if(customerList == null){
            realm.beginTransaction();
            customers.deleteAllFromRealm();
            realm.commitTransaction();
            return;
        }

        int position = 0;
        for (CustomersRealm customer : customers){
            realm.beginTransaction();
            Customers customer_remote_data = customerList.get(String.valueOf(customer.getId()));
            //update exsiting data(remove in remote data list after updating)
            if (customer_remote_data!=null){
                Log.d(TAG,"update exsiting data id:"+customer.getId());

                customer = CustomerRealmUtil.convertRemoteCustomerToRealmCustomer(customer,customer_remote_data);
                realm.insertOrUpdate(customer);
                customerList.remove(String.valueOf(customer.getId()));
            }else{
                Log.d(TAG,"remove exsiting data id:"+customer.getId());
                // remove from realm that is not found in remote list
                customers.deleteFromRealm(position);
            }
            realm.commitTransaction();
            position++;
        }

        //add the remaining data in remotelist to local realm db)
        for (Map.Entry<String, Customers> entry : customerList.entrySet())
        {
            try {
                realm.beginTransaction();
                Log.d("TAG", " inserting Key :" + entry.getKey() + " Value:" + entry.getValue());
                int customerId = Integer.parseInt(entry.getKey());
                Customers customer = entry.getValue();
                CustomersRealm customer_local = realm.createObject(CustomersRealm.class, customerId);
                CustomerRealmUtil.convertRemoteCustomerToRealmCustomer(customer_local, customer);
                realm.commitTransaction();
            }catch (RealmPrimaryKeyConstraintException re){
                Log.e(TAG,"failed to write key already exists:"+entry.getKey());
            }
        }
    }

    public void unregister (SyncInterface obj) {
        synchronized (MUTEX) {
            for(int i=0;i<observers.size();i++)
                Log.i(TAG,"unregister : Object Name is :" + observers.get(i).getClass().getSimpleName()+" obj:"+obj);
            Log.i(TAG,"unregister :Before remove :size is :" + observers.size());
            observers.remove (obj);
           // observerMap.remove(obj);
            Log.i(TAG,"unregister :After remove :size is :" + observers.size());
        }
    }

    public void register (SyncInterface obj) {
        Log.i(TAG,"register : Object Name is :" + obj.getClass().getSimpleName()+" obj:"+obj);
        if(obj == null) throw new NullPointerException("Null Observer");
        synchronized (MUTEX) {
            if(!observers.contains(obj)) {
                observers.add(obj);
                //observerMap.put(obj, true);
            }

            Log.i(TAG,"register : Object Name is : Size is :" + obj.getClass().getSimpleName() + "," + observers.size());
        }
    }

}
