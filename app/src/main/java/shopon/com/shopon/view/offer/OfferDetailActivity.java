package shopon.com.shopon.view.offer;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import shopon.com.shopon.R;
import shopon.com.shopon.datamodel.DetailEntry;
import shopon.com.shopon.datamodel.offer.Offer;
import shopon.com.shopon.datamodel.offer.OfferRealm;
import shopon.com.shopon.utils.Utils;
import shopon.com.shopon.view.base.BaseActivity;
import shopon.com.shopon.view.constants.Constants;
import shopon.com.shopon.view.customers.fragment.CustomerDetailFragment;
import shopon.com.shopon.view.offer.adpater.DetailOptionsAdapter;
import shopon.com.shopon.view.offer.dummy.OfferContent;
import shopon.com.shopon.view.offer.fragment.OfferDetailFragment;
import shopon.com.shopon.viewmodel.login.preferences.UserSharedPreferences;

public class OfferDetailActivity extends BaseActivity {

    private static final String TAG = OfferDetailActivity.class.getName();
    private DetailOptionsAdapter detailOptionsDetailAdapter;
    private RecyclerView offerDetail;
    private List<DetailEntry> offer_detail_entry = new ArrayList<>();
    private OfferRealm offer;
    private Menu menu;
    private int offerId;
    private DatabaseReference mDatabase;

    @Bind(R.id.title)
    TextView titleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_detail);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        offerId = getIntent().getExtras().getInt(Constants.EXTRAS_OFFER_ID);
        Log.d(TAG,"offerId:"+offerId);



        setTitle(R.string.offer_details);
        setHomeButton();

        titleView.setText(getString(R.string.offer_details));
        OfferDetailFragment offerDetailFragment = (OfferDetailFragment) getSupportFragmentManager().findFragmentByTag(OfferDetailFragment.TAG);
        if(offerDetailFragment == null) {
            offerDetailFragment = new OfferDetailFragment().newInstance(offerId, false);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.detail_container, offerDetailFragment, OfferDetailFragment.TAG).commit();

        MobileAds.initialize(getApplicationContext(), getString(R.string.firebase_app_id));

        //admob view
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        new AdRequest.Builder().addTestDevice("6126975EC3F9F4E7D0554BD539037A02");
        mAdView.loadAd(adRequest);

    }

}
