package shopon.com.shopon.view.login;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import shopon.com.shopon.R;
import shopon.com.shopon.datamodel.merchant.MerchantData;
import shopon.com.shopon.datamodel.merchant.Merchants;
import shopon.com.shopon.datamodel.merchant.MerchantsRealm;
import shopon.com.shopon.datamodel.shop_product_categories.CategoryList;
import shopon.com.shopon.db.provider.ShopOnContract;
import shopon.com.shopon.db.provider.ShopOnContractRealm;
import shopon.com.shopon.db.provider.ShopOnProvider;
import shopon.com.shopon.preferences.UserSharedPreferences;
import shopon.com.shopon.utils.Utils;
import shopon.com.shopon.view.base.BaseActivity;
import shopon.com.shopon.view.constants.Constants;

import shopon.com.shopon.view.login.adapter.ShopCategoriesQuiltAdapter;
import shopon.com.shopon.view.login.view.GridRecycleView;


public class ShopCategoryActivity extends BaseActivity implements ShopCategoriesQuiltAdapter.CategoryLevelInterface, DialogInterface.OnClickListener {

    private int GRID_COUNT = 3;
    private String CATEGORY_LIST = "product_shop_category_list.json";
    private static final String TAG = ShopCategoryActivity.class.getSimpleName();

    public static final String SELECTED_TAGS = "SELECTED_TAGS";
    public static final String ENABLE_SUB_LEVEL = "ENABLE_SUB_LEVEL";
    private GridLayoutManager mLayoutManager;
    private CategoryList categoryList;
    private HashMap<String, List<String>> selectedTags = new HashMap<>();

    private List<String> mSubscribedTags;
    ShopCategoriesQuiltAdapter mShopCategoriesAdapter;
    public boolean isLaunch = false;

    private String mEncodedSubId;
    private String mHandleName;
    private String mCurrentShopAddress;
    private boolean needToSaveCategories;
    private Menu menu;
    private boolean isSelected;
    private GridRecycleView recyclerShopView;

    @BindView(R.id.tool_bar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    private LinearLayoutManager mLinearLayoutManager;

    private boolean animationComplete = false;
    private boolean categoryPickOnlyOne = false;
    private boolean categoryForRequests = false;
    private DatabaseReference mDatabase;
    private Context mContext;
    boolean subLevel = false;
    private UserSharedPreferences userSharedPreferences;
    private boolean shouldUpdateMerchant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_shop_category);
        ButterKnife.bind(this);
        mContext = this;


        setupActionBar(toolbar);
        toolbarTitle.setText(getString(R.string.my_interests));

        if (getIntent().hasExtra(Constants.UPDATE_MERCHANT_DETAIL)) {
            shouldUpdateMerchant = getIntent().getExtras().getBoolean(Constants.UPDATE_MERCHANT_DETAIL);
            if (shouldUpdateMerchant) {
                registerRTUpdateListener();
            }
        }

