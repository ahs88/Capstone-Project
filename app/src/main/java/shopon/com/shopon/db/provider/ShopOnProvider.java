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

import shopon.com.shopon.view.constants.Constants;


public class ShopOnProvider extends ContentProvider {
    ShopOnDatabase mDatabaseHelper;

    public static final String TAG = ShopOnProvider.class.getName();
    /**
     * Content authority for this provider.
     */
    private static final String AUTHORITY = ShopOnContract.CONTENT_AUTHORITY;

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
        Log.d(TAG, "onCreate()");
        mDatabaseHelper = new ShopOnDatabase(getContext());
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
                return ShopOnContract.Entry.CONTENT_TYPE;
            case ROUTE_ENTRIES_MERCHANT_ID:
                return ShopOnContract.Entry.CONTENT_ITEM_TYPE;
            case ROUTE_ENTRIES_OFFER:
                return ShopOnContract.Entry.CONTENT_TYPE;
            case ROUTE_ENTRIES_OFFER_ID:
                return ShopOnContract.Entry.CONTENT_ITEM_TYPE;
            case ROUTE_ENTRIES_CUSTOMER:
                return ShopOnContract.Entry.CONTENT_TYPE;
            case ROUTE_ENTRIES_CUSTOMER_ID:
                return ShopOnContract.Entry.CONTENT_ITEM_TYPE;
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

        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        SelectionBuilder builder = new SelectionBuilder();
        int uriMatch = sUriMatcher.match(uri);

