package shopon.com.shopon.view.offer.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import shopon.com.shopon.R;
import shopon.com.shopon.datamodel.customer.CustomersRealm;
import shopon.com.shopon.datamodel.offer.Offer;
import shopon.com.shopon.datamodel.offer.OfferRealm;
import shopon.com.shopon.db.OfferRealmUtil;
import shopon.com.shopon.utils.Utils;
import shopon.com.shopon.view.base.BaseActivity;
import shopon.com.shopon.view.constants.Constants;
import shopon.com.shopon.view.customers.dummy.CustomerContent;
import shopon.com.shopon.view.offer.OfferActivity;
import shopon.com.shopon.view.offer.adpater.MyOfferRecyclerViewAdapter;
import shopon.com.shopon.view.offer.dummy.OfferContent;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class OfferFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    public static final String TAG = OfferFragment.class.getName();
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    @Nullable
    @Bind(R.id.create_offer_btn)
    public Button createOffer;
    @Bind(R.id.no_offers_label)
    public TextView offersLabel;
    private ArrayList<Offer> offerList;
    private MyOfferRecyclerViewAdapter myOfferAdapter;
    private boolean isTwoPane;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OfferFragment() {

    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static OfferFragment newInstance(int columnCount,boolean isTwoPane) {
        OfferFragment fragment = new OfferFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putBoolean(Constants.EXTRAS_IS_TWO_PANE,isTwoPane);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            isTwoPane = getArguments().getBoolean(Constants.EXTRAS_IS_TWO_PANE);
        }
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offer_list, container, false);
        ButterKnife.bind(this, view);

        setHasOptionsMenu(true);
        // Set the adapter


        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.offer_list);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        myOfferAdapter = new MyOfferRecyclerViewAdapter(OfferContent.ITEMS, mListener,getActivity(),isTwoPane);
        retrieveOfferList();
        displayEmptyList();
        recyclerView.setAdapter(myOfferAdapter);

        return view;
    }

    private void retrieveOfferList() {
        Log.d(TAG, "retrieveCustomerList");
        Realm realm = Realm.getDefaultInstance();
        RealmResults<OfferRealm> result = realm.where(OfferRealm.class).findAll();
        offerList = (ArrayList<Offer>) OfferRealmUtil.convertRealmOfferListToOfferList(result);
        Log.d(TAG, "customerList:" + offerList.toString());
        OfferContent.ITEMS.clear();
        OfferContent.ITEMS.addAll(offerList);
    }


    private void displayEmptyList() {
        if (OfferContent.ITEMS.size() == 0) {
            offersLabel.setVisibility(View.VISIBLE);
            createOffer.setVisibility(View.VISIBLE);
        } else {
            offersLabel.setVisibility(View.GONE);
            createOffer.setVisibility(View.GONE);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            //throw new RuntimeException(context.toString()
            //        + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Offer item);
    }

    @Nullable
    @OnClick(R.id.create_offer_btn)
    public void launchCreateOffer() {
        Log.d(TAG, "launchCreateOffer");
        Intent intent = new Intent(getActivity(), OfferActivity.class);
        startActivityForResult(intent,Constants.ADD_OFFER);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.ADD_OFFER && resultCode == Activity.RESULT_OK){
            displayEmptyList();
            retrieveOfferList();
            myOfferAdapter.notifyDataSetChanged();
        }
    }

    public void notifyDataChange() {
        if (myOfferAdapter != null) {
            displayEmptyList();
            retrieveOfferList();
            myOfferAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.offer_options_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
                case R.id.action_fitler_scheduled_sms: {

                retrieveScheduledOfferList();
                myOfferAdapter.notifyDataSetChanged();
                displayEmptyList();
                clearDetailScreen();
                break;
            }
            case R.id.action_fitler_sent_sms: {
                retrieveSentOfferList();
                myOfferAdapter.notifyDataSetChanged();
                displayEmptyList();
                clearDetailScreen();
                break;
            }
            case R.id.action_fitler_all_sms: {
                retrieveOfferList();
                myOfferAdapter.notifyDataSetChanged();
                displayEmptyList();
                clearDetailScreen();
                break;
            }
        }
        return super.onOptionsItemSelected(item);

    }

    public void clearDetailScreen(){
        if(isTwoPane){
            OfferDetailFragment offerDetailFragment = (OfferDetailFragment) ((BaseActivity)mListener).getSupportFragmentManager().findFragmentByTag(OfferDetailFragment.TAG);
            // -1 to clear the detail screen
            offerDetailFragment.setOfferId(-1);
        }
    }

    private void retrieveScheduledOfferList() {
        Log.d(TAG, "retrieveScheduledOfferList");
        Realm realm = Realm.getDefaultInstance();
        RealmResults<OfferRealm> result = realm.where(OfferRealm.class).equalTo("offerStatus",false).findAll();
        offerList = (ArrayList<Offer>) OfferRealmUtil.convertRealmOfferListToOfferList(result);
        Log.d(TAG, "scheduled offer List size:" + offerList.size());
        OfferContent.ITEMS.clear();
        OfferContent.ITEMS.addAll(offerList);
        if(isTwoPane){
            OfferDetailFragment offerDetailFragment = (OfferDetailFragment) ((BaseActivity)mListener).getSupportFragmentManager().findFragmentByTag(OfferDetailFragment.TAG);
            // -1 to clear the detail screen
            offerDetailFragment.setOfferId(-1);
        }
    }

    private void retrieveSentOfferList() {
        Log.d(TAG, "retrieveSentOfferList");
        Realm realm = Realm.getDefaultInstance();
        RealmResults<OfferRealm> result = realm.where(OfferRealm.class).equalTo("offerStatus",true).findAll();
        offerList = (ArrayList<Offer>) OfferRealmUtil.convertRealmOfferListToOfferList(result);
        Log.d(TAG, "sent offer List size:" + offerList.toString());
        OfferContent.ITEMS.clear();
        OfferContent.ITEMS.addAll(offerList);

    }
}
