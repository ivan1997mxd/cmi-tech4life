package com.estethapp.media.helper;

import android.content.SharedPreferences;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

public class ExaminatingHelper {
    public static ExaminatingHelper instance;

    static boolean frontClick =  true;
    public int recordingDevice = -1;
    public boolean recordingDeviceConnected = false ;
    public static boolean isFrontSelected = true;
    public boolean isBackSelected;

    public boolean isFirstTimeSelected;

    boolean  front_Lungs_Rt_upper_field_1;
    boolean  front_Lungs_Rt_upper_field_2;
    boolean  front_Lungs_Rt_middile_field;
    boolean  front_heart_pulmonic_valve;
    boolean  front_heart_erbpoint;
    boolean  front_heart_tricuspidvalve;
    boolean  front_Lungs_Rt_lower_field;
    boolean  front_heart_aortic_valve;
    boolean  front_heart_mitral_valve;
    boolean  front_lungs_lt_lower_field;
    boolean  front_lungs_lt_upper_field;

    public static int selectedFrontPosition = -1;
    public static int selectedBackPosition = 1;
    private boolean postionArray [] = {front_Lungs_Rt_upper_field_1,front_Lungs_Rt_upper_field_2,front_Lungs_Rt_middile_field,front_heart_pulmonic_valve, front_heart_erbpoint,
            front_heart_tricuspidvalve, front_Lungs_Rt_lower_field, front_heart_aortic_valve ,front_heart_mitral_valve, front_lungs_lt_lower_field,front_lungs_lt_upper_field };

    public static  boolean isRecordingStarted;


    public String frontPositionNames[] = {
            "Heart- Aortic valve",
            "Heart- Pulmonic valve",
            "Heart- Erb’s point",
            "Heart- Tricuspid valve",
            "Heart- Mitral valve",
            "Lungs-Right Upper Field 1",
            "Lungs-Right Upper Field 2",
            "Lungs-Right Middle Field",
            "Lungs-Right Lower Field",
            "Lungs-Left Upper field",
            "Lungs-Left Lower field"
    };

    public String backPostionNames[] = {
            "Lungs-Left Upper",
            "Lungs-Left Middle",
            "Lungs-Left Lower",
            "Lungs-Left Costophrenic angle",
            "Lungs-Right Upper",
            "Lungs-Right Middle",
            "Lungs-Right Lower",
            "Lungs-Right Costophrenic angle"
    };




    public static ArrayList<ExminigPostion> frontPostion =new ArrayList<>();
    public static ArrayList<ExminigPostion> backPostion =new ArrayList<>();

    public static ExaminatingHelper getInstance (){
        if(instance == null){
            instance =new ExaminatingHelper();
        }
        return instance;
    }

    public int getRecordingDevice() {
        return recordingDevice;
    }

    public void setRecordingDevice(int recordingDevice) {
        this.recordingDevice = recordingDevice;
    }



    public void setFrontClick(boolean frontClick) {
        this.frontClick = frontClick;
    }

    public boolean isRecordingStarted() {
        return isRecordingStarted;
    }

    public void setRecordingStarted(boolean recordingStarted) {
        isRecordingStarted = recordingStarted;
    }

    public boolean isFirstTimeSelected() {
        return isFirstTimeSelected;
    }

    public void setFirstTimeSelected(boolean firstTimeSelected) {
        isFirstTimeSelected = firstTimeSelected;
    }

    public boolean isFrontClick() {
        return frontClick;
    }

    public void setFrontBodyPostionSelected(int position){
        selectedFrontPosition = position;
    }

    public void setBackBodyPostionSelected(int position){
        selectedBackPosition = position;
    }

    public boolean isRecordingDeviceConnected() {
        return recordingDeviceConnected;
    }

    public void setRecordingDeviceConnected(boolean recordingDeviceConnected) {
        this.recordingDeviceConnected = recordingDeviceConnected;
    }

    public int getFrontBodyPostionSelected(){
        return selectedFrontPosition;
    }

    public int getBackBodyPostionSelected(){
        return selectedBackPosition;
    }


    public String getFrontPostionName(int poistion){
        return frontPositionNames[poistion-1];
    }


    public String getBackPostionName(int poistion){
        return backPostionNames[poistion-1];
    }

    public void setFrontRecordingValues(int bodyPostion, int startTime, int endTime){
        ExminigPostion exminigPostion =new ExminigPostion();
        exminigPostion.bodyPostion=bodyPostion;
        exminigPostion.startTime = startTime;
        exminigPostion.endTime = endTime;
        frontPostion.add(exminigPostion);
    }
    public void setBackRecordingValues(int bodyPostion, int startTime, int endTime){
        ExminigPostion exminigPostion =new ExminigPostion();
        exminigPostion.bodyPostion=bodyPostion;
        exminigPostion.startTime = startTime;
        exminigPostion.endTime = endTime;
        backPostion.add(exminigPostion);
    }

    public void saveEximatiningInfo(String fileName, SharedPreferences prefsInstance){
        Exmining em =new Exmining();
        em.fileName = fileName;
        em.frontPostion = frontPostion;
        em.isFront = isFrontSelected;

        em.backPostion = backPostion;
        em.isBack = isBackSelected;

        Gson gson = new Gson();
        String json = gson.toJson(em);


        if (prefsInstance != null){
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(json);
                String jsonString = jsonObject.toString();
                SharedPreferences.Editor editor = prefsInstance.edit();
                editor.remove(fileName).commit();
                editor.putString(fileName, jsonString);
                editor.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void reset(){
        frontPostion.clear();
        backPostion.clear();
        selectedFrontPosition = -1;
        selectedBackPosition= 1;
        isRecordingStarted = false;
        frontClick =true;
        isFrontSelected=true;
    }

    public Exmining getEximatiningInfo(String fileName,SharedPreferences prefsInstance){
        Exmining exmining = null;
        fileName = fileName.replace(".wav","");
        Gson gson = new Gson();
        String json = prefsInstance.getString(fileName, "");
        if(!json.equals("")){
            exmining = gson.fromJson(json, Exmining.class);
        }
        return exmining;
    }

    public Exmining castIntoEximatiningInfo(String exminingInfo){
        Exmining exmining = null;
        Gson gson = new Gson();
        if(!exminingInfo.equals("")){
            exmining = gson.fromJson(exminingInfo, Exmining.class);
        }
        return exmining;
    }

    public String getEximatiningStringInfo(String fileName,SharedPreferences prefsInstance){
        fileName = fileName.replace(".wav","");
        String json = prefsInstance.getString(fileName, "");
        return json;
    }


}
