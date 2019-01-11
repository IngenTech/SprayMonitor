package com.wrms.spraymonitor.background;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.wrms.spraymonitor.R;
import com.wrms.spraymonitor.SprayDetailThreeActivity;
import com.wrms.spraymonitor.app.DBAdapter;
import com.wrms.spraymonitor.app.Synchronize;
import com.wrms.spraymonitor.dataobject.ImageData;
import com.wrms.spraymonitor.dataobject.PaymentObject;
import com.wrms.spraymonitor.dataobject.SprayMonitorData;
import com.wrms.spraymonitor.dataobject.VideoData;
import com.wrms.spraymonitor.utils.AppManager;
import com.wrms.spraymonitor.utils.ResponseStatus;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Admin on 27-12-2016.
 */

public class UploadSprayMonitoringData {


    private final String TAG = "UploadSurveyData ";
    private final int finish = 1000;
    public static ResponseStatus sResponseStatus;
    private ArrayList<SprayMonitorData> formArrayList;
    private ArrayList<ImageData> imageArrayList;
    private ArrayList<VideoData> videoArrayList;
    private ArrayList<PaymentObject> paymentArrayList;

    Context ctx;
    DBAdapter db;
    private int formArrayCount = 0;
    private int imageArrayCount = 0;
    private int videoArrayCount = 0;
    private int paymentArrayCount = 0;

    public UploadSprayMonitoringData(Context ctx, DBAdapter db,
                                     ArrayList<SprayMonitorData> formArrayList,
                                     ArrayList<ImageData> imageArrayList,
                                     ArrayList<VideoData> videoArrayList,
                                     ArrayList<PaymentObject> paymentArrayList) {
        this.ctx = ctx;
        this.db = db;
        this.formArrayList = formArrayList;
        this.imageArrayList = imageArrayList;
        this.videoArrayList = videoArrayList;
        this.paymentArrayList = paymentArrayList;
        createInterfaceObj();
        uploadItems();
    }

    private void uploadItems() {
        if (AppManager.isOnline(ctx)) {
            if (imageArrayCount < imageArrayList.size()) {
                String imagePath = imageArrayList.get(imageArrayCount).getImage_path().toString();
                File image = new File(imagePath);
                if (image.exists()) {
                    String base64Image = AppManager.getBase64FromPath(imagePath);
                    ImageData imageData = imageArrayList.get(imageArrayCount);
                    UploadImage uploadImage = new UploadImage(imageData, base64Image, Synchronize.IMAGE_UPLOAD_API, db);
                    uploadImage.uploadFormThroughVolleyRequest(ctx);
                    imageArrayCount++;
                } else {
                    imageArrayCount++;
                    uploadItems();
                }
            } else {
                imageArrayCount = finish;
            }

            if (imageArrayCount == finish) {
                if (paymentArrayCount < paymentArrayList.size()) {
                    PaymentObject paymentObject = paymentArrayList.get(paymentArrayCount);
                    UploadPaymentData uploadForm = new UploadPaymentData(paymentObject, db);
                    uploadForm.uploadFormThroughVolleyRequest(ctx);
                    paymentArrayCount++;
                } else {
                    paymentArrayCount = finish;
                    uploadItems();
                }
            }

            if (paymentArrayCount == finish) {
                if (formArrayCount < formArrayList.size()) {
                    SprayMonitorData surveyFormData = formArrayList.get(formArrayCount);
                    UploadSpray uploadForm = new UploadSpray(surveyFormData, db, ctx);
                    uploadForm.uploadFormThroughVolleyRequest();
                    formArrayCount++;
                } else {
                    formArrayCount = finish;
                    uploadItems();
                }
            }

            SharedPreferences myPreference = PreferenceManager.getDefaultSharedPreferences(ctx);
            boolean videoTransmission = myPreference.getBoolean("video_transmission", true);
            if (formArrayCount == finish) {
                if (videoTransmission) {
                    if (videoArrayCount < videoArrayList.size()) {
                        /*String videoPath = videoArrayList.get(videoArrayCount).getVideo_path();
                        File video = new File(videoPath);
                        if (video.exists()) {
                            Log.d(TAG, "Video File Found");
                            double fileSize = AppManager.getFileSize(videoPath);
                            Log.d(TAG, "File Size : " + fileSize);
                            if (fileSize <= 5) {
                                VideoData videoData = videoArrayList.get(videoArrayCount);
                                UploadVideo uploadVideo = new UploadVideo(videoData, db, ctx);
                                uploadVideo.uploadVideoThroughVolleyRequest();
                                videoArrayCount++;
                            } else {
                                videoArrayCount++;
                                uploadItems();
                            }
                        } else {
                            Log.d(TAG, "Video File Not Found");
                            videoArrayCount++;
                            uploadItems();
                        }
*/
                    }
                } else{
                    formArrayCount = finish;
                    if (ctx instanceof SprayDetailThreeActivity) {
                        AppManager.closeActivities(ctx);
                    }
                }
            }else{
                if (ctx instanceof SprayDetailThreeActivity) {
                    AppManager.closeActivities(ctx);
                }
            }

        } else {
            Toast.makeText(ctx, ctx.getResources().getString(R.string.network_problem), Toast.LENGTH_SHORT).show();
        }

    }

    private void createInterfaceObj() {
        sResponseStatus = new ResponseStatus() {
            @Override
            public void statusSuccess() {
                uploadItems();
            }

            @Override
            public void statusFailed() {
                uploadItems();
            }
        };
    }


}
