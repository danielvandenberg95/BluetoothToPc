package com.gmail.danielvandenberg95;

import java.awt.AWTException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Random;
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
    private final static Random random = new Random();
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
        private final int connectionId;
        private TimerTask timerTask;

        public ConnectionHandler(StreamConnection streamConnection) {
            connectionId = random.nextInt();
            try {
                din = (streamConnection.openInputStream());
            } catch (IOException e) {
                e.printStackTrace();
                stopping = true;
            }
            reschedule();
            System.out.println("["+connectionId+"] Started connection");
        }

        public void run() {
            try {
                while (running && !stopping)
                {
                    StringBuilder cmd = new StringBuilder();
                    System.out.println("["+connectionId+"] Waiting for text to be sent...");
                    char tmpChar;
                    while (((tmpChar = (char) din.read()) > 0) && (tmpChar != '\n') && (tmpChar != '\4')) {
                        cmd.append(tmpChar);
                    }
                    if (tmpChar == '\4'){
                        System.out.println("["+connectionId+"] Connection closed.");
                        stopTimerTask();
                        return;
                    }
                    String command = cmd.toString();
                    System.out.println("["+connectionId+"] Received a stream of text.");
                    reschedule();
                    keyboard.type(command);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void stopTimerTask() {
            if (timerTask != null){
                timerTask.cancel();
            }
        }

        private void reschedule() {
            stopTimerTask();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    System.out.println("[" + connectionId + "] Connection timed out.");
                    stopping = true;
                    try {
                        din.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            timer.schedule(timerTask,10000);
        }
    }
}