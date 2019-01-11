package com.wrms.spraymonitor.background;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;
import com.wrms.spraymonitor.FarmersList;
import com.wrms.spraymonitor.PaymentActivity;
import com.wrms.spraymonitor.RegisterFarmerActivity;
import com.wrms.spraymonitor.SprayDetailThreeActivity;
import com.wrms.spraymonitor.SprayForms;
import com.wrms.spraymonitor.TabedActivity;
import com.wrms.spraymonitor.app.AppController;
import com.wrms.spraymonitor.app.DBAdapter;
import com.wrms.spraymonitor.app.Synchronize;
import com.wrms.spraymonitor.dataobject.FarmerData;
import com.wrms.spraymonitor.utils.ResponseStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Admin on 27-12-2016.
 */

public class UploadFarmerData {

    Context context;
    DBAdapter db;
    private ProgressDialog dialog;
    FarmerData farmerData;
    private String urlString;
    public static ResponseStatus sResponseStatus;
    private static final String TAG = UploadPaymentData.class.getSimpleName();

    public UploadFarmerData(FarmerData farmerData, DBAdapter db) {
        this.farmerData = farmerData;
        this.db = db;
        urlString = Synchronize.FARMER_UPLOAD_API;
    }

    public void uploadFormThroughVolleyRequest(Context context) {
        this.context = context;
        uploadFarmer(getVolleyParams());
    }

    public void uploadFormThroughHttpRequest() {
        uploadFarmer(getHttpParams());
    }


    private Map<String, String> getVolleyParams() {
        return getParams();
    }

    private RequestBody getHttpParams() {
        FormEncodingBuilder formBody = new FormEncodingBuilder();
        Map<String, String> map = farmerData.getParametersInMap(db);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            Log.d(TAG, entry.getKey() + " = " + entry.getValue());
            formBody.add(entry.getKey(), entry.getValue());
        }
        RequestBody requestBody = formBody.build();
        return requestBody;
    }

    private Map<String, String> getParams() {
        Map<String, String> param = new HashMap<>();
        param = farmerData.getParametersInMap(db);
        displayValue(param);
        return param;
    }

    private void displayValue(Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            Log.d(TAG, entry.getKey() + " = " + entry.getValue());
        }
    }

    private void uploadFarmer(final Map<String, String> param) {
        if (context instanceof Activity)
            dialog = ProgressDialog.show(context, "Uploading data", "Please wait...", true);
        StringRequest sendRequest = new StringRequest(Request.Method.POST, urlString,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String Response) {
                        Log.d(TAG, "Response weighing: " + Response);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(Response);
                            String status = jsonObject.getString("status");
                            if (status.equals("success")) {
                                success();
                            }
                            if (status.equals("fail")) {
                                if (jsonObject.getString("message").toString().contains("duplicate entry found")) {
                                    success();
                                } else {
                                    Toast.makeText(context, jsonObject.getString("message").toString(), Toast.LENGTH_SHORT).show();
                                    failed();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Json Parser Exception", Toast.LENGTH_SHORT).show();
                            failed();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                failed();

                Toast.makeText(context, "Not able to connect with server", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return param;
            }
        };

        sendRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(sendRequest);

    }

    private void uploadFarmer(RequestBody formBody) {


        JSONObject jsonObject = null;
        OkHttpClient client = new OkHttpClient();


        com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                .url(urlString)
                .post(formBody)
                .build();

        try {
            com.squareup.okhttp.Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            } else {
                String responseString = response.body().string();
                Log.d(TAG, "response string : " + responseString);
                try {
                    jsonObject = new JSONObject(responseString);
                    String status = jsonObject.getString("status");
                    if (status.equals("success")) {
                        success();
                    }
                    if (status.equals("fail")) {
                        if (jsonObject.has("message")) {
                            if (jsonObject.getString("message").toString().contains("already exists")) {
                                success();
                            } else {
                                failed();
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.d(TAG, "response parsing error : " + e.toString());
                    e.printStackTrace();
                }
            }

            // Do something with the response.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void success() {
        farmerData.setSendingStatus(DBAdapter.SENT);
        farmerData.save(db);
        if (context instanceof Activity) {
            try {
                if ((dialog != null) && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }catch (final Exception e) {
                e.printStackTrace();
            } finally {
                dialog = null;
            }

            if (context instanceof RegisterFarmerActivity) {

                RegisterFarmerActivity.sUploadable.onFarmerUploadingSuccess(db, farmerData);
            }
            if (context instanceof TabedActivity) {
                FarmersList.sUploadable.onFarmerUploadingSuccess(db, farmerData);
            }
        }
    }

    private void failed() {
        if (context instanceof Activity) {
            try {
                if ((dialog != null) && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }catch (final Exception e) {
                e.printStackTrace();
            } finally {
                dialog = null;
            }

            if (context instanceof RegisterFarmerActivity) {
                RegisterFarmerActivity.sUploadable.onFailed();
            }
            if(context instanceof TabedActivity){
                FarmersList.sUploadable.onFailed();
            }
        }
    }

}
