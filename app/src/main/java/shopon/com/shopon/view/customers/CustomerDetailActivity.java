package shopon.com.shopon.view.customers;

import android.os.Bundle;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import shopon.com.shopon.R;
import shopon.com.shopon.datamodel.customer.CustomerData;
import shopon.com.shopon.datamodel.customer.CustomersRealm;
import shopon.com.shopon.datamodel.DetailEntry;
import shopon.com.shopon.datamodel.offer.OfferRealm;
import shopon.com.shopon.preferences.UserSharedPreferences;
import shopon.com.shopon.view.base.BaseActivity;
import shopon.com.shopon.view.constants.Constants;
import shopon.com.shopon.view.customers.fragment.CustomerDetailFragment;
import shopon.com.shopon.view.offer.adpater.DetailOptionsAdapter;


public class CustomerDetailActivity extends BaseActivity {

    private static final String TAG = CustomerDetailActivity.class.getName();
    private DetailOptionsAdapter detailOptionsDetailAdapter;
    private RecyclerView customerDetail;
    private List<DetailEntry> customer_detail_entry = new ArrayList<>();
    private CustomersRealm customer;
    private int customerId;
    private DatabaseReference mDatabase;
    private UserSharedPreferences userSharedPreferences;

    @Bind(R.id.title)
    TextView titleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_detail);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        customerId = getIntent().getExtras().getInt(Constants.EXTRAS_CUSTOMER_ID);
        boolean isTwoPane = getIntent().getExtras().getBoolean(Constants.EXTRAS_IS_TWO_PANE);
        Log.d(TAG, "customerId:" + customerId+" isTwoPane:"+isTwoPane);

        setTitle(R.string.title_activity_customer_detail);
        titleView.setText(getString(R.string.title_activity_customer_detail));
        setHomeButton();
        CustomerDetailFragment customerDetailFragment = (CustomerDetailFragment) getSupportFragmentManager().findFragmentByTag(CustomerDetailFragment.TAG);
        if(customerDetailFragment == null) {
            customerDetailFragment = new CustomerDetailFragment().newInstance(customerId, isTwoPane);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.detail_container,customerDetailFragment,CustomerDetailFragment.TAG).commit();
    }


}
