/******************************************************************************
 *
 * Sheridan’s Centre for Mobile Innovation (CMI)
 *
 * All Rights Reserved.
 * © Copyright by The Sheridan College Institute of Technology and Advanced
 *   Learning’s (“Sheridan”) Centre for Mobile Innovation, September 2019
 *
 *
 * NOTICE:
 * All information contained herein is, and remains the property of Sheridan.
 * The intellectual and technical concepts contained herein are proprietary
 * to Sheridan and its suppliers, affiliates, and subsidiaries, and may be
 * covered by Canadian, U.S. and Foreign Patents, patents in process, and
 * are protected by trade secret or copyright law.
 * Dissemination of this information, reproduction of this material, or use
 * of this information for any purpose other than permission which is
 * expressly given by Sheridan is strictly forbidden unless prior explicit
 * written permission is obtained from Sheridan (and/or its
 * Centre for Mobile Innovation).
 *
 * Grant Funding that enabled the development of all information contained
 * herein:
 *   Natural Sciences and Engineering Research Council of Canada (NSERC)
 *   Grant #: Sheridan Account Department #06227
 *            NSERC CCIP - IE 503351-16 CMI
 *   Description:
 *   http://www.nserc-crsng.gc.ca/Professors-Professeurs/RPP-PP/CCI-ICC_eng.asp
 *
 * ****************************************************************************
 */

package com.estethapp.media.mSparrow;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.navigation.NavigationView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.estethapp.media.ActivitySaveData;
import com.estethapp.media.MainActivity;
import com.estethapp.media.R;
import com.estethapp.media.fragment.HomeFragment;
import com.estethapp.media.util.Prefs;
import com.estethapp.media.util.UserModel;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static com.estethapp.media.mSparrow.mSparrowFeather.makeGattUpdateIntentFilter;

//import com.estethapp.media.fragment.mSparrow.data.s;

/*
import static com.esteth.media.mSparrowFragments.mSparrow.data.mSparrowFeather.makeGattUpdateIntentFilter;
import static com.esteth.media.util.App.context;
*/

public class mSparrowMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
//public class mSparrowMainActivity extends Fragment implements NavigationView.OnNavigationItemSelectedListener {

    private mSparrowFeather mSparrowFeather = null;
    private int countDownTimer = 10;

    //Uart
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int UART_PROFILE_CONNECTED = 20;
    private static final int UART_PROFILE_DISCONNECTED = 21;

    private int mState = UART_PROFILE_DISCONNECTED;
    private BluetoothDevice mDevice = null;
    private BluetoothAdapter mBtAdapter = null;

    //constants
    public static final Integer ADC_BIT_RES = 4096;
    public static final Double ADC_DELTA = 0.05;
    public static final Double ADC_Voltage = 3.3;
    public static final Double ADC_SAMPLES_PER_SEC = 1 / ADC_DELTA;

    public static final int COUNT_DOWN_INTERVAL = 1000;
    public static final double SAMPLES_PER_SEC = 100;
    public static final int RESOLUTION = 16;
    public static final int DELAY_MILLIS = 3000;
    public static final String SERVER_URL = "http://142.55.32.86:50161/record/dataUpload.php";
    private static final boolean EXTENDED_DEBUG = false;

    int avgrespRate = 14;
    private int MEASURE_TIME = 10000;

    /*
    private int HEART_RATE_MEASURE_TIME = 20000;
    private int RESPIRATION_MEASURE_TIME = 10000;
    private int ECG_MEASURE_TIME = 10000;
    private int BLOOD_PRESSURE_MEASURE_TIME = 20000;
    private int HEART_RATE_NUM = 0;
    private int BLOOD_PRESSURE_NUM=1;
    private int RESPIRATION_NUM=2;
    private int ECG_NUM=3;
    */


    //esteth data
    private SharedPreferences prefsInstance;
    private Prefs prefs;
    UserModel profileModel;
