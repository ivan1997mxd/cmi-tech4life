package com.estethapp.media.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.crashlytics.android.Crashlytics;
import com.estethapp.media.MainActivity;
import com.estethapp.media.R;
import com.estethapp.media.adapter.BluetoothDeviceAdapter;
import com.estethapp.media.helper.ExminatingHelper;
import com.estethapp.media.recording.BluetoothHeadsetUtils;
import com.estethapp.media.recording.BluetoothSearchingUtils;
import com.estethapp.media.util.BluetoothStatus;
import com.estethapp.media.util.BluetoothUtil;
import com.estethapp.media.util.Constants;
import com.estethapp.media.util.ExminingPrefs;
import com.estethapp.media.util.SCODeviceHelper;
import com.estethapp.media.view.ProgressDialogView;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.concurrency.AsyncTask;

public class PairingFragment extends Fragment {

    private static PairingFragment fragment;
    Context context;

    private View root;
    private Button startBtn, testBtn;
    private ImageView addDeviceBtn;
    private static TextView txtDeviceConnected;
    public String labelInfo;
    BluetoothHelper mBluetoothHelper;
    public static FragmentManager fragmentManager;
    BluetoothStatus deviceStatus = BluetoothStatus.getInstance();

    private static SharedPreferences prefsInstance;
    private ExminingPrefs prefs;

    private static ArrayList<BluetoothUtil> blueToothList;
    private static ArrayList<BluetoothDevice> BluetoothObjectArraylist = null;
    private static ArrayList<String> bluethoothNameList;
    private static BluetoothSocket mBluetoothSocket;
    public static BluetoothAdapter bluetoothAdapter = null;
    private BluetoothSocket mTempBluetoothSocket;
    public SCODeviceHelper ScoBluetToothInstance = SCODeviceHelper.getInstance();

    ProgressDialogView dialogInstance = ProgressDialogView.getInstance();
    ProgressDialog progressDialog;

    ExminatingHelper exminatingHelper = ExminatingHelper.getInstance();
    public BluetoothSearch bluetooThSearchingInstance;

    private BluetoothDevice bdDevice;
    private BluetoothDevice mBluetoothDevice;
    int currentapiVersion;
    ToggleButton heartLungsswitch;
    private int countConnection = 0;
    private Dialog dialog;
    MusicIntentReceiver myReceiver;
    IntentFilter filterAuxCableReceiver;

    public PairingFragment() {
    }

    static boolean isBluetooConnectionRequest = false;

    public static PairingFragment newInstance(boolean isRequest) {
        isBluetooConnectionRequest = isRequest;
        PairingFragment instance = new PairingFragment();
        return instance;
    }

