package com.estethapp.media.view;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.Window;

/**
 * Created by Irfan Ali on 1/24/2017.
 */
public class ProgressDialogView  {

    public ProgressDialog progressDialog;
    public Dialog dialog;
    public static ProgressDialogView instance;
    public static ProgressDialogView getInstance(){
        if(instance == null){
            instance = new ProgressDialogView();
        }
        return  instance;
    }

    public ProgressDialog setProgressDialog(Context context,String title){
        progressDialog = new ProgressDialog(context);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setMessage(title);
        progressDialog.setCancelable(false);
        return progressDialog;
    }

    public ProgressDialog setProgressWithoutDialog(Context context,String title){
        progressDialog = new ProgressDialog(context);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setProgressTitle(title);
        progressDialog.setCancelable(false);
        return progressDialog;
    }

    public void setProgressTitle(String title){
        if(progressDialog !=null){
            progressDialog.setMessage(title);
        }
    }


}
