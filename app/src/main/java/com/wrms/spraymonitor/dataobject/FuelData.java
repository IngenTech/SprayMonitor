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
 * Created by WRMS on 20-06-2016.
 */
public class FuelData implements Parcelable {

    private String id;
    private String fuelId;
    private String fuelDate;
    private String morningFuelLevel;
    private String eveningFuelLevel;
    private String consumedFuel;
    private String dieselFilled;
    private String billNo;
    private String billAmount;
    private String filledDieselAmount;

    private String billNo1;
    private String billAmount1;
    private String filledDieselAmount1;

    private String billNo2;
    private String billAmount2;
    private String filledDieselAmount2;

    private String tractorLeased;
    private String costPerDay;
    private String remark;
    private String createdDateTime;
    private String sendingStatus;
    private String machineId;

    public FuelData() {

    }

    public FuelData(String fuelId, DBAdapter db) {

        Cursor fuelCursor = db.getFuelById(fuelId);
        if (fuelCursor.moveToFirst()) {
            this.id = (fuelCursor.getString(fuelCursor.getColumnIndex(DBAdapter.ID)));
            this.fuelId = (fuelCursor.getString(fuelCursor.getColumnIndex(DBAdapter.FUEL_ID)));
            this.fuelDate = (fuelCursor.getString(fuelCursor.getColumnIndex(DBAdapter.FUEL_DATE)));
            this.morningFuelLevel = (fuelCursor.getString(fuelCursor.getColumnIndex(DBAdapter.MORNING_FUEL_LEVEL)));
            this.eveningFuelLevel = (fuelCursor.getString(fuelCursor.getColumnIndex(DBAdapter.EVENING_FUEL_LEVEL)));
            this.consumedFuel = (fuelCursor.getString(fuelCursor.getColumnIndex(DBAdapter.CONSUMED_FUEL)));
            this.dieselFilled = (fuelCursor.getString(fuelCursor.getColumnIndex(DBAdapter.DIESEL_FILLED)));
            this.billNo = (fuelCursor.getString(fuelCursor.getColumnIndex(DBAdapter.BILL_NO)));
            this.billAmount = (fuelCursor.getString(fuelCursor.getColumnIndex(DBAdapter.BILL_AMOUNT)));
            this.filledDieselAmount = (fuelCursor.getString(fuelCursor.getColumnIndex(DBAdapter.FILLED_DIESEL_AMOUNT)));

            this.billNo1 = (fuelCursor.getString(fuelCursor.getColumnIndex(DBAdapter.BILL_NO1)));
            this.billAmount1 = (fuelCursor.getString(fuelCursor.getColumnIndex(DBAdapter.BILL_AMOUNT1)));
            this.filledDieselAmount1 = (fuelCursor.getString(fuelCursor.getColumnIndex(DBAdapter.FILLED_DIESEL_AMOUNT1)));

            this.billNo2 = (fuelCursor.getString(fuelCursor.getColumnIndex(DBAdapter.BILL_NO2)));
            this.billAmount2 = (fuelCursor.getString(fuelCursor.getColumnIndex(DBAdapter.BILL_AMOUNT2)));
            this.filledDieselAmount2 = (fuelCursor.getString(fuelCursor.getColumnIndex(DBAdapter.FILLED_DIESEL_AMOUNT2)));

            this.tractorLeased = (fuelCursor.getString(fuelCursor.getColumnIndex(DBAdapter.TRACTOR_LEASED)));
            this.costPerDay = (fuelCursor.getString(fuelCursor.getColumnIndex(DBAdapter.COST_PER_DAY)));
            this.remark = (fuelCursor.getString(fuelCursor.getColumnIndex(DBAdapter.REMARK)));
            this.createdDateTime = (fuelCursor.getString(fuelCursor.getColumnIndex(DBAdapter.CREATED_DATE_TIME)));
            this.sendingStatus = (fuelCursor.getString(fuelCursor.getColumnIndex(DBAdapter.SENDING_STATUS)));
            this.machineId = (fuelCursor.getString(fuelCursor.getColumnIndex(DBAdapter.MACHINE_ID)));
        }
        fuelCursor.close();
    }


