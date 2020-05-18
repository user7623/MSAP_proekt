package com.example.myneverendingservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static android.content.ContentValues.TAG;

public class top extends Service {
    boolean connected = false;
    private String date;
    private String host;
    private int count;
    private int packetSize;
    private int jobPeriod;
    private String report = "RESULT: ";

    public top() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        checkConnection();

        if(connected)
        {
            doYourJob();
        }

    }

    public void doYourJob()
    {
        String returnString = null;
        String statResult = "";
        try {
            Process pstat = Runtime.getRuntime().exec("top -n 1");
            BufferedReader in = new BufferedReader(new InputStreamReader(pstat.getInputStream()));
            String inputLine;

            while (returnString == null || returnString.contentEquals("")) {
                returnString = in.readLine();
            }
            statResult += returnString +",";
            while ((inputLine = in.readLine()) != null) {
                inputLine += ";";
                statResult += inputLine;
            }
            in.close();
            if (pstat != null) {
                pstat.getOutputStream().close();
                pstat.getInputStream().close();
                pstat.getErrorStream().close();
            }
            Log.d(TAG,"statResult = " + statResult);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void checkConnection()
    {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        // True if network is available.
        if (activeNetwork != null) {
            connected = activeNetwork.isConnectedOrConnecting();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent orderIntent = intent;

        date = orderIntent.getStringExtra("date");
        host = orderIntent.getStringExtra("host");

        //proverka dali se dobro preneseni
        Log.d(TAG, "date:" + date);
        Log.d(TAG, "host:" + host);
        onCreate();
        return START_NOT_STICKY;
    }
}
