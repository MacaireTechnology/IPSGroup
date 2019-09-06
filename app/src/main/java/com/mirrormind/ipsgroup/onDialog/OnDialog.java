package com.mirrormind.ipsgroup.onDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.mirrormind.ipsgroup.R;
import com.mirrormind.ipsgroup.login_reg.LoginOtp;
import java.util.Objects;
import uihelper.sharedPref.GlobalData;
import uihelper.sharedPref.SharedPreference;

public class OnDialog implements GlobalData {

    private static final String TAG = OnDialog.class.getSimpleName();
    private TextView tv_cancel,tv_ok,tv_msg,tv_title;
    private Activity mActivity;
    private int pageRefer;
    private AlertDialog mainDialog;

    public OnDialog(Activity activity, int pageRefers,String alertHeader,String alertMsg) {

        this.mActivity = activity;
        this.pageRefer = pageRefers;

        mainDialog = new AlertDialog.Builder(activity).create();
        LayoutInflater inflater = activity.getLayoutInflater();
        View convertView = inflater.inflate(R.layout.logout_alert, null);
        mainDialog.setCanceledOnTouchOutside(true);
        mainDialog.setCancelable(true);

        tv_title = convertView.findViewById(R.id.tv_title);
        tv_msg = convertView.findViewById(R.id.tv_msg);
        tv_ok = convertView.findViewById(R.id.tv_ok);
        tv_cancel = convertView.findViewById(R.id.tv_cancel);

        tv_title.setText(alertHeader);
        tv_msg.setText(alertMsg);

        if (pageRefer == 1) {
            tv_cancel.setVisibility(View.GONE);
        }else {
            tv_cancel.setVisibility(View.VISIBLE);
        }
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pageRefer == 1){
                    Log.e(TAG,"On Dialog alert");
                }else if (pageRefer == 2){
                    SharedPreference.setDefaults(mActivity,TAG_EMP_ID,"");
                    mActivity.startActivity(new Intent(mActivity, LoginOtp.class));
                    mActivity.finish();
                }
                mainDialog.dismiss();
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainDialog.dismiss();
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = mainDialog.getWindow();
        Display display = mActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        lp.width = size.x;
        lp.height = size.y;
        assert window != null;
        window.setAttributes(lp);
        mainDialog.setCancelable(false);
        mainDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mainDialog.setView(convertView);
        mainDialog.setTitle("");
        Objects.requireNonNull(mainDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mainDialog.getWindow().getAttributes().windowAnimations = R.style.BottomDialogsAnimation; //style id
        mainDialog.show();
    }
}
