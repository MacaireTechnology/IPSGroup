package com.mirrormind.ipsgroup.attendanceteam;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
import com.mirrormind.ipsgroup.R;

import uihelper.icomoon.Icomoon;
import uihelper.sharedPref.GlobalData;
import uihelper.sharedPref.SharedPreference;

public class TeamMap extends AppCompatActivity implements View.OnClickListener,
        OnMapReadyCallback, GlobalData {

    TextView tv_back;
    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    Activity mActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teammap);

        mActivity = this;
        tv_back = findViewById(R.id.tv_back);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        tv_back.setOnClickListener(this);
        findViewById(R.id.tv_call_map).setOnClickListener(this);
        mapFragment.getMapAsync(this);

        Icomoon.imageLogo.apply(this, tv_back);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                super.onBackPressed();
                break;
            case R.id.tv_call_map:
                onCallDirection();
                break;
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.parseDouble(SharedPreference.getDefaults(mActivity,TAG_CURR_LAT)),
                            Double.parseDouble(SharedPreference.getDefaults(mActivity,TAG_CURR_LONG))))
                    .title(SharedPreference.getDefaults(mActivity,TAG_USER_NAME))
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.mipmap.map_loc)));
            marker.showInfoWindow();
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(Double.parseDouble(SharedPreference.getDefaults(mActivity,TAG_CURR_LAT)),
                            Double.parseDouble(SharedPreference.getDefaults(mActivity,TAG_CURR_LONG))), 10));
        }catch (NullPointerException | NumberFormatException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void onCallDirection() {
        try {
            Uri gmmIntentUri = Uri.parse("google.navigation:q="+
                    SharedPreference.getDefaults(mActivity,TAG_CURR_LAT)+","+
                    SharedPreference.getDefaults(mActivity,TAG_CURR_LONG));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }catch (NullPointerException | NumberFormatException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
