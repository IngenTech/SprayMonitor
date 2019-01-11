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

public class ImageData implements Parcelable {

    public static final int IMAGE_HIEGHT = 200;
    public static final int IMAGE_WIDTH = 200;

    public static final String IMAGE_STRING = "";

    private String dbId;
    private String image_name;
    private String image_path;
    private String lat;
    private String lon;
    private String dateTime;
    private String formId;
    private String formType;
    private String sendingStatus;
    private String cellId;
    private String lacId;
    private String mcc;
    private String mnc;


    public String getCellId() {
        return cellId;
    }

    public void setCellId(String cellId) {
        this.cellId = cellId;
    }

    public String getLacId() {
        return lacId;
    }

    public void setLacId(String lacId) {
        this.lacId = lacId;
    }

    public String getMcc() {
        return mcc;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    public String getMnc() {
        return mnc;
    }

    public void setMnc(String mnc) {
        this.mnc = mnc;
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

    public ImageData(String image_name, String image_path, String lat, String lon, String dateTime, String formId, String formType, String is_submit,
                     String cellId, String lacId, String mcc, String mnc) {
        this.dbId = "-1";
        this.image_name = image_name;
        this.image_path = image_path;
        this.lat = lat;
        this.lon = lon;
        this.dateTime = dateTime;
        this.formId = formId;
        this.formType = formType;
        this.sendingStatus = is_submit;
        this.cellId = cellId;
        this.lacId = lacId;
        this.mcc = mcc;
        this.mnc = mnc;
    }

    public ImageData(Cursor cursor) {
        this.dbId = cursor.getString(cursor.getColumnIndex(DBAdapter.ID));
        this.image_name = cursor.getString(cursor.getColumnIndex(DBAdapter.IMAGE_NAME));
        this.image_path = cursor.getString(cursor.getColumnIndex(DBAdapter.IMAGE_PATH));
        this.lat = cursor.getString(cursor.getColumnIndex(DBAdapter.LAT));
        this.lon = cursor.getString(cursor.getColumnIndex(DBAdapter.LON));
        this.dateTime = cursor.getString(cursor.getColumnIndex(DBAdapter.CREATED_DATE_TIME));
        this.formId = cursor.getString(cursor.getColumnIndex(DBAdapter.SPRAY_MONITORING_ID));
        this.formType = cursor.getString(cursor.getColumnIndex(DBAdapter.COUNT_TYPE));
        this.sendingStatus = cursor.getString(cursor.getColumnIndex(DBAdapter.SENDING_STATUS));
        this.cellId = cursor.getString(cursor.getColumnIndex(DBAdapter.CELL_ID));
        this.lacId = cursor.getString(cursor.getColumnIndex(DBAdapter.LAC_ID));
        this.mcc = cursor.getString(cursor.getColumnIndex(DBAdapter.MCC));
        this.mnc = cursor.getString(cursor.getColumnIndex(DBAdapter.MNC));
    }

    public boolean save(DBAdapter db) {

        boolean isSaved = false;

        Cursor cursor = db.getImageByIdCount(this.getFormId(), this.getFormType(), this.getImage_name());
        if (cursor.getCount() > 0) {
            isSaved = true;
        }

        ContentValues values = new ContentValues();
        values.put(DBAdapter.IMAGE_NAME, this.getImage_name());
        values.put(DBAdapter.IMAGE_PATH, this.getImage_path());
        values.put(DBAdapter.LAT, this.getLat());
        values.put(DBAdapter.LON, this.getLon());
        values.put(DBAdapter.CREATED_DATE_TIME, this.getDateTime());
        values.put(DBAdapter.SPRAY_MONITORING_ID, this.getFormId());
        values.put(DBAdapter.COUNT_TYPE, this.getFormType());
        values.put(DBAdapter.SENDING_STATUS, this.getSendingStatus());
        values.put(DBAdapter.CELL_ID, this.getCellId());
        values.put(DBAdapter.LAC_ID, this.getLacId());
        values.put(DBAdapter.MCC, this.getMcc());
        values.put(DBAdapter.MNC, this.getMnc());

        long k = 0;

        System.out.println("Image Name " + this.getImage_name() + ", Image Date " + this.getDateTime() + " isSaved : " + isSaved + " FormId " + this.getFormId());

        if (isSaved) {
            k = db.db.update(DBAdapter.TABLE_IMAGE, values, DBAdapter.SPRAY_MONITORING_ID + "='" + formId + "' AND " + DBAdapter.ID + " = '" + this.getDbId() + "'", null);
        } else {
            k = db.db.insert(DBAdapter.TABLE_IMAGE, null, values);
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
        Element rootElement = document.createElement("ImageData");
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

        em = document.createElement("cellId");
        if (this.getCellId() != null) {
            em.appendChild(document.createTextNode(this.getCellId()));
            rootElement.appendChild(em);
        }

        em = document.createElement("lacId");
        if (this.getLacId() != null) {
            em.appendChild(document.createTextNode(this.getLacId()));
            rootElement.appendChild(em);
        }

        em = document.createElement("mcc");
        if (this.getMcc() != null) {
            em.appendChild(document.createTextNode(this.getMcc()));
            rootElement.appendChild(em);
        }

        em = document.createElement("mnc");
        if (this.getMnc() != null) {
            em.appendChild(document.createTextNode(this.getMnc()));
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

        em = document.createElement("FormId");
        if (this.getFormId() != null) {
            em.appendChild(document.createTextNode(this.getFormId()));
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

        File imageFile = new File(this.getImage_path());
        Map<String, String> map = new HashMap<>();
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

        if (imageFile.exists()) {

            Credential credential = new Credential();
            Cursor credentialCursor = db.getCredential();
            if (credentialCursor.getCount() > 0) {
                credentialCursor.moveToFirst();
                credential = new Credential(credentialCursor);
            }
            credentialCursor.close();

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(this.getImage_path(), options);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String imageString = Base64.encodeToString(byteArray, Base64.DEFAULT);

            if (credential.getUserId() != null) {
                map.put("user_id", credential.getUserId());
            }

            if (this.getFormId() != null) {
                map.put("experiment_id", this.getFormId());
            }
            if (credential.getImei() != null) {
                map.put("imei", credential.getImei());
            }
            if (this.getImage_name() != null) {
                map.put("image_name", this.getImage_name() + "_" + System.currentTimeMillis() + ".jpg");
            }
            if (imageString != null) {
                map.put("image_file", imageString);
            }
            if (this.getLat() != null) {
                map.put("latitude", this.getLat());
            }
            if (this.getLon() != null) {
                map.put("longitude", this.getLon());
            }
            if (this.getDateTime() != null) {
                map.put("device_date", this.getDateTime());
            }
            if (this.getCellId() != null) {
                map.put("cell_id", this.getCellId());
            }
            if (this.getLacId() != null) {
                map.put("lac_id", this.getLacId());
            }
            if (this.getMcc() != null) {
                map.put("mcc", this.getMcc());
            }
            if (this.getMnc() != null) {
                map.put("mnc", this.getMnc());
            }

            return map;

        } else {
            return null;
        }
    }

    public String send(DBAdapter db) {

        File imageFile = new File(this.getImage_path());


        if (imageFile.exists()) {

            Credential credential = new Credential();
            Cursor credentialCursor = db.getCredential();
            if (credentialCursor.getCount() > 0) {
                credentialCursor.moveToFirst();
                credential = new Credential(credentialCursor);
            }
            credentialCursor.close();

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(this.getImage_path(), options);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String imageString = Base64.encodeToString(byteArray, Base64.DEFAULT);

            ArrayList<NameValuePair> params = new ArrayList<>();

            String methodOnServer = "image";

            Map<String, String> map = this.getParametersInMap(db);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                if (entry.getKey().equals("image_file")) {
                    continue;
                }
                System.out.println(entry.getKey() + " : " + entry.getValue());

            }

            String response = Synchronize.requestForRESTPost(params, "image");

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
        } else {
            return "Image File does not exist";
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

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    public String getSendingStatus() {
        return sendingStatus;
    }

    public void setSendingStatus(String sendingStatus) {
        this.sendingStatus = sendingStatus;
    }

    public ImageData(Parcel in) {
        String[] data = new String[13];

        in.readStringArray(data);
        this.image_name = data[0];
        this.image_path = data[1];
        this.lat = data[2];
        this.lon = data[3];
        this.dateTime = data[4];
        this.formId = data[5];
        this.formType = data[6];
        this.sendingStatus = data[7];
        this.cellId = data[8];
        this.lacId = data[9];
        this.mcc = data[10];
        this.mnc = data[11];
        this.dbId = data[12];
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
                this.formId,
                this.formType,
                this.sendingStatus,
                this.cellId,
                this.lacId,
                this.mcc,
                this.mnc,
                this.dbId});
    }

    public static final Creator<ImageData> CREATOR = new Creator<ImageData>() {

        @Override
        public ImageData createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            return new ImageData(source); // using parcelable constructor
        }

        @Override
        public ImageData[] newArray(int size) {
            // TODO Auto-generated method stub
            return new ImageData[size];
        }
    };


    public static boolean updateImageStatus(DBAdapter db, String formId, String status) {
        boolean isUpdated = false;

        Cursor cursor = db.getImageByFormId(formId);
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
                String imageFormId = cursor.getString(cursor.getColumnIndex(DBAdapter.SPRAY_MONITORING_ID));
                values.put(DBAdapter.SPRAY_MONITORING_ID, imageFormId);
                String formType = cursor.getString(cursor.getColumnIndex(DBAdapter.COUNT_TYPE));
                values.put(DBAdapter.COUNT_TYPE, formType);
                String is_submit = status;
                values.put(DBAdapter.SENDING_STATUS, is_submit);
                String cellId = cursor.getString(cursor.getColumnIndex(DBAdapter.CELL_ID));
                values.put(DBAdapter.CELL_ID, cellId);
                String lacId = cursor.getString(cursor.getColumnIndex(DBAdapter.LAC_ID));
                values.put(DBAdapter.LAC_ID, lacId);
                String mcc = cursor.getString(cursor.getColumnIndex(DBAdapter.MCC));
                values.put(DBAdapter.MCC, mcc);
                String mnc = cursor.getString(cursor.getColumnIndex(DBAdapter.MNC));
                values.put(DBAdapter.MNC, mnc);

                int rowId = db.db.update(DBAdapter.TABLE_IMAGE, values, DBAdapter.SPRAY_MONITORING_ID + " = '" + formId + "'", null);

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