package com.wrms.spraymonitor.dataobject;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ExpandableListView;

import com.wrms.spraymonitor.app.Constents;
import com.wrms.spraymonitor.app.DBAdapter;
import com.wrms.spraymonitor.app.Synchronize;
import com.wrms.spraymonitor.utils.FolderManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
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
 * Created by WRMS on 20-10-2015.
 */

public class SprayMonitorData implements Parcelable, Comparable<SprayMonitorData> {

    private String sprayMonitorId;
    private String machineId;
    private String farmerName;
    private String farmerContact;
    private String state;
    private String district;
    private String tehsil;
    private String village;
    private String crop;
    private String productName;
    private String productUOM;
    private String productQty;
    private String totalAcrage;
    private String acrageCovered;
    private String productSprayedCount;
    private String lastSprayedQty;
    private String amountCollected;
    private String collectedBy;
    private String complaint;
    private String remark;
    private String deviceDateTime;
    private String latitude;
    private String longitude;
    private String lacId;
    private String cellId;
    private String mcc;
    private String mnc;
    private String barcode;
    private String sendingStatus;
    private String farmerId;
    private String farmerSendingStatus;
    private String villageName;
    private String paymentStatus;
    private String paymentCollectorName;
    private String totalAcreOfCrop;
    private String actualAcreCovered;

    public String getCrop_type() {
        return crop_type;
    }

    public void setCrop_type(String crop_type) {
        this.crop_type = crop_type;
    }

    private String amountReceivable;
    private String balanceAmount;

    private String crop_type;

    public String getPaymentCollectorName() {
        if (paymentCollectorName != null && paymentCollectorName.trim().length() > 0) {
            return paymentCollectorName;
        }
        return "";
    }

    public void setPaymentCollectorName(String paymentCollectorName) {
        this.paymentCollectorName = paymentCollectorName;
    }

    public String getTotalAcreOfCrop() {
        return totalAcreOfCrop;
    }

    public void setTotalAcreOfCrop(String totalAcreOfCrop) {
        this.totalAcreOfCrop = totalAcreOfCrop;
    }

    public String getActualAcreCovered() {
        return actualAcreCovered;
    }

    public void setActualAcreCovered(String actualAcreCovered) {
        this.actualAcreCovered = actualAcreCovered;
    }

    public String getAmountReceivable() {
        return (amountReceivable==null) ? "" : amountReceivable;
    }

    public void setAmountReceivable(String amountReceivable) {
        this.amountReceivable = amountReceivable;
    }

    public String getBalanceAmount() {
        return (balanceAmount==null) ? "" : balanceAmount;
    }