/*
    //Image views to show what devices are loading into which slots on the hub
    private ArrayList<ImageView> btnSlots;
    private ImageView btnSlot1;
    private ImageView btnSlot2;
    private ImageView btnSlot3;
    private ImageView btnSlot4;
    private ImageView btnSlot5;
    private ImageView btnSlot6;
    private ImageView btnSlot7;
    private ImageView btnSlot8;
    private ImageView hub;

    //Image views showing the status of a device as a dot, red(not connected), green (connected)
    private ArrayList<ImageView> dots;
    private ImageView dot1;
    private ImageView dot2;
    private ImageView dot3;
    private ImageView dot4;
    private ImageView dot5;
    private ImageView dot6;
    private ImageView dot7;
    private ImageView dot8;
*/

    private ImageView bluetoothStatus;

    private ImageButton powerButton;

    //Test Variables for validating JSON Data
    private String testData;
    //private String testData2;

    //textview used to analyze frame sent by biosginalsplux hub
    private TextView txtTest;

    Context mContext;

    private String txtBtnStat = "";
    //JSON data which is sent to the server
    private String readingsJSON;

    Toolbar toolbar;

    private int packetCounter = 0;

    //control buttons on layout
    private Button btnCapture;
    // private Button btnWeb;
    private Button btnTest;

    //layout elements to show a progress bar and countdown timer when measurements are taking place
    private ProgressBar progressBar;
    private TextView txtProgress;
    ConstraintLayout progressLayout;


    //booleans to handle whether or not devices are plugged in for capturing
    private boolean allGreen;
    private boolean noGreen;

    private boolean connectFlag = true;


    //boolean that handles whether the biosginalsplux hub is connected
    private boolean connectedState;

    //boolean that handles whether or not the frames are going to be captured
    private boolean capture = false;


    private int onCounter = 0;
    private int offCounter = -1;

    private boolean wasBtnPushed = false;

    private boolean btConnected = false;
    private boolean isConnected = false;
    private boolean firstStart = true;

    private boolean isReconnecting = false;


    //list that controls whether or not a device has been seleted for measurement already
    private int enableList[];

    //array that monitors status of 8 channels on hub
    private int[] channelsUsed;


    //a counter that counts the frames being recorded once capture is clicked
    private int frameCounter;


    //from biosiginals API
    private BioSignalsData bData;


    private final String TAG = this.getClass().getSimpleName();


    //char sequences for selection dialog when a slot is pressed
    private CharSequence colors[] = new CharSequence[]{"HeartRate", "Blood Pressure", "Respiration", "ECG", "None"};
    private CharSequence presets[] = new CharSequence[]{"Test1", "Test2"};

    //name of device and mac address to for testing hub
    private String mDeviceName;
    private String mDeviceAddress;

    private String mDeviceDataLine = "";

    //device lists to that will grow based on user inputted devices in slots
    ArrayList<BioSignalsDevice> deviceList;
    ArrayList<BioSignalsDevice> exportDeviceList;

    //com.esteth.media.mSparrowFragments.mSparrow.ui.mSparrowMainActivity fragment;
    Context context;
    static HomeFragment instance;
    public UserModel currentUser;
    //View root;

    //String time, date;

    scopeView BPMView = new scopeView();
    scopeView RESPView = new scopeView();
    scopeView SPView = new scopeView();
    scopeView TempView = new scopeView();


    void buildFakeData() {

//        BPMView.FakeData();
//        RESPView.FakeData();
//        SPView.FakeData();
//        TempView.FakeData();

        return;


    }


    String calculateHROO(int spOCalculation, int Amin, int Amax) {
        float spOCalcF = (float) (0.228 * 0.25 * spOCalculation);
        spOCalcF -= 0.8;
        int totalVal = (int) spOCalcF;

        String spoCalc = "--";

        // System.out.println("calculateHROO (" + spOCalculation + ", " + Amin + ",  " + Amax + " ) = " + totalVal);

        if (Amin < totalVal) {
            if (totalVal > Amax) {
                spoCalc = String.valueOf(Amax);
            } else {
                spoCalc = String.valueOf(totalVal);
            }
        }

        return spoCalc;
    }

    void updateView() {

        BPMView.drawScopeView();
        RESPView.drawScopeView();
        SPView.drawScopeView();
        TempView.drawScopeView();


        //        TextView respiRateText = (TextView)findViewById(R.id._respRateText);
        //        respiRateText.setText("88");
        TextView respiRateText = (TextView) findViewById(R.id._respRateText);

        final TextView _spORateText = (TextView) findViewById(R.id._spORate);

        //_Update Heart Rate - > hrtext
        final TextView heartRateText = (TextView) findViewById(R.id._hrtext);

        final TextView tempText = (TextView) findViewById(R.id._tempC);

        int curTemp = TempView.getAvgReading() - 127;

        if (curTemp > 100 || curTemp < 5)
            curTemp = 14;

        if (curTemp == 14) {
            tempText.setText(curTemp + "c");
        } else {
            tempText.setText("0c");
        }


        if (heartRateText.getText().equals(("HR"))) {
            heartRateText.setText("0");
        }

        if (respiRateText.getText().equals(("RR"))) {
            respiRateText.setText("0");
        }


        Log.i("__", "" + countDownTimer);

        if ((--countDownTimer) == 0) {

        }
        if (countDownTimer == 0) {
            countDownTimer = 10;

            avgrespRate += (RESPView.calcFreq());

            avgrespRate = avgrespRate / 2;

            //respiRateText.setText("14");
            respiRateText.setText(String.valueOf(avgrespRate));

            ///num = (((num * 100) / (ADC_BIT_RES)) - 50);
//            respCycle = waveNum / lastX;
            //          results.setText("Number of Breaths:" + Integer.toString(waveNum) + "\n"
            //          + "Respiration Cycle / Min :" + String.format("%.0f", respCycle * 60)+ "\n"
            //          + "Total Recording Time:" + String.format("%.00f",lastX) +"seconds");

            System.out.println("Freq: " + avgrespRate);

            // Update less often.
            if (!calculateHROO(BPMView.getAvgReading(), 10, 230).equals("--")) {
                heartRateText.setText(calculateHROO(BPMView.getAvgReading(), 10, 230));
            }

            if (!calculateHROO(SPView.getAvgReading(), 10, 100).equals("--")) {
                _spORateText.setText(calculateHROO(SPView.getAvgReading(), 10, 100));
            }

            if (!calculateHROO(RESPView.getAvgReading(), 10, 100).equals("--")) {
                avgrespRate += (RESPView.calcFreq());

                avgrespRate = avgrespRate / 2;

                //respiRateText.setText("14");
                respiRateText.setText(String.valueOf(avgrespRate));
            }

        } else {
//            countDownTimer--;
        }


        // setContentView(heartRateText);
        // setContentView(_spORateText);

        /*
        // BPMView.
//        int bpmCalculation = BPMView.getAvgReading();

        //respiRateText.setText("88");

        //_Update Heart Rate - > hrtext
        TextView heartRateText = (TextView)findViewById(R.id._hrtext);

        // BPMView.
        int bpmTCalculation = BPMView.getAvgReading();

        double bpmCalcF = (0.228 * 0.25 * bpmTCalculation ) - 0.8;

        //bpmCalcF = bpmCalcF
        String bmpCalcText = String.format("%.02f", bpmCalcF*100);

        if (bpmCalcF < 1 ) {
            bpmCalcF = 0;
            bmpCalcText = "--";
        } else {
            bmpCalcText = String.format("%02d", bpmCalcF*100);

        }



        heartRateText.setText(bmpCalcText);


        System.out.println("DDX: " + bmpCalcText + " [" + bpmTCalculation + " " );

*/

        return;

    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Log.d(TAG, "onCreate called");

        setContentView(R.layout.multidevice_capture);

        mSparrowFeather = new mSparrowFeather(null);
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();


        mContext = getApplicationContext();

        if (mBtAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        service_init();

        //get user data and saved mac from shared preferences
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("device", Context.MODE_PRIVATE);

        mDeviceName = preferences.getString("name", "none");
        mDeviceAddress = preferences.getString("mac", "none");


        //if no device data has been saved open the scan activity to find a new device
        if (mDeviceAddress != null)
            if (mDeviceAddress.equals("none")) {
                Intent intent = new Intent(mSparrowMainActivity.this, ScanActivity.class);
                mSparrowMainActivity.this.startActivity(intent);
            }

        initView();
        setUIElements();
        initVariables();


        //TextView respiRateText = (TextView)findViewById(R.id._respRateText);
        //respiRateText.setText("88");
        //setContentView(respiRateText);


        //used for webview testing ***
        /*btnWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(mSparrowMainActivity.this, WebViewActivity.class);
                mSparrowMainActivity.this.startActivity(myIntent);
            }
        });*/


        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        System.out.println("onCreate(Bundle savedInstanceState) = width: " + displayMetrics.widthPixels + " density: " + displayMetrics.density);
        System.out.println("dpHeight: " + dpHeight + " dpWIdth: " + dpWidth);

        ///               dpHeight: 683.4286 dpWIdth: 411.42856


        //     ImageView bpmView = findViewById(R.id.bpmData);

        BPMView.setImageView((ImageView) findViewById(R.id.bpmData));

        // Load the RESPi Data Link
        RESPView.setImageView((ImageView) findViewById(R.id.respData));
        RESPView.setLineColor(Color.MAGENTA);

        SPView.setImageView((ImageView) findViewById(R.id.spoData));
        SPView.setLineColor(Color.GREEN);

        TempView.setImageView((ImageView) findViewById(R.id.tempData));
        TempView.setLineColor(Color.CYAN);

        //_respRateText

        setTimerTask();
        // Populate fake data into the data list
        // setFlashTimerTask();


    }

    private void setTimerTask() {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //   do your stuff here.
                        //updateTextView();
                        updateView();
                    }
                });
            }
        }, 1000, 100);


    }


    private void setFlashTimerTask() {
        long delay = 3000;
        long periodToRepeat = 60 * 1000; /* 1 min */
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //   do your stuff here.
                        updateFlashTextView();
                    }
                });
            }
        }, 1000, 50);
    }

    int doubleOff = 0;


    private void updateFlashTextView() {


//      updateView(
        buildFakeData();

        /*
        if(doubleOff>0) {
            doubleOff--;
            return;

        }

        TextView mainStatus =  findViewById(R.id.textViewStatus);


        if(mainStatus.getVisibility()==View.INVISIBLE) {

            mainStatus.setVisibility(View.VISIBLE);
            doubleOff=4;
        } else {

            mainStatus.setVisibility(View.INVISIBLE);
            doubleOff=0;
        }
*/

    }

    /*
    private void updateTextView() {


        TextView mainStatus =  findViewById(R.id.textViewStatus);

        mainStatus.setText("HU");

    //    noteTS = Calendar.getInstance().getTime();

        String time = "hh:mm"; // 12:00
        mainStatus.setText(getTimeMethod(time));


        //toString(getScreenWidth());

        //mainStatus.setText(String.valueOf(getScreenWidth()));
      //  String date = "dd MMMMM yyyy"; // 01 January 2013
       // tvDate.setText(DateFormat.format(date, noteTS));
    }



*/

    @Override
    public void onResume() {

        firstStart = true;

        super.onResume();


        Log.d(TAG, "onResume");


        //If the bluetooth adapter is not enable, request permissions
        if (!mBtAdapter.isEnabled()) {
            Log.i(TAG, "onResume - BT not enabled yet");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            if (mSparrowFeather != null) {
                // ASk mSparrowFeather to start monitor.
                // mSparrowFeather.sendStartMessage();

                startAutoConnectTimer();
            }

        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(mSparrowMainActivity.this, MainActivity.class);
            startActivity(intent);
            finish();

        }


        //This section is only used to show that presets can be used on device for specific medical cases, presets would be built into
        // their own class later
        else if (id == R.id.nav_presets) {
            //dialog that will display preset options for devices


        } else if (id == R.id.nav_switchHub) {
            Intent myIntent = new Intent(mSparrowMainActivity.this, ScanActivity.class);
            mSparrowMainActivity.this.startActivity(myIntent);

        }

        // DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout2);
        //drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_presets) {


        } else if (id == R.id.action_scan) {
            Intent myIntent = new Intent(mSparrowMainActivity.this, ScanActivity.class);
            mSparrowMainActivity.this.startActivity(myIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy()");

        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(UARTStatusChangeReceiver);

            if (isConnected) {
                disconnectHub();
            }


        } catch (Exception ignore) {
            Log.e(TAG, ignore.toString());
        }

        //  unbindService(mSparrowFeather.mServiceConnection);

        if (mSparrowFeather != null) {


            mSparrowFeather.getmService().stopSelf();
            mSparrowFeather.setmService(null);

        }

    }

    @Override
    protected void onStart() {

        startAutoConnectTimer();
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onStop() {

        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onPause() {
        disconnectHub();
        firstStart = false;
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onRestart() {


        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "Back Pressed, go back to the future...");
        if (mState == UART_PROFILE_CONNECTED) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            Log.d(TAG, " startActivity(startMain);");
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
            // Auto restart the feathers data feed.
            mSparrowFeather.sendStartMessage();
        } else {
            Log.d(TAG, "E-L-S-E Detected --  go back to the future...");

        }
    }

    @Nullable
    /*
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_main, container, false);

        return view;


    }
*/
    //Method which is called in the onResume lifecycle activity which will run a 1 second timer that
    //will call the powerButton onClick method resulting in auto start for data capturing
    private void startAutoConnectTimer() {

        powerButton.setEnabled(false);

        Log.d(TAG, "startAutoConnectTimer called");

        bluetoothStatus.setImageResource(R.mipmap.bt_disconnected);

        txtTest.setText("Starting");

        new CountDownTimer(6000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                double msFloor = millisUntilFinished / 1000;
                msFloor = Math.floor(msFloor);

                //Calling powerButton.callOnClick() right away results in activity crash so it is called
                //2 seconds after activity start
                if (msFloor == 3.0) {
                    powerButton.callOnClick();
                }
            }

            @Override
            public void onFinish() {
                Log.d(TAG, "Timer Done");

                //btConnected is only made true when some connection occurs -- therefore this is only run when
                //no connection occurs on the first attempt
                if (!btConnected) {
                    bluetoothStatus.setImageResource(R.mipmap.bt_disconnected);
                    txtTest.setText("Disconnected");

                    //to ensure next click will be a connect attempt
                    powerButton.callOnClick();
                }
                btConnected = !btConnected;
                powerButton.setEnabled(true);
            }
        }.start();
    }


    private void sendDeviceDataToServer() {
        bData.setDevices(deviceList);
        Gson gson = new Gson();
        readingsJSON = gson.toJson(bData);

        Log.d(TAG, "Sending data to server.... ");

        // Log.d(TAG, testData2);
        sendData(readingsJSON);

        Log.d(TAG, "Json Data Submitted to Server:\n");

    }

    private void setJsonHeader() {
        Date d = new Date();
        prefs = Prefs.getInstance();
        prefsInstance = prefs.init(context);

        Gson gson = new Gson();
        String json = prefsInstance.getString(prefs.USERMODEL, "");
        profileModel = gson.fromJson(json, UserModel.class);


        bData = new BioSignalsData(Integer.toString(profileModel.UserID), mDeviceAddress, d, channelsUsed, RESOLUTION);
    }

    private void initVariables() {

        connectedState = false;
        channelsUsed = new int[8];
        enableList = new int[]{0, 0, 0, 0};
        deviceList = new ArrayList<>();
        exportDeviceList = new ArrayList<>();

    }

    private void setProgressBarVisibility(int visibility) {
        progressBar.setVisibility(visibility);
        txtProgress.setVisibility(visibility);
        progressLayout.setVisibility(visibility);
    }


    private void startCountdownTimer() {
        CountDownTimer cdt = new CountDownTimer(MEASURE_TIME, COUNT_DOWN_INTERVAL) {
            int count = 0;

            //on each tick, update progress bar and set text to remaining time
            public void onTick(long millisUntilFinished) {
                count += (COUNT_DOWN_INTERVAL);
                progressBar.setMax(MEASURE_TIME);
                progressBar.setProgress(count);

                if (count >= MEASURE_TIME) {
                    txtProgress.setText("Finished!");

                } else {
                    txtProgress.setText("Capturing..." + (millisUntilFinished / COUNT_DOWN_INTERVAL) + " seconds left...");
                }
            }

            public void onFinish() {
                Log.d(TAG, "Finished Capture.. Close window...");
                // Remove the progress window when complete.
                setProgressBarVisibility(View.INVISIBLE);
            }
        }.start();
    }

    private void resetCapture() {
        //cycle through all devices in device list and add readings to device list
        for (int i = 0; i < deviceList.size(); i++) {
            BioSignalsDevice d = deviceList.get(i);
            d.clearReadings();

        }
    }

    private void captureReadings(int[] array) {

        //cycle through all devices in device list and add readings to device list
        for (int i = 0; i < deviceList.size(); i++) {
            BioSignalsDevice d = deviceList.get(i);

            // why?  all readinds should be saved..
            //if(frameCounter <= d.getMeasureTime() / 50){
            //
            d.addReadings(new Reading(array[d.getChannel()], frameCounter));

            if (EXTENDED_DEBUG) {
                testData = array[d.getChannel()] + "\n";
                Log.d(TAG, "private void captureReadings(int[] array) : " + testData);
            }

            // }

            //testData2 += Integer.toString(array[1]) + "\n" ;
        }
        // increase the frame index for next item
        frameCounter++;
    }

    private void toggleCaptureButton() {

        //if at least 1 device is green and none are red
        if (allGreen && !noGreen) {
            btnCapture.setEnabled(true);
        } else {
            btnCapture.setEnabled(false);
        }
    }

    //handle changes in connection states
    private void checkDevicesConnectionState(int[] array) {

        //reset flags when checking connection state
        allGreen = true;
        noGreen = true;

        //cycle through all ports on hub
        for (int i = 0; i < array.length; i++) {
            checkChannelConnectionState(array[i], i);
            setConnectionStateFlags(i);
        }
    }

    private void setConnectionStateFlags(int i) {

    }

    private void checkChannelConnectionState(int channelValue, int position) {
        //if a channel is used and its receiving signal but connection state was red, set to green

    }

    private void setDeviceList() {

    }


    private void setSlotImage(ImageView iView, int slotPosition, int id, int deviceNumber) {
        iView.setImageResource(id);
        iView.setTag(id);
        enableList[deviceNumber] = 1;

    }

    private void initView() {

/*
        btnTest = findViewById(R.id.btn_test);

        btnSlots = new ArrayList<>();
        btnSlot1 = findViewById(R.id.btn_slot_1);
        btnSlot2 = findViewById(R.id.btn_slot_2);
        btnSlot3 = findViewById(R.id.btn_slot_3);
        btnSlot4 = findViewById(R.id.btn_slot_4);
        btnSlot5 = findViewById(R.id.btn_slot_5);
        btnSlot6 = findViewById(R.id.btn_slot_6);
        btnSlot7 = findViewById(R.id.btn_slot_7);
        btnSlot8 = findViewById(R.id.btn_slot_8);

        hub = findViewById(R.id.imageView2);

        btnSlots.add(btnSlot1);
        btnSlots.add(btnSlot2);
        btnSlots.add(btnSlot3);
        btnSlots.add(btnSlot4);
        btnSlots.add(btnSlot5);
        btnSlots.add(btnSlot6);
        btnSlots.add(btnSlot7);
        btnSlots.add(btnSlot8);
*/
        /*
        dots = new ArrayList<>();

        dot1 = findViewById(R.id.dot1);
        dot2 = findViewById(R.id.dot2);
        dot3 = findViewById(R.id.dot3);
        dot4 = findViewById(R.id.dot4);
        dot5 = findViewById(R.id.dot5);
        dot6 = findViewById(R.id.dot6);
        dot7 = findViewById(R.id.dot7);
        dot8 = findViewById(R.id.dot8);

        dots.add(dot1);
        dots.add(dot2);
        dots.add(dot3);
        dots.add(dot4);
        dots.add(dot5);
        dots.add(dot6);
        dots.add(dot7);
        dots.add(dot8);
*/

        bluetoothStatus = findViewById(R.id.btConnectStatus);
        txtTest = findViewById(R.id.txt_test_area);
        btnCapture = findViewById(R.id.btnHubCapture);
        progressBar = findViewById(R.id.capture_progress);
        txtProgress = findViewById(R.id.txt_progress_updates);
        progressLayout = findViewById(R.id.progress_layout);

        toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        powerButton = findViewById(R.id.powerButton);

        toolbar.setNavigationIcon(R.mipmap.ic_launcher);
        toolbar.setOverflowIcon(getResources().getDrawable(R.mipmap.ic_launcher));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout2);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view2);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);


        Menu selfAssessment = navigationView.getMenu();
        selfAssessment.findItem(R.id.nav_assessment).setVisible(false);
        selfAssessment.findItem(R.id.nav_assessment).setEnabled(false);
    }

    private void setUIElements() {

        bluetoothStatus.setImageResource(R.mipmap.bt_disconnected);


        if (powerButton != null)
            powerButton.setImageResource(R.drawable.powerred);
        powerButton.setTag(R.drawable.powerred);


        powerButton.setOnClickListener(actionListener);

        if (btnCapture != null)
            btnCapture.setOnClickListener(actionListener);

        // Send Start message to sparrow to begin conversion data sending
        if (btnTest != null)
            btnTest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSparrowFeather.sendStartMessage();
                }
            });


    }


    private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, final Intent intent) {
            String action = intent.getAction();

            final Intent mIntent = intent;


            //*********************//
            if (action.equals(UartService.ACTION_GATT_CONNECTED)) {


                //Fixes UI bug where on disconnect and immediate reconnect would occur
                if (wasBtnPushed == true) {
                    bluetoothStatus.setImageResource(R.mipmap.bt_pending);
                } else {
                    bluetoothStatus.setImageResource(R.mipmap.bt_disconnected);
                }


                btConnected = true;


                runOnUiThread(new Runnable() {
                    public void run() {


                        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                        Log.d(TAG, "UART_CONNECT_MSG");


                        // firstStart = false;

                        isConnected = true;

                        // txtTest.setText(mDeviceName+ " - ready");
                        mState = UART_PROFILE_CONNECTED;
                        connectedState = true;


                        powerButton.setImageResource(R.drawable.powergreen);
                        powerButton.setTag(R.drawable.powergreen);


                        txtTest.setText("Connected");
                        txtBtnStat = txtTest.getText().toString();


                        if (isReconnecting) {
                            isReconnecting = false;
                        }
                    }
                });
            }

            //*********************//
            if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {


                Log.d(TAG, "XYZ_DISCONNECTED");

                //If auto disconnect occurs, this value should be equal to onCounter, but if it does not occur, this
                //value should be onCounter - 1;
                offCounter++;

                runOnUiThread(new Runnable() {
                    public void run() {
                        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                        Log.d(TAG, "UART_DISCONNECT_MSG");
                        // txtTest.setText("Not Connected");
                        mState = UART_PROFILE_DISCONNECTED;
                        mSparrowFeather.getmService().close();
                        //setUiState();


                        connectedState = false;

                        isConnected = false;


                        disconnectHub();


                        //UI Stuff
                        powerButton.setImageResource(R.drawable.powerred);
                        powerButton.setTag(R.drawable.powerred);
                        txtTest.setText("Disconnected");


                        //Attempt to re-connect
                        if (offCounter == onCounter) {
                            attemptHubReconnect();
                        }
                    }
                });
            }

            //*********************//
            if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
                mSparrowFeather.getmService().enableTXNotification();
            }

            //*********************//
            if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {

                final byte[] txValue = intent.getByteArrayExtra(UartService.EXTRA_DATA);
                runOnUiThread(new Runnable() {
                    public void run() {
                        try {

                            // Process the received data
                            processHubResults(txValue);


                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                });
            }
            //*********************//
            if (action.equals(UartService.DEVICE_DOES_NOT_SUPPORT_UART)) {
                mSparrowFeather.getmService().disconnect();
            }

        }
    };


    //Attempts three reconnects over 10 seconds.
    private void attemptHubReconnect() {

        powerButton.setEnabled(false);

        txtTest.setText("Attempting to reconnect...");

        bluetoothStatus.setImageResource(R.mipmap.bt_pending);

        isReconnecting = true;

        final int timerVal = 11000;

        final int countDown = 1000;

        powerButton.setEnabled(false);

        new CountDownTimer(timerVal, countDown) {

            int counter = 0;

            @Override
            public void onTick(long millisUntilFinished) {

                double msFloor = millisUntilFinished / 1000;
                msFloor = Math.floor(msFloor);

                if (isConnected) {
                    this.cancel();
                }

                if (msFloor % 5 == 0) {

                    counter++;
                    powerButton.callOnClick();
                }
            }

            @Override
            public void onFinish() {

                if (!isConnected) {
                    txtTest.setText("Disconnected");
                    bluetoothStatus.setImageResource(R.mipmap.bt_disconnected);
                }
                powerButton.setEnabled(true);

            }
        }.start();
    }


    private void processHubResults(final byte[] txValue) {

        try {

            // Create a String object from the byte array returned by the BLE hardware
            String text = new String(txValue, StandardCharsets.UTF_8);

            if (EXTENDED_DEBUG) {
                Log.d(TAG, "Data Converted to raw string: [" + text + "]");
                Log.d(TAG, "Length of text: " + text.length());
            }

            char[] myNameChars = text.toCharArray();

            for (int i = 0; i < text.length(); i++) {
                if (EXTENDED_DEBUG) Log.d(TAG, "Char at loc = " + (text.charAt(i)) + " 0 ");
                if (myNameChars[i] == 10) {
                    if (EXTENDED_DEBUG) Log.d(TAG, "New Line detected!");

                }
                if (myNameChars[i] == 0) {
                    myNameChars[i] = ' ';
                    if (EXTENDED_DEBUG) Log.d(TAG, "Null Detected.");
                }
            }

            text = String.valueOf(myNameChars);

            if (EXTENDED_DEBUG)
                Log.d(TAG, "Raw Data Length: [" + txValue.length + "] Raw String: [" + text + "]");

            mDeviceDataLine += text;

            if (EXTENDED_DEBUG)
                Log.d(TAG, "Total Raw Bytes: " + txValue.length + " New String: [" + mDeviceDataLine + "]");

            // Remove any excess spaces from the string array
            text = text.trim();

            int[] intValues = new int[8];

            String[] values = text.split(",");

            if (EXTENDED_DEBUG)
                Log.d(TAG, "# of Vs: " + values.length);

            String[] lines = mDeviceDataLine.split("\n");

            if (EXTENDED_DEBUG)
                Log.d(TAG, "Lines Found: " + lines.length);

            if (lines.length > 2) {

                if (EXTENDED_DEBUG)
                    Log.d(TAG, "Rebuild the lines list and remove old lines.");

                mDeviceDataLine = "";

                if (EXTENDED_DEBUG) Log.d(TAG, "Process This Command: [" + lines[0] + "]");

                text = lines[0];
                values = text.split(",");

                if (EXTENDED_DEBUG)
                    Log.d(TAG, "Extrac Lines from Data Stream.. : [" + mDeviceDataLine + "]");

                for (int x = 1; x < lines.length; x++) {
                    mDeviceDataLine += lines[x];
                    if (x + 1 < lines.length) mDeviceDataLine += "\n";
                }

                if (EXTENDED_DEBUG)
                    Log.d(TAG, "New Lines List is: [" + mDeviceDataLine + "]");

                // all values have been detected.
                if (values.length > 7) {

                    if (EXTENDED_DEBUG) {
                        Log.d(TAG, "Evaluating Data from BLE Transmissions.  Calculation of values via parse int. start");
                        Log.d(TAG, "Extract Lines: " + values.length + " \nFrom: \"" + text + "\"");
                    }

                    for (int i = 0; i < values.length; i++) {

                        try {
                            intValues[i] = Integer.parseInt(values[i].trim());

                            if (EXTENDED_DEBUG)
                                Log.d(TAG, "Calculated - Parsed Value for Channel [" + i + "] = [" + intValues[i] + "]");

                        } catch (Exception e) {

                            Log.e(TAG, e.toString());

                            intValues[i] = 0;
                        }
                    }

                    packetCounter++;

                    //

                    BPMView.addData((intValues[0]));
                    //(intValues[0]/maxC)*70);
                    RESPView.addData((intValues[1]));
                    //(intValues[1]/maxC)*70);
                    // BPMView.FakeData();
                    // RESPView.FakeData();
                    SPView.addData((intValues[2]));
                    // SPView.FakeData();
                    // TempView.FakeData();
                    TempView.addData((intValues[3]));


                    // Update the string on the display for the app
                    //  (intValues[0]/maxC)*70
                 /*   System.out.print( (intValues[0]*70 /maxC) );
                    System.out.print(" X ");
                    System.out.println( (intValues[0]/maxC));
*/
                    bluetoothStatus.setImageResource(R.mipmap.bt_connected);
                    txtTest.setText(text + "-v:" + values.length);


                    //if mSparrow hub is connected, check that devices are receiving data
                    if (connectedState) {
                        checkDevicesConnectionState(intValues);
                    }

                    //if all devices are green (connected), enable capture, otherwise disable
                    if (!capture) {
                        toggleCaptureButton();
                    } else {
                        //if capture button has been pressed, begin capturing readings
                        captureReadings(intValues);
                    }

                }

            }

            if (EXTENDED_DEBUG)
                Log.d(TAG, "New Device DataLine: [" + mDeviceDataLine + "] \n");

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    String BleUardbuffer = "";

    private void processHubResultsML(final byte[] txValue) {

        try {


            String text = new String(txValue, "UTF-8");
//                            String text = new String(txValue, "UTF-8");

            Log.d(TAG, "DataLine:\n" + mDeviceDataLine);
            Log.d(TAG, "Raw Data Length: " + txValue.length);

            //Log.d(TAG, "is EOF here: " + txValue[txValue.length-1]);

            //???
            //text = text.trim();

            //                           text = text.replaceAll("\\r?\\n|\\r", "");

            if (mDeviceDataLine == null) {
                mDeviceDataLine = text;
            } else {
                mDeviceDataLine += text;
            }
            //text.trim();

            //text = text.trim();
            int[] intValues = new int[8];
            // String[] values = text.split(",");

            String[] Lines = mDeviceDataLine.split("\n");

            Log.d(TAG, "Lines Length: " + Lines.length);

            if (Lines.length > 1) {
                // for(int lineIndex = 0; lineIndex < Lines.length; lineIndex++) {

                String[] values = Lines[0].split(",");

                Log.d(TAG, "V: " + values.length);

                // txtTest.setText("Lines: " + Lines.length);

                //if (values.length >= 6){
                if (values.length >= 5) {

                    Log.d(TAG, "Extract Lines: " + values.length + " \nFrom: " + Lines[0]);

                    for (int i = 0; i < values.length; i++) {
                        intValues[i] = Integer.parseInt(values[i]);
                    }

                    // Update the string
                    txtTest.setText(Lines[0] + "Lines: " + Lines.length);

                    //if mSparrow hub is connected, check that devices are receiving data
                    if (connectedState) {
                        checkDevicesConnectionState(intValues);
                    }

                    //if all devices are green (connected), enable capture, otherwise disable
                    if (!capture) {
                        toggleCaptureButton();
                    } else {
                        //if capture button has been pressed, begin capturing readings
                        captureReadings(intValues);
                    }

                }

                // mDeviceDataLine = "";
                for (int lineIndex = 1; lineIndex < Lines.length; lineIndex++) {
                    mDeviceDataLine += Lines[lineIndex];
                    Log.d(TAG, "Adding data for next round of detection: " + Lines[lineIndex] + " - Line Index is: " + lineIndex);
                    //if (lineIndex  Lines.length) mDeviceDataLine += '\n';
                }
            }


            if (EXTENDED_DEBUG)
                Log.d(TAG, "New Device DataLine:\n" + mDeviceDataLine);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    private void service_init() {
        Intent bindIntent = new Intent(this, UartService.class);
        bindService(bindIntent, mSparrowFeather.mServiceConnection, getApplicationContext().BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
    }

    //Method which will disable the powerButton for 1 second after it has been clicked, this is to ensure
    //no button mashing will occur which could possibly result in app failure
    private void safeButtonTimer() {

        powerButton.setEnabled(false);
        new CountDownTimer(1000, 500) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                powerButton.setEnabled(true);

            }
        }.start();

    }


    private View.OnClickListener actionListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {


            switch (v.getId()) {

                case R.id.powerButton:

                    wasBtnPushed = true;

                    Log.d(TAG, "Clicked");

                    //If bluetooth adapter is not enabled, request permission to enable it
                    if (!mBtAdapter.isEnabled()) {
                        Log.i(TAG, "onClick - BT not enabled yet");
                        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                    } else {

                        try {

                            if (connectFlag) {


                                mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(mDeviceAddress);
                                String address = mDeviceAddress;
                                mSparrowFeather.getmService().connect(mDeviceAddress);


                                connectFlag = false;

                                //safeButtonTimer();

                                onCounter++;
                                offCounter++;

                            } else {

                                onCounter++;

                                disconnectHub();

                                connectFlag = true;

                                //safeButtonTimer();
                            }
                        } catch (Exception e) {
                            txtTest.setText("Disconnected");
                            Log.i("==", "Something went wrong: " + e);
                        }


                    }

                    break;

                case R.id.btnHubCapture:
                    setDeviceList();

                    // clear history from devices before we capture again.
                    resetCapture();
                    mDeviceDataLine = "";

                    setProgressBarVisibility(View.VISIBLE);
                    startCountdownTimer();
                    frameCounter = 0;
                    capture = true;
                    Handler h = new Handler();
                    h.postDelayed(new Runnable() {
                        public void run() {

                            // Actions to do after Measure_Time
                            capture = false;

                            Log.d(TAG, "Total Device List: " + deviceList.size());


                            for (int i = 0; i < deviceList.size(); i++) {
                                double readingsSize = deviceList.get(i).getReadings().size();
                                double measureTime = deviceList.get(i).getMeasureTime() / 1000;
                                deviceList.get(i).setSamplesPerSec(Math.round(readingsSize / measureTime));

                                Log.d(TAG, "Finished Calculations Samples per seconds: " + (readingsSize / measureTime));
                            }

                            setJsonHeader();
                            sendDeviceDataToServer();
                            tempStoreData(getApplicationContext(), readingsJSON);

                            // Ask Feather to stop transmitting data
                            mSparrowFeather.sendStopMessage();

                            Intent myIntent = new Intent(mSparrowMainActivity.this, MultiResultsActivity.class);
                            mSparrowMainActivity.this.startActivity(myIntent);
                        }

                    }, MEASURE_TIME);
                    break;
            }
        }
    };

    private void disconnectHub() {

        wasBtnPushed = false;

        //reset capture button and connection dots
        btnCapture.setEnabled(false);

        deviceList.clear();

        isConnected = false;

        if (!isReconnecting) {
            bluetoothStatus.setImageResource(R.mipmap.bt_disconnected);
        }

        powerButton.setImageResource(R.drawable.powerred);
        powerButton.setTag(R.drawable.powerred);

        try {
            mSparrowFeather.getmService().disconnect();

        } catch (Exception e) {

        }
    }

    public void sendData(final String jsonString) {

        new Thread(new Runnable() {
            public void run() {
                try {
                    Log.d(TAG, "Thread started for sendData to PHP server -- ");

                    URL object = new URL(SERVER_URL);

                    HttpURLConnection con = (HttpURLConnection) object.openConnection();
                    con.setDoOutput(true);
                    con.setDoInput(true);
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setRequestProperty("Accept", "application/json");
                    con.setRequestMethod("PUT");


                    OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                    wr.write(jsonString);
                    wr.flush();

                    //display what returns the POST request

                    StringBuilder sb = new StringBuilder();
                    int HttpResult = con.getResponseCode();
                    if (HttpResult == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(
                                new InputStreamReader(con.getInputStream(), "utf-8"));
                        String line = null;
                        while ((line = br.readLine()) != null) {
                            sb.append(line + "\n");
                        }

                        // Log the server return message.
                        Log.d(TAG, "Sending Data to server: " + sb.toString());

                        br.close();
                    } else {
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    //store data in cache memory for passing to fragments
    private void tempStoreData(Context context, String fcontent) {
        try {
            File file = new File(context.getCacheDir(), "results.txt");
            FileWriter fw = null;
            fw = new FileWriter(file.getAbsoluteFile(), false);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(fcontent);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Method which will save the user screen shot and proceed to share activity
    public void beginSaveProcess(View view){

        //Screenshot the window
        View v = getWindow().getDecorView().getRootView();
        v.setDrawingCacheEnabled(true);
        Bitmap bmp = Bitmap.createBitmap(v.getDrawingCache());

        //Compressing the bitmap into a byte array so that it can be passed through the activity
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        //Saving the image locally and getting its path
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bmp, "newPneu - " + Calendar.getInstance().getTime(), null);

        //Intent to the SaveData page, passing data context and image path
        Intent intent = new Intent(mSparrowMainActivity.this, ActivitySaveData.class);
        intent.putExtra("DATA_CONTEXT", "newPneu");
        intent.putExtra("DATA_ARRAY", byteArray);
        intent.putExtra("DATA_PATH", path);

        startActivityForResult(intent,0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Refresh the activity is the user is coming from the ActivitySaveData, required as this prevents bug where image data sent is duplicate
        if (requestCode == 0) {
            if (resultCode == RESULT_CANCELED) {
                finish();
                startActivity(getIntent());
            }
        }
    }
}