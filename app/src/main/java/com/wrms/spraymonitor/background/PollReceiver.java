package com.wrms.spraymonitor.background;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;

public class PollReceiver extends BroadcastReceiver {
  private static final int PERIOD=1200000;//20*60*1000; 20 minutes
//	private static final int PERIOD=60000;
//  private static final int PERIOD=10000;

  @Override
  public void onReceive(Context ctxt, Intent i) {
    SharedPreferences myPreference= PreferenceManager.getDefaultSharedPreferences(ctxt);
    String syncFrequency = myPreference.getString("sync_frequency", "15");
    int multiplication = Integer.valueOf(syncFrequency);
    int frequency = multiplication*60000;
    scheduleAlarms(ctxt,frequency);
  }

  public static void scheduleAlarms(Context ctxt,int syncFrequency) {
    AlarmManager mgr=
        (AlarmManager)ctxt.getSystemService(Context.ALARM_SERVICE);
    Intent i=new Intent(ctxt, AuthenticateService.class);
    PendingIntent pi=PendingIntent.getService(ctxt, 0, i, 0);

    /*mgr.setRepeating(AlarmManager.ELAPSED_REALTIME,
                     SystemClock.elapsedRealtime() + PERIOD, PERIOD, pi);*/
    mgr.setRepeating(AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime() + syncFrequency, syncFrequency, pi);
  }
}
