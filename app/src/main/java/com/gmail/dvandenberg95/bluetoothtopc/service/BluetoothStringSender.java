package com.gmail.dvandenberg95.bluetoothtopc.service;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.gmail.dvandenberg95.bluetoothtopc.service.bluetoothdeviceselection.BluetoothDeviceSelector;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Daniel on 25/8/2016.
 */
class BluetoothStringSender {
    public static final UUID MY_UUID = UUID.fromString("57669b2f-d0d6-4df3-9447-fbc2381cba19");

    private BluetoothDevice selectedBluetoothDevice = null;
    private BluetoothConnection bluetoothConnection;

    public void sendString(final String string, final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (selectedBluetoothDevice == null){
                    scan(context);
                }

                bluetoothConnection = null;
                connect();

                if (bluetoothConnection == null){
                    new RuntimeException("Couldn't get a bluetooth connection...").printStackTrace();
                    return;
                }
                try {
                    Thread.sleep(250);//For stability, seems to be required in order to make sure the sent data actually arrives.
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    bluetoothConnection.getOutputStream().write((string+'\n').getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bluetoothConnection.close();
            }
        }).start();
    }

    private void scan(Context context) {
        selectedBluetoothDevice = new BluetoothDeviceSelector().getBluetoothDevice(context);
    }

    private void connect() {
        if (selectedBluetoothDevice == null) {
            return;
        }

        if (bluetoothConnection != null && bluetoothConnection.isConnected()) {
            bluetoothConnection.close();
        }

        bluetoothConnection = new BluetoothConnection(selectedBluetoothDevice);
        bluetoothConnection.run();
    }
}
