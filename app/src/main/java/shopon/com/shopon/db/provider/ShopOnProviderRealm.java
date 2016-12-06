/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package shopon.com.shopon.db.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import shopon.com.shopon.datamodel.customer.CustomersRealm;
import shopon.com.shopon.datamodel.merchant.MerchantsRealm;
import shopon.com.shopon.datamodel.offer.OfferRealm;
import shopon.com.shopon.db.DataMigration;
import shopon.com.shopon.view.constants.Constants;


public class ShopOnProviderRealm extends ContentProvider {
    ShopOnDatabase mDatabaseHelper;

    public static final String TAG = ShopOnProviderRealm.class.getName();
    /**
     * Content authority for this provider.
     */
    private static final String AUTHORITY = ShopOnContractRealm.CONTENT_AUTHORITY;

    // The constants below represent individual URI routes, as IDs. Every URI pattern recognized by
    // this ContentProvider is defined using sUriMatcher.addURI(), and associated with one of these
    // IDs.
    //
    // When a incoming URI is run through sUriMatcher, it will be tested against the defined
    // URI patterns, and the corresponding route ID will be returned.
    /**
     * URI ID for route: /entries
     */
    public static final int ROUTE_ENTRIES_MERCHANT = 1;

    /**
     * URI ID for route: /entries/{ID}
     */
    public static final int ROUTE_ENTRIES_MERCHANT_ID = 2;

    /**
     * URI ID for route: /entries
     */
    public static final int ROUTE_ENTRIES_CUSTOMER = 3;

    /**
     * URI ID for route: /entries/{ID}
     */
    public static final int ROUTE_ENTRIES_CUSTOMER_ID = 4;


    /**
     * URI ID for route: /entries
     */
    public static final int ROUTE_ENTRIES_OFFER = 5;

    /**
     * URI ID for route: /entries/{ID}
     */
    public static final int ROUTE_ENTRIES_OFFER_ID = 6;


