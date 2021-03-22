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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.estethapp.media.R;
import com.google.gson.Gson;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
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

public class RespirationResults extends Fragment {

    public static final Integer ADC_BIT_RES = 4095;
    public static final Double ADC_DELTA = 0.05;
    public static final Double ADC_Voltage = 3.3;
    public static final Double ADC_SAMPLES_PER_SEC = 1/ADC_DELTA;
    public static final Integer MIN_CYCLE = 10;

    private View layout;

    GraphView respGraph;

    private double lastX=0;
    private LineGraphSeries<DataPoint> series;

    private TextView results;

    double respCycle;
    double highest = -50;
    double lowest = 50;

    boolean countHighest = true;

    int waveCounter = 0;
    int waveNum = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.multi_respiration_results, container, false);

        String device = read_file(getActivity(),"resp.txt");

        Gson gson = new Gson();

        BioSignalsDevice respirationDevice = gson.fromJson(device, BioSignalsDevice.class);

        respGraph   = layout.findViewById(R.id.multi_resp_graph);
        results     = layout.findViewById(R.id.txt_resp_results);

        series = new LineGraphSeries<DataPoint>();

        Viewport viewport= respGraph.getViewport();

        respGraph.getGridLabelRenderer().setNumHorizontalLabels(8);

        setData(respirationDevice.getReadings());

        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(-30);
        viewport.setMaxY(30);
        viewport.setXAxisBoundsManual(true);
        viewport.setMinX(0);
        viewport.setMaxX(series.getHighestValueX());

        respGraph.addSeries(series);

        respCycle = waveNum / lastX;
        results.setText("Number of Breaths:" + Integer.toString(waveNum) + "\n" +
                "Respiration Cycle / Min :" + String.format("%.0f", respCycle * 60)+ "\n" +
                "Total Recording Time:" + String.format("%.00f",lastX) +"seconds");

        return layout;
    }

    public void setData(ArrayList<Reading> data){

        respCycle = 0;

        for(Reading r : data) {
            double num = r.getData();

            //num = ADC_BIT_RES;
            // Calculation for Resp % (will swing negative) +/- 50
            num = (((num * 100) / (ADC_BIT_RES)) - 50);

            if (countHighest) {
                if (num > highest) {
                    highest = num;
                } else {
                    waveCounter++;
                    highest = num;
                    if (waveCounter >= MIN_CYCLE) {
                        waveCounter = 0;
                        countHighest = false;
                        highest = -50;
                    }
                }
            } else {
                if (num < lowest) {
                    lowest = num;
                } else {
                    lowest = num;
                    waveCounter++;
                    if (waveCounter >= MIN_CYCLE) {
                        waveCounter = 0;
                        countHighest = true;
                        lowest = 50;
                        waveNum++;
                    }
                }
            }

            series.appendData(new DataPoint(lastX = lastX + ADC_DELTA, num), true, 100000);
        }

    }

    public String read_file(Context context, String filename) {
        try {
            FileInputStream fis = new FileInputStream(new File(getActivity().getCacheDir(), filename));
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