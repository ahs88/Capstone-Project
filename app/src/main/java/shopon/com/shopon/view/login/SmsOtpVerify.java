package shopon.com.shopon.view.login;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import shopon.com.shopon.R;
import shopon.com.shopon.datamodel.merchant.Merchants;
import shopon.com.shopon.db.provider.ShopOnContract;
import shopon.com.shopon.db.provider.ShopOnContractRealm;
import shopon.com.shopon.preferences.UserSharedPreferences;
import shopon.com.shopon.utils.Utils;
import shopon.com.shopon.view.base.BaseActivity;
import shopon.com.shopon.view.constants.Constants;


public class SmsOtpVerify extends BaseActivity implements ChildEventListener, SharedPreferences.OnSharedPreferenceChangeListener, DialogInterface.OnClickListener {

    private static final String TAG = SmsOtpVerify.class.getName();
    private UserSharedPreferences userSharedPreferences;
    private String mOtp;
    @Bind(R.id.verification_number)
    EditText verificationNumberView;
    @Bind(R.id.tool_bar)
    Toolbar toolbar;
    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;
    @Bind(R.id.resend_sms)
    Button resendSms;

    @Bind(R.id.action_buttons)
    LinearLayout actionButtonLayout;

    private DatabaseReference mDatabase;
    private Context mContext;
    private Query queryRef;
    private ValueEventListener eventListener;
    Constants.DialogAction dialogAction = Constants.DialogAction.NO_DIALOG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userSharedPreferences = new UserSharedPreferences(this);
        //save pref with some value
        userSharedPreferences.savePref(Constants.CONNECTION_STATUS, 1000);

        setContentView(R.layout.sms_otp_verify);
        ButterKnife.bind(this);
        mContext = this;
        toolbarTitle.setText(getString(R.string.title_activity_sms_otp_verify));
        mOtp = userSharedPreferences.getPref(Constants.MERCHANT_OTP);


        Log.d(TAG, "OTP:" + mOtp);
        setOnEditListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        userSharedPreferences.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        userSharedPreferences.getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    private void checkIfNumberExists() {
        //asuming google servers never go down check if it can be pinged
        if (!Utils.isReachable()) {
            hideProgress();
            Utils.displayConnectToInternet(this, this);
            verificationNumberView.setText("");
            return;
        }
        showProgress(R.string.validate_num);
        mDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_MERCHANT_PREFIX + (String) userSharedPreferences.getPref(Constants.MERCHANT_MSISDN_PREF));
        queryRef = mDatabase.orderByChild("mobile");

        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                hideProgress();
                Log.d(TAG, "onDataChanged snapshot:" + snapshot);


