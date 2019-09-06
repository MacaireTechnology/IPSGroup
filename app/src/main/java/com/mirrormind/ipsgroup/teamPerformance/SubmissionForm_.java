package com.mirrormind.ipsgroup.teamPerformance;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
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
import retrofit.response.cutomerdata.CustomDataDetails;
import retrofit.response.cutomerdata.CustomDataRes;
import retrofit.response.subForm.SubFormDetails;
import retrofit.response.subForm.SubFormRes;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uihelper.SnackbarIps;
import uihelper.icomoon.Icomoon;
import uihelper.ripple.MaterialRippleLayout;
import uihelper.sharedPref.GlobalData;
import uihelper.sharedPref.SharedPreference;

public class SubmissionForm_ extends AppCompatActivity implements View.OnClickListener, GlobalData {

    public static final String TAG = SubmissionForm_.class.getSimpleName();
    TextView tv_back,tv_form,tv_submit,tv_sub_date,tv_sub_date_icon,tv_header_name,
            tv_search_icon,tv_back_search;
    Calendar myCalendar = Calendar.getInstance();
    ExpandableLayout expandable_layout;
    LinearLayout ll_sub_date;
    EditText et_total_submission,et_cv_shortlist,et_interview_sch,
            et_select_candidate,et_offered_candidate,et_join_candidate;
    RecyclerView rv_form;
    ApiInterface apiInterface;
    Activity mActivity;
    ProgressDialog myDialog;
    JSONObject allItems = new JSONObject();
    MaterialRippleLayout mrl_add_form;
    Intent intent;
    String TAG_PAGE_IDENTIFY1 = "";

