package com.estethapp.media;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.estethapp.media.adapter.PlayerPositionViewPagerAdapter;
import com.estethapp.media.db.JoinGroupContact;
import com.estethapp.media.helper.ExaminatingHelper;
import com.estethapp.media.helper.ExminigPostion;
import com.estethapp.media.helper.Exmining;
import com.estethapp.media.recording.PauseResumeAudioRecorder;
import com.estethapp.media.util.ChatUtil;
import com.estethapp.media.util.Contact;
import com.estethapp.media.util.ExminingPrefs;
import com.estethapp.media.util.MyContactsUtil;
import com.estethapp.media.util.Prefs;
import com.estethapp.media.util.UserModel;
import com.estethapp.media.util.Util;
import com.estethapp.media.view.AudioWife;
import com.estethapp.media.view.ProgressDialogView;
import com.estethapp.media.web.HttpCaller;
import com.estethapp.media.web.WebURL;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ActivityNewPlayer extends Activity implements View.OnClickListener{

    private ImageView btnPlay,imgShare,btnPause;
    SeekBar seekbar;
    private Handler myHandler = new Handler();;
    private Handler bodyPositionHandler = new Handler();;
    private double startTime = 0;
    private double finalTime = 0;
    public static int oneTimeOnly = 0;
    public TextView runTime;
    public TextView totalTime;
    private PauseResumeAudioRecorder mediaRecorder;
    private boolean isPlayed = false;
    private String filePath = null;
    private String imgurl;
    UserModel profileModel;
    private SharedPreferences eximingPrefsInstance;
    private SharedPreferences userPrefsInance;
    private ExminingPrefs eximingPref;
    private Prefs prefs;
    ChatUtil chatUtil = ChatUtil.getInstance();

    TextView txtRecordStatus;
    MyContactsUtil contactsPickedUtil = MyContactsUtil.getInstance();
    ExaminatingHelper exminatingHelper = ExaminatingHelper.getInstance();
    String fileName = "";
    ViewPager viewPager;
    PlayerPositionViewPagerAdapter playerPositionViewPagerAdapter;
    ArrayList<ExminigPostion> exminigFrontPostionHashMap;
    ArrayList<ExminigPostion> exminigBackPostionHashMap;
    Exmining exminingInstance;
    String exminingInfoString;
    boolean isFrontPlaying = true;

    ProgressDialogView dialogInstance = ProgressDialogView.getInstance();
    ProgressDialog progressDialog;

    AudioWife audioWifeInstance ;

    boolean isFirstTimePlay =true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //      Fabric.with(getApplicationContext(), new Crashlytics());
        setContentView(R.layout.activity_new_player);

        imgShare = (ImageView) findViewById(R.id.imgShare);
        txtRecordStatus = (TextView)findViewById(R.id.txt_recordStatus);

        progressDialog = dialogInstance.setProgressDialog(ActivityNewPlayer.this, "Creating group..");

        String recordFileDir = getIntent().getStringExtra("dir");
        imgurl = getIntent().getStringExtra("imgurl");

        fileName =getIntent().getStringExtra("fileName");
        if(fileName !=null) {
            if (!fileName.equals("")) {
                if (!fileName.contains(".wav")) {
                    fileName += ".wav";
                }
            }
        }else {
            fileName = "tempFile";
        }

        if(recordFileDir==null){
            recordFileDir = Util.getDirectory();
            filePath = recordFileDir+"/"+fileName;
        }else{
            filePath = recordFileDir;
        }

        String exminingInfo = getIntent().getStringExtra("Exmining");
        if(exminingInfo!=null){
            if(!exminingInfo.equals("")){
                exminingInstance = exminatingHelper.castIntoEximatiningInfo(exminingInfo);
                imgShare.setVisibility(View.INVISIBLE);
            }

        }



        String urlPath = getIntent().getStringExtra("URL");
        if(urlPath !=null){
            filePath = urlPath;
        }

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        playerPositionViewPagerAdapter = new PlayerPositionViewPagerAdapter(ActivityNewPlayer.this);
        viewPager.setAdapter(playerPositionViewPagerAdapter);


        runTime = (TextView)findViewById(R.id.run_time);
        totalTime = (TextView)findViewById(R.id.total_time);

        eximingPref = ExminingPrefs.getInstance();
        prefs = Prefs.getInstance();
        eximingPrefsInstance = eximingPref.init(ActivityNewPlayer.this);

        userPrefsInance = prefs.init(ActivityNewPlayer.this);


        startPostionFromPref();

        seekbar = (SeekBar)findViewById(R.id.media_seekbar);
        btnPlay = (ImageView) findViewById(R.id.play);
        btnPause = (ImageView) findViewById(R.id.pause);
        //btnPlay.setBackgroundResource(R.mipmap.play_ic);


        File file =new File(filePath);
        if(file.exists()){
            file.setReadable(true,false);
        }
        Uri pathUri = Uri.fromFile(file);


        audioWifeInstance = AudioWife.getInstance()
                .init(ActivityNewPlayer.this, file)
                .setPlayView(btnPlay)
                .setPauseView(btnPause)
                .setSeekBar(seekbar)
                .setRuntimeView(runTime)
                .setTotalTimeView(totalTime);

        seekbar.setClickable(false);


        Gson gson = new Gson();
        String json = userPrefsInance.getString(prefs.USERMODEL, "");
        profileModel = gson.fromJson(json, UserModel.class);

        imgShare.setOnClickListener(this);


        AudioWife.getInstance().addOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {

                // bodyPositionHandler.removeMessages(0);
                //resetPlayer();
            }
        });

        AudioWife.getInstance().addOnPlayClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try{
                    bodyPositionHandler.removeMessages(0);
                    isFrontPlaying=true;
                    backCount = 0;
                    frontCount = 1;


                    if(exminigFrontPostionHashMap != null){
                        if(!exminigFrontPostionHashMap.isEmpty()){
                            // myHandler.postDelayed(UpdateSongTime,100);
                            try{
                                viewPager.setCurrentItem(0, true);
                                ExminigPostion postions = exminigFrontPostionHashMap.get(0);
                                txtRecordStatus.setText(exminatingHelper.frontPositionNames[0]);
                                int time = (postions.endTime - postions.startTime) * 1000;
                                PlayerPositionViewPagerAdapter.showSelectedFrontPostionAnimation(""+postions.bodyPostion);
                                bodyPositionHandler.postDelayed(bodyPositionTime,time);
                            }catch(Exception e){

                            }
                        }
                    }


//                    ExminigPostion postions = exminigFrontPostionHashMap.get(0);
//                    int time = (postions.endTime - postions.startTime) * 1000;
//                    PlayerPositionViewPagerAdapter.showSelectedFrontPostionAnimation(""+postions.bodyPostion);
//                    bodyPositionHandler.postDelayed(bodyPositionTime,time);
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        });

        AudioWife.getInstance().addOnPauseClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });



    }

    public void startPostionFromPref(){
        if(exminingInstance == null){
            exminingInstance = exminatingHelper.getEximatiningInfo(fileName, eximingPrefsInstance);
            exminingInfoString = exminatingHelper.getEximatiningStringInfo(fileName, eximingPrefsInstance);

        }
        if(exminingInstance != null){
            exminigFrontPostionHashMap = exminingInstance.frontPostion;
            exminigBackPostionHashMap = exminingInstance.backPostion;
        }else{
            Toast.makeText(ActivityNewPlayer.this, R.string.couldnt_find_position_of_files, Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.imgShare) {
            //addMemberDialogShow();
            contactsPickedUtil.clearList();
            Intent intent = new Intent(ActivityNewPlayer.this, ContactActivity.class);
            startActivityForResult(intent, 5000);
        }
    }



    @Override
    public void onDestroy(){
        audioWifeInstance.getPlayerInstance().stop();
        super.onDestroy();
    }

    public void resetPlayer(){
        //myHandler.removeCallbacks(UpdateSongTime);
        bodyPositionHandler.removeCallbacks(bodyPositionTime);
    }


    /*private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            if(audioWifeInstance.getPlayerInstance()!=null) {
                startTime = audioWifeInstance.getPlayerInstance().getCurrentPosition();
                myHandler.postDelayed(this, 0);
            }
        }
    };*/




    public void progressDialogShow(String txt){
        if(progressDialog !=null){
            progressDialog.show();
        }

    }

    public void progressDialogDismiss(){
        if(progressDialog !=null){
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 5000 && resultCode == Activity.RESULT_OK) {
            contactsPickedUtil.getContactsList();
            if(contactsPickedUtil.getContactsList() != null){
                if(contactsPickedUtil.getContactsList().size() > 0){

                    if(!Util.isInternetAvailable(ActivityNewPlayer.this)) {
                        Toast.makeText(ActivityNewPlayer.this, "Internet connection has some problem", Toast.LENGTH_SHORT).show();
                    }else{
                        progressDialogShow("");
                        new Thread(new Runnable() {
                            public void run() {
                                uploadFile(filePath);
                            }
                        }).start();
                    }

                }
            }else{
                Toast.makeText(ActivityNewPlayer.this, "Please select contacts", Toast.LENGTH_SHORT).show();
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private class createGroup extends AsyncTask<Void, Void, String> {
        String chatID = "";
        public createGroup() {

        }

        @Override
        protected void onPostExecute(String result) {
            progressDialogDismiss();
        }

        @Override
        protected String doInBackground(Void... voids) {



            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("ChatName", "" + fileName);
                jsonObject.put("userID", "" + profileModel.UserID);
                jsonObject.put("exminingInfo", "" + exminingInfoString);
                jsonObject.put("CreateDate", ""+ chatUtil.getCurrentChatTime());
            } catch (JSONException e) {
                e.printStackTrace();
            }


            HttpCaller
                    .getInstance()
                    .createGroup(jsonObject,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if (response.getBoolean("Status")) {//{"Status":true,"Response":{"GroupID":6}}
                                            int groupID = response.getJSONObject("Response").getInt("GroupID");

                                            Gson gson = new Gson();
                                            String exminingInfoString = gson.toJson(exminingInstance);

                                            chatUtil.addGroup( fileName,fileName,exminingInfoString,"Me",""+profileModel.UserID,groupID,false);
                                            new SendInvitation(groupID).execute();
                                            chatID = ""+groupID;
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    progressDialogDismiss();
                                }
                            }, true);

            return chatID+"";
        }

        @Override
        protected void onPreExecute() {
//            progressDialogShow("Creating group..");

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    private class SendInvitation extends AsyncTask<Void, Void, String> {
        String chatID = "";
        int chatGroupID;
        public SendInvitation(int chatGroupID) {
            this.chatGroupID = chatGroupID;
        }

        @Override
        protected void onPostExecute(String result) {

            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
            intent.putExtra("ChatGroupID", chatGroupID);
            startActivity(intent);


        }

        @Override
        protected String doInBackground(Void... voids) {


            JSONArray array = new JSONArray();

            JSONObject favObject1 = new JSONObject();
            try {
                favObject1.put("UserID", profileModel.UserID);
                favObject1.put("ChatID", chatGroupID);
                favObject1.put("UserName", profileModel.Name);
                favObject1.put("ImgUrl", profileModel.ProfilePhoto);
                array.put(favObject1);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for (Contact selectedContact : contactsPickedUtil.getContactsList()) {
                JSONObject favObject = new JSONObject();
                try {
                    favObject.put("UserID", Integer.parseInt(selectedContact.UserID));
                    favObject.put("ChatID", chatGroupID);
                    favObject.put("UserName", selectedContact.Name);
                    favObject.put("ImgUrl", selectedContact.ProfilePhoto);
                    array.put(favObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JoinGroupContact joinGroupContact1 = new JoinGroupContact();
                joinGroupContact1.userName = selectedContact.Name;
                joinGroupContact1.userID = Integer.parseInt(selectedContact.UserID);
                joinGroupContact1.chatID = chatGroupID;
                joinGroupContact1.imgURL = selectedContact.ProfilePhoto;
                joinGroupContact1.save();
            }

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("FromUserID", "" + profileModel.UserID);
                jsonObject.put("JoinedContact", array);
                jsonObject.put("NotificationType", "ADDED_IN_GROUP");
            } catch (JSONException e) {
                e.printStackTrace();
            }


            HttpCaller
                    .getInstance()
                    .sendNotification(jsonObject,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        progressDialogDismiss();
                                        if (response.getBoolean("Status")) {
                                            int groupID = Integer.parseInt(response.getString("GroupID"));
                                            //  chatID = ""+groupID;
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    progressDialogDismiss();
                                }
                            });

            return "";
        }

        @Override
        protected void onPreExecute() {
            progressDialogShow("Creating Group..");

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }



    int frontCount = 1;
    int backCount = 0;


    private Runnable bodyPositionTime = new Runnable() {
        public void run() {
            if(!fileName.equals("")){
                if(exminingInstance.isFront && isFrontPlaying && exminigFrontPostionHashMap.size() > frontCount){
                    if(exminigFrontPostionHashMap.size()>= 1 && exminigFrontPostionHashMap.size() > frontCount){
                        viewPager.setCurrentItem(0, true);
                        ExminigPostion postions = exminigFrontPostionHashMap.get(frontCount);
                        int bodyPostion = postions.bodyPostion-1;
                        txtRecordStatus.setText(exminatingHelper.frontPositionNames[bodyPostion]);
                        int time = (postions.endTime - postions.startTime) * 1000;
                        int halfTime = time/2;
                        String keyPostion = ""+postions.bodyPostion;
                        PlayerPositionViewPagerAdapter.showSelectedFrontPostionAnimation(keyPostion);
                        bodyPositionHandler.postDelayed(bodyPositionTime,time);
                        // myHandler.postDelayed(this, time);
                        frontCount++;
                    }else{
                        // myHandler.postDelayed(this, 10);
                        isFrontPlaying = false;
                        frontCount =0;
                    }
                }else if(exminingInstance.isBack){
                    try{
                        if(exminigBackPostionHashMap.size()>= 1 && exminigBackPostionHashMap.size() >= backCount){
                            viewPager.setCurrentItem(1, true);
                            ExminigPostion postions = exminigBackPostionHashMap.get(backCount);
                            int bodyPostion = postions.bodyPostion-1;
                            txtRecordStatus.setText(exminatingHelper.backPostionNames[bodyPostion]);
                            int time = (postions.endTime - postions.startTime) * 1000;
                            //int halfTime = time/2;
                            String keyPostion = ""+postions.bodyPostion;
                            PlayerPositionViewPagerAdapter.showSelectedBackPostionAnimation(keyPostion);
                            bodyPositionHandler.postDelayed(bodyPositionTime,time);
                            //myHandler.postDelayed(this, time);
                            backCount++;
                        }else{
                            isFrontPlaying = true;
                            backCount =0;
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                }
            }
        }
    };

    public int uploadFile(final String sourceFileUri) {

        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        String serverResponseMessage = "";
        File sourceFile = new File(sourceFileUri);
        int serverResponseCode = 0;
        if (!sourceFile.isFile()) {

            return 0;

        } else {





            try {
                FileInputStream fileInputStream = new FileInputStream(
                        sourceFile);
                URL url = new URL(WebURL.UPLOAD_FILE);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("HEAD");
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type",
                        "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);
                conn.setConnectTimeout(20000);
                conn.setReadTimeout(20000);
                //conn.setRequestProperty("userid", "12345");

                fileName = fileName;
                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);


                try{
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {

                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    }

                    // send multipart form data necesssary after file data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    // Responses from the server (code and message)
                    serverResponseCode = conn.getResponseCode();
                    serverResponseMessage = conn.getResponseMessage();

                }catch (Exception e){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialogDismiss();
                            Toast.makeText(ActivityNewPlayer.this,
                                    "Failed to upload Auscultation file, Auscultation file is too large", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
                }




                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if (serverResponseCode == 200) {



                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialogDismiss();
                            new createGroup().execute();
                            Toast.makeText(ActivityNewPlayer.this,
                                    "Auscultation sync sucessfully", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialogDismiss();
                            // messageText
                            //        .setText("MalformedURLException Exception : check script url.");
                            Toast.makeText(ActivityNewPlayer.this,
                                    "Failed to upload Auscultation file", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
                }

                // close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                //dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        progressDialogDismiss();
                        // messageText
                        //        .setText("MalformedURLException Exception : check script url.");
                        Toast.makeText(ActivityNewPlayer.this,
                                "Failed to upload Auscultation file", Toast.LENGTH_SHORT)
                                .show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                //dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        progressDialogDismiss();
                        //messageText.setText("Got Exception : see logcat ");
                        Toast.makeText(ActivityNewPlayer.this,
                                "Failed to upload Auscultation file,", Toast.LENGTH_SHORT)
                                .show();
                    }
                });

            }
            //dialog.dismiss();
            return 0;
        }
    }


}

