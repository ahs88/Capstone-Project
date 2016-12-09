package shopon.com.shopon.remote;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import android.os.RemoteException;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observer;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;
import shopon.com.shopon.ShopOn;
import shopon.com.shopon.datamodel.customer.Customers;
import shopon.com.shopon.datamodel.customer.CustomersRealm;
import shopon.com.shopon.datamodel.offer.Offer;
import shopon.com.shopon.datamodel.offer.OfferRealm;
import shopon.com.shopon.db.CustomerRealmUtil;
import shopon.com.shopon.db.OfferRealmUtil;
import shopon.com.shopon.db.provider.ShopOnContract;
import shopon.com.shopon.preferences.UserSharedPreferences;
import shopon.com.shopon.utils.Utils;
import shopon.com.shopon.view.constants.Constants;


/**
 * Created by Akshath on 06-11-2016.
 */
public class SyncLocalDB extends AsyncTask<Void, Void, Void> {

    private final Context mContext;
    private DatabaseReference mDatabase;
    public static final String TAG = SyncLocalDB.class.getName();
    private UserSharedPreferences userSharedPreferences;
    private final Object MUTEX = new Object();
    private List<SyncInterface> observers = new ArrayList<>();

    public SyncLocalDB(Context ctx) {
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
                Log.d(TAG, "onDataChange snapshot:" + snapshot.getValue());


                syncSQLCustomerDB(snapshot);

                syncSQLOfferDB(snapshot);

                updateObservers();
                Log.d(TAG, "onDataChange snapshot:" + snapshot.getValue());
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
        GenericTypeIndicator<HashMap<String, Offer>> offer_list = new GenericTypeIndicator<HashMap<String, Offer>>() {
        };
        HashMap<String, Offer> offerList = snapshot.child(Constants.OFFER_PREFIX + Constants.FIREBASE_MERCHANT_PREFIX + (String) userSharedPreferences.getPref(Constants.MERCHANT_MSISDN_PREF)).getValue(offer_list);
        Realm realm = Realm.getDefaultInstance();

        RealmResults<OfferRealm> offers = realm.where(OfferRealm.class).findAll();
        //no data in remote db hence clear local db
        if (offerList == null) {
            realm.beginTransaction();
            offers.deleteAllFromRealm();
            realm.commitTransaction();
            return;
        }

        int position = 0;
        for (OfferRealm offer : offers) {
            realm.beginTransaction();
            Offer offer_remote_data = offerList.get(String.valueOf(offer.getOfferId()));
            //update exsiting data(remove in remote data list after updating)
            if (offer_remote_data != null) {
                Log.d(TAG, "update exsiting data id:" + offer.getOfferId());
                offer = OfferRealmUtil.convertRemoteOfferToRealmCustomer(offer, offer_remote_data);
                realm.insertOrUpdate(offer);
                offerList.remove(String.valueOf(offer.getOfferId()));
            } else {
                Log.d(TAG, "remove exsiting data id:" + offer.getOfferId());
                // remove from realm that is not found in remote list
                offers.deleteFromRealm(position);
            }
            realm.commitTransaction();
            position++;
        }

        //add the remaining data in remotelist to local realm db)
        for (Map.Entry<String, Offer> entry : offerList.entrySet()) {
            try {
                realm.beginTransaction();
                Log.d("TAG", " inserting Key :" + entry.getKey() + " Value:" + entry.getValue());
                int offerId = Integer.parseInt(entry.getKey());
                Offer offer = entry.getValue();
                OfferRealm offer_local = realm.createObject(OfferRealm.class, offerId);
                OfferRealmUtil.convertRemoteOfferToRealmCustomer(offer_local, offer);
                realm.commitTransaction();
            } catch (RealmPrimaryKeyConstraintException re) {
                Log.e(TAG, "failed to write key already exists:" + entry.getKey());
            }
        }
    }

