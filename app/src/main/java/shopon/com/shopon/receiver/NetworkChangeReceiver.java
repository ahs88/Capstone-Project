package shopon.com.shopon.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;

import shopon.com.shopon.preferences.UserSharedPreferences;
import shopon.com.shopon.utils.Utils;
import shopon.com.shopon.view.constants.Constants;

import static shopon.com.shopon.view.base.BaseFragment.TAG;

/**
 * Created by shetty on 29/11/16.
 */

public class NetworkChangeReceiver extends BroadcastReceiver{

    public static final String TAG = shopon.com.shopon.receiver.NetworkChangeReceiver.class.getName();
    public static final int TIMEOUT = 4000;
    private static final String TEST_HOST = "www.google.com";
    private static final java.lang.String NETWORK_CHECK = "network_check";
    Context mContext ;


    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        HandlerThread handlerThread = new HandlerThread(NETWORK_CHECK);
        handlerThread.start();
        Handler check_nw_handler = new Handler(handlerThread.getLooper());
        check_nw_handler.post(getNetworkCheckRunnable());
    }

    public Runnable getNetworkCheckRunnable() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (!isOnline(mContext)) {
                    Log.d(TAG, "Internet not connected");
                    UserSharedPreferences sharedPreferences = new UserSharedPreferences(mContext);
                    sharedPreferences.savePref(Constants.CONNECTION_STATUS, Constants.INTERNET_NOT_CONNECTED);
                } else {
                    Log.d(TAG, "Internet connected");
                    UserSharedPreferences sharedPreferences = new UserSharedPreferences(mContext);
                    sharedPreferences.savePref(Constants.CONNECTION_STATUS, Constants.INTERNET_CONNECTED);
                }
            }
        };
        return runnable;
    }

    public boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {


                if (Utils.isReachable()) {
                    Log.d(TAG,"host reachable");
                    return true;
                } else {
                    Log.d(TAG,"host not reachable");

                    return false;

                }


        }
        else
            return false;
    }


}