        switch (uriMatch) {

            case ROUTE_ENTRIES_CUSTOMER_ID: {
                String id = uri.getLastPathSegment();
                builder.where(ShopOnContract.Entry.COLUMN_CUSTOMER_ID + "=?", id);
            }
            case ROUTE_ENTRIES_CUSTOMER: {
                builder.table(ShopOnContract.Entry.CUSTOMER_TABLE_NAME)
                        .where(selection, selectionArgs);
                Cursor c = builder.query(db, projection, sortOrder);
                // Note: Notification URI must be manually set here for loaders to correctly
                // register ContentObservers.
                Context ctx = getContext();
                assert ctx != null;
                c.setNotificationUri(ctx.getContentResolver(), uri);
                return c;

            }
            case ROUTE_ENTRIES_MERCHANT_ID: {
                String id = uri.getLastPathSegment();
                builder.where(ShopOnContract.Entry.COLUMN_USER_ID + "=?", id);

            }
            case ROUTE_ENTRIES_MERCHANT: {
                Log.d(TAG, "querry merchant");
                builder.table(ShopOnContract.Entry.MERCHANT_TABLE_NAME)
                        .where(selection, selectionArgs);
                Cursor c = builder.query(db, projection, sortOrder);
                // Note: Notification URI must be manually set here for loaders to correctly
                // register ContentObservers.
                Context ctx = getContext();
                assert ctx != null;
                c.setNotificationUri(ctx.getContentResolver(), uri);
                return c;
            }

            case ROUTE_ENTRIES_OFFER_ID: {
                String id = uri.getLastPathSegment();
                builder.where(ShopOnContract.Entry.COLUMN_OFFER_ID + "=?", id);
            }
            case ROUTE_ENTRIES_OFFER: {
                Log.d(TAG, "query offers");
                builder.table(ShopOnContract.Entry.OFFER_TABLE_NAME)
                        .where(selection, selectionArgs);
                Cursor c = builder.query(db, projection, sortOrder);
                // Note: Notification URI must be manually set here for loaders to correctly
                // register ContentObservers.
                Context ctx = getContext();
                assert ctx != null;
                c.setNotificationUri(ctx.getContentResolver(), uri);
                return c;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }


    /**
     * Insert a new entry into the database.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        Uri result = null;
        final int match = sUriMatcher.match(uri);
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        switch (match) {
            case ROUTE_ENTRIES_CUSTOMER: {
                Log.d(TAG, "insert customer");
                long id = db.insertOrThrow(ShopOnContract.Entry.CUSTOMER_TABLE_NAME, null, values);
                result = Uri.parse(ShopOnContract.Entry.CONTENT_CUSTOMER_URI + "/" + id);
                break;
            }

            case ROUTE_ENTRIES_MERCHANT: {
                Log.d(TAG, "insert merchant");
                long id = db.insertOrThrow(ShopOnContract.Entry.MERCHANT_TABLE_NAME, null, values);
                result = Uri.parse(ShopOnContract.Entry.CONTENT_MERCHANT_URI + "/" + id);
                break;
            }

            case ROUTE_ENTRIES_OFFER: {
                Log.d(TAG, "insert offer");
                long id = db.insertOrThrow(ShopOnContract.Entry.OFFER_TABLE_NAME, null, values);
                result = Uri.parse(ShopOnContract.Entry.CONTENT_OFFER_URI + "/" + id);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Send broadcast to registered ContentObservers, to refresh UI.
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null, false);
        return result;
    }

    /**
     * Delete an entry by database by URI.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);
        SelectionBuilder builder = new SelectionBuilder();
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        int count = 0;
        switch (match) {
            case ROUTE_ENTRIES_CUSTOMER:
                count = builder.table(ShopOnContract.Entry.CUSTOMER_TABLE_NAME)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;

            case ROUTE_ENTRIES_CUSTOMER_ID: {
                String id = uri.getLastPathSegment();
                count = builder.table(ShopOnContract.Entry.CUSTOMER_TABLE_NAME)
                        .where(ShopOnContract.Entry.COLUMN_CUSTOMER_ID + "=?", id)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            }
            case ROUTE_ENTRIES_MERCHANT:
                count = builder.table(ShopOnContract.Entry.MERCHANT_TABLE_NAME)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            case ROUTE_ENTRIES_MERCHANT_ID: {
                String id = uri.getLastPathSegment();
                count = builder.table(ShopOnContract.Entry.MERCHANT_TABLE_NAME)
                        .where(ShopOnContract.Entry.COLUMN_USER_ID + "=?", id)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            }
            case ROUTE_ENTRIES_OFFER:
                count = builder.table(ShopOnContract.Entry.OFFER_TABLE_NAME)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            case ROUTE_ENTRIES_OFFER_ID: {
                String id = uri.getLastPathSegment();
                count = builder.table(ShopOnContract.Entry.OFFER_TABLE_NAME)
                        .where(ShopOnContract.Entry.COLUMN_OFFER_ID + "=?", id)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Send broadcast to registered ContentObservers, to refresh UI.
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null, false);
        return count;
    }

    /**
     * Update an etry in the database by URI.
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        int count;
        SelectionBuilder builder = new SelectionBuilder();
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        switch (match) {
            case ROUTE_ENTRIES_CUSTOMER:
                count = builder.table(ShopOnContract.Entry.CUSTOMER_TABLE_NAME)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;

            case ROUTE_ENTRIES_CUSTOMER_ID: {
                String id = uri.getLastPathSegment();
                count = builder.table(ShopOnContract.Entry.CUSTOMER_TABLE_NAME)
                        .where(ShopOnContract.Entry.COLUMN_CUSTOMER_ID + "=?", id)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            }
            case ROUTE_ENTRIES_MERCHANT:
                count = builder.table(ShopOnContract.Entry.MERCHANT_TABLE_NAME)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            case ROUTE_ENTRIES_MERCHANT_ID: {
                String id = uri.getLastPathSegment();
                count = builder.table(ShopOnContract.Entry.MERCHANT_TABLE_NAME)
                        .where(ShopOnContract.Entry.COLUMN_USER_ID + "=?", id)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            }
            case ROUTE_ENTRIES_OFFER:
                count = builder.table(ShopOnContract.Entry.OFFER_TABLE_NAME)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            case ROUTE_ENTRIES_OFFER_ID: {
                String id = uri.getLastPathSegment();
                count = builder.table(ShopOnContract.Entry.OFFER_TABLE_NAME)
                        .where(ShopOnContract.Entry.COLUMN_OFFER_ID + "=?", id)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null, false);
        return count;
    }

    /**
     * SQLite backend for @{link ShopOnProvider}.
     * <p>
     * Provides access to an disk-backed, SQLite datastore which is utilized by ShopOnProvider. This
     * database should never be accessed by other parts of the application directly.
     */
    static class ShopOnDatabase extends SQLiteOpenHelper {
        /**
         * Schema version.
         */
        public static final int DATABASE_VERSION = 3;
        /**
         * Filename for SQLite file.
         */
        public static final String DATABASE_NAME = "offersgalore.db";

        private static final String TYPE_TEXT = " TEXT";
        private static final String TYPE_INTEGER = " INTEGER";
        private static final String TYPE_BOOL = " BOOL";
        private static final String TYPE_DATETIME = " DATETIME";
        private static final String COMMA_SEP = ",";

        /**
         * SQL statement to create "entry" table.
         */
        private static final String SQL_CREATE_MERCHANT_ENTRIES =
                "CREATE TABLE IF NOT EXISTS " + ShopOnContract.Entry.MERCHANT_TABLE_NAME + " (" +
                        ShopOnContract.Entry.COLUMN_USER_ID + TYPE_INTEGER + " PRIMARY KEY" + COMMA_SEP +
                        ShopOnContract.Entry.COLUMN_NAME + TYPE_TEXT + COMMA_SEP +
                        ShopOnContract.Entry.COLUMN_EMAIL + TYPE_TEXT + COMMA_SEP +
                        ShopOnContract.Entry.COLUMN_MOBILE + TYPE_TEXT + COMMA_SEP +
                        ShopOnContract.Entry.COLUMN_MERCHANT_CATEGORY + TYPE_TEXT + COMMA_SEP +
                        ShopOnContract.Entry.COLUMN_CREATED_AT + TYPE_DATETIME +
                        ")";

        private static final String SQL_CREATE_CUSTOMER_ENTRIES =
                "CREATE TABLE IF NOT EXISTS " + ShopOnContract.Entry.CUSTOMER_TABLE_NAME + " (" +
                        ShopOnContract.Entry.COLUMN_CUSTOMER_ID + " INTEGER PRIMARY KEY," +
                        ShopOnContract.Entry.COLUMN_NAME + TYPE_TEXT + COMMA_SEP +
                        ShopOnContract.Entry.COLUMN_EMAIL + TYPE_TEXT + COMMA_SEP +
                        ShopOnContract.Entry.COLUMN_MOBILE + TYPE_TEXT + COMMA_SEP +
                        ShopOnContract.Entry.COLUMN_CUSTOMER_CATEGORY + TYPE_TEXT + COMMA_SEP +
                        ShopOnContract.Entry.COLUMN_CREATED_AT + TYPE_DATETIME +
                        ")";

        private static final String SQL_CREATE_OFFER_ENTRIES =
                "CREATE TABLE IF NOT EXISTS " + ShopOnContract.Entry.OFFER_TABLE_NAME + " (" +
                        ShopOnContract.Entry.COLUMN_OFFER_ID + " INTEGER PRIMARY KEY," +
                        ShopOnContract.Entry.COLUMN_OFFER_TEXT + TYPE_TEXT + COMMA_SEP +
                        ShopOnContract.Entry.COLUMN_OFFER_STATUS + TYPE_BOOL + COMMA_SEP +
                        ShopOnContract.Entry.COLUMN_CUSTOMER_NUMBERS + TYPE_TEXT + COMMA_SEP +
                        ShopOnContract.Entry.COLUMN_SCHEDULED_DATE + TYPE_DATETIME + COMMA_SEP +
                        ShopOnContract.Entry.COLUMN_CREATED_AT + TYPE_DATETIME +
                        ")";

        /**
         * SQL statement to drop "entry" table.
         */
        private static final String SQL_DELETE_OFFER_ENTRIES =
                "DROP TABLE IF EXISTS " + ShopOnContract.Entry.OFFER_TABLE_NAME;
        private static final String SQL_DELETE_MERCHANT_ENTRIES =
                "DROP TABLE IF EXISTS " + ShopOnContract.Entry.MERCHANT_TABLE_NAME;
        private static final String SQL_DELETE_CUSTOMER_ENTRIES =
                "DROP TABLE IF EXISTS " + ShopOnContract.Entry.CUSTOMER_TABLE_NAME;

        public ShopOnDatabase(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(TAG, "onCreate sqlitehelper SQL_CREATE_MERCHANT_ENTRIES:" + SQL_CREATE_MERCHANT_ENTRIES);
            db.execSQL(SQL_CREATE_MERCHANT_ENTRIES);
            db.execSQL(SQL_CREATE_CUSTOMER_ENTRIES);
            db.execSQL(SQL_CREATE_OFFER_ENTRIES);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_CUSTOMER_ENTRIES);
            db.execSQL(SQL_DELETE_OFFER_ENTRIES);
            db.execSQL(SQL_DELETE_MERCHANT_ENTRIES);

            onCreate(db);
        }
    }
}
