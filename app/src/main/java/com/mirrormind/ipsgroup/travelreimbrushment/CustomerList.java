package com.mirrormind.ipsgroup.travelreimbrushment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.mirrormind.ipsgroup.R;

import net.cachapa.expandablelayout.ExpandableLayout;

import uihelper.Interface.RecyclerClick;
import uihelper.icomoon.Icomoon;

public class CustomerList extends AppCompatActivity implements View.OnClickListener {
    TextView tv_calendar, tv_user, tv_back;
    RecyclerView rv_leaveAT;

    String[] leavetext = {"HR Help Desk", "Issue Approval"};

    int[] leaveicon = {
            R.string.att_teamatticon_color, R.string.att_historyicon_color
    };
    LeaveATAdapter leaveATAdapter;
    AutoCompleteTextView ac_tstatusview, ac_tplannedview;

    private static final String[] status = new String[]{"Approved",
            "Reject", "Cancel"};
    private static final String[] leaveplanned = new String[]{"Planned", "UnPlanned"};
    Activity mActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customerlist);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        initid();
        initlistener();


        rv_leaveAT = findViewById(R.id.rv_customerlist);
        rv_leaveAT.setLayoutManager(new GridLayoutManager(mActivity, 1));
        rv_leaveAT.setAdapter(leaveATAdapter = new LeaveATAdapter(mActivity,
                leavetext, leaveicon));

        rv_leaveAT.addOnItemTouchListener(new RecyclerClick(getApplicationContext(), rv_leaveAT,
                new RecyclerClick.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {

                        switch (position) {
                            case 0:
                                //                         startActivity(new Intent(getApplicationContext(), AttendanceAT.class));
                                break;
                            case 3:

                                break;
                            case 4:

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

    }


    private void initid() {
        tv_back = findViewById(R.id.tv_back);
        tv_calendar = findViewById(R.id.tv_calendar);
        tv_user = findViewById(R.id.tv_user);


    }

    private void initlistener() {
        tv_back.setOnClickListener(this);
        Icomoon.imageLogo.apply(this, tv_back);
        Icomoon.imageLogo.apply(this, tv_calendar);
        Icomoon.imageLogo.apply(this, tv_user);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                super.onBackPressed();
                break;


        }
    }


    private class LeaveATAdapter extends RecyclerView.Adapter<LeaveATAdapter.MyViewHolder> implements View.OnClickListener {
        Activity mActivity;
        String[] leavetext;
        int[] leaveicon;


        public LeaveATAdapter(Activity mActivity, String[] leavetext, int[] leaveicon) {
            this.mActivity = mActivity;
            this.leavetext = leavetext;
            this.leaveicon = leaveicon;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_customerlistdashboard,
                    viewGroup, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
//            holder.tv_option_icon.setText((leavetext[position]));

//            holder.tv_lvstatus_fromdate.setBackgroundColor(getResources().getColor(R.color.green));
//            holder.tv_lvstatus_todate.setBackgroundColor(getResources().getColor(R.color.red));
//            holder.tv_leave_days.setBackgroundColor(getResources().getColor(R.color.lightblue));
//            Icomoon.imageLogo.apply(getApplicationContext(), holder.tv_leave_user);
            //   holder.tv_leave_user.setOnClickListener(this);
//            holder.expandableLayout.setOnClickListener(this);
//            if (holder.expandableLayout.isExpanded()) {
//                holder.expandableLayout.collapse();
//            }
//
//            holder.tv_leave_user.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    //    leaveATAdapter.notifyDataSetChanged();
//                }
//            });

        }


        @Override
        public int getItemCount() {
            return leavetext.length;
        }

        @Override
        public void onClick(View view) {

        }


        public class MyViewHolder extends RecyclerView.ViewHolder {

            Button tv_lvstatus_fromdate, tv_lvstatus_todate, tv_leave_days;
            TextView tv_leave_user;
            ExpandableLayout expandableLayout;
            AutoCompleteTextView ed_ac_status, ed_ac_planning;
            ArrayAdapter<String> statusadapter, plannedadapter;
            Boolean click = true;
            Boolean click1 = true;

            public MyViewHolder(View itemView) {
                super(itemView);
//                tv_lvstatus_fromdate = itemView.findViewById(R.id.tv_lvstatus_fromdate);
//                tv_lvstatus_todate = itemView.findViewById(R.id.tv_lvstatus_todate);
//                tv_leave_user = itemView.findViewById(R.id.tv_leave_user);
//                expandableLayout = itemView.findViewById(R.id.expandable_layout_1);
//                tv_leave_days = itemView.findViewById(R.id.tv_leave_days);
//                ed_ac_status = itemView.findViewById(R.id.ed_ac_status);
//                ed_ac_planning = itemView.findViewById(R.id.ed_ac_planning);

                //         tv_option = itemView.findViewById(R.id.tv_teamperfm_text);

            }
        }
    }
}
