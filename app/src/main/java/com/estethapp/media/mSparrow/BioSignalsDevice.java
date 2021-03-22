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

import java.util.ArrayList;

public class BioSignalsDevice {

    public String getSensorType() {
        return sensorType;
    }

    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public ArrayList<Reading> getReadings() {
        return readings;
    }

    public void setReadings(ArrayList<Reading> readings) {
        this.readings = readings;
    }

    public void addReadings(Reading r){
        readings.add(r);
    }

    public void clearReadings() { readings.clear(); }


    String sensorType;
    int channel;
    ArrayList<Reading> readings;

    public int getMeasureTime() {
        return measureTime;
    }

    int measureTime;

    public double getSamplesPerSec() {
        return samplesPerSec;
    }

    public void setSamplesPerSec(double samplesPerSec) {
        this.samplesPerSec = samplesPerSec;
    }

    public void setMeasureTime(int measureTime) {
        this.measureTime = measureTime;
    }

    double samplesPerSec;

    public BioSignalsDevice (String sensorType, int channel, double samplesPerSec){
        this.sensorType = sensorType;
        this.channel = channel;
        this.samplesPerSec = samplesPerSec;
        readings = new ArrayList<Reading>();

        if(sensorType.equals("Respiration")){
            measureTime = 60000;
        }
        else if(sensorType.equals("ECG")){
            measureTime = 10000;
        }
        else if(sensorType.equals("Heart Rate")){
           measureTime = 30000;
        }
        else if (sensorType.equals("Blood Pressure Sis") || sensorType.equals("Blood Pressure Dia")) {
            measureTime = 60000;
        }


    }
}
