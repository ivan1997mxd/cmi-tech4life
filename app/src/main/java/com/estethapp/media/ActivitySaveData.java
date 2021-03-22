package com.estethapp.media;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.estethapp.media.util.App;

public class ActivitySaveData extends AppCompatActivity {

    EditText firstName;
    EditText middleName;
    EditText lastName;
    TextView dataInfoLabel;
    Uri screenshotUri;

    String dataContext;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_data);

        //Getting corresponding UI elements
        dataInfoLabel = findViewById(R.id.saveDataInformation);
        firstName = findViewById(R.id.firstNameField);
        middleName = findViewById(R.id.middleNameField);
        lastName = findViewById(R.id.lastNameField);

        middleName.setVisibility(View.INVISIBLE);

        //Resetting UI
        firstName.setText("");
        middleName.setText("");
        lastName.setText("");


        //Getting data passed via the intent
        dataContext = getIntent().getStringExtra("DATA_CONTEXT");
        String path = getIntent().getStringExtra("DATA_PATH");
        screenshotUri = Uri.parse(path);

        dataInfoLabel.setText("The data from the patient's " + dataContext + " test will be saved and shared with a health care provider.");
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    //Method called by the shareButton
    public void shareData(View view) {

        //Ensuring that the user inputted a first name and last name
        if (firstName.getText().toString().matches("") || lastName.getText().toString().matches("")){
            Toast.makeText(this, "First Name and Last Name fields cannot be blank", Toast.LENGTH_SHORT).show();
        }else{

            //Creating the body text for the email
            String body = "A screenshot of the patient's " + dataContext + " data are provided below.\n\nUser Information\n   Username: " + App.profileModel.PatientID + " \n   UserID: " + App.profileModel.UserID;

            if (middleName.getText().toString().matches("")){
                body += String.format("\n\nPatient Information \n   First Name: %s \n   Last Name: %s", firstName.getText(), lastName.getText());
            }else{
                body += String.format("\n\nPatient Information \n   First Name: %s \n   Middle Name: %s \n   Last Name: %s", firstName.getText(), middleName.getText(), lastName.getText());
            }

            //Creating new intent for the email client
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Patient Data Readings");
            intent.putExtra(Intent.EXTRA_TEXT, body);
            intent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
            intent.setType("image/png");
            startActivity(intent);
        }
    }


}