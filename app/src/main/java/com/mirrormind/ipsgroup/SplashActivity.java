package com.mirrormind.ipsgroup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import com.mirrormind.ipsgroup.login_reg.LoginOtp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import uihelper.sharedPref.GlobalData;
import uihelper.sharedPref.SharedPreference;

public class SplashActivity extends Activity implements GlobalData {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("IPSGroup", true);

        if (isFirstRun) {
            SharedPreference.setDefaults(this, GlobalData.TAG_IN_OUT_STATUS, "first");
            SharedPreference.setDefaults(SplashActivity.this,TAG_EMP_ID,"");
            SharedPreference.setDefaults(SplashActivity.this,TAG_ALARM_START_TIME,"");
            // Place your dialog code here to display the dialog
            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("IPSGroup", false)
                    .apply();
        }

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy",Locale.getDefault());
        String formattedDate = df.format(c);
        boolean everyDay = getSharedPreferences("EVERYDAY", MODE_PRIVATE).getBoolean(formattedDate, true);
        if (everyDay) {
            SharedPreference.setDefaults(this, GlobalData.TAG_IN_OUT_STATUS, "first");
            // Place your dialog code here to display the dialog
            getSharedPreferences("EVERYDAY", MODE_PRIVATE)
                    .edit()
                    .putBoolean(formattedDate, false)
                    .apply();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (SharedPreference.getDefaults(SplashActivity.this,TAG_EMP_ID)!= null &&
                            !SharedPreference.getDefaults(SplashActivity.this,TAG_EMP_ID).equals("") &&
                            !SharedPreference.getDefaults(SplashActivity.this,TAG_EMP_ID).isEmpty()){
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                    }else {
                        Intent intent = new Intent(SplashActivity.this, LoginOtp.class);
                        startActivity(intent);
                    }
                }catch (NumberFormatException | NullPointerException e) {
                    Intent intent = new Intent(SplashActivity.this, LoginOtp.class);
                    startActivity(intent);
                }catch (Exception e) {
                    Intent intent = new Intent(SplashActivity.this, LoginOtp.class);
                    startActivity(intent);
                }
                finish();
            }
        }, 2500);
    }

}
