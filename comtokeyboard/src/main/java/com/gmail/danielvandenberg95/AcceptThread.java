package com.gmail.danielvandenberg95;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

/**
 * Created by Daniel on 24/8/2016.
 */
public class AcceptThread extends Thread {
    public final UUID uuid = new UUID(                              //the uid of the service, it has to be unique,
            "57669b2fd0d64df39447fbc2381cba19", false); //it can be generated randomly
    public final String name = "Echo Server";                       //the name of the service
    public final String url = "btspp://localhost:" + uuid         //the service url
            + ";name=" + name
            + ";authenticate=true;encrypt=true;";
    LocalDevice local = null;
    StreamConnectionNotifier server = null;
    StreamConnection conn = null;
    final Robot robot;

    public AcceptThread() {
        Robot tmp = null;
        try {
            tmp = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        robot = tmp;

        if (robot == null){
            throw new RuntimeException("Could not take control of the keyboard.");
        }
    }

    public void run() {

        // The local server socket
        System.out.println("Setting device to be discoverable...");
        try {
            local = LocalDevice.getLocalDevice();
            local.setDiscoverable(DiscoveryAgent.GIAC);
            System.out.println("Start advertising service...");
            server = (StreamConnectionNotifier) Connector.open(url);
            System.out.println("Waiting for incoming connection...");
            conn = server.acceptAndOpen();
            System.out.println("Client Connected...");
            DataOutputStream dout = new DataOutputStream(conn.openOutputStream());
            dout.write("Hello from the server!".getBytes());
            dout.flush();
            System.out.println("Test data sent");
            InputStream din = (conn.openInputStream());
            while (true)

            {
                StringBuilder cmd = new StringBuilder();
                System.out.println("Receiving...");
                char tmpChar;
                while (((tmpChar = (char) din.read()) > 0) && (tmpChar != '\n')) {
                    cmd.append(tmpChar);
                }
                String command = cmd.toString();
                System.out.println("Received " + command);
                type(command);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("System stopped.");

    }

    public void type(CharSequence cs){
        for(int i=0;i<cs.length();i++){
            type(cs.charAt(i));
        }
    }

    public void type(char c){
        robot.keyPress(KeyEvent.VK_ALT);
        robot.keyPress(KeyEvent.VK_NUMPAD0);
        robot.keyRelease(KeyEvent.VK_NUMPAD0);
        String altCode=Integer.toString(c);
        for(int i=0;i<altCode.length();i++){
            c=(char)(altCode.charAt(i)+'0');
            robot.keyPress(c);
            robot.keyRelease(c);
        }
        robot.keyRelease(KeyEvent.VK_ALT);
    }
}