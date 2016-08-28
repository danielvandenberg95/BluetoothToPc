/*
 * Copyright (c) 2016. Daniël van den Berg.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * The software is provided "as is", without warranty of any kind, express
 * or implied, including but not limited to the warranties of
 * merchantability, fitness for a particular purpose, title and
 * non-infringement. In no event shall the copyright holders or anyone
 * distributing the software be liable for any damages or other liability,
 * whether in contract, tort or otherwise, arising from, out of or in
 * connection with the software or the use or other dealings in the
 * software.
 */

package com.gmail.dvandenberg95.bluetoothtopc;

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

import com.gmail.dvandenberg95.bluetoothtopc.service.BluetoothService;

public class MainActivity extends AppCompatActivity {

    private BluetoothService.BluetoothServiceBinder myServiceBinder;
    private final ServiceConnection myConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder binder) {
            myServiceBinder = ((BluetoothService.BluetoothServiceBinder) binder);
            Log.d("ServiceConnection", "connected");
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.d("ServiceConnection", "disconnected");
            myServiceBinder = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void doBindService() {
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

    private void showAlertDialog(final String string) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(MainActivity.this).setTitle(string).show();
            }
        });
    }

    public void serviceTest(@SuppressWarnings("UnusedParameters") View view) {
        if (myServiceBinder == null) {
            showAlertDialog("Service not bound…");
            return;
        }
        myServiceBinder.sendString("Test string");
    }
}
