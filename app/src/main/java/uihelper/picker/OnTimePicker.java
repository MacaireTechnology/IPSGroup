package uihelper.picker;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Typeface;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.mirrormind.ipsgroup.R;
import com.mirrormind.ipsgroup.teamPerformance.DailySales;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static uihelper.DateFormat.parseDateToday;

public class OnTimePicker {

    // On Time Picker
    public OnTimePicker(Activity activity,final TextView textView) {
        Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(activity,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        textView.setText(parseDateToday(hourOfDay + ":" + minute));
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }


}
