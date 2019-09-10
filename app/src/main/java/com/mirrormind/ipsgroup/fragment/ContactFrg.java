package com.mirrormind.ipsgroup.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mirrormind.ipsgroup.Dialog.DialogsUtils;
import com.mirrormind.ipsgroup.R;

import java.util.ArrayList;
import java.util.List;

import retrofit.ApiClient;
import retrofit.ApiInterface;
import retrofit.response.approval_attend.ApprovalAttendDetails;
import retrofit.response.contact.ContactDetails;
import retrofit.response.contact.ContactRes;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uihelper.SnackbarIps;
import uihelper.icomoon.Icomoon;
import uihelper.onKeyboard.OnKeyboardHide;

public class ContactFrg extends Fragment implements View.OnClickListener {

    private static final String TAG = ContactFrg.class.getSimpleName();
    Activity mActivity;
    RecyclerView rv_adminmng_dashboard;
    View view;
    TextView tv_back,tv_header_name,tv_search_icon,tv_back_search;
    ApiInterface apiInterface;
    LinearLayout ll_header,ll_search;
    EditText et_search;
    ContactAdapter contactAdapter;
    ProgressDialog myDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.attendance_fragment, container, false);

        mActivity = getActivity();

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        rv_adminmng_dashboard = view.findViewById(R.id.rv_adminmng_dashboard);
        tv_back = view.findViewById(R.id.tv_back);
        tv_header_name = view.findViewById(R.id.tv_header_name);
        tv_search_icon = view.findViewById(R.id.tv_search_icon);
        ll_header = view.findViewById(R.id.ll_header);
        ll_search = view.findViewById(R.id.ll_search);
        et_search = view.findViewById(R.id.et_search);
        tv_back_search = view.findViewById(R.id.tv_back_search);

        tv_header_name.setText("Contacts");
        tv_back.setOnClickListener(this);
        view.findViewById(R.id.fab_layout).setVisibility(View.GONE);
        view.findViewById(R.id.tv_search_icon).setVisibility(View.VISIBLE);

        Icomoon.imageLogo.apply(mActivity, tv_back);
        Icomoon.imageLogo.apply(mActivity, tv_search_icon);
        Icomoon.imageLogo.apply(mActivity, tv_back_search);

        view.findViewById(R.id.ll_header).setOnClickListener(this);
        view.findViewById(R.id.ll_search).setOnClickListener(this);
        view.findViewById(R.id.tv_search_icon).setOnClickListener(this);
        view.findViewById(R.id.tv_back_search).setOnClickListener(this);

        rv_adminmng_dashboard.setLayoutManager(new GridLayoutManager(mActivity, 2));

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (contactAdapter!=null)
                    contactAdapter.getFilter().filter(charSequence);
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (contactAdapter!=null)
                    contactAdapter.getFilter().filter(charSequence);
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        myDialog = DialogsUtils.showProgressDialog(mActivity, "Listing Contacts");
        myDialog.show();

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
                            rv_adminmng_dashboard.setAdapter(contactAdapter = new ContactAdapter(response.body().getReturnValue()));
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
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.tv_back:
                getFragmentManager().popBackStack();
                break;
            case R.id.tv_back_search:
                new OnKeyboardHide(mActivity,et_search);
                et_search.setText("");
                ll_header.setVisibility(View.VISIBLE);
                ll_search.setVisibility(View.GONE);
                break;
            case R.id.tv_search_icon:
                ll_header.setVisibility(View.GONE);
                ll_search.setVisibility(View.VISIBLE);
                break;
        }
    }

    private class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder>
            implements Filterable {

        List<ContactDetails> returnValue,listSearchView;

        public ContactAdapter(List<ContactDetails> returnValue) {
            this.returnValue = returnValue;
            this.listSearchView = returnValue;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adpt_contact,
                    parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

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

            holder.tv_username.setSelected(true);
            holder.tv_department.setSelected(true);
            holder.tv_call.setSelected(true);
            holder.tv_mail.setSelected(true);

            holder.ll_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (returnValue.get(holder.getAdapterPosition()).getMobileNumber()!=null &&
                            !returnValue.get(holder.getAdapterPosition()).getMobileNumber().isEmpty() &&
                            !returnValue.get(holder.getAdapterPosition()).getMobileNumber().equals("")){
                        doCallDialog(returnValue.get(holder.getAdapterPosition()).getMobileNumber());
                    }else {
                        new SnackbarIps(tv_back,"No valid Phone Number");
                    }
                }
            });
            holder.ll_mail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (returnValue.get(holder.getAdapterPosition()).getPrimaryEmail()!=null &&
                            !returnValue.get(holder.getAdapterPosition()).getPrimaryEmail().isEmpty() &&
                            !returnValue.get(holder.getAdapterPosition()).getPrimaryEmail().equals("")){
                        doMailDialog(returnValue.get(holder.getAdapterPosition()).getPrimaryEmail());
                    }else {
                        new SnackbarIps(tv_back,"No valid Mail ID");
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return returnValue.size();
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {

           TextView tv_call_icon,tv_mail_icon,tv_department_icon;
           TextView tv_username,tv_call,tv_mail,tv_department;
           ImageView iv_profile;
           LinearLayout ll_call,ll_mail;

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
                ll_call = itemView.findViewById(R.id.ll_call);
                ll_mail = itemView.findViewById(R.id.ll_mail);

                Icomoon.imageLogo.apply(mActivity,tv_call_icon);
                Icomoon.imageLogo.apply(mActivity,tv_mail_icon);
                Icomoon.imageLogo.apply(mActivity,tv_department_icon);

            }
        }
        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    String charString = charSequence.toString();
                    if (charString.isEmpty()) {
                        returnValue = listSearchView;
                    } else {
                        List<ContactDetails> filteredList = new ArrayList<>();
                        for (ContactDetails row : listSearchView) {

                            if ((row.getEmpFullName()!=null && row.getEmpFullName().toLowerCase().contains(charString.toLowerCase())) ||
                                    (row.getDepartment()!=null && row.getDepartment().toLowerCase().contains(charString.toLowerCase())) ||
                                    (row.getBranchName()!=null && row.getBranchName().toLowerCase().contains(charString.toLowerCase())) ||
                                    (row.getMobileNumber()!=null && row.getMobileNumber().toLowerCase().contains(charString.toLowerCase())) ||
                                    (row.getPrimaryEmail()!=null && row.getPrimaryEmail().toLowerCase().contains(charString.toLowerCase()))) {
                                filteredList.add(row);
                            }
                        }
                        returnValue = filteredList;
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = returnValue;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    returnValue = (ArrayList<ContactDetails>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }

    }
    public void doCallDialog(String phoneNo){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"+phoneNo));
        startActivity(intent);
    }
    public void doMailDialog(String mailId){
        Intent send = new Intent(Intent.ACTION_SENDTO);
        String uriText = "mailto:" + Uri.encode(mailId);
        Uri uri = Uri.parse(uriText);
        send.setData(uri);
        startActivity(Intent.createChooser(send, "Send mail..."));
    }

}