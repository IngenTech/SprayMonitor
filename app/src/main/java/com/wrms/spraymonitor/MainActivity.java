package com.wrms.spraymonitor;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wrms.spraymonitor.app.AppController;
import com.wrms.spraymonitor.app.Constents;
import com.wrms.spraymonitor.app.DBAdapter;
import com.wrms.spraymonitor.app.SettingsActivity;
import com.wrms.spraymonitor.app.Synchronize;
import com.wrms.spraymonitor.dataobject.FuelData;
import com.wrms.spraymonitor.fuelmanager.FuelManager;
import com.wrms.spraymonitor.fuelmanager.MachineRunTimeData;
import com.wrms.spraymonitor.utils.AppManager;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    public static final SimpleDateFormat FUEL_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private Toolbar toolbar;
    Button newFarmer;
    Button spray;
    Button myActivity;
    Button fuelManager;
    ProgressDialog pDialog;
    DBAdapter db;
    TextView syncAlert;
    TextView userName;
    /**
     * Root of the layout of this Activity.
     */
    private View mLayout;
    SharedPreferences sharedpreferences;
    int serverAppVersion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLayout = findViewById(R.id.sample_main_layout);

        sharedpreferences = getSharedPreferences("update_apk", Context.MODE_PRIVATE);
        /*toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);                    // Setting toolbar as the ActionBar with setSupportActionBar() call
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);*/

        db = new DBAdapter(MainActivity.this);
        db.open();

        newFarmer = (Button) findViewById(R.id.newFarmer);
        spray = (Button) findViewById(R.id.spray);
        myActivity = (Button) findViewById(R.id.myActivity);
        syncAlert = (TextView) findViewById(R.id.syncAlert);
        userName = (TextView) findViewById(R.id.userName);
        fuelManager = (Button) findViewById(R.id.fuelManager);

        Cursor getFarmerCursor = db.getFarmerList();
        if (!(getFarmerCursor.getCount() > 0)) {
            if (isNetworkAvailable()) {
                synchronize(Synchronize.syncList);
            } else {
                Toast.makeText(MainActivity.this, "No Internet access", Toast.LENGTH_LONG).show();
            }
        } else {
            newFarmer.setEnabled(true);
            spray.setEnabled(true);
        }
        getFarmerCursor.close();

        getSyncAlert();

