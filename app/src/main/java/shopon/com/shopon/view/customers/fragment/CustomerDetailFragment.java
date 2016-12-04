package shopon.com.shopon.view.customers.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


import shopon.com.shopon.R;
import shopon.com.shopon.datamodel.DetailEntry;

import shopon.com.shopon.datamodel.customer.Customers;


import shopon.com.shopon.db.provider.ShopOnContract;
import shopon.com.shopon.preferences.UserSharedPreferences;
import shopon.com.shopon.remote.FireBaseUtils;
import shopon.com.shopon.utils.Utils;
import shopon.com.shopon.view.base.BaseFragment;
import shopon.com.shopon.view.constants.Constants;
import shopon.com.shopon.view.login.ShopCategoryActivity;
import shopon.com.shopon.view.offer.adpater.DetailOptionsAdapter;



import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CustomerDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CustomerDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomerDetailFragment extends BaseFragment {
    public static final String TAG = CustomerDetailFragment.class.getName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private DetailOptionsAdapter detailOptionsDetailAdapter;
    private RecyclerView customerDetail;
    private List<DetailEntry> customer_detail_entry = new ArrayList<>();
    private Customers customer;

    private DatabaseReference mDatabase;
    private UserSharedPreferences userSharedPreferences;


    private OnFragmentInteractionListener mListener;
    private int customerId;
    private View convertView;
    private boolean isTwoPane;
    private ArrayList<String> categoryList = new ArrayList<>();

    public CustomerDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     *
     * @return A new instance of fragment CustomerDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomerDetailFragment newInstance(int customer_id,boolean isTwoPane) {
        CustomerDetailFragment fragment = new CustomerDetailFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.EXTRAS_CUSTOMER_ID, customer_id);
        args.putBoolean(Constants.EXTRAS_IS_TWO_PANE,isTwoPane);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            customerId = getArguments().getInt(Constants.EXTRAS_CUSTOMER_ID);
            isTwoPane = getArguments().getBoolean(Constants.EXTRAS_IS_TWO_PANE);
        }
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.content_customer_detail, container, false);

            Log.d(TAG, "customerId:" + customerId);
            populateCustomerDetails(customerId);
            setHasOptionsMenu(true);
            setDetailAdapter();
        }

        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        return convertView;
    }

    public void setCustomerId(int customer_id){
        this.customerId = customer_id;
        customer_detail_entry.clear();
        populateCustomerDetails(customerId);
        detailOptionsDetailAdapter.notifyDataSetChanged();
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void initializeData() {

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void populateCustomerDetails(int customerId) {
        convertView.findViewById(R.id.no_customer_label).setVisibility(View.GONE);
        Cursor cursor = getActivity().getContentResolver().query(ShopOnContract.Entry.CONTENT_CUSTOMER_URI,null,ShopOnContract.Entry.COLUMN_CUSTOMER_ID+"=?",new String[]{String.valueOf(customerId)},null);
        if(cursor!=null) {
            cursor.moveToFirst();
            customer = Utils.createCustomerFromCursor(cursor);

            if (customer == null) {
                convertView.findViewById(R.id.no_customer_label).setVisibility(View.VISIBLE);
                return;
            }
        }
        String label[] = new String[]{"Name", "Number", "Email", "Interests"};
        String value[] = new String[]{customer.getName(), customer.getMobile(), customer.getEmail(), String.valueOf(customer.getIntrestedIn())};
        String error[] = new String[]{getString(R.string.name_err),getString(R.string.number_err),getString(R.string.email_err),getString(R.string.interests_err)};
        int editType[] = new int[]{InputType.TYPE_CLASS_TEXT,InputType.TYPE_CLASS_NUMBER,InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS,InputType.TYPE_CLASS_TEXT};
        Boolean isFocusable[] = new Boolean[]{true,true,true,false};
        Boolean isEditable[] = new Boolean[]{true,true,true,true};

        Method clickAction[] = new Method[]{null,null,null,getChooseInterests()};
        for (int i = 0; i < label.length; i++) {
            DetailEntry customer_details = new DetailEntry();
            customer_details.setKey(label[i]);
            customer_details.setValue(value[i]);
            customer_details.setErrorMsg(error[i]);
            customer_details.setEditable(isEditable[i]);
            customer_details.setFocusable(isFocusable[i]);
            customer_details.setClickEventAction(clickAction[i]);
            customer_details.setInputType(editType[i]);
            customer_detail_entry.add(customer_details);
        }
    }

    public void setDetailAdapter() {
        detailOptionsDetailAdapter = new DetailOptionsAdapter(getActivity(), customer_detail_entry,this);

        customerDetail = (RecyclerView) convertView.findViewById(R.id.customer_details);
        // use a linear layout manager
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        customerDetail.setLayoutManager(mLayoutManager);
        customerDetail.setAdapter(detailOptionsDetailAdapter);
    }

    public void chooseInterests(){
        Intent intent = new Intent(getActivity(), ShopCategoryActivity.class);
        intent.putStringArrayListExtra (Constants.SHOP_CATEGORY_SUBSCRIPTION_LIST, categoryList);
        startActivityForResult(intent, Constants.RETRIEVE_CATEGORY);
    }

    public Method getChooseInterests(){
        Method selectCustomerMethod = null;
        try {
            selectCustomerMethod = CustomerDetailFragment.class.getMethod("chooseInterests");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return selectCustomerMethod;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            /*throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");*/
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                break;
            case R.id.action_edit_detail: {
                detailOptionsDetailAdapter.setEditEnabled(true);
                detailOptionsDetailAdapter.notifyDataSetChanged();
                getActivity().invalidateOptionsMenu();
                break;
            }
            case R.id.action_save_detail:{
                if(!validateCustomer()){
                    detailOptionsDetailAdapter.setEditEnabled(true);
                    detailOptionsDetailAdapter.notifyDataSetChanged();
                    return false;
                }
                detailOptionsDetailAdapter.setEditEnabled(false);
                detailOptionsDetailAdapter.notifyDataSetChanged();
                Customers customer = updateLocalCustomer(customerId);
                updateRemoteCustomer(customer);
                if (isTwoPane) {
                    getActivity().invalidateOptionsMenu();
                }
                else {
                    getActivity().finish();
                }
            }
        }

        return super.onOptionsItemSelected(item);

    }

    private boolean validateCustomer() {
        customer_detail_entry.get(0).setErrStatus((TextUtils.isEmpty(customer_detail_entry.get(0).getValue())));
        boolean notValid = customer_detail_entry.get(0).isErrStatus();
        if(notValid){
            return false;
        }
        customer_detail_entry.get(1).setErrStatus(!Utils.isValidMobile(customer_detail_entry.get(1).getValue()));
        notValid = customer_detail_entry.get(1).isErrStatus();
        if(notValid){
            return false;
        }
        customer_detail_entry.get(2).setErrStatus(!Utils.isValidEmail(customer_detail_entry.get(2).getValue()));
        notValid = customer_detail_entry.get(2).isErrStatus();
        if(notValid){
            return false;
        }
        return true;
    }

    private Customers updateLocalCustomer(int customerId){
        ContentValues contentValues = new ContentValues();
        contentValues.put(ShopOnContract.Entry.COLUMN_NAME,customer_detail_entry.get(0).getValue().toString());
        contentValues.put(ShopOnContract.Entry.COLUMN_MOBILE,customer_detail_entry.get(1).getValue().toString());
        contentValues.put(ShopOnContract.Entry.COLUMN_EMAIL,customer_detail_entry.get(2).getValue().toString());
        contentValues.put(ShopOnContract.Entry.COLUMN_CUSTOMER_CATEGORY,customer_detail_entry.get(3).getValue().toString());
        getActivity().getContentResolver().update(ShopOnContract.Entry.CONTENT_CUSTOMER_URI,contentValues,ShopOnContract.Entry.COLUMN_CUSTOMER_ID+"=?",new String[]{String.valueOf(customerId)});

        Cursor cursor = getActivity().getContentResolver().query(ShopOnContract.Entry.CONTENT_CUSTOMER_URI,null,ShopOnContract.Entry.COLUMN_CUSTOMER_ID+"=?",new String[]{String.valueOf(customerId)},null);
        if(cursor!=null) {
            cursor.moveToFirst();
            Customers customer = Utils.createCustomerFromCursor(cursor);
            return customer;
        }else
        {
            return null;
        }

    }

    private void updateRemoteCustomer(Customers customer) {
        FireBaseUtils.updateCustomer(getActivity(),customer);
        //+customer_realm.getMobile()
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if(customer == null){
            hideOption(menu,R.id.action_save_detail);
            hideOption(menu,R.id.action_edit_detail);
        }
        else {
            if (detailOptionsDetailAdapter.isEditEnabled()) {
                Log.d(TAG, "edit enabled");
                hideOption(menu, R.id.action_edit_detail);
                showOption(menu, R.id.action_save_detail);
            } else {
                Log.d(TAG, "edit not enabled");
                hideOption(menu, R.id.action_save_detail);
                showOption(menu, R.id.action_edit_detail);
            }
        }

    }




    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.customer_options_menu,menu);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RETRIEVE_CATEGORY && resultCode == RESULT_OK) {
            categoryList.clear();
            categoryList.addAll(data.getExtras().getStringArrayList(ShopCategoryActivity.SELECTED_TAGS));
            categoryList = Utils.getUinqueElementsInList(categoryList);
            Log.d(TAG,"selectedTags:"+categoryList.toString());
            detailOptionsDetailAdapter.setNumbers(3,categoryList.toString().replace("[","").replace("]",""));
        }
    }

}
