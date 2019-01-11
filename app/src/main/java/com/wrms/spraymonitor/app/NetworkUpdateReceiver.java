package com.wrms.spraymonitor.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.wrms.spraymonitor.background.AuthenticateService;

/**
 * Created by WRMS on 23-05-2016.
 */

public class NetworkUpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isConnected = isOnline(context);
        if (isConnected){
            Log.i("NET", "connected " +isConnected);
            final DBAdapter db = new DBAdapter(context);
            db.open();

            final Context ctx = context;

            if(!AuthenticateService.isRunning()) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        AuthenticateService.sendPendingData(db, ctx);
                        return null;
                    }
                }.execute();
            }

        }else{
            Log.i("NET", "not connected " +isConnected);
        }
    }

    public boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in air plan mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }

}