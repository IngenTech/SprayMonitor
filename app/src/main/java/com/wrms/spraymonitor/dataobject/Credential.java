package com.wrms.spraymonitor.dataobject;

import android.database.Cursor;

import com.wrms.spraymonitor.app.DBAdapter;

/**
 * Created by WRMS on 23-10-2015.
 */
public class Credential {

    private String userName;
    private String userId;
    private String password;
    private String imei;
    private String lastUpdated;
    private String foCode;

    public Credential(){

    }
    public Credential(Cursor credentialCursor){
        this.userName = credentialCursor.getString(credentialCursor.getColumnIndex(DBAdapter.USER_NAME));
        this.userId = credentialCursor.getString(credentialCursor.getColumnIndex(DBAdapter.USERID));
        this.password = credentialCursor.getString(credentialCursor.getColumnIndex(DBAdapter.PASSWORD));
        this.imei = credentialCursor.getString(credentialCursor.getColumnIndex(DBAdapter.IMEI));
//        this.imei = "1234567890";
        this.lastUpdated = credentialCursor.getString(credentialCursor.getColumnIndex(DBAdapter.CREATED_DATE_TIME));
        this.foCode = credentialCursor.getString(credentialCursor.getColumnIndex(DBAdapter.FO_CODE));

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getFoCode() {
        return foCode;
    }

    public void setFoCode(String foCode) {
        this.foCode = foCode;
    }
}
