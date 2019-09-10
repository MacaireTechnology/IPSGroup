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

public class AnnouncementFrg extends Fragment implements View.OnClickListener {

    Activity mActivity;
    View view;
    TextView tv_back_icon;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frg_announcement, container, false);

        mActivity = getActivity();
        tv_back_icon = view.findViewById(R.id.tv_back_icon);

        tv_back_icon.setOnClickListener(this);
        Icomoon.imageLogo.apply(mActivity, tv_back_icon);

        return view;
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_back_icon) {
            getFragmentManager().popBackStack();
        }
    }
}
