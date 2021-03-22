package com.estethapp.media.covid.device;

import android.bluetooth.BluetoothDevice;

public class BluetoothDeviceInfo
{
    private final String Address;
    private String Name;
    private final String Type;

    public BluetoothDeviceInfo(String address, BLEUartService service)
    {
        BluetoothDevice device = service.getBluetoothAdapter().getRemoteDevice(address);
        Address = device.getAddress();
        Name = device.getName();

        if (Name == null || Name.length() == 0)
        {
            Name = "Unknown Device";
        }

        switch (device.getType())
        {
            case BluetoothDevice.DEVICE_TYPE_CLASSIC:
                Type = "Bluetooth Classic";
                break;
            case BluetoothDevice.DEVICE_TYPE_LE:
                Type = "Bluetooth LE";
                break;
            case BluetoothDevice.DEVICE_TYPE_DUAL:
                Type = "Bluetooth Classic/LE";
                break;
            default:
                Type = "";
                break;
        }
    }

    public String getAddress()
    {
        return Address;
    }

    public String getName()
    {
        return Name;
    }

    public String getType()
    {
        return Type;
    }
}
