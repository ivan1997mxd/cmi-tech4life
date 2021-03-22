package com.estethapp.media.covid.device;


import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class SaveData {
    private static final String PersistentDataKey = "Data";
    private static final String LaunchedKey = "Launched";
    private static final String DeviceString = "Device";
    private static final String response = "Response";
    private static final String Users = "Users";
    private static final String CurrentUser = "CurrentUser";

    private static SharedPreferences PersistentData;

    public static boolean IsLaunched(Context context) {
        return GetPersistentData(context).getBoolean(LaunchedKey, false);
    }

    public static void SetLaunched(Context context, boolean value) {
        GetPersistentData(context).edit().putBoolean(LaunchedKey, value).apply();
    }

    public static String getResponse(Context context) {
        return GetPersistentData(context).getString(response, "");
    }

    public static void SetResponse(Context context, String value) {
        GetPersistentData(context).edit().putString(response, value).apply();
    }

    public static String getCurrentUser(Context context) {
        return GetPersistentData(context).getString(CurrentUser, "");
    }

    public static void SetCurrentUser(Context context, String currentUser) {
        GetPersistentData(context).edit().putString(CurrentUser, currentUser).apply();
    }

    private static SharedPreferences GetPersistentData(Context context) {
        if (PersistentData == null) {
            PersistentData = context.getSharedPreferences(PersistentDataKey, Context.MODE_PRIVATE);
        }

        return PersistentData;
    }

    public static String GetBTDeviceInfo(Context context) {
        return GetPersistentData(context).getString(DeviceString, "No Connection");
    }

    public static void SetDeviceInfo(Context context, String deviceInfo) {
        GetPersistentData(context).edit().putString(DeviceString, deviceInfo).apply();
    }

    public static void SetUserList(Context context, ArrayList<String> userList) {
        String[] myStringList = userList.toArray(new String[userList.size()]);
        GetPersistentData(context).edit().putString(Users, TextUtils.join("‚‗‚", myStringList)).apply();
    }
    public static ArrayList<String> GetUserList(Context context) {
        return new ArrayList<>(Arrays.asList(TextUtils.split(GetPersistentData(context).getString(Users, ""), "‚‗‚")));
    }
}
