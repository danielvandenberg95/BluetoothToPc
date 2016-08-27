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
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Daniel on 23/8/2016.
 */
class BluetoothConnection extends Thread {
    private final BluetoothSocket rfcommSocketToServiceRecord;

    public BluetoothConnection(BluetoothDevice selectedBluetoothDevice) {
        UUID uuid = (BluetoothStringSender.MY_UUID); //Standard SerialPortService ID
        BluetoothSocket tmp = null;
        try {
            tmp = selectedBluetoothDevice.createRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }
        rfcommSocketToServiceRecord = tmp;
    }

    public void run() {
        try {
            rfcommSocketToServiceRecord.connect();
            Log.d("BlueTooth", "Connected.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return rfcommSocketToServiceRecord.isConnected();
    }

    public OutputStream getOutputStream() {
        try {
            return rfcommSocketToServiceRecord.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void close() {
        try {
            getOutputStream().write('\4');
            rfcommSocketToServiceRecord.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
