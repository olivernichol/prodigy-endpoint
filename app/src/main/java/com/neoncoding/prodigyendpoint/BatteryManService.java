package com.neoncoding.prodigyendpoint;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class BatteryManService extends Service {
    private static final String TAG = "BatteryManService";
    public static final String BATTERY_UPDATE = "battery";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("BatteryMan", "BatteryMan Thread Started");
        if (intent != null && intent.hasExtra(BATTERY_UPDATE)){
            Log.i("ThreadStatus", "BatteryMan Intent Valid");
            new BatteryCheckAsync().execute();
        }

        return START_STICKY;
    }
    public void doBattRequest(float level) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        WifiManager wifiManager = (WifiManager) Application.getAppContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo ();
        String bssid = info.getBSSID().toString();
        if (!bssid.contains(sharedPref.getString("bssid", "YOURHOMEBSSIDHERE"))) { return; }
        RequestQueue queue = Volley.newRequestQueue(Application.getAppContext());
        JSONObject content = new JSONObject();
        try {
            content.put("device",Build.MODEL);
            content.put("type","batt");
            content.put("level",level);
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
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private class BatteryCheckAsync extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... arg0) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Application.getAppContext());
            if (!sharedPref.getBoolean("batteryState", true)) return null;
            //Battery State check - create log entries of current battery state
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = BatteryManService.this.registerReceiver(null, ifilter);

            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;
            Log.i("BatteryInfo", "Battery is charging: " + isCharging);
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            Log.i("BatteryInfo", "Battery charge level: " + ((level / (float)scale) * 100));
            doBattRequest(((level / (float)scale) * 100));
            return null;
        }

        protected void onPostExecute(){
            BatteryManService.this.stopSelf();
        }
    }
}