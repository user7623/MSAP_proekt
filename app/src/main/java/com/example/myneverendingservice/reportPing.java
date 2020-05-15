package com.example.myneverendingservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class reportPing extends Service {

    String reportString;
    boolean isConnected;
    URL url;
    HttpURLConnection con;
    int counter = 0;
    boolean storedReports = false;

    public reportPing() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
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

        //dokolku e povrzano prati inaku zacuvaj initializing
        if (reportString.length() < 10)
        {
            Log.d(TAG, "invalid report string, will skip this cycle!");
            return START_NOT_STICKY;
        }
        if(checkConnection())
        {
            Log.d(TAG, "initializing send function!");
            sendReport();
        }
        else
        {
            Log.d(TAG, "No connection, will write to save for later!");
            writeToMem();
        }
        return START_NOT_STICKY;
    }
    public void sendReport()
    {
        try
        {
            url = new URL ("http://10.0.2.2:5000/postresults ");
            Log.d(TAG, "url is :" + url.toString());
        }catch (IOException e)
        {
            if(e.getMessage() != null)
            {
                Log.d(TAG, e.getMessage());
            }
        }
        try {
            con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            if(storedReports)
            {
                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                SharedPreferences.Editor editor = pref.edit();

                String pom = pref.getString("storedReportString", null);

                reportString = reportString + pom;

                editor.clear();
                editor.apply();

                storedReports = false;
            }
            String jsonInputString = reportString;


            //TODO: fix fatal exception!
            OutputStream os = con.getOutputStream();

            try{
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
                Log.d(TAG, "sent report");
            }catch (IOException e)
            {
                if(e.getMessage() != null){
                    Log.d(TAG, e.getMessage());
                }
            }
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            try{
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                Log.d(TAG, "response is: " + response.toString());
                System.out.println(response.toString());
            }catch (IOException e)
            {
                if(e.getMessage() != null){
                    Log.d(TAG, e.getMessage());
                }
            }

        }catch (IOException e)
        {
            if(e.getMessage() != null){
                Log.d(TAG, e.getMessage());
            }
        }
    }
    public void writeToMem()
    {
        boolean flag = false;
        char c = '`';
        LinkedList<String> mList = new LinkedList<String>();
        String pom2 = "";
        Environment.getDataDirectory();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = pref.edit();

        String forStoring = pref.getString("storedReportString", null);

        if(counter > 3)
        {
            try{
                char []stored = forStoring.toCharArray();
                for(int i = 0 ; i <= stored.length ; i++)
                {
                    pom2 = pom2 + stored[i];
                    if(stored[i] == c)
                    {
                        mList.add(pom2);
                        pom2 = "";
                    }
                }
                mList.pop();//isfrli go najstariot report
                forStoring = "";
                while(mList.pop() != null)
                {
                    forStoring = forStoring + mList.pop();
                }
            }catch (Exception e)
            {
                if(e.getMessage() != null) {
                    Log.d(TAG, e.getMessage());
                }
            }
        }
        forStoring = forStoring + reportString;

        editor.putString("storedReportString", forStoring);
        editor.apply();//apply namesto commit za da bide vo pozadina(background)
        counter ++;
        storedReports = true;
        Log.d(TAG, "stored report string is: " + forStoring);
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
}
