package com.gmail.dvandenberg95.bluetoothhidtest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public static final UUID MY_UUID = UUID.fromString("57669b2f-d0d6-4df3-9447-fbc2381cba19");//UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private DeviceSpinnerItemSelectedListener deviceSpinnerItemSelectedListener = new DeviceSpinnerItemSelectedListener();
    private BondedDeviceAdapter bondedDeviceAdapter;
    private BluetoothDevice selectedBluetoothDevice = null;
    private BluetoothConnection bluetoothConnection;
    private Runnable onConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void scan(View view) {
        final BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();

        if(!defaultAdapter.isEnabled())
        {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        final Set<BluetoothDevice> bondedDevices = defaultAdapter.getBondedDevices();
        Spinner deviceSpinner = (Spinner) findViewById(R.id.deviceSpinner);
        BluetoothDevice[] bondedDeviceArray = new BluetoothDevice[0];
        bondedDeviceArray = bondedDevices.toArray(bondedDeviceArray);
        bondedDeviceAdapter = new BondedDeviceAdapter(bondedDeviceArray);
        deviceSpinner.setAdapter(bondedDeviceAdapter);
        deviceSpinner.setOnItemSelectedListener(deviceSpinnerItemSelectedListener);
    }

    public void connect(View view) {
        if (selectedBluetoothDevice == null) {
            error("No device selected.");
            return;
    }
        bluetoothConnection = new BluetoothConnection(MainActivity.this,selectedBluetoothDevice);
        onConnected = new BluetoothConnectionStreamHandler(bluetoothConnection,(TextView)findViewById(R.id.outputTextView), this);
        bluetoothConnection.setOnConnected(onConnected);
        bluetoothConnection.start();/*
        if (bluetoothConnection.isConnected()) {
            error("Connected successfully");
        }*/
    }

    public void error(final String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(MainActivity.this).setTitle(s).show();
            }
        });
    }

    public void sendTestData(View view) {
        try {
            bluetoothConnection.getOutputStream().write("Hello world!\n".getBytes());
            error("Test data sent");
        } catch (IOException e) {
            e.printStackTrace();
            error("Test data couldn't be sent");
        }
    }

    private class BondedDeviceAdapter extends ArrayAdapter<BluetoothDevice> {
        private final BluetoothDevice[] bondedDevices;

        public BondedDeviceAdapter(BluetoothDevice[] bondedDevices) {
            super(MainActivity.this,android.R.layout.simple_list_item_1,bondedDevices);
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
            selectedBluetoothDevice = bondedDeviceAdapter.getItem(i);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
}
