package shopon.com.shopon.view.offer.adpater;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import shopon.com.shopon.R;
import shopon.com.shopon.datamodel.offer.Offer;
import shopon.com.shopon.utils.Utils;
import shopon.com.shopon.view.base.BaseActivity;
import shopon.com.shopon.view.constants.Constants;
import shopon.com.shopon.view.customers.fragment.CustomerDetailFragment;
import shopon.com.shopon.view.offer.OfferDetailActivity;
import shopon.com.shopon.view.offer.fragment.OfferDetailFragment;
import shopon.com.shopon.view.offer.fragment.OfferFragment;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Offer} and makes a call to the
 * specified {@link OfferFragment.OnListFragmentInteractionListener }.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyOfferRecyclerViewAdapter extends RecyclerView.Adapter<MyOfferRecyclerViewAdapter.ViewHolder> {

    private final List<Offer> mValues;
    private final OfferFragment.OnListFragmentInteractionListener mListener;
    private final boolean isTwoPane;
    private Context mContext;
    private final static String TAG = MyOfferRecyclerViewAdapter.class.getName();

    public MyOfferRecyclerViewAdapter(List<Offer> items, OfferFragment.OnListFragmentInteractionListener listener, Context context, boolean isTwoPane) {
        mValues = items;
        mListener = listener;
        mContext = context;
        this.isTwoPane = isTwoPane;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.offer_item_new, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.offerDateView.setText(holder.mItem.getDeliverMessageOn());
        holder.offerTextView.setText(((Context) mListener).getString(R.string.offer_tag) + holder.mItem.getOfferText());
        holder.daySummaryView.setText(Utils.getDateDisplayText(holder.mItem.getDeliverMessageOn()));


        List<String> numberList = new ArrayList<String>(Arrays.asList(holder.mItem.getNumbers().replace("[", "").replace("]", "").split(",")));
        holder.customerCountView.setText(String.valueOf(numberList.size()) + "C");

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        public TextView offerDateView;
        public final TextView offerTextView;
        public TextView customerCountView;
        public TextView daySummaryView;
        public Offer mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mView.setOnClickListener(this);
            offerDateView = (TextView) view.findViewById(R.id.offer_date);
            offerTextView = (TextView) view.findViewById(R.id.offer_text);
            customerCountView = (TextView) view.findViewById(R.id.customer_count);
            daySummaryView = (TextView) view.findViewById(R.id.date_summary);
        }


        @Override
        public String toString() {
            return super.toString() + " '" + offerTextView.getText() + "'";
        }

        @Override
        public void onClick(View view) {
            if (isTwoPane) {
                OfferDetailFragment offerDetailFragment = (OfferDetailFragment) ((BaseActivity) mListener).getSupportFragmentManager().findFragmentByTag(OfferDetailFragment.TAG);
                offerDetailFragment.setOfferId(mItem.getOfferId());
            } else {
                Intent intent = new Intent(mContext, OfferDetailActivity.class);
                intent.putExtra(Constants.EXTRAS_OFFER_ID, mItem.getOfferId());
                mContext.startActivity(intent);
            }
        }
    }


}
