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

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

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
 * Created by WRMS on 26-10-2015.
 */
public class VideoData implements Parcelable {


    public static final int IMAGE_HIEGHT = 200;
    public static final int IMAGE_WIDTH = 200;

    public static final String IMAGE_STRING = "";

    private String dbId;
    private String video_name;
    private String video_path;
    private String lat;
    private String lon;
    private String dateTime;
    private String formId;
    private String formType;
    private String isTranscoded;
    private String sendingStatus;
    private String cellId;
    private String lacId;
    private String mcc;
    private String mnc;

    public String getIsTranscoded() {
        return isTranscoded;
    }

    public void setIsTranscoded(String isTranscoded) {
        this.isTranscoded = isTranscoded;
    }

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
        if (mcc != null) {
            return mcc;
        } else {
            return "0";
        }
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    public String getMnc() {
        if (mnc != null) {
            return mnc;
        } else {
            return "0";
        }
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

    public VideoData() {
        super();
    }

    public void setDbId(String dbId) {
        this.dbId = dbId;
    }

    public VideoData(String video_name, String video_path, String lat, String lon, String dateTime, String formId, String formType, String isTranscoded, String sendingStatus,
                     String cellId, String lacId, String mcc, String mnc) {
        this.dbId = "-1";
        this.video_name = video_name;
        this.video_path = video_path;
        this.lat = lat;
        this.lon = lon;
        this.dateTime = dateTime;
        this.formId = formId;
        this.formType = formType;
        this.isTranscoded = isTranscoded;
        this.sendingStatus = sendingStatus;
        this.cellId = cellId;
        this.lacId = lacId;
        this.mcc = mcc;
        this.mnc = mnc;
    }

    public VideoData(Cursor cursor) {
//        System.out.println(" ID : "+cursor.getString(cursor.getColumnIndex(DBAdapter.ID))+"\n PATH : "+cursor.getString(cursor.getColumnIndex(DBAdapter.VIDEO_PATH))+"\n NAME : "+cursor.getString(cursor.getColumnIndex(DBAdapter.VIDEO_NAME)));
        this.dbId = cursor.getString(cursor.getColumnIndex(DBAdapter.ID));
        this.video_name = cursor.getString(cursor.getColumnIndex(DBAdapter.VIDEO_NAME));
        this.video_path = cursor.getString(cursor.getColumnIndex(DBAdapter.VIDEO_PATH));
        this.lat = cursor.getString(cursor.getColumnIndex(DBAdapter.LAT));
        this.lon = cursor.getString(cursor.getColumnIndex(DBAdapter.LON));
        this.dateTime = cursor.getString(cursor.getColumnIndex(DBAdapter.CREATED_DATE_TIME));
        this.formId = cursor.getString(cursor.getColumnIndex(DBAdapter.SPRAY_MONITORING_ID));
        this.formType = cursor.getString(cursor.getColumnIndex(DBAdapter.COUNT_TYPE));
        this.isTranscoded = cursor.getString(cursor.getColumnIndex(DBAdapter.IS_TRANSCODED));
        this.sendingStatus = cursor.getString(cursor.getColumnIndex(DBAdapter.SENDING_STATUS));
        this.cellId = cursor.getString(cursor.getColumnIndex(DBAdapter.CELL_ID));
        this.lacId = cursor.getString(cursor.getColumnIndex(DBAdapter.LAC_ID));
        this.mcc = cursor.getString(cursor.getColumnIndex(DBAdapter.MCC));
        this.mnc = cursor.getString(cursor.getColumnIndex(DBAdapter.MNC));
    }

    public boolean save(DBAdapter db) {

        boolean isSaved = false;
        Cursor cursor = db.checkVideoForTankfilling(this.getFormId(), this.getFormType(), this.getVideo_name());
        if (cursor.getCount() > 0) {
            isSaved = true;
        }

        ContentValues values = new ContentValues();
        values.put(DBAdapter.VIDEO_NAME, this.getVideo_name());
        values.put(DBAdapter.VIDEO_PATH, this.getVideo_path());
        values.put(DBAdapter.LAT, this.getLat());
        values.put(DBAdapter.LON, this.getLon());
        values.put(DBAdapter.CREATED_DATE_TIME, this.getDateTime());
        values.put(DBAdapter.SPRAY_MONITORING_ID, this.getFormId());
        values.put(DBAdapter.COUNT_TYPE, this.getFormType());
        values.put(DBAdapter.IS_TRANSCODED, this.getIsTranscoded());
        values.put(DBAdapter.SENDING_STATUS, this.getSendingStatus());
        values.put(DBAdapter.CELL_ID, this.getCellId());
        values.put(DBAdapter.LAC_ID, this.getLacId());
        values.put(DBAdapter.MCC, this.getMcc());
        values.put(DBAdapter.MNC, this.getMnc());

        long k = 0;

        System.out.println("Video Name " + this.getVideo_name() + ", Video Date " + this.getDateTime() + " isSaved : " + isSaved + " FormId " + this.getFormId());

        if (isSaved) {
            k = db.db.update(DBAdapter.TABLE_VIDEO, values, DBAdapter.SPRAY_MONITORING_ID + "='" + formId + "' AND " + DBAdapter.ID + " = '" + this.getDbId() + "'", null);
        } else {
            k = db.db.insert(DBAdapter.TABLE_VIDEO, null, values);
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
        if (this.getVideo_name() != null) {
            em.appendChild(document.createTextNode(this.getVideo_name()));
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

        String filePath = getVideo_path().replace("mp4", "xml");
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

    public String send(DBAdapter db) {

        File imageFile = new File(this.getVideo_path());

        if (imageFile.exists()) {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(this.getVideo_path(), options);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String imageString = Base64.encodeToString(byteArray, Base64.DEFAULT);

            HashMap<String, String> params = new HashMap<String, String>();

            String methodOnServer = "submitImage";
            params.put("formId", this.getFormId());
            params.put("formType", this.getFormType());
            params.put("imageString", imageString);
            params.put("lat", this.getLat());
            params.put("lng", this.getLon());
            params.put("createdDate", this.getDateTime());
            params.put("cell_id", this.getCellId());
            params.put("lac_id", this.getLacId());
            params.put("mcc", this.getMcc());
            params.put("mnc", this.getMnc());

		/*	String methodOnServer = "getStationMaster";
            params.put("strname","Country");
			params.put("prmCountryId","0");
			params.put("PrmStateId","0");
			params.put("PrmDistrictid","0");
			params.put("PrmBlockId", "0");*/

            String response = Synchronize.requestWithServer(methodOnServer, params);

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


    public String getVideo_name() {
        return video_name;
    }

    public void setVideo_name(String video_name) {
        this.video_name = video_name;
    }

    public String getVideo_path() {
        return video_path;
    }

    public void setVideo_path(String video_path) {
        this.video_path = video_path;
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

    public VideoData(Parcel in) {
        String[] data = new String[14];

        in.readStringArray(data);
        this.video_name = data[0];
        this.video_path = data[1];
        this.lat = data[2];
        this.lon = data[3];
        this.dateTime = data[4];
        this.formId = data[5];
        this.formType = data[6];
        this.isTranscoded = data[7];
        this.sendingStatus = data[8];
        this.cellId = data[9];
        this.lacId = data[10];
        this.mcc = data[11];
        this.mnc = data[12];
        this.dbId = data[13];
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                this.video_name,
                this.video_path,
                this.lat,
                this.lon,
                this.dateTime,
                this.formId,
                this.formType,
                this.isTranscoded,
                this.sendingStatus,
                this.cellId,
                this.lacId,
                this.mcc,
                this.mnc,
                this.dbId});
    }

    public static final Creator<VideoData> CREATOR = new Creator<VideoData>() {

        @Override
        public VideoData createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            return new VideoData(source); // using parcelable constructor
        }

        @Override
        public VideoData[] newArray(int size) {
            // TODO Auto-generated method stub
            return new VideoData[size];
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

    public String post(DBAdapter db) {
//    public void post() {
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(Synchronize.URL + "video");
        File videoFile = new File(this.getVideo_path());
        String result;

        Credential credential = new Credential();
        Cursor credentialCursor = db.getCredential();
        if (credentialCursor.getCount() > 0) {
            credentialCursor.moveToFirst();
            credential = new Credential(credentialCursor);
        }
        credentialCursor.close();

        try {
            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            entity.addPart("video_file", new FileBody(new File(this.getVideo_path())));
            System.out.println("video_file : "+this.getVideo_path());

            if (credential.getUserId() != null) {
                entity.addPart("user_id", new StringBody(credential.getUserId()));
                System.out.println("video_file : "+this.getVideo_path());
            }

            if (this.getFormId() != null) {
                entity.addPart("experiment_id", new StringBody(this.getFormId()));
                System.out.println("else experiment_id : "+this.getFormId());
            }

            Cursor tankFillingCursor = db.getTankFillingById(this.getFormType());
            if (tankFillingCursor.moveToFirst()) {
                String tankFillingCount = tankFillingCursor.getString(tankFillingCursor.getColumnIndex(DBAdapter.TANK_FILLING_COUNT));
                if (tankFillingCount != null) {
                    entity.addPart("tankFillingNumber", new StringBody(tankFillingCount));
                    System.out.println("tankFillingNumber : "+tankFillingCount);
                }
            }else{
                entity.addPart("tankFillingNumber", new StringBody("1"));
                System.out.println("else tankFillingNumber : "+"1");
            }
            tankFillingCursor.close();

            if (this.getLat() != null) {
                entity.addPart("latitude", new StringBody(this.getLat()));
                System.out.println("latitude : "+this.getLat());
            }
            if (this.getLon() != null) {
                entity.addPart("longitude", new StringBody(this.getLon()));
                System.out.println("longitude : "+this.getLon());
            }
            if (credential.getImei() != null) {
                entity.addPart("imei", new StringBody(credential.getImei()));
                System.out.println("imei : "+credential.getImei());
            }
            if (this.getDateTime() != null) {
                entity.addPart("device_date", new StringBody(this.getDateTime()));
                System.out.println("device_date : "+this.getDateTime());
            }
            if (this.getMcc() != null) {
                entity.addPart("mcc", new StringBody(this.getMcc()));
                System.out.println("mcc : "+this.getMcc());
            }
            if (this.getMnc() != null) {
                entity.addPart("mnc", new StringBody(this.getMnc()));
                System.out.println("mnc : "+this.getMnc());
            }
            if (this.getLacId() != null) {
                entity.addPart("lac_id", new StringBody(this.getLacId()));
                System.out.println("lac_id : "+this.getLacId());
            }
            if (this.getCellId() != null) {
                entity.addPart("cell_id", new StringBody(this.getCellId()));
                System.out.println("cell_id : "+this.getCellId());
            }

            httpPost.setEntity(entity);

            HttpResponse response = httpClient.execute(httpPost, localContext);

            BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();

            result = sb.toString();
            System.out.println("Result : " + result);

        } catch (IOException e) {
            e.printStackTrace();
            result = Synchronize.SERVER_ERROR_MESSAGE;
        }
        return result;
    }


}
