package com.wrms.spraymonitor.background;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.wrms.spraymonitor.SprayDetailThreeActivity;
import com.wrms.spraymonitor.SprayForms;
import com.wrms.spraymonitor.TabedActivity;
import com.wrms.spraymonitor.app.AppController;
import com.wrms.spraymonitor.app.Constents;
import com.wrms.spraymonitor.app.DBAdapter;
import com.wrms.spraymonitor.app.Synchronize;
import com.wrms.spraymonitor.dataobject.Credential;
import com.wrms.spraymonitor.dataobject.VideoData;
import com.wrms.spraymonitor.utils.MultipartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yogendra Singh on 21-06-2016.
 */
public class UploadVideo {
    ProgressDialog dialog;
    VideoData videoData;
    Credential credential;
    String tankFillingCount;
    DBAdapter db;
    Context context;
    private static final String TAG = UploadVideo.class.getSimpleName();

    public UploadVideo() {
    }

    public UploadVideo(VideoData videoData,Credential credential,String tankFillingCount, DBAdapter db, Context context) {
        this.videoData = videoData;
        this.credential = credential;
        this.tankFillingCount = tankFillingCount;
        this.db = db;
        this.context = context;
    }
    public void uploadVideoThroughVolleyRequest() {
        final File videoFile = new File(videoData.getVideo_path());
        if (videoFile.exists()) {
            if (context instanceof Activity) {
                dialog = ProgressDialog.show(context, "Uploading video",
                        "Please wait...", true);
            }
            MultipartRequest stringVarietyRequest = new MultipartRequest(Synchronize.VIDEO_UPLOAD_API,
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            volleyError.printStackTrace();
                            Log.d(TAG,("Video Response: " + volleyError));
                            failed();
                        }
                    }, new Response.Listener<String>() {
                @Override
                public void onResponse(String Response) {
                    Log.d(TAG,"Response Video: " + Response);
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(Response);
                        String status = jsonObject.getString("status");
                        if (status.equals("success")) {
                            success();
                        }
                        if (status.equals("fail")) {
                            if (jsonObject.has("message")) {
                                if (jsonObject.getString("message").toString().equals("already exists")) {
                                    success();
                                } else {
                                    Log.d(TAG,"Failed");
                                        failed();
                                }
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(TAG,"Json Parser Exception");
                        failed();
                    }
                }
            }, videoFile, videoData,credential,tankFillingCount);
            stringVarietyRequest.setRetryPolicy(new

                    DefaultRetryPolicy(
                            60000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

            );

            AppController.getInstance().
                    addToRequestQueue(stringVarietyRequest);
        }
    }

    private static final String IMGUR_CLIENT_ID = "...";
    private static final MediaType MEDIA_TYPE_MP4 = MediaType.parse("video/mp4");

    private final OkHttpClient client = new OkHttpClient();
    /////////////////////////////////////////////////////////////////////////////
    public void uploadVideoThroughHttpRequest(File videoFile) {

        Map<String, String> map = new HashMap<>();
        map.put("user_id", credential.getUserId());
        map.put("experiment_id", videoData.getFormId());
        map.put("tankFillingNumber", tankFillingCount);
        map.put("latitude", videoData.getLat());
        map.put("longitude", videoData.getLon());
        map.put("imei", credential.getImei());
        map.put("device_date", videoData.getDateTime());
        map.put("mcc", videoData.getMcc());
        map.put("mnc", videoData.getMnc());
        map.put("lac_id", videoData.getLacId());
        map.put("cell_id", videoData.getCellId());

        FormEncodingBuilder formBody = new FormEncodingBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            Log.d(TAG, entry.getKey() + " = " + entry.getValue());
            formBody.add(entry.getKey(), entry.getValue());
        }
        formBody.build();

            // Use the imgur image upload API as documented at https://api.imgur.com/endpoints/image
            RequestBody requestBody = new MultipartBuilder()
                    .type(MultipartBuilder.FORM).addPart(RequestBody.create(null, "Square Logo"))
                    .addPart(RequestBody.create(MEDIA_TYPE_MP4, videoFile))
                    .build();



            Request request = new Request.Builder()
                    .header("Authorization", "Client-ID " + IMGUR_CLIENT_ID)
                    .url(Synchronize.VIDEO_UPLOAD_API)
                    .post(requestBody)
                    .build();
            try {
                com.squareup.okhttp.Response response = client.newCall(request).execute();
                String status = response.body().string();
                if (response.isSuccessful()){
                    if (status.equals("success")) {
                        success();
                    }
                    if (status.equals("fail")) {
                        failed();
                    }
                }else{
                    failed();
                }
                System.out.println(response.body().string());
            }catch (Exception e){
                e.printStackTrace();
                failed();
            }
    }

    public void success() {
        videoData.setSendingStatus(DBAdapter.SENT);
        videoData.save(db);
        if (context instanceof Activity) {
            if(dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            if (context instanceof TabedActivity) {
                SprayForms.sUploadable.onVideoUploadingSuccess(db, videoData);
                UploadSprayMonitoringData.sResponseStatus.statusSuccess();
            }
            if (context instanceof SprayDetailThreeActivity) {
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
