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

//import com.esteth.media.mSparrowFragments.mSparrow.data.BLEUartService;

public class HeartRateResults extends Fragment {
    private final static String TAG = HeartRateResults.class.getSimpleName();

    private static final Integer OBSERVATION_DATA_POINTS = 5;

    public static final Integer ADC_BIT_RES = 4095;
    public static final Double ADC_DELTA = 0.05;
    public static final Double ADC_Voltage = 3.3;
    public static final Double ADC_SAMPLES_PER_SEC = 1/ADC_DELTA;

    View layout;
    GraphView pulseGraph;
    GraphView hrGraph;

    private double lastX=0;
    private double highestX=0;
    private LineGraphSeries<DataPoint> series;
    private LineGraphSeries<DataPoint> series2;

    int highest = 0;
    int lowest = ADC_BIT_RES;
    int waveCounter = 0;
    boolean countHighest = true;
    int waveNum = 0;

    ArrayList<Double> peaks = new ArrayList<>();

    double samplesPerSec;

    private TextView results;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // inflate the results into the main container..
        layout = inflater.inflate(R.layout.multi_hr_results, container, false);

        lastX=0;
        highestX = 0;
        highest = 0;
        lowest = ADC_BIT_RES;
        waveCounter = 0;

        // Clear the array list on view creation
        ArrayList<Double> peaks = new ArrayList<>();


        Log.d(TAG, "Create Heart Rate Results");

        String device = read_file(getActivity(),"hr.txt");

        // Create a new GSON object for parsing the hr.txt file for results processing.
        Gson gson = new Gson();

        // Build a results from loading the gsondata into the BioSignalsDevice class
        BioSignalsDevice hrDevice = gson.fromJson(device, BioSignalsDevice.class);

        Log.d(TAG, "Log the samples per second: " + ADC_SAMPLES_PER_SEC);

        // Set the samples per second.
        hrDevice.setSamplesPerSec(ADC_SAMPLES_PER_SEC);

        samplesPerSec = hrDevice.getSamplesPerSec();

        pulseGraph = layout.findViewById(R.id.multi_pulse_graph);
        hrGraph = layout.findViewById(R.id.multi_hr_graph);
        results = layout.findViewById(R.id.txt_hr_results);

        series = new LineGraphSeries<>();
        series2 = new LineGraphSeries<>();
        series2.setDrawDataPoints(true);
        setPulseData(hrDevice.getReadings());
        setHrData(peaks);

        setGraphs();

        pulseGraph.addSeries(series);
        hrGraph.addSeries(series2);

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setGraphs() {
        pulseGraph.getViewport().setXAxisBoundsManual(true);
        pulseGraph.getViewport().setMaxX(series.getHighestValueX());
        pulseGraph.getViewport().setMinX(0);

        pulseGraph.getViewport().setYAxisBoundsManual(true);
        pulseGraph.getViewport().setMaxY(series.getHighestValueY());
        pulseGraph.getViewport().setMinY(series.getLowestValueY());

        pulseGraph.getGridLabelRenderer().setNumHorizontalLabels(5);
        pulseGraph.getGridLabelRenderer().setHorizontalAxisTitle("Seconds");

        pulseGraph.getGridLabelRenderer().setVerticalLabelsVisible(false);
        pulseGraph.getViewport().setScrollable(true);

        pulseGraph.setTitle("Pulse");

        hrGraph.getViewport().setXAxisBoundsManual(true);
        hrGraph.getViewport().setMaxX(series2.getHighestValueX());
        hrGraph.getViewport().setMinX(0);

        hrGraph.getViewport().setYAxisBoundsManual(true);
        hrGraph.getViewport().setMaxY(200);
        hrGraph.getViewport().setMinY(0);

        hrGraph.getGridLabelRenderer().setNumHorizontalLabels(5);
        hrGraph.getGridLabelRenderer().setHorizontalAxisTitle("Seconds");

        hrGraph.getViewport().setScrollable(true);

        hrGraph.setTitle("Heart Rate");
    }

    public void setHrData(ArrayList<Double> p){
        double averageData = 0;

        if(p.size() >= 2){
            double p1 = p.get(0);
            for (int i = 1; i < p.size(); i++){

                Log.d(TAG, "p1 " + p.get(i) + " " + p1 + "Divide by :" + ADC_SAMPLES_PER_SEC);

//                double timeLapse = (p.get(i) - p1) / samplesPerSec * ADC_SAMPLES_PER_SEC;
                double timeLapse = (p.get(i) - p1) / ADC_SAMPLES_PER_SEC;
                Log.d(TAG, "timeLapse " + timeLapse);

                averageData += 60/timeLapse;
                series2.appendData(new DataPoint(p.get(i), 60 / timeLapse ), true, 1000);
                p1 = p.get(i);

            }
        }
        averageData = averageData/(p.size()-1);
        results.setText("Avg. Heart Rate: " + Integer.toString((int)averageData));
        Log.d(TAG,"Calculated heartrate: " + averageData);


    }

    public void setPulseData(ArrayList<Reading> data) {

        if(data.get(0).getData() > data.get(1).getData()){
            countHighest = false;
        }

        for (Reading r : data) {
            // load the current reading into num for validation
            int num = r.getData();

            // if we are tracking the highest count, then check high
            if (countHighest) {

                if (num > highest) {
                    highest = num;
                    highestX = lastX;
                } else {

                    waveCounter++;

                    if (waveCounter >= OBSERVATION_DATA_POINTS) {
                        waveCounter = 0;
                        countHighest = false;

                        //series2.appendData(new DataPoint(highestX, highest), true, 100000);

                        peaks.add(highestX);

                        highest = 0; // reset highest
                    }

                }

            // Check the lowest count.
            } else {

                if (num < lowest) {
                    lowest = num;
                } else {

                    waveCounter++;

                    // stop saving input as the slope has changed directions.
                    if (waveCounter >= OBSERVATION_DATA_POINTS) {
                        // Reset waveCount and change directions.
                        waveCounter = 0;
                        countHighest = true;
                        // Reset the lowest.
                        lowest = ADC_BIT_RES;
                    }

                }
            }

            Log.d(TAG, "lowest: " + lowest + "Hi: " + highest);

            Log.d(TAG,"LastX: " + lastX + " ADC Delta: " + ADC_DELTA + "Num: " + num);
            // increment the delta time for the storage of the data.
            lastX += ADC_DELTA;
            series.appendData(new DataPoint(lastX, num), true, 10000);
        }


    }

    public String read_file(Context context, String filename) {
        try {
            FileInputStream fis = new FileInputStream(new File(context.getCacheDir(), filename));
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
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