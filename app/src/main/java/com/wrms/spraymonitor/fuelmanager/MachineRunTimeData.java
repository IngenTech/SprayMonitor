package com.wrms.spraymonitor.fuelmanager;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.wrms.spraymonitor.app.DBAdapter;
import com.wrms.spraymonitor.app.Synchronize;
import com.wrms.spraymonitor.dataobject.ContentData;
import com.wrms.spraymonitor.dataobject.Credential;
import com.wrms.spraymonitor.utils.FolderManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
 * Created by Admin on 27-03-2017.
 */

public class MachineRunTimeData implements Parcelable {

    private String id;
    private String fuelId;
    private String fuelDate;
    private String MachineStartHour;
    private String MachineEndHour;

    private String createdDateTime;
    private String sendingStatus;
    private String machineId;

    public MachineRunTimeData() {

    }


    public MachineRunTimeData(String fuelId, DBAdapter db) {

        Cursor runCursor = db.getRunFuelById(fuelId);
        System.out.println("Count String : " + runCursor.getCount());

        if (runCursor.moveToFirst()) {
            this.id = (runCursor.getString(runCursor.getColumnIndex(DBAdapter.ID)));
            this.fuelId = (runCursor.getString(runCursor.getColumnIndex(DBAdapter.FUEL_ID)));

            this.fuelDate = (runCursor.getString(runCursor.getColumnIndex(DBAdapter.FUEL_DATE)));
            this.MachineStartHour = (runCursor.getString(runCursor.getColumnIndex(DBAdapter.MACHINE_START_HOUR)));
            this.MachineEndHour = (runCursor.getString(runCursor.getColumnIndex(DBAdapter.MACHINE_STOP_HOUR)));

            this.createdDateTime = (runCursor.getString(runCursor.getColumnIndex(DBAdapter.CREATED_DATE_TIME)));
            this.sendingStatus = (runCursor.getString(runCursor.getColumnIndex(DBAdapter.SENDING_STATUS)));
            this.machineId = (runCursor.getString(runCursor.getColumnIndex(DBAdapter.MACHINE_ID)));
        }
        runCursor.close();
    }


