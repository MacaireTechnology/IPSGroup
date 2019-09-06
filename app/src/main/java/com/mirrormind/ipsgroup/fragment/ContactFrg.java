package com.mirrormind.ipsgroup.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mirrormind.ipsgroup.R;

import java.util.List;

import retrofit.ApiClient;
import retrofit.ApiInterface;
import retrofit.response.contact.ContactDetails;
import retrofit.response.contact.ContactRes;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uihelper.icomoon.Icomoon;

public class ContactFrg extends Fragment implements View.OnClickListener {

    private static final String TAG = ContactFrg.class.getSimpleName();
    Activity mActivity;
    RecyclerView rv_adminmng_dashboard;
    View view;
    TextView tv_back,tv_header_name;
    ApiInterface apiInterface;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.attendance_fragment, container, false);

        mActivity = getActivity();

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        rv_adminmng_dashboard = view.findViewById(R.id.rv_adminmng_dashboard);
        tv_back = view.findViewById(R.id.tv_back);
        tv_header_name = view.findViewById(R.id.tv_header_name);

        tv_header_name.setText("Contacts");
        tv_back.setOnClickListener(this);
        view.findViewById(R.id.fab_layout).setVisibility(View.GONE);

        Icomoon.imageLogo.apply(mActivity, tv_back);

        rv_adminmng_dashboard.setLayoutManager(new GridLayoutManager(mActivity, 2));

        doCallContact();

        return view;
    }

    private void doCallContact() {

        Call<ContactRes> doContact = apiInterface.doContact();
        doContact.enqueue(new Callback<ContactRes>() {
            @Override
            public void onResponse(@NonNull Call<ContactRes> call,@NonNull Response<ContactRes> response) {
                if (response.code() == 200){
                    if (response.body().getStatusCode().equals("00")) {
                        if (response.body().getReturnValue().size()>0){
                            rv_adminmng_dashboard.setAdapter(new ContactAdapter(response.body().getReturnValue()));
                        }else {
                            Log.e(TAG,"No Contact list");
                        }
                    }else {
                        Log.e(TAG,"No Data Else part");
                    }
                }else {
                    Log.e(TAG,"Server Error");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ContactRes> call,@NonNull Throwable t) {
                Log.e(TAG,"Internet or Server Error");
            }
        });
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.tv_back:
                getFragmentManager().popBackStack();
                break;
        }
    }

    private class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder> {

        List<ContactDetails> returnValue;

        public ContactAdapter(List<ContactDetails> returnValue) {
            this.returnValue = returnValue;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adpt_contact,
                    parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

            if (returnValue.get(position).getEmpFullName()!=null &&
                    !returnValue.get(position).getEmpFullName().isEmpty() &&
                    !returnValue.get(position).getEmpFullName().equals("")){
                holder.tv_username.setText(returnValue.get(position).getEmpFullName());
            }else {
                holder.tv_username.setText("-");
            }
            if (returnValue.get(position).getDepartment()!=null &&
                    !returnValue.get(position).getDepartment().isEmpty() &&
                    !returnValue.get(position).getDepartment().equals("")){
                holder.tv_department.setText(returnValue.get(position).getDepartment());
            }else {
                holder.tv_department.setText("-");
            }
            if (returnValue.get(position).getMobileNumber()!=null &&
                    !returnValue.get(position).getMobileNumber().isEmpty() &&
                    !returnValue.get(position).getMobileNumber().equals("")){
                holder.tv_call.setText(returnValue.get(position).getMobileNumber());
            }else {
                holder.tv_call.setText("-");
            }
            if (returnValue.get(position).getPrimaryEmail()!=null &&
                    !returnValue.get(position).getPrimaryEmail().isEmpty() &&
                    !returnValue.get(position).getPrimaryEmail().equals("")){
                holder.tv_mail.setText(returnValue.get(position).getPrimaryEmail());
            }else {
                holder.tv_mail.setText("-");
            }

            if (returnValue.get(position).getProfileImage()!=null &&
                    !returnValue.get(position).getProfileImage().isEmpty() &&
                    !returnValue.get(position).getProfileImage().equals("") &&
                    returnValue.get(position).getProfileImage().length()>4){
                Glide.with(mActivity).load(returnValue.get(position).getProfileImage()).error(R.drawable.placeholder_small)
                        .dontAnimate().centerCrop().into(holder.iv_profile);
            }else {
                Glide.with(mActivity).load(R.drawable.placeholder_small)
                        .placeholder(R.drawable.placeholder_small)
                        .error(R.drawable.placeholder_small)
                        .dontAnimate().centerCrop().into(holder.iv_profile);
            }
        }

        @Override
        public int getItemCount() {
            return returnValue.size();
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {

           TextView tv_call_icon,tv_mail_icon,tv_department_icon;
           TextView tv_username,tv_call,tv_mail,tv_department;
           ImageView iv_profile;

            public MyViewHolder(View itemView) {
                super(itemView);

                tv_call_icon = itemView.findViewById(R.id.tv_call_icon);
                tv_mail_icon = itemView.findViewById(R.id.tv_mail_icon);
                tv_department_icon = itemView.findViewById(R.id.tv_department_icon);
                tv_username = itemView.findViewById(R.id.tv_username);
                tv_department = itemView.findViewById(R.id.tv_department);
                tv_call = itemView.findViewById(R.id.tv_call);
                tv_mail = itemView.findViewById(R.id.tv_mail);
                iv_profile = itemView.findViewById(R.id.iv_profile);

                Icomoon.imageLogo.apply(mActivity,tv_call_icon);
                Icomoon.imageLogo.apply(mActivity,tv_mail_icon);
                Icomoon.imageLogo.apply(mActivity,tv_department_icon);

            }
        }
    }

}