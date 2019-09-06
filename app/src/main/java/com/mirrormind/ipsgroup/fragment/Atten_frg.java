package com.mirrormind.ipsgroup.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mirrormind.ipsgroup.OnDBSync;
import com.mirrormind.ipsgroup.attendanceteam.AttendanceAT;
import com.mirrormind.ipsgroup.attendanceteam.AttendanceHistory;
import com.mirrormind.ipsgroup.attendanceteam.Fingerprint_Activity;
import com.mirrormind.ipsgroup.attendanceteam.LeaveInformation_Activity;
import com.mirrormind.ipsgroup.R;
import com.mirrormind.ipsgroup.attendanceteam.TeamAttendance;
import com.mirrormind.ipsgroup.attendanceteam.TrackTeam;

import uihelper.Interface.RecyclerClick;
import uihelper.icomoon.Icomoon;

public class Atten_frg extends Fragment implements View.OnClickListener {

    Activity mActivity;
    RecyclerView rv_adminmng_dashboard;
    View view;
    TextView tv_back;

    String[] attendancetext = {"Team Attendance", "Attendance History", "Track Team"};

    int[] attendanceicon = {
            R.string.att_teamatticon_color, R.string.att_historyicon_color,
            R.string.att_trackicon_color
    };

    AttendanceAdapter attendanceAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.attendance_fragment, container, false);

        initID();
        return view;
    }

    private void initID() {

        rv_adminmng_dashboard = view.findViewById(R.id.rv_adminmng_dashboard);
        tv_back = view.findViewById(R.id.tv_back);

        tv_back.setOnClickListener(this);

        view.findViewById(R.id.ll_attendance).setOnClickListener(this);
        view.findViewById(R.id.ll_apply_leave).setOnClickListener(this);
        view.findViewById(R.id.ll_approval).setOnClickListener(this);

        Icomoon.imageLogo.apply(mActivity, tv_back);

        rv_adminmng_dashboard.setLayoutManager(new GridLayoutManager(mActivity, 1));
        rv_adminmng_dashboard.setAdapter(attendanceAdapter = new AttendanceAdapter(mActivity,
                attendancetext, attendanceicon));
        mActivity = getActivity();

        rv_adminmng_dashboard.addOnItemTouchListener(new RecyclerClick(getActivity(), rv_adminmng_dashboard,
                new RecyclerClick.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {

                        switch (position) {
                            case 0:
                                startActivity(new Intent(getActivity(), TeamAttendance.class));
                                break;
                            case 1:
                                startActivity(new Intent(getActivity(), AttendanceHistory.class));
                                break;
                            case 2:
                                startActivity(new Intent(getActivity(), TrackTeam.class));
                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                })
        );

        new OnDBSync(mActivity,1).execute();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.tv_back:
                getFragmentManager().popBackStack();
                break;
            case R.id.ll_attendance:
                startActivity(new Intent(getActivity(), Fingerprint_Activity.class));
                break;
            case R.id.ll_apply_leave:
                startActivity(new Intent(getActivity(), LeaveInformation_Activity.class));
                break;
            case R.id.ll_approval:
                startActivity(new Intent(getActivity(), AttendanceAT.class));
                break;

        }

    }

    private class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.MyViewHolder> {
        Activity mActivity;
        String[] attendancetext;
        int[] attendanceicon;

        public AttendanceAdapter(Activity mActivity, String[] attendancetext, int[] attendanceicon) {
            this.mActivity = mActivity;
            this.attendancetext = attendancetext;
            this.attendanceicon = attendanceicon;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.attendance_dashboard,
                    parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.tv_option.setText(attendancetext[position]);
            holder.tv_option_icon.setText(getResources().getString(attendanceicon[position]));
            holder.tv_option.setTextColor(getResources().getColor(R.color.black));
            Icomoon.imageLogo.apply(mActivity, holder.tv_option_icon);
        }

        @Override
        public int getItemCount() {
            return attendancetext.length;
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {

            LinearLayout ll_option;
            TextView tv_option_icon, tv_option;

            public MyViewHolder(View itemView) {
                super(itemView);
                ll_option = itemView.findViewById(R.id.ll_option);
                tv_option_icon = itemView.findViewById(R.id.tv_teamperfm_icon);
                tv_option = itemView.findViewById(R.id.tv_teamperfm_text);

            }
        }
    }

}