package com.mirrormind.ipsgroup.attendanceteam;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import retrofit.response.TrackRes;
import retrofit.response.Trackreturnvalue;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uihelper.SnackbarIps;
import uihelper.icomoon.Icomoon;
import uihelper.sharedPref.GlobalData;
import uihelper.sharedPref.SharedPreference;
import static uihelper.sharedPref.GlobalData.TAG_EMP_ID;
import static uihelper.sharedPref.GlobalData.TAG_SHARE_EMP_ID;
import static uihelper.sharedPref.GlobalData.TAG_SHARE_LAT;
import static uihelper.sharedPref.GlobalData.TAG_SHARE_LONG;
import static uihelper.sharedPref.GlobalData.TAG_SHARE_NAME;

public class TrackTeam extends AppCompatActivity implements View.OnClickListener,
        GlobalData {

    Activity mActivity;
    TextView tv_back,tv_header_name,tv_no_data_found,tv_search_icon,tv_back_search;
    RecyclerView rv_attend;
    ProgressDialog myDialog;
    ApiInterface apiInterface;
    LinearLayout ll_header,ll_search;
    EditText et_search;
    TeamAttendanceAdapter teamAttendanceAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_approval);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        mActivity = this;

        rv_attend = findViewById(R.id.rv_attend);
        tv_back = findViewById(R.id.tv_back);
        tv_header_name = findViewById(R.id.tv_header_name);
        tv_no_data_found = findViewById(R.id.tv_no_data_found);
        tv_search_icon = findViewById(R.id.tv_search_icon);
        tv_back_search = findViewById(R.id.tv_back_search);
        ll_header = findViewById(R.id.ll_header);
        ll_search = findViewById(R.id.ll_search);
        et_search = findViewById(R.id.et_search);

        tv_header_name.setText("Track Team");

        tv_no_data_found.setVisibility(View.GONE);
        Icomoon.imageLogo.apply(this, tv_back);
        tv_back.setOnClickListener(this);

        myDialog = DialogsUtils.showProgressDialog(TrackTeam.this, "Tracking");
        myDialog.show();

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
        tv_back.setOnClickListener(this);
        findViewById(R.id.ll_header).setOnClickListener(this);
        findViewById(R.id.ll_search).setOnClickListener(this);
        findViewById(R.id.tv_search_icon).setOnClickListener(this);
        findViewById(R.id.tv_back_search).setOnClickListener(this);

        Icomoon.imageLogo.apply(this, tv_back);
        Icomoon.imageLogo.apply(this, tv_search_icon);
        Icomoon.imageLogo.apply(this, tv_back_search);

        doCallTeam();
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    private void doCallTeam() {
        try {
            Call<TrackRes> trackTeamCall = apiInterface.dotrackteam(SharedPreference.getDefaults(getApplicationContext(),
                    GlobalData.TAG_EMP_ID));
            trackTeamCall.enqueue(new Callback<TrackRes>() {
                @Override
                public void onResponse(@NonNull Call<TrackRes> call,@NonNull Response<TrackRes> response) {
                    if (response.code() == 200) {
                        assert response.body() != null;
                        if (response.body().getStatusCode().equals("00")) {
                            if (response.body().getReturnValue().size() > 0) {
                                tv_no_data_found.setVisibility(View.GONE);
                                rv_attend.setVisibility(View.VISIBLE);
                                rv_attend.setLayoutManager(new GridLayoutManager(mActivity, 1));
                                rv_attend.setAdapter(teamAttendanceAdapter = new TeamAttendanceAdapter(mActivity,
                                        response.body().getReturnValue()));
                            }else {
                                rv_attend.setVisibility(View.GONE);
                                tv_no_data_found.setVisibility(View.VISIBLE);
                            }
                        } else {
                            rv_attend.setVisibility(View.GONE);
                            tv_no_data_found.setVisibility(View.VISIBLE);
                            new SnackbarIps(tv_back, "" + "Not a valid user", 2000);
                        }

                    } else {
                        rv_attend.setVisibility(View.GONE);
                        tv_no_data_found.setVisibility(View.VISIBLE);

                        new SnackbarIps(tv_back, "Server Error", 2000);
                    }
                    if (myDialog!=null && myDialog.isShowing()){
                        myDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<TrackRes> call,@NonNull Throwable t) {
                    if (myDialog!=null && myDialog.isShowing()){
                        myDialog.dismiss();
                    }
                    new SnackbarIps(tv_back, "Network Or Server Error", 2000);
                }
            });
        }catch (NumberFormatException | NullPointerException e){
            if (myDialog!=null && myDialog.isShowing()){
                myDialog.dismiss();
            }
        }catch (Exception e){
            if (myDialog!=null && myDialog.isShowing()){
                myDialog.dismiss();
            }
        }
    }
}
class TeamAttendanceAdapter extends RecyclerView.Adapter<TeamAttendanceAdapter.MyViewHolder> implements Filterable {

    private Context mActivity;
    private List<Trackreturnvalue> leavetext;
    private List<Trackreturnvalue> listSearchView;

