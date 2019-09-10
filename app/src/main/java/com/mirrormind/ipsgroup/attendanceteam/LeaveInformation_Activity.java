package com.mirrormind.ipsgroup.attendanceteam;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mirrormind.ipsgroup.Dialog.DialogsUtils;
import com.mirrormind.ipsgroup.R;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit.ApiClient;
import retrofit.ApiInterface;
import retrofit.response.ApplyLeaveRes;
import retrofit.response.LeaveReqRes;
import retrofit.response.LeaveReqreturnvalue;
import retrofit.response.leaveType.LeaveTypeDetails;
import retrofit.response.leaveType.LeaveTypeRes;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uihelper.SnackbarIps;
import uihelper.icomoon.Icomoon;
import uihelper.onKeyboard.OnKeyboardHide;
import uihelper.picker.OnCurrentDay;
import uihelper.sharedPref.GlobalData;
import uihelper.sharedPref.SharedPreference;

public class LeaveInformation_Activity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = LeaveInformation_Activity.class.getSimpleName();
    Activity mActivity;
    RecyclerView rv_leave_info;
    TextView tv_back,tv_to_date,tv_from_date,tv_apply_leave, tv_leave_type;
    Spinner spinner_leave_type;
    ExpandableLayout expandableLayout;
    Button btn_apply;
    JSONObject allItems = new JSONObject();
    ApiInterface apiInterface;
    ProgressDialog myDialog;
    EditText ed_app_leave;
    final Calendar myCalendar = Calendar.getInstance();
    List<LeaveTypeDetails> leaveTypeDetails = new ArrayList<>();
    String fromdate, todate, comments,selectLeaveTypeId="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        mActivity = this;

        rv_leave_info = findViewById(R.id.rv_leave_info);
        spinner_leave_type = findViewById(R.id.spinner_leave_type);
        tv_leave_type = findViewById(R.id.tv_leave_type);
        expandableLayout = findViewById(R.id.expandable_layout_1);
        btn_apply = findViewById(R.id.btn_apply);
        tv_back = findViewById(R.id.tv_back);
        tv_from_date = findViewById(R.id.tv_from_date);
        tv_to_date = findViewById(R.id.tv_to_date);
        ed_app_leave = findViewById(R.id.ed_app_leave);
        tv_apply_leave = findViewById(R.id.tv_apply_leave);

        Icomoon.imageLogo.apply(this, tv_back);

        btn_apply.setOnClickListener(this);
        expandableLayout.setOnClickListener(this);
        tv_back.setOnClickListener(this);
        tv_from_date.setOnClickListener(this);
        tv_to_date.setOnClickListener(this);
        ed_app_leave.setOnClickListener(this);
        tv_apply_leave.setOnClickListener(this);

        tv_from_date.setText(OnCurrentDay.getDateTime());
        tv_to_date.setText(OnCurrentDay.getDateTime());

        spinner_leave_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (leaveTypeDetails!=null && leaveTypeDetails.size()>0) {
                    selectLeaveTypeId = leaveTypeDetails.get(position).getLeaveID();
                    Log.e(TAG,"getLeaveID "+leaveTypeDetails.get(position).getDescription());
                    tv_leave_type.setText(leaveTypeDetails.get(position).getDescription());
                }else {
                    new SnackbarIps(spinner_leave_type,"No Leave Type Available");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        new LeaveType().execute();
    }

    protected void onResume() {
        super.onResume();
        try {
            updateleavestatus();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private void updateleavestatus() {
        doCallLeaveReq();
        myDialog = DialogsUtils.showProgressDialog(LeaveInformation_Activity.this, "Refreshing");
        myDialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_apply:
                if (this.expandableLayout.isExpanded()) {
                    this.expandableLayout.collapse();
                } else {
                    this.expandableLayout.expand();
                }
                break;
            case R.id.tv_from_date:
                datePicker(1);
                break;
            case R.id.tv_to_date:
                datePicker(2);
                break;
            case R.id.tv_apply_leave:
                new OnKeyboardHide(this,view);
                doLoginCondition();
                break;
            case R.id.tv_back:
                onBackPressed();
                break;

        }
    }

    private void doLoginCondition() {
        if (tv_from_date.getText().toString().equals("")) {
            new SnackbarIps(tv_apply_leave, "Please Select From Date");
        } else if (tv_to_date.getText().toString().equals("")) {
            new SnackbarIps(tv_apply_leave, "Please Select To Date");
        } else if (ed_app_leave.getText().toString().equals("")) {
            new SnackbarIps(tv_apply_leave, "Please Enter Leave Comments");
            Log.e("et_app_leave ", ed_app_leave.toString());
        } else {
            doCallAccessKey();
            myDialog = DialogsUtils.showProgressDialog(LeaveInformation_Activity.this, "Applying leave");
            myDialog.show();
        }
    }

    private void doCallAccessKey() {

        fromdate = tv_from_date.getText().toString();
        todate = tv_to_date.getText().toString();
        comments = ed_app_leave.getText().toString();

        try {
            allItems.put("EmployeeID", SharedPreference.getDefaults(getApplicationContext(), GlobalData.TAG_EMP_ID));
            allItems.put("LeaveType", selectLeaveTypeId);
            allItems.put("Commets", comments);
            allItems.put("FromDate", fromdate);
            allItems.put("ToDate", todate);
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = (JsonObject) jsonParser.parse(String.valueOf(allItems));
            Log.e("allItems ", allItems.toString());

            Call<ApplyLeaveRes> logResCall = apiInterface.doapplyleave(jsonObject);
            logResCall.enqueue(new Callback<ApplyLeaveRes>() {
                @Override
                public void onResponse(@NonNull Call<ApplyLeaveRes> call,@NonNull Response<ApplyLeaveRes> response) {
                    if (response.code() == 200) {
                        assert response.body() != null;
                        if (response.body().getStatusCode().equals("00")) {
                            myDialog.dismiss();
                            updateleavestatus();
                            if (expandableLayout.isExpanded()) {
                                expandableLayout.collapse();
                            }
                            ed_app_leave.setText("");
                            new SnackbarIps(tv_apply_leave, "" + "Applied Leave Successfully", 2000);
                        } else {
                            myDialog.dismiss();
                            new SnackbarIps(tv_apply_leave, "" + "Not a valid user", 2000);
                        }

                    } else {
                        myDialog.dismiss();
                        new SnackbarIps(tv_apply_leave, "Server Error", 2000);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ApplyLeaveRes> call,@NonNull Throwable t) {
                    myDialog.dismiss();
                    new SnackbarIps(tv_apply_leave, "Network Or Server Error", 2000);
                }
            });

        } catch (Exception e) {
            myDialog.dismiss();
        }
    }

    private void doCallLeaveReq() {

        Call<LeaveReqRes> leaveReqResCall = apiInterface.dorequestleavestatus(SharedPreference.getDefaults(this,
                GlobalData.TAG_EMP_ID));
//        Call<LeaveReqRes> leaveReqResCall = apiInterface.dorequestleavestatus("7");
        leaveReqResCall.enqueue(new Callback<LeaveReqRes>() {
            @Override
            public void onResponse(@NonNull Call<LeaveReqRes> call,@NonNull Response<LeaveReqRes> response) {
                Log.e("leavestatus", String.valueOf(response.code()));
                if (response.code() == 200) {
                    if (response.body().getStatusCode().equals("00")) {
                        if (response.body().getReturnValue().size() > 0) {
                            myDialog.dismiss();
                            rv_leave_info.setLayoutManager(new GridLayoutManager(mActivity, 1));
                            rv_leave_info.setAdapter(new LeaveAdapter(mActivity, response.body().getReturnValue()));
                            Log.e("leavestatus", response.body().getReturnValue().get(0).getStatus());
                        }else {
                            myDialog.dismiss();
                        }
                    } else {
                        myDialog.dismiss();
                        new SnackbarIps(tv_apply_leave, "" + "Not a valid user", 2000);
                    }
                } else {
                    myDialog.dismiss();
                    new SnackbarIps(tv_apply_leave, "Server Error", 2000);
                }
            }
            @Override
            public void onFailure(@NonNull Call<LeaveReqRes> call,@NonNull Throwable t) {
                myDialog.dismiss();;
                new SnackbarIps(tv_apply_leave, "Network Or Server Error", 2000);
            }
        });
    }

    private void datePicker(final int selectDay) {
        Calendar mcurrentDate = Calendar.getInstance();
        int mYear = mcurrentDate.get(Calendar.YEAR);
        int mMonth = mcurrentDate.get(Calendar.MONTH);
        int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker = new DatePickerDialog(LeaveInformation_Activity.this,
                new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                myCalendar.set(Calendar.YEAR, selectedyear);
                myCalendar.set(Calendar.MONTH, selectedmonth);
                myCalendar.set(Calendar.DAY_OF_MONTH, selectedday);
                String myFormat = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                if (selectDay == 2){
                    tv_to_date.setText(sdf.format(myCalendar.getTime()));
                }else {
                    tv_from_date.setText(sdf.format(myCalendar.getTime()));
                }
            }
        }, mYear, mMonth, mDay);
        mDatePicker.setTitle("Select date");

        mDatePicker.show();
    }

    private class LeaveAdapter extends RecyclerView.Adapter<LeaveAdapter.MyViewHolder> {

        Activity mActivity;
        List<LeaveReqreturnvalue> leavetext;

        private LeaveAdapter(Activity mActivity, List<LeaveReqreturnvalue> leavetext) {
            this.mActivity = mActivity;
            this.leavetext = leavetext;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_leave_info,
                    parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

            if (leavetext.get(position).getReqDate()!=null &&
                    !leavetext.get(position).getReqDate().equals("") &&
                    !leavetext.get(position).getReqDate().isEmpty()){
                holder.tv_apply_date.setText(leavetext.get(position).getReqDate());
            }else {
                holder.tv_apply_date.setText("-");
            }
            if (leavetext.get(position).getFrmDT()!=null &&
                    !leavetext.get(position).getFrmDT().equals("") &&
                    !leavetext.get(position).getFrmDT().isEmpty()){
                holder.tv_from_date.setText(leavetext.get(position).getFrmDT());
            }else {
                holder.tv_from_date.setText("-");
            }
            if (leavetext.get(position).getToDT()!=null &&
                    !leavetext.get(position).getToDT().equals("") &&
                    !leavetext.get(position).getToDT().isEmpty()){
                holder.tv_to_date.setText(leavetext.get(position).getToDT());
            }else {
                holder.tv_to_date.setText("-");
            }

            if (leavetext.get(position).getDays()!=null &&
                    !leavetext.get(position).getDays().equals("") &&
                    !leavetext.get(position).getDays().isEmpty()){
                holder.tv_total_days.setText(leavetext.get(position).getDays());
            }else {
                holder.tv_total_days.setText("-");
            }

            if (leavetext.get(position).getCmt()!=null &&
                    !leavetext.get(position).getCmt().equals("") &&
                    !leavetext.get(position).getCmt().isEmpty()){
                holder.tv_approval_cmt.setText(leavetext.get(position).getCmt());
            }else {
                holder.tv_approval_cmt.setText("-");
            }

            if (leavetext.get(position).getDescription()!=null &&
                    !leavetext.get(position).getDescription().equals("") &&
                    !leavetext.get(position).getDescription().isEmpty()){
                holder.tv_leave_type.setText(leavetext.get(position).getDescription());
            }else {
                holder.tv_leave_type.setText("-");
            }

            if (leavetext.get(position).getStatus()!=null &&
                    !leavetext.get(position).getStatus().equals("") &&
                    !leavetext.get(position).getStatus().isEmpty()){
                holder.tv_status_type.setText(leavetext.get(position).getStatus());
                if (leavetext.get(position).getStatus().equals("Pending")) {
                    holder.tv_status_type.setBackgroundColor(getResources().getColor(R.color.pending));
                }else if (leavetext.get(position).getStatus().equals("Reject")){
                    holder.tv_status_type.setBackgroundColor(getResources().getColor(R.color.red_medium));
                }else {
                    holder.tv_status_type.setBackgroundColor(getResources().getColor(R.color.green_));
                }
            }else {
                holder.tv_status_type.setText("-");
            }
        }

        @Override
        public int getItemCount() {
            return leavetext.size();
        }


        private class MyViewHolder extends RecyclerView.ViewHolder {

            TextView tv_apply_date,tv_total_days,tv_to_date,tv_from_date,tv_approval_cmt,tv_down_icon,
                    tv_leave_calendar,tv_status_type,tv_leave_type;

            private MyViewHolder(View itemView) {
                super(itemView);

                tv_apply_date = itemView.findViewById(R.id.tv_apply_date);
                tv_leave_calendar = itemView.findViewById(R.id.tv_leave_calendar);
                tv_total_days = itemView.findViewById(R.id.tv_total_days);
                tv_to_date = itemView.findViewById(R.id.tv_to_date);
                tv_from_date = itemView.findViewById(R.id.tv_from_date);
                tv_approval_cmt = itemView.findViewById(R.id.tv_approval_cmt);
                tv_down_icon = itemView.findViewById(R.id.tv_down_icon);
                tv_status_type = itemView.findViewById(R.id.tv_status_type);
                tv_leave_type = itemView.findViewById(R.id.tv_leave_type);

                Icomoon.imageLogo.apply(mActivity,tv_down_icon);
                Icomoon.imageLogo.apply(mActivity,tv_leave_calendar);

            }
        }
    }
    private class LeaveType extends AsyncTask<String, String, String> implements GlobalData {

        private LeaveType() {
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Call<LeaveTypeRes> leaveTypeResCall = apiInterface.doLeaveType();
                leaveTypeResCall.enqueue(new Callback<LeaveTypeRes>() {
                    @Override
                    public void onResponse(@NonNull Call<LeaveTypeRes> call,@NonNull Response<LeaveTypeRes> response) {
                        if (response.code() == 200) {
                            assert response.body() != null;
                            if (response.body().getStatusCode().equals("00")) {
                                if (response.body().getReturnValue()!=null &&
                                        response.body().getReturnValue().size()>0){
                                    leaveTypeDetails = response.body().getReturnValue();

                                    spinner_leave_type.setAdapter(new CustomAdapter(leaveTypeDetails));

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
                    public void onFailure(@NonNull Call<LeaveTypeRes> call,@NonNull Throwable t) {
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

        List<LeaveTypeDetails> leaveTypeDetails;
        LayoutInflater inflter;

        private CustomAdapter(List<LeaveTypeDetails> leaveTypeDetails) {
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
            tv_leave_type.setText(leaveTypeDetails.get(i).getDescription());
            return view;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