    public void setBalanceAmount(String balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    public SprayMonitorData() {
        super();
    }

    @Override
    public int compareTo(SprayMonitorData o) {
        Date date = new Date();
        try {
            date = Constents.sdf.parse(this.getDeviceDateTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Date objectDate = new Date();
        try {
            objectDate = Constents.sdf.parse(o.getDeviceDateTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date.compareTo(objectDate);
    }


    public SprayMonitorData(Parcel in) {

        String[] data = new String[39];

        in.readStringArray(data);
        this.sprayMonitorId = data[0];
        this.machineId = data[1];
        this.farmerName = data[2];
        this.farmerContact = data[3];
        this.state = data[4];
        this.district = data[5];
        this.tehsil = data[6];
        this.village = data[7];
        this.crop = data[8];
        this.productName = data[9];
        this.productUOM = data[10];
        this.productQty = data[11];
        this.totalAcrage = data[12];
        this.acrageCovered = data[13];
        this.productSprayedCount = data[14];
        this.lastSprayedQty = data[15];
        this.amountCollected = data[16];
        this.collectedBy = data[17];
        this.complaint = data[18];
        this.remark = data[19];
        this.deviceDateTime = data[20];
        this.latitude = data[21];
        this.longitude = data[22];
        this.lacId = data[23];
        this.cellId = data[24];
        this.mcc = data[25];
        this.mnc = data[26];
        this.barcode = data[27];
        this.sendingStatus = data[28];
        this.farmerId = data[29];
        this.farmerSendingStatus = data[30];
        this.villageName = data[31];
        this.paymentStatus = data[32];
        this.paymentCollectorName = data[33];
        this.totalAcreOfCrop = data[34];
        this.actualAcreCovered = data[35];
        this.amountReceivable = data[36];
        this.balanceAmount = data[37];
        this.crop_type = data[38];
    }

    public static final Parcelable.Creator<SprayMonitorData> CREATOR = new Parcelable.Creator<SprayMonitorData>() {

        @Override
        public SprayMonitorData createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            return new SprayMonitorData(source); // using parcelable constructor
        }

        @Override
        public SprayMonitorData[] newArray(int size) {
            // TODO Auto-generated method stub
            return new SprayMonitorData[size];
        }
    };

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{this.sprayMonitorId,
                this.machineId,
                this.farmerName,
                this.farmerContact,
                this.state,
                this.district,
                this.tehsil,
                this.village,
                this.crop,
                this.productName,
                this.productUOM,
                this.productQty,
                this.totalAcrage,
                this.acrageCovered,
                this.productSprayedCount,
                this.lastSprayedQty,
                this.amountCollected,
                this.collectedBy,
                this.complaint,
                this.remark,
                this.deviceDateTime,
                this.latitude,
                this.longitude,
                this.lacId,
                this.cellId,
                this.mcc,
                this.mnc,
                this.barcode,
                this.sendingStatus,
                this.farmerId,
                this.farmerSendingStatus,
                this.villageName,
                this.paymentStatus,
                this.paymentCollectorName,
                this.totalAcreOfCrop,
                this.actualAcreCovered,
                this.amountReceivable,
                this.balanceAmount,
                this.crop_type});
    }

    public SprayMonitorData(String id, DBAdapter db) {
        Cursor cursor = db.getFormByID(id);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            this.sprayMonitorId = cursor.getString(cursor.getColumnIndex(DBAdapter.SPRAY_MONITORING_ID));
            this.machineId = cursor.getString(cursor.getColumnIndex(DBAdapter.MACHINE_ID));
            this.farmerName = cursor.getString(cursor.getColumnIndex(DBAdapter.FIRST_NAME));
            this.farmerContact = cursor.getString(cursor.getColumnIndex(DBAdapter.FARMER_CONTACT));
            this.state = cursor.getString(cursor.getColumnIndex(DBAdapter.STATE_ID));
            this.district = cursor.getString(cursor.getColumnIndex(DBAdapter.DISTRICT_ID));
            this.tehsil = cursor.getString(cursor.getColumnIndex(DBAdapter.TEHSIL_ID));
            this.village = cursor.getString(cursor.getColumnIndex(DBAdapter.VILLAGE));
            this.crop = cursor.getString(cursor.getColumnIndex(DBAdapter.CROP_ID));
            this.productName = cursor.getString(cursor.getColumnIndex(DBAdapter.PRODUCT_ID));
            this.productUOM = cursor.getString(cursor.getColumnIndex(DBAdapter.MEASURING_UNIT_ID));
            this.productQty = cursor.getString(cursor.getColumnIndex(DBAdapter.PRODUCT_QTY));
            this.totalAcrage = cursor.getString(cursor.getColumnIndex(DBAdapter.TOTAL_ACRA));
            this.acrageCovered = cursor.getString(cursor.getColumnIndex(DBAdapter.ACRA_COVERED));
            this.productSprayedCount = cursor.getString(cursor.getColumnIndex(DBAdapter.PRODUCT_SPRAY_COUNT));
            this.lastSprayedQty = cursor.getString(cursor.getColumnIndex(DBAdapter.SPRAYED_QTY));
            this.amountCollected = cursor.getString(cursor.getColumnIndex(DBAdapter.AMOUNT_COLLECTED));
            this.collectedBy = cursor.getString(cursor.getColumnIndex(DBAdapter.COLLECTED_BY));
            this.complaint = cursor.getString(cursor.getColumnIndex(DBAdapter.COMPLAINT));
            this.remark = cursor.getString(cursor.getColumnIndex(DBAdapter.REMARK));
            this.deviceDateTime = cursor.getString(cursor.getColumnIndex(DBAdapter.CREATED_DATE_TIME));
            this.latitude = cursor.getString(cursor.getColumnIndex(DBAdapter.LAT));
            this.longitude = cursor.getString(cursor.getColumnIndex(DBAdapter.LON));
            this.lacId = cursor.getString(cursor.getColumnIndex(DBAdapter.LAC_ID));
            this.cellId = cursor.getString(cursor.getColumnIndex(DBAdapter.CELL_ID));
            this.mcc = cursor.getString(cursor.getColumnIndex(DBAdapter.MCC));
            this.mnc = cursor.getString(cursor.getColumnIndex(DBAdapter.MNC));
            this.barcode = cursor.getString(cursor.getColumnIndex(DBAdapter.BARCODE));
            this.sendingStatus = cursor.getString(cursor.getColumnIndex(DBAdapter.SENDING_STATUS));
            this.farmerId = cursor.getString(cursor.getColumnIndex(DBAdapter.FARMER_ID));
            this.paymentStatus = cursor.getString(cursor.getColumnIndex(DBAdapter.PAYMENT_STATUS));
            this.paymentCollectorName = cursor.getString(cursor.getColumnIndex(DBAdapter.PAYMENT_COLLECTOR_NAME));

            this.totalAcreOfCrop = cursor.getString(cursor.getColumnIndex(DBAdapter.TOTAL_ACRA_OF_CROP));
            this.actualAcreCovered = cursor.getString(cursor.getColumnIndex(DBAdapter.ACTUAL_ACRA_COVERED));
            this.amountReceivable = cursor.getString(cursor.getColumnIndex(DBAdapter.AMOUNT_RECEIVABLE));
            this.balanceAmount = cursor.getString(cursor.getColumnIndex(DBAdapter.BALANCE_AMOUNT));
            this.crop_type = cursor.getString(cursor.getColumnIndex(DBAdapter.CROP_TYPE));

            if (this.village != null && !this.village.isEmpty()) {
                Cursor villageCursor = db.getVillageById(this.village);
                if (villageCursor.moveToFirst()) {
                    this.villageName = villageCursor.getString(villageCursor.getColumnIndex(DBAdapter.VILLAGE));
                }
                villageCursor.close();
            }
            Cursor getFarmerById = db.getFarmerById(this.getFarmerId());
            if (getFarmerById.moveToFirst()) {
                this.farmerSendingStatus = getFarmerById.getString(getFarmerById.getColumnIndex(DBAdapter.SENDING_STATUS));
            }
            getFarmerById.close();

        }
    }


    public boolean save(DBAdapter db) {
        boolean isSave = true;

        boolean isIdExist = false;
        Cursor cursor = db.getFormByID(this.getSprayMonitorId());
        if (cursor.getCount() > 0) {
            isIdExist = true;
        }
        cursor.close();

        ContentValues values = new ContentValues();
        values.put(DBAdapter.SPRAY_MONITORING_ID, this.getSprayMonitorId());
        values.put(DBAdapter.MACHINE_ID, this.getMachineId());
        values.put(DBAdapter.FIRST_NAME, this.getFarmerName());
        values.put(DBAdapter.FARMER_CONTACT, this.getFarmerContact());
        values.put(DBAdapter.STATE_ID, this.getState());
        values.put(DBAdapter.DISTRICT_ID, this.getDistrict());
        values.put(DBAdapter.TEHSIL_ID, this.getTehsil());
        values.put(DBAdapter.VILLAGE, this.getVillage());
        values.put(DBAdapter.CROP_ID, this.getCrop());
        values.put(DBAdapter.PRODUCT_ID, this.getProductName());
        values.put(DBAdapter.MEASURING_UNIT_ID, this.getProductUOM());
        values.put(DBAdapter.PRODUCT_QTY, this.getProductQty());
        values.put(DBAdapter.TOTAL_ACRA, this.getTotalAcrage());
        values.put(DBAdapter.ACRA_COVERED, this.getAcrageCovered());
        values.put(DBAdapter.PRODUCT_SPRAY_COUNT, this.getProductSprayedCount());
        values.put(DBAdapter.SPRAYED_QTY, this.getLastSprayedQty());
        values.put(DBAdapter.AMOUNT_COLLECTED, this.getAmountCollected());
        values.put(DBAdapter.COLLECTED_BY, this.getCollectedBy());
        values.put(DBAdapter.COMPLAINT, this.getComplaint());
        values.put(DBAdapter.REMARK, this.getRemark());
        values.put(DBAdapter.LAT, this.getLatitude());
        values.put(DBAdapter.LON, this.getLongitude());
        values.put(DBAdapter.CELL_ID, this.getCellId());
        values.put(DBAdapter.LAC_ID, this.getLacId());
        values.put(DBAdapter.MCC, this.getMcc());
        values.put(DBAdapter.MNC, this.getMnc());
        values.put(DBAdapter.BARCODE, this.getBarcode());
        System.out.println("this.getSendingStatus() " + this.getSendingStatus());
        values.put(DBAdapter.SENDING_STATUS, this.getSendingStatus());
        values.put(DBAdapter.FARMER_ID, this.getFarmerId());
        values.put(DBAdapter.PAYMENT_STATUS, this.getPaymentStatus());
        values.put(DBAdapter.PAYMENT_COLLECTOR_NAME, this.getPaymentCollectorName());


        values.put(DBAdapter.TOTAL_ACRA_OF_CROP, this.getTotalAcreOfCrop());
        values.put(DBAdapter.ACTUAL_ACRA_COVERED, this.getActualAcreCovered());
        values.put(DBAdapter.AMOUNT_RECEIVABLE, this.getAmountReceivable());
        values.put(DBAdapter.BALANCE_AMOUNT, this.getBalanceAmount());
        values.put(DBAdapter.CROP_TYPE, this.getCrop_type());


        long isInserted = 0;
        System.out.println(DBAdapter.SPRAY_MONITORING_ID + " : " + this.getSprayMonitorId());
        if (!isIdExist) {
            //add new record
            /*values.put(DBAdapter.SENDING_STATUS, DBAdapter.SAVE);*/
            Date date = new Date();
            String dateString = Constents.sdf.format(date);
            values.put(DBAdapter.CREATED_DATE_TIME, dateString);
            isInserted = db.db.insert(DBAdapter.TABLE_SPRAY_MONITORING_FORM, null, values);

        } else {
            //update existing id
            isInserted = db.db.update(DBAdapter.TABLE_SPRAY_MONITORING_FORM, values, DBAdapter.SPRAY_MONITORING_ID + " = '" + this.getSprayMonitorId() + "'", null);
        }

        if (isInserted == -1) {
            isSave = false;
        }
        saveToXML(db);
        System.out.println("Spray is saved " + isSave + " isIdExist " + isIdExist);
        return isSave;
    }

    public boolean isSprayExists(DBAdapter db){
        boolean isIdExist = false;
        Cursor cursor = db.getFormByID(this.getSprayMonitorId());
        if (cursor.getCount() > 0) {
            isIdExist = true;
        }
        cursor.close();
        return isIdExist;
    }

    public boolean saveToXML(DBAdapter db) {
        boolean isSavedToXML = true;
        File directory = new File(FolderManager.getFarmDir(this.getSprayMonitorId()));
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
            Element rootElement = document.createElement("FORM");
            document.appendChild(rootElement);

            Element em = document.createElement("sprayMonitorId");
            if (this.getSprayMonitorId() != null) {
                em.appendChild(document.createTextNode(this.getSprayMonitorId()));
                rootElement.appendChild(em);
            }

            em = document.createElement("machineId");
            if (this.getMachineId() != null) {
                em.appendChild(document.createTextNode(this.getMachineId()));
                rootElement.appendChild(em);
            }

            em = document.createElement("farmerId");
            if (this.getFarmerId() != null) {
                em.appendChild(document.createTextNode(this.getFarmerId()));
                rootElement.appendChild(em);
            }

            em = document.createElement("farmerName");
            if (this.getFarmerName() != null) {
                em.appendChild(document.createTextNode(this.getFarmerName()));
                rootElement.appendChild(em);
            }

            em = document.createElement("farmerContact");
            if (this.getFarmerContact() != null) {
                em.appendChild(document.createTextNode(this.getFarmerContact()));
                rootElement.appendChild(em);
            }

            em = document.createElement("state");
            if (this.getState() != null) {
                em.appendChild(document.createTextNode(this.getState()));
                rootElement.appendChild(em);
            }

            /*em = document.createElement("territory");
            if(this.getTerritory()!=null) {
                em.appendChild(document.createTextNode(this.getTerritory()));
                rootElement.appendChild(em);
            }*/

            em = document.createElement("district");
            if (this.getDistrict() != null) {
                em.appendChild(document.createTextNode(this.getDistrict()));
                rootElement.appendChild(em);
            }

            em = document.createElement("tehsil");
            if (this.getTehsil() != null) {
                em.appendChild(document.createTextNode(this.getTehsil()));
                rootElement.appendChild(em);
            }


            em = document.createElement("village_code");
            if (this.getVillage() != null) {
                em.appendChild(document.createTextNode(this.getVillage()));
                rootElement.appendChild(em);
            }

            em = document.createElement("crop");
            if (this.getCrop() != null) {
                em.appendChild(document.createTextNode(this.getCrop()));
                rootElement.appendChild(em);
            }

            JSONArray tankFillingArray = new JSONArray();
            Cursor tankFillingCursor = db.getTankFilling(this.getSprayMonitorId());
            if (tankFillingCursor.moveToFirst()) {
                do {
                    String tankFillingId = tankFillingCursor.getString(tankFillingCursor.getColumnIndex(DBAdapter.TANK_FILLING_ID));

                    TankFillingData tankFillingData = new TankFillingData(tankFillingId, db);
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("tankFillingNumber", Integer.parseInt(tankFillingData.getTankFillingCount()));
                        jsonObject.put("cropCode", tankFillingData.getTankFillingCropId());
                        jsonObject.put("acrageCovered", Double.parseDouble(tankFillingData.getAcreCoveredByTank()));

                        Cursor productCursor = db.getSprayedProduct(tankFillingId);
                        if (productCursor.moveToFirst()) {
                            int productCount = 1;
                            do {
                                String productDataId = productCursor.getString(productCursor.getColumnIndex(DBAdapter.PRODUCT_DATA_ID));
                                ProductData productData = new ProductData(productDataId, db);

                                jsonObject.put("productCode" + productCount, productData.getProductId());
                                jsonObject.put("uomCode" + productCount, productData.getUomId());
                                jsonObject.put("productQuantity" + productCount, Double.parseDouble(productData.getProductQuantity()));

                                productCount++;
                            } while (productCursor.moveToNext());
                        }
                        productCursor.close();

                        tankFillingArray.put(jsonObject);

                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                } while (tankFillingCursor.moveToNext());
            }
            tankFillingCursor.close();

            em = document.createElement("tankFillingDetail");
            if (tankFillingArray.toString() != null) {
                em.appendChild(document.createTextNode(tankFillingArray.toString()));
                rootElement.appendChild(em);
            }

            Cursor sprayCropCursor = db.getSprayCrop(this.getSprayMonitorId());
            JSONObject cropJson = new JSONObject();
            if (sprayCropCursor.moveToFirst()) {
                try {
                    do {
                        String sprayCropIs = sprayCropCursor.getString(sprayCropCursor.getColumnIndex(DBAdapter.SPRAY_CROP_ID));
                        SprayCropData sprayCropData = new SprayCropData(sprayCropIs, db);
                        if(sprayCropData.getCropId().equals("-1")){
                            em = document.createElement("no_crop_acrage");
                            if (sprayCropData.getCropAcre() != null) {
                                em.appendChild(document.createTextNode(sprayCropData.getCropAcre()));
                                rootElement.appendChild(em);
                            }
                            continue;
                        }
                        cropJson.put(sprayCropData.getCropId(),Double.parseDouble(sprayCropData.getCropAcre()));

                    } while (sprayCropCursor.moveToNext());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            sprayCropCursor.close();

            em = document.createElement("Crop");
            if (cropJson.toString() != null) {
                em.appendChild(document.createTextNode(cropJson.toString()));
                rootElement.appendChild(em);
            }



            Cursor productCursor = db.getSprayedProduct(this.getSprayMonitorId());
            if (productCursor.moveToFirst()) {
                int i = 1;
                do {
                    String productName = productCursor.getString(productCursor.getColumnIndex(DBAdapter.PRODUCT_ID));
                    String productUOM = productCursor.getString(productCursor.getColumnIndex(DBAdapter.MEASURING_UNIT_ID));
                    String productQty = productCursor.getString(productCursor.getColumnIndex(DBAdapter.PRODUCT_QTY));

                    em = document.createElement("productName" + i);
                    if (productName != null) {
                        em.appendChild(document.createTextNode(productName));
                        rootElement.appendChild(em);
                    }

                    em = document.createElement("productUOM" + i);
                    if (productUOM != null) {
                        em.appendChild(document.createTextNode(productUOM));
                        rootElement.appendChild(em);
                    }

                    em = document.createElement("productQty" + i);
                    if (productQty != null) {
                        em.appendChild(document.createTextNode(productQty));
                        rootElement.appendChild(em);
                    }
                    i++;
                } while (productCursor.moveToNext());
            }
            productCursor.close();

            /*em = document.createElement("tankFillingNo");
            if (this.getTankFillingNo() != null) {
                em.appendChild(document.createTextNode(this.getTankFillingNo()));
                rootElement.appendChild(em);
            }


            em = document.createElement("tankfillingDateTime");
            if (this.getTankfillingDateTime() != null) {
                em.appendChild(document.createTextNode(this.getTankfillingDateTime()));
                rootElement.appendChild(em);
            }*/

            em = document.createElement("totalAcrage");
            if (this.getTotalAcrage() != null) {
                em.appendChild(document.createTextNode(this.getTotalAcrage()));
                rootElement.appendChild(em);
            }

            /*em = document.createElement("totalAcreOFCrop");
            if (this.getTotalAcreOfCrop() != null) {
                em.appendChild(document.createTextNode(this.getTotalAcreOfCrop()));
                rootElement.appendChild(em);
            }*/

            em = document.createElement("acrageCovered");
            if (this.getAcrageCovered() != null) {
                em.appendChild(document.createTextNode(this.getAcrageCovered()));
                rootElement.appendChild(em);
            }

            /*em = document.createElement("actualAcreCovered");
            if (this.getActualAcreCovered() != null) {
                em.appendChild(document.createTextNode(this.getActualAcreCovered()));
                rootElement.appendChild(em);
            }*/

            em = document.createElement("productSprayedCount");
            if (this.getProductSprayedCount() != null) {
                em.appendChild(document.createTextNode(this.getProductSprayedCount()));
                rootElement.appendChild(em);
            }

            em = document.createElement("lastSprayedQty");
            if (this.getLastSprayedQty() != null) {
                em.appendChild(document.createTextNode(this.getLastSprayedQty()));
                rootElement.appendChild(em);
            }

            em = document.createElement("amountCollected");
            if (this.getAmountCollected() != null) {
                em.appendChild(document.createTextNode(this.getAmountCollected()));
                rootElement.appendChild(em);
            }

            em = document.createElement("collectedBy");
            if (this.getCollectedBy() != null) {
                em.appendChild(document.createTextNode(this.getCollectedBy()));
                rootElement.appendChild(em);
            }

            /* em = document.createElement("amountReceivable");
            if (this.getAmountReceivable() != null) {
                em.appendChild(document.createTextNode(this.getAmountReceivable()));
                rootElement.appendChild(em);
            }

           em = document.createElement("balanceAmount");
            if (this.getBalanceAmount() != null) {
                em.appendChild(document.createTextNode(this.getBalanceAmount()));
                rootElement.appendChild(em);
            }

            em = document.createElement("complaint");
            if (this.getComplaint() != null) {
                em.appendChild(document.createTextNode(this.getComplaint()));
                rootElement.appendChild(em);
            }*/

            em = document.createElement("remark");
            if (this.getRemark() != null) {
                em.appendChild(document.createTextNode(this.getRemark()));
                rootElement.appendChild(em);
            }

            em = document.createElement("deviceDateTime");
            if (this.getDeviceDateTime() != null) {
                em.appendChild(document.createTextNode(this.getDeviceDateTime()));
                rootElement.appendChild(em);
            }

            em = document.createElement("latitude");
            if (this.getLatitude() != null) {
                em.appendChild(document.createTextNode(this.getLatitude()));
                rootElement.appendChild(em);
            }

            em = document.createElement("longitude");
            if (this.getLongitude() != null) {
                em.appendChild(document.createTextNode(this.getLongitude()));
                rootElement.appendChild(em);
            }

            em = document.createElement("lacId");
            if (this.getLacId() != null) {
                em.appendChild(document.createTextNode(this.getLacId()));
                rootElement.appendChild(em);
            }

            em = document.createElement("cellId");
            if (this.getCellId() != null) {
                em.appendChild(document.createTextNode(this.getCellId()));
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

            /*em = document.createElement("barcode");
            if (this.getBarcode() != null) {
                em.appendChild(document.createTextNode(this.getBarcode()));
                rootElement.appendChild(em);
            }

            em = document.createElement("paymentStatus");
            if (this.getPaymentStatus() != null) {
                em.appendChild(document.createTextNode(this.getPaymentStatus()));
                rootElement.appendChild(em);
            }

            em = document.createElement("paymentCollectorName");
            if (this.getPaymentCollectorName() != null) {
                em.appendChild(document.createTextNode(this.getPaymentCollectorName()));
                rootElement.appendChild(em);
            }*/

            em = document.createElement("sendingStatus");
            if (this.getSendingStatus() != null) {
                em.appendChild(document.createTextNode(this.getSendingStatus()));
                rootElement.appendChild(em);
            }

            Credential credential = new Credential();
            Cursor credentialCursor = db.getCredential();
            if (credentialCursor.getCount() > 0) {
                credentialCursor.moveToFirst();
                credential = new Credential(credentialCursor);
            }
            credentialCursor.close();

            em = document.createElement("fo_code");
            if (credential.getFoCode() != null) {
                em.appendChild(document.createTextNode(credential.getFoCode()));
                rootElement.appendChild(em);
            }

            em = document.createElement("total_acrage_of_crop");
            if (this.getTotalAcreOfCrop() != null) {
                em.appendChild(document.createTextNode(this.getTotalAcreOfCrop()));
                rootElement.appendChild(em);
            }

            em = document.createElement("actual_acrage_covered");
            if (this.getActualAcreCovered() != null) {
                em.appendChild(document.createTextNode(this.getActualAcreCovered()));
                rootElement.appendChild(em);
            }

            em = document.createElement("receivable_amount");
            if (this.getAmountReceivable() != null) {
                em.appendChild(document.createTextNode(this.getAmountReceivable()));
                rootElement.appendChild(em);
            }

            em = document.createElement("balance_amount");
            if (this.getBalanceAmount() != null) {
                em.appendChild(document.createTextNode(this.getBalanceAmount()));
                rootElement.appendChild(em);
            }

            em = document.createElement("crop_type");
            if (this.getCrop_type() != null) {
                em.appendChild(document.createTextNode(this.getCrop_type()));
                rootElement.appendChild(em);
            }

            Cursor farmerCursor = db.getFarmerByContact(this.getFarmerContact());
            String farmerCode = "_";
            if (farmerCursor.moveToFirst()) {
                farmerCode = farmerCursor.getString(farmerCursor.getColumnIndex(DBAdapter.FARMER_CODE));
            }
            farmerCursor.close();

            em = document.createElement("farmer_code");
            if (farmerCode != null) {
                em.appendChild(document.createTextNode(farmerCode));
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
            StreamResult result = new StreamResult(directory.getAbsolutePath() + "/form.xml");                          //  :(
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

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getProductQty() {
        if (productQty != null && productQty.trim().length() > 0) {
            return productQty;
        } else {
            return "0";
        }
    }

    public String getFarmerId() {
        if (farmerId != null && farmerId.trim().length() > 0) {
            return farmerId;
        }
        return "-1";
    }

    public String getFarmerSendingStatus() {
        if (farmerSendingStatus != null && farmerSendingStatus.trim().length() > 0) {
            return farmerSendingStatus;
        }
        return "";
    }

    public void setFarmerId(String farmerId, DBAdapter db) {
        this.farmerId = farmerId;
        Cursor getFarmerById = db.getFarmerById(this.getFarmerId());
        if (getFarmerById.moveToFirst()) {
            this.farmerSendingStatus = getFarmerById.getString(getFarmerById.getColumnIndex(DBAdapter.SENDING_STATUS));
        }
        getFarmerById.close();
    }

    public void setProductQty(String productQty) {
        this.productQty = productQty;
    }

    public void setAcrageCovered(String acrageCovered) {
        this.acrageCovered = acrageCovered;
    }

    public String getSprayMonitorId() {
        return sprayMonitorId;
    }

    public void setSprayMonitorId(String sprayMonitorId) {
        this.sprayMonitorId = sprayMonitorId;
    }

    public String getMachineId() {
        if (machineId != null && machineId.trim().length() > 0) {
            return machineId;
        } else {
            return "-1";
        }
    }

    public String getBarcode() {
        if (barcode != null && barcode.trim().length() > 0) {
            return barcode;
        } else {
            return "";
        }
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getSendingStatus() {
        return sendingStatus;
    }

    public void setSendingStatus(String sendingStatus) {
        this.sendingStatus = sendingStatus;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getFarmerName() {
        if (this.farmerName != null && this.farmerName.trim().length() > 0) {
            return farmerName;
        } else {
            return "";
        }
    }

    public void setFarmerName(String farmerName) {
        this.farmerName = farmerName;
    }

    public String getFarmerContact() {
        if (this.farmerContact != null && this.farmerContact.trim().length() > 0) {
            return farmerContact;
        } else {
            return "";
        }
    }

    public void setFarmerContact(String farmerContact) {
        this.farmerContact = farmerContact;
    }

    public String getState() {
        if (state != null && state.length() > 0) {
            return state;
        } else {
            return "-1";
        }

    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDistrict() {
        if (district != null && district.length() > 0) {
            return district;
        } else {
            return "-1";
        }

    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getTehsil() {
        if (tehsil != null && tehsil.length() > 0) {
            return tehsil;
        } else {
            return "-1";
        }

    }

    public void setTehsil(String tehsil) {
        this.tehsil = tehsil;
    }

    public String getVillage() {
        if (village != null && village.length() > 0) {
            return village;
        } else {
            return "";
        }
    }

    public void setVillage(String village, DBAdapter db) {

        System.out.println("Vilage id : " + village);
        if (village != null && !village.isEmpty()) {
            Cursor villageCursor = db.getVillageById(village);
            System.out.println("Village Cursor Length : " + villageCursor.getCount());
            if (villageCursor.moveToFirst()) {
                this.villageName = villageCursor.getString(villageCursor.getColumnIndex(DBAdapter.VILLAGE));
            }
            villageCursor.close();
        }
        this.village = village;

    }

    public String getVillageName() {
        return this.villageName;
    }

    public String getCrop() {

        if (crop != null && crop.length() > 0) {
            return crop;
        } else {
            return "-1";
        }
    }

    public void setCrop(String crop) {
        this.crop = crop;
    }

    public String getProductName() {
        if (productName != null && productName.length() > 0) {
            return productName;
        } else {
            return "-1";
        }
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductUOM() {
        if (productUOM != null && productUOM.length() > 0) {
            return productUOM;
        } else {
            return "-1";
        }
    }

    public void setProductUOM(String productUOM) {
        this.productUOM = productUOM;
    }

    public String getTotalAcrage() {
        if (totalAcrage != null && totalAcrage.length() > 0) {
            return totalAcrage;
        } else {
            return "";
        }
    }

    public void setTotalAcrage(String totalAcrage) {
        this.totalAcrage = totalAcrage;
    }

    public String getAcrageCovered() {
        if (acrageCovered != null && acrageCovered.length() > 0) {
            return acrageCovered;
        } else {
            return "";
        }
    }

    public void setAcrgaeCovered(String acrageCovered) {
        this.acrageCovered = acrageCovered;
    }

    public String getProductSprayedCount() {
        if (productSprayedCount != null && productSprayedCount.length() > 0) {
            return productSprayedCount;
        } else {
            return "";
        }
    }

    public void setProductSprayedCount(String productSprayedCount) {
        this.productSprayedCount = productSprayedCount;
    }

    public String getLastSprayedQty() {
        if (lastSprayedQty != null && lastSprayedQty.length() > 0) {
            return lastSprayedQty;
        } else {
            return "";
        }
    }

    public void setLastSprayedQty(String lastSprayedQty) {
        this.lastSprayedQty = lastSprayedQty;
    }

    public String getAmountCollected() {
        if (amountCollected != null && amountCollected.length() > 0) {
            return amountCollected;
        } else {
            return "";
        }
    }

    public void setAmountCollected(String amountCollected) {
        this.amountCollected = amountCollected;
    }

    public String getCollectedBy() {
        if (collectedBy != null && collectedBy.length() > 0) {
            return collectedBy;
        } else {
            return "";
        }
    }

    public void setCollectedBy(String collectedBy) {
        this.collectedBy = collectedBy;
    }

    public String getComplaint() {
        if (complaint != null && complaint.length() > 0) {
            return complaint;
        } else {
            return "";
        }
    }

    public void setComplaint(String complaint) {
        this.complaint = complaint;
    }

    public String getRemark() {
        if (remark != null && remark.length() > 0) {
            return remark;
        } else {
            return "";
        }
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDeviceDateTime() {
        if (deviceDateTime != null && deviceDateTime.trim().length() > 0) {
            return deviceDateTime;
        }
        return "";
    }

    public void setDeviceDateTime(String deviceDateTime) {
        this.deviceDateTime = deviceDateTime;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLacId() {
        return lacId;
    }

    public void setLacId(String lacId) {
        this.lacId = lacId;
    }

    public String getCellId() {
        return cellId;
    }

    public void setCellId(String cellId) {
        this.cellId = cellId;
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

    public ArrayList<ImageData> getImages(DBAdapter db) {
        ArrayList<ImageData> imageDatas = new ArrayList<>();
        Cursor getImageByFormId = db.getImageByFormId(this.sprayMonitorId);
        if (getImageByFormId.getCount() > 0) {
            getImageByFormId.moveToNext();
            do {
                ImageData data = new ImageData(getImageByFormId);
                imageDatas.add(data);
            } while (getImageByFormId.moveToNext());
        }
        getImageByFormId.close();
        return imageDatas;
    }

    public ArrayList<VideoData> getVideos(DBAdapter db) {
        ArrayList<VideoData> videoDatas = new ArrayList<>();
        Cursor getVideoByFormId = db.getVideoByFormId(this.sprayMonitorId);
        if (getVideoByFormId.getCount() > 0) {
            getVideoByFormId.moveToNext();
            do {
                VideoData data = new VideoData(getVideoByFormId);
                videoDatas.add(data);
            } while (getVideoByFormId.moveToNext());
        }
        getVideoByFormId.close();
        return videoDatas;
    }

    public ArrayList<PaymentObject> getPayments(DBAdapter db) {
        ArrayList<PaymentObject> paymentObjects = new ArrayList<>();
        Cursor getSprayPayment = db.getSprayPayment(this.sprayMonitorId);
        if (getSprayPayment.getCount() > 0) {
            getSprayPayment.moveToNext();
            do {
                PaymentObject data = new PaymentObject(getSprayPayment, this.getAmountReceivable());
                paymentObjects.add(data);
            } while (getSprayPayment.moveToNext());
        }
        getSprayPayment.close();
        return paymentObjects;
    }

    public String send(DBAdapter db) {
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        String methodOnServer = "form";

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
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        String methodOnServer = "form";
        Credential credential = new Credential();
        Cursor credentialCursor = db.getCredential();
        if (credentialCursor.getCount() > 0) {
            credentialCursor.moveToFirst();
            credential = new Credential(credentialCursor);
        }
        credentialCursor.close();

        if (credential.getUserId() != null) {
            map.put("user_id", credential.getUserId());
        }
        if (this.getSprayMonitorId() != null) {
            map.put("experiment_id", this.getSprayMonitorId());
        }
        if (this.getMachineId() != null) {
            map.put("machine_code", this.getMachineId());
        }
        if (this.getFarmerName() != null) {
            map.put("farmer_name", this.getFarmerName());
        }
        if (this.getFarmerContact() != null) {
            map.put("farmer_contact", this.getFarmerContact());
        }
        if (this.getState() != null) {
            map.put("state_id", this.getState());
        }
//        map.put("teritory_id", this.getTerritory());

        if (this.getDistrict() != null) {
            map.put("district_id", this.getDistrict());
        }
        if (this.getTehsil() != null) {
            map.put("tehsil_id", this.getTehsil());
        }
        if (this.getVillage() != null) {
            map.put("village_code", this.getVillage());
        }
        if (this.getCrop() != null) {
            map.put("crop_id", this.getCrop());
        }
        if (this.getPaymentStatus() != null) {
            map.put("payment_status", this.getPaymentStatus());
        }
        if (this.getPaymentCollectorName() != null) {
            map.put("payment_collected_by", this.getPaymentCollectorName());
        }

        JSONArray tankFillingArray = new JSONArray();
        Cursor tankFillingCursor = db.getTankFilling(this.getSprayMonitorId());
        if (tankFillingCursor.moveToFirst()) {
            do {
                String tankFillingId = tankFillingCursor.getString(tankFillingCursor.getColumnIndex(DBAdapter.TANK_FILLING_ID));

                TankFillingData tankFillingData = new TankFillingData(tankFillingId, db);
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("tankFillingNumber", Integer.parseInt(tankFillingData.getTankFillingCount()));
                    jsonObject.put("cropCode", tankFillingData.getTankFillingCropId());
                    jsonObject.put("acrageCovered", Double.parseDouble(tankFillingData.getAcreCoveredByTank()));

                    /*jsonObject.put("machineStartTime", tankFillingData.getStartTime());
                    jsonObject.put("machineStopTime",tankFillingData.getEndTime());*/

                    Cursor productCursor = db.getSprayedProduct(tankFillingId);
                    if (productCursor.moveToFirst()) {
                        int productCount = 1;
                        do {
                            String productDataId = productCursor.getString(productCursor.getColumnIndex(DBAdapter.PRODUCT_DATA_ID));
                            ProductData productData = new ProductData(productDataId, db);

                            jsonObject.put("productCode" + productCount, productData.getProductId());
                            jsonObject.put("uomCode" + productCount, productData.getUomId());
                            jsonObject.put("productQuantity" + productCount, Double.parseDouble(productData.getProductQuantity()));

                            productCount++;
                        } while (productCursor.moveToNext());
                    }
                    productCursor.close();

                    tankFillingArray.put(jsonObject);

                } catch (Exception e) {
                    e.printStackTrace();

                }
            } while (tankFillingCursor.moveToNext());
        }
        tankFillingCursor.close();
        map.put("tankFillingDetail", tankFillingArray.toString());

        Cursor sprayCropCursor = db.getSprayCrop(this.getSprayMonitorId());
        JSONObject cropJson = new JSONObject();
        if (sprayCropCursor.moveToFirst()) {
            try {
                do {
                    String sprayCropIs = sprayCropCursor.getString(sprayCropCursor.getColumnIndex(DBAdapter.SPRAY_CROP_ID));
                    SprayCropData sprayCropData = new SprayCropData(sprayCropIs, db);
                    if(sprayCropData.getCropId().equals("-1")){
                        map.put("no_crop_acrage", sprayCropData.getCropAcre());
                        continue;
                    }
                    cropJson.put(sprayCropData.getCropId(),Double.parseDouble(sprayCropData.getCropAcre()));

                } while (sprayCropCursor.moveToNext());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        sprayCropCursor.close();
        map.put("Crop", cropJson.toString());



        /*Cursor productCursor = db.getSprayedProduct(this.getSprayMonitorId());
        if (productCursor.moveToFirst()) {
            int count = 1;
            do {
                if (count < 4) {
                    if (productCursor.getString(productCursor.getColumnIndex(DBAdapter.PRODUCT_ID)) != null) {
                        map.put("product_id" + count, productCursor.getString(productCursor.getColumnIndex(DBAdapter.PRODUCT_ID)));
                    }
                    if (productCursor.getString(productCursor.getColumnIndex(DBAdapter.MEASURING_UNIT_ID)) != null) {
                        map.put("uom_id" + count, productCursor.getString(productCursor.getColumnIndex(DBAdapter.MEASURING_UNIT_ID)));
                    }
                    if (productCursor.getString(productCursor.getColumnIndex(DBAdapter.PRODUCT_QTY)) != null) {
                        map.put("uom_count" + count, productCursor.getString(productCursor.getColumnIndex(DBAdapter.PRODUCT_QTY)));
                    }
                }
                count++;
            } while (productCursor.moveToNext());
        }
        productCursor.close();*/

        if (this.getTotalAcrage() != null) {
            map.put("total_acrage", this.getTotalAcrage());
        }
        if (this.getAcrageCovered() != null) {
            map.put("acrage_covered", this.getAcrageCovered());
        }
        if (this.getProductSprayedCount() != null) {
            map.put("product_sprayed_count", this.getProductSprayedCount());
        }
        if (this.getLastSprayedQty() != null) {
            map.put("last_tankfilling_qty", this.getLastSprayedQty());
        }
        if (this.getAmountCollected() != null) {
            map.put("amount_collected", this.getAmountCollected());
        }
        if (this.getCollectedBy() != null) {
            map.put("field_collector", this.getCollectedBy());
        }
        if (this.getComplaint() != null) {
            map.put("complaint", this.getComplaint());
        }
        if (this.getRemark() != null) {
            map.put("remarks", this.getRemark());
        }
        if (this.getDeviceDateTime() != null) {
            map.put("device_date", this.getDeviceDateTime());
        }
        if (this.getLatitude() != null) {
            map.put("latitude", this.getLatitude());
        }
        if (this.getLongitude() != null) {
            map.put("longitude", this.getLongitude());
        }
        if (this.getLacId() != null) {
            map.put("lac_id", this.getLacId());
        }
        if (this.getCellId() != null) {
            map.put("cell_id", this.getCellId());
        }
        if (this.getMcc() != null) {
            map.put("mcc", this.getMcc());
        }
        if (this.getMnc() != null) {
            map.put("mnc", this.getMnc());
        }
        if (this.getBarcode() != null) {
            map.put("barcode", this.getBarcode());
        }

        Cursor farmerCursor = db.getFarmerByContact(this.getFarmerContact());
        String farmerCode = "_";
        if (farmerCursor.moveToFirst()) {
            farmerCode = farmerCursor.getString(farmerCursor.getColumnIndex(DBAdapter.FARMER_CODE));
        }
        farmerCursor.close();
        if (credential.getFoCode() != null) {
            map.put("fo_code", credential.getFoCode());
        }

        map.put("schedule_id", "");
        if (farmerCode != null) {
            map.put("farmer_code", farmerCode);
        }

        if (this.getTotalAcreOfCrop() != null) {
            map.put("total_acrage_of_crop", this.getTotalAcreOfCrop());
        }
        if (this.getActualAcreCovered() != null) {
            map.put("actual_acrage_covered", this.getActualAcreCovered());
        }
        if (this.getAmountReceivable() != null) {
            map.put("receivable_amount", this.getAmountReceivable());
        }
        if (this.getBalanceAmount() != null) {
            map.put("balance_amount", this.getBalanceAmount());
        }

        if (this.getCrop_type() != null) {
            map.put("crop_type", this.getCrop_type());
        }

        return map;
    }


    public ArrayList<ContentData> getListContentData(DBAdapter db) {

        ArrayList<ContentData> data = new ArrayList<>();
        data.add(getContentData("Spray Monitoring Id", this.getSprayMonitorId()));
        data.add(getContentData("Machine Id", db.getMachineNameById(this.getMachineId())));
        data.add(getContentData("Farmer Name", this.getFarmerName()));
        data.add(getContentData("Farmer Contact", this.getFarmerContact()));
//        data.add(getContentData("State", db.getStateNameById(this.getState()) + "(" + this.getState() + ")"));
//        data.add(getContentData("Territory",db.getTeritoryNameById(this.getTerritory())+"("+this.getTerritory()+")"));
//        data.add(getContentData("District", db.getDistrictNameById(this.getDistrict()) + "(" + this.getDistrict() + ")"));
//        data.add(getContentData("Tehsil", db.getTehsilNameById(this.getTehsil()) + "(" + this.getTehsil() + ")"));
        Cursor villageCursor = db.getVillageById(this.getVillage());
        String villageName = "";
        if (villageCursor.moveToFirst()) {
            villageName = villageCursor.getString(villageCursor.getColumnIndex(DBAdapter.VILLAGE));
        }
        villageCursor.close();
        data.add(getContentData("Village", villageName + "(" + this.getVillage() + ")"));
//        data.add(getContentData("Crop", db.getCropNameById(this.getCrop())));
        data.add(getContentData("Total Acre ", this.getTotalAcrage()));
//        data.add(getContentData("Total Acre of Crop", this.getTotalAcreOfCrop()));
//        data.add(getContentData("Acre to be Covered", this.getAcrageCovered()));
        data.add(getContentData("Actual Acre Covered", this.getActualAcreCovered()));


        Cursor productCursor = db.getSprayedProduct(this.getSprayMonitorId());
        int totalProductCount = productCursor.getCount();
        if (productCursor.moveToFirst()) {
            int i = 1;
            do {
                String priductId = productCursor.getString(productCursor.getColumnIndex(DBAdapter.PRODUCT_ID));
                String productName = db.getProductNameById(priductId);

                String uomId = productCursor.getString(productCursor.getColumnIndex(DBAdapter.MEASURING_UNIT_ID));
                String uomName = db.getUomNameById(uomId);

                String productQty = productCursor.getString(productCursor.getColumnIndex(DBAdapter.PRODUCT_QTY));

                data.add(getContentData("Product Name " + i, productName));
                data.add(getContentData("UOM " + i, uomName));
                data.add(getContentData("Product" + i + " Quantity", productQty));

                i++;
            } while (productCursor.moveToNext());
        }
        productCursor.close();


//        data.add(getContentData("Product Name",db.getProductNameById(this.getProductName())));
//        data.add(getContentData("UOM",db.getMeasureUnitNameById(this.getProductUOM())));
//        data.add(getContentData("Barcode",this.getBarcode()));
//        data.add(getContentData("Product Quantity",this.getProductQty()));
//        data.add(getContentData("Tank Filling Number",this.getTankFillingNo()));
//        data.add(getContentData("Tank Filling Date Time",this.getTankfillingDateTime()));

//        data.add(getContentData("Product Spray Count",this.getProductSprayedCount()));
//        data.add(getContentData("Quantity Sprayed",this.getLastSprayedQty()));
        data.add(getContentData("Amount Receivable", this.getAmountReceivable() + " Rs."));
        data.add(getContentData("Amount Collected", this.getAmountCollected() + " Rs."));
        if (!this.getBalanceAmount().equals("0.0")) {
            data.add(getContentData("Balance Amount", this.getBalanceAmount() + " Rs."));
            data.add(getContentData("Balance will be Collected by", this.getCollectedBy()));
        }

        Cursor paymentCursor = db.getSprayPayment(this.getSprayMonitorId());
        if (paymentCursor.moveToFirst()) {
            int i = 1;
            do {
                String amountCollectedString = paymentCursor.getString(paymentCursor.getColumnIndex(DBAdapter.AMOUNT_COLLECTED));
                String balanceAmountString = paymentCursor.getString(paymentCursor.getColumnIndex(DBAdapter.BALANCE_AMOUNT));
                String sendingStatus = paymentCursor.getString(paymentCursor.getColumnIndex(DBAdapter.SENDING_STATUS));
                data.add(getContentData("Payment " + i, amountCollectedString + " Rs."));
                data.add(getContentData("Sending Status of Payment " + i, sendingStatus));

                i++;
            } while (paymentCursor.moveToNext());
            paymentCursor.moveToLast();
        }
        paymentCursor.close();

//        data.add(getContentData("Collected By", this.getCollectedBy()));
//        data.add(getContentData("Payment Status", this.getPaymentStatus()));
      /*  if (!this.getCollectedBy().equalsIgnoreCase("myself")) {
            data.add(getContentData("Payment Collector Name", this.getPaymentCollectorName()));
        }*/
//        data.add(getContentData("Complaint",this.getComplaint()));
        data.add(getContentData("Remark", this.getRemark()));
        data.add(getContentData("Device Date Time", this.getDeviceDateTime()));
        data.add(getContentData("Latitude", this.getLatitude()));
        data.add(getContentData("Longitude", this.getLongitude()));
        data.add(getContentData("Lac Id", this.getLacId()));
        data.add(getContentData("Cell Id", this.getCellId()));
        data.add(getContentData("MCC", this.getMcc()));
        data.add(getContentData("MNC", this.getMnc()));
        data.add(getContentData("Sending Status", this.getSendingStatus()));
        data.add(getContentData("Crop Type", this.getCrop_type()));

        int totalImageCount = 0;
        int savedImageCount = 0;
        int sentImageCount = 0;
        int submitImageCount = 0;
        Cursor imageCursor = db.getImageByFormId(this.getSprayMonitorId());
        totalImageCount = imageCursor.getCount();
        if (imageCursor.moveToFirst()) {
            do {
                String isSaved = imageCursor.getString(imageCursor.getColumnIndex(DBAdapter.SENDING_STATUS));
                if (isSaved.equals(DBAdapter.SUBMIT)) {
                    submitImageCount++;
                } else if (isSaved.equals(DBAdapter.SENT)) {
                    sentImageCount++;
                } else {
                    savedImageCount++;
                }
            } while (imageCursor.moveToNext());
        }
        imageCursor.close();

        data.add(getContentData("Total Image", String.valueOf(totalImageCount)));
        /*if(savedImageCount>0)
        data.add(getContentData("Saved Image",String.valueOf(savedImageCount)));*/
        if (submitImageCount > 0)
            data.add(getContentData("Saved Image", String.valueOf(submitImageCount)));
        if (sentImageCount > 0)
            data.add(getContentData("Sent Image", String.valueOf(sentImageCount)));


        int totalVideoCount = 0;
        int savedVideoCount = 0;
        int sentVideoCount = 0;
        int submitVideoCount = 0;
        Cursor videoCursor = db.getVideoByFormId(this.getSprayMonitorId());
        totalVideoCount = videoCursor.getCount();
        if (videoCursor.moveToFirst()) {
            do {
                String isSaved = videoCursor.getString(videoCursor.getColumnIndex(DBAdapter.SENDING_STATUS));
                if (isSaved.equals(DBAdapter.SUBMIT)) {
                    submitVideoCount++;
                } else if (isSaved.equals(DBAdapter.SENT)) {
                    sentVideoCount++;
                } else {
                    savedVideoCount++;
                }
            } while (videoCursor.moveToNext());
        }
        videoCursor.close();

        data.add(getContentData("Total Video", String.valueOf(totalVideoCount)));
        /*if(savedVideoCount>0)
            data.add(getContentData("Saved Video",String.valueOf(savedVideoCount)));*/
        if (submitVideoCount > 0)
            data.add(getContentData("Saved Video", String.valueOf(submitVideoCount)));
        if (sentVideoCount > 0)
            data.add(getContentData("Sent Video", String.valueOf(sentVideoCount)));


        return data;
    }

    private ContentData getContentData(String title, String value) {
        ContentData data = new ContentData();
        data.setTitle(title);
        data.setValue(value);
        return data;
    }

}
