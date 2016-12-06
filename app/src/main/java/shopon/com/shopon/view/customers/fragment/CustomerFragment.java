package shopon.com.shopon.view.customers.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import shopon.com.shopon.R;
import shopon.com.shopon.datamodel.customer.Customers;
import shopon.com.shopon.datamodel.customer.CustomersRealm;
import shopon.com.shopon.db.CustomerRealmUtil;
import shopon.com.shopon.db.provider.ShopOnContract;
import shopon.com.shopon.db.provider.ShopOnContractRealm;
import shopon.com.shopon.view.constants.Constants;
import shopon.com.shopon.view.customers.CustomerActivity;
import shopon.com.shopon.view.customers.adapter.MyCustomerRecyclerViewAdapter;
import shopon.com.shopon.view.customers.dummy.CustomerContent;
import shopon.com.shopon.view.tagview.Tag.Utils;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class CustomerFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    public static final String TAG = CustomerFragment.class.getName();


    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    @Bind(R.id.create_customer)
    Button createCustomer;
    @Bind(R.id.no_customer_label)
    TextView customerLabel;
    @Bind(R.id.list)
    RecyclerView customerListView;

    public LinearLayoutManager mLinearLayoutManager;
    private MyCustomerRecyclerViewAdapter customerAapter;
    private List<Customers> customerList;
    private boolean isTwoPane;
    private Cursor mCursor;
    private CustomerFragment customerFragment;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CustomerFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static CustomerFragment newInstance(int columnCount, boolean isTwoPane) {
        CustomerFragment fragment = new CustomerFragment();
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
        customerFragment = this;
        setRetainInstance(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().initLoader(Constants.ALL_CUSTOMERS, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_customer_list, container, false);
        ButterKnife.bind(this, view);
        // Set the adapter
        setHasOptionsMenu(true);
        Context context = view.getContext();

        if (mColumnCount <= 1) {
            mLinearLayoutManager = new LinearLayoutManager(context);
        } else {
            mLinearLayoutManager = new GridLayoutManager(context, mColumnCount);
        }
        customerListView.setLayoutManager(mLinearLayoutManager);


        customerAapter = new MyCustomerRecyclerViewAdapter(CustomerContent.ITEMS, mListener, false, isTwoPane);
        customerListView.setAdapter(customerAapter);
        displayEmptyList();


        return view;
    }


    private void retrieveCustomerList() {
        Log.d(TAG, "retrieveCustomerList");
        Realm realm = Realm.getDefaultInstance();
        RealmResults<CustomersRealm> result = realm.where(CustomersRealm.class).findAll();
        customerList = CustomerRealmUtil.convertRealmCustomerListToCustomerList(result);
        Log.d(TAG, "customerList:" + customerList.toString());
        CustomerContent.ITEMS.clear();
        CustomerContent.ITEMS.addAll(customerList);
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

    private void displayEmptyList() {
        if (CustomerContent.ITEMS.size() == 0) {
            customerLabel.setVisibility(View.VISIBLE);
            createCustomer.setVisibility(View.VISIBLE);
        } else {
            customerLabel.setVisibility(View.GONE);
            createCustomer.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.create_customer)
    public void createCustomer() {
        Intent intent = new Intent(getActivity(), CustomerActivity.class);
        getActivity().startActivityForResult(intent, Constants.ADD_CUSTOMER);
    }

    public void notifyDataChange() {
        Log.d(TAG, "notifyDataChange");
        getLoaderManager().initLoader(Constants.ALL_CUSTOMERS, null, this);
    }

    public void refreshView() {
        if (customerAapter != null) {
            if (mCursor != null) {
                populateCustomerListFromCursor(mCursor);
            }
            displayEmptyList();
            customerAapter.notifyDataSetChanged();
        }
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader");
        return new CursorLoader(getActivity(),  // Context
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
        refreshView();
        if (getLoaderManager().hasRunningLoaders()) {
            getLoaderManager().destroyLoader(Constants.ALL_CUSTOMERS);
        }

    }

    @Override
    public void onLoaderReset(Loader loader) {

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
        void onListFragmentInteraction(Customers item);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Log.d(TAG, "setUserVisibilityHint");
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (customerAapter != null) {
                getLoaderManager().initLoader(Constants.ALL_CUSTOMERS, null, this);
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult");
        getLoaderManager().initLoader(Constants.ALL_CUSTOMERS, null, customerFragment);
    }
}
