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

package com.gmail.danielvandenberg95;

import com.gmail.danielvandenberg95.runonstartup.RunOnStartup;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
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
import java.io.File;
import java.net.URISyntaxException;

/**
 * Created by Daniel on 25/8/2016.
 */
class SystemTrayController extends Thread {
    private static final String ICON_STRING = "P";
    private final AcceptThread acceptThread;

    public SystemTrayController(AcceptThread acceptThread) {
        this.acceptThread = acceptThread;
    }

    private static Image createImage() {
        BufferedImage img = new BufferedImage(16, 16,
                BufferedImage.TYPE_INT_RGB);
        final Graphics2D graphics = img.createGraphics();
        graphics.setColor(Color.GREEN);
        graphics.fillRect(0, 0, img.getWidth(), img.getHeight());
        graphics.setColor(Color.BLACK);
        final FontMetrics fontMetrics = graphics.getFontMetrics();
        final Rectangle2D stringBounds = fontMetrics.getStringBounds(ICON_STRING, graphics);
        graphics.drawString(ICON_STRING, (int) (img.getWidth() - stringBounds.getWidth()) / 2, (int) (img.getHeight() - stringBounds.getHeight()) / 2 + fontMetrics.getAscent());
        graphics.drawImage(img, null, 0, 0);
        return img;
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

        {
            try {
                RunOnStartup runOnStartup = new RunOnStartup(new File(SystemTrayController.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()));
                CheckboxMenuItem menuItem = new CheckboxMenuItem("Run on startup");
                menuItem.setState(runOnStartup.getRunOnStartup());
                menuItem.addItemListener(actionEvent -> {
                    final boolean newValue = !runOnStartup.getRunOnStartup();
                    runOnStartup.setRunOnStartup(newValue);
                    menuItem.setState(newValue);
                });
                popup.add(menuItem);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        {
            MenuItem menuItem;
            menuItem = new MenuItem("Quit");
            menuItem.addActionListener(actionEvent -> {
                acceptThread.exit();
                systemTray.remove(trayIcon);
                System.exit(0);
            });
            popup.add(menuItem);
        }

        trayIcon.setPopupMenu(popup);
        trayIcon.setImageAutoSize(true);

        try {
            systemTray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
        }
    }
}