    /**
     * UriMatcher, used to decode incoming URIs.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(AUTHORITY, "merchant_entries", ROUTE_ENTRIES_MERCHANT);
        sUriMatcher.addURI(AUTHORITY, "merchant_entries/*", ROUTE_ENTRIES_MERCHANT_ID);

        sUriMatcher.addURI(AUTHORITY, "offer_entries", ROUTE_ENTRIES_OFFER);
        sUriMatcher.addURI(AUTHORITY, "offer_entries/*", ROUTE_ENTRIES_OFFER_ID);

        sUriMatcher.addURI(AUTHORITY, "customer_entries", ROUTE_ENTRIES_CUSTOMER);
        sUriMatcher.addURI(AUTHORITY, "customer_entries/*", ROUTE_ENTRIES_CUSTOMER_ID);
    }


    @Override
    public boolean onCreate() {
        Realm.init(getContext().getApplicationContext());
        RealmConfiguration realmConfig = new RealmConfiguration.Builder().
                migration(new DataMigration(getContext().getApplicationContext())).schemaVersion(1).deleteRealmIfMigrationNeeded().
                build(); //.
        Realm.setDefaultConfiguration(realmConfig);

        return true;
    }


    /**
     * Determine the mime type for entries returned by a given URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ROUTE_ENTRIES_MERCHANT:
                return ShopOnContractRealm.Entry.CONTENT_TYPE;
            case ROUTE_ENTRIES_MERCHANT_ID:
                return ShopOnContractRealm.Entry.CONTENT_ITEM_TYPE;
            case ROUTE_ENTRIES_OFFER:
                return ShopOnContractRealm.Entry.CONTENT_TYPE;
            case ROUTE_ENTRIES_OFFER_ID:
                return ShopOnContractRealm.Entry.CONTENT_ITEM_TYPE;
            case ROUTE_ENTRIES_CUSTOMER:
                return ShopOnContractRealm.Entry.CONTENT_TYPE;
            case ROUTE_ENTRIES_CUSTOMER_ID:
                return ShopOnContractRealm.Entry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    /**
     * Perform a database query by URI.
     * <p>
     * <p>Currently supports returning all entries (/entries) and individual entries by ID
     * (/entries/{ID}).
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {


        int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch) {
            case ROUTE_ENTRIES_CUSTOMER: {
                Log.d(TAG, "query customers");
                Realm mRealm = Realm.getDefaultInstance();

                RealmQuery<CustomersRealm> query = mRealm.where(CustomersRealm.class);
                query = (RealmQuery<CustomersRealm>) getSelectionType(query, selection, selectionArgs);
                RealmResults<CustomersRealm> results = query.findAll();
                if (projection == null) {
                    projection = new String[]{ShopOnContractRealm.Entry.COLUMN_USER_ID, ShopOnContractRealm.Entry.COLUMN_NAME, ShopOnContractRealm.Entry.COLUMN_MOBILE, ShopOnContractRealm.Entry.COLUMN_EMAIL, ShopOnContractRealm.Entry.COLUMN_CUSTOMER_CATEGORY};
                }
                MatrixCursor matrixCursor =
                        new MatrixCursor(projection);
                for (CustomersRealm item : results) {
                    Object[] rowData =
                            new
                                    Object[]{item.getId(), item.getName(), item.getMobile(), item.getEmail(), item.getIntrestedIn()};
                    matrixCursor.addRow(rowData);
                }
                Log.d(TAG, "matrix cursor length:" + matrixCursor.getCount() + " results size:" + results.size());
                return matrixCursor;
            }
            case ROUTE_ENTRIES_CUSTOMER_ID:

                break;
            case ROUTE_ENTRIES_MERCHANT: {
                Log.d(TAG, "querry merchant");
                Realm mRealm = Realm.getDefaultInstance();
                RealmQuery<MerchantsRealm> query = mRealm.where(MerchantsRealm.class);
                query = (RealmQuery<MerchantsRealm>) getSelectionType(query, selection, selectionArgs);
                RealmResults<MerchantsRealm> results = query.findAll();
                if (projection == null) {
                    projection = new String[]{ShopOnContractRealm.Entry.COLUMN_USER_ID, ShopOnContractRealm.Entry.COLUMN_NAME, ShopOnContractRealm.Entry.COLUMN_MOBILE, ShopOnContractRealm.Entry.COLUMN_EMAIL, ShopOnContractRealm.Entry.COLUMN_MERCHANT_CATEGORY};
                }
                MatrixCursor matrixCursor =
                        new MatrixCursor(projection);
                for (MerchantsRealm item : results) {
                    Object[] rowData =
                            new
                                    Object[]{item.getUserId(), item.getName(), item.getMobile(), item.getEmail(), item.getMerchentCategory()};
                    matrixCursor.addRow(rowData);
                }
                return matrixCursor;

            }
            case ROUTE_ENTRIES_MERCHANT_ID:
                break;
            case ROUTE_ENTRIES_OFFER: {
                Log.d(TAG, "query offers");
                Realm mRealm = Realm.getDefaultInstance();
                //mRealm.setAutoRefresh(true);

                RealmQuery<OfferRealm> query = mRealm.where(OfferRealm.class);
                if (selection != null && !TextUtils.isEmpty(selection))
                    query = (RealmQuery<OfferRealm>) getSelectionType(query, selection, selectionArgs);
                RealmResults<OfferRealm> results = query.findAll();
                if (projection == null) {
                    projection = new String[]{ShopOnContractRealm.Entry.COLUMN_OFFER_ID, ShopOnContractRealm.Entry.COLUMN_OFFER_STATUS, ShopOnContractRealm.Entry.COLUMN_OFFER_TEXT, ShopOnContractRealm.Entry.COLUMN_CUSTOMER_NUMBERS, ShopOnContractRealm.Entry.COLUMN_SCHEDULED_DATE};
                }
                MatrixCursor matrixCursor =
                        new MatrixCursor(projection);
                for (OfferRealm item : results) {
                    Object[] rowData =
                            new Object[]{item.getOfferId(), item.getOfferStatus(), item.getOfferText(), item.getNumbers(), item.getDeliverMessageOn()};
                    matrixCursor.addRow(rowData);
                }
                Log.d(TAG, "matrix cursor length:" + results.size() + " result size:" + results.size());
                return matrixCursor;
            }
            case ROUTE_ENTRIES_OFFER_ID:
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return null;
    }

    private RealmQuery<? extends RealmObject> getSelectionType(RealmQuery<? extends RealmObject> query, String selection, String selectionArgs[]) {
        if (selection == null || TextUtils.isEmpty(selection)) {
            return query;
        }

        try {
            String query_filters[] = selection.split(";");
            for (int i = 0; i < query_filters.length; i++) {
                String query_filter[] = query_filters[i].split(",");
                String column_name = query_filter[0];
                String filter_type = query_filter[1];
                Log.d(TAG, "filterType:" + filter_type + " columnName:" + column_name + " selection args:" + selectionArgs[i]);
                switch (filter_type) {
                    case Constants.FILTER_EQUAL_TO:
                        if (column_name.equals(ShopOnContractRealm.Entry.COLUMN_USER_ID) || column_name.equals(ShopOnContractRealm.Entry.COLUMN_CUSTOMER_ID) || column_name.equals(ShopOnContractRealm.Entry.COLUMN_OFFER_ID)) {
                            query = query.equalTo(column_name, Integer.parseInt(selectionArgs[i]));
                        } else if (column_name.equals(ShopOnContractRealm.Entry.COLUMN_OFFER_STATUS)) {
                            Log.d(TAG, "selection arg:" + Boolean.parseBoolean(selectionArgs[i]));
                            query = query.equalTo(column_name, Boolean.parseBoolean(selectionArgs[i]));
                        } else {
                            query = query.equalTo(column_name, selectionArgs[i]);
                        }
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Improper selection format,  Expected format *****,*****;*****,******");
            //return query;
        }
        return query;
    }

    /**
     * Insert a new entry into the database.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {


        final int match = sUriMatcher.match(uri);

        switch (match) {
            case ROUTE_ENTRIES_CUSTOMER: {
                Log.d(TAG, "insert customer");
                Realm mRealm = Realm.getDefaultInstance();

                mRealm.beginTransaction();

                CustomersRealm item = mRealm.createObject(CustomersRealm.class);
                item.setId(values.getAsInteger(ShopOnContractRealm.Entry.COLUMN_USER_ID));
                item.setName(values.getAsString(ShopOnContractRealm.Entry.COLUMN_NAME));
                item.setEmail(values.getAsString(ShopOnContractRealm.Entry.COLUMN_EMAIL));
                item.setMobile(values.getAsString(ShopOnContractRealm.Entry.COLUMN_MOBILE));
                item.setMerchentId(values.getAsInteger(ShopOnContractRealm.Entry.COLUMN_CUSTOMER_MERCHANT_ID));
                item.setIntrestedIn(values.getAsString(ShopOnContractRealm.Entry.COLUMN_CUSTOMER_CATEGORY));
                mRealm.commitTransaction();
                return Uri.withAppendedPath(uri, String.valueOf(item.getId()));
            }
            case ROUTE_ENTRIES_CUSTOMER_ID:

                break;
            case ROUTE_ENTRIES_MERCHANT: {
                Log.d(TAG, "insert merchant");
                Realm mRealm = Realm.getDefaultInstance();
                mRealm.beginTransaction();
                MerchantsRealm item = mRealm.createObject(MerchantsRealm.class);
                item.setUserId(values.getAsInteger(ShopOnContractRealm.Entry.COLUMN_USER_ID));
                item.setName(values.getAsString(ShopOnContractRealm.Entry.COLUMN_NAME));
                item.setEmail(values.getAsString(ShopOnContractRealm.Entry.COLUMN_EMAIL));
                item.setMobile(values.getAsString(ShopOnContractRealm.Entry.COLUMN_MOBILE));
                item.setMerchentCategory(values.getAsString(ShopOnContractRealm.Entry.COLUMN_MERCHANT_CATEGORY));
                mRealm.commitTransaction();
                return Uri.withAppendedPath(uri, String.valueOf(item.getUserId()));
            }
            case ROUTE_ENTRIES_MERCHANT_ID:
                break;
            case ROUTE_ENTRIES_OFFER: {
                Log.d(TAG, "insert offer");
                Realm mRealm = Realm.getDefaultInstance();
                mRealm.beginTransaction();

                OfferRealm item = mRealm.createObject(OfferRealm.class);
                item.setOfferId(values.getAsInteger(ShopOnContractRealm.Entry.COLUMN_OFFER_ID));
                item.setDeliverMessageOn(values.getAsString(ShopOnContractRealm.Entry.COLUMN_SCHEDULED_DATE));
                item.setNumbers(values.getAsString(ShopOnContractRealm.Entry.COLUMN_MOBILE));
                item.setOfferStatus(values.getAsBoolean(ShopOnContractRealm.Entry.COLUMN_OFFER_STATUS));
                item.setOfferText(values.getAsString(ShopOnContractRealm.Entry.COLUMN_OFFER_TEXT));
                mRealm.commitTransaction();
                return Uri.withAppendedPath(uri, String.valueOf(item.getOfferId()));
            }

            case ROUTE_ENTRIES_OFFER_ID:
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Send broadcast to registered ContentObservers, to refresh UI.
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null, false);
        return null;
    }

    /**
     * Delete an entry by database by URI.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case ROUTE_ENTRIES_CUSTOMER:

                break;
            case ROUTE_ENTRIES_CUSTOMER_ID:
                /**/
                break;
            case ROUTE_ENTRIES_MERCHANT:
                break;
            case ROUTE_ENTRIES_MERCHANT_ID:
                break;
            case ROUTE_ENTRIES_OFFER:
                break;
            case ROUTE_ENTRIES_OFFER_ID:
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Send broadcast to registered ContentObservers, to refresh UI.
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null, false);
        return 0;
    }

    /**
     * Update an etry in the database by URI.
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        int count;
        switch (match) {
            case ROUTE_ENTRIES_CUSTOMER:

                break;
            case ROUTE_ENTRIES_CUSTOMER_ID:
                /**/
                break;
            case ROUTE_ENTRIES_MERCHANT:
                Realm mRealm = Realm.getDefaultInstance();
                mRealm.beginTransaction();
                RealmQuery<MerchantsRealm> query = mRealm.where(MerchantsRealm.class);
                query = (RealmQuery<MerchantsRealm>) getSelectionType(query, selection, selectionArgs);
                MerchantsRealm item = query.findFirst();
                Log.d(TAG, "update item:" + item.getUserId());
                item.setName((values.getAsString(ShopOnContractRealm.Entry.COLUMN_NAME) != null) ? (values.getAsString(ShopOnContractRealm.Entry.COLUMN_NAME)) : item.getName());
                item.setEmail((values.getAsString(ShopOnContractRealm.Entry.COLUMN_EMAIL) != null) ? (values.getAsString(ShopOnContractRealm.Entry.COLUMN_EMAIL)) : item.getEmail());
                item.setMobile((values.getAsString(ShopOnContractRealm.Entry.COLUMN_MOBILE) != null) ? (values.getAsString(ShopOnContractRealm.Entry.COLUMN_MOBILE)) : item.getMobile());
                item.setMerchentCategory((values.getAsString(ShopOnContractRealm.Entry.COLUMN_MERCHANT_CATEGORY) != null) ? (values.getAsString(ShopOnContractRealm.Entry.COLUMN_MERCHANT_CATEGORY)) : item.getMerchentCategory());
                mRealm.commitTransaction();
                break;
            case ROUTE_ENTRIES_MERCHANT_ID:
                break;
            case ROUTE_ENTRIES_OFFER:
                break;
            case ROUTE_ENTRIES_OFFER_ID:
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null, false);
        return 0;
    }

    /**
     * SQLite backend for @{link ShopOnProviderRealm}.
     * <p>
     * Provides access to an disk-backed, SQLite datastore which is utilized by ShopOnProviderRealm. This
     * database should never be accessed by other parts of the application directly.
     */
    static class ShopOnDatabase extends SQLiteOpenHelper {
        /**
         * Schema version.
         */
        public static final int DATABASE_VERSION = 2;
        /**
         * Filename for SQLite file.
         */
        public static final String DATABASE_NAME = "offersgalore.db";

        private static final String TYPE_TEXT = " TEXT";
        private static final String TYPE_INTEGER = " INTEGER";
        private static final String TYPE_BOOL = " BOOL";
        private static final String COMMA_SEP = ",";
        /** SQL statement to create "entry" table. */


        /**
         * SQL statement to drop "entry" table.
         */


        public ShopOnDatabase(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over

            onCreate(db);
        }
    }
}
