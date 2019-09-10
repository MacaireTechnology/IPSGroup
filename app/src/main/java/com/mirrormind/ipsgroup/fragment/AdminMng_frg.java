package com.mirrormind.ipsgroup.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mirrormind.ipsgroup.R;
import com.mirrormind.ipsgroup.adminmanagement.IssueTracker;
import com.mirrormind.ipsgroup.adminmanagement.IssueTrackerList;

import uihelper.icomoon.Icomoon;

public class AdminMng_frg extends Fragment implements View.OnClickListener {

    Activity mActivity;
    View view;
    TextView tv_back_icon;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.adminmng_fragment, container, false);

        mActivity = getActivity();
        tv_back_icon = view.findViewById(R.id.tv_back_icon);

        tv_back_icon.setOnClickListener(this);
        view.findViewById(R.id.rl_help_desk).setOnClickListener(this);
        view.findViewById(R.id.rl_issue_approval).setOnClickListener(this);

        Icomoon.imageLogo.apply(mActivity, tv_back_icon);

        return view;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back_icon:
                getFragmentManager().popBackStack();
                break;
            case R.id.rl_help_desk:
                startActivity(new Intent(mActivity, IssueTracker.class));
                break;
            case R.id.rl_issue_approval:
                startActivity(new Intent(mActivity, IssueTrackerList.class));
                break;
        }
    }
}
