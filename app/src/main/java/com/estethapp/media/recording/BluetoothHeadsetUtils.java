package com.estethapp.media.recording;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.util.Log;

import com.estethapp.media.helper.ExminatingHelper;

/**
 * Created by Irfan Ali on 1/13/2017.
 */
public abstract class BluetoothHeadsetUtils {
    private Context mContext;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothHeadset mBluetoothHeadset;
    private BluetoothDevice mConnectedHeadset;

    private AudioManager mAudioManager;

    private boolean mIsCountDownOn;
    private boolean mIsStarting;
    private boolean mIsOnHeadsetSco;
    private boolean mIsStarted;
    ExminatingHelper exminatingHelper = ExminatingHelper.getInstance();
    private static final String TAG = "BluetoothHeadsetUtils";

    /**
     * Constructor
     *
     * @param context
     */
    public BluetoothHeadsetUtils(Context context) {
        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }

    /**
     * Call this to start BluetoothHeadsetUtils functionalities.
     *
     * @return The return value of startBluetooth() or startBluetooth11()
     */
    public boolean start() {
        if (!mIsStarted) {
            mIsStarted = true;
            mIsStarted = startBluetooth();
            /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                mIsStarted = startBluetooth();
            } else {
                System.out.println("");
               // mIsStarted = startBluetooth11();
            }*/
        }

        return mIsStarted;
    }

    /**
     * Should call this on onResume or onDestroy.
     * Unregister broadcast receivers and stop Sco audio connection
     * and cancel count down.
     */
    public void stop() {
        if (mIsStarted) {
            mIsStarted = false;
            stopBluetooth();
            /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                stopBluetooth();
            } else {
                System.out.println("");
               // stopBluetooth11();
            }*/
        }
    }

    /**
     * @return true if audio is connected through headset.
     */
    public boolean isOnHeadsetSco() {
        return mIsOnHeadsetSco;
    }

    public abstract void onHeadsetDisconnected();

    public abstract void onHeadsetConnected();

    public abstract void onScoAudioDisconnected();

    public abstract void onScoAudioConnected();


    @SuppressWarnings("deprecation")
    private boolean startBluetooth() {
        Log.d(TAG, "startBluetooth"); //$NON-NLS-1$

        // Device support bluetooth
        if (mBluetoothAdapter != null) {
            if (mAudioManager.isBluetoothScoAvailableOffCall()) {
                mContext.registerReceiver(mBroadcastReceiver,
                        new IntentFilter(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED));

                mIsCountDownOn = true;
                // mCountDown repeatedly tries to start bluetooth Sco audio connection.
                mCountDown.start();

                // need for audio sco, see mBroadcastReceiver
                mIsStarting = true;

                return true;
            }
        }

        return false;
    }

    private CountDownTimer mCountDown = new CountDownTimer(10000, 1000) {

        @SuppressWarnings("synthetic-access")
        @Override
        public void onTick(long millisUntilFinished) {
            // When this call is successful, this count down timer will be canceled.
            mAudioManager.startBluetoothSco();

            Log.d(TAG, "\nonTick start bluetooth Sco"); //$NON-NLS-1$
        }

        @SuppressWarnings("synthetic-access")
        @Override
        public void onFinish() {
            // Calls to startBluetoothSco() in onStick are not successful.
            // Should implement something to inform user of this failure
            mIsCountDownOn = false;
            Log.d(TAG, "\nonFinish fail to connect to headset audio"); //$NON-NLS-1$
        }
    };

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @SuppressWarnings({"deprecation", "synthetic-access"})
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int getRecrodingDevice = exminatingHelper.getRecordingDevice();
            if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
                mConnectedHeadset = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                BluetoothClass bluetoothClass = mConnectedHeadset.getBluetoothClass();
                if (bluetoothClass != null) {
                    // Check if device is a headset. Besides the 2 below, are there other
                    // device classes also qualified as headset?
                    int deviceClass = bluetoothClass.getDeviceClass();
                    if (deviceClass == BluetoothClass.Device.AUDIO_VIDEO_HANDSFREE
                            || deviceClass == BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET) {
                        // start bluetooth Sco audio connection.
                        // Calling startBluetoothSco() always returns faIL here,
                        // that why a count down timer is implemented to call
                        // startBluetoothSco() in the onTick.
                        mAudioManager.setMode(AudioManager.MODE_IN_CALL);
                        mIsCountDownOn = true;
                        mCountDown.start();

                        // override this if you want to do other thing when the device is connected.
                        onHeadsetConnected();
                    }
                }

                Log.d(TAG, mConnectedHeadset.getName() + " connected"); //$NON-NLS-1$
            } else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
                Log.d(TAG, "Headset disconnected"); //$NON-NLS-1$

                if (mIsCountDownOn) {
                    mIsCountDownOn = false;
                    mCountDown.cancel();
                }

                // override this if you want to do other thing when the device is disconnected.
                onHeadsetDisconnected();
            } else if (action.equals(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED) && getRecrodingDevice != 2) {
                int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE,
                        AudioManager.SCO_AUDIO_STATE_ERROR);

                if (state == AudioManager.SCO_AUDIO_STATE_CONNECTED) {
                    mIsOnHeadsetSco = true;

                    if (mIsStarting) {
                        // When the device is connected before the application starts,
                        // ACTION_ACL_CONNECTED will not be received, so call onHeadsetConnected here
                        mIsStarting = false;
                        onHeadsetConnected();
                    }

                    if (mIsCountDownOn) {
                        mIsCountDownOn = false;
                        mCountDown.cancel();
                    }

                    // override this if you want to do other thing when Sco audio is connected.
                    onScoAudioConnected();

                    Log.d(TAG, "Sco connected"); //$NON-NLS-1$
                } else if (state == AudioManager.SCO_AUDIO_STATE_DISCONNECTED) {
                    Log.d(TAG, "Sco disconnected"); //$NON-NLS-1$

                    // Always receive SCO_AUDIO_STATE_DISCONNECTED on call to startBluetooth()
                    // which at that stage we do not want to do anything. Thus the if condition.
                    if (!mIsStarting) {
                        mIsOnHeadsetSco = false;

                        // Need to call stopBluetoothSco(), otherwise startBluetoothSco()
                        // will not be successful.
                        mAudioManager.stopBluetoothSco();

                        // override this if you want to do other thing when Sco audio is disconnected.
                        onScoAudioDisconnected();
                    }
                }
            }
        }
    };

    private void stopBluetooth() {
        Log.d(TAG, "stopBluetooth"); //$NON-NLS-1$

        if (mIsCountDownOn) {
            mIsCountDownOn = false;
            mCountDown.cancel();
        }

        // Need to stop Sco audio connection here when the app
        // change orientation or close with headset still turns on.
        mContext.unregisterReceiver(mBroadcastReceiver);
        mAudioManager.stopBluetoothSco();
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
    }
}
