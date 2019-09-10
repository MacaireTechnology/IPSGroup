package com.mirrormind.ipsgroup.attendanceteam;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.List;

import retrofit.ApiClient;
import retrofit.ApiInterface;
import retrofit.response.team.GetTeamDetails;
import retrofit.response.team.GetTeamRes;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uihelper.SnackbarIps;
import uihelper.icomoon.Icomoon;
import uihelper.sharedPref.GlobalData;
import uihelper.sharedPref.SharedPreference;

public class TeamMap extends AppCompatActivity implements View.OnClickListener,
        OnMapReadyCallback, GlobalData {

    private static final String TAG = TeamMap.class.getSimpleName();
    TextView tv_back;
    SupportMapFragment mapFragment;
    Activity mActivity;
    RecyclerView rv_getTeam;
    ApiInterface apiInterface;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teammap);

        mActivity = this;
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        tv_back = findViewById(R.id.tv_back);
        rv_getTeam = findViewById(R.id.rv_getTeam);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        tv_back.setOnClickListener(this);
        findViewById(R.id.tv_call_map).setOnClickListener(this);
        mapFragment.getMapAsync(this);
        Icomoon.imageLogo.apply(this, tv_back);

        rv_getTeam.setLayoutManager(new LinearLayoutManager(mActivity,
                LinearLayoutManager.VERTICAL,false));

        doGetTeam();
    }

    private void doGetTeam() {

        Call<GetTeamRes> getTeamResCall = apiInterface.doGetTeamData(
                SharedPreference.getDefaults(mActivity,TAG_SHARE_EMP_ID));
        getTeamResCall.enqueue(new Callback<GetTeamRes>() {
            @Override
            public void onResponse(@NonNull Call<GetTeamRes> call,@NonNull Response<GetTeamRes> response) {
                if (response.code() == 200){
                    if (response.body().getStatusCode().equals("00")){
                        if (response.body().getReturnValue().size()>0){
                            rv_getTeam.setAdapter(new TeamAdapter(response.body().getReturnValue()));
                        }else {
                            new SnackbarIps(tv_back,"No Data Found");
                        }
                    }else {
                        new SnackbarIps(tv_back,"No Data Found");
                    }
                }else {
                    new SnackbarIps(tv_back,"Server Error");
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetTeamRes> call,@NonNull Throwable t) {
                new SnackbarIps(tv_back,"Internet or Server Error");
            }
        });
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
                    .position(new LatLng(Double.parseDouble(SharedPreference.getDefaults(mActivity,TAG_SHARE_LAT)),
                            Double.parseDouble(SharedPreference.getDefaults(mActivity,TAG_SHARE_LONG))))
                    .title(SharedPreference.getDefaults(mActivity,TAG_SHARE_NAME))
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.mipmap.map_loc)));
            marker.showInfoWindow();
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(Double.parseDouble(SharedPreference.getDefaults(mActivity,TAG_SHARE_LAT)),
                            Double.parseDouble(SharedPreference.getDefaults(mActivity,TAG_SHARE_LONG))), 10));
        }catch (NullPointerException | NumberFormatException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void onCallDirection() {
        try {
            Uri gmmIntentUri = Uri.parse("google.navigation:q="+
                    SharedPreference.getDefaults(mActivity,TAG_SHARE_LAT)+","+
                    SharedPreference.getDefaults(mActivity,TAG_SHARE_LONG));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }catch (NullPointerException | NumberFormatException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.MyViewHolder> {
        List<GetTeamDetails> returnValue;
        private TeamAdapter(List<GetTeamDetails> returnValue) {
            this.returnValue = returnValue;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(mActivity).inflate(R.layout.adpt_team_map,parent,false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            if (returnValue.get(position).getGpsdt()!=null &&
                    !returnValue.get(position).getGpsdt().equals("") &&
                    !returnValue.get(position).getGpsdt().isEmpty()){
                holder.tv_date.setText(returnValue.get(position).getGpsdt());
            }else {
                holder.tv_date.setText("-");
            }
            if (returnValue.get(position).getGpsAddress()!=null &&
                    !returnValue.get(position).getGpsdt().equals("") &&
                    !returnValue.get(position).getGpsdt().isEmpty()){
                holder.tv_address.setText(returnValue.get(position).getGpsAddress());
            }else {
                holder.tv_address.setText("-");
            }
        }

        @Override
        public int getItemCount() {
            return returnValue.size();
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {

            TextView tv_calendar_icon,tv_date,tv_loc_icon,tv_address;

            private MyViewHolder(View itemView) {
                super(itemView);

                tv_calendar_icon = itemView.findViewById(R.id.tv_calendar_icon);
                tv_date = itemView.findViewById(R.id.tv_date);
                tv_loc_icon = itemView.findViewById(R.id.tv_loc_icon);
                tv_address = itemView.findViewById(R.id.tv_address);

                Icomoon.imageLogo.apply(mActivity,tv_calendar_icon);
                Icomoon.imageLogo.apply(mActivity,tv_loc_icon);
            }
        }
    }
}
