package com.gmail.dvandenberg95.bluetoothhidtest.service;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Daniel on 23/8/2016.
 */
public class BluetoothConnection extends Thread{
    private final BluetoothSocket rfcommSocketToServiceRecord;
    private Runnable onConnected;

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
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        if (onConnected != null) {
            onConnected.run();
        }
    }

    public boolean isConnected() {
        return rfcommSocketToServiceRecord.isConnected();
    }

    public InputStream getInputStream(){
        try {
            return rfcommSocketToServiceRecord.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setOnConnected(Runnable onConnected) {
        this.onConnected = onConnected;
    }

    public OutputStream getOutputStream() {
        try {
            return rfcommSocketToServiceRecord.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void close(){
        try {
            getOutputStream().write('\4');
            rfcommSocketToServiceRecord.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
