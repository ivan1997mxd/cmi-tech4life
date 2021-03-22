package com.estethapp.media.recording;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.widget.Toast;

import java.util.ArrayList;

import com.estethapp.media.R;
import com.estethapp.media.adapter.BluetoothDeviceAdapter;
import com.estethapp.media.util.BluetoothUtil;

/**
 * Created by Irfan Ali on 1/14/2017.
 */
public abstract class BluetoothSearchingUtils {

    public Context context;

    private BluetoothDevice bdDevice;
    private BluetoothAdapter bluetoothAdapter = null;
    private ArrayList<BluetoothUtil> blueToothList;
    private ArrayList<BluetoothDevice> BluetoothObjectArraylist = null;
    private ProgressDialog mBluetoothConnectProgressDialog;
    private BluetoothDevice mBluetoothDevice;
    private BluetoothSocket mBluetoothSocket;
    private BluetoothSocket mTempBluetoothSocket;
    private BluetoothDeviceAdapter adapter;

    public BluetoothSearchingUtils(Context context) {

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.startDiscovery();
        onBluetoothAdapterInstance(bluetoothAdapter);
        blueToothList = new ArrayList<BluetoothUtil>();
        BluetoothObjectArraylist = new ArrayList<>();


    }

    public void registerReceiver(Context context) {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(myReceiver, filter);
    }

    public void unRegisterReceiver(Context context) {
        if (myReceiver != null) {
            context.unregisterReceiver(myReceiver);
        }
    }

    public abstract void onBluetoothFoundObject(ArrayList<BluetoothDevice> list);

    public abstract void onBluetoothFoundList(ArrayList<BluetoothUtil> obj);

    public abstract void onBluetoothAdapterInstance(BluetoothAdapter bluetoothAdapter);

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Message msg = Message.obtain();
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                BluetoothUtil util = new BluetoothUtil();
                util.setAddress(device.getAddress());
                util.setDeviceName(device.getName());
                util.setPaired(false);
                blueToothList.add(util);
                BluetoothObjectArraylist.add(device);

                onBluetoothFoundObject(BluetoothObjectArraylist);
                onBluetoothFoundList(blueToothList);
            }
            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                Toast.makeText(context, context.getString(R.string.txt_bt_connected), Toast.LENGTH_SHORT).show();
            }
        }
    };


}
