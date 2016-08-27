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

package com.gmail.dvandenberg95.bluetoothtopc.service;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.gmail.dvandenberg95.bluetoothtopc.service.bluetoothdeviceselection.BluetoothDeviceSelector;

import java.io.IOException;
import java.util.UUID;

class BluetoothStringSender {
    public static final UUID MY_UUID = UUID.fromString("57669b2f-d0d6-4df3-9447-fbc2381cba19");

    private BluetoothDevice selectedBluetoothDevice = null;
    private BluetoothConnection bluetoothConnection;

    public void sendString(final String string, final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (selectedBluetoothDevice == null) {
                    scan(context);
                }

                bluetoothConnection = null;
                connect();

                if (bluetoothConnection == null) {
                    new RuntimeException("Couldn't get a bluetooth connection...").printStackTrace();
                    return;
                }
                try {
                    Thread.sleep(250);//For stability, seems to be required in order to make sure the sent data actually arrives.
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    bluetoothConnection.getOutputStream().write((string + '\n').getBytes());
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
