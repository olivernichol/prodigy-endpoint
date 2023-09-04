package com.neoncoding.prodigyendpoint;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.content.Intent;
import android.service.notification.StatusBarNotification;
import android.widget.Toast;
import android.util.Log;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.android.volley.VolleyError;
import android.app.Notification;
import android.os.Bundle;
import android.os.Build;
import org.json.JSONObject;
import org.json.JSONException;
import android.content.Context;
import android.content.Intent;

public class NotificationWatchdog extends NotificationListenerService {
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (!sharedPref.getBoolean("notifState", true)) {
            super.onNotificationPosted(sbn);
            return;
        }
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo ();
        String bssid = info.getBSSID().toString();
        if (!bssid.contains(sharedPref.getString("bssid", "YOURHOMEBSSIDHERE"))) {
            super.onNotificationPosted(sbn);
            return;
        }
        Bundle extraNotifData = sbn.getNotification().extras;
        Notification notifRef = sbn.getNotification();
        Log.i("NotifStatus", "Notification Recieved");
        Log.d("NotifData", "Package ID: " + sbn.getPackageName());
        Log.d("NoftifData", "Title: " + sbn.getNotification().extras.get(sbn.getNotification().EXTRA_TITLE));
        Log.d("NotifData", "Message: " + sbn.getNotification().extras.get(sbn.getNotification().EXTRA_TEXT));
        Log.d("NotifData", "Ticker: " + sbn.getNotification().tickerText);
        Log.d("NotifData", "Time: " + sbn.getNotification().when);
        Log.d("NotifData", "Category: " + sbn.getNotification().category);
        Log.d("NotifData", "addInfo: " + sbn.getNotification().extras.get(sbn.getNotification().EXTRA_INFO_TEXT));
        //Log.d("NotifData", "Persons: " + sbn.getNotification().extras.get(sbn.getNotification().EXTRA_PEOPLE)); TODO: Try and get People Stuffs working. We can live without it for now, but still.
        Log.d("NotifData", "Device: " + Build.MODEL);
        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject content = new JSONObject();
        try {
            content.put("app",sbn.getPackageName());
            content.put("title",extraNotifData.get(notifRef.EXTRA_TITLE));
            content.put("message",extraNotifData.get(notifRef.EXTRA_TEXT));
            content.put("ticker",notifRef.tickerText);
            content.put("time",notifRef.when);
            content.put("category",notifRef.category);
            content.put("addinfo",extraNotifData.get(notifRef.EXTRA_INFO_TEXT));
            content.put("device",Build.MODEL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("Status", "Json Object Created");
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, "http://" + sharedPref.getString("ip", "DEFAULTIP"), content, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("WebRequest", "Responded");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("WebRequest", "Error");
            }
        });
        jor.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jor);
        super.onNotificationPosted(sbn);
    }
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (!sharedPref.getBoolean("notifState", true)) {
            super.onNotificationRemoved(sbn);
            return;
        }
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo ();
        String bssid = info.getBSSID().toString();
        if (!bssid.contains(sharedPref.getString("bssid", "YOURHOMEBSSIDHERE"))) {
            super.onNotificationRemoved(sbn);
            return;
        }
        Bundle extraNotifData = sbn.getNotification().extras;
        Notification notifRef = sbn.getNotification();
        Log.i("NotifStatus", "Notification Recieved");
        Log.d("NotifData", "Package ID: " + sbn.getPackageName());
        Log.d("NoftifData", "Title: " + sbn.getNotification().extras.get(sbn.getNotification().EXTRA_TITLE));
        Log.d("NotifData", "Message: " + sbn.getNotification().extras.get(sbn.getNotification().EXTRA_TEXT));
        Log.d("NotifData", "Ticker: " + sbn.getNotification().tickerText);
        Log.d("NotifData", "Time: " + sbn.getNotification().when);
        Log.d("NotifData", "Category: " + sbn.getNotification().category);
        Log.d("NotifData", "addInfo: " + sbn.getNotification().extras.get(sbn.getNotification().EXTRA_INFO_TEXT));
        Log.d("NotifData", "Device: " + Build.MODEL);
        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject content = new JSONObject();
        try {
            content.put("type","dismiss");
            content.put("app",sbn.getPackageName());
            content.put("title",extraNotifData.get(notifRef.EXTRA_TITLE));
            content.put("message",extraNotifData.get(notifRef.EXTRA_TEXT));
            content.put("ticker",notifRef.tickerText);
            content.put("time",notifRef.when);
            content.put("category",notifRef.category);
            content.put("addinfo",extraNotifData.get(notifRef.EXTRA_INFO_TEXT));
            content.put("device",Build.MODEL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("Status", "Json Object Created");
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, "http://" + sharedPref.getString("ip", "DEFAULTIP"), content, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("WebRequest", "Responded");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("WebRequest", "Error");
            }
        });
        jor.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jor);
        super.onNotificationRemoved(sbn);
    }
    @Override
    public void onListenerConnected() {
        Log.i("NotifStatus", "Listener Awake");
        super.onListenerConnected();
    }
    @Override
    public void onCreate() {
        Log.i("ThreadStatus", "Thread Created.");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AlarmReceiver.startAlarms(this.getApplicationContext());
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        Log.i("ThreadStatus", "Thread Destroyed");
    }
}
