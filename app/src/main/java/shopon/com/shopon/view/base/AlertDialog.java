package shopon.com.shopon.view.base;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;


public class AlertDialog extends DialogFragment {

    private String TAG = this.getClass().getCanonicalName();
    private static final String ARGS_MESSAGE = "message";
    private static final String ARGS_TITLE = "title";
    private static final String ARGS_VIEWTYPE = "viewtype";


    private DialogInterface.OnClickListener positiveButtonListener;
    private DialogInterface.OnClickListener negativeButtonListener;
    private String mPositiveButtonText;
    private String mNegativeButtonText;
    private int mContentViewId;
    private Intent mRequestIntent;


    private Dialog mDialog;


    public static AlertDialog newInstance(String title, String message) {
        AlertDialog alertDialog = new AlertDialog();
        Bundle args = new Bundle();
        args.putString(ARGS_TITLE, title);
        args.putString(ARGS_MESSAGE, message);
        alertDialog.setArguments(args);
        return alertDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString(ARGS_TITLE);
        String message = getArguments().getString(ARGS_MESSAGE);

        //Context context,int title,int content,String positiveButtonText,String negativeButtonText,
        mDialog = BaseDialogUtils.createDialog(getActivity(), title, message, positiveButtonListener,
                negativeButtonListener);

        return mDialog;
    }

    public void setPositiveButton(final DialogInterface.OnClickListener listener) {
        positiveButtonListener = listener;
    }

    public void setNegativeButton(final DialogInterface.OnClickListener listener) {
        negativeButtonListener = listener;
    }

    public void setActionButtonText(String pText, String nText) {
        mPositiveButtonText = pText;
        mNegativeButtonText = nText;
    }

    public void setContentView(int vId) {
        mContentViewId = vId;

    }

}