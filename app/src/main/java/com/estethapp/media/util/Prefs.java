package com.estethapp.media.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Irfan Ali on 12/29/2016.
 */
public class Prefs {

    public String ISLOGIN = "ISLOGIN";
    public String GCM_TOKEN = "GCM_TOKEN";
    public String PIN = "PIN";
    public String USERMODEL = "USERMODEL";
public String USERNAME = "USERNAME";

    private static Prefs ourInstance = new Prefs();
    private String PREFS_NAME = "estethPrefs";
    private SharedPreferences prefs;
    public static Prefs getInstance() {
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
