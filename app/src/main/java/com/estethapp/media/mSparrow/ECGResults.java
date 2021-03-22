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
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.estethapp.media.R;
import com.google.gson.Gson;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class ECGResults extends Fragment {

    public static final Integer ADC_BIT_RES = 4096;
    public static final Double ADC_DELTA = 0.05;
    public static final Double ADC_Voltage = 3.0;
    public static final Integer ADC_MV_CONVERSION = 1000000;
    public static String ECG_STRING_NAME = "ecg.txt";

    View layout;
    GraphView ecgGraph;

    private double lastX=0;
    private LineGraphSeries<DataPoint> series;
    private TextView results;

    private static final String TAG = "MyActivity";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.multi_ecg_results, container, false);

        String device = read_file(getActivity(), ECG_STRING_NAME);

        Log.d("N", device);

        Log.d(TAG, "index=" + device);

        Gson gson = new Gson();

        BioSignalsDevice ecgDevice = gson.fromJson(device, BioSignalsDevice.class);

        ecgGraph    = layout.findViewById(R.id.multi_ecg_graph);
        results     = layout.findViewById(R.id.txt_ecg_results);

        series      = new LineGraphSeries<DataPoint>();

        setData(ecgDevice.getReadings());

        setGraphs();

        ecgGraph.addSeries(series);

        results.setText("Channel: " + Integer.toString(ecgDevice.getChannel()));

        return layout;
    }

    private void setGraphs() {

        ecgGraph.getViewport().setXAxisBoundsManual(true);

        ecgGraph.getViewport().setMaxX(series.getHighestValueX());

        ecgGraph.getViewport().setMinX(0);

        ecgGraph.getGridLabelRenderer().setNumHorizontalLabels(8);

        ecgGraph.getGridLabelRenderer().setHorizontalAxisTitle("Seconds");

        ecgGraph.getGridLabelRenderer().setVerticalAxisTitle("mV");

        ecgGraph.getViewport().setScrollable(true);

        ecgGraph.setTitle("ECG");

    }


    public void setData(ArrayList<Reading> data) {

        // Loop though the array of the data recorded by the device
        for (Reading r : data) {

            double num = r.getData();

//            num = (((num / ADC_BIT_RES) - 1/2) * ADC_Voltage) / 1000;
            num = (((num / ADC_BIT_RES)) * ADC_Voltage) / 1000;

            // Convert into mV
            num *= ADC_MV_CONVERSION;

            // Fixed for 50ms Data Recordings.
            series.appendData(new DataPoint(lastX += ADC_DELTA, num), true, 100000);
        }
    }

    public String read_file(Context context, String filename) {

        try {
            File myFile =
                    new File(getActivity().getCacheDir(), filename);

            FileInputStream fis     = new FileInputStream(myFile);

            InputStreamReader isr   = new InputStreamReader(fis, "UTF-8");

            BufferedReader bufferedReader = new BufferedReader(isr);

            StringBuilder sb = new StringBuilder();

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            // Remove the data file from the system
           // myFile.delete();

            //File(getActivity().getCacheDir(), filename);

            return sb.toString();

        } catch (FileNotFoundException e) {
            return "";
        } catch (UnsupportedEncodingException e) {
            return "";
        } catch (IOException e) {
            return "";
        }
    }
}
