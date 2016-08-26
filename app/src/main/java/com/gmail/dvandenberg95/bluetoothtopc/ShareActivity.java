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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        textToSend = getIntent().getStringExtra(Intent.EXTRA_TEXT);
    }

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
