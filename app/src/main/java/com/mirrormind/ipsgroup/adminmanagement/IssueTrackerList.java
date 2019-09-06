package com.mirrormind.ipsgroup.adminmanagement;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mirrormind.ipsgroup.R;

import net.cachapa.expandablelayout.ExpandableLayout;

import uihelper.Interface.RecyclerClick;
import uihelper.icomoon.Icomoon;

import static uihelper.DateFormat.parseDateToday;

public class IssueTrackerList extends AppCompatActivity implements View.OnClickListener {

    TextView tv_back,tv_header_name,tv_no_data_found;
    RecyclerView rv_attend;
    String[] attendancetext = {"HR Help Desk", "Issue Approval"};
    int[] attendanceicon = {R.string.att_teamatticon_color, R.string.att_historyicon_color};
    AttendanceATAdapter attendanceATAdapter;
    Activity mActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_approval);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        tv_back = findViewById(R.id.tv_back);
        tv_header_name = findViewById(R.id.tv_header_name);
        tv_no_data_found = findViewById(R.id.tv_no_data_found);
        tv_back.setOnClickListener(this);

        tv_header_name.setText(getResources().getString(R.string.issue_tracker_list));
        tv_no_data_found.setVisibility(View.GONE);
        Icomoon.imageLogo.apply(this, tv_back);

        rv_attend = findViewById(R.id.rv_attend);
        rv_attend.setLayoutManager(new GridLayoutManager(mActivity, 1));
        rv_attend.setAdapter(attendanceATAdapter = new AttendanceATAdapter(mActivity,
                attendancetext, attendanceicon));
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                super.onBackPressed();
                break;
        }
    }

    private class AttendanceATAdapter extends RecyclerView.Adapter<AttendanceATAdapter.MyViewHolder> {

        Activity mActivity;
        String[] attendancetext;
        int[] attendanceicon;


        public AttendanceATAdapter(Activity mActivity, String[] attendancetext, int[] attendanceicon) {
            this.mActivity = mActivity;
            this.attendancetext = attendancetext;
            this.attendanceicon = attendanceicon;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_issuetrackerlist_dashboard,
                    viewGroup, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

            holder.ll_edit_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (holder.expandableLayout.isExpanded()) {
                        holder.expandableLayout.collapse();

                            }else {
                        holder.expandableLayout.expand();
                        }

                    }

            });
        }


        @Override
        public int getItemCount() {
            return attendancetext.length;
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {

            Button tv_lvstatus_fromdate, tv_lvstatus_todate;
            TextView tv_leave_user,tv_edit_user,tv_user_icon,tv_command_icon;
            ExpandableLayout expandableLayout;

            LinearLayout ll_edit_user;

            public MyViewHolder(View itemView) {
                super(itemView);
                tv_lvstatus_fromdate = itemView.findViewById(R.id.tv_lvstatus_fromdate);
                tv_lvstatus_todate = itemView.findViewById(R.id.tv_lvstatus_todate);
                tv_leave_user = itemView.findViewById(R.id.tv_leave_user);
                expandableLayout = itemView.findViewById(R.id.expandable_layout_1);
                tv_edit_user = itemView.findViewById(R.id.tv_edit_user);
                ll_edit_user = itemView.findViewById(R.id.ll_edit_user);
                tv_user_icon = itemView.findViewById(R.id.tv_user_icon);
                tv_command_icon = itemView.findViewById(R.id.tv_command_icon);

                Icomoon.imageLogo.apply(mActivity,tv_edit_user);
                Icomoon.imageLogo.apply(mActivity,tv_user_icon);
                Icomoon.imageLogo.apply(mActivity,tv_command_icon);

            }
        }
    }
}
