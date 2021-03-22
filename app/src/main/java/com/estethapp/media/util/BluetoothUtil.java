package com.estethapp.media.util;

/**
 * Created by Irfan Ali on 1/12/2017.
 */
public class BluetoothUtil {

    String deviceName;
    String Address;
    boolean isPaired;


    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public boolean isPaired() {
        return isPaired;
    }

    public void setPaired(boolean paired) {
        isPaired = paired;
    }
}
