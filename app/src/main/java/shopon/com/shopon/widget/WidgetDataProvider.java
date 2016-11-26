package shopon.com.shopon.widget;

/**
 * Created by shetty on 26/01/16.
 */

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
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
import shopon.com.shopon.utils.Utils;


@SuppressLint("NewApi")
public class WidgetDataProvider implements RemoteViewsFactory {

    public static final String TAG="WidgetDataProvider";

    List mCollections = new ArrayList();
    Context mContext = null;
    private RealmResults<OfferRealm> result;
    private int count = 0;

    public WidgetDataProvider(Context context, Intent intent) {
        mContext = context;
    }

    @Override
    public int getCount() {
        Log.d(TAG,"getCount:"+count);
        return count;
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
        Log.d(TAG,"getViewAt:"+position);


        RealmResults<OfferRealm> result = getCurrentDaysOffer();
        if(result == null || result.size() == 0 ){
            Log.d(TAG,"result :"+result);
            return null;
        }
        Log.d(TAG,"result size:"+result.size());
        OfferRealm offer = result.get(position);

        RemoteViews mView = new RemoteViews(mContext.getPackageName(),
                R.layout.offer_item_new);


        //ViewHolder mHolder = new ViewHolder(mView);

            //mCollections.add("ListView item " + i);
            //final ViewHolder mHolder = (ViewHolder) mView.getTag();
        mView.setTextViewText(R.id.date_summary, Utils.getDateDisplayText(offer.getDeliverMessageOn()));
        //home_name.setText(cursor.getString(COL_HOME));
        mView.setTextViewText(R.id.offer_text, offer.getOfferText());
        mView.setTextColor(R.id.offer_text,mContext.getColor(R.color.black));
        //mHolder.away_name.setText(cursor.getString(COL_AWAY));
        mView.setTextViewText(R.id.customer_count, String.valueOf(offer.getNumbers().split(",").length)+"C");
        //mHolder.date.setText(cursor.getString(COL_MATCHTIME));
        mView.setTextViewText(R.id.offer_date,offer.getDeliverMessageOn());
        mView.setTextColor(R.id.offer_date,mContext.getColor(R.color.black));

        return mView;
    }

    @Override
    public int getViewTypeCount() {

        return 1;
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
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                initData();
            }
        };
        handler.post(runnable);
    }

    private void initData() {
        //mCollections.clear();
        Log.d(TAG, "initData");

        RealmResults<OfferRealm> result = getCurrentDaysOffer();
        count = result.size();
        Log.d(TAG, "initdata result size:" + result.size());

    }

    @Override
    public void onDestroy() {
        result = null;
    }


    public RealmResults<OfferRealm> getCurrentDaysOffer(){
        Date date = new Date(System.currentTimeMillis());//+((i-2)*86400000));
        SimpleDateFormat mformat = new SimpleDateFormat("dd-MMM, yyyy");
        String[] dates = new String[1];
        dates[0] = mformat.format(date);

        Realm realm = Realm.getDefaultInstance();
        RealmResults<OfferRealm> result = realm.where(OfferRealm.class).contains("deliverMessageOn",dates[0]).findAll();
        return result;
    }

}
