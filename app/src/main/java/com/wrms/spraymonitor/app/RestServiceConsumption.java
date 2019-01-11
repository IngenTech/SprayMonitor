package com.wrms.spraymonitor.app;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.wrms.spraymonitor.dataobject.Credential;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by WRMS on 10-11-2015.
 */
public class RestServiceConsumption {

    //    public static final SimpleDateFormat APP_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
    public static final SimpleDateFormat APP_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    public static final String APP_DIRECTORY = "SprayMonitoring";

    public static final String LOW = "Low";
    public static final String HIGH = "High";

    public static final String SITE_IMAGE = "SiteImage";
    public static final String ID_IMAGE = "IdImage";
    public static final String CARETAKER_IMAGE = "CareTakerImage";
    public static final String OTHER_IMAGE = "OtherImage";

    //    public final static String URL = "http://111.118.177.61:99/Service.svc";
    public final static String URL = "http://111/Service.svc";
    public static final String NAMESPACE ="http://tempuri.org/";
    public static final String SOAP_ACTION_PREFIX = "IService/";

    public static final int TimeOut=640000;

    public static final String MACHINE_ID_SYNC = "MachineID";
    public static final String STATE_SYNC = "State";
    public static final String TERRITORY_SYNC = "Territory";
    public static final String DISTRICT_SYNC = "District";
    public static final String TEHSIL_SYNC = "Tehsil";
    public static final String CROP_SYNC = "Crop";
    public static final String PRODUCT_SYNC = "Product";
    public static final String UOM_SYNC = "UOM";


    public static final String[] syncList = new String[]{
            MACHINE_ID_SYNC,
            STATE_SYNC,
            TERRITORY_SYNC,
            DISTRICT_SYNC,
            TEHSIL_SYNC,
            CROP_SYNC,
            PRODUCT_SYNC,
            UOM_SYNC
    };

    public static boolean isConnectedToServer;

    private void requestMachineId(final DBAdapter db,Credential credential,Activity context){

        //Toast.makeText(getActivity(),url,Toast.LENGTH_LONG).show();
        JSONObject loginInfo = new JSONObject();
        try {
            loginInfo.put("user_id", credential.getUserId());

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("Request JSON : " + loginInfo.toString());
        // making fresh volley request and getting json
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST, Synchronize.URL, loginInfo, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    try {
                        if (response.has("machines_ids")) {
                            SQLiteDatabase SqliteDB = db.getSQLiteDatabase();
                            SqliteDB.beginTransaction();
                            JSONArray machineIds = response.getJSONArray("machines_ids");
                            try {
                                if (machineIds.length() > 0) {
                                    db.db.execSQL("delete from " + DBAdapter.TABLE_MACHINE_ID);
                                    String query = "INSERT INTO " + DBAdapter.TABLE_MACHINE_ID + "(" + DBAdapter.MACHINE_ID + "," + DBAdapter.MACHINE +"," + DBAdapter.CROP_TYPE + ") VALUES (?,?,?)";

                                    SQLiteStatement stmt = SqliteDB.compileStatement(query);
                                    for (int i = 0; i < machineIds.length(); i++) {
                                        stmt.bindString(1, machineIds.getString(i));
                                        stmt.bindString(2, machineIds.getString(i));
                                        stmt.execute();
                                    }
                                }
                                SqliteDB.setTransactionSuccessful();
                                SqliteDB.endTransaction();
                            }catch (Exception e){
                                e.printStackTrace();
                                SqliteDB.setTransactionSuccessful();
                                SqliteDB.endTransaction();
                            }
                        }
                    }catch (JSONException es) {
                        es.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("MachineId", "Error: " + error.getMessage());
            }
        }) {


            /** Passing some request headers **/
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
//                headers.put("ApiKey", Const.AuthenticationKey);
                return headers;
            }
        };

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);
    }

    public static String syncFor(String syncFor,DBAdapter db,Activity context){

        String insertingResult = "";
        Credential credential;
        Cursor credentialCursor = db.getCredential();
        if(credentialCursor.getCount()>0){
            credentialCursor.moveToFirst();
            credential = new Credential(credentialCursor);
        }

        HashMap<String, String> params = new HashMap<String,String>();

        if(syncFor.equals(MACHINE_ID_SYNC)){

        }

        if(syncFor.equals(STATE_SYNC)){

        }

        if(syncFor.equals(TERRITORY_SYNC)){


        }


        if(syncFor.equals(DISTRICT_SYNC)){


        }

        if(syncFor.equals(TEHSIL_SYNC)){

        }

        if(syncFor.equals(CROP_SYNC)){

        }

        if(syncFor.equals(PRODUCT_SYNC)){

        }

        if(syncFor.equals(UOM_SYNC)){

        }


        return insertingResult;
    }
}
