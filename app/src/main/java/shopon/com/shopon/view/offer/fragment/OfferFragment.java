package shopon.com.shopon.view.offer.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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

import shopon.com.shopon.R;
import shopon.com.shopon.datamodel.offer.Offer;

import shopon.com.shopon.db.provider.ShopOnContract;

import shopon.com.shopon.utils.Utils;
import shopon.com.shopon.view.base.BaseActivity;
import shopon.com.shopon.view.constants.Constants;
import shopon.com.shopon.view.offer.OfferActivity;
import shopon.com.shopon.view.offer.adpater.MyOfferRecyclerViewAdapter;
import shopon.com.shopon.view.offer.dummy.OfferContent;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class OfferFragment extends Fragment implements LoaderManager.LoaderCallbacks {

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
    private Cursor mCursor;
    private OfferFragment offerFragment;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OfferFragment() {

    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static OfferFragment newInstance(int columnCount, boolean isTwoPane) {
        OfferFragment fragment = new OfferFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putBoolean(Constants.EXTRAS_IS_TWO_PANE, isTwoPane);
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
        offerFragment = this;
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

        myOfferAdapter = new MyOfferRecyclerViewAdapter(OfferContent.ITEMS, mListener, getActivity(), isTwoPane);

        displayEmptyList();
        recyclerView.setAdapter(myOfferAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        retrieveOfferList();
    }

    private void retrieveOfferList() {
        Log.d(TAG, "retrieveCustomerList");

        getLoaderManager().initLoader(Constants.ALL_OFFERS, null, offerFragment);

    }

    private void populateOfferListFromCursor(Cursor cursor) {
        OfferContent.ITEMS.clear();
        cursor.moveToFirst();
        Log.d(TAG, "populateOfferListFromCursor cursor:" + cursor.getCount());
        for (int i = 0; i < cursor.getCount(); i++) {
            Offer offer = new Offer();
            offer.setOfferId(cursor.getInt(0));
            offer.setOfferText(cursor.getString(1));
            offer.setOfferStatus((cursor.getInt(2) == 0) ? false : true);
            offer.setNumbers(cursor.getString(3));
            offer.setDeliverMessageOn(cursor.getString(4));
            OfferContent.ITEMS.add(offer);
            cursor.moveToNext();
        }

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
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id) {
            case Constants.ALL_OFFERS:
                Log.d(TAG, "loading all offers");
                return new CursorLoader(getActivity(),  // Context
                        ShopOnContract.Entry.CONTENT_OFFER_URI, // URI
                        null,                // Projection
                        null,                           // Selection
                        null,                           // Selection args
                        ShopOnContract.Entry.COLUMN_SCHEDULED_DATE + " desc");
            case Constants.SCHEDULED_OFFER:
                Log.d(TAG, "loading scheduled offers");
                return new CursorLoader(getActivity(),  // Context
                        ShopOnContract.Entry.CONTENT_OFFER_URI, // URI
                        null,                // Projection
                        ShopOnContract.Entry.COLUMN_OFFER_STATUS + "=?",                           // Selection
                        new String[]{String.valueOf(0)},                           // Selection args
                        ShopOnContract.Entry.COLUMN_SCHEDULED_DATE + " desc");
            case Constants.SENT_OFFER:
                Log.d(TAG, "loading sent offers");
                return new CursorLoader(getActivity(),  // Context
                        ShopOnContract.Entry.CONTENT_OFFER_URI, // URI
                        null,                // Projection
                        ShopOnContract.Entry.COLUMN_OFFER_STATUS + "=?",                           // Selection
                        new String[]{String.valueOf(1)},                           // Selection args
                        ShopOnContract.Entry.COLUMN_SCHEDULED_DATE + " desc");
        }
        return new CursorLoader(getActivity(),  // Context
                ShopOnContract.Entry.CONTENT_OFFER_URI, // URI
                null,                // Projection
                null,                           // Selection
                null,                           // Selection args
                ShopOnContract.Entry.COLUMN_SCHEDULED_DATE + " desc");
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        Cursor cursor = (Cursor) data;
        mCursor = cursor;
        refreshView();
        if (getLoaderManager().hasRunningLoaders()) {
            getLoaderManager().destroyLoader(Constants.ALL_OFFERS);
        }

    }

    @Override
    public void onLoaderReset(Loader loader) {

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
        getActivity().startActivityForResult(intent, Constants.ADD_OFFER);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult");
        if (requestCode == Constants.ADD_OFFER && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "onActivityResult loading all offers");
            retrieveOfferList();
            Utils.refreshAppWidget(getActivity());
        }
    }

    public void notifyDataChange() {
        retrieveOfferList();
        Utils.refreshAppWidget(getActivity());
    }

    public void refreshView() {
        if (myOfferAdapter != null) {
            if (mCursor != null)
                populateOfferListFromCursor(mCursor);

            displayEmptyList();
            myOfferAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Log.d(TAG, "setUserVisibilityHint");
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            displayEmptyList();
            if (myOfferAdapter != null) {

                retrieveOfferList();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.offer_options_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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

    public void clearDetailScreen() {
        if (isTwoPane) {
            OfferDetailFragment offerDetailFragment = (OfferDetailFragment) ((BaseActivity) mListener).getSupportFragmentManager().findFragmentByTag(OfferDetailFragment.TAG);
            // -1 to clear the detail screen
            offerDetailFragment.setOfferId(-1);
        }
    }

    private void retrieveScheduledOfferList() {

        Log.d(TAG, "retrieveScheduledOfferList");

        getLoaderManager().initLoader(Constants.SCHEDULED_OFFER, null, offerFragment);

    }

    private void retrieveSentOfferList() {


        Log.d(TAG, "retrieveScheduledOfferList");

        getLoaderManager().initLoader(Constants.SENT_OFFER, null, offerFragment);

    }


}
