package shopon.com.shopon.view.login.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import shopon.com.shopon.R;
import shopon.com.shopon.view.base.BaseActivity;
import shopon.com.shopon.view.constants.Constants;
import shopon.com.shopon.view.login.adapter.SubCategoriesAdapter;


public class SubCategoryActivity extends BaseActivity {

    private static final String TAG = SubCategoryActivity.class.getName();
    private RecyclerView subCategoryList;
    private ArrayList<String> subCategoryData = new ArrayList<>();
    private ArrayList<String> selectedCategoryData;
    private SubCategoriesAdapter subCategoryAdapter;
    private boolean isSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setupActionBar(toolbar);
        subCategoryData = getIntent().getStringArrayListExtra(Constants.SUB_CATEGORY_LIST);
        selectedCategoryData = getIntent().getStringArrayListExtra(Constants.SELECTED_CATEGORY_LIST);
        Log.d(TAG, "selectedCategoryData:" + selectedCategoryData);
        if (selectedCategoryData.containsAll(subCategoryData)) {
            isSelected = true;
        }
        setupCategoryList();
    }

    private void setupCategoryList() {
        subCategoryList = (RecyclerView) findViewById(R.id.sub_category_list);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        subCategoryList.setLayoutManager(mLinearLayoutManager);
        subCategoryAdapter = new SubCategoriesAdapter(this, subCategoryData);
        subCategoryAdapter.setSubscribedTags(selectedCategoryData);
        subCategoryList.setAdapter(subCategoryAdapter);

    }

    public void onDone(View v) {

        onActionDone();
    }

    private void onActionDone() {
        Intent intent = getIntent();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(Constants.SELECTED_CATEGORY_LIST, (ArrayList<String>) subCategoryAdapter.getSelecedTags());
        bundle.putStringArrayList(Constants.SUB_CATEGORY_LIST, (ArrayList) subCategoryData);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pick_category_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (subCategoryAdapter != null) {
            if (!isSelected) {
                showOption(menu, R.id.select_all);
                hideOption(menu, R.id.deselect_all);
            } else {
                showOption(menu, R.id.deselect_all);
                hideOption(menu, R.id.select_all);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.select_all:
                subCategoryAdapter.selectAllItems();
                subCategoryAdapter.notifyDataSetChanged();
                isSelected = true;
                invalidateOptionsMenu();
                return true;
            case R.id.deselect_all:
                subCategoryAdapter.deselectAllItems();
                subCategoryAdapter.notifyDataSetChanged();
                isSelected = false;
                invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
