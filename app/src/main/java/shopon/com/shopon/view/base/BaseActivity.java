package shopon.com.shopon.view.base;

import android.app.ActionBar;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;

import shopon.com.shopon.R;
import shopon.com.shopon.view.dialogs.ProgressDialog;


public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getName();
    protected ProgressDialog progress;
    //protected BazaarApplication app;
    protected ActionBar actionBar;
    protected android.support.v4.app.Fragment currentFragment;
    private Toolbar toolbar;
    private boolean defaultFontLoaded;

    public BaseActivity() {
        progress = ProgressDialog.newInstance(R.string.dlg_wait_please);
    }

    public synchronized void showProgress() {
        // Fragment transactions are committed asynchronously. Make sure last hide Operation is complete.
        getSupportFragmentManager().executePendingTransactions();

        if (!progress.isAdded()) {
            progress.show(getSupportFragmentManager(), null);
        }
    }

    public synchronized void showProgress(int text_id) {
        // Fragment transactions are committed asynchronously. Make sure last hide Operation is complete.
        getSupportFragmentManager().executePendingTransactions();
        progress = ProgressDialog.newInstance(text_id);

        if (!progress.isAdded()) {
            progress.show(getSupportFragmentManager(), null);
        }
    }

    public synchronized void setProgressMessage(final int msgId) {
        if (progress.isAdded()) {
            final android.app.ProgressDialog progDlg = (android.app.ProgressDialog) progress.getDialog();
            Runnable changeMessage = new Runnable() {
                @Override
                public void run() {
                    progDlg.setMessage(getString(msgId));
                }
            };
            runOnUiThread(changeMessage);
        }
    }

    public synchronized void hideProgress() {
        if (progress != null && progress.getActivity() != null) {
            // Revert to original Progress Message.
            final android.app.ProgressDialog progDlg = (android.app.ProgressDialog) progress.getDialog();
            if (progDlg != null) {
                Runnable changeMessage = new Runnable() {
                    @Override
                    public void run() {
                        progDlg.setMessage(getString(R.string.dlg_wait_please));
                    }
                };
                runOnUiThread(changeMessage);
            }
            progress.dismissAllowingStateLoss();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        //app = BazaarApplication.getInstance();
        actionBar = getActionBar();
    }


    public void setupActionBar(Toolbar toolbar) {

        this.toolbar = toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setBackgroundDrawable(getResources().getDrawable(R.color.colorPrimary));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    protected void navigateToParent() {
        Intent intent = NavUtils.getParentActivityIntent(this);
        if (intent == null) {
            finish();
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            NavUtils.navigateUpTo(this, intent);
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> T _findViewById(int viewId) {
        return (T) findViewById(viewId);
    }

    public void setCurrentFragment(android.support.v4.app.Fragment fragment, int container_id, String TAG) {
        currentFragment = fragment;
        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction transaction = buildTransaction();
        transaction.replace(container_id, fragment, TAG);
        transaction.commit();
    }

    public Fragment getCurrentFragment() {

        return currentFragment;
    }

    public void addFragmentToStack(android.support.v4.app.Fragment fragment, int container_id) {
        currentFragment = fragment;
        //getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        android.support.v4.app.FragmentTransaction transaction = buildTransaction();
        transaction.add(container_id, fragment, null).addToBackStack(fragment.getTag());
        transaction.commit();
    }

    public void popBackStack(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction trans = manager.beginTransaction();
        trans.remove(fragment);
        trans.commit();
        manager.popBackStack();
    }

    private FragmentTransaction buildTransaction() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        return transaction;
    }

    public void hideActionBarProgress() {
        setVisibilityActionBarProgress(false);
    }

    public void showActionBarProgress() {
        setVisibilityActionBarProgress(true);
    }

    public void setVisibilityActionBarProgress(boolean visibility) {
        setProgressBarIndeterminateVisibility(visibility);
    }

    public void setHomeButton() {
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void disableHomeButton() {
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    public void setCustomHomeButton(Drawable drawable_icon) {
        if (toolbar != null) {
            toolbar.setNavigationIcon(drawable_icon);
        }
    }

    public void hideOption(Menu menu, int id) {

        if (menu == null)
            return;
        MenuItem item = menu.findItem(id);
        if (item != null)
            item.setVisible(false);
        android.util.Log.d(TAG, "hideOption invoked");
    }

    public void showOption(Menu menu, int id) {

        if (menu == null)
            return;
        MenuItem item = menu.findItem(id);
        if (item != null)
            item.setVisible(true);
        android.util.Log.d(TAG, "showOption invoked");
    }


}