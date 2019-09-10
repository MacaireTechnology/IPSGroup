package com.mirrormind.ipsgroup.backgroundservice;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mirrormind.ipsgroup.alarm_sec.RemindersIntentManager;

import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimerTask;
import retrofit.ApiClient;
import retrofit.ApiInterface;
import retrofit.response.ApplyLeaveRes;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uihelper.picker.OnCurrentDay;
import uihelper.sharedPref.GlobalData;
import uihelper.sharedPref.SharedPreference;

public class CustomTimerTask extends TimerTask implements GlobalData , LocationListener,
        ResultCallback<Status>, OnMapReadyCallback {

    public static final String TAG = CustomTimerTask.class.getSimpleName();
    private Context mContext;
    private Handler mHandler = new Handler();
    private LocationManager locationManager;
    public double latitude = 0.0, logitude = 0.0;
    String address2="",split_city="";
    ApiInterface apiInterface;
    JSONObject allItems = new JSONObject();

    CustomTimerTask(Context con) {
        this.mContext = con;
    }
    @Override
    public void run() {
        new Thread(new Runnable() {
            public void run() {
                mHandler.post(new Runnable() {
                    public void run() {
//                        Toast.makeText(context, "Welcome", Toast.LENGTH_SHORT).show();
                        Log.e(TAG,"Welcome to background");
                        onCheckCondition();
                    }
                });
            }
        }).start();
    }

    private void onCheckCondition() {

        if (SharedPreference.getDefaults(mContext,TAG_LAST_PUNCH_TIME)!=null &&
                !SharedPreference.getDefaults(mContext,TAG_LAST_PUNCH_TIME).equals("") &&
                !SharedPreference.getDefaults(mContext,TAG_LAST_PUNCH_TIME).isEmpty()){
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                Date startDate = simpleDateFormat.parse(SharedPreference.getDefaults(mContext,TAG_LAST_PUNCH_TIME));
                Date endDate = simpleDateFormat.parse(OnCurrentDay.getTime());

                long difference = endDate.getTime() - startDate.getTime();
                if(difference<0) {
                    Date dateMax = simpleDateFormat.parse("24:00");
                    Date dateMin = simpleDateFormat.parse("00:00");
                    difference=(dateMax.getTime() -startDate.getTime() )+(endDate.getTime()-dateMin.getTime());
                }
                int days = (int) (difference / (1000*60*60*24));
                int hours = (int) ((difference - (1000*60*60*24*days)) / (1000*60*60));
                int min = (int) (difference - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);

                if (min>25 || hours>0) {
                    doApiCallLatLong();
                }else {
                    Log.e(TAG,"ELSE PART");
                }
                Log.e("log_tag","Hours: "+hours+", Mins: "+min);
            }catch (ParseException e){
                e.printStackTrace();
            }
        }else {
            doApiCallLatLong();
        }

    }

    private void doApiCallLatLong() {
        try {
            locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    5000, 500, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        try {
            allItems.put("AttId",-1);
            allItems.put("Address",SharedPreference.getDefaults(mContext,TAG_CURR_ADDRESS));
            allItems.put("EmpID",SharedPreference.getDefaults(mContext,TAG_EMP_ID));
            allItems.put("Lat",SharedPreference.getDefaults(mContext,TAG_CURR_LAT));
            allItems.put("Lng",SharedPreference.getDefaults(mContext,TAG_CURR_LONG));

            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = (JsonObject) jsonParser.parse(String.valueOf(allItems));
            Log.e(TAG,"all Items "+allItems);
            Call<ApplyLeaveRes> doSaveGPSAddr = apiInterface.doSaveGPSAddr(jsonObject);
            doSaveGPSAddr.enqueue(new Callback<ApplyLeaveRes>() {
                @Override
                public void onResponse(@NonNull Call<ApplyLeaveRes> call, @NonNull Response<ApplyLeaveRes> response) {
                    if (response.code() == 200){
                        assert response.body() != null;
                        if (response.body().getStatusCode().equals("00")){
                            Intent serviceIntent = new Intent(mContext, NotificationService.class);
                            serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android");
                            ContextCompat.startForegroundService(mContext, serviceIntent);
                            SharedPreference.setDefaults(mContext,TAG_LAST_PUNCH_TIME,OnCurrentDay.getTime());
                            Log.e(TAG,"Register GPS Address");
                            setUpNotification();
                        }else {
                            setUpNotification();
                            Log.e(TAG,"Not Register GPS Address");
                        }
                    }else {
                        setUpNotification();
                        Log.e(TAG,"Server Error");
                    }

                }
                @Override
                public void onFailure(@NonNull Call<ApplyLeaveRes> call,@NonNull Throwable t) {
                    Log.e(TAG,"Internet or Server Error");
                    setUpNotification();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            setUpNotification();
        }catch (NumberFormatException | NullPointerException e){
            e.printStackTrace();
            setUpNotification();
        }catch (Exception e){
            e.printStackTrace();
            setUpNotification();
        }
    }
    void setUpNotification() {
        long time = new GregorianCalendar().getTimeInMillis() + 60 * 1000;
        RemindersIntentManager remindersIntentManager = RemindersIntentManager.getInstance(mContext);

        String toParse ="31-03-2017 16:44"; // set any date
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm",Locale.getDefault()); // I assume d-M, you may refer to M-d for month-day instead.
        Date date = null;  // You will need try/catch around this
        try {
            date = formatter.parse(toParse);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long timeFromDay = date.getTime();

        final AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
//         alarmManager.cancel(PendingIntent.getBroadcast(this, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
        assert alarmManager != null;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, AlarmManager.INTERVAL_HOUR,
                remindersIntentManager.getChristmasIntent());
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeFromDay, remindersIntentManager.getDotaIntent());
    }
    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        logitude = location.getLongitude();

        Log.e(TAG,"locationView "+"Latitude: " + location.getLatitude()
                + "\n Longitude: " + location.getLongitude());
        try {
            SharedPreference.setDefaults(mContext,TAG_CURR_LAT,String.valueOf(latitude));
            SharedPreference.setDefaults(mContext,TAG_CURR_LONG,String.valueOf(logitude));
        }catch (NumberFormatException | NullPointerException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            address2=addresses.get(0).getAddressLine(0);
            split_city=addresses.get(0).getSubAdminArea();

            try {
                SharedPreference.setDefaults(mContext,TAG_CURR_ADDRESS,address2);
                SharedPreference.setDefaults(mContext,TAG_CURR_CITY,split_city);
            }catch (NumberFormatException | NullPointerException e){
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }

            Log.e(TAG,"address2address2address2 "+address2);
            Log.e(TAG,"split_citysplit_city "+split_city);
        }catch(Exception e) {
            Log.e(TAG,"error "+e.toString());
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onResult(@NonNull Status status) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}