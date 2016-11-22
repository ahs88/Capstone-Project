package shopon.com.shopon.view.base;

import android.app.Activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import shopon.com.shopon.R;
import shopon.com.shopon.view.base.AlertDialog;
import shopon.com.shopon.view.base.ProgressDialog;

public abstract class BaseFragment extends android.support.v4.app.Fragment {

    protected static final String ARG_TITLE = "title";
    public static final String TAG = BaseFragment.class.getName();


    protected Activity baseActivity;
    protected String title;
    boolean defaultFontLoaded;
    protected AlertDialog errorDlg;
    protected ProgressDialog progress;
    protected DialogInterface.OnClickListener positiveButtonOnClickListener;

    public String getTitle() {
        return title;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseActivity = (Activity) getActivity();

        if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE);
        }
        progress = ProgressDialog.newInstance(R.string.dlg_wait_please);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(title);
    }

    public Activity getBaseActivity() {
        return (Activity) getActivity();
    }
    public abstract void initializeData();
    protected boolean isExistActivity() {
        return ((!isDetached()) && (getBaseActivity() != null));
    }



    public synchronized void showProgress() {
        // Fragment transactions are committed asynchronously. Make sure last hide Operation is complete.
        getChildFragmentManager().executePendingTransactions();

        if (!progress.isAdded()) {
            progress.show(getFragmentManager(), null);
        }
    }

    public synchronized void showProgress(int text_id) {
        // Fragment transactions are committed asynchronously. Make sure last hide Operation is complete.
        getFragmentManager().executePendingTransactions();
        progress = ProgressDialog.newInstance(text_id);

        if (!progress.isAdded()) {
            progress.show(getFragmentManager(), null);
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
            getActivity().runOnUiThread(changeMessage);
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
                getActivity().runOnUiThread(changeMessage);
            }
            progress.dismissAllowingStateLoss();
        }
    }

    public void hideOption(Menu menu, int id) {

        if(menu == null)
            return;
        MenuItem item = menu.findItem(id);
        if (item != null)
            item.setVisible(false);
        android.util.Log.d(TAG, "hideOption invoked");
    }

    public void showOption(Menu menu,int id) {

        if(menu == null)
            return;
        MenuItem item = menu.findItem(id);
        if (item != null)
            item.setVisible(true);
        android.util.Log.d(TAG, "showOption invoked");
    }

}

