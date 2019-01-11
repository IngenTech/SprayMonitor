package com.wrms.spraymonitor.dataobject;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wrms.spraymonitor.app.AppController;
import com.wrms.spraymonitor.app.Constents;
import com.wrms.spraymonitor.app.DBAdapter;
import com.wrms.spraymonitor.app.Synchronize;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by WRMS on 04-03-2016.
 */
public class FarmerData implements Parcelable, Comparable<FarmerData>{
    
    private String farmerId;
    private String firstName;
    private String lastName;
    private String farmerContact;
    private String stateId;
    private String districtId;
    private String tehsilId;
    private String addressLine;
    private String villageId;
    private String villageName;
    private String sendingStatus;
    private String cropId;
    private String totalAcre;
    private String createdDateTime;
    private String farmerCode;

    public FarmerData() {
        super();
    }

    public FarmerData(Parcel in) {

        String[] data = new String[15];
        in.readStringArray(data);

        this.farmerId = data[0];
        this.firstName = data[1];
        this.farmerContact = data[2];
        this.stateId = data[3];
        this.districtId = data[4];
        this.tehsilId = data[5];
        this.addressLine = data[6];
        this.villageId = data[7];
        this.villageName = data[8];
        this.sendingStatus = data[9];
        this.cropId = data[10];
        this.totalAcre = data[11];
        this.createdDateTime = data[12];
        this.lastName = data[13];
        this.farmerCode = data[14];

    }

    public static final Parcelable.Creator<FarmerData> CREATOR = new Parcelable.Creator<FarmerData>() {

        @Override
        public FarmerData createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            return new FarmerData(source); // using parcelable constructor
        }

