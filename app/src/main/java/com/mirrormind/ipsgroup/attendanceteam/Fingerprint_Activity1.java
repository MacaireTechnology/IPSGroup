package com.mirrormind.ipsgroup.attendanceteam;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mirrormind.ipsgroup.Dialog.DialogsUtils;
import com.mirrormind.ipsgroup.OnDBSync;
import com.mirrormind.ipsgroup.R;
import com.mirrormind.ipsgroup.camera.RunTimePermission;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import db.ClockInOutTime;
import db.DatabaseHelper;
import retrofit.ApiClient;
import retrofit.ApiInterface;
import retrofit.response.ClockinRes;
import retrofit.response.ImageRes;
import retrofit.response.ReturnValue;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uihelper.SnackbarIps;
import uihelper.icomoon.Icomoon;
import uihelper.network.UtilService;
import uihelper.sharedPref.GlobalData;
import uihelper.sharedPref.SharedPreference;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.os.Build.VERSION_CODES.M;

public class Fingerprint_Activity1 extends Activity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        GlobalData , LocationListener,
        ResultCallback<Status>, OnMapReadyCallback {

    public static final String TAG = Fingerprint_Activity1.class.getSimpleName();
    TextView tv_fingerprint_icon, tv_back,
            tdate, tv_sec, tv_ampm, tv_day,
            tv_punchin, tv_no_data_found;
    RecyclerView rv_list_clockinout;
    SimpleDateFormat sd_hm, sd_sec, sd_ap, sd_day;
    View bar;
    Animation animation;
    ApiInterface apiInterface;
    // Camera
    private RunTimePermission runTimePermission;
    protected GoogleApiClient mGoogleApiClient;
    ProgressDialog myDialog;
    Activity mActivity;
    ImageView iv_bg;
    JSONObject allItems = new JSONObject();
    InputStream imageStream;
    public double latitude = 0.0, logitude = 0.0;
    String address2="",split_city="";
    private GoogleMap googleMap;
    private LocationManager locationManager;
    private static final long MIN_TIME = 1000;
    private static final float MIN_DISTANCE = 1000;
    // Database
    DatabaseHelper databaseHelper;
    ClockInOutTime clockInOutTime = new ClockInOutTime();
    String FA_EMP_ID,FA_CLOCK_TYPE,FA_CLOCK_IN_ID,FA_IMAGE_URI,FA_DATE_TIME,FA_LATITUDE,
            FA_LONGITUDE,FA_CURR_LOC;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint);

        databaseHelper = new DatabaseHelper(this);
        clockInOutTime = new ClockInOutTime();

        mActivity = this;
        initID();
        initListener();
        updatedTime();
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        rv_list_clockinout = findViewById(R.id.rv_list_clockinout);
        rv_list_clockinout.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                false));
        rv_list_clockinout.setNestedScrollingEnabled(false);
        // shared preference
        try {
            if (SharedPreference.getDefaults(mActivity, GlobalData.TAG_IN_OUT_STATUS).equalsIgnoreCase("first")) {
                tv_fingerprint_icon.setTextColor(getResources().getColor(R.color.green));
                tv_punchin.setTextColor(getResources().getColor(R.color.green));
                tv_punchin.setText("Tap Here to PunchIn");
            } else if (SharedPreference.getDefaults(mActivity, GlobalData.TAG_IN_OUT_STATUS).equalsIgnoreCase("second")) {
                tv_fingerprint_icon.setTextColor(getResources().getColor(R.color.red));
                tv_punchin.setTextColor(getResources().getColor(R.color.red));
                tv_punchin.setText("Tap Here to PunchOut");
            }
        } catch (NullPointerException | NumberFormatException e) {
            SharedPreference.setDefaults(mActivity, GlobalData.TAG_IN_OUT_STATUS, "first");
        } catch (Exception e) {
            SharedPreference.setDefaults(mActivity, GlobalData.TAG_IN_OUT_STATUS, "first");
        }

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();

        if (Build.VERSION.SDK_INT >= 23) {
            ArrayList<String> listPermissionsNeeded = new ArrayList<String>();
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                listPermissionsNeeded.add(Manifest.permission.CAMERA);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (!listPermissionsNeeded.isEmpty())
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
        }
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    500, 5, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        rv_list_clockinout.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL,
                false));

        new ClockedIn_details(this, apiInterface, rv_list_clockinout).execute();
        myDialog.setCanceledOnTouchOutside(true);
        tv_fingerprint_icon.setEnabled(true);

        Log.e(TAG,"STATUS "+SharedPreference.getDefaults(mActivity, GlobalData.TAG_IN_OUT_STATUS));
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void updatedTime() {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                long date = System.currentTimeMillis();

                                sd_hm = new SimpleDateFormat("hh:mm", Locale.getDefault());
                                sd_sec = new SimpleDateFormat("ss", Locale.getDefault());
                                sd_ap = new SimpleDateFormat("a", Locale.getDefault());
                                sd_day = new SimpleDateFormat("EEE", Locale.getDefault());

                                String str_hm = sd_hm.format(date);
                                String str_sec = sd_sec.format(date);
                                String str_ap = sd_ap.format(date);
                                String str_day = sd_day.format(date);

                                tdate.setText(str_hm);
                                tv_sec.setText(str_sec);
                                tv_ampm.setText(str_ap);
                                tv_day.setText(str_day);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    Log.e(TAG,"InterruptExecption "+e.getMessage());
                }
            }
        };
        t.start();
    }

    private void initID() {

        tv_fingerprint_icon = findViewById(R.id.tv_fingerprint_icon);
        tv_back = findViewById(R.id.tv_back);
        tv_punchin = findViewById(R.id.tv_punchin);
        tv_no_data_found = findViewById(R.id.tv_no_data_found);
        rv_list_clockinout = findViewById(R.id.rv_list_clockinout);

        tdate = findViewById(R.id.tv_time);
        tv_sec = findViewById(R.id.tv_sec);
        tv_ampm = findViewById(R.id.tv_ampm);
        tv_day = findViewById(R.id.tv_day);
        iv_bg = findViewById(R.id.iv_bg);

        bar = findViewById(R.id.bar);

        animation = AnimationUtils.loadAnimation(Fingerprint_Activity1.this, R.anim.scanner);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                bar.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        Log.e(TAG,"databaseHelper.getClockInOut().size() "+databaseHelper.getClockInOut().size());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListener() {
        tv_fingerprint_icon.setOnClickListener(this);
        tv_back.setOnClickListener(this);
        tv_no_data_found.setOnClickListener(this);

        Icomoon.imageLogo.apply(this, tv_fingerprint_icon);
        Icomoon.imageLogo.apply(this, tv_back);

        /*switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        bar.setVisibility(View.VISIBLE);
                        bar.startAnimation(animation);
                        startanimate();
                        tv_fingerprint_icon.setEnabled(false);
                        new SnackbarIps(tv_fingerprint_icon, "Verifying Your Credentials");

                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                        endanimate();
                        return true; // if you want to handle the touch event
                }*/
        tv_fingerprint_icon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (new UtilService().isNetworkAvailable(mActivity)) {
                            if (databaseHelper.getClockInOut().size()>0) {
                                new SnackbarIps(tv_fingerprint_icon,"Synchronizing");
                                new OnDBSync(mActivity,2).execute();
                            }else {
                                bar.setVisibility(View.VISIBLE);
                                bar.startAnimation(animation);
                                startAnimate();
                                tv_fingerprint_icon.setEnabled(false);
                                new SnackbarIps(tv_fingerprint_icon, "Verifying Your Credentials");
                            }
                        }else {
                            // PRESSED
                            bar.setVisibility(View.VISIBLE);
                            bar.startAnimation(animation);
                            startAnimate();
                            tv_fingerprint_icon.setEnabled(false);
                            new SnackbarIps(tv_fingerprint_icon, "Verifying Your Credentials");
                        }

                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                        endAnimate();
                        return true; // if you want to handle the touch event
                }


                return false;
            }
        });

        /*switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (new UtilService().isNetworkAvailable(mActivity)) {
                            if (databaseHelper.getClockInOut().size()>0) {
                                new SnackbarIps(tv_fingerprint_icon,"Synchronizing");
                                new OnDBSync(mActivity,2).execute();
                            }else {
                                bar.setVisibility(View.VISIBLE);
                                bar.startAnimation(animation);
                                startAnimate();
                                tv_fingerprint_icon.setEnabled(false);
                                new SnackbarIps(tv_fingerprint_icon, "Verifying Your Credentials");
                            }
                        }else {
                            // PRESSED
                            bar.setVisibility(View.VISIBLE);
                            bar.startAnimation(animation);
                            startAnimate();
                            tv_fingerprint_icon.setEnabled(false);
                            new SnackbarIps(tv_fingerprint_icon, "Verifying Your Credentials");
                        }

                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                        endAnimate();
                        return true; // if you want to handle the touch event
                }*/
    }

    private void startAnimate() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                bar.clearAnimation();
                bar.setVisibility(View.GONE);
                runTimePermission = new RunTimePermission(Fingerprint_Activity1.this);
                runTimePermission.requestPermission(new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, new RunTimePermission.RunTimePermissionListener() {

                    @Override
                    public void permissionGranted() {
                        PickFromCamera();
                    }
                    @Override
                    public void permissionDenied() {
                        finish();
                    }
                });
            }
        }, 2000);
    }

    private void endAnimate() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                bar.clearAnimation();
                bar.setVisibility(View.GONE);
            }
        }, 2000);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                onBackPressed();
                break;
        }
    }

    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(0);
        locationRequest.setFastestInterval(0);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                .checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(Fingerprint_Activity1.this, 110);
                        } catch (IntentSender.SendIntentException e) {

                        }
                        break;
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (runTimePermission != null) {
            runTimePermission.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(mActivity,
                    "You are not access this App without Storage permission",
                    Toast.LENGTH_LONG).show();
            finish();
        } else if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(mActivity,
                    "You are not access this App without Camera permission",
                    Toast.LENGTH_LONG).show();
            finish();
        } else if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(mActivity,
                    "You are not access this App without Location permission",
                    Toast.LENGTH_LONG).show();
            finish();
        } else if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(mActivity,
                    "You are not access this App without Location permission",
                    Toast.LENGTH_LONG).show();
            finish();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        logitude = location.getLongitude();

        Log.e(TAG,"locationView "+"Latitude: " + location.getLatitude()
                + "\n Longitude: " + location.getLongitude());
        try {
            SharedPreference.setDefaults(mActivity,TAG_CURR_LAT,String.valueOf(latitude));
            SharedPreference.setDefaults(mActivity,TAG_CURR_LONG,String.valueOf(latitude));
        }catch (NumberFormatException | NullPointerException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            Geocoder geocoder = new Geocoder(mActivity, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            address2=addresses.get(0).getAddressLine(0);
            split_city=addresses.get(0).getSubAdminArea();

            try {
                SharedPreference.setDefaults(mActivity,TAG_CURR_ADDRESS,address2);
                SharedPreference.setDefaults(mActivity,TAG_CURR_CITY,split_city);
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
    public void onMapReady(GoogleMap googleMap1) {
        googleMap = googleMap1;

        if (ActivityCompat.checkSelfPermission(mActivity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mActivity,
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.setPadding(0, 0, 0, 0);

        googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                googleMap.clear();
                latitude = location.getLatitude();
                logitude = location.getLongitude();
                MarkerOptions mp = new MarkerOptions();
                mp.position(new LatLng(latitude, logitude));
                mp.title("your position");
                googleMap.addMarker(mp);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(), location.getLongitude()), 17));
                try {
                    SharedPreference.setDefaults(mActivity,TAG_CURR_LAT,String.valueOf(latitude));
                    SharedPreference.setDefaults(mActivity,TAG_CURR_LONG,String.valueOf(logitude));
                }catch (NullPointerException | NumberFormatException e){
                    e.printStackTrace();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
    }

    @SuppressLint("StaticFieldLeak")
    private class ClockedIn_details extends AsyncTask<String, String, String> {

        private Activity mActivity;
        private ApiInterface apiInterface;
        private RecyclerView rv_list_clockinout;

        private ClockedIn_details(Activity activity, ApiInterface apiInterface, RecyclerView rv_list_clockinout) {
            this.mActivity = activity;
            this.apiInterface = apiInterface;
            this.rv_list_clockinout = rv_list_clockinout;

            myDialog = DialogsUtils.showProgressDialog(Fingerprint_Activity1.this, "Fetching Clock IN OUT TIME");
            myDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
                String currentDate = df.format(c);

                Call<ClockinRes> dogetattdetails = apiInterface.dogetattdetails(
                        SharedPreference.getDefaults(mActivity,TAG_EMP_ID), currentDate);

                dogetattdetails.enqueue(new Callback<ClockinRes>() {
                    @Override
                    public void onResponse(@NonNull Call<ClockinRes> call,@NonNull Response<ClockinRes> response) {
                        if (response.code() == 200) {
                            assert response.body() != null;
                            if (response.body().getStatusCode().equals("00")) {
                                myDialog.dismiss();
                                if (response.body().getReturnValue().size() > 0) {
                                    tv_no_data_found.setVisibility(View.GONE);
                                    rv_list_clockinout.setAdapter(new ClockedinAdapter(
                                            mActivity, response.body().getReturnValue()));
                                }else {
                                    tv_no_data_found.setVisibility(View.VISIBLE);
                                }
                            } else {
                                myDialog.dismiss();
                                new SnackbarIps(tv_fingerprint_icon, "" + response.body().getStatusDescription(), 2000);
                            }
                        } else {
                            myDialog.dismiss();
                            new SnackbarIps(tv_fingerprint_icon, "Server Error", 2000);
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<ClockinRes> call,@NonNull Throwable t) {
                        myDialog.dismiss();
                        new SnackbarIps(tv_fingerprint_icon, "Network or Server Error", 2000);
                    }
                });
            } catch (Exception e) {
                myDialog.dismiss();
                e.printStackTrace();
            }
            return null;
        }

        private class ClockedinAdapter extends RecyclerView.Adapter<ClockedinAdapter.MyViewHolder> {

            Activity mActivity;
            List<ReturnValue> clockenin_deatils;

            private ClockedinAdapter(Activity mActivity, List<ReturnValue> clockenin_deatils) {
                this.mActivity = mActivity;
                this.clockenin_deatils = clockenin_deatils;
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(mActivity).inflate(R.layout.adpt_clockedin,
                        parent, false);
                return new MyViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

                if (clockenin_deatils.get(position).getClockInDT()!= null &&
                        !clockenin_deatils.get(position).getClockInDT().equals("") &&
                        !clockenin_deatils.get(position).getClockInDT().isEmpty()) {
                    holder.tv_time_in.setText(clockenin_deatils.get(position).getClockInDT());
                } else {
                    holder.tv_time_in.setText("-");
                }
                if (clockenin_deatils.get(position).getClockOutDT()!= null &&
                        !clockenin_deatils.get(position).getClockOutDT().equals("") &&
                        !clockenin_deatils.get(position).getClockOutDT().isEmpty()) {
                    holder.tv_time_out.setText(clockenin_deatils.get(position).getClockOutDT());
                } else {
                    holder.tv_time_out.setText("-");
                }
                if (clockenin_deatils.get(position).getClockInLocaction()!= null &&
                        !clockenin_deatils.get(position).getClockInLocaction().equals("") &&
                        !clockenin_deatils.get(position).getClockInLocaction().isEmpty()) {
                    holder.tv_clock_in_loc.setText(clockenin_deatils.get(position).getClockInLocaction());
                } else {
                    holder.tv_clock_in_loc.setText("-");
                }
                if (clockenin_deatils.get(position).getClockOutLocation()!= null &&
                        !clockenin_deatils.get(position).getClockOutLocation().equals("") &&
                        !clockenin_deatils.get(position).getClockOutLocation().isEmpty()) {
                    holder.tv_clock_out_loc.setText(clockenin_deatils.get(position).getClockOutLocation());
                } else {
                    holder.tv_clock_out_loc.setText("-");
                }

                holder.tv_adjust_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(mActivity,AdjustTime.class)
                                .putExtra(TAG_ATT_ID,clockenin_deatils.get(holder.getAdapterPosition()).getAttID())
                                .putExtra(TAG_REC_ID,clockenin_deatils.get(holder.getAdapterPosition()).getClockInDateTime())
                        );
                    }
                });
            }

            @Override
            public int getItemCount() {
                return clockenin_deatils.size();
            }

            private class MyViewHolder extends RecyclerView.ViewHolder {

                TextView tv_time_in_icon,tv_time_in,tv_time_out_icon,tv_time_out,tv_time_in_loc,
                        tv_clock_in_loc_icon,tv_clock_in_loc,tv_time_out_loc,tv_clock_out_loc_icon,
                        tv_clock_out_loc,tv_adjust_icon;

                private MyViewHolder(View itemView) {
                    super(itemView);

                    tv_time_in_icon = itemView.findViewById(R.id.tv_time_in_icon);
                    tv_time_in = itemView.findViewById(R.id.tv_time_in);
                    tv_time_out_icon = itemView.findViewById(R.id.tv_time_out_icon);
                    tv_time_out = itemView.findViewById(R.id.tv_time_out);
                    tv_time_in_loc = itemView.findViewById(R.id.tv_time_in_loc);
                    tv_clock_in_loc_icon = itemView.findViewById(R.id.tv_clock_in_loc_icon);
                    tv_clock_in_loc = itemView.findViewById(R.id.tv_clock_in_loc);
                    tv_time_out_loc = itemView.findViewById(R.id.tv_time_out_loc);
                    tv_clock_out_loc_icon = itemView.findViewById(R.id.tv_clock_out_loc_icon);
                    tv_clock_out_loc = itemView.findViewById(R.id.tv_clock_out_loc);
                    tv_adjust_icon = itemView.findViewById(R.id.tv_adjust_icon);

                    Icomoon.imageLogo.apply(mActivity,tv_time_in_icon);
                    Icomoon.imageLogo.apply(mActivity,tv_time_out_icon);
                    Icomoon.imageLogo.apply(mActivity,tv_clock_in_loc_icon);
                    Icomoon.imageLogo.apply(mActivity,tv_clock_out_loc_icon);
                    Icomoon.imageLogo.apply(mActivity,tv_time_out_loc);
                    Icomoon.imageLogo.apply(mActivity,tv_time_in_loc);
                    Icomoon.imageLogo.apply(mActivity,tv_adjust_icon);
                }
            }
        }
    }

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissions = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 107;
    String pictureFilePath = "";

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss",Locale.getDefault()).format(new Date());
        String pictureFile = "IPS_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(pictureFile, ".jpg", storageDir);
        pictureFilePath = image.getAbsolutePath();
        return image;
    }
    private void sendTakePictureIntent() {

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION, true);
        cameraIntent.addFlags(FLAG_GRANT_READ_URI_PERMISSION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            cameraIntent.addFlags(FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        }
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {

            File pictureFile = null;
            try {
                pictureFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this,
                        "Photo file can't be created, please try again",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (pictureFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.mirrormind.ipsgroup.fileprovider",
                        pictureFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, 200);
            }
        }
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();
        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }
        return result;
    }
    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }
    public void PickFromCamera() {

        permissions.add(CAMERA);
        permissions.add(READ_EXTERNAL_STORAGE);
        permissions.add(WRITE_EXTERNAL_STORAGE);
        permissionsToRequest = findUnAskedPermissions(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                Log.e(TAG, "higher");
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            } else {
                if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    sendTakePictureIntent();
                    Log.e(TAG, "higher");
                }
            }
        } else {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, 201);
            Log.e(TAG, "lower");
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode){
                case 200:
                    Log.e(TAG, "Higher Version --> ");
                    File imgFile = new File(pictureFilePath);
                    String filePath = imgFile.getPath();
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                    Uri uridata = getImageUri(this,bitmap);
                    new doCallPunchINOUT(uridata).execute();
                    break;
                case 201:
                    Log.e(TAG, "Lower Version -->");
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    Uri uridata1 = getImageUri(this,photo);
                    new doCallPunchINOUT(uridata1).execute();
                    break;
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class doCallPunchINOUT extends AsyncTask<String, String, String> {

        Uri imageUri;
        private doCallPunchINOUT(Uri uriData) {
            this.imageUri = uriData;
            myDialog = DialogsUtils.showProgressDialog(mActivity, "Update Clock IN OUT TIME");
            myDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                try {
                    imageStream = getContentResolver().openInputStream(imageUri);
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    String encodedImage = encodeImage(selectedImage);
                    byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                    String imgString = Base64.encodeToString(decodedString, Base64.DEFAULT);
                    try {
                        try {

                            allItems.put("Empid", SharedPreference.getDefaults(mActivity, GlobalData.TAG_EMP_ID));
                            if (SharedPreference.getDefaults(mActivity, GlobalData.TAG_IN_OUT_STATUS) != null &&
                                    SharedPreference.getDefaults(mActivity, GlobalData.TAG_IN_OUT_STATUS).equals("first")) {
                                FA_CLOCK_TYPE = "ClockIn";
                                FA_CLOCK_IN_ID = "-1";
                                allItems.put("ClockType", "ClockIn");
                                allItems.put("ClockInID", -1);
                            } else if (SharedPreference.getDefaults(mActivity, GlobalData.TAG_IN_OUT_STATUS) != null &&
                                    SharedPreference.getDefaults(mActivity, GlobalData.TAG_IN_OUT_STATUS).equals("second")) {
                                allItems.put("ClockType", "ClockOut");
                                FA_CLOCK_TYPE = "ClockOut";

                                if (SharedPreference.getDefaults(mActivity, GlobalData.TAG_CLOCK_OUT_ID) != null &&
                                        !SharedPreference.getDefaults(mActivity, GlobalData.TAG_CLOCK_OUT_ID).equals("-1")) {
                                    allItems.put("ClockInID", SharedPreference.getDefaults(mActivity, GlobalData.TAG_CLOCK_OUT_ID));
                                    FA_CLOCK_IN_ID = SharedPreference.getDefaults(mActivity, GlobalData.TAG_CLOCK_OUT_ID);
                                } else {
                                    FA_CLOCK_IN_ID = "-1";
                                    allItems.put("ClockInID", "-1");
                                }
                            } else {
                                FA_CLOCK_TYPE = "ClockIn";
                                FA_CLOCK_IN_ID = "-1";
                                allItems.put("ClockInID", "-1");
                                allItems.put("ClockType", "ClockIn");
                            }
                        } catch (NumberFormatException | NullPointerException e) {
                            allItems.put("ClockType", "ClockIn");
                            allItems.put("ClockInID", -1);
                            FA_CLOCK_TYPE = "ClockIn";
                            FA_CLOCK_IN_ID = "-1";
                        } catch (Exception e) {
                            allItems.put("ClockType", "ClockIn");
                            allItems.put("ClockInID", -1);
                            FA_CLOCK_TYPE = "ClockIn";
                            FA_CLOCK_IN_ID = "-1";
                        }
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                        allItems.put("img", "data:image/jpeg;base64,"+imgString);
                        allItems.put("DateTime", timeStamp);
                        allItems.put("Lat", SharedPreference.getDefaults(mActivity, GlobalData.TAG_CURR_LAT));
                        allItems.put("Long",  SharedPreference.getDefaults(mActivity, GlobalData.TAG_CURR_LONG));
                        allItems.put("CurrentLocation",  SharedPreference.getDefaults(mActivity, GlobalData.TAG_CURR_ADDRESS));

                        FA_EMP_ID = SharedPreference.getDefaults(mActivity, GlobalData.TAG_EMP_ID);
                        FA_IMAGE_URI = imageUri.toString();
                        FA_DATE_TIME = timeStamp;
                        FA_LATITUDE = SharedPreference.getDefaults(mActivity, GlobalData.TAG_CURR_LAT);
                        FA_LONGITUDE = SharedPreference.getDefaults(mActivity, GlobalData.TAG_CURR_LONG);
                        FA_CURR_LOC = SharedPreference.getDefaults(mActivity, GlobalData.TAG_CURR_ADDRESS);

                        JsonParser jsonParser = new JsonParser();
                        JsonObject jsonObject = (JsonObject) jsonParser.parse(String.valueOf(allItems));
                        apiInterface = ApiClient.getClient().create(ApiInterface.class);
                        Call<ImageRes> imgResCall = apiInterface.douploadimg(jsonObject);
                        imgResCall.enqueue(new Callback<ImageRes>() {
                            @Override
                            public void onResponse(@NonNull Call<ImageRes> call, @NonNull Response<ImageRes> response) {
                                if (myDialog!=null && myDialog.isShowing()){
                                    myDialog.dismiss();
                                }
                                if (response.code() == 200) {
                                    assert response.body() != null;
                                    if (response.body().getStatusCode().equals("00")) {
                                        try {
                                            if (response.body().getReturnValue()!=null) {
                                                SharedPreference.setDefaults(mActivity,TAG_CLOCK_OUT_ID,
                                                        response.body().getReturnValue());
                                            }else {
                                                SharedPreference.setDefaults(mActivity,TAG_CLOCK_OUT_ID,
                                                        "-1");
                                            }
                                        }catch (NumberFormatException | NullPointerException e){
                                            SharedPreference.setDefaults(mActivity,TAG_CLOCK_OUT_ID,
                                                    "-1");
                                        }catch (Exception e){
                                            SharedPreference.setDefaults(mActivity,TAG_CLOCK_OUT_ID,
                                                    "-1");
                                        }
                                        if (SharedPreference.getDefaults(mActivity, GlobalData.TAG_IN_OUT_STATUS).equalsIgnoreCase("first")) {
                                            SharedPreference.setDefaults(mActivity, GlobalData.TAG_IN_OUT_STATUS, "second");
                                        } else if (SharedPreference.getDefaults(mActivity, GlobalData.TAG_IN_OUT_STATUS).equalsIgnoreCase("second")) {
                                            SharedPreference.setDefaults(mActivity, GlobalData.TAG_IN_OUT_STATUS, "first");
                                        } else {
                                            SharedPreference.setDefaults(mActivity, GlobalData.TAG_IN_OUT_STATUS, "");
                                        }

                                        if (SharedPreference.getDefaults(mActivity, GlobalData.TAG_IN_OUT_STATUS).equalsIgnoreCase("first")) {
                                            tv_fingerprint_icon.setTextColor(getResources().getColor(R.color.green));
                                            tv_punchin.setTextColor(getResources().getColor(R.color.green));
                                            tv_punchin.setText("Tap Here to PunchIn");
                                        } else if (SharedPreference.getDefaults(mActivity, GlobalData.TAG_IN_OUT_STATUS).equalsIgnoreCase("second")) {
                                            tv_fingerprint_icon.setTextColor(getResources().getColor(R.color.red));
                                            tv_punchin.setTextColor(getResources().getColor(R.color.red));
                                            tv_punchin.setText("Tap Here to PunchOut");
                                        }

                                        Log.e(TAG,"response desc "+response.body().getStatusDescription());

                                        new ClockedIn_details(mActivity, apiInterface, rv_list_clockinout).execute();

                                    } else {
                                        onSaveDB(FA_EMP_ID,FA_CLOCK_TYPE,FA_CLOCK_IN_ID,FA_IMAGE_URI,FA_DATE_TIME,
                                                FA_LATITUDE,FA_LONGITUDE,FA_CURR_LOC);
                                        new SnackbarIps(tv_back,"Saved IN successfully");
                                    }

                                } else {
                                    onSaveDB(FA_EMP_ID,FA_CLOCK_TYPE,FA_CLOCK_IN_ID,FA_IMAGE_URI,FA_DATE_TIME,
                                            FA_LATITUDE,FA_LONGITUDE,FA_CURR_LOC);
                                    new SnackbarIps(tv_back,"Saved IN successfully");
                                }

                                if (myDialog!=null && myDialog.isShowing()){
                                    myDialog.dismiss();
                                }
                            }
                            @Override
                            public void onFailure(@NonNull Call<ImageRes> call, @NonNull Throwable t) {
                                Log.e(TAG, "Internet or Server Error "+t.getMessage());
                                if (myDialog!=null && myDialog.isShowing()){
                                    myDialog.dismiss();
                                }
                                onSaveDB(FA_EMP_ID,FA_CLOCK_TYPE,FA_CLOCK_IN_ID,FA_IMAGE_URI,FA_DATE_TIME,
                                        FA_LATITUDE,FA_LONGITUDE,FA_CURR_LOC);
                                new SnackbarIps(tv_back,"Saved IN successfully");
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (myDialog!=null && myDialog.isShowing()){
                            myDialog.dismiss();
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    if (myDialog!=null && myDialog.isShowing()){
                        myDialog.dismiss();
                    }
                }
            } catch (Exception e) {
                if (myDialog!=null && myDialog.isShowing()){
                    myDialog.dismiss();
                }
                e.printStackTrace();
            }
            return null;
        }
    }

    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,60,baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }
    public Uri getImageUri(Activity inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 60, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(),
                inImage, "Title", null);
        return Uri.parse(path);
    }

    public void onSaveDB(String empID,String clockType,String clockInId,String imageUri,String dateTime,
                         String latitude,String longitude,String currentLocation){

        Log.e(TAG,"TAG_IN_OUT_STATUS "+SharedPreference.getDefaults(mActivity, GlobalData.TAG_IN_OUT_STATUS));
        if (SharedPreference.getDefaults(mActivity, GlobalData.TAG_IN_OUT_STATUS).equalsIgnoreCase("first")) {
            SharedPreference.setDefaults(mActivity, GlobalData.TAG_IN_OUT_STATUS, "second");
        } else if (SharedPreference.getDefaults(mActivity, GlobalData.TAG_IN_OUT_STATUS).equalsIgnoreCase("second")) {
            SharedPreference.setDefaults(mActivity, GlobalData.TAG_IN_OUT_STATUS, "first");
        } else {
            SharedPreference.setDefaults(mActivity, GlobalData.TAG_IN_OUT_STATUS, "first");
        }

        if (SharedPreference.getDefaults(mActivity, GlobalData.TAG_IN_OUT_STATUS).equalsIgnoreCase("first")) {
            tv_fingerprint_icon.setTextColor(getResources().getColor(R.color.green));
            tv_punchin.setTextColor(getResources().getColor(R.color.green));
            tv_punchin.setText("Tap Here to PunchIn");
        } else if (SharedPreference.getDefaults(mActivity, GlobalData.TAG_IN_OUT_STATUS).equalsIgnoreCase("second")) {
            tv_fingerprint_icon.setTextColor(getResources().getColor(R.color.red));
            tv_punchin.setTextColor(getResources().getColor(R.color.red));
            tv_punchin.setText("Tap Here to PunchOut");
        }

        databaseHelper = new DatabaseHelper(this);
        clockInOutTime = new ClockInOutTime();

        clockInOutTime.setEmp_id(empID);
        clockInOutTime.setClock_type(clockType);
        clockInOutTime.setClock_in_id(clockInId);
        clockInOutTime.setImg_uri(imageUri);
        clockInOutTime.setDate_time(dateTime);
        clockInOutTime.setLatitude(latitude);
        clockInOutTime.setLongitude(longitude);
        clockInOutTime.setCurrentLocation(currentLocation);

        databaseHelper.SaveClockINOUT(clockInOutTime);

    }
}