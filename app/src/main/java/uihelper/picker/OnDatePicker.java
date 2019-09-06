package uihelper.picker;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.graphics.Typeface;
import android.widget.DatePicker;
import android.widget.TextView;

import com.mirrormind.ipsgroup.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class OnDatePicker {

    private Calendar myCalendar = Calendar.getInstance();

    // On Date Picker
    public OnDatePicker(final Activity activity, final TextView textView) {
        Calendar mCurrentDate = Calendar.getInstance();
        int mYear = mCurrentDate.get(Calendar.YEAR);
        int mMonth = mCurrentDate.get(Calendar.MONTH);
        int mDay = mCurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker = new DatePickerDialog(activity,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedYear,
                                          int selectedMonth, int selectedDay) {
                        myCalendar.set(Calendar.YEAR, selectedYear);
                        myCalendar.set(Calendar.MONTH, selectedMonth);
                        myCalendar.set(Calendar.DAY_OF_MONTH, selectedDay);
                        updateLabel(activity,textView);
                    }
                }, mYear, mMonth, mDay);
        mDatePicker.setTitle("Select date");

        mDatePicker.show();
    }
    private void updateLabel(Activity act,TextView textView) {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        textView.setText(sdf.format(myCalendar.getTime()));
        textView.setTypeface(textView.getTypeface(), Typeface.NORMAL);
        textView.setTextColor(act.getResources().getColor(R.color.six_e));
    }
}
