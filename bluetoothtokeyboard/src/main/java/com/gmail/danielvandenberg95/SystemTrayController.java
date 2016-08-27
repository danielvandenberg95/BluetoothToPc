package com.gmail.danielvandenberg95;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Created by Daniel on 25/8/2016.
 */
class SystemTrayController extends Thread {
    private static final String ICON_STRING = "P";
    private final AcceptThread acceptThread;

    public SystemTrayController(AcceptThread acceptThread) {
        this.acceptThread = acceptThread;
    }

    @Override
    public void run() {
        if (!SystemTray.isSupported()){
            return;
        }
        final SystemTray systemTray = SystemTray.getSystemTray();

        final PopupMenu popup = new PopupMenu();
        final TrayIcon trayIcon = new TrayIcon(createImage());

        popup.add("BluetoothToKeyboard");
        MenuItem menuItem = new MenuItem("Quit");
        menuItem.addActionListener(actionEvent -> {
            acceptThread.exit();
            systemTray.remove(trayIcon);
            System.exit(0);
        });
        popup.add(menuItem);

        trayIcon.setPopupMenu(popup);
        trayIcon.setImageAutoSize(true);

        try {
            systemTray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
        }
    }

    private static Image createImage() {
        BufferedImage img = new BufferedImage(16, 16,
                BufferedImage.TYPE_INT_RGB);
        final Graphics2D graphics = img.createGraphics();
        graphics.setColor(Color.GREEN);
        graphics.fillRect(0,0,img.getWidth(),img.getHeight());
        graphics.setColor(Color.BLACK);
        final FontMetrics fontMetrics = graphics.getFontMetrics();
        final Rectangle2D stringBounds = fontMetrics.getStringBounds(ICON_STRING, graphics);
        graphics.drawString(ICON_STRING,(int)(img.getWidth()-stringBounds.getWidth())/2,(int)(img.getHeight()-stringBounds.getHeight())/2 + fontMetrics.getAscent());
        graphics.drawImage(img,null,0,0);
        return img;
    }
}
