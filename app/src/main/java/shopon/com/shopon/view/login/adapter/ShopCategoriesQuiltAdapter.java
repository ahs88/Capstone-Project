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
    private LinkedHashMap<String,String> hashMap = new LinkedHashMap<>();
    private Activity mContext;
    private List<Map.Entry<String,String>> entryList;

    private LinkedHashMap<String,List<String>> hashMapSubCategories = new LinkedHashMap<>();
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

    public ShopCategoriesQuiltAdapter(CategoryLevelInterface context, CategoryList categoryList, boolean forRequests){
        mContext = (Activity) context;
        this.categoryList = categoryList;
        mCategoryForRequests = forRequests;
        loadCategories();
        shopQuiltAdapter = this;
        categoryLevelInterface = context;
        //Log.i(TAG,"onCreate | Quilt | forRequests" + forRequests);
    }

    public void setSubscribedTags(List<String> sTags)
    {
        selectedTags.clear();
        selectedTags.addAll((ArrayList)sTags);
    }

    public void enableSubLevel(boolean sub_level_enabled){
        subLevelEnabled = sub_level_enabled;
    }

    public void setCategoryPickOnlyOne (boolean categoryPickOnlyOne) {
        mCategoryPickOnlyOne = categoryPickOnlyOne;
    }

    private void loadCategories() {
        Log.i(TAG,"loadCategories :" + mCategoryForRequests);
        int index=0;
        //if(!mCategoryForRequests)
       // hashMap.put(mContext.getResources().getString(R.string.custom_category, ""), "");

        for(Category categoryData :  categoryList.getCategories())
        {
            hashMap.put(categoryData.getString().get(0), categoryData.getString().get(1));
            hashMapSubCategories.put(categoryData.getString().get(0), categoryData.getArray().getString());
            Log.d(TAG,"hashMapSubCategories:"+ categoryData.getArray().getString().size());
            index++;
        }
       /* hashMap.put(mContext.getString(R.string.womens_sc),R.drawable.women_shoe);
        hashMap.put(mContext.getString(R.string.mens_sc),R.drawable.menswear);
        hashMap.put(mContext.getString(R.string.children_sc),R.drawable.children_filled);
        hashMap.put(mContext.getString(R.string.shoes_sc),R.drawable.women_shoe);
        hashMap.put(mContext.getString(R.string.accessories),R.drawable.accessories);
        hashMap.put(mContext.getString(R.string.beauty_supplies),R.drawable.beauty);
        hashMap.put(mContext.getString(R.string.furniture),R.drawable.furniture);
        hashMap.put(mContext.getString(R.string.home_products),R.drawable.home_products);
        hashMap.put(mContext.getString(R.string.mobile_accessories),R.drawable.mobile);
        hashMap.put(mContext.getString(R.string.electronics),R.drawable.electronics);
        hashMap.put(mContext.getString(R.string.groceries),R.drawable.groceries);
        hashMap.put(mContext.getString(R.string.health),R.drawable.health);
        hashMap.put(mContext.getString(R.string.sports),R.drawable.sports_goods);
        hashMap.put(mContext.getString(R.string.speciality),R.drawable.speciality);
        hashMap.put(mContext.getString(R.string.pets),R.drawable.pets);
        hashMap.put(mContext.getString(R.string.restaurant),R.drawable.restaurant_filled);
        hashMap.put(mContext.getString(R.string.casual_food),R.drawable.casual_food);
        hashMap.put(mContext.getString(R.string.salon_massage),R.drawable.salon_massage);
        hashMap.put(mContext.getString(R.string.home_repair),R.drawable.home_repair);
        hashMap.put(mContext.getString(R.string.automative),R.drawable.automotive);
        hashMap.put(mContext.getString(R.string.education_category),R.drawable.education_sc);
        hashMap.put(mContext.getString(R.string.fitness),R.drawable.fitness);
        hashMap.put(mContext.getString(R.string.medical),R.drawable.medical);
        hashMap.put(mContext.getString(R.string.arts_entertainment),R.drawable.arts_and_entertainment);
        hashMap.put(mContext.getString(R.string.professional),R.drawable.professional);
        hashMap.put(mContext.getString(R.string.hotel_travel),R.drawable.hotel_travel);
        hashMap.put(mContext.getString(R.string.real_estate),R.drawable.realestate);*/
        entryList = new ArrayList<>(hashMap.entrySet());
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shop_category_quilt_item, viewGroup, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder itemViewHolder, int position) {

       /* if(position%10 == 0 && position!=0) {
            *//*StaggeredGridLayoutManager.LayoutParams layoutParams =
                    (StaggeredGridLayoutManager.LayoutParams) itemViewHolder.itemView.getLayoutParams(); *//*          //(ViewGroup.LayoutParams.MATCH_PARENT,
            //ViewGroup.LayoutParams.MATCH_PARENT);
            GridLayoutManager.LayoutParams layoutParams =
                    (GridLayoutManager.LayoutParams) itemViewHolder.itemView.getLayoutParams();
            layoutParams.height = DatabaseUtils.convertDpToPixel(140, mContext);
            Log.d(TAG, "bindShopFront setting cart layout params screenWidth");

            //layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            itemViewHolder.itemView.setLayoutParams(layoutParams);
        }*/

        if(level == 0) {//|| entryList.get(position).getKey() == null
                itemViewHolder.bind(entryList.get(position).getKey(), entryList.get(position).getValue(), hashMapSubCategories.get(entryList.get(position).getKey()), position);

        }
        else
        {
            itemViewHolder.bindSubCatgory(hashMapSubCategories.get(entryList.get(mainPositionEntered).getKey()).get(position));
        }
        //Here you can fill your row view
    }

    @Override
    public int getItemCount() {
        int size = 0;
        if(level == 0 || hashMapSubCategories.get(entryList.get(mainPositionEntered).getKey()) == null) {
            size =  entryList.size();
        } else {
            size  = hashMapSubCategories.get(entryList.get(mainPositionEntered).getKey()).size();
        }
        //Log.d(TAG,"getItemCount size:"+size+" level:"+level);
        return size;

    }


    public int getCategoryLevel(){
        return level;
    }

    public void setCategoryLevel(int level){
        this.level = level;
    }



    public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

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
            category_widget = (TextView)itemView.findViewById(R.id.categoryText);
            categoryContainer = (RelativeLayout)itemView.findViewById(R.id.category_container);
            categoryImage = (ImageView)itemView.findViewById(R.id.categoryImage);
            /*icon_widget = (ImageView)itemView.findViewById(R.id.categoryImage);
            check_box = (CheckBox)itemView.findViewById(R.id.checkbox);
            category_layout=(LinearLayout)itemView.findViewById(R.id.category_layout);*/
            //itemView.setOnClickListener(this);
            categoryContainer.setOnClickListener(this);
            selectDeselect = (ImageView)itemView.findViewById(R.id.select_deselect);
        }

        public void bind(String categoryName,String category_drawable,List<String> subCategories,int position){


            if(isTagSubscribed(categoryName)) {
                Log.i(TAG, "Category Found :" + categoryName);
                selectDeselect.setImageResource(R.drawable.ok_filled);
            }

            //display add button in green
            Log.i(TAG, "Category name :" + categoryName+" image:");

            int drawable  = mContext.getResources().getIdentifier("shopon.com.shopon:drawab‌​le/" + category_drawable, null, null);
            if(drawable > 0 ) {
                Picasso.with(mContext).load(drawable).into(categoryImage);
            }

            this.subCategories = subCategories;
            this.categoryName = categoryName;
            this.position = position;
            category_widget.setText(categoryName);

            //for accessibility
            categoryImage.setContentDescription(categoryName);

            if(selectedTags.contains(categoryName)) {
                Log.d(TAG, " selected main tags:" + categoryName);
                //category_widget.setBackgroundColor(mContext.getResources().getColor(R.color.green_shade));
                selectDeselect.setImageResource(R.drawable.ok_filled);
                categoryContainer.setBackgroundResource(R.drawable.category_bg_selected);
                //categoryContainer.setBackgroundResource(R.drawable.b_circlethin_3x);
            }
            else
            {
                selectDeselect.setImageResource(R.drawable.b_circlethin_2x);
                categoryContainer.setBackgroundResource(R.drawable.category_bg);
                //category_widget.setBackgroundColor(mContext.getResources().getColor(R.color.bazaar_red));
            }
            int resourceId = mContext.getResources().getIdentifier(category_drawable, "drawable", mContext.getPackageName());//initialize res and context in adapter's contructor
            categoryImage.setImageResource(resourceId);

            //icon_widget.setImageDrawable(mContext.getResources().getDrawable(category_drawable));
        }



        public void bindSubCatgory(String name){
            categoryName = name;
            category_widget.setText(name);
            if(selectedTags.contains(categoryName)) {
                //category_widget.setBackgroundColor(mContext.getResources().getColor(R.color.green_shade));
                selectDeselect.setImageResource(R.drawable.ok_filled);
                categoryContainer.setBackgroundResource(R.drawable.category_bg_selected);
            }
            else
            {
                selectDeselect.setImageResource(R.drawable.b_circlethin_2x);
                categoryContainer.setBackgroundResource(R.drawable.category_bg);
                //category_widget.setBackgroundColor(mContext.getResources().getColor(R.color.bazaar_red));
            }
            Drawable transparentDrawable = new ColorDrawable(Color.TRANSPARENT);
            categoryImage.setImageDrawable(transparentDrawable);
            categoryImage.setImageBitmap(null);
        }




        @Override
        public void onClick(View v) {
            if(level == 0) { // main category click handler
                Log.d(TAG," subLevelEnable:"+subLevelEnabled);
                if (!subLevelEnabled) {
                    selectUnselectCategory(categoryName);
                    return;
                }

                if(!selectedTags.contains(categoryName)) { //if not selected
                    mainPositionEntered = position;
                    if(subCategories != null) {
                       startSubCateoryActivity();
                        /*level = 1;
                        notifyDataSetChanged();
                        mainCategoryEntered = categoryName;
                        categoryLevelInterface.categoryLevel(level);*/
                    }
                    selectedTags.add(categoryName);
                    selectDeselect.setImageResource(R.drawable.ok_filled);
                    categoryContainer.setBackgroundResource(R.drawable.category_bg_selected);
                    //category_widget.setBackgroundColor(mContext.getResources().getColor(R.color.green_shade));
                }
                else
                {
                    // should find a way to check if subcategories are selected
                    //if subcategories of the main category are selected then go to level 1
                    if(subCategoriesSelected(subCategories)){
                        mainPositionEntered = position;
                        startSubCateoryActivity();
                        /*level = 1;
                        notifyDataSetChanged();
                        mainCategoryEntered = categoryName;
                        categoryLevelInterface.categoryLevel(level);*/
                    }
                    else { //if sub categories are not selected and main category already selected
                        selectedTags.remove(categoryName);
                        selectDeselect.setImageResource(R.drawable.b_circlethin_2x);
                        categoryContainer.setBackgroundResource(R.drawable.category_bg);
                        //category_widget.setBackgroundColor(mContext.getResources().getColor(R.color.bazaar_red));
                    }
                }
            }
            else //sub category  click handler
            {
                selectUnselectCategory(categoryName);
            }
        }

        public void selectUnselectCategory(String categoryName) {
            if (mCategoryPickOnlyOne) {
                selectedTags.clear();
                notifyDataSetChanged();
            }

            if(!selectedTags.contains(categoryName)) {
                selectedTags.add(categoryName);
                //category_widget.setBackgroundColor(mContext.getResources().getColor(R.color.green_shade));
                categoryContainer.setBackgroundResource(R.drawable.category_bg_selected);
                selectDeselect.setImageResource(R.drawable.ok_filled);
            } else {
                selectedTags.remove(categoryName);
                selectDeselect.setImageResource(R.drawable.b_circlethin_2x);
                categoryContainer.setBackgroundResource(R.drawable.category_bg);
                //category_widget.setBackgroundColor(mContext.getResources().getColor(R.color.bazaar_red));
            }
        }






        private boolean isTagSubscribed(String key) {
            if(selectedTags != null)
                return selectedTags.contains(key);
            else
                return false;
        }

        private boolean subCategoriesSelected(List<String> subCategories){
            if(subCategories == null)
                return false;
            for (String category : subCategories){
                if(selectedTags.contains(category)){
                    return true;
                }
            }
            return false;
        }


    }

    private void startSubCateoryActivity() {
        Intent intent =new Intent(mContext, SubCategoryActivity.class);
        intent.putStringArrayListExtra(Constants.SUB_CATEGORY_LIST,(ArrayList)hashMapSubCategories.get(entryList.get(mainPositionEntered).getKey()));
        intent.putStringArrayListExtra(Constants.SELECTED_CATEGORY_LIST,(ArrayList)getSelectedTags());
        mContext.startActivityForResult(intent,Constants.REQUEST_SUB_CATEGORY);
    }

    public List<String> getRenderedSubCategories() {
        return hashMapSubCategories.get(mainCategoryEntered);
    }

    public void addToSelectedTagsList(List<String> selectedTags){
        this.selectedTags.addAll(selectedTags);
    }

    public void removeFromSelectedTagsList(List<String> selectedTags){
        //Log.d(TAG,"removeFromSelectedTagsList selectedTags content:"+this.selectedTags+"  tags to be removed content:"+selectedTags);
        this.selectedTags.removeAll(selectedTags);
    }

    public void clearSelectedTags(){
        this.selectedTags.clear();
    }

    public List<String> getSelectedTags()
    {
        ArrayList<String> list = new ArrayList<>();
        list.addAll(selectedTags);
        return list;
    }

    public interface CategoryLevelInterface{
        void categoryLevel(int level);
    }

}
