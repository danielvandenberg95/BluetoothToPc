package com.gmail.dvandenberg95.bluetoothhidtest;

import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Daniel on 23/8/2016.
 */
public class BluetoothConnectionStreamHandler implements Runnable {
    private final BluetoothConnection bluetoothConnection;
    private final TextView textView;
    private final MainActivity mainActivity;

    public BluetoothConnectionStreamHandler(BluetoothConnection bluetoothConnection, TextView textView, MainActivity mainActivity) {
        this.bluetoothConnection = bluetoothConnection;
        this.textView = textView;
        this.mainActivity = mainActivity;
    }

    @Override
    public void run() {
        final InputStream inputStream = bluetoothConnection.getInputStream();
        final OutputStream outputStream = bluetoothConnection.getOutputStream();
        while (!mainActivity.isDestroyed()) {
            try {
                final int read = inputStream.read();
                if (read == -1){
                    return;
                }
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(textView.getText());
                        stringBuilder.append((char)read);
                        textView.setText(stringBuilder);
                    }
                });
                outputStream.write(read);
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
