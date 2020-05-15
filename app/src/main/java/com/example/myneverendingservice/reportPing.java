package com.example.myneverendingservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static android.content.ContentValues.TAG;
public class reportPing extends Service {
    public String mHost = "http://10.0.2.2:5000/postresults";
    public static final String report_1 = "report_1";
    public static final String report_2 = "report_2";
    public static final String report_3 = "report_3";
    public static final String RESULT = "RESULT";
    URL url;
    String reportString;
    boolean isConnected;
    public reportPing() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate() {
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        reportString = intent.getStringExtra("report");

        Log.d(TAG, "received report string is :" + reportString);
        if (reportString.length() < 10)
        {
            Log.d(TAG, "invalid report string, will skip this cycle!");
            return START_NOT_STICKY;
        }
        if(checkConnection())
        {
            Log.d(TAG, "initializing send function!");
            //sendReport();
            threadInBackground mThread = new threadInBackground( this, reportString);
            new Thread(mThread).start();
        }
        return START_NOT_STICKY;
    }

    public  boolean checkConnection()
    {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork != null) {
            isConnected = activeNetwork.isConnectedOrConnecting();
        }
        return isConnected;
    }

    class threadInBackground implements Runnable
    {
        public Context context;
        public String initialResult;

        threadInBackground(Context ctx, String input)
        {
            this.initialResult = input;
            this.context = ctx;
        }

        @Override
        public void run()
        {
            SharedPreferences preferences = getApplicationContext().getSharedPreferences("MyPref", 0);
            SharedPreferences.Editor editor = preferences.edit();

            ConnectivityManager connManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo info = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            boolean connected = info.isConnected();
            if(connected)
            {
                try
                {
                    url = new URL (mHost);
                    Log.d(TAG, "url is :" + url.toString());
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json; utf-8");
                    conn.setDoOutput(true);
                    String res1 = preferences.getString(report_1, "");
                    String res2 = preferences.getString(report_2, "");
                    String res3 = preferences.getString(report_3, "");
                    if(!res1.equals("") && !res2.equals("") && !res3.equals(""))
                    {
                        JSONObject jsonparam = new JSONObject();
                        jsonparam.put(RESULT, res1 + ";" + res2 + ";" + res3);
                        DataOutputStream printout = new DataOutputStream(conn.getOutputStream());
                        printout.writeBytes(URLEncoder.encode(jsonparam.toString(), "UTF-8"));
                        printout.flush();
                        printout.close();
                        editor.remove(report_1).apply();
                        editor.remove(report_2).apply();
                        editor.remove(report_3).apply();
                        editor.putString(report_1, initialResult);
                    }
                    else if(!res1.equals("") && !res2.equals(""))
                    {
                        JSONObject jsonparam = new JSONObject();
                        jsonparam.put(RESULT, res1 + ";" + res2 + ";" + initialResult);
                        DataOutputStream printout = new DataOutputStream(conn.getOutputStream());
                        printout.writeBytes(URLEncoder.encode(jsonparam.toString(), "UTF-8"));
                        printout.flush();
                        printout.close();
                        editor.remove(report_1).apply();
                        editor.remove(report_2).apply();
                    }
                    else if(!res1.equals(""))
                    {
                        JSONObject jsonparam = new JSONObject();
                        jsonparam.put(RESULT, res1 + ";" + initialResult);
                        DataOutputStream printout = new DataOutputStream(conn.getOutputStream());
                        printout.writeBytes(URLEncoder.encode(jsonparam.toString(), "UTF-8"));
                        printout.flush();
                        printout.close();
                        editor.remove(report_1).apply();
                    }
                    else
                    {
                        JSONObject jsonparam = new JSONObject();
                        jsonparam.put(RESULT, initialResult);
                        DataOutputStream printout = new DataOutputStream(conn.getOutputStream());
                        printout.writeBytes(URLEncoder.encode(jsonparam.toString(), "UTF-8"));
                        printout.flush();
                        printout.close();
                    }

                    try
                    {
                        int i;
                        i = conn.getResponseCode();
                        Log.d("Result", "Response code from host is: " + i);
                    }catch (Exception e)
                    {
                        Log.d("ResultERR", "Error: " + e.getMessage());
                    }
                    /*try
                    {
                        BufferedReader br = new BufferedReader(
                                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                        StringBuilder response = new StringBuilder();
                        String responseLine = null;
                        while( (responseLine = br.readLine()) != null )
                        {
                            response.append(responseLine.trim());
                        }
                        Log.d("Result", "Result is: " + response.toString());
                    }
                    catch (Exception e)
                    {
                        Log.d("ResultERR", "Error: " + e.getMessage());
                    }*/
                }
                catch (MalformedURLException e)
                {
                    Log.d("Malform", "Error: " + e.getMessage());
                }
                catch (IOException e)
                {
                    Log.d("IO", "Error: " + e.getMessage());
                } catch (JSONException e) {
                    Log.d("JSON", "Error: " + e.getMessage());
                }
            }
            else
            {
                String res1 = preferences.getString(report_1, "");
                String res2 = preferences.getString(report_2, "");
                String res3 = preferences.getString(report_3, "");
                if(res1.equals("") && res2.equals("") && res3.equals(""))
                {
                    editor.remove(report_1).apply();
                    editor.remove(report_2).apply();
                    editor.remove(report_3).apply();
                    editor.putString(report_1, res2).apply();
                    editor.putString(report_2, res3).apply();
                    editor.putString(report_3, initialResult).apply();
                }
                else if(!res1.equals("") && !res2.equals(""))
                {
                    preferences.edit().putString(report_3, initialResult).apply();
                }
                else if(!res1.equals(""))
                {
                    preferences.edit().putString(report_2, initialResult).apply();
                }
                else
                {
                    preferences.edit().putString(report_1, initialResult).apply();
                }
            }
        }
    }
}