package com.wrms.spraymonitor.app;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.wrms.spraymonitor.dataobject.Credential;
import com.wrms.spraymonitor.dataobject.FarmerData;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by WRMS on 08-05-2015.
 */


public class Synchronize {

    //    public static final SimpleDateFormat APP_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
    public static final SimpleDateFormat APP_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    //    public final static String URL = "http://111.118.177.61:99/Service.svc";
//    public final static String URL = "http://54.172.21.127/spm/services/";//was working
    public final static String URL = "http://ecodrivesolution.co.in/services/";

    public final static String IMAGE_UPLOAD_API = URL+"image";
    public final static String VIDEO_UPLOAD_API = URL+"video";
    public final static String SPRAY_FORM_UPLOAD_API = URL+"form";
    public final static String PAYMENT_UPLOAD_API = URL+"payments";
    public final static String FARMER_UPLOAD_API = URL+"farmerRegistration";

    public static final String NAMESPACE = "http://tempuri.org/";
    public static final String SOAP_ACTION_PREFIX = "IService/";

    public static final int TimeOut = 640000;

    public static final String MACHINE_ID_SYNC = "MachineID";
    public static final String STATE_SYNC = "State";
    public static final String TERRITORY_SYNC = "Territory";
    public static final String DISTRICT_SYNC = "District";
    public static final String TEHSIL_SYNC = "Tehsil";
    public static final String CROP_SYNC = "Crop";
    public static final String PRODUCT_SYNC = "Product";
    public static final String PRODUCT_CROP_ASSIGNMENT = "Product Crop Assignment";
    public static final String UOM_SYNC = "UOM";
    public static final String FIELD_COLLECTOR = "Collector";
    public static final String FARMER = "Farmer";
    public static final String VILLAGE = "Village";
    public static final String RATE = "Rate";


    public static final String[] syncList = new String[]{
            MACHINE_ID_SYNC,
            STATE_SYNC,
            TERRITORY_SYNC,
            DISTRICT_SYNC,
            TEHSIL_SYNC,
            CROP_SYNC,
            PRODUCT_SYNC,
            UOM_SYNC,
            FIELD_COLLECTOR,
            FARMER,
            VILLAGE,
            RATE
    };

    public static boolean isConnectedToServer;

