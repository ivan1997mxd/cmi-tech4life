package com.estethapp.media.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.estethapp.media.R;

public class AudioInOutChangeReceiver extends BroadcastReceiver {

	private static AudioInOutChangeReceiver mInstance;

	private static final String ACTION_HEADSET_PLUG_STATE = "android.intent.action.HEADSET_PLUG";

	private AudioManager mAudioManager;

	private BluetoothHeadsetManager mBTHeadSetManager;

	private Context mContext;

	private BluetoothHeadset mBluetoothHeadset;

	private AudioInOutChangeReceiver(Context aContext)
	{

		if (mContext == null)
			mContext = aContext;

		initiBTHeadSetConfiguration();
	}


	public static AudioInOutChangeReceiver getInstance(Context aContext)
	{
		if(mInstance == null)
		{
			mInstance = new AudioInOutChangeReceiver(aContext);
		}
		return mInstance;
	}


	public Context getActiveContext()
	{
		return mContext;
	}


	private void initiBTHeadSetConfiguration()
	{
		if (mBTHeadSetManager == null)
			mBTHeadSetManager = new BluetoothHeadsetManager(mContext);

		mBTHeadSetManager.addListener(mHeadsetCallback);

		// if BT is turn on then we move next step
		if(mBTHeadSetManager.hasEnableBluetooth())
		{
			mBTHeadSetManager.connectionToProxy();
		}
	}

	private void resetBTHeadSetConfiguration()
	{
		mBTHeadSetManager.disconnectProxy();
	}


	@Override
	public void onReceive(Context context, Intent intent)
	{
		// initial audio manager
		initiAudioManager(context);

		final String action = intent.getAction();
		// check action should not empty
		if(TextUtils.isEmpty(action))return;

		 //Broadcast Action: Wired Headset plugged in or unplugged.
		 if (action.equals(ACTION_HEADSET_PLUG_STATE))
		 {
			 updateHeadSetDeviceInfo(context, intent);

		} else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)
				|| BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)
				|| BluetoothAdapter.ACTION_STATE_CHANGED.equals(action))
		 {

			updateBTHeadSetDeviceInfo(context, intent);
		 }

	}

	public void updateHeadSetDeviceInfo(Context context, Intent intent)
	{
		 //0 for unplugged, 1 for plugged.
		 int state = intent.getIntExtra("state", -1);

		 StringBuilder stateName = null;

		 //Headset type, human readable string
		 String name = intent.getStringExtra("name");

		 // - 1 if headset has a microphone, 0 otherwise, 1 mean h2w
		 int microPhone = intent.getIntExtra("microphone", -1);

		 switch (state)
		 {
           case 0:
        	   stateName = new StringBuilder(context.getString(R.string.headset_unplugged));
			   Toast.makeText(context,""+stateName,Toast.LENGTH_SHORT).show();
               break;
           case 1:
               stateName = new StringBuilder(context.getString(R.string.headset_plugged));
			   Toast.makeText(context,""+stateName,Toast.LENGTH_SHORT).show();
               break;
           default:
			   Toast.makeText(context,context.getString(R.string.headset_state_unknown),Toast.LENGTH_SHORT).show();
		 }

//	 	HeadSetModel tempModel = new HeadSetModel().setState(state).setStateName(stateName.toString()).setHandSetName(name).setMicroPhone(microPhone);

	 	// update the prefrence key 'KEY_HEADSET_MODEL'  for headset ino
//		DeviceAudioOutputManager.getInstance(context).setHeadsetInfo(tempModel);

/*
		if(hasController())
		{
			mAudioController.updateHeadSetInfo(tempModel.getState(), tempModel);
		}
*/
	}

	public void updateBTHeadSetDeviceInfo(Context context, Intent intent)
	{
		if(intent != null)
		{
			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			if(device != null)
			{
				verifyHeadSetState(true,device);
			}
		}
	}
	public BluetoothHeadsetManager.onBluetoothHeadSetListener mHeadsetCallback = new BluetoothHeadsetManager.onBluetoothHeadSetListener() {

		@Override
		public void disconnectedToProxy()
		{
			verifyHeadSetState(false,null);
		}

		@Override
		public void connectedToProxy(BluetoothHeadset aBluetoothHeadset)
		{
			mBluetoothHeadset = aBluetoothHeadset;
			if(mBluetoothHeadset != null)
			{
				verifyHeadSetState(false,null);
			}
		}
	};

	private void verifyHeadSetState(boolean flag,BluetoothDevice device)
	{

		//TODO Wait for other wise result getting wrong
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		int state = 0;
		if(flag)
		{
			// STATE_DISCONNECTED	0
			// STATE_CONNECTED 		2

			// STATE_CONNECTING		1
			// STATE_DISCONNECTING	3

			state = mBluetoothHeadset.getConnectionState(device);
			Log.d("Bluetooth", "State :" + state);
		}
		else
		{
			AudioManager am = getAudioManager();
			state = (am.isBluetoothA2dpOn() == true) ? 1 : 0;
		}

/*		switch (state)
		{
			case 0:
			{
				Log.d(getTAG(), "isBluetoothA2dpOff()");
				btheasetdmodel= new HeadSetModel().setState(0).setStateName("BluetoothA2dpOff");
				break;
			}
			case 1:
			{
				Log.d(getTAG(), "isBluetoothA2dpOn()");
				btheasetdmodel= new HeadSetModel().setState(1).setStateName("BluetoothA2dpOn");
				break;
			}
			case 2:
			{
				Log.d(getTAG(), "isBluetoothA2dpOn()");
				btheasetdmodel= new HeadSetModel().setState(1).setStateName("BluetoothA2dpOn");
				break;
			}
			default:
				break;
		}

	 	// TODO Please call setBTHeadsetInfo instead of setHeadsetInfo
		DeviceAudioOutputManager.getInstance(getActiveContext()).setBTHeadsetInfo(btheasetdmodel);

		if(hasController())
		{
			mAudioController.updateHeadSetInfo(btheasetdmodel.getState(), btheasetdmodel);
		}*/

	}

	private AudioManager initiAudioManager(Context aContext)
	{
		if(mAudioManager == null)
		mAudioManager = (AudioManager) aContext.getSystemService(Context.AUDIO_SERVICE);
		return mAudioManager;
	}

	public AudioManager getAudioManager()
	{
		return mAudioManager != null ? mAudioManager : initiAudioManager(getActiveContext()) ;
	}


}
