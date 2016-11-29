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

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Field and table name constants for
 * {@link ShopOnProvider}.
 */
public class ShopOnContract {
    private ShopOnContract() {
    }

    /**
     * Content provider authority.
     */
    public static final String CONTENT_AUTHORITY = "shopon.com.shopon.db.provider";

    /**
     * Base URI. (content://com.example.android.network.sync.basicsyncadapter)
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Path component for "entry"-type resources..
     */
    private static final String PATH_ENTRIES = "entries";

    /**
     * Columns supported by "entries" records.
     */
    public static class Entry implements BaseColumns {
        /**
         * MIME type for lists of entries.
         */
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.offersgalore.entries";
        /**
         * MIME type for individual entries.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.offersgalore.entry";

        private static final String MERCHANT_PATH_ENTRIES = "merchant_entries";
        private static final String CUSTOMER_PATH_ENTRIES = "customer_entries";
        private static final String OFFER_PATH_ENTRIES = "offer_entries";


        /**
         * Fully qualified URI for "entry" resources.
         */
        public static final Uri CONTENT_MERCHANT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(MERCHANT_PATH_ENTRIES).build();


        public static final Uri CONTENT_CUSTOMER_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(CUSTOMER_PATH_ENTRIES).build();

        public static final Uri CONTENT_OFFER_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(OFFER_PATH_ENTRIES).build();

        //OFFER TABLE
        public static final String OFFER_TABLE_NAME = "offer";

        public static final String COLUMN_OFFER_ID = "offer_id";

        public static final String COLUMN_SCHEDULED_DATE = "scheduled_date";

        public static final String COLUMN_OFFER_TEXT = "offerText";

        public static final String COLUMN_CUSTOMER_NUMBERS = "numbers";

        //MERCHANT TABLE
        public static final String MERCHANT_TABLE_NAME = "merchant";

        public static final String COLUMN_USER_ID = "userId";

        public static final String COLUMN_EMAIL = "email";

        public static final String COLUMN_NAME = "name";

        public static final String COLUMN_MERCHANT_CATEGORY = "merchantCategory";

        public static final String COLUMN_MOBILE = "mobile";

        //CUSTOMER TABLE
        public static final String CUSTOMER_TABLE_NAME = "customer";

        public static final String COLUMN_CUSTOMER_ID = "id";

        public static final String COLUMN_CUSTOMER_CATEGORY = "intrestedIn";

        public static final String COLUMN_CUSTOMER_MERCHANT_ID = "merchentId";

        public static final String COLUMN_OFFER_STATUS = "offerStatus";
    }
}