    protected MachineRunTimeData(Parcel in) {
        id = in.readString();
        fuelId = in.readString();
        fuelDate = in.readString();
        MachineStartHour = in.readString();
        MachineEndHour = in.readString();

        createdDateTime = in.readString();
        sendingStatus = in.readString();
        machineId = in.readString();


    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(fuelId);
        dest.writeString(fuelDate);
        dest.writeString(MachineStartHour);
        dest.writeString(MachineEndHour);

        dest.writeString(createdDateTime);
        dest.writeString(sendingStatus);
        dest.writeString(machineId);


    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MachineRunTimeData> CREATOR = new Creator<MachineRunTimeData>() {
        @Override
        public MachineRunTimeData createFromParcel(Parcel in) {
            return new MachineRunTimeData(in);
        }

        @Override
        public MachineRunTimeData[] newArray(int size) {
            return new MachineRunTimeData[size];
        }
    };

    public boolean save(DBAdapter db) {

        boolean isSaved = false;

        Cursor cursor = db.getRunFuelByDate(this.getFuelDate());
        if (cursor.moveToFirst()) {
            if (cursor.getString(cursor.getColumnIndex(DBAdapter.SENDING_STATUS)).equals(DBAdapter.SENT)) {
                return false;
            }
            isSaved = true;
        }

        ContentValues values = new ContentValues();
        values.put(DBAdapter.FUEL_ID, this.getFuelId());
        values.put(DBAdapter.FUEL_DATE, this.getFuelDate());
        values.put(DBAdapter.MACHINE_ID, this.getMachineId());
        values.put(DBAdapter.MACHINE_START_HOUR, this.getMachineStartHour());
        values.put(DBAdapter.MACHINE_STOP_HOUR, this.getMachineEndHour());
        values.put(DBAdapter.CREATED_DATE_TIME, this.getCreatedDateTime());
        values.put(DBAdapter.SENDING_STATUS, this.getSendingStatus());

        long k = 0;

        if (isSaved) {
            k = db.db.update(DBAdapter.TABLE_RUN_TIME, values, DBAdapter.FUEL_DATE + "='" + this.getFuelDate() + "'", null);
        } else {
            k = db.db.insert(DBAdapter.TABLE_RUN_TIME, null, values);
        }
        if (k == -1) {
            isSaved = false;
        } else {
            isSaved = true;
        }

        saveToXML();

        return isSaved;

    }


    public boolean saveToXML() {
        boolean isSavedToXML = true;
        File directory = new File(FolderManager.getFuelDir());
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        try {

            Document document = documentBuilder.newDocument();
            Element rootElement = document.createElement("fuelDetails");
            document.appendChild(rootElement);

            Element em = document.createElement("fuelId");
            if (this.getFuelId() != null) {
                em.appendChild(document.createTextNode(this.getFuelId()));
                rootElement.appendChild(em);
            }

            em = document.createElement("fuelDate");
            if (this.getFuelDate() != null) {
                em.appendChild(document.createTextNode(this.getFuelDate()));
                rootElement.appendChild(em);
            }

            em = document.createElement("machineId");
            if (this.getMachineId() != null) {
                em.appendChild(document.createTextNode(this.getMachineId()));
                rootElement.appendChild(em);
            }


            em = document.createElement("createdDateTime");
            if (this.getCreatedDateTime() != null) {
                em.appendChild(document.createTextNode(this.getCreatedDateTime()));
                rootElement.appendChild(em);
            }


            em = document.createElement("sendingStatus");
            if (this.getSendingStatus() != null) {
                em.appendChild(document.createTextNode(this.getSendingStatus()));
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
            DOMSource source = new DOMSource(document);                                  // with Android
            StreamResult result = new StreamResult(directory.getAbsolutePath() + "/" + this.getFuelDate() + ".xml");                          //  :(
            try {
                transformer.transform(source, result);
            } catch (TransformerException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("Some Error in storing xml data");
            e.printStackTrace();
        }
        return isSavedToXML;
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

        map.put("UserID", credential.getUserId());

        if (this.getFuelId() != null) {
            map.put("RunHourID", this.getFuelId());
        } else {
            map.put("RunHourID", "");
        }

        if (this.getFuelDate() != null) {
            map.put("Date", this.getFuelDate());
        } else {
            map.put("Date", "");
        }

        if (this.getFuelDate() != null) {
            map.put("MachineStartHour", this.getMachineStartHour());
        } else {
            map.put("MachineStartHour", "");
        }

        if (this.getFuelDate() != null) {
            map.put("MachineEndHour", this.getMachineEndHour());
        } else {
            map.put("MachineEndHour", "");
        }

        if (this.getMachineId() != null && (!this.getMachineId().trim().equals("-1"))) {
            map.put("MachineCode", this.getMachineId());
        }/* else {
            map.put("MachineCode", "");
        }*/



        return map;
    }


    public String sendRunFuelDetail(DBAdapter db) {
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        String methodOnServer = "machineRunHour";
        Map<String, String> map = this.getParametersInMap(db);

        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
            params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        String response = Synchronize.requestForRESTPost(params, methodOnServer);

        if (Synchronize.isConnectedToServer) {
            if (response.contains("success") || response.contains("AlreadyExist")) {
                response = "Success";
                this.setSendingStatus(DBAdapter.SENT);
                this.save(db);
            } else {
                this.setSendingStatus(DBAdapter.SAVE);
                this.save(db);
                response = "Fail";
            }
            return response;
        } else {
            return Synchronize.SERVER_ERROR_MESSAGE;
        }
    }


    public ArrayList<ContentData> getRunListContentData() {
        ArrayList<ContentData> data = new ArrayList<>();
        data.add(getContentData("Fuel Detail Id", this.getFuelId()));
        data.add(getContentData("Fuel Date", this.getFuelDate()));
        data.add(getContentData("Machine Code", this.getMachineId()));
        data.add(getContentData("Machine Start Hour", this.getMachineStartHour()));
        data.add(getContentData("Machine Stop Hour", this.getMachineEndHour()));


        data.add(getContentData("Created Date Time", this.getCreatedDateTime()));
        data.add(getContentData("Created Date Time", this.getCreatedDateTime()));
        data.add(getContentData("Sending Status", this.getSendingStatus()));

        return data;
    }

    private ContentData getContentData(String title, String value) {
        ContentData data = new ContentData();
        data.setTitle(title);
        data.setValue(value);
        return data;
    }


    public static final String createFuelId() {
        String newId = "";
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((10000 - 1000) + 1) + 1000;
        newId = String.valueOf(System.currentTimeMillis()) + "_" + randomNum;
        return newId;
    }



    public String getId() {
        if (id != null) {
            return id;
        } else {
            return "";
        }
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFuelId() {
        if (fuelId != null) {
            return fuelId;
        } else {
            return "";
        }
    }

    public void setFuelId(String fuelId) {
        this.fuelId = fuelId;
    }

    public String getFuelDate() {
        if (fuelDate != null) {
            return fuelDate;
        } else {
            return "";
        }
    }


    public String getMachineId() {
        if (machineId != null && machineId.trim().length() > 0) {
            return machineId;
        } else {
            return "";
        }
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }


    public String getSendingStatus() {
        if (sendingStatus != null) {
            return sendingStatus;
        } else {
            return "";
        }
    }

    public void setFuelDate(String fuelDate) {
        this.fuelDate = fuelDate;
    }

    public String getMachineStartHour() {
        return MachineStartHour;
    }

    public void setMachineStartHour(String machineStartHour) {
        MachineStartHour = machineStartHour;
    }

    public String getMachineEndHour() {
        return MachineEndHour;
    }

    public void setMachineEndHour(String machineEndHour) {
        MachineEndHour = machineEndHour;
    }

    public void setSendingStatus(String sendingStatus) {
        this.sendingStatus = sendingStatus;
    }

    public String getCreatedDateTime() {
        if (createdDateTime != null) {
            return createdDateTime;
        } else {
            return "";
        }
    }

    public void setCreatedDateTime(String createdDateTime) {
        this.createdDateTime = createdDateTime;
    }
}
