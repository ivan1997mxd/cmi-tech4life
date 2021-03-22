package com.estethapp.media;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

//import com.crashlytics.android.Crashlytics;

import com.estethapp.media.util.Prefs;
//import io.fabric.sdk.android.Fabric;

public class ActivityPin extends Activity implements View.OnClickListener{

    private SharedPreferences prefsInstance;
    private Prefs prefs;

    Button btnOne,btnTwo,btnThree,btnFour,btnFive,btnSix,btnSeven,btnEight,btnNine,btnZeo,btnHash,btnStar,btnStorAdd;
    ImageButton btnTextRemove;
    EditText numberInput;
    TextView txtIgnore,pinCodeText,pin_verfication_textview, pinLoginText;
    boolean isLogin;
    String pinCode = "";
    String input = "";

    boolean isFirstTime = true;
    String pin1 = "";
    String pin2 = "";

    String isTypeFrist = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // Fabric.with(getApplicationContext(), new Crashlytics());
        setContentView(R.layout.activity_pin);
        prefs = Prefs.getInstance();
        prefsInstance = prefs.init(ActivityPin.this);

        numberInput =   (EditText) findViewById(R.id.pinEditText) ;

        btnOne      =   (Button)findViewById(R.id.btn_one);
        btnTwo      =   (Button)findViewById(R.id.btn_two);
        btnThree    =   (Button)findViewById(R.id.btn_three);
        btnFour     =   (Button)findViewById(R.id.btn_four);
        btnFive     =   (Button)findViewById(R.id.btn_five);
        btnSix      =   (Button)findViewById(R.id.btn_six);
        btnSeven    =   (Button)findViewById(R.id.btn_seven);
        btnEight    =   (Button)findViewById(R.id.btn_eight);
        btnNine     =   (Button)findViewById(R.id.btn_nine);
        btnZeo      =   (Button)findViewById(R.id.btn_zero);
        btnHash     =   (Button)findViewById(R.id.btn_hash);
        btnStar     =   (Button)findViewById(R.id.btn_star);
        btnTextRemove     =   (ImageButton)findViewById(R.id.txtRemoveBtn);

        txtIgnore     =   (TextView)findViewById(R.id.txt_ignore);
        pinCodeText     =   (TextView)findViewById(R.id.pinCodeText);
        pinLoginText = (TextView)findViewById(R.id.pin_login);
        pin_verfication_textview     =   (TextView)findViewById(R.id.pin_verfication_textview);

        btnStorAdd     =   (Button)findViewById(R.id.pinBtn);

        pinLoginText.setVisibility(View.GONE);

        isLogin = prefsInstance.getBoolean(prefs.ISLOGIN, false);
        pinCode = prefsInstance.getString(prefs.PIN,"");
        if (isLogin && !pinCode.equals("")) {
            btnStorAdd.setVisibility(View.GONE);
            txtIgnore.setVisibility(View.GONE);
            pinLoginText.setVisibility(View.VISIBLE);
        }

        isLogin =true;

        numberInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {

                if (isLogin) {
                    if(pinCode.equals(arg0.toString()) && arg0.length() == 4){
                        ChangeScreen();
                    }else{
                        if(!pinCode.equals("")){
                            if(!arg0.equals("") && arg0.length() ==4){
                                pinCodeText.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {




            }
        });


        btnOne.setOnClickListener(this);
        btnTwo.setOnClickListener(this);
        btnThree.setOnClickListener(this);
        btnFour.setOnClickListener(this);
        btnFive.setOnClickListener(this);
        btnSix.setOnClickListener(this);
        btnSeven.setOnClickListener(this);
        btnEight.setOnClickListener(this);
        btnNine.setOnClickListener(this);
        btnZeo.setOnClickListener(this);
        btnHash.setOnClickListener(this);
        btnStar.setOnClickListener(this);
        btnStorAdd.setOnClickListener(this);
        txtIgnore.setOnClickListener(this);
        btnTextRemove.setOnClickListener(this);
        pinLoginText.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_one:
                input += "1";
                numberInput.setText(input);
                numberInput.setSelection(numberInput.getText().length());
                break;
            case R.id.btn_two:
                input += "2";
                numberInput.setText(input);
                numberInput.setSelection(numberInput.getText().length());
                break;
            case R.id.btn_three:
                input += "3";
                numberInput.setText(input);
                numberInput.setSelection(numberInput.getText().length());
                break;
            case R.id.btn_four:
                input += "4";
                numberInput.setText(input);
                numberInput.setSelection(numberInput.getText().length());
                break;
            case R.id.btn_five:
                input += "5";
                numberInput.setText(input);
                numberInput.setSelection(numberInput.getText().length());
                break;
            case R.id.btn_six:
                input += "6";
                numberInput.setText(input);
                numberInput.setSelection(numberInput.getText().length());
                break;
            case R.id.btn_seven:
                input += "7";
                numberInput.setText(input);
                numberInput.setSelection(numberInput.getText().length());
                break;
            case R.id.btn_eight:
                input += "8";
                numberInput.setText(input);
                numberInput.setSelection(numberInput.getText().length());
                break;
            case R.id.btn_nine:
                input += "9";
                numberInput.setText(input);
                numberInput.setSelection(numberInput.getText().length());
                break;
            case R.id.btn_zero:
                input += "0";
                numberInput.setText(input);
                numberInput.setSelection(numberInput.getText().length());
                break;
            case R.id.btn_hash:
                input += "#";
                numberInput.setText(input);
                numberInput.setSelection(numberInput.getText().length());
                break;
            case R.id.btn_star:
                input += "*";
                numberInput.setText(input);
                numberInput.setSelection(numberInput.getText().length());
                break;
            case R.id.pinBtn:
                savePin();
                break;
            case R.id.txt_ignore:
                ChangeScreen();
                break;
            case R.id.pin_login:
                goToLoginActivity();
                break;
            case R.id.txtRemoveBtn:
                resetPin();
                numberInput.setText("");
                break;

        }
    }

    public void resetPin(){
        input = "";
        isFirstTime= true;
        pin1= "";
        pin2= "";
    }

    public void removeTxt(){
        if(input.length() > 0){
            input = input.substring(0,input.length() - 1);
            numberInput.setText(input);
        }
    }
    public void savePin(){
        String pin = numberInput.getText().toString();
        if(!pin.equals("")){
            if(pin.length() == 4){
                if(isFirstTime){
                    isFirstTime= false;
                    pin1= pin;
                    numberInput.setText("");
                    input ="";
                    pin_verfication_textview.setText(getString(R.string.confirm_pin_code));
                }else{
                    pin2 = pin;
                }


                if(pin1.equals(pin2)){
                    prefsInstance.edit().putString(prefs.PIN,pin).commit();
                    ChangeScreen();
                }else if(!pin2.equals("")){
                    Toast.makeText(ActivityPin.this, getString(R.string.confirm_pin_not_match),Toast.LENGTH_SHORT).show();
                }

            }else{
                Toast.makeText(ActivityPin.this, getString(R.string.atleast_4_digit),Toast.LENGTH_SHORT).show();
                input ="";
                numberInput.setText("");
            }
        }else{
            Toast.makeText(ActivityPin.this, getString(R.string.type_pin),Toast.LENGTH_SHORT).show();
            input = "";
        }
    }

    public void pinL(){
        Intent intent = new Intent(ActivityPin.this,ActivityLogin.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void ChangeScreen(){
        Intent intent = new Intent(ActivityPin.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void goToLoginActivity(){
        Intent intent = new Intent(ActivityPin.this,ActivityLogin.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
