
package com.estethapp.media.mSparrow;

import java.util.ArrayList;
import java.util.Date;

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
 *     Created:  4/8/2018
 * ****************************************************************************
 */

public class BioSignalsData{

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int[] getChannels() {
        return channels;
    }

    public void setChannels(int[] channels) {
        this.channels = channels;
    }

    public int getResolution() {
        return resolution;
    }

    public void setResolution(int resolution) {
        this.resolution = resolution;
    }

    public ArrayList<BioSignalsDevice> getDevices() {
        return devices;
    }

    public void setDevices(ArrayList<BioSignalsDevice> devices) {
        this.devices = devices;
    }

    String user;
    String mac;
    Date date;
    int[] channels;
    int resolution;
    ArrayList<BioSignalsDevice> devices;

    public BioSignalsData(String user, String mac, Date date, int[] channels, int resolution){
        this.user = user;
        this.mac = mac;
        this.date = date;
        this.channels = channels;
        this.resolution = resolution;

    }










}
