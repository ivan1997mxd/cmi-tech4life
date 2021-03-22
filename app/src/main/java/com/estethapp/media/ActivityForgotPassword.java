package com.estethapp.media;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.estethapp.media.util.App;
import com.estethapp.media.util.EmailValidator;
import com.estethapp.media.util.Helper;
import com.estethapp.media.view.ToastStyle;
import com.estethapp.media.web.HttpCaller;


import org.json.JSONException;
import org.json.JSONObject;

public class ActivityForgotPassword extends AppCompatActivity {

    EditText txtResetEmail;
    Button btnResetPassword;
    TextView txtLogin;
    private EmailValidator emailValidator;
    App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        app=(App)getApplicationContext();
        emailValidator = new EmailValidator();
        txtResetEmail = (EditText)findViewById(R.id.txt_resetemail);
        btnResetPassword = (Button)findViewById(R.id.btn_reset);
        txtLogin = (TextView)findViewById(R.id.txt_login);

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ActivityForgotPassword.this, ActivityLogin.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = txtResetEmail.getText().toString();
                boolean isEmailValid = emailValidator.validate(email);
                if(email.equals("")){
                    app.setErrorOnEditText(txtResetEmail, getString(R.string.please_put_all_field3));
                }
                else if(isEmailValid){
                    requestServerLogin(email);
                    txtResetEmail.setText("");
                }else{
                    Helper.showToast(getString(R.string.enter_valid_email), ToastStyle.ERROR);
                }



            }
        });
    }

    void requestServerLogin(String email) {
       try {
            JSONObject jsonbody = new JSONObject();
            jsonbody.put("Email"        ,email);

            HttpCaller
                    .getInstance()
                    .forgetPassword(ActivityForgotPassword.this,jsonbody,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if(response.getBoolean("Status")){
                                            Helper.showToast(""+response.getString("Message").toString(), ToastStyle.SUCCESS);
                                        }else{
                                            Helper.showToast(""+response.getString("Message").toString(), ToastStyle.ERROR);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }

                            },
                            new Response.ErrorListener(){
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Helper.showToast(getString(R.string.something_wrong), ToastStyle.ERROR);

                                }
                            },true);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
