package com.estethapp.media.adapter;

import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import  com.estethapp.media.R;
import  com.estethapp.media.fragment.ExaminingFragment;
import  com.estethapp.media.helper.ExminatingHelper;
import  com.estethapp.media.util.ModelObject;
import  com.estethapp.media.view.RippleAnimationView;

import java.util.ArrayList;
import java.util.HashMap;


public class HealthViewPagerAdapter extends PagerAdapter {

    private ExaminingFragment mContext;
    public static ArrayList<String> frontPostions = new ArrayList<>();
    public static ArrayList<String> backPostions = new ArrayList<>();
    public static HashMap<String,Button> frontRecoredPositions = new HashMap<String,Button>();
    public static HashMap<String,Button> backRecoredPositions = new HashMap<String,Button>();

    //front

    // heart postions
    public static RippleAnimationView ripple_front_heart_aortic_valve; //Postion  1
    public static RippleAnimationView front_ripple_heart_pulmonic_valve;//Postion 2
    public static RippleAnimationView ripple_front_heart_erbpoint;//Postion 3
    public static RippleAnimationView front_ripple_heart_tricuspidvalve;//Postion 4
    public static RippleAnimationView ripple_front_heart_mitral_valve;//Postion 5

    public Button front_heart_aortic_valve;//Postion 1
    public Button front_heart_pulmonic_valve;//Postion 2
    public Button front_heart_erbpoint;//Postion 3
    public Button front_heart_tricuspidvalve;//Postion 4
    public Button front_heart_mitral_valve;//Postion 5


    // lungs
    public static RippleAnimationView fripple_front_Lungs_Rt_upper_field_1;
    public static RippleAnimationView fripple_front_Lungs_Rt_upper_field_2;
    public static RippleAnimationView fripple_front_Lungs_Rt_middile_field;
    public static RippleAnimationView fripple_front_Lungs_Rt_lower_field;
    public static RippleAnimationView ripple_lungs_lt_upper_field;
    public static RippleAnimationView ripple_lungs_lt_lower_field;

    public Button front_Lungs_Rt_upper_field_1;
    public Button front_Lungs_Rt_upper_field_2;
    public Button front_Lungs_Rt_middile_field ;
    public Button front_Lungs_Rt_lower_field ;
    public Button front_lungs_lt_upper_field;
    public Button front_lungs_lt_lower_field;

    // Selected Postion for shwoing Ripple


    RippleAnimationView rippleSelectedView;


    // back

    public static RippleAnimationView ripple_back_lungs_lt_upper_field; // Postion 1
    public static RippleAnimationView ripple_back_lungs_lt_middle_field;// Postion 2
    public static RippleAnimationView ripple_back_lungs_lt_lower_field;// Postion 3
    public static RippleAnimationView ripple_back_lungs_lt_costophrenicangle;// Postion  4
    public static RippleAnimationView ripple_back_lungs_rt_upper_field;// Postion 5
    public static RippleAnimationView ripple_back_lungs_rt_middle_field;// Postion 6
    public static RippleAnimationView ripple_back_lungs_rt_lower_field;// Postion 7
    public static RippleAnimationView ripple_back_lungs_rt_costophrenicangle;// Postion 8

    public static Button back_lungs_lt_upper_field ;// Postion  1
    public static Button back_lungs_lt_middle_field;// Postion  2
    public static Button back_lungs_lt_lower_field;// Postion  3
    public static Button back_lungs_lt_costophrenicangle;// Postion 4
    public static Button back_lungs_rt_upper_field;// Postion  5
    public static Button back_lungs_rt_middle_field;// Postion  6
    public static Button back_lungs_rt_lower_field;// Postion  7
    public static Button back_lungs_rt_costophrenicangle;// Postion  8

    ExminatingHelper exminatingHelper = ExminatingHelper.getInstance();

