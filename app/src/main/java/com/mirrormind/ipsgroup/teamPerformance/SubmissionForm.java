package com.mirrormind.ipsgroup.teamPerformance;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mirrormind.ipsgroup.Dialog.DialogsUtils;
import com.mirrormind.ipsgroup.R;
import com.mirrormind.ipsgroup.attendanceteam.AttendanceApproval;
import com.mirrormind.ipsgroup.camera.RunTimePermission;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import retrofit.ApiClient;
import retrofit.ApiInterface;
import retrofit.response.ApplyLeaveRes;
import retrofit.response.ImageRes;
import retrofit.response.cutomerdata.CustomDataDetails;
import retrofit.response.cutomerdata.CustomDataRes;
import retrofit.response.subForm.SubFormDetails;
import retrofit.response.subForm.SubFormRes;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uihelper.SnackbarIps;
import uihelper.icomoon.Icomoon;
import uihelper.onKeyboard.OnKeyboardHide;
import uihelper.picker.OnDatePicker;
import uihelper.picker.OnTimePicker;
import uihelper.ripple.MaterialRippleLayout;
import uihelper.sharedPref.GlobalData;
import uihelper.sharedPref.SharedPreference;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.os.Build.VERSION_CODES.M;
import static uihelper.DateFormat.parseDateToday;

public class SubmissionForm extends AppCompatActivity implements View.OnClickListener, GlobalData {

    public static final String TAG = SubmissionForm.class.getSimpleName();
    TextView tv_back,tv_form,tv_submit,tv_sub_date,tv_sub_date_icon,tv_header_name,
            tv_search_icon,tv_back_search;
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
    InputStream imageStream;
    Uri uriCameraImage;

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


