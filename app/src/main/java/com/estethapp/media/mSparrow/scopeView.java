/******************************************************************************
 *
 * Warren Zajac
 *
 * All Rights Reserved.
 * © Copyright by Warren Zajac, December 2019
 *
 *
 * NOTICE:
 * All information contained herein is, and remains the property of Warren
 * Zajac. The intellectual and technical concepts contained herein are proprietary
 * to Warren and his suppliers, affiliates, and subsidiaries, and may be
 * covered by Canadian, U.S. and Foreign Patents, patents in process, and
 * are protected by trade secret or copyright law.
 * Dissemination of this information, reproduction of this material, or use
 * of this information for any purpose other than permission which is
 * expressly given by Warren is strictly forbidden unless prior explicit
 * written permission is obtained from Warren Zajac
 *
 * ****************************************************************************
 */

package com.estethapp.media.mSparrow;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class scopeView {

    List<Integer> scopeDataPoints = new ArrayList<>();
    List<Integer> scopeHistoryPoints = new ArrayList<>();
    private float densityMetric;
    private int displayWidth = 0;

    int maxDataPoints = 40;
    int maxHistoryPoints = 100;
    private int ADCScale = 5500;

    Random randomNum = new Random();
    int lineColor = Color.BLUE;

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    // Image Drawing Canvas.
    private ImageView mBPMView = null;
    private int avgDataPoint = 0;
    private int avgHistPoint = 0;

    public void setMaxDataPoints(int maxNumberOfPoints) {
        maxDataPoints = maxNumberOfPoints;
    }

    public void setScalar(double newScalar) {
        ADCScale = (int) (5500 * newScalar);
    }

    public int getAvgReading() {
        return avgDataPoint;
    }

    public int getScreenWidth() {

        return this.displayWidth;
    }

    public scopeView()
    {

        this.displayWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        //float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        //float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        //System.out.println("dpHeight: " + dpHeight + " dpWIdth: " + dpWidth);
        //System.out.println("onCreate(Bundle savedInstanceState) = width: " + displayMetrics.widthPixels + " density: " + displayMetrics.density);

        // Save density metric
        this.densityMetric = displayMetrics.density;

        // set default metric to 1
        if(this.densityMetric < 1) this.densityMetric = 1;

    }

    public void setImageView(ImageView pViewPoint)
    {
        mBPMView = pViewPoint;
    }

    public void addData(int newDataPoint) {

        // Adjust value based on ADC center region
        // 5500 is good for resting level of 22xx
        float fDataPoint = (newDataPoint*100) / ADCScale;

        int newCalcPoint = (int) (fDataPoint * this.densityMetric);

        scopeDataPoints.add(newCalcPoint);

        scopeHistoryPoints.add(newDataPoint);

        avgDataPoint = ( newDataPoint + avgDataPoint );
        /// scopeDataPoints.size();

        avgDataPoint /=2;

        avgHistPoint+=newDataPoint;
        avgHistPoint /=2;
        //scopeHistoryPoints.size();

        //avgDataPoint = newDataPoint;


        //scopeDataPoints.size());

        if(scopeHistoryPoints.size() >  maxHistoryPoints) {
            scopeHistoryPoints.remove(1 );
        }
        // Remove Excessive DataPoints.
        if(scopeDataPoints.size() > maxDataPoints) {
            scopeDataPoints.remove(1 );
        }

    }

    public int calcFreq() {

        final int ABOVE = 1;
        final int BELOW = 2;

        int newFreq = 0;
        int dir=ABOVE;

        // if no data is available to draw, skip.
        if(scopeHistoryPoints.size()==0) return 14;

        int freq = 0;
        int curAvg = 0;

        int minX = 6000;
        int maxX = 0;

        // Draw all points on the graph
        for(int i = scopeHistoryPoints.size()-1; i>0; i--){

            int curElement = scopeHistoryPoints.get(i);

            if(maxX<curElement)
                maxX = curElement;

            if(minX>curElement)
                minX = curElement;

            curAvg+=scopeHistoryPoints.get(i);

        }



        if(scopeHistoryPoints.size()>1)
            curAvg=curAvg/scopeHistoryPoints.size()-1;

        ///System.out.println("    MIN/MAX/AVG " + minX + " " + maxX + " " + curAvg);

        if(maxX - minX < 100) {
            //System.out.println("No Statistical Difference!.. Abort Conversion.");
            return 14;
        }

        avgHistPoint = curAvg;


        int iAhove = 0;
        int iBlow = 0;

        // Draw all points on the graph
        for(int i = scopeHistoryPoints.size()-1; i>0; i--){

            curAvg+=scopeHistoryPoints.get(i);
            curAvg/=2;

          //  System.out.println("Cur: " + curAvg + " AVG: " + avgHistPoint);

            if(curAvg > avgHistPoint && (dir == BELOW ))
            {
//                System.out.println("    ABOVE !" + i);
                dir = ABOVE;
                iAhove = i;
                freq++;
            }
            else if (curAvg < avgHistPoint && (dir == ABOVE))
            {
                iBlow = i;
                freq++;
               // System.out.println("    BELOW !" + i);
                dir = BELOW;
            }


            if(iAhove>0 && iBlow>0 && 1==0)
            {

                System.out.println("REFERENCE: " + iAhove + " Below: " + iBlow);

                // Swap High and Low Positions.
                if( iAhove > iBlow)
                {
                    int x = iAhove;
                    iAhove = iBlow;
                    iBlow = x;
                }

                System.out.println("REFERENCE: " + iAhove + " Below: " + iBlow);

            //    double freqX = 1/(iBlow-iAhove);

                int delta = (iBlow - iAhove);

                float bpm = (delta / 3) * 4;

                System.out.println("Delta: " + (iBlow - iAhove) + "BPM " + bpm);

                if(delta < 6 || delta > 20)
                {
                    iAhove = 0;
                    iBlow = 0;
                    // Continue

                } else {

                    return (int) bpm;

                }

              //  return (int)freqX * 10;

                /*
                iAhove = 0;
                iBlow = 0;

                freq++;

                 */
  //              return ((iBlow-iAhove) );

//                System.out.println("REFERENCE: " + iAhove + " Below: " + iBlow);
                //int delta =
                //freq = iAhove + iBlow
            }


        }

        newFreq = freq;




        return newFreq;
    }

    public void FakeData() {

        // Create a random number for drawing on the graph
        int showMe = randomNum.nextInt(70);

       // showMe = (int) ((float)showMe * this.densityMetric);

        // Add fake data to the graph
        addData(showMe);


    }



    public void drawScopeView() {

        // If View is not set, return
        if(mBPMView==null) return;

        // Detect an offscreen object, do not create, skip.
        if(mBPMView.getHeight() == 0) return;

        // Calculate the display size for the graph
        int displaySize = (int)((getScreenWidth()) - (140 * this.densityMetric));

        // Initialize a new Bitmap object
        Bitmap bitmap = Bitmap.createBitmap(
                displaySize, // Width
                mBPMView.getHeight(), // Height
                Bitmap.Config.ARGB_8888 // Config
        );

        // Initialize a new Canvas instance
        Canvas canvas = new Canvas(bitmap);

        // Grab the Alpha or background of the view
        ColorDrawable drawable = (ColorDrawable) mBPMView.getBackground();

        // Draw a solid color on the canvas as background
        canvas.drawColor(drawable.getColor());

        // Initialize a new Paint instance to draw the line
        Paint paint = new Paint();

        // Set the line color
        paint.setColor(lineColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);

        // Line width in pixels
        paint.setStrokeWidth(3*densityMetric);
        paint.setAntiAlias(true);

        // Draw Mid line
        Paint p2 = new Paint();
        p2.setAntiAlias(true);
        p2.setColor(Color.BLACK);
        p2.setStrokeWidth(1*densityMetric);

        // Draw the baseline
        canvas.drawLine(0,canvas.getHeight() / 2,canvas.getWidth(),canvas.getHeight() / 2, p2);

        // Set a pixels value to offset the line from canvas edge
        Path path = new Path();

        // if no data is available to draw, skip.
        if(scopeDataPoints.size()==0) return;

        // Set start pen point to first element.
        path.moveTo(canvas.getWidth(), canvas.getHeight() / 2);

        // Calculate the canvas width and add for size of the dataset.
        int calcWidth = canvas.getWidth() / scopeDataPoints.size();

        int halfHeight = canvas.getHeight() / 2;
        int qtrHeight = canvas.getHeight() / 4;

        // Draw all points on the graph
        for(int i = scopeDataPoints.size()-1; i>0; i--){

            // System.out.println(" " + scopeDataPoints.get(i) + "\t Calc Width: " + calcWidth);

            int offsetX = (halfHeight) + ((qtrHeight) - scopeDataPoints.get(i));

            path.lineTo( (i*calcWidth), offsetX);

        }

        // Draw the line on the canvas.
        canvas.drawPath(path, paint);

        // Display the newly created bitmap on app interface
        mBPMView.setImageBitmap(bitmap);

        // Make sure the view is visible
        mBPMView.setVisibility(View.VISIBLE);
    }


}

