package shopon.com.shopon.view.login;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import shopon.com.shopon.R;
import shopon.com.shopon.datamodel.customer.Customers;
import shopon.com.shopon.datamodel.merchant.MerchantsRealm;
import shopon.com.shopon.datamodel.offer.Offer;
import shopon.com.shopon.db.provider.ShopOnContractRealm;
import shopon.com.shopon.preferences.UserSharedPreferences;
import shopon.com.shopon.remote.SyncInterface;
import shopon.com.shopon.remote.SyncLocalDB;
import shopon.com.shopon.view.base.BaseActivity;
import shopon.com.shopon.view.constants.Constants;
import shopon.com.shopon.view.customers.CustomerActivity;
import shopon.com.shopon.view.customers.fragment.CustomerDetailFragment;
import shopon.com.shopon.view.customers.fragment.CustomerFragment;
import shopon.com.shopon.view.offer.OfferActivity;
import shopon.com.shopon.view.offer.fragment.OfferDetailFragment;
import shopon.com.shopon.view.offer.fragment.OfferFragment;


public class ShopOnActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,OfferFragment.OnListFragmentInteractionListener,SyncInterface,CustomerFragment.OnListFragmentInteractionListener {

    private static final String TAG =ShopOnActivity.class.getName() ;
    @Bind(R.id.fab)FloatingActionButton fab;
    TextView userNameView;
    TextView emailView;
    private CustomerFragment customerFragment;
    private OfferFragment offerFragment;
    private SyncLocalDB syncLocalDB;
    private TextView letterView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_on);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);
        View headerLayout =
                navigationView.getHeaderView(0);
        userNameView = (TextView)headerLayout.findViewById(R.id.user_name);
        letterView = (TextView)headerLayout.findViewById(R.id.letterDisplay);
        emailView = (TextView)headerLayout.findViewById(R.id.email_id);
        customerFragment = (CustomerFragment) getSupportFragmentManager().findFragmentByTag(CustomerFragment.TAG);

        //handle orientation change
        if(savedInstanceState!=null){
            String tag = (String)savedInstanceState.get(Constants.CURRENT_FRAGMENT);
            if(tag.equals(CustomerFragment.TAG)){
                addCustomerFragment();
            }else if(tag.equals(OfferFragment.TAG)){
                addOfferFragment();
            }

        }
        else
        {
            addCustomerFragment();
        }

        bindNavHeader();
        sync();
    }

    private void sync() {
        syncLocalDB = new SyncLocalDB(this);
        syncLocalDB.register(this);
        syncLocalDB.execute();
    }


    private void bindNavHeader() {
        UserSharedPreferences userSharedPrefernce = new UserSharedPreferences(this);

        Cursor cursor = getContentResolver().query(ShopOnContractRealm.Entry.CONTENT_MERCHANT_URI,null, ShopOnContractRealm.Entry.COLUMN_USER_ID+"=?",new String[]{String.valueOf((Integer) userSharedPrefernce.getPref(Constants.MERCHANT_ID_PREF))},null);
        cursor.moveToFirst();
        userNameView.setText(cursor.getString(1));
        String email = cursor.getString(2);
        emailView.setText(email);

        letterView.setText(((email!=null) && (!TextUtils.isEmpty(email)))?String.valueOf(email.charAt(0)):"");
    }

    public void setContentDescription(){
        if(getCurrentFragment() instanceof CustomerFragment || getCurrentFragment() instanceof CustomerDetailFragment)
        {
            fab.setContentDescription(getString(R.string.create_customer));
        }
        else if(getCurrentFragment() instanceof OfferFragment || getCurrentFragment() instanceof OfferDetailFragment){
            fab.setContentDescription(getString(R.string.create_offer));
        }
    }

    @OnClick(R.id.fab)
    public void setupFloatActionListener() {

      /*fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {*/
        Log.d(TAG,"setupFloatActionListener currentFragment:"+currentFragment.getClass().getName());
                if(getCurrentFragment() instanceof CustomerFragment || getCurrentFragment() instanceof CustomerDetailFragment)
                {
                    fab.setContentDescription(getString(R.string.create_customer));
                    Intent intent = new Intent(this, CustomerActivity.class);
                    startActivityForResult(intent,Constants.ADD_CUSTOMER);
                    Log.d(TAG,"intent invoked to launch customer activity");

                }
                else if(getCurrentFragment() instanceof OfferFragment || getCurrentFragment() instanceof OfferDetailFragment){
                    fab.setContentDescription(getString(R.string.create_offer));
                    Intent intent = new Intent(this, OfferActivity.class);
                    startActivityForResult(intent,Constants.ADD_OFFER);
                    Log.d(TAG,"intent invoked to launch offer activity");
                }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.shop_on, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.customer) {
            addCustomerFragment();
            // Handle the camera action
        } else if (id == R.id.offer) {
            addOfferFragment();
        } else if (id == R.id.nav_share) {

        }
        //accessibility
        setContentDescription();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void addCustomerFragment() {
        setTitle(getString(R.string.customers));
        customerFragment = (CustomerFragment) getSupportFragmentManager().findFragmentByTag(CustomerFragment.TAG);
        if(customerFragment == null) {
            customerFragment = CustomerFragment.newInstance(1, (findViewById(R.id.detail_container) != null) ? true : false);
        }
        setCurrentFragment(customerFragment,R.id.main_container,CustomerFragment.TAG);
        addCustomerDetailsFragment();
    }

    public void addCustomerDetailsFragment(){
        if(findViewById(R.id.detail_container)!=null) {
            setTitle(getString(R.string.customers));
            CustomerDetailFragment customerDetailFragment = (CustomerDetailFragment) getSupportFragmentManager().findFragmentByTag(CustomerDetailFragment.TAG);
            if(customerDetailFragment == null) {
                customerDetailFragment = CustomerDetailFragment.newInstance(1, (findViewById(R.id.detail_container) != null) ? true : false);
            }
            setCurrentFragment(customerDetailFragment, R.id.detail_container, CustomerDetailFragment.TAG);
        }
    }

    private void addOfferFragment() {
        setTitle(getString(R.string.created_offers));
        offerFragment = (OfferFragment) getSupportFragmentManager().findFragmentByTag(OfferFragment.TAG);
        if(offerFragment == null) {
            offerFragment = OfferFragment.newInstance(1, (findViewById(R.id.detail_container) != null) ? true : false);
        }
        setCurrentFragment(offerFragment,R.id.main_container,OfferFragment.TAG);
        addOfferDetailsFragment();
    }

    public void addOfferDetailsFragment(){
        if(findViewById(R.id.detail_container)!=null) {
            setTitle(getString(R.string.customers));
            OfferDetailFragment offerDetailFragment = (OfferDetailFragment) getSupportFragmentManager().findFragmentByTag(OfferDetailFragment.TAG);
            if(offerDetailFragment == null) {
                offerDetailFragment = OfferDetailFragment.newInstance(1, (findViewById(R.id.detail_container) != null) ? true : false);
            }
            setCurrentFragment(offerDetailFragment, R.id.detail_container, OfferDetailFragment.TAG);
        }
    }

    @Override
    public void onListFragmentInteraction(Offer item) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //syncLocalDB.unregister(this);
        customerFragment = null;
        offerFragment = null;
    }

    @Override
    public void update(String action, Object... object) {
        if(currentFragment instanceof CustomerFragment) {
            customerFragment.notifyDataChange();
        }
        if(currentFragment instanceof OfferFragment) {
            offerFragment.notifyDataChange();
        }

    }

    @Override
    public void onListFragmentInteraction(Customers item) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(currentFragment!=null) {
            outState.putString(Constants.CURRENT_FRAGMENT, currentFragment.getTag());
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG,"onActivityResult");
        currentFragment.onActivityResult(requestCode,resultCode,data);

    }


    @Override
    protected void onResume() {
        super.onResume();

        //readFromRealmOnMainThread();
    }


}
