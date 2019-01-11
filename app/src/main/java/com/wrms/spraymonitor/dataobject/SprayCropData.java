package com.wrms.spraymonitor.dataobject;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.wrms.spraymonitor.app.DBAdapter;

/**
 * Created by Admin on 27-08-2016.
 */
public class SprayCropData implements Parcelable {

    private String sprayId;
    private String sprayCropId;
    private String cropId;
    private String cropAcre;
    private String cropName;


    public SprayCropData(){
        super();
    }

    public String getSprayId() {
        return sprayId;
    }

    public void setSprayId(String sprayId) {
        this.sprayId = sprayId;
    }

    public String getSprayCropId() {
        return sprayCropId;
    }

    public void setSprayCropId(String sprayCropId) {
        this.sprayCropId = sprayCropId;
    }

    public String getCropId() {
        return cropId;
    }

    public void setCropId(String cropId) {
        this.cropId = cropId;
    }

    public String getCropAcre() {
        return cropAcre;
    }

    public void setCropAcre(String cropAcre) {
        this.cropAcre = cropAcre;
    }

    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    public SprayCropData(String sprayCropId,DBAdapter db){
        Cursor cursor = db.getSprayCropById(sprayCropId);
        if(cursor.moveToFirst()){
            this.sprayId = cursor.getString(cursor.getColumnIndex(DBAdapter.SPRAY_MONITORING_ID));
            this.sprayCropId = cursor.getString(cursor.getColumnIndex(DBAdapter.SPRAY_CROP_ID));
            this.cropAcre = cursor.getString(cursor.getColumnIndex(DBAdapter.CROP_ACRE));
            String cropId = cursor.getString(cursor.getColumnIndex(DBAdapter.CROP_ID));
            this.cropId = cropId;
            if(cropId.equals("-1")){
                this.cropName = "No Crop";
            }else {
                this.cropName = db.getCropNameById(this.cropId);
            }
        }
        cursor.close();
    }

    public boolean save(DBAdapter db){
        boolean isSaved = false;
        ContentValues values = new ContentValues();
        values.put(DBAdapter.SPRAY_MONITORING_ID,getSprayId());
        values.put(DBAdapter.SPRAY_CROP_ID,getSprayCropId());
        values.put(DBAdapter.CROP_ID,getCropId());
        values.put(DBAdapter.CROP_ACRE,getCropAcre());
        long k = db.db.insert(DBAdapter.TABLE_SPRAY_CROPS ,null, values);
        if(k!=-1){
            isSaved = true;
        }
        return  isSaved;
    }

    public boolean delete(DBAdapter db){
        boolean isDelete = false;

        long k = db.db.delete(DBAdapter.TABLE_SPRAY_CROPS ,DBAdapter.SPRAY_CROP_ID+" = '"+getSprayCropId()+"'", null);
        if(k!=-1){
            isDelete = true;
        }
        return  isDelete;
    }


    protected SprayCropData(Parcel in) {
        sprayId = in.readString();
        sprayCropId = in.readString();
        cropId = in.readString();
        cropAcre = in.readString();
        cropName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sprayId);
        dest.writeString(sprayCropId);
        dest.writeString(cropId);
        dest.writeString(cropAcre);
        dest.writeString(cropName);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SprayCropData> CREATOR = new Creator<SprayCropData>() {
        @Override
        public SprayCropData createFromParcel(Parcel in) {
            return new SprayCropData(in);
        }

        @Override
        public SprayCropData[] newArray(int size) {
            return new SprayCropData[size];
        }
    };
}
