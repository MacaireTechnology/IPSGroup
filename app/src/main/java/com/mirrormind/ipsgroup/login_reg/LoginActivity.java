package com.mirrormind.ipsgroup.login_reg;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.mirrormind.ipsgroup.MainActivity;
import com.mirrormind.ipsgroup.R;
import com.mirrormind.ipsgroup.onDialog.OnDialog;

import github.ishaan.buttonprogressbar.ButtonProgressBar;
import retrofit.ApiClient;
import retrofit.ApiInterface;
import retrofit.response.LoginRes;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uihelper.SnackbarIps;
import uihelper.onKeyboard.OnKeyboardHide;
import uihelper.sharedPref.GlobalData;
import uihelper.sharedPref.SharedPreference;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    ButtonProgressBar bt_login;
    EditText ed_username, ed_password;

    ApiInterface apiInterface;
    Activity mActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mActivity = this;

        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        initid();
        initlistener();

    }


    private void initid() {
        bt_login = findViewById(R.id.bt_login);
        ed_username = findViewById(R.id.ed_username);
        ed_password = findViewById(R.id.ed_password);
    }

    private void initlistener() {
        bt_login.setOnClickListener(this);
        ed_username.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.bt_login:
                new OnKeyboardHide(this,view);
                doLoginCondition();
                break;
            case R.id.tv_forgot:
                Toast.makeText(getApplicationContext(), "Are u Forget yout password", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void doLoginCondition() {

        if (ed_username.getText().toString().equals("")) {
            new SnackbarIps(bt_login, "Please Enter a Valid Username");
        } else if (ed_password.getText().toString().equals("")) {
            new SnackbarIps(bt_login, "Please Enter a Valid Password");
        } else {
            doCallAccessKey();
            bt_login.startLoader();
        }
    }

    private void doCallAccessKey() {
        Call<LoginRes> logResCall = apiInterface.doLogin(ed_username.getText().toString(), ed_password.getText().toString());
        logResCall.enqueue(new Callback<LoginRes>() {
            @Override
            public void onResponse(@NonNull Call<LoginRes> call, @NonNull Response<LoginRes> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    if (response.body().getStatusCode().equals("00")) {
                        SharedPreference.setDefaults(mActivity,GlobalData.TAG_EMP_ID,response.body().getEmpID());
                        SharedPreference.setDefaults(mActivity,GlobalData.TAG_USER_NAME,response.body().getUserName());
                        bt_login.stopLoader();
                        startActivity(new Intent(mActivity, MainActivity.class));
                        finish();
                    } else {
                        bt_login.stopLoader();
                        bt_login.reset();
                        new OnDialog(LoginActivity.this,1,"Alert!!!","Not a valid user");
//                        new SnackbarIps(bt_login, "" + "Not a valid user");
                    }

                } else {
                    bt_login.stopLoader();
                    bt_login.reset();
                    new SnackbarIps(bt_login, "Server Error");
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginRes> call,@NonNull  Throwable t) {
                bt_login.stopLoader();
                bt_login.reset();
                new SnackbarIps(bt_login, "Network Or Server Error", 2000);
            }
        });
    }

    @Override
    public void onBackPressed() {

    }
}
