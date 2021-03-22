package com.estethapp.media.mSparrow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.estethapp.media.R;
import com.estethapp.media.mSparrow.AssessmentFragments.PageFragmentEight;
import com.estethapp.media.mSparrow.AssessmentFragments.PageFragmentFive;
import com.estethapp.media.mSparrow.AssessmentFragments.PageFragmentFour;
import com.estethapp.media.mSparrow.AssessmentFragments.PageFragmentOne;
import com.estethapp.media.mSparrow.AssessmentFragments.PageFragmentSeven;
import com.estethapp.media.mSparrow.AssessmentFragments.PageFragmentSix;
import com.estethapp.media.mSparrow.AssessmentFragments.PageFragmentThree;
import com.estethapp.media.mSparrow.AssessmentFragments.PageFragmentTwo;

import java.util.ArrayList;
import java.util.List;

public class SelfAssessmentActivity extends AppCompatActivity{
    Toolbar toolbar;

    //Array to store user results
    private String[] assessmentResults = {"N","N","N","N","N","N"};
    private String[] storedAssessmentResults = {"N","N","N","N","N","N"};
    SharedPreferences share;
    SharedPreferences.Editor edit;

    //Pager
    private ViewPager pager;
    private PagerAdapter adapter;

    //Buttons
    private Button nextButton;
    private Button previousButton;
    private Button retakeButton;
    private Button linkButton;

    //TextViews
    TextView finalPageTitle;
    TextView finalPageSubTitle;

    //Help Link String
    String helpLink;

