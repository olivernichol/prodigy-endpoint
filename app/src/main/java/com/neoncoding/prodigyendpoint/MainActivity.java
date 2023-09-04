package com.neoncoding.prodigyendpoint;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.CheckBox;
import android.content.SharedPreferences;
import android.content.Context;
import android.view.View.OnFocusChangeListener;
import android.view.View;

import android.os.Bundle;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, NotificationWatchdog.class);
        startService(intent);
        SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
        ((EditText)findViewById(R.id.ipInput)).setText(sharedPref.getString("ip", "DEFAULTIP"));
        ((EditText)findViewById(R.id.bssidInput)).setText(sharedPref.getString("bssid", "YOURHOMEBSSIDHERE"));
        Log.i("ThreadStatus", "All Services Started.");
        ((EditText)findViewById(R.id.ipInput)).setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("ip", ((EditText)findViewById(R.id.ipInput)).getText().toString());
                    editor.apply();
                    Context context = getApplicationContext();
                    CharSequence text = "Saved Changes";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        });
        ((EditText)findViewById(R.id.bssidInput)).setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("bssid", ((EditText)findViewById(R.id.bssidInput)).getText().toString());
                    editor.apply();
                    Context context = getApplicationContext();
                    CharSequence text = "Saved Changes";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        });
    }
    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.batteryCheck:
                if (checked) {
                    SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("batteryState", checked);
                    editor.apply();
                    Context context = getApplicationContext();
                    CharSequence text = "Saved Changes";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            else {
                    SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("batteryState", checked);
                    editor.apply();
                    Context context = getApplicationContext();
                    CharSequence text = "Saved Changes";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                break;
            case R.id.notificationCheck:
                if (checked) {
                    SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("notifState", checked);
                    editor.apply();
                    Context context = getApplicationContext();
                    CharSequence text = "Saved Changes";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            else {
                    SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("notifState", checked);
                    editor.apply();
                    Context context = getApplicationContext();
                    CharSequence text = "Saved Changes";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                break;
        }
    }
    public void onTestNotification(View view) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (!sharedPref.getBoolean("notifState", true)) return; //TODO: Add a super junction to ensure normal processes still occur.
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo ();
        String bssid = info.getBSSID().toString();
        if (!bssid.contains(sharedPref.getString("bssid", "YOURHOMEBSSIDHERE"))) return;
        Log.i("NotifStatus", "Sending Test Notification");
        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject content = new JSONObject();
        try {
            content.put("app","ProdigyEndpoint");
            content.put("title","Test Notification");
            content.put("message","This is a test notification");
            content.put("ticker","TestNotification");
            content.put("time", null);
            content.put("category","Test");
            content.put("addinfo",null);
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
        Context context = getApplicationContext();
        CharSequence text = "Notification Sent";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void onSave(View view) {
        SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("bssid", ((EditText)findViewById(R.id.bssidInput)).getText().toString());
        editor.putString("ip", ((EditText)findViewById(R.id.ipInput)).getText().toString());
        editor.apply();
        Context context = getApplicationContext();
        CharSequence text = "Saved Changes";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void onDestroy() {
        super.onDestroy();
    }
}