                if (snapshot.getValue() == null) {
                    proceedToNextScreen();
                } else {
                    //MerchantData merchantData = snapshot.getValue(MerchantData.class);
                    Merchants merchant = snapshot.child(Constants.FIREBASE_MERCHANT_PREFIX + (String) userSharedPreferences.getPref(Constants.MERCHANT_MSISDN_PREF)).getValue(Merchants.class);
                    Log.d(TAG, "merchant:" + merchant + " merchant number :" + merchant.getMobile());
                    writeToDb(merchant);
                    userSharedPreferences.savePref(Constants.MERCHANT_ID_PREF, merchant.getUserId());
                    userSharedPreferences.savePref(Constants.MERCHANT_MSISDN_PREF, merchant.getMobile());
                    userSharedPreferences.savePref(Constants.IS_RETURNING_CUSTOMER, true);
                    userSharedPreferences.savePref(Constants.CURRENT_LOGIN_STATE, Constants.LOGIN_COMPLETE);
                    launnchMainScreen();
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                hideProgress();
                Log.d(TAG, "DatabaseError error:" + error.toString());
                Toast.makeText(mContext, "Please connect to internet and try again", Toast.LENGTH_LONG).show();
            }
        };
        queryRef.addValueEventListener(eventListener);
    }

    private void launnchMainScreen() {
        Intent intent = new Intent(this, ShopOnActivity.class);
        startActivity(intent);
        finish();
    }

    private void setOnEditListener() {


        verificationNumberView.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() == 4) {
                    Log.d(TAG, "verifying number  --- s:" + s.toString() + " mOtp:" + mOtp);
                    if (s.toString().equals(String.valueOf(mOtp))) {

                        writeToDb();
                        checkIfNumberExists();
                    } else {
                        verificationNumberView.setText("");
                        Toast.makeText(mContext, getString(R.string.invalid_otp), Toast.LENGTH_LONG).show();
                    }
                    Log.d("DEBUG", "onEditorAction length is 4 color set");
                }
                //s is the current character in the eddittext after it is changed
            }
        });
    }

    private void proceedToNextScreen() {
        Intent intent = new Intent(this, ShopOnProfileCreation.class);
        startActivity(intent);
        userSharedPreferences.savePref(Constants.CURRENT_LOGIN_STATE, Constants.PROFILE_STATE);
        finish();
    }


    public void writeToDb() {
        int userId = 0;
        userId = (int) Math.abs(Math.random() * 1000);
        ContentValues contentValues = new ContentValues();
        contentValues.put(ShopOnContract.Entry.COLUMN_USER_ID, userId);
        contentValues.put(ShopOnContract.Entry.COLUMN_MOBILE, (String) userSharedPreferences.getPref(Constants.MERCHANT_MSISDN_PREF));
        Log.d(TAG, "initial write id:" + userId + " number:" + (String) userSharedPreferences.getPref(Constants.MERCHANT_MSISDN_PREF));
        Uri uri = getContentResolver().insert(ShopOnContract.Entry.CONTENT_MERCHANT_URI, contentValues);
        savePref(userId);
    }

    public void writeToDb(Merchants merchant) {
        Log.d(TAG, "merchant:" + merchant + " merchant");
        ContentValues contentValues = new ContentValues();
        contentValues.put(ShopOnContract.Entry.COLUMN_USER_ID, merchant.getUserId());
        contentValues.put(ShopOnContract.Entry.COLUMN_MOBILE, merchant.getMobile());
        contentValues.put(ShopOnContract.Entry.COLUMN_NAME, merchant.getName());
        contentValues.put(ShopOnContract.Entry.COLUMN_EMAIL, merchant.getEmail());
        contentValues.put(ShopOnContract.Entry.COLUMN_MERCHANT_CATEGORY, merchant.getMerchentCategory());
        getContentResolver().insert(ShopOnContractRealm.Entry.CONTENT_MERCHANT_URI, contentValues);
    }

    private void savePref(int user_id) {
        Log.d(TAG, "saving preferences");
        userSharedPreferences.savePref(Constants.MERCHANT_ID_PREF, user_id);
        //userSharedPreferences.savePref(Constants.CURRENT_LOGIN_STATE,Constants.PROFILE_STATE);
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        System.out.println(dataSnapshot.getValue());
        Log.d(TAG, "onChildAdded");

        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
            Merchants merchant = postSnapshot.getValue(Merchants.class);
            Log.d(TAG, "merchant data:" + merchant.getMobile());
            if (merchant.getMobile().equals(userSharedPreferences.getPref(Constants.MERCHANT_MSISDN_PREF))) {
                Intent intent = new Intent(this, ShopOnActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        Log.d(TAG, "onChildChanged");
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        Log.d(TAG, "onChildRemoved");
    }


    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        Log.d(TAG, "onChildMoved");
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.d(TAG, "onCancelled databaseError:" + databaseError.getMessage());
        proceedToNextScreen();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        queryRef.removeEventListener(eventListener);

    }

    @OnClick(R.id.resend_sms)
    public void resendSms() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Utils.sendSmsToMobileAPI22(this, mOtp, (String) userSharedPreferences.getPref(Constants.MERCHANT_MSISDN_PREF));
        } else {
            Utils.sendSmsToMobile(this, mOtp, (String) userSharedPreferences.getPref(Constants.MERCHANT_MSISDN_PREF));
        }
        actionButtonLayout.setVisibility(View.GONE);
        delayForAMin();
    }

    private void delayForAMin() {
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable displayActionLayout = new Runnable() {
            @Override
            public void run() {
                actionButtonLayout.setVisibility(View.VISIBLE);
            }
        };
        handler.postDelayed(displayActionLayout, 6000);
    }


    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        dialogAction = Constants.DialogAction.NO_DIALOG;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        synchronized (userSharedPreferences) {
            Log.d(TAG, "onPreferenceChange");

            if (userSharedPreferences != null) {
                int status = userSharedPreferences.getPref(Constants.CONNECTION_STATUS);
                if (status == Constants.INTERNET_NOT_CONNECTED && dialogAction != Constants.DialogAction.NETWORK_CHECK) {
                    hideProgress();
                    Utils.displayConnectToInternet(this, this);
                    dialogAction = Constants.DialogAction.NETWORK_CHECK;
                }
            }
        }
    }
}

