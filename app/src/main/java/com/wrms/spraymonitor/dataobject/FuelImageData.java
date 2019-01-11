package com.wrms.spraymonitor.dataobject;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

import com.wrms.spraymonitor.app.DBAdapter;
import com.wrms.spraymonitor.app.Synchronize;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by Admin on 25-03-2017.
 */
public class FuelImageData implements Parcelable {

    public static final int IMAGE_HIEGHT = 200;
    public static final int IMAGE_WIDTH = 200;

    public static final String IMAGE_STRING = "";

    private String dbId;
    private String image_name;
    private String image_path;
    private String lat;
    private String lon;
    private String dateTime;
    private String fuelId;
    private String machineID;
    private String sendingStatus;


    public FuelImageData() {

    }

    public String getDbId() {
        if (dbId == null || (dbId.trim().length() == 0)) {
            dbId = "-1";
        }
        return dbId;
    }

    public void setDbId(String dbId) {
        this.dbId = dbId;
    }

    public FuelImageData(String image_name, String image_path, String lat, String lon, String dateTime, String fuelID, String machineID, String is_submit) {
        this.dbId = "-1";
        this.image_name = image_name;
        this.image_path = image_path;
        this.lat = lat;
        this.lon = lon;
        this.dateTime = dateTime;
        this.fuelId = fuelID;
        this.machineID = machineID;
        this.sendingStatus = is_submit;

    }

    public FuelImageData(Cursor cursor) {
        this.dbId = cursor.getString(cursor.getColumnIndex(DBAdapter.ID));
        this.image_name = cursor.getString(cursor.getColumnIndex(DBAdapter.IMAGE_NAME));
        this.image_path = cursor.getString(cursor.getColumnIndex(DBAdapter.IMAGE_PATH));
        this.lat = cursor.getString(cursor.getColumnIndex(DBAdapter.LAT));
        this.lon = cursor.getString(cursor.getColumnIndex(DBAdapter.LON));
        this.dateTime = cursor.getString(cursor.getColumnIndex(DBAdapter.CREATED_DATE_TIME));
        this.fuelId = cursor.getString(cursor.getColumnIndex(DBAdapter.FUEL_ID));
        this.machineID = cursor.getString(cursor.getColumnIndex(DBAdapter.MACHINE_ID));
        this.sendingStatus = cursor.getString(cursor.getColumnIndex(DBAdapter.SENDING_STATUS));

    }

    public boolean save(DBAdapter db) {

        boolean isSaved = false;

        Cursor cursor = db.getFuelImageByIdCount(this.getFuelId(), this.getMachineID(), this.getImage_name());
        if (cursor.getCount() > 0) {
            isSaved = true;
        }

        ContentValues values = new ContentValues();
        values.put(DBAdapter.IMAGE_NAME, this.getImage_name());
        values.put(DBAdapter.IMAGE_PATH, this.getImage_path());
        values.put(DBAdapter.LAT, this.getLat());
        values.put(DBAdapter.LON, this.getLon());
        values.put(DBAdapter.CREATED_DATE_TIME, this.getDateTime());
        values.put(DBAdapter.FUEL_ID, this.getFuelId());
        values.put(DBAdapter.MACHINE_ID, this.getMachineID());
        values.put(DBAdapter.SENDING_STATUS, this.getSendingStatus());


        long k = 0;

        System.out.println("Image Name " + this.getImage_name() + ", Image Date " + this.getDateTime() + " isSaved : " + isSaved + " FormId " + this.getFuelId());

        if (isSaved) {
            k = db.db.update(DBAdapter.TABLE_FUEL_IMAGE, values, DBAdapter.FUEL_ID + "='" + fuelId + "' AND " + DBAdapter.ID + " = '" + this.getDbId() + "'", null);
        } else {
            k = db.db.insert(DBAdapter.TABLE_FUEL_IMAGE, null, values);
            this.setDbId(String.valueOf(k));
        }
        if (k == -1) {
            isSaved = false;
        } else {
            isSaved = true;
        }
        saveToXML();
        return isSaved;

    }


    public void saveToXML() {

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Document document = documentBuilder.newDocument();
        Element rootElement = document.createElement("FuelImageData");
        document.appendChild(rootElement);

        Element em = document.createElement("Latitude");
        if (this.getLat() != null) {
            em.appendChild(document.createTextNode(this.getLat()));
            rootElement.appendChild(em);
        }


        em = document.createElement("Longitude");
        if (this.getLon() != null) {
            em.appendChild(document.createTextNode(this.getLon()));
            rootElement.appendChild(em);
        }


        em = document.createElement("DateTime");
        if (this.getDateTime() != null) {
            em.appendChild(document.createTextNode(this.getDateTime()));
            rootElement.appendChild(em);
        }

        em = document.createElement("Name");
        if (this.getImage_name() != null) {
            em.appendChild(document.createTextNode(this.getImage_name()));
            rootElement.appendChild(em);
        }

        em = document.createElement("FuelId");
        if (this.getFuelId() != null) {
            em.appendChild(document.createTextNode(this.getFuelId()));
            rootElement.appendChild(em);
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();  // This code
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }                // doesn't work

        String filePath = getImage_path().replace("jpg", "xml");
        File exitingXML = new File(filePath);
        if (exitingXML.exists()) {
            boolean isDeleted = exitingXML.delete();
            System.out.println("XML isDeleted : " + isDeleted);
        }

        DOMSource source = new DOMSource(document);
        StreamResult streamResult = new StreamResult(filePath);
        try {
            transformer.transform(source, streamResult);
        } catch (TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Map<String, String> getParametersInMap(DBAdapter db) {


        Map<String, String> map = new HashMap<>();


        Credential credential = new Credential();
        Cursor credentialCursor = db.getCredential();
        if (credentialCursor.getCount() > 0) {
            credentialCursor.moveToFirst();
            credential = new Credential(credentialCursor);
        }
        credentialCursor.close();


        if (credential.getUserId() != null) {
            map.put("UserID", credential.getUserId());
        }

        if (this.getFuelId() != null) {
            map.put("RunHourID", this.getFuelId());
        }


        if (this.getMachineID() != null) {
            map.put("MachineCode", this.getMachineID());
        }
        if (credential.getImei() != null) {
            map.put("Imei", credential.getImei());
        }
        if (this.getImage_name() != null) {
            map.put("ImageName", getImage_name());
        }
        if (this.getImage_path() != null) {
            map.put("BaseString", this.getImage_path());
        }
        if (this.getLat() != null) {
            map.put("Latitude", this.getLat());
        }
        if (this.getLon() != null) {
            map.put("Longitude", this.getLon());
        }
        if (this.getDateTime() != null) {
            map.put("DeviceDateTime", this.getDateTime());
        }


        return map;

    }

    public String send(DBAdapter db) {


            Credential credential = new Credential();
            Cursor credentialCursor = db.getCredential();
            if (credentialCursor.getCount() > 0) {
                credentialCursor.moveToFirst();
                credential = new Credential(credentialCursor);
            }
            credentialCursor.close();



            ArrayList<NameValuePair> params = new ArrayList<>();


            Map<String, String> map = this.getParametersInMap(db);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));

                System.out.println(entry.getKey() + " : " + entry.getValue());

            }