        if (Build.VERSION.SDK_INT >= 23) {
            ArrayList<String> listPermissionsNeeded = new ArrayList<String>();
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                listPermissionsNeeded
                        .add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                listPermissionsNeeded
                        .add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }

            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                listPermissionsNeeded.add(Manifest.permission.CAMERA);
            }

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                listPermissionsNeeded
                        .add(Manifest.permission.ACCESS_FINE_LOCATION);
            }

            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                listPermissionsNeeded
                        .add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }

            if (!listPermissionsNeeded.isEmpty())
                ActivityCompat.requestPermissions(this, listPermissionsNeeded
                        .toArray(new String[listPermissionsNeeded.size()]), 1);
        }

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
                new OnDatePicker(this,tv_sub_date);
                break;
            case R.id.tv_form:
                if (expandable_layout.isExpanded()) {
                    expandable_layout.collapse();
                }else {
                    expandable_layout.expand();
                }
                break;
            case R.id.tv_submit:
                doValidateCondition(view);
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
    private void doValidateCondition(View view) {
        new OnKeyboardHide(this,view);
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
            View view = LayoutInflater.from(mActivity).inflate(R.layout.adpt_sub_forms,
                    viewGroup,false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            if (returnValue.get(position).getSubmissionDate()!=null &&
                    !returnValue.get(position).getSubmissionDate().equals("") &&
                    !returnValue.get(position).getSubmissionDate().isEmpty()){
                holder.tv_cus_name.setText(returnValue.get(position).getSubmissionDate());
            }else {
                holder.tv_cus_name.setText("-");
            }
            if (returnValue.get(position).getSubmission()!=null &&
                    !returnValue.get(position).getSubmission().equals("") &&
                    !returnValue.get(position).getSubmission().isEmpty()){
                holder.tv_mobile_no.setText(returnValue.get(position).getSubmission());
            }else {
                holder.tv_mobile_no.setText("-");
            }
            if (returnValue.get(position).getCVShort()!=null &&
                    !returnValue.get(position).getCVShort().equals("") &&
                    !returnValue.get(position).getCVShort().isEmpty()){
                holder.tv_mail.setText(returnValue.get(position).getCVShort());
            }else {
                holder.tv_mail.setText("-");
            }
            if (returnValue.get(position).getInterviewSchedule()!=null &&
                    !returnValue.get(position).getInterviewSchedule().equals("") &&
                    !returnValue.get(position).getInterviewSchedule().isEmpty()){
                holder.tv_contact.setText(returnValue.get(position).getInterviewSchedule());
            }else {
                holder.tv_contact.setText("-");
            }
            if (returnValue.get(position).getSelectedCandidate()!=null &&
                    !returnValue.get(position).getSelectedCandidate().equals("") &&
                    !returnValue.get(position).getSelectedCandidate().isEmpty()){
                holder.tv_outlet.setText(returnValue.get(position).getSelectedCandidate());
            }else {
                holder.tv_outlet.setText("-");
            }
            if (returnValue.get(position).getOfferedCandidates()!=null &&
                    !returnValue.get(position).getOfferedCandidates().equals("") &&
                    !returnValue.get(position).getOfferedCandidates().isEmpty()){
                holder.tv_landmark.setText(returnValue.get(position).getOfferedCandidates());
            }else {
                holder.tv_landmark.setText("-");
            }
            if (returnValue.get(position).getJoinCandidate()!=null &&
                    !returnValue.get(position).getJoinCandidate().equals("") &&
                    !returnValue.get(position).getJoinCandidate().isEmpty()){
                holder.tv_address.setText(returnValue.get(position).getJoinCandidate());
            }else {
                holder.tv_address.setText("-");
            }
        }

        @Override
        public int getItemCount() {
            return returnValue.size();
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {

            TextView tv_cus_name,tv_edit_user,tv_mobile_no,tv_mail,tv_contact,
                    tv_outlet,tv_landmark,tv_address,tv_sub_date,tv_upload;
            TextView tv_mobile_icon,tv_mail_icon,tv_contact_icon,tv_outlet_icon,
                    tv_landmark_icon,tv_address_icon,tv_sub_date_icon,tv_img_upload_icon;
            LinearLayout ll_edit_user,ll_option,ll_sub_date;
            EditText et_status,et_remark;
            ExpandableLayout expandable_layout;

            private MyViewHolder(@NonNull View itemView) {
                super(itemView);

                tv_cus_name = itemView.findViewById(R.id.tv_cus_name);
                tv_edit_user = itemView.findViewById(R.id.tv_edit_user);
                tv_mobile_no = itemView.findViewById(R.id.tv_mobile_no);
                tv_mail = itemView.findViewById(R.id.tv_mail);
                tv_contact = itemView.findViewById(R.id.tv_contact);
                tv_outlet = itemView.findViewById(R.id.tv_outlet);
                tv_landmark = itemView.findViewById(R.id.tv_landmark);
                tv_address = itemView.findViewById(R.id.tv_address);

                tv_mobile_icon = itemView.findViewById(R.id.tv_mobile_icon);
                tv_mail_icon = itemView.findViewById(R.id.tv_mail_icon);
                tv_contact_icon = itemView.findViewById(R.id.tv_contact_icon);
                tv_outlet_icon = itemView.findViewById(R.id.tv_outlet_icon);
                tv_landmark_icon = itemView.findViewById(R.id.tv_landmark_icon);
                tv_address_icon = itemView.findViewById(R.id.tv_address_icon);
                tv_sub_date_icon = itemView.findViewById(R.id.tv_sub_date_icon);
                tv_img_upload_icon = itemView.findViewById(R.id.tv_img_upload_icon);

                ll_edit_user = itemView.findViewById(R.id.ll_edit_user);
                ll_option = itemView.findViewById(R.id.ll_option);
                ll_sub_date = itemView.findViewById(R.id.ll_sub_date);
                tv_sub_date = itemView.findViewById(R.id.tv_sub_date);
                et_status = itemView.findViewById(R.id.et_status);
                et_remark = itemView.findViewById(R.id.et_remark);
                tv_upload = itemView.findViewById(R.id.tv_upload);
                expandable_layout = itemView.findViewById(R.id.expandable_layout);

                tv_mobile_icon.setText(getResources().getString(R.string.total_sub_icon));
                tv_mail_icon.setText(getResources().getString(R.string.shortlist_icon));
                tv_contact_icon.setText(getResources().getString(R.string.interview_candi_icon));
                tv_outlet_icon.setText(getResources().getString(R.string.selected_candi_icon));
                tv_landmark_icon.setText(getResources().getString(R.string.offer_candi_icon));
                tv_address_icon.setText(getResources().getString(R.string.join_candi_icon));
                tv_edit_user.setVisibility(View.GONE);
                Icomoon.imageLogo.apply(mActivity,tv_edit_user);
                Icomoon.imageLogo.apply(mActivity,tv_mobile_icon);
                Icomoon.imageLogo.apply(mActivity,tv_mail_icon);
                Icomoon.imageLogo.apply(mActivity,tv_contact_icon);
                Icomoon.imageLogo.apply(mActivity,tv_outlet_icon);
                Icomoon.imageLogo.apply(mActivity,tv_landmark_icon);
                Icomoon.imageLogo.apply(mActivity,tv_address_icon);
                Icomoon.imageLogo.apply(mActivity,tv_sub_date_icon);
                Icomoon.imageLogo.apply(mActivity,tv_img_upload_icon);
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
            View view = LayoutInflater.from(mActivity).inflate(R.layout.adpt_sub_forms,
                    viewGroup,false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

            if (holder.expandable_layout.isExpanded()) {
                holder.expandable_layout.collapse();
            }

            if (returnValue.get(position).getCustName()!=null &&
                    !returnValue.get(position).getCustName().equals("") &&
                    !returnValue.get(position).getCustName().isEmpty()){
                holder.tv_cus_name.setText(returnValue.get(position).getCustName());
            }else {
                holder.tv_cus_name.setText("-");
            }
            if (returnValue.get(position).getCustMobileNo()!=null &&
                    !returnValue.get(position).getCustMobileNo().equals("") &&
                    !returnValue.get(position).getCustMobileNo().isEmpty()){
                holder.tv_mobile_no.setText(returnValue.get(position).getCustMobileNo());
            }else {
                holder.tv_mobile_no.setText("-");
            }
            if (returnValue.get(position).getCustEmaiIID()!=null &&
                    !returnValue.get(position).getCustEmaiIID().equals("") &&
                    !returnValue.get(position).getCustEmaiIID().isEmpty()){
                holder.tv_mail.setText(returnValue.get(position).getCustEmaiIID());
            }else {
                holder.tv_mail.setText("-");
            }
            if (returnValue.get(position).getContactPerson()!=null &&
                    !returnValue.get(position).getContactPerson().equals("") &&
                    !returnValue.get(position).getContactPerson().isEmpty()){
                holder.tv_contact.setText(returnValue.get(position).getContactPerson());
            }else {
                holder.tv_contact.setText("-");
            }
            if (returnValue.get(position).getOutletType()!=null &&
                    !returnValue.get(position).getOutletType().equals("") &&
                    !returnValue.get(position).getOutletType().isEmpty()){
                holder.tv_outlet.setText(returnValue.get(position).getOutletType());
            }else {
                holder.tv_outlet.setText("-");
            }
            if (returnValue.get(position).getLandMark()!=null &&
                    !returnValue.get(position).getLandMark().equals("") &&
                    !returnValue.get(position).getLandMark().isEmpty()){
                holder.tv_landmark.setText(returnValue.get(position).getLandMark());
            }else {
                holder.tv_landmark.setText("-");
            }
            if (returnValue.get(position).getAddress()!=null &&
                    !returnValue.get(position).getAddress().equals("") &&
                    !returnValue.get(position).getAddress().isEmpty()) {
                holder.tv_address.setText(returnValue.get(position).getAddress());
            }else {
                holder.tv_address.setText("-");
            }
            holder.ll_edit_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new OnKeyboardHide(mActivity,view);

                    if (holder.expandable_layout.isExpanded()) {
                        holder.expandable_layout.collapse();
                        holder.tv_sub_date.setText("");
                        holder.tv_sub_date.setHint("yyyy-MM-dd");
                    } else {
                        holder.tv_sub_date.setText("");
                        holder.tv_sub_date.setHint("yyyy-MM-dd");
                        adapterCustomList.notifyDataSetChanged();
                        holder.expandable_layout.expand();
                    }
                }
            });
            
            holder.ll_sub_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new OnKeyboardHide(mActivity,v);
                    new OnDatePicker(mActivity,holder.tv_sub_date);
                }
            });
            holder.tv_img_upload_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new OnKeyboardHide(mActivity,v);
                    pickFromCamera();
                }
            });
            holder.tv_upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new OnKeyboardHide(mActivity,v);
                    if (holder.tv_sub_date.getText().toString().equals("") || holder.tv_sub_date.getText().toString().isEmpty()){
                        new SnackbarIps(holder.tv_sub_date,"Please Select Date");
                    }else if (holder.et_status.getText().toString().equals("") || holder.et_status.getText().toString().isEmpty()){
                        new SnackbarIps(holder.tv_sub_date,"Please Enter Statud");
                    }else if (holder.et_remark.getText().toString().equals("") || holder.et_remark.getText().toString().isEmpty()){
                        new SnackbarIps(holder.tv_sub_date,"Please Enter Remarks");
                    }else if (uriCameraImage!=null && (uriCameraImage.toString().equals("") || uriCameraImage.toString().isEmpty())){
                        new SnackbarIps(holder.tv_sub_date,"Please Upload the Image");
                    }else {
                        doCallUpdate(returnValue.get(holder.getAdapterPosition()).getCustMobileNo(),
                                returnValue.get(holder.getAdapterPosition()).getContactPerson(),
                                returnValue.get(holder.getAdapterPosition()).getLandMark(),
                                returnValue.get(holder.getAdapterPosition()).getAddress(),
                                returnValue.get(holder.getAdapterPosition()).getOutletType(),
                                returnValue.get(holder.getAdapterPosition()).getCustID(),
                                holder.et_status.getText().toString(),holder.et_remark.getText().toString(),
                                uriCameraImage);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return returnValue.size();
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {

            TextView tv_cus_name,tv_edit_user,tv_mobile_no,tv_mail,tv_contact,
                    tv_outlet,tv_landmark,tv_address,tv_sub_date,tv_upload;
            TextView tv_mobile_icon,tv_mail_icon,tv_contact_icon,tv_outlet_icon,
                    tv_landmark_icon,tv_address_icon,tv_sub_date_icon,tv_img_upload_icon;
            LinearLayout ll_edit_user,ll_option,ll_sub_date;
            EditText et_status,et_remark;
            ExpandableLayout expandable_layout;

            private MyViewHolder(@NonNull View itemView) {
                super(itemView);

                tv_cus_name = itemView.findViewById(R.id.tv_cus_name);
                tv_edit_user = itemView.findViewById(R.id.tv_edit_user);
                tv_mobile_no = itemView.findViewById(R.id.tv_mobile_no);
                tv_mail = itemView.findViewById(R.id.tv_mail);
                tv_contact = itemView.findViewById(R.id.tv_contact);
                tv_outlet = itemView.findViewById(R.id.tv_outlet);
                tv_landmark = itemView.findViewById(R.id.tv_landmark);
                tv_address = itemView.findViewById(R.id.tv_address);

                tv_mobile_icon = itemView.findViewById(R.id.tv_mobile_icon);
                tv_mail_icon = itemView.findViewById(R.id.tv_mail_icon);
                tv_contact_icon = itemView.findViewById(R.id.tv_contact_icon);
                tv_outlet_icon = itemView.findViewById(R.id.tv_outlet_icon);
                tv_landmark_icon = itemView.findViewById(R.id.tv_landmark_icon);
                tv_address_icon = itemView.findViewById(R.id.tv_address_icon);
                tv_sub_date_icon = itemView.findViewById(R.id.tv_sub_date_icon);
                tv_img_upload_icon = itemView.findViewById(R.id.tv_img_upload_icon);

                ll_edit_user = itemView.findViewById(R.id.ll_edit_user);
                ll_option = itemView.findViewById(R.id.ll_option);
                ll_sub_date = itemView.findViewById(R.id.ll_sub_date);
                tv_sub_date = itemView.findViewById(R.id.tv_sub_date);
                et_status = itemView.findViewById(R.id.et_status);
                et_remark = itemView.findViewById(R.id.et_remark);
                tv_upload = itemView.findViewById(R.id.tv_upload);
                expandable_layout = itemView.findViewById(R.id.expandable_layout);

                Icomoon.imageLogo.apply(mActivity,tv_edit_user);
                Icomoon.imageLogo.apply(mActivity,tv_mobile_icon);
                Icomoon.imageLogo.apply(mActivity,tv_mail_icon);
                Icomoon.imageLogo.apply(mActivity,tv_contact_icon);
                Icomoon.imageLogo.apply(mActivity,tv_outlet_icon);
                Icomoon.imageLogo.apply(mActivity,tv_landmark_icon);
                Icomoon.imageLogo.apply(mActivity,tv_address_icon);
                Icomoon.imageLogo.apply(mActivity,tv_sub_date_icon);
                Icomoon.imageLogo.apply(mActivity,tv_img_upload_icon);
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

    private void doCallUpdate(String mobile_no,String contactPerson,String landmark,String address,
                              String outlet,String customId,String status,String remarks,Uri decodeImage) {
        myDialog = DialogsUtils.showProgressDialog(this, "Update Daily Sales");
        myDialog.show();
        try {
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
            String formattedDate = df.format(c);

            allItems.put("EmpID", SharedPreference.getDefaults(this,TAG_ACCESS_KEY));
            allItems.put("CustMobileNo", mobile_no);
            allItems.put("NameOfOutlet", "NameOfOutlet");
            allItems.put("ContactPerson", contactPerson);
            allItems.put("LandMark", landmark);
            allItems.put("Address", address);
            allItems.put("OutletType", outlet);
            allItems.put("DSRDate", formattedDate);
            allItems.put("CustID", customId);
            allItems.put("NxtFollwDate", tv_sub_date.getText().toString());
            allItems.put("Status", status);
            allItems.put("Remark", remarks);

            try {
                imageStream = getContentResolver().openInputStream(decodeImage);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                String encodedImage = encodeImage(selectedImage);
                byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                String imgString = Base64.encodeToString(decodedString, Base64.DEFAULT);
                allItems.put("DsrImage", "data:image/jpeg;base64,"+imgString);
            }catch (NullPointerException | NumberFormatException e){
                allItems.put("DsrImage", "demo");
            }catch (FileNotFoundException e) {
                e.printStackTrace();
            }catch (OutOfMemoryError e){
                allItems.put("DsrImage", "demo");
            }catch (Exception e){
                allItems.put("DsrImage", "demo");
            }
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = (JsonObject) jsonParser.parse(String.valueOf(allItems));

            Call<ApplyLeaveRes> saveDSR = apiInterface.doSaveDSR(jsonObject);
            saveDSR.enqueue(new Callback<ApplyLeaveRes>() {
                @Override
                public void onResponse(@NonNull Call<ApplyLeaveRes> call,@NonNull Response<ApplyLeaveRes> response) {

                    if (response.code() == 200){
                        assert response.body() != null;
                        if (response.body().getStatusCode()!=null && response.body().getStatusCode().equals("00")){
                            if (response.body().getStatusDescription()!=null)
                                new SnackbarIps(ll_sub_date, response.body().getStatusDescription());
                            if (myDialog!=null && myDialog.isShowing())
                                myDialog.dismiss();
                            doListCusList();
                        }else {
                            if (response.body().getStatusDescription()!=null)
                                new SnackbarIps(ll_sub_date, response.body().getStatusDescription());
                        }
                    }else {
                        new SnackbarIps(ll_sub_date,"Server Error");
                    }
                    if (myDialog!=null && myDialog.isShowing())
                        myDialog.dismiss();
                }

                @Override
                public void onFailure(@NonNull Call<ApplyLeaveRes> call,@NonNull Throwable t) {
                    new SnackbarIps(ll_sub_date,"Internet or Server Error");
                    if (myDialog!=null && myDialog.isShowing())
                        myDialog.dismiss();

                    Log.e(TAG,"Throwable "+t.getMessage());
                }
            });
        }catch (OutOfMemoryError e){
            new SnackbarIps(ll_sub_date,"Out of Memory Exception");
            Log.e(TAG,"NumberFormatException "+e.getMessage());
            if (myDialog!=null && myDialog.isShowing())
                myDialog.dismiss();
        }catch (NullPointerException | NumberFormatException e){
            if (myDialog!=null && myDialog.isShowing())
                myDialog.dismiss();
            new SnackbarIps(ll_sub_date,"Internet or Server Error");
            Log.e(TAG,"NumberFormatException sdfgdf "+e.getMessage());
        }catch (Exception e){
            if (myDialog!=null && myDialog.isShowing())
                myDialog.dismiss();
            Log.e(TAG,"Exception "+e.getMessage());
            new SnackbarIps(ll_sub_date,"Internet or Server Error");
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode){
                case 200:
                    Log.e(TAG, "Higher Version --> ");
                    File imgFile = new File(pictureFilePath);
                    String filePath = imgFile.getPath();
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                    uriCameraImage = getImageUri(this,bitmap);
                    break;
                case 201:
                    Log.e(TAG, "Lower Version -->");
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    uriCameraImage = getImageUri(this,photo);
                    break;
            }
        }
    }

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissions = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 107;
    String pictureFilePath = "";

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss",Locale.getDefault()).format(new Date());
        String pictureFile = "IPS_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(pictureFile, ".jpg", storageDir);
        pictureFilePath = image.getAbsolutePath();
        return image;
    }

    private void sendTakePictureIntent() {

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION, true);
        cameraIntent.addFlags(FLAG_GRANT_READ_URI_PERMISSION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            cameraIntent.addFlags(FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        }
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {

            File pictureFile = null;
            try {
                pictureFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this,
                        "Photo file can't be created, please try again",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (pictureFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.mirrormind.ipsgroup.fileprovider",
                        pictureFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, 200);
            }
        }
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();
        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }
        return result;
    }
    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }
    public void pickFromCamera() {

        permissions.add(CAMERA);
        permissions.add(READ_EXTERNAL_STORAGE);
        permissions.add(WRITE_EXTERNAL_STORAGE);
        permissionsToRequest = findUnAskedPermissions(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                Log.e(TAG, "higher");
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            } else {
                if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    sendTakePictureIntent();
                    Log.e(TAG, "higher");
                }
            }
        } else {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, 201);
            Log.e(TAG, "lower");
        }
    }

    public Uri getImageUri(Activity inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 60, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage,
                "Title", null);
        return Uri.parse(path);
    }
    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,60,baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }
}
