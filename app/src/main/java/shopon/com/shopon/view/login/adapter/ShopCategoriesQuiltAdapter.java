package shopon.com.shopon.view.login.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import shopon.com.shopon.R;
import shopon.com.shopon.datamodel.shop_product_categories.Category;
import shopon.com.shopon.datamodel.shop_product_categories.CategoryList;
import shopon.com.shopon.view.constants.Constants;

import shopon.com.shopon.view.login.view.SubCategoryActivity;

/**
 * A custom adapter to use with the RecyclerView widget.
 */
public class ShopCategoriesQuiltAdapter extends RecyclerView.Adapter<ShopCategoriesQuiltAdapter.CategoryViewHolder> {

    public static final String CATEGORY_NAME = "CATEGORY_NAME";
    private static final String TAG = "TAG";
    private int level = 0;
    public static final int CUSTOM_CATEGORY_CODE = 200;
    private LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
    private Activity mContext;
    private List<Map.Entry<String, String>> entryList;

    private LinkedHashMap<String, List<String>> hashMapSubCategories = new LinkedHashMap<>();
    private int mainPositionEntered = 0;


    private CategoryList categoryList;
    private List<String> mSubscribedTags;
    private HashSet<String> selectedTags = new HashSet<>();

    private ShopCategoriesQuiltAdapter shopQuiltAdapter;
    private boolean subLevelEnabled = true;
    private String mainCategoryEntered;
    private CategoryLevelInterface categoryLevelInterface;
    private boolean mCategoryPickOnlyOne;
    private boolean mCategoryForRequests;

    public ShopCategoriesQuiltAdapter(CategoryLevelInterface context, CategoryList categoryList, boolean forRequests) {
        mContext = (Activity) context;
        this.categoryList = categoryList;
        mCategoryForRequests = forRequests;
        loadCategories();
        shopQuiltAdapter = this;
        categoryLevelInterface = context;
    }

    public void setSubscribedTags(List<String> sTags) {
        selectedTags.clear();
        selectedTags.addAll((ArrayList) sTags);
    }

    public void enableSubLevel(boolean sub_level_enabled) {
        subLevelEnabled = sub_level_enabled;
    }

    public void setCategoryPickOnlyOne(boolean categoryPickOnlyOne) {
        mCategoryPickOnlyOne = categoryPickOnlyOne;
    }

    private void loadCategories() {
        Log.i(TAG, "loadCategories :" + mCategoryForRequests);
        int index = 0;

        for (Category categoryData : categoryList.getCategories()) {
            hashMap.put(categoryData.getString().get(0), categoryData.getString().get(1));
            hashMapSubCategories.put(categoryData.getString().get(0), categoryData.getArray().getString());
            Log.d(TAG, "hashMapSubCategories:" + categoryData.getArray().getString().size());
            index++;
        }

        entryList = new ArrayList<>(hashMap.entrySet());
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shop_category_quilt_item, viewGroup, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder itemViewHolder, int position) {


        if (level == 0) {
            itemViewHolder.bind(entryList.get(position).getKey(), entryList.get(position).getValue(), hashMapSubCategories.get(entryList.get(position).getKey()), position);

        } else {
            itemViewHolder.bindSubCatgory(hashMapSubCategories.get(entryList.get(mainPositionEntered).getKey()).get(position));
        }
        //Here you can fill your row view
    }

    @Override
    public int getItemCount() {
        int size = 0;
        if (level == 0 || hashMapSubCategories.get(entryList.get(mainPositionEntered).getKey()) == null) {
            size = entryList.size();
        } else {
            size = hashMapSubCategories.get(entryList.get(mainPositionEntered).getKey()).size();
        }
        //Log.d(TAG,"getItemCount size:"+size+" level:"+level);
        return size;

    }


    public int getCategoryLevel() {
        return level;
    }

    public void setCategoryLevel(int level) {
        this.level = level;
    }


