package com.estethapp.media.adapter;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import java.util.ArrayList;
import com.estethapp.media.R;
import com.estethapp.media.util.ModelObject;
import com.estethapp.media.view.RippleAnimationView;


public class PlayerPositionViewPagerAdapter extends PagerAdapter {

    private Context mContext;
    public static RippleAnimationView ripple_front_heart_aortic_valve; //Postion  1
    public static RippleAnimationView front_ripple_heart_pulmonic_valve;//Postion 2
    public static RippleAnimationView ripple_front_heart_erbpoint;//Postion 3
    public static RippleAnimationView front_ripple_heart_tricuspidvalve;//Postion 4
    public static RippleAnimationView ripple_front_heart_mitral_valve;//Postion 5

    public static RippleAnimationView fripple_front_Lungs_Rt_upper_field_1;
    public static RippleAnimationView fripple_front_Lungs_Rt_upper_field_2;
    public static RippleAnimationView fripple_front_Lungs_Rt_middile_field;
    public static RippleAnimationView fripple_front_Lungs_Rt_lower_field;
    public static RippleAnimationView ripple_lungs_lt_lower_field;
    public static RippleAnimationView ripple_lungs_lt_upper_field;


    // back

    public static RippleAnimationView ripple_back_lungs_lt_upper_field; // Postion 1
    public static RippleAnimationView ripple_back_lungs_lt_middle_field;// Postion 2
    public static RippleAnimationView ripple_back_lungs_lt_lower_field;// Postion 3
    public static RippleAnimationView ripple_back_lungs_lt_costophrenicangle;// Postion  4
    public static RippleAnimationView ripple_back_lungs_rt_upper_field;// Postion 5
    public static RippleAnimationView ripple_back_lungs_rt_middle_field;// Postion 6
    public static RippleAnimationView ripple_back_lungs_rt_lower_field;// Postion 7
    public static RippleAnimationView ripple_back_lungs_rt_costophrenicangle;// Postion 8



    public static ArrayList<String> frontPostions = new ArrayList<>();
    public static ArrayList<String> backPostions = new ArrayList<>();

    public PlayerPositionViewPagerAdapter(Context context) {
        mContext = context;
    }

    private static ToggleButton toggleButton;

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        ModelObject modelObject = ModelObject.values()[position];
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(modelObject.getLayoutResId(), collection, false);
        collection.addView(layout);

        if(position == 0){

            toggleButton   = (ToggleButton) layout.findViewById(R.id.heart_lungsswitch);
            toggleButton.setVisibility(View.INVISIBLE);

            ripple_front_heart_aortic_valve  = (RippleAnimationView)layout.findViewById(R.id.ripple_front_heart_aortic_valve);
            ripple_front_heart_mitral_valve  = (RippleAnimationView)layout.findViewById(R.id.ripple_front_heart_mitral_valve);
            front_ripple_heart_pulmonic_valve  = (RippleAnimationView)layout.findViewById(R.id.front_ripple_heart_pulmonic_valve);
            ripple_front_heart_erbpoint  = (RippleAnimationView)layout.findViewById(R.id.ripple_front_heart_erbpoint);
            front_ripple_heart_tricuspidvalve  = (RippleAnimationView)layout.findViewById(R.id.front_ripple_heart_tricuspidvalve);


            fripple_front_Lungs_Rt_upper_field_1  = (RippleAnimationView)layout.findViewById(R.id.fripple_front_Lungs_Rt_upper_field_1);
            fripple_front_Lungs_Rt_upper_field_2  = (RippleAnimationView)layout.findViewById(R.id.fripple_front_Lungs_Rt_upper_field_2);
            fripple_front_Lungs_Rt_middile_field  = (RippleAnimationView)layout.findViewById(R.id.fripple_front_Lungs_Rt_middile_field);
            fripple_front_Lungs_Rt_lower_field  = (RippleAnimationView)layout.findViewById(R.id.fripple_front_Lungs_Rt_lower_field);
            ripple_lungs_lt_lower_field  = (RippleAnimationView)layout.findViewById(R.id.ripple_lungs_lt_lower_field);
            ripple_lungs_lt_upper_field   = (RippleAnimationView)layout.findViewById(R.id.ripple_lungs_lt_upper_field);


            ripple_front_heart_aortic_valve.stopRippleAnimation(); //Postion  1
            front_ripple_heart_pulmonic_valve.stopRippleAnimation();//Postion 2
            ripple_front_heart_erbpoint.stopRippleAnimation();//Postion 3
            front_ripple_heart_tricuspidvalve.stopRippleAnimation();//Postion 4
            ripple_front_heart_mitral_valve.stopRippleAnimation();//Postion 5

            fripple_front_Lungs_Rt_upper_field_1.stopRippleAnimation(); // Postion 6
            fripple_front_Lungs_Rt_upper_field_2.stopRippleAnimation();// Postion 7
            fripple_front_Lungs_Rt_middile_field.stopRippleAnimation();// Postion 8
            fripple_front_Lungs_Rt_lower_field.stopRippleAnimation();// Postion 9
            ripple_lungs_lt_lower_field.stopRippleAnimation();// Postion 10
            ripple_lungs_lt_upper_field.stopRippleAnimation();// Postion 11



        }else{

            ripple_back_lungs_lt_upper_field = (RippleAnimationView)layout.findViewById(R.id.ripple_back_lungs_lt_upper_field);
            ripple_back_lungs_lt_middle_field = (RippleAnimationView)layout.findViewById(R.id.ripple_back_lungs_lt_middle_field);
            ripple_back_lungs_lt_lower_field  = (RippleAnimationView)layout.findViewById(R.id.ripple_back_lungs_lt_lower_field);
            ripple_back_lungs_lt_costophrenicangle  = (RippleAnimationView)layout.findViewById(R.id.ripple_back_lungs_lt_costophrenicangle);
            ripple_back_lungs_rt_upper_field  = (RippleAnimationView)layout.findViewById(R.id.ripple_back_lungs_rt_upper_field);
            ripple_back_lungs_rt_middle_field  = (RippleAnimationView)layout.findViewById(R.id.ripple_back_lungs_rt_middle_field);
            ripple_back_lungs_rt_lower_field  = (RippleAnimationView)layout.findViewById(R.id.ripple_back_lungs_rt_lower_field);
            ripple_back_lungs_rt_costophrenicangle  = (RippleAnimationView)layout.findViewById(R.id.ripple_back_lungs_rt_costophrenicangle);

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
        frontPostions.add(""+position);
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
        backPostions.add(""+position);
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
            toggleButton.setChecked(true);
           if(isStart){
               ripple_front_heart_aortic_valve.startRippleAnimation();
           }else{
               ripple_front_heart_aortic_valve.stopRippleAnimation();
           }
        }
        if(postion == 2){
           if(isStart){
               front_ripple_heart_pulmonic_valve.startRippleAnimation();
           }else{
               front_ripple_heart_pulmonic_valve.stopRippleAnimation();
           }
        }
        if(postion == 3){
           if(isStart){
               ripple_front_heart_erbpoint.startRippleAnimation();
           }else{
               ripple_front_heart_erbpoint.stopRippleAnimation();
           }
        }
        if(postion == 4){
           if(isStart){
               front_ripple_heart_tricuspidvalve.startRippleAnimation();
           }else{
               front_ripple_heart_tricuspidvalve.stopRippleAnimation();
           }
        }