        needToSaveCategories = false;
        setUpCategoryListView();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            recyclerShopView.setAdapter(mShopCategoriesAdapter);
        }

        toolBarOptionsCheck();
    }

    private void registerRTUpdateListener() {
        userSharedPreferences = new UserSharedPreferences(mContext);
        mDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_MERCHANT_PREFIX + userSharedPreferences.getPref(Constants.MERCHANT_MSISDN_PREF));

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Merchants merchant = postSnapshot.getValue(Merchants.class);

                    Log.d(TAG, "onDataChange merchat Id:" + merchant.getUserId() + " userId:" + userSharedPreferences.getPref(Constants.MERCHANT_ID_PREF));
                    //Getting the data from snapshot
                    if (userSharedPreferences.getPref(Constants.MERCHANT_ID_PREF).equals(merchant.getUserId())) {
                        navigateToMainScreen();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }

        });
    }


    private void setUpCategoryListView() {
        recyclerShopView = (GridRecycleView) findViewById(R.id.categoryShopView);


        mLayoutManager = new GridLayoutManager(this, GRID_COUNT);
        recyclerShopView.setLayoutManager(mLayoutManager);
        //mLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                Log.i(TAG, "getSpanSize :" + categoryForRequests + ",GRID_COUNT :" + GRID_COUNT);
                if (!categoryForRequests)
                    return (position == 0 && mShopCategoriesAdapter.getCategoryLevel() == 0) ? GRID_COUNT : 1;
                else
                    return 1;
            }
        });
        recyclerShopView.setLayoutManager(mLayoutManager);
        loadCategoryList();
        Log.i(TAG, "onCreate | categoryForRequests :" + categoryForRequests);
        mShopCategoriesAdapter = new ShopCategoriesQuiltAdapter(this, categoryList, categoryForRequests);
        mShopCategoriesAdapter.enableSubLevel(subLevel);
        recyclerShopView.scheduleLayoutAnimation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    public boolean isTagSubscribed(String key) {

        if (mSubscribedTags != null)
            return mSubscribedTags.contains(key);
        else
            return false;
    }

    public void switchLayoutManager() {
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerShopView.setLayoutManager(mLayoutManager);
        //mLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);

    }


    public void loadCategoryList() {
        Log.i(TAG, "loadCategoryList :" + CATEGORY_LIST);
        Gson gson = new Gson();
        String jsonString = "";
        InputStream inputStream = null;
        try {
            inputStream = getAssets().open(CATEGORY_LIST);

            Log.d(TAG, "inputStream:" + inputStream.available());

            byte[] buffer = new byte[inputStream.available()];
            String contents = "";
            int bytesRead = 0;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                jsonString = new String(buffer, 0, bytesRead);
            }
            categoryList = gson.fromJson(jsonString, CategoryList.class);
            inputStream.close();
            Log.d(TAG, " retrieveCategoryJson:" + categoryList);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, " retrieveCategoryJson failed:" + e.getMessage());
        }

    }

    private void toolBarOptionsCheck() {


    }

    public void onDone(View v) {

        onActionDone();
    }

    private void onActionDone() {

        Log.d(TAG, "Clicked on action doShopCategoriesQuiltAdapterne: " + mShopCategoriesAdapter.getCategoryLevel());
        if (mShopCategoriesAdapter.getCategoryLevel() == 1) {
            mShopCategoriesAdapter.setCategoryLevel(0);
            mShopCategoriesAdapter.notifyDataSetChanged();
            invalidateOptionsMenu();

        } else {
            //setSelectedTag();
            if (shouldUpdateMerchant) {
                if (!Utils.isReachable()) {
                    Utils.displayConnectToInternet(this, this);
                    return;
                }
                setSelectedTagFromAdapter();
            } else {
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra(SELECTED_TAGS, (ArrayList) mShopCategoriesAdapter.getSelectedTags());
                setResult(Activity.RESULT_OK, intent);
                finish();
            }

        }
    }

    public void doSelectAll(View view) {

        mShopCategoriesAdapter.addToSelectedTagsList(mShopCategoriesAdapter.getRenderedSubCategories());
        mShopCategoriesAdapter.notifyDataSetChanged();
        isSelected = true;

    }

    public void doUnSelectAll(View view) {

        mShopCategoriesAdapter.removeFromSelectedTagsList(mShopCategoriesAdapter.getRenderedSubCategories());
        mShopCategoriesAdapter.notifyDataSetChanged();
        isSelected = false;
    }

    private void setSelectedTagFromAdapter() {
        Cursor cursor = updateMerchantInterest();
        if (cursor == null) {
            return;
        }
        cursor.moveToFirst();
        updateRTDataBase(cursor);
    }

    private ArrayList<String> getUinqueElementsInList(List<String> inputList) {
        //pass unique subscribed tags to intent - remove duplicates
        HashSet set = new HashSet();
        set.addAll(inputList);
        ArrayList list = new ArrayList();
        list.addAll(set);
        return list;
    }

    private void setSelectedTag() {
        Iterator it = this.selectedTags.entrySet().iterator();
        List<String> selectedTags = new ArrayList<>();
        while (it.hasNext()) {
            Map.Entry<String, List<String>> pair = (Map.Entry) it.next();
            selectedTags.add(pair.getKey());
            selectedTags.addAll(pair.getValue());
            // avoids a ConcurrentModificationException
        }

        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(SELECTED_TAGS, (ArrayList) selectedTags);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public static void printMap(Map mp) {

    }

    public void removeTags(String key) {
        selectedTags.remove(key);
    }


    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        Log.d(TAG, "onEnterAnimationComplete");
        if (!animationComplete) {
            recyclerShopView.setAdapter(mShopCategoriesAdapter);
            recyclerShopView.scheduleLayoutAnimation();
        }
        animationComplete = true;
    }

    public boolean equalLists(List<String> a, List<String> b) {
        // Check for sizes and nulls
        if (a == null && b == null) return true;

        if ((a == null && b != null) || (a != null && b == null) || (a.size() != b.size())) {
            return false;
        }

        // Sort and compare the two lists
        Collections.sort(a);
        Collections.sort(b);
        return a.equals(b);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQUEST_SUB_CATEGORY && resultCode == RESULT_OK) {
            ArrayList selected_sub_tags = data.getExtras().getStringArrayList(Constants.SELECTED_CATEGORY_LIST);
            mShopCategoriesAdapter.addToSelectedTagsList(selected_sub_tags);
            mShopCategoriesAdapter.notifyDataSetChanged();

        }
    }

    private void navigateToMainScreen() {
        UserSharedPreferences userSharedPreferences = new UserSharedPreferences(this);
        userSharedPreferences.savePref(Constants.CURRENT_LOGIN_STATE, Constants.LOGIN_COMPLETE);
        Intent intent = new Intent(this, ShopOnActivity.class);
        startActivity(intent);
        finish();
    }

    private Cursor updateMerchantInterest() {

        UserSharedPreferences userSharedPreferences = new UserSharedPreferences(this);
        Log.d(TAG, "merchant id:" + (Integer) userSharedPreferences.getPref(Constants.MERCHANT_ID_PREF) + " selected_tags:" + mShopCategoriesAdapter.getSelectedTags().toString());
        if (mShopCategoriesAdapter.getSelectedTags() == null || mShopCategoriesAdapter.getSelectedTags().size() == 0) {
            return null;
        }

        Cursor cursor = getContentResolver().query(ShopOnContractRealm.Entry.CONTENT_MERCHANT_URI, null, ShopOnContractRealm.Entry.COLUMN_USER_ID + "=?", new String[]{String.valueOf((Integer) userSharedPreferences.getPref(Constants.MERCHANT_ID_PREF))}, null);
        cursor.moveToFirst();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ShopOnContractRealm.Entry.COLUMN_NAME, cursor.getString(1));
        contentValues.put(ShopOnContractRealm.Entry.COLUMN_EMAIL, cursor.getString(2));
        contentValues.put(ShopOnContractRealm.Entry.COLUMN_MOBILE, cursor.getString(3));
        contentValues.put(ShopOnContractRealm.Entry.COLUMN_MERCHANT_CATEGORY, mShopCategoriesAdapter.getSelectedTags().toString());
        contentValues.put(ShopOnContract.Entry.COLUMN_CREATED_AT, Utils.getCurrentDate());
        getContentResolver().update(ShopOnContractRealm.Entry.CONTENT_MERCHANT_URI, contentValues, ShopOnContractRealm.Entry.COLUMN_USER_ID + "=?", new String[]{String.valueOf((Integer) userSharedPreferences.getPref(Constants.MERCHANT_ID_PREF))});

        Cursor cursor1 = getContentResolver().query(ShopOnContractRealm.Entry.CONTENT_MERCHANT_URI, null, ShopOnContractRealm.Entry.COLUMN_USER_ID + "=?", new String[]{String.valueOf((Integer) userSharedPreferences.getPref(Constants.MERCHANT_ID_PREF))}, null);
        return cursor1;
    }

    private void updateRTDataBase(MerchantsRealm merchant_realm) {
        MerchantData merchant_data = new MerchantData();
        merchant_data.setRealmMerchant(merchant_realm);
        UserSharedPreferences userSharedPreferences = new UserSharedPreferences(this);
        mDatabase.child(Constants.FIREBASE_MERCHANT_PREFIX + (String) userSharedPreferences.getPref(Constants.MERCHANT_MSISDN_PREF)).setValue(merchant_data);//+userSharedPreferences.getPref(Constants.MERCHANT_ID_PREF)
    }

    private void updateRTDataBase(Cursor cursor) {
        Merchants merchant_data = Utils.createMerchantFromCursor(cursor);
        Log.d(TAG, "updateRTDataBase interestedIn:" + merchant_data.getMerchentCategory() + " createdAt:" + merchant_data.getCreatedAt());
        UserSharedPreferences userSharedPreferences = new UserSharedPreferences(this);
        mDatabase.child(Constants.FIREBASE_MERCHANT_PREFIX + (String) userSharedPreferences.getPref(Constants.MERCHANT_MSISDN_PREF)).setValue(merchant_data);//+userSharedPreferences.getPref(Constants.MERCHANT_ID_PREF)
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.pick_category_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mShopCategoriesAdapter != null) {
            if (mShopCategoriesAdapter.getCategoryLevel() == 1) {
                if (!isSelected) {
                    showOption(menu, R.id.select_all);
                    hideOption(menu, R.id.deselect_all);
                } else {
                    showOption(menu, R.id.deselect_all);
                    hideOption(menu, R.id.select_all);
                }
            } else {
                hideOption(menu, R.id.select_all);
                hideOption(menu, R.id.deselect_all);
            }
        }

        return super.onPrepareOptionsMenu(menu);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.select_all:
                mShopCategoriesAdapter.addToSelectedTagsList(mShopCategoriesAdapter.getRenderedSubCategories());
                mShopCategoriesAdapter.notifyDataSetChanged();
                isSelected = true;
                invalidateOptionsMenu();
                return true;
            case R.id.deselect_all:
                mShopCategoriesAdapter.removeFromSelectedTagsList(mShopCategoriesAdapter.getRenderedSubCategories());
                mShopCategoriesAdapter.notifyDataSetChanged();
                isSelected = false;
                invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void categoryLevel(int level) {

    }


    @Override
    public void onClick(DialogInterface dialogInterface, int i) {

    }
}
