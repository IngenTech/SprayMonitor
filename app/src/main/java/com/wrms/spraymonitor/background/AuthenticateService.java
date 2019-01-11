package com.wrms.spraymonitor.background;

import android.app.Activity;
import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wrms.spraymonitor.TabedActivity;
import com.wrms.spraymonitor.app.AppController;
import com.wrms.spraymonitor.app.DBAdapter;
import com.wrms.spraymonitor.app.Synchronize;
import com.wrms.spraymonitor.dataobject.Credential;
import com.wrms.spraymonitor.dataobject.FarmerData;
import com.wrms.spraymonitor.dataobject.FuelData;
import com.wrms.spraymonitor.dataobject.FuelImageData;
import com.wrms.spraymonitor.dataobject.ImageData;
import com.wrms.spraymonitor.dataobject.PaymentObject;
import com.wrms.spraymonitor.dataobject.SprayMonitorData;
import com.wrms.spraymonitor.dataobject.VideoData;
import com.wrms.spraymonitor.fuelmanager.MachineRunTimeData;
import com.wrms.spraymonitor.utils.FolderManager;
import com.wrms.spraymonitor.utils.MultipartRequest;

import net.ypresto.androidtranscoder.MediaTranscoder;
import net.ypresto.androidtranscoder.format.MediaFormatStrategyPresets;

import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONObject;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

public class AuthenticateService extends IntentService {

    public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static int errorCounter = 0;
    private static boolean isRunning = false;

    public AuthenticateService() {
        super("AuthenticateService");

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(getClass().getSimpleName(), "Spray Monitor background Service ran......!");
        String result = "";
        DBAdapter db = new DBAdapter(getApplicationContext());
        db.open();
        /*Credential credential = new Credential();
        Cursor credentialCursor = db.getCredential();
        if (credentialCursor.getCount() > 0) {
            credentialCursor.moveToFirst();
            credential = new Credential(credentialCursor);
        }
        credentialCursor.close();*/
        sendPendingData(db, getApplicationContext());

    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        isRunning = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }

    public static boolean isRunning(){
        return isRunning;
    }

