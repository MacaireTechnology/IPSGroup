package com.mirrormind.ipsgroup.login_reg;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mirrormind.ipsgroup.MainActivity;
import com.mirrormind.ipsgroup.R;
import com.mirrormind.ipsgroup.onDialog.OnDialog;

import github.ishaan.buttonprogressbar.ButtonProgressBar;
import retrofit.ApiClient;
import retrofit.ApiInterface;
import retrofit.response.AccessKeyRes;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uihelper.SnackbarIps;
import uihelper.icomoon.Icomoon;
import uihelper.onKeyboard.OnKeyboardHide;

public class LoginOtp extends AppCompatActivity implements View.OnClickListener {

    TextView otp_icon, tv_back;
    EditText ed_enterotp;
    ButtonProgressBar btn_verify;
    ApiInterface apiInterface;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginotp);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        inintview();
        initlistener();

    }


    private void inintview() {
        otp_icon = findViewById(R.id.otp_icon);
        btn_verify = findViewById(R.id.btn_verify);
        ed_enterotp = findViewById(R.id.ed_enterotp);
        tv_back = findViewById(R.id.tv_back);

    }

    private void initlistener() {
        otp_icon.setOnClickListener(this);
        btn_verify.setOnClickListener(this);
        ed_enterotp.setOnClickListener(this);
        tv_back.setOnClickListener(this);


        Icomoon.imageLogo.apply(this, otp_icon);
        Icomoon.imageLogo.apply(this, tv_back);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_verify:
                new OnKeyboardHide(this,view);
                doLoginCondition();
                break;
            case R.id.tv_back:
                super.onBackPressed();
                break;
        }
    }

    private void doLoginCondition() {

        new OnKeyboardHide(this,ed_enterotp);

        if (ed_enterotp.getText().toString().equals("")) {
            new SnackbarIps(btn_verify, "Please Enter a Valid Otp");
        } else {
            doCallAccessKey();
            btn_verify.startLoader();
        }
    }

    private void doCallAccessKey() {
        Call<AccessKeyRes> keyResCall = apiInterface.doLoginOtp(ed_enterotp.getText().toString());
        keyResCall.enqueue(new Callback<AccessKeyRes>() {
            @Override
            public void onResponse(Call<AccessKeyRes> call, Response<AccessKeyRes> response) {
                if (response.code() == 200) {
                    if (response.body().getStatusCode().equals("00")) {
                        btn_verify.stopLoader();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();
                    } else {
                        btn_verify.stopLoader();
                        btn_verify.reset();
                        new OnDialog(LoginOtp.this,1,"Alert!!!","Not a valid user");
//                        new SnackbarIps(btn_verify, "Not a valid user", 2000);
                    }

                } else {
                    btn_verify.stopLoader();
                    btn_verify.reset();
                    new SnackbarIps(btn_verify, "Server Error", 2000);
                }
            }

            @Override
            public void onFailure(Call<AccessKeyRes> call, Throwable t) {
                btn_verify.stopLoader();
                btn_verify.reset();

                new SnackbarIps(btn_verify, "Network Or Server Error", 2000);
            }
        });
    }

}



