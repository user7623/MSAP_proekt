package com.example.myneverendingservice;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.ContentValues.TAG;

@SuppressLint("Registered")
public class reportActivity extends Service {

    String reportString;
    boolean isConnected;
    URL url;
    HttpURLConnection con;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        reportString = intent.getStringExtra("report");

        Log.d(TAG, "received report string is :" + reportString);

        //dokolku e povrzano prati
        if(checkConnection())
        {
            sendReport();
        }
        else
        {
            writeToMem();
        }
        return START_NOT_STICKY;
    }

    public void sendReport()
    {
        try
        {
            url = new URL ("http://10.0.2.2:5000/postresults ");
        }catch (Exception e)
        {
            Log.d(TAG, e.getMessage());
        }
        try {
                con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json; utf-8");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);
                String jsonInputString = reportString;

            try(OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                Log.d(TAG, response.toString());
                System.out.println(response.toString());
            }

        }catch (Exception e)
        {
            Log.d(TAG, e.getMessage());
        }
    }
    public void writeToMem()
    {

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


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //setContentView(R.layout.activity_report);



    }
}