    public static PairingFragment getInstance() {
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragment = this;
        return inflater.inflate(R.layout.activity_pairing_screen, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        context = getActivity();
        root = view;
        fragment = this;
        Fabric.with(context, new Crashlytics());
        bluethoothNameList = new ArrayList<>();
        blueToothList = new ArrayList<BluetoothUtil>();
        BluetoothObjectArraylist = new ArrayList<BluetoothDevice>();

        setupAndStartAnimations();

        progressDialog = dialogInstance.setProgressDialog(context, context.getString(R.string.connecting_to_bluetooth_device));

        prefs = ExminingPrefs.getInstance();
        prefsInstance = prefs.init(context);

        startBtn = (Button) view.findViewById(R.id.btn_start);
        testBtn = (Button) view.findViewById(R.id.btn_test);
        addDeviceBtn = (ImageView) view.findViewById(R.id.add_btn);
        txtDeviceConnected = (TextView) view.findViewById(R.id.txtDeviceConnected);
        heartLungsswitch = (ToggleButton) view.findViewById(R.id.connection_toggle);


        if (!MainActivity.isBluetoothEnabled()) {
            prefsInstance.edit().putString(Constants.BLUE_ADDRESS, "").commit();
            prefsInstance.edit().putString(Constants.BLUE_Name, "").commit();
            prefsInstance.edit().putString(Constants.UDID, "").commit();
        }


        if (MainActivity.isBluetoothEnabled()) {
            if (BluetoothObjectArraylist.size() <= 0) {
                bluetooThSearchingInstance = new BluetoothSearch(context);
                bluetooThSearchingInstance.registerReceiver(context);
            }
        } else {
            exminatingHelper.setRecordingDevice(-1);
            startAuxCableReceiber();
        }

        String savedAddress = prefsInstance.getString(Constants.BLUE_ADDRESS, "");
        String BLUE_Name = prefsInstance.getString(Constants.BLUE_Name, "");


        if (isBluetooConnectionRequest) {
            addDeviceBtn.setVisibility(View.VISIBLE);
        }

        addDeviceBtn.setVisibility(View.INVISIBLE);

        int getRecrodingDevice = exminatingHelper.getRecordingDevice();
        if (!savedAddress.equals("")) {
//            labelInfo = "" + BLUE_Name + " is connected";
//            txtDeviceConnected.setText(labelInfo);
//            txtDeviceConnected.setTextColor(getResources().getColor(R.color.green));

            String udid = prefsInstance.getString(Constants.UDID, "");
            if (!udid.equals("")) {
                String address = prefsInstance.getString(Constants.BLUE_ADDRESS, "");
                bdDevice = bluetoothAdapter.getRemoteDevice(address);

                new DefaultConnectDeviceAsync(udid).execute();
            }

            heartLungsswitch.setChecked(true);
            exminatingHelper.setRecordingDeviceConnected(true);
            exminatingHelper.setRecordingDevice(2);
            addDeviceBtn.setVisibility(View.VISIBLE);


        } else if (getRecrodingDevice == -1) {
            txtDeviceConnected.setVisibility(View.VISIBLE);
            txtDeviceConnected.setText(R.string.devices_not_connnected);
            txtDeviceConnected.setTextColor(getResources().getColor(R.color.red));

            prefsInstance.edit().putString(Constants.BLUE_ADDRESS, "").commit();
            prefsInstance.edit().putString(Constants.BLUE_Name, "").commit();
            prefsInstance.edit().putString(Constants.UDID, "").commit();
        } else if (getRecrodingDevice == 1 && exminatingHelper.isRecordingDeviceConnected()) { // setRecordingDevice(1) Aux cable
            txtDeviceConnected.setVisibility(View.VISIBLE);
            txtDeviceConnected.setText(R.string.connected);
            txtDeviceConnected.setTextColor(getResources().getColor(R.color.green));
            addDeviceBtn.setVisibility(View.INVISIBLE);
            heartLungsswitch.setChecked(false);
            exminatingHelper.setRecordingDevice(1);
        } else {
            addDeviceBtn.setVisibility(View.VISIBLE);
            txtDeviceConnected.setVisibility(View.VISIBLE);
            txtDeviceConnected.setText(getString(R.string.txt_device_disconnect));
            txtDeviceConnected.setTextColor(Color.parseColor("#cf262d"));
        }

        currentapiVersion = android.os.Build.VERSION.SDK_INT;
        int version = android.os.Build.VERSION_CODES.LOLLIPOP;
        if (currentapiVersion <= version) {
            heartLungsswitch.setChecked(true);
            addDeviceBtn.setVisibility(View.VISIBLE);
        }


        heartLungsswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    exminatingHelper.setRecordingDeviceConnected(false);
                    addDeviceBtn.setVisibility(View.VISIBLE);

                    txtDeviceConnected.setText(context.getString(R.string.txt_device_disconnect));
                    txtDeviceConnected.setTextColor(Color.parseColor("#cf262d"));

                    try {
                        MusicIntentReceiver myRec1eiver = new MusicIntentReceiver();
                        getActivity().unregisterReceiver(myRec1eiver);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {

                    if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {

                        String savedAddress = prefsInstance.getString(Constants.BLUE_ADDRESS, "");
                        if (!savedAddress.equals("")) {
                            Toast.makeText(getActivity(), getString(R.string.txt_disconnect_bluetooth_first), Toast.LENGTH_SHORT).show();
                            heartLungsswitch.setChecked(true);
                            addDeviceBtn.setVisibility(View.VISIBLE);
                        } else {

                            exminatingHelper.setRecordingDeviceConnected(false);
                            exminatingHelper.setRecordingDevice(1);
                            addDeviceBtn.setVisibility(View.INVISIBLE);
                            heartLungsswitch.setChecked(false);

                            exminatingHelper.setRecordingDevice(-1);
                            startAuxCableReceiber();
                        }

                    } else {
                        addDeviceBtn.setVisibility(View.VISIBLE);
                        heartLungsswitch.setChecked(true);
                        Toast.makeText(context, getString(R.string.txt_version_support), Toast.LENGTH_SHORT).show();
                    }


                }
            }
        });

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (exminatingHelper.isRecordingDeviceConnected() || deviceStatus.isSCODeviceStart()) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, ExaminingFragment.newInstance(), null)
                            .commit();
                } else {
                    Toast.makeText(context, getString(R.string.txt_recording_device_notconnected), Toast.LENGTH_SHORT).show();
                }
            }
        });

        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deviceStatus.isDeviceConnected() & deviceStatus.isSCODeviceStart()) {
                    Toast.makeText(context, getString(R.string.txt_connected_ready), Toast.LENGTH_SHORT).show();
                } else if (deviceStatus.isDeviceConnected()) {
                    Toast.makeText(context, getString(R.string.txt_headset_connected_not_ready), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, getString(R.string.txt_recording_device_notconnected), Toast.LENGTH_SHORT).show();
                }

            }
        });

        addDeviceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countConnection = 0;
                if (!MainActivity.isBluetoothEnabled()) {
                    showGPSDisabledAlertToUser();
                } else {
                    if (bluethoothNameList.size() >= 0) {
                        for (BluetoothUtil util : blueToothList) {
                            bluethoothNameList.add(util.getDeviceName());
                        }
                    }
                    pairdDeviceDialog();
                }
            }
        });


        super.onViewCreated(view, savedInstanceState);
    }

    private void setupAndStartAnimations() {
        Animation animation;
        Animation animation2;
        animation = AnimationUtils.loadAnimation(context,
                R.anim.bottom_to_original);
        animation2 = AnimationUtils.loadAnimation(context, R.anim.zoom_in);
//        Animation fadeIn = new AlphaAnimation(0, 1);
//        fadeIn.setInterpolator(new AccelerateInterpolator());
//        fadeIn.setDuration(700);

        root.findViewById(R.id.ivAuxCable).setAnimation(animation2);
        root.findViewById(R.id.ivBluetooth).setAnimation(animation2);
        root.findViewById(R.id.ivEsteth).setAnimation(animation);
    }

    public void startAuxCableReceiber() {
        try {
            myReceiver = new MusicIntentReceiver();
            String action;

            if (Build.VERSION.SDK_INT >= 21) {
                action = AudioManager.ACTION_HEADSET_PLUG;
            } else {
                action = Intent.ACTION_HEADSET_PLUG;
            }
            filterAuxCableReceiver = new IntentFilter(action);
            getActivity().registerReceiver(myReceiver, filterAuxCableReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean connect(String bdDeviceName, String udid, BluetoothDevice bdDevice) {
        boolean isConnected = false;
        try {

            final UUID MY_UUID = UUID.fromString(udid);
            mBluetoothSocket = bdDevice.createRfcommSocketToServiceRecord(MY_UUID);
            Log.d("bluetooth ", udid);
            bluetoothAdapter.cancelDiscovery();
            mBluetoothSocket.connect();

            if (mBluetoothSocket.isConnected()) {
                exminatingHelper.setRecordingDevice(2);
                exminatingHelper.setRecordingDeviceConnected(true);

                labelInfo = " " + bdDeviceName + " " + getContext().getString(R.string.is_connected);

/*
                txtDeviceConnected.setVisibility(View.VISIBLE);
                txtDeviceConnected.setText(labelInfo);
                txtDeviceConnected.setTextColor(getResources().getColor(R.color.applozic_green_color));
*/


                isConnected = true;
            }
        } catch (IOException eConnectException) {
           /* try {
                mBluetoothSocket.close();
            } catch(IOException close) {
                Log.d("CONNECTTHREAD", "Could not close connection:" + eConnectException.toString());
            }*/

            /*if(!isConnected) {
                if (countConnection != 2) {
                    ++countConnection;
                    if (bdDevice != null)
                        connect(bdDeviceName, udid, bdDevice);
                } else {
                    exminatingHelper.setRecordingDevice(-1);
                    labelInfo = "Couldn't connect with device " + bdDeviceName;
                    isConnected = false;
                }
            }*/
            isConnected = false;
        }

        return isConnected;
    }


    public boolean createBond(BluetoothDevice btDevice)
            throws Exception {
        Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
        //  Method createBondMethod = class1.getMethod("createBond");
        Method createBondMethod = btDevice.getClass().getMethod("createBond", (Class[]) null);
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class BluetoothHelper extends BluetoothHeadsetUtils {
        public BluetoothHelper(Context context) {
            super(context);
        }

        @Override
        public void onScoAudioDisconnected() {
            Activity activity = getActivity();
            if (activity != null) {
                txtDeviceConnected.setTextColor(getResources().getColor(android.R.color.holo_red_dark
                ));
                txtDeviceConnected.setText(getString(R.string.txt_aux_disconnected));
                //deviceStatus.setSCODeviceStart(false);
                ScoBluetToothInstance.setDeviceReadyForRecording(false);
            }
        }

        @Override
        public void onScoAudioConnected() {
            deviceStatus.setSCODeviceStart(true);
            txtDeviceConnected.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            txtDeviceConnected.setText(R.string.connected);
            ScoBluetToothInstance.setDeviceReadyForRecording(true);
        }

        @Override
        public void onHeadsetDisconnected() {
            txtDeviceConnected.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            txtDeviceConnected.setText(getString(R.string.bluetooth_device_disconnected));
            deviceStatus.setDeviceConnected(false);
            ScoBluetToothInstance.setDeviceReadyForRecording(false);
        }

        @Override
        public void onHeadsetConnected() {
            txtDeviceConnected.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            txtDeviceConnected.setText(context.getString(R.string.bluetooth_device_connected));
            deviceStatus.setSCODeviceStart(true);
            deviceStatus.setDeviceConnected(true);

        }
    }


    public static class BluetoothSearch extends BluetoothSearchingUtils {

        public BluetoothSearch(Context context) {
            super(context);
        }

        @Override
        public void onBluetoothFoundObject(ArrayList<BluetoothDevice> device) {
            BluetoothObjectArraylist.addAll(device);

        }

        @Override
        public void onBluetoothFoundList(ArrayList<BluetoothUtil> list) {
            blueToothList.addAll(list);
        }

        @Override
        public void onBluetoothAdapterInstance(BluetoothAdapter adapter) {
            bluetoothAdapter = adapter;
            Set<BluetoothDevice> pairedDevice = bluetoothAdapter.getBondedDevices();
            if (pairedDevice.size() > 0) {
                for (BluetoothDevice device : pairedDevice) {
                    BluetoothUtil util = new BluetoothUtil();
                    if (device != null) {
                        util.setAddress(device.getAddress());
                        util.setDeviceName(device.getName());
                    }
                    util.setPaired(true);
                    blueToothList.add(util);
                    BluetoothObjectArraylist.add(device);
                }
            }
        }
    }

    public void pairdDeviceDialog() {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.device_pair_layout);
        dialog.setTitle(getString(R.string.txt_devices));

        ListView devicesList = (ListView) dialog.findViewById(R.id.devicespairedlistView);
        BluetoothDeviceAdapter adapter = new BluetoothDeviceAdapter(context, blueToothList);
        devicesList.setAdapter(adapter);

        devicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {


                countConnection = 0;
                dialog.dismiss();
                bdDevice = BluetoothObjectArraylist.get(position);

                String savedAddress = bdDevice.getAddress();
                String BLUE_Name = bdDevice.getName();
                if (!savedAddress.equals("")) {
                    labelInfo = " " + BLUE_Name + " " + getContext().getString(R.string.is_connected);

                    prefsInstance.edit().putString(Constants.BLUE_ADDRESS, savedAddress).commit();
                    prefsInstance.edit().putString(Constants.BLUE_Name, BLUE_Name).commit();
                    countConnection = 0;

                    String udid = prefsInstance.getString(Constants.UDID, "");
                    if (!udid.equals("")) {
                        final UUID MY_UUID = UUID.fromString(udid);
                        try {
                            mBluetoothSocket = bdDevice.createRfcommSocketToServiceRecord(MY_UUID);
                            if (!mBluetoothSocket.isConnected())
                                new ConnectDeviceAsync().execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        new ConnectDeviceAsync().execute();
                    }
                }
            }
        });
        dialog.show();
    }

    public class DefaultConnectDeviceAsync extends AsyncTask<Boolean, Boolean, Boolean> {

        public String udid;

        public DefaultConnectDeviceAsync(String udid) {
            this.udid = udid;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }


        @Override
        protected Boolean doInBackground(Boolean... voids) {

            boolean isConnct = false;
            try {
                createBond(bdDevice);

                String bdDeviceName = bdDevice.getName();

                isConnct = connect(bdDeviceName, udid, bdDevice);
                if (isConnct) {
                    prefsInstance.edit().putString(Constants.UDID, udid).commit();
                } else {
                    if (countConnection != 2) {
                        ++countConnection;
                        if (bdDevice != null)
                            isConnct = connect(bdDeviceName, udid, bdDevice);
                    } else {
                        exminatingHelper.setRecordingDevice(-1);
                        labelInfo = getString(R.string.cannot_connect_to) + " " + bdDeviceName;

                    }
                }
                return isConnct;
            } catch (Exception e) {
                e.printStackTrace();
                labelInfo = getString(R.string.could_not_pair_device);
            }
            return isConnct;
        }

        @Override
        protected void onPostExecute(Boolean isConnected) {

            if (isConnected) {

                mBluetoothHelper = new BluetoothHelper(context);
                mBluetoothHelper.start();
                ScoBluetToothInstance.setmBluetoothHelper(mBluetoothHelper);
                if (isAdded()) {
                    txtDeviceConnected.setText(labelInfo);
                    txtDeviceConnected.setTextColor(getResources().getColor(R.color.green));
                }
            } else {
                prefsInstance.edit().putString(Constants.BLUE_ADDRESS, "").commit();
                prefsInstance.edit().putString(Constants.BLUE_Name, "").commit();
                prefsInstance.edit().putString(Constants.UDID, "").commit();
                if (isAdded()) {
                    txtDeviceConnected.setText(labelInfo);
                    txtDeviceConnected.setTextColor(getResources().getColor(R.color.red));
                }

            }
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            txtDeviceConnected.setVisibility(View.VISIBLE);
            txtDeviceConnected.setText(labelInfo);
        }

    }

    public class ConnectDeviceAsync extends AsyncTask<Boolean, Boolean, Boolean> {

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }


        @Override
        protected Boolean doInBackground(Boolean... voids) {

            boolean isConnct = false;
            try {
                createBond(bdDevice);

                String address = bdDevice.getAddress();
                mBluetoothDevice = bluetoothAdapter.getRemoteDevice(address);

                BluetoothDevice hxm = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(bdDevice.getAddress());
                ParcelUuid[] getUuidsMethod = mBluetoothDevice.getUuids();
                String bdDeviceName = bdDevice.getName();

                String st0 = getUuidsMethod[0].getUuid().toString();

                isConnct = connect(bdDeviceName, st0, mBluetoothDevice);

                if (isConnct) {
                    prefsInstance.edit().putString(Constants.UDID, st0).commit();
                } else {
                    if (countConnection != 2) {
                        ++countConnection;
                        if (bdDevice != null) {
                            isConnct = connect(bdDeviceName, st0, mBluetoothDevice);
                            if (!isConnct)
                                labelInfo = getString(R.string.could_not_connect) + " " + bdDeviceName;
                        }

                    } else {
                        exminatingHelper.setRecordingDevice(-1);
                        labelInfo = getString(R.string.could_not_connect) + " " + bdDeviceName;

                    }
                }
                return isConnct;
            } catch (Exception e) {
                e.printStackTrace();
                labelInfo = getString(R.string.could_not_pair_device);
            }
            return isConnct;
        }

        @Override
        protected void onPostExecute(Boolean isConnected) {

            if (isConnected) {

                mBluetoothHelper = new BluetoothHelper(context);
                mBluetoothHelper.start();
                ScoBluetToothInstance.setmBluetoothHelper(mBluetoothHelper);
                if (isAdded()) {
                    txtDeviceConnected.setText(labelInfo);
                    txtDeviceConnected.setTextColor(getResources().getColor(R.color.green));
                }
            } else {
                prefsInstance.edit().putString(Constants.BLUE_ADDRESS, "").commit();
                prefsInstance.edit().putString(Constants.BLUE_Name, "").commit();
                prefsInstance.edit().putString(Constants.UDID, "").commit();

                if (isAdded()) {
                    txtDeviceConnected.setText(labelInfo);
                    txtDeviceConnected.setTextColor(getResources().getColor(R.color.red));
                }

            }
            dialog.dismiss();
            progressDialog.dismiss();
            txtDeviceConnected.setVisibility(View.VISIBLE);
            txtDeviceConnected.setText(labelInfo);
        }

    }


    private void showGPSDisabledAlertToUser() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setMessage(
                        getString(R.string.txt_bluetooth_not_enabled))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.txt_enable),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intentOpenBluetoothSettings = new Intent();
                                intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                                startActivityForResult(intentOpenBluetoothSettings, 5000);
                            }
                        });
        alertDialogBuilder.setNegativeButton(getString(R.string.txt_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public static class MusicIntentReceiver extends BroadcastReceiver {
        ExminatingHelper exminatingHelper = ExminatingHelper.getInstance();

        @Override
        public void onReceive(Context context, Intent intent) {
            int getRecrodingDevice = exminatingHelper.getRecordingDevice();
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG) && getRecrodingDevice != 2) {
                int state = intent.getIntExtra("state", -1);
                switch (state) {
                    case 0:
                        exminatingHelper.setRecordingDevice(-1);
                        txtDeviceConnected.setText(R.string.headset_disconnected);
                        txtDeviceConnected.setTextColor(context.getResources().getColor(R.color.red));
                        break;
                    case 1:
                        exminatingHelper.setRecordingDevice(1);
                        txtDeviceConnected.setText(R.string.connected);
                        exminatingHelper.setRecordingDeviceConnected(true);
                        txtDeviceConnected.setTextColor(context.getResources().getColor(R.color.green));
                        break;
                    default:
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            getActivity().registerReceiver(myReceiver, filterAuxCableReceiver);
            bluetooThSearchingInstance.registerReceiver(context);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStop() {
        try {
            getActivity().unregisterReceiver(myReceiver);
            bluetooThSearchingInstance.unRegisterReceiver(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStop();
    }
}
