package com.wrms.spraymonitor.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.wrms.spraymonitor.MainActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by piyush on 9/24/15.
 */
public class AppManager {


    private static AppManager appManager;

    private AppManager() {
    }

    public static AppManager getInstance() {

        if (appManager == null)
            appManager = new AppManager();
        return appManager;
    }

    public static boolean isOnline(Context context) {
        boolean isConnected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(context.CONNECTIVITY_SERVICE);
            isConnected = cm.getActiveNetworkInfo().isConnected();
        } catch (Exception ex) {
            isConnected = false;
        }
        return isConnected;
    }

    public static Boolean isGPSenabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps(context);
            return false;
        } else {
            return true;
        }
    }


    private static void buildAlertMessageNoGps(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Your Location Service is Disable");
        builder.setMessage("Please Enable Location Service")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static double getFileSize(String path) {
        File file = new File(path);
        double sizeInBytes = file.length();
        double sizeInMb = sizeInBytes / (1024 * 1024);
        DecimalFormat precision = new DecimalFormat("0.00");
        return Double.parseDouble(precision.format(sizeInMb));
    }

    public static boolean isMemorySufficient(int reqMemory) {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        double sdAvailSize = (double) stat.getAvailableBlocks() * (double) stat.getBlockSize();
        double megabite = sdAvailSize / 1048576;
        if (reqMemory <= megabite) {
            return true;
        }
        return false;
    }



    public static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);
        fileOrDirectory.delete();
    }


    public void importDB(Context context) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            if (sd.canWrite()) {
                File backupDB = context.getDatabasePath("cce");
                String backupDBPath = String.format("%s.bak", "cce");
                File currentDB = new File(sd, backupDBPath);
                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void exportDB(Context context) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String backupDBPath = String.format("%s.bak", "cce");
                File currentDB = context.getDatabasePath("cce");
                File backupDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static final String LAC_ID = "0000";
    public static final String CELL_ID = "0000";
    public static final String MCC = "000";
    public static final String MNC = "000";

    public static final String LOCATION_PREFERENCE = "location_prefrance";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String LOCATION_TIME_STAMP = "location_time_stamp";
    public static final String LOCATION_WITHIN_TIME = "location_within_time";
    public static final String LOCATION_WITHIN_TIME_YES = "yes";
    public static final String LOCATION_WITHIN_TIME_NO = "no";

    private static final long LOCATION_TIME_LIMIT = 60000;

    public static boolean setLocation(Context ctx, double latitude, double longitude, long datetimeStamp) {
        SharedPreferences.Editor editor = ctx.getSharedPreferences(LOCATION_PREFERENCE, ctx.MODE_PRIVATE).edit();
        editor.putString(LATITUDE, String.valueOf(latitude));
        editor.putString(LONGITUDE, String.valueOf(longitude));
        editor.putLong(LOCATION_TIME_STAMP, datetimeStamp);
        return editor.commit();
    }

    public static Map<String, String> getLocation(Context ctx) {
        Map<String, String> locationMap = new HashMap<>();
        SharedPreferences prefs = ctx.getSharedPreferences(LOCATION_PREFERENCE, ctx.MODE_PRIVATE);
        String latitude = prefs.getString(LATITUDE, "0.0");
        locationMap.put(LATITUDE, latitude);
        String longitude = prefs.getString(LONGITUDE, "0.0");
        locationMap.put(LONGITUDE, longitude);
        long locationDateTimeStamp = prefs.getLong(LOCATION_TIME_STAMP, 0);
        if (locationDateTimeStamp == 0) {
            locationMap.put(LOCATION_WITHIN_TIME, LOCATION_WITHIN_TIME_NO);
        } else {
            long timeDifference = new Date().getTime() - locationDateTimeStamp;
            if (timeDifference < LOCATION_TIME_LIMIT) {
                locationMap.put(LOCATION_WITHIN_TIME, LOCATION_WITHIN_TIME_YES);
            } else {
                locationMap.put(LOCATION_WITHIN_TIME, LOCATION_WITHIN_TIME_NO);
            }
        }
        return locationMap;
    }


    public static boolean isLowerVersion(Context context) {
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = pInfo.versionName;
        if (version.contains("(4.2)")) {
            return true;
        }
        return false;
    }

    public static String getBase64FromPath(String imagePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        BitmapFactory.Options options = new BitmapFactory.Options();
        String base64String = null;
        if (bitmap != null) {

            if (bitmap.getWidth() > 1600) {
                options.inSampleSize = 8;
                System.out.println("option " + 8);
            } else if (bitmap.getWidth() > 1280) {
                options.inSampleSize = 2;
                System.out.println("option " + 2);
            } else {
                options.inSampleSize = 1;
                System.out.println("option " + 1);
            }
            bitmap = BitmapFactory.decodeFile(imagePath, options);
            System.out.println("Image Width " + options.outWidth);
            System.out.println("Image Hieght " + options.outHeight);

            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
            byte[] ba = bao.toByteArray();
            base64String = Base64.encodeBytes(ba);
        }
        return base64String;
    }

    public static void closeActivities(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static String getLanguagePrefs(Context ctx){
        SharedPreferences myPreference= PreferenceManager.getDefaultSharedPreferences(ctx);
        String languagePreference = myPreference.getString("language_pref_key", "1");
        return languagePreference;
    }

}

