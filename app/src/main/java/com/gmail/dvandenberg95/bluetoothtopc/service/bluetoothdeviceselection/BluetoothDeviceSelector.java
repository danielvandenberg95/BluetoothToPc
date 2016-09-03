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
        if (BluetoothDeviceContainer.bluetoothDevice != null) {
            return BluetoothDeviceContainer.bluetoothDevice;
        }
        synchronized (synchronisation) {
            if (BluetoothDeviceContainer.bluetoothDevice != null) {
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

    private void findBluetoothDevice(Context context) {
        Intent intent = new Intent(context, DeviceSelector.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static class BluetoothDeviceContainer {
        public static final Object waitable = new Object();
        public static BluetoothDevice bluetoothDevice;
    }
}
