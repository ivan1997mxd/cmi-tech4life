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

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ResultsAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    ArrayList<BioSignalsDevice> devices;
    BioSignalsDevice tempDevice;
    Context context;

    public ResultsAdapter(FragmentManager fm, int NumOfTabs, ArrayList<BioSignalsDevice> devices, Context context) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.devices = devices;
        this.context = context;

        for(int i = 0; i < devices.size(); i ++){
            if (devices.get(i).getSensorType().equals("Blood Pressure Dia") ){
                tempDevice = devices.get(i);
                devices.remove(i);
                break;
            }
        }

    }

    @Override
    public Fragment getItem(int position) {

        BioSignalsDevice sensor = devices.get(position);
        Gson gson = new Gson();
        String device = gson.toJson(sensor);

        switch (sensor.getSensorType()) {
            case "Respiration":
                RespirationResults tab1 = new RespirationResults();
                tempStoreData(context,device,"resp.txt");
                return tab1;
            case "ECG":
                // Create Fragment
                ECGResults tab2 = new ECGResults();
                tempStoreData(context,device,"ecg.txt");
                return tab2;
            case "Blood Pressure Sis":
                BloodPressureResults tab3 = new BloodPressureResults();
                String device2 = gson.toJson(tempDevice);
                tempStoreData(context,device,"bp1.txt");
                tempStoreData(context,device2,"bp2.txt");
                return tab3;
            case "Heart Rate":

                HeartRateResults tab4 = new HeartRateResults();
                tempStoreData(context,device,"hr.txt");
                return tab4;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    private void tempStoreData(Context context, String fcontent , String fileName) {
        try {
            File file           = new File(context.getCacheDir(), fileName);
            FileWriter fw       = new FileWriter(file.getAbsoluteFile(), false);
            BufferedWriter bw   = new BufferedWriter(fw);
            bw.write(fcontent);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}