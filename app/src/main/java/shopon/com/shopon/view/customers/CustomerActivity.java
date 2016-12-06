package shopon.com.shopon.view.customers;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import io.realm.Realm;
import shopon.com.shopon.R;
import shopon.com.shopon.ShopOn;
import shopon.com.shopon.datamodel.customer.CustomerData;
import shopon.com.shopon.datamodel.customer.Customers;
import shopon.com.shopon.datamodel.customer.CustomersRealm;
import shopon.com.shopon.datamodel.merchant.MerchantData;
import shopon.com.shopon.datamodel.merchant.MerchantsRealm;
import shopon.com.shopon.db.CustomerRealmUtil;
import shopon.com.shopon.db.provider.ShopOnContract;
import shopon.com.shopon.preferences.UserSharedPreferences;
import shopon.com.shopon.remote.FireBaseUtils;
import shopon.com.shopon.utils.Utils;
import shopon.com.shopon.view.base.BaseActivity;
import shopon.com.shopon.view.constants.Constants;
import shopon.com.shopon.view.customers.dummy.CustomerContent;
import shopon.com.shopon.view.login.ShopCategoryActivity;
import shopon.com.shopon.view.tagview.Tag.OnTagClickListener;
import shopon.com.shopon.view.tagview.Tag.OnTagDeleteListener;
import shopon.com.shopon.view.tagview.Tag.Tag;
import shopon.com.shopon.view.tagview.Tag.TagView;


public class CustomerActivity extends BaseActivity {


    private static final String TAG = ShopOn.class.getCanonicalName();
    @Bind(R.id.name)
    public EditText name;

    @Bind(R.id.mobile_number)
    public EditText mobile;

    @Bind(R.id.name_err)
    TextView nameError;
    @Bind(R.id.mobile_err)
    public TextView mobileError;
    @Bind(R.id.email_err)
    public TextView emailError;
    @Bind(R.id.interest_err)
    public TextView interestError;


    @Bind(R.id.add_cutomer)
    public Button addCustomer;

    @Bind(R.id.tags)
    public TagView tagGroup;
    @Bind(R.id.tool_bar)
    Toolbar toolbar;
    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;
    protected ArrayList<String> categoryList = new ArrayList<>();

    @Bind(R.id.email)
    public EditText email;
    private EditText phone;
    private CustomerActivity mContext;
    private DatabaseReference mDatabase;
    private UserSharedPreferences userSharedPreferences;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
        ButterKnife.bind(this);
        toolbarTitle.setText(getString(R.string.create_customer));
        setupActionBar(toolbar);
        setHomeButton();
        mContext = this;

        setTags(categoryList, tagGroup);
        setTagDeleteListener();
        setTagClickListener();

        userSharedPreferences = new UserSharedPreferences(this);

