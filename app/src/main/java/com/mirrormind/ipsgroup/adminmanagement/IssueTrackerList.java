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
import android.widget.AutoCompleteTextView;
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
import com.mirrormind.ipsgroup.attendanceteam.AttendanceApproval;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import retrofit.ApiClient;
import retrofit.ApiInterface;
import retrofit.response.ApplyLeaveRes;
import retrofit.response.contact.ContactDetails;
import retrofit.response.issueApproval.IssueApprovalListDetails;
import retrofit.response.issueApproval.IssueApprovalListRes;
import retrofit.response.status_type.StatusTypeDetails;
import retrofit.response.status_type.StatusTypeRes;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uihelper.Interface.RecyclerClick;
import uihelper.SnackbarIps;
import uihelper.icomoon.Icomoon;
import uihelper.onKeyboard.OnKeyboardHide;
import uihelper.picker.OnTimePicker;
import uihelper.sharedPref.GlobalData;
import uihelper.sharedPref.SharedPreference;

import static uihelper.DateFormat.parseDateToday;
import static uihelper.DateFormat.parseUpdateServer;

public class IssueTrackerList extends AppCompatActivity implements View.OnClickListener,
        GlobalData {

    private static final String TAG = IssueTrackerList.class.getSimpleName();
    TextView tv_back,tv_header_name,tv_no_data_found,
            tv_search_icon,tv_back_search;
    LinearLayout ll_header,ll_search;
    EditText et_search;
    RecyclerView rv_attend;
    AttendanceATAdapter attendanceATAdapter;
    Activity mActivity;
    ApiInterface apiInterface;
    List<StatusTypeDetails> statusTypeDetails = new ArrayList<>();
    CustomAdapter customAdapter;
    ProgressDialog myDialog;
    JSONObject allItems = new JSONObject();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_approval);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mActivity = this;
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        tv_back = findViewById(R.id.tv_back);
        tv_header_name = findViewById(R.id.tv_header_name);
        tv_no_data_found = findViewById(R.id.tv_no_data_found);
        tv_search_icon = findViewById(R.id.tv_search_icon);
        ll_header = findViewById(R.id.ll_header);
        ll_search = findViewById(R.id.ll_search);
        et_search = findViewById(R.id.et_search);
        tv_back_search = findViewById(R.id.tv_back_search);
        rv_attend = findViewById(R.id.rv_attend);

        tv_back.setOnClickListener(this);
        findViewById(R.id.ll_header).setOnClickListener(this);
        findViewById(R.id.ll_search).setOnClickListener(this);
        findViewById(R.id.tv_search_icon).setOnClickListener(this);
        findViewById(R.id.tv_back_search).setOnClickListener(this);

        Icomoon.imageLogo.apply(mActivity, tv_back);
        Icomoon.imageLogo.apply(mActivity, tv_search_icon);
        Icomoon.imageLogo.apply(mActivity, tv_back_search);

        tv_header_name.setText(getResources().getString(R.string.issue_tracker_list));
        tv_no_data_found.setVisibility(View.GONE);
        rv_attend.setLayoutManager(new GridLayoutManager(mActivity, 1));

        doListIssue();

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (attendanceATAdapter!=null)
                    attendanceATAdapter.getFilter().filter(charSequence);
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (attendanceATAdapter!=null)
                    attendanceATAdapter.getFilter().filter(charSequence);
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void doListIssue() {
        myDialog = DialogsUtils.showProgressDialog(this, "Issue Tracker List");
        myDialog.show();

        Call<IssueApprovalListRes> approvalListResCall = apiInterface.doGetIssueList(
                SharedPreference.getDefaults(mActivity,TAG_EMP_ID));

        approvalListResCall.enqueue(new Callback<IssueApprovalListRes>() {
            @Override
            public void onResponse(@NonNull Call<IssueApprovalListRes> call,@NonNull Response<IssueApprovalListRes> response) {
                if (response.code() == 200){
                    if (response.body().getStatusCode()!=null && response.body().getStatusCode().equals("00")){
                        tv_no_data_found.setVisibility(View.GONE);
                        if (response.body().getReturnValue().size()>0){
                            tv_no_data_found.setVisibility(View.GONE);
                            if (myDialog!=null && myDialog.isShowing()) {
                                myDialog.dismiss();
                            }
                            rv_attend.setAdapter(attendanceATAdapter = new AttendanceATAdapter(response.body().getReturnValue()));
                        }else {
                            if (myDialog!=null && myDialog.isShowing()) {
                                myDialog.dismiss();
                            }
                            tv_no_data_found.setVisibility(View.GONE);
                        }
                    }else {
                        if (myDialog!=null && myDialog.isShowing()) {
                            myDialog.dismiss();
                        }
                        tv_no_data_found.setVisibility(View.VISIBLE);
                    }
                }else {
                    if (myDialog!=null && myDialog.isShowing()) {
                        myDialog.dismiss();
                    }
                    new SnackbarIps(tv_back,"Server Error");
                }
            }

            @Override
            public void onFailure(@NonNull Call<IssueApprovalListRes> call,@NonNull Throwable t) {
                new SnackbarIps(tv_back,"Internet or Server Error");
                if (myDialog!=null && myDialog.isShowing()) {
                    myDialog.dismiss();
                }
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                super.onBackPressed();
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

    private class AttendanceATAdapter extends RecyclerView.Adapter<AttendanceATAdapter.MyViewHolder>
            implements Filterable {

        List<IssueApprovalListDetails> returnValue,listSearchView;
        String selectStatusTypeId="";

        private AttendanceATAdapter(List<IssueApprovalListDetails> returnValue) {
            this.returnValue = returnValue;
            this.listSearchView = returnValue;

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
            if (returnValue.get(position).getEmpFullName()!=null &&
                    !returnValue.get(position).getEmpFullName().equals("") &&
                    !returnValue.get(position).getEmpFullName().isEmpty()){
                holder.tv_emp_name.setText(returnValue.get(position).getEmpFullName());
            }else {
                holder.tv_emp_name.setText("-");
            }

            if (returnValue.get(position).getCreateDate()!=null &&
                    !returnValue.get(position).getCreateDate().equals("") &&
                    !returnValue.get(position).getCreateDate().isEmpty()){
                holder.tv_apply_date.setText(returnValue.get(position).getCreateDate());
            }else {
                holder.tv_apply_date.setText("-");
            }

            if (returnValue.get(position).getDepartment()!=null &&
                    !returnValue.get(position).getDepartment().equals("") &&
                    !returnValue.get(position).getDepartment().isEmpty()){
                holder.tv_department.setText(returnValue.get(position).getDepartment());
            }else {
                holder.tv_department.setText("-");
            }

            if (returnValue.get(position).getStatusID()!=null &&
                    !returnValue.get(position).getStatusID().equals("") &&
                    !returnValue.get(position).getStatusID().isEmpty()){
                holder.tv_status.setText(returnValue.get(position).getStatusID());
            }else {
                holder.tv_status.setText("-");
            }

            if (returnValue.get(position).getIssue()!=null &&
                    !returnValue.get(position).getIssue().equals("") &&
                    !returnValue.get(position).getIssue().isEmpty()){
                holder.tv_issue.setText(returnValue.get(position).getIssue());
            }else {
                holder.tv_issue.setText("-");
            }

            if (returnValue.get(position).getDescription()!=null &&
                    !returnValue.get(position).getDescription().equals("") &&
                    !returnValue.get(position).getDescription().isEmpty()){
                holder.tv_description.setText(returnValue.get(position).getDescription());
            }else {
                holder.tv_description.setText("-");
            }

            holder.tv_edit_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new OnKeyboardHide(mActivity,view);

                    if (holder.expandableLayout.isExpanded()) {
                        holder.expandableLayout.collapse();
                        et_search.setText("");
                    } else {
                        attendanceATAdapter.notifyDataSetChanged();
                        if (customAdapter!=null)
                            customAdapter.notifyDataSetChanged();

                        holder.expandableLayout.expand();
                    }
                }
            });
            if (holder.expandableLayout.isExpanded()) {
                holder.expandableLayout.collapse();
            }

            holder.spinner_leave_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if (statusTypeDetails!=null && statusTypeDetails.size()>0) {
                        selectStatusTypeId = statusTypeDetails.get(position).getStatusID();
                        Log.e(TAG,"getLeaveID "+statusTypeDetails.get(position).getStatusCode());
                        holder.tv_status_type.setText(statusTypeDetails.get(position).getStatusCode());
                    }else {
                        new SnackbarIps(holder.tv_status_type,"No Status Type Available");
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            holder.tv_update_leave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new OnKeyboardHide(mActivity,holder.et_comment);

                    if (selectStatusTypeId!=null && selectStatusTypeId.equals("") && selectStatusTypeId.isEmpty()){
                        new SnackbarIps(holder.et_comment,"Select Status");
                    }else if (holder.et_comment.getText().toString().equals("") ||
                            holder.et_comment.getText().toString().isEmpty()){
                        new SnackbarIps(holder.et_comment,"Enter Comments");
                    }else {
                        myDialog = DialogsUtils.showProgressDialog(mActivity, "Update Issue Tracker List");
                        myDialog.show();
                        myDialog.setCanceledOnTouchOutside(true);

                        try {
                            allItems.put("IssueTrackerID",returnValue.get(holder.getAdapterPosition()).getIssueTrackerID());
                            allItems.put("IssueModifiyDate",returnValue.get(holder.getAdapterPosition()).getCreateDate());
                            allItems.put("IssueModifiyID",SharedPreference.getDefaults(mActivity,TAG_EMP_ID));
                            allItems.put("IssueApporvalComment",holder.et_comment.getText().toString());
                            allItems.put("StatusID",selectStatusTypeId);
                            JsonParser jsonParser = new JsonParser();
                            JsonObject jsonObject = (JsonObject) jsonParser.parse(String.valueOf(allItems));
                            Log.e("allItems ", allItems.toString());

                            Call<ApplyLeaveRes> applyLeaveResCall = apiInterface.doSaveIssueTrack(jsonObject);

                            applyLeaveResCall.enqueue(new Callback<ApplyLeaveRes>() {
                                @Override
                                public void onResponse(@NonNull Call<ApplyLeaveRes> call,@NonNull Response<ApplyLeaveRes> response) {
                                    if (response.code() == 200){
                                        assert response.body() != null;
                                        if (response.body().getStatusCode().equals("00")) {
                                            if (myDialog!=null && myDialog.isShowing()) {
                                                myDialog.dismiss();
                                            }
                                            doListIssue();
                                            new SnackbarIps(holder.et_comment,""+response.body().getStatusDescription());
                                        }else {
                                            if (myDialog!=null && myDialog.isShowing()) {
                                                myDialog.dismiss();
                                            }
                                            new SnackbarIps(holder.et_comment,""+response.body().getStatusDescription());
                                        }
                                    }else {
                                        if (myDialog!=null && myDialog.isShowing()) {
                                            myDialog.dismiss();
                                        }
                                        new SnackbarIps(holder.et_comment,"Server Error");
                                    }
                                    if (myDialog!=null && myDialog.isShowing()) {
                                        myDialog.dismiss();
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<ApplyLeaveRes> call,@NonNull Throwable t) {
                                    new SnackbarIps(holder.et_comment,"Internet or Server Error");
                                    Log.e(TAG,"Throwable e "+t.getMessage());

                                    if (myDialog!=null && myDialog.isShowing()) {
                                        myDialog.dismiss();
                                    }
                                }
                            });

                        }catch (NullPointerException | NumberFormatException e){
                            if (myDialog!=null && myDialog.isShowing()) {
                                myDialog.dismiss();
                            }
                            Log.e(TAG,"NullPointerException e "+e.getMessage());
                            new SnackbarIps(holder.et_comment,"Internet or Server Error");
                        }catch (Exception e){
                            if (myDialog!=null && myDialog.isShowing()){
                                myDialog.dismiss();
                            }
                            Log.e(TAG,"Exception e "+e.getMessage());
                            new SnackbarIps(holder.et_comment,"Internet or Server Error");
                        }
                    }
                }
            });
        }
        @Override
        public int getItemCount() {
            return returnValue.size();
        }
        private class MyViewHolder extends RecyclerView.ViewHolder {

            ExpandableLayout expandableLayout;
            EditText et_comment;
            TextView tv_emp_name,tv_apply_date,tv_department,tv_status,
                    tv_issue,tv_description,tv_status_type,tv_update_leave;
            TextView tv_edit_user;
            Spinner spinner_leave_type;

            private MyViewHolder(View itemView) {
                super(itemView);

                tv_edit_user = itemView.findViewById(R.id.tv_edit_user);
                tv_emp_name = itemView.findViewById(R.id.tv_emp_name);
                tv_apply_date = itemView.findViewById(R.id.tv_apply_date);
                tv_department = itemView.findViewById(R.id.tv_department);
                tv_status = itemView.findViewById(R.id.tv_status);
                tv_status_type = itemView.findViewById(R.id.tv_status_type);
                tv_issue = itemView.findViewById(R.id.tv_issue);
                tv_description = itemView.findViewById(R.id.tv_description);
                spinner_leave_type = itemView.findViewById(R.id.spinner_leave_type);
                expandableLayout = itemView.findViewById(R.id.expandable_layout_1);
                et_comment = itemView.findViewById(R.id.et_comment);
                tv_update_leave = itemView.findViewById(R.id.tv_update_leave);

                Icomoon.imageLogo.apply(mActivity,tv_edit_user);
                new StatusType(spinner_leave_type).execute();
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
                        List<IssueApprovalListDetails> filteredList = new ArrayList<>();
                        for (IssueApprovalListDetails row : listSearchView) {

                            if ((row.getEmpFullName()!=null && row.getEmpFullName().toLowerCase().contains(charString.toLowerCase())) ||
                                    (row.getDepartment()!=null && row.getDepartment().toLowerCase().contains(charString.toLowerCase())) ||
                                    (row.getStatusID()!=null && row.getStatusID().toLowerCase().contains(charString.toLowerCase())) ||
                                    (row.getIssue()!=null && row.getIssue().toLowerCase().contains(charString.toLowerCase())) ||
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
                    returnValue = (ArrayList<IssueApprovalListDetails>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }
    }

    public class StatusType extends AsyncTask<String, String, String> implements GlobalData {

        Spinner spinner;
        private StatusType(Spinner spinner) {
            this.spinner = spinner;
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Call<StatusTypeRes> leaveTypeResCall = apiInterface.doStatusType("issuetracker");
                leaveTypeResCall.enqueue(new Callback<StatusTypeRes>() {
                    @Override
                    public void onResponse(@NonNull Call<StatusTypeRes> call,@NonNull Response<StatusTypeRes> response) {
                        if (response.code() == 200) {
                            assert response.body() != null;
                            if (response.body().getStatusCode().equals("00") || response.body().getStatusCode().equals("-1")) {
                                if (response.body().getReturnValue()!=null &&
                                        response.body().getReturnValue().size()>0){
                                    statusTypeDetails = response.body().getReturnValue();

                                    spinner.setAdapter(customAdapter = new CustomAdapter(statusTypeDetails));

                                }else {
                                    Log.e(TAG,"No Status Type Data");
                                }
                            }else {
                                Log.e(TAG,"No Status Type Data");
                            }
                        }else {
                            Log.e(TAG,"Server Error");
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<StatusTypeRes> call,@NonNull Throwable t) {
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

        List<StatusTypeDetails> leaveTypeDetails;
        LayoutInflater inflter;

        private CustomAdapter(List<StatusTypeDetails> leaveTypeDetails) {
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
            tv_leave_type.setText(leaveTypeDetails.get(i).getStatusCode());
            return view;
        }
    }
}
