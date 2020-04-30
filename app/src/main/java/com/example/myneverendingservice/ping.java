package com.example.myneverendingservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static android.content.ContentValues.TAG;

public class ping extends Service {
    Boolean isConnected = false,
            isWiFi = false,
            isMobile = false;
    String host = "10.0.2.2";
    public ping() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "starting ping check!");
        makePing();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onCreate();
        return START_NOT_STICKY;
    }

    private void makePing() {
        // https://developer.android.com/training/monitoring-device-state/connectivity-monitoring
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        // True if network is available.
        if (activeNetwork != null) {

            // True if using WiFI
            isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;

            // True if using Mobile Data
            isMobile = activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;

            isConnected = activeNetwork.isConnectedOrConnecting();
        }

        if (isConnected) {
            if (isWiFi) {
                //Toast.makeText(this, "Yes, WiF", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "connection via wifi");
                // ping google server for testing purpose
                if (isConnectedToThisServer(host)) {
                    //Toast.makeText(this, "Yes, Connected to Google", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Connected to host");
                } else {
                    //Toast.makeText(this, "No Google Connection", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "unable to connect to host");
                }
            }


            if (isMobile) {

                //Toast.makeText(this, "Yes, Mobile", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "connection via mobile data");
                if (isConnectedToThisServer(host)) {
                    //Toast.makeText(this, "Yes, Connected to Google", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Connected to host");
                } else {
                    //Toast.makeText(this, "No Google Connection", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "unable to connect to host");
                }
            }
        } else {
            Toast.makeText(this, "No Network", Toast.LENGTH_SHORT).show();
        }

       /* try{
            reaktivator();
        }catch (Exception e){
            //Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.i(TAG, e.getMessage());
        }*/
    }

    private void reaktivator() {
        try{
            Thread.sleep(10000);
            makePing();
        }catch (Exception e){
            //Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.i(TAG, e.getMessage());
        }
    }


    // Function that uses ping, takes server name or ip as argument.
    public boolean isConnectedToThisServer(String host) {
        // https://stackoverflow.com/questions/3905358/how-to-ping-external-ip-from-java-android
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 " + host);
            int exitValue = ipProcess.waitFor();

            BufferedReader in = new BufferedReader( new InputStreamReader(ipProcess.getInputStream()));
            String inputLine;

            while((inputLine = in.readLine())!= null){
                //Toast.makeText(this, inputLine, Toast.LENGTH_SHORT).show();
                Log.i(TAG, inputLine);
            }

            return (exitValue == 0);
        }catch (Exception e) {
            //Toast.makeText(this, "Error: "+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            Log.i(TAG, e.getMessage());
        }
        /*catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        return false;
    }

}
