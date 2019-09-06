package com.mirrormind.ipsgroup.attendanceteam;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mirrormind.ipsgroup.Dialog.DialogsUtils;
import com.mirrormind.ipsgroup.R;

import java.util.ArrayList;
import java.util.List;

import retrofit.ApiClient;
import retrofit.ApiInterface;
import retrofit.response.approval_attend.ApprovalAttendDetails;
import retrofit.response.team_attend.TeamAttendDetail;
import retrofit.response.team_attend.TeamAttendRes;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uihelper.SnackbarIps;
import uihelper.icomoon.Icomoon;
import uihelper.sharedPref.GlobalData;
import uihelper.sharedPref.SharedPreference;

public class TeamAttendance extends AppCompatActivity implements View.OnClickListener , GlobalData {

    Activity mActivity;
    TextView tv_back,tv_back_search,tv_search_icon,tv_no_data_found;
    RecyclerView rv_attend;
    TeamAttendanceAdapter teamAttendanceAdapter;
    ApiInterface apiInterface;
    ProgressDialog myDialog;

    LinearLayout ll_header,ll_search;
    EditText et_search;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_approval);

        tv_back = findViewById(R.id.tv_back);
        tv_no_data_found = findViewById(R.id.tv_no_data_found);
        tv_back_search = findViewById(R.id.tv_back_search);
        tv_search_icon = findViewById(R.id.tv_search_icon);
        ll_header = findViewById(R.id.ll_header);
        ll_search = findViewById(R.id.ll_search);
        et_search = findViewById(R.id.et_search);

        tv_no_data_found.setVisibility(View.GONE);
        tv_back.setOnClickListener(this);
        ll_header.setOnClickListener(this);
        ll_search.setOnClickListener(this);
        tv_back_search.setOnClickListener(this);
        tv_search_icon.setOnClickListener(this);
        Icomoon.imageLogo.apply(this, tv_back);
        Icomoon.imageLogo.apply(this, tv_back_search);
        Icomoon.imageLogo.apply(this, tv_search_icon);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        rv_attend = findViewById(R.id.rv_attend);
        rv_attend.setLayoutManager(new GridLayoutManager(mActivity, 1));

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (teamAttendanceAdapter!=null)
                    teamAttendanceAdapter.getFilter().filter(charSequence);
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (teamAttendanceAdapter!=null)
                    teamAttendanceAdapter.getFilter().filter(charSequence);
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        doTeamAttend();
    }

    private void doTeamAttend() {

        myDialog = DialogsUtils.showProgressDialog(this, "Fetching Team Attendance");
        myDialog.setCanceledOnTouchOutside(true);

        try {
            myDialog.show();

            Call<TeamAttendRes> teamAttendResCall = apiInterface.doTeamAttend(SharedPreference.getDefaults(this,TAG_EMP_ID));

            teamAttendResCall.enqueue(new Callback<TeamAttendRes>() {
                @Override
                public void onResponse(@NonNull Call<TeamAttendRes> call,@NonNull Response<TeamAttendRes> response) {
                    if (response.code() == 200){
                        if (response.body().getStatusCode().equals("00")){
                            if (response.body().getReturnValue()!= null && response.body().getReturnValue().size()>0) {
                                tv_no_data_found.setVisibility(View.GONE);
                                rv_attend.setAdapter(teamAttendanceAdapter = new TeamAttendanceAdapter(response.body().getReturnValue()));
                            }else {
                                tv_no_data_found.setVisibility(View.VISIBLE);
                            }
                        }else {
                            tv_no_data_found.setVisibility(View.VISIBLE);
                            new SnackbarIps(rv_attend,"Server Error");
                        }
                    }else {
                        tv_no_data_found.setVisibility(View.VISIBLE);
                        new SnackbarIps(rv_attend,"Server Error");
                    }
                    if (myDialog!=null && myDialog.isShowing()) {
                        myDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<TeamAttendRes> call,@NonNull Throwable t) {
                    if (myDialog!=null && myDialog.isShowing()) {
                        myDialog.dismiss();
                    }
                    tv_no_data_found.setVisibility(View.VISIBLE);
                    new SnackbarIps(rv_attend,"Network or Server Error");
                }
            });
        }catch (NullPointerException | NumberFormatException e){
            if (myDialog!=null && myDialog.isShowing()) {
                myDialog.dismiss();
            }
            tv_no_data_found.setVisibility(View.VISIBLE);
            new SnackbarIps(rv_attend,"Network or Server Error");
        }catch (Exception e){
            if (myDialog!=null && myDialog.isShowing()) {
                myDialog.dismiss();
            }
            tv_no_data_found.setVisibility(View.VISIBLE);
            new SnackbarIps(rv_attend,"Network or Server Error");
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
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
        }
    }
    class TeamAttendanceAdapter extends RecyclerView.Adapter<TeamAttendanceAdapter.MyViewHolder>
            implements Filterable {

        List<TeamAttendDetail> returnValue,listSearchView;

        private TeamAttendanceAdapter(List<TeamAttendDetail> returnValue) {
            this.returnValue = returnValue;
            this.listSearchView = returnValue;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_teamattendancedashboard,
                    parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

            if (returnValue.get(position).getMobileNumber()!= null &&
                    !returnValue.get(position).getMobileNumber().equals("") &&
                    !returnValue.get(position).getMobileNumber().isEmpty()) {
                holder.tv_phone_no.setText(returnValue.get(position).getMobileNumber());
            }else {
                holder.tv_phone_no.setText("-");
            }

            if (returnValue.get(position).getEmpFullName()!= null &&
                    !returnValue.get(position).getEmpFullName().equals("") &&
                    !returnValue.get(position).getEmpFullName().isEmpty()) {
                holder.tv_person_name.setText(returnValue.get(position).getEmpFullName());
            }else {
                holder.tv_person_name.setText("-");
            }

            if (returnValue.get(position).getClockInDT()!= null &&
                    !returnValue.get(position).getClockInDT().equals("") &&
                    !returnValue.get(position).getClockInDT().isEmpty()) {
                holder.tv_att_time_in.setText(returnValue.get(position).getClockInDT());
            }else {
                holder.tv_att_time_in.setText("-");
            }

            if (returnValue.get(position).getClockOutDT()!= null &&
                    !returnValue.get(position).getClockOutDT().equals("") &&
                    !returnValue.get(position).getClockOutDT().isEmpty()) {
                holder.tv_att_time_out.setText(returnValue.get(position).getClockOutDT());
            }else {
                holder.tv_att_time_out.setText("-");
            }

            if (returnValue.get(position).getClockInLocaction()!= null &&
                    !returnValue.get(position).getClockInLocaction().equals("") &&
                    !returnValue.get(position).getClockInLocaction().isEmpty()) {
                holder.tv_location.setText(returnValue.get(position).getClockInLocaction());
            }else {
                holder.tv_location.setText("-");
            }

            holder.v_time_in.setBackgroundColor(getResources().getColor(R.color.light_gray));
            holder.v_time_out.setBackgroundColor(getResources().getColor(R.color.white));

            holder.tv_att_time_in.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.v_time_in.setBackgroundColor(getResources().getColor(R.color.light_gray));
                    holder.v_time_out.setBackgroundColor(getResources().getColor(R.color.white));

                    if (returnValue.get(holder.getAdapterPosition()).getMobileNumber()!= null &&
                            !returnValue.get(holder.getAdapterPosition()).getMobileNumber().equals("") &&
                            !returnValue.get(holder.getAdapterPosition()).getMobileNumber().isEmpty()) {
                        holder.tv_location.setText(returnValue.get(holder.getAdapterPosition()).getClockInLocaction());
                    }else {
                        holder.tv_location.setText("-");
                    }
                }
            });
            holder.tv_att_time_out.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.v_time_in.setBackgroundColor(getResources().getColor(R.color.white));
                    holder.v_time_out.setBackgroundColor(getResources().getColor(R.color.light_gray));

                    if (returnValue.get(holder.getAdapterPosition()).getClockOutLocation()!= null &&
                            !returnValue.get(holder.getAdapterPosition()).getClockOutLocation().equals("") &&
                            !returnValue.get(holder.getAdapterPosition()).getClockOutLocation().isEmpty()) {
                        holder.tv_location.setText(returnValue.get(holder.getAdapterPosition()).getClockOutLocation());
                    }else {
                        holder.tv_location.setText("-");
                    }
                }
            });


        }

        @Override
        public int getItemCount() {
            return returnValue.size();
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView tv_phone_no,tv_person_name,tv_att_time_in_icon,tv_att_time_in,
                    tv_att_time_out_icon,tv_att_time_out,
                    tv_location_icon,tv_location;

            View v_time_in, v_time_out;

            public MyViewHolder(View itemView) {
                super(itemView);

                tv_phone_no = itemView.findViewById(R.id.tv_phone_no);
                tv_person_name = itemView.findViewById(R.id.tv_person_name);
                tv_att_time_in_icon = itemView.findViewById(R.id.tv_att_time_in_icon);
                tv_att_time_in = itemView.findViewById(R.id.tv_att_time_in);
                tv_att_time_out_icon = itemView.findViewById(R.id.tv_att_time_out_icon);
                tv_att_time_out = itemView.findViewById(R.id.tv_att_time_out);
                tv_location_icon = itemView.findViewById(R.id.tv_location_icon);
                tv_location = itemView.findViewById(R.id.tv_location);
                v_time_in = itemView.findViewById(R.id.v_time_in);
                v_time_out = itemView.findViewById(R.id.v_time_out);

                Icomoon.imageLogo.apply(getApplicationContext(), tv_att_time_in_icon);
                Icomoon.imageLogo.apply(getApplicationContext(), tv_att_time_out_icon);
                Icomoon.imageLogo.apply(getApplicationContext(), tv_location_icon);
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
                        List<TeamAttendDetail> filteredList = new ArrayList<>();
                        for (TeamAttendDetail row : listSearchView) {

                            if ((row.getEmpFullName()!=null && row.getEmpFullName().toLowerCase().contains(charString.toLowerCase())) ||
                                    (row.getMobileNumber()!=null && row.getMobileNumber().toLowerCase().contains(charString.toLowerCase())) ||
                                    (row.getClockInDT()!=null && row.getClockInDT().toLowerCase().contains(charString.toLowerCase())) ||
                                    (row.getClockOutDT()!=null && row.getClockOutDT().toLowerCase().contains(charString.toLowerCase())) ||
                                    (row.getClockOutLocation()!=null && row.getClockOutLocation().toLowerCase().contains(charString.toLowerCase())) ||
                                    (row.getClockInLocaction()!=null && row.getClockInLocaction().toLowerCase().contains(charString.toLowerCase()))) {
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
                    returnValue = (ArrayList<TeamAttendDetail>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }
    }
}