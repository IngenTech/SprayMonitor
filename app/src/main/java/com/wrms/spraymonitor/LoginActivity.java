package com.wrms.spraymonitor;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wrms.spraymonitor.app.AppController;
import com.wrms.spraymonitor.app.Constents;
import com.wrms.spraymonitor.app.DBAdapter;
import com.wrms.spraymonitor.app.Synchronize;
import com.wrms.spraymonitor.background.Lat_Lon_CellID;
import com.wrms.spraymonitor.background.LocationPollReceiver;
import com.wrms.spraymonitor.background.PollReceiver;
import com.wrms.spraymonitor.dataobject.Credential;
import com.wrms.spraymonitor.utils.ConnectionDetector;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {


    //    private static final String TAG = "TranscoderActivity";
    private static final int REQUEST_CODE_PICK = 1;
    private static final int SCAN_BARCODE = 2;

//    private Toolbar toolbar;

    EditText username;
    EditText password;
    TextView version;
    Button login;

    DBAdapter db;
    ProgressDialog pDialog;

    Credential credential;
    private View mLayout;
    public static final String TAG = "permission tag";
    private static final int REQUEST_GPS = 1;
    private static final int REQUEST_PHONE_STATE = 2;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 3;

    private static final int PERMISSION_REQUEST_CODE = 1;
    String p1 = Manifest.permission.WRITE_EXTERNAL_STORAGE,
            p2 = android.Manifest.permission.ACCESS_FINE_LOCATION,
            p3 = Manifest.permission.READ_PHONE_STATE,
            p4 = Manifest.permission.CAMERA,
            p5 = Manifest.permission.ACCESS_COARSE_LOCATION;

    CheckBox remeberCheck;
    String remeberType = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mLayout = findViewById(R.id.loginLayout);

        credential = new Credential();
        permissionAccess();

       /* getWriteExternalStorage();
        getReadPhoneState();
        getGPSLocation();
*/

        final SharedPreferences sharedpreferences = getSharedPreferences("LoginSession", Context.MODE_PRIVATE);
        String str = sharedpreferences.getString("remember", null);

        remeberCheck = (CheckBox) findViewById(R.id.check_password_remeber);

        remeberCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {

                    remeberType = "1";
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("remember", remeberType);
                    editor.commit();
                } else {
                    remeberCheck.setChecked(false);
                    remeberType = "0";
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("remember", remeberType);
                    editor.commit();
                }
            }
        });


        if (str != null && str.length() > 0) {
            if (str.equalsIgnoreCase("1")) {
                remeberCheck.setChecked(true);
                remeberType = "1";
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("remember", remeberType);
                editor.commit();
            } else {
                remeberCheck.setChecked(false);
                remeberType = "0";
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("remember", remeberType);
                editor.commit();
            }
        }


        version = (TextView) findViewById(R.id.version);
        version.setText("Ver : " + BuildConfig.VERSION_NAME);

        /*SharedPreferences shared = getSharedPreferences("sync_frequency", MODE_PRIVATE);
        String channel = (shared.getString("sync_frequency", ""));
        System.out.println("channel : " + channel);

        SharedPreferences videoTransmission = getSharedPreferences("video_transmission",MODE_PRIVATE);
        System.out.println("video Transmission : "+videoTransmission.getBoolean("video_transmission",false));*/

        SharedPreferences myPreference = PreferenceManager.getDefaultSharedPreferences(this);
        String syncFrequency = myPreference.getString("sync_frequency", "15");
        int frequency = Integer.valueOf(syncFrequency) * 60000;
        boolean videoTransmission = myPreference.getBoolean("video_transmission", false);
        System.out.println("frequency : " + frequency + " videoTransmission : " + videoTransmission);

        PollReceiver.scheduleAlarms(this, frequency);

        db = new DBAdapter(LoginActivity.this);
        db.open();

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            NOGPSDialog(this);
        }


        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);

       /* if (!db.getCredentials()) {
            ContentValues values = new ContentValues();
            values.put(DBAdapter.USERID, "sarita");
            values.put(DBAdapter.USER_NAME, "sarita");
            values.put(DBAdapter.PASSWORD, "1234");
            values.put(DBAdapter.CREATED_DATE_TIME, "2015-10-23 16:27:55");
            db.db.insert(DBAdapter.TABLE_CREDENTIAL, null, values);
        }*/

        login.setOnClickListener(loginListner);

        Cursor credentialCursor = db.getCredential();
        if (credentialCursor.moveToLast()) {
            String userName = credentialCursor.getString(credentialCursor.getColumnIndex(DBAdapter.USER_NAME));
            username.setText(userName);

            // SharedPreferences sharedpreferences = getSharedPreferences("LoginSession", Context.MODE_PRIVATE);
            String str1 = sharedpreferences.getString("remember", null);

            if (str1 != null) {

                if (str1.equalsIgnoreCase("1")) {
                    String pswd = credentialCursor.getString(credentialCursor.getColumnIndex(DBAdapter.PASSWORD));

                    Log.v("mdldl", pswd);

                    password.setText(pswd);
                } else {

                    password.setText("");
                }
            }
        }
        credentialCursor.close();


    }

    private void permissionAccess() {


        if (!checkPermission(p1)) {
            Log.e("TAG", p1);
            requestPermission(p1);
        } else if (!checkPermission(p2)) {
            Log.e("TAG", p2);
            requestPermission(p2);
            try {
                startService(new Intent(LoginActivity.this, Lat_Lon_CellID.class));
                LocationPollReceiver.scheduleAlarms(LoginActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        else if (!checkPermission(p3)) {
            Log.e("TAG", p3);
            requestPermission(p3);
            try {
                TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
                credential.setImei(tm.getDeviceId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (!checkPermission(p4)) {
            Log.e("TAG", p4);
            requestPermission(p4);
        } else {
            // Toast.makeText(getApplicationContext(), "All permission granted", Toast.LENGTH_LONG).show();
            try {
                TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
                credential.setImei(tm.getDeviceId());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                startService(new Intent(LoginActivity.this, Lat_Lon_CellID.class));
                LocationPollReceiver.scheduleAlarms(LoginActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }


    }


    private boolean checkPermission(String permission) {

        int result = ContextCompat.checkSelfPermission(this, permission);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission(String permission) {

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{permission}, PERMISSION_REQUEST_CODE);
        } else {
            //Do the stuff that requires permission...
            Log.e("TAG", "Not say request");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                Log.e("TAG", "val " + grantResults[0]);
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionAccess();
                } else {
                    Toast.makeText(getApplicationContext(), "Bye bye", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

   /* public void getWriteExternalStorage() {
        Log.i(TAG, "Write External storage. Checking permission.");
        // BEGIN_INCLUDE(camera_permission)
        // Check if the Camera permission is already available.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Camera permission has not been granted.

            requestWriteExternalStoragePermission();

        } else {

            // Camera permissions is already available, show the camera preview.
            Log.i(TAG,
                    "Location permission has already been granted. Displaying camera preview.");

        }


    }


    private void requestWriteExternalStoragePermission() {
        Log.i(TAG, "WRITE_EXTERNAL_STORAGE has NOT been granted. Requesting permission.");

        // BEGIN_INCLUDE(Location_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Log.i(TAG,
                    "Displaying fine WRITE_EXTERNAL_STORAGE permission rationale to provide additional context.");
            Snackbar.make(mLayout, R.string.permission_write_external_storage,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(LoginActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    REQUEST_WRITE_EXTERNAL_STORAGE);
                        }
                    })
                    .show();
        } else {

            // Location permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE);
        }
        // END_INCLUDE(Location_permission_request)
    }


    public void getReadPhoneState() {
        Log.i(TAG, "Read phone state action. Checking permission.");
        // BEGIN_INCLUDE(camera_permission)
        // Check if the Camera permission is already available.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // Camera permission has not been granted.

            requestReadPhoneStatePermission();

        } else {

            // Camera permissions is already available, show the camera preview.
            Log.i(TAG,
                    "Location permission has already been granted. Displaying camera preview.");
            try {
                TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
                credential.setImei(tm.getDeviceId());
            } catch (Exception e) {
                e.printStackTrace();
            }

//            showCameraPreview();
        }
        // END_INCLUDE(camera_permission)

    }


    private void requestReadPhoneStatePermission() {
        Log.i(TAG, "READ_PHONE_STATE has NOT been granted. Requesting permission.");

        // BEGIN_INCLUDE(Location_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_PHONE_STATE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Log.i(TAG,
                    "Displaying fine location permission rationale to provide additional context.");
            Snackbar.make(mLayout, R.string.permission_read_phone_state_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(LoginActivity.this,
                                    new String[]{Manifest.permission.READ_PHONE_STATE},
                                    REQUEST_PHONE_STATE);
                        }
                    })
                    .show();
            try {
                TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
                credential.setImei(tm.getDeviceId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

            // Location permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    REQUEST_PHONE_STATE);
        }
        // END_INCLUDE(Location_permission_request)
    }


    public void getGPSLocation() {
        Log.i(TAG, "Show camera button pressed. Checking permission.");
        // BEGIN_INCLUDE(camera_permission)
        // Check if the Camera permission is already available.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Camera permission has not been granted.

            requestLocationPermission();

        } else {

            // Camera permissions is already available, show the camera preview.
            Log.i(TAG,
                    "Location permission has already been granted. Displaying camera preview.");
//            startService(new Intent(LoginActivity.this, Lat_Lon_CellID.class));
            LocationPollReceiver.scheduleAlarms(LoginActivity.this);

        }


    }


    private void requestLocationPermission() {
        Log.i(TAG, "Location permission has NOT been granted. Requesting permission.");

        // BEGIN_INCLUDE(Location_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Log.i(TAG,
                    "Displaying fine location permission rationale to provide additional context.");
            Snackbar.make(mLayout, R.string.permission_Location_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(LoginActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_GPS);
                        }
                    })
                    .show();
//            startService(new Intent(LoginActivity.this, Lat_Lon_CellID.class));
            LocationPollReceiver.scheduleAlarms(LoginActivity.this);
        } else {

            // Location permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_GPS);
        }
        // END_INCLUDE(Location_permission_request)
    }

    */

    /**
     * Callback received when a permissions request has been completed.
     *//*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_GPS) {
            // BEGIN_INCLUDE(permission_result)
            // Received permission result for camera permission.
            Log.i(TAG, "Received response for GPS permission request.");

            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission has been granted, preview can be displayed
                Log.i(TAG, "GPS permission has now been granted. Showing preview.");
                Snackbar.make(mLayout, R.string.permission_available_gps,
                        Snackbar.LENGTH_SHORT).show();
            } else {
                Log.i(TAG, "GPS permission was NOT granted.");
                Snackbar.make(mLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT).show();

            }
            // END_INCLUDE(permission_result)

        } else if (requestCode == REQUEST_PHONE_STATE) {

            // BEGIN_INCLUDE(permission_result)
            // Received permission result for camera permission.
            Log.i(TAG, "Received response for READ_PHONE_STATE permission request.");

            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission has been granted, preview can be displayed
                Log.i(TAG, "GPS permission has now been granted. Showing preview.");
                Snackbar.make(mLayout, R.string.permission_available_gps,
                        Snackbar.LENGTH_SHORT).show();
            } else {
                Log.i(TAG, "GPS permission was NOT granted.");
                Snackbar.make(mLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT).show();

            }

        } else if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {

            Log.i(TAG, "Received response for WRITE_EXTERNAL_STORAGE permission request.");


            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission has been granted, preview can be displayed
                Log.i(TAG, "GPS permission has now been granted. Showing preview.");
                Snackbar.make(mLayout, R.string.permission_available_gps,
                        Snackbar.LENGTH_SHORT).show();
            } else {
                Log.i(TAG, "GPS permission was NOT granted.");
                Snackbar.make(mLayout, R.string.permissions_not_granted, Snackbar.LENGTH_SHORT).show();

            }
            // END_INCLUDE(permission_result)


        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
*/
    private boolean isValid() {
        boolean isValid = true;
        String userName = username.getText().toString();
        if (userName != null && userName.length() > 0) {
            credential.setUserName(userName);
        } else {
            Toast.makeText(LoginActivity.this, "Please Enter UserName", Toast.LENGTH_LONG).show();
            return false;
        }

        String userPass = password.getText().toString();
        if (userPass != null && userPass.length() > 0) {
            credential.setPassword(userPass);
        } else {
            Toast.makeText(LoginActivity.this, "Please Enter Password", Toast.LENGTH_LONG).show();
            return false;
        }

        Date date = new Date();
        String createdDateTime = Constents.sdf.format(date);

        credential.setLastUpdated(createdDateTime);

        return isValid;
    }


    private void moveAfterLogin() {
//        Intent intent = new Intent(LoginActivity.this, DetailActivity.class);
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
        startActivity(intent);
    }

    private void checkLogin() {
        // creating connection detector class instance
        ConnectionDetector cd = new ConnectionDetector(this);
        Boolean isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            requestLogin();
        }
    }


    public Button.OnClickListener loginListner = new Button.OnClickListener() {
        public void onClick(View v) {

            if (isValid()) {


//                    checkLogin();
                if (isNetworkAvailable()) {
//                    sendLoginRequest();
                    requestLogin();
                } else {
                    boolean isAuthenticated = db.isAuthenticated(credential.getUserName(), credential.getPassword());
                    if (isAuthenticated) {
                        moveAfterLogin();
                    } else {
                        Toast.makeText(LoginActivity.this, "No Internet Access", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    };

    ProgressDialog dialog;

    private void requestLogin() {
        String methodeName = "login";
        // making fresh volley request and getting json
        StringRequest loginRequest = new StringRequest(Request.Method.POST, Synchronize.URL + methodeName,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String registrationResponse) {
                        System.out.println("registrationResponse : " + registrationResponse);
                        dialog.dismiss();
                        if (registrationResponse != null && registrationResponse.length() > 0) {
                            try {
                                JSONObject jObject = new JSONObject(registrationResponse);

                                ContentValues contentValues = null;
                                contentValues = new ContentValues();
                                System.out.println("status : " + jObject.getString("status").equals("success"));
                                if (jObject.getString("status").equals("success")) {
                                    System.out.println("jObject.getString(\"user_id\") : " + jObject.getString("user_id"));

                                    db.db.delete(DBAdapter.TABLE_CREDENTIAL, null, null);

                                    String foCode = jObject.getString("fo_code");
                                    String foName = jObject.getString("fo_name");
                                    String mobileNo = jObject.getString("mobile_no");
                                    contentValues.put(DBAdapter.USER_NAME, credential.getUserName());
                                    contentValues.put(DBAdapter.USERID, jObject.getString("user_id"));
                                    contentValues.put(DBAdapter.PASSWORD, credential.getPassword());
                                    contentValues.put(DBAdapter.CREATED_DATE_TIME, credential.getLastUpdated());
                                    contentValues.put(DBAdapter.IMEI, credential.getImei());
                                    contentValues.put(DBAdapter.FO_CODE, foCode);
                                    contentValues.put(DBAdapter.FO_NAME, foName);
                                    contentValues.put(DBAdapter.MOBILE_NO, mobileNo);

                                    long k = db.db.insert(DBAdapter.TABLE_CREDENTIAL, null, contentValues);

                                    if (k != -1) {
                                        moveAfterLogin();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Could not insert login detail in Database", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(LoginActivity.this, "User with \n USERNAME " + credential.getUserName() + "\n PASSWORD " + credential.getPassword() + "\n doesn't exist on server", Toast.LENGTH_LONG).show();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(LoginActivity.this, "Data Pattern is Wrong For Login", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Do not get any response for USERNAME " + credential.getUserName(), Toast.LENGTH_LONG).show();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                dialog.cancel();
                Toast.makeText(LoginActivity.this, "Not able to connect with server", Toast.LENGTH_LONG).show();
                boolean isAuthenticated = db.isAuthenticated(credential.getUserName(), credential.getPassword());
                if (isAuthenticated) {
                    moveAfterLogin();
                } else {
                    Toast.makeText(LoginActivity.this, "No Internet Access", Toast.LENGTH_LONG).show();
                }
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", credential.getUserName());

                try {
                    byte[] bytesOfMessage = credential.getPassword().getBytes("UTF-8");
                    MessageDigest md = MessageDigest.getInstance("MD5");
                    byte[] thedigest = md.digest(bytesOfMessage);
                    BigInteger bigInt = new BigInteger(1, thedigest);
                    String hashtext = bigInt.toString(16);
                    // Now we need to zero pad it if you actually want the full 32 chars.
                    while (hashtext.length() < 32) {
                        hashtext = "0" + hashtext;
                    }

                    params.put("password", hashtext);
                    Log.v("pass", hashtext);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                params.put("imei", credential.getImei());

                Log.v("nkjfhfshd", credential.getUserName());

                Log.v("imei", credential.getImei());

                return params;
            }

        };
        dialog = ProgressDialog.show(LoginActivity.this, "Login",
                "Please wait...", true);

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(loginRequest);
    }


    public Button.OnClickListener mScan = new Button.OnClickListener() {
        public void onClick(View v) {
//            Intent intent = new Intent(LoginActivity.this,CaptureActivity.class);
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
            startActivityForResult(intent, SCAN_BARCODE);
        }
    };





   /* static  int synchronizeCount = 0;
    String syncResult = "";
    String syncedElement = "";
    private void synchronize(final String[] syncName){

        final int arrayCount = syncName.length;
        synchronizeCount = 0;
        syncResult = "";
        syncedElement = "";

        class Background extends AsyncTask<Void,Void,String>{

            //            ProgressDialog pDialog  = new ProgressDialog(HomeActivity.this);
            String syncString;

            Background(String syncString){
                this.syncString = syncString;
            }

            @Override
            protected void onPreExecute() {
               *//* pDialog.setMessage("Synchronize for "+syncString+".....");
                pDialog.show();*//*
                pDialog =  ProgressDialog.show(LoginActivity.this, "",
                        "Synchronize for " + syncString + ".....", true);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = Synchronize.syncFor(syncString, db,LoginActivity.this);
                if((!Synchronize.isConnectedToServer)|| (!response.contains("Success"))){
                    syncResult = syncResult+"Could not sync for "+syncString+"\n";
                }else{
                    syncedElement = syncedElement+","+syncString;
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                if(pDialog!=null) {
                    if (pDialog.isShowing()) {
                        pDialog.dismiss();
                    }
                }
                synchronizeCount++;
                if(synchronizeCount<arrayCount){
                    new Background(syncName[synchronizeCount]).execute();
                }else{
                    String[] syncedArray = syncedElement.split(",");

                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle("Syncing Status");
                    if(syncResult.trim().length()>0){
                        builder.setMessage(syncResult);
                    }else if(syncedArray.length != Synchronize.syncList.length){
                        builder.setMessage("Could Not Synchronize. Please Sync again");
                    }else{
                        builder.setMessage("Synchronize Successfully");
                    }
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
            }

        }

        new Background(syncName[synchronizeCount]).execute();
    }*/

    public void NOGPSDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("GPS OFF");
        builder.setMessage("Please Enable GPS &\nlaunch again")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        LoginActivity.this.finish();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        LoginActivity.this.finish();
                        Intent intent1 = new Intent(Intent.ACTION_MAIN);
                        intent1.addCategory(Intent.CATEGORY_HOME);
                        startActivity(intent1);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View view = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (view instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            if (event.getAction() == MotionEvent.ACTION_UP
                    && (x < w.getLeft() || x >= w.getRight()
                    || y < w.getTop() || y > w.getBottom())) {

                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // TODO: handle exception
                }

            }
        }
        return ret;
    }
}
