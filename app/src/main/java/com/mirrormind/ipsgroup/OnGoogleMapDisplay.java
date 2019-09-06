package com.mirrormind.ipsgroup;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import uihelper.icomoon.Icomoon;
import uihelper.sharedPref.GlobalData;
import uihelper.sharedPref.SharedPreference;

public class OnGoogleMapDisplay extends FragmentActivity implements
        View.OnClickListener,OnMapReadyCallback, GlobalData {

    public static final String TAG = OnGoogleMapDisplay.class.getSimpleName();
    private SupportMapFragment mSupportMapFragment;
    TextView tv_back_icon;
    Activity mActivity;
    GoogleMap mMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_on_map);

        mActivity = this;

        tv_back_icon = findViewById(R.id.tv_back_icon);
        Icomoon.imageLogo.apply(this,tv_back_icon);

        mSupportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mSupportMapFragment.getMapAsync(this);

        try {
            if (mSupportMapFragment != null) {
                mSupportMapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(final GoogleMap googleMap) {

                    }
                });

            }
        }catch (NullPointerException | NumberFormatException e) {
            Log.e(TAG,"Num | Null Exception1 "+e.getMessage());
        }catch (Exception e){
            Log.e(TAG,"Num | Null Exception1 "+e.getMessage());
        }
        findViewById(R.id.tv_call_map).setOnClickListener(this);
        findViewById(R.id.tv_back_icon).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_call_map:
                break;
            case R.id.tv_back_icon:
                onBackPressed();
                break;
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            mMap.setMyLocationEnabled(true);
        }

        if (googleMap != null) {
            googleMap.getUiSettings().setAllGesturesEnabled(true);
        }
        try {

            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            if (SharedPreference.getDefaults(mActivity,TAG_CURR_LAT)!=null &&
                    SharedPreference.getDefaults(mActivity,TAG_CURR_LONG)!=null){

                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(SharedPreference.getDefaults(mActivity,TAG_CURR_LAT)),
                                Double.parseDouble(SharedPreference.getDefaults(mActivity,TAG_CURR_LONG))))
                        .title(SharedPreference.getDefaults(mActivity,TAG_USER_NAME))
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.mipmap.map_loc)));
                marker.showInfoWindow();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(Double.parseDouble(SharedPreference.getDefaults(mActivity,TAG_CURR_LAT)),
                                Double.parseDouble(SharedPreference.getDefaults(mActivity,TAG_CURR_LONG))), 15));
            }
        }catch (NullPointerException | NumberFormatException e) {
            Log.e(TAG,"Num | Null Exception "+e.getMessage());
        }catch (Exception e){
            Log.e(TAG,"Num | Null Exception "+e.getMessage());
        }

    }
}
