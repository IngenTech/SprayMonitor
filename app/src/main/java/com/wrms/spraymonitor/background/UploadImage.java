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
import com.wrms.spraymonitor.MainActivity;
import com.wrms.spraymonitor.SprayDetailThreeActivity;
import com.wrms.spraymonitor.SprayForms;
import com.wrms.spraymonitor.TabedActivity;
import com.wrms.spraymonitor.app.AppController;
import com.wrms.spraymonitor.app.DBAdapter;
import com.wrms.spraymonitor.dataobject.ImageData;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yogendra Singh on 17-06-2016.
 */
public class UploadImage {

    private static final String TAG = UploadImage.class.getSimpleName();
    Context context;
    DBAdapter db;
    private ProgressDialog dialog;
    ImageData imageData;
    private String base64Image;
    private String urlString;

    public UploadImage(ImageData imageData, String base64Image, String urlString, DBAdapter db) {
        this.imageData = imageData;
        this.base64Image = base64Image;
        this.db = db;
        this.urlString = urlString;
    }

    public void uploadFormThroughVolleyRequest(Context context) {
        this.context = context;
        uploadImage(getVolleyParams());
    }

    public void uploadFormThroughHttpRequest() {
        uploadImage(getHttpParams());
    }


    private Map<String, String> getVolleyParams() {
        return getParams();
    }

    private Map<String, String> getParams() {
        Map<String, String> param = new HashMap<>();
        param = imageData.getParametersInMap(db);
        displayValue(param);
        return param;
    }
    private void displayValue(Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            Log.d(TAG,entry.getKey()+" - "+entry.getValue());
        }
    }
    private void uploadImage(final Map<String, String> param) {
        if (context instanceof Activity)
            dialog = ProgressDialog.show(context, "Uploading Image", "Please wait...", true);
        StringRequest sendRequest = new StringRequest(Request.Method.POST, urlString,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String Response) {
                        Log.d(TAG,"Response Image: " + Response);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(Response);
                            String status = jsonObject.getString("status");
                            if (status.equals("success")) {
                                success();
                            }
                            if (status.equals("fail")) {
                                if (jsonObject.getString("message").toString().contains("already exist")) {
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

    private RequestBody getHttpParams() {
        FormEncodingBuilder formBody = new FormEncodingBuilder();
        Map<String, String> map = imageData.getParametersInMap(db);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            Log.d(TAG, entry.getKey() + " = " + entry.getValue());
            formBody.add(entry.getKey(), entry.getValue());
        }
        RequestBody requestBody = formBody.build();
        return requestBody;
    }

    private void uploadImage(RequestBody formBody) {

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
        imageData.setSendingStatus(DBAdapter.SENT);
        imageData.save(db);
        if (context instanceof Activity) {
            if(dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            if (context instanceof SprayDetailThreeActivity) {
                SprayDetailThreeActivity.sUploadable.onImageUploadingSuccess(db, imageData);
                UploadSprayMonitoringData.sResponseStatus.statusSuccess();
            }
            if (context instanceof TabedActivity) {
                SprayForms.sUploadable.onImageUploadingSuccess(db, imageData);
                UploadSprayMonitoringData.sResponseStatus.statusSuccess();
            }
        }
    }
    private void failed() {
        if (context instanceof Activity) {
            if(dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            if (context instanceof SprayDetailThreeActivity) {
                UploadSprayMonitoringData.sResponseStatus.statusFailed();
            }
            if (context instanceof TabedActivity) {
                UploadSprayMonitoringData.sResponseStatus.statusFailed();
            }
        }
    }
}
