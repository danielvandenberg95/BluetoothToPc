package com.gmail.dvandenberg95.bluetoothtopc.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Daniel on 25/8/2016.
 */
public class BluetoothService extends Service {
    private final Binder BINDER = new BluetoothServiceBinder();
    private final BluetoothStringSender bluetoothStringSender = new BluetoothStringSender();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return BINDER;
    }

    private void sendString(final String string){
        bluetoothStringSender.sendString(string, this);
    }

    public class BluetoothServiceBinder extends Binder {
        public void sendString(final String string){
            BluetoothService.this.sendString(string);
        }
    }
}
