package com.estethapp.media.util;

import com.estethapp.media.fragment.PairingFragment;

/**
 * Created by Irfan Ali on 1/13/2017.
 */
public class SCODeviceHelper {

    public static SCODeviceHelper instance;
    private boolean isDeviceReadyForRecording;

    public static SCODeviceHelper getInstance(){
        if(instance == null){
            instance = new SCODeviceHelper();
        }
        return instance;
    }

    public PairingFragment.BluetoothHelper mBluetoothHelper;

    public PairingFragment.BluetoothHelper getmBluetoothHelper() {
        return mBluetoothHelper;
    }

    public void setmBluetoothHelper(PairingFragment.BluetoothHelper mBluetoothHelper) {
        this.mBluetoothHelper = mBluetoothHelper;
    }

    public boolean isDeviceReadyForRecording() {
        return isDeviceReadyForRecording;
    }

    public void setDeviceReadyForRecording(boolean deviceReadyForRecording) {
        isDeviceReadyForRecording = deviceReadyForRecording;
    }
}
