package com.wrms.spraymonitor.background;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Lat_Lon_CellID extends Service {
    public static double lat = 0.0, lon = 0.0;
    public static long gps_time = 0;
    public static String imeino, datetimestamp = null;
    LocationManager _locationManager;
    LocationListener _connector;

    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";

    public static final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);

    TelephonyManager tm;
    GsmCellLocation gsm_cell_location;

    public static String CellID, LAC;
    public static String mcc, mnc;

    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String DATE_TIME_STAMP = "date_time_stamp";
    public static final String CELL_ID = "cell_id";
    public static final String LAC_ID = "lac_id";
    public static final String MCC = "mcc";
    public static final String MNC = "mnc";
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 1f;

    @Override
    public void onCreate() {
        try {
            tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            imeino = tm.getDeviceId();
            gsm_cell_location = (GsmCellLocation) tm.getCellLocation();
            //CellID = gsm_cell_location.getCid();
            Lat_Lon_CellID.datetimestamp = sdf.format((new Date()).getTime());
            turnGPSOn();
            _locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            _connector = new MyLocationListener();
            if (Build.VERSION.SDK_INT >= 23 &&
                    ContextCompat.checkSelfPermission(Lat_Lon_CellID.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(Lat_Lon_CellID.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            _locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, _connector);
            final Timer timer = new Timer();
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    System.out.println("location timer is running");
                    if (gsm_cell_location != null) {
                        try {
                            gsm_cell_location = (GsmCellLocation) tm.getCellLocation();
                            CellID = String.valueOf(gsm_cell_location.getCid());
                            LAC = String.valueOf(gsm_cell_location.getLac());
                            String mcc_mnc = tm.getSimOperator();
                            mcc = String.valueOf(Integer.valueOf(mcc_mnc) / 100);
                            mnc = String.valueOf(Integer.valueOf(mcc_mnc) % 100);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    Lat_Lon_CellID.datetimestamp = sdf.format((new Date()).getTime());
                    if (Build.VERSION.SDK_INT >= 23 &&
                            ContextCompat.checkSelfPermission(Lat_Lon_CellID.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(Lat_Lon_CellID.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    _locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, _connector);
                }

            }, 1000, 30 * 1000);

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            System.out.println("Exception" + e);
        }
        //stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    private void turnGPSOn() {
        try {
            String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

            if (!provider.contains("gps")) { //if gps is disabled
                final Intent poke = new Intent();
                poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
                poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
                poke.setData(Uri.parse("3"));
                sendBroadcast(poke);
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public static String getCELLID() {
        String str;
        str = Lat_Lon_CellID.CellID;
        if (str == null) {
            return "0";
        }
        return str;
    }

    public static String getLac() {
        String str;
        str = Lat_Lon_CellID.LAC;
        if (str == null) {
            return "0";
        }
        return str;
    }

    public static String getMCC() {
        String str;
        str = Lat_Lon_CellID.mcc;
        if (str == null) {
            return "0";
        }
        return str;

    }

    public static String getMNC() {
        String str;
        str = Lat_Lon_CellID.mnc;
        if (str == null) {
            return "0";
        }
        return str;
    }

    public static String getDatetimestamp() {
        String str;
        str = Lat_Lon_CellID.datetimestamp;
        if (str == null) {
            return "0000-00-00 00:00:00";
        }
        return str;
    }


}

class MyLocationListener implements LocationListener {
    Date d1 = new Date();

    @Override
    public void onLocationChanged(Location loc) {
        //Toast.makeText(get, text, duration)
        Lat_Lon_CellID.lat = loc.getLatitude();
        Lat_Lon_CellID.lon = loc.getLongitude();
        Lat_Lon_CellID.gps_time = d1.getTime();

        System.out.println("Get the GPS " + loc.getLatitude() + "");

        String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        Lat_Lon_CellID.datetimestamp = sdf.format(Lat_Lon_CellID.gps_time);
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }
}