    private void syncCustomerDB(DataSnapshot snapshot) {//Realm realm,RealmResults<CustomersRealm> customers,HashMap<String,Customers> customerList) {

        HashMap<String, Customers> customerList = FireBaseUtils.getAllCustomers(mContext, snapshot);
        Realm realm = Realm.getDefaultInstance();

        RealmResults<CustomersRealm> customers = realm.where(CustomersRealm.class).findAll();
        //no data in remote db hence clear local db
        if (customerList == null) {
            realm.beginTransaction();
            customers.deleteAllFromRealm();
            realm.commitTransaction();
            return;
        }

        int position = 0;
        for (CustomersRealm customer : customers) {
            realm.beginTransaction();
            Customers customer_remote_data = customerList.get(String.valueOf(customer.getId()));
            //update exsiting data(remove in remote data list after updating)
            if (customer_remote_data != null) {
                Log.d(TAG, "update exsiting data id:" + customer.getId());

                customer = CustomerRealmUtil.convertRemoteCustomerToRealmCustomer(customer, customer_remote_data);
                realm.insertOrUpdate(customer);
                customerList.remove(String.valueOf(customer.getId()));
            } else {
                Log.d(TAG, "remove exsiting data id:" + customer.getId());
                // remove from realm that is not found in remote list
                customers.deleteFromRealm(position);
            }
            realm.commitTransaction();
            position++;
        }

        //add the remaining data in remotelist to local realm db)
        for (Map.Entry<String, Customers> entry : customerList.entrySet()) {
            try {
                realm.beginTransaction();
                Log.d("TAG", " inserting Key :" + entry.getKey() + " Value:" + entry.getValue());
                int customerId = Integer.parseInt(entry.getKey());
                Customers customer = entry.getValue();
                CustomersRealm customer_local = realm.createObject(CustomersRealm.class, customerId);
                CustomerRealmUtil.convertRemoteCustomerToRealmCustomer(customer_local, customer);
                realm.commitTransaction();
            } catch (RealmPrimaryKeyConstraintException re) {
                Log.e(TAG, "failed to write key already exists:" + entry.getKey());
            }
        }
    }


