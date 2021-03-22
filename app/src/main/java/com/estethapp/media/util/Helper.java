package com.estethapp.media.util;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.estethapp.media.R;
import com.estethapp.media.view.ToastStyle;


/**
 * Created by Irfan Ali on 30/12/16.
 */
public class Helper {


    public static String[] permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.SYSTEM_ALERT_WINDOW,
            Manifest.permission.CAPTURE_AUDIO_OUTPUT,
            Manifest.permission.READ_EXTERNAL_STORAGE,
  //    Manifest.permission.FOREGROUND_SERVICE
//      Manifest.permission.CAMERA,
      //ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION

    };

    public static void fullScreen(Activity appCompatActivity) {

        if (Build.VERSION.SDK_INT < 16) {
            // Hide the status and action bar. for below Android v4.1
            appCompatActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            // Hide the status bar. for Above Android v4.0
            appCompatActivity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

            // Hide the action bar
            ActionBar actionBar = appCompatActivity.getActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }

        }
    }


    public static boolean checkPermissionsArray(Context context) {


        for (String permission : permissions) {
            if (!checkPermissions(context, permission)) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkRecordPermissionsArray(Context context) {


        String permission = Manifest.permission.RECORD_AUDIO;
        //for (String permission : permissions) {
        if (!checkPermissions(context, permission)) {
            return false;
        }
        //}
        return true;
    }

    public static boolean checkPermissions(Context context, String permission) {
        int permissionRequest = ActivityCompat.checkSelfPermission(context, permission);
        return permissionRequest == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermissions(Activity activity, int permissionsRequestCode) {
        ActivityCompat.requestPermissions(
                activity,
                permissions,
                permissionsRequestCode
        );

    }

    public static void requestAudioRecordPermissions(Activity activity, int permissionsRequestCode) {
        ActivityCompat.requestPermissions(
                activity,
                new String[]{Manifest.permission.RECORD_AUDIO},
                permissionsRequestCode
        );

    }

    public static void hideKeyboard(Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(((Activity) mContext).getWindow()
                .getCurrentFocus().getWindowToken(), 0);
    }

    public static void showToast(String message, ToastStyle status) {
        Toast toast = Toast.makeText(App.context, message, Toast.LENGTH_LONG);
        if (status == ToastStyle.SUCCESS) {
            toast.getView().setBackgroundResource(R.drawable.toast_success_bg);
            TextView tv = (TextView) ((ViewGroup) toast.getView()).getChildAt(0);
            tv.setTextColor(Color.WHITE);

        } else if (status == ToastStyle.ERROR) {
            toast.getView().setBackgroundResource(R.drawable.toast_failure_bg);
            TextView tv = (TextView) ((ViewGroup) toast.getView()).getChildAt(0);
            tv.setTextColor(Color.WHITE);

        } else if (status == ToastStyle.DEFAULT) {
            toast.getView().setBackgroundResource(R.drawable.toast_default_bg);
            TextView tv = (TextView) ((ViewGroup) toast.getView()).getChildAt(0);
            tv.setTextColor(Color.WHITE);
        }
        toast.show();
    }

    public static String getCustomEncryptedValue(String text) {
        StringBuilder builder = new StringBuilder();
        for (char character : text.toCharArray()) {
            String ch = Character.getNumericValue(character) + "";
            String enc = Base64.encodeToString(ch.getBytes(), Base64.DEFAULT).replace("\n", "")
              .replace("=", "").replace("\n\r", "");
            builder.append(enc);
        }
        return text;// builder.toString();
    }

}
