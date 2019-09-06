package com.mirrormind.ipsgroup.adminmanagement;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import com.mirrormind.ipsgroup.R;
import net.cachapa.expandablelayout.ExpandableLayout;
import java.util.Arrays;
import java.util.List;
import uihelper.icomoon.Icomoon;

public class IssueTracker extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    TextView tv_back;
    Spinner ac_department, ac_querytype, ac_priority;
    ExpandableLayout expandableLayout;
    Button btn_apply;
    RecyclerView rv_issuetracker;
    Activity mActivity;
    IssueTrackerAdapter issueTrackerAdapter;
    ArrayAdapter<String> departmentadapter,querytypeadapter, priorityadapter;

    private static final String[] department = new String[]{"Management & Administration",
            "Business Acquisition & Client Engagement", "FSS", "Finance & Accounts", "Payroll & Compliance", "Ips Infra", "HR & Admin", "Admin", "Executive Search", "Ips Tel", "Ips Tech"};
    private static final String[] querytype = new String[]{"Appointment Letter", "Salary Deduction", "Salary Slip", "TDS", "Leave", "Attendance", "Stationary", "ID Card", "Others"};

    private static final String[] priority = new String[]{"High", "Normal", "Low"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issuetracker);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        initID();

    }

    private void initID() {

        tv_back = findViewById(R.id.tv_back);
        rv_issuetracker = findViewById(R.id.rv_issuetracker);
        expandableLayout = findViewById(R.id.expandable_layout);

        ac_department = findViewById(R.id.ac_department);
        ac_querytype = findViewById(R.id.ac_querytype);
        ac_priority = findViewById(R.id.ac_priority);
        btn_apply = findViewById(R.id.btn_apply);

        Icomoon.imageLogo.apply(this, tv_back);

        tv_back.setOnClickListener(this);
        btn_apply.setOnClickListener(this);
        expandableLayout.setOnClickListener(this);
        rv_issuetracker.setOnClickListener(this);

        departmentadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, department);
        departmentadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ac_department.setAdapter(departmentadapter);

        querytypeadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, querytype);
        querytypeadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ac_querytype.setAdapter(querytypeadapter);

        priorityadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, priority);
        priorityadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ac_priority.setAdapter(priorityadapter);

        rv_issuetracker.setLayoutManager(new GridLayoutManager(mActivity, 1));
        rv_issuetracker.setAdapter(issueTrackerAdapter = new IssueTrackerAdapter(mActivity, Arrays.asList(priority)));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                super.onBackPressed();
                break;
            case R.id.btn_apply:
                if (this.expandableLayout.isExpanded()) {
                    this.expandableLayout.collapse();
                } else {
                    this.expandableLayout.expand();
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    private class IssueTrackerAdapter extends RecyclerView.Adapter<IssueTrackerAdapter.MyViewHolder> {

        Activity mActivity;
        List leavetext;

        public IssueTrackerAdapter(Activity mActivity, List leavetext) {
            this.mActivity = mActivity;
            this.leavetext = leavetext;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_issuetrackerdashboard,
                    parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        }

        @Override
        public int getItemCount() {
            return leavetext.size();
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView tv_option_icon, tv_leavearrow, tv_option, tv_lvstatus_date, tv_lvstatus_fromdate,
                    tv_lvstatus_todate, tv_lvstatus_days, tv_lvstatus_cmts;

            public MyViewHolder(View itemView) {
                super(itemView);

                tv_option_icon = itemView.findViewById(R.id.tv_leave_calendar);
                tv_option = itemView.findViewById(R.id.tv_leave_user);
                tv_leavearrow = itemView.findViewById(R.id.tv_leavearrow);

                tv_lvstatus_date = itemView.findViewById(R.id.tv_lvstatus_date);
                tv_lvstatus_fromdate = itemView.findViewById(R.id.tv_lvstatus_fromdate);
                tv_lvstatus_todate = itemView.findViewById(R.id.tv_lvstatus_todate);
                tv_lvstatus_days = itemView.findViewById(R.id.tv_lvstatus_days);
                tv_lvstatus_cmts = itemView.findViewById(R.id.tv_lvstatus_cmts);

                Icomoon.imageLogo.apply(mActivity, tv_option_icon);
                Icomoon.imageLogo.apply(mActivity, tv_option);
            }
        }
    }

}
