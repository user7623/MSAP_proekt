package com.example.myneverendingservice;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;
import static com.example.myneverendingservice.App.CHANNEL_ID;

public class ExampleService extends Service {

    private int counter = 0;
    Context ct;
    Boolean isConnected = false,
            isWiFi = false,
            isMobile = false;
    //TODO: VNESI ADRESA ZA host !!!
    String host = "google.com";

    @Override
    public void onCreate() {
        ct = this;
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "starting Service !!");
        counter = 0;
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Service")
                .setContentText("background service is started")
                .setSmallIcon(R.drawable.ic_android)
                .setContentIntent(pendingIntent)
                .build();
        Log.i(TAG, "starting foreground");
        startForeground(1, notification);
        startTimer();
        //startPingService();

        return START_NOT_STICKY;
    }

    private void startPingService() {
        //Intent serviceI = new Intent(this, ping.class);
        //ContextCompat.startForegroundService(this, serviceIntent);
        //startService(serviceI);
        Intent serviceI = new Intent(this, ping.class);
        startService(serviceI);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy called");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.i(TAG, "onTaskRemoved called");
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
        Log.i(TAG, "Starting timer");
        stoptimertask();
        timer = new Timer();

        initializeTimerTask();

        Log.i(TAG, "Scheduling...");
        timer.schedule(timerTask, 1000, 1000); //
    }

    public void initializeTimerTask() {
        Log.i(TAG, "initialising TimerTask");
        timerTask = new TimerTask() {
            public void run() {
                Log.i("in timer", "in timer ++++  " + (counter++));
                //ako pominale x sekundi povikaj ping
                if(counter % 10 == 0)
                {
                    startPingService();
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

}