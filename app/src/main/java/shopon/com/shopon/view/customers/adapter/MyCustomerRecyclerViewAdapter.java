package shopon.com.shopon.view.customers.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import shopon.com.shopon.R;
import shopon.com.shopon.datamodel.customer.Customers;
import shopon.com.shopon.utils.FontFitTextView;
import shopon.com.shopon.view.base.BaseActivity;
import shopon.com.shopon.view.constants.Constants;
import shopon.com.shopon.view.customers.CustomerDetailActivity;
import shopon.com.shopon.view.customers.fragment.CustomerDetailFragment;
import shopon.com.shopon.view.customers.fragment.CustomerFragment;


import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Customers} and makes a call to the
 * <p>
 * TODO: Replace the implementation with code for your data type.
 */
public class MyCustomerRecyclerViewAdapter extends RecyclerView.Adapter<MyCustomerRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = MyCustomerRecyclerViewAdapter.class.getName();
    private final List<Customers> mValues;
    private final CustomerFragment.OnListFragmentInteractionListener mListener;
    private final boolean isSelectable;
    private final List<Customers> selectedCustomers = new ArrayList<>();
    private final List<String> selectedNumbers = new ArrayList<>();
    private final boolean isTwoPane;


    public MyCustomerRecyclerViewAdapter(List<Customers> items, CustomerFragment.OnListFragmentInteractionListener listener, boolean selectable, boolean isTwoPane) {
        mValues = items;
        mListener = listener;
        isSelectable = selectable;
        this.isTwoPane = isTwoPane;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.customer_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder");
        holder.mItem = mValues.get(position);
        holder.mName.setText(mValues.get(position).getName());
        holder.mPhoneNumber.setText(mValues.get(position).getMobile());
        holder.mEmail.setText(mValues.get(position).getEmail());
        try {
            holder.nameFontView.setText(mValues.get(position).getName().substring(0, 1).toUpperCase());
        } catch (IndexOutOfBoundsException ie) {
            ie.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount:" + mValues.size());
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        public final TextView mName;
        public final TextView mEmail;
        public final TextView mPhoneNumber;
        public Customers mItem;
        public ImageView selectDeselect;
        public FontFitTextView nameFontView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mView.setOnClickListener(this);
            nameFontView = (FontFitTextView) view.findViewById(R.id.name_font);
            mName = (TextView) view.findViewById(R.id.name);
            mEmail = (TextView) view.findViewById(R.id.email);
            mPhoneNumber = (TextView) view.findViewById(R.id.phone_number);
            selectDeselect = (ImageView) view.findViewById(R.id.select_deselect);
            if (isSelectable) {
                selectDeselect.setVisibility(View.VISIBLE);
            } else {
                selectDeselect.setVisibility(View.GONE);
            }


        }

        @Override
        public String toString() {
            return super.toString() + " '" + mPhoneNumber.getText() + "'";
        }

        @Override
        public void onClick(View view) {
            if (!isSelectable) {
                //display detail activivty.
                if (isTwoPane) {
                    CustomerDetailFragment customerDetailFragment = (CustomerDetailFragment) ((BaseActivity) mListener).getSupportFragmentManager().findFragmentByTag(CustomerDetailFragment.TAG);
                    customerDetailFragment.setCustomerId(mItem.getId());
                } else {
                    Intent intent = new Intent((Activity) mListener, CustomerDetailActivity.class);
                    intent.putExtra(Constants.EXTRAS_CUSTOMER_ID, mItem.getId());
                    intent.putExtra(Constants.EXTRAS_IS_TWO_PANE, isTwoPane);
                    ((Activity) mListener).startActivityForResult(intent, Constants.ADD_CUSTOMER);
                }
                return;
            }
            if (!selectedCustomers.contains(mItem)) {
                selectedCustomers.add(mItem);
                selectedNumbers.add(mItem.getMobile());
                selectDeselect.setImageResource(R.drawable.ok_filled);
                selectDeselect.setContentDescription(((Activity)mListener).getString(R.string.customer_deselected,mItem.getName()));
            } else {
                selectedCustomers.remove(mItem);
                selectedNumbers.remove(mItem.getMobile());
                selectDeselect.setImageResource(R.drawable.b_circlethin_2x);
                selectDeselect.setContentDescription(((Activity)mListener).getString(R.string.customer_deselected,mItem.getName()));
            }
        }


    }


    public List<String> getSelectedNumbers() {
        return selectedNumbers;
    }

}
