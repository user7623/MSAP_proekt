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

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {
    Boolean isConnected = false,
            isWiFi = false,
            isMobile = false;
    boolean flag = false;
    String host = "10.0.2.2";
    private boolean serviceRunning = false;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //setContentView(R.layout.activity_main);
            startService();
            //makePing();

            finish();
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

    }