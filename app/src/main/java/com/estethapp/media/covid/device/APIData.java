package com.estethapp.media.covid.device;


import android.util.Log;

import java.lang.reflect.Field;
import java.util.HashMap;

public class APIData
{
    private static final String Unset = "NA";

    public String case_code = "871";
    public String weight = Unset;
    public String weightunit = "kg";
    public String height = Unset;
    public String heightunit = "inches";
    public String painpatienty = "No";
    public String painpatient = "0";
    public String city = "1";
    public String data = Unset;
    public String diagnosis = Unset;
    public String disease_id = "1";
    public String gender = "f";
    public String history = Unset;
    public String location_id = "35";
    public String patient_id = "0";
    public String users_id = "829";
    public String comment = Unset;
    public String m_age = "3";
    public String y_age = "20";
    public String created_at = "2020-07-21 1:30:37";
    public String form_id = "1";
    public String referer_id = "829";
    public String fname = Unset;
    public String lname = Unset;
    public String temperature = Unset;
    public String temperaturevalue = "℃";
    public String oraltemperature = "Oral";
    public String systolic = Unset;
    public String diastolic = Unset;
    public String hands = Unset;
    public String bloodpressureposition = Unset;
    public String pulsepres = "Regular";
    public String pulse = Unset;
    public String respiratoryrate = Unset;
    public String saturation = Unset;
    public String satoxygen = "On Oxygen";

    boolean mDebugEnable = true;
    boolean enableURLPadding = true;

    public boolean checkData(){
        return !temperature.equals("-") & !saturation.equals("-") & !pulse.equals("-");
    }

    public String getStringData(){
        return String.format("%s, %s, %s, %s, %s, %s", created_at, case_code, y_age, temperature, saturation, pulse);
    }

    public HashMap<String, String> GetData()
    {
        HashMap<String, String> result = new HashMap<>();

        for (Field field : APIData.class.getFields())
        {
            if (field.getType() == String.class)
            {
                try
                {
                    result.put(field.getName(), (String)field.get(this));
                }
                catch (Exception ex)
                {
                    Log.e("Serialize", ex.toString());
                }
            }
        }

        return result;
    }
}