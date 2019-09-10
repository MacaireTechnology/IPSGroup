package com.mirrormind.ipsgroup.adminmanagement;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mirrormind.ipsgroup.Dialog.DialogsUtils;
import com.mirrormind.ipsgroup.R;
import com.mirrormind.ipsgroup.attendanceteam.LeaveInformation_Activity;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit.ApiClient;
import retrofit.ApiInterface;
import retrofit.response.ApplyLeaveRes;
import retrofit.response.admin.IssueTrackerDetails;
import retrofit.response.admin.IssueTrackerRes;
import retrofit.response.contact.ContactDetails;
import retrofit.response.dept.DeptDetails;
import retrofit.response.dept.DeptRes;
import retrofit.response.leaveType.LeaveTypeDetails;
import retrofit.response.leaveType.LeaveTypeRes;
import retrofit.response.queryType.QueryTypeDetails;
import retrofit.response.queryType.QueryTypeRes;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uihelper.SnackbarIps;
import uihelper.icomoon.Icomoon;
import uihelper.onKeyboard.OnKeyboardHide;
import uihelper.picker.OnCurrentDay;
import uihelper.sharedPref.GlobalData;
import uihelper.sharedPref.SharedPreference;

public class IssueTracker extends AppCompatActivity implements View.OnClickListener,
        GlobalData {

    private static final String TAG = IssueTracker.class.getSimpleName();
    TextView tv_back,tv_dept,tv_query_type,tv_submit_issue,
            tv_search_icon,tv_back_search;
    LinearLayout ll_header,ll_search;
    EditText et_description,et_search;
    Spinner sp_dept,sp_query_type,sp_priority;
    ExpandableLayout expandableLayout;
    Button btn_apply;
    RecyclerView rv_issuetracker;
    Activity mActivity;
    ApiInterface apiInterface;
    IssueTrackerAdapter issueTrackerAdapter;
    List<DeptDetails> deptList = new ArrayList<>();
    List<QueryTypeDetails> queryTypeDetailsList = new ArrayList<>();
    private static final String[] priority = new String[]{"High", "Normal", "Low"};
    String selectDeptId="",selectQueryTypeID="",selectPriorityId="";
    JSONObject allItems = new JSONObject();
    ArrayAdapter<String> priorityAdapter;
    ProgressDialog myDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issuetracker);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mActivity = this;
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        tv_back = findViewById(R.id.tv_back);
        tv_dept = findViewById(R.id.tv_dept);
        tv_query_type = findViewById(R.id.tv_query_type);
        rv_issuetracker = findViewById(R.id.rv_issuetracker);
        expandableLayout = findViewById(R.id.expandable_layout);
        tv_submit_issue = findViewById(R.id.tv_submit_issue);
        et_description = findViewById(R.id.et_description);
        tv_search_icon = findViewById(R.id.tv_search_icon);
        ll_header = findViewById(R.id.ll_header);
        ll_search = findViewById(R.id.ll_search);
        et_search = findViewById(R.id.et_search);
        tv_back_search = findViewById(R.id.tv_back_search);

        sp_dept = findViewById(R.id.sp_dept);
        sp_query_type = findViewById(R.id.sp_query_type);
        sp_priority = findViewById(R.id.sp_priority);
        btn_apply = findViewById(R.id.btn_apply);

        Icomoon.imageLogo.apply(this, tv_back);
        Icomoon.imageLogo.apply(mActivity, tv_search_icon);
        Icomoon.imageLogo.apply(mActivity, tv_back_search);

        findViewById(R.id.ll_header).setOnClickListener(this);
        findViewById(R.id.ll_search).setOnClickListener(this);
        findViewById(R.id.tv_search_icon).setOnClickListener(this);
        findViewById(R.id.tv_back_search).setOnClickListener(this);
        tv_back.setOnClickListener(this);
        btn_apply.setOnClickListener(this);
        expandableLayout.setOnClickListener(this);
        rv_issuetracker.setOnClickListener(this);
        tv_submit_issue.setOnClickListener(this);

        rv_issuetracker.setLayoutManager(new GridLayoutManager(mActivity, 1));

        doListIssueTracker();
        new Department().execute();
        new QueryType().execute();

        priorityAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, priority);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_priority.setAdapter(priorityAdapter);

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (issueTrackerAdapter!=null)
                    issueTrackerAdapter.getFilter().filter(charSequence);
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (issueTrackerAdapter!=null)
                    issueTrackerAdapter.getFilter().filter(charSequence);
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

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
                    new OnKeyboardHide(this,view);
                } else {
                    this.expandableLayout.expand();
                }
                break;
            case R.id.tv_submit_issue:
                new OnKeyboardHide(this,view);
                doVerifySubmitIssue();
                break;
            case R.id.tv_back_search:
                new OnKeyboardHide(mActivity,et_search);
                et_search.setText("");
                ll_header.setVisibility(View.VISIBLE);
                ll_search.setVisibility(View.GONE);
                break;
            case R.id.tv_search_icon:
                ll_header.setVisibility(View.GONE);
                ll_search.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void doVerifySubmitIssue() {

        if (tv_dept.getText().toString().equals("") || tv_dept.getText().toString().equals("Select Department")){
            new SnackbarIps(tv_back,"Select Department");
        }else if (tv_query_type.getText().toString().equals("") || tv_query_type.getText().toString().equals("Select Query Type")){
            new SnackbarIps(tv_back,"Select Query Type");
        }else if (et_description.getText().toString().equals("") || et_description.getText().toString().isEmpty()){
            new SnackbarIps(tv_back,"Enter Description");
        }else if (selectPriorityId.equals("") || selectPriorityId.equals("0")){
            new SnackbarIps(tv_back,"Select Priority");
        }else {
            doSubmitIssue();
        }
    }

    private void doSubmitIssue() {

        myDialog = DialogsUtils.showProgressDialog(this, "Submit Issue");
        myDialog.show();
        try {
            allItems.put("IssueCreationID",SharedPreference.getDefaults(this,TAG_EMP_ID));
            allItems.put("DepartmentID",selectDeptId);
            allItems.put("IssueID",selectQueryTypeID);
            allItems.put("Description",et_description.getText().toString());
            allItems.put("PriorityID",selectPriorityId);
            allItems.put("IssueCreationDate", OnCurrentDay.getDateTime());
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = (JsonObject) jsonParser.parse(String.valueOf(allItems));
            Log.e("allItems ", allItems.toString());

            Call<ApplyLeaveRes> logResCall = apiInterface.doIssueTracker(jsonObject);
            logResCall.enqueue(new Callback<ApplyLeaveRes>() {
                @Override
                public void onResponse(@NonNull Call<ApplyLeaveRes> call,@NonNull Response<ApplyLeaveRes> response) {
                    if (response.code() == 200) {
                        assert response.body() != null;
                        if (response.body().getStatusCode().equals("00")) {
                            if (myDialog!=null && myDialog.isShowing())
                                myDialog.dismiss();
                            doListIssueTracker();
                            if (expandableLayout.isExpanded()) {
                                expandableLayout.collapse();
                            }
                            new SnackbarIps(tv_back, "" + "Saved Isse Successfully", 2000);
                        } else {
                            if (myDialog!=null && myDialog.isShowing())
                                myDialog.dismiss();
                            new SnackbarIps(tv_back, "" + "Not a valid user", 2000);
                        }

                    } else {
                        if (myDialog!=null && myDialog.isShowing())
                            myDialog.dismiss();
                        new SnackbarIps(tv_back, "Server Error", 2000);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ApplyLeaveRes> call,@NonNull Throwable t) {
                    if (myDialog!=null && myDialog.isShowing())
                        myDialog.dismiss();
                    new SnackbarIps(tv_back, "Network Or Server Error", 2000);
                }
            });

        }catch (NullPointerException  | NumberFormatException e) {
            if (myDialog!=null && myDialog.isShowing())
                myDialog.dismiss();
        } catch (Exception e) {
            if (myDialog!=null && myDialog.isShowing())
                myDialog.dismiss();
        }
    }

    private void doListIssueTracker() {
        myDialog = DialogsUtils.showProgressDialog(this, "Submit Issue");
        myDialog.show();

        Call<IssueTrackerRes> doListIssueTracker = apiInterface.doListIssueTracker(
                SharedPreference.getDefaults(this,TAG_EMP_ID));
        doListIssueTracker.enqueue(new Callback<IssueTrackerRes>() {
            @Override
            public void onResponse(@NonNull Call<IssueTrackerRes> call,@NonNull Response<IssueTrackerRes> response) {
                if (response.code() == 200){
                    if (response.body().getStatusCode().equals("00")){
                        if (response.body().getReturnValue().size()>0){
                            if (myDialog!=null && myDialog.isShowing())
                                myDialog.dismiss();
                            rv_issuetracker.setAdapter(issueTrackerAdapter = new IssueTrackerAdapter(response.body().getReturnValue()));
                        }else {
                            if (myDialog!=null && myDialog.isShowing())
                                myDialog.dismiss();
                            new SnackbarIps(tv_back,"No Data Found");
                        }
                    }else {
                        if (myDialog!=null && myDialog.isShowing())
                            myDialog.dismiss();
                        new SnackbarIps(tv_back,"No Data Found");
                    }
                }else {
                    if (myDialog!=null && myDialog.isShowing())
                        myDialog.dismiss();
                    new SnackbarIps(tv_back,"Server Error");
                    Log.e(TAG,"Server Error");
                }
            }

            @Override
            public void onFailure(@NonNull Call<IssueTrackerRes> call,@NonNull Throwable t) {
                Log.e(TAG,"Internet or Server Error");
                new SnackbarIps(tv_back,"Internet or Server Error");
                if (myDialog!=null && myDialog.isShowing())
                    myDialog.dismiss();
            }
        });

        sp_dept.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (deptList!=null && deptList.size()>0) {
                    selectDeptId = deptList.get(position).getDeprtID();
                    tv_dept.setText(deptList.get(position).getDepartment());
                }else {
                    new SnackbarIps(tv_back,"No Department Available");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp_query_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (queryTypeDetailsList!=null && queryTypeDetailsList.size()>0) {
                    selectQueryTypeID = queryTypeDetailsList.get(position).getIssueID();
                    tv_query_type.setText(queryTypeDetailsList.get(position).getIssue());
                }else {
                    new SnackbarIps(tv_back,"No Department Available");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp_priority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (priority[position].toString().equals("High")){
                    selectPriorityId = "1";
                }else if (priority[position].toString().equals("Normal")){
                    selectPriorityId = "2";
                }else if (priority[position].toString().equals("Low")){
                    selectPriorityId = "3";
                }else {
                    selectPriorityId = "0";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private class IssueTrackerAdapter extends RecyclerView.Adapter<IssueTrackerAdapter.MyViewHolder>
            implements Filterable {

        List<IssueTrackerDetails> returnValue,listSearchView;

        private IssueTrackerAdapter(List<IssueTrackerDetails> returnValue) {
            this.returnValue = returnValue;
            this.listSearchView = returnValue;
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

            if (returnValue.get(position).getCreateDate()!=null &&
                    !returnValue.get(position).getCreateDate().equals("") &&
                    !returnValue.get(position).getCreateDate().isEmpty()) {
                holder.tv_apply_date.setText(returnValue.get(position).getCreateDate());
            }else {
                holder.tv_apply_date.setText("-");
            }

            if (returnValue.get(position).getStatus()!=null &&
                    !returnValue.get(position).getStatus().equals("") &&
                    !returnValue.get(position).getStatus().isEmpty()){
                holder.tv_status_type.setText(returnValue.get(position).getStatus());
            }else {
                holder.tv_status_type.setText("-");
            }

            if (returnValue.get(position).getDepartment()!=null &&
                    !returnValue.get(position).getDepartment().equals("") &&
                    !returnValue.get(position).getDepartment().isEmpty()){
                holder.tv_department.setText(returnValue.get(position).getDepartment());
            }else {
                holder.tv_department.setText("-");
            }

            if (returnValue.get(position).getQueryType()!=null &&
                    !returnValue.get(position).getQueryType().equals("") &&
                    !returnValue.get(position).getQueryType().isEmpty()){
                holder.tv_query_type.setText(returnValue.get(position).getQueryType());
            }else {
                holder.tv_query_type.setText("-");
            }

            if (returnValue.get(position).getDescription()!=null &&
                    !returnValue.get(position).getDescription().equals("") &&
                    !returnValue.get(position).getDescription().isEmpty()){
                holder.tv_description.setText(returnValue.get(position).getDescription());
            }else {
                holder.tv_description.setText("-");
            }
        }

        @Override
        public int getItemCount() {
            return returnValue.size();
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {

            TextView tv_leave_calendar;
            TextView tv_apply_date,tv_status_type,tv_department,
                    tv_query_type,tv_description;

            private MyViewHolder(View itemView) {
                super(itemView);

                tv_leave_calendar = itemView.findViewById(R.id.tv_leave_calendar);
                tv_apply_date = itemView.findViewById(R.id.tv_apply_date);
                tv_status_type = itemView.findViewById(R.id.tv_status_type);
                tv_department = itemView.findViewById(R.id.tv_department);
                tv_query_type = itemView.findViewById(R.id.tv_query_type);
                tv_description = itemView.findViewById(R.id.tv_description);

                Icomoon.imageLogo.apply(mActivity, tv_leave_calendar);
            }
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    String charString = charSequence.toString();
                    if (charString.isEmpty()) {
                        returnValue = listSearchView;
                    } else {
                        List<IssueTrackerDetails> filteredList = new ArrayList<>();
                        for (IssueTrackerDetails row : listSearchView) {

                            if ((row.getStatus()!=null && row.getStatus().toLowerCase().contains(charString.toLowerCase())) ||
                                    (row.getDepartment()!=null && row.getDepartment().toLowerCase().contains(charString.toLowerCase())) ||
                                    (row.getQueryType()!=null && row.getQueryType().toLowerCase().contains(charString.toLowerCase())) ||
                                    (row.getCreateDate()!=null && row.getCreateDate().toLowerCase().contains(charString.toLowerCase()))) {
                                filteredList.add(row);
                            }
                        }
                        returnValue = filteredList;
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = returnValue;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    returnValue = (ArrayList<IssueTrackerDetails>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }
    }

    public class Department extends AsyncTask<String, String, String> implements GlobalData {

        private Department() {
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Call<DeptRes> leaveTypeResCall = apiInterface.doGetDepartment();
                leaveTypeResCall.enqueue(new Callback<DeptRes>() {
                    @Override
                    public void onResponse(@NonNull Call<DeptRes> call,@NonNull Response<DeptRes> response) {
                        if (response.code() == 200) {
                            assert response.body() != null;
                            if (response.body().getStatusCode().equals("00")) {
                                if (response.body().getReturnValue()!=null &&
                                        response.body().getReturnValue().size()>0){
                                    deptList = response.body().getReturnValue();

                                    sp_dept.setAdapter(new CustomAdapter(deptList));

                                }else {
                                    Log.e(TAG,"No Leave Type Data");
                                }
                            }else {
                                Log.e(TAG,"No Leave Type Data");
                            }
                        }else {
                            Log.e(TAG,"Server Error");
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<DeptRes> call,@NonNull Throwable t) {
                        Log.e(TAG,"Throwable "+t.getMessage());
                    }
                });
            }catch (NullPointerException | NumberFormatException e){
                Log.e(TAG,"NullPointerException "+e.getMessage());
            }catch (Exception e){
                Log.e(TAG,"Exception "+e.getMessage());
            }
            return null;
        }
    }

    public class CustomAdapter extends BaseAdapter {

        List<DeptDetails> leaveTypeDetails;
        LayoutInflater inflter;

        private CustomAdapter(List<DeptDetails> leaveTypeDetails) {
            this.leaveTypeDetails = leaveTypeDetails;
            inflter = (LayoutInflater.from(mActivity));
        }

        @Override
        public int getCount() {
            return leaveTypeDetails.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.adapter_spinner, null);
            TextView tv_leave_type = view.findViewById(R.id.tv_leave_type);
            tv_leave_type.setText(leaveTypeDetails.get(i).getDepartment());
            return view;
        }
    }

    public class QueryType extends AsyncTask<String, String, String> implements GlobalData {

        private QueryType() {
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Call<QueryTypeRes> leaveTypeResCall = apiInterface.doQueryType();
                leaveTypeResCall.enqueue(new Callback<QueryTypeRes>() {
                    @Override
                    public void onResponse(@NonNull Call<QueryTypeRes> call,@NonNull Response<QueryTypeRes> response) {
                        if (response.code() == 200) {
                            assert response.body() != null;
                            if (response.body().getStatusCode().equals("00")) {
                                if (response.body().getReturnValue()!=null &&
                                        response.body().getReturnValue().size()>0){
                                    queryTypeDetailsList = response.body().getReturnValue();

                                    sp_query_type.setAdapter(new CustomAdapterQueryType(queryTypeDetailsList));

                                }else {
                                    Log.e(TAG,"No Leave Type Data");
                                }
                            }else {
                                Log.e(TAG,"No Leave Type Data");
                            }
                        }else {
                            Log.e(TAG,"Server Error");
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<QueryTypeRes> call,@NonNull Throwable t) {
                        Log.e(TAG,"Throwable "+t.getMessage());
                    }
                });
            }catch (NullPointerException | NumberFormatException e){
                Log.e(TAG,"NullPointerException "+e.getMessage());
            }catch (Exception e){
                Log.e(TAG,"Exception "+e.getMessage());
            }
            return null;
        }
    }

    public class CustomAdapterQueryType extends BaseAdapter {

        List<QueryTypeDetails> leaveTypeDetails;
        LayoutInflater inflter;

        private CustomAdapterQueryType(List<QueryTypeDetails> leaveTypeDetails) {
            this.leaveTypeDetails = leaveTypeDetails;
            inflter = (LayoutInflater.from(mActivity));
        }

        @Override
        public int getCount() {
            return leaveTypeDetails.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.adapter_spinner, null);
            TextView tv_leave_type = view.findViewById(R.id.tv_leave_type);
            tv_leave_type.setText(leaveTypeDetails.get(i).getIssue());
            return view;
        }
    }
}
