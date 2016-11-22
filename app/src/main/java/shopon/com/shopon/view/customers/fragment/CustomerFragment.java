package shopon.com.shopon.view.customers.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import shopon.com.shopon.utils.Utils;
import shopon.com.shopon.view.constants.Constants;
import shopon.com.shopon.view.customers.CustomerActivity;
import shopon.com.shopon.view.customers.adapter.MyCustomerRecyclerViewAdapter;
import shopon.com.shopon.view.customers.dummy.CustomerContent;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class CustomerFragment extends Fragment {

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

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CustomerFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static CustomerFragment newInstance(int columnCount,boolean isTwoPane) {
        CustomerFragment fragment = new CustomerFragment();
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
            isTwoPane= getArguments().getBoolean(Constants.EXTRAS_IS_TWO_PANE);
        }
        setRetainInstance(true);
    }

    @Override
    public void onResume() {
        super.onResume();

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

        retrieveCustomerList();
        customerAapter = new MyCustomerRecyclerViewAdapter(CustomerContent.ITEMS, mListener,false,isTwoPane);
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
        startActivityForResult(intent, Constants.ADD_CUSTOMER);
    }

    public void notifyDataChange() {
        if (customerAapter != null) {
            retrieveCustomerList();
            displayEmptyList();
            customerAapter.notifyDataSetChanged();
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
            displayEmptyList();
            if (customerAapter != null) {
                retrieveCustomerList();
                customerAapter.notifyDataSetChanged();
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.ADD_CUSTOMER && resultCode == Activity.RESULT_OK){
            displayEmptyList();
            retrieveCustomerList();
            customerAapter.notifyDataSetChanged();
        }
    }
}
