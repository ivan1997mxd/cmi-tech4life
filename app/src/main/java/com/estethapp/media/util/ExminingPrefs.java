package com.estethapp.media.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Irfan Ali on 12/29/2016.
 */
public class ExminingPrefs {


    private static ExminingPrefs ourInstance = new ExminingPrefs();
    private String PREFS_NAME = "Eximing";

    private SharedPreferences prefs;
//  public String USERNAME = "USERNAME";
    public static ExminingPrefs getInstance() {
        return ourInstance;
    }

    public SharedPreferences init(Context context){
        if(prefs == null){
            prefs = context.getSharedPreferences(PREFS_NAME,
                    Context.MODE_PRIVATE);
        }
        return prefs;
    }

}
