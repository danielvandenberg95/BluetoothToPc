package com.gmail.danielvandenberg95;

import java.awt.AWTException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

/**
 * Created by Daniel on 24/8/2016.
 */
class AcceptThread extends Thread {
    private final UUID uuid = new UUID(                              //the uid of the service, it has to be unique,
            "57669b2fd0d64df39447fbc2381cba19", false); //it can be generated randomly
    private final String name = "Echo Server";                       //the name of the service
    private final String url = "btspp://localhost:" + uuid         //the service url
            + ";name=" + name
            + ";authenticate=true;encrypt=true;";
    private StreamConnectionNotifier server = null;
    private final Keyboard keyboard;
    private boolean running = true;
    private final Set<WeakReference<ConnectionHandler>> connectionHandlers = new HashSet<>();

    public AcceptThread() {
        Keyboard tmp = null;
        try {
            tmp = new Keyboard();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        keyboard = tmp;

        if (keyboard == null) {
            throw new RuntimeException("Could not take control of the keyboard.");
        }
    }

    public void run() {

        // The local server socket
        System.out.println("Setting device to be discoverable...");
        try {
            LocalDevice local = LocalDevice.getLocalDevice();
            local.setDiscoverable(DiscoveryAgent.GIAC);
            System.out.println("Start advertising service...");
            server = (StreamConnectionNotifier) Connector.open(url);
            System.out.println("Waiting for incoming connection...");
            while (running) {
                final ConnectionHandler connectionHandler = new ConnectionHandler(server.acceptAndOpen());
                System.out.println("Client Connected...");
                connectionHandler.start();
                connectionHandlers.add(new WeakReference<>(connectionHandler));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("System stopped.");

    }

    public void exit() {
        running = false;
        interrupt();
        if (server != null) {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (WeakReference<ConnectionHandler> connectionHandler : connectionHandlers){
            final ConnectionHandler handler = connectionHandler.get();
            if (handler == null){
                continue;
            }
            handler.interrupt();
        }
    }

    private class ConnectionHandler extends Thread {
        private final Timer timer = new Timer();
        private boolean stopping = false;
        private InputStream din;

        public ConnectionHandler(StreamConnection streamConnection) {
            try {
                din = (streamConnection.openInputStream());
            } catch (IOException e) {
                e.printStackTrace();
                stopping = true;
            }
            reschedule();
        }

        public void run() {
            try {
                while (running && !stopping)
                {
                    StringBuilder cmd = new StringBuilder();
                    System.out.println("Receiving...");
                    char tmpChar;
                    while (((tmpChar = (char) din.read()) > 0) && (tmpChar != '\n') && (tmpChar != '\4')) {
                        cmd.append(tmpChar);
                    }
                    if (tmpChar == '\4'){
                        System.out.println("Connection closed.");
                        return;
                    }
                    String command = cmd.toString();
                    System.out.println("Received " + command);
                    reschedule();
                    keyboard.type(command);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void reschedule() {
            timer.purge();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    System.out.println("Stopping connection...");
                    stopping = true;
                    try {
                        din.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            },10000);
        }
    }
}