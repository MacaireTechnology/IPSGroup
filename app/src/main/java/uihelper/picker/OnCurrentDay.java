package uihelper.picker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OnCurrentDay {

    public static String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
    public static String getTime() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
