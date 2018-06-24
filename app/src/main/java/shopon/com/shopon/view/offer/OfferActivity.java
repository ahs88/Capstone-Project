package shopon.com.shopon.view.offer;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.DateTimeKeyListener;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.RunnableFuture;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.internal.Util;
import shopon.com.shopon.R;
import shopon.com.shopon.ShopOn;
import shopon.com.shopon.datamodel.customer.CustomerData;
import shopon.com.shopon.datamodel.customer.Customers;
import shopon.com.shopon.datamodel.customer.CustomersRealm;
import shopon.com.shopon.datamodel.merchant.MerchantData;
import shopon.com.shopon.datamodel.merchant.Merchants;
import shopon.com.shopon.datamodel.merchant.MerchantsRealm;
import shopon.com.shopon.datamodel.offer.Offer;
import shopon.com.shopon.datamodel.offer.OfferRealm;
import shopon.com.shopon.db.OfferRealmUtil;
import shopon.com.shopon.db.provider.ShopOnContract;
import shopon.com.shopon.preferences.UserSharedPreferences;
import shopon.com.shopon.remote.FireBaseUtils;
import shopon.com.shopon.service.SMSService;
import shopon.com.shopon.utils.Utils;
import shopon.com.shopon.view.base.BaseActivity;
import shopon.com.shopon.view.constants.Constants;
import shopon.com.shopon.view.customers.CustomerActivity;
import shopon.com.shopon.view.customers.SelectableCustomers;
import shopon.com.shopon.view.customers.dummy.CustomerContent;
import shopon.com.shopon.view.login.ShopCategoryActivity;
import shopon.com.shopon.view.offer.dummy.OfferContent;
import shopon.com.shopon.view.tagview.Tag.OnTagClickListener;
import shopon.com.shopon.view.tagview.Tag.OnTagDeleteListener;
import shopon.com.shopon.view.tagview.Tag.Tag;
import shopon.com.shopon.view.tagview.Tag.TagView;

import static java.lang.Thread.sleep;


public class OfferActivity extends BaseActivity implements DateTimPickerUtils.ScheduledDateInterface {

    private static final String TAG = OfferActivity.class.getName();
    @BindView(R.id.tool_bar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.date)
    TextView date;
    @BindView(R.id.tags)
    TagView tagGroup;
    @BindView(R.id.offer_text)
    EditText offerTextView;
    @BindView(R.id.offer_err)
    TextView offerError;
    @BindView(R.id.date_err)
    TextView dateError;
    @BindView(R.id.tags_err)
    TextView customerError;

