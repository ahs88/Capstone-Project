package shopon.com.shopon.widget;

/**
 * Created by shetty on 26/01/16.
 */

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.Looper;
import android.preference.Preference;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import io.realm.Realm;
import io.realm.RealmResults;
import shopon.com.shopon.R;
import shopon.com.shopon.datamodel.offer.Offer;
import shopon.com.shopon.datamodel.offer.OfferRealm;
import shopon.com.shopon.db.provider.ShopOnContract;
import shopon.com.shopon.preferences.UserSharedPreferences;
import shopon.com.shopon.utils.Utils;
import shopon.com.shopon.view.constants.Constants;


@SuppressLint("NewApi")
public class WidgetDataProvider implements RemoteViewsFactory {

    public static final String TAG = "WidgetDataProvider";

    List mCollections = new ArrayList();
    Context mContext = null;
    private RealmResults<OfferRealm> result;
    private int count = 0;
    private Cursor mCursor;
    UserSharedPreferences userSharedPreference;

    public WidgetDataProvider(Context context, Intent intent) {
        mContext = context;
        userSharedPreference = new UserSharedPreferences(mContext);
    }

    @Override
    public int getCount() {
        Log.d(TAG, "getCount:" + mCursor.getCount());
        return (mCursor.getCount() == 0) ? 1 : mCursor.getCount();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        Log.d(TAG, "getViewAt:" + position);
        if (mCursor != null && mCursor.getCount() > 0)
            mCursor.moveToPosition(position);


        RemoteViews mView = null;
        if (mCursor.getCount() > 0) {
            mView = new RemoteViews(mContext.getPackageName(),
                    R.layout.offer_item_new);


            mView.setTextViewText(R.id.date_summary, Utils.getDateDisplayText(mCursor.getString(mCursor.getColumnIndex(ShopOnContract.Entry.COLUMN_SCHEDULED_DATE))));
            mView.setTextViewText(R.id.offer_text, mCursor.getString(mCursor.getColumnIndex(ShopOnContract.Entry.COLUMN_OFFER_TEXT)));
            mView.setTextColor(R.id.offer_text, mContext.getColor(R.color.black));
            int number_of_customers = mCursor.getString(mCursor.getColumnIndex(ShopOnContract.Entry.COLUMN_CUSTOMER_NUMBERS)).split(",").length;
            mView.setTextViewText(R.id.customer_count, mContext.getString(R.string.customer_count_placeholder, String.valueOf(number_of_customers)));
            mView.setTextViewText(R.id.offer_date, mCursor.getString(mCursor.getColumnIndex(ShopOnContract.Entry.COLUMN_SCHEDULED_DATE)));
            mView.setTextColor(R.id.offer_date, mContext.getColor(R.color.black));
        } else {
            mView = new RemoteViews(mContext.getPackageName(),
                    R.layout.offer_item_no_data);
            if ((Integer) userSharedPreference.getPref(Constants.CURRENT_LOGIN_STATE) != null && (int) userSharedPreference.getPref(Constants.CURRENT_LOGIN_STATE) == Constants.LOGIN_COMPLETE) {
                mView.setTextViewText(R.id.no_data, mContext.getString(R.string.no_offers_today));
                mView.setTextColor(R.id.no_data, mContext.getColor(R.color.black));
            } else {
                mView.setTextViewText(R.id.no_data, mContext.getString(R.string.pls_login));
                mView.setTextColor(R.id.no_data, mContext.getColor(R.color.black));
            }
        }
        return mView;
    }

    @Override
    public int getViewTypeCount() {

        return 2;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {
        initData();
    }

    @Override
    public void onDataSetChanged() {
        Log.d(TAG,"onDataSetChanged");
        final long identityToken = Binder.clearCallingIdentity();
        initData();
        Binder.restoreCallingIdentity(identityToken);
    }

    private void initData() {

        Log.d(TAG, "initData");

        getCurrentDaysOffer();
    }

    @Override
    public void onDestroy() {
        result = null;
    }


    public void getCurrentDaysOffer() {
        Date date = new Date(System.currentTimeMillis());//+((i-2)*86400000));
        SimpleDateFormat mformat = new SimpleDateFormat("dd-MMM, yyyy");
        String[] dates = new String[1];
        dates[0] = mformat.format(date);


        mCursor = mContext.getContentResolver().query(ShopOnContract.Entry.CONTENT_OFFER_URI, null, ShopOnContract.Entry.COLUMN_SCHEDULED_DATE + " LIKE ? OR " + ShopOnContract.Entry.COLUMN_SCHEDULED_DATE + " LIKE ? OR " + ShopOnContract.Entry.COLUMN_SCHEDULED_DATE + " LIKE ?", new String[]{"% " + dates[0] + " %", dates[0] + " %", "% " + dates[0]}, null);
        Log.d(TAG, "current date:" + dates[0] + " mCursor size:" + mCursor.getCount());
        mCursor.moveToFirst();
    }


}
