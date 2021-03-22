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
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class BloodPressureResults extends Fragment {

    View layout;

    double testdata1;
    double testdata2;

    GraphView bpGraph;

    private TextView results;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.multi_bp_results, container, false);

        results = layout.findViewById(R.id.txt_bp_results);
        bpGraph = layout.findViewById(R.id.bp_results_graph);

        setGraphs();

        String device = read_file(getActivity(),"bp1.txt");
        String device2 = read_file(getActivity(),"bp2.txt");

        testdata1 = 0.0;
        testdata2 = 0.0;

        Gson gson = new Gson();

        BioSignalsDevice bloodPressureSis = gson.fromJson(device, BioSignalsDevice.class);
        BioSignalsDevice bloodPressureDia = gson.fromJson(device2, BioSignalsDevice.class);

        for(int i = bloodPressureSis.getReadings().size() - 1; i > bloodPressureSis.getReadings().size() - 11; i--) {
            testdata1+= (bloodPressureSis.getReadings().get(i).getData());
            testdata2+= (bloodPressureDia.getReadings().get(i).getData());
        }

        testdata1 /= 10;
        testdata2 /= 10;

        testdata1 = 0.25* Math.pow(2,-6)*testdata1-0.8;
        testdata2 = 0.25* Math.pow(2,-6)*testdata2-0.8;

        results.setText(Integer.toString((int)testdata1) + "/" + Integer.toString((int)testdata2) );


        PointsGraphSeries<DataPoint> series = new PointsGraphSeries<>(new DataPoint[] {
                new DataPoint(testdata2, testdata1)
        });

        bpGraph.addSeries(series);

        return layout;
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

    private void setGraphs() {

        bpGraph.getViewport().setXAxisBoundsManual(true);

        bpGraph.getViewport().setMaxX(100);
        bpGraph.getViewport().setMinX(40);

        bpGraph.getViewport().setYAxisBoundsManual(true);

        bpGraph.getViewport().setMinY(70);
        bpGraph.getViewport().setMaxY(190);

        bpGraph.getGridLabelRenderer().setNumHorizontalLabels(5);
        bpGraph.getGridLabelRenderer().setNumVerticalLabels(13);

        bpGraph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);

    }
}
