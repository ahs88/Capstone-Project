package shopon.com.shopon.view.customers;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import shopon.com.shopon.R;
import shopon.com.shopon.datamodel.customer.Customers;

import shopon.com.shopon.db.provider.ShopOnContract;
import shopon.com.shopon.utils.Utils;
import shopon.com.shopon.view.base.BaseActivity;
import shopon.com.shopon.view.constants.Constants;
import shopon.com.shopon.view.contact.Contacts;
import shopon.com.shopon.view.customers.adapter.MyCustomerRecyclerViewAdapter;
import shopon.com.shopon.view.customers.dummy.CustomerContent;
import shopon.com.shopon.view.customers.fragment.CustomerFragment;


public class SelectableCustomers extends BaseActivity implements CustomerFragment.OnListFragmentInteractionListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = SelectableCustomers.class.getName();
    @Bind(R.id.tool_bar)
    Toolbar toolbar;
    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;

    @Bind(R.id.list)
    RecyclerView customerListView;
    private LinearLayoutManager mLinearLayoutManager;
    public int mColumnCount = 1;
    private MyCustomerRecyclerViewAdapter customerAapter;
    private ArrayList<Customers> customerList;
    @Bind(R.id.phone_book)
    Button addPhoneBookNumber;
    @Bind(R.id.no_customer_label)
    TextView customerLabel;
    private Cursor mCursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectable_customer_activity);
        ButterKnife.bind(this);
        setupActionBar(toolbar);
        setHomeButton();
        setupCustomerList();
        toolbarTitle.setText(getString(R.string.select_customer));
    }

    private void setupCustomerList() {
        if (mColumnCount <= 1) {
            mLinearLayoutManager = new LinearLayoutManager(this);
        } else {
            mLinearLayoutManager = new GridLayoutManager(this, mColumnCount);
        }
        customerListView.setLayoutManager(mLinearLayoutManager);


        customerAapter = new MyCustomerRecyclerViewAdapter(CustomerContent.ITEMS, this, true, false);
        displayEmptyList();
        customerListView.setAdapter(customerAapter);
    }

    private void displayEmptyList() {
        if (CustomerContent.ITEMS.size() == 0) {
            customerLabel.setVisibility(View.VISIBLE);
            addPhoneBookNumber.setVisibility(View.VISIBLE);
        } else {
            customerLabel.setVisibility(View.GONE);
            addPhoneBookNumber.setVisibility(View.GONE);
        }
    }


    @OnClick(R.id.phone_book)
    public void selectFromPhoneBoook() {
        /*Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        startActivityForResult(intent, Constants.PICK_CONTACT);*/

    }


    @Override
    public void onListFragmentInteraction(Customers item) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.shop_on, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done: {
                Bundle bundle = new Bundle();
                bundle.putStringArrayList(Constants.SELECTED_NUMBERS, (ArrayList) customerAapter.getSelectedNumbers());
                Intent intent = getIntent();
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
            case android.R.id.home: {
                finish();
            }
        }


        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Constants.ADD_CONTACTS) {
            Log.d(TAG, "onActivityResult");
            Bundle bundle = new Bundle();
            bundle.putStringArrayList(Constants.SELECTED_NUMBERS, data.getExtras().getStringArrayList(Constants.SELECTED_NUMBERS));
            Intent intent = getIntent();
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
            finish();
        }
    }


    @OnClick(R.id.phone_book)
    public void launchContacts() {
        Intent intent = new Intent(this, Contacts.class);
        startActivityForResult(intent, Constants.ADD_CONTACTS);
    }


    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader");
        return new CursorLoader(this,  // Context
                ShopOnContract.Entry.CONTENT_CUSTOMER_URI, // URI
                null,                // Projection
                null,                           // Selection
                null,                           // Selection args
                ShopOnContract.Entry.COLUMN_CREATED_AT + " asc");
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        Log.d(TAG, "onLoadFinished size:" + data.getCount());
        mCursor = data;
        populateCustomerListFromCursor(data);
        if (getSupportLoaderManager().hasRunningLoaders()) {
            getSupportLoaderManager().destroyLoader(Constants.ALL_CUSTOMERS);
        }

    }

    private void populateCustomerListFromCursor(Cursor cursor) {
        CustomerContent.ITEMS.clear();
        cursor.moveToFirst();
        Log.d(TAG, "populateOfferListFromCursor cursor:" + cursor.getCount());
        for (int i = 0; i < cursor.getCount(); i++) {
            Customers customers = shopon.com.shopon.utils.Utils.createCustomerFromCursor(cursor);
            CustomerContent.ITEMS.add(customers);
            cursor.moveToNext();
        }
    }


    @Override
    public void onLoaderReset(Loader loader) {

    }

}
