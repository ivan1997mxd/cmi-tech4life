package com.estethapp.media;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;
import com.estethapp.media.util.App;
import com.estethapp.media.util.Helper;
import com.estethapp.media.util.Prefs;
import com.estethapp.media.util.UserModel;
import com.estethapp.media.util.Util;
import com.estethapp.media.view.ToastStyle;
import com.estethapp.media.web.HttpCaller;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Irfan Ali on 12/29/2016.
 */
public class ActivityLogin extends AppCompatActivity implements View.OnClickListener{


  private EditText editTextUsername ;
  private EditText editTextUserpass ;

  private CheckBox checkBoxRememberMe;
  private Button btnSignIn;
  private TextView btnSignup;

  private TextView txtForgetPass;

  private SharedPreferences prefsInstance;
  private Prefs prefs;

  private boolean isRemember = false;
  App app;

  Intent intent;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Fabric.with(getApplicationContext(), new Crashlytics());
    setContentView(R.layout.activity_activity_login);

    app=(App)getApplicationContext();

    editTextUsername = (EditText) findViewById(R.id.txt_username);
    editTextUserpass = (EditText) findViewById(R.id.txt_userpass);


    btnSignIn        = (Button) findViewById(R.id.btn_login);
    btnSignup        = (TextView) findViewById(R.id.btn_signup);

    checkBoxRememberMe = (CheckBox) findViewById(R.id.isRememberCheckBox);

    txtForgetPass    = (TextView) findViewById(R.id.txt_forgotpass);

    prefs = Prefs.getInstance();
    prefsInstance = prefs.init(ActivityLogin.this);

    btnSignIn.setOnClickListener(this);
    btnSignup.setOnClickListener(this);
    txtForgetPass.setOnClickListener(this);

    FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(ActivityLogin.this);


    Bundle bundle = new Bundle();
    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "1");
    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "irfan");
    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

    Bundle params = new Bundle();
    params.putString("image_name", "df");
    params.putString("full_text", "asdff");
    mFirebaseAnalytics.logEvent("share_image", params);
  }

  @Override
  public void onClick(View view) {

    if(!Util.isInternetAvailable(ActivityLogin.this)) {
      Toast.makeText(ActivityLogin.this, getString(R.string.internet_problem), Toast.LENGTH_SHORT).show();
    }else{
      switch (view.getId()) {
        case R.id.btn_login:
          String userName = editTextUsername.getText().toString();
          String userPass = editTextUserpass.getText().toString();


          //Username: dkseelro
          //Password: Mycomputer.123

          // Empty Info Generates autologin for testing

          if ( userName.isEmpty() ) {
            userName = "dkseelro";
            userPass = "mycomputer.123";
          }

          isRemember = checkBoxRememberMe.isChecked();

          if (userName.equals("")) {
            app.setErrorOnEditText(editTextUsername, getString(R.string.please_put_username));
          }else if(userPass.equals("")){
            app.setErrorOnEditText(editTextUserpass, getString(R.string.please_put_pass));
          } else {
            try {

              requestServerLogin(userName, userPass);
              //  Toast.makeText(getApplicationContext(),""+userName,Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
              e.printStackTrace();
            }
          }
          break;
        case R.id.btn_signup:
          intent = new Intent(ActivityLogin.this, ActivitySignup.class);
          intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
          startActivity(intent);
          finish();
          break;
        case R.id.txt_forgotpass:
          intent = new Intent(ActivityLogin.this, ActivityForgotPassword.class);
          intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
          startActivity(intent);
          finish();
          break;
      }
    }
  }



  void requestServerLogin(String email, String password) throws JSONException {

    try {
      JSONObject jsonbody = new JSONObject();
      jsonbody.put("Email"        ,email);
      jsonbody.put("Password"     ,Helper.getCustomEncryptedValue(password));

      HttpCaller
        .getInstance()
        .loginCall(ActivityLogin.this,jsonbody,
          new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
              try {
                if(response.getBoolean("Status")){

                  if(isRemember){
                    prefsInstance.edit().putBoolean(prefs.ISLOGIN,true).commit();
                  }
                  String res = response.getJSONObject("Response").toString();
                  prefsInstance.edit().putString(prefs.USERMODEL,res).commit();
                  App.profileModel = new Gson().fromJson(res, UserModel.class);
                  App.profileModel.GCMToken = prefsInstance.getString(prefs.GCM_TOKEN,"");
                  prefsInstance.edit().putString("USERNAME",editTextUsername.getText().toString()).commit();
           //      Toast.makeText(getContext(),prefsInstance.getString("USERNAME","AHSAN"),Toast.LENGTH_SHORT).show();
                  FirebaseInstanceId.getInstance().getToken();
                  // Register Applozic Chat Service

                  if(isRemember){

                  //  Toast.makeText(app, prefs.USERNAME+"IF HUN", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ActivityLogin.this, ActivityPin.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                  }else{


                   //   Toast.makeText(app, prefs.USERNAME+"ELSE HN", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ActivityLogin.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();


                  }

                }else{
                  Helper.showToast(""+response.getString("Reason").toString(), ToastStyle.ERROR);
                }
              } catch (Exception e) {
                e.printStackTrace();
              }

            }

          },
          new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {

              Toast.makeText(app, error.toString(), Toast.LENGTH_SHORT).show();

            }
          },true);

    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

}