/*
    void mpx_updateView() {
        // Initialize a new Bitmap object
        Bitmap bitmap = Bitmap.createBitmap(
                800, // Width
                100, // Height
                Bitmap.Config.ARGB_8888 // Config
        );

        // Initialize a new Canvas instance
        Canvas canvas = new Canvas(bitmap);

        // Draw a solid color on the canvas as background
        canvas.drawColor(Color.WHITE);

        // Initialize a new Paint instance to draw the line
        Paint paint = new Paint();
        // Line color
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.BEVEL);

        // Line width in pixels
        paint.setStrokeWidth(3);
        paint.setAntiAlias(true);

        // Draw Mid line
        Paint p2 = new Paint();
        p2.setAntiAlias(true);
        p2.setColor(Color.BLACK);
        p2.setStrokeWidth(1);
        canvas.drawLine(0,canvas.getHeight() / 2,canvas.getWidth(),canvas.getHeight() / 2,p2);

        // Set a pixels value to offset the line from canvas edge
        Path path = new Path();

        // path.moveTo(0, canvas.getHeight() / 2);

        // Don't draw, leave.
        if(listX.size()==0) return;

        // Set start pen point to first element.
        path.moveTo(canvas.getWidth(), listX.get(0));

        int calcWidth = canvas.getWidth() / listX.size();

        for(int i = listX.size()-1; i>0; i--){

            System.out.println(" " + listX.get(i) + "\t Calc Width: " + calcWidth);

            int offsetx = (canvas.getHeight() / 2) + ((canvas.getHeight() / 4) - listX.get(i));

            System.out.println("Offset : \t" + offsetx);


            path.lineTo(canvas.getWidth() - (i*calcWidth), offsetx);



        }

        canvas.drawPath(path, paint);

        // Display the newly created bitmap on app interface
        mImageView.setImageBitmap(bitmap);

                mImageView.setVisibility(View.VISIBLE);
                }
                */