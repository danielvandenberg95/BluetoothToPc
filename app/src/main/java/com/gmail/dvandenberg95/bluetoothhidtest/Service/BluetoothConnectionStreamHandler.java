package com.gmail.dvandenberg95.bluetoothhidtest.service;

import android.app.Activity;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Daniel on 23/8/2016.
 */
public class BluetoothConnectionStreamHandler implements Runnable {
    private final BluetoothConnection bluetoothConnection;
    private final TextView textView;
    private boolean running = true;

    public BluetoothConnectionStreamHandler(BluetoothConnection bluetoothConnection, TextView textView) {
        this.bluetoothConnection = bluetoothConnection;
        this.textView = textView;
    }

    @Override
    public void run() {
        final InputStream inputStream = bluetoothConnection.getInputStream();
        //final OutputStream outputStream = bluetoothConnection.getOutputStream();
        while (running) {
            try {
                final int read = inputStream.read();
                if (read == -1){
                    return;
                }
                if (textView != null) {
                    if (textView.getContext() instanceof Activity){
                        ((Activity)textView.getContext()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append(textView.getText());
                                stringBuilder.append((char)read);
                                textView.setText(stringBuilder);
                            }
                        });
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    public void stopRunning(){
        running = false;
    }
}
