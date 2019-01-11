package com.wrms.spraymonitor.dataobject;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.wrms.spraymonitor.app.DBAdapter;
import com.wrms.spraymonitor.app.Synchronize;
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
 * Created by WRMS on 09-06-2016.
 */
public class PaymentObject implements Parcelable {

    private String paymentId;
    private String sprayMonitoringId;
    private String dateTime;
    private double collectedAmount;
    private double balanceAmount;
    private double receivableAmount;
    private String sendingStatus;
    private String farmerId;


    public PaymentObject() {

    }

    public boolean save(DBAdapter db) {

        boolean isSaved = false;

        String paymentDbId = null;
        Cursor cursor = db.getSprayPayment(this.getSprayMonitoringId(), this.getDateTime());
        if (cursor.moveToFirst()) {
            paymentDbId = cursor.getString(cursor.getColumnIndex(DBAdapter.ID));
            isSaved = true;
        }

        ContentValues values = new ContentValues();
        values.put(DBAdapter.SPRAY_MONITORING_ID, this.getSprayMonitoringId());
        values.put(DBAdapter.AMOUNT_COLLECTED, this.getCollectedAmount());
        values.put(DBAdapter.BALANCE_AMOUNT, this.getBalanceAmount());
        values.put(DBAdapter.CREATED_DATE_TIME, this.getDateTime());
        values.put(DBAdapter.SENDING_STATUS, this.getSendingStatus());
        values.put(DBAdapter.PAYMENT_ID, this.getPaymentId());
        values.put(DBAdapter.FARMER_ID, this.getFarmerId());

        long k = 0;

        if (isSaved) {
            k = db.db.update(DBAdapter.TABLE_PAYMENT, values, DBAdapter.SPRAY_MONITORING_ID + "='" + this.getSprayMonitoringId() + "' AND " + DBAdapter.PAYMENT_ID + " = '" + this.getPaymentId() + "'", null);
        } else {
            k = db.db.insert(DBAdapter.TABLE_IMAGE, null, values);
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

        System.out.println("Inside save to xml");

        boolean isSavedToXML = true;
        File directory = new File(FolderManager.getFarmDir(this.getSprayMonitoringId()));
        System.out.println("directory.getAbsolutePath()" + directory.getAbsolutePath());
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
            Element rootElement = document.createElement("Payments");
            document.appendChild(rootElement);


            Element em = document.createElement("sprayMonitorId");
            if (this.getSprayMonitoringId() != null) {
                em.appendChild(document.createTextNode(this.getSprayMonitoringId()));
                rootElement.appendChild(em);
            }
            System.out.println("this.getSprayMonitoringId()" + this.getSprayMonitoringId());

            em = document.createElement("paymentId");
            if (this.getPaymentId() != null) {
                em.appendChild(document.createTextNode(this.getPaymentId()));
                rootElement.appendChild(em);
            }
            System.out.println("this.getPaymentId()" + this.getPaymentId());

            em = document.createElement("farmerId");
            if (this.getFarmerId() != null) {
                em.appendChild(document.createTextNode(this.getFarmerId()));
                rootElement.appendChild(em);
            }

            em = document.createElement("dateTime");
            if (this.getDateTime() != null) {
                em.appendChild(document.createTextNode(this.getDateTime()));
                rootElement.appendChild(em);
            }
            System.out.println("this.getDateTime()" + this.getDateTime());

            em = document.createElement("collectedAmount");
            em.appendChild(document.createTextNode(String.valueOf(this.getCollectedAmount())));
            rootElement.appendChild(em);
            System.out.println("this.getCollectedAmount()" + this.getCollectedAmount());

            em = document.createElement("balanceAmount");
            em.appendChild(document.createTextNode(String.valueOf(this.getBalanceAmount())));
            rootElement.appendChild(em);
            System.out.println("this.getBalanceAmount()" + this.getBalanceAmount());


            em = document.createElement("receivableAmount");
            em.appendChild(document.createTextNode(String.valueOf(this.getReceivableAmount())));
            rootElement.appendChild(em);
            System.out.println("this.getReceivableAmount()" + this.getReceivableAmount());


            em = document.createElement("sendingStatus");
            if (this.getSendingStatus() != null) {
                em.appendChild(document.createTextNode(this.getSendingStatus()));
                rootElement.appendChild(em);
                System.out.println("this.getSendingStatus()" + this.getSendingStatus());
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
            StreamResult result = new StreamResult(directory.getAbsolutePath() + File.separator + this.getPaymentId() + ".xml");                          //  :(
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


    public PaymentObject(Cursor cursor, String receivableAmount) {
        this.sprayMonitoringId = cursor.getString(cursor.getColumnIndex(DBAdapter.SPRAY_MONITORING_ID));
        this.dateTime = cursor.getString(cursor.getColumnIndex(DBAdapter.CREATED_DATE_TIME));
        try {
            this.collectedAmount = Double.parseDouble(cursor.getString(cursor.getColumnIndex(DBAdapter.AMOUNT_COLLECTED)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            this.balanceAmount = Double.parseDouble(cursor.getString(cursor.getColumnIndex(DBAdapter.BALANCE_AMOUNT)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            this.receivableAmount = Double.parseDouble(receivableAmount);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.paymentId = cursor.getString(cursor.getColumnIndex(DBAdapter.PAYMENT_ID));
        this.farmerId = cursor.getString(cursor.getColumnIndex(DBAdapter.FARMER_ID));
        this.sendingStatus = cursor.getString(cursor.getColumnIndex(DBAdapter.SENDING_STATUS));
    }


    public String send(DBAdapter db) {
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        String methodOnServer = "payments";

        Map<String, String> map = this.getParametersInMap(db);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
            params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
/*        params.add(new BasicNameValuePair("user_id", credential.getUserId()));
        params.add(new BasicNameValuePair("experiment_id", this.getSprayMonitorId()));*/
        String response = Synchronize.requestForRESTPost(params, methodOnServer);
        if (Synchronize.isConnectedToServer) {
            if (response.contains("success") || response.contains("FormAlreadyExist")) {
                response = "Success";
                this.setSendingStatus(DBAdapter.SENT);
                this.save(db);
//                SaveImage.updateImageStatus(db, this.getFormId(), DBAdapter.SENT);
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


    public Map<String, String> getParametersInMap(DBAdapter db) {
        Map<String, String> map = new HashMap<>();

      /*  Credential credential = new Credential();
        Cursor credentialCursor = db.getCredential();
        if (credentialCursor.getCount() > 0) {
            credentialCursor.moveToFirst();
            credential = new Credential(credentialCursor);
        }
        credentialCursor.close();*/
        if (this.getPaymentId() != null) {
            map.put("payment_id", this.getPaymentId());
        }

        if (this.getFarmerId() != null) {
            map.put("farmer_code", this.getFarmerId());
        }

        if (this.getSprayMonitoringId() != null) {
            map.put("spray_monitoring_id", this.getSprayMonitoringId());
        }
        if (this.getDateTime() != null) {
            map.put("date_time", this.getDateTime());
        }
        map.put("collected_amount", String.valueOf(this.getCollectedAmount()));
        map.put("balance_amount", String.valueOf(this.getBalanceAmount()));

        return map;

    }

    public String getFarmerId() {
        return farmerId;
    }

    public void setFarmerId(String farmerId) {
        this.farmerId = farmerId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getSprayMonitoringId() {
        return sprayMonitoringId;
    }

    public void setSprayMonitoringId(String sprayMonitoringId) {
        this.sprayMonitoringId = sprayMonitoringId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public double getReceivableAmount() {
        return receivableAmount;
    }

    public void setReceivableAmount(double receivableAmount) {
        this.receivableAmount = receivableAmount;
    }

    public double getCollectedAmount() {
        return collectedAmount;
    }

    public void setCollectedAmount(double collectedAmount) {
        this.collectedAmount = collectedAmount;
    }

    public double getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(double balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    public String getSendingStatus() {
        return sendingStatus;
    }

    public void setSendingStatus(String sendingStatus) {
        this.sendingStatus = sendingStatus;
    }

    protected PaymentObject(Parcel in) {
        paymentId = in.readString();
        sprayMonitoringId = in.readString();
        dateTime = in.readString();
        collectedAmount = in.readDouble();
        balanceAmount = in.readDouble();
        receivableAmount = in.readDouble();
        sendingStatus = in.readString();
        farmerId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(paymentId);
        dest.writeString(sprayMonitoringId);
        dest.writeString(dateTime);
        dest.writeDouble(collectedAmount);
        dest.writeDouble(balanceAmount);
        dest.writeDouble(receivableAmount);
        dest.writeString(sendingStatus);
        dest.writeString(farmerId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PaymentObject> CREATOR = new Creator<PaymentObject>() {
        @Override
        public PaymentObject createFromParcel(Parcel in) {
            return new PaymentObject(in);
        }

        @Override
        public PaymentObject[] newArray(int size) {
            return new PaymentObject[size];
        }
    };


    public static final String createPaymentId() {
        String paymentId = "";

        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((10000 - 1000) + 1) + 1000;
        paymentId = String.valueOf(System.currentTimeMillis()) + "_" + String.valueOf(randomNum);

        return paymentId;
    }

}
