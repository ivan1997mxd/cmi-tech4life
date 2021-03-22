package com.estethapp.media;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
//import com.crashlytics.android.Crashlytics;
import com.estethapp.media.mSparrow.APIService;
import com.estethapp.media.util.Helper;
import com.estethapp.media.util.Prefs;

import java.sql.Date;

//import io.fabric.sdk.android.Fabric;

public class SplashScreen extends Activity {

    private SharedPreferences prefsInstance;
    private Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fabric.with(getApplicationContext(), new Crashlytics());
        setContentView(R.layout.activity_splash_screen);
        Helper.fullScreen(this);
        prefs = Prefs.getInstance();
        prefsInstance = prefs.init(SplashScreen.this);



        launchLandingScreen();
    }

    // run post requests to server for testing
    private void testAPI() {

        Log.d("testAPI", "Send Test API Data to remote server All Permissions");

        APIService API = new APIService();

        API.convertDateCode();

        // Set values for test data points
        API.setPopulateDemoData();

        // Set a date for these tests
        // Touch creation date as now.
        API.setCreationDate();

        API.setPainPatient("0");
        API.setPatientPainPresent("No");

        // Set the SPO2 Saturation
        API.setOxygenSaturation("95");

        // Set the Respiration Rate
        API.setRespirationRate("89");

        // Set the patient heart rate
        API.setHeartRate("45");

        API.sendData(true);

        // Set the patientID

        API.setPatientID("837");
        API.setPainPatient("1");
        API.setPatientPainPresent("Yes");

        // Set the patient heart rate
        API.setHeartRate("85");

        // Set the patient heart rate
        API.setOxygenSaturation("89");

        // Set a date for these tests
        // Touch creation date as now.
        API.setCreationDate();

        Log.d("chevk", "Json of Data:"+ API.toJson());

        API.sendData(true);

        API.setPainPatient("9");
        API.setPatientPainPresent("Yes");

        // Set the patient heart rate
        API.setHeartRate("2");

        // Set the patient heart rate
        API.setOxygenSaturation("5");

        // Set a date for these tests
        // Touch creation date as now.
        //API.setCreationDate();
        // Add 1 second to the date of NOW.
        // Date date = (Date) new java.util.Date(System.currentTimeMillis()-120000);

        try {

            // Back date to 1 minute ago
            Date date = new Date(System.currentTimeMillis() - 60000);

            API.setCreationDate(date);
        } catch(Exception e)
        {
            Log.d("Error", e.toString());
        }


        Log.d("JD", "hhm of Data:"+ API.toJson());

        API.sendData(true);


    }

    private void launchLandingScreen() {
        new Thread() {
            public void run() {
                try {
                    sleep(1 * 2000);

                    boolean isLogin = prefsInstance.getBoolean(prefs.ISLOGIN, false);
                    String pinCode = prefsInstance.getString(prefs.PIN,"");
                    if (isLogin && pinCode.equals("")) {
                        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        return;
                    }else if(!pinCode.equals("")){
                        Intent intent = new Intent(SplashScreen.this, ActivityPin.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }else{
                        Intent i = new Intent(getBaseContext(), ActivityLogin.class);
                        startActivity(i);
                        finish();
                    }



                } catch (Exception e) {
                }
            }
        }.start();
    }



}
