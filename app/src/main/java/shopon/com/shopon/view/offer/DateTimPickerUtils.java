package shopon.com.shopon.view.offer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import shopon.com.shopon.R;

/**
 * Created by Akshath on 29-07-2016.
 */

public class DateTimPickerUtils {

    ScheduledDateInterface mInterface;
    Context mContext;

    public DateTimPickerUtils(Context ctx, ScheduledDateInterface scheduled_date_interface) {
        mContext = ctx;
        mInterface = scheduled_date_interface;
    }

    private static final String TAG = DateTimPickerUtils.class.getName();
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mhour;
    private int mMin;

    public AlertDialog schedule() {
        if (mContext == null) {
            Log.d(TAG, "reintialize context");
            return null;
        }
        final View dialogView = View.inflate(mContext, R.layout.share_datetime_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
        final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
        final TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);

        final Calendar c = Calendar.getInstance();
        alertDialog.setView(dialogView);
        final long currentTime = c.getTimeInMillis() - 1000;
        datePicker.setMinDate(currentTime);

        dialogView.findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                        datePicker.getMonth(),
                        datePicker.getDayOfMonth(),
                        timePicker.getCurrentHour(),
                        timePicker.getCurrentMinute());

                long time = calendar.getTimeInMillis();


                mYear = datePicker.getYear();
                mMonth = datePicker.getMonth();
                mDay = datePicker.getDayOfMonth();
                mhour = timePicker.getCurrentHour();
                mMin = timePicker.getCurrentMinute();

                final long currentTime = c.getTimeInMillis() - 1000;
                if (calendar.getTimeInMillis() < currentTime) {
                    Toast.makeText(mContext, mContext.getString(R.string.time_invalid_message), Toast.LENGTH_LONG).show();
                    return;
                }
                alertDialog.dismiss();

                GregorianCalendar c = new GregorianCalendar(mYear, mMonth, mDay, mhour, mMin);
                mInterface.scheduledDate(scheduleDate(), c);
            }
        });

        dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (datePicker.getVisibility() == View.VISIBLE) {
                    timePicker.setVisibility(View.VISIBLE);
                    datePicker.setVisibility(View.GONE);
                    ((Button) view).setText(mContext.getString(R.string.set_date));

                } else {
                    timePicker.setVisibility(View.GONE);
                    datePicker.setVisibility(View.VISIBLE);
                    ((Button) view).setText(mContext.getString(R.string.set_time));
                }
            }
        });


        alertDialog.setView(dialogView);


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alertDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        alertDialog.show();
        alertDialog.getWindow().setLayout(lp.width, lp.height);
        alertDialog.show();
        return alertDialog;
    }

    private String scheduleDate() {

        GregorianCalendar c = new GregorianCalendar(mYear, mMonth, mDay, mhour, mMin);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM, yyyy");
        String transDateString = sdf.format(c.getTime());
        Log.i(TAG, "Scheduled Date String  :" + transDateString);

        SimpleDateFormat sdfTime = new SimpleDateFormat("h:mm a");
        String transTimeString = sdfTime.format(c.getTime());
        Log.i(TAG, "Scheduled Time String  :" + transTimeString);
        String finalDate = transTimeString + " " + transDateString;
        Log.i(TAG, "Scheduled String  :" + finalDate);
        SimpleDateFormat sdfWeekDay = new SimpleDateFormat("EE");
        String dayOfTheWeek = sdfWeekDay.format(c.getTime());
        String weekTitle = mDay + ":" + dayOfTheWeek;
        Log.i(TAG, "Scheduled weekday String  :" + weekTitle);
        return finalDate;
    }


    private void releaseContext() {
        mContext = null;
    }


    public interface ScheduledDateInterface {
        public void scheduledDate(String scheduledDate, Calendar calendar);

    }
}
