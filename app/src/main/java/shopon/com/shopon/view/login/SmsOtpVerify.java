package shopon.com.shopon.view.login;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
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

import java.util.Map;
import java.util.Objects;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import shopon.com.shopon.R;
import shopon.com.shopon.datamodel.merchant.MerchantData;
import shopon.com.shopon.datamodel.merchant.Merchants;
import shopon.com.shopon.datamodel.merchant.MerchantsRealm;
import shopon.com.shopon.utils.Utils;
import shopon.com.shopon.view.base.BaseActivity;
import shopon.com.shopon.view.constants.Constants;
import shopon.com.shopon.viewmodel.login.preferences.UserSharedPreferences;

public class SmsOtpVerify extends BaseActivity implements ChildEventListener{

    private static final String TAG = SmsOtpVerify.class.getName();
    private UserSharedPreferences userSharedPreferences;
    private String mOtp;
    @Bind(R.id.verification_number)EditText verificationNumberView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userSharedPreferences = new UserSharedPreferences(this);
        setContentView(R.layout.sms_otp_verify);
        ButterKnife.bind(this);
        mContext = this;
        toolbarTitle.setText(getString(R.string.title_activity_sms_otp_verify));
        mOtp = userSharedPreferences.getPref(Constants.MERCHANT_OTP);

        Log.d(TAG,"OTP:"+mOtp);
        setOnEditListener();
    }

    private void checkIfNumberExists() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_MERCHANT_PREFIX+(String)userSharedPreferences.getPref(Constants.MERCHANT_MSISDN_PREF));
        queryRef  = mDatabase.orderByChild("mobile");//.equalTo((String)userSharedPreferences.getPref(Constants.MERCHANT_MSISDN_PREF))

        //queryRef.addChildEventListener(this);
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                hideProgress();
                Log.d(TAG,"onDataChanged snapshot:"+snapshot);

                System.out.println(snapshot.child("merchants"));

                if(snapshot.getValue() == null)
                {
                    proceedToNextScreen();
                }
                else
                {
                    //MerchantData merchantData = snapshot.getValue(MerchantData.class);
                    Merchants merchant = snapshot.child(Constants.FIREBASE_MERCHANT_PREFIX+(String)userSharedPreferences.getPref(Constants.MERCHANT_MSISDN_PREF)).child("merchants").getValue(Merchants.class);
                    Log.d(TAG,"merchant:"+merchant+" merchant number :"+merchant.getMobile());
                    launnchMainScreen();
                    writeToDb(merchant);
                    userSharedPreferences.savePref(Constants.MERCHANT_ID_PREF,merchant.getUserId());
                    userSharedPreferences.savePref(Constants.MERCHANT_MSISDN_PREF,merchant.getMobile());
                    userSharedPreferences.savePref(Constants.IS_RETURNING_CUSTOMER,true);
                    userSharedPreferences.savePref(Constants.CURRENT_LOGIN_STATE,Constants.LOGIN_COMPLETE);
                }

            }
            @Override public void onCancelled(DatabaseError error) {
                hideProgress();
                Log.d(TAG,"DatabaseError error:"+error.toString());
                Toast.makeText(mContext,"Please connect to internet and try again",Toast.LENGTH_LONG).show();
            }
        };
        queryRef.addValueEventListener(eventListener);
    }

    private void launnchMainScreen() {
        Intent intent = new Intent(this,ShopOnActivity.class);
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
                    Log.d(TAG, "verifying number  --- s:" + s.toString()+" mOtp:"+mOtp);
                    if (s.toString().equals(String.valueOf(mOtp))) {
                        showProgress(R.string.validate_num);
                        writeToDb();


                        checkIfNumberExists();
                    } else {
                        verificationNumberView.setText("");
                        Toast.makeText(mContext,getString(R.string.invalid_otp),Toast.LENGTH_LONG).show();
                    }
                    Log.d("DEBUG", "onEditorAction length is 4 color set");
                }
                //s is the current character in the eddittext after it is changed
            }
        });
    }

    private void proceedToNextScreen() {
        Intent intent = new Intent(this,ShopOnProfileCreation.class);
        startActivity(intent);
        userSharedPreferences.savePref(Constants.CURRENT_LOGIN_STATE,Constants.PROFILE_STATE);
        finish();
    }


    public void writeToDb() {
        int userId = 0;

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        MerchantsRealm user = realm.createObject(MerchantsRealm.class); // Create a new object
        userId = (int) Math.abs(Math.random() * 1000000);
        user.setUserId(userId);
        user.setMobile((String)userSharedPreferences.getPref(Constants.MERCHANT_MSISDN_PREF));
        realm.commitTransaction();
        savePref(userId);
    }

    public void writeToDb(Merchants merchant) {
        Log.d(TAG,"merchant:"+merchant);

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        MerchantsRealm user = realm.createObject(MerchantsRealm.class); // Create a new object
        user.setUserId(merchant.getUserId());
        user.setMobile(merchant.getMobile());
        user.setName(merchant.getName());
        user.setEmail(merchant.getEmail());
        realm.commitTransaction();
    }

    private void savePref(int user_id) {
        Log.d(TAG, "saving preferences");
        userSharedPreferences.savePref(Constants.MERCHANT_ID_PREF, user_id);
        userSharedPreferences.savePref(Constants.CURRENT_LOGIN_STATE,Constants.PROFILE_STATE);
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        System.out.println(dataSnapshot.getValue());
        Log.d(TAG,"onChildAdded");

        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
            Merchants merchant = postSnapshot.getValue(Merchants.class);
            Log.d(TAG,"merchant data:"+merchant.getMobile());
            if(merchant.getMobile().equals(userSharedPreferences.getPref(Constants.MERCHANT_MSISDN_PREF))){
                Intent intent = new Intent(this,ShopOnActivity.class);
                startActivity(intent);
                finish();
            }
        }
        //proceedToNextScreen();
        //Log.d(TAG,"onChildAdded mobile:"+mobile+" preference mobile number:"+userSharedPreferences.getPref(Constants.MERCHANT_MSISDN_PREF));


    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        Log.d(TAG,"onChildChanged");
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        Log.d(TAG,"onChildRemoved");
    }



    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        Log.d(TAG,"onChildMoved");
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.d(TAG,"onCancelled databaseError:"+databaseError.getMessage());
        proceedToNextScreen();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        queryRef.removeEventListener(eventListener);

    }

    @OnClick(R.id.resend_sms)
    public void resendSms(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Utils.sendSmsToMobileAPI22(this,mOtp,(String)userSharedPreferences.getPref(Constants.MERCHANT_MSISDN_PREF));
        } else {
            Utils.sendSmsToMobile(this,mOtp,(String)userSharedPreferences.getPref(Constants.MERCHANT_MSISDN_PREF));
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
        handler.postDelayed(displayActionLayout,6000);
    }

}