    public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView category_widget;
        private ImageView icon_widget;
        private String categoryName;
        private CheckBox check_box;
        private List<String> subCategories;
        private int position;
        private ImageView selectDeselect;
        private LinearLayout category_layout;
        private RelativeLayout categoryContainer;
        private ImageView categoryImage;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            category_widget = (TextView) itemView.findViewById(R.id.categoryText);
            categoryContainer = (RelativeLayout) itemView.findViewById(R.id.category_container);
            categoryImage = (ImageView) itemView.findViewById(R.id.categoryImage);
            categoryContainer.setOnClickListener(this);
            selectDeselect = (ImageView) itemView.findViewById(R.id.select_deselect);
        }

        public void bind(String categoryName, String category_drawable, List<String> subCategories, int position) {


            if (isTagSubscribed(categoryName)) {
                Log.i(TAG, "Category Found :" + categoryName);
                selectDeselect.setImageResource(R.drawable.ok_filled);
            }

            //display add button in green
            Log.i(TAG, "Category name :" + categoryName + " image:");

            int drawable = mContext.getResources().getIdentifier("shopon.com.shopon:drawab‌​le/" + category_drawable, null, null);
            if (drawable > 0) {
                Picasso.with(mContext).load(drawable).into(categoryImage);
            }

            this.subCategories = subCategories;
            this.categoryName = categoryName;
            this.position = position;
            category_widget.setText(categoryName);

            //for accessibility
            categoryImage.setContentDescription(categoryName);

            if (selectedTags.contains(categoryName)) {
                Log.d(TAG, " selected main tags:" + categoryName);
                selectDeselect.setImageResource(R.drawable.ok_filled);
                categoryContainer.setBackgroundResource(R.drawable.category_bg_selected);
                selectDeselect.setContentDescription(mContext.getString(R.string.customer_selected,categoryName));
            } else {
                selectDeselect.setImageResource(R.drawable.b_circlethin_2x);
                categoryContainer.setBackgroundResource(R.drawable.category_bg);
                selectDeselect.setContentDescription(mContext.getString(R.string.customer_deselected,categoryName));
            }
            int resourceId = mContext.getResources().getIdentifier(category_drawable, "drawable", mContext.getPackageName());//initialize res and context in adapter's contructor
            categoryImage.setImageResource(resourceId);
        }


        public void bindSubCatgory(String name) {
            categoryName = name;
            category_widget.setText(name);
            if (selectedTags.contains(categoryName)) {
                selectDeselect.setImageResource(R.drawable.ok_filled);
                categoryContainer.setBackgroundResource(R.drawable.category_bg_selected);
                selectDeselect.setContentDescription(mContext.getString(R.string.customer_selected,categoryName));
            } else {
                selectDeselect.setImageResource(R.drawable.b_circlethin_2x);
                categoryContainer.setBackgroundResource(R.drawable.category_bg);
                selectDeselect.setContentDescription(mContext.getString(R.string.customer_deselected,categoryName));
            }
            Drawable transparentDrawable = new ColorDrawable(Color.TRANSPARENT);
            categoryImage.setImageDrawable(transparentDrawable);
            categoryImage.setImageBitmap(null);
        }


        @Override
        public void onClick(View v) {
            if (level == 0) { // main category click handler
                Log.d(TAG, " subLevelEnable:" + subLevelEnabled);
                if (!subLevelEnabled) {
                    selectUnselectCategory(categoryName);
                    return;
                }

                if (!selectedTags.contains(categoryName)) { //if not selected
                    mainPositionEntered = position;
                    if (subCategories != null) {
                        startSubCateoryActivity();
                    }
                    selectedTags.add(categoryName);
                    selectDeselect.setImageResource(R.drawable.ok_filled);
                    categoryContainer.setBackgroundResource(R.drawable.category_bg_selected);
                    selectDeselect.setContentDescription(mContext.getString(R.string.customer_selected,categoryName));
                } else {
                    // should find a way to check if subcategories are selected
                    if (subCategoriesSelected(subCategories)) {
                        mainPositionEntered = position;
                        startSubCateoryActivity();
                    } else { //if sub categories are not selected and main category already selected
                        selectedTags.remove(categoryName);
                        selectDeselect.setImageResource(R.drawable.b_circlethin_2x);
                        categoryContainer.setBackgroundResource(R.drawable.category_bg);
                        selectDeselect.setContentDescription(mContext.getString(R.string.customer_deselected,categoryName));
                    }
                }
            } else //sub category  click handler
            {
                selectUnselectCategory(categoryName);
            }
        }

        public void selectUnselectCategory(String categoryName) {
            if (mCategoryPickOnlyOne) {
                selectedTags.clear();
                notifyDataSetChanged();
            }

            if (!selectedTags.contains(categoryName)) {
                selectedTags.add(categoryName);
                categoryContainer.setBackgroundResource(R.drawable.category_bg_selected);
                selectDeselect.setImageResource(R.drawable.ok_filled);
            } else {
                selectedTags.remove(categoryName);
                selectDeselect.setImageResource(R.drawable.b_circlethin_2x);
                categoryContainer.setBackgroundResource(R.drawable.category_bg);
            }
        }


        private boolean isTagSubscribed(String key) {
            if (selectedTags != null)
                return selectedTags.contains(key);
            else
                return false;
        }

        private boolean subCategoriesSelected(List<String> subCategories) {
            if (subCategories == null)
                return false;
            for (String category : subCategories) {
                if (selectedTags.contains(category)) {
                    return true;
                }
            }
            return false;
        }


    }

    private void startSubCateoryActivity() {
        Intent intent = new Intent(mContext, SubCategoryActivity.class);
        intent.putStringArrayListExtra(Constants.SUB_CATEGORY_LIST, (ArrayList) hashMapSubCategories.get(entryList.get(mainPositionEntered).getKey()));
        intent.putStringArrayListExtra(Constants.SELECTED_CATEGORY_LIST, (ArrayList) getSelectedTags());
        mContext.startActivityForResult(intent, Constants.REQUEST_SUB_CATEGORY);
    }

    public List<String> getRenderedSubCategories() {
        return hashMapSubCategories.get(mainCategoryEntered);
    }

    public void addToSelectedTagsList(List<String> selectedTags) {
        this.selectedTags.addAll(selectedTags);
    }

    public void removeFromSelectedTagsList(List<String> selectedTags) {
        this.selectedTags.removeAll(selectedTags);
    }

    public void clearSelectedTags() {
        this.selectedTags.clear();
    }

    public List<String> getSelectedTags() {
        ArrayList<String> list = new ArrayList<>();
        list.addAll(selectedTags);
        return list;
    }

    public interface CategoryLevelInterface {
        void categoryLevel(int level);
    }

}
