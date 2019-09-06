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
import com.mirrormind.ipsgroup.teamPerformance.Customers;
import com.mirrormind.ipsgroup.teamPerformance.SubmissionForm;
import uihelper.icomoon.Icomoon;
import uihelper.sharedPref.GlobalData;

public class TeamPerfm_frg extends Fragment implements View.OnClickListener, GlobalData {

    Activity mActivity;
    View view;
    TextView tv_back,tv_dsr_icon,tv_submission_icon,tv_customer_icon;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.teamperfm_fragment, container, false);

        mActivity = getActivity();
        tv_back = view.findViewById(R.id.tv_back);
        tv_dsr_icon = view.findViewById(R.id.tv_dsr_icon);
        tv_submission_icon = view.findViewById(R.id.tv_submission_icon);
        tv_customer_icon = view.findViewById(R.id.tv_customer_icon);

        tv_back.setOnClickListener(this);
        view.findViewById(R.id.ll_dsr).setOnClickListener(this);
        view.findViewById(R.id.ll_submission_form).setOnClickListener(this);
        view.findViewById(R.id.ll_customer).setOnClickListener(this);

        Icomoon.imageLogo.apply(mActivity, tv_back);
        Icomoon.imageLogo.apply(mActivity, tv_dsr_icon);
        Icomoon.imageLogo.apply(mActivity, tv_submission_icon);
        Icomoon.imageLogo.apply(mActivity, tv_customer_icon);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_top:
                break;
            case R.id.tv_back:
                getFragmentManager().popBackStack();
                break;
            case R.id.ll_dsr:
//                startActivity(new Intent(getActivity(), DailySales.class));
                startActivity(new Intent(getActivity(), SubmissionForm.class).putExtra(TAG_PAGE_IDENTIFY,"111"));
//                startActivity(new Intent(getActivity(), SubmissionForm.class).putExtra(TAG_PAGE_IDENTIFY,"111"));
                break;
            case R.id.ll_submission_form:
                startActivity(new Intent(getActivity(), SubmissionForm.class).putExtra(TAG_PAGE_IDENTIFY,"000"));
                break;
            case R.id.ll_customer:
                startActivity(new Intent(getActivity(), Customers.class));
                break;
        }
    }
}