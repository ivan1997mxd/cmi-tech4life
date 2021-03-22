package com.estethapp.media.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.estethapp.media.ActivityLogin;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;

/**
 * Created by Irfan Ali on 1/3/2017.
 */
public class Util {
  public static Context context;
  public static  Prefs prefs  = Prefs.getInstance();
  public static SharedPreferences prefsInstance  = prefs.init(context); ;






  public static String getDirectory(){



   //   prefsInstance.getString("USERNAME","AHSAN");
   // File myDirectory = new File(Environment.getExternalStorageDirectory(), "estethApp");

     File myDirectory = new File(Environment.getExternalStorageDirectory()
          + File.separator + "estethApp" + File.separator + prefsInstance.getString("USERNAME","AHSAN"));


        if(!myDirectory.exists()) {
            myDirectory.mkdirs();
          Log.e("if",""+Environment.getExternalStorageDirectory()+
            File.separator+"estethApp"+File.separator + prefsInstance.getString("USERNAME","AHSAN"));
        }
      Log.e("else",""+Environment.getExternalStorageDirectory()
        + File.separator + "estethApp" + File.separator + prefsInstance.getString("USERNAME","AHSAN"));
        return myDirectory.getAbsolutePath();

    }

    public static String getProfileDirectory(){
        File myDirectory = new File(Environment.getExternalStorageDirectory(), "estethProfile");

        if(!myDirectory.exists()) {
            myDirectory.mkdirs();
        }

        return myDirectory.getAbsolutePath();
    }

    public static String getCurrentDateTime(){
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, hh:mm a");
        return df.format(Calendar.getInstance().getTime());
    }

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = mgr.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected() && netInfo.isAvailable());
    }

    public static File createImageFile(Context context){
        // Create an image file name
        File storageDir = context.getFilesDir();
        File image = null;
        try{
            image = File.createTempFile(
                    "eStethTempImage",  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        }catch (Exception e){

        }
        return image;
    }

    public void updatePref(Context context, UserModel profileModel, SharedPreferences prefsInstance, Prefs prefs){
        JSONObject jsonbody = new JSONObject();
        try {
            jsonbody.put("UserID"        ,profileModel.UserID);
            jsonbody.put("Name"          ,profileModel.Name);
            jsonbody.put("PatientID"     , profileModel.PatientID);
            jsonbody.put("Email"         , profileModel.Email);
            jsonbody.put("Password"      , profileModel.Password);
            jsonbody.put("Gender"        , profileModel.Gender);
            jsonbody.put("ProfilePhoto"  , profileModel.ProfilePhoto);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        prefsInstance.edit().putString(prefs.USERMODEL,jsonbody.toString()).commit();

    }

    public static String getRecvAudioFilesDirectory(){

        File myDirectory = new File(getDirectory(), "Received");

        if(!myDirectory.exists()) {
            myDirectory.mkdirs();
        }

        return myDirectory.getAbsolutePath();
    }


    public static void scanMediaFiles(Context context,String filename)
    {

        File file = new File(filename);
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        context.sendBroadcast(intent);

    }

    public static boolean isServiceRunning(Context context,Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}
