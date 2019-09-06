package com.mirrormind.ipsgroup.attendanceteam;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.mirrormind.ipsgroup.R;

import uihelper.Interface.RecyclerClick;
import uihelper.icomoon.Icomoon;

public class AttendanceAT extends AppCompatActivity implements View.OnClickListener {

    TextView tv_back,tv_att_icon,tv_leave_icon;
    LinearLayout ll_attendance,ll_leave;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_at);

        tv_att_icon = findViewById(R.id.tv_att_icon);
        tv_leave_icon = findViewById(R.id.tv_leave_icon);
        ll_attendance = findViewById(R.id.ll_attendance);
        ll_leave = findViewById(R.id.ll_leave);
        tv_back = findViewById(R.id.tv_back);
        Icomoon.imageLogo.apply(this, tv_back);

        Icomoon.imageLogo.apply(this,tv_att_icon);
        Icomoon.imageLogo.apply(this,tv_leave_icon);

        tv_back.setOnClickListener(this);
        ll_attendance.setOnClickListener(this);
        ll_leave.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                super.onBackPressed();
                break;
            case R.id.ll_attendance:
                startActivity(new Intent(getApplicationContext(), AttendanceApproval.class));
                break;
            case R.id.ll_leave:
                startActivity(new Intent(getApplicationContext(), LeaveApproval.class));
                break;
        }
    }
}