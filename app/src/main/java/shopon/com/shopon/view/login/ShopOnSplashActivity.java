package shopon.com.shopon.view.login;

import android.content.Intent;
import android.support.annotation.BinderThread;
import android.support.annotation.IntDef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import butterknife.ButterKnife;
import shopon.com.shopon.R;
import shopon.com.shopon.preferences.UserSharedPreferences;
import shopon.com.shopon.view.constants.Constants;


import static shopon.com.shopon.view.constants.Constants.CATEGORY_STATE;
import static shopon.com.shopon.view.constants.Constants.LOGIN_COMPLETE;
import static shopon.com.shopon.view.constants.Constants.MSISDN_STATE;
import static shopon.com.shopon.view.constants.Constants.OTP_STATE;
import static shopon.com.shopon.view.constants.Constants.PROFILE_STATE;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ShopOnSplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_shop_on_splash);

        delayHandler();
    }

    private void nextScreen() {
        UserSharedPreferences userSharedPreferences = new UserSharedPreferences(this);
        int loginState = MSISDN_STATE;
        try {
            loginState = userSharedPreferences.getPref(Constants.CURRENT_LOGIN_STATE);
        } catch (Exception e) {
            e.printStackTrace();
        }


        //mCurrentState = loginState;
        Class classType = ShopOnMsisdnActivity.class;
        switch (loginState) {
            case MSISDN_STATE:
                classType = ShopOnMsisdnActivity.class;
                break;
            case OTP_STATE:
                classType = SmsOtpVerify.class;
                break;
            case PROFILE_STATE:
                classType = ShopOnProfileCreation.class;
                break;
            case CATEGORY_STATE:
                classType = ShopCategoryActivity.class;
                break;
            case LOGIN_COMPLETE:
                classType = ShopOnActivity.class;
                break;
        }
        Intent intent = new Intent(this, classType);
        startActivity(intent);
        finish();
    }

    private void delayHandler() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                nextScreen();
            }
        }, Constants.SPLASH_TIME_OUT);
    }

}