    public void syncSQLCustomerDB(DataSnapshot snapshot) {
        ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();

        HashMap<String, Customers> customerList = FireBaseUtils.getAllCustomers(mContext, snapshot);
        final ContentResolver contentResolver = mContext.getContentResolver();
        // Get list of all items
        Log.i(TAG, "Fetching local entries for merge");
        Uri uri = ShopOnContract.Entry.CONTENT_CUSTOMER_URI; // Get all entries
        Cursor c = contentResolver.query(uri, null, null, null, null);
        assert c != null;
        Log.i(TAG, "Found " + c.getCount() + " local entries. Computing merge solution...");

        // Find stale data
        int id;

        String name;
        String email;
        String mobile;
        String category;

        while (c.moveToNext()) {

            id = c.getInt(c.getColumnIndex(ShopOnContract.Entry.COLUMN_CUSTOMER_ID));

            name = c.getString(c.getColumnIndex(ShopOnContract.Entry.COLUMN_NAME));
            email = c.getString(c.getColumnIndex(ShopOnContract.Entry.COLUMN_EMAIL));
            mobile = c.getString(c.getColumnIndex(ShopOnContract.Entry.COLUMN_MOBILE));
            category = c.getString(c.getColumnIndex(ShopOnContract.Entry.COLUMN_CUSTOMER_CATEGORY));

            Customers customer = null;
            if (customerList != null) {
                customer = customerList.get(String.valueOf(id));
            }

            if (customer != null) {
                Log.d(TAG, " found local customer with id :" + id + " in remote list");
                // Entry exists. Remove from entry map to prevent insert later.
                customerList.remove(String.valueOf(id));
                // Check to see if the entry needs to be updated
                Uri existingUri = ShopOnContract.Entry.CONTENT_CUSTOMER_URI.buildUpon()
                        .appendPath(Integer.toString(id)).build();
                if ((customer.getName() != null && !customer.getName().equals(name)) || (customer.getMobile() != null && !customer.getMobile().equals(mobile)) ||

                        (customer.getEmail() != null && !customer.getEmail().equals(email)) ||
                        (customer.getIntrestedIn() != null && !customer.getIntrestedIn().equals(category))) {
                    // Update existing record
                    Log.i(TAG, "Scheduling update: " + existingUri);
                    batch.add(ContentProviderOperation.newUpdate(existingUri)
                            .withValue(ShopOnContract.Entry.COLUMN_NAME, customer.getName())
                            .withValue(ShopOnContract.Entry.COLUMN_EMAIL, customer.getEmail())
                            .withValue(ShopOnContract.Entry.COLUMN_MOBILE, customer.getMobile())
                            .withValue(ShopOnContract.Entry.COLUMN_CUSTOMER_CATEGORY, customer.getIntrestedIn())
                            .build());

                } else {
                    Log.i(TAG, "No action: " + existingUri);
                }
            } else {
                Log.d(TAG, "did not find local customer with id :" + id + " in remote list");
                // try pushing local data to remote db, since creating customers and offers will not require internet.
                customer = Utils.createCustomerFromCursor(c);
                FireBaseUtils.updateCustomer(mContext, customer);
            }
        }
        c.close();

        // Add new items
        if (customerList != null && customerList.size() > 0) {
            for (Customers customer : customerList.values()) {
                Log.i(TAG, "Scheduling insert: entry_id=" + customer.getId());
                batch.add(ContentProviderOperation.newInsert(ShopOnContract.Entry.CONTENT_CUSTOMER_URI)
                        .withValue(ShopOnContract.Entry.COLUMN_CUSTOMER_ID, customer.getId())
                        .withValue(ShopOnContract.Entry.COLUMN_NAME, customer.getName())
                        .withValue(ShopOnContract.Entry.COLUMN_EMAIL, customer.getEmail())
                        .withValue(ShopOnContract.Entry.COLUMN_MOBILE, customer.getMobile())
                        .withValue(ShopOnContract.Entry.COLUMN_CUSTOMER_CATEGORY, customer.getIntrestedIn())
                    /*.withValue(MovieContract.Entry.COLUMN_IS_FAVOURITE, e.isFavourite())*/
                        .build());

            }
        }
        Log.i(TAG, "Merge solution ready. Applying batch update");
        try {
            mContext.getContentResolver().applyBatch(ShopOnContract.CONTENT_AUTHORITY, batch);
            mContext.getContentResolver().notifyChange(
                    ShopOnContract.Entry.CONTENT_CUSTOMER_URI, // URI where data was modified
                    null,                           // No local observer
                    false);                         // IMPORTANT: Do not sync to network
            // This sample doesn't support uploads, but if *your* code does, make sure you set
            // syncToNetwork=false in the line above to prevent duplicate syncs.
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }

    }


