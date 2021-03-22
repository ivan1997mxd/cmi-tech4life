package com.estethapp.media.util;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.estethapp.media.ChatActivity;
import com.estethapp.media.ChatDetailActivity;
import com.estethapp.media.web.HttpCaller;
import com.franmontiel.localechanger.LocaleChanger;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Irfan Ali on 12/29/2016.
 */
public class App extends Application {

    public static boolean isGroupChat = false;
    public static boolean isCommentDetail = false;

    public static ChatActivity chatActivityContext;
    public static ChatDetailActivity chatDetailActivityContext;

    public static Context context;
    public static String GCMToken;
    public static UserModel profileModel = new UserModel();
    public static RequestQueue requestQueue;

    private SharedPreferences prefsInstance;
    private Prefs prefs;

    public static final List<Locale> SUPPORTED_LOCALES =
            Arrays.asList(
                    new Locale("en", "US"),
                    new Locale("es", "ES")
            );

    @Override
    public void onCreate() {
        super.onCreate();

//        LocaleChanger.initialize(getApplicationContext(), SUPPORTED_LOCALES);

        context = getApplicationContext();
        prefs = Prefs.getInstance();
        prefsInstance = prefs.init(context);

        Fabric.with(this, new Crashlytics());

        Configuration dbConfiguration = new Configuration.Builder(this).setDatabaseName("eSteth.db").create();
        ActiveAndroid.initialize(this);

        HttpCaller.init(getApplicationContext());
        requestQueue = Volley.newRequestQueue(getApplicationContext());


        //FOR SPANISH es, ENGLISH en
        Locale myLocale = new Locale("en");
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

    }

    public void setErrorOnEditText(final EditText mEditText, String error) {
        mEditText.setError(error);
        mEditText.requestFocus();
        mEditText.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mEditText.setError(null);
                mEditText.clearFocus();
                return false;
            }
        });
    }

}
