package com.estethapp.media.covid.device;

public class BLEUartEvent
{
    private String Action;
    private byte[] Data;

    public BLEUartEvent(String action)
    {
        Action = action;
        Data = new byte[0];
    }

    public BLEUartEvent(String action, byte[] data)
    {
        Action = action;
        Data = data;
    }

    public String getAction()
    {
        return Action;
    }

    public byte[] getData()
    {
        return Data;
    }
}
