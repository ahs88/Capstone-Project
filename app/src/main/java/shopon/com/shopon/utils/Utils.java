package shopon.com.shopon.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

import io.realm.RealmResults;
import shopon.com.shopon.R;
import shopon.com.shopon.datamodel.customer.Customers;
import shopon.com.shopon.datamodel.customer.CustomersRealm;
import shopon.com.shopon.datamodel.merchant.Merchants;
import shopon.com.shopon.datamodel.offer.Offer;
import shopon.com.shopon.datamodel.offer.OfferRealm;
import shopon.com.shopon.view.base.AlertDialog;
import shopon.com.shopon.view.constants.Constants;
import shopon.com.shopon.view.contact.AlphabetListAdapter;
import shopon.com.shopon.widget.WidgetProvider;


public class Utils {

    public static String TAG = Utils.class.getName();

    public static ArrayList<String> getUinqueElementsInList(List<String> inputList) {
        //pass unique subscribed tags to intent - remove duplicates
        LinkedHashSet set = new LinkedHashSet();
        set.addAll(inputList);
        ArrayList list = new ArrayList();
        list.addAll(set);
        return list;
    }

    public static ArrayList<AlphabetListAdapter.Item> getUinqueElementsInList(ArrayList<AlphabetListAdapter.Item> inputList) {
        //pass unique subscribed tags to intent - remove duplicates

        LinkedHashSet set = new LinkedHashSet();
        set.addAll(inputList);
        ArrayList list = new ArrayList();
        list.addAll(set);
        return list;
    }

