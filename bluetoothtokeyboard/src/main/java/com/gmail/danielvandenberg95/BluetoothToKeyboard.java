package com.gmail.danielvandenberg95;

class BluetoothToKeyboard {
    public static void main(String[] args) {
        System.out.println("Starting BluetoothToKeyboard");
        final AcceptThread acceptThread = new AcceptThread();
        acceptThread.start();
        new SystemTrayController(acceptThread).start();
    }
}
