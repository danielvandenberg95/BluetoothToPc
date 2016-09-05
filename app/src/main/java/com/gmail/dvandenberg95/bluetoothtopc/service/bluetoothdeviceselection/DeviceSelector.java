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

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.gmail.dvandenberg95.bluetoothtopc.MainActivity;
import com.gmail.dvandenberg95.bluetoothtopc.R;

import java.util.Set;

/**
 * Created by Daniel on 25/8/2016.
 */
public class DeviceSelector extends Activity {
    private static final int RESULT_CODE_ENABLE_BLUETOOTH = 4397553;


    private final DeviceSpinnerItemSelectedListener deviceSpinnerItemSelectedListener = new DeviceSpinnerItemSelectedListener();
    private BondedDeviceAdapter bondedDeviceAdapter;
    private boolean askedToEnable = false;
    private SharedPreferences defaultSharedPreferences;
    private boolean success = false;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                if (state == BluetoothAdapter.STATE_ON) {
                    run();

                }
            }
        }
    };

    public void connect(@SuppressWarnings("UnusedParameters") View view) {
        success = true;
        notifyContainer();
        finish();
    }

    private void notifyContainer() {
        synchronized (BluetoothDeviceSelector.BluetoothDeviceContainer.waitable) {
            if (!success) {
                BluetoothDeviceSelector.BluetoothDeviceContainer.bluetoothDevice = null;
            }
            BluetoothDeviceSelector.BluetoothDeviceContainer.waitable.notifyAll();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.service_select_device);
        super.onCreate(savedInstanceState);
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        run();
    }

    private void run() {
        final BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!defaultAdapter.isEnabled()) {
            if (!defaultSharedPreferences.getBoolean(MainActivity.SHARED_PREFERENCES_AUTO_ENABLE_BLUETOOTH, false)) {
                if (askedToEnable) {
                    notifyContainer();
                    return;
                }
                askedToEnable = true;
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, RESULT_CODE_ENABLE_BLUETOOTH);
            } else {
                defaultAdapter.enable();
                while (defaultAdapter.getState() == BluetoothAdapter.STATE_TURNING_ON) {
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (!defaultAdapter.isEnabled()) {
                    return;
                }
            }
        }

        final Set<BluetoothDevice> bondedDevices = defaultAdapter.getBondedDevices();
        Spinner deviceSpinner = (Spinner) findViewById(R.id.deviceSpinner);
        BluetoothDevice[] bondedDeviceArray = new BluetoothDevice[0];
        bondedDeviceArray = bondedDevices.toArray(bondedDeviceArray);
        bondedDeviceAdapter = new BondedDeviceAdapter(this, bondedDeviceArray);
        deviceSpinner.setAdapter(bondedDeviceAdapter);
        deviceSpinner.setOnItemSelectedListener(deviceSpinnerItemSelectedListener);
    }

    @Override
    protected void onDestroy() {
        notifyContainer();
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_CODE_ENABLE_BLUETOOTH) {
            run();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void openSettings(@SuppressWarnings("UnusedParameters") View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private class BondedDeviceAdapter extends ArrayAdapter<BluetoothDevice> {
        private final BluetoothDevice[] bondedDevices;

        public BondedDeviceAdapter(Context context, BluetoothDevice[] bondedDevices) {
            super(context, android.R.layout.simple_list_item_1, bondedDevices);
            this.bondedDevices = bondedDevices;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = (TextView) super.getView(position, convertView, parent);
            textView.setText(bondedDevices[position].getName());
            return textView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView textView = (TextView) super.getView(position, convertView, parent);
            textView.setText(bondedDevices[position].getName());
            return textView;
        }
    }

    private class DeviceSpinnerItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            BluetoothDeviceSelector.BluetoothDeviceContainer.bluetoothDevice = bondedDeviceAdapter.getItem(i);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            BluetoothDeviceSelector.BluetoothDeviceContainer.bluetoothDevice = null;
        }
    }
}