    public static String syncFor(String syncFor, DBAdapter db, Activity context) {

        String insertingResult = "";

        Credential credential = new Credential();
        Cursor credentialCursor = db.getCredential();
        if (credentialCursor.getCount() > 0) {
            credentialCursor.moveToFirst();
            credential = new Credential(credentialCursor);
        }
        credentialCursor.close();

        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

        if (syncFor.equals(MACHINE_ID_SYNC)) {
            String methodOnServer = "machines";
            params.add(new BasicNameValuePair("user_id", credential.getUserId()));
            String response = requestForRESTPost(params, methodOnServer);

            System.out.println("Machine ID Response : " + response);
           /* response = "[\n" +
                    "    {\"id\":\"1\", \"machine_id\":\"00001\"},\n" +
                    "    {\"id\":\"2\", \"machine_id\":\"00002\"},\n" +
                    "    {\"id\":\"3\",\"machine_id\":\"00003\"}\n" +
                    "]";*/
            insertingResult = insertMachineId(db, response);
        }

        if (syncFor.equals(STATE_SYNC)) {
            String methodOnServer = "locality";
            params.add(new BasicNameValuePair("user_id", credential.getUserId()));
            params.add(new BasicNameValuePair("locality", "state"));
            String response = requestForRESTPost(params, methodOnServer);
            System.out.println("State Response : " + response);
            insertingResult = insertState(db, response);
        }


        if (syncFor.equals(STATE_SYNC)) {
            String methodOnServer = "cropRates";

            params = new ArrayList<NameValuePair>();

            String response = requestForRESTPost(params, methodOnServer);
            System.out.println("State Response : " + response);
            insertingResult = insertRate(db, response);
        }

        /*if(syncFor.equals(TERRITORY_SYNC)){
            String methodOnServer = "locality";
            params.add(new BasicNameValuePair("user_id", credential.getUserId()));
            params.add(new BasicNameValuePair("locality", "teritory"));
            String response = requestForRESTPost(params, methodOnServer);
            System.out.println("Territory Response : "+response);
//            response = " {\"status\":\"success\",\"teritoryList\":[[\"1\",\"Territory1\",\"1\"],[\"2\",\"Territory2\",\"1\"]]}";
            insertingResult = insertTerritory(db, response);

        }*/


        if (syncFor.equals(DISTRICT_SYNC)) {
            String methodOnServer = "locality";

            params.add(new BasicNameValuePair("user_id", credential.getUserId()));
            params.add(new BasicNameValuePair("locality", "district"));
            String response = requestForRESTPost(params, methodOnServer);
            System.out.println("District Response : " + response);
//            response = " {\"status\":\"success\",\"districtList\":[[\"1\",\"District1\",\"1\"],[\"2\",\"District2\",\"1\"]]}";
            insertingResult = insertDistrict(db, response);

        }

        if (syncFor.equals(TEHSIL_SYNC)) {
            String methodOnServer = "locality";
            params.add(new BasicNameValuePair("user_id", credential.getUserId()));
            params.add(new BasicNameValuePair("locality", "tehsil"));
            String response = requestForRESTPost(params, methodOnServer);
            System.out.println("Tehsil Response : " + response);
//            response = " {\"status\":\"success\",\"tehsilList\":[[\"1\",\"Tehsil1\",\"1\"],[\"2\",\"Tehsil2\",\"1\"]]}";
            insertingResult = insertTehsil(db, response);
        }

        if (syncFor.equals(VILLAGE)) {
            String methodOnServer = "villages";
            params.add(new BasicNameValuePair("user_id", credential.getUserId()));
//            params.add(new BasicNameValuePair("locality", "village"));
            String response = requestForRESTPost(params, methodOnServer);
            System.out.println("Village Response : " + response);
//            response = " {\"status\":\"success\",\"villageList\":[[\"1\",\"Village1\",\"1\"],[\"2\",\"Village2\",\"1\"]]}";
            insertingResult = insertVillage(db, response);
        }

        if (syncFor.equals(CROP_SYNC)) {
            String methodOnServer = "crop";
            params.add(new BasicNameValuePair("user_id", credential.getUserId()));
            String response = requestForRESTGet(methodOnServer);
            System.out.println("Crop Response : " + response);
            insertingResult = insertCrop(db, response);
        }

        if (syncFor.equals(PRODUCT_SYNC)) {
            String methodOnServer = "products";
            params.add(new BasicNameValuePair("user_id", credential.getUserId()));
            String response = requestForRESTPost(params, methodOnServer);
            System.out.println("Product Response : " + response);
            insertingResult = insertProduct(db, response);
        }

        if (syncFor.equals(PRODUCT_CROP_ASSIGNMENT)) {
            String methodOnServer = "productCropAssignments";
            params.add(new BasicNameValuePair("user_id", credential.getUserId()));
            String response = requestForRESTPost(params, methodOnServer);
            System.out.println("Product Crop Assignment Response : " + response);
            insertingResult = insertProductCropAssignment(db, response);
        }

        if (syncFor.equals(UOM_SYNC)) {
            String methodOnServer = "uom";
            params.add(new BasicNameValuePair("user_id", credential.getUserId()));
            String response = requestForRESTGet(methodOnServer);
//            response = "{\"status\":\"success\",\"uoms\":{\"1\":\"KG\",\"2\":\"Liter\"}}";
            System.out.println("Product Response : " + response);
            insertingResult = insertUOM(db, response);
        }

        if (syncFor.equals(FIELD_COLLECTOR)) {
            /*String methodOnServer = "getFieldCollector";
            params.add(new BasicNameValuePair("user_id", credential.getUserId()));
            String response = requestForRESTPost(params, methodOnServer);
            System.out.println("Product Response : "+response);*/
            String response = "{\"status\":\"success\",\"fieldCollector\":{\"1\":\"Farmer\",\"2\":\"FO\",\"3\":\"TM\",\"4\":\"Distributor\",\"5\":\"GOV/UNI\",\"6\":\"DEMO PURPOSE\"}}";
            insertingResult = insertFieldCollector(db, response);
        }


        if (syncFor.equals(FARMER)) {

            Cursor getSavedFarmer = db.getSavedFarmer();
            ArrayList<FarmerData> arrayList = new ArrayList<>();
            if (getSavedFarmer.getCount() > 0) {
                getSavedFarmer.moveToFirst();

                do {
                    String farmerId = getSavedFarmer.getString(getSavedFarmer.getColumnIndex(DBAdapter.FARMER_ID));
                    FarmerData farmerData = new FarmerData(farmerId, db);
                    arrayList.add(farmerData);
                } while (getSavedFarmer.moveToNext());

            }
            getSavedFarmer.close();
            if(arrayList.size()>0)
            FarmerData.registerRequest(arrayList,db,0);

            Cursor getSubmittedFarmer = db.getSubmittedFarmer();
            if (getSubmittedFarmer.getCount() > 0) {
                getSubmittedFarmer.moveToFirst();

                do {
                    String farmerId = getSubmittedFarmer.getString(getSubmittedFarmer.getColumnIndex(DBAdapter.FARMER_ID));
                    FarmerData farmerData = new FarmerData(farmerId, db);
                    String response = farmerData.sendRegistrationConfirmation(db);
                } while (getSubmittedFarmer.moveToNext());

            }
            getSubmittedFarmer.close();


            String methodOnServer = "farmers";
            params.add(new BasicNameValuePair("user_id", credential.getUserId()));
            String response = requestForRESTPost(params, methodOnServer);
            /*response ="{\"status\":\"success\",\"farmer\":[{\"farmerId\":\"1\",\"farmerName\":" +
                    "\"Ram\",\"contactNo\":\"9795990090\",\"stateId\":\"1\",\"territoryId\":\"1\"," +
                    "\"districtId\":\"1\",\"tehsilId\":\"1\",\"village\":\"Rawatpur Gaon\",\"cropId\":\"1\",\"totalAcre\":\"12\"}]}";*/
            System.out.println("Farmer Response : " + response);
            insertingResult = insertFarmer(db, response);
        }

        return insertingResult;
    }

