package com.mirrormind.ipsgroup.teamPerformance;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mirrormind.ipsgroup.Dialog.DialogsUtils;
import com.mirrormind.ipsgroup.R;
import org.json.JSONObject;
import retrofit.ApiClient;
import retrofit.ApiInterface;
import retrofit.response.ApplyLeaveRes;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uihelper.SnackbarIps;
import uihelper.icomoon.Icomoon;
import uihelper.sharedPref.GlobalData;
import uihelper.sharedPref.SharedPreference;

public class Customers extends AppCompatActivity implements View.OnClickListener , GlobalData {

    public static final String TAG = Customers.class.getSimpleName();
    TextView tv_back,tv_submit;
    EditText et_cus_code,et_cus_name,et_mobile_no,et_mail_id,et_contact_person,
            et_outlet_type,et_landmark,et_address;
    ApiInterface apiInterface;
    ProgressDialog myDialog;
    JSONObject allItems = new JSONObject();
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        tv_back = findViewById(R.id.tv_back);
        et_cus_code = findViewById(R.id.et_cus_code);
        et_cus_name = findViewById(R.id.et_cus_name);
        et_mobile_no = findViewById(R.id.et_mobile_no);
        et_mail_id = findViewById(R.id.et_mail_id);
        et_contact_person = findViewById(R.id.et_contact_person);
        et_outlet_type = findViewById(R.id.et_outlet_type);
        et_landmark = findViewById(R.id.et_landmark);
        et_address = findViewById(R.id.et_address);
        tv_submit = findViewById(R.id.tv_submit);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        tv_back.setOnClickListener(this);
        tv_submit.setOnClickListener(this);
        Icomoon.imageLogo.apply(this, tv_back);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                super.onBackPressed();
                break;
            case R.id.tv_submit:
                onCheckValidation();
                break;
        }
    }

    private void onCheckValidation() {
        if (et_cus_code.getText().toString().equals("") || et_cus_code.getText().toString().isEmpty()){
            new SnackbarIps(et_cus_code,"Enter Customer code");
        }else if (et_cus_name.getText().toString().equals("") || et_cus_name.getText().toString().isEmpty()){
            new SnackbarIps(et_cus_code,"Enter Customer Name");
        }else if (et_mobile_no.getText().toString().equals("") || et_mobile_no.getText().toString().isEmpty()){
            new SnackbarIps(et_cus_code,"Enter Mobile Number");
        }else if (et_mobile_no.getText().toString().length() > 6 && et_mobile_no.getText().toString().length() < 14){
            new SnackbarIps(et_cus_code,"Enter Mobile Number");
        }else if (et_mail_id.getText().toString().equals("") || et_mail_id.getText().toString().isEmpty()){
            new SnackbarIps(et_cus_code,"Enter Mail Id");
        }else if (et_mail_id.getText().toString().matches(emailPattern)){
            new SnackbarIps(et_cus_code,"Enter Valid E-Mail Id");
        }else if (et_contact_person.getText().toString().equals("") || et_contact_person.getText().toString().isEmpty()){
            new SnackbarIps(et_cus_code,"Enter Contact Person");
        }else if (et_outlet_type.getText().toString().equals("") || et_outlet_type.getText().toString().isEmpty()){
            new SnackbarIps(et_cus_code,"Enter Outlet Type");
        }else if (et_landmark.getText().toString().equals("") || et_landmark.getText().toString().isEmpty()){
            new SnackbarIps(et_cus_code,"Enter Landmark");
        }else if (et_address.getText().toString().equals("") || et_address.getText().toString().isEmpty()){
            new SnackbarIps(et_cus_code,"Enter Address");
        }else {
            new AddCustomers(this, apiInterface).execute();
        }
    }
    private class AddCustomers extends AsyncTask<String, String, String> {

        private Activity mActivity;
        private ApiInterface apiInterface;

        private AddCustomers(Activity activity, ApiInterface apiInterface) {
            this.mActivity = activity;
            this.apiInterface = apiInterface;
            myDialog = DialogsUtils.showProgressDialog(activity, "Save Customer");
            myDialog.show();
            myDialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                allItems.put("EmployeeID",SharedPreference.getDefaults(mActivity,TAG_EMP_ID));
                allItems.put("CustCode",""+et_cus_code.getText().toString());
                allItems.put("CustName",""+et_cus_name.getText().toString());
                allItems.put("CustMobileNo",""+et_mobile_no.getText().toString());
                allItems.put("CustEmaiI_ID",""+et_mail_id.getText().toString());
                allItems.put("ContactPerson",""+et_contact_person.getText().toString());
                allItems.put("OutletType",""+et_outlet_type.getText().toString());
                allItems.put("LandMark",""+et_landmark.getText().toString());
                allItems.put("Address",""+et_address.getText().toString());
                JsonParser jsonParser = new JsonParser();
                JsonObject jsonObject = (JsonObject) jsonParser.parse(String.valueOf(allItems));
                Log.e("allItems ", allItems.toString());

                Call<ApplyLeaveRes> applyLeaveResCall = apiInterface.doSaveCustomers(jsonObject);
                applyLeaveResCall.enqueue(new Callback<ApplyLeaveRes>() {
                    @Override
                    public void onResponse(@NonNull Call<ApplyLeaveRes> call,@NonNull  Response<ApplyLeaveRes> response) {
                        if (response.code() == 200) {
                            assert response.body() != null;
                            if (response.body().getStatusCode().equals("00")) {
                                if (response.body().getStatusDescription()!=null)
                                    new SnackbarIps(et_address, "" + response.body().getStatusDescription());

                                et_cus_code.setText("");
                                et_cus_name.setText("");
                                et_mobile_no.setText("");
                                et_mail_id.setText("");
                                et_contact_person.setText("");
                                et_outlet_type.setText("");
                                et_landmark.setText("");
                                et_address.setText("");

                            } else {
                                if (response.body().getStatusDescription()!=null)
                                    new SnackbarIps(et_address, "" + response.body().getStatusDescription());
                            }
                        } else {
                            new SnackbarIps(et_address, "Server Error");
                        }

                        if (myDialog!=null && myDialog.isShowing()){
                            myDialog.dismiss();
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<ApplyLeaveRes> call,@NonNull  Throwable t) {
                        if (myDialog!=null && myDialog.isShowing()){
                            myDialog.dismiss();
                        }
                        Log.e(TAG,"Throwable "+t.getMessage());
                        new SnackbarIps(et_address, "Network or Server Error");
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
    }
}
