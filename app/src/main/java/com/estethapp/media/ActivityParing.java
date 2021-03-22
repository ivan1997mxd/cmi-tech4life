package com.estethapp.media;

import android.bluetooth.BluetoothAdapter;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


import com.estethapp.media.util.Prefs;

/**
 * Created by Irfan Ali on 12/29/2016.
 */
public class ActivityParing extends AppCompatActivity {

    private SharedPreferences prefsInstance;
    private Prefs prefs;


   Button startBtn;
   Button startTest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Fabric.with(getApplicationContext(), new Crashlytics());
        setContentView(R.layout.activity_splash_screen);

        prefs = Prefs.getInstance();
        prefsInstance = prefs.init(ActivityParing.this);

        prefsInstance.edit().putBoolean(prefs.ISLOGIN , true).commit();

        startBtn = (Button) findViewById(R.id.btn_star);
        startTest = (Button) findViewById(R.id.btn_test);


        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        startTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });



    }

    public boolean isBluetoothEnabled()
    {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter.isEnabled();

    }

}
