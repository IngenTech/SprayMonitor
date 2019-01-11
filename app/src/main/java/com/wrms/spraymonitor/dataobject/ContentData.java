package com.wrms.spraymonitor.dataobject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by WRMS on 19-11-2015.
 */
public class ContentData implements Parcelable{

    private String title;
    private String value;

    public ContentData(){};

    public ContentData(String title, String value) {
        this.title = title;
        this.value = value;
    }

    protected ContentData(Parcel in) {
        title = in.readString();
        value = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(value);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ContentData> CREATOR = new Creator<ContentData>() {
        @Override
        public ContentData createFromParcel(Parcel in) {
            return new ContentData(in);
        }

        @Override
        public ContentData[] newArray(int size) {
            return new ContentData[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