    public synchronized static void sendPendingData(DBAdapter db, Context context) {

        errorCounter = 0;

        Cursor submittedFuelCursor = db.getSubmittedFuel();
        if (submittedFuelCursor.getCount() > 0) {
            submittedFuelCursor.moveToFirst();

            do {
                String fuelId = submittedFuelCursor.getString(submittedFuelCursor.getColumnIndex(DBAdapter.FUEL_ID));
                FuelData fuelData = new FuelData(fuelId, db);
                String response = fuelData.sendFuelDetail(db);
                if (response.contains(Synchronize.SERVER_ERROR_MESSAGE)) {
                    errorCounter = errorCounter + 1;
                    if (errorCounter >= 3) {
                        return;
                    }
                }
            } while (submittedFuelCursor.moveToNext());

        }
        submittedFuelCursor.close();


        Cursor submittedRunFuelCursor = db.getRunSubmittedFuel();
        if (submittedRunFuelCursor.getCount() > 0) {
            submittedRunFuelCursor.moveToFirst();

            do {
                String fuelId = submittedRunFuelCursor.getString(submittedRunFuelCursor.getColumnIndex(DBAdapter.FUEL_ID));
                MachineRunTimeData fuelData = new MachineRunTimeData(fuelId, db);
                String response = fuelData.sendRunFuelDetail(db);
                if (response.contains(Synchronize.SERVER_ERROR_MESSAGE)) {
                    errorCounter = errorCounter + 1;
                    if (errorCounter >= 3) {
                        return;
                    }
                }
            } while (submittedRunFuelCursor.moveToNext());

        }
        submittedRunFuelCursor.close();


        Cursor getSavedFarmer = db.getSavedFarmer();
        if (getSavedFarmer.getCount() > 0) {
            getSavedFarmer.moveToFirst();

            do {
                String farmerId = getSavedFarmer.getString(getSavedFarmer.getColumnIndex(DBAdapter.FARMER_ID));
                FarmerData farmerData = new FarmerData(farmerId, db);
                String response = farmerData.sendRegistrationRequest(db);
                if (response.contains(Synchronize.SERVER_ERROR_MESSAGE)) {
                    errorCounter = errorCounter + 1;
                    if (errorCounter >= 3) {
                        return;
                    }
                }
            } while (getSavedFarmer.moveToNext());

        }
        getSavedFarmer.close();


        Cursor getSubmittedFarmer = db.getSubmittedFarmer();
        if (getSubmittedFarmer.getCount() > 0) {
            getSubmittedFarmer.moveToFirst();

            do {
                String farmerId = getSubmittedFarmer.getString(getSubmittedFarmer.getColumnIndex(DBAdapter.FARMER_ID));
                FarmerData farmerData = new FarmerData(farmerId, db);
                String response = farmerData.sendRegistrationConfirmation(db);
                if (response.contains(Synchronize.SERVER_ERROR_MESSAGE)) {
                    errorCounter = errorCounter + 1;
                    if (errorCounter >= 3) {
                        return;
                    }
                }
            } while (getSubmittedFarmer.moveToNext());

        }
        getSubmittedFarmer.close();


        /********************************Survey Sending Code****************************************/
        Cursor submittedFormCursor = db.getSubmittedForm();
        if (submittedFormCursor.getCount() > 0) {
            submittedFormCursor.moveToFirst();

            do {
                String sprayMonitoringId = submittedFormCursor.getString(submittedFormCursor.getColumnIndex(DBAdapter.SPRAY_MONITORING_ID));
                SprayMonitorData sprayMonitorData = new SprayMonitorData(sprayMonitoringId, db);
                sprayMonitorData.send(db);
//                submitFormRequest(sprayMonitorData, null, null, null, db, context);
            } while (submittedFormCursor.moveToNext());

        }
        submittedFormCursor.close();


        ArrayList<ImageData> images = new ArrayList<>();
        Cursor getImageByFormId = db.getSubmittedImage();
        System.out.println("Image Cursor Length : " + getImageByFormId.getCount());
        if (getImageByFormId.getCount() > 0) {
            getImageByFormId.moveToFirst();
            do {
                ImageData imageData = new ImageData(getImageByFormId);
                images.add(imageData);
            } while (getImageByFormId.moveToNext());
        }
        getImageByFormId.close();
        for (ImageData data : images) {
            data.send(db);
        }

        //Fuel images sending data like start stop hours

        ArrayList<FuelImageData> runimages = new ArrayList<>();
        Cursor getRunImageByFormId = db.getSubmittedFuelImage();
        System.out.println("Image Cursor Length : " + getRunImageByFormId.getCount());
        if (getRunImageByFormId.getCount() > 0) {
            getRunImageByFormId.moveToFirst();
            do {
                FuelImageData imageData = new FuelImageData(getRunImageByFormId);
                runimages.add(imageData);
            } while (getRunImageByFormId.moveToNext());
        }
        getRunImageByFormId.close();
        for (FuelImageData data : runimages) {
            data.send(db);
        }


        ArrayList<PaymentObject> paymentObjects = new ArrayList<>();
        Cursor getPayments = db.getSubmittedPayments();
        System.out.println("Payment Cursor Length : " + getPayments.getCount());
        if (getPayments.moveToFirst()) {
            do {
                Cursor sprayCursor = db.getFormByID(getPayments.getString(getPayments.getColumnIndex(DBAdapter.SPRAY_MONITORING_ID)));
                String recievableAmount = "0.0";
                if (sprayCursor.moveToFirst()) {
                    recievableAmount = sprayCursor.getString(sprayCursor.getColumnIndex(DBAdapter.AMOUNT_RECEIVABLE));
                }
                sprayCursor.close();
                PaymentObject paymentObject = new PaymentObject(getPayments, recievableAmount);
                if (!paymentObject.getSendingStatus().equals(DBAdapter.SENT)) {
                    paymentObjects.add(paymentObject);
                }
            } while (getPayments.moveToNext());
        }
        getPayments.close();
        for (PaymentObject obj : paymentObjects) {
            obj.send(db);
        }


        ArrayList<VideoData> videos = new ArrayList<>();
        SharedPreferences myPreference = PreferenceManager.getDefaultSharedPreferences(context);
        boolean videoTransmission = myPreference.getBoolean("video_transmission", true);

        System.out.println("videoTransmissions : " + videoTransmission);
        if (videoTransmission) {
            Cursor getVideoByFormId = db.getSubmittedVideo();
            if (getVideoByFormId.getCount() > 0) {
                getVideoByFormId.moveToFirst();
                do {
                    VideoData videoData = new VideoData(getVideoByFormId);
                    videos.add(videoData);
                } while (getVideoByFormId.moveToNext());
            }
            getVideoByFormId.close();
        }

        if (videoTransmission) {
            Cursor getVideoByFormId = db.getSubmittedVideo();
            if (getVideoByFormId.moveToFirst()) {
                do {
                    VideoData videoData = new VideoData(getVideoByFormId);
                    if (videoData.getIsTranscoded().equals(DBAdapter.FALSE)) {
                        transcodeVideo(videoData, context);
                    }
                    if (videoData.getIsTranscoded().equals(DBAdapter.TRUE)) {
                        String result = videoData.post(db);
                        if (result.contains("success")) {
                            videoData.setSendingStatus(DBAdapter.SENT);
                            videoData.save(db);
                        }

                    }
                } while (getVideoByFormId.moveToNext());

            }
            getVideoByFormId.close();
        }


        /*if (images.size() > 0) {
            submitImageRequest(images, db, 0, paymentObjects, videos, context);
        } else {
            if (paymentObjects.size() > 0) {
                submitPaymentRequest(paymentObjects, db, 0, videos, context);
            } else {
                System.out.println("videoTansmission : " + videoTransmission);
                if (videoTransmission) {
                    Cursor getVideoByFormId = db.getSubmittedVideo();
                    if (getVideoByFormId.getCount() > 0) {
                        getVideoByFormId.moveToFirst();
                        do {
                            VideoData videoData = new VideoData(getVideoByFormId);
                            if (videoData.getIsTranscoded().equals(DBAdapter.FALSE)) {
                                transcodeVideo(videoData, context);
                            }
                            if (videoData.getIsTranscoded().equals(DBAdapter.TRUE)) {
                                submitVideoRequest(videos, db, 0, context);
                            }

                        } while (getVideoByFormId.moveToNext());
                    }
                    getVideoByFormId.close();
                }
            }

        }*/


        /********************************END of Survey Sending Code*********************************/

    }


