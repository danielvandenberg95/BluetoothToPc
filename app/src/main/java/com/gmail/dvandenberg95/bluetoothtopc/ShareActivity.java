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

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.gmail.dvandenberg95.bluetoothtopc.service.BluetoothService;

/**
 * Created by Daniel on 26/8/2016.
 */
public class ShareActivity extends Activity {
    private String textToSend = null;
    private BluetoothService.BluetoothServiceBinder myServiceBinder;
    private final ServiceConnection myConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder binder) {
            myServiceBinder = ((BluetoothService.BluetoothServiceBinder) binder);
            Log.d("ServiceConnection", "connected");
            myServiceBinder.sendString(textToSend);
            textToSend = null;
            Toast.makeText(ShareActivity.this,"Sending text via Bluetooth",Toast.LENGTH_LONG).show();
            finish();
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.d("ServiceConnection", "disconnected");
            myServiceBinder = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        textToSend = getIntent().getStringExtra(Intent.EXTRA_TEXT);
    }

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
}