            String response = Synchronize.requestForRESTPost(params, "machineRunHourImage");

			/*response = "[\n" +
                    "    {\"Response\":\"Success\", \"Id\":\"1234\"},\n" +
					"]";

			response = "Success";*/

            if (Synchronize.isConnectedToServer) {
                this.setSendingStatus(DBAdapter.SENT);
                this.save(db);
                return response;
            } else {
                return Synchronize.SERVER_ERROR_MESSAGE;
            }

    }


    private String getAppDateFormat(String dateTime) {
        String resultDateString = "";

        String[] array = dateTime.split("@");
        if (array.length == 2) {
            String date = array[0].replaceAll("_", "/");
            String time = array[1].replaceAll("_", ":");
            resultDateString = date + " " + time;
        }

        return resultDateString;
    }

    public String getImage_name() {
        return image_name;
    }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getFuelId() {
        return fuelId;
    }

    public void setFuelId(String fuelId) {
        this.fuelId = fuelId;
    }

    public String getMachineID() {
        return machineID;
    }

    public void setMachineID(String machineID) {
        this.machineID = machineID;
    }

    public String getSendingStatus() {
        return sendingStatus;
    }

    public void setSendingStatus(String sendingStatus) {
        this.sendingStatus = sendingStatus;
    }

    public FuelImageData(Parcel in) {
        String[] data = new String[9];

        in.readStringArray(data);
        this.image_name = data[0];
        this.image_path = data[1];
        this.lat = data[2];
        this.lon = data[3];
        this.dateTime = data[4];
        this.fuelId = data[5];
        this.machineID = data[6];
        this.sendingStatus = data[7];

        this.dbId = data[8];
    }


    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                this.image_name,
                this.image_path,
                this.lat,
                this.lon,
                this.dateTime,
                this.fuelId,
                this.machineID,
                this.sendingStatus,

                this.dbId});
    }

    public static final Creator<FuelImageData> CREATOR = new Creator<FuelImageData>() {

        @Override
        public FuelImageData createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            return new FuelImageData(source); // using parcelable constructor
        }

        @Override
        public FuelImageData[] newArray(int size) {
            // TODO Auto-generated method stub
            return new FuelImageData[size];
        }
    };


    public static boolean updateImageStatus(DBAdapter db, String formId, String status) {
        boolean isUpdated = false;

        Cursor cursor = db.getFuelImageByFormId(formId);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                ContentValues values = new ContentValues();
                String image_name = cursor.getString(cursor.getColumnIndex(DBAdapter.IMAGE_NAME));
                values.put(DBAdapter.IMAGE_NAME, image_name);
                String image_path = cursor.getString(cursor.getColumnIndex(DBAdapter.IMAGE_PATH));
                values.put(DBAdapter.IMAGE_PATH, image_path);
                String lat = cursor.getString(cursor.getColumnIndex(DBAdapter.LAT));
                values.put(DBAdapter.LAT, lat);
                String lon = cursor.getString(cursor.getColumnIndex(DBAdapter.LON));
                values.put(DBAdapter.LON, lon);
                String dateTime = cursor.getString(cursor.getColumnIndex(DBAdapter.CREATED_DATE_TIME));
                values.put(DBAdapter.CREATED_DATE_TIME, dateTime);
                String imageFormId = cursor.getString(cursor.getColumnIndex(DBAdapter.FUEL_ID));
                values.put(DBAdapter.FUEL_ID, imageFormId);
                String formType = cursor.getString(cursor.getColumnIndex(DBAdapter.COUNT_TYPE));
                values.put(DBAdapter.MACHINE_ID, formType);
                String is_submit = status;
                values.put(DBAdapter.SENDING_STATUS, is_submit);

                int rowId = db.db.update(DBAdapter.TABLE_FUEL_IMAGE, values, DBAdapter.FUEL_ID + " = '" + formId + "'", null);

                if (rowId != -1) {
                    isUpdated = true;
                } else {
                    isUpdated = false;
                }

            } while (cursor.moveToNext());
        }
        cursor.close();

        return isUpdated;
    }


}