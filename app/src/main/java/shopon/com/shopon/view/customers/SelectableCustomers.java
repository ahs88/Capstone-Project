package shopon.com.shopon.view.customers;

import android.content.Intent;
import android.os.Bundle;
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
import io.realm.Realm;
import io.realm.RealmResults;
import shopon.com.shopon.R;
import shopon.com.shopon.datamodel.customer.Customers;
import shopon.com.shopon.datamodel.customer.CustomersRealm;
import shopon.com.shopon.db.CustomerRealmUtil;
import shopon.com.shopon.utils.Utils;
import shopon.com.shopon.view.base.BaseActivity;
import shopon.com.shopon.view.constants.Constants;
import shopon.com.shopon.view.contact.Contacts;
import shopon.com.shopon.view.customers.adapter.MyCustomerRecyclerViewAdapter;
import shopon.com.shopon.view.customers.dummy.CustomerContent;
import shopon.com.shopon.view.customers.fragment.CustomerFragment;


public class SelectableCustomers extends BaseActivity implements CustomerFragment.OnListFragmentInteractionListener {

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

        retrieveCustomerList();
        customerAapter = new MyCustomerRecyclerViewAdapter(CustomerContent.ITEMS, this,true,false);
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
    public void selectFromPhoneBoook(){
        /*Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        startActivityForResult(intent, Constants.PICK_CONTACT);*/

    }


    private void retrieveCustomerList() {
        Log.d(TAG, "retrieveCustomerList");
        Realm realm = Realm.getDefaultInstance();
        RealmResults<CustomersRealm> result = realm.where(CustomersRealm.class).findAll();
        customerList = (ArrayList<Customers>) CustomerRealmUtil.convertRealmCustomerListToCustomerList(result);
        Log.d(TAG, "customerList:" + customerList.toString());
        CustomerContent.ITEMS.clear();
        CustomerContent.ITEMS.addAll(customerList);
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
        switch(item.getItemId()){
            case R.id.action_done: {
                Bundle bundle = new Bundle();
                bundle.putStringArrayList(Constants.SELECTED_NUMBERS,(ArrayList)customerAapter.getSelectedNumbers());
                Intent intent= getIntent();
                        intent.putExtras(bundle);
                setResult(RESULT_OK,intent);
                finish();
            }
            case android.R.id.home:{
                finish();
            }
        }


        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == Constants.ADD_CONTACTS){
            Log.d(TAG,"onActivityResult");
            Bundle bundle = new Bundle();
            bundle.putStringArrayList(Constants.SELECTED_NUMBERS,data.getExtras().getStringArrayList(Constants.SELECTED_NUMBERS));
            Intent intent= getIntent();
            intent.putExtras(bundle);
            setResult(RESULT_OK,intent);
            finish();
        }
    }


    @OnClick(R.id.phone_book)
    public void launchContacts(){
        Intent intent = new Intent(this, Contacts.class);
        startActivityForResult(intent,Constants.ADD_CONTACTS);
    }





}