    public HealthViewPagerAdapter(ExaminingFragment context) {
        mContext = context;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        ModelObject modelObject = ModelObject.values()[position];
        LayoutInflater inflater = LayoutInflater.from(mContext.getActivity());
        ViewGroup layout = (ViewGroup) inflater.inflate(modelObject.getLayoutResId(), collection, false);
        collection.addView(layout);

        if(position == 0){

            exminatingHelper.setFrontClick(true);

            final ToggleButton heartLungsswitch = (ToggleButton)layout.findViewById(R.id.heart_lungsswitch);

            ripple_front_heart_mitral_valve  = (RippleAnimationView)layout.findViewById(R.id.ripple_front_heart_mitral_valve);
            front_ripple_heart_pulmonic_valve  = (RippleAnimationView)layout.findViewById(R.id.front_ripple_heart_pulmonic_valve);
            ripple_front_heart_erbpoint  = (RippleAnimationView)layout.findViewById(R.id.ripple_front_heart_erbpoint);
            front_ripple_heart_tricuspidvalve  = (RippleAnimationView)layout.findViewById(R.id.front_ripple_heart_tricuspidvalve);
            ripple_front_heart_aortic_valve  = (RippleAnimationView)layout.findViewById(R.id.ripple_front_heart_aortic_valve);


            fripple_front_Lungs_Rt_upper_field_1  = (RippleAnimationView)layout.findViewById(R.id.fripple_front_Lungs_Rt_upper_field_1);
            fripple_front_Lungs_Rt_upper_field_2  = (RippleAnimationView)layout.findViewById(R.id.fripple_front_Lungs_Rt_upper_field_2);
            fripple_front_Lungs_Rt_middile_field  = (RippleAnimationView)layout.findViewById(R.id.fripple_front_Lungs_Rt_middile_field);
            fripple_front_Lungs_Rt_lower_field  = (RippleAnimationView)layout.findViewById(R.id.fripple_front_Lungs_Rt_lower_field);
            ripple_lungs_lt_upper_field   = (RippleAnimationView)layout.findViewById(R.id.ripple_lungs_lt_upper_field);
            ripple_lungs_lt_lower_field  = (RippleAnimationView)layout.findViewById(R.id.ripple_lungs_lt_lower_field);


            //lungs
            front_Lungs_Rt_upper_field_1  = (Button)layout.findViewById(R.id.front_Lungs_Rt_upper_field_1);// postion 1
            front_Lungs_Rt_upper_field_2  = (Button)layout.findViewById(R.id.front_Lungs_Rt_upper_field_2);// postion 2
            front_Lungs_Rt_middile_field  = (Button)layout.findViewById(R.id.front_Lungs_Rt_middile_field);// postion 3
            front_Lungs_Rt_lower_field = (Button)layout.findViewById(R.id. front_Lungs_Rt_lower_field);// postion 4
            front_lungs_lt_upper_field  = (Button)layout.findViewById(R.id.front_lungs_lt_upper_field);// postion 5
            front_lungs_lt_lower_field  = (Button)layout.findViewById(R.id.front_lungs_lt_lower_field);// postion 6

            //heart
            front_heart_mitral_valve  = (Button)layout.findViewById(R.id.front_heart_mitral_valve); // postion 1
            front_heart_pulmonic_valve  = (Button)layout.findViewById(R.id. front_heart_pulmonic_valve);// postion 2
            front_heart_erbpoint    = (Button)layout.findViewById(R.id.front_heart_erbpoint);// postion 3
            front_heart_tricuspidvalve  = (Button)layout.findViewById(R.id.front_heart_tricuspidvalve);// postion 4
            front_heart_aortic_valve  = (Button)layout.findViewById(R.id.front_heart_aortic_valve);// postion 5



            ripple_front_heart_mitral_valve.startRippleAnimation();
            front_ripple_heart_pulmonic_valve.startRippleAnimation();
            ripple_front_heart_erbpoint.startRippleAnimation();
            front_ripple_heart_tricuspidvalve.startRippleAnimation();
            ripple_front_heart_aortic_valve.startRippleAnimation();

            fripple_front_Lungs_Rt_upper_field_1.setVisibility(View.INVISIBLE);
            fripple_front_Lungs_Rt_upper_field_2.setVisibility(View.INVISIBLE);
            fripple_front_Lungs_Rt_middile_field.setVisibility(View.INVISIBLE);
            fripple_front_Lungs_Rt_lower_field.setVisibility(View.INVISIBLE);
            ripple_lungs_lt_upper_field.setVisibility(View.INVISIBLE);
            ripple_lungs_lt_lower_field.setVisibility(View.INVISIBLE);



            heartLungsswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){

                        ripple_front_heart_aortic_valve.setVisibility(View.VISIBLE);
                        front_ripple_heart_pulmonic_valve.setVisibility(View.VISIBLE);
                        ripple_front_heart_erbpoint.setVisibility(View.VISIBLE);
                        front_ripple_heart_tricuspidvalve.setVisibility(View.VISIBLE);
                        ripple_front_heart_mitral_valve.setVisibility(View.VISIBLE);

                        ripple_front_heart_aortic_valve.startRippleAnimation();
                        front_ripple_heart_pulmonic_valve.startRippleAnimation();
                        ripple_front_heart_erbpoint.startRippleAnimation();
                        front_ripple_heart_tricuspidvalve.startRippleAnimation();
                        ripple_front_heart_mitral_valve.startRippleAnimation();

                        fripple_front_Lungs_Rt_upper_field_1.setVisibility(View.INVISIBLE);
                        fripple_front_Lungs_Rt_upper_field_2.setVisibility(View.INVISIBLE);
                        fripple_front_Lungs_Rt_middile_field.setVisibility(View.INVISIBLE);
                        fripple_front_Lungs_Rt_lower_field.setVisibility(View.INVISIBLE);
                        ripple_lungs_lt_upper_field.setVisibility(View.INVISIBLE);
                        ripple_lungs_lt_lower_field.setVisibility(View.INVISIBLE);


                    }else{
                        fripple_front_Lungs_Rt_upper_field_1.setVisibility(View.VISIBLE);
                        fripple_front_Lungs_Rt_upper_field_2.setVisibility(View.VISIBLE);
                        fripple_front_Lungs_Rt_middile_field.setVisibility(View.VISIBLE);
                        fripple_front_Lungs_Rt_lower_field.setVisibility(View.VISIBLE);
                        ripple_lungs_lt_upper_field.setVisibility(View.VISIBLE);
                        ripple_lungs_lt_lower_field.setVisibility(View.VISIBLE);


                        fripple_front_Lungs_Rt_upper_field_1.startRippleAnimation();
                        fripple_front_Lungs_Rt_upper_field_2.startRippleAnimation();
                        fripple_front_Lungs_Rt_middile_field.startRippleAnimation();
                        fripple_front_Lungs_Rt_lower_field.startRippleAnimation();
                        ripple_lungs_lt_upper_field.startRippleAnimation();
                        ripple_lungs_lt_lower_field.startRippleAnimation();

                        ripple_front_heart_aortic_valve.setVisibility(View.INVISIBLE);
                        front_ripple_heart_pulmonic_valve.setVisibility(View.INVISIBLE);
                        ripple_front_heart_erbpoint.setVisibility(View.INVISIBLE);
                        front_ripple_heart_tricuspidvalve.setVisibility(View.INVISIBLE);
                        ripple_front_heart_mitral_valve.setVisibility(View.INVISIBLE);
                    }
                }
            });

            //heart
            // Postion 1
            front_heart_aortic_valve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ExaminingFragment.isFrontPositionsSelected = true;
                    exminatingHelper.setFrontBodyPostionSelected(1);
                    exminatingHelper.setFrontClick(true);
                    ExaminingFragment.enableRecordingButton();
                    rippleSelectedView = ripple_front_heart_aortic_valve;
                    showSelectedFrontPostionAnimation(""+1);

                    if(exminatingHelper.isRecordingStarted()){
                        
                        frontRecoredPositions.put(""+1,front_heart_aortic_valve);
                    }
                    mContext.showPostionsBeforeRecording();

                }
            });
            //Positon 2
            front_heart_pulmonic_valve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ExaminingFragment.isFrontPositionsSelected = true;
                    exminatingHelper.setFrontBodyPostionSelected(2);
                    exminatingHelper.setFrontClick(true);
                    ExaminingFragment.enableRecordingButton();
                    rippleSelectedView = front_ripple_heart_pulmonic_valve;
                    showSelectedFrontPostionAnimation(""+2);
                    if(exminatingHelper.isRecordingStarted()){
                        frontRecoredPositions.put(""+2,front_heart_pulmonic_valve);
                    }
                    mContext.showPostionsBeforeRecording();

                }
            });

            // Postion 3
            front_heart_erbpoint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ExaminingFragment.isFrontPositionsSelected = true;
                    exminatingHelper.setFrontBodyPostionSelected(3);
                    exminatingHelper.setFrontClick(true);
                    ExaminingFragment.enableRecordingButton();
                    rippleSelectedView = ripple_front_heart_erbpoint;
                    showSelectedFrontPostionAnimation(""+3);
                    if(exminatingHelper.isRecordingStarted()){
                        
                        frontRecoredPositions.put(""+3,front_heart_erbpoint);
                    }
                    mContext.showPostionsBeforeRecording();
                }
            });
            //Positon 4
            front_heart_tricuspidvalve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ExaminingFragment.isFrontPositionsSelected = true;
                    exminatingHelper.setFrontBodyPostionSelected(4);
                    exminatingHelper.setFrontClick(true);
                    ExaminingFragment.enableRecordingButton();
                    rippleSelectedView = front_ripple_heart_tricuspidvalve;
                    showSelectedFrontPostionAnimation(""+4);
                    if(exminatingHelper.isRecordingStarted()){
                        
                        frontRecoredPositions.put(""+4,front_heart_tricuspidvalve);
                    }
                    mContext.showPostionsBeforeRecording();
                }
            });

            // Postion 5
            front_heart_mitral_valve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ExaminingFragment.isFrontPositionsSelected = true;
                    exminatingHelper.setFrontBodyPostionSelected(5);
                    exminatingHelper.setFrontClick(true);
                    ExaminingFragment.enableRecordingButton();
                    rippleSelectedView = ripple_front_heart_mitral_valve;
                    showSelectedFrontPostionAnimation(""+5);
                    if(exminatingHelper.isRecordingStarted()){
                        
                        frontRecoredPositions.put(""+5,front_heart_mitral_valve);
                    }
                    mContext.showPostionsBeforeRecording();
                }
            });


            //lungs

            // Postion 6
            front_Lungs_Rt_upper_field_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ExaminingFragment.isFrontPositionsSelected = true;
                    exminatingHelper.setFrontBodyPostionSelected(6);
                    exminatingHelper.setFrontClick(true);
                    ExaminingFragment.enableRecordingButton();
                    rippleSelectedView = fripple_front_Lungs_Rt_upper_field_1;
                    showSelectedFrontPostionAnimation(""+6);
                    if(exminatingHelper.isRecordingStarted()){
                        
                        frontRecoredPositions.put(""+5,front_Lungs_Rt_upper_field_1);
                    }
                    mContext.showPostionsBeforeRecording();
                }
            });
            //Positon 7
            front_Lungs_Rt_upper_field_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ExaminingFragment.isFrontPositionsSelected = true;
                    exminatingHelper.setFrontBodyPostionSelected(7);
                    exminatingHelper.setFrontClick(true);
                    ExaminingFragment.enableRecordingButton();
                    rippleSelectedView = fripple_front_Lungs_Rt_upper_field_2;
                    showSelectedFrontPostionAnimation(""+7);
                    if(exminatingHelper.isRecordingStarted()){
                        
                        frontRecoredPositions.put(""+5,front_Lungs_Rt_upper_field_2);
                    }
                    mContext.showPostionsBeforeRecording();
                }
            });

            // Postion 8
            front_Lungs_Rt_middile_field.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ExaminingFragment.isFrontPositionsSelected = true;
                    exminatingHelper.setFrontBodyPostionSelected(8);
                    exminatingHelper.setFrontClick(true);
                    ExaminingFragment.enableRecordingButton();
                    rippleSelectedView = fripple_front_Lungs_Rt_middile_field;
                    showSelectedFrontPostionAnimation(""+8);
                    if(exminatingHelper.isRecordingStarted()){
                        
                        frontRecoredPositions.put(""+5,front_Lungs_Rt_middile_field);
                    }
                    mContext.showPostionsBeforeRecording();
                }
            });
            //Positon 9
            front_Lungs_Rt_lower_field.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ExaminingFragment.isFrontPositionsSelected = true;
                    exminatingHelper.setFrontBodyPostionSelected(9);
                    exminatingHelper.setFrontClick(true);
                    ExaminingFragment.enableRecordingButton();
                    rippleSelectedView = fripple_front_Lungs_Rt_lower_field;
                    showSelectedFrontPostionAnimation(""+9);
                    if(exminatingHelper.isRecordingStarted()){
                        
                        frontRecoredPositions.put(""+9,front_Lungs_Rt_lower_field);
                    }
                    mContext.showPostionsBeforeRecording();
                }
            });

            // Postion 10
            front_lungs_lt_upper_field.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ExaminingFragment.isFrontPositionsSelected = true;
                    exminatingHelper.setFrontBodyPostionSelected(10);
                    exminatingHelper.setFrontClick(true);
                    ExaminingFragment.enableRecordingButton();
                    rippleSelectedView = ripple_lungs_lt_upper_field;
                    showSelectedFrontPostionAnimation(""+10);
                    if(exminatingHelper.isRecordingStarted()){
                        
                        frontRecoredPositions.put(""+10,front_lungs_lt_upper_field);
                    }
                    mContext.showPostionsBeforeRecording();
                }
            });

            // Postion 11
            front_lungs_lt_lower_field.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ExaminingFragment.isFrontPositionsSelected = true;
                    exminatingHelper.setFrontBodyPostionSelected(11);
                    ExaminingFragment.enableRecordingButton();
                    exminatingHelper.setFrontClick(true);
                    rippleSelectedView = ripple_lungs_lt_lower_field;
                    showSelectedFrontPostionAnimation(""+11);
                    if(exminatingHelper.isRecordingStarted()){
                        
                        frontRecoredPositions.put(""+11,front_lungs_lt_lower_field);
                    }
                    mContext.showPostionsBeforeRecording();
                }
            });

        }else{
            exminatingHelper.setFrontClick(false);
            ripple_back_lungs_lt_upper_field = (RippleAnimationView)layout.findViewById(R.id.ripple_back_lungs_lt_upper_field);
            ripple_back_lungs_lt_middle_field = (RippleAnimationView)layout.findViewById(R.id.ripple_back_lungs_lt_middle_field);
            ripple_back_lungs_lt_lower_field  = (RippleAnimationView)layout.findViewById(R.id.ripple_back_lungs_lt_lower_field);
            ripple_back_lungs_lt_costophrenicangle  = (RippleAnimationView)layout.findViewById(R.id.ripple_back_lungs_lt_costophrenicangle);
            ripple_back_lungs_rt_upper_field  = (RippleAnimationView)layout.findViewById(R.id.ripple_back_lungs_rt_upper_field);
            ripple_back_lungs_rt_middle_field  = (RippleAnimationView)layout.findViewById(R.id.ripple_back_lungs_rt_middle_field);
            ripple_back_lungs_rt_lower_field  = (RippleAnimationView)layout.findViewById(R.id.ripple_back_lungs_rt_lower_field);
            ripple_back_lungs_rt_costophrenicangle  = (RippleAnimationView)layout.findViewById(R.id.ripple_back_lungs_rt_costophrenicangle);



            back_lungs_lt_upper_field = (Button)layout.findViewById(R.id.back_lungs_lt_upper_field);
            back_lungs_lt_middle_field = (Button)layout.findViewById(R.id.back_lungs_lt_middle_field);
            back_lungs_lt_lower_field  = (Button)layout.findViewById(R.id.back_lungs_lt_lower_field);
            back_lungs_lt_costophrenicangle  = (Button)layout.findViewById(R.id.back_lungs_lt_costophrenicangle);
            back_lungs_rt_upper_field  = (Button)layout.findViewById(R.id.back_lungs_rt_upper_field);
            back_lungs_rt_middle_field  = (Button)layout.findViewById(R.id.back_lungs_rt_middle_field);
            back_lungs_rt_lower_field  = (Button)layout.findViewById(R.id.back_lungs_rt_lower_field);
            back_lungs_rt_costophrenicangle  = (Button)layout.findViewById(R.id.back_lungs_rt_costophrenicangle);



            ripple_back_lungs_lt_upper_field.startRippleAnimation();
            ripple_back_lungs_lt_middle_field.startRippleAnimation();
            ripple_back_lungs_lt_lower_field.startRippleAnimation();
            ripple_back_lungs_rt_upper_field.startRippleAnimation();
            ripple_back_lungs_rt_middle_field.startRippleAnimation();
            ripple_back_lungs_rt_lower_field.startRippleAnimation();
            ripple_back_lungs_lt_costophrenicangle.startRippleAnimation();
            ripple_back_lungs_rt_costophrenicangle.startRippleAnimation();

            // Postion 1
            back_lungs_lt_upper_field.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ExaminingFragment.isAnyPostionSelect = false;
                    exminatingHelper.setBackBodyPostionSelected(1);
                    ExaminingFragment.enableRecordingButton();
                    exminatingHelper.setFrontClick(false);
                    showSelectedBackPostionAnimation(""+1);
                    if(exminatingHelper.isRecordingStarted()){
                        backRecoredPositions.put(""+1,back_lungs_lt_upper_field);
                    }
                    mContext.showPostionsBeforeRecording();
                }
            });
            //Positon 2
            back_lungs_lt_middle_field.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ExaminingFragment.isFrontPositionsSelected = false;
                    exminatingHelper.setBackBodyPostionSelected(2);
                    ExaminingFragment.enableRecordingButton();
                    exminatingHelper.setFrontClick(false);
                    showSelectedBackPostionAnimation(""+2);
                    if(exminatingHelper.isRecordingStarted()){
                        backRecoredPositions.put(""+2,back_lungs_lt_middle_field);
                    }
                    mContext.showPostionsBeforeRecording();

                }
            });

            // Postion 3
            back_lungs_lt_lower_field.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ExaminingFragment.isFrontPositionsSelected = false;
                    exminatingHelper.setBackBodyPostionSelected(3);
                    ExaminingFragment.enableRecordingButton();
                    exminatingHelper.setFrontClick(false);
                    showSelectedBackPostionAnimation(""+3);
                    if(exminatingHelper.isRecordingStarted()){
                        backRecoredPositions.put(""+3,back_lungs_lt_lower_field);
                    }

                    mContext.showPostionsBeforeRecording();

                }
            });
            //Positon 4
            back_lungs_lt_costophrenicangle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ExaminingFragment.isFrontPositionsSelected = false;
                    exminatingHelper.setBackBodyPostionSelected(4);
                    ExaminingFragment.enableRecordingButton();
                    exminatingHelper.setFrontClick(false);
                    showSelectedBackPostionAnimation(""+4);
                    if(exminatingHelper.isRecordingStarted()){
                        backRecoredPositions.put(""+4,back_lungs_lt_costophrenicangle);
                    }
                    mContext.showPostionsBeforeRecording();
                }
            });

            // Postion 5
            back_lungs_rt_upper_field.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ExaminingFragment.isFrontPositionsSelected = false;
                    exminatingHelper.setBackBodyPostionSelected(5);
                    ExaminingFragment.enableRecordingButton();
                    exminatingHelper.setFrontClick(false);
                    showSelectedBackPostionAnimation(""+5);
                    if(exminatingHelper.isRecordingStarted()){
                        backRecoredPositions.put(""+5,back_lungs_rt_upper_field);
                    }
                    mContext.showPostionsBeforeRecording();
                }
            });

            // Postion 6
            back_lungs_rt_middle_field.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ExaminingFragment.isFrontPositionsSelected = false;
                    exminatingHelper.setBackBodyPostionSelected(6);
                    ExaminingFragment.enableRecordingButton();
                    exminatingHelper.setFrontClick(false);
                    showSelectedBackPostionAnimation(""+6);
                    if(exminatingHelper.isRecordingStarted()){
                        backRecoredPositions.put(""+6,back_lungs_rt_middle_field);
                    }
                    mContext.showPostionsBeforeRecording();

                }
            });
            //Positon 7
            back_lungs_rt_lower_field.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ExaminingFragment.isFrontPositionsSelected = false;
                    exminatingHelper.setBackBodyPostionSelected(7);
                    ExaminingFragment.enableRecordingButton();
                    exminatingHelper.setFrontClick(false);
                    showSelectedBackPostionAnimation(""+7);
                    if(exminatingHelper.isRecordingStarted()){
                        backRecoredPositions.put(""+7,back_lungs_rt_lower_field);
                    }
                    mContext.showPostionsBeforeRecording();

                }
            });

            // Postion 8
            back_lungs_rt_costophrenicangle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ExaminingFragment.isFrontPositionsSelected = false;
                    exminatingHelper.setBackBodyPostionSelected(8);
                    ExaminingFragment.enableRecordingButton();
                    exminatingHelper.setFrontClick(false);
                    showSelectedBackPostionAnimation(""+8);
                    if(exminatingHelper.isRecordingStarted()){
                        backRecoredPositions.put(""+8,back_lungs_rt_costophrenicangle);
                    }
                    mContext.showPostionsBeforeRecording();

                }
            });


        }


        return layout;
    }




    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return ModelObject.values().length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return ""; /*mContext.getString(customPagerEnum.getTitleResId())*/
    }

    public static void showSelectedFrontPostionAnimation(String position){
        initilizeFrontPostion();
        stopLastFrontAnimation();
        startORStopFrontAnimation(position,true);
    }

    public static void stopLastFrontAnimation( ){
        if(frontPostions.size() > 0){
            for(String position : frontPostions){
                startORStopFrontAnimation(position,false);
            }
        }
    }

    public static void showSelectedBackPostionAnimation(String position){
        initilizeBackPostion();
        stopLastBackAnimation();
        startORStopBackAnimation(position,true);
    }

    public static void stopLastBackAnimation( ){
        if(backPostions.size() > 0){
            for(String position : backPostions){
                startORStopBackAnimation(position,false);
            }
        }
    }
    public static void startORStopFrontAnimation(String pos, boolean isStart){
        int postion= Integer.parseInt(pos);
        //heart section
        if(postion == 1){
            if(isStart){
                ripple_front_heart_aortic_valve.startRippleAnimation();
            }else{
                ripple_front_heart_aortic_valve.stopRippleAnimation();
            }
            if(!frontRecoredPositions.isEmpty()){
                Button isRecored = frontRecoredPositions.get(pos);
                if(isRecored!=null && !isStart){
                    ripple_front_heart_aortic_valve.stopRippleAnimation();
                    isRecored.setEnabled(false);
                }
            }

        }
        if(postion == 2){
            if(isStart){
                front_ripple_heart_pulmonic_valve.startRippleAnimation();
            }else{
                front_ripple_heart_pulmonic_valve.stopRippleAnimation();
            }
            if(!frontRecoredPositions.isEmpty()){
                Button isRecored = frontRecoredPositions.get(pos);
                if(isRecored!=null && !isStart){
                    front_ripple_heart_pulmonic_valve.stopRippleAnimation();
                    isRecored.setEnabled(false);
                }
            }
        }
        if(postion == 3){
            if(isStart){
                ripple_front_heart_erbpoint.startRippleAnimation();
            }else{
                ripple_front_heart_erbpoint.stopRippleAnimation();
            }
            if(!frontRecoredPositions.isEmpty() && !isStart) {
                Button isRecored = frontRecoredPositions.get(pos);
                if (isRecored != null) {
                    ripple_front_heart_erbpoint.stopRippleAnimation();
                    isRecored.setEnabled(false);
                }
            }
        }
        if(postion == 4){
            if(isStart){
                front_ripple_heart_tricuspidvalve.startRippleAnimation();
            }else{
                front_ripple_heart_tricuspidvalve.stopRippleAnimation();
            }
            if(!frontRecoredPositions.isEmpty() && !isStart) {
                Button isRecored = frontRecoredPositions.get(pos);
                if (isRecored != null) {
                    front_ripple_heart_tricuspidvalve.stopRippleAnimation();
                    isRecored.setEnabled(false);
                }
            }
        }

        if(postion == 5){
            if(isStart){
                ripple_front_heart_mitral_valve.startRippleAnimation();
            }else{
                ripple_front_heart_mitral_valve.stopRippleAnimation();
            }
            if(!frontRecoredPositions.isEmpty() && !isStart) {
                Button isRecored = frontRecoredPositions.get(pos);
                if (isRecored != null) {
                    ripple_front_heart_mitral_valve.stopRippleAnimation();
                    isRecored.setEnabled(false);
                }
            }

        }

        //lungs section

        if(postion == 6){
            if(isStart){
                fripple_front_Lungs_Rt_upper_field_1.startRippleAnimation();
            }else{
                fripple_front_Lungs_Rt_upper_field_1.stopRippleAnimation();
            }
            if(!frontRecoredPositions.isEmpty() && !isStart) {
                Button isRecored = frontRecoredPositions.get(pos);
                if (isRecored != null) {
                    fripple_front_Lungs_Rt_upper_field_1.stopRippleAnimation();
                    isRecored.setEnabled(false);
                }
            }
        }
        if(postion == 7){
            if(isStart){
                fripple_front_Lungs_Rt_upper_field_2.startRippleAnimation();
            }else{
                fripple_front_Lungs_Rt_upper_field_2.stopRippleAnimation();
            }
            if(!frontRecoredPositions.isEmpty() && !isStart) {
                Button isRecored = frontRecoredPositions.get(pos);
                if (isRecored != null) {
                    fripple_front_Lungs_Rt_upper_field_2.stopRippleAnimation();
                    isRecored.setEnabled(false);
                }
            }
        }
        if(postion == 8){
            if(isStart){
                fripple_front_Lungs_Rt_middile_field.startRippleAnimation();
            }else{
                fripple_front_Lungs_Rt_middile_field.stopRippleAnimation();
            }
            if(!frontRecoredPositions.isEmpty() && !isStart) {
                Button isRecored = frontRecoredPositions.get(pos);
                if (isRecored != null) {
                    fripple_front_Lungs_Rt_middile_field.stopRippleAnimation();
                    isRecored.setEnabled(false);
                }
            }
        }
        if(postion == 9){
            if(isStart){
                fripple_front_Lungs_Rt_lower_field.startRippleAnimation();
            }else{
                fripple_front_Lungs_Rt_lower_field.stopRippleAnimation();
            }
            if(!frontRecoredPositions.isEmpty() && !isStart) {
                Button isRecored = frontRecoredPositions.get(pos);
                if (isRecored != null) {
                    fripple_front_Lungs_Rt_lower_field.stopRippleAnimation();
                    isRecored.setEnabled(false);
                }
            }
        }

        if(postion == 10){
            if(isStart){
                ripple_lungs_lt_upper_field.startRippleAnimation();
            }else{
                ripple_lungs_lt_upper_field.stopRippleAnimation();
            }
            if(!frontRecoredPositions.isEmpty() && !isStart) {
                Button isRecored = frontRecoredPositions.get(pos);
                if (isRecored != null) {
                    ripple_lungs_lt_upper_field.stopRippleAnimation();
                    isRecored.setEnabled(false);
                }
            }
        }
        if(postion == 11){
            if(isStart){
                ripple_lungs_lt_lower_field.startRippleAnimation();
            }else{
                ripple_lungs_lt_lower_field.stopRippleAnimation();
            }
            if(!frontRecoredPositions.isEmpty() && !isStart) {
                Button isRecored = frontRecoredPositions.get(pos);
                if (isRecored != null) {
                    ripple_lungs_lt_lower_field.stopRippleAnimation();
                    isRecored.setEnabled(false);
                }
            }
        }

    }

    public static void startORStopBackAnimation(String pos, boolean isStart){
        int postion= Integer.parseInt(pos);
        //heart section
        if(postion == 1){
            if(isStart){
                ripple_back_lungs_lt_upper_field.startRippleAnimation();
            }else{
                ripple_back_lungs_lt_upper_field.stopRippleAnimation();
            }

            if(!backRecoredPositions.isEmpty()){
                Button isRecored = backRecoredPositions.get(pos);
                if(isRecored!=null && !isStart){
                    ripple_back_lungs_lt_upper_field.stopRippleAnimation();
                    isRecored.setEnabled(false);
                }
            }
        }
        if(postion == 2){
            if(isStart){
                ripple_back_lungs_lt_middle_field.startRippleAnimation();
            }else{
                ripple_back_lungs_lt_middle_field.stopRippleAnimation();
            }

            if(!backRecoredPositions.isEmpty()){
                Button isRecored = backRecoredPositions.get(pos);
                if(isRecored!=null && !isStart){
                    ripple_back_lungs_lt_middle_field.stopRippleAnimation();
                    isRecored.setEnabled(false);
                }
            }
        }
        if(postion == 3){
            if(isStart){
                ripple_back_lungs_lt_lower_field.startRippleAnimation();
            }else{
                ripple_back_lungs_lt_lower_field.stopRippleAnimation();
            }

            if(!backRecoredPositions.isEmpty()){
                Button isRecored = backRecoredPositions.get(pos);
                if(isRecored!=null && !isStart){
                    ripple_back_lungs_lt_lower_field.stopRippleAnimation();
                    isRecored.setEnabled(false);
                }
            }
        }
        if(postion == 4){
            if(isStart){
                ripple_back_lungs_lt_costophrenicangle.startRippleAnimation();
            }else{
                ripple_back_lungs_lt_costophrenicangle.stopRippleAnimation();
            }

            if(!backRecoredPositions.isEmpty()){
                Button isRecored = backRecoredPositions.get(pos);
                if(isRecored!=null && !isStart){
                    ripple_back_lungs_lt_costophrenicangle.stopRippleAnimation();
                    isRecored.setEnabled(false);
                }
            }
        }

        if(postion == 5){
            if(isStart){
                ripple_back_lungs_rt_upper_field.startRippleAnimation();
            }else{
                ripple_back_lungs_rt_upper_field.stopRippleAnimation();
            }

            if(!backRecoredPositions.isEmpty()){
                Button isRecored = backRecoredPositions.get(pos);
                if(isRecored!=null && !isStart){
                    ripple_back_lungs_rt_upper_field.stopRippleAnimation();
                    isRecored.setEnabled(false);
                }
            }
        }

        //lungs section

        if(postion == 6){
            if(isStart){
                ripple_back_lungs_rt_middle_field.startRippleAnimation();
            }else{
                ripple_back_lungs_rt_middle_field.stopRippleAnimation();
            }

            if(!backRecoredPositions.isEmpty()){
                Button isRecored = backRecoredPositions.get(pos);
                if(isRecored!=null && !isStart){
                    ripple_back_lungs_rt_middle_field.stopRippleAnimation();
                    isRecored.setEnabled(false);
                }
            }
        }
        if(postion == 7){
            if(isStart){
                ripple_back_lungs_rt_lower_field.startRippleAnimation();
            }else{
                ripple_back_lungs_rt_lower_field.stopRippleAnimation();
            }

            if(!backRecoredPositions.isEmpty()){
                Button isRecored = backRecoredPositions.get(pos);
                if(isRecored!=null && !isStart){
                    ripple_back_lungs_rt_lower_field.stopRippleAnimation();
                    isRecored.setEnabled(false);
                }
            }

        }
        if(postion == 8){
            if(isStart){
                ripple_back_lungs_rt_costophrenicangle.startRippleAnimation();
            }else{
                ripple_back_lungs_rt_costophrenicangle.stopRippleAnimation();
            }

            if(!backRecoredPositions.isEmpty()){
                Button isRecored = backRecoredPositions.get(pos);
                if(isRecored!=null && !isStart){
                    ripple_back_lungs_rt_costophrenicangle.stopRippleAnimation();
                    isRecored.setEnabled(false);
                }
            }
        }

    }

    public void stopAllAnimation(){

    }


    public static void initilizeFrontPostion(){
        if(frontPostions.size() <= 0){
            frontPostions.add(""+1);
            frontPostions.add(""+2);
            frontPostions.add(""+3);
            frontPostions.add(""+4);
            frontPostions.add(""+5);
            frontPostions.add(""+6);
            frontPostions.add(""+7);
            frontPostions.add(""+8);
            frontPostions.add(""+9);
            frontPostions.add(""+10);
            frontPostions.add(""+11);
        }

    }

    public static void initilizeBackPostion(){
        if(backPostions.size() <= 0){
            backPostions.add(""+1);
            backPostions.add(""+2);
            backPostions.add(""+3);
            backPostions.add(""+4);
            backPostions.add(""+5);
            backPostions.add(""+6);
            backPostions.add(""+7);
            backPostions.add(""+8);
            backPostions.add(""+9);
            backPostions.add(""+10);
            backPostions.add(""+11);
        }

    }



}