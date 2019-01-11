package com.wrms.spraymonitor.dataobject;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.wrms.spraymonitor.app.DBAdapter;

/**
 * Created by Admin on 29-08-2016.
 */
public class TankFillingData implements Parcelable{

    private String tankFillingId;
    private String tankFillingCropId;
    private String acreCoveredByTank;
    private String tankFillingCount;
    private String sprayMonitoringId;
    private String startTime;
    private String endTime;


    public TankFillingData(String tankFillingId,DBAdapter db){
        Cursor cursor = db.getTankFillingById(tankFillingId);
        if(cursor.moveToFirst()){
            this.tankFillingId = cursor.getString(cursor.getColumnIndex(DBAdapter.TANK_FILLING_ID));
            this.tankFillingCropId = cursor.getString(cursor.getColumnIndex(DBAdapter.TANK_FILLING_CROP));
            this.acreCoveredByTank = cursor.getString(cursor.getColumnIndex(DBAdapter.ACRE_COVERED_BY_TANK));
            this.tankFillingCount = cursor.getString(cursor.getColumnIndex(DBAdapter.TANK_FILLING_COUNT));
            this.sprayMonitoringId = cursor.getString(cursor.getColumnIndex(DBAdapter.SPRAY_MONITORING_ID));
            this.startTime = cursor.getString(cursor.getColumnIndex(DBAdapter.TANK_FILLING_START_TIME));
            this.endTime = cursor.getString(cursor.getColumnIndex(DBAdapter.TANK_FILLING_STOP_TIME));
        }
        cursor.close();
    }

    public boolean save(DBAdapter db){
        boolean isSaved = false;
        ContentValues values = new ContentValues();
        values.put(DBAdapter.TANK_FILLING_ID,getTankFillingId());
        values.put(DBAdapter.TANK_FILLING_CROP,getTankFillingCropId());
        values.put(DBAdapter.ACRE_COVERED_BY_TANK,getAcreCoveredByTank());
        values.put(DBAdapter.TANK_FILLING_COUNT,getTankFillingCount());
        values.put(DBAdapter.SPRAY_MONITORING_ID,getSprayMonitoringId());
        values.put(DBAdapter.TANK_FILLING_START_TIME,getStartTime());
        values.put(DBAdapter.TANK_FILLING_STOP_TIME,getEndTime());
        long k = db.db.insert(DBAdapter.TABLE_TANK_FILLING ,null, values);
        if(k!=-1){
            isSaved = true;
        }
        return  isSaved;
    }

    public boolean delete(DBAdapter db){
        boolean isDelete = false;

        long k = db.db.delete(DBAdapter.TABLE_TANK_FILLING ,DBAdapter.TANK_FILLING_ID+" = '"+getTankFillingId()+"'", null);
        if(k!=-1){
            isDelete = true;
        }
        return  isDelete;
    }


    public TankFillingData() {
        super();
    }

    protected TankFillingData(Parcel in) {
        tankFillingId = in.readString();
        tankFillingCropId = in.readString();
        acreCoveredByTank = in.readString();
        tankFillingCount = in.readString();
        sprayMonitoringId = in.readString();
        startTime = in.readString();
        endTime = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tankFillingId);
        dest.writeString(tankFillingCropId);
        dest.writeString(acreCoveredByTank);
        dest.writeString(tankFillingCount);
        dest.writeString(sprayMonitoringId);
        dest.writeString(startTime);
        dest.writeString(endTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TankFillingData> CREATOR = new Creator<TankFillingData>() {
        @Override
        public TankFillingData createFromParcel(Parcel in) {
            return new TankFillingData(in);
        }

        @Override
        public TankFillingData[] newArray(int size) {
            return new TankFillingData[size];
        }
    };

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getSprayMonitoringId() {
        return sprayMonitoringId;
    }

    public void setSprayMonitoringId(String sprayMonitoringId) {
        this.sprayMonitoringId = sprayMonitoringId;
    }

    public String getTankFillingId() {
        return tankFillingId;
    }

    public void setTankFillingId(String tankFillingId) {
        this.tankFillingId = tankFillingId;
    }

    public String getTankFillingCropId() {
        return tankFillingCropId;
    }

    public void setTankFillingCropId(String tankFillingCropId) {
        this.tankFillingCropId = tankFillingCropId;
    }

    public String getAcreCoveredByTank() {
        return acreCoveredByTank;
    }

    public void setAcreCoveredByTank(String acreCoveredByTank) {
        this.acreCoveredByTank = acreCoveredByTank;
    }

    public String getTankFillingCount() {
        return tankFillingCount;
    }

    public void setTankFillingCount(String tankFillingCount) {
        this.tankFillingCount = tankFillingCount;
    }
}