    //Final Page Int Indicator
    int finalPageVersion;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_assessment);

        share = getSharedPreferences("SelfAssessmentData", Context.MODE_PRIVATE);
        edit = share.edit();

        //Creating helpLink string which redirects to gov of canada TeleHealth page
        helpLink = "https://ca.thrive.health/covid19app/resources/e0545c20-f916-4b41-bddd-ee35c9c42437";

        //Connecting UI to code
        nextButton = findViewById(R.id.nextBtn);
        previousButton = findViewById(R.id.prevBtn);
        retakeButton = findViewById(R.id.restartAssessment);
        linkButton = findViewById(R.id.linkBtn);
        finalPageTitle = findViewById(R.id.finalPageHeader);
        finalPageSubTitle = findViewById(R.id.finalPageSubHeader);

        finalPageVersion = 0;

        //Populating list of fragments
        List<Fragment> list = new ArrayList<>();
        list.add(new PageFragmentOne());
        list.add(new PageFragmentTwo());
        list.add(new PageFragmentThree());
        list.add(new PageFragmentFour());
        list.add(new PageFragmentFive());
        list.add(new PageFragmentSix());
        list.add(new PageFragmentSeven());
        list.add(new PageFragmentEight());

        //Setting up pager
        pager = findViewById(R.id.pager);
        adapter = new SlidePageAdapter(getSupportFragmentManager(), list);
        pager.setAdapter(adapter);
        pager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        setupUI();
    }

    //Method used to setup the user interface
    private void setupUI(){
        toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        previousButton.setVisibility(View.INVISIBLE);
        nextButton.setText("Start");
        finalPageTitle.setVisibility(View.INVISIBLE);
        finalPageSubTitle.setVisibility(View.INVISIBLE);
        retakeButton.setVisibility(View.INVISIBLE);
        retakeButton.setEnabled(false);
        linkButton.setVisibility(View.INVISIBLE);
        linkButton.setEnabled(false);
    }

    //Method called to navigate to the next page in the pager
    public void onNext(View view) {
        RadioButton yesBtn = null;
        RadioButton noBtn = null;

        //Getting the current visible fragment in the pager
        Fragment fragment = (Fragment) pager
                .getAdapter()
                .instantiateItem(pager, pager.getCurrentItem());

        //Switch case to determine which pages radio button the 'yesBtn' and 'noBtn' will represent
        switch (pager.getCurrentItem()){
            case 1:
                yesBtn = fragment.getActivity().findViewById(R.id.pageTwoYes);
                noBtn = fragment.getActivity().findViewById(R.id.pageTwoNo);
                break;
            case 2:
                yesBtn = fragment.getActivity().findViewById(R.id.pageThreeYes);
                noBtn = fragment.getActivity().findViewById(R.id.pageThreeNo);
                break;
            case 3:
                yesBtn = fragment.getActivity().findViewById(R.id.pageFourYes);
                noBtn = fragment.getActivity().findViewById(R.id.pageFourNo);
                break;
            case 4:
                yesBtn = fragment.getActivity().findViewById(R.id.pageFiveYes);
                noBtn = fragment.getActivity().findViewById(R.id.pageFiveNo);
                break;
            case 5:
                yesBtn = fragment.getActivity().findViewById(R.id.pageSixYes);
                noBtn = fragment.getActivity().findViewById(R.id.pageSixNo);
                break;
            case 6:
                yesBtn = fragment.getActivity().findViewById(R.id.pageSevenYes);
                noBtn = fragment.getActivity().findViewById(R.id.pageSevenNo);
                break;
            default:
                break;
        }

        //If its on the first page, proceed without any checks
        if (pager.getCurrentItem() == 0) {

            pager.setCurrentItem(getItem(1), true);

            if (pager.getCurrentItem() == 1){
                previousButton.setVisibility(View.VISIBLE);
                nextButton.setText("Next");
            }

        //If the current page is not the last one
        }else if (pager.getCurrentItem() < 7){

            //Checking the current page to set specific UI values
            if (!yesBtn.isChecked() && !noBtn.isChecked()){
                Toast.makeText(fragment.getActivity(), "Error: Please answer the question.", Toast.LENGTH_SHORT).show();

            }else{
                //Jump to result page if the user is feeling severe symptoms
                if (pager.getCurrentItem() == 1 && yesBtn.isChecked()){
                    pager.setCurrentItem(getItem(6), false);
                    nextButton.setVisibility(View.INVISIBLE);
                    nextButton.setEnabled(false);
                    previousButton.setVisibility(View.INVISIBLE);
                    previousButton.setEnabled(false);
                    finalPageVersion = 1;
                    assessmentResults[0] = "Y";
                    saveAssessmentResults();

                }else if (pager.getCurrentItem() == 2 && yesBtn.isChecked()){
                    pager.setCurrentItem(getItem(6), false);
                    nextButton.setVisibility(View.INVISIBLE);
                    nextButton.setEnabled(false);
                    previousButton.setVisibility(View.INVISIBLE);
                    previousButton.setEnabled(false);
                    finalPageVersion = 2;
                    assessmentResults[1] = "Y";
                    saveAssessmentResults();
                } else{
                    if (yesBtn.isChecked()){
                        finalPageVersion = 3;

                        if (assessmentResults[2].equals("N") && pager.getCurrentItem() == 3){
                            assessmentResults[2] = "Y";
                        }else if (assessmentResults[3].equals("N") && pager.getCurrentItem() == 4){
                            assessmentResults[3] = "Y";
                        }else if (assessmentResults[4].equals("N") && pager.getCurrentItem() == 5) {
                            assessmentResults[4] = "Y";
                        }else if (assessmentResults[5].equals("N") && pager.getCurrentItem() == 6){
                            assessmentResults[5] ="Y";
                        }
                    }

                    pager.setCurrentItem(getItem(1), true);

                    if (pager.getCurrentItem() == 7){
                        nextButton.setVisibility(View.INVISIBLE);
                        nextButton.setEnabled(false);
                        previousButton.setVisibility(View.INVISIBLE);
                        previousButton.setEnabled(false);

                        saveAssessmentResults();

                    }
                }
                setFinalPageInfo();
            }
        }
    }

    //Method which will save the assessment results
    private void saveAssessmentResults(){

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < assessmentResults.length; i++) {
            if (i != assessmentResults.length-1){
                sb.append(assessmentResults[i]).append(",");
            }else{
                sb.append(assessmentResults[i]);
            }
        }
        edit.putString("selfAssessment", sb.toString());
        edit.apply();
    }

    //Method which will retrieve the saved assessment results
    private void getAssessmentResults(){

        //Todo: Can call this method first, to see if there are any results stored so no values are overwritten by assessment results that way
        // you can have 2 arrays: 1 from previous assessment, and 1 from most recent assessment (future implementation: UI page showing assessment results?)
        String storedResults = share.getString("selfAssessment", "oops");
        storedAssessmentResults = storedResults.split(",");

        Log.i("storedAssessmentResults", share.getString("selfAssessment", "oops"));
    }



    //Method used to determine the outcome of the users selection, and set the self-assessment UI elements accordingly
    private void setFinalPageInfo(){
        switch (finalPageVersion){
            case 0:
                finalPageTitle.setText(getString(R.string.covid_self_assessment_final_stay_home_title));
                finalPageSubTitle.setText(getString(R.string.covid_self_assessment_final_stay_home_subtitle));
                break;
            case 1:
                finalPageTitle.setText(getString(R.string.covid_self_assessment_final_emergency_title));
                finalPageSubTitle.setText(getString(R.string.covid_self_assessment_final_emergency_subtitle));
                break;
            case 2:
                finalPageTitle.setText(getString(R.string.covid_self_assessment_final_family_doctor_title));
                finalPageSubTitle.setText(getString(R.string.covid_self_assessment_final_family_doctor_subtitle));
                break;
            case 3:
                finalPageTitle.setText(getString(R.string.covid_self_assessment_final_quarantine_title));
                finalPageSubTitle.setText(getString(R.string.covid_self_assessment_final_quarantine_subtitle));
                break;
            default:
                break;
        }

        //If its the last page in the pager show & enable these UI elements
        if (pager.getCurrentItem() == 7) {
            finalPageTitle.setVisibility(View.VISIBLE);
            finalPageSubTitle.setVisibility(View.VISIBLE);

            retakeButton.setVisibility(View.VISIBLE);
            retakeButton.setEnabled(true);

            linkButton.setVisibility(View.VISIBLE);
            linkButton.setEnabled(true);
        }
    }

    //Method used to go to the previous page in the pager
    public void onPrev(View view){
        pager.setCurrentItem(getItem(-1), true);

        //If its the first page, hide the previous button
        if (pager.getCurrentItem() == 0){
            previousButton.setVisibility(View.INVISIBLE);
            nextButton.setText("Start");
        }
    }

    //Method used to restart the self-assessment
    public void restartAssessment(View view) {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    //Method used to increment and return the next page
    private int getItem(int i) {
        return pager.getCurrentItem() + i;
    }

    //Method which will redirect the user to the Government of Canada's TeleHealth page
    public void onLink(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(helpLink)));
    }
}
