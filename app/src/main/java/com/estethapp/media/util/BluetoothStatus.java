package com.estethapp.media.util;

/**
 * Created by Irfan Ali on 1/16/2017.
 */
public class BluetoothStatus {

    public static BluetoothStatus instance ;

    public static BluetoothStatus getInstance(){
        if(instance ==null){
            instance = new BluetoothStatus();
        }
        return instance;
    }

    public boolean isDeviceConnected;
    public boolean isSCODeviceStart;
    public boolean isDeviceDisconnected;

    public boolean isDeviceConnected() {
        return isDeviceConnected;
    }

    public boolean isDeviceDisconnected() {
        return isDeviceDisconnected;
    }

    public void setDeviceConnected(boolean deviceConnected) {
        isDeviceConnected = deviceConnected;
    }

    public void setDeviceDisconnected(boolean deviceDisconnected){
        isDeviceDisconnected = deviceDisconnected;
    }

    public boolean isSCODeviceStart() {
        return isSCODeviceStart;
    }

    public void setSCODeviceStart(boolean SCODeviceStart) {
        isSCODeviceStart = SCODeviceStart;
    }
}
