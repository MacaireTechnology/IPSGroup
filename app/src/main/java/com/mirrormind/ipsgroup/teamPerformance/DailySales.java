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
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mirrormind.ipsgroup.Dialog.DialogsUtils;
import com.mirrormind.ipsgroup.R;
import com.mirrormind.ipsgroup.camera.RunTimePermission;

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
import java.util.Locale;

import retrofit.ApiClient;
import retrofit.ApiInterface;
import retrofit.response.ApplyLeaveRes;
import retrofit.response.ImageRes;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uihelper.SnackbarIps;
import uihelper.icomoon.Icomoon;
import uihelper.sharedPref.GlobalData;
import uihelper.sharedPref.SharedPreference;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.os.Build.VERSION_CODES.M;

public class DailySales extends AppCompatActivity implements View.OnClickListener, GlobalData {

    public static final String TAG = DailySales.class.getSimpleName();
    TextView tv_back_icon,tv_img_upload,tv_upload_sales;
    Calendar myCalendar = Calendar.getInstance();
    TextView tv_mobile_no,tv_cus_name,tv_contact_person,tv_landmark,tv_address,
            tv_outlet_type,tv_sub_date,tv_sub_date_icon;
    EditText et_status,et_remark;
    Intent intent;
    String TAG_CUS_ID_ = "";
    JSONObject allItems = new JSONObject();
    ApiInterface apiInterface;
    ProgressDialog myDialog;
    LinearLayout ll_sub_date;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_dailysales);

        intent = getIntent();
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        tv_back_icon = findViewById(R.id.tv_back_icon);
        tv_img_upload=findViewById(R.id.tv_img_upload);
        tv_upload_sales=findViewById(R.id.tv_upload_sales);
        tv_mobile_no=findViewById(R.id.tv_mobile_no);
        tv_cus_name=findViewById(R.id.tv_cus_name);
        tv_contact_person=findViewById(R.id.tv_contact_person);
        tv_landmark=findViewById(R.id.tv_landmark);
        tv_address=findViewById(R.id.tv_address);
        tv_outlet_type=findViewById(R.id.tv_outlet_type);
        tv_sub_date=findViewById(R.id.tv_sub_date);
        tv_sub_date_icon=findViewById(R.id.tv_sub_date_icon);
        et_status=findViewById(R.id.et_status);
        et_remark=findViewById(R.id.et_remark);
        ll_sub_date=findViewById(R.id.ll_sub_date);

        if (intent!=null){
            if (intent.getStringExtra(TAG_CUS_MOB_NO)!=null){
                tv_mobile_no.setText(intent.getStringExtra(TAG_CUS_MOB_NO)+"");
            }else {
                tv_mobile_no.setText("-");
            }
            if (intent.getStringExtra(TAG_CUS_NAME)!=null){
                tv_cus_name.setText(intent.getStringExtra(TAG_CUS_NAME)+"");
            }else {
                tv_cus_name.setText("-");
            }
            if (intent.getStringExtra(TAG_CONTACT_PERSON)!=null){
                tv_contact_person.setText(intent.getStringExtra(TAG_CONTACT_PERSON)+"");
            }else {
                tv_contact_person.setText("-");
            }
            if (intent.getStringExtra(TAG_LANDMARK)!=null){
                tv_landmark.setText(intent.getStringExtra(TAG_LANDMARK)+"");
            }else {
                tv_landmark.setText("-");
            }
            if (intent.getStringExtra(TAG_ADDRESS)!=null){
                tv_address.setText(intent.getStringExtra(TAG_ADDRESS)+"");
            }else {
                tv_address.setText("-");
            }
            if (intent.getStringExtra(TAG_OUTLET_TYPE)!=null){
                tv_outlet_type.setText(intent.getStringExtra(TAG_OUTLET_TYPE)+"");
            }else {
                tv_outlet_type.setText("-");
            }
            if (intent.getStringExtra(TAG_CUS_ID)!=null){
                TAG_CUS_ID_ = intent.getStringExtra(TAG_CUS_ID);
            }else {
                TAG_CUS_ID_ = "";
            }
        }

        tv_back_icon.setOnClickListener(this);
        tv_img_upload.setOnClickListener(this);
        tv_upload_sales.setOnClickListener(this);
        ll_sub_date.setOnClickListener(this);

        Icomoon.imageLogo.apply(this, tv_back_icon);
        Icomoon.imageLogo.apply(this, tv_img_upload);
        Icomoon.imageLogo.apply(this, tv_sub_date_icon);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back_icon:
                super.onBackPressed();
                break;
            case R.id.ll_sub_date:
                datePicker();
                break;
            case R.id.tv_upload_sales:
                if (tv_sub_date.getText().toString().equals("yyyy-MM-dd") ||
                        tv_sub_date.getText().toString().equals("") ||
                        tv_sub_date.getText().toString().isEmpty()) {
                    new SnackbarIps(tv_upload_sales,"Select Next Follow Date");
                }else if (et_status.getText().toString().isEmpty() || et_status.getText().toString().equals("")) {
                    new SnackbarIps(tv_upload_sales,"Enter Status");
                }else if (et_status.getText().toString().isEmpty() || et_status.getText().toString().equals("")) {
                    new SnackbarIps(tv_upload_sales,"Enter Remarks");
                }else {
                    doCallUpdate();
                }
                break;
        }
    }

    private void doCallUpdate() {
        myDialog = DialogsUtils.showProgressDialog(this, "Update Daily Sales");
        myDialog.show();
        try {
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
            String formattedDate = df.format(c);

            allItems.put("EmpID", SharedPreference.getDefaults(this,TAG_ACCESS_KEY));
            allItems.put("CustMobileNo", tv_mobile_no.getText().toString());
            allItems.put("NameOfOutlet", "NameOfOutlet");
            allItems.put("ContactPerson", tv_contact_person.getText().toString());
            allItems.put("LandMark", tv_landmark.getText().toString());
            allItems.put("Address", tv_address.getText().toString());
            allItems.put("OutletType", tv_outlet_type.getText().toString());
            allItems.put("DSRDate", formattedDate);
            allItems.put("CustID", TAG_CUS_ID_);
            allItems.put("NxtFollwDate", tv_sub_date.getText().toString());
            allItems.put("Status", et_status.getText().toString());
            allItems.put("Remark", et_remark.getText().toString());
            allItems.put("DsrImage", "demo_demo");

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
                                new SnackbarIps(tv_address, response.body().getStatusDescription());
                            onBackPressed();
                        }else {
                            if (response.body().getStatusDescription()!=null)
                                new SnackbarIps(tv_address, response.body().getStatusDescription());
                        }
                    }else {
                        new SnackbarIps(tv_address,"Server Error");
                    }
                    if (myDialog!=null && myDialog.isShowing())
                        myDialog.dismiss();
                }

                @Override
                public void onFailure(@NonNull Call<ApplyLeaveRes> call,@NonNull Throwable t) {
                    new SnackbarIps(tv_address,"Internet or Server Error");
                    if (myDialog!=null && myDialog.isShowing())
                        myDialog.dismiss();

                    Log.e(TAG,"Throwable "+t.getMessage());
                }
            });
        }catch (NullPointerException | NumberFormatException e){
            if (myDialog!=null && myDialog.isShowing())
                myDialog.dismiss();
            new SnackbarIps(tv_address,"Internet or Server Error");
            Log.e(TAG,"NumberFormatException "+e.getMessage());
        }catch (Exception e){
            if (myDialog!=null && myDialog.isShowing())
                myDialog.dismiss();
            Log.e(TAG,"Exception "+e.getMessage());
            new SnackbarIps(tv_address,"Internet or Server Error");
        }
    }
    private void datePicker() {
        Calendar mCurrentDate = Calendar.getInstance();
        int mYear = mCurrentDate.get(Calendar.YEAR);
        int mMonth = mCurrentDate.get(Calendar.MONTH);
        int mDay = mCurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker = new DatePickerDialog(DailySales.this,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedYear,
                                          int selectedMonth, int selectedDay) {
                        myCalendar.set(Calendar.YEAR, selectedYear);
                        myCalendar.set(Calendar.MONTH, selectedMonth);
                        myCalendar.set(Calendar.DAY_OF_MONTH, selectedDay);
                        updateLabel();
                    }
                }, mYear, mMonth, mDay);
        mDatePicker.setTitle("Select date");

        mDatePicker.show();
    }
    private void updateLabel() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        tv_sub_date.setText(sdf.format(myCalendar.getTime()));
        tv_sub_date.setTypeface(tv_sub_date.getTypeface(), Typeface.NORMAL);
        tv_sub_date.setTextColor(getResources().getColor(R.color.light_gray));
    }



}