        @Override
        public FarmerData[] newArray(int size) {
            // TODO Auto-generated method stub
            return new FarmerData[size];
        }
    };

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                this.farmerId,
                this.firstName,
                this.farmerContact,
                this.stateId,
                this.districtId,
                this.tehsilId,
                this.addressLine,
                this.villageId,
                this.villageName,
                this.sendingStatus,
                this.cropId,
                this.totalAcre,
                this.createdDateTime,
                this.lastName,
                this.farmerCode});
    }

    public FarmerData(String id, DBAdapter db) {
        Cursor cursor = db.getFarmerById(id);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            this.farmerId = cursor.getString(cursor.getColumnIndex(DBAdapter.FARMER_ID));
            this.firstName = cursor.getString(cursor.getColumnIndex(DBAdapter.FIRST_NAME));
            this.lastName = cursor.getString(cursor.getColumnIndex(DBAdapter.LAST_NAME));
            this.farmerContact = cursor.getString(cursor.getColumnIndex(DBAdapter.FARMER_CONTACT));
            this.addressLine = cursor.getString(cursor.getColumnIndex(DBAdapter.ADDRESS));
            this.villageId = cursor.getString(cursor.getColumnIndex(DBAdapter.VILLAGE_ID));

            this.farmerCode = cursor.getString(cursor.getColumnIndex(DBAdapter.FARMER_CODE));

            Cursor tehsilIdCursor = db.getVillageById(this.villageId);
            if(tehsilIdCursor.moveToFirst()){
                this.tehsilId = tehsilIdCursor.getString(tehsilIdCursor.getColumnIndex(DBAdapter.TEHSIL_ID));
                this.villageName = tehsilIdCursor.getString(tehsilIdCursor.getColumnIndex(DBAdapter.VILLAGE));

                Cursor districtIdCursor = db.getTehsilById(this.tehsilId);
                if(districtIdCursor.moveToFirst()){
                    this.districtId = districtIdCursor.getString(districtIdCursor.getColumnIndex(DBAdapter.DISTRICT_ID));
                    Cursor stateIdCursor = db.getDistrictById(this.districtId);
                    if(stateIdCursor.moveToFirst()){
                        this.stateId = stateIdCursor.getString(stateIdCursor.getColumnIndex(DBAdapter.STATE_ID));
                    }
                    stateIdCursor.close();
                }
                districtIdCursor.close();
            }
            tehsilIdCursor.close();

            this.sendingStatus = cursor.getString(cursor.getColumnIndex(DBAdapter.SENDING_STATUS));
            this.cropId = cursor.getString(cursor.getColumnIndex(DBAdapter.CROP_ID));
            this.totalAcre = cursor.getString(cursor.getColumnIndex(DBAdapter.TOTAL_ACRA));
            this.createdDateTime = cursor.getString(cursor.getColumnIndex(DBAdapter.CREATED_DATE_TIME));

        }
        cursor.close();
    }

    @Override
    public int compareTo(FarmerData o) {
        Date date = new Date();
        try{
            date = Constents.sdf.parse(this.getCreatedDateTime());
        }catch (Exception e){
            e.printStackTrace();
        }
        Date objectDate = new Date();
        try{
            objectDate = Constents.sdf.parse(o.getCreatedDateTime());
        }catch (Exception e){
            e.printStackTrace();
        }
        return date.compareTo(objectDate);
    }


    public boolean save(DBAdapter db) {
        boolean isSave = true;
        long isInserted = 0;
        Cursor cursor = db.getFarmerByContact(this.getFarmerContact());
        ContentValues values = new ContentValues();
        if (cursor.getCount() > 0) {
//            TODO need to check it
            values.put(DBAdapter.FARMER_ID, this.getFarmerId());
            values.put(DBAdapter.FARMER_CODE, this.getFarmerCode());
            values.put(DBAdapter.SENDING_STATUS, this.getSendingStatus());
            isInserted = db.db.update(DBAdapter.TABLE_FARMER, values, DBAdapter.FARMER_CONTACT + " = '" + this.getFarmerContact() + "'", null);
        }else {
            values.put(DBAdapter.FARMER_ID, this.getFarmerId());
            values.put(DBAdapter.FIRST_NAME, this.getFirstName());
            values.put(DBAdapter.LAST_NAME, this.getLastName());
            values.put(DBAdapter.FARMER_CONTACT, this.getFarmerContact());
            values.put(DBAdapter.VILLAGE_ID, this.getVillageId());
            values.put(DBAdapter.ADDRESS, this.getAddressLine());
            values.put(DBAdapter.SENDING_STATUS, this.getSendingStatus());
            values.put(DBAdapter.CROP_ID, this.getCropId());
            values.put(DBAdapter.FARMER_CODE, this.getFarmerCode());
            values.put(DBAdapter.CREATED_BY, DBAdapter.CREATED_BY_USER);

            Date date = new Date();
            String dateString  = Constents.sdf.format(date);
            values.put(DBAdapter.CREATED_DATE_TIME,dateString);
            isInserted = db.db.insert(DBAdapter.TABLE_FARMER, null, values);

        }

        if (isInserted == -1) {
            isSave = false;
        }
        return isSave;
    }

    public boolean delete(DBAdapter db){
        boolean isDeleted = false;

        long k = db.db.delete(DBAdapter.TABLE_FARMER,DBAdapter.FARMER_ID+" = '"+this.getFarmerId()+"'",null);
        if(k!=-1){
            isDeleted = true;
        }

        return isDeleted;
    }

    public String getFarmerCode() {
        return farmerCode;
    }

    public void setFarmerCode(String farmerCode) {
        this.farmerCode = farmerCode;
    }

    public String getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(String createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getVillageName() {
        if(villageName!=null && villageName.trim().length()>0){
            return villageName;
        }else {
            return " ";
        }
    }

    public void setVillageName(String villageName) {
        this.villageName = villageName;
    }

    public String getFarmerId() {
        if(farmerId!=null&&farmerId.trim().length()>0){
            return farmerId;
        }
        return "-1";
    }

    public void setFarmerId(String farmerId) {
        this.farmerId = farmerId;
    }


    public String getSendingStatus() {
        return sendingStatus;
    }

    public void setSendingStatus(String sendingStatus) {
        this.sendingStatus = sendingStatus;
    }

    public String getFirstName() {
        if(this.firstName !=null && this.firstName.trim().length()>0){
            return firstName;
        }else{
            return "";
        }
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFarmerContact() {
        if(this.farmerContact!=null && this.farmerContact.trim().length()>0){
            return farmerContact;
        }else{
            return "";
        }
    }

    public void setFarmerContact(String farmerContact) {
        this.farmerContact = farmerContact;
    }

    public String getStateId() {
        if (stateId != null && stateId.length() > 0) {
            return stateId;
        } else {
            return "-1";
        }

    }

    public void setStateId(String state) {
        this.stateId = state;
    }


    public String getDistrictId() {
        if (districtId != null && districtId.length() > 0) {
            return districtId;
        } else {
            return "-1";
        }

    }

    public void setDistrictId(String district) {
        this.districtId = district;
    }

    public String getTehsilId() {
        if (tehsilId != null && tehsilId.length() > 0) {
            return tehsilId;
        } else {
            return "-1";
        }

    }

    public void setTehsilId(String tehsilId) {
        this.tehsilId = tehsilId;
    }

    public String getVillageId() {
        if (villageId != null && villageId.length() > 0) {
            return villageId;
        } else {
            return "-1";
        }
    }

    public void setVillageId(String villageId) {
        this.villageId = villageId;
    }

    public String getCropId() {

        if (cropId != null && cropId.length() > 0) {
            return cropId;
        } else {
            return "-1";
        }
    }

    public void setCropId(String cropId) {
        this.cropId = cropId;
    }

    public String getTotalAcre() {
        if (totalAcre != null && totalAcre.length() > 0) {
            return totalAcre;
        } else {
            return "";
        }
    }

    public void setTotalAcre(String totalAcrage) {
        this.totalAcre = totalAcre;
    }


    public Map<String,String> getParametersInMap(DBAdapter db){
        Map<String,String> map = new HashMap<>();
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        String methodOnServer = "form";
        Credential credential = new Credential();
        Cursor credentialCursor = db.getCredential();
        if (credentialCursor.getCount() > 0) {
            credentialCursor.moveToFirst();
            credential = new Credential(credentialCursor);
        }
        credentialCursor.close();

        if(credential.getUserId() != null) {
            map.put("AccountId", credential.getUserId());
        }
//        map.put("farmer_id", this.getFarmerId());
        if(this.getFirstName() != null) {
            map.put("FirstName", this.getFirstName());
        }
        if(this.getFarmerId() != null) {
            map.put("FarmerCode", this.getFarmerId());
        }
        if(this.getLastName() != null) {
            map.put("LastName", this.getLastName());
        }
        if(this.getFarmerContact()!= null) {
            map.put("FarmerContact", this.getFarmerContact());
        }
        if(this.getAddressLine() != null) {
            map.put("FarmerAddress", this.getAddressLine());
        }

        if(this.getVillageId().equals("-1")){
            map.put("VillageId", "0");
        }else {
            map.put("VillageId", this.getVillageId());
        }
        return map;
    }


    public static boolean registerRequest(final ArrayList<FarmerData> arrayList,final DBAdapter db,final int count) {
        boolean isRegistered = true;
        if(arrayList.size()>count) {
            final FarmerData data = arrayList.get(count);
            String methodeName = "farmerRegistration";

            StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, Synchronize.URL + methodeName,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String registrationResponse) {
                            try {
                                System.out.println("Register Farmer Response : " + registrationResponse);
                                JSONObject jsonObject = new JSONObject(registrationResponse);

                                if (jsonObject.has("status")) {
                                    if (jsonObject.getString("status").equals("success") || jsonObject.getString("status").equals("ContactAlreadyExist")) {

                                        if (jsonObject.has("FarmerId")) {
                                            String farmerId = jsonObject.getString("FarmerId");
                                            data.setFarmerId(farmerId);
                                            data.setSendingStatus(DBAdapter.SENT);
                                            data.save(db);
                                        } else {
                                            data.setSendingStatus(DBAdapter.SUBMIT);
                                            data.save(db);
                                        }
                                        int nextCount = count+1;
                                        registerRequest(arrayList,db,nextCount);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    volleyError.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
//                System.out.println("Get Params has been called");
                    Map<String, String> map = data.getParametersInMap(db);
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        System.out.println(entry.getKey() + " : " + entry.getValue());
                    }
                    return map;
                }
            };

            stringVarietyRequest.setRetryPolicy(new DefaultRetryPolicy(
                    60000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            AppController.getInstance().addToRequestQueue(stringVarietyRequest);
        }else{
            return false;
        }
        return isRegistered;
    }


    public String sendRegistrationConfirmation(DBAdapter db){
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        String methodOnServer = "farmerStatus";
        Credential credential = new Credential();
        Cursor credentialCursor = db.getCredential();
        if (credentialCursor.getCount() > 0) {
            credentialCursor.moveToFirst();
            credential = new Credential(credentialCursor);
        }
        credentialCursor.close();

        params.add(new BasicNameValuePair("AccountId", credential.getUserId()));
        params.add(new BasicNameValuePair("FarmerContact", this.getFarmerContact()));

        String response = Synchronize.requestForRESTPost(params, methodOnServer);
       /* response = "[\n" +
                "    {\"Response\":\"Success\", \"Id\":\"1234\"},\n" +
                "]";*/
//        response = "Success";
        if(Synchronize.isConnectedToServer){

            try {
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.has("status")) {
                    String farmerStatus = jsonObject.getString("status");
                    System.out.println("STATUS : "+farmerStatus.contains("success"));
                    if (farmerStatus.contains("success")) {
                        response = "Success";
                        JSONObject farmerJsonObject = jsonObject.getJSONObject("FarmerDetail");

                        this.setFarmerId(farmerJsonObject.getString("FarmerId"));
                        this.setFarmerContact(farmerJsonObject.getString("FarmerContact"));
                        this.setFirstName(farmerJsonObject.getString("FirstName"));
                        this.setLastName(farmerJsonObject.getString("LastName"));
                        this.setVillageId(farmerJsonObject.getString("VillageId"));
                        this.setAddressLine(farmerJsonObject.getString("FarmerAddress"));
                        this.setSendingStatus(DBAdapter.SENT);
                        System.out.println("Is Saved : " + this.save(db));
                    }
                    if (farmerStatus.contains("fail")){
                        response = "Fail";
                        this.setSendingStatus(DBAdapter.REJECTED);
                        this.save(db);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                response = "Invalid Response";
            }
            return response;
        }else {
            return Synchronize.SERVER_ERROR_MESSAGE;
        }
    }


    public String sendRegistrationRequest(DBAdapter db){
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        String methodOnServer = "farmerRegistration";
        Map<String,String> map = this.getParametersInMap(db);

        for (Map.Entry<String, String> entry : map.entrySet())
        {
            System.out.println(entry.getKey() + " : " + entry.getValue());
            params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        String response = Synchronize.requestForRESTPost(params, methodOnServer);

        if(Synchronize.isConnectedToServer){
            if(response.contains("success")||response.contains("ContactAlreadyExist")){
                response = "Success";
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.has("FarmerId")){
                        String farmerId = jsonObject.getString("FarmerId");
                        this.setFarmerId(farmerId);
                        this.setFarmerCode(farmerId);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                this.setSendingStatus(DBAdapter.SUBMIT);
                this.save(db);
            }else{
                this.setSendingStatus(DBAdapter.SAVE);
                this.save(db);
                response = "Fail";
            }
            return response;
        }else {
            return Synchronize.SERVER_ERROR_MESSAGE;
        }
    }
    
    
}