    public TeamAttendanceAdapter(Activity activity, List<Trackreturnvalue> leavetext) {
        this.mActivity = activity;
        this.leavetext = leavetext;
        this.listSearchView = leavetext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_trackteam_dashboard,
                parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        if (leavetext.get(position).getEmpFullName()!=null &&
                !leavetext.get(position).getEmpFullName().equals("") &&
                !leavetext.get(position).getEmpFullName().isEmpty()){
            holder.tv_teamperfm_text.setText(leavetext.get(position).getEmpFullName());
        }else {
            holder.tv_teamperfm_text.setText("-");
        }
        if (leavetext.get(position).getMobileNumber()!=null &&
                !leavetext.get(position).getMobileNumber().equals("") &&
                !leavetext.get(position).getMobileNumber().isEmpty()){
            holder.tv_mobileno.setText(leavetext.get(position).getMobileNumber());
        }else {
            holder.tv_mobileno.setText("-");
        }
        if (leavetext.get(position).getPrimaryEmail()!=null &&
                !leavetext.get(position).getPrimaryEmail().equals("") &&
                !leavetext.get(position).getPrimaryEmail().isEmpty()){
            holder.tv_mail.setText(leavetext.get(position).getPrimaryEmail());
        }else {
            holder.tv_mail.setText("-");
        }
        if (leavetext.get(position).getGPSAddress()!=null &&
                !leavetext.get(position).getGPSAddress().equals("") &&
                !leavetext.get(position).getGPSAddress().isEmpty()){
            holder.tv_teamlocation.setText(leavetext.get(position).getGPSAddress());
        }else {
            holder.tv_teamlocation.setText("-");
        }
        holder.ll_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (leavetext.get(holder.getAdapterPosition()).getLat()!=null &&
                        !leavetext.get(holder.getAdapterPosition()).getLat().equals("") &&
                        !leavetext.get(holder.getAdapterPosition()).getLat().isEmpty()){
                    SharedPreference.setDefaults(mActivity,TAG_SHARE_LAT,leavetext.get(holder.getAdapterPosition()).getLat());
                }else {
                    SharedPreference.setDefaults(mActivity,TAG_SHARE_LAT,"0.00");
                }

                if (leavetext.get(holder.getAdapterPosition()).getLng()!=null &&
                        !leavetext.get(holder.getAdapterPosition()).getLng().equals("") &&
                        !leavetext.get(holder.getAdapterPosition()).getLng().isEmpty()){
                    SharedPreference.setDefaults(mActivity,TAG_SHARE_LONG,leavetext.get(holder.getAdapterPosition()).getLng());
                }else {
                    SharedPreference.setDefaults(mActivity,TAG_SHARE_LONG,"0.00");
                }

                if (leavetext.get(holder.getAdapterPosition()).getEmpFullName()!=null &&
                        !leavetext.get(holder.getAdapterPosition()).getEmpFullName().equals("") &&
                        !leavetext.get(holder.getAdapterPosition()).getEmpFullName().isEmpty()){
                    SharedPreference.setDefaults(mActivity,TAG_SHARE_NAME,leavetext.get(holder.getAdapterPosition()).getEmpFullName());
                }else {
                    SharedPreference.setDefaults(mActivity,TAG_SHARE_NAME,"0.00");
                }

                if (leavetext.get(holder.getAdapterPosition()).getEmpId()!=null &&
                        !leavetext.get(holder.getAdapterPosition()).getEmpId().equals("") &&
                        !leavetext.get(holder.getAdapterPosition()).getEmpId().isEmpty()){
                    SharedPreference.setDefaults(mActivity,TAG_SHARE_EMP_ID,leavetext.get(holder.getAdapterPosition()).getEmpId());
                }else {
                    SharedPreference.setDefaults(mActivity,TAG_SHARE_EMP_ID,SharedPreference.getDefaults(mActivity,TAG_EMP_ID));
                }

                mActivity.startActivity(new Intent(mActivity,TeamMap.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return leavetext.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_option_icon, tv_location, tv_option,tv_mail_icon,tv_mail,
                t_att_timeinicon_color, tv_teamperfm_text, tv_mobileno,
                tv_teamlocation,tv_location_icon;
        LinearLayout ll_option;

        public MyViewHolder(View itemView) {
            super(itemView);

            tv_location = itemView.findViewById(R.id.tv_location);
            tv_option_icon = itemView.findViewById(R.id.tv_option_icon);
            tv_option = itemView.findViewById(R.id.tv_option);
            tv_mail_icon = itemView.findViewById(R.id.tv_mail_icon);
            tv_mail = itemView.findViewById(R.id.tv_mail);
            t_att_timeinicon_color = itemView.findViewById(R.id.t_att_timeinicon_color);
            tv_teamperfm_text = itemView.findViewById(R.id.tv_teamperfm_text);
            tv_mobileno = itemView.findViewById(R.id.tv_mobileno);
            tv_teamlocation = itemView.findViewById(R.id.tv_teamlocation);
            tv_location_icon = itemView.findViewById(R.id.tv_location_icon);
            ll_option = itemView.findViewById(R.id.ll_option);

            Icomoon.imageLogo.apply(mActivity, tv_mail_icon);
            Icomoon.imageLogo.apply(mActivity, tv_location);
            Icomoon.imageLogo.apply(mActivity, t_att_timeinicon_color);
            Icomoon.imageLogo.apply(mActivity, tv_location_icon);
        }
    }
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    leavetext = listSearchView;
                } else {
                    List<Trackreturnvalue> filteredList = new ArrayList<>();
                    for (Trackreturnvalue row : listSearchView) {

                        if ((row.getEmpFullName()!=null && row.getEmpFullName().toLowerCase().contains(charString.toLowerCase())) ||
                                (row.getMobileNumber()!=null && row.getMobileNumber().toLowerCase().contains(charString.toLowerCase())) ||
                                (row.getPrimaryEmail()!=null && row.getPrimaryEmail().toLowerCase().contains(charString.toLowerCase()))) {
                            filteredList.add(row);
                        }
                    }
                    leavetext = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = leavetext;
                return filterResults;
            }
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                leavetext = (ArrayList<Trackreturnvalue>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}