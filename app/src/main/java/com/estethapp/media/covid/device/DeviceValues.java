package com.estethapp.media.covid.device;


public class DeviceValues
{
    private String DeviceName = "";
    private float Temperature = Float.NaN;
    private float BPM = Float.NaN;
    private float Confidence = Float.NaN;
    private float SpO2 = Float.NaN;

    private int Status;
    private int StatusExt;

    private float RValue;
    private int BatteryLevel;

    private int PayloadType;
    private int SubCode;

    public DeviceValues(float[] values)
    {
        if (values.length >= 1) Temperature = values[0];
        if (values.length >= 2) BPM = values[1];
        if (values.length >= 3) Confidence = values[2];
        if (values.length >= 4) SpO2 = values[3];
        if (values.length >= 5) Status = (int)values[4];
        if (values.length >= 6) StatusExt = (int)values[5];
        if (values.length >= 7) RValue = values[6];
        if (values.length >= 8) BatteryLevel = (int)values[7];
        if (values.length >= 9) PayloadType = (int)values[8];
        if (values.length >= 10) SubCode = (int)values[9];
    }

    public DeviceValues(String deviceName){
        DeviceName = deviceName;
    }

    public float getBPM()
    {
        return BPM;
    }

    public float getSpO2()
    {
        return SpO2;
    }

    public float getTemperature()
    {
        return Temperature;
    }

    public String getDeviceName(){
        return DeviceName;
    }


    public float getConfidence()
    {
        return Confidence;
    }

    public int getStatus()
    {
        return Status;
    }

    public int getStatusExt()
    {
        return StatusExt;
    }

    public float getRValue()
    {
        return RValue;
    }

    public int getBatteryLevel()
    {
        return BatteryLevel;
    }

    public int getPayloadType()
    {
        return PayloadType;
    }

    public int getSubCode()
    {
        return SubCode;
    }

    public static class BaseStatus
    {
        public static final int NoObjectDetected = 0;
        public static final int ObjectDetected = 1;
        public static final int ObjectOtherThanFingerDetected = 2;
        public static final int FingerDetected = 3;
    }

    public static class ExtendedStatus
    {
        public static final int Success = 0;
        public static final int NotReady = 1;
        public static final int ObjectDetected = -1;
        public static final int ExcessiveSensorDeviceMotion = -2;
        public static final int NoObjectDetected = -3;
        public static final int PressingTooHard = -4;
        public static final int ObjectOtherThanFingerDetected = -5;
        public static final int ExcessiveFingerMotion = -6;
    }

    public static class PayloadTypes
    {
        public static final int SensorData = 1;
        public static final int SamplingStatus = 2;
        public static final int Information = 3;
        public static final int Error = 4;
        public static final int HubStatus = 5;
    }

    public static class SubCodes
    {
        public static final int Temperature_BatteryLevel = 1;
        public static final int HR_Confidence_SPO2_StatusBase_StatusEx_R_BatteryLevel = 2;
        public static final int BatteryLevel = 3;

        public static final int SamplingError = 0;
        public static final int SamplingOnCourse = 1;
        public static final int SuccessfulSampling = 2;

        public static final int HwVersion = 1;
        public static final int SwVersion = 2;

        public static final int ErrorInitializingBluetooth = 1;
        public static final int MAX32664CommError = 2;
        public static final int ErrorConfiguringSensor = 3;
    }
}
