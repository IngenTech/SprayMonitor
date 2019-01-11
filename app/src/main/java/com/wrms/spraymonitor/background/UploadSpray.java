package com.wrms.spraymonitor.background;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;
import com.wrms.spraymonitor.MainActivity;
import com.wrms.spraymonitor.SprayDetailThreeActivity;
import com.wrms.spraymonitor.app.AppController;
import com.wrms.spraymonitor.app.DBAdapter;
import com.wrms.spraymonitor.app.Synchronize;
import com.wrms.spraymonitor.dataobject.ImageData;
import com.wrms.spraymonitor.dataobject.PaymentObject;
import com.wrms.spraymonitor.dataobject.SprayMonitorData;
import com.wrms.spraymonitor.dataobject.VideoData;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Admin on 27-12-2016.
 */

public class UploadSpray {


    private int formType;
    private ProgressDialog dialog;
    private String URL = "";
    Context context;
    SprayMonitorData surveyFormData;
    DBAdapter db;
    private static final String TAG = UploadSpray.class.getSimpleName();


    public UploadSpray(SprayMonitorData surveyFormData, DBAdapter db, Context context) {
        this.surveyFormData = surveyFormData;
        this.db = db;
        this.context = context;
        URL = Synchronize.SPRAY_FORM_UPLOAD_API;
    }

    public void uploadFormThroughVolleyRequest() {
        uploadForm(getVolleyParams());
    }

    public void uploadFormThroughHttpRequest() {
        uploadForm(getHttpParams());
    }

    private Map<String, String> getVolleyParams() {
        Map<String, String> param = null;
        param = surveyFormData.getParametersInMap(db);
        return param;
    }

    private RequestBody getHttpParams() {
        FormEncodingBuilder formBody = new FormEncodingBuilder();
        Map<String, String> map = surveyFormData.getParametersInMap(db);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            Log.d(TAG, entry.getKey() + " = " + entry.getValue());
            formBody.add(entry.getKey(), entry.getValue());
        }
        RequestBody requestBody = formBody.build();
        return requestBody;
    }

    private void uploadForm(final Map<String, String> param) {
        if (context instanceof Activity)
            dialog = ProgressDialog.show(context, "Uploading Form", "Please wait...", true);
        StringRequest sendRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String Response) {
                        Log.d(TAG, "Volley Form Response : " + Response);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(Response);
                            if (jsonObject.has("status")) {
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
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "Json Parser Exception");
                            failed();
                        }
                    }
                }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                Log.d(TAG, "Not able to connect with server");
                failed();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return param;
            }
        };
        sendRequest.setRetryPolicy(new

                DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        );
        AppController.getInstance().addToRequestQueue(sendRequest);
    }

    ///////////////////////////////////////////////////////////////////////////////////
    private void uploadForm(RequestBody formBody) {

        JSONObject jsonObject = null;
        OkHttpClient client = new OkHttpClient();


        com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                .url(URL)
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
        surveyFormData.setSendingStatus(DBAdapter.SENT);
        surveyFormData.save(db);
        ArrayList<ImageData> images = surveyFormData.getImages(db);
        for (ImageData imageData : images) {
            imageData.setSendingStatus(DBAdapter.SUBMIT);
            imageData.save(db);
        }
        ArrayList<VideoData> videos = surveyFormData.getVideos(db);
        for (VideoData videoData : videos) {
            videoData.setSendingStatus(DBAdapter.SUBMIT);
            videoData.save(db);
        }

        ArrayList<PaymentObject> paymentObjects = surveyFormData.getPayments(db);
        for (PaymentObject paymentObject : paymentObjects) {
            paymentObject.setSendingStatus(DBAdapter.SUBMIT);
            paymentObject.save(db);
        }
        if (context instanceof Activity) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            if (context instanceof SprayDetailThreeActivity) {
                SprayDetailThreeActivity.sUploadable.onSprayFormUploadingSuccess(db, surveyFormData);
                UploadSprayMonitoringData.sResponseStatus.statusSuccess();
            }
            if (context instanceof MainActivity){
                UploadSprayMonitoringData.sResponseStatus.statusSuccess();
            }
        }
    }

    private void failed() {
        if (context instanceof Activity) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            if (context instanceof SprayDetailThreeActivity) {
                UploadSprayMonitoringData.sResponseStatus.statusFailed();
            }
            if (context instanceof MainActivity) {
                UploadSprayMonitoringData.sResponseStatus.statusFailed();
            }
        }
    }

}
