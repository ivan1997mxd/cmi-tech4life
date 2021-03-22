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

public class Reading {

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    // Get the current sequence.
    public int getSequence() {
        return sequence;
    }

    // Set the current sequence.
    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    // Data returned by device
    private int data;

    // Sequence Number for this data point.
    private int sequence;

    // Store reading and sequence number into class.
    public Reading(int data, int sequence){
        this.data = data;
        this.sequence = sequence;
    }

}
