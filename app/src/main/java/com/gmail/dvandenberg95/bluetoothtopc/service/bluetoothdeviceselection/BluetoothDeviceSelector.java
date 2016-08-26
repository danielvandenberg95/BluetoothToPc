package com.gmail.dvandenberg95.bluetoothtopc.service.bluetoothdeviceselection;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Daniel on 25/8/2016.
 */
public class BluetoothDeviceSelector {

    private final Object synchronisation = new Object();

    public BluetoothDevice getBluetoothDevice(Context context) {
        if (BluetoothDeviceContainer.bluetoothDevice != null){
            return BluetoothDeviceContainer.bluetoothDevice;
        }
        synchronized (synchronisation) {
            if (BluetoothDeviceContainer.bluetoothDevice != null){
                return BluetoothDeviceContainer.bluetoothDevice;
            }

            findBluetoothDevice(context);
            try {
                synchronized (BluetoothDeviceContainer.waitable) {
                    BluetoothDeviceContainer.waitable.wait();
                }
            } catch (InterruptedException ignored) {
            }

            return BluetoothDeviceContainer.bluetoothDevice;
        }
    }

    public static class BluetoothDeviceContainer{
        public static BluetoothDevice bluetoothDevice;
        public static final Object waitable = new Object();
    }

    private void findBluetoothDevice(Context context) {
        Intent intent = new Intent(context,DeviceSelector.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
