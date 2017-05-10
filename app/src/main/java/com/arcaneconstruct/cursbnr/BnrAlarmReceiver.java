package com.arcaneconstruct.cursbnr;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.Calendar;

/**
 * Cand este generata o alarma se primeste Intentul si porneste un IntentService pentru a face procesari
 * {@code SchedulingService}
 * */
public class BnrAlarmReceiver extends WakefulBroadcastReceiver {
    private static final  String TAG = "BnrAlarmReceiver" ;
    // AlarmManager, furnizeaza acces la  serviciile de alarma ale sistemului
    private AlarmManager alarmMgr;
    // Pending Intent care este declansat cand este declandata alarma
    private PendingIntent alarmIntent;
  
    @Override
    public void onReceive(Context context, Intent intent) {   

        Intent service = new Intent(context, SchedulingService.class);
        Log.d(TAG, "received alarm ");
        // Porneste serviciul pastrand sistemul treaz la procesarea acestuia
        startWakefulService(context, service);
    }


    /**
     * Seteaza o alarma care porneste la un interval configurat
     * @param context
     */
    public void setAlarm(Context context) {
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, BnrAlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent,0);
        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
               2*60*1000,  5*60*1000, alarmIntent);
        Log.d(TAG, "setare alarma  ");
    }

    /**
     *Anuleaza alarma
     * @param context
     */

    public void cancelAlarm(Context context) {

        if (alarmMgr!= null) {
            alarmMgr.cancel(alarmIntent);
        }
        

    }

}
