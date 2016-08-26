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
class BluetoothConnection extends Thread{
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
            Log.d("BlueTooth","Connected.");
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

    public void close(){
        try {
            getOutputStream().write('\4');
            rfcommSocketToServiceRecord.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
