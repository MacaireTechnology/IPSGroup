package com.mirrormind.ipsgroup.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.mirrormind.ipsgroup.R;
import com.mirrormind.ipsgroup.SplashActivity;
import com.mirrormind.ipsgroup.login_reg.LoginActivity;
import com.mirrormind.ipsgroup.login_reg.LoginOtp;
import com.mirrormind.ipsgroup.onDialog.OnDialog;

import java.util.Objects;

import uihelper.icomoon.Icomoon;
import uihelper.sharedPref.GlobalData;
import uihelper.sharedPref.SharedPreference;

public class Profile_frg extends Fragment implements View.OnClickListener,GlobalData{

    View view;
    TextView tv_person_name,tv_back,tv_logout;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.profile_fragment, container, false);

        tv_person_name = view.findViewById(R.id.tv_person_name);
        tv_back = view.findViewById(R.id.tv_back);
        tv_logout = view.findViewById(R.id.tv_logout);
        tv_person_name.setText(SharedPreference.getDefaults(getContext(), GlobalData.TAG_USER_NAME));

        tv_back.setOnClickListener(this);
        tv_logout.setOnClickListener(this);
        Icomoon.imageLogo.apply(getActivity(),tv_back);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                getFragmentManager().popBackStack();
                break;
            case R.id.tv_logout:
                new OnDialog(getActivity(),2,"Logout","Are you sure to Logout");
                break;
        }
    }

    TextView tv_cancel,tv_ok;

    public void LogoutAlert() {


        final AlertDialog maindialog = new AlertDialog.Builder(getActivity()).create();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View convertView = inflater.inflate(R.layout.logout_alert, null);
        maindialog.setCanceledOnTouchOutside(true);
        maindialog.setCancelable(true);
        Objects.requireNonNull(maindialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        tv_ok = convertView.findViewById(R.id.tv_ok);
        tv_cancel = convertView.findViewById(R.id.tv_cancel);

        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maindialog.dismiss();
                SharedPreference.setDefaults(getActivity(),TAG_EMP_ID,"");
                startActivity(new Intent(getActivity(), LoginOtp.class));
                getActivity().finish();
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maindialog.dismiss();
            }
        });


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = maindialog.getWindow();
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        lp.width = size.x;
        lp.height = size.y;
        window.setAttributes(lp);
        maindialog.setCancelable(false);
        maindialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        maindialog.setView(convertView);
        maindialog.setTitle("");
        maindialog.getWindow().getAttributes().windowAnimations = R.style.BottomDialogsAnimation; //style id
        maindialog.show();
    }
}
