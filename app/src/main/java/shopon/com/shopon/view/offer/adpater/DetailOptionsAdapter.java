package shopon.com.shopon.view.offer.adpater;

import android.app.Activity;
import android.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;
import shopon.com.shopon.R;
import shopon.com.shopon.datamodel.DetailEntry;
import shopon.com.shopon.view.offer.DateTimPickerUtils;

/**
 * A custom adapter to use with the RecyclerView widget.
 */
public class DetailOptionsAdapter extends RecyclerView.Adapter<DetailOptionsAdapter.DetailOptionHolder> implements DateTimPickerUtils.ScheduledDateInterface {

    private static final String TAG = DetailOptionsAdapter.class.getName();
    private Activity mContext;
    private List<DetailEntry> offerDetails;
    private boolean isEditEnabled;
    private android.support.v4.app.Fragment fragment;

    public DetailOptionsAdapter(Activity context, List<DetailEntry> offer_details, android.support.v4.app.Fragment fragment){
        mContext = context;
        offerDetails = offer_details;
        this.fragment = fragment;
    }
    


    @Override
    public DetailOptionHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Log.d(TAG,"onCreateViewHolder");

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.detail_item, viewGroup, false);
        return new DetailOptionHolder(view);
    }

    @Override
    public void onBindViewHolder(DetailOptionHolder itemViewHolder, int position) {
        Log.d(TAG,"onBindViewHolder  position:"+position);
        itemViewHolder.bindView(position);
        //Here you can fill your row view
    }

    @Override
    public int getItemCount() {
        return offerDetails.size();
    }

    public void setEditEnabled(boolean is_enabled){
        isEditEnabled = is_enabled;
    }

    public boolean isEditEnabled(){
        return isEditEnabled;
    }

    @Override
    public void scheduledDate(String scheduledDate, Calendar calendar) {

    }

    public void setNumbers(int key,String value) {
        DetailEntry detailEntry = offerDetails.get(key);
        detailEntry.setValue(value);
        notifyDataSetChanged();
    }

    /*public void setInterests(int key, String value) {
        DetailEntry detailEntry = offerDetails.get(key);
        detailEntry.setValue(value);
        notifyDataSetChanged();
    }*/

    public class DetailOptionHolder extends RecyclerView.ViewHolder implements View.OnClickListener,DateTimPickerUtils.ScheduledDateInterface{

        @Bind(R.id.label_name)
        TextView label;
        @Bind(R.id.label_value)
        EditText value;
        @Bind(R.id.error)
        TextView errorView;
        int position;
        DetailEntry offerDetailEntry;

        public DetailOptionHolder(View itemView) {
            super(itemView);
            try {
                ButterKnife.bind(this, itemView);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }



        public void bindView(int pos) {
            position = pos;
            Log.d(TAG,"bindView details:"+offerDetails.get(pos).toString());
            try {
                this.offerDetailEntry = offerDetails.get(pos);
                //set key/value name
                this.label.setText (offerDetailEntry.getKey());
                this.value.setText(offerDetailEntry.getValue());

                //label.setText(priceTag[1]);
                //setEditstate and event listener
                Log.d(TAG,"iseditable :"+offerDetailEntry.isEditable()+" isFocusable:"+offerDetailEntry.isFocusable()+" iseditenabled:"+isEditEnabled);
                if(isEditEnabled ) {
                    value.setEnabled(offerDetailEntry.isEditable());
                    value.setFocusable(offerDetailEntry.isFocusable());
                    value.setOnClickListener(this);
                }
                else{
                    value.setEnabled(false);
                }

                //set inputType
                value.setInputType(offerDetailEntry.getInputType());

                //display error if client validation failed
                if(offerDetailEntry.isErrStatus()){
                    errorView.setText(offerDetailEntry.getErrorMsg());
                    errorView.setVisibility(View.VISIBLE);
                }
                else
                {
                    errorView.setVisibility(View.GONE);
                }

            }catch (IndexOutOfBoundsException io){
               return;
            }catch(NullPointerException ne){
                return;
            }
        }


        @OnFocusChange(R.id.label_value)
        void onFocusChanged(boolean focused) {
            Log.d(TAG,"onFocusChanged");
            if(focused) {
                //value.setText("");
            } else {
                if(TextUtils.isEmpty(value.getText().toString())){
                    value.setText((offerDetailEntry!=null)?offerDetailEntry.getValue():"");
                }
                if(!TextUtils.isEmpty(value.getText().toString())) {
                    offerDetailEntry.setValue(value.getText().toString());
                }
            }
        }

        @OnTextChanged(R.id.label_value)
        void onTextChanged(Editable editable){
            Log.d(TAG,"onTextChanged:"+editable.toString());
            //if(!TextUtils.isEmpty(value.getText().toString())) {
                offerDetailEntry.setValue(value.getText().toString());
            //}
        }


        @Override
        public void onClick(View view) {
            Log.d(TAG,"onCLick");
            Method method = offerDetailEntry.getClickEventAction();

            if(method != null) {
                if(method.getName().equals("chooseDate")) {

                    try {
                        method.invoke(fragment, mContext, this);
                        Log.d(TAG,"choose date clicked in detail");
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    };
                }
                if(method.getName().equals("selectableCustomers")) {
                    try {
                        method.invoke(fragment);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    };
                }

                if(method.getName().equals("chooseInterests")) {
                    try {
                        method.invoke(fragment);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    };
                }

            }
        }

        @Override
        public void scheduledDate(String scheduledDate, Calendar calendar) {
            value.setText(scheduledDate);
        }
    }

}
