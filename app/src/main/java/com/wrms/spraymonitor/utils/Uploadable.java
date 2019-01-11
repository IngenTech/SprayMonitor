package com.wrms.spraymonitor.utils;


import com.wrms.spraymonitor.app.DBAdapter;
import com.wrms.spraymonitor.dataobject.FarmerData;
import com.wrms.spraymonitor.dataobject.ImageData;
import com.wrms.spraymonitor.dataobject.PaymentObject;
import com.wrms.spraymonitor.dataobject.SprayMonitorData;
import com.wrms.spraymonitor.dataobject.VideoData;

/**
 * Created by Yogendra Singh on 20-06-2016.
 */
public interface Uploadable {

    public void onSprayFormUploadingSuccess(DBAdapter db, SprayMonitorData cceFormData);

    public void onImageUploadingSuccess(DBAdapter db, ImageData imageData);

    public void onVideoUploadingSuccess(DBAdapter db, VideoData videoData);

    public void onPaymentUploadingSuccess(DBAdapter db, PaymentObject weighingData);

    public void onFarmerUploadingSuccess(DBAdapter db, FarmerData audioData);

    public void onFailed();
}
