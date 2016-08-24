package com.gmail.dvandenberg95.bluetoothhidtest;

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
    private final BluetoothDevice selectedBluetoothDevice;
    private MainActivity mainActivity;
    private Runnable onConnected;

    public BluetoothConnection(MainActivity mainActivity, BluetoothDevice selectedBluetoothDevice) {
        this.mainActivity = mainActivity;
        this.selectedBluetoothDevice = selectedBluetoothDevice;
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
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
            mainActivity.error(e.getLocalizedMessage());
            return;
        }
        mainActivity.error("Connected successfully");
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
}
