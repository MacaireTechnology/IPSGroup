package com.mirrormind.ipsgroup.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.mirrormind.ipsgroup.Dialog.DialogsUtils;
import com.mirrormind.ipsgroup.R;
import com.mirrormind.ipsgroup.onDialog.OnDialog;
import retrofit.ApiClient;
import retrofit.ApiInterface;
import retrofit.response.contact.ContactRes;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uihelper.icomoon.Icomoon;
import uihelper.sharedPref.GlobalData;
import uihelper.sharedPref.SharedPreference;

public class Profile_frg extends Fragment implements View.OnClickListener,GlobalData{

    private static final String TAG = Profile_frg.class.getSimpleName();
    View view;
    TextView tv_person_name,tv_back,tv_logout;
    ApiInterface apiInterface;
    ProgressDialog myDialog;
    TextView tv_mobile_no,tv_designation,tv_department,tv_mail,tv_type;
    ImageView iv_profile;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.profile_fragment, container, false);

        tv_person_name = view.findViewById(R.id.tv_person_name);
        tv_back = view.findViewById(R.id.tv_back);
        tv_logout = view.findViewById(R.id.tv_logout);
        tv_mobile_no = view.findViewById(R.id.tv_mobile_no);
        tv_designation = view.findViewById(R.id.tv_designation);
        tv_department = view.findViewById(R.id.tv_department);
        tv_mail = view.findViewById(R.id.tv_mail);
        tv_type = view.findViewById(R.id.tv_type);
        iv_profile = view.findViewById(R.id.iv_profile);

        tv_back.setOnClickListener(this);
        tv_logout.setOnClickListener(this);
        Icomoon.imageLogo.apply(getActivity(),tv_back);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        myDialog = DialogsUtils.showProgressDialog(getActivity(), "Get Profile Details");
        myDialog.show();

        onDetails();
        doCallProfile();

        return view;
    }

    private void onDetails() {

        if (SharedPreference.getDefaults(getContext(), GlobalData.TAG_USER_NAME)!=null &&
                !SharedPreference.getDefaults(getContext(), GlobalData.TAG_USER_NAME).equals("") &&
                !SharedPreference.getDefaults(getContext(), GlobalData.TAG_USER_NAME).isEmpty()){
            tv_person_name.setText(SharedPreference.getDefaults(getContext(), GlobalData.TAG_USER_NAME));
        }else {
            tv_person_name.setText("-");
        }

        if (SharedPreference.getDefaults(getContext(), GlobalData.TAG_EMP_NUMBER)!=null &&
                !SharedPreference.getDefaults(getContext(), GlobalData.TAG_EMP_NUMBER).equals("") &&
                !SharedPreference.getDefaults(getContext(), GlobalData.TAG_EMP_NUMBER).isEmpty()){
            tv_mobile_no.setText(SharedPreference.getDefaults(getContext(), GlobalData.TAG_EMP_NUMBER));
        }else {
            tv_mobile_no.setText("-");
        }

        if (SharedPreference.getDefaults(getContext(), GlobalData.TAG_EMP_EMAIL_ID)!=null &&
                !SharedPreference.getDefaults(getContext(), GlobalData.TAG_EMP_EMAIL_ID).equals("") &&
                !SharedPreference.getDefaults(getContext(), GlobalData.TAG_EMP_EMAIL_ID).isEmpty()){
            tv_mail.setText(SharedPreference.getDefaults(getContext(), GlobalData.TAG_EMP_EMAIL_ID));
        }else {
            tv_mail.setText("-");
        }

        if (SharedPreference.getDefaults(getContext(), GlobalData.TAG_EMP_DEPT)!=null &&
                !SharedPreference.getDefaults(getContext(), GlobalData.TAG_EMP_DEPT).equals("") &&
                !SharedPreference.getDefaults(getContext(), GlobalData.TAG_EMP_DEPT).isEmpty()){
            tv_department.setText(SharedPreference.getDefaults(getContext(), GlobalData.TAG_EMP_DEPT));
        }else {
            tv_department.setText("-");
        }

        if (SharedPreference.getDefaults(getContext(), GlobalData.TAG_EMP_DESIGNATION)!=null &&
                !SharedPreference.getDefaults(getContext(), GlobalData.TAG_EMP_DESIGNATION).equals("") &&
                !SharedPreference.getDefaults(getContext(), GlobalData.TAG_EMP_DESIGNATION).isEmpty()){
            tv_designation.setText(SharedPreference.getDefaults(getContext(), GlobalData.TAG_EMP_DESIGNATION));
        }else {
            tv_designation.setText("-");
        }

        if (SharedPreference.getDefaults(getContext(), GlobalData.TAG_EMP_TYPE)!=null &&
                !SharedPreference.getDefaults(getContext(), GlobalData.TAG_EMP_TYPE).equals("") &&
                !SharedPreference.getDefaults(getContext(), GlobalData.TAG_EMP_TYPE).isEmpty()){
            tv_type.setText(SharedPreference.getDefaults(getContext(), GlobalData.TAG_EMP_TYPE));
        }else {
            tv_type.setText("-");
        }

        if (SharedPreference.getDefaults(getContext(), GlobalData.TAG_EMP_IMAGE)!=null &&
                !SharedPreference.getDefaults(getContext(), GlobalData.TAG_EMP_IMAGE).equals("") &&
                !SharedPreference.getDefaults(getContext(), GlobalData.TAG_EMP_IMAGE).isEmpty()){
            Glide.with(this).load(SharedPreference.getDefaults(getContext(), GlobalData.TAG_EMP_IMAGE))
                    .error(R.drawable.splash_logo).dontAnimate().fitCenter().into(iv_profile);
        }else {
            Glide.with(this).load(R.drawable.splash_logo)
                    .placeholder(R.drawable.splash_logo)
                    .error(R.drawable.splash_logo).dontAnimate().fitCenter().into(iv_profile);
        }
    }

    private void doCallProfile() {

        Call<ContactRes> doContact = apiInterface.doProfile(SharedPreference.getDefaults(getActivity(),TAG_EMP_ID));
        doContact.enqueue(new Callback<ContactRes>() {
            @Override
            public void onResponse(@NonNull Call<ContactRes> call,@NonNull Response<ContactRes> response) {
                if (response.code() == 200){
                    assert response.body() != null;
                    if (response.body().getStatusCode().equals("00")) {
                        if (response.body().getReturnValue().size()>0) {

                            for (int i=0;i<response.body().getReturnValue().size();i++) {
                                try {
                                    if (response.body().getReturnValue().get(i).getUserName()!=null)
                                        SharedPreference.setDefaults(getActivity(), GlobalData.TAG_USER_NAME,response.body().getReturnValue().get(i).getUserName());
                                    else
                                        SharedPreference.setDefaults(getActivity(), GlobalData.TAG_USER_NAME,"");

                                    if (response.body().getReturnValue().get(i).getMobileNumber()!=null)
                                        SharedPreference.setDefaults(getActivity(), GlobalData.TAG_EMP_NUMBER,response.body().getReturnValue().get(i).getMobileNumber());
                                    else
                                        SharedPreference.setDefaults(getActivity(), GlobalData.TAG_EMP_NUMBER,"");

                                    if (response.body().getReturnValue().get(i).getDesignation()!=null)
                                        SharedPreference.setDefaults(getActivity(), GlobalData.TAG_EMP_DESIGNATION,response.body().getReturnValue().get(i).getDesignation());
                                    else
                                        SharedPreference.setDefaults(getActivity(), GlobalData.TAG_EMP_DESIGNATION,"");

                                    if (response.body().getReturnValue().get(i).getPrimaryEmail()!=null)
                                        SharedPreference.setDefaults(getActivity(), GlobalData.TAG_EMP_EMAIL_ID,response.body().getReturnValue().get(i).getPrimaryEmail());
                                    else
                                        SharedPreference.setDefaults(getActivity(), GlobalData.TAG_EMP_EMAIL_ID,"");

                                    if (response.body().getReturnValue().get(i).getDepartment()!=null)
                                        SharedPreference.setDefaults(getActivity(), GlobalData.TAG_EMP_DEPT,response.body().getReturnValue().get(i).getDepartment());
                                    else
                                        SharedPreference.setDefaults(getActivity(), GlobalData.TAG_EMP_DEPT,"");

                                    if (response.body().getReturnValue().get(i).getUserTypeDesc()!=null)
                                        SharedPreference.setDefaults(getActivity(), GlobalData.TAG_EMP_TYPE,response.body().getReturnValue().get(i).getUserTypeDesc());
                                    else
                                        SharedPreference.setDefaults(getActivity(), GlobalData.TAG_EMP_TYPE,"");

                                    if (response.body().getReturnValue().get(i).getProfileImage()!=null)
                                        SharedPreference.setDefaults(getActivity(), GlobalData.TAG_EMP_IMAGE,response.body().getReturnValue().get(i).getProfileImage());
                                    else
                                        SharedPreference.setDefaults(getActivity(), GlobalData.TAG_EMP_IMAGE,"");

                                    doCallProfile();
                                }catch (NumberFormatException | NullPointerException e){
                                    e.printStackTrace();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                            if (myDialog!=null && myDialog.isShowing()) {
                                myDialog.dismiss();
                            }
                        }else {
                            if (myDialog!=null && myDialog.isShowing()) {
                                myDialog.dismiss();
                            }
                            Log.e(TAG,"No Contact list");
                        }
                    }else {
                        if (myDialog!=null && myDialog.isShowing()) {
                            myDialog.dismiss();
                        }
                        Log.e(TAG,"No Data Else part");
                    }
                }else {
                    if (myDialog!=null && myDialog.isShowing()) {
                        myDialog.dismiss();
                    }
                    Log.e(TAG,"Server Error");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ContactRes> call,@NonNull Throwable t) {
                Log.e(TAG,"Internet or Server Error");
                if (myDialog!=null && myDialog.isShowing()) {
                    myDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                getFragmentManager().popBackStack();
                break;
            case R.id.tv_logout:
                new OnDialog(getActivity(),2,
                        "Logout","Are you sure to Logout");
                break;
        }
    }
}
