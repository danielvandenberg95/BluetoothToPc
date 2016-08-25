package com.gmail.dvandenberg95.bluetoothhidtest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.gmail.dvandenberg95.bluetoothhidtest.service.BluetoothService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private BluetoothService.BluetoothServiceBinder myServiceBinder;
    public ServiceConnection myConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder binder) {
            myServiceBinder = ((BluetoothService.BluetoothServiceBinder) binder);
            Log.d("ServiceConnection", "connected");
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.d("ServiceConnection", "disconnected");
            myServiceBinder = null;
        }
    };

    public void doBindService() {
        Intent intent = new Intent(this, BluetoothService.class);
        bindService(intent, myConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        if (myServiceBinder == null) {
            doBindService();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (myServiceBinder != null) {
            unbindService(myConnection);
            myServiceBinder = null;
        }
        super.onPause();
    }

    public void error(final String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(MainActivity.this).setTitle(s).show();
            }
        });
    }

    public void serviceTest(View view) {
        if (myServiceBinder == null) {
            error("Service not bound…");
            return;
        }
        myServiceBinder.sendString("Test string");
    }
}
