package com.gmail.dvandenberg95.bluetoothhidtest.service.bluetoothdeviceselection;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.gmail.dvandenberg95.bluetoothhidtest.R;

import java.util.Set;

/**
 * Created by Daniel on 25/8/2016.
 */
public class DeviceSelector extends Activity {
    private static final int RESULT_CODE_ENABLE_BLUETOOTH = 4397553;


    private DeviceSpinnerItemSelectedListener deviceSpinnerItemSelectedListener = new DeviceSpinnerItemSelectedListener();
    private BondedDeviceAdapter bondedDeviceAdapter;
    private boolean askedToEnable = false;

    public void connect(View view) {
        notifyContainer();
        finish();
    }

    private void notifyContainer() {
        synchronized (BluetoothDeviceSelecter.BluetoothDeviceContainer.waitable) {
            BluetoothDeviceSelecter.BluetoothDeviceContainer.waitable.notifyAll();
        }
    }

    private class BondedDeviceAdapter extends ArrayAdapter<BluetoothDevice> {
        private final BluetoothDevice[] bondedDevices;

        public BondedDeviceAdapter(Context context, BluetoothDevice[] bondedDevices) {
            super(context,android.R.layout.simple_list_item_1,bondedDevices);
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
            BluetoothDeviceSelecter.BluetoothDeviceContainer.bluetoothDevice = bondedDeviceAdapter.getItem(i);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            BluetoothDeviceSelecter.BluetoothDeviceContainer.bluetoothDevice = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.service_select_device);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        run();
    }

    private void run() {
        final BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();

        if(!defaultAdapter.isEnabled())
        {
            if (askedToEnable){
                notifyContainer();
                return;
            }
            askedToEnable = true;
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth,RESULT_CODE_ENABLE_BLUETOOTH);
        }

        final Set<BluetoothDevice> bondedDevices = defaultAdapter.getBondedDevices();
        Spinner deviceSpinner = (Spinner) findViewById(R.id.deviceSpinner);
        BluetoothDevice[] bondedDeviceArray = new BluetoothDevice[0];
        bondedDeviceArray = bondedDevices.toArray(bondedDeviceArray);
        bondedDeviceAdapter = new BondedDeviceAdapter(this,bondedDeviceArray);
        deviceSpinner.setAdapter(bondedDeviceAdapter);
        deviceSpinner.setOnItemSelectedListener(deviceSpinnerItemSelectedListener);
    }

    @Override
    protected void onDestroy() {
        notifyContainer();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_CODE_ENABLE_BLUETOOTH){
            run();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