    protected FuelData(Parcel in) {
        id = in.readString();
        fuelId = in.readString();
        fuelDate = in.readString();
        morningFuelLevel = in.readString();
        eveningFuelLevel = in.readString();
        consumedFuel = in.readString();
        dieselFilled = in.readString();
        billNo = in.readString();
        billAmount = in.readString();
        filledDieselAmount = in.readString();
        tractorLeased = in.readString();
        costPerDay = in.readString();
        remark = in.readString();
        createdDateTime = in.readString();
        sendingStatus = in.readString();
        machineId = in.readString();
        billNo1 = in.readString();
        billAmount1 = in.readString();
        filledDieselAmount1 = in.readString();

        billNo2 = in.readString();
        billAmount2 = in.readString();
        filledDieselAmount2 = in.readString();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(fuelId);
        dest.writeString(fuelDate);
        dest.writeString(morningFuelLevel);
        dest.writeString(eveningFuelLevel);
        dest.writeString(consumedFuel);
        dest.writeString(dieselFilled);
        dest.writeString(billNo);
        dest.writeString(billAmount);
        dest.writeString(filledDieselAmount);
        dest.writeString(tractorLeased);
        dest.writeString(costPerDay);
        dest.writeString(remark);
        dest.writeString(createdDateTime);
        dest.writeString(sendingStatus);
        dest.writeString(machineId);

        dest.writeString(billNo1);
        dest.writeString(billAmount1);
        dest.writeString(filledDieselAmount1);

        dest.writeString(billNo2);
        dest.writeString(billAmount2);
        dest.writeString(filledDieselAmount2);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FuelData> CREATOR = new Creator<FuelData>() {
        @Override
        public FuelData createFromParcel(Parcel in) {
            return new FuelData(in);
        }

        @Override
        public FuelData[] newArray(int size) {
            return new FuelData[size];
        }
    };

    public boolean save(DBAdapter db) {

        boolean isSaved = false;

        Cursor cursor = db.getFuelByDate(this.getFuelDate());
        if (cursor.moveToFirst()) {
            if (cursor.getString(cursor.getColumnIndex(DBAdapter.SENDING_STATUS)).equals(DBAdapter.SENT)) {
                return false;
            }
            isSaved = true;
        }

        ContentValues values = new ContentValues();
        values.put(DBAdapter.FUEL_ID, this.getFuelId());
        values.put(DBAdapter.FUEL_DATE, this.getFuelDate());
        values.put(DBAdapter.MORNING_FUEL_LEVEL, this.getMorningFuelLevel());
        values.put(DBAdapter.EVENING_FUEL_LEVEL, this.getEveningFuelLevel());
        values.put(DBAdapter.CONSUMED_FUEL, this.getConsumedFuel());
        values.put(DBAdapter.DIESEL_FILLED, this.getDieselFilled());
        values.put(DBAdapter.BILL_NO, this.getBillNo());
        values.put(DBAdapter.BILL_AMOUNT, this.getBillAmount());
        values.put(DBAdapter.FILLED_DIESEL_AMOUNT, this.getFilledDieselAmount());

        values.put(DBAdapter.BILL_NO1, this.getBillNo1());
        values.put(DBAdapter.BILL_AMOUNT1, this.getBillAmount1());
        values.put(DBAdapter.FILLED_DIESEL_AMOUNT1, this.getFilledDieselAmount1());

        values.put(DBAdapter.BILL_NO2, this.getBillNo2());
        values.put(DBAdapter.BILL_AMOUNT2, this.getBillAmount2());
        values.put(DBAdapter.FILLED_DIESEL_AMOUNT2, this.getFilledDieselAmount2());

        values.put(DBAdapter.TRACTOR_LEASED, this.getTractorLeased());
        values.put(DBAdapter.COST_PER_DAY, this.getCostPerDay());
        values.put(DBAdapter.REMARK, this.getRemark());
        values.put(DBAdapter.CREATED_DATE_TIME, this.getCreatedDateTime());
        values.put(DBAdapter.SENDING_STATUS, this.getSendingStatus());
        values.put(DBAdapter.MACHINE_ID, this.getMachineId());

        long k = 0;

        if (isSaved) {
            k = db.db.update(DBAdapter.TABLE_FUEL_MANAGER, values, DBAdapter.FUEL_DATE + "='" + this.getFuelDate() + "'", null);
        } else {
            k = db.db.insert(DBAdapter.TABLE_FUEL_MANAGER, null, values);
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

            em = document.createElement("morningFuelLevel");
            if (this.getMorningFuelLevel() != null) {
                em.appendChild(document.createTextNode(this.getMorningFuelLevel()));
                rootElement.appendChild(em);
            }

            em = document.createElement("eveningFuelLevel");
            if (this.getEveningFuelLevel() != null) {
                em.appendChild(document.createTextNode(this.getEveningFuelLevel()));
                rootElement.appendChild(em);
            }

            em = document.createElement("consumedFuel");
            if (this.getConsumedFuel() != null) {
                em.appendChild(document.createTextNode(this.getConsumedFuel()));
                rootElement.appendChild(em);
            }

            em = document.createElement("dieselFilled");
            if (this.getDieselFilled() != null) {
                em.appendChild(document.createTextNode(this.getDieselFilled()));
                rootElement.appendChild(em);
            }

            em = document.createElement("billNo");
            if (this.getBillNo() != null) {
                em.appendChild(document.createTextNode(this.getBillNo()));
                rootElement.appendChild(em);
            }

            em = document.createElement("billAmount");
            if (this.getBillAmount() != null) {
                em.appendChild(document.createTextNode(this.getBillAmount()));
                rootElement.appendChild(em);
            }


            em = document.createElement("filledDieselAmount");
            if (this.getFilledDieselAmount() != null) {
                em.appendChild(document.createTextNode(this.getFilledDieselAmount()));
                rootElement.appendChild(em);
            }


            em = document.createElement("billNo1");
            if (this.getBillNo1() != null) {
                em.appendChild(document.createTextNode(this.getBillNo1()));
                rootElement.appendChild(em);
            }

            em = document.createElement("billAmount1");
            if (this.getBillAmount1() != null) {
                em.appendChild(document.createTextNode(this.getBillAmount1()));
                rootElement.appendChild(em);
            }


            em = document.createElement("filledDieselAmount1");
            if (this.getFilledDieselAmount1() != null) {
                em.appendChild(document.createTextNode(this.getFilledDieselAmount1()));
                rootElement.appendChild(em);
            }

            em = document.createElement("billNo2");
            if (this.getBillNo2() != null) {
                em.appendChild(document.createTextNode(this.getBillNo2()));
                rootElement.appendChild(em);
            }

            em = document.createElement("billAmount2");
            if (this.getBillAmount2() != null) {
                em.appendChild(document.createTextNode(this.getBillAmount2()));
                rootElement.appendChild(em);
            }


            em = document.createElement("filledDieselAmount2");
            if (this.getFilledDieselAmount2() != null) {
                em.appendChild(document.createTextNode(this.getFilledDieselAmount2()));
                rootElement.appendChild(em);
            }


            em = document.createElement("isTractorLeased");
            if (this.getTractorLeased() != null) {
                em.appendChild(document.createTextNode(this.getTractorLeased()));
                rootElement.appendChild(em);
            }

            em = document.createElement("costPerDay");
            if (this.getCostPerDay() != null) {
                em.appendChild(document.createTextNode(this.getCostPerDay()));
                rootElement.appendChild(em);
            }

            em = document.createElement("Remark");
            if (this.getRemark() != null) {
                em.appendChild(document.createTextNode(this.getRemark()));
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

        map.put("FOCode", credential.getFoCode());
        if (this.getFuelId() != null) {
            map.put("FuelID", this.getFuelId());
        } else {
            map.put("FuelID", "");
        }

        if (this.getFuelDate() != null) {
            map.put("FuelDate", this.getFuelDate());
        } else {
            map.put("FuelDate", "");
        }

        if (this.getMachineId() != null && (!this.getMachineId().trim().equals("-1"))) {
            map.put("MachineCode", this.getMachineId());
        }/* else {
            map.put("MachineCode", "");
        }*/

        if (this.getMorningFuelLevel() != null) {
            map.put("MorningFuelReading", this.getMorningFuelLevel());
        } else {
            map.put("MorningFuelReading", "");
        }

        if (this.getEveningFuelLevel() != null) {
            map.put("EveningFuelReading", this.getEveningFuelLevel());
        } else {
            map.put("EveningFuelReading", "");
        }
        if (this.getConsumedFuel() != null) {
            map.put("FuelConsumed", this.getConsumedFuel());
        } else {
            map.put("FuelConsumed", "");
        }
        if (this.getDieselFilled() != null) {
            map.put("FuelFilled", this.getDieselFilled());
        } else {
            map.put("FuelFilled", "");
        }
        if (this.getBillNo() != null) {
            map.put("BillNumber", this.getBillNo());
        } else {
            map.put("BillNumber", "");
        }
        if (this.getBillAmount() != null) {
            map.put("BillAmount", this.getBillAmount());
        } else {
            map.put("BillAmount", "");
        }
        if (this.getFilledDieselAmount() != null) {
            map.put("FuelLiter", this.getFilledDieselAmount());
        } else {
            map.put("FuelLiter", "");
        }


        if (this.getBillNo1() != null) {
            map.put("BillNumber1", this.getBillNo1());
        }
        if (this.getBillAmount1() != null) {
            map.put("BillAmount1", this.getBillAmount1());
        }
        if (this.getFilledDieselAmount1() != null) {
            map.put("FuelLiter1", this.getFilledDieselAmount1());
        }

        if (this.getBillNo2() != null) {
            map.put("BillNumber2", this.getBillNo2());
        }
        if (this.getBillAmount2() != null) {
            map.put("BillAmount2", this.getBillAmount2());
        }
        if (this.getFilledDieselAmount2() != null) {
            map.put("FuelLiter2", this.getFilledDieselAmount2());
        }

        if (this.getTractorLeased() != null) {
            map.put("IsTractorLeased", this.getTractorLeased());
        } else {
            map.put("IsTractorLeased", "");
        }
        if (this.getCostPerDay() != null) {
            map.put("LeasingCostPerDay", this.getCostPerDay());
        } else {
            map.put("LeasingCostPerDay", "");
        }
        if (this.getRemark() != null) {
            map.put("Remarks", this.getRemark());
        } else {
            map.put("Remarks", "");
        }
//        map.put("created_date_time",this.getCreatedDateTime());

        return map;
    }


    public String sendFuelDetail(DBAdapter db) {
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        String methodOnServer = "fuelDetails";
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


    public ArrayList<ContentData> getListContentData() {
        ArrayList<ContentData> data = new ArrayList<>();
        data.add(getContentData("Fuel Detail Id", this.getFuelId()));
        data.add(getContentData("Fuel Date", this.getFuelDate()));
        data.add(getContentData("Machine Code", this.getMachineId()));
        data.add(getContentData("Morning Fuel Level", this.getMorningFuelLevel()));
        data.add(getContentData("Evening Fuel Level", this.getEveningFuelLevel()));

        data.add(getContentData("Consumed Fuel", this.getConsumedFuel()));
        if (this.getDieselFilled().equals("1")) {
            data.add(getContentData("Diesel Filled", "Yes"));
            data.add(getContentData("Bill No.", this.getBillNo()));
            data.add(getContentData("Bill Amount", this.getBillAmount()));
            data.add(getContentData("Filled Diesel Amount", this.getFilledDieselAmount()));

            if(this.getBillNo1()!=null && this.getBillNo1().trim().length()>0){
                data.add(getContentData("Bill No.1", this.getBillNo1()));
                data.add(getContentData("Bill Amount1", this.getBillAmount1()));
                data.add(getContentData("Filled Diesel Amount1", this.getFilledDieselAmount1()));
            }


            if(this.getBillNo2()!=null && this.getBillNo2().trim().length()>0){
                data.add(getContentData("Bill No.2", this.getBillNo2()));
                data.add(getContentData("Bill Amount2", this.getBillAmount2()));
                data.add(getContentData("Filled Diesel Amount2", this.getFilledDieselAmount2()));
            }


        } else {
            data.add(getContentData("Diesel Filled", "No"));
        }

        if (this.getTractorLeased().equals("1")) {
            data.add(getContentData("Is Tractor Leased", "Yes"));
            data.add(getContentData("Cost Per Day", this.getCostPerDay()));
        } else {
            data.add(getContentData("Is Tractor Leased", "No"));
        }
        data.add(getContentData("Remark", this.getRemark()));
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

    public String getBillNo1() {
        return billNo1;
    }

    public void setBillNo1(String billNo1) {
        this.billNo1 = billNo1;
    }

    public String getBillAmount1() {
        return billAmount1;
    }

    public void setBillAmount1(String billAmount1) {
        this.billAmount1 = billAmount1;
    }

    public String getFilledDieselAmount1() {
        return filledDieselAmount1;
    }

    public void setFilledDieselAmount1(String filledDieselAmount1) {
        this.filledDieselAmount1 = filledDieselAmount1;
    }

    public String getBillNo2() {
        return billNo2;
    }

    public void setBillNo2(String billNo2) {
        this.billNo2 = billNo2;
    }

    public String getBillAmount2() {
        return billAmount2;
    }

    public void setBillAmount2(String billAmount2) {
        this.billAmount2 = billAmount2;
    }

    public String getFilledDieselAmount2() {
        return filledDieselAmount2;
    }

    public void setFilledDieselAmount2(String filledDieselAmount2) {
        this.filledDieselAmount2 = filledDieselAmount2;
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

    public void setFuelDate(String fuelDate) {
        this.fuelDate = fuelDate;
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

    public String getMorningFuelLevel() {
        if (morningFuelLevel != null) {
            return morningFuelLevel;
        } else {
            return "";
        }
    }

    public void setMorningFuelLevel(String morningFuelLevel) {
        this.morningFuelLevel = morningFuelLevel;
    }

    public String getEveningFuelLevel() {
        if (eveningFuelLevel != null) {
            return eveningFuelLevel;
        } else {
            return "";
        }
    }

    public void setEveningFuelLevel(String eveningFuelLevel) {
        this.eveningFuelLevel = eveningFuelLevel;
    }

    public String getConsumedFuel() {
        if (consumedFuel != null) {
            return consumedFuel;
        } else {
            return "";
        }
    }

    public void setConsumedFuel(String consumedFuel) {
        this.consumedFuel = consumedFuel;
    }

    public String getDieselFilled() {
        if (dieselFilled != null) {
            return dieselFilled;
        } else {
            return "";
        }
    }

    public void setDieselFilled(String dieselFilled) {
        this.dieselFilled = dieselFilled;
    }

    public String getBillNo() {
        if (billNo != null) {
            return billNo;
        } else {
            return "";
        }
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getBillAmount() {
        if (billAmount != null) {
            return billAmount;
        } else {
            return "";
        }
    }

    public void setBillAmount(String billAmount) {
        this.billAmount = billAmount;
    }

    public String getFilledDieselAmount() {
        if (filledDieselAmount != null) {
            return filledDieselAmount;
        } else {
            return "";
        }
    }

    public void setFilledDieselAmount(String filledDieselAmount) {
        this.filledDieselAmount = filledDieselAmount;
    }

    public String getTractorLeased() {
        if (tractorLeased != null) {
            return tractorLeased;
        } else {
            return "";
        }
    }

    public void setTractorLeased(String tractorLeased) {
        this.tractorLeased = tractorLeased;
    }

    public String getCostPerDay() {
        if (costPerDay != null) {
            return costPerDay;
        } else {
            return "";
        }
    }

    public void setCostPerDay(String costPerDay) {
        this.costPerDay = costPerDay;
    }

    public String getRemark() {
        if (remark != null) {
            return remark;
        } else {
            return "";
        }
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public String getSendingStatus() {
        if (sendingStatus != null) {
            return sendingStatus;
        } else {
            return "";
        }
    }

    public void setSendingStatus(String sendingStatus) {
        this.sendingStatus = sendingStatus;
    }
}