    public void syncSQLOfferDB(DataSnapshot snapshot) {
        ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();
        HashMap<String, Offer> offerList = FireBaseUtils.getAllOffer(mContext, snapshot);

        final ContentResolver contentResolver = mContext.getContentResolver();
        // Get list of all items
        Log.i(TAG, "Fetching local entries for merge");
        Uri uri = ShopOnContract.Entry.CONTENT_OFFER_URI; // Get all entries
        Cursor c = contentResolver.query(uri, null, null, null, null);
        assert c != null;
        Log.i(TAG, "Found " + c.getCount() + " local entries. Computing merge solution...");

        // Find stale data
        int id;

        String scheduled_date;
        String offer_text;
        boolean offer_status;
        String numbers;

        while (c.moveToNext()) {

            id = c.getInt(c.getColumnIndex(ShopOnContract.Entry.COLUMN_OFFER_ID));

            scheduled_date = c.getString(c.getColumnIndex(ShopOnContract.Entry.COLUMN_SCHEDULED_DATE));
            offer_text = c.getString(c.getColumnIndex(ShopOnContract.Entry.COLUMN_OFFER_TEXT));
            offer_status = (c.getInt(c.getColumnIndex(ShopOnContract.Entry.COLUMN_OFFER_STATUS)) == 0) ? false : true;
            numbers = c.getString(c.getColumnIndex(ShopOnContract.Entry.COLUMN_CUSTOMER_NUMBERS));

            Offer offer = null;
            if (offerList != null) {
                offer = offerList.get(String.valueOf(id));
            }


            if (offer != null) {
                Log.d(TAG, " found local customer with id :" + id + " in remote list");
                // Entry exists. Remove from entry map to prevent insert later.
                offerList.remove(String.valueOf(id));
                // Check to see if the entry needs to be updated
                Uri existingUri = ShopOnContract.Entry.CONTENT_OFFER_URI.buildUpon()
                        .appendPath(Integer.toString(id)).build();
                if ((offer.getDeliverMessageOn() != null && !offer.getDeliverMessageOn().equals(scheduled_date)) || (offer.getOfferText() != null && !offer.getOfferText().equals(offer_text)) ||
                        (offer.getNumbers() != null && !offer.getNumbers().equals(numbers)) || !(offer.getOfferStatus() == offer_status)) {
                    // Update existing record
                    Log.i(TAG, "Scheduling update: " + existingUri);
                    batch.add(ContentProviderOperation.newUpdate(existingUri)
                            .withValue(ShopOnContract.Entry.COLUMN_SCHEDULED_DATE, offer.getDeliverMessageOn())
                            .withValue(ShopOnContract.Entry.COLUMN_OFFER_TEXT, offer.getOfferText())
                            .withValue(ShopOnContract.Entry.COLUMN_CUSTOMER_NUMBERS, offer.getNumbers())
                            .withValue(ShopOnContract.Entry.COLUMN_OFFER_STATUS, (offer.getOfferStatus()) ? 1 : 0)
                            .build());

                } else {
                    Log.i(TAG, "No action: " + existingUri);
                }
            } else {
                Log.d(TAG, "did not find local customer with id :" + id + " in remote list");
                // try pushing local data to remote db, since creating customers and offers will not require internet.
                offer = Utils.createOfferFromCursor(c);
                FireBaseUtils.updateOfferDataBase(mContext, offer);
            }
        }
        c.close();

        if (offerList != null && offerList.size() > 0) {
            // Add new items
            for (Offer offer : offerList.values()) {
                Log.i(TAG, "Scheduling insert: entry_id=" + offer.getOfferId());
                batch.add(ContentProviderOperation.newInsert(ShopOnContract.Entry.CONTENT_OFFER_URI)
                        .withValue(ShopOnContract.Entry.COLUMN_OFFER_ID, offer.getOfferId())
                        .withValue(ShopOnContract.Entry.COLUMN_SCHEDULED_DATE, offer.getDeliverMessageOn())
                        .withValue(ShopOnContract.Entry.COLUMN_OFFER_TEXT, offer.getOfferText())
                        .withValue(ShopOnContract.Entry.COLUMN_CUSTOMER_NUMBERS, offer.getNumbers())
                        .withValue(ShopOnContract.Entry.COLUMN_OFFER_STATUS, offer.getOfferStatus() ? 1 : 0)
                        .build());

            }
        }

        Log.i(TAG, "Merge solution ready. Applying batch update");
        try {
            mContext.getContentResolver().applyBatch(ShopOnContract.CONTENT_AUTHORITY, batch);
            mContext.getContentResolver().notifyChange(
                    ShopOnContract.Entry.CONTENT_OFFER_URI, // URI where data was modified
                    null,                           // No local observer
                    false);                         // IMPORTANT: Do not sync to network
            // This sample doesn't support uploads, but if *your* code does, make sure you set
            // syncToNetwork=false in the line above to prevent duplicate syncs.
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }

    }


    public void unregister(SyncInterface obj) {
        synchronized (MUTEX) {
            for (int i = 0; i < observers.size(); i++)
                Log.i(TAG, "unregister : Object Name is :" + observers.get(i).getClass().getSimpleName() + " obj:" + obj);
            Log.i(TAG, "unregister :Before remove :size is :" + observers.size());
            observers.remove(obj);
            // observerMap.remove(obj);
            Log.i(TAG, "unregister :After remove :size is :" + observers.size());
        }
    }

    public void register(SyncInterface obj) {
        Log.i(TAG, "register : Object Name is :" + obj.getClass().getSimpleName() + " obj:" + obj);
        if (obj == null) throw new NullPointerException("Null Observer");
        synchronized (MUTEX) {
            if (!observers.contains(obj)) {
                observers.add(obj);
                //observerMap.put(obj, true);
            }

            Log.i(TAG, "register : Object Name is : Size is :" + obj.getClass().getSimpleName() + "," + observers.size());
        }
    }

}