        if(postion == 5){
            if(isStart){
                ripple_front_heart_mitral_valve.startRippleAnimation();
            }else{
                ripple_front_heart_mitral_valve.stopRippleAnimation();
            }
        }

        //lungs section

        if(postion == 6){
            toggleButton.setChecked(false);
            if(isStart){
                fripple_front_Lungs_Rt_upper_field_1.startRippleAnimation();
            }else{
                fripple_front_Lungs_Rt_upper_field_1.stopRippleAnimation();
            }
        }
        if(postion == 7){
            if(isStart){
                fripple_front_Lungs_Rt_upper_field_2.startRippleAnimation();
            }else{
                fripple_front_Lungs_Rt_upper_field_2.stopRippleAnimation();
            }
        }
        if(postion == 8){
            if(isStart){
                fripple_front_Lungs_Rt_middile_field.startRippleAnimation();
            }else{
                fripple_front_Lungs_Rt_middile_field.stopRippleAnimation();
            }
        }
        if(postion == 9){
            if(isStart){
                fripple_front_Lungs_Rt_lower_field.startRippleAnimation();
            }else{
                fripple_front_Lungs_Rt_lower_field.stopRippleAnimation();
            }
        }

        if(postion == 10){
            if(isStart){
                ripple_lungs_lt_upper_field.startRippleAnimation();
            }else{
                ripple_lungs_lt_upper_field.stopRippleAnimation();
            }
        }
        if(postion == 11){
            if(isStart){
                ripple_lungs_lt_lower_field.startRippleAnimation();
            }else{
                ripple_lungs_lt_lower_field.stopRippleAnimation();
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
        }
        if(postion == 2){
            if(isStart){
                ripple_back_lungs_lt_middle_field.startRippleAnimation();
            }else{
                ripple_back_lungs_lt_middle_field.stopRippleAnimation();
            }
        }
        if(postion == 3){
            if(isStart){
                ripple_back_lungs_lt_lower_field.startRippleAnimation();
            }else{
                ripple_back_lungs_lt_lower_field.stopRippleAnimation();
            }
        }
        if(postion == 4){
            if(isStart){
                ripple_back_lungs_lt_costophrenicangle.startRippleAnimation();
            }else{
                ripple_back_lungs_lt_costophrenicangle.stopRippleAnimation();
            }
        }

        if(postion == 5){
            if(isStart){
                ripple_back_lungs_rt_upper_field.startRippleAnimation();
            }else{
                ripple_back_lungs_rt_upper_field.stopRippleAnimation();
            }
        }

        //lungs section

        if(postion == 6){
            if(isStart){
                ripple_back_lungs_rt_middle_field.startRippleAnimation();
            }else{
                ripple_back_lungs_rt_middle_field.stopRippleAnimation();
            }
        }
        if(postion == 7){
            if(isStart){
                ripple_back_lungs_rt_lower_field.startRippleAnimation();
            }else{
                ripple_back_lungs_rt_lower_field.stopRippleAnimation();
            }
        }
        if(postion == 8){
            if(isStart){
                ripple_back_lungs_rt_costophrenicangle.startRippleAnimation();
            }else{
                ripple_back_lungs_rt_costophrenicangle.stopRippleAnimation();
            }
        }

    }

}