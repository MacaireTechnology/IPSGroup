package com.mirrormind.ipsgroup.attendanceteam;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
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
import net.cachapa.expandablelayout.ExpandableLayout;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import retrofit.ApiClient;
import retrofit.ApiInterface;
import retrofit.response.ApplyLeaveRes;
import retrofit.response.approval_attend.ApprovalAttendDetails;
import retrofit.response.leave_approval.LeaveApprovalDetails;
import retrofit.response.leave_approval.LeaveApprovalRes;
import retrofit.response.status_type.StatusTypeDetails;
import retrofit.response.status_type.StatusTypeRes;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uihelper.SnackbarIps;
import uihelper.icomoon.Icomoon;
import uihelper.onKeyboard.OnKeyboardHide;
import uihelper.sharedPref.GlobalData;
import uihelper.sharedPref.SharedPreference;

public class LeaveApproval extends AppCompatActivity implements View.OnClickListener, GlobalData {

    public static final String TAG = LeaveApproval.class.getSimpleName();
    private static final String[] LEAVE_PLAN = new String[]{"Select Plan","Planned", "UnPlanned"};
    TextView tv_back,tv_header_name,tv_no_data_found,tv_search_icon,tv_back_search;
    RecyclerView rv_attend;
    LeaveATAdapter leaveATAdapter;
    String selectPlan = "Select Plan";
    JSONObject allItems = new JSONObject();
    Activity mActivity;
    ApiInterface apiInterface;
    ProgressDialog myDialog;
    List<StatusTypeDetails> statusTypeDetails = new ArrayList<>();
    LinearLayout ll_header,ll_search;
    EditText et_search;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_attendance_approval);

        mActivity = this;
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        tv_back = findViewById(R.id.tv_back);
        tv_header_name = findViewById(R.id.tv_header_name);
        rv_attend = findViewById(R.id.rv_attend);
        tv_no_data_found = findViewById(R.id.tv_no_data_found);
        ll_header = findViewById(R.id.ll_header);
        ll_search = findViewById(R.id.ll_search);
        et_search = findViewById(R.id.et_search);
        tv_search_icon = findViewById(R.id.tv_search_icon);
        tv_back_search = findViewById(R.id.tv_back_search);

        tv_header_name.setText(getResources().getString(R.string.leave_approval));

        rv_attend.setLayoutManager(new GridLayoutManager(mActivity, 1));
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (leaveATAdapter!=null)
                    leaveATAdapter.getFilter().filter(charSequence);
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (leaveATAdapter!=null)
                    leaveATAdapter.getFilter().filter(charSequence);
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        rv_attend.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                new OnKeyboardHide(LeaveApproval.this,et_search);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return false;
            }
        });

        tv_no_data_found.setVisibility(View.GONE);
        tv_back.setOnClickListener(this);

        findViewById(R.id.ll_header).setOnClickListener(this);
        findViewById(R.id.ll_search).setOnClickListener(this);
        findViewById(R.id.tv_search_icon).setOnClickListener(this);
        findViewById(R.id.tv_back_search).setOnClickListener(this);

        Icomoon.imageLogo.apply(this, tv_back);
        Icomoon.imageLogo.apply(this, tv_search_icon);
        Icomoon.imageLogo.apply(this, tv_back_search);

        doCallAdjustAttend();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                super.onBackPressed();
                break;
            case R.id.tv_back_search:
                new OnKeyboardHide(LeaveApproval.this,et_search);
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
    private void doCallAdjustAttend() {

        myDialog = DialogsUtils.showProgressDialog(this, "Fetching Approval Attendance");
        myDialog.show();
        myDialog.setCanceledOnTouchOutside(true);

        try {
            Call<LeaveApprovalRes> approvalAttendResCall = apiInterface.doLeaveRequest(
                    SharedPreference.getDefaults(this,TAG_EMP_ID));

            approvalAttendResCall.enqueue(new Callback<LeaveApprovalRes>() {
                @Override
                public void onResponse(@NonNull Call<LeaveApprovalRes> call,@NonNull Response<LeaveApprovalRes> response) {
                    if (response.code() == 200){
                        assert response.body() != null;
                        if (response.body().getStatusCode().equals("00")){
                            if (response.body().getReturnValue()!= null && response.body().getReturnValue().size()>0){
                                tv_no_data_found.setVisibility(View.GONE);
                                rv_attend.setVisibility(View.VISIBLE);
                                rv_attend.setAdapter(leaveATAdapter = new LeaveATAdapter(response.body().getReturnValue()));
                            }else {
                                tv_no_data_found.setVisibility(View.VISIBLE);
                                rv_attend.setVisibility(View.GONE);
                                new SnackbarIps(rv_attend,"No Data Found");
                            }
                        }else {
                            new SnackbarIps(rv_attend,"No Data Found");
                        }
                    }else {
                        new SnackbarIps(rv_attend,"Server Error");
                    }

                    if (myDialog!=null && myDialog.isShowing()) {
                        myDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<LeaveApprovalRes> call,@NonNull Throwable t) {
                    new SnackbarIps(rv_attend,"Internet or Server Error");
                    if (myDialog!=null && myDialog.isShowing()) {
                        myDialog.dismiss();
                    }
                }
            });
        }catch (NumberFormatException | NullPointerException e){
            new SnackbarIps(rv_attend,"Internet or Server Error");
            if (myDialog!=null && myDialog.isShowing()) {
                myDialog.dismiss();
            }
        }catch (Exception e){
            new SnackbarIps(rv_attend,"Internet or Server Error");
            if (myDialog!=null && myDialog.isShowing()) {
                myDialog.dismiss();
            }
        }
    }

    private class LeaveATAdapter extends RecyclerView.Adapter<LeaveATAdapter.MyViewHolder>
            implements Filterable {

        List<LeaveApprovalDetails> returnValue;
        List<LeaveApprovalDetails> listSearchView;
        ArrayAdapter<String> planStatus;
        String selectStatusTypeId="";

        public LeaveATAdapter(List<LeaveApprovalDetails> returnValue) {
            this.returnValue = returnValue;
            this.listSearchView = returnValue;
            planStatus = new ArrayAdapter<String>(mActivity,android.R.layout.simple_spinner_item,LEAVE_PLAN);
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_leaveapproval_dashboard,
                    viewGroup, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

            if (returnValue.get(position).getLeaveReqDateTime()!= null &&
                    !returnValue.get(position).getLeaveReqDateTime().equals("") &&
                    !returnValue.get(position).getLeaveReqDateTime().isEmpty()){
                holder.tv_lv_date.setText(returnValue.get(position).getLeaveReqDateTime());
            }else {
                holder.tv_lv_date.setText("-");
            }

            if (returnValue.get(position).getEmpFullName()!= null &&
                    !returnValue.get(position).getEmpFullName().equals("") &&
                    !returnValue.get(position).getEmpFullName().isEmpty()){
                holder.tv_username.setText(returnValue.get(position).getEmpFullName());
            }else {
                holder.tv_username.setText("-");
            }

            if (returnValue.get(position).getComments()!= null &&
                    !returnValue.get(position).getComments().equals("") &&
                    !returnValue.get(position).getComments().isEmpty()){
                holder.tv_command.setText(returnValue.get(position).getComments());
            }else {
                holder.tv_command.setText("-");
            }

            if (returnValue.get(position).getFromDate()!= null &&
                    !returnValue.get(position).getFromDate().equals("") &&
                    !returnValue.get(position).getFromDate().isEmpty()){
                holder.tv_from_date.setText(returnValue.get(position).getFromDate());
            }else {
                holder.tv_from_date.setText("-");
            }

            if (returnValue.get(position).getToDate()!= null &&
                    !returnValue.get(position).getToDate().equals("") &&
                    !returnValue.get(position).getToDate().isEmpty()){
                holder.tv_to_date.setText(returnValue.get(position).getToDate());
            }else {
                holder.tv_to_date.setText("-");
            }

            if (returnValue.get(position).getNoOfWorkingDay()!= null &&
                    !returnValue.get(position).getNoOfWorkingDay().equals("") &&
                    !returnValue.get(position).getNoOfWorkingDay().isEmpty()){
                if (returnValue.get(position).getNoOfWorkingDay().equals("1")){
                    holder.tv_day.setText("Day");
                }else {
                    holder.tv_day.setText("Days");
                }
                holder.tv_total_days.setText(returnValue.get(position).getNoOfWorkingDay());
            }else {
                holder.tv_total_days.setText("-");
            }

            if (returnValue.get(position).getLeaveType()!= null &&
                    !returnValue.get(position).getLeaveType().equals("") &&
                    !returnValue.get(position).getLeaveType().isEmpty()){
                holder.tv_leave_type.setText(returnValue.get(position).getLeaveType());
            }else {
                holder.tv_leave_type.setText("-");
            }

            if (returnValue.get(position).getApprovalComments()!= null &&
                    !returnValue.get(position).getApprovalComments().equals("") &&
                    !returnValue.get(position).getApprovalComments().isEmpty()){
                holder.tv_approval_cmt.setText(returnValue.get(position).getApprovalComments());
            }else {
                holder.tv_approval_cmt.setText("-");
            }

            if (holder.expandableLayout.isExpanded()) {
                holder.expandableLayout.collapse();
            }
            holder.ll_edit_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new OnKeyboardHide(LeaveApproval.this,et_search);
                    new OnKeyboardHide(LeaveApproval.this,holder.et_comments);

                    if (holder.expandableLayout.isExpanded()) {
                        holder.expandableLayout.collapse();

                        holder.tv_from_date.setOnClickListener(null);
                        holder.tv_to_date.setOnClickListener(null);

                        if (returnValue.get(holder.getAdapterPosition()).getFromDate()!=null &&
                                !returnValue.get(holder.getAdapterPosition()).getFromDate().equals("") &&
                                !returnValue.get(holder.getAdapterPosition()).getFromDate().isEmpty()) {
                            holder.tv_from_date.setText(returnValue.get(holder.getAdapterPosition()).getFromDate());
                        }else {
                            holder.tv_from_date.setText("-");
                        }

                        if (returnValue.get(holder.getAdapterPosition()).getToDate()!=null &&
                                !returnValue.get(holder.getAdapterPosition()).getToDate().equals("") &&
                                !returnValue.get(holder.getAdapterPosition()).getToDate().isEmpty()) {
                            holder.tv_to_date.setText(returnValue.get(holder.getAdapterPosition()).getToDate());
                        }else {
                            holder.tv_to_date.setText("-");
                        }

                    } else {
                        leaveATAdapter.notifyDataSetChanged();
                        if (customAdapter!=null)
                            customAdapter.notifyDataSetChanged();
                        planStatus.notifyDataSetChanged();

                        planStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        holder.spinner_plan_un_plan.setAdapter(planStatus);

                        holder.spinner_plan_un_plan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Log.e(TAG,"STATUS[position]--------> "+LEAVE_PLAN[position]);
                                selectPlan = LEAVE_PLAN[position];
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        holder.spinner_status_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                        holder.expandableLayout.expand();
                    }
                }
            });

            holder.tv_leave_approval.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new OnKeyboardHide(LeaveApproval.this,holder.et_comments);

                    if (selectPlan.equals("Select Plan")){
                        new SnackbarIps(holder.tv_leave_approval,"Select Plan");
                    }else if (selectStatusTypeId!=null && selectStatusTypeId.equals("") && selectStatusTypeId.isEmpty()){
                        new SnackbarIps(holder.et_comments,"Select Status");
                    }else if (holder.et_comments.getText().toString().equals("") ||
                            holder.et_comments.getText().toString().isEmpty()){
                        new SnackbarIps(holder.et_comments,"Enter Comments");
                    }else {
                        myDialog = DialogsUtils.showProgressDialog(mActivity, "Update Leave Approval");
                        myDialog.show();
                        myDialog.setCanceledOnTouchOutside(true);
                        try {
                            allItems.put("LeaveID",returnValue.get(holder.getAdapterPosition()).getLeaveID());
                            if (selectPlan.equals("Planned")){
                                allItems.put("IsPlanned",1);
                            }else if (selectPlan.equals("UnPlanned")) {
                                allItems.put("IsPlanned",2);
                            }
                            allItems.put("ApprovedBy",SharedPreference.getDefaults(mActivity,TAG_EMP_ID));
                            allItems.put("ApprovalComments",holder.et_comments.getText().toString());
                            allItems.put("IsApproved",selectStatusTypeId);

                            JsonParser jsonParser = new JsonParser();
                            JsonObject jsonObject = (JsonObject) jsonParser.parse(String.valueOf(allItems));
                            Log.e("allItems ", allItems.toString());

                            Call<ApplyLeaveRes> applyLeaveResCall = apiInterface.doLeaveReq(jsonObject);

                            applyLeaveResCall.enqueue(new Callback<ApplyLeaveRes>() {
                                @Override
                                public void onResponse(@NonNull Call<ApplyLeaveRes> call,@NonNull Response<ApplyLeaveRes> response) {
                                    if (response.code() == 200){
                                        assert response.body() != null;
                                        if (response.body().getStatusCode().equals("00")) {
                                            if (myDialog!=null && myDialog.isShowing()) {
                                                myDialog.dismiss();
                                            }
//                                            returnValue.get(holder.getAdapterPosition()).setApprovalComments(holder.et_comments.getText().toString());
                                            doCallAdjustAttend();
                                            new SnackbarIps(holder.tv_leave_approval,""+response.body().getStatusDescription());
                                        }else {
                                            new SnackbarIps(holder.tv_leave_approval,""+response.body().getStatusDescription());
                                        }
                                    }else {
                                        new SnackbarIps(holder.tv_leave_approval,"Server Error");
                                    }
                                    if (myDialog!=null && myDialog.isShowing()) {
                                        myDialog.dismiss();
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<ApplyLeaveRes> call,@NonNull Throwable t) {
                                    new SnackbarIps(holder.tv_leave_approval,"Internet or Server Error");
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
                            new SnackbarIps(holder.tv_leave_approval,"Internet or Server Error");
                        }catch (Exception e){
                            if (myDialog!=null && myDialog.isShowing()){
                                myDialog.dismiss();
                            }
                            Log.e(TAG,"Exception e "+e.getMessage());
                            new SnackbarIps(holder.tv_leave_approval,"Internet or Server Error");
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

            TextView tv_command_icon,tv_user_icon,tv_username,tv_command,tv_edit_user,
                    tv_lv_date,tv_leave_approval,tv_from_date,tv_to_date,tv_total_days,
                    tv_leave_type,tv_approval_cmt,tv_status_type,tv_down_icon,tv_day;
            LinearLayout ll_edit_user;
            EditText et_comments;
            ExpandableLayout expandableLayout;
            Spinner spinner_plan_un_plan,spinner_status_type;

            private MyViewHolder(View itemView) {
                super(itemView);

                expandableLayout = itemView.findViewById(R.id.expandable_layout_1);
                spinner_plan_un_plan = itemView.findViewById(R.id.spinner_plan_un_plan);
                spinner_status_type = itemView.findViewById(R.id.spinner_status_type);
                tv_status_type = itemView.findViewById(R.id.tv_status_type);
                et_comments = itemView.findViewById(R.id.et_comments);
                tv_leave_approval = itemView.findViewById(R.id.tv_leave_approval);
                tv_command_icon = itemView.findViewById(R.id.tv_command_icon);
                tv_user_icon = itemView.findViewById(R.id.tv_user_icon);
                tv_username = itemView.findViewById(R.id.tv_username);
                tv_command = itemView.findViewById(R.id.tv_command);
                tv_edit_user = itemView.findViewById(R.id.tv_edit_user);
                ll_edit_user = itemView.findViewById(R.id.ll_edit_user);
                tv_lv_date = itemView.findViewById(R.id.tv_lv_date);
                tv_from_date = itemView.findViewById(R.id.tv_from_date);
                tv_to_date = itemView.findViewById(R.id.tv_to_date);
                tv_total_days = itemView.findViewById(R.id.tv_total_days);
                tv_leave_type = itemView.findViewById(R.id.tv_leave_type);
                tv_approval_cmt = itemView.findViewById(R.id.tv_approval_cmt);
                tv_down_icon = itemView.findViewById(R.id.tv_down_icon);
                tv_day = itemView.findViewById(R.id.tv_day);

                Icomoon.imageLogo.apply(mActivity,tv_command_icon);
                Icomoon.imageLogo.apply(mActivity,tv_user_icon);
                Icomoon.imageLogo.apply(mActivity,tv_edit_user);
                Icomoon.imageLogo.apply(mActivity,tv_down_icon);

                new StatusType(spinner_status_type).execute();
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
                        List<LeaveApprovalDetails> filteredList = new ArrayList<>();
                        for (LeaveApprovalDetails row : listSearchView) {

                            if ((row.getEmpFullName()!=null && row.getEmpFullName().toLowerCase().contains(charString.toLowerCase())) ||
                                    (row.getLeaveReqDateTime()!=null && row.getLeaveReqDateTime().toLowerCase().contains(charString.toLowerCase())) ||
                                    (row.getFromDate()!=null && row.getFromDate().toLowerCase().contains(charString.toLowerCase())) ||
                                    (row.getNoOfWorkingDay()!=null && row.getNoOfWorkingDay().toLowerCase().contains(charString.toLowerCase())) ||
                                    (row.getToDate()!=null && row.getToDate().toLowerCase().contains(charString.toLowerCase())) ||
                                    (row.getLeaveType()!=null && row.getLeaveType().toLowerCase().contains(charString.toLowerCase())) ||
                                    (row.getComments()!=null && row.getComments().toLowerCase().contains(charString.toLowerCase()))) {
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
                    returnValue = (ArrayList<LeaveApprovalDetails>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }
    }

    CustomAdapter customAdapter;

    public class StatusType extends AsyncTask<String, String, String> implements GlobalData {

        Spinner spinner;
        private StatusType(Spinner spinner) {
            this.spinner = spinner;
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Call<StatusTypeRes> leaveTypeResCall = apiInterface.doStatusType("attendance");
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
