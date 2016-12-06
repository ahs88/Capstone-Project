package shopon.com.shopon.view.login.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import java.util.ArrayList;
import java.util.List;

import shopon.com.shopon.R;

/**
 * A custom adapter to use with the RecyclerView widget.
 */
public class SubCategoriesAdapter extends RecyclerView.Adapter<SubCategoriesAdapter.SubCategoryHolder> {

    private String TAG = this.getClass().getCanonicalName();
    private Context mContext;
    private List<String> subTags;
    private boolean checkAllItemsFlag;
    private String mainTag;
    private List<String> selectedTags = new ArrayList<>();
    private List<String> mSubscribedTags;

    public SubCategoriesAdapter(Context context, ArrayList<String> tags) {
        mContext = context;
        subTags = tags;
        this.mainTag = mainTag;
    }

    public void setSubscribedTags(List<String> sTags) {
        mSubscribedTags = sTags;
    }

    @Override
    public SubCategoryHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sub_categories, viewGroup, false);
        return new SubCategoryHolder(view);
    }


    @Override
    public void onBindViewHolder(SubCategoryHolder itemViewHolder, int position) {
        itemViewHolder.bind(subTags.get(position), position);
        //Here you can fill your row view
    }

    @Override
    public int getItemCount() {
        return subTags.size();
    }

    public class SubCategoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //public TextView subCategory;
        public CheckedTextView subCategoryCheckBox;
        public int position;

        public SubCategoryHolder(View itemView) {
            super(itemView);
            subCategoryCheckBox = (CheckedTextView) itemView.findViewById(R.id.sub_category_check);
            subCategoryCheckBox.setOnClickListener(this);
        }

        public void bind(String s, int position) {
            Log.i(TAG, "Sub Category Bind Category:" + s + " checkAllItemsFlag:" + checkAllItemsFlag);

            this.position = position;
            subCategoryCheckBox.setText(s);
            subCategoryCheckBox.setChecked(checkAllItemsFlag);


            if (isTagSubscribed(s))
                subCategoryCheckBox.setChecked(true);

            setViewBackground(subCategoryCheckBox);
        }

        @Override
        public void onClick(View view) {
            CheckedTextView checkedTextView = (CheckedTextView) view;
            Log.d(TAG, "onClick" + checkedTextView.isChecked());
            if (!checkedTextView.isChecked()) {
                selectedTags.add(subTags.get(position));
                mSubscribedTags.add(subTags.get(position));
                checkedTextView.setChecked(true);

            } else {
                selectedTags.remove(subTags.get(position));
                mSubscribedTags.remove(subTags.get(position));
                checkedTextView.setChecked(false);
            }
            setViewBackground(checkedTextView);
        }
    }

    public void setViewBackground(CheckedTextView view) {
        if (view.isChecked()) {
            view.setBackgroundColor(mContext.getResources().getColor(R.color.bazaar_app_bar));
            view.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            view.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            view.setTextColor(mContext.getResources().getColor(R.color.bazaar_app_bar));
        }
    }

    public void selectAllItems() {
        checkAllItemsFlag = true;
        selectedTags.addAll(subTags);
        mSubscribedTags.addAll(subTags);
        notifyDataSetChanged();
    }

    public void deselectAllItems() {
        checkAllItemsFlag = false;
        selectedTags.clear();
        mSubscribedTags.clear();
        notifyDataSetChanged();
    }

    public List<String> getSelecedTags() {
        return selectedTags;
    }

    private boolean isTagSubscribed(String key) {
        if (mSubscribedTags != null)
            return mSubscribedTags.contains(key);
        else
            return false;
    }


}
