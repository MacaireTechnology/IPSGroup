package com.mirrormind.ipsgroup.attendanceteam;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
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
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mirrormind.ipsgroup.Dialog.DialogsUtils;
import com.mirrormind.ipsgroup.R;
import net.cachapa.expandablelayout.ExpandableLayout;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import retrofit.ApiClient;
import retrofit.ApiInterface;
import retrofit.response.ApplyLeaveRes;
import retrofit.response.approval_attend.ApprovalAttendDetails;
import retrofit.response.approval_attend.ApprovalAttendRes;
import retrofit.response.status_type.StatusTypeDetails;
import retrofit.response.status_type.StatusTypeRes;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uihelper.SnackbarIps;
import uihelper.icomoon.Icomoon;
import uihelper.onKeyboard.OnKeyboardHide;
import uihelper.picker.OnTimePicker;
import uihelper.sharedPref.GlobalData;
import uihelper.sharedPref.SharedPreference;
import static uihelper.DateFormat.parseDateToday;
import static uihelper.DateFormat.parseUpdateServer;

public class AttendanceApproval extends AppCompatActivity implements View.OnClickListener, GlobalData {

    public static final String TAG = AttendanceApproval.class.getSimpleName();

    TextView tv_back,tv_back_search,tv_search_icon,tv_no_data_found;
    RecyclerView rv_attend;
    AttendanceATAdapter attendanceATAdapter;
    ApiInterface apiInterface;
    JSONObject allItems = new JSONObject();
    Activity mActivity;
    ProgressDialog myDialog;
    LinearLayout ll_header,ll_search;
    EditText et_search;
    List<StatusTypeDetails> statusTypeDetails = new ArrayList<>();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_approval);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mActivity = this;

        tv_back = findViewById(R.id.tv_back);
        tv_back_search = findViewById(R.id.tv_back_search);
        tv_search_icon = findViewById(R.id.tv_search_icon);
        tv_no_data_found = findViewById(R.id.tv_no_data_found);
        ll_header = findViewById(R.id.ll_header);
        ll_search = findViewById(R.id.ll_search);
        et_search = findViewById(R.id.et_search);

        Icomoon.imageLogo.apply(this, tv_back);
        Icomoon.imageLogo.apply(this, tv_back_search);
        Icomoon.imageLogo.apply(this, tv_search_icon);
        tv_no_data_found.setVisibility(View.GONE);

        tv_back.setOnClickListener(this);
        findViewById(R.id.ll_header).setOnClickListener(this);
        findViewById(R.id.ll_search).setOnClickListener(this);
        findViewById(R.id.tv_search_icon).setOnClickListener(this);
        findViewById(R.id.tv_back_search).setOnClickListener(this);

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

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        rv_attend = findViewById(R.id.rv_attend);
        rv_attend.setHasFixedSize(true);
        rv_attend.setNestedScrollingEnabled(true);
        rv_attend.setLayoutManager(new GridLayoutManager(mActivity, 1));
        rv_attend.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                new OnKeyboardHide(AttendanceApproval.this,et_search);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return false;
            }
        });

        doCallAdjustAttend();
    }

    private void doCallAdjustAttend() {

        myDialog = DialogsUtils.showProgressDialog(this, "Fetching Approval Attendance");
        myDialog.show();
        myDialog.setCanceledOnTouchOutside(true);

        try {
            Call<ApprovalAttendRes> approvalAttendResCall = apiInterface.doAdjustAttend(
                    SharedPreference.getDefaults(this,TAG_EMP_ID));

            approvalAttendResCall.enqueue(new Callback<ApprovalAttendRes>() {
                @Override
                public void onResponse(@NonNull Call<ApprovalAttendRes> call,@NonNull Response<ApprovalAttendRes> response) {
                    if (response.code() == 200){
                        assert response.body() != null;
                        if (response.body().getStatusCode().equals("00")){
                            if (response.body().getReturnValue()!= null && response.body().getReturnValue().size()>0){
                                tv_no_data_found.setVisibility(View.GONE);
                                rv_attend.setVisibility(View.VISIBLE);
                                rv_attend.setAdapter(attendanceATAdapter = new AttendanceATAdapter(response.body().getReturnValue()));
                            }else {
                                rv_attend.setVisibility(View.VISIBLE);
                                tv_no_data_found.setVisibility(View.VISIBLE);
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
                public void onFailure(@NonNull Call<ApprovalAttendRes> call,@NonNull Throwable t) {
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
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                super.onBackPressed();
                break;
            case R.id.tv_back_search:
                new OnKeyboardHide(this,et_search);
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
    public class AttendanceATAdapter extends RecyclerView.Adapter<AttendanceATAdapter.MyViewHolder>
            implements Filterable {

        List<ApprovalAttendDetails> listSearchView,returnValue;
        String selectStatusTypeId="";

        private AttendanceATAdapter(List<ApprovalAttendDetails> returnValue) {
            this.returnValue = returnValue;
            this.listSearchView = returnValue;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_attendanceapproval_dashboard,
                    viewGroup, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

            if (returnValue.get(position).getReqDate()!=null && !returnValue.get(position).getReqDate().equals("") &&
                    !returnValue.get(position).getReqDate().isEmpty()) {
                holder.tv_lv_date.setText(returnValue.get(position).getReqDate());
            }else {
                holder.tv_lv_date.setText("-");
            }

            if (returnValue.get(position).getEmpFullName()!=null && !returnValue.get(position).getEmpFullName().equals("") &&
                    !returnValue.get(position).getEmpFullName().isEmpty()) {
                holder.tv_username.setText(returnValue.get(position).getEmpFullName());
            }else {
                holder.tv_username.setText("-");
            }

            if (returnValue.get(position).getComment()!=null && !returnValue.get(position).getComment().equals("") &&
                    !returnValue.get(position).getComment().isEmpty()) {
                holder.tv_command.setText(returnValue.get(position).getComment());
            }else {
                holder.tv_command.setText("-");
            }

            if (returnValue.get(position).getInTime()!=null && !returnValue.get(position).getInTime().equals("") &&
                    !returnValue.get(position).getInTime().isEmpty()) {
                holder.tv_in_time.setText(parseDateToday(returnValue.get(position).getInTime(),""));
            }else {
                holder.tv_in_time.setText("-");
            }

            if (returnValue.get(position).getOutTime()!=null && !returnValue.get(position).getOutTime().equals("") &&
                    !returnValue.get(position).getOutTime().isEmpty()) {
                holder.tv_out_time.setText(parseDateToday(returnValue.get(position).getOutTime(),""));
            }else {
                holder.tv_out_time.setText("-");
            }
            if (holder.expandableLayout.isExpanded()) {
                holder.expandableLayout.collapse();
            }

            holder.ll_edit_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new OnKeyboardHide(AttendanceApproval.this,view);

                    if (holder.expandableLayout.isExpanded()) {
                        holder.expandableLayout.collapse();

                        holder.ll_in_time.setOnClickListener(null);
                        holder.ll_out_time.setOnClickListener(null);

                        if (returnValue.get(holder.getAdapterPosition()).getInTime()!=null &&
                                !returnValue.get(holder.getAdapterPosition()).getInTime().equals("") &&
                                !returnValue.get(holder.getAdapterPosition()).getInTime().isEmpty()) {
                            holder.tv_in_time.setText(parseDateToday(returnValue.get(holder.getAdapterPosition()).getInTime(),""));
                        }else {
                            holder.tv_in_time.setText("-");
                        }

                        if (returnValue.get(holder.getAdapterPosition()).getOutTime()!=null &&
                                !returnValue.get(holder.getAdapterPosition()).getOutTime().equals("") &&
                                !returnValue.get(holder.getAdapterPosition()).getOutTime().isEmpty()) {
                            holder.tv_out_time.setText(parseDateToday(returnValue.get(holder.getAdapterPosition()).getOutTime(),""));
                        }else {
                            holder.tv_out_time.setText("-");
                        }

                    } else {
                        attendanceATAdapter.notifyDataSetChanged();
                        if (customAdapter!=null)
                            customAdapter.notifyDataSetChanged();

                        holder.ll_in_time.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.e(TAG,"tv_out_time ");
                                new OnTimePicker(mActivity,holder.tv_in_time);
                            }
                        });
                        holder.ll_out_time.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.e(TAG,"tv_out_time ");
                                new OnTimePicker(mActivity,holder.tv_out_time);
                            }
                        });

                        holder.expandableLayout.expand();
                    }
                }
            });

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

                    new OnKeyboardHide(AttendanceApproval.this,holder.et_comment);

                    if (selectStatusTypeId!=null && selectStatusTypeId.equals("") && selectStatusTypeId.isEmpty()) {
                        new SnackbarIps(holder.et_comment,"Select Status");
                    } else if (holder.et_comment.getText().toString().equals("") ||
                            holder.et_comment.getText().toString().isEmpty()){
                        new SnackbarIps(holder.et_comment,"Enter Comments");
                    } else {
                        myDialog = DialogsUtils.showProgressDialog(mActivity, "Update Leave Approval");
                        myDialog.show();
                        myDialog.setCanceledOnTouchOutside(true);
                        try {
                            allItems.put("RecID",returnValue.get(holder.getAdapterPosition()).getRecID());
                            allItems.put("AttID",returnValue.get(holder.getAdapterPosition()).getAttID());
                            allItems.put("AdjAttClockIn",parseUpdateServer(holder.tv_in_time.getText().toString()));
                            allItems.put("AdjAttClockOut",parseUpdateServer(holder.tv_out_time.getText().toString()));
                            allItems.put("ApprovedBy",SharedPreference.getDefaults(mActivity,TAG_EMP_ID));
                            allItems.put("ApprovalRemark",holder.et_comment.getText().toString());
                            allItems.put("AttStatus",selectStatusTypeId);
                            JsonParser jsonParser = new JsonParser();
                            JsonObject jsonObject = (JsonObject) jsonParser.parse(String.valueOf(allItems));
                            Log.e("allItems ", allItems.toString());

                            Call<ApplyLeaveRes> applyLeaveResCall = apiInterface.doUpdateLeave(jsonObject);
                            applyLeaveResCall.enqueue(new Callback<ApplyLeaveRes>() {
                                @Override
                                public void onResponse(@NonNull Call<ApplyLeaveRes> call,@NonNull Response<ApplyLeaveRes> response) {
                                    if (response.code() == 200){
                                        assert response.body() != null;
                                        if (response.body().getStatusCode().equals("00")) {
                                            returnValue.get(holder.getAdapterPosition()).setInTime(holder.tv_in_time.getText().toString()+":00");
                                            returnValue.get(holder.getAdapterPosition()).setOutTime(holder.tv_out_time.getText().toString()+":00");
                                            attendanceATAdapter.notifyDataSetChanged();

                                            new SnackbarIps(holder.et_comment,""+response.body().getStatusDescription());
                                        }else {
                                            new SnackbarIps(holder.et_comment,""+response.body().getStatusDescription());
                                        }
                                    }else {
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
        public class MyViewHolder extends RecyclerView.ViewHolder {

            ExpandableLayout expandableLayout;
            EditText et_comment;
            TextView tv_user_icon,tv_username,tv_command_icon,tv_command,tv_edit_user,
                    tv_lv_date,tv_in_time,tv_out_time,tv_update_leave,tv_status_type,
                    tv_time_in_icon,tv_time_out_icon;
            Spinner spinner_leave_type;
            LinearLayout ll_edit_user,ll_in_time,ll_out_time;

            public MyViewHolder(View itemView) {
                super(itemView);

                expandableLayout = itemView.findViewById(R.id.expandable_layout_1);
                tv_user_icon = itemView.findViewById(R.id.tv_user_icon);
                tv_username = itemView.findViewById(R.id.tv_username);
                tv_command_icon = itemView.findViewById(R.id.tv_command_icon);
                tv_command = itemView.findViewById(R.id.tv_command);
                tv_lv_date = itemView.findViewById(R.id.tv_lv_date);
                tv_edit_user = itemView.findViewById(R.id.tv_edit_user);
                tv_in_time = itemView.findViewById(R.id.tv_in_time);
                tv_out_time = itemView.findViewById(R.id.tv_out_time);
                spinner_leave_type = itemView.findViewById(R.id.spinner_leave_type);
                ll_edit_user = itemView.findViewById(R.id.ll_edit_user);
                tv_update_leave = itemView.findViewById(R.id.tv_update_leave);
                et_comment = itemView.findViewById(R.id.et_comment);
                tv_status_type = itemView.findViewById(R.id.tv_status_type);
                tv_time_in_icon = itemView.findViewById(R.id.tv_time_in_icon);
                tv_time_out_icon = itemView.findViewById(R.id.tv_time_out_icon);
                ll_in_time = itemView.findViewById(R.id.ll_in_time);
                ll_out_time = itemView.findViewById(R.id.ll_out_time);

                Icomoon.imageLogo.apply(mActivity,tv_edit_user);
                Icomoon.imageLogo.apply(mActivity,tv_user_icon);
                Icomoon.imageLogo.apply(mActivity,tv_command_icon);
                Icomoon.imageLogo.apply(mActivity,tv_time_in_icon);
                Icomoon.imageLogo.apply(mActivity,tv_time_out_icon);

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
                        List<ApprovalAttendDetails> filteredList = new ArrayList<>();
                        for (ApprovalAttendDetails row : listSearchView) {

                            if ((row.getEmpFullName()!=null && row.getEmpFullName().toLowerCase().contains(charString.toLowerCase())) ||
                                    (row.getReqDate()!=null && row.getReqDate().toLowerCase().contains(charString.toLowerCase())) ||
                                    (row.getComment()!=null && row.getComment().toLowerCase().contains(charString.toLowerCase()))) {
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
                    returnValue = (ArrayList<ApprovalAttendDetails>) filterResults.values;
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
