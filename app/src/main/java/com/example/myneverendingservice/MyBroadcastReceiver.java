package com.example.myneverendingservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import static android.content.ContentValues.TAG;

public class MyBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "MyBroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {

        String broadcast = "";
        try {
            broadcast = intent.getAction().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        switch (broadcast){
            case "ACTION_BOOT_COMPLETED" :
                Log.d(TAG, "Service restarted after successful boot");
                Toast.makeText(context, "Service restarted after successful boot", Toast.LENGTH_LONG).show();
                startServiceFromReceiver(context);
                break;
            case "SCREEN_ON" :
                Log.d(TAG, "Service restarted");
                Toast.makeText(context, "Service restarted", Toast.LENGTH_LONG).show();
                startServiceFromReceiver(context);
                break;
            case "ACTION_POWER_CONNECTED" :
                Log.d(TAG, "ACTION_POWER_CONNECTED ");
                Toast.makeText(context, "ACTION_POWER_CONNECTED ", Toast.LENGTH_LONG).show();
                startServiceFromReceiver(context);
                break;

                default:
                    startServiceFromReceiver(context);

        }
        /*
        Log.d(TAG, "Service restarted after successful boot");
        Toast.makeText(context, "Service restarted after successful boot", Toast.LENGTH_LONG).show();

        Intent i = new Intent(context, MainActivity.class);
        context.startService(i);
        */

        /*
        Intent secondServiceIntent = new Intent(context, ExampleService.class);
        ContextCompat.startForegroundService(context, secondServiceIntent);
        */
    }

    private void startServiceFromReceiver(Context context) {
        Log.d(TAG, "Service restarted after successful boot");
        Toast.makeText(context, "Service restarted after successful boot", Toast.LENGTH_LONG).show();

        Intent i = new Intent(context, MainActivity.class);
        context.startService(i);

    }
}
