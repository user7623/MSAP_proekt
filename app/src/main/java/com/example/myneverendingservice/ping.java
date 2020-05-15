package com.example.myneverendingservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

public class ping extends Service {
    Boolean isConnected = false,
            isWiFi = false,
            isMobile = false;
    private String date;
    private String host;
    private int count;
    private int packetSize;
    private int jobPeriod;
    private String report = "result: ";
    //public boolean connection;
    public ping() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "starting ping check!");
        startTimer();
        makePing();

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent orderIntent = intent;

        date = orderIntent.getStringExtra("date");
        host = orderIntent.getStringExtra("host");
        count = orderIntent.getIntExtra("count" , 1);
        packetSize = orderIntent.getIntExtra("packetSize" , 1);
        jobPeriod = orderIntent.getIntExtra("jobPeriod" , 1);

        //proverka dali se ok preneseni
        Log.d(TAG, "date:" + date);
        Log.d(TAG, "host:" + host);
        Log.d(TAG, "packet size:" + packetSize);
        Log.d(TAG, "job period: " + jobPeriod);
        onCreate();
        return START_NOT_STICKY;
    }

    private void makePing() {
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
                Log.i(TAG, "connection via wifi");
                if (isConnectedToThisServer(host)) {
                    Log.i(TAG, "Connected to host");
                } else {
                    Log.i(TAG, "unable to connect to host");
                    sendReport();
                }
            }


            if (isMobile) {

                Log.i(TAG, "connection via mobile data");
                if (isConnectedToThisServer(host)) {
                    Log.i(TAG, "Connected to host");
                } else {
                    Log.i(TAG, "unable to connect to host");
                    sendReport();
                }
            }
        } else {
            Toast.makeText(this, "No Network", Toast.LENGTH_SHORT).show();
        }

    }

    public boolean isConnectedToThisServer(String host) {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -s " + packetSize + " -c " + count + " " + host);
            int exitValue = ipProcess.waitFor();

            BufferedReader in = new BufferedReader( new InputStreamReader(ipProcess.getInputStream()));
            String inputLine;

            while((inputLine = in.readLine())!= null){
                //napolni go report so povratnite podatoci
                report = report + inputLine;
                Log.i(TAG, inputLine);
            }

            report = report + "; " + "`"; // znakot ( ` ) go koristam za pokasno da mozam da izdvojuvam posebi poraki

            return (exitValue == 0);
        }catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
        return false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private static Timer timer;
    private static TimerTask timerTask;
    long oldTime = 0;

    public void startTimer() {
        stoptimertask();
        timer = new Timer();
        initializeTimerTask();

        timer.schedule(timerTask, 1000, 1000); //
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                try {
                    Thread.sleep(jobPeriod * 100);
                        if(jobPeriod > 0)
                        {
                            makePing();
                        }
                        try {
                            sendReport();
                        }catch (Exception e)
                        {
                            Log.d(TAG, e.getMessage());
                        }
                }catch (Exception e)
                {
                    Log.d(TAG, e.getMessage());
                }
            }
       };
    }

    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    private void sendReport()
    {
        Intent reportIntent = new Intent(this, reportPing.class);
        reportIntent.putExtra("report", report);
        startService(reportIntent);
    }
}
