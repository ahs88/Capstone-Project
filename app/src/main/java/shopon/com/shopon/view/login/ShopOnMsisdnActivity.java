package shopon.com.shopon.view.login;

import android.Manifest;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.Editable;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import io.realm.Realm;


import shopon.com.shopon.R;

import shopon.com.shopon.preferences.UserSharedPreferences;
import shopon.com.shopon.utils.Utils;
import shopon.com.shopon.view.base.AlertDialog;
import shopon.com.shopon.view.base.BaseActivity;

import shopon.com.shopon.view.constants.Constants;
import shopon.com.shopon.view.dialogs.ProgressDialog;


public class ShopOnMsisdnActivity extends BaseActivity implements DialogInterface.OnClickListener {

    private static final String TAG = ShopOnMsisdnActivity.class.getCanonicalName();
    @BindView(R.id.tool_bar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.country_code)
    EditText countryCodeView;
    @BindView(R.id.phone_number)
    EditText msisdn;
    @BindView(R.id.acc_continue)
    TextView accContinue;
    private ShopOnMsisdnActivity mContext;


    Constants.DialogAction currentDialogAction = Constants.DialogAction.NETWORK_CHECK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_on_msisdn);
        ButterKnife.bind(this);
        setupActionBar(toolbar);
        toolbarTitle.setText(getString(R.string.verify_phone_number));

        mContext = this;

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE
        }, 1);

        setDefaultCountryCode();

    }

    private void setDefaultCountryCode() {
        String cc = Utils.getCountryDialCode(this);
        countryCodeView.setText("+"+cc);
        msisdn.requestFocus();
    }

    @OnClick(R.id.acc_continue)
    public void navigateToCategory() {
        if (msisdn.getText().toString().length() == 10) { //check if mobile number valid
            if (Utils.getConnectivityStatus(this) == Constants.INTERNET_NOT_CONNECTED) {
                Utils.displayConnectToInternet(this, this);
                return;
            }
            currentDialogAction = Constants.DialogAction.SMS_CHECK;
            boolean isGranted = checkIfPermissionGranted(this, Manifest.permission.SEND_SMS);
            if (!isGranted) {
                Log.d(TAG, "send sms not granted");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        Constants.MY_PERMISSIONS_REQUEST_SEND_SMS);
            } else {
                isGranted = checkIfPermissionGranted(this, Manifest.permission.READ_PHONE_STATE);
                if (!isGranted) {
                    Log.d(TAG, "phone state not granted");
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_PHONE_STATE},
                            Constants.MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
                } else {
                    Utils.displaySMSWarning(this, this);
                }
            }

        } else {
            Toast.makeText(this, "Please Enter a valid number", Toast.LENGTH_SHORT);
        }

    }


    private void proceedToOTP() {
        String otp = Utils.generateRandomOTP(4);
        boolean status = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            status = Utils.sendSmsToMobileAPI22(this, otp, msisdn.getText().toString());
        } else {
            status = Utils.sendSmsToMobile(this, otp, msisdn.getText().toString());
        }

        if (status) {
            savePref(otp);
            Intent intent = new Intent(this, SmsOtpVerify.class);
            startActivity(intent);
            finish();
        } else {
            displaySIMWarning();
        }
    }

    private void displaySIMWarning() {
        AlertDialog alertDialog = AlertDialog.newInstance(getString(R.string.sim_warning), getString(R.string.sim_required_err));
        alertDialog.setPositiveButton(this);
        alertDialog.show(getFragmentManager(), TAG);
    }


    @OnTextChanged(R.id.phone_number)
    void onTextChanged(Editable editable) {
        Log.d(TAG, "onTextChanged:" + editable.toString());
        if (msisdn.getText().toString().length() == 10) {
            accContinue.setBackgroundColor(Color.RED);
        } else {
            accContinue.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
    }


    private void savePref(String otp) {
        Log.d(TAG, "saving preferences otp:" + otp);
        UserSharedPreferences userSharedPreferences = new UserSharedPreferences(this);
        userSharedPreferences.savePref(Constants.MERCHANT_OTP, otp);
        userSharedPreferences.savePref(Constants.MERCHANT_MSISDN_PREF, msisdn.getText().toString());
        userSharedPreferences.savePref(Constants.CURRENT_LOGIN_STATE, Constants.OTP_STATE);
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        Log.d(TAG, "onclick of dialog i:" + i);
        if (currentDialogAction == Constants.DialogAction.SMS_CHECK) {
            if (i == DialogInterface.BUTTON_POSITIVE) {
                proceedToOTP();
            } else {
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    public static boolean checkIfPermissionGranted(Activity activity, String permission) {
        int permissionCheck = ContextCompat.checkSelfPermission(activity,
                permission);
        Log.d(TAG, "permissionCheck:" + permissionCheck + " permission :" + permission);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (checkIfPermissionGranted(this, Manifest.permission.SEND_SMS)) {
                    Log.d(TAG, "permission request send sms granted");
                    boolean isGranted = checkIfPermissionGranted(this, Manifest.permission.READ_PHONE_STATE);

                    if (!isGranted) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.READ_PHONE_STATE},
                                Constants.MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
                    } else {
                        Utils.displaySMSWarning(mContext, mContext);
                    }

                }
            }
            case Constants.MY_PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                if (checkIfPermissionGranted(this, Manifest.permission.READ_PHONE_STATE)) {
                    Log.d(TAG, "permission request read phone state granted");
                    boolean isGranted = checkIfPermissionGranted(this, Manifest.permission.SEND_SMS);
                    // permission was granted, yay! Do the
                    if (!isGranted) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.SEND_SMS},
                                Constants.MY_PERMISSIONS_REQUEST_SEND_SMS);
                    } else {
                        Utils.displaySMSWarning(mContext, mContext);
                    }
                }
            }
        }
    }


}