    public static String insertMachineId(DBAdapter db, String resp) {
        System.out.println("in to insertMachineId ");
        String status = "";
        SQLiteDatabase SqliteDB = db.getSQLiteDatabase();
        SqliteDB.beginTransaction();

        try {
            JSONObject jsonObject = new JSONObject(resp);
            if (jsonObject.has("status")) {
                if (jsonObject.get("status").equals("success")) {
                    if (jsonObject.has("MachineList")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("MachineList");
                        if (jsonArray.length() > 0) {
                            db.db.execSQL("delete from " + DBAdapter.TABLE_MACHINE_ID);
                        }

                        String query = "INSERT INTO " + DBAdapter.TABLE_MACHINE_ID + "(" + DBAdapter.MACHINE_ID + "," + DBAdapter.MACHINE +"," + DBAdapter.CROP_TYPE + ") VALUES (?,?,?)";
                        SQLiteStatement stmt = SqliteDB.compileStatement(query);

                        System.out.println("jsonArray.length() : "+jsonArray.length());

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject machineList = jsonArray.getJSONObject(i);


                            stmt.bindString(1, machineList.getString("MachineCode"));
                            stmt.bindString(2, machineList.getString("MachineCode"));
                            stmt.bindString(3, machineList.getString("CropType"));
                            stmt.execute();
                        }
                        status = "Success";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            status = "Wrong server response for Village";
        }
        SqliteDB.setTransactionSuccessful();
        SqliteDB.endTransaction();
        return status;





    }

    public static String insertState(DBAdapter db, String resp) {
        String status = "";
        SQLiteDatabase SqliteDB = db.getSQLiteDatabase();
        SqliteDB.beginTransaction();

        try {

            JSONObject jsonObject = new JSONObject(resp);
            if (jsonObject.has("status")) {
                if (jsonObject.get("status").equals("success")) {
                    if (jsonObject.has("StateList")) {
                        JSONObject stateJsonObject = jsonObject.getJSONObject("StateList");
                        Iterator<?> keys = stateJsonObject.keys();
                        if (keys.hasNext()) {
                            db.db.execSQL("delete from " + DBAdapter.TABLE_STATE);
                        }

                        String query = "INSERT INTO " + DBAdapter.TABLE_STATE + "(" + DBAdapter.STATE_ID + "," + DBAdapter.STATE + ") VALUES (?,?)";

                        SQLiteStatement stmt = SqliteDB.compileStatement(query);
                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            System.out.println("State key " + key);
                            stmt.bindString(1, key);
                            stmt.bindString(2, stateJsonObject.getString(key));
                            try {
                                stmt.execute();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        status = "Success";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            status = "Wrong server response for machine id";
        }
        SqliteDB.setTransactionSuccessful();
        SqliteDB.endTransaction();

        return status;

    }

    public static String insertRate(DBAdapter db, String resp) {
        String status = "";
        SQLiteDatabase SqliteDB = db.getSQLiteDatabase();
        SqliteDB.beginTransaction();

        try {

            JSONObject jsonObject = new JSONObject(resp);
            if (jsonObject.has("status")) {
                if (jsonObject.get("status").equals("success")) {
                    if (jsonObject.has("RateList")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("RateList");
                        if (jsonArray.length() > 0) {
                            db.db.execSQL("delete from " + DBAdapter.RATE_LIST);
                        }

                        String query = "INSERT INTO " + DBAdapter.RATE_LIST + "(" + DBAdapter.DISTRICT_CODE + "," + DBAdapter.RATE +  "," + DBAdapter.CROP_TYPE +") VALUES (?,?,?)";

                        SQLiteStatement stmt = SqliteDB.compileStatement(query);
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject territoryElementArray = jsonArray.getJSONObject(i);

                            stmt.bindString(1, territoryElementArray.getString("DistrictID"));
                            stmt.bindString(2, territoryElementArray.getString("Rate"));
                            stmt.bindString(3, territoryElementArray.getString("CropType"));
                            stmt.execute();
                        }
                        status = "Success";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            status = "Wrong server response for Rate";
        }
        SqliteDB.setTransactionSuccessful();
        SqliteDB.endTransaction();

        return status;

    }

/*    public static String insertTerritory(DBAdapter db,String resp){
        String status = "";
        SQLiteDatabase SqliteDB = db.getSQLiteDatabase();
        SqliteDB.beginTransaction();
        try {
            JSONObject jsonObject = new JSONObject(resp);
            if(jsonObject.has("status")) {
                if (jsonObject.get("status").equals("success")) {
                    if(jsonObject.has("teritoryList")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("teritoryList");
                        if(jsonArray.length()>0){
                            db.db.execSQL("delete from "+ DBAdapter.TABLE_TERITORY);
                        }

                        String query = "INSERT INTO "+DBAdapter.TABLE_TERITORY+"("+DBAdapter.TERRITORY_ID+","+DBAdapter.TERRITORY+","+DBAdapter.STATE_ID+") VALUES (?,?,?)";
                        SQLiteStatement stmt = SqliteDB.compileStatement(query);

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONArray territoryElementArray = jsonArray.getJSONArray(i);

                            stmt.bindString(1, territoryElementArray.get(0).toString());
                            stmt.bindString(2, territoryElementArray.get(1).toString());
                            stmt.bindString(3, territoryElementArray.get(2).toString());
                            stmt.execute();
                        }
                        status = "Success";
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            status = "Wrong server response for trritory";
        }
        SqliteDB.setTransactionSuccessful();
        SqliteDB.endTransaction();
        return status;
    }*/

    public static String insertDistrict(DBAdapter db, String resp) {
        String status = "";
        SQLiteDatabase SqliteDB = db.getSQLiteDatabase();
        SqliteDB.beginTransaction();
        try {
            JSONObject jsonObject = new JSONObject(resp);
            if (jsonObject.has("status")) {
                if (jsonObject.get("status").equals("success")) {
                    if (jsonObject.has("DistrictList")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("DistrictList");
                        if (jsonArray.length() > 0) {
                            db.db.execSQL("delete from " + DBAdapter.TABLE_DISTRICT);
                        }

                        String query = "INSERT INTO " + DBAdapter.TABLE_DISTRICT + "(" + DBAdapter.DISTRICT_ID + "," + DBAdapter.DISTRICT +"," + DBAdapter.CROP_TYPE + "," + DBAdapter.STATE_ID + ") VALUES (?,?,?,?)";
                        SQLiteStatement stmt = SqliteDB.compileStatement(query);

                        for (int i = 0; i < jsonArray.length(); i++) {

                           // JSONArray territoryElementArray = jsonArray.getJSONArray(i);

                            JSONArray cropArray = jsonArray.getJSONObject(i).getJSONArray("CropType");
                            for (int a = 0 ;a<cropArray.length();a++){

                                String cropT = cropArray.getJSONObject(a).getString("cropTypeCode");

                                stmt.bindString(1, jsonArray.getJSONObject(i).getString("0"));
                                stmt.bindString(2, jsonArray.getJSONObject(i).getString("1"));
                                stmt.bindString(4, jsonArray.getJSONObject(i).getString("2"));
                                stmt.bindString(3, cropT);
                                stmt.execute();

                            }


                        }
                        status = "Success";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            status = "Wrong server response for District";
        }
        SqliteDB.setTransactionSuccessful();
        SqliteDB.endTransaction();
        return status;
    }

    public static String insertTehsil(DBAdapter db, String resp) {
        String status = "";
        SQLiteDatabase SqliteDB = db.getSQLiteDatabase();
        SqliteDB.beginTransaction();

        try {
            JSONObject jsonObject = new JSONObject(resp);
            if (jsonObject.has("status")) {
                if (jsonObject.get("status").equals("success")) {
                    if (jsonObject.has("TehsilList")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("TehsilList");
                        if (jsonArray.length() > 0) {
                            db.db.execSQL("delete from " + DBAdapter.TABLE_TEHSIL);
                        }

                        String query = "INSERT INTO " + DBAdapter.TABLE_TEHSIL + "(" + DBAdapter.TEHSIL_ID + "," + DBAdapter.TEHSIL + "," + DBAdapter.DISTRICT_ID + ") VALUES (?,?,?)";
                        SQLiteStatement stmt = SqliteDB.compileStatement(query);

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONArray territoryElementArray = jsonArray.getJSONArray(i);

                            stmt.bindString(1, territoryElementArray.get(0).toString());
                            stmt.bindString(2, territoryElementArray.get(1).toString());
                            stmt.bindString(3, territoryElementArray.get(2).toString());
                            stmt.execute();
                        }
                        status = "Success";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            status = "Wrong server response for Tehsil";
        }
        SqliteDB.setTransactionSuccessful();
        SqliteDB.endTransaction();
        return status;

    }

    public static String insertVillage(DBAdapter db, String resp) {
        String status = "";
        SQLiteDatabase SqliteDB = db.getSQLiteDatabase();
        SqliteDB.beginTransaction();

        try {
            JSONObject jsonObject = new JSONObject(resp);
            if (jsonObject.has("status")) {
                if (jsonObject.get("status").equals("success")) {
                    if (jsonObject.has("VillageList")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("VillageList");
                        if (jsonArray.length() > 0) {
                            db.db.execSQL("delete from " + DBAdapter.TABLE_VILLAGE);
                        }

                        String query = "INSERT INTO " + DBAdapter.TABLE_VILLAGE + "(" + DBAdapter.VILLAGE_ID + "," + DBAdapter.VILLAGE + "," + DBAdapter.TEHSIL_ID + ") VALUES (?,?,?)";
                        SQLiteStatement stmt = SqliteDB.compileStatement(query);

                        System.out.println("jsonArray.length() : "+jsonArray.length());

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject villageElementObj = jsonArray.getJSONObject(i);

                            /*System.out.println("VillageId : "+villageElementObj.getString("VillageId")+" VillageName : "+villageElementObj.getString("VillageName")+
                            " TehsilId : "+villageElementObj.getString("TehsilId"));*/

                            stmt.bindString(1, villageElementObj.getString("VillageId"));
                            stmt.bindString(2, villageElementObj.getString("VillageName"));
                            stmt.bindString(3, villageElementObj.getString("TehsilId"));
                            stmt.execute();
                        }
                        status = "Success";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            status = "Wrong server response for Village";
        }
        SqliteDB.setTransactionSuccessful();
        SqliteDB.endTransaction();
        return status;

    }

   /* public static String insertCrop(DBAdapter db, String resp) {
        String status = "";
        SQLiteDatabase SqliteDB = db.getSQLiteDatabase();
        SqliteDB.beginTransaction();
        try {

            JSONObject jsonObject = new JSONObject(resp);
            if (jsonObject.has("status")) {
                if (jsonObject.get("status").equals("success")) {
                    if (jsonObject.has("crops")) {
                        JSONObject cropJsonObject = jsonObject.getJSONObject("crops");
                        Iterator<?> keys = cropJsonObject.keys();
                        if (keys.hasNext()) {
                            db.db.execSQL("delete from " + DBAdapter.TABLE_CROP);
                        }

                        String query = "INSERT INTO " + DBAdapter.TABLE_CROP + "(" + DBAdapter.CROP_ID + "," + DBAdapter.CROP + "," + DBAdapter.CROP_TYPE +") VALUES (?,?,?)";

                        SQLiteStatement stmt = SqliteDB.compileStatement(query);
                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            stmt.bindString(1, key);
                            stmt.bindString(2, cropJsonObject.getString(key));
                            stmt.execute();

                        }
                        status = "Success";
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            status = "Wrong server response for crop";
        }
        SqliteDB.setTransactionSuccessful();
        SqliteDB.endTransaction();
        return status;

    }*/


    public static String insertCrop(DBAdapter db, String resp) {
        String status = "";
        SQLiteDatabase SqliteDB = db.getSQLiteDatabase();
        SqliteDB.beginTransaction();
        try {
            JSONObject jsonObject = new JSONObject(resp);
            if (jsonObject.has("status")) {
                if (jsonObject.get("status").equals("success")) {
                    if (jsonObject.has("CropList")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("CropList");
                        if (jsonArray.length() > 0) {
                            db.db.execSQL("delete from " + DBAdapter.TABLE_CROP);
                        }

                        String query = "INSERT INTO " + DBAdapter.TABLE_CROP + "(" + DBAdapter.CROP_ID + "," + DBAdapter.CROP + "," + DBAdapter.CROP_TYPE +") VALUES (?,?,?)";
                        SQLiteStatement stmt = SqliteDB.compileStatement(query);

                        System.out.println("jsonArray.length() : "+jsonArray.length());

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject cropOBJ = jsonArray.getJSONObject(i);

                            stmt.bindString(1, cropOBJ.getString("CropID"));
                            stmt.bindString(2, cropOBJ.getString("CropName"));
                            stmt.bindString(3, cropOBJ.getString("CropType"));
                            stmt.execute();
                        }
                        status = "Success";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            status = "Wrong server response for Crop";
        }
        SqliteDB.setTransactionSuccessful();
        SqliteDB.endTransaction();
        return status;

    }

    public static String insertProduct(DBAdapter db, String resp) {
        String status = "";
        SQLiteDatabase SqliteDB = db.getSQLiteDatabase();
        SqliteDB.beginTransaction();

        try {
            JSONObject jsonObject = new JSONObject(resp);

            if (jsonObject.has("status")) {
                if (jsonObject.get("status").equals("success")) {
                    if (jsonObject.has("products")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("products");
                        if (jsonArray.length() > 0) {
                            db.db.execSQL("delete from " + DBAdapter.TABLE_PRODUCT);
                        }

                        String query = "INSERT INTO " + DBAdapter.TABLE_PRODUCT + "(" + DBAdapter.PRODUCT_ID + "," + DBAdapter.PRODUCT + "," + DBAdapter.MEASURING_UNIT_ID + ") VALUES (?,?,?)";
                        SQLiteStatement stmt = SqliteDB.compileStatement(query);

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONArray productElementArray = jsonArray.getJSONArray(i);
                            stmt.bindString(1, productElementArray.get(1).toString());
                            stmt.bindString(2, productElementArray.get(0).toString());
                            stmt.bindString(3, productElementArray.get(2).toString());
                            stmt.execute();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            status = "Wrong server response for product";
        }
        SqliteDB.setTransactionSuccessful();
        SqliteDB.endTransaction();
        return status;
    }

    public static String insertProductCropAssignment(DBAdapter db, String resp) {
        String status = "";
        SQLiteDatabase SqliteDB = db.getSQLiteDatabase();
        SqliteDB.beginTransaction();

        try {
            JSONObject jsonObject = new JSONObject(resp);

            if (jsonObject.has("status")) {
                if (jsonObject.get("status").equals("success")) {
                    if (jsonObject.has("ProductCropList")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("ProductCropList");
                        if (jsonArray.length() > 0) {
                            db.db.execSQL("delete from " + DBAdapter.TABLE_PRODUCT_CROP_ASSIGNMENT);
                        }

                        String query = "INSERT INTO " + DBAdapter.TABLE_PRODUCT_CROP_ASSIGNMENT + "(" + DBAdapter.PRODUCT_ID + "," + DBAdapter.CROP_ID + ") VALUES (?,?)";
                        SQLiteStatement stmt = SqliteDB.compileStatement(query);

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject productJson = jsonArray.getJSONObject(i);
                            String productId = productJson.getString("ProductId");
                            JSONArray cropIdArray = productJson.getJSONArray("CropIds");
                            for (int j = 0; j < cropIdArray.length(); j++) {
                                stmt.bindString(1, productId);
                                stmt.bindString(2, cropIdArray.get(j).toString());
                                stmt.execute();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            status = "Wrong server response for product crop assignment";
        }
        SqliteDB.setTransactionSuccessful();
        SqliteDB.endTransaction();
        return status;
    }


    public static String insertUOM(DBAdapter db, String resp) {
        String status = "";
        SQLiteDatabase SqliteDB = db.getSQLiteDatabase();
        SqliteDB.beginTransaction();
        try {

            JSONObject jsonObject = new JSONObject(resp);
            if (jsonObject.has("status")) {
                if (jsonObject.get("status").equals("success")) {
                    if (jsonObject.has("uoms")) {
                        JSONObject cropJsonObject = jsonObject.getJSONObject("uoms");
                        Iterator<?> keys = cropJsonObject.keys();
                        if (keys.hasNext()) {
                            db.db.execSQL("delete from " + DBAdapter.TABLE_MEASURING_UNIT);
                        }

                        String query = "INSERT INTO " + DBAdapter.TABLE_MEASURING_UNIT + "(" + DBAdapter.MEASURING_UNIT_ID + "," + DBAdapter.MEASURING_UNIT + "," + DBAdapter.MEASURING_UNIT_TYPE + ") VALUES (?,?,?)";

                        SQLiteStatement stmt = SqliteDB.compileStatement(query);
                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            stmt.bindString(1, key);
                            stmt.bindString(2, cropJsonObject.getString(key));
                            stmt.bindString(3, "1");
                            stmt.execute();

                        }
                        status = "Success";
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            status = "Wrong server response for crop";
        }
        SqliteDB.setTransactionSuccessful();
        SqliteDB.endTransaction();
        return status;

    }

    public static String insertFieldCollector(DBAdapter db, String resp) {
        String status = "";
        SQLiteDatabase SqliteDB = db.getSQLiteDatabase();
        SqliteDB.beginTransaction();

        try {

            JSONObject jsonObject = new JSONObject(resp);
            if (jsonObject.has("status")) {
                if (jsonObject.get("status").equals("success")) {
                    if (jsonObject.has("fieldCollector")) {
                        JSONObject cropJsonObject = jsonObject.getJSONObject("fieldCollector");
                        Iterator<?> keys = cropJsonObject.keys();
                        if (keys.hasNext()) {
                            db.db.execSQL("delete from " + DBAdapter.TABLE_FIELD_COLLECTOR);
                        }

                        String query = "INSERT INTO " + DBAdapter.TABLE_FIELD_COLLECTOR + "(" + DBAdapter.FIELD_COLLECTOR_ID + "," + DBAdapter.FIELD_COLLECTOR + ") VALUES (?,?)";

                        SQLiteStatement stmt = SqliteDB.compileStatement(query);
                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            stmt.bindString(1, key);
                            stmt.bindString(2, cropJsonObject.getString(key));
                            stmt.execute();

                        }
                        status = "Success";
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            status = "Wrong server response for crop";
        }
        SqliteDB.setTransactionSuccessful();
        SqliteDB.endTransaction();
        return status;
    }

    public static String insertFarmer(DBAdapter db, String resp) {
        String status = "";
        SQLiteDatabase SqliteDB = db.getSQLiteDatabase();
        SqliteDB.beginTransaction();
        try {

            JSONObject jsonObject = new JSONObject(resp);
            if (jsonObject.has("status")) {
                if (jsonObject.get("status").equals("success")) {
                    if (jsonObject.has("FarmerList")) {
                        JSONArray farmerArray = jsonObject.getJSONArray("FarmerList");
                        if (farmerArray.length() > 0) {
//                            db.db.execSQL("delete from "+ DBAdapter.TABLE_FARMER);
                            int deletedRecords = db.db.delete(DBAdapter.TABLE_FARMER, DBAdapter.CREATED_BY + " = '" + DBAdapter.CREATED_BY_SERVER + "'", null);
                            Log.i("Syncronize", "Deleted Record : " + deletedRecords);
                        }
                        String insertQuery = "INSERT INTO " + DBAdapter.TABLE_FARMER + "(" +
                                DBAdapter.FARMER_ID + "," + DBAdapter.FARMER_CONTACT + "," +
                                DBAdapter.FIRST_NAME + "," + DBAdapter.LAST_NAME + "," + DBAdapter.FARMER_CODE + "," +
                                DBAdapter.VILLAGE_ID + "," + DBAdapter.ADDRESS + "," + DBAdapter.SENDING_STATUS + "," +
                                DBAdapter.CREATED_BY + "," + DBAdapter.CREATED_DATE_TIME +
                                ") VALUES (?,?,?,?,?,?,?,?,?,?)";
                        Date date = new Date();
                        String dateString = Constents.sdf.format(date);

                        SQLiteStatement stmt = SqliteDB.compileStatement(insertQuery);
                        for (int i = 0; i < farmerArray.length(); i++) {
                            JSONObject farmerObject = farmerArray.getJSONObject(i);
                            stmt.bindString(1, farmerObject.getString("FarmerId"));
                            stmt.bindString(2, farmerObject.getString("FarmerContact"));
                            stmt.bindString(3, farmerObject.getString("FirstName"));
                            stmt.bindString(4, farmerObject.getString("LastName"));
                            stmt.bindString(5, farmerObject.getString("FarmerCode"));
                            stmt.bindString(6, farmerObject.getString("VillageId"));
                            stmt.bindString(7, farmerObject.getString("FarmerAddress"));
                            stmt.bindString(8, DBAdapter.SENT);
                            stmt.bindString(9, DBAdapter.CREATED_BY_SERVER);
                            stmt.bindString(10, dateString);
                            try {
                                stmt.execute();
                            } catch (Exception e) {
                                Log.e("Syncronize ", "insertion failed for " + farmerObject.getString("FarmerContact") + " , " + farmerObject.getString("FirstName"));
                                e.printStackTrace();

                                String updateQuery = "UPDATE " + DBAdapter.TABLE_FARMER + " SET " +
                                        DBAdapter.FARMER_ID + " = '" + farmerObject.getString("FarmerId") + "' , " +
                                        DBAdapter.FARMER_CONTACT + " = '" + farmerObject.getString("FarmerContact") + "' , " +
                                        DBAdapter.FIRST_NAME + " = '" + farmerObject.getString("FirstName") + "' , " +
                                        DBAdapter.LAST_NAME + " = '" + farmerObject.getString("LastName") + "' , " +
                                        DBAdapter.FARMER_CODE + " = '" + farmerObject.getString("FarmerCode") + "' , " +
                                        DBAdapter.VILLAGE_ID + " = '" + farmerObject.getString("VillageId") + "' , " +
                                        DBAdapter.ADDRESS + " = '" + farmerObject.getString("FarmerAddress") + "' , " +
                                        DBAdapter.SENDING_STATUS + " = '" + DBAdapter.SENT + "' , " +
                                        DBAdapter.CREATED_BY + " = '" + DBAdapter.CREATED_BY_USER + "' , " +
                                        DBAdapter.CREATED_DATE_TIME + " = '" + dateString +
                                        "' WHERE " + DBAdapter.FARMER_CONTACT + " = '" + farmerObject.getString("FarmerContact") + "';";

                                Log.i("Update Query ", updateQuery);
                                SQLiteStatement updateStmt = SqliteDB.compileStatement(updateQuery);
                                try {
                                    updateStmt.execute();
                                } catch (Exception updateE) {
                                    Log.e("Syncronize Farmer ", "Updation failed for " + farmerObject.getString("FarmerContact") + " , " + farmerObject.getString("FirstName"));
                                    updateE.printStackTrace();
                                }
                            }
                        }
                        status = "Success";
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            status = "Wrong server response for Farmer";
        }
        SqliteDB.setTransactionSuccessful();
        SqliteDB.endTransaction();
        return status;

    }


    public static final String SERVER_ERROR_MESSAGE = "Not able to connect with the server";

    public static String requestForRESTPost(ArrayList<NameValuePair> params, String methodeName) {
        isConnectedToServer = true;
        String resp = "";
        try {

            try {
                System.out.println("URL : " + URL + methodeName);
                resp = CustomHttpClient.executeHttpPost(URL + methodeName, params);
                System.out.println("Response Data : " + resp);
            } catch (IOException e) {
                e.printStackTrace();
                isConnectedToServer = false;
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                isConnectedToServer = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp = e.getMessage();
            isConnectedToServer = false;
        }
        return resp;
    }

    public static String requestForRESTGet(String methodeName) {
        isConnectedToServer = true;
        String resp = "";
        try {

            try {
                resp = CustomHttpClient.executeHttpGet(URL + methodeName);
                System.out.println("Response : " + resp);
            } catch (IOException e) {
                e.printStackTrace();
                isConnectedToServer = false;
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                isConnectedToServer = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp = e.getMessage();
            isConnectedToServer = false;
        }
        return resp;
    }

    public static String requestWithSoapObject(String methodName, SoapObject request) {
        isConnectedToServer = true;
        String resp = "";
        try {

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope.dotNet = true;

//            request.addProperty(pair.getKey().toString(),pair.getValue().toString());

            for (int i = 0; i < request.getPropertyCount(); i++) {
                if (i == 2) {
                    continue;
                }
                if (request != null) {
                    System.out.println("(" + i + ") = " + request.getPropertyAsString(i));
                } else {
                    if (request.getPropertyAsString(i) == null) {
                        System.out.println("(" + i + ") request.getPropertyAsString(i)==null = " + null);
                    }
                    System.out.println("(" + i + ") request== " + null);
                }

            }

            envelope.setOutputSoapObject(request);

            HttpTransportSE transport = new HttpTransportSE(URL, TimeOut);
            try {
                System.setProperty("http.keepAlive", "false");
                transport.call(NAMESPACE + SOAP_ACTION_PREFIX + methodName, envelope);
            } catch (IOException e) {
                e.printStackTrace();
                isConnectedToServer = false;
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                isConnectedToServer = false;
            }
            //bodyIn is the body object received with this envelope
            if (envelope.bodyIn != null) {
                System.out.println("RESPONSE : " + ((SoapObject) envelope.bodyIn).toString());
                SoapPrimitive resultSOAP = (SoapPrimitive) ((SoapObject) envelope.bodyIn)
                        .getProperty(0);
                resp = resultSOAP.toString();

            }
        } catch (Exception e) {
            e.printStackTrace();
            resp = e.getMessage();
            isConnectedToServer = false;
        }
        return resp;
    }

    public static String requestWithServer(String methodName, HashMap<String, String> params) {
        isConnectedToServer = true;
        String resp = "";
        try {

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            SoapObject request = new SoapObject(NAMESPACE, methodName);

            Iterator it = params.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                request.addProperty(pair.getKey().toString(), pair.getValue().toString());
                System.out.println(pair.getKey().toString() + " = " + pair.getValue().toString());
                it.remove(); // avoids a ConcurrentModificationException
            }
//            request.addProperty(pair.getKey().toString(),pair.getValue().toString());

            envelope.setOutputSoapObject(request);
            HttpTransportSE transport = new HttpTransportSE(URL, TimeOut);
            try {
                transport.call(NAMESPACE + SOAP_ACTION_PREFIX + methodName, envelope);
            } catch (IOException e) {
                e.printStackTrace();
                isConnectedToServer = false;
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                isConnectedToServer = false;
            }
            //bodyIn is the body object received with this envelope
            if (envelope.bodyIn != null) {
                System.out.println("RESPONSE : " + ((SoapObject) envelope.bodyIn).toString());
                SoapPrimitive resultSOAP = (SoapPrimitive) ((SoapObject) envelope.bodyIn)
                        .getProperty(0);
                resp = resultSOAP.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp = e.getMessage();
            isConnectedToServer = false;
        }
        return resp;
    }
}
