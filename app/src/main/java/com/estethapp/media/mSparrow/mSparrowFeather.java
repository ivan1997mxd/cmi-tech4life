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

import android.content.ComponentName;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import java.io.UnsupportedEncodingException;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

public class mSparrowFeather {

    public UartService getmService() {
        return mService;
    }

    public void setmService(UartService mService) {
        this.mService = mService;
    }

    private UartService mService;

    public mSparrowFeather(UartService mService){
        this.mService = mService;
    }

    public void sendStartMessage(){
        String message = "G";
        byte[] value;
        try {
            //send data to service
            value = message.getBytes("UTF-8");

            mService.writeRXCharacteristic(value);

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void sendStopMessage(){
        String message = "S";
        byte[] value;
        try {
            //send data to service
            value = message.getBytes("UTF-8");

            mService.writeRXCharacteristic(value);

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            mService = ((UartService.LocalBinder) rawBinder).getService();

            Log.d(TAG, "onServiceConnected mService= " + mService);
            if (!mService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
            }

        }

        public void onServiceDisconnected(ComponentName classname) {
            ////     mService.disconnect(mDevice);
            mService = null;
        }
    };

    public static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART);
        return intentFilter;
    }
}


