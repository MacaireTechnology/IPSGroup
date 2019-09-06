package uihelper;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateFormat {

    public static final String TAG = DateFormat.class.getSimpleName();

    public static String parseDateToday(String time) {
        String inputPattern = "H:mm";
        String outputPattern = "HH:mm";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern,Locale.getDefault());

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String parseDateToday(String time,String time1) {
        String inputPattern = "HH:mm:ss";
        String outputPattern = "HH:mm";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern,Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern,Locale.getDefault());

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String parseUpdateServer(String time) {
        String inputPattern = "HH:mm";
        String outputPattern = "HH:mm:ss";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern,Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern,Locale.getDefault());

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            str = "00:00:00";
        }

        Log.e(TAG,"strstrstrstrstr "+str.toString());
        return str;
    }
}