    public static void sendSprayMonitoringData(SprayMonitorData data, ArrayList<ImageData> images, final ArrayList<PaymentObject> paymentObjects, ArrayList<VideoData> videos, DBAdapter db, Context context) {
        System.out.println("get inside sendSprayMonitoringData " + images.size() + " , " + videos.size());
        errorCounter = 0;

        if (data.getFarmerSendingStatus().equalsIgnoreCase(DBAdapter.SAVE)) {
            FarmerData farmerData = new FarmerData(data.getFarmerId(), db);
            if (farmerData.getFarmerContact() != null && farmerData.getFarmerContact().trim().length() > 0) {
                registerRequest(farmerData, db, data, images, paymentObjects, videos, context);
            }

        } else {

            submitFormRequest(data, images, paymentObjects, videos, db, context);
        }
    }


    private static void registerRequest(final FarmerData data, final DBAdapter db, final SprayMonitorData sprayData, final ArrayList<ImageData> images, final ArrayList<PaymentObject> paymentObjects, final ArrayList<VideoData> videos, final Context context) {

        String methodeName = "farmerRegistration";
        if (context instanceof Activity) {
            dialog = ProgressDialog.show(context, "Registration Request",
                    "Please wait...", true);
        }

        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, Synchronize.URL + methodeName,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String registrationResponse) {

                        if (context instanceof Activity && dialog != null) {
                            dialog.cancel();
                        }

                        try {
                            System.out.println("Register Farmer Response : " + registrationResponse);
                            JSONObject jsonObject = new JSONObject(registrationResponse);

                            if (jsonObject.has("status")) {
                                if (jsonObject.getString("status").equals("success") || jsonObject.getString("status").equals("ContactAlreadyExist")) {

                                    if (jsonObject.has("FarmerId")) {
                                        String farmerId = jsonObject.getString("FarmerId");
                                        data.setFarmerId(farmerId);
                                        data.setSendingStatus(DBAdapter.SENT);
                                        data.save(db);
                                    } else {
                                        data.setSendingStatus(DBAdapter.SUBMIT);
                                        data.save(db);
                                    }
                                    submitFormRequest(sprayData, images, paymentObjects, videos, db, context);
                                } else {
                                    if (context instanceof Activity && dialog != null) {
                                        ((Activity) context).finish();
                                        Intent intent = new Intent(((Activity) context), TabedActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        context.startActivity(intent);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                if (context instanceof Activity && dialog != null) {
                    dialog.cancel();
                    ((Activity) context).finish();
                    Intent intent = new Intent(((Activity) context), TabedActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
//                System.out.println("Get Params has been called");
                Map<String, String> map = data.getParametersInMap(db);
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };

        stringVarietyRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(stringVarietyRequest);
    }

    public static ProgressDialog dialog;

    private static void submitFormRequest(final SprayMonitorData data, final ArrayList<ImageData> images, final ArrayList<PaymentObject> paymentObjects, final ArrayList<VideoData> videos, final DBAdapter db, final Context context) {

        String methodOnServer = "form";

        if (context instanceof Activity) {
            dialog = ProgressDialog.show(context, "Form Sending",
                    "Please wait...", true);
        }

        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, Synchronize.URL + methodOnServer,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String formSubmitResponse) {
                        System.out.println("Form submit response Response : " + formSubmitResponse);

                        if (context instanceof Activity && dialog != null) {
                            dialog.cancel();
                        }

                        if (formSubmitResponse.contains("success") || formSubmitResponse.contains("FormAlreadyExist")) {

                            formSubmitResponse = "Success";
                            data.setSendingStatus(DBAdapter.SENT);
                            data.save(db);
                            if (images == null) {
                                if (context instanceof Activity) {
                                    ((Activity) context).finish();
                                    Intent intent = new Intent(((Activity) context), TabedActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    context.startActivity(intent);
                                } else {
                                    return;
                                }
                            }
                            if (paymentObjects == null) {
                                if (context instanceof Activity) {
                                    ((Activity) context).finish();
                                    Intent intent = new Intent(((Activity) context), TabedActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    context.startActivity(intent);
                                } else {
                                    return;
                                }
                            }
                            if (videos == null) {
                                if (context instanceof Activity) {
                                    ((Activity) context).finish();
                                    Intent intent = new Intent(((Activity) context), TabedActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    context.startActivity(intent);
                                } else {
                                    return;
                                }
                            }

                            if (images.size() > 0) {
                                submitImageRequest(images, db, 0, paymentObjects, videos, context);
                            } else {
                                SharedPreferences myPreference = PreferenceManager.getDefaultSharedPreferences(context);
                                boolean videoTransmission = myPreference.getBoolean("video_transmission", true);
                                Log.i("VIDEO TRANSMISSION", String.valueOf(videoTransmission));

                                if (videoTransmission) {
                                    for (VideoData videoData : videos) {
                                        if (!videoData.getSendingStatus().equals(DBAdapter.SENT)) {
                                            System.out.println("WentInside videoArray");
                                            if (videoData.getIsTranscoded().equals(DBAdapter.FALSE)) {
                                                transcodeVideo(videoData, context);
                                            }
                                        }
                                    }
                                    submitVideoRequest(videos, db, 0, context);
                                } else {
                                    if (context instanceof Activity) {
                                        ((Activity) context).finish();
                                        Intent intent = new Intent(((Activity) context), TabedActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        context.startActivity(intent);
                                    }
                                }
                            }
                        } else {
                            data.setSendingStatus(DBAdapter.SAVE);
                            data.save(db);
                            if (context instanceof Activity) {
                                Intent intent = new Intent(((Activity) context), TabedActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                context.startActivity(intent);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                if (context instanceof Activity && dialog != null) {
                    dialog.cancel();
                    ((Activity) context).finish();
                    Intent intent = new Intent(((Activity) context), TabedActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                }
            }
        }

        )

        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
//                System.out.println("Get Params has been called");
                Map<String, String> map = data.getParametersInMap(db);
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };

        stringVarietyRequest.setRetryPolicy(new

                DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        );
        AppController.getInstance().addToRequestQueue(stringVarietyRequest);

    }


    private static boolean submitImageRequest(final ArrayList<ImageData> images, final DBAdapter db, final int count, final ArrayList<PaymentObject> paymentObjects, final ArrayList<VideoData> videos, final Context context) {
        if (count < images.size()) {
            String methodOnServer = "image";
            final ImageData data = images.get(count);

            if (context instanceof Activity) {
                dialog = ProgressDialog.show(context, "Image Sending",
                        "Please wait...", true);
            }

            StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, Synchronize.URL + methodOnServer,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String formSubmitResponse) {
                            if (context instanceof Activity && dialog != null) {
                                dialog.cancel();
                            }

                            System.out.println("Form submit response Response : " + formSubmitResponse);
                            if (formSubmitResponse.contains("success") || formSubmitResponse.contains("FormAlreadyExist")) {
                                data.setSendingStatus(DBAdapter.SENT);
                                data.save(db);
                            }
                            int nextCount = count + 1;
                            boolean wasLastImage = submitImageRequest(images, db, nextCount, paymentObjects, videos, context);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    volleyError.printStackTrace();
                    if (context instanceof Activity && dialog != null) {
                        dialog.cancel();
                        ((Activity) context).finish();
                        Intent intent = new Intent(((Activity) context), TabedActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);
                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> map = data.getParametersInMap(db);
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        System.out.println(entry.getKey() + " : " + entry.getValue());
                    }
                    return map;
                }
            };

            stringVarietyRequest.setRetryPolicy(new DefaultRetryPolicy(
                    60000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            AppController.getInstance().addToRequestQueue(stringVarietyRequest);
            return true;
        } else {

            submitPaymentRequest(paymentObjects, db, 0, videos, context);
            return false;
        }
    }


    private static boolean submitVideoRequest(final ArrayList<VideoData> videos, final DBAdapter db, final int count, final Context context) {

        System.out.println("SUBMIT VIDEO REQUEST HAS BEEN CALLED");

        if (count < videos.size()) {
            String methodOnServer = "video";
            final VideoData data = videos.get(count);
            Credential credential = new Credential();
            Cursor credentialCursor = db.getCredential();
            if (credentialCursor.getCount() > 0) {
                credentialCursor.moveToFirst();
                credential = new Credential(credentialCursor);
            }
            credentialCursor.close();

            File videoFile = new File(data.getVideo_path());
            if (videoFile.exists()) {
                if (context instanceof Activity) {
                    dialog = ProgressDialog.show(context, "Video Sending",
                            "Please wait...", true);
                }

                Cursor tankFillingCursor = db.getTankFillingById(data.getFormType());
                String tankFillingCount = "0";
                if (tankFillingCursor.moveToFirst()) {
                    tankFillingCount = tankFillingCursor.getString(tankFillingCursor.getColumnIndex(DBAdapter.TANK_FILLING_COUNT));
                }
                tankFillingCursor.close();


                MultipartRequest stringVarietyRequest = new MultipartRequest(Synchronize.URL + methodOnServer,
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                volleyError.printStackTrace();
                                if (context instanceof Activity && dialog != null) {
                                    dialog.cancel();
                                    ((Activity) context).finish();
                                    Intent intent = new Intent(((Activity) context), TabedActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    context.startActivity(intent);
                                }
                            }
                        }, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String videoSubmitResponse) {

                        if (context instanceof Activity && dialog != null) {
                            dialog.cancel();
                        }

                        System.out.println("Video submit response Response : " + videoSubmitResponse);
//{"status":"success"}
                        if (videoSubmitResponse.contains("success")) {
                            data.setSendingStatus(DBAdapter.SENT);
                            data.save(db);
                        }
                        int nextCount = count + 1;
                        boolean wasLastImage = submitVideoRequest(videos, db, nextCount, context);
                        if (!wasLastImage) {
                            if (context instanceof Activity) {
                                ((Activity) context).finish();
                                Intent intent = new Intent(((Activity) context), TabedActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                context.startActivity(intent);
                            }
                        }
                    }
                }, videoFile, data, credential,tankFillingCount);

                stringVarietyRequest.setRetryPolicy(new DefaultRetryPolicy(
                        60000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                AppController.getInstance().addToRequestQueue(stringVarietyRequest);
                return true;
            } else {
                int nextCount = count + 1;
                submitVideoRequest(videos, db, nextCount, context);
            }
        } else {
            if (context instanceof Activity) {
                ((Activity) context).finish();
                Intent intent = new Intent(((Activity) context), TabedActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
            }
        }
        return true;
    }

    private static boolean submitPaymentRequest(final ArrayList<PaymentObject> paymentObjects, final DBAdapter db, final int count, final ArrayList<VideoData> videos, final Context context) {

        if (count < paymentObjects.size()) {
            String methodOnServer = "payments";
            final PaymentObject data = paymentObjects.get(count);

            if (context instanceof Activity) {
                dialog = ProgressDialog.show(context, "Payment Sending",
                        "Please wait...", true);
            }

            StringRequest paymentRequest = new StringRequest(Request.Method.POST, Synchronize.URL + methodOnServer,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String paymentSubmitResponse) {
                            if (context instanceof Activity && dialog != null) {
                                dialog.cancel();
                            }
                            try {
                                System.out.println("Payment submit response Response : " + paymentSubmitResponse);

                                if (paymentSubmitResponse.contains("success") || paymentSubmitResponse.contains("FormAlreadyExist")) {
                                    data.setSendingStatus(DBAdapter.SENT);
                                    data.save(db);
                                }
                                int nextCount = count + 1;
                                boolean wasLastImage = submitPaymentRequest(paymentObjects, db, nextCount, videos, context);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    volleyError.printStackTrace();
                    if (context instanceof Activity && dialog != null) {
                        dialog.cancel();
                        ((Activity) context).finish();
                        Intent intent = new Intent(((Activity) context), TabedActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);
                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> map = data.getParametersInMap(db);
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        System.out.println(entry.getKey() + " : " + entry.getValue());
                    }
                    return map;
                }
            };

            paymentRequest.setRetryPolicy(new DefaultRetryPolicy(
                    60000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            AppController.getInstance().addToRequestQueue(paymentRequest);
            return true;
        } else {

            SharedPreferences myPreference = PreferenceManager.getDefaultSharedPreferences(context);
            boolean videoTransmission = myPreference.getBoolean("video_transmission", true);
            Log.i("VIDEO TRANSMISSION", String.valueOf(videoTransmission));

            if (videoTransmission) {
                for (VideoData videoData : videos) {
                    if (!videoData.getSendingStatus().equals(DBAdapter.SENT)) {
                        System.out.println("WentInside videoArray");
                        if (videoData.getIsTranscoded().equals(DBAdapter.FALSE)) {
                            transcodeVideo(videoData, context);
                        }
                    }
                }
                submitVideoRequest(videos, db, 0, context);

            } else {
                if (context instanceof Activity) {
                    ((Activity) context).finish();
                    Intent intent = new Intent(((Activity) context), TabedActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                }
            }
            return false;
        }

    }


    static boolean transcodeVideo(final VideoData data, Context context) {
        boolean isTranscoded = false;

        Map<String, String> pathMap = FolderManager.getVidDir(data.getFormId(), data.getVideo_name(), data.getFormType());
        final String lowVideoPath = pathMap.get(FolderManager.LOW_RESOLUTION_DIRECTORY);

        MediaTranscoder.Listener listener = new MediaTranscoder.Listener() {
            @Override
            public void onTranscodeProgress(double progress) {

            }

            @Override
            public void onTranscodeCompleted() {
                data.setIsTranscoded(DBAdapter.TRUE);
                data.setVideo_path(lowVideoPath);
            }

            @Override
            public void onTranscodeFailed(Exception exception) {
                data.setIsTranscoded(DBAdapter.FALSE);
            }
        };

        ContentResolver resolver = context.getContentResolver();
        final ParcelFileDescriptor parcelFileDescriptor;
        try {
            File file = new File(data.getVideo_path());
            Uri outputFileUri = Uri.fromFile(file);
            parcelFileDescriptor = resolver.openFileDescriptor(outputFileUri, "r");
        } catch (FileNotFoundException e) {
            Log.w("Could not open '" + data.getVideo_path() + "'", e);
            return false;
        }
        final FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();


        MediaTranscoder.getInstance().transcodeVideo(fileDescriptor, lowVideoPath,
                MediaFormatStrategyPresets.createAndroid720pStrategy(), listener);

        return isTranscoded;
    }


}

