package com.example.myneverendingservice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {
    private String date;
    private String host;
    private int count;
    private int packetSize;
    private int jobPeriod;
    private String jobType;
    private boolean serviceRunning = false;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //setContentView(R.layout.activity_main);
            startInfCiklus();
            startTimer();
            //startService();

            finish();
        }

    private void startInfCiklus() {
    }

    private void lookForJob() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:5000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        jobInterface jobinterface = retrofit.create(jobInterface.class);

        Call<List<Job>> call = jobinterface.getJobs();

        call.enqueue(new Callback<List<Job>>() {
            @Override
            public void onResponse(Call<List<Job>> call, Response<List<Job>> response) {
                //ako e zgresena adresata ili e ne e ulkucen backend
                if(!response.isSuccessful())
                {
                    String errorString = "Error" + response.code();
                    Log.d(TAG, errorString);
                }

                List<Job> jobs = response.body();

                for(Job job : jobs)
                {
                    date = job.getDate();
                    host = job.getHost();
                    count = job.getCount();
                    packetSize = job.getPacketSize();
                    jobPeriod = job.getJobPeriod();
                    jobType = job.getJobType();
                    //proveri go imeto na job-ot i povikaj soodvetna funk
                    if( jobType.equals("PING"))
                    {
                        callPing();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Job>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });

    }

    public void callPing()
        {
            Intent serviceI = new Intent(this, ping.class);

            serviceI.putExtra("date", date);
            serviceI.putExtra("host", host);
            serviceI.putExtra("count", count);
            serviceI.putExtra("packetSize", packetSize);
            serviceI.putExtra("jobPeriod", jobPeriod);
            Log.d(TAG, host);
            Log.d(TAG, date);
            Log.d(TAG, "" + count);
            startService(serviceI);
        }
        public void startService() {
                Log.d(TAG, "ProcessMainClass: start service go!!!!");
                Intent serviceIntent = new Intent(this, ExampleService.class);
                ContextCompat.startForegroundService(this, serviceIntent);
                //serviceRunning = true;
        }

        public void stopService() {
            Intent serviceIntent = new Intent(this, ExampleService.class);
            stopService(serviceIntent);
            //serviceRunning = false;
        }

        @Override
        public void finish() {
            super.finish();
        }
    private static Timer timer;
    private static TimerTask timerTask;
    long oldTime = 0;

    public void startTimer() {
        stoptimertask();
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 1000, 600000); //
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
               lookForJob();
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