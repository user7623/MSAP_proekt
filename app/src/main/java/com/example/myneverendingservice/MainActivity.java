package com.example.myneverendingservice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {

    private boolean serviceRunning = false;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //setContentView(R.layout.activity_main);
            startService();
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