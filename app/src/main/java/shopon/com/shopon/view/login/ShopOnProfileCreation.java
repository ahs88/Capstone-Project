package shopon.com.shopon.view.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import shopon.com.shopon.R;
import shopon.com.shopon.datamodel.merchant.MerchantsRealm;
import shopon.com.shopon.preferences.UserSharedPreferences;
import shopon.com.shopon.utils.Utils;
import shopon.com.shopon.view.constants.Constants;


public class ShopOnProfileCreation extends AppCompatActivity {

    @Bind(R.id.user_name)
    EditText userNameView;

    @Bind(R.id.user_name_err)
    TextView userNameErrView;

    @Bind(R.id.email_err)
    TextView emailError;

    @Bind(R.id.email_id)
    EditText emailIdView;

    @Bind(R.id.toolbar_title)
    TextView toolbarTitleView;
    private UserSharedPreferences userSharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.shop_on_profile);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        toolbarTitleView.setText(getString(R.string.create_profile));
    }

    public void updateProfile(){

        Realm realm = Realm.getDefaultInstance();

        userSharedPreferences = new UserSharedPreferences(this);

        MerchantsRealm merchants = realm.where(MerchantsRealm.class).equalTo("userId",(Integer) userSharedPreferences.getPref(Constants.MERCHANT_ID_PREF)).findFirst();
        realm.beginTransaction();
        merchants.setEmail(emailIdView.getText().toString());
        merchants.setName(userNameView.getText().toString());
        realm.commitTransaction();
    }

    private boolean validateProfile() {
        if(TextUtils.isEmpty(userNameView.getText().toString()) || userNameView.getText().toString().length()<4){
            userNameErrView.setText(getString(R.string.profile_name_err));
            return false;
        }
        else
        {
            userNameErrView.setText("");
        }

        if(!Utils.isValidEmail(emailIdView.getText().toString()) ){
            emailError.setText(getString(R.string.email_err));
            return false;
        }
        else
        {
            emailError.setText("");
        }
        return true;
    }

    @OnClick(R.id.bazaar_login_button)
    public void createProfile(){
        if(!validateProfile()){
            return;
        }
        updateProfile();
        userSharedPreferences.savePref(Constants.CURRENT_LOGIN_STATE,Constants.CATEGORY_STATE);
        nextScreen();
    }

    private void nextScreen() {
        Intent intent = new Intent(this,ShopCategoryActivity.class);
        intent.putExtra(Constants.UPDATE_MERCHANT_DETAIL,true);
        startActivity(intent);
        finish();
    }


}
