package shopon.com.shopon.view.offer.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DatabaseReference;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import io.realm.Realm;
import shopon.com.shopon.R;
import shopon.com.shopon.datamodel.DetailEntry;
import shopon.com.shopon.datamodel.offer.Offer;
import shopon.com.shopon.datamodel.offer.OfferRealm;
import shopon.com.shopon.db.OfferRealmUtil;
import shopon.com.shopon.db.provider.ShopOnContract;
import shopon.com.shopon.remote.FireBaseUtils;
import shopon.com.shopon.utils.Utils;
import shopon.com.shopon.view.base.BaseFragment;
import shopon.com.shopon.view.constants.Constants;
import shopon.com.shopon.view.customers.SelectableCustomers;
import shopon.com.shopon.view.offer.DateTimPickerUtils;
import shopon.com.shopon.view.offer.OfferActivity;
import shopon.com.shopon.view.offer.adpater.DetailOptionsAdapter;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OfferDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OfferDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OfferDetailFragment extends BaseFragment {
   


    private OnFragmentInteractionListener mListener;
    private View convertView;
    private DatabaseReference mDatabase;
    private int offerId;
    private Offer offer;
    private DetailOptionsAdapter detailOptionsDetailAdapter;
    private RecyclerView offerDetail;
    private List<DetailEntry> offer_detail_entry = new ArrayList<>();
    private Menu menu;
    private boolean isTwoPane;
    private List<String> customerList = new ArrayList<>();

    public OfferDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     
     * @return A new instance of fragment OfferDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OfferDetailFragment newInstance(int offer_id,boolean isTwoPane) {
        OfferDetailFragment fragment = new OfferDetailFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.EXTRAS_OFFER_ID, offer_id);
        args.putBoolean(Constants.EXTRAS_IS_TWO_PANE, isTwoPane);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            offerId = getArguments().getInt(Constants.EXTRAS_OFFER_ID);
            isTwoPane = getArguments().getBoolean(Constants.EXTRAS_IS_TWO_PANE);
        }
        setRetainInstance(true);

    }

    @Override
    public void initializeData() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG,"onCreateView");
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.content_offer_detail, container, false);
            populateOfferDetails(offerId);
            setHasOptionsMenu(true);
            setDetailAdapter();
        }

        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        return convertView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
           /* throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");*/
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setOfferId(int offerId) {
        this.offerId = offerId;
        offer_detail_entry.clear();
        populateOfferDetails(offerId);
        detailOptionsDetailAdapter.notifyDataSetChanged();
        getActivity().invalidateOptionsMenu();
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

    private void populateOfferDetails(int offerId) {
        convertView.findViewById(R.id.no_offer_label).setVisibility(View.GONE);
        Cursor cursor = getActivity().getContentResolver().query(ShopOnContract.Entry.CONTENT_OFFER_URI,null,ShopOnContract.Entry.COLUMN_OFFER_ID+"=?",new String[]{String.valueOf(offerId)},null);
        if(cursor!=null) {
            cursor.moveToFirst();
            offer = Utils.createOfferFromCursor(cursor);

            if (offer == null) {
                convertView.findViewById(R.id.no_offer_label).setVisibility(View.VISIBLE);
                return;
            }
        }
        customerList.addAll(Arrays.asList(offer.getNumbers().replace("[","").replace("]","").split(",")));
        String label[] = new String[]{"Offer Text","Delivery Date","Recipients","Status"};
        int editType[] = new int[]{InputType.TYPE_CLASS_TEXT,InputType.TYPE_CLASS_TEXT,InputType.TYPE_CLASS_TEXT,InputType.TYPE_CLASS_TEXT};
        String value[] =new String[]{offer.getOfferText(),offer.getDeliverMessageOn(),offer.getNumbers().replace("[","").replace("]",""),offer.getOfferStatus()?getString(R.string.sms_sent):getString(R.string.sms_scheduled)};
        String error[] = new String[]{getString(R.string.offer_text_err),getString(R.string.delivery_date_err),getString(R.string.receipient_err),""};
        Boolean isFocusable[] = new Boolean[]{true,false,false,false};
        Boolean isEditable[] = new Boolean[]{true,true,true,false};
        Method clickAction[] = new Method[]{null,getChooseDate(),getSelectableCustomers(),null};
        for(int i =0;i<label.length;i++){
            DetailEntry offerDetail = new DetailEntry();
            offerDetail.setKey(label[i]);
            offerDetail.setValue(value[i]);
            offerDetail.setErrorMsg(error[i]);
            offerDetail.setEditable(isEditable[i]);
            offerDetail.setFocusable(isFocusable[i]);
            offerDetail.setInputType(editType[i]);
            offerDetail.setClickEventAction(clickAction[i]);
            offer_detail_entry.add(offerDetail);
        }
    }

    public void setDetailAdapter(){
        detailOptionsDetailAdapter = new DetailOptionsAdapter(getActivity(),offer_detail_entry,this);

        offerDetail = (RecyclerView) convertView.findViewById(R.id.offer_details);
        // use a linear layout manager
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        offerDetail.setLayoutManager(mLayoutManager);
        offerDetail.setAdapter(detailOptionsDetailAdapter);
    }





    public void chooseDate(final Context context, final DateTimPickerUtils.ScheduledDateInterface date_interface) {
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                DateTimPickerUtils dateTimePickerUtis = new DateTimPickerUtils(context, date_interface);
                dateTimePickerUtis.schedule();
            }
        };
        View view = getActivity().getCurrentFocus();
        Log.d(TAG,"choose date detail view:"+view);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            handler.postDelayed(runnable, 500);
        }
    }

    public Method getChooseDate(){
        //date method
        Class[] parameterTypes = new Class[2];
        parameterTypes[0] = Context.class;
        parameterTypes[1] = DateTimPickerUtils.ScheduledDateInterface.class;
        Method dateMethod = null;
        try {
            dateMethod = OfferDetailFragment.class.getMethod("chooseDate", parameterTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return dateMethod;
    }

    public Method getSelectableCustomers(){
        Method selectCustomerMethod = null;
        try {
            selectCustomerMethod = OfferDetailFragment.class.getMethod("selectableCustomers");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return selectCustomerMethod;
    }

    public void selectableCustomers(){
        Intent intent = new Intent(getActivity(), SelectableCustomers.class);
        startActivityForResult(intent, Constants.RETRIEVE_MSISDN);
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
                if(!validateOffer()) {
                    detailOptionsDetailAdapter.setEditEnabled(true);
                    return false;
                }
                detailOptionsDetailAdapter.setEditEnabled(false);
                detailOptionsDetailAdapter.notifyDataSetChanged();
                Offer offer = updaLocalOffer(offerId);
                if(offer !=null) {
                    updateRemoteOffer(offer);
                }


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

    private Offer updaLocalOffer(int offerId) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(ShopOnContract.Entry.COLUMN_OFFER_TEXT,offer_detail_entry.get(0).getValue());
        contentValues.put(ShopOnContract.Entry.COLUMN_SCHEDULED_DATE,offer_detail_entry.get(1).getValue());
        contentValues.put(ShopOnContract.Entry.COLUMN_CUSTOMER_NUMBERS,offer_detail_entry.get(2).getValue());
        contentValues.put(ShopOnContract.Entry.COLUMN_OFFER_STATUS,offer_detail_entry.get(3).getValue().equals(getString(R.string.sms_sent))?true:false);
        getActivity().getContentResolver().update(ShopOnContract.Entry.CONTENT_OFFER_URI,contentValues,ShopOnContract.Entry.COLUMN_OFFER_ID+"=?",new String[]{String.valueOf(offerId)});

        Cursor cursor = getActivity().getContentResolver().query(ShopOnContract.Entry.CONTENT_OFFER_URI,null,ShopOnContract.Entry.COLUMN_OFFER_ID+"=?",new String[]{String.valueOf(offerId)},null);
        if(cursor!=null) {
            cursor.moveToFirst();
            Offer offer = Utils.createOfferFromCursor(cursor);
            return offer;
        }else
        {
            return null;
        }

    }

    private void updateRemoteOffer(Offer offer){
        Log.d(TAG,"remote update offer_id:"+offer.getOfferId());
        FireBaseUtils.updateOfferDataBase(getActivity(),offer);
    }



    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        Log.d(TAG,"onPrepareOptionsMenu");
        if(offer == null || offer.getOfferStatus()){
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
            this.menu = menu;
            inflater.inflate(R.menu.detail_options, menu);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == Constants.RETRIEVE_MSISDN){
            customerList.clear();
            customerList.addAll(data.getExtras().getStringArrayList(Constants.SELECTED_NUMBERS));
            detailOptionsDetailAdapter.setNumbers(2,customerList.toString().replace("[","").replace("]",""));
        }
    }


    public boolean validateOffer(){
        if(TextUtils.isEmpty(offer_detail_entry.get(0).getValue().trim())){
            offer_detail_entry.get(0).setErrStatus(true);
            detailOptionsDetailAdapter.notifyDataSetChanged();
            return false;
        }else {
            offer_detail_entry.get(0).setErrStatus(false);
        }


        if(TextUtils.isEmpty(offer_detail_entry.get(2).getValue().trim())){
            offer_detail_entry.get(2).setErrStatus(true);
            detailOptionsDetailAdapter.notifyDataSetChanged();
            return false;
        }else{
            offer_detail_entry.get(0).setErrStatus(false);
        }

        detailOptionsDetailAdapter.notifyDataSetChanged();
        return true;
    }

}