    LinearLayout ll_header,ll_search;
    EditText et_search;
    AdapterCustomList adapterCustomList;
    AdapterForm adapterForm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submissionform);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mActivity = this;

        tv_back = findViewById(R.id.tv_back);
        tv_form = findViewById(R.id.tv_form);
        expandable_layout = findViewById(R.id.expandable_layout);
        ll_sub_date = findViewById(R.id.ll_sub_date);
        et_total_submission = findViewById(R.id.et_total_submission);
        et_cv_shortlist = findViewById(R.id.et_cv_shortlist);
        et_interview_sch = findViewById(R.id.et_interview_sch);
        et_select_candidate = findViewById(R.id.et_select_candidate);
        et_offered_candidate = findViewById(R.id.et_offered_candidate);
        et_join_candidate = findViewById(R.id.et_join_candidate);
        tv_sub_date = findViewById(R.id.tv_sub_date);
        tv_sub_date_icon = findViewById(R.id.tv_sub_date_icon);
        tv_submit = findViewById(R.id.tv_submit);
        rv_form = findViewById(R.id.rv_form);
        mrl_add_form = findViewById(R.id.mrl_add_form);
        tv_header_name = findViewById(R.id.tv_header_name);
        tv_search_icon = findViewById(R.id.tv_search_icon);
        tv_back_search = findViewById(R.id.tv_back_search);
        ll_header = findViewById(R.id.ll_header);
        ll_search = findViewById(R.id.ll_search);
        et_search = findViewById(R.id.et_search);

        rv_form.setLayoutManager(new LinearLayoutManager(this));
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        tv_back.setOnClickListener(this);
        tv_form.setOnClickListener(this);
        ll_sub_date.setOnClickListener(this);
        tv_submit.setOnClickListener(this);
        tv_sub_date.setOnClickListener(this);
        findViewById(R.id.ll_header).setOnClickListener(this);
        findViewById(R.id.ll_search).setOnClickListener(this);
        findViewById(R.id.tv_search_icon).setOnClickListener(this);
        findViewById(R.id.tv_back_search).setOnClickListener(this);

        Icomoon.imageLogo.apply(this, tv_back);
        Icomoon.imageLogo.apply(this, tv_sub_date_icon);
        Icomoon.imageLogo.apply(this, tv_search_icon);
        Icomoon.imageLogo.apply(this, tv_back_search);
        intent = getIntent();
        if (intent!=null){
            try {
                TAG_PAGE_IDENTIFY1 = intent.getStringExtra(TAG_PAGE_IDENTIFY);
                Log.e(TAG,"TAG_PAGE_IDENTIFY "+TAG_PAGE_IDENTIFY1);
                if (TAG_PAGE_IDENTIFY1.equals("111")){
                    tv_header_name.setText("Customer List");
                    mrl_add_form.setVisibility(View.GONE);
                    doListCusList();
                }else {
                    tv_header_name.setText("Submission Form");
                    mrl_add_form.setVisibility(View.VISIBLE);
                    doListForm(1);
                }
            }catch (NumberFormatException | NullPointerException e){
                tv_header_name.setText("Submission Form");
                mrl_add_form.setVisibility(View.VISIBLE);
                doListForm(1);
            }catch (Exception e){
                tv_header_name.setText("Submission Form");
                mrl_add_form.setVisibility(View.VISIBLE);
                doListForm(1);
            }

        }else {
            tv_header_name.setText("Submission Form");
            mrl_add_form.setVisibility(View.VISIBLE);
            doListForm(1);
        }

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (adapterCustomList!=null)
                    adapterCustomList.getFilter().filter(charSequence);
                if (adapterForm!=null)
                    adapterForm.getFilter().filter(charSequence);
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (adapterCustomList!=null)
                    adapterCustomList.getFilter().filter(charSequence);
                if (adapterForm!=null)
                    adapterForm.getFilter().filter(charSequence);
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void doListCusList() {
        myDialog = DialogsUtils.showProgressDialog(this, "Customers List");
        myDialog.show();
        try {
            Call<CustomDataRes> doCustomerList = apiInterface.doCustomData();

            doCustomerList.enqueue(new Callback<CustomDataRes>() {
                @Override
                public void onResponse(@NonNull Call<CustomDataRes> call,@NonNull Response<CustomDataRes> response) {

                    if (response.code() == 200){
                        assert response.body() != null;
                        if (response.body().getStatusCode()!=null && response.body().getStatusCode().equals("00")){
                            if (response.body().getReturnValue()!=null && response.body().getReturnValue().size()>0){
                                rv_form.setAdapter(adapterCustomList = new AdapterCustomList(response.body().getReturnValue()));
                            }else {
                                if (response.body().getStatusDescription()!=null)
                                    new SnackbarIps(rv_form,""+response.body().getStatusDescription());
                            }
                        }else {
                            if (response.body().getStatusDescription()!=null)
                                new SnackbarIps(rv_form,""+response.body().getStatusDescription());
                        }
                        if (myDialog!=null && myDialog.isShowing()){
                            myDialog.dismiss();
                        }
                    }else {
                        if (myDialog!=null && myDialog.isShowing()){
                            myDialog.dismiss();
                        }
                        new SnackbarIps(rv_form,"Server Error");
                    }

                }
                @Override
                public void onFailure(@NonNull Call<CustomDataRes> call,@NonNull Throwable t) {
                    if (myDialog!=null && myDialog.isShowing()){
                        myDialog.dismiss();
                    }
                    new SnackbarIps(rv_form,"Internet or Server Error");
                }
            });
        }catch (Exception e){
            if (myDialog!=null && myDialog.isShowing()){
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
            case R.id.tv_sub_date:
            case R.id.ll_sub_date:
                datePicker();
                break;
            case R.id.tv_form:
                if (expandable_layout.isExpanded()) {
                    expandable_layout.collapse();
                }else {
                    expandable_layout.expand();
                }
                break;
            case R.id.tv_submit:
                doValidateCondition();
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
    private void doValidateCondition() {
        if (tv_sub_date.getText().toString().equals("yyyy-MM-dd") || tv_sub_date.getText().toString().equals("")){
            new SnackbarIps(tv_sub_date,"Select Date");
        }else if (et_total_submission.getText().toString().equals("") || et_total_submission.getText().toString().isEmpty()) {
            new SnackbarIps(tv_sub_date,"Enter Total Submission");
        }else if (et_cv_shortlist.getText().toString().equals("") || et_cv_shortlist.getText().toString().isEmpty()) {
            new SnackbarIps(tv_sub_date,"Enter C.V Shortlisted");
        }else if (et_total_submission.getText().toString().equals("") || et_total_submission.getText().toString().isEmpty()) {
            new SnackbarIps(tv_sub_date,"Enter Interview Schedule");
        }else if (et_total_submission.getText().toString().equals("") || et_total_submission.getText().toString().isEmpty()) {
            new SnackbarIps(tv_sub_date,"Enter Select Candidate");
        }else if (et_total_submission.getText().toString().equals("") || et_total_submission.getText().toString().isEmpty()) {
            new SnackbarIps(tv_sub_date,"Enter Offered Candidate");
        }else if (et_total_submission.getText().toString().equals("") || et_total_submission.getText().toString().isEmpty()) {
            new SnackbarIps(tv_sub_date,"Enter Joined Candidate");
        }else {
            doSubmitForm();
        }
    }
    private void doSubmitForm() {
        myDialog = DialogsUtils.showProgressDialog(this, "Uploading Submission Form");
        myDialog.show();
        try {
            allItems.put("EmpID",SharedPreference.getDefaults(this,TAG_EMP_ID));
            allItems.put("SubmissionDate",tv_sub_date.getText().toString());
            allItems.put("TotalSubmission",et_total_submission.getText().toString());
            allItems.put("CVShortlisted",et_cv_shortlist.getText().toString());
            allItems.put("InterviewSchedule",et_interview_sch.getText().toString());
            allItems.put("SelectedCandidate",et_select_candidate.getText().toString());
            allItems.put("OfferedCandidates",et_offered_candidate.getText().toString());
            allItems.put("JoinedCandidates",et_join_candidate.getText().toString());

            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = (JsonObject) jsonParser.parse(String.valueOf(allItems));
            Call<ApplyLeaveRes> subForm = apiInterface.doSaveSubForm(jsonObject);
            subForm.enqueue(new Callback<ApplyLeaveRes>() {
                @Override
                public void onResponse(@NonNull Call<ApplyLeaveRes> call,@NonNull Response<ApplyLeaveRes> response) {
                    if (response.code() == 200){
                        assert response.body() != null;
                        if (response.body().getStatusCode()!=null && response.body().getStatusCode().equals("00")){
                            if (response.body().getStatusDescription()!=null)
                                new SnackbarIps(rv_form,response.body().getStatusDescription());

                            if (expandable_layout.isExpanded()) {
                                expandable_layout.collapse();
                            }

                            et_total_submission.setText("");
                            et_cv_shortlist.setText("");
                            et_interview_sch.setText("");
                            et_select_candidate.setText("");
                            et_offered_candidate.setText("");
                            et_join_candidate.setText("");
                            tv_sub_date.setText("yyyy-MM-dd");
                            doListForm(2);

                        }else {
                            if (myDialog!=null && myDialog.isShowing()){
                                myDialog.dismiss();
                            }
                            if (response.body().getStatusDescription()!=null)
                                new SnackbarIps(rv_form,response.body().getStatusDescription());
                        }
                    }else {
                        if (myDialog!=null && myDialog.isShowing()){
                            myDialog.dismiss();
                        }
                        new SnackbarIps(rv_form,"Server Error");
                    }
                }
                @Override
                public void onFailure(@NonNull Call<ApplyLeaveRes> call,@NonNull Throwable t) {
                    if (myDialog!=null && myDialog.isShowing()){
                        myDialog.dismiss();
                    }
                    new SnackbarIps(rv_form,"Internet or Server Error");
                }
            });

        }catch (Exception e){
            if (myDialog!=null && myDialog.isShowing()){
                myDialog.dismiss();
            }
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
        mDatePicker.setTitle("Select date");

        mDatePicker.show();
    }
    private void updateLabel() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

        tv_sub_date.setText(sdf.format(myCalendar.getTime()));
        tv_sub_date.setTypeface(tv_sub_date.getTypeface(), Typeface.NORMAL);
        tv_sub_date.setTextColor(getResources().getColor(R.color.six_e));
    }
    private void doListForm(int showProgressBar) {
        if (showProgressBar == 1){
            myDialog = DialogsUtils.showProgressDialog(this, "Submission Form Listing");
            myDialog.show();
        }
        try {
            Call<SubFormRes> doCustomerList = apiInterface.doSubForm(SharedPreference.getDefaults(this,TAG_EMP_ID));

            doCustomerList.enqueue(new Callback<SubFormRes>() {
                @Override
                public void onResponse(@NonNull Call<SubFormRes> call,@NonNull Response<SubFormRes> response) {

                    if (response.code() == 200){
                        assert response.body() != null;
                        if (response.body().getStatusCode()!=null && response.body().getStatusCode().equals("00")){
                            if (response.body().getReturnValue()!=null && response.body().getReturnValue().size()>0){
                                rv_form.setAdapter(adapterForm = new AdapterForm(response.body().getReturnValue()));
                            }else {
                                if (response.body().getStatusDescription()!=null)
                                    new SnackbarIps(rv_form,""+response.body().getStatusDescription());
                            }
                        }else {
                            if (response.body().getStatusDescription()!=null)
                                new SnackbarIps(rv_form,""+response.body().getStatusDescription());
                        }
                        if (myDialog!=null && myDialog.isShowing()){
                            myDialog.dismiss();
                        }
                    }else {
                        if (myDialog!=null && myDialog.isShowing()){
                            myDialog.dismiss();
                        }
                        new SnackbarIps(rv_form,"Server Error");
                    }

                }
                @Override
                public void onFailure(@NonNull Call<SubFormRes> call,@NonNull Throwable t) {
                    if (myDialog!=null && myDialog.isShowing()){
                        myDialog.dismiss();
                    }
                    new SnackbarIps(rv_form,"Internet or Server Error");
                }
            });
        }catch (Exception e){
            if (myDialog!=null && myDialog.isShowing()){
                myDialog.dismiss();
            }
        }

    }
    private class AdapterForm extends RecyclerView.Adapter<AdapterForm.MyViewHolder>
            implements Filterable {

        List<SubFormDetails> returnValue,listSearchView;

        private AdapterForm(List<SubFormDetails> returnValue) {
            this.returnValue = returnValue;
            this.listSearchView = returnValue;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.adpt_sub_form,
                    viewGroup,false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            if (returnValue.get(position).getSubmissionDate()!=null &&
                    !returnValue.get(position).getSubmissionDate().equals("") &&
                    !returnValue.get(position).getSubmissionDate().isEmpty()){
                holder.tv_sub_date.setText(returnValue.get(position).getSubmissionDate());
            }else {
                holder.tv_sub_date.setText("-");
            }
            if (returnValue.get(position).getSubmission()!=null &&
                    !returnValue.get(position).getSubmission().equals("") &&
                    !returnValue.get(position).getSubmission().isEmpty()){
                holder.tv_total_sub.setText(returnValue.get(position).getSubmission());
            }else {
                holder.tv_total_sub.setText("-");
            }
            if (returnValue.get(position).getCVShort()!=null &&
                    !returnValue.get(position).getCVShort().equals("") &&
                    !returnValue.get(position).getCVShort().isEmpty()){
                holder.tv_cv_short.setText(returnValue.get(position).getCVShort());
            }else {
                holder.tv_cv_short.setText("-");
            }
            if (returnValue.get(position).getInterviewSchedule()!=null &&
                    !returnValue.get(position).getInterviewSchedule().equals("") &&
                    !returnValue.get(position).getInterviewSchedule().isEmpty()){
                holder.tv_interview_sec.setText(returnValue.get(position).getInterviewSchedule());
            }else {
                holder.tv_interview_sec.setText("-");
            }
            if (returnValue.get(position).getSelectedCandidate()!=null &&
                    !returnValue.get(position).getSelectedCandidate().equals("") &&
                    !returnValue.get(position).getSelectedCandidate().isEmpty()){
                holder.tv_select_candi.setText(returnValue.get(position).getSelectedCandidate());
            }else {
                holder.tv_select_candi.setText("-");
            }
            if (returnValue.get(position).getOfferedCandidates()!=null &&
                    !returnValue.get(position).getOfferedCandidates().equals("") &&
                    !returnValue.get(position).getOfferedCandidates().isEmpty()){
                holder.tv_offer_candi.setText(returnValue.get(position).getOfferedCandidates());
            }else {
                holder.tv_offer_candi.setText("-");
            }
            if (returnValue.get(position).getJoinCandidate()!=null &&
                    !returnValue.get(position).getJoinCandidate().equals("") &&
                    !returnValue.get(position).getJoinCandidate().isEmpty()){
                holder.tv_join_candi.setText(returnValue.get(position).getJoinCandidate());
            }else {
                holder.tv_join_candi.setText("-");
            }
        }

        @Override
        public int getItemCount() {
            return returnValue.size();
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {

            TextView tv_sub_date,tv_total_sub,tv_cv_short,tv_interview_sec,
                    tv_select_candi,tv_offer_candi,tv_join_candi;

            private MyViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_sub_date = itemView.findViewById(R.id.tv_sub_date);
                tv_total_sub = itemView.findViewById(R.id.tv_total_sub);
                tv_cv_short = itemView.findViewById(R.id.tv_cv_short);
                tv_interview_sec = itemView.findViewById(R.id.tv_interview_sec);
                tv_select_candi = itemView.findViewById(R.id.tv_select_candi);
                tv_offer_candi = itemView.findViewById(R.id.tv_offer_candi);
                tv_join_candi = itemView.findViewById(R.id.tv_join_candi);
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
                        List<SubFormDetails> filteredList = new ArrayList<>();
                        for (SubFormDetails row : listSearchView) {

                            if ((row.getSubmissionDate()!=null && row.getSubmissionDate().toLowerCase().contains(charString.toLowerCase())) ||
                                    (row.getSubmission()!=null && row.getSubmission().toLowerCase().contains(charString.toLowerCase())) ||
                                    (row.getCVShort()!=null && row.getCVShort().toLowerCase().contains(charString.toLowerCase())) ||
                                    (row.getInterviewSchedule()!=null && row.getInterviewSchedule().toLowerCase().contains(charString.toLowerCase())) ||
                                    (row.getSelectedCandidate()!=null && row.getSelectedCandidate().toLowerCase().contains(charString.toLowerCase())) ||
                                    (row.getOfferedCandidates()!=null && row.getOfferedCandidates().toLowerCase().contains(charString.toLowerCase())) ||
                                    (row.getJoinCandidate()!=null && row.getJoinCandidate().toLowerCase().contains(charString.toLowerCase()))) {
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
                    returnValue = (ArrayList<SubFormDetails>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }

    }

    private class AdapterCustomList extends RecyclerView.Adapter<AdapterCustomList.MyViewHolder>
            implements Filterable {

        List<CustomDataDetails> returnValue,listSearchView;

        private AdapterCustomList(List<CustomDataDetails> returnValue) {
            this.returnValue = returnValue;
            this.listSearchView = returnValue;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.adpt_sub_form,
                    viewGroup,false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
            if (returnValue.get(position).getCustName()!=null &&
                    !returnValue.get(position).getCustName().equals("") &&
                    !returnValue.get(position).getCustName().isEmpty()){
                holder.tv_sub_date.setText(returnValue.get(position).getCustName());
            }else {
                holder.tv_sub_date.setText("-");
            }
            if (returnValue.get(position).getCustMobileNo()!=null &&
                    !returnValue.get(position).getCustMobileNo().equals("") &&
                    !returnValue.get(position).getCustMobileNo().isEmpty()){
                holder.tv_total_sub.setText(returnValue.get(position).getCustMobileNo());
            }else {
                holder.tv_total_sub.setText("-");
            }
            if (returnValue.get(position).getCustEmaiIID()!=null &&
                    !returnValue.get(position).getCustEmaiIID().equals("") &&
                    !returnValue.get(position).getCustEmaiIID().isEmpty()){
                holder.tv_cv_short.setText(returnValue.get(position).getCustEmaiIID());
            }else {
                holder.tv_cv_short.setText("-");
            }
            if (returnValue.get(position).getContactPerson()!=null &&
                    !returnValue.get(position).getContactPerson().equals("") &&
                    !returnValue.get(position).getContactPerson().isEmpty()){
                holder.tv_interview_sec.setText(returnValue.get(position).getContactPerson());
            }else {
                holder.tv_interview_sec.setText("-");
            }
            if (returnValue.get(position).getOutletType()!=null &&
                    !returnValue.get(position).getOutletType().equals("") &&
                    !returnValue.get(position).getOutletType().isEmpty()){
                holder.tv_select_candi.setText(returnValue.get(position).getOutletType());
            }else {
                holder.tv_select_candi.setText("-");
            }
            if (returnValue.get(position).getLandMark()!=null &&
                    !returnValue.get(position).getLandMark().equals("") &&
                    !returnValue.get(position).getLandMark().isEmpty()){
                holder.tv_offer_candi.setText(returnValue.get(position).getLandMark());
            }else {
                holder.tv_offer_candi.setText("-");
            }
            if (returnValue.get(position).getAddress()!=null &&
                    !returnValue.get(position).getAddress().equals("") &&
                    !returnValue.get(position).getAddress().isEmpty()) {
                holder.tv_join_candi.setText(returnValue.get(position).getAddress());
            }else {
                holder.tv_join_candi.setText("-");
            }


            holder.ll_option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.e(TAG,"TAG_CUS_ID "+returnValue.get(holder.getAdapterPosition()).getCustID());

                    startActivity(new Intent(SubmissionForm_.this,DailySales.class)
                            .putExtra(TAG_CUS_MOB_NO,returnValue.get(holder.getAdapterPosition()).getCustMobileNo())
                            .putExtra(TAG_CUS_NAME,returnValue.get(holder.getAdapterPosition()).getCustName())
                            .putExtra(TAG_CUS_EMAIL_ID,returnValue.get(holder.getAdapterPosition()).getCustEmaiIID())
                            .putExtra(TAG_CONTACT_PERSON,returnValue.get(holder.getAdapterPosition()).getContactPerson())
                            .putExtra(TAG_OUTLET_TYPE,returnValue.get(holder.getAdapterPosition()).getOutletType())
                            .putExtra(TAG_LANDMARK,returnValue.get(holder.getAdapterPosition()).getLandMark())
                            .putExtra(TAG_ADDRESS,returnValue.get(holder.getAdapterPosition()).getAddress())
                            .putExtra(TAG_CUS_ID,returnValue.get(holder.getAdapterPosition()).getCustID())
                    );
                }
            });
        }

        @Override
        public int getItemCount() {
            return returnValue.size();
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {

            TextView tv_sub_date,tv_total_sub,tv_cv_short,tv_interview_sec,
                    tv_select_candi,tv_offer_candi,tv_join_candi;
            TextView tv_sub_date_header,tv_total_sub_header,tv_cv_short_header,
                    tv_interview_sec_header, tv_select_candi_header,
                    tv_offer_candi_header,tv_join_candi_header;
            LinearLayout ll_option;

            private MyViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_sub_date = itemView.findViewById(R.id.tv_sub_date);
                tv_total_sub = itemView.findViewById(R.id.tv_total_sub);
                tv_cv_short = itemView.findViewById(R.id.tv_cv_short);
                tv_interview_sec = itemView.findViewById(R.id.tv_interview_sec);
                tv_select_candi = itemView.findViewById(R.id.tv_select_candi);
                tv_offer_candi = itemView.findViewById(R.id.tv_offer_candi);
                tv_join_candi = itemView.findViewById(R.id.tv_join_candi);

                tv_sub_date_header = itemView.findViewById(R.id.tv_sub_date_header);
                tv_total_sub_header = itemView.findViewById(R.id.tv_total_sub_header);
                tv_cv_short_header = itemView.findViewById(R.id.tv_cv_short_header);
                tv_interview_sec_header = itemView.findViewById(R.id.tv_interview_sec_header);
                tv_select_candi_header = itemView.findViewById(R.id.tv_select_candi_header);
                tv_offer_candi_header = itemView.findViewById(R.id.tv_offer_candi_header);
                tv_join_candi_header = itemView.findViewById(R.id.tv_join_candi_header);
                ll_option = itemView.findViewById(R.id.ll_option);

                tv_sub_date_header.setText("Customer Name");
                tv_total_sub_header.setText("Customer Mobile No");
                tv_cv_short_header.setText("Customer EmailId");
                tv_interview_sec_header.setText("Contact Person");
                tv_select_candi_header.setText("Outlet Type");
                tv_offer_candi_header.setText("LandMark");
                tv_join_candi_header.setText("Address");
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
                        List<CustomDataDetails> filteredList = new ArrayList<>();
                        for (CustomDataDetails row : listSearchView) {

                            if ((row.getCustName()!=null && row.getCustName().toLowerCase().contains(charString.toLowerCase())) ||
                                    (row.getCustMobileNo()!=null && row.getCustMobileNo().toLowerCase().contains(charString.toLowerCase())) ||
                                    (row.getContactPerson()!=null && row.getContactPerson().toLowerCase().contains(charString.toLowerCase())) ||
                                    (row.getOutletType()!=null && row.getOutletType().toLowerCase().contains(charString.toLowerCase())) ||
                                    (row.getAddress()!=null && row.getAddress().toLowerCase().contains(charString.toLowerCase())) ||
                                    (row.getCustEmaiIID()!=null && row.getCustEmaiIID().toLowerCase().contains(charString.toLowerCase()))) {
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
                    returnValue = (ArrayList<CustomDataDetails>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }
    }
}