    public static String generateRandomOTP(int size) {

        StringBuilder generatedToken = new StringBuilder();
        try {
            SecureRandom number = null;
            try {
                number = SecureRandom.getInstance("SHA1PRNG");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            // Generate 20 integers 0..20
            for (int i = 0; i < size; i++) {
                generatedToken.append(number.nextInt(9));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return generatedToken.toString();
    }

    public static boolean sendSMS(Context ctx, int simID, String toNum, String centerNum, String smsText, PendingIntent sentIntent, PendingIntent deliveryIntent) {
        String name;

        try {
            if (simID == 0) {
                name = "isms";
                // for model : "Philips T939" name = "isms0"
            } else if (simID == 1) {
                name = "isms2";
            } else {
                throw new Exception("can not get service which for sim '" + simID + "', only 0,1 accepted as values");
            }
            Method method = Class.forName("android.os.ServiceManager").getDeclaredMethod("getService", String.class);
            method.setAccessible(true);
            Object param = method.invoke(null, name);

            method = Class.forName("com.android.internal.telephony.ISms$Stub").getDeclaredMethod("asInterface", IBinder.class);
            method.setAccessible(true);
            Object stubObj = method.invoke(null, param);
            if (Build.VERSION.SDK_INT < 18) {
                method = stubObj.getClass().getMethod("sendText", String.class, String.class, String.class, PendingIntent.class, PendingIntent.class);
                method.invoke(stubObj, toNum, centerNum, smsText, sentIntent, deliveryIntent);
            } else {
                method = stubObj.getClass().getMethod("sendText", String.class, String.class, String.class, String.class, PendingIntent.class, PendingIntent.class);
                method.invoke(stubObj, ctx.getPackageName(), toNum, centerNum, smsText, sentIntent, deliveryIntent);
            }

            return true;
        } catch (ClassNotFoundException e) {
            Log.e("apipas", "ClassNotFoundException:" + e.getMessage());
        } catch (NoSuchMethodException e) {
            Log.e("apipas", "NoSuchMethodException:" + e.getMessage());
        } catch (InvocationTargetException e) {
            Log.e("apipas", "InvocationTargetException:" + e.getMessage());
        } catch (IllegalAccessException e) {
            Log.e("apipas", "IllegalAccessException:" + e.getMessage());
        } catch (Exception e) {
            Log.e("apipas", "Exception:" + e.getMessage());
        }
        return false;
    }


    public static boolean sendMultipartTextSMS(Context ctx, int simID, String toNum, String centerNum, ArrayList<String> smsTextlist, ArrayList<PendingIntent> sentIntentList, ArrayList<PendingIntent> deliveryIntentList) {
        String name;
        try {
            if (simID == 0) {
                name = "isms";
                // for model : "Philips T939" name = "isms0"
            } else if (simID == 1) {
                name = "isms2";
            } else {
                throw new Exception("can not get service which for sim '" + simID + "', only 0,1 accepted as values");
            }
            Method method = Class.forName("android.os.ServiceManager").getDeclaredMethod("getService", String.class);
            method.setAccessible(true);
            Object param = method.invoke(null, name);

            method = Class.forName("com.android.internal.telephony.ISms$Stub").getDeclaredMethod("asInterface", IBinder.class);
            method.setAccessible(true);
            Object stubObj = method.invoke(null, param);
            if (Build.VERSION.SDK_INT < 18) {
                method = stubObj.getClass().getMethod("sendMultipartText", String.class, String.class, List.class, List.class, List.class);
                method.invoke(stubObj, toNum, centerNum, smsTextlist, sentIntentList, deliveryIntentList);
            } else {
                method = stubObj.getClass().getMethod("sendMultipartText", String.class, String.class, String.class, List.class, List.class, List.class);
                method.invoke(stubObj, ctx.getPackageName(), toNum, centerNum, smsTextlist, sentIntentList, deliveryIntentList);
            }
            return true;
        } catch (ClassNotFoundException e) {
            Log.e("apipas", "ClassNotFoundException:" + e.getMessage());
        } catch (NoSuchMethodException e) {
            Log.e("apipas", "NoSuchMethodException:" + e.getMessage());
        } catch (InvocationTargetException e) {
            Log.e("apipas", "InvocationTargetException:" + e.getMessage());
        } catch (IllegalAccessException e) {
            Log.e("apipas", "IllegalAccessException:" + e.getMessage());
        } catch (Exception e) {
            Log.e("apipas", "Exception:" + e.getMessage());
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public static List<Integer> getActiveSubscriptionInfoList(Context context) {
        List<Integer> subId = new ArrayList<>();
        SubscriptionManager subscriptionManager = SubscriptionManager.from(context);
        List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
        if (subscriptionInfoList == null) {
            return subId;
        }

        for (SubscriptionInfo subscriptionInfo : subscriptionInfoList) {
            int subscriptionId = subscriptionInfo.getSubscriptionId();
            Log.d("apipas", "subscriptionId:" + subscriptionId);
            subId.add(subscriptionId);
        }
        return subId;
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static boolean isValidMobile(String phone) {
        return (phone.length() == 10) && (phone.matches("[-+]?\\d*\\.?\\d+")) && android.util.Patterns.PHONE.matcher(phone).matches();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public static boolean sendSmsToMobileAPI22(Context context, String otp, String recipient) {

        List<Integer> subId = Utils.getActiveSubscriptionInfoList(context);
        if (subId.size() == 0) {
            return false;
        }
        for (int id : subId) {
            SmsManager.getSmsManagerForSubscriptionId(id).sendTextMessage(recipient, null, String.valueOf(otp), null, null);
        }
        return true;
    }

    public static boolean sendSmsToMobile(Context context, String otp, String recipient) {
        Log.d(TAG, "recipient:" + recipient);
        String textSms = otp;
        ArrayList<String> messageList = SmsManager.getDefault().divideMessage(textSms);
        if (messageList.size() > 1) {
            return Utils.sendMultipartTextSMS(context, 0, recipient, null, messageList, null, null);
        } else {
            return Utils.sendSMS(context, 0, recipient, null, textSms, null, null);
        }
    }

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return Constants.WIFI_ENABLED;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return Constants.MOBILE_DATA_ENABLED;
        }
        return Constants.INTERNET_NOT_CONNECTED;
    }

    public static String getDateDisplayText(String dateCardDate) {
        String imageText = "";
        if (!TextUtils.isEmpty(dateCardDate)) {
            SimpleDateFormat df = new SimpleDateFormat("hh:mm a dd-MMM, yyyy");
            Date parsed = null;
            try {
                parsed = df.parse(dateCardDate);
            } catch (ParseException e) {
                e.printStackTrace();
                Log.d(TAG, "date parse error:" + e.getMessage());
                return "";
            }
            String dayString = (String) DateFormat.format("dd", parsed);
            GregorianCalendar newCalendar = new GregorianCalendar();
            System.out.println("parsed date: " + parsed);
            newCalendar.setTime(parsed);
            SimpleDateFormat sdfWeekDay = new SimpleDateFormat("EE");
            String dayOfTheWeek = sdfWeekDay.format(newCalendar.getTime());
            imageText = dayString + "\n" + dayOfTheWeek.toUpperCase();
            // Set AlertTex
        }
        return imageText;
    }

    public static String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("hh:mm a dd-MMM, yyyy");
        String formattedDate = df.format(c.getTime());
        return formattedDate;

    }

    public static void displayConnectToInternet(Activity context, DialogInterface.OnClickListener listener) {
        AlertDialog alertDialog = AlertDialog.newInstance(context.getString(R.string.internet_warning), context.getString(R.string.connect_to_internet));
        alertDialog.setPositiveButton(listener);
        alertDialog.show(context.getFragmentManager(), TAG);
    }

    public static void displaySMSWarning(Activity context, DialogInterface.OnClickListener listener) {
        AlertDialog alertDialog = AlertDialog.newInstance(context.getString(R.string.sms_warning), context.getString(R.string.carrier_charge_warning));
        alertDialog.setNegativeButton(listener);
        alertDialog.setPositiveButton(listener);
        alertDialog.show(context.getFragmentManager(), TAG);
    }

    public static boolean isReachable() {

        System.out.println("executeCommand");
        Runtime runtime = Runtime.getRuntime();
        try {
            Process mIpAddrProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int mExitValue = mIpAddrProcess.waitFor();
            System.out.println(" mExitValue " + mExitValue);
            if (mExitValue == 0) {
                return true;
            } else {
                return false;
            }
        } catch (InterruptedException ignore) {
            ignore.printStackTrace();
            System.out.println(" Exception:" + ignore);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(" Exception:" + e);
        }
        return false;
    }


    public static Merchants createMerchantFromCursor(Cursor cursor) {
        Merchants merchants = new Merchants();
        merchants.setUserId(cursor.getInt(0));
        merchants.setName(cursor.getString(1));
        merchants.setEmail(cursor.getString(2));
        merchants.setMobile(cursor.getString(3));
        merchants.setMerchentCategory(cursor.getString(4));
        merchants.setCreatedAt(cursor.getString(5));
        return merchants;
    }

    public static Offer createOfferFromCursor(Cursor cursor) {
        Log.d(TAG, "offer status:" + cursor.getInt(2) + " offer text:" + cursor.getString(1));
        Offer offer = new Offer();
        offer.setOfferId(cursor.getInt(0));
        offer.setOfferText(cursor.getString(1));
        offer.setOfferStatus((cursor.getInt(2) == 0) ? false : true);
        offer.setNumbers(cursor.getString(3));
        offer.setDeliverMessageOn(cursor.getString(4));
        return offer;
    }

    public static Customers createCustomerFromCursor(Cursor cursor) {
        Customers customer = new Customers();
        customer.setId(cursor.getInt(0));
        customer.setName(cursor.getString(1));
        customer.setEmail(cursor.getString(2));
        customer.setMobile(cursor.getString(3));
        customer.setIntrestedIn(cursor.getString(4));
        return customer;
    }

    public static void refreshAppWidget(Context context) {
        Intent intent = new Intent(context,WidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
// Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
// since it seems the onUpdate() is only fired on that:
        int[] ids = {R.xml.widget_provider};
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        context.sendBroadcast(intent);
    }

    public static String getCountryDialCode(Context context){
        String contryId = null;
        String contryDialCode = null;

        TelephonyManager telephonyMngr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        contryId = telephonyMngr.getSimCountryIso().toUpperCase();
        String[] arrContryCode=context.getResources().getStringArray(R.array.DialingCountryCode);
        for(int i=0; i<arrContryCode.length; i++){
            String[] arrDial = arrContryCode[i].split(",");
            if(arrDial[1].trim().equals(contryId.trim())){
                contryDialCode = arrDial[0];
                break;
            }
        }
        return contryDialCode;
    }

}
