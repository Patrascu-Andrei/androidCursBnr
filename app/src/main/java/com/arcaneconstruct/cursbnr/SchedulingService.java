package com.arcaneconstruct.cursbnr;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * SchedulingService face toate procesarile la declansarea alarmei programate mentinand. Receiver-ul alarmei mentine
 * device-ul activ atat timp cat se face procesarea, La finalul procesarii se notifica Receiver-ul sa elibereze markerul activ
 */
public class SchedulingService extends IntentService {
    public SchedulingService() {
        super("SchedulingService");
    }

    public static final String TAG = "SchedulingService";
    ContentResolver resolver;


    @Override
    protected void onHandleIntent(Intent intent) {
        boolean updateNeeded=false;
        resolver=getContentResolver();
        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        String lastSync = settings.getString("lastSync", "");
        Log.d(TAG,"lastSync ="+lastSync);
        XMLRetriever getXML = new XMLRetriever(this);

        try {
            Log.d(TAG,"Service read from url");
            URL url = new URL("http://bnr.ro/nbrfxrates.xml");
            List<Curs> lista =getXML.parseXML(url.openConnection().getInputStream());
            Curs temp=null;
            for(int i=0;i<lista.size();i++){
                temp=lista.get(i);
                if(i==0&&(!temp.getDate().equals(lastSync))) {
                    Log.d(TAG,"actualizare necesara, data noua:"+temp.getDate());
                    updateNeeded=true;
                }
                if(!updateNeeded) break;
                ContentValues values=new ContentValues();
                values.put(ExchangeDatabase.COLUMN_CURRENCY,temp.getCurrency());
                values.put(ExchangeDatabase.COLUMN_DATE,temp.getDate());
                values.put(ExchangeDatabase.COLUMN_RATE, temp.getRate());
                Log.d(TAG, "actualizare necesara"+updateNeeded+" writing " + values.toString());
                resolver.insert(ExchangeProvider.CONTENT_URI,values);
            }
            if(updateNeeded){
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("lastSync", temp.getDate());

                editor.commit();
            }

        } catch (MalformedURLException me) {
            me.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(updateNeeded) {
            Log.d(TAG, "send notification");
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this).setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Curs BNR")
                            .setContentText("Curs BNR actualizat");
            Intent notificationIntent = new Intent(this, MainActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mBuilder.setContentIntent(contentIntent);
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mBuilder.setSound(alarmSound);
            mBuilder.setAutoCancel(true);
            notificationManager.notify(1, mBuilder.build());
        }
        BnrAlarmReceiver.completeWakefulIntent(intent);

    }


}