/*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

        newFarmer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterFarmerActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
            }
        });

        spray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SprayDetailOneActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);

            }
        });

        myActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PerformanceActivity.class);
                startActivity(intent);
            }
        });

        fuelManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FuelManager.class);
                Date date = new Date();
                String dateString = FUEL_DATE_TIME_FORMAT.format(date);
                System.out.println("date String : " + dateString);


                Cursor fuelCursor_run = db.getRunFuelByDate(dateString);
                System.out.println("run count: " + fuelCursor_run.getCount());
                if (fuelCursor_run.moveToFirst()) {
                    String fuelId = fuelCursor_run.getString(fuelCursor_run.getColumnIndex(DBAdapter.FUEL_ID));
                    System.out.println("FuelID : " + fuelId);

                    MachineRunTimeData runData = new MachineRunTimeData(fuelId, db);
                    if (runData.getSendingStatus().equals(DBAdapter.SAVE)) {
                        intent.putExtra("run_data", runData);

                        System.out.println("Run Time kkk " + runData.toString());
                    }
                }
                fuelCursor_run.close();

                Cursor fuelCursor = db.getFuelByDate(dateString);
                if (fuelCursor.moveToFirst()) {
                    String fuelId = fuelCursor.getString(fuelCursor.getColumnIndex(DBAdapter.FUEL_ID));
                    FuelData fuelData = new FuelData(fuelId, db);
                    if (fuelData.getSendingStatus().equals(DBAdapter.SAVE)) {
                        intent.putExtra("fuel_data", fuelData);
                    }
                }
                fuelCursor.close();



                startActivity(intent);
            }
        });

        Cursor credentialCursor = db.getCredential();
        if(credentialCursor.moveToLast()){
            String userName = credentialCursor.getString(credentialCursor.getColumnIndex(DBAdapter.FO_NAME));
            this.userName.setText(userName);
        }
        credentialCursor.close();

        showCamera(mLayout);
        AppManager.isGPSenabled(this);

    }

    private void getSyncAlert() {
        Cursor getSyncDetail = db.getSyncDetail();
        if (getSyncDetail.moveToFirst()) {
            String dateTimeString = getSyncDetail.getString(getSyncDetail.getColumnIndex(DBAdapter.CREATED_DATE_TIME));
            Date startDate = new Date();
            try {
                startDate = Constents.sdf.parse(dateTimeString);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Date endDate = new Date();

            long duration = endDate.getTime() - startDate.getTime();
            long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);
            if (diffInHours > 24) {
                syncAlert.setVisibility(View.VISIBLE);
            } else {
                syncAlert.setVisibility(View.GONE);
            }
        } else {
            syncAlert.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        //             switch (item.getItemId()) {
        switch (item.getItemId()) {

            case R.id.action_sync: {
                if (isNetworkAvailable()) {
                    synchronize(Synchronize.syncList);
                } else {
                    Toast.makeText(MainActivity.this, "No Internet access", Toast.LENGTH_LONG).show();
                }
                break;
            }
            case R.id.action_logout: {
                logoutAlert();
                break;
            }

            case R.id.action_settings: {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            }
            case R.id.action_update: {
                checkApplicationUpdate(MainActivity.this);
                break;
            }

            case R.id.action_share_error: {
                File logFile = new File(Environment.getExternalStorageDirectory(), "SprayMonitorErrorLog.txt");
                if (logFile.exists()) {

                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    // set the type to 'email'
                    emailIntent.setType("vnd.android.cursor.dir/email");
                    String to[] = {"vishal.tripathi@iembsys.com"};
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
                    // the attachment
                    emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(logFile));
                    // the mail subject
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Spray Monitor Error log");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Sent from Spray Monitor app");

                    if (emailIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(Intent.createChooser(emailIntent, "Send email..."));
                    } else {
                        Toast.makeText(this, "No email application is available to share error log file", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(this, "AFS ErrorLog file does not exist ", Toast.LENGTH_LONG).show();
                }
                break;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
        return true;
    }

    private void checkApplicationUpdate(final Context context) {

        final ProgressDialog dialog = ProgressDialog.show(context, "Checking",
                "Please wait...", true);
//todo need to add valid url
        StringRequest stringAuthenticationRequest = new StringRequest(Request.Method.POST, "http://ecodrivesolution.co.in/services/apkDetail",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String updateResponse) {
//                        authenticationResponse = "{\"status\":\"success\",\"version\":\"1.01\",\"url\":\"http:\\/\\/ingencce.com\\/MobileApi\\/downloadApk\",\"imei_no\":\"353330061159194\",\"message\":\"Activation Success.Now you are able to sync.\",\"videoMaxDuration\":30,\"dateTime\":\"2016-07-28 11:46:50\"}";
                        System.out.println("Update Response : " + updateResponse);
                        try {
                            JSONObject jsonObject = new JSONObject(updateResponse);
                            if (jsonObject.has("status")) {

                                if (jsonObject.getString("status").equals("success")) {

                                    int appVersion = 0;
                                    serverAppVersion = jsonObject.getInt("version");
                                    String version_code = sharedpreferences.getString("version",null);

                                    try {
                                        appVersion = BuildConfig.VERSION_CODE;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    final String url = jsonObject.getString("url");
                                    final String uploadingTime = jsonObject.getString("datetime");
                                    if (version_code !=null && version_code.equalsIgnoreCase(String.valueOf(serverAppVersion))) {


                                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
                                        builder.setTitle("UPDATE");
                                        builder.setMessage("Application is already updated")
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.dismiss();

                                                    }
                                                })
                                                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.dismiss();
                                                    }
                                                });
                                        builder.show();




                                    } else {
                                     //   Toast.makeText(MainActivity.this, "Application is already updated", Toast.LENGTH_SHORT).show();
                                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
                                        builder.setTitle("UPDATE");
                                        builder.setMessage("New version available on server")
                                                .setPositiveButton("DOWNLOAD", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                        SharedPreferences.Editor editor = sharedpreferences.edit();
                                                        editor.putString("version", String.valueOf(serverAppVersion));
                                                        editor.commit();

                                                        dialogInterface.dismiss();
                                                        new DownloadAPK().execute(url);
                                                    }
                                                })
                                                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.dismiss();
                                                    }
                                                });
                                        builder.show();
                                    }

                                } else {
                                    Toast.makeText(context, "No Update", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(context, "Blank Response", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Response is not as expected", Toast.LENGTH_LONG).show();
                        }
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                Toast.makeText(context, "Not able to connect with server", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
//                map.put("imei_no", imei);
                return map;
            }
        };

        stringAuthenticationRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(stringAuthenticationRequest);

    }


    private class DownloadAPK extends AsyncTask<String, Void, Void> {

        String message = "";
        String apkName = "SparayMonitor.apk";
        String apkPath = Environment.getExternalStorageDirectory() + "/" + apkName;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(MainActivity.this, "",
                    "Downloading New Version.Please Wait....", true);

        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                if (params.length > 0) {
                    System.out.println("URL : " + params[0]);
                    URL u = new URL(params[0]);
                    InputStream is = u.openStream();

                    DataInputStream dis = new DataInputStream(is);

                    byte[] buffer = new byte[1024];
                    int length;
                    File file = new File(apkPath);
                    if (file.exists()) {
                        file.delete();
                    }
                    FileOutputStream fos = new FileOutputStream(file);
                    while ((length = dis.read(buffer)) > 0) {
                        fos.write(buffer, 0, length);
                    }
                    message = "success";
                    System.out.println("message : " + message);
                }
            } catch (MalformedURLException mue) {
                Log.e("SYNC getUpdate", "malformed url error", mue);
                message = "malformed url error";
            } catch (IOException ioe) {
                message = "io error";
                Log.e("SYNC getUpdate", "io error", ioe);
            } catch (SecurityException se) {
                Log.e("SYNC getUpdate", "security error", se);
                message = "security error";
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            dialog.cancel();
            final File apkFile = new File(apkPath);
            //System.out.println("message  "+message);
            if (message.contains("success")) {
                //System.out.println("apkFile.exists()  "+apkFile.exists());
                if (apkFile.exists()) {
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Would you like to install the new version?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    MainActivity.this.finish();
                                    Intent intent = new Intent(Intent.ACTION_VIEW);


                                    if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {

                                        File file = new File(Environment.getExternalStorageDirectory(), "SparayMonitor.apk");
                                        Uri photoURI = FileProvider.getUriForFile(MainActivity.this,  BuildConfig.APPLICATION_ID  + ".provider", file);
                                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        Log.v("uduasdh",photoURI+"");
                                        intent.setDataAndType(photoURI, "application/vnd.android.package-archive");

                                        startActivity(intent);
                                    }else {

                                        Uri uri = Uri.fromFile(apkFile);
                                        Log.v("udua",uri+"");
                                        //intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                                        intent.setDataAndType(uri, "application/vnd.android.package-archive");
                                        startActivity(intent);
                                    }
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    android.support.v7.app.AlertDialog alert = builder.create();
                    alert.show();
                }
            } else {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Could not download. Please try again")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                return;
                            }
                        });

                android.support.v7.app.AlertDialog alert = builder.create();
                alert.show();
            }

        }

    }

    static int synchronizeCount = 0;
    String syncResult = "";
    String syncedElement = "";

    private void synchronize(final String[] syncName) {

        final int arrayCount = syncName.length;
        synchronizeCount = 0;
        syncResult = "";
        syncedElement = "";

        class Background extends AsyncTask<Void, Void, String> {
            String syncString;

            Background(String syncString) {
                this.syncString = syncString;
            }

            @Override
            protected void onPreExecute() {
                pDialog = ProgressDialog.show(MainActivity.this, "",
                        "Synchronize for " + syncString + ".....", true);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = Synchronize.syncFor(syncString, db, MainActivity.this);
                if ((!Synchronize.isConnectedToServer)) {
                    syncResult = syncResult + "Could not sync for " + syncString + "\n";
                } else if (!response.contains("Success")) {
                    syncResult = syncResult + response + "\n";
                } else {
                    syncedElement = syncedElement + "," + syncString;
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                if (pDialog != null) {
                    if (pDialog.isShowing()) {
                        pDialog.dismiss();
                    }
                }
                synchronizeCount++;
                if (synchronizeCount < arrayCount) {
                    new Background(syncName[synchronizeCount]).execute();
                } else {
                    String[] syncedArray = syncedElement.split(",");

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Syncing Status");
                    if (syncResult.trim().length() > 0) {
                        builder.setMessage(syncResult);
                    }/*else if(syncedArray.length != Synchronize.syncList.length){
                        builder.setMessage("Could Not Synchronize. Please Sync again");
                    }*/ else {
                        builder.setMessage("Synchronize Successfully");
                        updateSyncDetail();
                        newFarmer.setEnabled(true);
                        spray.setEnabled(true);
                    }
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            getSyncAlert();
                        }
                    });
                    builder.show();
                }
            }
        }
        new Background(syncName[synchronizeCount]).execute();
    }

    private boolean updateSyncDetail() {
        boolean isUpdated = false;
        db.db.execSQL("delete from " + DBAdapter.TABLE_SYNC_DETAIL);
        ContentValues values = new ContentValues();
        Date date = new Date();
        String dateString = Constents.sdf.format(date);
        values.put(DBAdapter.CREATED_DATE_TIME, dateString);
        values.put(DBAdapter.VERSION, "1.0");
        long k = db.db.insert(DBAdapter.TABLE_SYNC_DETAIL, null, values);
        if (k != -1) {
            isUpdated = true;
        }
        return isUpdated;
    }


    @Override
    public void onBackPressed() {
        logoutAlert();
    }

    private void logoutAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to logout?").
                setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        MainActivity.this.finish();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }).
                setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        builder.show();
    }

    public static final String TAG = "permission tag";
    private static final int REQUEST_CAMERA = 0;
    private static final int REQUEST_GPS = 1;



    /*ACCESS_NETWORK_STATE" />
    permission.WRITE_EXTERNAL_STORAGE" />
    permission.READ_EXTERNAL_STORAGE" />
    permission.RECORD_AUDIO" />
    permission.CAMERA" />
    permission.READ_PHONE_STATE" />
    permission.ACCESS_COARSE_LOCATION" />
    permission.ACCESS_FINE_LOCATION" />*/

    /**
     * Called when the 'show camera' button is clicked.
     * Callback is defined in resource layout definition.
     */
    public void showCamera(View view) {
        Log.i(TAG, "Show camera button pressed. Checking permission.");
        // BEGIN_INCLUDE(camera_permission)
        // Check if the Camera permission is already available.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Camera permission has not been granted.

            requestCameraPermission();

        } else {

            // Camera permissions is already available, show the camera preview.
            Log.i(TAG,
                    "CAMERA permission has already been granted. Displaying camera preview.");
//            showCameraPreview();
        }
        // END_INCLUDE(camera_permission)

    }

    private void requestCameraPermission() {
        Log.i(TAG, "CAMERA permission has NOT been granted. Requesting permission.");

        // BEGIN_INCLUDE(camera_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Log.i(TAG,
                    "Displaying camera permission rationale to provide additional context.");
            Snackbar.make(mLayout, R.string.permission_camera_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    REQUEST_CAMERA);
                        }
                    })
                    .show();
        } else {

            // Camera permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
        }
        // END_INCLUDE(camera_permission_request)
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CAMERA) {
            // BEGIN_INCLUDE(permission_result)
            // Received permission result for camera permission.
            Log.i(TAG, "Received response for Camera permission request.");

            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission has been granted, preview can be displayed
                Log.i(TAG, "CAMERA permission has now been granted. Showing preview.");
                Snackbar.make(mLayout, R.string.permission_available_camera,
                        Snackbar.LENGTH_SHORT).show();
            } else {
                Log.i(TAG, "CAMERA permission was NOT granted.");
                Snackbar.make(mLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT).show();

            }
            // END_INCLUDE(permission_result)

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
                    || y < w.getTop() || y > w.getBottom()) ) {

                try {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                }catch (Exception e) {
                    // TODO: handle exception
                }

            }
        }
        return ret;
    }


}
