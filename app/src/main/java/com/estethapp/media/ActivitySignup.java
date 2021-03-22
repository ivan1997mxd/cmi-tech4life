package com.estethapp.media;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.regex.Pattern;

import  com.estethapp.media.util.App;
import  com.estethapp.media.util.EmailValidator;
import  com.estethapp.media.util.Prefs;
import  com.estethapp.media.util.UserModel;
import  com.estethapp.media.util.Util;
import  com.estethapp.media.web.HttpCaller;
//import io.fabric.sdk.android.Fabric;

/**
 * Created by Irfan Ali on 12/29/2016.
 */
public class ActivitySignup  extends AppCompatActivity implements View.OnClickListener{

    private EditText editTextFirstname ;
    private EditText editTextUserName ;
    private EditText editTextUserEmail ;
    private EditText editTextUserpass ;
    private EditText editTextUseConfirmrpass ;
    private TextView txtLogin ;

    private Button btnSignup;

    private EmailValidator emailValidator;

    private SharedPreferences prefsInstance;
    private Prefs prefs;

    Pattern pattern = Pattern.compile("[A-Za-z0-9_]+");
    App app;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //   Fabric.with(getApplicationContext(), new Crashlytics());
        setContentView(R.layout.activity_activity_signup);

        app=(App)getApplicationContext();

        editTextFirstname = (EditText) findViewById(R.id.txt_firstname);
        editTextUserName = (EditText) findViewById(R.id.txt_username);
        editTextUserEmail = (EditText) findViewById(R.id.txt_email);
        editTextUserpass = (EditText) findViewById(R.id.txt_userpass);
        editTextUseConfirmrpass = (EditText) findViewById(R.id.txt_confrimpass);

        txtLogin = (TextView) findViewById(R.id.txt_login);

        btnSignup        = (Button) findViewById(R.id.btn_signup);
        btnSignup.setOnClickListener(this);
        txtLogin.setOnClickListener(this);


        emailValidator = new EmailValidator();

        editTextUserpass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try{
                    String value = editTextUserpass.getText().toString().toLowerCase(Locale.getDefault());
                    if(charSequence.length() >= 5){
                        if(!isValid(charSequence.toString())){
                            //app.setErrorOnEditText(editTextUserpass, getString(R.string.please_put_eight_char));
                        }

                    }

                }
                catch (Exception ex){
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        prefs = Prefs.getInstance();
        prefsInstance = prefs.init(ActivitySignup.this);

    }

    @Override
    public void onClick(View view) {

        if(!Util.isInternetAvailable(ActivitySignup.this)) {
            Toast.makeText(ActivitySignup.this, getString(R.string.internet_problem), Toast.LENGTH_SHORT).show();
        }else {
            switch (view.getId()) {
                case R.id.btn_signup:

                    String firstName = editTextFirstname.getText().toString();
                    String userName = editTextUserName.getText().toString();
                    String userEmail = editTextUserEmail.getText().toString();
                    String userPass = editTextUserpass.getText().toString();
                    String userconfirmPass = editTextUseConfirmrpass.getText().toString();

                    boolean isEmailValid = emailValidator.validate(userEmail);

                    boolean isValid =true;
                    if (firstName.equals("")) {
                        app.setErrorOnEditText(editTextFirstname, getString(R.string.please_put_all_field1));
                        isValid = false;
                    }else if (userName.equals("")) {
                        app.setErrorOnEditText(editTextUserName, getString(R.string.please_put_all_field2));
                        isValid = false;
                    }else if (userEmail.equals("")) {
                        app.setErrorOnEditText(editTextUserEmail, getString(R.string.please_put_all_field3));
                        isValid = false;
                    }else if(!isEmailValid){
                        app.setErrorOnEditText(editTextUserEmail, getString(R.string.please_put_valid_email));
                        isValid = false;
                    }else if (userPass.equals("")) {
                        app.setErrorOnEditText(editTextUserpass, getString(R.string.please_put_all_field4));
                        isValid = false;
                    }else if(!isValid(userPass)){
                        app.setErrorOnEditText(editTextUserpass, getString(R.string.please_put_eight_char));
                        isValid = false;
                    }else if(!pattern.matcher(userName).matches()){
                        app.setErrorOnEditText(editTextUserName, getString(R.string.put_valid_username));
                        isValid = false;
                    }else if(!userPass.equals(userconfirmPass)){
                        app.setErrorOnEditText(editTextUseConfirmrpass, getString(R.string.pass_not_matched));
                        isValid = false;
                    }else{
                        requestServer(firstName, userName, userEmail, userPass);
                    }

                    break;

                case R.id.txt_login:
                    Intent intent = new Intent(ActivitySignup.this, ActivityLogin.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();

                    break;
            }
        }
    }

    void requestServer(final String fName, final String patientID, final String email, String password){

//        App.profileModel.GCMToken = App.profileModel.GCMToken == null ? FirebaseInstanceId.getInstance().getToken() : App.profileModel.GCMToken;
        JSONObject jsonbody = new JSONObject();
        try{
            jsonbody.put("Name",fName);
            jsonbody.put("PatientID",patientID);
            jsonbody.put("Email",email);
            jsonbody.put("Password",password);
            jsonbody.put("GCMToken" , App.profileModel.GCMToken);
        }catch (Exception e){
            e.printStackTrace();
        }

           HttpCaller.getInstance()
                    .registerCall(
                            ActivitySignup.this,
                            jsonbody,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if(response.getBoolean("Status")){

                                            prefsInstance.edit().putBoolean(prefs.ISLOGIN,true).commit();
                                            String res = response.getJSONObject("Response").toString();
                                            prefsInstance.edit().putString(prefs.USERMODEL,res).commit();


                                            App.profileModel =  new Gson().fromJson(res, UserModel.class);


                                            Intent intent = new Intent(ActivitySignup.this, ActivityPin.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish();

                                        }else{
                                            if(response.getString("Reason").equalsIgnoreCase("ALREADY_REGISTERED")){
                                                Toast.makeText(ActivitySignup.this, getString(R.string.txt_email_exist),Toast.LENGTH_LONG).show();
                                            }else{
                                                Toast.makeText(ActivitySignup.this, ""+response.getString("Message")+"\n"+response.getString("Reason"),Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            },
                            new Response.ErrorListener(){
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(ActivitySignup.this, ""+error.getMessage() ,Toast.LENGTH_LONG).show();

                                }
                            },true);

    }

    public static boolean isValid(String password) {
        Boolean atleastOneUpper = false;
        Boolean atleastOneLower = false;
        Boolean atleastOneDigit = false;

        if (password.length() < 8) { // If its less then 8 characters, its automatically not valid
            return false;
        }

        for (int i = 0; i < password.length(); i++) { // Lets iterate over only once. Saving time
            if (Character.isUpperCase(password.charAt(i))) {
                atleastOneUpper = true;
            }
            else if (Character.isLowerCase(password.charAt(i))) {
                atleastOneLower = true;
            }
            else if (Character.isDigit(password.charAt(i))) {
                atleastOneDigit = true;
            }
        }

        return (atleastOneUpper && atleastOneLower && atleastOneDigit); // Return true IFF the password is atleast eight characters long, has atleast one upper, lower and digit
    }



}