    Context mContext;
    private ArrayList<String> customerList = new ArrayList<>();
    private DatabaseReference mDatabase;
    private UserSharedPreferences userSharedPreferences;
    private int offerId;
    private Calendar mCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_offer);
        ButterKnife.bind(this);
        mContext = this;
        setupActionBar(toolbar);
        setHomeButton();
        toolbarTitle.setText(getString(R.string.create_offer));

        userSharedPreferences = new UserSharedPreferences(this);

        setTags(customerList, tagGroup);
        setTagDeleteListener();
        setTagClickListener();
        registerRTUpdateListener();

        //keep the keyboard down else date picker will not allow you to pick the dates at the lower regions
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        loadSavedInstanceState(savedInstanceState);
    }

    private void loadSavedInstanceState(Bundle savedState) {
        if (savedState != null) {
            String dateTime = savedState.getString(Constants.EXTRAS_SCHEDULED_DATE);
            Log.d(TAG, "loadSavedInstanceState date:" + dateTime);
            date.setText(dateTime);
            offerTextView.setText(savedState.getString(Constants.EXTRAS_OFFER_TEXT, date.getText().toString()));
            customerList = savedState.getStringArrayList(Constants.EXTRAS_CUSTOMER_LIST);
            Log.d(TAG, "customerList:" + customerList);
            if (customerList != null) {
                Log.d(TAG, "customerList size: " + customerList.size());
                setTags(customerList, tagGroup);
            }
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState date:" + date.getText().toString());
        outState.putString(Constants.EXTRAS_SCHEDULED_DATE, date.getText().toString());
        outState.putString(Constants.EXTRAS_OFFER_TEXT, offerTextView.getText().toString());
        if (customerList != null) {
            outState.putStringArrayList(Constants.EXTRAS_CUSTOMER_LIST, customerList);
        }
    }

    private void registerRTUpdateListener() {
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d(TAG, "onDataChange snapshot:" + snapshot.getValue());

                Offer offer = FireBaseUtils.getOfferById(mContext, offerId, snapshot);
                if (offer != null && offer.getOfferText().equals(offerTextView.getText().toString())) {

                    try {
                        Thread.currentThread().sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    hideProgress();
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
            Tag tag = new Tag(getString(R.string.select_customer));
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
                    Intent intent = new Intent(mContext, SelectableCustomers.class);
                    ArrayList<String> tags = new ArrayList<String>();
                    Log.d(TAG, "Starting ShopCategoryActivity:: " + tagGroup.getTags().size());
                    for (int i = 0; i < tagGroup.getTags().size(); i++) {
                        if (i > 1) {
                            System.out.println("SHOP CATEGORY TAG Value :" + tagGroup.getTags().get(i).text);
                            tags.add(tagGroup.getTags().get(i).text);
                        }
                    }
                    startActivityForResult(intent, Constants.RETRIEVE_MSISDN);
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
        customerList.remove((position == 0) ? position : position - 1);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            hideProgress();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @OnClick(R.id.create_offer)
    public void createOffer() {
        if (!validateOffer()) {
            return;
        }

        offerId = (int) Math.abs(Math.random() * 1000000);

        ContentValues contentValues = new ContentValues();
        contentValues.put(ShopOnContract.Entry.COLUMN_OFFER_ID, offerId);
        contentValues.put(ShopOnContract.Entry.COLUMN_OFFER_TEXT, offerTextView.getText().toString());
        contentValues.put(ShopOnContract.Entry.COLUMN_SCHEDULED_DATE, date.getText().toString());
        contentValues.put(ShopOnContract.Entry.COLUMN_CUSTOMER_NUMBERS, customerList.toString());
        contentValues.put(ShopOnContract.Entry.COLUMN_OFFER_STATUS, false);
        Uri uri = getContentResolver().insert(ShopOnContract.Entry.CONTENT_OFFER_URI, contentValues);
        Cursor cursor = getContentResolver().query(ShopOnContract.Entry.CONTENT_OFFER_URI, null, ShopOnContract.Entry.COLUMN_OFFER_ID + "=?", new String[]{String.valueOf(offerId)}, null);
        if (cursor != null) {
            cursor.moveToFirst();
            Offer fOffer = Utils.createOfferFromCursor(cursor);

            FireBaseUtils.updateOfferDataBase(mContext, fOffer);

            createSMSPendingIntent(offerId);
        } else {
            Toast.makeText(mContext, getString(R.string.offer_create_failed), Toast.LENGTH_LONG).show();
        }

    }

    private void createSMSPendingIntent(int offerId) {
        Intent intent = new Intent(this, SMSService.class);

        intent.putExtra(Constants.EXTRAS_OFFER_ID, offerId);
        PendingIntent pintent = PendingIntent.getService(this, offerId, intent, Intent.FILL_IN_DATA);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.set(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), pintent);
    }

    private MerchantData retrieveMerchantData(Realm realm, Offer offer) {
        RealmResults<MerchantsRealm> result = realm.where(MerchantsRealm.class).findAll();
        MerchantsRealm merchantRealm = result.get(0);
        MerchantData merchantData = new MerchantData();
        merchantData.setRealmMerchant(merchantRealm);
        return merchantData;
    }

    @OnClick(R.id.date)
    public void scheduleDate() {

        // Things we do to hide faults in the framework :)
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                DateTimPickerUtils dateTimePickerUtis = new DateTimPickerUtils((OfferActivity) mContext, (OfferActivity) mContext);
                dateTimePickerUtis.schedule();
            }
        };
        releaseKeypad(handler, runnable);
    }

    public void releaseKeypad(Handler handler, Runnable runnable) {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            handler.postDelayed(runnable, 100);
        }
    }

    @Override
    public void scheduledDate(String scheduledDate, Calendar calendar) {
        Log.d(TAG, "scheduled date:" + scheduledDate);
        date.setText(scheduledDate);
        this.mCalendar = calendar;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Constants.RETRIEVE_MSISDN) {
            customerList.clear();
            customerList.addAll(data.getExtras().getStringArrayList(Constants.SELECTED_NUMBERS));
            setTags(customerList, tagGroup);
        }
    }

    public boolean validateOffer() {
        if (TextUtils.isEmpty(date.getText().toString())) {
            dateError.setText(getString(R.string.delivery_date_err));
            return false;
        } else {
            dateError.setText("");
        }

        if (TextUtils.isEmpty(offerTextView.getText().toString())) {
            offerError.setText(getString(R.string.offer_text_err));
            return false;
        } else {
            offerError.setText("");
        }

        if (customerList.size() < 1) {
            customerError.setText(getString(R.string.customer_err));
            return false;
        } else {
            customerError.setText("");
        }

        return true;
    }

}
