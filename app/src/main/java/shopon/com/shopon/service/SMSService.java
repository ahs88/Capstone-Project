package shopon.com.shopon.service;

import android.app.IntentService;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import shopon.com.shopon.R;
import shopon.com.shopon.datamodel.offer.Offer;
import shopon.com.shopon.datamodel.offer.OfferRealm;
import shopon.com.shopon.db.OfferRealmUtil;
import shopon.com.shopon.db.provider.ShopOnContract;
import shopon.com.shopon.preferences.UserSharedPreferences;
import shopon.com.shopon.utils.Utils;
import shopon.com.shopon.view.constants.Constants;


public class SMSService extends IntentService {

    private static final String TAG = SMSService.class.getName();
    private DatabaseReference mDatabase;

    public SMSService() {
        super("SMSService");
    }

    public SMSService(String name) {
        super(name);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int offer_id = intent.getExtras().getInt(Constants.EXTRAS_OFFER_ID);
        intent.removeExtra(Constants.EXTRAS_OFFER_ID);
        Log.d(TAG, "onHandleIntent offer_id:" + offer_id);

        //change status to sent
        ContentValues contentValues = new ContentValues();
        contentValues.put(ShopOnContract.Entry.COLUMN_OFFER_STATUS, true);
        getContentResolver().update(ShopOnContract.Entry.CONTENT_OFFER_URI, contentValues, ShopOnContract.Entry.COLUMN_OFFER_ID + "=?", new String[]{String.valueOf(offer_id)});

        Offer offer_remote = null;
        Cursor cursor = getContentResolver().query(ShopOnContract.Entry.CONTENT_OFFER_URI, null, ShopOnContract.Entry.COLUMN_OFFER_ID + "=?", new String[]{String.valueOf(offer_id)}, null);
        if (cursor != null) {
            cursor.moveToFirst();
            offer_remote = Utils.createOfferFromCursor(cursor);

            //update in remote db

            Log.d(TAG, "updating remote offer with id:" + offer_remote.getOfferId());
            UserSharedPreferences userSharedPreference = new UserSharedPreferences(this);
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child(Constants.OFFER_PREFIX + Constants.FIREBASE_MERCHANT_PREFIX + (String) userSharedPreference.getPref(Constants.MERCHANT_MSISDN_PREF)).child(String.valueOf(offer_remote.getOfferId())).setValue(offer_remote);

            //send sms to recipients
            //TODO need to replace with a decent sms API which doesnt charge much
            String recipients[] = offer_remote.getNumbers().replace("[", "").replace("]", "").split(",");
            for (int i = 0; i < recipients.length; i++) {

                Log.d(TAG, "sending sms to recipient " + i + " msisdn:" + recipients[i]);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    sendSmsToMobileAPI22(offer_remote.getOfferText(), recipients[i]);
                } else {
                    sendSmsToMobile(offer_remote.getOfferText(), recipients[i]);
                }
            }

        } else {
            Toast.makeText(this, getString(R.string.failed_to_send_sms), Toast.LENGTH_SHORT).show();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    private boolean sendSmsToMobileAPI22(String otp, String msisdn) {

        List<Integer> subId = Utils.getActiveSubscriptionInfoList(this);
        if (subId.size() == 0)
            return false;
        for (int id : subId) {
            SmsManager.getSmsManagerForSubscriptionId(id).sendTextMessage(msisdn, null, String.valueOf(otp), null, null);
        }
        return true;
    }

    private boolean sendSmsToMobile(String otp, String msisdn) {
        String textSms = otp;
        ArrayList<String> messageList = SmsManager.getDefault().divideMessage(textSms);
        if (messageList.size() > 1) {
            return Utils.sendMultipartTextSMS(this, 0, msisdn, null, messageList, null, null);
        } else {
            return Utils.sendSMS(this, 0, msisdn, null, textSms, null, null);
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }
}
