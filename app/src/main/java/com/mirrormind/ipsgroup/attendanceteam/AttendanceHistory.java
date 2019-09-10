package com.mirrormind.ipsgroup.attendanceteam;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mirrormind.ipsgroup.Dialog.DialogsUtils;
import com.mirrormind.ipsgroup.R;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.ApiClient;
import retrofit.ApiInterface;
import retrofit.response.ClockinRes;
import retrofit.response.ReturnValue;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uihelper.SnackbarIps;
import uihelper.icomoon.Icomoon;
import uihelper.sharedPref.GlobalData;
import uihelper.sharedPref.SharedPreference;

public class AttendanceHistory extends AppCompatActivity implements GlobalData, View.OnClickListener {

    RecyclerView rv_attendancehistory;
    ApiInterface apiInterface;
    TextView tv_no_data_found,tv_back_icon,tv_calendar_icon,tv_calendar,
            tv_user_icon,tv_user,tv_calendar_icon1;
    ProgressDialog myDialog;
    Calendar myCalendar = Calendar.getInstance();
    ClockedIn_details.ClockedinAdapter clockedInAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendancehistory);

        tv_back_icon = findViewById(R.id.tv_back_icon);
        tv_calendar_icon = findViewById(R.id.tv_calendar_icon);
        tv_user_icon = findViewById(R.id.tv_user_icon);
        tv_user = findViewById(R.id.tv_user);
        tv_calendar = findViewById(R.id.tv_calendar);
        tv_calendar_icon1 = findViewById(R.id.tv_calendar_icon1);
        tv_no_data_found = findViewById(R.id.tv_no_data_found);
        rv_attendancehistory = findViewById(R.id.rv_attendancehistory);

        rv_attendancehistory.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        findViewById(R.id.ll_calender).setOnClickListener(this);
        findViewById(R.id.ll_calender1).setOnClickListener(this);
        findViewById(R.id.tv_back_icon).setOnClickListener(this);

        Icomoon.imageLogo.apply(this,tv_back_icon);
        Icomoon.imageLogo.apply(this,tv_calendar_icon);
        Icomoon.imageLogo.apply(this,tv_user_icon);
        Icomoon.imageLogo.apply(this,tv_calendar_icon1);

        if (SharedPreference.getDefaults(this,TAG_USER_NAME)!=null &&
                !SharedPreference.getDefaults(this,TAG_USER_NAME).isEmpty() &&
                !SharedPreference.getDefaults(this,TAG_USER_NAME).equals("")){
            tv_user.setText(SharedPreference.getDefaults(this,TAG_USER_NAME));
        }else {
            tv_user.setText("-");
        }

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = df.format(c);

        new ClockedIn_details(this,apiInterface,rv_attendancehistory,
                currentDate).execute();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back_icon:
                onBackPressed();
                break;
            case R.id.ll_calender:
            case R.id.ll_calender1:
                datePicker();
                break;
        }
    }
    private void datePicker() {
        Calendar mCurrentDate = Calendar.getInstance();
        int mYear = mCurrentDate.get(Calendar.YEAR);
        int mMonth = mCurrentDate.get(Calendar.MONTH);
        int mDay = mCurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                myCalendar.set(Calendar.YEAR, selectedyear);
                myCalendar.set(Calendar.MONTH, selectedmonth);
                myCalendar.set(Calendar.DAY_OF_MONTH, selectedday);
                updateLabel();
            }
        }, mYear, mMonth, mDay);
        mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
        mDatePicker.setTitle("Select date");

        mDatePicker.show();
    }
    private void updateLabel() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        tv_calendar.setText(sdf.format(myCalendar.getTime()));

        new ClockedIn_details(this,apiInterface,rv_attendancehistory,
                sdf.format(myCalendar.getTime())).execute();
    }
    private class ClockedIn_details extends AsyncTask<String, String, String> {

        private Activity mActivity;
        private ApiInterface apiInterface;
        private RecyclerView rv_list_clockinout;
        private String currentDate;

        private ClockedIn_details(Activity activity, ApiInterface apiInterface, RecyclerView rv_list_clockinout,
                                  String currentDate) {
            this.mActivity = activity;
            this.apiInterface = apiInterface;
            this.rv_list_clockinout = rv_list_clockinout;
            this.currentDate = currentDate;
            tv_calendar.setText(currentDate);
            myDialog = DialogsUtils.showProgressDialog(activity, "Fetching Attendance History");
            myDialog.show();
            myDialog.setCanceledOnTouchOutside(true);
        }


        @Override
        protected String doInBackground(String... params) {
            try {
                Call<ClockinRes> dogetattdetails = apiInterface.dogetattdetails(SharedPreference.getDefaults(mActivity,TAG_EMP_ID),
                        currentDate);

                dogetattdetails.enqueue(new Callback<ClockinRes>() {
                    @Override
                    public void onResponse(@NonNull Call<ClockinRes> call,@NonNull Response<ClockinRes> response) {
                        if (response.code() == 200) {
                            assert response.body() != null;
                            if (response.body().getStatusCode().equals("00")) {

                                if (response.body().getReturnValue().size() > 0) {
                                    tv_no_data_found.setVisibility(View.GONE);
                                    rv_list_clockinout.setVisibility(View.VISIBLE);

                                    rv_list_clockinout.setAdapter(clockedInAdapter = new ClockedinAdapter(
                                            mActivity, response.body().getReturnValue()));
                                }else {
                                    rv_list_clockinout.setVisibility(View.GONE);
                                    tv_no_data_found.setVisibility(View.VISIBLE);
                                }
                            } else {
                                new SnackbarIps(tv_no_data_found, "" + response.body().getStatusDescription(), 2000);
                            }
                        } else {
                            new SnackbarIps(tv_no_data_found, "Server Error", 2000);
                        }

                        if (myDialog!=null && myDialog.isShowing()){
                            myDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ClockinRes> call,@NonNull Throwable t) {
                        if (myDialog!=null && myDialog.isShowing()){
                            myDialog.dismiss();
                        }
                        new SnackbarIps(tv_no_data_found, "Network or Server Error", 2000);

                    }
                });
            } catch (Exception e) {
                if (myDialog!=null && myDialog.isShowing()){
                    myDialog.dismiss();
                }
                e.printStackTrace();

            }
            return null;
        }

        private class ClockedinAdapter extends RecyclerView.Adapter<ClockedinAdapter.MyViewHolder> {

            Activity mActivity;
            List<ReturnValue> clockenin_deatils;

            public ClockedinAdapter(Activity mActivity, List<ReturnValue> clockenin_deatils) {
                this.mActivity = mActivity;
                this.clockenin_deatils = clockenin_deatils;
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(mActivity).inflate(R.layout.adpt_clockedin,
                        parent, false);
                return new MyViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

                if (clockenin_deatils.get(position).getClockInDT()!= null &&
                        !clockenin_deatils.get(position).getClockInDT().equals("") &&
                        !clockenin_deatils.get(position).getClockInDT().isEmpty()) {
                    holder.tv_time_in.setText(clockenin_deatils.get(position).getClockInDT());
                } else {
                    holder.tv_time_in.setText("-");
                }
                if (clockenin_deatils.get(position).getClockOutDT()!= null &&
                        !clockenin_deatils.get(position).getClockOutDT().equals("") &&
                        !clockenin_deatils.get(position).getClockOutDT().isEmpty()) {
                    holder.tv_time_out.setText(clockenin_deatils.get(position).getClockOutDT());
                } else {
                    holder.tv_time_out.setText("-");
                }
                if (clockenin_deatils.get(position).getClockInLocaction()!= null &&
                        !clockenin_deatils.get(position).getClockInLocaction().equals("") &&
                        !clockenin_deatils.get(position).getClockInLocaction().isEmpty()) {
                    holder.tv_clock_in_loc.setText(clockenin_deatils.get(position).getClockInLocaction());
                } else {
                    holder.tv_clock_in_loc.setText("-");
                }
                if (clockenin_deatils.get(position).getClockOutLocation()!= null &&
                        !clockenin_deatils.get(position).getClockOutLocation().equals("") &&
                        !clockenin_deatils.get(position).getClockOutLocation().isEmpty()) {
                    holder.tv_clock_out_loc.setText(clockenin_deatils.get(position).getClockOutLocation());
                } else {
                    holder.tv_clock_out_loc.setText("-");
                }

                holder.tv_adjust_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (clockenin_deatils.get(holder.getAdapterPosition()).getAttID()!=null &&
                                !clockenin_deatils.get(holder.getAdapterPosition()).getAttID().isEmpty() &&
                                !clockenin_deatils.get(holder.getAdapterPosition()).getAttID().equals("")){
                            SharedPreference.setDefaults(mActivity,TAG_ATT_ID,clockenin_deatils.get(holder.getAdapterPosition()).getAttID());
                        }else {
                            SharedPreference.setDefaults(mActivity,TAG_ATT_ID,"-1");
                        }
                        if (clockenin_deatils.get(holder.getAdapterPosition()).getClockInDT()!=null &&
                                !clockenin_deatils.get(holder.getAdapterPosition()).getClockInDT().isEmpty() &&
                                !clockenin_deatils.get(holder.getAdapterPosition()).getClockInDT().equals("")){
                            SharedPreference.setDefaults(mActivity,TAG_IN_TIME,clockenin_deatils.get(holder.getAdapterPosition()).getClockInDT());
                        }else {
                            SharedPreference.setDefaults(mActivity,TAG_IN_TIME,"00:00:00");
                        }
                        if (clockenin_deatils.get(holder.getAdapterPosition()).getClockOutDT()!=null &&
                                !clockenin_deatils.get(holder.getAdapterPosition()).getClockOutDT().isEmpty() &&
                                !clockenin_deatils.get(holder.getAdapterPosition()).getClockOutDT().equals("")){
                            SharedPreference.setDefaults(mActivity,TAG_OUT_TIME,clockenin_deatils.get(holder.getAdapterPosition()).getClockOutDT());
                        }else {
                            SharedPreference.setDefaults(mActivity,TAG_OUT_TIME,"00:00:00");
                        }
                        startActivity(new Intent(mActivity,AdjustTime.class));
                    }
                });

                if (clockenin_deatils.get(position).getClockInIMG()!= null &&
                        !clockenin_deatils.get(position).getClockInIMG().equals("") &&
                        !clockenin_deatils.get(position).getClockInIMG().isEmpty()) {
                    Glide.with(mActivity).load(clockenin_deatils.get(position).getClockInIMG())
                            .placeholder(R.drawable.placeholder_small)
                            .error(R.drawable.placeholder_small)
                            .dontAnimate().centerCrop().into(holder.civ_in_time);
                } else {
                    Glide.with(mActivity).load(R.drawable.placeholder_small)
                            .placeholder(R.drawable.placeholder_small)
                            .error(R.drawable.placeholder_small)
                            .dontAnimate().centerCrop().into(holder.civ_in_time);
                }

                if (clockenin_deatils.get(position).getClockOutIMG()!= null &&
                        !clockenin_deatils.get(position).getClockOutIMG().equals("") &&
                        !clockenin_deatils.get(position).getClockOutIMG().isEmpty()) {
                    Glide.with(mActivity).load(clockenin_deatils.get(position).getClockOutIMG())
                            .placeholder(R.drawable.placeholder_small)
                            .error(R.drawable.placeholder_small)
                            .dontAnimate().centerCrop().into(holder.civ_out_time);
                } else {
                    Glide.with(mActivity).load(R.drawable.placeholder_small)
                            .placeholder(R.drawable.placeholder_small)
                            .error(R.drawable.placeholder_small)
                            .dontAnimate().centerCrop().into(holder.civ_out_time);
                }
            }

            @Override
            public int getItemCount() {
                return clockenin_deatils.size();
            }

            public class MyViewHolder extends RecyclerView.ViewHolder {

                TextView tv_time_in_icon,tv_time_in,tv_time_out_icon,tv_time_out,tv_time_in_loc,
                        tv_clock_in_loc_icon,tv_clock_in_loc,tv_time_out_loc,tv_clock_out_loc_icon,
                        tv_clock_out_loc,tv_adjust_icon;
                CircleImageView civ_in_time,civ_out_time;

                public MyViewHolder(View itemView) {
                    super(itemView);

                    tv_time_in_icon = itemView.findViewById(R.id.tv_time_in_icon);
                    tv_time_in = itemView.findViewById(R.id.tv_time_in);
                    tv_time_out_icon = itemView.findViewById(R.id.tv_time_out_icon);
                    tv_time_out = itemView.findViewById(R.id.tv_time_out);
                    tv_time_in_loc = itemView.findViewById(R.id.tv_time_in_loc);
                    tv_clock_in_loc_icon = itemView.findViewById(R.id.tv_clock_in_loc_icon);
                    tv_clock_in_loc = itemView.findViewById(R.id.tv_clock_in_loc);
                    tv_time_out_loc = itemView.findViewById(R.id.tv_time_out_loc);
                    tv_clock_out_loc_icon = itemView.findViewById(R.id.tv_clock_out_loc_icon);
                    tv_clock_out_loc = itemView.findViewById(R.id.tv_clock_out_loc);
                    tv_adjust_icon = itemView.findViewById(R.id.tv_adjust_icon);
                    civ_in_time = itemView.findViewById(R.id.civ_in_time);
                    civ_out_time = itemView.findViewById(R.id.civ_out_time);

                    Icomoon.imageLogo.apply(mActivity,tv_time_in_icon);
                    Icomoon.imageLogo.apply(mActivity,tv_time_out_icon);
                    Icomoon.imageLogo.apply(mActivity,tv_clock_in_loc_icon);
                    Icomoon.imageLogo.apply(mActivity,tv_clock_out_loc_icon);
                    Icomoon.imageLogo.apply(mActivity,tv_time_out_loc);
                    Icomoon.imageLogo.apply(mActivity,tv_time_in_loc);
                    Icomoon.imageLogo.apply(mActivity,tv_adjust_icon);
                }
            }
        }
    }
}
