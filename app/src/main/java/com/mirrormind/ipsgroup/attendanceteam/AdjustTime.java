package com.mirrormind.ipsgroup.attendanceteam;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mirrormind.ipsgroup.Dialog.DialogsUtils;
import com.mirrormind.ipsgroup.R;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit.ApiClient;
import retrofit.ApiInterface;
import retrofit.response.ApplyLeaveRes;
import retrofit.response.adjuest.AdjustList;
import retrofit.response.adjuest.AdjustListRes;
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
import static uihelper.DateFormat.parseUpdateServer;

public class AdjustTime extends AppCompatActivity implements View.OnClickListener, GlobalData {

    public static final String TAG = AdjustTime.class.getSimpleName();
    TextView tv_time_in_icon,tv_in_time,tv_time_out_icon,tv_out_time,
            tv_apply,tv_back_icon,tv_search_icon,tv_back_search;
    EditText et_reason,et_search;
    RecyclerView rv_form;
    LinearLayout ll_header,ll_search;
    ProgressDialog myDialog;
    JSONObject allItems = new JSONObject();
    ApiInterface apiInterface;
    Activity mActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.act_adjust_time);

        mActivity = this;
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        tv_back_icon = findViewById(R.id.tv_back_icon);
        tv_time_in_icon = findViewById(R.id.tv_time_in_icon);
        tv_in_time = findViewById(R.id.tv_in_time);
        tv_time_out_icon = findViewById(R.id.tv_time_out_icon);
        tv_out_time = findViewById(R.id.tv_out_time);
        tv_apply = findViewById(R.id.tv_apply);
        et_reason = findViewById(R.id.et_reason);
        rv_form = findViewById(R.id.rv_form);
        tv_search_icon = findViewById(R.id.tv_search_icon);
        tv_back_search = findViewById(R.id.tv_back_search);
        ll_header = findViewById(R.id.ll_header);
        ll_search = findViewById(R.id.ll_search);
        et_search = findViewById(R.id.et_search);
        new OnKeyboardHide(this,et_reason);

        Icomoon.imageLogo.apply(this,tv_back_icon);
        Icomoon.imageLogo.apply(this,tv_search_icon);
        Icomoon.imageLogo.apply(this,tv_time_in_icon);
        Icomoon.imageLogo.apply(this,tv_time_out_icon);
        Icomoon.imageLogo.apply(this,tv_back_search);

        findViewById(R.id.ll_in_time).setOnClickListener(this);
        findViewById(R.id.ll_out_time).setOnClickListener(this);
        findViewById(R.id.ll_header).setOnClickListener(this);
        findViewById(R.id.ll_search).setOnClickListener(this);
        findViewById(R.id.tv_apply).setOnClickListener(this);
        tv_back_icon.setOnClickListener(this);
        ll_header.setOnClickListener(this);
        ll_search.setOnClickListener(this);
        tv_back_search.setOnClickListener(this);
        tv_search_icon.setOnClickListener(this);

        rv_form.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,
                false));

        if (SharedPreference.getDefaults(mActivity,TAG_IN_TIME)!=null &&
                !SharedPreference.getDefaults(mActivity,TAG_IN_TIME).isEmpty() &&
                !SharedPreference.getDefaults(mActivity,TAG_IN_TIME).equals("")){
            tv_in_time.setText(SharedPreference.getDefaults(mActivity,TAG_IN_TIME));
        }else {
            tv_in_time.setText("-");
        }

        if (SharedPreference.getDefaults(mActivity,TAG_OUT_TIME)!=null &&
                !SharedPreference.getDefaults(mActivity,TAG_OUT_TIME).isEmpty() &&
                !SharedPreference.getDefaults(mActivity,TAG_OUT_TIME).equals("")){
            tv_out_time.setText(SharedPreference.getDefaults(mActivity,TAG_OUT_TIME));
        }else {
            tv_out_time.setText("-");
        }

        doCallAdjustList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back_icon:
                onBackPressed();
                break;
            case R.id.tv_back_search:
                et_search.setText("");
                ll_header.setVisibility(View.VISIBLE);
                ll_search.setVisibility(View.GONE);
                break;
            case R.id.tv_search_icon:
                ll_header.setVisibility(View.GONE);
                ll_search.setVisibility(View.VISIBLE);
                break;
            case R.id.ll_in_time:
                new OnTimePicker(this,tv_in_time);
                break;
            case R.id.ll_out_time:
                new OnTimePicker(this,tv_out_time);
                break;
            case R.id.tv_apply:
                new OnKeyboardHide(this,v);
                doAdjustTime();
                break;
        }
    }

    private void doAdjustTime() {

        if (tv_in_time.getText().toString().equals("") || tv_in_time.getText().toString().equals("-") ||
                tv_in_time.getText().toString().equals("00:00") || tv_in_time.getText().toString().isEmpty()){
            new SnackbarIps(et_reason,"Select Clock IN Time");
        }else if (tv_out_time.getText().toString().equals("") || tv_out_time.getText().toString().equals("-") ||
                tv_out_time.getText().toString().equals("00:00") || tv_out_time.getText().toString().isEmpty()){
            new SnackbarIps(et_reason,"Select Clock Out Time");
        }else if (et_reason.getText().toString().equals("") ||
                et_reason.getText().toString().isEmpty()) {
            new SnackbarIps(et_reason,"Enter Reason");
        }else {
            myDialog = DialogsUtils.showProgressDialog(this, "Update Adjust Attendance");
            myDialog.show();
            myDialog.setCanceledOnTouchOutside(true);
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String currentDate = df.format(c);
            try {

                allItems.put("EmployeeID",SharedPreference.getDefaults(this,TAG_EMP_ID));
                allItems.put("AttID",SharedPreference.getDefaults(this,TAG_ATT_ID));
                allItems.put("AdjAttClockIn",tv_in_time.getText().toString());
                allItems.put("AdjAttClockOut",tv_out_time.getText().toString());
                allItems.put("ReqDate",currentDate);
                allItems.put("Comment",et_reason.getText().toString());

                JsonParser jsonParser = new JsonParser();
                JsonObject jsonObject = (JsonObject) jsonParser.parse(String.valueOf(allItems));
                Log.e("allItems ", allItems.toString());

                Call<ApplyLeaveRes> applyLeaveResCall = apiInterface.doSaveAdjustAttendance(jsonObject);

                applyLeaveResCall.enqueue(new Callback<ApplyLeaveRes>() {
                    @Override
                    public void onResponse(@NonNull Call<ApplyLeaveRes> call, @NonNull Response<ApplyLeaveRes> response) {
                        if (response.code() == 200){
                            assert response.body() != null;
                            if (response.body().getStatusCode().equals("00")) {
                                if (myDialog!=null && myDialog.isShowing()) {
                                    myDialog.dismiss();
                                }
                                doCallAdjustList();
                                new SnackbarIps(et_reason,""+response.body().getStatusDescription());
                            }else {
                                new SnackbarIps(et_reason,""+response.body().getStatusDescription());
                            }
                        }else {
                            new SnackbarIps(et_reason,"Server Error");
                        }
                        if (myDialog!=null && myDialog.isShowing()) {
                            myDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApplyLeaveRes> call,@NonNull Throwable t) {
                        new SnackbarIps(et_reason,"Internet or Server Error");
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
                new SnackbarIps(et_reason,"Internet or Server Error");
            }catch (Exception e){
                if (myDialog!=null && myDialog.isShowing()){
                    myDialog.dismiss();
                }
                Log.e(TAG,"Exception e "+e.getMessage());
                new SnackbarIps(et_reason,"Internet or Server Error");
            }
        }
    }

    private void doCallAdjustList() {

        myDialog = DialogsUtils.showProgressDialog(this, "Adjust Attendance");
        myDialog.show();

        Call<AdjustList> doAdjustAtt = apiInterface.doAdjustAtt(SharedPreference.getDefaults(this,TAG_EMP_ID));
        doAdjustAtt.enqueue(new Callback<AdjustList>() {
            @Override
            public void onResponse(@NonNull Call<AdjustList> call,@NonNull Response<AdjustList> response) {
                if (response.code() == 200){
                    if (response.body().getStatusCode().equals("00")) {
                        if (response.body().getReturnValue().size()>0){
                            rv_form.setAdapter(new AdapterAdjust(response.body().getReturnValue()));
                        }
                        if (myDialog!=null && myDialog.isShowing()) {
                            myDialog.dismiss();
                        }
                    }else {
                        if (myDialog!=null && myDialog.isShowing()) {
                            myDialog.dismiss();
                        }
                    }
                }else {
                    if (myDialog!=null && myDialog.isShowing()) {
                        myDialog.dismiss();
                    }
                    new SnackbarIps(tv_back_icon,"Server Error");
                }

                if (myDialog!=null && myDialog.isShowing()) {
                    myDialog.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AdjustList> call,@NonNull Throwable t) {
                new SnackbarIps(tv_back_icon,"Internet or Server Error");
                if (myDialog!=null && myDialog.isShowing()) {
                    myDialog.dismiss();
                }
            }
        });
    }

    private class AdapterAdjust extends RecyclerView.Adapter<AdapterAdjust.MyViewHolder> {

        List<AdjustListRes> returnValue;

        public AdapterAdjust(List<AdjustListRes> returnValue) {
            this.returnValue = returnValue;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.adapter_adjust_time,
                    parent,false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

            if (returnValue.get(position).getEmpFullName()!=null &&
                    !returnValue.get(position).getEmpFullName().equals("") &&
                    !returnValue.get(position).getEmpFullName().isEmpty()){
                holder.tv_apply_date.setText(returnValue.get(position).getEmpFullName());
            }else {
                holder.tv_apply_date.setText("-");
            }
            if (returnValue.get(position).getInTime()!=null &&
                    !returnValue.get(position).getInTime().equals("") &&
                    !returnValue.get(position).getInTime().isEmpty()){
                holder.tv_from_date.setText(returnValue.get(position).getInTime());
            }else {
                holder.tv_from_date.setText("-");
            }
            if (returnValue.get(position).getOutTime()!=null &&
                    !returnValue.get(position).getOutTime().equals("") &&
                    !returnValue.get(position).getOutTime().isEmpty()){
                holder.tv_to_date.setText(returnValue.get(position).getOutTime());
            }else {
                holder.tv_to_date.setText("-");
            }

            if (returnValue.get(position).getReqDate()!=null &&
                    !returnValue.get(position).getReqDate().equals("") &&
                    !returnValue.get(position).getReqDate().isEmpty()){
                holder.tv_total_days.setText(returnValue.get(position).getReqDate());
            }else {
                holder.tv_total_days.setText("-");
            }

            if (returnValue.get(position).getApprovalRemark()!=null &&
                    !returnValue.get(position).getApprovalRemark().equals("") &&
                    !returnValue.get(position).getApprovalRemark().isEmpty()){
                holder.tv_approval_cmt.setText(returnValue.get(position).getApprovalRemark());
            }else {
                holder.tv_approval_cmt.setText("-");
            }

            if (returnValue.get(position).getComment()!=null &&
                    !returnValue.get(position).getComment().equals("") &&
                    !returnValue.get(position).getComment().isEmpty()) {
                holder.tv_leave_type.setText(returnValue.get(position).getComment());
            }else {
                holder.tv_leave_type.setText("-");
            }
            
            if (returnValue.get(position).getAttStatus()!=null &&
                    !returnValue.get(position).getAttStatus().equals("") &&
                    !returnValue.get(position).getAttStatus().isEmpty()){
                holder.tv_status_type.setText(returnValue.get(position).getAttStatus());
                if (returnValue.get(position).getAttStatus().equals("Pending")) {
                    holder.tv_status_type.setBackgroundColor(getResources().getColor(R.color.pending));
                }else if (returnValue.get(position).getAttStatus().equals("Reject")){
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
            return returnValue.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView tv_apply_date,tv_total_days,tv_to_date,tv_from_date,tv_approval_cmt,tv_down_icon,
                    tv_status_type,tv_leave_type;

            public MyViewHolder(View itemView) {
                super(itemView);

                tv_apply_date = itemView.findViewById(R.id.tv_apply_date);
                tv_total_days = itemView.findViewById(R.id.tv_total_days);
                tv_to_date = itemView.findViewById(R.id.tv_to_date);
                tv_from_date = itemView.findViewById(R.id.tv_from_date);
                tv_approval_cmt = itemView.findViewById(R.id.tv_approval_cmt);
                tv_down_icon = itemView.findViewById(R.id.tv_down_icon);
                tv_status_type = itemView.findViewById(R.id.tv_status_type);
                tv_leave_type = itemView.findViewById(R.id.tv_leave_type);

                Icomoon.imageLogo.apply(mActivity,tv_down_icon);

            }
        }
    }
}
