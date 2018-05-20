package com.example.dell.sleepcare.Bluetooth;

public class BleDevice {
    private String macAddr, name;

    public String getMacAddr() {
        return macAddr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMacAddr(String macAddr) {
        this.macAddr = macAddr;
    }
}