        registerRTUpdateListener();


    }

    private void loadSavedInstanceState(Bundle savedState) {
        if (savedState != null) {
            name.setText(savedState.getString(Constants.EXTRAS_CUSTOMER_NAME));
            mobile.setText(savedState.getString(Constants.EXTRAS_MOBILE));
            email.setText(savedState.getString(Constants.EXTRAS_EMAIL));
            categoryList = savedState.getStringArrayList(Constants.EXTRAS_CATEGORY_LIST);
            Log.d(TAG, "customerList:" + categoryList);
            if (categoryList != null) {
                Log.d(TAG, "customerList size: " + categoryList.size());
                setTags(categoryList, tagGroup);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(Constants.EXTRAS_CUSTOMER_NAME, name.getText().toString());
        outState.putString(Constants.EXTRAS_MOBILE, mobile.getText().toString());
        outState.putString(Constants.EXTRAS_EMAIL, email.getText().toString());

        if (categoryList != null) {
            outState.putStringArrayList(Constants.EXTRAS_CATEGORY_LIST, categoryList);
        }
    }

    @OnClick(R.id.add_cutomer)
    public void addCustomer() {
        if (!validateCustomer()) {
            return;
        }
        Log.d(TAG, "addCustomer categoryList:" + categoryList.toString());
        userId = (int) Math.abs(Math.random() * 1000000);

        ContentValues contentValues = new ContentValues();
        contentValues.put(ShopOnContract.Entry.COLUMN_CUSTOMER_ID, userId);
        contentValues.put(ShopOnContract.Entry.COLUMN_NAME, name.getText().toString());
        contentValues.put(ShopOnContract.Entry.COLUMN_EMAIL, email.getText().toString());
        contentValues.put(ShopOnContract.Entry.COLUMN_MOBILE, mobile.getText().toString());
        contentValues.put(ShopOnContract.Entry.COLUMN_CUSTOMER_CATEGORY, categoryList.toString());
        contentValues.put(ShopOnContract.Entry.COLUMN_CREATED_AT, Utils.getCurrentDate());

        getContentResolver().insert(ShopOnContract.Entry.CONTENT_CUSTOMER_URI, contentValues);

        Cursor cursor = getContentResolver().query(ShopOnContract.Entry.CONTENT_CUSTOMER_URI, null, ShopOnContract.Entry.COLUMN_CUSTOMER_ID + "=?", new String[]{String.valueOf(userId)}, null);
        cursor.moveToFirst();
        Customers customers = Utils.createCustomerFromCursor(cursor);

        FireBaseUtils.updateCustomer(this, customers);
    }


    private void registerRTUpdateListener() {
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d(TAG, "onDataChange snapshot:" + snapshot.getValue());

                Customers customers = FireBaseUtils.getCustomerById(mContext, userId, snapshot);

                if (customers != null && email.getText().toString().equals(customers.getEmail())) {
                    Log.d(TAG, "onDataChange snapshot:" + snapshot.getValue() + " customers.getEmail:" + customers.getEmail());
                    setResult(RESULT_OK);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }

        });
    }

    protected void setTags(ArrayList<String> stringList, TagView tagGroup) {
        ArrayList<Tag> tags = new ArrayList<>();
        Tag tag;

        for (int i = 0; i < stringList.size(); i++) {
            Log.d(TAG, "category string:" + stringList.get(i));
            tag = new Tag(stringList.get(i));
            tag.radius = 10f;
            tag.layoutColor = Color.parseColor("#003366");//getResources().getColor(R.color.bazaar_darker_gray)
            tag.isDeletable = true;
            tag.tagTextColor = Color.parseColor("#ffffff");
            tags.add(tag);
        }

        addMoreAsFirstTag(tags);
        tagGroup.addTags(tags);

    }

    private void addMoreAsFirstTag(ArrayList<Tag> tags) {
        if (tags.size() == 0 || (tags.size() > 0 && !tags.get(0).text.equalsIgnoreCase("More"))) {
            Tag tag = new Tag(getString(R.string.select_interests));
            tag.radius = 10f;
            tag.layoutColor = Color.parseColor("#003366");//getResources().getColor(R.color.bazaar_darker_gray);
            tag.tagTextColor = Color.parseColor("#ffffff");
            tags.add(0, tag);
        }
    }

    protected void setTagClickListener() {
        tagGroup.setOnTagClickListener(new OnTagClickListener() {
            @Override
            public void onTagClick(Tag tag, int position) {
                if (position == 0) {
                    Intent intent = new Intent(mContext, ShopCategoryActivity.class);
                    ArrayList<String> tags = new ArrayList<String>();
                    Log.d(TAG, "Starting ShopCategoryActivity:: " + tagGroup.getTags().size());
                    for (int i = 0; i < tagGroup.getTags().size(); i++) {
                        if (i > 1) {
                            System.out.println("SHOP CATEGORY TAG Value :" + tagGroup.getTags().get(i).text);
                            tags.add(tagGroup.getTags().get(i).text);
                        }
                    }
                    intent.putStringArrayListExtra(Constants.SHOP_CATEGORY_SUBSCRIPTION_LIST, categoryList);
                    startActivityForResult(intent, Constants.RETRIEVE_CATEGORY);
                }
            }
        });
    }

    protected void setTagDeleteListener() {
        tagGroup.setOnTagDeleteListener(new OnTagDeleteListener() {

            @Override
            public void onTagDeleted(final TagView view, final Tag tag, final int position) {
                Bundle bundle = new Bundle();
                bundle.putInt("POSITION", position);
                removeTag(view, position);
            }
        });
        //validate launch/product
    }

    public void removeTag(TagView view, int position) {
        view.remove(position);
        // list doesnt not contain more option
        categoryList.remove((position == 0) ? position : position - 1);

    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RETRIEVE_CATEGORY && resultCode == RESULT_OK) {
            categoryList.addAll(data.getExtras().getStringArrayList(ShopCategoryActivity.SELECTED_TAGS));
            categoryList = Utils.getUinqueElementsInList(categoryList);
            Log.d(TAG, "selectedTags:" + categoryList);
            setTags(categoryList, tagGroup);

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);

    }

    private boolean validateCustomer() {
        if (TextUtils.isEmpty(name.getText().toString())) {
            nameError.setText(getString(R.string.name_err));
            return false;
        } else {
            nameError.setText("");
        }

        if (!Utils.isValidMobile(mobile.getText().toString())) {
            mobileError.setText(getString(R.string.number_err));
            return false;
        } else {
            mobileError.setText("");
        }

        if (!Utils.isValidEmail(email.getText().toString())) {
            emailError.setText(getString(R.string.email_err));
            return false;
        } else {
            emailError.setText("");
        }

        if (categoryList.size() <= 1) {
            interestError.setText(getString(R.string.interests_err));
            return false;
        } else {
            interestError.setText("");
        }


        return true;
    }


}
