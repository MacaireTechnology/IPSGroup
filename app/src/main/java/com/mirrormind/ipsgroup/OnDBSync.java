package com.mirrormind.ipsgroup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mirrormind.ipsgroup.Dialog.DialogsUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import db.DatabaseHelper;
import retrofit.ApiClient;
import retrofit.ApiInterface;
import retrofit.response.ImageRes;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uihelper.sharedPref.GlobalData;
import uihelper.sharedPref.SharedPreference;

public class OnDBSync extends AsyncTask<String, String, String> implements GlobalData{

    public static final String TAG = OnDBSync.class.getSimpleName();
    private Activity mActivity;
    private ApiInterface apiInterface;
    private ProgressDialog myDialog;
    private DatabaseHelper databaseHelper;
    private JSONObject allItems = new JSONObject();
    private InputStream imageStream;
    private int finalI;

    public OnDBSync(Activity activity,int progressBarView) {
        this.mActivity = activity;
        databaseHelper = new DatabaseHelper(mActivity);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        myDialog = new ProgressDialog(mActivity);
        if (progressBarView == 2) {
            myDialog = DialogsUtils.showProgressDialog(activity, "Synchronizing");
            myDialog.show();
        }
    }
    @Override
    protected String doInBackground(String... params) {
        databaseHelper = new DatabaseHelper(mActivity);
        try {
            if (databaseHelper.getClockInOut().size() > 0) {
                for (int i = 0; i < databaseHelper.getClockInOut().size(); i++) {
                    try {
                        imageStream = mActivity.getContentResolver().openInputStream(Uri.parse(databaseHelper.getClockInOut().get(i).getImg_uri()));
                        Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        String encodedImage = encodeImage(selectedImage);
                        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                        String imgString = Base64.encodeToString(decodedString, Base64.DEFAULT);
                        try {
                            try {
                                if (databaseHelper.getClockInOut().get(i).getClock_type().equals("ClockOut")) {
                                    if (SharedPreference.getDefaults(mActivity, GlobalData.TAG_CLOCK_OUT_ID) != null &&
                                            !SharedPreference.getDefaults(mActivity, GlobalData.TAG_CLOCK_OUT_ID).equals("-1")) {
                                        allItems.put("ClockInID", SharedPreference.getDefaults(mActivity, GlobalData.TAG_CLOCK_OUT_ID));
                                    } else {
                                        allItems.put("ClockInID", "-1");
                                    }
                                } else {
                                    allItems.put("ClockType", "ClockIn");
                                    allItems.put("ClockInID", -1);
                                }
                                allItems.put("ClockType", databaseHelper.getClockInOut().get(i).getClock_type());
                                allItems.put("img", "data:image/jpeg;base64," + imgString);
                                allItems.put("Empid", databaseHelper.getClockInOut().get(i).getEmp_id());
                                allItems.put("DateTime", databaseHelper.getClockInOut().get(i).getDate_time());
                                allItems.put("Lat", databaseHelper.getClockInOut().get(i).getLatitude());
                                allItems.put("Long", databaseHelper.getClockInOut().get(i).getLongitude());
                                allItems.put("CurrentLocation", databaseHelper.getClockInOut().get(i).getCurrentLocation());

                                JsonParser jsonParser = new JsonParser();
                                JsonObject jsonObject = (JsonObject) jsonParser.parse(String.valueOf(allItems));
                                Call<ImageRes> imgResCall = apiInterface.douploadimg(jsonObject);
                                finalI = i;
                                imgResCall.enqueue(new Callback<ImageRes>() {
                                    @Override
                                    public void onResponse(@NonNull Call<ImageRes> call, @NonNull Response<ImageRes> response) {
                                        if (response.code() == 200) {
                                            assert response.body() != null;
                                            if (response.body().getStatusCode().equals("00")) {

                                                databaseHelper.deleteColor(databaseHelper.getClockInOut().get(finalI).getId());
                                                databaseHelper=new DatabaseHelper(mActivity);
                                                Log.e(TAG, "" + response.body().getStatusDescription());

                                                try {
                                                    if (response.body().getReturnValue() != null) {
                                                        SharedPreference.setDefaults(mActivity, TAG_CLOCK_OUT_ID,
                                                                response.body().getReturnValue());
                                                    } else {
                                                        SharedPreference.setDefaults(mActivity, TAG_CLOCK_OUT_ID,
                                                                "-1");
                                                    }
                                                } catch (NumberFormatException | NullPointerException e) {
                                                    SharedPreference.setDefaults(mActivity, TAG_CLOCK_OUT_ID,
                                                            "-1");
                                                } catch (Exception e) {
                                                    SharedPreference.setDefaults(mActivity, TAG_CLOCK_OUT_ID,
                                                            "-1");
                                                }
                                            } else {
                                                if (myDialog != null && myDialog.isShowing()) {
                                                    myDialog.dismiss();
                                                }
                                                Log.e(TAG, "" + response.body().getStatusDescription());
                                            }

                                        } else {
                                            if (myDialog != null && myDialog.isShowing()) {
                                                myDialog.dismiss();
                                            }
                                            Log.e(TAG, "Server Error");
                                        }
                                    }
                                    @Override
                                    public void onFailure(@NonNull Call<ImageRes> call, @NonNull Throwable t) {
                                        Log.e(TAG, "Internet or Server Error " + t.getMessage());
                                        if (myDialog != null && myDialog.isShowing()) {
                                            myDialog.dismiss();
                                        }
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                                if (myDialog != null && myDialog.isShowing()) {
                                    myDialog.dismiss();
                                }
                                Log.e(TAG, "JSONException " + e.getMessage());
                            }
                        } catch (NullPointerException | NumberFormatException e) {
                            e.printStackTrace();
                            if (myDialog != null && myDialog.isShowing()) {
                                myDialog.dismiss();
                            }
                            Log.e(TAG, "NullNum " + e.getMessage());
                        }
                    }catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Log.e(TAG, "FileNotFoundException " + e.getMessage());
                        if (myDialog != null && myDialog.isShowing()) {
                            myDialog.dismiss();
                        }
                    }
                }
                if (myDialog != null && myDialog.isShowing()) {
                    myDialog.dismiss();
                }
            }else {
                Log.e(TAG, "else");
                if (myDialog != null && myDialog.isShowing())
                    myDialog.dismiss();
            }
        }catch (Exception e){
            e.printStackTrace();
            if (myDialog != null && myDialog.isShowing()) {
                myDialog.dismiss();
            }
            Log.e(TAG, "Exception " + e.getMessage());
        }
        return null;
    }
    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,60,baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }
}